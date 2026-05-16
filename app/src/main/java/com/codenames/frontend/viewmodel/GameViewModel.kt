package com.codenames.frontend.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codenames.frontend.data.model.ChatLists
import com.codenames.frontend.data.model.enums.ConnectionState
import com.codenames.frontend.data.model.enums.Role
import com.codenames.frontend.data.repository.ChatRepository
import com.codenames.frontend.network.dto.ClueDto
import com.codenames.frontend.network.dto.GameMessage
import com.codenames.frontend.network.dto.WebSocketJoinMessage
import com.codenames.frontend.network.websocket.GameWebSocketHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel
    @Inject
    constructor(
        private val client: GameWebSocketHandler,
        private val chatRepository: ChatRepository,
    ) : ViewModel() {
        private var job: Job? = null

        private val _uiState = MutableStateFlow(GameMessage())
        val uiState: StateFlow<GameMessage> = _uiState

        // _chatState is mutable and should only be used by view model
        private val _chatState = MutableStateFlow(ChatLists())

        // chatState is not mutable and is meant for the UI
        val chatState: StateFlow<ChatLists> = _chatState

        private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.IDLE)
        val connectionState: StateFlow<ConnectionState> = _connectionState

        fun connect(
            username: String,
            lobbyCode: String,
            team: String,
            role: String,
        ) {
            job?.cancel()

            job =
                viewModelScope.launch {
                    _connectionState.value = ConnectionState.CONNECTING

                    try {
                        client.connectStomp()

                        _connectionState.value = ConnectionState.CONNECTED

                        launch {
                            client
                                .subscribeToLobby(lobbyCode)
                                .collect { handleMessage(it) }
                        }

                        launch {
                            // msg is the domain model chat we emit in the ChatRepository
                            chatRepository.observeChat("/topic/chat/$lobbyCode", username).collect { msg ->
                                _chatState.update { currentState ->
                                    currentState.copy(lobbyMessages = currentState.lobbyMessages + msg)
                                }
                            }
                        }

                        launch {
                            chatRepository.observeChat("/topic/chat/$lobbyCode/$team", username).collect { msg ->
                                _chatState.update { currentState ->
                                    currentState.copy(teamMessages = currentState.teamMessages + msg)
                                }
                            }
                        }

                        if (role == Role.OPERATIVE.name) {
                            launch {
                                chatRepository.observeChat("/topic/chat/$lobbyCode/$team/operative", username).collect { msg ->
                                    _chatState.update { currentState ->
                                        currentState.copy(operativeMessages = currentState.operativeMessages + msg)
                                    }
                                }
                            }
                        }

                        client.registerWebSocketSession(WebSocketJoinMessage(username, lobbyCode))
                    } catch (e: Exception) {
                        _connectionState.value = ConnectionState.Error(e.message ?: "Connection error")
                    }
                }
        }

        fun sendLobbyMessage(
            lobbyCode: String,
            username: String,
            content: String,
        ) {
            viewModelScope.launch {
                chatRepository.sendMessage("/app/chat/$lobbyCode", username, content)
            }
        }

        fun sendTeamMessage(
            lobbyCode: String,
            team: String,
            username: String,
            content: String,
        ) {
            viewModelScope.launch {
                chatRepository.sendMessage("/app/chat/$lobbyCode/$team", username, content)
            }
        }

        fun sendOperativeMessage(
            lobbyCode: String,
            team: String,
            username: String,
            content: String,
        ) {
            viewModelScope.launch {
                chatRepository.sendMessage("/app/chat/$lobbyCode/$team/operative", username, content)
            }
        }

    fun submitClue(lobbyCode: String, word: String, count: Int) {
        viewModelScope.launch {
            try {
                client.sendClue(lobbyCode, ClueDto(word, count))
            } catch (e: Exception) {
                _connectionState.value = ConnectionState.Error(e.message ?: "Connection error")
            }
        }
    }

        fun handleMessage(message: GameMessage) {
            _uiState.value = message
            // Add logic to handle incoming messages
        }
    }
