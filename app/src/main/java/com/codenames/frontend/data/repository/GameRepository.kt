package com.codenames.frontend.data.repository

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
    }
