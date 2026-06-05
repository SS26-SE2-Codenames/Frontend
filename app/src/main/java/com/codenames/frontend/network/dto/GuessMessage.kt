package com.codenames.frontend.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class GuessMessage(
    val username: String,
    val word: String,
    val number: Int,
)
