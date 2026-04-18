package com.codenames.codenames_frontend.network.dto

import com.codenames.codenames_frontend.data.model.Player
import kotlinx.serialization.Serializable

@Serializable
data class LobbyResponse(val lobbyCode: String, val playerList: List<PlayerDto>)
