package com.codenames.codenames_frontend.network.api

import com.codenames.codenames_frontend.network.dto.LobbyResponse
import com.codenames.codenames_frontend.network.dto.PlayerDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

//will be extended / adapted to different endpoints when backend is ready
interface LobbyApi {

    @POST("lobby/create")
    suspend fun createLobby(
        @Query("username") username: String
    ): LobbyResponse

    @POST("lobby/join")
    suspend fun joinLobby(
        @Query("username") username: String, @Query("lobbyCode") lobbyCode: String
    ): LobbyResponse

    @GET("lobby/{lobbyCode}")
    suspend fun getLobbyInfo(
        @Path("lobbyCode") lobbyCode: String
    ): LobbyResponse

    @POST("lobby/{lobbyCode}/{username}/leave")
    suspend fun leaveLobby(
        @Path("username") username: String, @Path("lobbyCode") lobbyCode: String
    ): LobbyResponse

    @POST("lobby/{lobbyCode}/roleChange")
    suspend fun changeRole(
        @Path("lobbyCode") lobbyCode: String, @Body playerDto: PlayerDto
    ): LobbyResponse
}