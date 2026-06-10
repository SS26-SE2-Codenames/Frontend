package com.codenames.frontend.data.repository

import com.codenames.frontend.network.dto.ChatMessageDto
import com.codenames.frontend.network.websocket.GameWebSocketController
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.test.assertFailsWith

class ChatRepositoryTest {
    private lateinit var webSocketHandler: GameWebSocketController
    private lateinit var repository: ChatRepository

    private val testLobbyCode = "ABCD"
    private val testTeam = "RED"

    private val lobbyTopic = "/topic/chat/$testLobbyCode"
    private val teamTopic = "/topic/chat/$testLobbyCode/$testTeam"
    private val operativeTopic = "/topic/chat/$testLobbyCode/$testTeam/operative"
    private val testUser = "TestUser"
    private val testContent = "Hello World"
    private val testDto = ChatMessageDto(senderUsername = testUser, content = testContent)

    @Before
    fun setup() {
        webSocketHandler = mockk()
        repository = ChatRepository(webSocketHandler)
        // Suspend methods use coroutines (lighter version of thread) -> instead of every, we use coEvery
        coEvery { webSocketHandler.subscribeToChat(any()) } returns flowOf(testDto)
    }

    @Test
    fun testObserveChat_correctContent() =
        runTest {
            // Since we mock the function to return a flow, we can call toList and grab all the contents
            val result =
                repository
                    .observeLobbyChat(
                        testLobbyCode,
                        testUser,
                    ).toList()
            assertEquals(testContent, result[0].text)
        }

    @Test
    fun testObserveChat_correctUser() =
        runTest {
            val result =
                repository
                    .observeLobbyChat(
                        testLobbyCode,
                        testUser,
                    ).toList()
            assertEquals(testUser, result[0].sender)
        }

    @Test
    fun testObserveChat_fromMe() =
        runTest {
            val result =
                repository
                    .observeLobbyChat(
                        testLobbyCode,
                        testUser,
                    ).toList()
            assertTrue(result[0].isFromMe)
        }

    @Test
    fun testObserveChat_notFromMe() =
        runTest {
            val result =
                repository
                    .observeLobbyChat(
                        testLobbyCode,
                        "NotTestUser",
                    ).toList()
            assertFalse(result[0].isFromMe)
        }

    @Test
    fun testSendMessage() =
        runTest {
            coEvery {
                webSocketHandler.sendChatMessage(
                    "/app/chat/$testLobbyCode",
                    testDto,
                )
            } just Runs

            repository.sendLobbyMessage(
                testLobbyCode,
                testUser,
                testContent,
            )

            coVerify {
                webSocketHandler.sendChatMessage(
                    "/app/chat/$testLobbyCode",
                    testDto,
                )
            }
        }

    @Test
    fun testObserveChat_emptyFlow() =
        runTest {
            coEvery {
                webSocketHandler.subscribeToChat(lobbyTopic)
            } returns emptyFlow()

            val result =
                repository
                    .observeLobbyChat(
                        testLobbyCode,
                        testUser,
                    ).toList()

            assertTrue(result.isEmpty())
        }

    @Test
    fun testObserveChat_multipleMessages() =
        runTest {
            val dtos =
                flowOf(
                    ChatMessageDto(testUser, "First"),
                    ChatMessageDto("OtherUser", "Second"),
                )
            coEvery {
                webSocketHandler.subscribeToChat(lobbyTopic)
            } returns dtos

            val result =
                repository
                    .observeLobbyChat(
                        testLobbyCode,
                        testUser,
                    ).toList()

            assertEquals(2, result.size)
        }

    @Test
    fun testObserveChat_webSocketError() =
        runTest {
            coEvery {
                webSocketHandler.subscribeToChat(any())
            } throws RuntimeException()

            assertFailsWith<RuntimeException> {
                repository
                    .observeLobbyChat(
                        testLobbyCode,
                        testUser,
                    ).toList()
            }
        }

    @Test
    fun testObserveChat_errorDuringCollection() =
        runTest {
            val flowWithError =
                flow {
                    emit(testDto)
                    throw RuntimeException("Connection Lost")
                }
            coEvery {
                webSocketHandler.subscribeToChat(lobbyTopic)
            } returns flowWithError

            val flow =
                repository.observeLobbyChat(
                    testLobbyCode,
                    testUser,
                )
            assertFailsWith<RuntimeException> {
                flow.toList()
            }
        }

    // Testing early cancellation of flow using .take(1)
    @Test
    fun testObserveChat_earlyCancellation() =
        runTest {
            val dtos =
                flowOf(
                    ChatMessageDto(testUser, "First"),
                    ChatMessageDto("OtherUser", "Second"),
                )
            coEvery {
                webSocketHandler.subscribeToChat(lobbyTopic)
            } returns dtos

            val result =
                repository
                    .observeLobbyChat(
                        testLobbyCode,
                        testUser,
                    ).take(1)
                    .toList()

            assertEquals(1, result.size)
        }
}
