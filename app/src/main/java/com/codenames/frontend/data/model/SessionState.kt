package com.codenames.frontend.data.model

import com.codenames.frontend.data.model.enums.Role
import com.codenames.frontend.data.model.enums.Team
import java.util.UUID

data class SessionState(
    val username: String?,
    val userId: UUID?,
    val lobbyCode: String? = null,
    val lobbyRole: Role? = null,
    val lobbyTeam: Team? = null
)
