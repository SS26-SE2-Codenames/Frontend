package com.codenames.codenames_frontend.data.model

import com.codenames.codenames_frontend.data.model.enums.Role
import com.codenames.codenames_frontend.data.model.enums.Team

data class Player(
    val name: String,
    val role: Role?,
    val team: Team?,
    val isHost: Boolean = false,
    val isReady: Boolean = false //if we want to add an isReady Button to UI and wait, until every Player is ready with game start
)