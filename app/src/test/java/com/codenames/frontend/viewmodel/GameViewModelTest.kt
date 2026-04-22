package com.codenames.frontend.viewmodel

import com.codenames.frontend.network.dto.GameMessage
import com.codenames.frontend.network.dto.WebSocketJoinMessage
import com.codenames.frontend.network.websocket.GameWebSocketHandler
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

    private lateinit var viewModel: GameViewModel
    private val client: GameWebSocketHandler = mockk(relaxed = true)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = GameViewModel(client)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun connect_shouldCallClientAndUpdateState() =
        runTest {
            val lobbyCode = "1234"
            val username = "user"

            val testMessage =
                GameMessage(
                    "",
                    "red",
                    0,
                    0,
                    "",
                    0,
                    emptyList(),
                )

            val flow = flowOf(testMessage)

            coEvery { client.connectStomp() } just Runs
            coEvery { client.subscribeToLobby(lobbyCode) } returns flow

            viewModel.connect(username, lobbyCode)

            advanceUntilIdle()

            coVerify { client.connectStomp() }
            coVerify { client.subscribeToLobby(lobbyCode) }

            assertEquals(testMessage, viewModel.uiState.value)
        }

    @Test
    fun connect_shouldCallClientAndUpdateState_isAlreadyConnected() =
        runTest {
            val lobbyCode = "1234"
            val username = "user"

            val testMessage =
                GameMessage(
                    "",
                    "red",
                    0,
                    0,
                    "",
                    0,
                    emptyList(),
                )

            val flow = flowOf(testMessage)

            coEvery { client.connectStomp() } just Runs
            coEvery { client.subscribeToLobby(lobbyCode) } returns flow

            viewModel.connect(username, lobbyCode)

            advanceUntilIdle()

            viewModel.connect(username, lobbyCode)

            coVerify { client.connectStomp() }
            coVerify { client.subscribeToLobby(lobbyCode) }

            assertEquals(testMessage, viewModel.uiState.value)
        }

    @Test
    fun connect_shouldSendJoinMessage() =
        runTest {
            val lobbyCode = "1234"
            val username = "user"

            val testMessage =
                GameMessage(
                    "",
                    "red",
                    0,
                    0,
                    "",
                    0,
                    emptyList(),
                )

            val flow = flowOf(testMessage)

            coEvery { client.connectStomp() } just Runs
            coEvery { client.subscribeToLobby(lobbyCode) } returns flow
            coEvery { client.sendLobbyJoinMessage(any()) } just Runs

            viewModel.connect(username, lobbyCode)

            advanceUntilIdle()

            coVerify { client.sendLobbyJoinMessage(WebSocketJoinMessage(username, lobbyCode)) }
        }
}
