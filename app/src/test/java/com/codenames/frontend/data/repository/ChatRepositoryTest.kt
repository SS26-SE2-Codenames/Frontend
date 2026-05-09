package com.codenames.frontend.data.repository

import com.codenames.frontend.network.dto.ChatMessageDto
import com.codenames.frontend.network.websocket.GameWebSocketHandler
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test


class ChatRepositoryTest {
    private lateinit var webSocketHandler: GameWebSocketHandler
    private lateinit var repository: ChatRepository

    private val testTopic = "/topic/chat"
    private val testUser = "TestUser"
    private val testContent = "Hello World"
    private val testDto = ChatMessageDto(senderUsername = testUser, content = testContent)

    @Before
    fun setup() {
        webSocketHandler = mockk()
        repository = ChatRepository(webSocketHandler)
        // Suspend methods use coroutines (lighter version of thread) -> instead of every, we use coEvery
        coEvery { webSocketHandler.subscribeToChat(testTopic) } returns flowOf(testDto)
    }

    @Test
    fun testObserveChat_correctContent() = runTest {
        // Since we mock the function to return a flow, we can call toList and grab all the contents
        val result = repository.observeChat(testTopic, testUser).toList()
        assertEquals(testContent, result[0].text)
    }

    @Test
    fun testObserveChat_correctUser() = runTest {
        val result = repository.observeChat(testTopic, testUser).toList()
        assertEquals(testUser, result[0].sender)
    }

    @Test
    fun testObserveChat_fromMe() = runTest {
        val result = repository.observeChat(testTopic, testUser).toList()
        assertTrue(result[0].isFromMe)
    }

    @Test
    fun testObserveChat_notFromMe() = runTest {
        val result = repository.observeChat(testTopic, "NotTestUser").toList()
        assertFalse(result[0].isFromMe)
    }

    @Test
    fun testSendMessage() = runTest{
        coEvery { webSocketHandler.sendChatMessage(testTopic, testDto) } just Runs
        repository.sendMessage(testTopic, testUser, testContent)
        coVerify { webSocketHandler.sendChatMessage(testTopic, testDto) }
    }
}