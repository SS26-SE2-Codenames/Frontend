package com.codenames.frontend.viewmodel

import com.codenames.frontend.data.model.ChatDomainModel
import com.codenames.frontend.data.repository.ChatRepository
import com.codenames.frontend.network.dto.GameMessage
import com.codenames.frontend.network.dto.WebSocketJoinMessage
import com.codenames.frontend.network.websocket.GameWebSocketHandler
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    private val lobbyCode = "1234"
    private val username = "user"
    private val team = "RED"
    private val role = "OPERATIVE"

    private val testMessage =
        GameMessage(
            "",
            "red",
            0,
            0,
            "",
            0,
            emptyList(),
        )

    private lateinit var viewModel: GameViewModel
    private lateinit var client: GameWebSocketHandler
    private lateinit var chatRepository: ChatRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        client = mockk(relaxed = true)
        chatRepository = mockk(relaxed = true)
        viewModel = GameViewModel(client, chatRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun connect_shouldCallClientAndUpdateState() =
        runTest {

            val flow = flowOf(testMessage)

            coEvery { client.connectStomp() } just Runs
            coEvery { client.subscribeToLobby(lobbyCode) } returns flow

            viewModel.connect(username, lobbyCode, team, role)

            advanceUntilIdle()

            coVerify { client.connectStomp() }
            coVerify { client.subscribeToLobby(lobbyCode) }

            assertEquals(testMessage, viewModel.uiState.value)
        }

    @Test
    fun connect_shouldCallClientAndUpdateState_isAlreadyConnected() =
        runTest {

            val flow = flowOf(testMessage)

            coEvery { client.connectStomp() } just Runs
            coEvery { client.subscribeToLobby(lobbyCode) } returns flow

            viewModel.connect(username, lobbyCode, team, role)

            advanceUntilIdle()

            viewModel.connect(username, lobbyCode, team, role)

            coVerify { client.connectStomp() }
            coVerify { client.subscribeToLobby(lobbyCode) }

            assertEquals(testMessage, viewModel.uiState.value)
        }

    @Test
    fun connect_shouldSendJoinMessage() =
        runTest {

            val flow = flowOf(testMessage)

            coEvery { client.connectStomp() } just Runs
            coEvery { client.subscribeToLobby(lobbyCode) } returns flow
            coEvery { client.sendLobbyJoinMessage(any()) } just Runs

            viewModel.connect(username, lobbyCode, team, role)

            advanceUntilIdle()

            coVerify { client.sendLobbyJoinMessage(WebSocketJoinMessage(username, lobbyCode)) }
        }

    @Test
    fun testSendLobbyMessage() = runTest {
        val content = "Test msg"
        viewModel.sendLobbyMessage(lobbyCode, username, content)
        advanceUntilIdle()

        coVerify {
            chatRepository.sendMessage("/app/chat/$lobbyCode", username, content)
        }
    }

    @Test
    fun testSendTeamMessage() = runTest {
        val content = "Test msg"
        viewModel.sendTeamMessage(lobbyCode, team, username, content)
        advanceUntilIdle()

        coVerify {
            chatRepository.sendMessage("/app/chat/$lobbyCode/$team", username, content)
        }
    }

    @Test
    fun testSendOperativeMessage() = runTest {
        val content = "Test msg"
        viewModel.sendOperativeMessage(lobbyCode, team, username, content)
        advanceUntilIdle()

        coVerify {
            chatRepository.sendMessage("/app/chat/$lobbyCode/$team/operative", username, content)
        }
    }

}
