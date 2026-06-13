package com.codenames.frontend.ui.screens

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import com.codenames.frontend.data.model.RejoinState
import com.codenames.frontend.data.model.toPlayerRole
import com.codenames.frontend.ui.navigation.Screen
import com.codenames.frontend.ui.roles.PlayerRoles
import com.codenames.frontend.viewmodel.ChatViewModel
import com.codenames.frontend.viewmodel.GameViewModel
import com.codenames.frontend.viewmodel.LobbyViewModel
import com.codenames.frontend.viewmodel.SessionViewModel

@Composable
@Suppress("ktlint:standard:function-naming")
fun GameScreenWrapper(
    navController: NavHostController,
    lobbyViewModel: LobbyViewModel,
    gameViewModel: GameViewModel,
    chatViewModel: ChatViewModel,
    sessionViewModel: SessionViewModel,
) {
    val lobbyState by lobbyViewModel.state.collectAsState()
    val gameState by gameViewModel.uiState.collectAsState()
    val chatState by chatViewModel.chatState.collectAsState()
    val userState by sessionViewModel.userState.collectAsState()
    val rejoinState by sessionViewModel.rejoinSessionState.collectAsState()

    val availableRejoinState = rejoinState as? RejoinState.Available

    val username = userState.username
    var lobbyCode = lobbyState.lobbyCode.orEmpty()
    val currentPlayer = lobbyState.players.firstOrNull { it.name == username }
    val team = currentPlayer?.team
    var userRole = lobbyViewModel.getRoleForUser(username)
    val availableChatTabs = lobbyViewModel.getAvailableChatTabsForUser(username)
    val winner = gameState.winner

    Log.d("GameScreenWrapper", "Available Rejoin state: $availableRejoinState")

    if(userRole == PlayerRoles.NONE && availableRejoinState != null) {
        val rejoinedTeam = availableRejoinState.sessionState.lobbyTeam
        val rejoinedRole = availableRejoinState.sessionState.lobbyRole

        userRole = toPlayerRole(rejoinedTeam, rejoinedRole)
        Log.d("GameScreenWrapper", "User role aus Speicher genommen: $userRole")
    }

    if(lobbyCode.isEmpty() && availableRejoinState?.sessionState?.lobbyCode != null) {

        lobbyCode = availableRejoinState.sessionState.lobbyCode
    }

    LaunchedEffect(winner) {
        if(winner != null) {
            sessionViewModel.clearLobby()
        }
    }

    GameboardScreen(
        userRole = userRole,
        gameState =
            gameState.copy(
                chatLists = chatState,
                availableChatTabs = availableChatTabs,
            ),
        onHintChange = { word, count ->
            gameViewModel.submitClue(lobbyCode, word, count, team)
        },
        onReveal = { positions ->
            gameViewModel.submitGuesses(lobbyCode, positions, team)
        },
        onSendChatMessage = { tab, message ->
            chatViewModel.sendChatMessage(
                tab = tab,
                lobbyCode = lobbyCode,
                username = username,
                team = team,
                content = message,
                availableChatTabs = availableChatTabs,
            )
        },
        onSettingsClick = {
            navController.navigate(Screen.Settings.route)
        },
    )
}
