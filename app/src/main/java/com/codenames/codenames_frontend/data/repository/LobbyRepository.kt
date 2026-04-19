package com.codenames.codenames_frontend.data.repository

import com.codenames.codenames_frontend.data.model.enums.Role
import com.codenames.codenames_frontend.data.model.enums.Team
import com.codenames.codenames_frontend.network.api.LobbyApi
import com.codenames.codenames_frontend.network.dto.LobbyResponse
import com.codenames.codenames_frontend.network.dto.PlayerDto
import javax.inject.Inject

class LobbyRepository @Inject constructor(private val api: LobbyApi) {

    suspend fun createLobby(username: String): LobbyResponse {
        return api.createLobby(username)
    }

    suspend fun leaveLobby(username: String, lobbyCode: String): LobbyResponse {
        return api.leaveLobby(username, lobbyCode)
    }

    suspend fun joinLobby(username: String, lobbyCode: String): LobbyResponse {
        return api.joinLobby(username, lobbyCode)
    }

    suspend fun getLobbyInfo(lobbyCode: String): LobbyResponse {
        return api.getLobbyInfo(lobbyCode)
    }

    suspend fun changeRole(
        username: String,
        lobbyCode: String,
        role: Role,
        team: Team
    ): LobbyResponse {
        val player = PlayerDto(username, role, team, false)
        return api.changeRole(lobbyCode, player)
    }
}