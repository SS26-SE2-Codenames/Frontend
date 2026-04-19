package com.codenames.codenames_frontend.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codenames.codenames_frontend.data.model.LobbyUiState
import com.codenames.codenames_frontend.data.model.enums.Role
import com.codenames.codenames_frontend.data.model.enums.Team
import com.codenames.codenames_frontend.data.model.toLobbyState
import com.codenames.codenames_frontend.data.repository.LobbyRepository
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
class LobbyViewModel @Inject constructor(private val repository: LobbyRepository) : ViewModel() {

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

    fun joinLobby(username: String, lobbyCode: String) {
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

    fun leaveLobby(username: String) {
        val lobbyCode = _state.value.lobbyCode

        if (lobbyCode.isNullOrBlank()) {
            setError("Not in a lobby, leaving not possible")
            return
        }

        viewModelScope.launch {
            setLoading(true)

            try {

                val response = repository.leaveLobby(username, lobbyCode)
                _state.update {
                    response.toLobbyState()
                }

                stopPolling()

            } catch (e: Exception) {
                setError(e.message)
            } finally {
                setLoading(false)
            }
        }
    }

    fun changeRole(username: String, role: Role, team: Team) {
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

            } catch (e: Exception) {
                setError(e.message)
            } finally {
                setLoading(false)
            }
        }

    }

    private fun startPolling(lobbyCode: String) {
        if (pollingJob != null) return

        pollingJob = viewModelScope.launch {
            while (isActive) {
                try {
                    val response = repository.getLobbyInfo(lobbyCode)

                    _state.update {
                        response.toLobbyState()
                    }
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
        val errorMessage = if (msg.isNullOrBlank()) {
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

    //for testing polling
    internal fun startPollingForTest(lobbyCode: String) {
        startPolling(lobbyCode)
    }

    internal fun stopPollingForTest() {
        stopPolling()
    }
}