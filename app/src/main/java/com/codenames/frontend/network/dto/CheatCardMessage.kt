package com.codenames.frontend.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class CheatCardMessage(
    val lobbyCode: String,
    val username: String,
    val positions: List<Int>,
)
