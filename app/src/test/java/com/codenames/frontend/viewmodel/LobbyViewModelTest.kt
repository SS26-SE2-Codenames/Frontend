package com.codenames.frontend.viewmodel

import android.util.Log
import com.codenames.frontend.data.model.enums.Role
import com.codenames.frontend.data.model.enums.Team
import com.codenames.frontend.data.repository.LobbyRepository
import com.codenames.frontend.network.dto.LobbyResponse
import com.codenames.frontend.network.dto.PlayerDto
import com.codenames.frontend.ui.roles.PlayerRoles
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
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

@OptIn(ExperimentalCoroutinesApi::class)
class LobbyViewModelTest {
    private val dispatcher = StandardTestDispatcher()

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
            val repository = mockk<LobbyRepository>()

            val response =
                LobbyResponse(
                    lobbyCode = "1234",
                    playerList = listOf(PlayerDto("User", null, Team.RED, true)),
                    isStarted = false,
                )

            coEvery { repository.createLobby("User") } returns response
            coEvery { repository.getLobbyInfo(any()) } returns response

            val viewModel = LobbyViewModel(repository)

            viewModel.createLobby("User")

            advanceTimeBy(2000)

            viewModel.stopPollingForTest()

            val state = viewModel.state.value

            assertEquals("1234", state.lobbyCode)
            assertFalse(state.isLoading)
            assertNull(state.error)
        }

    @Test
    fun testCreateLobby_error() =
        runTest {
            val repository = mockk<LobbyRepository>()

            coEvery { repository.createLobby(any()) } throws RuntimeException("Network error")

            val viewModel = LobbyViewModel(repository)

            viewModel.createLobby("Max")

            advanceTimeBy(2000)

            viewModel.stopPollingForTest()

            val state = viewModel.state.value

            assertEquals("Network error", state.error)
            assertFalse(state.isLoading)
        }

    @Test
    fun testJoinLobby_success() =
        runTest {
            val repository = mockk<LobbyRepository>()

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

            val viewModel = LobbyViewModel(repository)

            viewModel.joinLobby("User", "1234")

            advanceTimeBy(2000)

            viewModel.stopPollingForTest()

            val state = viewModel.state.value

            assertEquals("1234", state.lobbyCode)
            assertFalse(state.isLoading)
            assertNull(state.error)
        }

    @Test
    fun testJoinLobby_error() =
        runTest {
            val repository = mockk<LobbyRepository>()

            coEvery { repository.joinLobby(any(), any()) } throws RuntimeException("Network error")

            val viewModel = LobbyViewModel(repository)

            viewModel.joinLobby("Max", "1234")

            advanceTimeBy(2000)

            viewModel.stopPollingForTest()

            val state = viewModel.state.value

            assertEquals("Network error", state.error)
            assertFalse(state.isLoading)
        }

    @Test
    fun testLeaveLobby_success() =
        runTest {
            val repository = mockk<LobbyRepository>()

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

            coEvery { repository.joinLobby("User", "1234") } returns response
            coEvery { repository.getLobbyInfo(any()) } returns response
            coEvery { repository.leaveLobby("1234", "User") } returns response2

            val viewModel = LobbyViewModel(repository)

            viewModel.joinLobby("User", "1234")
            advanceTimeBy(2000)
            viewModel.leaveLobby("User", onResult = {})

            advanceTimeBy(2000)

            viewModel.stopPollingForTest()

            val state = viewModel.state.value

            assertEquals(null, state.lobbyCode)
            assertFalse(state.isLoading)
            assertNull(state.error)
        }

    @Test
    fun testLeaveLobby_error() =
        runTest {
            val repository = mockk<LobbyRepository>()

            val response =
                LobbyResponse(
                    lobbyCode = "1234",
                    playerList = listOf(PlayerDto("User", null, Team.RED, true)),
                    false,
                )

            coEvery { repository.joinLobby("User", "1234") } returns response
            coEvery { repository.leaveLobby(any(), any()) } throws RuntimeException("Network error")

            val viewModel = LobbyViewModel(repository)

            viewModel.joinLobby("User", "1234")

            advanceTimeBy(2000)

            viewModel.leaveLobby("User", onResult = {})

            advanceTimeBy(2000)

            viewModel.stopPollingForTest()

            val state = viewModel.state.value

            assertEquals("Network error", state.error)
            assertFalse(state.isLoading)
        }

    @Test
    fun testChangeRole_success() =
        runTest {
            val repository = mockk<LobbyRepository>()

            val newRole = Role.OPERATIVE
            val newTeam = Team.RED

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

            coEvery { repository.joinLobby("User", "1234") } returns response
            coEvery { repository.getLobbyInfo(any()) } returns response
            coEvery { repository.changeRole("User", "1234", newRole, newTeam) } returns response2

            val viewModel = LobbyViewModel(repository)

            viewModel.joinLobby("User", "1234")

            advanceTimeBy(2000)

            viewModel.changeRole(newRole, newTeam, "User")
            viewModel.stopPollingForTest()

            advanceTimeBy(2000)

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
            val repository = mockk<LobbyRepository>()

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
                )
            } throws RuntimeException("Network error")

            val viewModel = LobbyViewModel(repository)

            viewModel.joinLobby("User", "1234")

            advanceTimeBy(2000)

            viewModel.changeRole(Role.OPERATIVE, Team.RED, "User")

            advanceTimeBy(2000)

            viewModel.stopPollingForTest()

            val state = viewModel.state.value

            assertEquals("Network error", state.error)
            assertFalse(state.isLoading)
        }

    @Test
    fun testSetError_WithEmptyValue() =
        runTest {
            val repository = mockk<LobbyRepository>()

            coEvery { repository.createLobby(any()) } throws RuntimeException("")

            val viewModel = LobbyViewModel(repository)

            viewModel.createLobby("Max")

            advanceTimeBy(2000)

            viewModel.stopPollingForTest()

            val state = viewModel.state.value

            assertEquals("Unknown error", state.error)
            assertFalse(state.isLoading)
        }

    @Test
    fun testSetError_WithNullValue() =
        runTest {
            val repository = mockk<LobbyRepository>()

            coEvery { repository.createLobby(any()) } throws RuntimeException()

            val viewModel = LobbyViewModel(repository)

            viewModel.createLobby("Max")

            advanceTimeBy(2000)

            viewModel.stopPollingForTest()

            val state = viewModel.state.value

            assertEquals("Unknown error", state.error)
            assertFalse(state.isLoading)
        }

    // Polling Tests
    @Test
    fun testPolling_callsRepositoryRepeatedly() =
        runTest {
            val repository = mockk<LobbyRepository>()

            coEvery { repository.getLobbyInfo(any()) } returns
                LobbyResponse(
                    lobbyCode = "1234",
                    playerList = listOf(PlayerDto("User", null, Team.RED, true)),
                    false,
                )

            val viewModel = LobbyViewModel(repository)

            viewModel.startPollingForTest("1234")

            advanceTimeBy(2000)
            advanceTimeBy(2000)

            viewModel.stopPollingForTest()

            coVerify(atLeast = 2) {
                repository.getLobbyInfo("1234")
            }
        }

    @Test
    fun testPolling_stateIsUpdated() =
        runTest {
            val repository = mockk<LobbyRepository>()

            coEvery { repository.getLobbyInfo(any()) } returnsMany
                listOf(
                    LobbyResponse("1", emptyList(), false),
                    LobbyResponse("2", emptyList(), false),
                )

            val viewModel = LobbyViewModel(repository)

            viewModel.startPollingForTest("1234")

            advanceTimeBy(2000)
            advanceTimeBy(2000)

            viewModel.stopPollingForTest()

            assertEquals("2", viewModel.state.value.lobbyCode)
        }

    @Test
    fun testPolling_doesNotStartTwice() =
        runTest {
            val repository = mockk<LobbyRepository>()

            coEvery { repository.getLobbyInfo("1234") } returns
                LobbyResponse(
                    lobbyCode = "1234",
                    playerList = listOf(PlayerDto("User", null, Team.RED, true)),
                    false,
                )

            val viewModel = LobbyViewModel(repository)

            viewModel.startPollingForTest("1234")

            advanceTimeBy(2000)
            advanceTimeBy(2000)

            viewModel.startPollingForTest("2345")

            advanceTimeBy(2000)

            viewModel.stopPollingForTest()

            coVerify(atLeast = 2) {
                repository.getLobbyInfo("1234")
            }
        }

    // Tests with wrong lobby state
    @Test
    fun testCreateLobby_alreadyInLobby() =
        runTest {
            val repository = mockk<LobbyRepository>()

            val response =
                LobbyResponse(
                    lobbyCode = "1234",
                    playerList = listOf(PlayerDto("User", null, Team.RED, true)),
                    false,
                )

            coEvery { repository.createLobby("User") } returns response

            val viewModel = LobbyViewModel(repository)

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
            val repository = mockk<LobbyRepository>()

            val response =
                LobbyResponse(
                    lobbyCode = "1234",
                    playerList = listOf(PlayerDto("User", null, Team.RED, true)),
                    false,
                )

            coEvery { repository.createLobby("User") } returns response

            val viewModel = LobbyViewModel(repository)

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
            val repository = mockk<LobbyRepository>()

            val viewModel = LobbyViewModel(repository)

            viewModel.leaveLobby("User", onResult = {})

            advanceUntilIdle()

            val state = viewModel.state.value

            assertEquals(null, state.lobbyCode)
            assertFalse(state.isLoading)
            assertEquals("Not in a lobby, leaving not possible", state.error)
        }

    @Test
    fun testChangeRoles_notInLobby() =
        runTest {
            val repository = mockk<LobbyRepository>()

            val viewModel = LobbyViewModel(repository)

            viewModel.changeRole(Role.OPERATIVE, Team.RED, "User")

            advanceUntilIdle()

            val state = viewModel.state.value

            assertEquals(null, state.lobbyCode)
            assertFalse(state.isLoading)
            assertEquals("Not in a Lobby", state.error)
        }

    @Test
    fun changeRole_DelegatesCorrectly1() {
        val repository = mockk<LobbyRepository>()
        val viewModel = spyk<LobbyViewModel>(LobbyViewModel(repository), recordPrivateCalls = true)
        viewModel.changeRole(PlayerRoles.BLUE_SPYMASTER, "Alice")

        verify {
            viewModel.changeRole(Role.SPYMASTER, Team.BLUE, "Alice")
        }
    }

    @Test
    fun changeRole_DelegatesCorrectly2() {
        val repository = mockk<LobbyRepository>()
        val viewModel = spyk<LobbyViewModel>(LobbyViewModel(repository), recordPrivateCalls = true)

        viewModel.changeRole(PlayerRoles.RED_OPERATIVE, "Bob")

        verify {
            viewModel.changeRole(Role.OPERATIVE, Team.RED, "Bob")
        }
    }

    @Test
    fun `getRoleForUser returns BLUE_OPERATIVE`() =
        runTest {
            val repository = mockk<LobbyRepository>()
            val viewModel = LobbyViewModel(repository)

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

            viewModel.joinLobby("Max", "ABCD")

            advanceUntilIdle()

            val result = viewModel.getRoleForUser("Max")

            assertEquals(PlayerRoles.BLUE_OPERATIVE, result)
        }

    @Test
    fun `getRoleForUser returns NONE when player does not exist`() =
        runTest {
            val repository = mockk<LobbyRepository>()
            val viewModel = LobbyViewModel(repository)

            val result = viewModel.getRoleForUser("Unknown")

            assertEquals(PlayerRoles.NONE, result)
        }

    @Test
    fun `changeRole updates player role correctly`() =
        runTest {
            val repository = mockk<LobbyRepository>()
            val viewModel = LobbyViewModel(repository)

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

            coEvery {
                repository.joinLobby("Max", "ABCD")
            } returns joinResponse

            coEvery {
                repository.changeRole(
                    "Max",
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
            )

            advanceUntilIdle()

            val result = viewModel.getRoleForUser("Max")

            assertEquals(PlayerRoles.RED_SPYMASTER, result)

            coVerify(exactly = 1) {
                repository.changeRole(
                    "Max",
                    "ABCD",
                    Role.SPYMASTER,
                    Team.RED,
                )
            }
        }

    @Test
    fun `changeRole sets error when not in lobby`() =
        runTest {
            val repository = mockk<LobbyRepository>()
            val viewModel = LobbyViewModel(repository)

            viewModel.changeRole(
                role = Role.SPYMASTER,
                team = Team.RED,
                username = "Max",
            )

            advanceUntilIdle()

            assertTrue(
                viewModel.state.value.error
                    ?.contains("Not in a Lobby") == true,
            )
        }

    @Test
    fun changeRole_DelegatesCorrectly_redSpymaster() {
        val repository = mockk<LobbyRepository>()
        val viewModel = spyk<LobbyViewModel>(LobbyViewModel(repository))

        viewModel.changeRole(PlayerRoles.RED_SPYMASTER, "Alice")

        verify {
            viewModel.changeRole(Role.SPYMASTER, Team.RED, "Alice")
        }
    }

    @Test
    fun changeRole_DelegatesCorrectly_blueOperative() {
        val repository = mockk<LobbyRepository>()
        val viewModel = spyk<LobbyViewModel>(LobbyViewModel(repository))

        viewModel.changeRole(PlayerRoles.BLUE_OPERATIVE, "Bob")

        verify {
            viewModel.changeRole(Role.OPERATIVE, Team.BLUE, "Bob")
        }
    }

    @Test
    fun changeRole_invalidRole_setsError() =
        runTest {
            val repository = mockk<LobbyRepository>()

            val viewModel = LobbyViewModel(repository)

            viewModel.changeRole(PlayerRoles.NONE, "User")

            advanceUntilIdle()

            assertEquals(
                "Invalid role",
                viewModel.state.value.error,
            )
        }

    @Test
    fun getRoleForUser_userNotFound_returnsNone() {
        val repository = mockk<LobbyRepository>()
        val viewModel = LobbyViewModel(repository)

        val result = viewModel.getRoleForUser("Unknown")

        assertEquals(PlayerRoles.NONE, result)
    }

    @Test
    fun sendStartGame_blankUsername_doesNothing() =
        runTest {
            val repository = mockk<LobbyRepository>(relaxed = true)

            val viewModel = LobbyViewModel(repository)

            viewModel.sendStartGame("")

            advanceUntilIdle()

            coVerify(exactly = 0) {
                repository.sendStartGame(any(), any())
            }
        }

    @Test
    fun getRoleForUser_blueOperative_returnsCorrectRole() =
        runTest {
            val repository = mockk<LobbyRepository>()

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

            val viewModel = LobbyViewModel(repository)

            viewModel.joinLobby("Alice", "12345")

            advanceUntilIdle()

            val result = viewModel.getRoleForUser("Alice")

            assertEquals(PlayerRoles.BLUE_OPERATIVE, result)
        }

    @Test
    fun getRoleForUser_redSpymaster_returnsCorrectRole() =
        runTest {
            val repository = mockk<LobbyRepository>()

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

            val viewModel = LobbyViewModel(repository)

            viewModel.joinLobby("Bob", "12345")

            advanceUntilIdle()

            val result = viewModel.getRoleForUser("Bob")

            assertEquals(PlayerRoles.RED_SPYMASTER, result)
        }

    @Test
    fun getRoleForUser_nullRole_returnsNone() =
        runTest {
            val repository = mockk<LobbyRepository>()

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

            val viewModel = LobbyViewModel(repository)

            viewModel.joinLobby("Bob", "12345")

            advanceUntilIdle()

            val result = viewModel.getRoleForUser("Bob")

            assertEquals(PlayerRoles.NONE, result)
        }

    @Test
    fun getIsHost_returnsTrueForHost() =
        runTest {
            val repository = mockk<LobbyRepository>()

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

            val viewModel = LobbyViewModel(repository)

            viewModel.joinLobby("Host", "12345")

            advanceUntilIdle()

            assertTrue(viewModel.getIsHost("Host"))
        }

    @Test
    fun getIsHost_returnsFalseForNonHost() =
        runTest {
            val repository = mockk<LobbyRepository>()

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

            val viewModel = LobbyViewModel(repository)

            viewModel.joinLobby("User", "12345")

            advanceUntilIdle()

            assertFalse(viewModel.getIsHost("User"))
        }

    @Test
    fun sendStartGame_callsRepository() =
        runTest {
            mockkStatic(Log::class)
            every { Log.d(any(), any()) } returns 0

            val repository = mockk<LobbyRepository>()

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
                repository.sendStartGame("12345", "Host")
            } returns startResponse

            val viewModel = LobbyViewModel(repository)

            viewModel.joinLobby("Host", "12345")

            advanceUntilIdle()

            viewModel.sendStartGame("Host")

            advanceUntilIdle()

            coVerify {
                repository.sendStartGame("12345", "Host")
            }
        }

    @Test
    fun sendStartGame_exception_setsError() =
        runTest {
            mockkStatic(Log::class)
            every { Log.d(any(), any()) } returns 0

            val repository = mockk<LobbyRepository>()

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

            val viewModel = LobbyViewModel(repository)

            viewModel.joinLobby("Host", "12345")

            advanceUntilIdle()

            viewModel.sendStartGame("Host")

            advanceUntilIdle()

            assertEquals(
                "Start failed",
                viewModel.state.value.error,
            )
        }
}
