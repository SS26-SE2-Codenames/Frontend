package com.codenames.frontend.network.websocket

import android.util.Log
import com.codenames.frontend.data.model.enums.Team
import com.codenames.frontend.network.dto.ChatMessageDto
import com.codenames.frontend.network.dto.ClueMessageDto
import com.codenames.frontend.network.dto.GameMessage
import com.codenames.frontend.network.dto.GuessMessage
import com.codenames.frontend.network.dto.StartGameMessage
import com.codenames.frontend.network.dto.WebSocketJoinMessage
import kotlinx.coroutines.flow.Flow
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.conversions.kxserialization.StompSessionWithKxSerialization
import org.hildan.krossbow.stomp.conversions.kxserialization.convertAndSend
import org.hildan.krossbow.stomp.conversions.kxserialization.json.withJsonConversions
import org.hildan.krossbow.stomp.conversions.kxserialization.subscribe
import javax.inject.Inject
import javax.inject.Singleton

const val BASE_URL = "ws://10.0.2.2:8080/ws-fallback"

@Singleton
class GameWebSocketHandler
    @Inject
    constructor(
        private val client: StompClient,
    ) {
        lateinit var session: StompSessionWithKxSerialization

        suspend fun connectStomp() {
            session = client.connect(BASE_URL).withJsonConversions()
            Log.d("WebSocket", "Connected to Websocket, session: $session")
        }

        suspend fun startGame(msg: StartGameMessage) {
            session.convertAndSend("/app/start-game", msg, StartGameMessage.serializer())
        }

        suspend fun sendGuess(msg: GuessMessage) {
            session.convertAndSend("/app/game/guess", msg, GuessMessage.serializer())
        }

        @Suppress("kotlin:S6309")
        suspend fun subscribeToLobby(lobbyCode: String): Flow<GameMessage> =
            session.subscribe("/topic/game/$lobbyCode", GameMessage.serializer())

        suspend fun sendReconnectMessage(msg: WebSocketJoinMessage) {
            session.convertAndSend("/app/join", msg, WebSocketJoinMessage.serializer())
        }

        @Suppress("kotlin:S6309")
        suspend fun subscribeToChat(topicPath: String): Flow<ChatMessageDto> = session.subscribe(topicPath, ChatMessageDto.serializer())

        suspend fun sendChatMessage(
            destination: String,
            msg: ChatMessageDto,
        ) {
            session.convertAndSend(destination, msg, ChatMessageDto.serializer())
        }

        suspend fun sendClue(
            lobbyCode: String,
            word: String,
            guessAmount: Int,
            currentTurn: Team,
        ) {
            val msg =
                ClueMessageDto(
                    lobbyCode = lobbyCode,
                    word = word,
                    guessAmount = guessAmount,
                    currentTurn = currentTurn,
                )
            session.convertAndSend("/app/submit-clue", msg, ClueMessageDto.serializer())
        }
    }
