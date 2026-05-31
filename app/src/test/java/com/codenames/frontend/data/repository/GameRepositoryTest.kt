package com.codenames.frontend.data.repository

import com.codenames.frontend.data.model.enums.Team
import com.codenames.frontend.network.websocket.GameWebSocketHandler
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GameRepositoryTest {
    private val webSocketHandler: GameWebSocketHandler = mockk()
    private lateinit var gameRepository: GameRepository

    @Before
    fun setUp() {
        this.gameRepository = GameRepository(webSocketHandler)
    }

    @Test
    fun testStartGame() =
        runTest {
            val lobbyCode = "ABCDE"
            coEvery { webSocketHandler.startGame(any()) } just Runs

            gameRepository.startGame(lobbyCode)

            coVerify { webSocketHandler.startGame(any()) }
        }

    @Test
    fun testSubmitClue() =
        runTest {
            val lobbyCode = "ABCDE"
            val word = "test"
            val guessAmount = 2
            val currentTurn = Team.RED

            coEvery { webSocketHandler.sendClue(any()) } just Runs

            gameRepository.submitClue(lobbyCode, word, guessAmount, currentTurn)

            coVerify { webSocketHandler.sendClue(any()) }
        }
}
