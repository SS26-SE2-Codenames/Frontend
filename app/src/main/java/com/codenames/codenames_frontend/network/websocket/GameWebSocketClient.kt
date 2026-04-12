package com.codenames.codenames_frontend.network.websocket

import com.codenames.codenames_frontend.network.dto.GameMessage
import com.codenames.codenames_frontend.network.dto.GuessMessage
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.conversions.kxserialization.json.withJsonConversions
import org.hildan.krossbow.stomp.use
import org.hildan.krossbow.websocket.WebSocketClient


import kotlinx.coroutines.flow.Flow
import org.hildan.krossbow.stomp.conversions.kxserialization.StompSessionWithKxSerialization
import org.hildan.krossbow.stomp.conversions.kxserialization.convertAndSend
import org.hildan.krossbow.websocket.builtin.builtIn
import org.hildan.krossbow.stomp.conversions.kxserialization.subscribe

const val BASE_URL = "ws://localhost:8080"

class GameWebSocketClient {
    val client = StompClient(WebSocketClient.builtIn())
    private lateinit var session : StompSessionWithKxSerialization

    suspend fun connectStomp(){
        session = client.connect(BASE_URL).withJsonConversions()
        //example lobby code, fetch from lobby viewmodel or so
        this.subscribeToLobby("ABCDE")
    }

    suspend fun sendGuess(msg: GuessMessage) {
        session.convertAndSend("/game/guess", msg, GuessMessage.serializer())
    }

    private suspend fun subscribeToLobby(lobbyCode: String) {
        session.use { s ->
            val messages: Flow<GameMessage> = s.subscribe("/game/${lobbyCode}", GameMessage.serializer())

            messages.collect { msg ->
                //füge msg zu Flow hinzu (GameViewModel)
            }
        }
    }
}