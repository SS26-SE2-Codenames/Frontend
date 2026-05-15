package com.codenames.frontend.viewmodel

import androidx.lifecycle.ViewModel
import com.codenames.frontend.data.model.ChatMessage
import com.codenames.frontend.data.model.ChatUiState
import com.codenames.frontend.data.model.enums.ChatTab
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ChatViewModel : ViewModel() {
    private val _uiState =
        MutableStateFlow(
            ChatUiState(
                messages =
                    listOf(
                        ChatMessage(
                            id = 1,
                            sender = "SYSTEM",
                            message = "Game started",
                            timestamp = "12:00",
                            chatTab = ChatTab.GLOBAL,
                        ),
                        ChatMessage(
                            id = 2,
                            sender = "Anna",
                            message = "Hello team",
                            timestamp = "12:01",
                            chatTab = ChatTab.TEAM,
                        ),
                        ChatMessage(
                            id = 3,
                            sender = "Max",
                            message = "Lets win this",
                            timestamp = "12:02",
                            chatTab = ChatTab.OPERATIVES,
                        ),
                    ),
            ),
        )
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun updateInput(newInput: String) {
        _uiState.value =
            _uiState.value.copy(
                currentInput = newInput,
            )
    }

    fun sendMessage(
        username: String,
        tab: ChatTab,
    ) {
        val text = _uiState.value.currentInput.trim()

        if (text.isBlank()) {
            return
        }

        val newMessage =
            ChatMessage(
                id = _uiState.value.messages.size + 1,
                sender = username,
                message = text,
                timestamp = "NOW",
                chatTab = tab,
            )

        _uiState.value =
            _uiState.value.copy(
                messages = _uiState.value.messages + newMessage,
                currentInput = "",
            )
    }
}
