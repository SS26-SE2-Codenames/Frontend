package com.codenames.frontend.viewmodel

import android.util.Log
import com.codenames.frontend.data.model.enums.ChatTab
import com.codenames.frontend.data.model.enums.Role
import com.codenames.frontend.data.model.enums.Team
import com.codenames.frontend.data.repository.LobbyRepository
import com.codenames.frontend.data.repository.SessionRepository
import com.codenames.frontend.network.dto.LobbyResponse
import com.codenames.frontend.network.dto.PlayerDto
import com.codenames.frontend.ui.roles.PlayerRoles
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.UUID
import kotlin.test.assertNotNull
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
class LobbyViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    val repository = mockk<LobbyRepository>()
    val sessionRepository = mockk<SessionRepository>()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testCreateLobby_success() =
        runTest {
            val response =
                LobbyResponse(
                    lobbyCode = "1234",
                    playerList = listOf(PlayerDto("User", null, Team.RED, true)),
                    isStarted = false,
                )

            coEvery { repository.createLobby("User") } returns response
            coEvery { repository.getLobbyInfo(any()) } returns response
            coEvery { sessionRepository.clearUserId() } just Runs

            val viewModel = LobbyViewModel(repository, sessionRepository)

            viewModel.createLobby("User")

            advanceTimeBy(2000.milliseconds)

            viewModel.stopPollingForTest()

            val state = viewModel.state.value

            assertEquals("1234", state.lobbyCode)
            assertFalse(state.isLoading)
            assertNull(state.error)
        }

    @Test
    fun testCreateLobby_error() =
        runTest {
            coEvery { repository.createLobby(any()) } throws RuntimeException("Network error")
            coEvery { sessionRepository.clearUserId() } just Runs

            val viewModel = LobbyViewModel(repository, sessionRepository)

            viewModel.createLobby("Max")

            advanceTimeBy(2000.milliseconds)

            viewModel.stopPollingForTest()

            val state = viewModel.state.value

            assertEquals("Network error", state.error)
            assertFalse(state.isLoading)
        }

    @Test
    fun testJoinLobby_success() =
        runTest {
            val response =
                LobbyResponse(
                    lobbyCode = "1234",
                    playerList = listOf(PlayerDto("User", null, Team.RED, true)),
                    false,
                )

            coEvery { repository.joinLobby("User", "1234") } returns response
            coEvery { repository.getLobbyInfo(any()) } returns
                LobbyResponse(
                    lobbyCode = "1234",
                    playerList = listOf(PlayerDto("User", null, Team.RED, true)),
                    false,
                )

            coEvery { sessionRepository.clearUserId() } just Runs

            val viewModel = LobbyViewModel(repository, sessionRepository)

            viewModel.joinLobby("User", "1234")

            advanceTimeBy(2000.milliseconds)

            viewModel.stopPollingForTest()

            val state = viewModel.state.value

            assertEquals("1234", state.lobbyCode)
            assertFalse(state.isLoading)
            assertNull(state.error)
        }

    @Test
    fun testJoinLobby_error() =
        runTest {
            coEvery { repository.joinLobby(any(), any()) } throws RuntimeException("Network error")
            coEvery { sessionRepository.clearUserId() } just Runs

            val viewModel = LobbyViewModel(repository, sessionRepository)

            viewModel.joinLobby("Max", "1234")

            advanceTimeBy(2000.milliseconds)

            viewModel.stopPollingForTest()

            val state = viewModel.state.value

            assertEquals("Network error", state.error)
            assertFalse(state.isLoading)
        }

    @Test
    fun testLeaveLobby_success() =
        runTest {
            val response =
                LobbyResponse(
                    lobbyCode = "1234",
                    playerList = listOf(PlayerDto("User", null, Team.RED, true)),
                    false,
                )

            val response2 =
                LobbyResponse(
                    lobbyCode = "",
                    playerList = emptyList(),
                    false,
                )

            val userId = UUID.randomUUID()

            coEvery { repository.joinLobby("User", "1234") } returns response
            coEvery { repository.getLobbyInfo(any()) } returns response
            coEvery { repository.leaveLobby("1234", userId) } returns response2
            coEvery { sessionRepository.clearUserId() } just Runs

            val viewModel = LobbyViewModel(repository, sessionRepository)

            viewModel.joinLobby("User", "1234")
            advanceTimeBy(2000.milliseconds)
            viewModel.leaveLobby(userId = userId, onResult = {})

            advanceTimeBy(2000.milliseconds)

            viewModel.stopPollingForTest()

            val state = viewModel.state.value

            assertEquals(null, state.lobbyCode)
            assertFalse(state.isLoading)
            assertNull(state.error)
        }

    @Test
    fun testLeaveLobby_error() =
        runTest {
            val response =
                LobbyResponse(
                    lobbyCode = "1234",
                    playerList = listOf(PlayerDto("User", null, Team.RED, true)),
                    false,
                )
            val userID = UUID.randomUUID()

            coEvery { repository.joinLobby("User", "1234") } returns response
            coEvery { repository.leaveLobby(any(), any()) } throws RuntimeException("Network error")
            coEvery { sessionRepository.clearUserId() } just Runs

            val viewModel = LobbyViewModel(repository, sessionRepository)

            viewModel.joinLobby("User", "1234")

            advanceTimeBy(2000.milliseconds)

            viewModel.leaveLobby(userId = userID, onResult = {})

            advanceTimeBy(2000.milliseconds)

            viewModel.stopPollingForTest()

            val state = viewModel.state.value

            assertEquals("Network error", state.error)
            assertFalse(state.isLoading)
        }

    @Test
    fun testChangeRole_success() =
        runTest {
            val newRole = Role.OPERATIVE
            val newTeam = Team.RED
            val username = "User"

            val response =
                LobbyResponse(
                    lobbyCode = "1234",
                    playerList = listOf(PlayerDto("User", null, Team.RED, true)),
                    false,
                )

            val response2 =
                LobbyResponse(
                    lobbyCode = "1234",
                    playerList = listOf(PlayerDto("User", newRole, newTeam, true)),
                    false,
                )
            val userId = UUID.randomUUID()

            coEvery { repository.joinLobby("User", "1234") } returns response
            coEvery { repository.getLobbyInfo(any()) } returns response
            coEvery { repository.changeRole("User", userId, "1234", newRole, newTeam) } returns response2
            coEvery { sessionRepository.clearUserId() } just Runs

            val viewModel = LobbyViewModel(repository, sessionRepository)

            viewModel.joinLobby("User", "1234")

            advanceTimeBy(2000.milliseconds)

            viewModel.changeRole(newRole, newTeam, username, userId)
            viewModel.stopPollingForTest()

            advanceTimeBy(2000.milliseconds)

            val state = viewModel.state.value

            assertEquals("1234", state.lobbyCode)
            assertEquals(newRole, state.players[0].role)
            assertEquals(newTeam, state.players[0].team)
            assertFalse(state.isLoading)
            assertNull(state.error)
        }

    @Test
    fun testChangeRole_error() =
        runTest {
            val response =
                LobbyResponse(
                    lobbyCode = "1234",
                    playerList = listOf(PlayerDto("User", null, Team.RED, true)),
                    false,
                )

            coEvery { repository.joinLobby("User", "1234") } returns response
            coEvery {
                repository.changeRole(
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                )
            } throws RuntimeException("Network error")

            coEvery { sessionRepository.clearUserId() } just Runs

            val viewModel = LobbyViewModel(repository, sessionRepository)

            viewModel.joinLobby("User", "1234")

            advanceTimeBy(2000.milliseconds)

            viewModel.changeRole(Role.OPERATIVE, Team.RED, "User", UUID.randomUUID())

            advanceTimeBy(2000.milliseconds)

            viewModel.stopPollingForTest()

            val state = viewModel.state.value

            assertEquals("Network error", state.error)
            assertFalse(state.isLoading)
        }

    @Test
    fun testSetError_WithEmptyValue() =
        runTest {
            coEvery { repository.createLobby(any()) } throws RuntimeException("")
            coEvery { sessionRepository.clearUserId() } just Runs

            val viewModel = LobbyViewModel(repository, sessionRepository)

            viewModel.createLobby("Max")

            advanceTimeBy(2000.milliseconds)

            viewModel.stopPollingForTest()

            val state = viewModel.state.value

            assertEquals("Something went wrong. Please try again.", state.error)
            assertFalse(state.isLoading)
        }

    @Test
    fun testSetError_WithNullValue() =
        runTest {
            coEvery { repository.createLobby(any()) } throws RuntimeException()
            coEvery { sessionRepository.clearUserId() } just Runs

            val viewModel = LobbyViewModel(repository, sessionRepository)

            viewModel.createLobby("Max")

            advanceTimeBy(2000.milliseconds)

            viewModel.stopPollingForTest()

            val state = viewModel.state.value

            assertEquals("Something went wrong. Please try again.", state.error)
            assertFalse(state.isLoading)
        }

    // Polling Tests
    @Test
    fun testPolling_callsRepositoryRepeatedly() =
        runTest {
            coEvery { repository.getLobbyInfo(any()) } returns
                LobbyResponse(
                    lobbyCode = "1234",
                    playerList = listOf(PlayerDto("User", null, Team.RED, true)),
                    false,
                )

            val viewModel = LobbyViewModel(repository, sessionRepository)

            viewModel.startPollingForTest("1234")

            advanceTimeBy(4000.milliseconds)

            viewModel.stopPollingForTest()

            coVerify(atLeast = 2) {
                repository.getLobbyInfo("1234")
            }
        }

    @Test
    fun testPolling_stateIsUpdated() =
        runTest {
            coEvery { repository.getLobbyInfo(any()) } returnsMany
                listOf(
                    LobbyResponse("1", emptyList(), false),
                    LobbyResponse("2", emptyList(), false),
                )

            val viewModel = LobbyViewModel(repository, sessionRepository)

            viewModel.startPollingForTest("1234")

            advanceTimeBy(4000.milliseconds)

            viewModel.stopPollingForTest()

            assertEquals("2", viewModel.state.value.lobbyCode)
        }

    @Test
    fun testPolling_doesNotStartTwice() =
        runTest {
            coEvery { repository.getLobbyInfo("1234") } returns
                LobbyResponse(
                    lobbyCode = "1234",
                    playerList = listOf(PlayerDto("User", null, Team.RED, true)),
                    false,
                )

            val viewModel = LobbyViewModel(repository, sessionRepository)

            viewModel.startPollingForTest("1234")

            advanceTimeBy(4000.milliseconds)

            viewModel.startPollingForTest("2345")

            advanceTimeBy(2000.milliseconds)

            viewModel.stopPollingForTest()

            coVerify(atLeast = 2) {
                repository.getLobbyInfo("1234")
            }
        }

    // Tests with wrong lobby state
    @Test
    fun testCreateLobby_alreadyInLobby() =
        runTest {
            val response =
                LobbyResponse(
                    lobbyCode = "1234",
                    playerList = listOf(PlayerDto("User", null, Team.RED, true)),
                    false,
                )

            coEvery { repository.createLobby("User") } returns response
            coEvery { sessionRepository.clearUserId() } just Runs

            val viewModel = LobbyViewModel(repository, sessionRepository)

            viewModel.createLobby("User")

            advanceUntilIdle()

            viewModel.createLobby("User")

            advanceUntilIdle()

            val state = viewModel.state.value

            assertEquals("1234", state.lobbyCode)
            assertFalse(state.isLoading)
            assertEquals("Already in a lobby, creating a new lobby not possible", state.error)
        }

    @Test
    fun testJoinLobby_alreadyInLobby() =
        runTest {
            val response =
                LobbyResponse(
                    lobbyCode = "1234",
                    playerList = listOf(PlayerDto("User", null, Team.RED, true)),
                    false,
                )

            coEvery { repository.createLobby("User") } returns response
            coEvery { sessionRepository.clearUserId() } just Runs

            val viewModel = LobbyViewModel(repository, sessionRepository)

            viewModel.createLobby("User")

            advanceUntilIdle()

            viewModel.joinLobby("User", "1234")

            advanceUntilIdle()

            val state = viewModel.state.value

            assertEquals("1234", state.lobbyCode)
            assertFalse(state.isLoading)
            assertEquals("Already in a lobby, joining not possible", state.error)
        }

    @Test
    fun testLeaveLobby_notInLobby() =
        runTest {
            val viewModel = LobbyViewModel(repository, sessionRepository)

            viewModel.leaveLobby(UUID.randomUUID(), onResult = {})

            advanceUntilIdle()

            val state = viewModel.state.value

            assertEquals(null, state.lobbyCode)
            assertFalse(state.isLoading)
            assertEquals("Not in a lobby, leaving not possible", state.error)
        }

    @Test
    fun testLeaveLobby_nullIdDoesNothing() =
        runTest {
            val viewModel = LobbyViewModel(repository, sessionRepository)
            val username = "User"
            val lobbyCode = "ABCDE"
            val onResult = { bool: Boolean -> }

            val response =
                LobbyResponse(
                    lobbyCode = "ABCDE",
                    playerList = listOf(PlayerDto("User", null, Team.RED, true)),
                    false,
                )

            coEvery {
                repository.joinLobby(username, lobbyCode)
            } returns response
            coEvery { sessionRepository.clearUserId() } just Runs

            viewModel.joinLobby(username, lobbyCode)

            advanceUntilIdle()

            viewModel.leaveLobby(null, onResult)

            assertNotNull(viewModel.state.value.error)
        }

    @Test
    fun testChangeRoles_notInLobby() =
        runTest {
            val viewModel = LobbyViewModel(repository, sessionRepository)

            viewModel.changeRole(Role.OPERATIVE, Team.RED, "User", UUID.randomUUID())

            advanceUntilIdle()

            val state = viewModel.state.value

            assertEquals(null, state.lobbyCode)
            assertFalse(state.isLoading)
            assertEquals("Not in a Lobby", state.error)
        }

    @Test
    fun changeRole_DelegatesCorrectly1() {
        val viewModel = spyk<LobbyViewModel>(LobbyViewModel(repository, sessionRepository), recordPrivateCalls = true)
        viewModel.changeRole(PlayerRoles.BLUE_SPYMASTER, "Alice", UUID.randomUUID())

        verify {
            viewModel.changeRole(Role.SPYMASTER, Team.BLUE, "Alice", any())
        }
    }

    @Test
    fun changeRole_DelegatesCorrectly2() {
        val viewModel = spyk<LobbyViewModel>(LobbyViewModel(repository, sessionRepository), recordPrivateCalls = true)

        viewModel.changeRole(PlayerRoles.RED_OPERATIVE, "Bob", UUID.randomUUID())

        verify {
            viewModel.changeRole(Role.OPERATIVE, Team.RED, "Bob", any())
        }
    }

    @Test
    fun changeRole_DoesNothingWithoutId() =
        runTest {
            val viewModel = LobbyViewModel(repository, sessionRepository)

            viewModel.changeRole(PlayerRoles.RED_SPYMASTER, "User", null)

            assertNotNull(viewModel.state.value.error)
        }

    @Test
    fun `getRoleForUser returns BLUE_OPERATIVE`() =
        runTest {
            val viewModel = LobbyViewModel(repository, sessionRepository)

            val players =
                listOf(
                    PlayerDto(
                        username = "Max",
                        role = Role.OPERATIVE,
                        team = Team.BLUE,
                        isHost = true,
                    ),
                )

            val response =
                LobbyResponse(
                    lobbyCode = "ABCD",
                    playerList = players,
                    false,
                )

            coEvery {
                repository.joinLobby("Max", "ABCD")
            } returns response

            coEvery { sessionRepository.clearUserId() } just Runs

            viewModel.joinLobby("Max", "ABCD")

            advanceUntilIdle()

            val result = viewModel.getRoleForUser("Max")

            assertEquals(PlayerRoles.BLUE_OPERATIVE, result)
        }

    @Test
    fun `getRoleForUser returns NONE when player does not exist`() =
        runTest {
            val viewModel = LobbyViewModel(repository, sessionRepository)

            val result = viewModel.getRoleForUser("Unknown")

            assertEquals(PlayerRoles.NONE, result)
        }

    @Test
    fun `changeRole updates player role correctly`() =
        runTest {
            val viewModel = LobbyViewModel(repository, sessionRepository)

            val initialPlayers =
                listOf(
                    PlayerDto(
                        username = "Max",
                        role = Role.OPERATIVE,
                        team = Team.BLUE,
                        isHost = true,
                    ),
                )

            val updatedPlayers =
                listOf(
                    PlayerDto(
                        username = "Max",
                        role = Role.SPYMASTER,
                        team = Team.RED,
                        isHost = false,
                    ),
                )

            val joinResponse =
                LobbyResponse(
                    lobbyCode = "ABCD",
                    playerList = initialPlayers,
                    false,
                )

            val changeRoleResponse =
                LobbyResponse(
                    lobbyCode = "ABCD",
                    playerList = updatedPlayers,
                    false,
                )

            val userId = UUID.randomUUID()

            coEvery {
                repository.joinLobby("Max", "ABCD")
            } returns joinResponse
            coEvery { sessionRepository.clearUserId() } just Runs

            coEvery {
                repository.changeRole(
                    "Max",
                    userId,
                    "ABCD",
                    Role.SPYMASTER,
                    Team.RED,
                )
            } returns changeRoleResponse

            viewModel.joinLobby("Max", "ABCD")
            advanceUntilIdle()

            viewModel.changeRole(
                role = Role.SPYMASTER,
                team = Team.RED,
                username = "Max",
                userId = userId,
            )

            advanceUntilIdle()

            val result = viewModel.getRoleForUser("Max")

            assertEquals(PlayerRoles.RED_SPYMASTER, result)

            coVerify(exactly = 1) {
                repository.changeRole(
                    "Max",
                    any(),
                    "ABCD",
                    Role.SPYMASTER,
                    Team.RED,
                )
            }
        }

    @Test
    fun `changeRole sets error when not in lobby`() =
        runTest {
            val viewModel = LobbyViewModel(repository, sessionRepository)

            viewModel.changeRole(
                role = Role.SPYMASTER,
                team = Team.RED,
                username = "Max",
                userId = UUID.randomUUID(),
            )

            advanceUntilIdle()

            assertTrue(
                viewModel.state.value.error
                    ?.contains("Not in a Lobby") == true,
            )
        }

    @Test
    fun changeRole_DelegatesCorrectly_redSpymaster() {
        val viewModel = spyk<LobbyViewModel>(LobbyViewModel(repository, sessionRepository))

        viewModel.changeRole(PlayerRoles.RED_SPYMASTER, "Alice", UUID.randomUUID())

        verify {
            viewModel.changeRole(Role.SPYMASTER, Team.RED, "Alice", any())
        }
    }

    @Test
    fun changeRole_DelegatesCorrectly_blueOperative() {
        val viewModel = spyk<LobbyViewModel>(LobbyViewModel(repository, sessionRepository))

        viewModel.changeRole(PlayerRoles.BLUE_OPERATIVE, "Bob", UUID.randomUUID())

        verify {
            viewModel.changeRole(Role.OPERATIVE, Team.BLUE, "Bob", any())
        }
    }

    @Test
    fun changeRole_invalidRole_setsError() =
        runTest {
            val viewModel = LobbyViewModel(repository, sessionRepository)

            viewModel.changeRole(PlayerRoles.NONE, "User", UUID.randomUUID())

            advanceUntilIdle()

            assertEquals(
                "Invalid role",
                viewModel.state.value.error,
            )
        }

    @Test
    fun getRoleForUser_userNotFound_returnsNone() {
        val viewModel = LobbyViewModel(repository, sessionRepository)

        val result = viewModel.getRoleForUser("Unknown")

        assertEquals(PlayerRoles.NONE, result)
    }

    @Test
    fun sendStartGame_blankUsername_doesNothing() =
        runTest {
            val viewModel = LobbyViewModel(repository, sessionRepository)

            viewModel.sendStartGame("", UUID.randomUUID())

            advanceUntilIdle()

            coVerify(exactly = 0) {
                repository.sendStartGame(any(), any())
            }
        }

    @Test
    fun sendStartGame_NullId_doesNothing() =
        runTest {
            val viewModel = LobbyViewModel(repository, sessionRepository)

            viewModel.sendStartGame("User", null)
            advanceUntilIdle()
            coVerify(exactly = 0) {
                repository.sendStartGame("User", any())
            }
        }

    @Test
    fun getRoleForUser_blueOperative_returnsCorrectRole() =
        runTest {
            val response =
                LobbyResponse(
                    lobbyCode = "12345",
                    playerList =
                        listOf(
                            PlayerDto(
                                username = "Alice",
                                role = Role.OPERATIVE,
                                team = Team.BLUE,
                                isHost = false,
                            ),
                        ),
                    isStarted = false,
                )

            coEvery {
                repository.joinLobby("Alice", "12345")
            } returns response

            coEvery { sessionRepository.clearUserId() } just Runs

            val viewModel = LobbyViewModel(repository, sessionRepository)

            viewModel.joinLobby("Alice", "12345")

            advanceUntilIdle()

            val result = viewModel.getRoleForUser("Alice")

            assertEquals(PlayerRoles.BLUE_OPERATIVE, result)
        }

    @Test
    fun getRoleForUser_redSpymaster_returnsCorrectRole() =
        runTest {
            val response =
                LobbyResponse(
                    lobbyCode = "12345",
                    playerList =
                        listOf(
                            PlayerDto(
                                username = "Bob",
                                role = Role.SPYMASTER,
                                team = Team.RED,
                                isHost = false,
                            ),
                        ),
                    false,
                )

            coEvery {
                repository.joinLobby("Bob", "12345")
            } returns response

            coEvery { sessionRepository.clearUserId() } just Runs

            val viewModel = LobbyViewModel(repository, sessionRepository)

            viewModel.joinLobby("Bob", "12345")

            advanceUntilIdle()

            val result = viewModel.getRoleForUser("Bob")

            assertEquals(PlayerRoles.RED_SPYMASTER, result)
        }

    @Test
    fun getRoleForUser_nullRole_returnsNone() =
        runTest {
            val response =
                LobbyResponse(
                    lobbyCode = "12345",
                    playerList =
                        listOf(
                            PlayerDto(
                                username = "Bob",
                                role = null,
                                team = Team.RED,
                                isHost = false,
                            ),
                        ),
                    isStarted = false,
                )

            coEvery {
                repository.joinLobby("Bob", "12345")
            } returns response

            coEvery {
                sessionRepository.clearUserId()
            } just Runs

            val viewModel = LobbyViewModel(repository, sessionRepository)

            viewModel.joinLobby("Bob", "12345")

            advanceUntilIdle()

            val result = viewModel.getRoleForUser("Bob")

            assertEquals(PlayerRoles.NONE, result)
        }

    @Test
    fun getIsHost_returnsTrueForHost() =
        runTest {
            val response =
                LobbyResponse(
                    lobbyCode = "12345",
                    playerList =
                        listOf(
                            PlayerDto(
                                username = "Host",
                                role = null,
                                team = null,
                                isHost = true,
                            ),
                        ),
                    isStarted = false,
                )

            coEvery {
                repository.joinLobby("Host", "12345")
            } returns response

            coEvery { sessionRepository.clearUserId() } just Runs

            val viewModel = LobbyViewModel(repository, sessionRepository)

            viewModel.joinLobby("Host", "12345")

            advanceUntilIdle()

            assertTrue(viewModel.getIsHost("Host"))
        }

    @Test
    fun getIsHost_returnsFalseForNonHost() =
        runTest {
            val response =
                LobbyResponse(
                    lobbyCode = "12345",
                    playerList =
                        listOf(
                            PlayerDto(
                                username = "User",
                                isHost = false,
                            ),
                        ),
                    isStarted = false,
                )

            coEvery {
                repository.joinLobby("User", "12345")
            } returns response

            coEvery {
                sessionRepository.clearUserId()
            } just Runs

            val viewModel = LobbyViewModel(repository, sessionRepository)

            viewModel.joinLobby("User", "12345")

            advanceUntilIdle()

            assertFalse(viewModel.getIsHost("User"))
        }

    @Test
    fun sendStartGame_callsRepository() =
        runTest {
            mockkStatic(Log::class)
            every { Log.d(any(), any()) } returns 0

            val joinResponse =
                LobbyResponse(
                    lobbyCode = "12345",
                    playerList =
                        listOf(
                            PlayerDto(
                                username = "Host",
                                isHost = true,
                            ),
                        ),
                    isStarted = false,
                )

            val startResponse =
                LobbyResponse(
                    lobbyCode = "12345",
                    playerList = joinResponse.playerList,
                    isStarted = true,
                )

            coEvery {
                repository.joinLobby("Host", "12345")
            } returns joinResponse

            coEvery {
                repository.sendStartGame("12345", any())
            } returns startResponse

            coEvery { sessionRepository.clearUserId() } just Runs

            val viewModel = LobbyViewModel(repository, sessionRepository)

            viewModel.joinLobby("Host", "12345")

            advanceUntilIdle()

            viewModel.sendStartGame("Host", UUID.randomUUID())

            advanceUntilIdle()

            coVerify {
                repository.sendStartGame("12345", any())
            }
        }

    @Test
    fun sendStartGame_exception_setsError() =
        runTest {
            mockkStatic(Log::class)
            every { Log.d(any(), any()) } returns 0

            val joinResponse =
                LobbyResponse(
                    lobbyCode = "12345",
                    playerList =
                        listOf(
                            PlayerDto(
                                username = "Host",
                                isHost = true,
                            ),
                        ),
                    isStarted = false,
                )

            coEvery {
                repository.joinLobby("Host", "12345")
            } returns joinResponse

            coEvery {
                repository.sendStartGame(any(), any())
            } throws RuntimeException("Start failed")

            coEvery { sessionRepository.clearUserId() } just Runs

            val viewModel = LobbyViewModel(repository, sessionRepository)

            viewModel.joinLobby("Host", "12345")

            advanceUntilIdle()

            viewModel.sendStartGame("Host", UUID.randomUUID())

            advanceUntilIdle()

            assertEquals(
                "Start failed",
                viewModel.state.value.error,
            )
        }

    @Test
    fun getAvailableChatTabsForUser_unknownUser_returnsGlobalOnly() {
        val viewModel = LobbyViewModel(repository, sessionRepository)

        val result = viewModel.getAvailableChatTabsForUser("Unknown")

        assertEquals(listOf(ChatTab.GLOBAL), result)
    }

    @Test
    fun getAvailableChatTabsForUser_playerWithoutTeam_returnsGlobalOnly() =
        runTest {
            val response =
                LobbyResponse(
                    lobbyCode = "12345",
                    playerList = listOf(PlayerDto(username = "Alice", role = null, team = null, isHost = false)),
                    isStarted = false,
                )

            coEvery { repository.joinLobby("Alice", "12345") } returns response
            coEvery { repository.getLobbyInfo("12345") } returns response
            coEvery { sessionRepository.clearUserId() } just Runs

            val viewModel = LobbyViewModel(repository, sessionRepository)

            viewModel.joinLobby("Alice", "12345")
            advanceTimeBy(1.milliseconds)
            viewModel.stopPollingForTest()

            assertEquals(listOf(ChatTab.GLOBAL), viewModel.getAvailableChatTabsForUser("Alice"))
        }

    @Test
    fun getAvailableChatTabsForUser_spymasterWithTeam_returnsGlobalAndTeam() =
        runTest {
            val response =
                LobbyResponse(
                    lobbyCode = "12345",
                    playerList = listOf(PlayerDto("Alice", Role.SPYMASTER, Team.BLUE, false)),
                    isStarted = false,
                )

            coEvery { repository.joinLobby("Alice", "12345") } returns response
            coEvery { repository.getLobbyInfo("12345") } returns response
            coEvery { sessionRepository.clearUserId() } just Runs

            val viewModel = LobbyViewModel(repository, sessionRepository)

            viewModel.joinLobby("Alice", "12345")
            advanceTimeBy(1.milliseconds)
            viewModel.stopPollingForTest()

            assertEquals(listOf(ChatTab.GLOBAL, ChatTab.TEAM), viewModel.getAvailableChatTabsForUser("Alice"))
        }

    @Test
    fun getAvailableChatTabsForUser_singleOperative_returnsGlobalAndTeam() =
        runTest {
            val response =
                LobbyResponse(
                    lobbyCode = "12345",
                    playerList =
                        listOf(
                            PlayerDto("Alice", Role.OPERATIVE, Team.BLUE, false),
                            PlayerDto("Bob", Role.SPYMASTER, Team.BLUE, false),
                        ),
                    isStarted = false,
                )

            coEvery { repository.joinLobby("Alice", "12345") } returns response
            coEvery { repository.getLobbyInfo("12345") } returns response
            coEvery { sessionRepository.clearUserId() } just Runs

            val viewModel = LobbyViewModel(repository, sessionRepository)

            viewModel.joinLobby("Alice", "12345")
            advanceTimeBy(1.milliseconds)
            viewModel.stopPollingForTest()

            assertEquals(
                listOf(ChatTab.GLOBAL, ChatTab.TEAM, ChatTab.OPERATIVES),
                viewModel.getAvailableChatTabsForUser("Alice"),
            )
        }

    @Test
    fun getAvailableChatTabsForUser_multipleSameTeamOperatives_returnsAllTabs() =
        runTest {
            val response =
                LobbyResponse(
                    lobbyCode = "12345",
                    playerList =
                        listOf(
                            PlayerDto("Alice", Role.OPERATIVE, Team.BLUE, false),
                            PlayerDto("Bob", Role.OPERATIVE, Team.BLUE, false),
                        ),
                    isStarted = false,
                )

            coEvery { repository.joinLobby("Alice", "12345") } returns response
            coEvery { repository.getLobbyInfo("12345") } returns response
            coEvery { sessionRepository.clearUserId() } just Runs

            val viewModel = LobbyViewModel(repository, sessionRepository)

            viewModel.joinLobby("Alice", "12345")
            advanceTimeBy(1.milliseconds)
            viewModel.stopPollingForTest()

            assertEquals(
                listOf(ChatTab.GLOBAL, ChatTab.TEAM, ChatTab.OPERATIVES),
                viewModel.getAvailableChatTabsForUser("Alice"),
            )
        }

    @Test
    fun clearError_resetsError() =
        runTest {
            coEvery { repository.createLobby(any()) } throws RuntimeException("Network error")
            coEvery { sessionRepository.clearUserId() } just Runs

            val viewModel = LobbyViewModel(repository, sessionRepository)

            viewModel.createLobby("Max")

            advanceUntilIdle()

            assertEquals("Network error", viewModel.state.value.error)

            viewModel.clearError()

            assertNull(viewModel.state.value.error)
        }
}
