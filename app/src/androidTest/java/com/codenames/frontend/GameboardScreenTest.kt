package com.codenames.frontend

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.codenames.frontend.data.model.ChatDomainModel
import com.codenames.frontend.ui.roles.PlayerRoles
import com.codenames.frontend.ui.screens.CardType
import com.codenames.frontend.ui.screens.GameCard
import com.codenames.frontend.ui.screens.GameState
import com.codenames.frontend.ui.screens.GameboardScreen
import org.junit.Rule
import org.junit.Test

class GameboardScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun gameboardDisplaysGameStateFromParameters() {
        val cards =
            listOf(
                GameCard("BERLIN", CardType.BLUE, revealed = false),
                GameCard("ROME", CardType.RED, revealed = true),
                GameCard("MOON", CardType.NEUTRAL, revealed = false),
                GameCard("VIPER", CardType.ASSASSIN, revealed = false),
            )

        composeRule.setContent {
            GameboardScreen(
                userRole = PlayerRoles.BLUE_SPYMASTER,
                gameState =
                    GameState(
                        currentHint = "EAGLE",
                        currentTurn = "BLUE",
                        remainingGuesses = 3,
                        currentBlueFound = 2,
                        currentRedFound = 1,
                        cards = cards,
                    ),
                onHintChange = {},
                onReveal = {},
            )
        }

        composeRule.onNodeWithText("BERLIN").assertIsDisplayed()
        composeRule.onNodeWithText("ROME").assertIsDisplayed()
        composeRule.onNodeWithText("Turn: BLUE | Guesses: 3").assertIsDisplayed()
        composeRule.onNodeWithText("2 FOUND").assertIsDisplayed()
        composeRule.onNodeWithText("1 FOUND").assertIsDisplayed()
        composeRule.onAllNodesWithText("Hint: EAGLE").assertCountEquals(0)
    }

    @Test
    fun gameboardDisplaysHintForOperative() {
        composeRule.setContent {
            GameboardScreen(
                userRole = PlayerRoles.BLUE_OPERATIVE,
                gameState =
                    GameState(
                        currentHint = "EAGLE",
                        cards = listOf(GameCard("BERLIN", CardType.BLUE)),
                    ),
                onHintChange = {},
                onReveal = {},
            )
        }

        composeRule.onNodeWithText("Hint: EAGLE").assertIsDisplayed()
    }

    @Test
    fun gameboardDisplaysTeamChatMessages() {
        composeRule.setContent {
            GameboardScreen(
                userRole = PlayerRoles.BLUE_OPERATIVE,
                gameState =
                    GameState(
                        currentHint = "EAGLE",
                        cards = listOf(GameCard("BERLIN", CardType.BLUE)),
                        chatMessages =
                            listOf(
                                ChatDomainModel(
                                    sender = "Max",
                                    text = "Take Berlin",
                                    isFromMe = false,
                                ),
                            ),
                    ),
                onHintChange = {},
                onReveal = {},
            )
        }

        composeRule.onNodeWithText("Chat").performClick()
        composeRule.onNodeWithText("Max").assertIsDisplayed()
        composeRule.onNodeWithText("Take Berlin").assertIsDisplayed()
    }
}
