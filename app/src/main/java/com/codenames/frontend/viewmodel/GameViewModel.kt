package com.codenames.frontend.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codenames.frontend.data.model.GameState
import com.codenames.frontend.data.model.enums.ConnectionState
import com.codenames.frontend.data.model.enums.Team
import com.codenames.frontend.data.model.toGameState
import com.codenames.frontend.data.repository.GameRepository
import com.codenames.frontend.network.dto.GameMessage
import com.codenames.frontend.network.websocket.GameWebSocketController
import com.codenames.frontend.ui.roles.PlayerRoles
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val CONNECTION_ERROR_MESSAGE = "Connection error"

@HiltViewModel
class GameViewModel
    @Inject
    constructor(
        private val handler: GameWebSocketController,
        private val gameRepository: GameRepository,
    ) : ViewModel() {
        private var job: Job? = null

        private val _uiState = MutableStateFlow(GameState())
        val uiState: StateFlow<GameState> = _uiState
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

                        if (isHost) {
                            delay(2000)
                            sendGameStart(lobbyCode)
                        }
                    } catch (e: Exception) {
                        _connectionState.value = ConnectionState.Error(e.message ?: CONNECTION_ERROR_MESSAGE)
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
            team: Team?,
        ) {
            if (lobbyCode.isBlank() || team == null) {
                return
            }

            val turn = uiState.value.currentTurn
            if (!team.isActiveSpymasterTurn(turn)) return
            viewModelScope.launch {
                try {
                    gameRepository.submitClue(lobbyCode, word, count, team)
                } catch (e: Exception) {
                    _connectionState.value = ConnectionState.Error(e.message ?: CONNECTION_ERROR_MESSAGE)
                }
            }
        }

        fun submitGuess(
            lobbyCode: String,
            position: Int,
            team: Team?,
        ) {
            if (lobbyCode.isBlank() || team == null) {
                return
            }

            val turn = uiState.value.currentTurn
            if (!team.isActiveOperativeTurn(turn)) return
            viewModelScope.launch {
                try {
                    gameRepository.submitGuess(lobbyCode, position, team)
                } catch (e: Exception) {
                    _connectionState.value = ConnectionState.Error(e.message ?: CONNECTION_ERROR_MESSAGE)
                }
            }
        }

        fun submitGuesses(
            lobbyCode: String,
            positions: List<Int>,
            team: Team?,
        ) {
            if (lobbyCode.isBlank() || positions.isEmpty() || team == null) {
                return
            }

            val turn = uiState.value.currentTurn
            if (!team.isActiveOperativeTurn(turn)) return
            viewModelScope.launch {
                try {
                    positions.forEach { position ->
                        gameRepository.submitGuess(lobbyCode, position, team)
                    }
                } catch (e: Exception) {
                    _connectionState.value = ConnectionState.Error(e.message ?: CONNECTION_ERROR_MESSAGE)
                }
            }
        }

        private fun Team.isActiveOperativeTurn(turn: PlayerRoles): Boolean =
            (this == Team.BLUE && turn == PlayerRoles.BLUE_OPERATIVE) ||
                (this == Team.RED && turn == PlayerRoles.RED_OPERATIVE)

        private fun Team.isActiveSpymasterTurn(turn: PlayerRoles): Boolean =
            (this == Team.BLUE && turn == PlayerRoles.BLUE_SPYMASTER) ||
                (this == Team.RED && turn == PlayerRoles.RED_SPYMASTER)

        fun handleMessage(message: GameMessage) {
            val state = message.toGameState()
            Log.d("GameViewModel", "New Game State: $state")
            _uiState.update {
                state
            }
        }
    }
