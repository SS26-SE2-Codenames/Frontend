package com.codenames.frontend.data.model

data class LobbyUiState(
    val isLoading: Boolean = false,
    val lobbyCode: String? = null,
    val players: List<Player> = emptyList(),
    val error: String? = null,
    val isGameStarted: Boolean = false,
    val blueOperatives: List<String> = emptyList(),
    val blueSpymasters: List<String> = emptyList(),
    val redOperatives: List<String> = emptyList(),
    val redSpymasters: List<String> = emptyList(),
)
