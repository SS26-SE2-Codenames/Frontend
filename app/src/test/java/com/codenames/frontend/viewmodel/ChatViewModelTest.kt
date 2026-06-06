package com.codenames.frontend.viewmodel

import com.codenames.frontend.data.model.ChatDomainModel
import com.codenames.frontend.data.model.enums.ChatTab
import com.codenames.frontend.data.model.enums.Role
import com.codenames.frontend.data.model.enums.Team
import com.codenames.frontend.data.repository.ChatRepository
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ChatViewModelTest {
    private lateinit var repository: ChatRepository
    private lateinit var viewModel: ChatViewModel

    @Before
    fun setup() {
        repository = mockk(relaxed = true)

        every {
            repository.observeChat(any(), any())
        } returns emptyFlow()

        viewModel = ChatViewModel(repository)
    }

    @Test
    fun sendChatMessage_global_sendsLobbyMessage() =
        runTest {
            viewModel.sendChatMessage(
                tab = ChatTab.GLOBAL,
                lobbyCode = "ABCD",
                username = "Max",
                team = Team.RED,
                content = "Hallo",
                availableChatTabs = listOf(ChatTab.GLOBAL),
            )

            coVerify {
                repository.sendMessage(
                    "/app/chat/ABCD",
                    "Max",
                    "Hallo",
                )
            }
        }

    @Test
    fun sendChatMessage_team_sendsTeamMessage() =
        runTest {
            viewModel.sendChatMessage(
                tab = ChatTab.TEAM,
                lobbyCode = "ABCD",
                username = "Max",
                team = Team.RED,
                content = "Hallo Team",
                availableChatTabs =
                    listOf(
                        ChatTab.GLOBAL,
                        ChatTab.TEAM,
                    ),
            )

            coVerify {
                repository.sendMessage(
                    "/app/chat/ABCD/RED",
                    "Max",
                    "Hallo Team",
                )
            }
        }

    @Test
    fun sendChatMessage_operatives_sendsOperativeMessage() =
        runTest {
            viewModel.sendChatMessage(
                tab = ChatTab.OPERATIVES,
                lobbyCode = "ABCD",
                username = "Max",
                team = Team.RED,
                content = "Secret",
                availableChatTabs =
                    listOf(
                        ChatTab.GLOBAL,
                        ChatTab.TEAM,
                        ChatTab.OPERATIVES,
                    ),
            )

            coVerify {
                repository.sendMessage(
                    "/app/chat/ABCD/RED/operative",
                    "Max",
                    "Secret",
                )
            }
        }

    @Test
    fun sendChatMessage_operativesNotAvailable_doesNothing() =
        runTest {
            viewModel.sendChatMessage(
                tab = ChatTab.OPERATIVES,
                lobbyCode = "ABCD",
                username = "Max",
                team = Team.RED,
                content = "Secret",
                availableChatTabs =
                    listOf(
                        ChatTab.GLOBAL,
                        ChatTab.TEAM,
                    ),
            )

            coVerify(exactly = 0) {
                repository.sendMessage(any(), any(), any())
            }
        }

    @Test
    fun sendChatMessage_blankLobbyCode_doesNothing() =
        runTest {
            viewModel.sendChatMessage(
                tab = ChatTab.GLOBAL,
                lobbyCode = "",
                username = "Max",
                team = Team.RED,
                content = "Hallo",
                availableChatTabs = listOf(ChatTab.GLOBAL),
            )

            coVerify(exactly = 0) {
                repository.sendMessage(any(), any(), any())
            }
        }

    @Test
    fun subscribeToChats_lobbyMessage_updatesLobbyMessages() =
        runTest {
            val msg =
                ChatDomainModel(
                    sender = "Anna",
                    text = "Hallo",
                    isFromMe = false,
                )

            every {
                repository.observeChat(
                    "/topic/chat/ABCD",
                    "Max",
                )
            } returns flowOf(msg)

            every {
                repository.observeChat(
                    "/topic/chat/ABCD/RED",
                    "Max",
                )
            } returns emptyFlow()

            every {
                repository.observeChat(
                    "/topic/chat/ABCD/RED/operative",
                    "Max",
                )
            } returns emptyFlow()

            viewModel.subscribeToChats(
                username = "Max",
                lobbyCode = "ABCD",
                team = "RED",
                role = Role.OPERATIVE.name,
            )

            advanceUntilIdle()

            val messages = viewModel.chatState.value.lobbyMessages
            if (messages.isNotEmpty()) {
                println("First sender: ${messages.first().sender}")
                println("First text: ${messages.first().text}")
            }

            assertEquals(1, messages.size)

            val first = messages.first()

            assertEquals("Anna", first.sender)
            assertEquals("Hallo", first.text)
        }
}
