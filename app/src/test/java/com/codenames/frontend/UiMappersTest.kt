package com.codenames.frontend

import com.codenames.frontend.data.model.Player
import com.codenames.frontend.data.model.enums.Role
import com.codenames.frontend.data.model.enums.Team
import com.codenames.frontend.network.dto.CardDto
import com.codenames.frontend.ui.roles.PlayerRoles
import com.codenames.frontend.ui.screens.CardType
import com.codenames.frontend.ui.toGameCard
import com.codenames.frontend.ui.toPlayerRole
import org.junit.Assert.assertEquals
import org.junit.Test

class UiMappersTest {
    @Test
    fun cardDtoMapsBackendColorsToGameCards() {
        assertEquals(CardType.BLUE, CardDto("A", "BLUE", false).toGameCard().type)
        assertEquals(CardType.RED, CardDto("A", "RED", false).toGameCard().type)
        assertEquals(CardType.ASSASSIN, CardDto("A", "BLACK", false).toGameCard().type)
        assertEquals(CardType.NEUTRAL, CardDto("A", "HIDDEN", false).toGameCard().type)
    }

    @Test
    fun playerMapsToPlayerRole() {
        val player =
            Player(
                name = "Max",
                role = Role.SPYMASTER,
                team = Team.BLUE,
            )

        assertEquals(PlayerRoles.BLUE_SPYMASTER, player.toPlayerRole())
    }
}
