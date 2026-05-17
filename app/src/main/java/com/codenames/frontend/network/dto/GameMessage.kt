package com.codenames.frontend.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class GameMessage(
    val winner: String? = null,
    val currentTurn: String = "",
    val currentRedFound: Int = 0,
    val currentBlueFound: Int = 0,
    val currentClue: String? = null,
    val remainingGuesses: Int = 0,
    val cardList: List<CardDto> = emptyList(),
    val error: String? = null
)
