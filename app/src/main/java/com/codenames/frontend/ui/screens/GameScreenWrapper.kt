package com.codenames.frontend.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import com.codenames.frontend.ui.navigation.Screen
import com.codenames.frontend.ui.roles.PlayerRoles

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
