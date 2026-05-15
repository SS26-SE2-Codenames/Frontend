package com.codenames.frontend.ui

import com.codenames.frontend.data.model.Player
import com.codenames.frontend.data.model.enums.Role
import com.codenames.frontend.data.model.enums.Team
import com.codenames.frontend.network.dto.CardDto
import com.codenames.frontend.ui.roles.PlayerRoles
import com.codenames.frontend.ui.screens.CardType
import com.codenames.frontend.ui.screens.GameCard

fun CardDto.toGameCard(): GameCard =
    GameCard(
        word = word,
        type =
            when (color.uppercase()) {
                "BLUE" -> CardType.BLUE
                "RED" -> CardType.RED
                "BLACK" -> CardType.ASSASSIN
                "ASSASSIN" -> CardType.ASSASSIN
                "WHITE" -> CardType.NEUTRAL
                "NEUTRAL" -> CardType.NEUTRAL
                else -> CardType.NEUTRAL
            },
        revealed = isGuessed,
    )

fun Player.toPlayerRole(): PlayerRoles =
    when (team to role) {
        Team.BLUE to Role.OPERATIVE -> PlayerRoles.BLUE_OPERATIVE
        Team.BLUE to Role.SPYMASTER -> PlayerRoles.BLUE_SPYMASTER
        Team.RED to Role.OPERATIVE -> PlayerRoles.RED_OPERATIVE
        Team.RED to Role.SPYMASTER -> PlayerRoles.RED_SPYMASTER
        else -> PlayerRoles.NONE
    }

fun PlayerRoles.toTeamAndRole(): Pair<Team, Role>? =
    when (this) {
        PlayerRoles.BLUE_OPERATIVE -> Team.BLUE to Role.OPERATIVE
        PlayerRoles.BLUE_SPYMASTER -> Team.BLUE to Role.SPYMASTER
        PlayerRoles.RED_OPERATIVE -> Team.RED to Role.OPERATIVE
        PlayerRoles.RED_SPYMASTER -> Team.RED to Role.SPYMASTER
        PlayerRoles.NONE -> null
    }
