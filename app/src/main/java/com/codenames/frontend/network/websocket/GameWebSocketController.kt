package com.codenames.frontend.network.websocket

import com.codenames.frontend.network.dto.ChatMessageDto
import com.codenames.frontend.network.dto.ClueMessageDto
import com.codenames.frontend.network.dto.GameMessage
import com.codenames.frontend.network.dto.GuessMessage
import com.codenames.frontend.network.dto.StartGameMessage
import com.codenames.frontend.network.dto.WebSocketJoinMessage
import kotlinx.coroutines.flow.Flow
import org.hildan.krossbow.stomp.conversions.kxserialization.convertAndSend
import org.hildan.krossbow.stomp.conversions.kxserialization.subscribe
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameWebSocketController
    @Inject
    constructor(
        private val webSocketSessionManager: WebSocketSessionManager,
    ) {
        suspend fun connectStomp() {
            webSocketSessionManager.connectStomp()
        }

        suspend fun startGame(msg: StartGameMessage) {
            webSocketSessionManager.getSession().convertAndSend("/app/start-game", msg, StartGameMessage.serializer())
        }

        @Suppress("kotlin:S6309")
        suspend fun subscribeToLobby(lobbyCode: String): Flow<GameMessage> =
            webSocketSessionManager.getSession().subscribe("/topic/game/$lobbyCode", GameMessage.serializer())

        suspend fun sendReconnectMessage(msg: WebSocketJoinMessage) {
            webSocketSessionManager.getSession().convertAndSend("/app/join", msg, WebSocketJoinMessage.serializer())
        }

        @Suppress("kotlin:S6309")
        suspend fun subscribeToChat(topicPath: String): Flow<ChatMessageDto> =
            webSocketSessionManager.getSession().subscribe(topicPath, ChatMessageDto.serializer())

        suspend fun sendChatMessage(
            destination: String,
            msg: ChatMessageDto,
        ) {
            webSocketSessionManager.getSession().convertAndSend(destination, msg, ChatMessageDto.serializer())
        }

        suspend fun sendClue(msg: ClueMessageDto) {
            webSocketSessionManager.getSession().convertAndSend("/app/submit-clue", msg, ClueMessageDto.serializer())
        }

        suspend fun sendGuess(msg: GuessMessage) {
            webSocketSessionManager.getSession().convertAndSend("/app/reveal-card", msg, GuessMessage.serializer())
        }
    }
