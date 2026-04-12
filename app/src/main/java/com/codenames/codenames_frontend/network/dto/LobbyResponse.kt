package com.codenames.codenames_frontend.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class LobbyResponse(val lobbyCode: String, val playerList: List<String>)
