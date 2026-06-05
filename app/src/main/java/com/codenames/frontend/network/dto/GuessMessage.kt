package com.codenames.frontend.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class GuessMessage(
    val lobbyCode: String,
    val position: Int,
    val currentTurn: String,
)
