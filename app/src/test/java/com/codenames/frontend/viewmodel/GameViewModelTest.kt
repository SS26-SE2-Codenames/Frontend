package com.codenames.frontend.viewmodel

import android.util.Log
import com.codenames.frontend.data.model.ChatDomainModel
import com.codenames.frontend.data.model.GameState
import com.codenames.frontend.data.model.Player
import com.codenames.frontend.data.model.enums.CardType
import com.codenames.frontend.data.model.enums.Role
import com.codenames.frontend.data.model.enums.Team
import com.codenames.frontend.data.repository.ChatRepository
import com.codenames.frontend.data.repository.GameRepository
import com.codenames.frontend.network.dto.CardDto
import com.codenames.frontend.network.dto.GameMessage
import com.codenames.frontend.network.websocket.GameWebSocketHandler
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
import kotlinx.coroutines.flow.MutableSharedFlow
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

@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    private val lobbyCode = "12345"
    private val username = "user"
    private val team = Team.RED.name
    private val role = Role.OPERATIVE.name

    private val testState =
        GameState(
            currentHint = "Hint",
            cards = listOf(),
            currentTurn = PlayerRoles.RED_OPERATIVE,
            winner = null,
            remainingGuesses = 0
        )
    private val testMessage =
        GameMessage(
            winner = null,
            Team.RED,
            Role.SPYMASTER,
            0,
            0,
            "",
            0
        )


    private lateinit var viewModel: GameViewModel
    private lateinit var client: GameWebSocketHandler
    private lateinit var chatRepository: ChatRepository
    private lateinit var gameRepository: GameRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        client = mockk<GameWebSocketHandler>()
        chatRepository = mockk(relaxed = true)
        gameRepository = mockk(relaxed = true)
        viewModel = GameViewModel(client, chatRepository, gameRepository)
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

            assertEquals(testState, viewModel.uiState.value)
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
    fun testSendLobbyMessage() =
        runTest {
            val content = "Test msg"
            viewModel.sendLobbyMessage(lobbyCode, username, content)
            advanceUntilIdle()

            coVerify {
                chatRepository.sendMessage("/app/chat/$lobbyCode", username, content)
            }
        }

    @Test
    fun testSendTeamMessage() =
        runTest {
            val content = "Test msg"
            viewModel.sendTeamMessage(lobbyCode, team, username, content)
            advanceUntilIdle()

            coVerify {
                chatRepository.sendMessage("/app/chat/$lobbyCode/$team", username, content)
            }
        }

    @Test
    fun testSendOperativeMessage() =
        runTest {
            val content = "Test msg"
            viewModel.sendOperativeMessage(lobbyCode, team, username, content)
            advanceUntilIdle()

            coVerify {
                chatRepository.sendMessage("/app/chat/$lobbyCode/$team/operative", username, content)
            }
        }

    @Test
    fun testConnectUpdateLobbyChat() =
        runTest {
            // We use shared instead of state flow, since state requires us to pass an argument and fill the List with an element upon start of the test
            // This way we can never start from a clean slate and test with emit
            val customLobbyFlow = MutableSharedFlow<ChatDomainModel>(replay = 1)
            every { chatRepository.observeChat("/topic/chat/$lobbyCode", username) } returns customLobbyFlow
            coEvery { client.connectStomp() } just Runs
            coEvery { client.subscribeToLobby(any()) } returns emptyFlow()
            every {
                chatRepository.observeChat("/topic/chat/$lobbyCode/$team", username)
            } returns emptyFlow()

            viewModel.connect(username, lobbyCode, team, role)
            advanceUntilIdle()

            val testChat = ChatDomainModel(sender = username, text = "Test msg", isFromMe = false)
            customLobbyFlow.emit(testChat)
            advanceUntilIdle()

            val currentMessageList = viewModel.chatState.value.lobbyMessages
            assertEquals("Test msg", currentMessageList[0].text)
        }

    @Test
    fun testConnectUpdateTeamChat() =
        runTest {
            val customLobbyFlow = MutableSharedFlow<ChatDomainModel>()
            every { chatRepository.observeChat("/topic/chat/$lobbyCode/$team", username) } returns customLobbyFlow

            viewModel.connect(username, lobbyCode, team, role)
            advanceUntilIdle()

            val testChat = ChatDomainModel(sender = username, text = "Test msg", isFromMe = false)
            customLobbyFlow.emit(testChat)
            advanceUntilIdle()

            val currentMessageList = viewModel.chatState.value.teamMessages
            assertEquals("Test msg", currentMessageList[0].text)
        }

    @Test
    fun testConnectUpdateOperativeChat() =
        runTest {
            val customLobbyFlow = MutableSharedFlow<ChatDomainModel>()
            every { chatRepository.observeChat("/topic/chat/$lobbyCode/$team/operative", username) } returns customLobbyFlow

            viewModel.connect(username, lobbyCode, team, role)
            advanceUntilIdle()

            val testChat = ChatDomainModel(sender = username, text = "Test msg", isFromMe = false)
            customLobbyFlow.emit(testChat)
            advanceUntilIdle()

            val currentMessageList = viewModel.chatState.value.operativeMessages
            assertEquals("Test msg", currentMessageList[0].text)
        }

    @Test
    fun testConnectUpdateOperativeChat_notOperative() =
        runTest {
            val customLobbyFlow = MutableSharedFlow<ChatDomainModel>()
            every { chatRepository.observeChat("/topic/chat/$lobbyCode/$team/operative", username) } returns customLobbyFlow

            viewModel.connect(username, lobbyCode, team, Role.SPYMASTER.name)
            advanceUntilIdle()

            val testChat = ChatDomainModel(sender = username, text = "Test msg", isFromMe = false)
            customLobbyFlow.emit(testChat)
            advanceUntilIdle()

            val currentMessageList = viewModel.chatState.value.operativeMessages
            assertTrue(currentMessageList.isEmpty())
        }

    @Test
    fun handleMessage_updatesGameState() =
        runTest {
            mockkStatic(Log::class)
            every {Log.d(any(), any())} returns 0
            val message =
                GameMessage(
                    winner = null,
                    currentTurn = Team.BLUE,
                    currentPhase = Role.SPYMASTER,
                    currentRedFound = 1,
                    currentBlueFound = 2,
                    currentClue = "EAGLE",
                    remainingGuesses = 3,
                    cardList =
                        listOf(
                            CardDto("BERLIN", CardType.BLUE, false),
                            CardDto("ROME", CardType.RED, true),
                        ),
                )

            viewModel.handleMessage(message)

            val state = viewModel.uiState.value

            assertEquals(PlayerRoles.BLUE_SPYMASTER, state.currentTurn)
            assertEquals("EAGLE", state.currentHint)
            assertEquals(3, state.remainingGuesses)
            assertEquals(2, state.cards.size)
            assertEquals("BERLIN", state.cards[0].word)
        }
}
