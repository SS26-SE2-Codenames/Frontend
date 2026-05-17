package com.codenames.frontend.network.websocket

import android.util.Log
import com.codenames.frontend.network.dto.ChatMessageDto
import com.codenames.frontend.network.dto.GameMessage
import com.codenames.frontend.network.dto.GuessMessage
import com.codenames.frontend.network.dto.StartGameMessage
import com.codenames.frontend.network.dto.WebSocketJoinMessage
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.conversions.kxserialization.StompSessionWithKxSerialization
import org.hildan.krossbow.stomp.conversions.kxserialization.convertAndSend
import org.hildan.krossbow.stomp.conversions.kxserialization.json.withJsonConversions
import org.hildan.krossbow.stomp.conversions.kxserialization.subscribe
import org.junit.Before
import org.junit.Test

class GameWebSocketHandlerTest {
    private lateinit var client: StompClient
    private lateinit var session: StompSessionWithKxSerialization
    private lateinit var wsClient: GameWebSocketHandler

    @Before
    fun setup() {
        client = mockk()
        session = mockk(relaxed = true)
        wsClient = GameWebSocketHandler(client)
        wsClient.session = session
    }

    @Test
    fun testConnectStomp() =
        runTest {
            val client = mockk<StompClient>()
            val session = mockk<StompSession>()
            val sessionWithJson = mockk<StompSessionWithKxSerialization>()

            mockkStatic(Log::class)

            coEvery { client.connect(BASE_URL) } returns session
            every { Log.d(any(), any()) } returns 0

            coEvery {
                sessionWithJson.subscribe<GameMessage>(any(), any())
            } returns emptyFlow()

            val wsClient = GameWebSocketHandler(client)

            wsClient.connectStomp()

            coVerify {
                client.connect(BASE_URL)
                session.withJsonConversions()
            }
        }

    @Test
    fun testSendGuess_sendsCorrectMessage(): Unit =
        runTest {
            val session = mockk<StompSessionWithKxSerialization>(relaxed = true)
            val client = mockk<StompClient>()

            val wsClient = GameWebSocketHandler(client)
            wsClient.session = session

            val msg = GuessMessage("name", "word", 1)

            wsClient.sendGuess(msg)

            coVerify {
                session.convertAndSend("/app/game/guess", msg, GuessMessage.serializer())
            }
        }

    @Test
    fun testSubscribeToLobby_subscribesToCorrectTopic(): Unit =
        runTest {
            val session = mockk<StompSessionWithKxSerialization>(relaxed = true)
            val client = mockk<StompClient>()

            coEvery {
                session.subscribe<GameMessage>("/topic/game/ABCDE")
            } returns emptyFlow()

            val wsClient = GameWebSocketHandler(client)
            wsClient.session = session

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
            val session = mockk<StompSessionWithKxSerialization>(relaxed = true)
            val client = mockk<StompClient>()

            val wsClient = GameWebSocketHandler(client)
            wsClient.session = session

            val msg = WebSocketJoinMessage("name", "1234")

            wsClient.sendReconnectMessage(msg)

            coVerify {
                session.convertAndSend("/app/join", msg, WebSocketJoinMessage.serializer())
            }
        }

    @Test
    fun testSubscribeToChat() =
        runTest {
            val topic = "/topic/chat/123"

            wsClient.subscribeToChat(topic)

            coVerify {
                session.subscribe(
                    match { it.destination == topic },
                    ChatMessageDto.serializer(),
                )
            }
        }

    @Test
    fun testSendMessage() =
        runTest {
            val destination = "app/chat/123"
            val msg = ChatMessageDto("TestUser", "TestMsg")

            wsClient.sendChatMessage(destination, msg)

            coVerify { session.convertAndSend(destination, msg, ChatMessageDto.serializer()) }
        }

    @Test
    fun testStartGame() =
        runTest {
            val destination = "/app/start-game"
            val msg = StartGameMessage(lobbyCode = "ABCDE")

            wsClient.startGame(msg)

            coVerify { session.convertAndSend(destination, msg, StartGameMessage.serializer()) }
        }
}
