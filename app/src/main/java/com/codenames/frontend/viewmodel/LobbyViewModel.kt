package com.codenames.frontend.viewmodel

import android.util.Log
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
                    Log.d("LobbyViewModel", "Lobby created: ${response.lobbyCode}")
                    Log.d("LobbyViewModel", "UI State: ${_state.value}")
                } catch (e: Exception) {
                    setError(e.message)
                    Log.e("LobbyViewModel", "Error creating lobby: ${e.message}")
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
                    startPolling(response.lobbyCode)
                } catch (e: Exception) {
                    setError(e.message)
                } finally {
                    setLoading(false)
                }
            }
        }

        fun leaveLobby(username: String) : Boolean{
            val lobbyCode = _state.value.lobbyCode
            var successful = false

            if (lobbyCode.isNullOrBlank()) {
                setError("Not in a lobby, leaving not possible")
                return successful
            }

            viewModelScope.launch {
                setLoading(true)

                try {
                    val response = repository.leaveLobby(username, lobbyCode)
                    _state.update {
                        response.toLobbyState()
                    }

                    stopPolling()
                    successful = true
                } catch (e: Exception) {
                    setError(e.message)
                    Log.e("LobbyViewModel", "Error leaving lobby: ${e.message}")
                    successful = false
                } finally {
                    setLoading(false)
                }
                return@launch
            }
            Log.d("LobbyViewModel", "Leaving lobby: $lobbyCode, successful: $successful, error: ${_state.value.error}")
            return successful
        }

        fun changeRole(
            role: Role,
            team: Team,
            username: String
        ) {
            Log.d("LobbyViewModel", "Changing role to: $role, team: $team, username: $username")
            val lobbyCode = _state.value.lobbyCode
            if (lobbyCode.isNullOrBlank()) {
                setError("Not in a Lobby")
                Log.d("LobbyViewModel", "Not in a lobby")
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
                    Log.d("LobbyViewModel", "Role changed: $role")
                    Log.d("LobbyViewModel", "Team changed: $team")
                    Log.d("LobbyViewModel", "UI State: ${_state.value}")
                } catch (e: Exception) {
                    setError(e.message)
                    Log.e("LobbyViewModel", "Error changing role: ${e.message}")
                } finally {
                    setLoading(false)
                }
            }
        }

        fun changeRole(role: PlayerRoles, username: String) {
            when(role) {
                PlayerRoles.BLUE_SPYMASTER -> changeRole(role = Role.SPYMASTER, team = Team.BLUE, username = username)
                PlayerRoles.RED_SPYMASTER -> changeRole(role = Role.SPYMASTER, team = Team.RED, username = username)
                PlayerRoles.BLUE_OPERATIVE -> changeRole(role = Role.OPERATIVE, team = Team.BLUE, username = username)
                PlayerRoles.RED_OPERATIVE -> changeRole(role = Role.OPERATIVE, team = Team.RED, username = username)
                else -> setError("Invalid role")
            }
        }

    fun getRoleForUser(username: String) : PlayerRoles {
        val player: Player = _state.value.players.firstOrNull { it.name == username } ?: return PlayerRoles.NONE
        return when(player.role) {
            Role.OPERATIVE -> if(player.team == Team.BLUE) PlayerRoles.BLUE_OPERATIVE else PlayerRoles.RED_OPERATIVE
            Role.SPYMASTER -> if(player.team == Team.BLUE) PlayerRoles.BLUE_SPYMASTER else PlayerRoles.RED_SPYMASTER
            null -> PlayerRoles.NONE
        }
    }

    private fun updateUiState(players: List<Player>) {
        _state.update {
            it.copy(
                blueOperatives = players
                    .filter { p -> p.team == Team.BLUE && p.role == Role.OPERATIVE }
                    .map { it.name },

                blueSpymasters = players
                    .filter { p -> p.team == Team.BLUE && p.role == Role.SPYMASTER }
                    .map { it.name },

                redOperatives = players
                    .filter { p -> p.team == Team.RED && p.role == Role.OPERATIVE }
                    .map { it.name },

                redSpymasters = players
                    .filter { p -> p.team == Team.RED && p.role == Role.SPYMASTER }
                    .map { it.name },
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
                            Log.d("LobbyViewModel", "Polling: ${_state.value}")
                            updateUiState(_state.value.players)
                        } catch (e: Exception) {
                            setError(e.message)
                            Log.e("LobbyViewModel", "Error polling: ${e.message}")
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
