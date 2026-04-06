package com.codenames.codenames_frontend.ui.navigation

sealed class Screen(val route: String) {
    object Start : Screen("start");
    object JoinLobby : Screen ("join_lobby")
    object Lobby : Screen("lobby");
    object Gameboard : Screen("gameboard");

}


