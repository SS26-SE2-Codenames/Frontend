package com.codenames.frontend.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class ExposeCheatMessage(
    val lobbyCode: String,
    val username: String,
)
