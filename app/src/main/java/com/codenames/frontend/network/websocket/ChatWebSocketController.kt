package com.codenames.frontend.network.websocket

import com.codenames.frontend.network.dto.ChatMessageDto
import kotlinx.coroutines.flow.Flow
import org.hildan.krossbow.stomp.conversions.kxserialization.convertAndSend
import org.hildan.krossbow.stomp.conversions.kxserialization.subscribe
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatWebSocketController
    @Inject
    constructor(
        private val webSocketSessionManager: WebSocketSessionManager,
    ) {
        @Suppress("kotlin:S6309")
        suspend fun subscribeToChat(topicPath: String): Flow<ChatMessageDto> =
            webSocketSessionManager.getSession().subscribe(
                topicPath,
                ChatMessageDto.serializer(),
            )

        suspend fun sendChatMessage(
            destination: String,
            msg: ChatMessageDto,
        ) {
            webSocketSessionManager.getSession().convertAndSend(
                destination,
                msg,
                ChatMessageDto.serializer(),
            )
        }
    }
