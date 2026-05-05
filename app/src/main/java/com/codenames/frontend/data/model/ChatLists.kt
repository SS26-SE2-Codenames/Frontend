package com.codenames.frontend.data.model

data class ChatLists (
    val lobbyMessages: List<ChatDomainModel> = emptyList(),
    val teamMessages: List<ChatDomainModel> = emptyList(),
    val operativeMessages: List<ChatDomainModel> = emptyList()
)