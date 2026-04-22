package com.codenames.frontend.data.repository

import com.codenames.frontend.data.model.enums.Role
import com.codenames.frontend.data.model.enums.Team
import com.codenames.frontend.network.api.LobbyApi
import com.codenames.frontend.network.dto.LobbyResponse
import com.codenames.frontend.network.dto.PlayerDto
import javax.inject.Inject

class LobbyRepository
    @Inject
    constructor(
        private val api: LobbyApi,
    ) {
        suspend fun createLobby(username: String): LobbyResponse = api.createLobby(username)

        suspend fun leaveLobby(
            username: String,
            lobbyCode: String,
        ): LobbyResponse = api.leaveLobby(username, lobbyCode)

        suspend fun joinLobby(
            username: String,
            lobbyCode: String,
        ): LobbyResponse = api.joinLobby(username, lobbyCode)

        suspend fun getLobbyInfo(lobbyCode: String): LobbyResponse = api.getLobbyInfo(lobbyCode)

        suspend fun changeRole(
            username: String,
            lobbyCode: String,
            role: Role,
            team: Team,
        ): LobbyResponse {
            val player = PlayerDto(username, role, team, false)
            return api.changeRole(lobbyCode, player)
        }
    }
