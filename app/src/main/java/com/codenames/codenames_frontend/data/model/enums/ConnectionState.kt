package com.codenames.codenames_frontend.data.model.enums

sealed class ConnectionState {
    object IDLE: ConnectionState()
    object CONNECTING: ConnectionState()
    object CONNECTED: ConnectionState()
    data class Error(val message: String) : ConnectionState()
}