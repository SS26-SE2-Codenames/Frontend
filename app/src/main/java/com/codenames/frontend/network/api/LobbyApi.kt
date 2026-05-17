package com.codenames.frontend.network.api

import com.codenames.frontend.network.dto.LobbyResponse
import com.codenames.frontend.network.dto.PlayerDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

// will be extended / adapted to different endpoints when backend is ready
interface LobbyApi {
    @GET("lobby/create")
    suspend fun createLobby(
        @Query("username") username: String,
    ): LobbyResponse

    @GET("lobby/{lobbyCode}/join")
    suspend fun joinLobby(
        @Path("lobbyCode") lobbyCode: String,
        @Query("username") username: String,
    ): LobbyResponse

    @GET("lobby/{lobbyCode}")
    suspend fun getLobbyInfo(
        @Path("lobbyCode") lobbyCode: String,
    ): LobbyResponse

    @GET("lobby/{lobbyCode}/leave")
    suspend fun leaveLobby(
        @Path("lobbyCode") lobbyCode: String,
        @Query("username") username: String,
    ): LobbyResponse

    @POST("lobby/{lobbyCode}/select-position")
    suspend fun changeRole(
        @Path("lobbyCode") lobbyCode: String,
        @Body playerDto: PlayerDto,
    ): LobbyResponse

    @GET("lobby/{lobbyCode}/start-game")
    suspend fun startGame(
        @Path("lobbyCode") lobbyCode: String, @Query("username") username: String
    ): LobbyResponse
}
