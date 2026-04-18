package com.codenames.codenames_frontend.data.model

import com.codenames.codenames_frontend.network.dto.LobbyResponse
import com.codenames.codenames_frontend.network.dto.PlayerDto

fun LobbyResponse.toLobbyState(): LobbyUiState {
    return LobbyUiState(
        lobbyCode = lobbyCode,
        players = playerList.map { it.toUi() }
    )
}

fun PlayerDto.toUi(): Player {
    return Player(
        name = username,
        role = role,
        team = team,
        isHost = isHost,
        isReady = false // if we add this functionality
    )
}