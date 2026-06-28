package com.codenames.frontend.data.repository

import com.codenames.frontend.data.model.enums.ChatMessageType
import com.codenames.frontend.data.model.enums.CheatExposureResult
import com.codenames.frontend.network.dto.ChatMessageDto
import com.codenames.frontend.network.websocket.ChatWebSocketController
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
    private lateinit var chatSocketHandler: ChatWebSocketController
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
        chatSocketHandler = mockk()
        repository = ChatRepository(chatSocketHandler)
        // Suspend methods use coroutines (lighter version of thread) -> instead of every, we use coEvery
        coEvery { chatSocketHandler.subscribeToChat(any()) } returns flowOf(testDto)
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
                chatSocketHandler.sendChatMessage(
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
                chatSocketHandler.sendChatMessage(
                    "/app/chat/$testLobbyCode",
                    testDto,
                )
            }
        }

    @Test
    fun testObserveChat_emptyFlow() =
        runTest {
            coEvery {
                chatSocketHandler.subscribeToChat(lobbyTopic)
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
                chatSocketHandler.subscribeToChat(lobbyTopic)
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
                chatSocketHandler.subscribeToChat(any())
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
                chatSocketHandler.subscribeToChat(lobbyTopic)
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
                chatSocketHandler.subscribeToChat(lobbyTopic)
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

    @Test
    fun testObserveTeamChat_correctMapping() =
        runTest {
            val dto = ChatMessageDto(testUser, testContent)

            coEvery { chatSocketHandler.subscribeToChat(teamTopic) } returns flowOf(dto)

            val result =
                repository
                    .observeTeamChat(testLobbyCode, testTeam, testUser)
                    .toList()

            assertEquals(testContent, result[0].text)
            assertEquals(testUser, result[0].sender)
        }

    @Test
    fun testObserveOperativeChat_correctTopicAndMapping() =
        runTest {
            val dto = ChatMessageDto(testUser, testContent)

            coEvery { chatSocketHandler.subscribeToChat(operativeTopic) } returns flowOf(dto)

            val result =
                repository
                    .observeOperativeChat(testLobbyCode, testTeam, testUser)
                    .toList()

            assertEquals(testContent, result[0].text)
            assertEquals(testUser, result[0].sender)
        }

    @Test
    fun testObserveTeamChat_usesCorrectTopic() =
        runTest {
            coEvery { chatSocketHandler.subscribeToChat(teamTopic) } returns flowOf(testDto)

            repository.observeTeamChat(testLobbyCode, testTeam, testUser).toList()

            coVerify { chatSocketHandler.subscribeToChat(teamTopic) }
        }

    @Test
    fun testObserveOperativeChat_usesCorrectTopic() =
        runTest {
            coEvery { chatSocketHandler.subscribeToChat(operativeTopic) } returns flowOf(testDto)

            repository.observeOperativeChat(testLobbyCode, testTeam, testUser).toList()

            coVerify { chatSocketHandler.subscribeToChat(operativeTopic) }
        }

    @Test
    fun testSendTeamMessage_callsCorrectDestination() =
        runTest {
            val dto = ChatMessageDto(testUser, testContent)

            coEvery {
                chatSocketHandler.sendChatMessage(teamTopic.replace("/topic", "/app"), dto)
            } just Runs

            repository.sendTeamMessage(testLobbyCode, testTeam, testUser, testContent)

            coVerify {
                chatSocketHandler.sendChatMessage(
                    "/app/chat/$testLobbyCode/$testTeam",
                    dto,
                )
            }
        }

    @Test
    fun testSendOperativeMessage_callsCorrectDestination() =
        runTest {
            val dto = ChatMessageDto(testUser, testContent)

            coEvery {
                chatSocketHandler.sendChatMessage("/app/chat/$testLobbyCode/$testTeam/operative", dto)
            } just Runs

            repository.sendOperativeMessage(testLobbyCode, testTeam, testUser, testContent)

            coVerify {
                chatSocketHandler.sendChatMessage(
                    "/app/chat/$testLobbyCode/$testTeam/operative",
                    dto,
                )
            }
        }

    @Test
    fun testObserveTeamChat_emptyFlow() =
        runTest {
            coEvery { chatSocketHandler.subscribeToChat(teamTopic) } returns emptyFlow()

            val result = repository.observeTeamChat(testLobbyCode, testTeam, testUser).toList()

            assertTrue(result.isEmpty())
        }

    @Test
    fun testObserveSystemMessages_correctMapping() =
        runTest {
            val dto =
                ChatMessageDto(
                    "System",
                    "London is correct",
                )

            coEvery {
                chatSocketHandler.subscribeToSystemMessages()
            } returns flowOf(dto)

            val result =
                repository
                    .observeSystemMessages(testUser)
                    .toList()

            assertEquals("System", result[0].sender)
            assertEquals("London is correct", result[0].text)
        }

    @Test
    fun testObserveOperativeChat_mapsCorrectExposeResult() =
        runTest {
            val dto = ChatMessageDto("System", "EXPOSE_CORRECT", ChatMessageType.SYSTEM)
            coEvery { chatSocketHandler.subscribeToChat(operativeTopic) } returns flowOf(dto)

            val result = repository.observeOperativeChat(testLobbyCode, testTeam, testUser).toList()

            assertEquals(
                "Cheat exposed correctly. The opposing team's next turn is skipped.",
                result[0].text,
            )
            assertEquals(CheatExposureResult.CORRECT, result[0].cheatExposureResult)
        }

    @Test
    fun testObserveOperativeChat_mapsWrongExposeResult() =
        runTest {
            val dto = ChatMessageDto("System", "EXPOSE_WRONG", ChatMessageType.SYSTEM)
            coEvery { chatSocketHandler.subscribeToChat(operativeTopic) } returns flowOf(dto)

            val result = repository.observeOperativeChat(testLobbyCode, testTeam, testUser).toList()

            assertEquals("Wrong accusation. Your team's turn ends.", result[0].text)
            assertEquals(CheatExposureResult.WRONG, result[0].cheatExposureResult)
        }
}
