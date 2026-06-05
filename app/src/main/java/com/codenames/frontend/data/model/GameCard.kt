package com.codenames.frontend.data.model

import com.codenames.frontend.data.model.enums.CardType

data class GameCard(
    val word: String,
    val type: CardType,
    val revealed: Boolean = false,
)
