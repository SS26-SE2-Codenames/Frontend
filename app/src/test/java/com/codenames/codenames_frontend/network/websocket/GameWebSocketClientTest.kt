package com.codenames.codenames_frontend.network.websocket

import com.codenames.codenames_frontend.network.dto.GameMessage
import com.codenames.codenames_frontend.network.dto.GuessMessage
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.conversions.kxserialization.StompSessionWithKxSerialization
import org.hildan.krossbow.stomp.conversions.kxserialization.convertAndSend
import org.hildan.krossbow.stomp.conversions.kxserialization.json.withJsonConversions
import org.hildan.krossbow.stomp.conversions.kxserialization.subscribe
import org.junit.Test

class GameWebSocketClientTest {

    @Test
    fun testConnectStomp() = runTest {
        val client = mockk<StompClient>()
        val session = mockk<StompSession>()
        val sessionWithJson = mockk<StompSessionWithKxSerialization>()

        coEvery { client.connect(BASE_URL) } returns session

        coEvery {
            sessionWithJson.subscribe<GameMessage>(any(), any())
        } returns emptyFlow()

        val wsClient = GameWebSocketClient(client)

        wsClient.connectStomp()

        coVerify {
            client.connect(BASE_URL)
            session.withJsonConversions()
        }
    }

    @Test
    fun testSendGuess_sendsCorrectMessage(): Unit = runTest {
        val session = mockk<StompSessionWithKxSerialization>(relaxed = true)
        val client = mockk<StompClient>()

        val wsClient = GameWebSocketClient(client)
        wsClient.session = session // ggf. sichtbar machen

        val msg = GuessMessage("name", "word", 1)

        wsClient.sendGuess(msg)

        coVerify {
            session.convertAndSend("/game/guess", msg, GuessMessage.serializer())
        }
    }

    @Test
    fun testSubscribeToLobby_subscribesToCorrectTopic(): Unit = runTest {
        val session = mockk<StompSessionWithKxSerialization>(relaxed = true)
        val client = mockk<StompClient>()

        coEvery {
            session.subscribe<GameMessage>("/game/ABCDE")
        } returns emptyFlow()

        val wsClient = GameWebSocketClient(client)
        wsClient.session = session

        wsClient.subscribeToLobby("ABCDE") //warnings can be ignored, only subscribe is tested here, not the return value

        //only check destination, ignore headers and payload
        coVerify {
            session.subscribe(
                match { it.destination == "/game/ABCDE" },
                GameMessage.serializer(),)
        }
    }
}