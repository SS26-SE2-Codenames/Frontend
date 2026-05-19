package com.codenames.frontend

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.codenames.frontend.data.model.ChatDomainModel
import com.codenames.frontend.data.model.ChatLists
import com.codenames.frontend.data.model.GameCard
import com.codenames.frontend.data.model.GameState
import com.codenames.frontend.data.model.enums.CardType
import com.codenames.frontend.data.model.enums.ChatTab
import com.codenames.frontend.ui.roles.PlayerRoles
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
                        currentTurn = PlayerRoles.BLUE_OPERATIVE,
                        remainingGuesses = 3,
                        currentBlueFound = 0,
                        currentRedFound = 1,
                        cards = cards,
                    ),
                onHintChange = { _, _ -> },
                onReveal = {},
            )
        }

        composeRule.onNodeWithText("BERLIN").assertIsDisplayed()
        composeRule.onNodeWithText("ROME").assertIsDisplayed()
        composeRule.onNodeWithText("Turn: BLUE_OPERATIVE | Guesses: 3").assertIsDisplayed()
        composeRule.onNodeWithText("0 FOUND").assertIsDisplayed()
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
                onHintChange = { _, _ -> },
                onReveal = {},
            )
        }

        composeRule.onNodeWithText("Hint: EAGLE").assertIsDisplayed()
    }

    @Test
    fun spymasterCanOpenLobbyChat() {
        composeRule.setContent {
            GameboardScreen(
                userRole = PlayerRoles.BLUE_SPYMASTER,
                gameState =
                    GameState(
                        currentHint = "EAGLE",
                        cards = listOf(GameCard("BERLIN", CardType.BLUE)),
                        chatLists =
                            ChatLists(
                                lobbyMessages =
                                    listOf(
                                        ChatDomainModel(
                                            sender = "Anna",
                                            text = "Lobby message",
                                            isFromMe = false,
                                        ),
                                    ),
                            ),
                        availableChatTabs = listOf(ChatTab.GLOBAL),
                    ),
                onHintChange = { _, _ -> },
                onReveal = {},
            )
        }

        composeRule.onNodeWithText("Chat").performClick()
        composeRule.onNodeWithText("Global Chat").assertIsDisplayed()
        composeRule.onNodeWithText("Anna").assertIsDisplayed()
        composeRule.onNodeWithText("Lobby message").assertIsDisplayed()
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
                        chatLists =
                            ChatLists(
                                teamMessages =
                                    listOf(
                                        ChatDomainModel(
                                            sender = "Max",
                                            text = "Take Berlin",
                                            isFromMe = false,
                                        ),
                                    ),
                            ),
                        availableChatTabs = listOf(ChatTab.GLOBAL, ChatTab.TEAM),
                    ),
                onHintChange = { _, _ -> },
                onReveal = {},
            )
        }

        composeRule.onNodeWithText("Chat").performClick()
        composeRule.onNodeWithText("Team").performClick()
        composeRule.onNodeWithText("Max").assertIsDisplayed()
        composeRule.onNodeWithText("Take Berlin").assertIsDisplayed()
    }

    @Test
    fun operativeChatTabIsHiddenWhenNotAvailable() {
        composeRule.setContent {
            GameboardScreen(
                userRole = PlayerRoles.BLUE_OPERATIVE,
                gameState =
                    GameState(
                        currentHint = "EAGLE",
                        cards = listOf(GameCard("BERLIN", CardType.BLUE)),
                        availableChatTabs = listOf(ChatTab.GLOBAL, ChatTab.TEAM),
                    ),
                onHintChange = { _, _ -> },
                onReveal = {},
            )
        }

        composeRule.onNodeWithText("Chat").performClick()
        composeRule.onAllNodesWithText("Operatives").assertCountEquals(0)
    }
}
