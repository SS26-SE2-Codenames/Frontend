package com.codenames.codenames_frontend.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codenames.codenames_frontend.data.model.LobbyUiState
import com.codenames.codenames_frontend.data.model.enums.Role
import com.codenames.codenames_frontend.data.model.enums.Team
import com.codenames.codenames_frontend.data.model.toLobbyState
import com.codenames.codenames_frontend.data.repository.LobbyRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
const val LOG_NAME = "LobbyViewModel"

class LobbyViewModel(private val repository: LobbyRepository) : ViewModel(){

    private val _state = MutableStateFlow(LobbyUiState())
    val state: StateFlow<LobbyUiState> = _state

    private var pollingJob: Job? = null


    fun createLobby(username: String) {
        val inLobby = !_state.value.lobbyCode.isNullOrBlank()
        if(inLobby) {
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

                Log.d(LOG_NAME, "Lobby Code: ${response.lobbyCode}")
            }catch (e: Exception) {
                setError(e.message)
                Log.e(LOG_NAME, "Error creating Lobby", e)
            }finally {
                setLoading(false)
            }
        }
    }

    fun joinLobby(username: String, lobbyCode: String) {
        val inLobby = !_state.value.lobbyCode.isNullOrBlank()
        if(inLobby){
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
                Log.d(LOG_NAME, "Joined Lobby: ${response.lobbyCode}")
            }catch (e: Exception) {
                setError(e.message)
                Log.e(LOG_NAME, "Error while joining Lobby", e)
            }finally {
                setLoading(false)
            }
        }
    }

    fun leaveLobby(username: String) {
        val inLobby = !_state.value.lobbyCode.isNullOrBlank()
        val lobbyCode = _state.value.lobbyCode

        if(!inLobby || lobbyCode.isNullOrBlank()) {
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
                Log.d(LOG_NAME, "Left lobby: ${response.lobbyCode}")

            } catch (e: Exception) {
                setError(e.message)
                Log.e(LOG_NAME, "Error while leaving lobby", e)
            }finally {
                setLoading(false)
            }
        }
    }

    fun changeRole(username: String, role: Role, team: Team) {
        val lobbyCode = _state.value.lobbyCode
        val inLobby = !_state.value.lobbyCode.isNullOrBlank()
        if(!inLobby || lobbyCode.isNullOrBlank()){
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

                Log.d(LOG_NAME, "Changed role successfully.")
            } catch (e: Exception) {
                setError(e.message)
                Log.e(LOG_NAME, "Error at role change", e)
            }finally {
                setLoading(false)
            }
        }

    }

    private fun setError(msg: String?) {
        _state.update {
            it.copy(error = msg ?: "Unknown Error")
        }
    }

    private fun setLoading(loading: Boolean) {
        _state.update {
            it.copy(isLoading = loading)
        }
    }
}