package com.codenames.codenames_frontend.data.model

import com.codenames.codenames_frontend.data.model.Player

data class LobbyUiState(
    val isLoading: Boolean = false,
    val lobbyCode: String? = null,
    val players: List<Player> = emptyList(),
    val error: String? = null,
    val isGameStarted: Boolean = false
)
