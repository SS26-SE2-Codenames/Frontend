package com.codenames.frontend.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import com.codenames.frontend.ui.navigation.Screen
import com.codenames.frontend.ui.roles.PlayerRoles
import com.codenames.frontend.ui.toGameCard
import com.codenames.frontend.viewmodel.GameViewModel
import com.codenames.frontend.viewmodel.LobbyViewModel
import com.codenames.frontend.viewmodel.SessionViewModel

@Composable
@Suppress("ktlint:standard:function-naming")
fun GameScreenWrapper(
    navController: NavHostController,
    userRole: PlayerRoles,
    lobbyViewModel: LobbyViewModel,
    gameViewModel: GameViewModel,
    sessionViewModel: SessionViewModel,
) {
    val lobbyState by lobbyViewModel.state.collectAsState()
    val gameState by gameViewModel.uiState.collectAsState()
    val chatState by gameViewModel.chatState.collectAsState()
    val usernameState by sessionViewModel.username.collectAsState()

    val currentPlayer = lobbyState.players.firstOrNull { it.name == usernameState.username }
    val effectiveRole = lobbyViewModel.getRoleForUser(usernameState.username)
    val team = currentPlayer?.team
    val lobbyCode = lobbyState.lobbyCode.orEmpty()
    val cards = gameState.cardList.map { it.toGameCard() }

    GameboardScreen(
        userRole = effectiveRole,
        gameState =
            GameState(
                currentHint = gameState.currentClue ?: "Waiting for hint...",
                currentTurn = gameState.currentTurn,
                currentPhase = gameState.currentPhase,
                winner = gameState.winner,
                remainingGuesses = gameState.remainingGuesses,
                currentRedFound = gameState.currentRedFound,
                currentBlueFound = gameState.currentBlueFound,
                cards = cards,
                chatMessages = chatState.teamMessages,
            ),
        onHintChange = { word, count ->

            if (lobbyCode.isNotBlank()) {
                gameViewModel.submitClue(lobbyCode, word, count)
            }
            // TODO: Check if I implemented the correct endpoint in submitClue.
        },
        onReveal = {
            // TODO: Send guess through GameViewModel once backend endpoint exists.
        },
        onSendChatMessage = { message ->
            if (lobbyCode.isNotBlank() && team != null) {
                gameViewModel.sendTeamMessage(
                    lobbyCode = lobbyCode,
                    team = team.name,
                    username = usernameState.username,
                    content = message,
                )
            }
        },
        onSettingsClick = {
            navController.navigate(Screen.Settings.route)
        },
    )
}
