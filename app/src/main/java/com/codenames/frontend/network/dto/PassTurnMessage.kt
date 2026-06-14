package com.codenames.frontend.network.dto

import com.codenames.frontend.data.model.enums.Team
import kotlinx.serialization.Serializable

@Serializable
data class PassTurnMessage(
    val lobbyCode: String,
    val currentTurn: Team,
)
