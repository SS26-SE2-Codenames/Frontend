package com.codenames.frontend.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class LobbyResponse(
    val lobbyCode: String,
    val playerList: List<PlayerDto>,
)
