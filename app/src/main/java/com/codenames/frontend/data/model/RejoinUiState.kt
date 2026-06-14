package com.codenames.frontend.data.model

import com.codenames.frontend.data.model.enums.ConnectionState
import java.util.UUID

data class RejoinUiState(
    val availableRejoinState: RejoinState.Available,
    val connectionState: ConnectionState,
    val gameState: GameState,
    val username: String,
    val userId: UUID?,
    val lobbyCode: String,
)
