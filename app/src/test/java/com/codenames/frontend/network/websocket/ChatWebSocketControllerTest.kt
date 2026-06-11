package com.codenames.frontend.network.websocket

import com.codenames.frontend.network.dto.ChatMessageDto
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

class ChatWebSocketControllerTest {
    private lateinit var wsClient: ChatWebSocketController
    private lateinit var sessionManager: WebSocketSessionManager
    private lateinit var session: StompSessionWithKxSerialization

    @Before
    fun setup() {
        sessionManager = mockk()
        session = mockk(relaxed = true)
        wsClient = ChatWebSocketController(sessionManager)

        coEvery { sessionManager.getSession() } returns session
    }

    @Test
    fun testSubscribeToChat() =
        runTest {
            val topic = "/topic/chat/123"

            coEvery {
                session.subscribe<ChatMessageDto>(topic)
            } returns emptyFlow()

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

            coVerify {
                session.convertAndSend(
                    destination,
                    msg,
                    ChatMessageDto.serializer(),
                )
            }
        }
}
