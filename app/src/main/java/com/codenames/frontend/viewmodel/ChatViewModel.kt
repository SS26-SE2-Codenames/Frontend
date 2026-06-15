package com.codenames.frontend.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

        fun subscribeToChats(
            username: String,
            lobbyCode: String,
            team: String,
            role: String,
        ) {
            viewModelScope.launch {
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
            }

            viewModelScope.launch {
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
            }

            if (role == Role.OPERATIVE.name) {
                viewModelScope.launch {
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
                chatRepository.sendLobbyMessage(
                    lobbyCode = lobbyCode,
                    username = username,
                    text = content,
                )
            }
        }

        fun sendTeamMessage(
            lobbyCode: String,
            team: String,
            username: String,
            content: String,
        ) {
            viewModelScope.launch {
                chatRepository.sendTeamMessage(
                    lobbyCode = lobbyCode,
                    team = team,
                    username = username,
                    text = content,
                )
            }
        }

        fun sendOperativeMessage(
            lobbyCode: String,
            team: String,
            username: String,
            content: String,
        ) {
            viewModelScope.launch {
                chatRepository.sendOperativeMessage(
                    lobbyCode = lobbyCode,
                    team = team,
                    username = username,
                    text = content,
                )
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
    }
