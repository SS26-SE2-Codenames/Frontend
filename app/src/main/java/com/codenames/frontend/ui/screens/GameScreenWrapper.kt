package com.codenames.frontend.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import com.codenames.frontend.data.model.Player
import com.codenames.frontend.data.model.enums.ChatTab
import com.codenames.frontend.data.model.enums.Role
import com.codenames.frontend.data.model.enums.Team
import com.codenames.frontend.ui.navigation.Screen
import com.codenames.frontend.ui.roles.PlayerRoles
import com.codenames.frontend.viewmodel.GameViewModel
import com.codenames.frontend.viewmodel.LobbyViewModel
import com.codenames.frontend.viewmodel.SessionViewModel

@Composable
@Suppress("ktlint:standard:function-naming")
fun GameScreenWrapper(
    navController: NavHostController,
    lobbyViewModel: LobbyViewModel,
    gameViewModel: GameViewModel,
    sessionViewModel: SessionViewModel,
) {
    val lobbyState by lobbyViewModel.state.collectAsState()
    val gameState by gameViewModel.uiState.collectAsState()
    val chatState by gameViewModel.chatState.collectAsState()
    val usernameState by sessionViewModel.username.collectAsState()

    val username = usernameState.username
    val userRole = lobbyViewModel.getRoleForUser(username)
    val currentPlayer = lobbyState.players.firstOrNull { it.name == username }
    val team = currentPlayer?.team
    val lobbyCode = lobbyState.lobbyCode.orEmpty()
    val availableChatTabs = getAvailableChatTabs(userRole, lobbyState.players, team)

    GameboardScreen(
        userRole = userRole,
        gameState =
            gameState.copy(
                chatLists = chatState,
                availableChatTabs = availableChatTabs,
            ),
        onHintChange = { word, count ->
            sendHintIfPossible(
                lobbyCode = lobbyCode,
                word = word,
                count = count,
                gameViewModel = gameViewModel,
            )
        },
        onReveal = {
            // TODO: Send guess through GameViewModel once backend endpoint exists.
        },
        onSendChatMessage = { tab, message ->
            sendChatMessage(
                tab = tab,
                message = message,
                lobbyCode = lobbyCode,
                username = username,
                team = team,
                availableChatTabs = availableChatTabs,
                gameViewModel = gameViewModel,
            )
        },
        onSettingsClick = {
            navController.navigate(Screen.Settings.route)
        },
    )
}

private fun getAvailableChatTabs(
    userRole: PlayerRoles,
    players: List<Player>,
    team: Team?,
): List<ChatTab> {
    val tabs = mutableListOf(ChatTab.GLOBAL)

    if (team != null) {
        tabs.add(ChatTab.TEAM)
    }

    if (canUseOperativesChat(userRole, players, team)) {
        tabs.add(ChatTab.OPERATIVES)
    }

    return tabs
}

private fun canUseOperativesChat(
    userRole: PlayerRoles,
    players: List<Player>,
    team: Team?,
): Boolean {
    if (team == null) {
        return false
    }

    val isOperative =
        userRole == PlayerRoles.BLUE_OPERATIVE || userRole == PlayerRoles.RED_OPERATIVE
    val sameTeamOperativeCount =
        players.count { player ->
            player.team == team && player.role == Role.OPERATIVE
        }

    return isOperative && sameTeamOperativeCount > 1
}

private fun sendHintIfPossible(
    lobbyCode: String,
    word: String,
    count: Int,
    gameViewModel: GameViewModel,
) {
    if (lobbyCode.isNotBlank()) {
        gameViewModel.submitClue(lobbyCode, word, count)
    }
}

private fun sendChatMessage(
    tab: ChatTab,
    message: String,
    lobbyCode: String,
    username: String,
    team: Team?,
    availableChatTabs: List<ChatTab>,
    gameViewModel: GameViewModel,
) {
    if (lobbyCode.isBlank()) {
        return
    }

    when (tab) {
        ChatTab.GLOBAL ->
            gameViewModel.sendLobbyMessage(
                lobbyCode = lobbyCode,
                username = username,
                content = message,
            )

        ChatTab.TEAM ->
            sendTeamChatMessage(
                lobbyCode = lobbyCode,
                username = username,
                team = team,
                message = message,
                gameViewModel = gameViewModel,
            )

        ChatTab.OPERATIVES ->
            sendOperativesChatMessage(
                lobbyCode = lobbyCode,
                username = username,
                team = team,
                message = message,
                availableChatTabs = availableChatTabs,
                gameViewModel = gameViewModel,
            )
    }
}

private fun sendTeamChatMessage(
    lobbyCode: String,
    username: String,
    team: Team?,
    message: String,
    gameViewModel: GameViewModel,
) {
    if (team == null) {
        return
    }

    gameViewModel.sendTeamMessage(
        lobbyCode = lobbyCode,
        team = team.name,
        username = username,
        content = message,
    )
}

private fun sendOperativesChatMessage(
    lobbyCode: String,
    username: String,
    team: Team?,
    message: String,
    availableChatTabs: List<ChatTab>,
    gameViewModel: GameViewModel,
) {
    if (team == null || ChatTab.OPERATIVES !in availableChatTabs) {
        return
    }

    gameViewModel.sendOperativeMessage(
        lobbyCode = lobbyCode,
        team = team.name,
        username = username,
        content = message,
    )
}
