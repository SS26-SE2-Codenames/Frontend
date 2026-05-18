package com.codenames.frontend.data.model

import com.codenames.frontend.data.model.enums.CardType
import com.codenames.frontend.data.model.enums.Role
import com.codenames.frontend.data.model.enums.Team
import com.codenames.frontend.network.dto.CardDto
import com.codenames.frontend.network.dto.ClueDto
import com.codenames.frontend.network.dto.GameMessage
import com.codenames.frontend.ui.roles.PlayerRoles
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.test.assertNull

class MapperTest {
    @Test
    fun getCurrentTurn_redSpymaster_returnsRedSpymaster() {
        val message =
            GameMessage(
                currentTurn = Team.RED,
                currentPhase = Role.SPYMASTER,
            )

        val result = message.getCurrentTurn()

        assertEquals(PlayerRoles.RED_SPYMASTER, result)
    }

    @Test
    fun getCurrentTurn_redOperative_returnsRedOperative() {
        val message =
            GameMessage(
                currentTurn = Team.RED,
                currentPhase = Role.OPERATIVE,
            )

        val result = message.getCurrentTurn()

        assertEquals(PlayerRoles.RED_OPERATIVE, result)
    }

    @Test
    fun getCurrentTurn_blueSpymaster_returnsBlueSpymaster() {
        val message =
            GameMessage(
                currentTurn = Team.BLUE,
                currentPhase = Role.SPYMASTER,
            )

        val result = message.getCurrentTurn()

        assertEquals(PlayerRoles.BLUE_SPYMASTER, result)
    }

    @Test
    fun getCurrentTurn_blueOperative_returnsBlueOperative() {
        val message =
            GameMessage(
                currentTurn = Team.BLUE,
                currentPhase = Role.OPERATIVE,
            )

        val result = message.getCurrentTurn()

        assertEquals(PlayerRoles.BLUE_OPERATIVE, result)
    }

    @Test
    fun toGameState_withCurrentClue_mapsCorrectly() {
        val gameMessage =
            GameMessage(
                currentClue = ClueDto(word = "Animal", guessAmount = 3),
                cardList =
                    listOf(
                        CardDto(
                            word = "Dog",
                            color = CardType.RED,
                            isGuessed = false,
                        ),
                    ),
                currentTurn = Team.RED,
                currentPhase = Role.OPERATIVE,
                winner = null,
                remainingGuesses = 2,
            )

        val result = gameMessage.toGameState()

        assertEquals("Animal", result.currentHint)
        assertEquals(PlayerRoles.RED_OPERATIVE, result.currentTurn)
        assertEquals(2, result.remainingGuesses)
        assertNull(result.winner)

        assertEquals(1, result.cards.size)
        assertEquals("Dog", result.cards[0].word)
    }

    @Test
    fun toGameState_withNullCurrentClue_usesEmptyString() {
        val gameMessage =
            GameMessage(
                currentClue = null,
                cardList = emptyList(),
                currentTurn = Team.BLUE,
                currentPhase = Role.SPYMASTER,
                winner = null,
                remainingGuesses = 0,
            )

        val result = gameMessage.toGameState()

        assertEquals("", result.currentHint)
    }
}
