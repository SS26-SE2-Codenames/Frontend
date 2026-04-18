package com.codenames.codenames_frontend.viewmodel

import com.codenames.codenames_frontend.network.dto.GameMessage
import com.codenames.codenames_frontend.network.websocket.GameWebSocketHandler
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
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
    fun connect_shouldCallClientAndUpdateState() = runTest {
        val lobbyCode = "1234"

        val testMessage = GameMessage(/* fill with test data */)

        val flow = flowOf(testMessage)

        coEvery { client.connectStomp() } just Runs
        coEvery { client.subscribeToLobby(lobbyCode) } returns flow

        viewModel.connect(lobbyCode)

        advanceUntilIdle()

        coVerify { client.connectStomp() }
        coVerify { client.subscribeToLobby(lobbyCode) }

        assertEquals(testMessage, viewModel.uiState.value)
    }

    @Test
    fun connect_shouldCallClientAndUpdateState_isAlreadyConnected() = runTest {
        val lobbyCode = "1234"

        val testMessage = GameMessage(/* fill with test data */)

        val flow = flowOf(testMessage)

        coEvery { client.connectStomp() } just Runs
        coEvery { client.subscribeToLobby(lobbyCode) } returns flow

        viewModel.connect(lobbyCode)

        advanceUntilIdle()

        viewModel.connect(lobbyCode)

        coVerify { client.connectStomp() }
        coVerify { client.subscribeToLobby(lobbyCode) }

        assertEquals(testMessage, viewModel.uiState.value)
    }


}