package com.codenames.frontend.data.repository

import com.codenames.frontend.data.model.enums.Team
import com.codenames.frontend.network.dto.ClueMessageDto
import com.codenames.frontend.network.dto.StartGameMessage
import com.codenames.frontend.network.websocket.GameWebSocketHandler
import javax.inject.Inject

class GameRepository
    @Inject
    constructor(
        private val webSocketHandler: GameWebSocketHandler,
    ) {
        suspend fun startGame(lobbyCode: String) {
            val msg = StartGameMessage(lobbyCode)
            webSocketHandler.startGame(msg)
        }

        suspend fun submitClue(
            lobbyCode: String,
            word: String,
            count: Int,
            currentTurn: Team,
        ) {
            val msg =
                ClueMessageDto(
                    lobbyCode = lobbyCode,
                    word = word,
                    guessAmount = count,
                    currentTurn = currentTurn,
                )
            webSocketHandler.sendClue(msg)
        }
    }
