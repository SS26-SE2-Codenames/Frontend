package com.codenames.frontend.network.dto

import com.codenames.frontend.data.model.enums.Team
import kotlinx.serialization.Serializable

@Serializable
data class ClueMessageDto(
    val lobbyCode: String,
    val word: String,
    val guessAmount: Int,
    val currentTurn: Team
)