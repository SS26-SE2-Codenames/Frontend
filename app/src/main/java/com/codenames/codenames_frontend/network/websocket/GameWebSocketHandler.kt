package com.codenames.codenames_frontend.network.websocket

import com.codenames.codenames_frontend.network.dto.GameMessage
import com.codenames.codenames_frontend.network.dto.GuessMessage
import com.codenames.codenames_frontend.network.dto.WebSocketJoinMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.conversions.kxserialization.StompSessionWithKxSerialization
import org.hildan.krossbow.stomp.conversions.kxserialization.convertAndSend
import org.hildan.krossbow.stomp.conversions.kxserialization.json.withJsonConversions
import org.hildan.krossbow.stomp.conversions.kxserialization.subscribe
import javax.inject.Inject

const val BASE_URL = "ws://10.0.2.2:8080/ws"

class GameWebSocketHandler @Inject constructor(private val client: StompClient) {
    lateinit var session: StompSessionWithKxSerialization

    //called by GameViewModel
    suspend fun connectStomp() {
        session = client.connect(BASE_URL).withJsonConversions()
    }

    suspend fun sendGuess(msg: GuessMessage) {
        session.convertAndSend("/game/guess", msg, GuessMessage.serializer())
    }

    //suspend is necessary here, because api calls are suspending functions
    suspend fun subscribeToLobby(lobbyCode: String): Flow<List<String>> {
        return session.subscribe(
            "/topic/lobby/$lobbyCode",
            ListSerializer(String.serializer())
        )
    }

    suspend fun sendLobbyJoinMessage(msg: WebSocketJoinMessage) {
        session.convertAndSend(
            "/app/join",
            msg,
            WebSocketJoinMessage.serializer()
        )
    }
}