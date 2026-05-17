package com.codenames.frontend.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class StartGameMessage(val lobbyCode: String)
