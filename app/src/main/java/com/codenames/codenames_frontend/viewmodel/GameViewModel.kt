package com.codenames.codenames_frontend.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codenames.codenames_frontend.network.dto.GameMessage
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

    fun connect(lobbyCode: String) {

        job?.cancel()

        job = viewModelScope.launch {
            client.connectStomp()

            client.subscribeToLobby(lobbyCode)
                .collect { handleMessage(it) }
        }
    }

    fun handleMessage(message: GameMessage) {
        _uiState.value = message
        //Add logic to handle incoming messages
    }
}