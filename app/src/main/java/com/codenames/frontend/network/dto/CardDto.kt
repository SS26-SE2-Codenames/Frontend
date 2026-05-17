package com.codenames.frontend.network.dto

import com.codenames.frontend.data.model.enums.CardType
import kotlinx.serialization.Serializable

@Serializable
data class CardDto(
    val word: String,
    val color: CardType,
    val isGuessed: Boolean,
)
