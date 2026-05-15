package com.codenames.frontend.viewmodel

import com.codenames.frontend.data.model.enums.ChatTab
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ChatViewModelTest {
    private lateinit var viewModel: ChatViewModel

    @Before
    fun setup() {
        viewModel = ChatViewModel()
    }

    @Test
    fun updateInput_updatesCurrentInput() {
        viewModel.updateInput("Hallo")

        assertEquals(
            "Hallo",
            viewModel.uiState.value.currentInput,
        )
    }

    @Test
    fun sendMessage_addsMessage() {
        viewModel.updateInput("Neue Nachricht")

        val before =
            viewModel.uiState.value.messages.size

        viewModel.sendMessage(
            username = "Max",
            tab = ChatTab.GLOBAL,
        )

        val state = viewModel.uiState.value

        assertEquals(before + 1, state.messages.size)

        val last = state.messages.last()

        assertEquals("Max", last.sender)
        assertEquals("Neue Nachricht", last.message)
        assertEquals(ChatTab.GLOBAL, last.chatTab)
    }

    @Test
    fun sendMessage_clearsInputAfterSend() {
        viewModel.updateInput("Text")

        viewModel.sendMessage(
            "Max",
            ChatTab.GLOBAL,
        )

        assertEquals(
            "",
            viewModel.uiState.value.currentInput,
        )
    }



    @Test
    fun sendMessage_blankMessage_doesNothing() {
        viewModel.updateInput("     ")

        val before =
            viewModel.uiState.value.messages.size

        viewModel.sendMessage(
            "Max",
            ChatTab.GLOBAL,
        )

        val after =
            viewModel.uiState.value.messages.size

        assertEquals(before, after)
    }


}
