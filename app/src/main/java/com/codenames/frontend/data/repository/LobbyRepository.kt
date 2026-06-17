package com.codenames.frontend.data.repository

import com.codenames.frontend.data.model.enums.Role
import com.codenames.frontend.data.model.enums.Team
import com.codenames.frontend.network.api.LobbyApi
import com.codenames.frontend.network.dto.LobbyResponse
import com.codenames.frontend.network.dto.PlayerDto
import java.util.UUID
import javax.inject.Inject

class LobbyRepository
    @Inject
    constructor(
        private val api: LobbyApi,
    ) {
        suspend fun createLobby(username: String): LobbyResponse = api.createLobby(username)

        suspend fun leaveLobby(
            lobbyCode: String,
            userId: UUID,
        ): LobbyResponse = api.leaveLobby(lobbyCode, userId.toString())

        suspend fun joinLobby(
            username: String,
            lobbyCode: String,
            userId: UUID?
        ): LobbyResponse {
            var id : String? = userId.toString()
            if(id.equals("null")) id = null
            return api.joinLobby(lobbyCode, username, id)
        }

        suspend fun getLobbyInfo(lobbyCode: String): LobbyResponse = api.getLobbyInfo(lobbyCode)

        suspend fun changeRole(
            username: String,
            userId: UUID,
            lobbyCode: String,
            role: Role,
            team: Team,
        ): LobbyResponse {
            val player = PlayerDto(username, role, team, false, userId.toString())
            return api.changeRole(lobbyCode, player)
        }

        suspend fun sendStartGame(
            lobbyCode: String,
            userId: UUID,
        ): LobbyResponse = api.startGame(lobbyCode, userId.toString())
    }
