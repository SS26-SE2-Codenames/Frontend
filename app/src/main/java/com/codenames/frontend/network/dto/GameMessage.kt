package com.codenames.frontend.network.dto

import com.codenames.frontend.data.model.enums.Role
import com.codenames.frontend.data.model.enums.Team
import kotlinx.serialization.Serializable

@Serializable
data class GameMessage(
    val winner: Team? = null,
    val currentTurn: Team? = null,
    val currentPhase: Role? = null,
    val currentRedFound: Int = 0,
    val currentBlueFound: Int = 0,
    val currentClue: String? = null, //wird noch zu dto geändert
    val remainingGuesses: Int = 0,
    val cardList: List<CardDto> = emptyList(),
    val error: String? = null,
)
