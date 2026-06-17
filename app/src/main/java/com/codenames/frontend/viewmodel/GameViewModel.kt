package com.codenames.frontend.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codenames.frontend.data.error.ErrorMessageMapper
import com.codenames.frontend.data.model.GameState
import com.codenames.frontend.data.model.RejoinState
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
import java.util.UUID
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

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
            lobbyCode: String,
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
                            delay(3000.milliseconds)
                            sendGameStart(lobbyCode)
                        }
                    } catch (e: Exception) {
                        setConnectionError(e)
                    }
                }
        }

        private fun sendGameStart(lobbyCode: String) {
            if (lobbyCode.isBlank()) {
                return
            }
            viewModelScope.launch {
                try {
                    gameRepository.startGame(lobbyCode)
                } catch (e: Exception) {
                    setConnectionError(e)
                }
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
                    setConnectionError(e)
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
                    setConnectionError(e)
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
                    setConnectionError(e)
                }
            }
        }

        fun requestCheat(
            lobbyCode: String,
            username: String,
            positions: List<Int>,
        ) {
            if (lobbyCode.isBlank() || positions.isEmpty()) {
                return
            }

            viewModelScope.launch {
                try {
                    gameRepository.requestCheat(
                        lobbyCode = lobbyCode,
                        username = username,
                        positions = positions,
                    )
                } catch (e: Exception) {
                    setConnectionError(e)
                }
            }
        }

        fun passTurn(
            lobbyCode: String,
            team: Team?,
        ) {
            if (lobbyCode.isBlank() || team == null) {
                return
            }

            val turn = uiState.value.currentTurn
            if (!team.isActiveOperativeTurn(turn)) return
            viewModelScope.launch {
                try {
                    gameRepository.passTurn(lobbyCode, team)
                } catch (e: Exception) {
                    setConnectionError(e)
                }
            }
        }

        fun rejoinGame(
            username: String,
            userId: UUID,
            rejoinState: RejoinState.Available?,
        ) {
            if (rejoinState == null) return

            val lobbyCode = rejoinState.sessionState.lobbyCode
            val team = rejoinState.sessionState.lobbyTeam
            val role = rejoinState.sessionState.lobbyRole

            if (lobbyCode != null && team != null && role != null) {
                viewModelScope.launch {
                    try {
                        gameRepository.sendRejoin(username, userId, lobbyCode)
                    } catch (e: Exception) {
                        setConnectionError(e)
                    }
                }
            }
        }

        fun handleMessage(message: GameMessage) {
            if (!message.error.isNullOrBlank()) {
                _connectionState.value = ConnectionState.Error(message.error)
                return
            }

            val state = message.toGameState()
            Log.d("GameViewModel", "New Game State: $state")
            _uiState.update {
                state
            }
        }

        fun clearError() {
            if (_connectionState.value is ConnectionState.Error) {
                _connectionState.value = ConnectionState.IDLE
            }
        }

        private fun setConnectionError(error: Throwable) {
            _connectionState.value = ConnectionState.Error(ErrorMessageMapper.toUserMessage(error))
        }

        private fun Team.isActiveOperativeTurn(turn: PlayerRoles): Boolean =
            (this == Team.BLUE && turn == PlayerRoles.BLUE_OPERATIVE) ||
                (this == Team.RED && turn == PlayerRoles.RED_OPERATIVE)

        private fun Team.isActiveSpymasterTurn(turn: PlayerRoles): Boolean =
            (this == Team.BLUE && turn == PlayerRoles.BLUE_SPYMASTER) ||
                (this == Team.RED && turn == PlayerRoles.RED_SPYMASTER)
    }
