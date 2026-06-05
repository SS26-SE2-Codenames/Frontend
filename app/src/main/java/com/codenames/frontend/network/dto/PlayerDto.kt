package com.codenames.frontend.network.dto

import com.codenames.frontend.data.model.enums.Role
import com.codenames.frontend.data.model.enums.Team
import kotlinx.serialization.Serializable

@Serializable
data class PlayerDto(
    val username: String,
    val role: Role? = null,
    val team: Team? = null,
    val isHost: Boolean,
)
