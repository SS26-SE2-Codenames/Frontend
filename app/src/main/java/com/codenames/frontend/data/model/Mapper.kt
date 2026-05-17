package com.codenames.frontend.data.model

import com.codenames.frontend.network.dto.CardDto
import com.codenames.frontend.network.dto.GameMessage
import com.codenames.frontend.network.dto.LobbyResponse
import com.codenames.frontend.network.dto.PlayerDto

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
        currentHint = currentClue ?: "",
        cards = cardList.map { it.toGameCard() },
        currentTurn = currentTurn,
        winner = winner,
        remainingGuesses = remainingGuesses,
    )

fun CardDto.toGameCard(): GameCard =
    GameCard(
        word = word,
        type = color,
        revealed = isGuessed,
    )
