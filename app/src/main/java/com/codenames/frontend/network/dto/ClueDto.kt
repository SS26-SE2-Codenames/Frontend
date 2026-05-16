package com.codenames.frontend.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class ClueDto(val word: String, val count: Int) {
}