package com.codenames.frontend.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codenames.frontend.data.model.LobbyUiState
import com.codenames.frontend.data.model.Player
import com.codenames.frontend.data.model.enums.Role
import com.codenames.frontend.data.model.enums.Team
import com.codenames.frontend.data.model.toLobbyState
import com.codenames.frontend.data.repository.LobbyRepository
import com.codenames.frontend.ui.roles.PlayerRoles
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LobbyViewModel
    @Inject
    constructor(
        private val repository: LobbyRepository,
    ) : ViewModel() {
        private val _state = MutableStateFlow(LobbyUiState())
        val state: StateFlow<LobbyUiState> = _state

        private var pollingJob: Job? = null
        private val pollingTime: Long = 2000

        fun createLobby(username: String) {
            val inLobby = !_state.value.lobbyCode.isNullOrBlank()
            if (inLobby) {
                setError("Already in a lobby, creating a new lobby not possible")
                return
            }
            viewModelScope.launch {
                setLoading(true)

                try {
                    val response = repository.createLobby(username)

                    _state.update {
                        response.toLobbyState()
                    }
                    startPolling(response.lobbyCode)
                } catch (e: Exception) {
                    setError(e.message)
                } finally {
                    setLoading(false)
                }
            }
        }

        fun joinLobby(
            username: String,
            lobbyCode: String,
        ) {
            val inLobby = !_state.value.lobbyCode.isNullOrBlank()
            if (inLobby) {
                setError("Already in a lobby, joining not possible")
                return
            }

            viewModelScope.launch {
                setLoading(true)

                try {
                    val response = repository.joinLobby(username, lobbyCode)

                    _state.update {
                        response.toLobbyState()
                    }
                    updateUiState(_state.value.players)
                    startPolling(response.lobbyCode)
                } catch (e: Exception) {
                    setError(e.message)
                } finally {
                    setLoading(false)
                }
            }
        }

        fun leaveLobby(
            username: String,
            onResult: (Boolean) -> Unit,
        ) {
            val lobbyCode = _state.value.lobbyCode
            var successful = false

            if (lobbyCode.isNullOrBlank()) {
                setError("Not in a lobby, leaving not possible")
                onResult(successful)
                return
            }

            viewModelScope.launch {
                setLoading(true)

                try {
                    val response = repository.leaveLobby(lobbyCode, username)
                    _state.update {
                        response.toLobbyState()
                    }

                    stopPolling()
                    successful = true
                } catch (e: Exception) {
                    setError(e.message)
                    successful = false
                } finally {
                    setLoading(false)
                    onResult(successful)
                    if (successful) cleanup()
                }
            }
        }

        fun changeRole(
            role: Role,
            team: Team,
            username: String,
        ) {
            val lobbyCode = _state.value.lobbyCode
            if (lobbyCode.isNullOrBlank()) {
                setError("Not in a Lobby")
                return
            }

            viewModelScope.launch {
                setLoading(true)

                try {
                    val response = repository.changeRole(username, lobbyCode, role, team)

                    _state.update {
                        response.toLobbyState()
                    }
                    updateUiState(_state.value.players)
                } catch (e: Exception) {
                    setError(e.message)
                } finally {
                    setLoading(false)
                }
            }
        }

        fun changeRole(
            role: PlayerRoles,
            username: String,
        ) {
            when (role) {
                PlayerRoles.BLUE_SPYMASTER -> changeRole(role = Role.SPYMASTER, team = Team.BLUE, username = username)
                PlayerRoles.RED_SPYMASTER -> changeRole(role = Role.SPYMASTER, team = Team.RED, username = username)
                PlayerRoles.BLUE_OPERATIVE -> changeRole(role = Role.OPERATIVE, team = Team.BLUE, username = username)
                PlayerRoles.RED_OPERATIVE -> changeRole(role = Role.OPERATIVE, team = Team.RED, username = username)
                else -> setError("Invalid role")
            }
        }

        fun getRoleForUser(username: String): PlayerRoles {
            val player: Player = _state.value.players.firstOrNull { it.name == username } ?: return PlayerRoles.NONE
            return when (player.role) {
                Role.OPERATIVE -> if (player.team == Team.BLUE) PlayerRoles.BLUE_OPERATIVE else PlayerRoles.RED_OPERATIVE
                Role.SPYMASTER -> if (player.team == Team.BLUE) PlayerRoles.BLUE_SPYMASTER else PlayerRoles.RED_SPYMASTER
                null -> PlayerRoles.NONE
            }
        }

    fun getIsHost(username: String) : Boolean {
        val player: Player = _state.value.players.firstOrNull { it.name == username } ?: return false
        return player.isHost
    }

        private fun cleanup() {
            _state.update {
                it.copy(
                    lobbyCode = null,
                    players = emptyList(),
                    blueOperatives = emptyList(),
                    blueSpymasters = emptyList(),
                    redOperatives = emptyList(),
                    redSpymasters = emptyList(),
                )
            }
        }

        private fun updateUiState(players: List<Player>) {
            _state.update {
                it.copy(
                    blueOperatives =
                        players
                            .filter { p -> p.team == Team.BLUE && p.role == Role.OPERATIVE }
                            .map { p -> p.name },
                    blueSpymasters =
                        players
                            .filter { p -> p.team == Team.BLUE && p.role == Role.SPYMASTER }
                            .map { p -> p.name },
                    redOperatives =
                        players
                            .filter { p -> p.team == Team.RED && p.role == Role.OPERATIVE }
                            .map { p -> p.name },
                    redSpymasters =
                        players
                            .filter { p -> p.team == Team.RED && p.role == Role.SPYMASTER }
                            .map { p -> p.name },
                )
            }
        }

        private fun startPolling(lobbyCode: String) {
            if (pollingJob != null) return

            pollingJob =
                viewModelScope.launch {
                    while (isActive) {
                        try {
                            val response = repository.getLobbyInfo(lobbyCode)

                            _state.update {
                                response.toLobbyState()
                            }
                            updateUiState(_state.value.players)
                        } catch (e: Exception) {
                            setError(e.message)
                            return@launch
                        }

                        delay(pollingTime)
                    }
                }
        }

        private fun stopPolling() {
            pollingJob?.cancel()
            pollingJob = null
        }

        private fun setError(msg: String?) {
            val errorMessage =
                if (msg.isNullOrBlank()) {
                    "Unknown error"
                } else {
                    msg
                }

            _state.update {
                it.copy(error = errorMessage)
            }
        }

        private fun setLoading(loading: Boolean) {
            _state.update {
                it.copy(isLoading = loading)
            }
        }

        // for testing polling
        internal fun startPollingForTest(lobbyCode: String) {
            startPolling(lobbyCode)
        }

        internal fun stopPollingForTest() {
            stopPolling()
        }
    }
