package com.codenames.frontend.network.websocket

import com.codenames.frontend.data.model.enums.Team
import com.codenames.frontend.network.dto.ClueMessageDto
import com.codenames.frontend.network.dto.GameMessage
import com.codenames.frontend.network.dto.StartGameMessage
import com.codenames.frontend.network.dto.WebSocketJoinMessage
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import org.hildan.krossbow.stomp.conversions.kxserialization.StompSessionWithKxSerialization
import org.hildan.krossbow.stomp.conversions.kxserialization.convertAndSend
import org.hildan.krossbow.stomp.conversions.kxserialization.subscribe
import org.junit.Before
import org.junit.Test

class GameWebSocketControllerTest {
    private lateinit var wsClient: GameWebSocketController
    private lateinit var sessionManager: WebSocketSessionManager
    private lateinit var session: StompSessionWithKxSerialization

    @Before
    fun setup() {
        sessionManager = mockk()
        session = mockk(relaxed = true)
        wsClient = GameWebSocketController(sessionManager)

        coEvery { sessionManager.getSession() } returns session
    }

    @Test
    fun testConnect() =
        runTest {
            coEvery { sessionManager.connectStomp() } returns Unit

            wsClient.connectStomp()

            coVerify { sessionManager.connectStomp() }
        }

    @Test
    fun testSubscribeToLobby_subscribesToCorrectTopic(): Unit =
        runTest {
            coEvery {
                session.subscribe<GameMessage>("/topic/game/ABCDE")
            } returns emptyFlow()

            wsClient.subscribeToLobby("ABCDE")

            coVerify {
                session.subscribe(
                    match { it.destination == "/topic/game/ABCDE" },
                    GameMessage.serializer(),
                )
            }
        }

    @Test
    fun testSendReconnectMessageSendsMessage() =
        runTest {
            val msg = WebSocketJoinMessage("name", "1234")

            wsClient.sendReconnectMessage(msg)

            coVerify {
                session.convertAndSend("/app/join", msg, WebSocketJoinMessage.serializer())
            }
        }

    @Test
    fun testStartGame() =
        runTest {
            val destination = "/app/start-game"
            val msg = StartGameMessage(lobbyCode = "ABCDE")

            wsClient.startGame(msg)

            coVerify { session.convertAndSend(destination, msg, StartGameMessage.serializer()) }
        }

    @Test
    fun testSendClue() =
        runTest {
            val lobbyCode = "LOBBY123"
            val word = "clueWord"
            val guessAmount = 2
            val currentTurn = Team.RED

            val clueMessage = ClueMessageDto(lobbyCode, word, guessAmount, currentTurn)

            wsClient.sendClue(clueMessage)

            val expectedMsg =
                ClueMessageDto(
                    lobbyCode = lobbyCode,
                    word = word,
                    guessAmount = guessAmount,
                    currentTurn = currentTurn,
                )

            coVerify {
                session.convertAndSend(
                    "/app/submit-clue",
                    expectedMsg,
                    ClueMessageDto.serializer(),
                )
            }
        }
}
