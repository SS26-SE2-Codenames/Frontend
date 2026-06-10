package com.codenames.frontend.data.repository

import com.codenames.frontend.data.model.ChatDomainModel
import com.codenames.frontend.network.dto.ChatMessageDto
import com.codenames.frontend.network.websocket.GameWebSocketController
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ChatRepository
@Inject
constructor(
    private val webSocketHandler: GameWebSocketController,
) {
    fun observeLobbyChat(
        lobbyCode: String,
        currentUsername: String,
    ): Flow<ChatDomainModel> =
        observeChat(
            topic = "/topic/chat/$lobbyCode",
            currentUsername = currentUsername,
        )

    fun observeTeamChat(
        lobbyCode: String,
        team: String,
        currentUsername: String,
    ): Flow<ChatDomainModel> =
        observeChat(
            topic = "/topic/chat/$lobbyCode/$team",
            currentUsername = currentUsername,
        )

    fun observeOperativeChat(
        lobbyCode: String,
        team: String,
        currentUsername: String,
    ): Flow<ChatDomainModel> =
        observeChat(
            topic = "/topic/chat/$lobbyCode/$team/operative",
            currentUsername = currentUsername,
        )

    private fun observeChat(
        topic: String,
        currentUsername: String,
    ): Flow<ChatDomainModel> =
        flow {
            webSocketHandler.subscribeToChat(topic).collect { dto ->
                emit(
                    ChatDomainModel(
                        sender = dto.senderUsername,
                        text = dto.content,
                        isFromMe = dto.senderUsername == currentUsername,
                    ),
                )
            }
        }

    suspend fun sendLobbyMessage(
        lobbyCode: String,
        username: String,
        text: String,
    ) {
        sendMessage(
            destination = "/app/chat/$lobbyCode",
            username = username,
            text = text,
        )
    }

    suspend fun sendTeamMessage(
        lobbyCode: String,
        team: String,
        username: String,
        text: String,
    ) {
        sendMessage(
            destination = "/app/chat/$lobbyCode/$team",
            username = username,
            text = text,
        )
    }

    suspend fun sendOperativeMessage(
        lobbyCode: String,
        team: String,
        username: String,
        text: String,
    ) {
        sendMessage(
            destination = "/app/chat/$lobbyCode/$team/operative",
            username = username,
            text = text,
        )
    }

    private suspend fun sendMessage(
        destination: String,
        username: String,
        text: String,
    ) {
        val dto = ChatMessageDto(
            senderUsername = username,
            content = text,
        )

        webSocketHandler.sendChatMessage(destination, dto)
    }
}