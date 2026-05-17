package com.codenames.frontend.data.model

data class GameState(
    val currentHint: String = "",
    val cards: List<GameCard> = emptyList(),
    val currentTurn: String = "",
    val winner: String? = null,
    val remainingGuesses: Int = 0,
)