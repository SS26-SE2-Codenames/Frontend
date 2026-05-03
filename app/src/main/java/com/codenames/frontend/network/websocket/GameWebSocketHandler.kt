package com.codenames.frontend.network.websocket

import com.codenames.frontend.network.dto.GameMessage
import com.codenames.frontend.network.dto.GuessMessage
import com.codenames.frontend.network.dto.WebSocketJoinMessage
import kotlinx.coroutines.flow.Flow
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.conversions.kxserialization.StompSessionWithKxSerialization
import org.hildan.krossbow.stomp.conversions.kxserialization.convertAndSend
import org.hildan.krossbow.stomp.conversions.kxserialization.json.withJsonConversions
import org.hildan.krossbow.stomp.conversions.kxserialization.subscribe
import javax.inject.Inject

const val BASE_URL = "ws://localhost:8080/ws"

class GameWebSocketHandler
    @Inject
    constructor(
        private val client: StompClient,
    ) {
        lateinit var session: StompSessionWithKxSerialization

        // called by GameViewModel
        suspend fun connectStomp() {
            session = client.connect(BASE_URL).withJsonConversions()
        }

        suspend fun sendGuess(msg: GuessMessage) {
            session.convertAndSend("/game/guess", msg, GuessMessage.serializer())
        }

        // suspend is necessary here, because api calls are suspending functions
        @Suppress("kotlin:S6309")
        suspend fun subscribeToLobby(lobbyCode: String): Flow<GameMessage> = session.subscribe("/game/$lobbyCode", GameMessage.serializer())

        suspend fun sendLobbyJoinMessage(msg: WebSocketJoinMessage) {
            val lobbyCode = msg.lobbyCode
            session.convertAndSend("app/$lobbyCode/join", msg, WebSocketJoinMessage.serializer())
        }
    }
