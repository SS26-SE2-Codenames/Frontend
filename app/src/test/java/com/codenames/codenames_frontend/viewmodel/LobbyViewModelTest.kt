package com.codenames.codenames_frontend.viewmodel

import com.codenames.codenames_frontend.data.model.enums.Role
import com.codenames.codenames_frontend.data.model.enums.Team
import com.codenames.codenames_frontend.data.repository.LobbyRepository
import com.codenames.codenames_frontend.network.dto.LobbyResponse
import com.codenames.codenames_frontend.network.dto.PlayerDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
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
    fun testCreateLobby_success() = runTest {
        val repository = mockk<LobbyRepository>()

        val response = LobbyResponse(
            lobbyCode = "1234",
            playerList = listOf(PlayerDto("User", null, Team.RED, true))
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
    fun testCreateLobby_error() = runTest {
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
    fun testJoinLobby_success() = runTest {
        val repository = mockk<LobbyRepository>()

        val response = LobbyResponse(
            lobbyCode = "1234",
            playerList = listOf(PlayerDto("User", null, Team.RED, true))
        )

        coEvery { repository.joinLobby("User", "1234") } returns response
        coEvery { repository.getLobbyInfo(any()) } returns LobbyResponse(
            lobbyCode = "1234",
            playerList = listOf(PlayerDto("User", null, Team.RED, true))
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
    fun testJoinLobby_error() = runTest {
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
    fun testLeaveLobby_success() = runTest {
        val repository = mockk<LobbyRepository>()

        val response = LobbyResponse(
            lobbyCode = "1234",
            playerList = listOf(PlayerDto("User", null, Team.RED, true))
        )

        val response2 = LobbyResponse(
            lobbyCode = "",
            playerList = emptyList()
        )

        coEvery { repository.joinLobby("User", "1234") } returns response
        coEvery { repository.getLobbyInfo(any()) } returns response
        coEvery { repository.leaveLobby("User", "1234") } returns response2

        val viewModel = LobbyViewModel(repository)

        viewModel.joinLobby("User", "1234")
        advanceTimeBy(2000)
        viewModel.leaveLobby("User")

        advanceTimeBy(2000)

        viewModel.stopPollingForTest()

        val state = viewModel.state.value

        assertEquals("", state.lobbyCode)
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun testLeaveLobby_error() = runTest {
        val repository = mockk<LobbyRepository>()

        val response = LobbyResponse(
            lobbyCode = "1234",
            playerList = listOf(PlayerDto("User", null, Team.RED, true))
        )

        coEvery { repository.joinLobby("User", "1234") } returns response
        coEvery { repository.leaveLobby(any(), any()) } throws RuntimeException("Network error")

        val viewModel = LobbyViewModel(repository)

        viewModel.joinLobby("User", "1234")

        advanceTimeBy(2000)

        viewModel.leaveLobby("User")

        advanceTimeBy(2000)

        viewModel.stopPollingForTest()

        val state = viewModel.state.value

        assertEquals("Network error", state.error)
        assertFalse(state.isLoading)
    }

    @Test
    fun testChangeRole_success() = runTest {
        val repository = mockk<LobbyRepository>()

        val newRole = Role.OPERATIVE
        val newTeam = Team.RED

        val response = LobbyResponse(
            lobbyCode = "1234",
            playerList = listOf(PlayerDto("User", null, Team.RED, true))
        )

        val response2 = LobbyResponse(
            lobbyCode = "1234",
            playerList = listOf(PlayerDto("User", newRole, newTeam, true))
        )

        coEvery { repository.joinLobby("User", "1234") } returns response
        coEvery { repository.getLobbyInfo(any()) } returns response
        coEvery { repository.changeRole("User", "1234", newRole, newTeam) } returns response2

        val viewModel = LobbyViewModel(repository)

        viewModel.joinLobby("User", "1234")

        advanceTimeBy(2000)

        viewModel.changeRole("User", newRole, newTeam)
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
    fun testChangeRole_error() = runTest {
        val repository = mockk<LobbyRepository>()

        val response = LobbyResponse(
            lobbyCode = "1234",
            playerList = listOf(PlayerDto("User", null, Team.RED, true))
        )

        coEvery { repository.joinLobby("User", "1234") } returns response
        coEvery {
            repository.changeRole(
                any(),
                any(),
                any(),
                any()
            )
        } throws RuntimeException("Network error")

        val viewModel = LobbyViewModel(repository)

        viewModel.joinLobby("User", "1234")

        advanceTimeBy(2000)

        viewModel.changeRole("User", Role.OPERATIVE, Team.RED)

        advanceTimeBy(2000)

        viewModel.stopPollingForTest()

        val state = viewModel.state.value

        assertEquals("Network error", state.error)
        assertFalse(state.isLoading)
    }

    @Test
    fun testSetError_WithEmptyValue() = runTest {
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
    fun testSetError_WithNullValue() = runTest {
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

    //Polling Tests
    @Test
    fun testPolling_callsRepositoryRepeatedly() = runTest {
        val repository = mockk<LobbyRepository>()

        coEvery { repository.getLobbyInfo(any()) } returns LobbyResponse(
            lobbyCode = "1234",
            playerList = listOf(PlayerDto("User", null, Team.RED, true))
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
    fun testPolling_stateIsUpdated() = runTest {
        val repository = mockk<LobbyRepository>()

        coEvery { repository.getLobbyInfo(any()) } returnsMany listOf(
            LobbyResponse("1", emptyList()),
            LobbyResponse("2", emptyList())
        )

        val viewModel = LobbyViewModel(repository)

        viewModel.startPollingForTest("1234")

        advanceTimeBy(2000)
        advanceTimeBy(2000)

        viewModel.stopPollingForTest()

        assertEquals("2", viewModel.state.value.lobbyCode)
    }

    @Test
    fun testPolling_doesNotStartTwice() = runTest {
        val repository = mockk<LobbyRepository>()

        coEvery { repository.getLobbyInfo("1234") } returns LobbyResponse(
            lobbyCode = "1234",
            playerList = listOf(PlayerDto("User", null, Team.RED, true))
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

    //Tests with wrong lobby state
    @Test
    fun testCreateLobby_alreadyInLobby() = runTest {
        val repository = mockk<LobbyRepository>()

        val response = LobbyResponse(
            lobbyCode = "1234",
            playerList = listOf(PlayerDto("User", null, Team.RED, true))
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
    fun testJoinLobby_alreadyInLobby() = runTest {
        val repository = mockk<LobbyRepository>()

        val response = LobbyResponse(
            lobbyCode = "1234",
            playerList = listOf(PlayerDto("User", null, Team.RED, true))
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
    fun testLeaveLobby_notInLobby() = runTest {
        val repository = mockk<LobbyRepository>()

        val viewModel = LobbyViewModel(repository)

        viewModel.leaveLobby("User")

        advanceUntilIdle()

        val state = viewModel.state.value

        assertEquals(null, state.lobbyCode)
        assertFalse(state.isLoading)
        assertEquals("Not in a lobby, leaving not possible", state.error)
    }

    @Test
    fun testChangeRoles_notInLobby() = runTest {
        val repository = mockk<LobbyRepository>()

        val viewModel = LobbyViewModel(repository)

        viewModel.changeRole("User", Role.OPERATIVE, Team.RED)

        advanceUntilIdle()

        val state = viewModel.state.value

        assertEquals(null, state.lobbyCode)
        assertFalse(state.isLoading)
        assertEquals("Not in a Lobby", state.error)
    }

}