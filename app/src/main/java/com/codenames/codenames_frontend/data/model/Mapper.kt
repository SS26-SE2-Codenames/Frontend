package com.codenames.codenames_frontend.data.model

import com.codenames.codenames_frontend.network.dto.LobbyResponse

fun LobbyResponse.mapToLobby(): Lobby{
    return Lobby(lobbyCode = lobbyCode, playerList = playerList)
}