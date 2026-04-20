package com.codenames.codenames_frontend.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class WebSocketJoinMessage(val username: String, val lobbyCode: String) {
}