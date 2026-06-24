package com.codenames.frontend.viewmodel

import android.util.Log
import com.codenames.frontend.data.model.GameState
import com.codenames.frontend.data.model.RejoinState
import com.codenames.frontend.data.model.SessionState
import com.codenames.frontend.data.model.enums.CardType
import com.codenames.frontend.data.model.enums.ConnectionState
import com.codenames.frontend.data.model.enums.Role
import com.codenames.frontend.data.model.enums.Team
import com.codenames.frontend.data.repository.GameRepository
import com.codenames.frontend.network.dto.CardDto
import com.codenames.frontend.network.dto.ClueDto
import com.codenames.frontend.network.dto.GameMessage
import com.codenames.frontend.network.websocket.GameWebSocketController
import com.codenames.frontend.ui.roles.PlayerRoles
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    private val lobbyCode = "12345"
    private val testState =
        GameState(
            currentHint = "",
            cards = listOf(),
            currentTurn = PlayerRoles.RED_SPYMASTER,
            winner = null,
            remainingGuesses = 0,
        )
    private val testMessage =
        GameMessage(
            winner = null,
            Team.RED,
            Role.SPYMASTER,
            null,
            listOf(),
        )

    private lateinit var viewModel: GameViewModel
    private lateinit var client: GameWebSocketController
    private lateinit var gameRepository: GameRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        client = mockk<GameWebSocketController>()
        gameRepository = mockk(relaxed = true)

        viewModel =
            GameViewModel(
                client,
                gameRepository,
            )
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

            viewModel.connect(lobbyCode)

            advanceUntilIdle()

            coVerify { client.connectStomp() }
            coVerify { client.subscribeToLobby(lobbyCode) }

            assertEquals(testState, viewModel.uiState.value)
        }

    @Test
    fun connect_shouldCallClientAndUpdateState_isAlreadyConnected() =
        runTest {
            val flow = flowOf(testMessage)

            coEvery { client.connectStomp() } just Runs
            coEvery { client.subscribeToLobby(lobbyCode) } returns flow

            viewModel.connect(lobbyCode)

            advanceUntilIdle()

            viewModel.connect(lobbyCode)

            coVerify { client.connectStomp() }
            coVerify { client.subscribeToLobby(lobbyCode) }

            assertEquals(testState, viewModel.uiState.value)
        }

    @Test
    fun handleMessage_updatesGameState() =
        runTest {
            mockkStatic(Log::class)
            every { Log.d(any(), any()) } returns 0
            val message =
                GameMessage(
                    winner = null,
                    currentTurn = Team.BLUE,
                    currentPhase = Role.OPERATIVE,
                    currentClue = ClueDto("EAGLE", 3),
                    cardList =
                        listOf(
                            CardDto("BERLIN", CardType.BLUE, false),
                            CardDto("ROME", CardType.RED, true),
                        ),
                )

            viewModel.handleMessage(message)

            val state = viewModel.uiState.value

            assertEquals(PlayerRoles.BLUE_OPERATIVE, state.currentTurn)
            assertEquals("EAGLE", state.currentHint)
            assertEquals(3, state.numGuesses)
            assertEquals(2, state.cards.size)
            assertEquals("BERLIN", state.cards[0].word)
        }

    @Test
    fun connect_shouldUpdateConnectionStateOnError() =
        runTest {
            coEvery {
                client.connectStomp()
            } throws RuntimeException("Connection failed")

            viewModel.connect(lobbyCode)

            advanceUntilIdle()

            val state = viewModel.connectionState.value

            assertTrue(state is ConnectionState.Error)

            assertEquals(
                "Connection failed",
                (state as ConnectionState.Error).message,
            )
        }

    @Test
    fun connect_asHost_shouldStartGame() =
        runTest {
            coEvery { client.connectStomp() } just Runs
            coEvery { client.subscribeToLobby(any()) } returns emptyFlow()

            coEvery {
                gameRepository.startGame(lobbyCode)
            } just Runs

            viewModel.connect(
                lobbyCode,
                isHost = true,
            )

            advanceUntilIdle()

            coVerify {
                gameRepository.startGame(lobbyCode)
            }
        }

    @Test
    fun connect_asHost_blankLobbyCode_shouldNotStartGame() =
        runTest {
            coEvery { client.connectStomp() } just Runs
            coEvery { client.subscribeToLobby(any()) } returns emptyFlow()

            viewModel.connect(
                "",
                isHost = true,
            )

            advanceUntilIdle()

            coVerify(exactly = 0) {
                gameRepository.startGame(any())
            }
        }

    @Test
    fun handleMessage_withNullClue_setsEmptyHint() =
        runTest {
            mockkStatic(Log::class)
            every { Log.d(any(), any()) } returns 0

            val message =
                GameMessage(
                    winner = null,
                    currentTurn = Team.RED,
                    currentPhase = Role.OPERATIVE,
                    currentClue = null,
                    cardList = emptyList(),
                )

            viewModel.handleMessage(message)

            assertEquals("", viewModel.uiState.value.currentHint)
        }

    @Test
    fun testSubmitClue_RedSpymaster_Success() =
        runTest {
            coEvery {
                gameRepository.submitClue(any(), any(), any(), any())
            } just Runs

            viewModel.handleMessage(testMessage.copy(currentTurn = Team.RED, currentPhase = Role.SPYMASTER))

            viewModel.submitClue(lobbyCode, "EAGLE", 2, Team.RED)
            advanceUntilIdle()

            coVerify { gameRepository.submitClue(lobbyCode, "EAGLE", 2, Team.RED) }
        }

    @Test
    fun testSubmitClue_NetworkError_UpdatesConnectionState() =
        runTest {
            coEvery {
                gameRepository.submitClue(any(), any(), any(), any())
            } throws Exception("Network connection failed")

            viewModel.handleMessage(testMessage.copy(currentTurn = Team.RED, currentPhase = Role.SPYMASTER))

            viewModel.submitClue(lobbyCode, "EAGLE", 2, Team.RED)
            advanceUntilIdle()

            val state = viewModel.connectionState.value
            assertTrue(state is ConnectionState.Error)
        }

    @Test
    fun testSubmitClue_whenTurnIsNone_doesNotSendClue() =
        runTest {
            viewModel.submitClue(lobbyCode, "EAGLE", 2, Team.RED)
            advanceUntilIdle()
            coVerify(exactly = 0) { gameRepository.submitClue(any(), any(), any(), any()) }
        }

    @Test
    fun testSubmitClue_blankLobbyCode_doesNotSendClue() =
        runTest {
            viewModel.handleMessage(testMessage.copy(currentTurn = Team.RED, currentPhase = Role.SPYMASTER))

            viewModel.submitClue("", "EAGLE", 2, Team.RED)
            advanceUntilIdle()
            coVerify(exactly = 0) {
                gameRepository.submitClue(any(), any(), any(), any())
            }
        }

    @Test
    fun testSubmitClue_nullTeam_doesNotSendClue() =
        runTest {
            viewModel.handleMessage(testMessage.copy(currentTurn = Team.RED, currentPhase = Role.SPYMASTER))

            viewModel.submitClue(lobbyCode, "EAGLE", 2, null)
            advanceUntilIdle()

            coVerify(exactly = 0) {
                gameRepository.submitClue(any(), any(), any(), any())
            }
        }

    @Test
    fun testSubmitClue_wrongTeamForCurrentTurn_doesNotSendClue() =
        runTest {
            viewModel.handleMessage(testMessage.copy(currentTurn = Team.RED, currentPhase = Role.SPYMASTER))

            viewModel.submitClue(lobbyCode, "EAGLE", 2, Team.BLUE)
            advanceUntilIdle()

            coVerify(exactly = 0) {
                gameRepository.submitClue(any(), any(), any(), any())
            }
        }

    @Test
    fun testSubmitGuess_RedOperative_Success() =
        runTest {
            coEvery {
                gameRepository.submitGuess(any(), any(), any())
            } just Runs

            viewModel.handleMessage(testMessage.copy(currentTurn = Team.RED, currentPhase = Role.OPERATIVE))

            viewModel.submitGuess(lobbyCode, 3, Team.RED)
            advanceUntilIdle()

            coVerify { gameRepository.submitGuess(lobbyCode, 3, Team.RED) }
        }

    @Test
    fun testSubmitGuess_whenTurnIsSpymaster_doesNotSendGuess() =
        runTest {
            viewModel.handleMessage(testMessage.copy(currentTurn = Team.RED, currentPhase = Role.SPYMASTER))

            viewModel.submitGuess(lobbyCode, 3, Team.RED)
            advanceUntilIdle()

            coVerify(exactly = 0) {
                gameRepository.submitGuess(any(), any(), any())
            }
        }

    @Test
    fun testSubmitGuess_blankLobbyCode_doesNotSendGuess() =
        runTest {
            viewModel.handleMessage(testMessage.copy(currentTurn = Team.RED, currentPhase = Role.OPERATIVE))

            viewModel.submitGuess("", 3, Team.RED)
            advanceUntilIdle()

            coVerify(exactly = 0) {
                gameRepository.submitGuess(any(), any(), any())
            }
        }

    @Test
    fun testSubmitGuess_NetworkError_UpdatesConnectionState() =
        runTest {
            coEvery {
                gameRepository.submitGuess(any(), any(), any())
            } throws Exception("Network connection failed")

            viewModel.handleMessage(testMessage.copy(currentTurn = Team.RED, currentPhase = Role.OPERATIVE))

            viewModel.submitGuess(lobbyCode, 3, Team.RED)
            advanceUntilIdle()

            val state = viewModel.connectionState.value
            assertTrue(state is ConnectionState.Error)
        }

    @Test
    fun submitGuesses_redOperative_sendsEachGuessInOrder() =
        runTest {
            coEvery {
                gameRepository.submitGuess(any(), any(), any())
            } just Runs

            viewModel.handleMessage(testMessage.copy(currentTurn = Team.RED, currentPhase = Role.OPERATIVE))

            viewModel.submitGuesses(lobbyCode, listOf(2, 4), Team.RED)
            advanceUntilIdle()

            coVerify {
                gameRepository.submitGuess(lobbyCode, 2, Team.RED)
                gameRepository.submitGuess(lobbyCode, 4, Team.RED)
            }
        }

    @Test
    fun submitGuesses_blueOperative_sendsBlueTeamGuess() =
        runTest {
            coEvery {
                gameRepository.submitGuess(any(), any(), any())
            } just Runs

            viewModel.handleMessage(testMessage.copy(currentTurn = Team.BLUE, currentPhase = Role.OPERATIVE))

            viewModel.submitGuesses(lobbyCode, listOf(1), Team.BLUE)
            advanceUntilIdle()

            coVerify {
                gameRepository.submitGuess(lobbyCode, 1, Team.BLUE)
            }
        }

    @Test
    fun submitGuesses_emptyPositions_doesNotSendGuess() =
        runTest {
            viewModel.handleMessage(testMessage.copy(currentTurn = Team.RED, currentPhase = Role.OPERATIVE))

            viewModel.submitGuesses(lobbyCode, emptyList(), Team.RED)
            advanceUntilIdle()

            coVerify(exactly = 0) {
                gameRepository.submitGuess(any(), any(), any())
            }
        }

    @Test
    fun submitGuesses_blankLobbyCode_doesNotSendGuess() =
        runTest {
            viewModel.handleMessage(testMessage.copy(currentTurn = Team.RED, currentPhase = Role.OPERATIVE))

            viewModel.submitGuesses("", listOf(3), Team.RED)
            advanceUntilIdle()

            coVerify(exactly = 0) {
                gameRepository.submitGuess(any(), any(), any())
            }
        }

    @Test
    fun submitGuesses_nullTeam_doesNotSendGuess() =
        runTest {
            viewModel.handleMessage(testMessage.copy(currentTurn = Team.RED, currentPhase = Role.OPERATIVE))

            viewModel.submitGuesses(lobbyCode, listOf(3), null)
            advanceUntilIdle()

            coVerify(exactly = 0) {
                gameRepository.submitGuess(any(), any(), any())
            }
        }

    @Test
    fun submitGuesses_whenTurnIsSpymaster_doesNotSendGuess() =
        runTest {
            viewModel.handleMessage(testMessage.copy(currentTurn = Team.RED, currentPhase = Role.SPYMASTER))

            viewModel.submitGuesses(lobbyCode, listOf(3), Team.RED)
            advanceUntilIdle()

            coVerify(exactly = 0) {
                gameRepository.submitGuess(any(), any(), any())
            }
        }

    @Test
    fun submitGuesses_wrongTeamForCurrentTurn_doesNotSendGuess() =
        runTest {
            viewModel.handleMessage(testMessage.copy(currentTurn = Team.RED, currentPhase = Role.OPERATIVE))

            viewModel.submitGuesses(lobbyCode, listOf(3), Team.BLUE)
            advanceUntilIdle()

            coVerify(exactly = 0) {
                gameRepository.submitGuess(any(), any(), any())
            }
        }

    @Test
    fun submitGuesses_networkError_updatesConnectionState() =
        runTest {
            coEvery {
                gameRepository.submitGuess(any(), any(), any())
            } throws Exception("Network connection failed")

            viewModel.handleMessage(testMessage.copy(currentTurn = Team.RED, currentPhase = Role.OPERATIVE))

            viewModel.submitGuesses(lobbyCode, listOf(3), Team.RED)
            advanceUntilIdle()

            val state = viewModel.connectionState.value
            assertTrue(state is ConnectionState.Error)
        }

    @Test
    fun passTurn_redOperative_sendsPassTurn() =
        runTest {
            coEvery {
                gameRepository.passTurn(any(), any())
            } just Runs

            viewModel.handleMessage(testMessage.copy(currentTurn = Team.RED, currentPhase = Role.OPERATIVE))

            viewModel.passTurn(lobbyCode, Team.RED)
            advanceUntilIdle()

            coVerify {
                gameRepository.passTurn(lobbyCode, Team.RED)
            }
        }

    @Test
    fun passTurn_blankLobbyCode_doesNotSendPassTurn() =
        runTest {
            viewModel.handleMessage(testMessage.copy(currentTurn = Team.RED, currentPhase = Role.OPERATIVE))

            viewModel.passTurn("", Team.RED)
            advanceUntilIdle()

            coVerify(exactly = 0) {
                gameRepository.passTurn(any(), any())
            }
        }

    @Test
    fun passTurn_nullTeam_doesNotSendPassTurn() =
        runTest {
            viewModel.handleMessage(testMessage.copy(currentTurn = Team.RED, currentPhase = Role.OPERATIVE))

            viewModel.passTurn(lobbyCode, null)
            advanceUntilIdle()

            coVerify(exactly = 0) {
                gameRepository.passTurn(any(), any())
            }
        }

    @Test
    fun passTurn_whenTurnIsSpymaster_doesNotSendPassTurn() =
        runTest {
            viewModel.handleMessage(testMessage.copy(currentTurn = Team.RED, currentPhase = Role.SPYMASTER))

            viewModel.passTurn(lobbyCode, Team.RED)
            advanceUntilIdle()

            coVerify(exactly = 0) {
                gameRepository.passTurn(any(), any())
            }
        }

    @Test
    fun passTurn_wrongTeamForCurrentTurn_doesNotSendPassTurn() =
        runTest {
            viewModel.handleMessage(testMessage.copy(currentTurn = Team.RED, currentPhase = Role.OPERATIVE))

            viewModel.passTurn(lobbyCode, Team.BLUE)
            advanceUntilIdle()

            coVerify(exactly = 0) {
                gameRepository.passTurn(any(), any())
            }
        }

    @Test
    fun passTurn_networkError_updatesConnectionState() =
        runTest {
            coEvery {
                gameRepository.passTurn(any(), any())
            } throws Exception("Network connection failed")

            viewModel.handleMessage(testMessage.copy(currentTurn = Team.RED, currentPhase = Role.OPERATIVE))

            viewModel.passTurn(lobbyCode, Team.RED)
            advanceUntilIdle()

            val state = viewModel.connectionState.value
            assertTrue(state is ConnectionState.Error)
        }

    @Test
    fun testSendRejoinMessage_sendsMessage() =
        runTest {
            coEvery {
                gameRepository.sendRejoin(any(), any(), any())
            } just Runs

            val rejoinState = RejoinState.Available(SessionState(lobbyCode, Role.OPERATIVE, Team.RED))

            viewModel.rejoinGame("User", UUID.randomUUID(), rejoinState)

            advanceUntilIdle()

            coVerify {
                gameRepository.sendRejoin(any(), any(), any())
            }
        }

    @Test
    fun testSendRejoinMessage_ignoresWithoutRejoinState() =
        runTest {
            coEvery {
                gameRepository.sendRejoin(any(), any(), any())
            } just Runs

            val rejoinState = null

            viewModel.rejoinGame("User", UUID.randomUUID(), rejoinState)

            advanceUntilIdle()

            coVerify(exactly = 0) {
                gameRepository.sendRejoin(any(), any(), any())
            }
        }

    @Test
    fun testSendRejoinMessage_ignoresNullValuesInRejoinState() =
        runTest {
            coEvery {
                gameRepository.sendRejoin(any(), any(), any())
            } just Runs

            val rejoinState = RejoinState.Available(SessionState(null, null, null))

            viewModel.rejoinGame("User", UUID.randomUUID(), rejoinState)

            advanceUntilIdle()

            coVerify(exactly = 0) {
                gameRepository.sendRejoin(any(), any(), any())
            }
        }

    @Test
    fun handleMessage_withError_setsConnectionError() =
        runTest {
            val message =
                GameMessage(
                    error = "Not your turn.",
                )

            viewModel.handleMessage(message)

            val state = viewModel.connectionState.value

            assertTrue(state is ConnectionState.Error)
            assertEquals(
                "Not your turn.",
                (state as ConnectionState.Error).message,
            )
        }

    @Test
    fun clearError_whenConnectionStateIsError_resetsToIdle() =
        runTest {
            coEvery {
                client.connectStomp()
            } throws RuntimeException("Connection failed")

            viewModel.connect(lobbyCode)

            advanceUntilIdle()

            assertTrue(viewModel.connectionState.value is ConnectionState.Error)

            viewModel.clearError()

            assertEquals(ConnectionState.IDLE, viewModel.connectionState.value)
        }

    @Test
    fun resetGameState_resetsGameState() =
        runTest {
            mockkStatic(Log::class)
            every { Log.d(any(), any()) } returns 0

            coEvery { client.disconnect() } just Runs

            viewModel.handleMessage(testMessage)

            advanceUntilIdle()

            viewModel.resetGameState()

            advanceUntilIdle()

            val compareMessage = GameState()

            assertEquals(compareMessage.cards, viewModel.uiState.value.cards)
            assertEquals(compareMessage.winner, viewModel.uiState.value.winner)
            assertEquals(compareMessage.currentTurn, viewModel.uiState.value.currentTurn)
        }

    @Test
    fun requestCheat_callsRepository() =
        runTest {
            viewModel.requestCheat(
                lobbyCode = "ABCD",
                username = "Max",
                positions = listOf(1, 3, 5),
            )

            advanceUntilIdle()

            coVerify {
                gameRepository.requestCheat(
                    "ABCD",
                    "Max",
                    listOf(1, 3, 5),
                )
            }
        }

    @Test
    fun requestCheat_emptyPositions_doesNothing() =
        runTest {
            viewModel.requestCheat(
                lobbyCode = "ABCD",
                username = "Max",
                positions = emptyList(),
            )

            coVerify(exactly = 0) {
                gameRepository.requestCheat(
                    any(),
                    any(),
                    any(),
                )
            }
        }

    @Test
    fun requestCheat_blankLobbyCode_doesNothing() =
        runTest {
            viewModel.requestCheat(
                lobbyCode = "",
                username = "Max",
                positions = listOf(1, 2, 3),
            )

            coVerify(exactly = 0) {
                gameRepository.requestCheat(
                    any(),
                    any(),
                    any(),
                )
            }
        }

    @Test
    fun requestCheat_repositoryThrows() =
        runTest {
            coEvery {
                gameRepository.requestCheat(any(), any(), any())
            } throws RuntimeException()

            viewModel.requestCheat(
                "ABCD",
                "Max",
                listOf(1, 2, 3),
            )

            advanceUntilIdle()

            assertTrue(viewModel.connectionState.value is ConnectionState.Error)
        }

    @Test
    fun exposeCheat_callsRepository() =
        runTest {
            viewModel.exposeCheat("ABCD", "Max")

            advanceUntilIdle()

            coVerify { gameRepository.exposeCheat("ABCD", "Max") }
        }

    @Test
    fun exposeCheat_invalidInput_doesNothing() =
        runTest {
            viewModel.exposeCheat("", "Max")
            viewModel.exposeCheat("ABCD", "")

            coVerify(exactly = 0) { gameRepository.exposeCheat(any(), any()) }
        }

    @Test
    fun exposeCheat_repositoryThrows() =
        runTest {
            coEvery { gameRepository.exposeCheat(any(), any()) } throws RuntimeException()

            viewModel.exposeCheat("ABCD", "Max")
            advanceUntilIdle()

            assertTrue(viewModel.connectionState.value is ConnectionState.Error)
        }
}
