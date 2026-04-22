package com.codenames.codenames_frontend.ui.navigation

import android.util.Log
import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument

import com.codenames.codenames_frontend.ui.roles.PlayerRole
import com.codenames.codenames_frontend.ui.screens.*

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

        // ---------------- GAME SCREEN ----------------
        composable(
            route = "${Screen.Gameboard.route}/{role}",
            arguments = listOf(navArgument("role") { type = NavType.StringType })
        ) { backStackEntry ->

            val roleString =
                backStackEntry.arguments?.getString("role") ?: PlayerRole.NONE.name

            val passedRole = try {
                PlayerRole.valueOf(roleString)
            } catch (e: IllegalArgumentException) {
                PlayerRole.NONE
            }

            GameScreenWrapper(userRole = passedRole)
        }

        composable(Screen.GameSettings.route) {
            GameSettingsScreen()
        }

        composable(Screen.Settings.route) {
            SettingsScreen()
        }

        composable("game_test") {
            GameTestScreen()
        }
    }
}

@Composable
fun GameScreenWrapper(userRole: PlayerRole) {

    var currentHint by remember { mutableStateOf("Waiting for hint...") }

    val cards = remember {
        mutableStateListOf(
            *List(25) {
                GameCard(
                    word = "Word ${it + 1}",
                    type = when (it) {
                        0 -> CardType.ASSASSIN
                        in 1..8 -> CardType.BLUE
                        in 9..15 -> CardType.RED
                        else -> CardType.NEUTRAL
                    }
                )
            }.toTypedArray()
        )
    }

    fun revealCard(index: Int) {
        val card = cards[index]
        cards[index] = card.copy(revealed = true)
    }

    GameboardScreen(
        userRole = userRole,
        currentHint = currentHint,
        onHintChange = { currentHint = it },
        cards = cards,
        onReveal = { index ->
            revealCard(index)
        }
    )
}