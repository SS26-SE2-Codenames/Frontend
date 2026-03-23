package com.codenames.codenames_frontend.ui.navigation

//add new Screens here as an object with the corresponding path
sealed class Screen(val route: String) {
    object Home : Screen("home");
    object Lobby : Screen("lobby");
    object Game : Screen("game");
}