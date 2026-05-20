package com.codenames.frontend.data.model

import com.codenames.frontend.data.model.enums.CardType
import com.codenames.frontend.data.model.enums.Role
import com.codenames.frontend.data.model.enums.Team
import com.codenames.frontend.network.dto.CardDto
import com.codenames.frontend.network.dto.GameMessage
import com.codenames.frontend.network.dto.LobbyResponse
import com.codenames.frontend.network.dto.PlayerDto
import com.codenames.frontend.ui.roles.PlayerRoles

fun LobbyResponse.toLobbyState(): LobbyUiState =
    LobbyUiState(
        lobbyCode = lobbyCode,
        players = playerList.map { it.toUi() },
        isGameStarted = isStarted,
    )

fun PlayerDto.toUi(): Player =
    Player(
        name = username,
        role = role,
        team = team,
        isHost = isHost,
        isReady = false,
    )

fun GameMessage.toGameState(): GameState {
    val cards = cardList.map { it.toGameCard() }

    return GameState(
        currentHint = currentClue?.word ?: "",
        cards = cards,
        currentTurn = getCurrentTurn(),
        winner = winner,
        remainingGuesses = currentClue?.guessAmount ?: 0,
        currentRedFound = cards.count { it.type == CardType.RED && it.revealed },
        currentBlueFound = cards.count { it.type == CardType.BLUE && it.revealed },
    )
}

fun CardDto.toGameCard(): GameCard =
    GameCard(
        word = word,
        type = color,
        revealed = isGuessed,
    )

fun GameMessage.getCurrentTurn(): PlayerRoles {
    if (currentTurn == Team.RED) {
        if (currentPhase == Role.SPYMASTER) return PlayerRoles.RED_SPYMASTER
        return PlayerRoles.RED_OPERATIVE
    }
    if (currentPhase == Role.SPYMASTER) return PlayerRoles.BLUE_SPYMASTER
    return PlayerRoles.BLUE_OPERATIVE
}
