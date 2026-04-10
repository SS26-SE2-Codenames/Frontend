package com.codenames.codenames_frontend.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.codenames.codenames_frontend.ui.screens.GameSettingsScreen
import com.codenames.codenames_frontend.ui.screens.GameboardScreen
import com.codenames.codenames_frontend.ui.screens.JoinlobbyScreen
import com.codenames.codenames_frontend.ui.screens.LobbyScreen
import com.codenames.codenames_frontend.ui.screens.SettingsScreen
import com.codenames.codenames_frontend.ui.screens.StartScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Start.route
    ) {
        composable(Screen.Start.route) {
            StartScreen(navController)
        }
        composable(Screen.Lobby.route) {
            LobbyScreen(navController)
        }
        composable(Screen.JoinLobby.route) {
            JoinlobbyScreen()
        }
        composable(Screen.Gameboard.route) {
            GameboardScreen()
        }
        composable(Screen.GameSettings.route) {
            GameSettingsScreen()
        }
        composable(Screen.Settings.route) {
            SettingsScreen()
        }
    }
}