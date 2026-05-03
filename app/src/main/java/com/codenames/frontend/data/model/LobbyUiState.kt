package com.codenames.frontend.data.model

data class LobbyUiState(
    val isLoading: Boolean = false,
    val lobbyCode: String? = null,
    val players: List<Player> = emptyList(),
    val error: String? = null,
    val isGameStarted: Boolean = false,
)
