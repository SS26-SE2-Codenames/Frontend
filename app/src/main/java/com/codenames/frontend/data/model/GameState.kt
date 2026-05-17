package com.codenames.frontend.data.model

data class GameState(
    val currentHint: String,
    val cards: List<GameCard>,
    val currentTurn: String = "",
    val winner: String? = null,
    val remainingGuesses: Int = 0,
    val currentRedFound: Int = 0,
    val currentBlueFound: Int = 0,
    val chatMessages: List<ChatDomainModel> = emptyList(),
)