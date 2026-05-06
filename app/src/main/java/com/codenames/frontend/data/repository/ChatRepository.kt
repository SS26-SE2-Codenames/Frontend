package com.codenames.frontend.data.repository

import com.codenames.frontend.data.model.ChatDomainModel
import com.codenames.frontend.network.dto.ChatMessageDto
import com.codenames.frontend.network.websocket.GameWebSocketHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ChatRepository
    @Inject
    constructor(
        private val webSocketHandler: GameWebSocketHandler,
    ) {
        fun observeChat(
            topic: String,
            currentUsername: String,
        ): Flow<ChatDomainModel> =
            flow {
                val subscriptionFlow = webSocketHandler.subscribeToChat(topic)

                subscriptionFlow.collect { dto ->
                    emit(
                        ChatDomainModel(
                            sender = dto.senderUsername,
                            text = dto.content,
                            isFromMe = dto.senderUsername == currentUsername,
                        ),
                    )
                }
            }

        suspend fun sendMessage(
            destination: String,
            username: String,
            text: String,
        ) {
            val dto = ChatMessageDto(senderUsername = username, content = text)
            webSocketHandler.sendChatMessage(destination, dto)
        }
    }
