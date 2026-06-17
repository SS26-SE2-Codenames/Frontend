package com.codenames.frontend.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class LobbyResponse(
    val lobbyCode: String = "",
    val playerList: List<PlayerDto>? = emptyList(),
    val isStarted: Boolean = false,
    val message: String = "",
    val uuid: String? = null,
)
