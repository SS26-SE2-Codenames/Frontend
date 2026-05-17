package com.codenames.frontend.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import com.codenames.frontend.data.model.GameState
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
    val usernameState by sessionViewModel.username.collectAsState()
    val userRole = lobbyViewModel.getRoleForUser(usernameState.username)

    val currentPlayer = lobbyState.players.firstOrNull { it.name == usernameState.username }
    val team = currentPlayer?.team
    val lobbyCode = lobbyState.lobbyCode.orEmpty()
    val cards = gameState.cards

    GameboardScreen(
        userRole = userRole,
        gameState =
            GameState(
                currentHint = gameState.currentHint,
                currentTurn = gameState.currentTurn,
                winner = gameState.winner,
                remainingGuesses = gameState.remainingGuesses,
                cards = cards,
            ),
        onHintChange = {
            // TODO: Send clue through GameViewModel once backend endpoint exists.
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
        gameViewModel = gameViewModel,
    )
}
