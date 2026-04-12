package com.codenames.codenames_frontend.network.websocket

import com.codenames.codenames_frontend.network.dto.GameMessage
import com.codenames.codenames_frontend.network.dto.GuessMessage
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.conversions.kxserialization.json.withJsonConversions
import org.hildan.krossbow.stomp.use
import org.hildan.krossbow.websocket.WebSocketClient
import org.hildan.krossbow.websocket.builtin.builtIn
import org.hildan.krossbow.stomp.subscribe

import kotlinx.coroutines.flow.Flow

const val BASE_URL = "ws://localhost:8080"

class GameWebSocketClient {
    val client = StompClient(WebSocketClient.builtIn())
    private lateinit var session : StompSession
    private lateinit var jsonStompSession : StompSession

    suspend fun connectStomp(){
        session = client.connect(BASE_URL)
        jsonStompSession = session.withJsonConversions()
        this.subscribeToLobby()
    }

    suspend fun sendGuess(msg: GuessMessage) {
        jsonStompSession.use { s ->
            s.convertAndSend("/game/guess", msg, GuessMessage.serializer())
        }
    }

    private suspend fun subscribeToLobby(lobbyCode: String) {
        jsonStompSession.use { s ->
            val messages: Flow<GameMessage> = s.subscribe("/game/${lobbyCode}")
        }
    }
}