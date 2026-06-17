package com.codenames.frontend.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import com.codenames.frontend.ui.navigation.Screen
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

    val username = userState.username
    val lobbyCode = lobbyState.lobbyCode.orEmpty()
    val currentPlayer = lobbyState.players.firstOrNull { it.name == username }
    val team = currentPlayer?.team
    val userRole = lobbyViewModel.getRoleForUser(username)
    val availableChatTabs = lobbyViewModel.getAvailableChatTabsForUser(username)
    val winner = gameState.winner

    LaunchedEffect(winner) {
        if (winner != null) {
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
        onPassTurn = {
            gameViewModel.passTurn(lobbyCode, team)
        },
        onCheatRequest = { positions ->
            gameViewModel.requestCheat(
                lobbyCode = lobbyCode,
                username = username,
                positions = positions,
            )
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
        onReturnToHome = {
            navController.navigate(Screen.Start.route)
            lobbyViewModel.cleanup()
            gameViewModel.resetGameState()
        }
    )
}
