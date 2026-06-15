package com.codenames.frontend.viewmodel

import com.codenames.frontend.data.model.ChatDomainModel
import com.codenames.frontend.data.model.enums.ChatTab
import com.codenames.frontend.data.model.enums.Role
import com.codenames.frontend.data.model.enums.Team
import com.codenames.frontend.data.repository.ChatRepository
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class ChatViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: ChatRepository
    private lateinit var viewModel: ChatViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        repository = mockk(relaxed = true)

        every {
            repository.observeLobbyChat(any(), any())
        } returns emptyFlow()

        every {
            repository.observeTeamChat(any(), any(), any())
        } returns emptyFlow()

        every {
            repository.observeOperativeChat(any(), any(), any())
        } returns emptyFlow()

        every {
            repository.observeSystemMessages(any())
        } returns emptyFlow()

        viewModel = ChatViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
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

            advanceUntilIdle()

            coVerify {
                repository.sendLobbyMessage(
                    "ABCD",
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

            advanceUntilIdle()

            coVerify {
                repository.sendTeamMessage(
                    "ABCD",
                    "RED",
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

            advanceUntilIdle()

            coVerify {
                repository.sendOperativeMessage(
                    "ABCD",
                    "RED",
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
                repository.sendOperativeMessage(
                    any(),
                    any(),
                    any(),
                    any(),
                )
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
                repository.sendLobbyMessage(any(), any(), any())
            }

            coVerify(exactly = 0) {
                repository.sendTeamMessage(any(), any(), any(), any())
            }

            coVerify(exactly = 0) {
                repository.sendOperativeMessage(any(), any(), any(), any())
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
                repository.observeLobbyChat(
                    "ABCD",
                    "Max",
                )
            } returns flowOf(msg)

            every {
                repository.observeTeamChat(
                    "ABCD",
                    "RED",
                    "Max",
                )
            } returns emptyFlow()

            every {
                repository.observeOperativeChat(
                    "ABCD",
                    "RED",
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

    @Test
    fun subscribeToChats_systemMessage_updatesOperativeMessages() =
        runTest {
            val msg =
                ChatDomainModel(
                    sender = "System",
                    text = "London is correct",
                    isFromMe = false,
                )

            every {
                repository.observeSystemMessages("Max")
            } returns flowOf(msg)

            every {
                repository.observeLobbyChat(any(), any())
            } returns emptyFlow()

            every {
                repository.observeTeamChat(any(), any(), any())
            } returns emptyFlow()

            every {
                repository.observeOperativeChat(any(), any(), any())
            } returns emptyFlow()

            viewModel.subscribeToChats(
                username = "Max",
                lobbyCode = "ABCD",
                team = "RED",
                role = Role.OPERATIVE.name,
            )

            advanceUntilIdle()

            assertEquals(
                1,
                viewModel.chatState.value.operativeMessages.size,
            )
        }
}
