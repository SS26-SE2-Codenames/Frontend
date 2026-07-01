package com.codenames.frontend.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codenames.frontend.data.error.ErrorMessageMapper
import com.codenames.frontend.data.model.ChatLists
import com.codenames.frontend.data.model.enums.ChatTab
import com.codenames.frontend.data.model.enums.Role
import com.codenames.frontend.data.model.enums.Team
import com.codenames.frontend.data.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel
    @Inject
    constructor(
        private val chatRepository: ChatRepository,
    ) : ViewModel() {
        private val _chatState = MutableStateFlow(ChatLists())
        val chatState: StateFlow<ChatLists> = _chatState

        private val _errorMessage = MutableStateFlow<String?>(null)
        val errorMessage: StateFlow<String?> = _errorMessage

        fun subscribeToChats(
            username: String,
            lobbyCode: String,
            team: String,
            role: String,
        ) {
            viewModelScope.launch {
                try {
                    chatRepository
                        .observeLobbyChat(
                            lobbyCode = lobbyCode,
                            currentUsername = username,
                        ).collect { msg ->
                            _chatState.update {
                                it.copy(
                                    lobbyMessages = it.lobbyMessages + msg,
                                )
                            }
                        }
                } catch (e: Exception) {
                    setError(e)
                }
            }

            viewModelScope.launch {
                try {
                    chatRepository
                        .observeTeamChat(
                            lobbyCode = lobbyCode,
                            team = team,
                            currentUsername = username,
                        ).collect { msg ->
                            _chatState.update {
                                it.copy(
                                    teamMessages = it.teamMessages + msg,
                                )
                            }
                        }
                } catch (e: Exception) {
                    setError(e)
                }
            }

            if (role == Role.OPERATIVE.name) {
                viewModelScope.launch {
                    try {
                        chatRepository
                            .observeOperativeChat(
                                lobbyCode = lobbyCode,
                                team = team,
                                currentUsername = username,
                            ).collect { msg ->
                                _chatState.update {
                                    it.copy(
                                        operativeMessages = it.operativeMessages + msg,
                                    )
                                }
                            }
                    } catch (e: Exception) {
                        setError(e)
                    }
                }

                viewModelScope.launch {
                    chatRepository
                        .observeSystemMessages(
                            currentUsername = username,
                        ).collect { msg ->
                            _chatState.update {
                                it.copy(
                                    operativeMessages = it.operativeMessages + msg,
                                )
                            }
                        }
                }
            }
        }

        fun sendLobbyMessage(
            lobbyCode: String,
            username: String,
            content: String,
        ) {
            viewModelScope.launch {
                try {
                    chatRepository.sendLobbyMessage(
                        lobbyCode = lobbyCode,
                        username = username,
                        text = content,
                    )
                } catch (e: Exception) {
                    setError(e)
                }
            }
        }

        fun sendTeamMessage(
            lobbyCode: String,
            team: String,
            username: String,
            content: String,
        ) {
            viewModelScope.launch {
                try {
                    chatRepository.sendTeamMessage(
                        lobbyCode = lobbyCode,
                        team = team,
                        username = username,
                        text = content,
                    )
                } catch (e: Exception) {
                    setError(e)
                }
            }
        }

        fun sendOperativeMessage(
            lobbyCode: String,
            team: String,
            username: String,
            content: String,
        ) {
            viewModelScope.launch {
                try {
                    chatRepository.sendOperativeMessage(
                        lobbyCode = lobbyCode,
                        team = team,
                        username = username,
                        text = content,
                    )
                } catch (e: Exception) {
                    setError(e)
                }
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

        fun clearError() {
            _errorMessage.value = null
        }

        private fun setError(error: Throwable) {
            _errorMessage.value = ErrorMessageMapper.toUserMessage(error)
        }

        fun cleanup() {
            viewModelScope.launch {
                _errorMessage.update {
                    null
                }
                _chatState.update {
                    ChatLists()
                }
            }
        }
    }
