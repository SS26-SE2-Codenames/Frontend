package com.codenames.frontend.data.model

import com.codenames.frontend.network.dto.LobbyResponse
import com.codenames.frontend.network.dto.PlayerDto

fun LobbyResponse.toLobbyState(): LobbyUiState =
    LobbyUiState(
        lobbyCode = lobbyCode,
        players = playerList.map { it.toUi() },
    )

fun PlayerDto.toUi(): Player =
    Player(
        name = username,
        role = role,
        team = team,
        isHost = isHost,
        isReady = false, // if we add this functionality
    )
