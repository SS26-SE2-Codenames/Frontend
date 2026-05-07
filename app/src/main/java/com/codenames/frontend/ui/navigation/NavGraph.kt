package com.codenames.frontend.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.codenames.frontend.ui.roles.PlayerRoles
import com.codenames.frontend.ui.screens.CardType
import com.codenames.frontend.ui.screens.GameCard
import com.codenames.frontend.ui.screens.GameSettingsScreen
import com.codenames.frontend.ui.screens.GameTestScreen
import com.codenames.frontend.ui.screens.GameboardScreen
import com.codenames.frontend.ui.screens.JoinlobbyScreen
import com.codenames.frontend.ui.screens.LobbyScreen
import com.codenames.frontend.ui.screens.SettingsScreen
import com.codenames.frontend.ui.screens.StartScreen
import com.codenames.frontend.ui.screens.UserNameScreen

@Composable
@Suppress("ktlint:standard:function-naming")
fun NavGraph() {
    val navController = rememberNavController()

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
        ) {
            StartScreen(navController)
        }

        composable(Screen.Lobby.route) {
            LobbyScreen(navController)
        }

        composable(Screen.JoinLobby.route) {
            JoinlobbyScreen(navController)
        }

        composable(
            route = "${Screen.Gameboard.route}/{role}",
            arguments =
                listOf(
                    navArgument("role") { type = NavType.StringType },
                ),
        ) { backStackEntry ->
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
                userRole = passedRole,
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

@Composable
@Suppress("ktlint:standard:function-naming")
fun GameScreenWrapper(
    navController: NavHostController,
    userRole: PlayerRoles,
) {
    var currentHint by remember { mutableStateOf("Waiting for hint...") }

    val cards =
        remember {
            mutableStateListOf(
                *List(25) {
                    GameCard(
                        word = "Word ${it + 1}",
                        type =
                            when (it) {
                                0 -> CardType.ASSASSIN
                                in 1..8 -> CardType.BLUE
                                in 9..15 -> CardType.RED
                                else -> CardType.NEUTRAL
                            },
                    )
                }.toTypedArray(),
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
        },
        onSettingsClick = {
            navController.navigate(Screen.Settings.route)
        },
    )
}
