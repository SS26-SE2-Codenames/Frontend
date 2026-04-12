package com.codenames.codenames_frontend.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codenames.codenames_frontend.data.repository.LobbyRepository
import kotlinx.coroutines.launch
const val LOG_NAME = "LobbyViewModel"

class LobbyViewModel : ViewModel(){
    private val repository = LobbyRepository()


    fun createLobby(username: String) {
        viewModelScope.launch {
            try {
                val response = repository.createLobby(username)
                Log.d(LOG_NAME, "Lobby Code: ${response.lobbyCode}")
            }catch (e: Exception) {
                Log.e(LOG_NAME, "Error creating Lobby", e)
            }
        }
    }

    fun joinLobby(username: String, lobbyCode: String) {
        viewModelScope.launch {
            try {
                val response = repository.joinLobby(username, lobbyCode)
                Log.d(LOG_NAME, "Joined Lobby: ${response.lobbyCode}")
            }catch (e: Exception) {
                Log.e(LOG_NAME, "Error while joining Lobby", e)
            }
        }
    }

    fun leaveLobby(username: String, lobbyCode: String) {
        viewModelScope.launch {
            try {
                val response = repository.joinLobby(username, lobbyCode)
                Log.d(LOG_NAME, "Left lobby: ${response.lobbyCode}")
            } catch (e: Exception) {
                Log.e(LOG_NAME, "Error while leaving lobby", e)
            }
        }
    }
}