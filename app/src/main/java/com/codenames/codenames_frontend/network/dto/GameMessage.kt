package com.codenames.codenames_frontend.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class GameMessage(
    val winner: String = "",
    val currentTurn: String = "",
    val currentRedFound: Int = 0,
    val currentBlueFound: Int = 0,
    val currentClue: String = "",
    val remainingGuesses: Int = 0,
    val cardList: List<CardDto> = emptyList()
)
