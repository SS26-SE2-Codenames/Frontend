package com.codenames.frontend.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codenames.frontend.data.model.RejoinState
import com.codenames.frontend.data.model.UserState
import com.codenames.frontend.data.model.enums.Role
import com.codenames.frontend.data.model.enums.Team
import com.codenames.frontend.data.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SessionViewModel
    @Inject
    constructor(
        private val sessionRepository: SessionRepository
    ) : ViewModel() {
        private val _userState = MutableStateFlow(UserState("", null)) //initialized with random UUID, is replaced when real uuid is collected
        val userState: StateFlow<UserState> = _userState

        private val _rejoinSessionState = MutableStateFlow<RejoinState>(RejoinState.None)
        val rejoinSessionState: StateFlow<RejoinState> = _rejoinSessionState

        init {
            observeUser()
            observeSession()
        }

        fun setUsername(username: String) {
            _userState.update {
                it.copy(username = username)
            }
        }

        fun setUserId(userId: UUID) {
            _userState.update {
                it.copy(
                    userId = userId
                )
            }
        }

        fun persistLobbyState(lobbyCode: String, role: Role, team: Team) {
            viewModelScope.launch {
                sessionRepository.saveLobby(
                    lobbyCode, role, team
                )
            }
        }

        fun persistUserState() {
            viewModelScope.launch {
                sessionRepository.saveUser(
                    _userState.value.username,
                    _userState.value.userId ?: return@launch
                )
            }
        }

        fun clearLobby() {
            viewModelScope.launch {
                sessionRepository.clearLobbyData()
            }
        }

        private fun observeSession() {
            viewModelScope.launch {
                sessionRepository.sessionFlow.collect { session ->
                    _rejoinSessionState.value =
                        if (
                            session.lobbyCode != null &&
                            session.lobbyRole != null &&
                            session.lobbyTeam != null
                        ) {
                            RejoinState.Available(session)
                        } else {
                            RejoinState.None
                        }
                }
            }
        }

        private fun observeUser() {
            viewModelScope.launch {
                sessionRepository.userFlow.collect { state ->
                    _userState.value = state
                }
            }
        }
}
