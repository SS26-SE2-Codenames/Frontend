package com.codenames.frontend.data.model

import com.codenames.frontend.data.model.enums.ChatTab
import kotlinx.serialization.Serializable

@Serializable
data class ChatMessage(
    val id: Int,
    val sender: String,
    val message: String,
    val timestamp: String,
    val chatTab: ChatTab,
    val isSystemMessage: Boolean = false,
    val isFromMe: Boolean = false,
)
