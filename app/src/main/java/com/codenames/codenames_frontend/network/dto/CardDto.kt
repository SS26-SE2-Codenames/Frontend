package com.codenames.codenames_frontend.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class CardDto(val word: String, val color: String, val isGuessed: Boolean)
