package com.codenames.frontend.network.dto

import com.codenames.frontend.data.model.enums.ChatMessageType
import kotlinx.serialization.Serializable


@Serializable
data class ChatMessageDto(
    val senderUsername: String,
    val content: String,
    val type: ChatMessageType = ChatMessageType.CHAT
)