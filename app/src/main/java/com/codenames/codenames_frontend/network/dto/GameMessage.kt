package com.codenames.codenames_frontend.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class GameMessage(val winner: String, val currentTurn: String, val currentRedFound: Int, val currentBlueFound: Int, val currentClue: String, val remainingGuesses: Int, val cardList: List<CardDto>)
