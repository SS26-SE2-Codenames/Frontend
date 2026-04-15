package com.codenames.codenames_frontend.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codenames.codenames_frontend.network.dto.GameMessage
import com.codenames.codenames_frontend.network.websocket.GameWebSocketClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class GameViewModel(private val client: GameWebSocketClient) : ViewModel() {
    private val _uiState = MutableStateFlow(GameMessage())
    val uiState: StateFlow<GameMessage> = _uiState

    fun connect(lobbyCode: String) {
        viewModelScope.launch {
            client.connectStomp()

            client.subscribeToLobby(lobbyCode)
                .collect { message ->
                    handleMessage(message)
                }
        }
    }

    fun handleMessage(message: GameMessage) {
        _uiState.value = message
        //Add logic to handle incoming messages
    }
}