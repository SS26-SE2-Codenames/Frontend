package com.codenames.codenames_frontend.data.repository

import com.codenames.codenames_frontend.network.RetrofitClient
import com.codenames.codenames_frontend.network.dto.LobbyResponse

class LobbyRepository {

    private val api = RetrofitClient.api

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
}