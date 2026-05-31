package com.codenames.frontend.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codenames.frontend.data.model.ChatLists
import com.codenames.frontend.data.model.GameState
import com.codenames.frontend.data.model.enums.ChatTab
import com.codenames.frontend.data.model.enums.ConnectionState
import com.codenames.frontend.data.model.enums.Role
import com.codenames.frontend.data.model.enums.Team
import com.codenames.frontend.data.model.toGameState
import com.codenames.frontend.data.repository.ChatRepository
import com.codenames.frontend.data.repository.GameRepository
import com.codenames.frontend.network.dto.GameMessage
import com.codenames.frontend.network.websocket.GameWebSocketHandler
import com.codenames.frontend.ui.roles.PlayerRoles
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel
    @Inject
    constructor(
        private val handler: GameWebSocketHandler,
        private val chatRepository: ChatRepository,
        private val gameRepository: GameRepository,
    ) : ViewModel() {
        private var job: Job? = null

        private val _uiState = MutableStateFlow(GameState())
        val uiState: StateFlow<GameState> = _uiState

        private val _chatState = MutableStateFlow(ChatLists())
        val chatState: StateFlow<ChatLists> = _chatState

        private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.IDLE)
        val connectionState: StateFlow<ConnectionState> = _connectionState

        fun connect(
            username: String,
            lobbyCode: String,
            team: String,
            role: String,
            isHost: Boolean = false,
        ) {
            job?.cancel()

            job =
                viewModelScope.launch {
                    _connectionState.value = ConnectionState.CONNECTING

                    try {
                        handler.connectStomp()

                        Log.d("GameViewModel", "Connection successful")

                        _connectionState.value = ConnectionState.CONNECTED

                        launch {
                            handler
                                .subscribeToLobby(lobbyCode)
                                .collect { handleMessage(it) }
                        }

                        Log.d("GameViewModel", "Subscribed to Lobby")

                        launch {
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

                        if (isHost) {
                            delay(2000)
                            sendGameStart(lobbyCode)
                        }
                    } catch (e: Exception) {
                        _connectionState.value = ConnectionState.Error(e.message ?: "Connection error")
                    }
                }
        }

        private fun sendGameStart(lobbyCode: String) {
            if (lobbyCode.isBlank()) {
                return
            }
            viewModelScope.launch {
                gameRepository.startGame(lobbyCode)
            }
        }

        fun submitClue(
            lobbyCode: String,
            word: String,
            count: Int,
        ) {
            if (lobbyCode.isBlank()) {
                return
            }

            val turn = uiState.value.currentTurn
            if (turn != PlayerRoles.BLUE_SPYMASTER && turn != PlayerRoles.RED_SPYMASTER) return

            val team = if (turn == PlayerRoles.BLUE_SPYMASTER) Team.BLUE else Team.RED
            viewModelScope.launch {
                try {
                    gameRepository.submitClue(lobbyCode, word, count, team)
                } catch (e: Exception) {
                    _connectionState.value = ConnectionState.Error(e.message ?: "Connection error")
                }
            }
        }

        fun handleMessage(message: GameMessage) {
            val state = message.toGameState()
            _uiState.update {
                state
            }
        }

        fun sendChatMessage(
            tab: ChatTab,
            lobbyCode: String,
            username: String,
            team: Team?,
            content: String,
            availableChatTabs: List<ChatTab>,
        ) {
            if (lobbyCode.isBlank()) {
                return
            }

            when (tab) {
                ChatTab.GLOBAL ->
                    sendLobbyMessage(
                        lobbyCode = lobbyCode,
                        username = username,
                        content = content,
                    )

                ChatTab.TEAM ->
                    if (team != null) {
                        sendTeamMessage(
                            lobbyCode = lobbyCode,
                            team = team.name,
                            username = username,
                            content = content,
                        )
                    }

                ChatTab.OPERATIVES ->
                    if (team != null && ChatTab.OPERATIVES in availableChatTabs) {
                        sendOperativeMessage(
                            lobbyCode = lobbyCode,
                            team = team.name,
                            username = username,
                            content = content,
                        )
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
    }
