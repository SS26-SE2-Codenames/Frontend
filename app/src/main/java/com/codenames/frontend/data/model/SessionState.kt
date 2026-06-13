package com.codenames.frontend.data.model

import com.codenames.frontend.data.model.enums.Role
import com.codenames.frontend.data.model.enums.Team

data class SessionState(
    val lobbyCode: String? = null,
    val lobbyRole: Role? = null,
    val lobbyTeam: Team? = null,
    val consumed: Boolean = false
)
