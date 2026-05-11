package com.codenames.frontend.data.model

data class ChatDomainModel(
    val sender: String,
    val text: String,
    val isFromMe: Boolean,
)
