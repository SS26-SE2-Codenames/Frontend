package com.codenames.codenames_frontend.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codenames.codenames_frontend.data.model.enums.ConnectionState
import com.codenames.codenames_frontend.network.dto.GameMessage
import com.codenames.codenames_frontend.network.dto.WebSocketJoinMessage
import com.codenames.codenames_frontend.network.websocket.GameWebSocketHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(private val client: GameWebSocketHandler) : ViewModel() {

    private var job: Job? = null

    private val _uiState = MutableStateFlow(GameMessage())
    val uiState: StateFlow<GameMessage> = _uiState

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.IDLE)
    val connectionState: StateFlow<ConnectionState> = _connectionState

    fun connect(username: String, lobbyCode: String) {

        job?.cancel()

        job = viewModelScope.launch {
            _connectionState.value = ConnectionState.CONNECTING

            try {
                client.connectStomp()

                _connectionState.value = ConnectionState.CONNECTED

                launch {
                    client.subscribeToLobby(lobbyCode)
                        .collect { handleMessage(it) }
                }
                client.sendLobbyJoinMessage(WebSocketJoinMessage(username, lobbyCode))
            } catch(e: Exception) {
                _connectionState.value = ConnectionState.Error(e.message ?: "Connection error")
            }
        }
    }

    fun handleMessage(message: GameMessage) {
        _uiState.value = message
        //Add logic to handle incoming messages
    }
}