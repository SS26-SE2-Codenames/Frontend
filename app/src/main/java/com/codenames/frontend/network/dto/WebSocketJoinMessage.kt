package com.codenames.frontend.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WebSocketJoinMessage(
    @SerialName("name")
    val username: String,
    @SerialName("code")
    val lobbyCode: String,
)
