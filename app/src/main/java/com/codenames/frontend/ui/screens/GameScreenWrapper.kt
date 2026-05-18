package com.codenames.frontend.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import com.codenames.frontend.data.model.enums.ChatTab
import com.codenames.frontend.ui.navigation.Screen
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
    val userRole = lobbyViewModel.getRoleForUser(usernameState.username)

    val currentPlayer = lobbyState.players.firstOrNull { it.name == usernameState.username }
    val team = currentPlayer?.team
    val lobbyCode = lobbyState.lobbyCode.orEmpty()

    GameboardScreen(
        userRole = userRole,
        gameState = gameState.copy(chatLists = chatState),
        onHintChange = { word, count ->
            if (lobbyCode.isNotBlank()) {
                gameViewModel.submitClue(lobbyCode, word, count)
            }
        },
        onReveal = {
            // TODO: Send guess through GameViewModel once backend endpoint exists.
        },
        onSendChatMessage = { tab, message ->
            if (lobbyCode.isBlank()) {
                return@GameboardScreen
            }

            when (tab) {
                ChatTab.GLOBAL -> {
                    gameViewModel.sendLobbyMessage(
                        lobbyCode = lobbyCode,
                        username = usernameState.username,
                        content = message,
                    )
                }

                ChatTab.TEAM -> {
                    if (team != null) {
                        gameViewModel.sendTeamMessage(
                            lobbyCode = lobbyCode,
                            team = team.name,
                            username = usernameState.username,
                            content = message,
                        )
                    }
                }

                ChatTab.OPERATIVES -> {
                    if (team != null) {
                        gameViewModel.sendOperativeMessage(
                            lobbyCode = lobbyCode,
                            team = team.name,
                            username = usernameState.username,
                            content = message,
                        )
                    }
                }
            }
        },
        onSettingsClick = {
            navController.navigate(Screen.Settings.route)
        },
    )
}
