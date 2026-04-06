package com.codenames.codenames_frontend.ui.navigation
import com.codenames.codenames_frontend.ui.screens.StartScreen
import com.codenames.codenames_frontend.ui.screens.LobbyScreen
import com.codenames.codenames_frontend.ui.screens.JoinlobbyScreen
import com.codenames.codenames_frontend.ui.screens.GameboardScreen
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable

@Composable
fun NavGraph(){
    val navController = rememberNavController();

    NavHost(
        navController = navController,
        startDestination = Screen.Start.route
    ) {
        composable(Screen.Start.route) {
            StartScreen(navController)
        }
        composable(Screen.Lobby.route){
            LobbyScreen(navController)
        }
        composable(Screen.JoinLobby.route){
            JoinlobbyScreen(navController)
        }
        composable(Screen.Gameboard.route){
           GameboardScreen(navController)
        }
    }
}