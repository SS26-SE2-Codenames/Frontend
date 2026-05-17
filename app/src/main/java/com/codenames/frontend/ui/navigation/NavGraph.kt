package com.codenames.frontend.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.codenames.frontend.ui.screens.GameScreenWrapper
import com.codenames.frontend.ui.screens.GameSettingsScreen
import com.codenames.frontend.ui.screens.JoinlobbyScreen
import com.codenames.frontend.ui.screens.LobbyScreen
import com.codenames.frontend.ui.screens.OfflineGameStateTestScreen
import com.codenames.frontend.ui.screens.SettingsScreen
import com.codenames.frontend.ui.screens.StartScreen
import com.codenames.frontend.ui.screens.UserNameScreen
import com.codenames.frontend.viewmodel.GameViewModel
import com.codenames.frontend.viewmodel.LobbyViewModel
import com.codenames.frontend.viewmodel.SessionViewModel

@Composable
@Suppress("ktlint:standard:function-naming")
fun NavGraph(
    lobbyViewModel: LobbyViewModel = hiltViewModel(),
    sessionViewModel: SessionViewModel = hiltViewModel(),
    gameViewModel: GameViewModel = hiltViewModel(),
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Username.route,
    ) {
        composable(Screen.Username.route) {
            UserNameScreen(navController, sessionViewModel)
        }

        composable(
            Screen.Start.route,
        ) {
            StartScreen(
                lobbyViewModel = lobbyViewModel,
                navController = navController,
                sessionViewModel = sessionViewModel,
            )
        }

        composable(Screen.Lobby.route) {
            LobbyScreen(
                navController = navController,
                viewModel = lobbyViewModel,
                gameViewModel = gameViewModel,
                sessionViewModel = sessionViewModel,
            )
        }

        composable(Screen.JoinLobby.route) {
            JoinlobbyScreen(viewModel = lobbyViewModel, navController = navController, sessionViewModel = sessionViewModel)
        }

        composable(
            route = Screen.Gameboard.route,
        ) {
            GameScreenWrapper(
                navController = navController,
                lobbyViewModel = lobbyViewModel,
                gameViewModel = gameViewModel,
                sessionViewModel = sessionViewModel,
            )
        }

        composable(Screen.GameSettings.route) {
            GameSettingsScreen()
        }

        composable(Screen.Settings.route) {
            SettingsScreen(navController)
        }

        composable("game_test") {
            OfflineGameStateTestScreen(
                gameViewModel,
            )
        }
    }
}
