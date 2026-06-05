package com.codenames.frontend.network.dto

import com.codenames.frontend.data.model.enums.Role
import com.codenames.frontend.data.model.enums.Team
import kotlinx.serialization.Serializable

@Serializable
data class GameMessage(
    val winner: Team? = null,
    val currentTurn: Team? = null,
    val currentPhase: Role? = null,
    val currentClue: ClueDto? = null,
    val cardList: List<CardDto> = emptyList(),
    val error: String? = null,
)
