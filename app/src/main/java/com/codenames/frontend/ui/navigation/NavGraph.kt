package com.codenames.frontend.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.codenames.frontend.ui.screens.GameScreenWrapper
import com.codenames.frontend.ui.screens.GameSettingsScreen
import com.codenames.frontend.ui.screens.GameTestScreen
import com.codenames.frontend.ui.screens.JoinLobbyScreen
import com.codenames.frontend.ui.screens.LobbyScreen
import com.codenames.frontend.ui.screens.SettingsScreen
import com.codenames.frontend.ui.screens.StartScreen
import com.codenames.frontend.ui.screens.UserNameScreen
import com.codenames.frontend.viewmodel.LobbyViewModel
import com.codenames.frontend.viewmodel.SessionViewModel

@Composable
@Suppress("ktlint:standard:function-naming")
fun NavGraph(viewModel: LobbyViewModel = hiltViewModel(), sessionViewModel: SessionViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val usernameState by sessionViewModel.username.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Username.route,
        route = "main_graph",
    ) {
        composable(Screen.Username.route) {
            UserNameScreen(navController)
        }

        composable(
            Screen.Start.route,
        ) {
            StartScreen(navController = navController)
        }

        composable(Screen.Lobby.route) {
            LobbyScreen(navController)
        }

        composable(
            route = Screen.JoinLobby.route,
        ) {
            JoinLobbyScreen(navController)
        }

        composable(
            route = Screen.Gameboard.route
        ) {
            val currentRole = viewModel.getRoleForUser(usernameState.username)

            GameScreenWrapper(
                navController = navController,
                userRole = currentRole,
            )
        }

        composable(Screen.GameSettings.route) {
            GameSettingsScreen()
        }

        composable(Screen.Settings.route) {
            SettingsScreen(navController)
        }

        composable("game_test") {
            GameTestScreen()
        }
    }
}