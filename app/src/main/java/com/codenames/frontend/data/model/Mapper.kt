package com.codenames.frontend.data.model

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
        isReady = false, // if we add this functionality
    )

fun GameMessage.toGameState(): GameState =
    GameState(
        currentHint = currentClue?.word ?: "",
        cards = cardList.map { it.toGameCard() },
        currentTurn = getCurrentTurn(),
        winner = winner,
        remainingGuesses = currentClue?.guessAmount ?: 0,
    )

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
