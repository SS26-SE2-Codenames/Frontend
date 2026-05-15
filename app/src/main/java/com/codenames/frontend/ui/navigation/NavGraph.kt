package com.codenames.frontend.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.codenames.frontend.ui.roles.PlayerRoles
import com.codenames.frontend.ui.screens.GameSettingsScreen
import com.codenames.frontend.ui.screens.GameState
import com.codenames.frontend.ui.screens.GameboardScreen
import com.codenames.frontend.ui.screens.JoinlobbyScreen
import com.codenames.frontend.ui.screens.LobbyScreen
import com.codenames.frontend.ui.screens.OfflineGameStateTestScreen
import com.codenames.frontend.ui.screens.SettingsScreen
import com.codenames.frontend.ui.screens.StartScreen
import com.codenames.frontend.ui.screens.UserNameScreen
import com.codenames.frontend.ui.toGameCard
import com.codenames.frontend.ui.toPlayerRole
import com.codenames.frontend.viewmodel.GameViewModel
import com.codenames.frontend.viewmodel.LobbyViewModel

@Composable
@Suppress("ktlint:standard:function-naming")
fun NavGraph() {
    val navController = rememberNavController()
    val lobbyViewModel: LobbyViewModel = hiltViewModel()
    val gameViewModel: GameViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Username.route,
    ) {
        composable(Screen.Username.route) {
            UserNameScreen(navController)
        }

        composable(
            route = "${Screen.Start.route}/{username}",
            arguments =
                listOf(
                    navArgument("username") { type = NavType.StringType },
                ),
        ) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username").orEmpty()

            StartScreen(
                navController = navController,
                username = username,
                lobbyViewModel = lobbyViewModel,
            )
        }

        composable(Screen.Lobby.route) {
            val lobbyState by lobbyViewModel.state.collectAsState()
            val currentUsername =
                lobbyState.players
                    .firstOrNull { it.isHost }
                    ?.name
                    .orEmpty()

            LobbyScreen(
                navController = navController,
                username = currentUsername,
                lobbyViewModel = lobbyViewModel,
                gameViewModel = gameViewModel,
            )
        }

        composable(
            route = "${Screen.JoinLobby.route}/{username}",
            arguments =
                listOf(
                    navArgument("username") { type = NavType.StringType },
                ),
        ) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username").orEmpty()

            JoinlobbyScreen(
                navController = navController,
                username = username,
                lobbyViewModel = lobbyViewModel,
            )
        }

        composable(
            route = "${Screen.Gameboard.route}/{username}/{role}",
            arguments =
                listOf(
                    navArgument("username") { type = NavType.StringType },
                    navArgument("role") { type = NavType.StringType },
                ),
        ) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username").orEmpty()
            val roleString =
                backStackEntry.arguments?.getString("role") ?: PlayerRoles.NONE.name

            val passedRole =
                try {
                    PlayerRoles.valueOf(roleString)
                } catch (e: IllegalArgumentException) {
                    PlayerRoles.NONE
                }

            GameScreenWrapper(
                navController = navController,
                username = username,
                userRole = passedRole,
                lobbyViewModel = lobbyViewModel,
                gameViewModel = gameViewModel,
            )
        }

        composable(Screen.GameSettings.route) {
            GameSettingsScreen()
        }

        composable(Screen.Settings.route) {
            SettingsScreen(navController)
        }

        composable("game_test") {
            OfflineGameStateTestScreen()
        }
    }
}

@Composable
@Suppress("ktlint:standard:function-naming")
fun GameScreenWrapper(
    navController: NavHostController,
    username: String,
    userRole: PlayerRoles,
    lobbyViewModel: LobbyViewModel,
    gameViewModel: GameViewModel,
) {
    val lobbyState by lobbyViewModel.state.collectAsState()
    val gameState by gameViewModel.uiState.collectAsState()
    val chatState by gameViewModel.chatState.collectAsState()

    val currentPlayer = lobbyState.players.firstOrNull { it.name == username }
    val effectiveRole = currentPlayer?.toPlayerRole() ?: userRole
    val team = currentPlayer?.team
    val lobbyCode = lobbyState.lobbyCode.orEmpty()
    val cards = gameState.cardList.map { it.toGameCard() }

    GameboardScreen(
        userRole = effectiveRole,
        gameState =
            GameState(
                currentHint = gameState.currentClue ?: "Waiting for hint...",
                currentTurn = gameState.currentTurn,
                winner = gameState.winner,
                remainingGuesses = gameState.remainingGuesses,
                currentRedFound = gameState.currentRedFound,
                currentBlueFound = gameState.currentBlueFound,
                cards = cards,
                chatMessages = chatState.teamMessages,
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
                    username = username,
                    content = message,
                )
            }
        },
        onSettingsClick = {
            navController.navigate(Screen.Settings.route)
        },
    )
}
