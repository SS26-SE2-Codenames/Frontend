package com.codenames.codenames_frontend.data.repository

import com.codenames.codenames_frontend.data.model.enums.Role
import com.codenames.codenames_frontend.data.model.enums.Team
import com.codenames.codenames_frontend.network.api.LobbyApi
import com.codenames.codenames_frontend.network.dto.LobbyResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlin.test.assertFailsWith

@OptIn(ExperimentalCoroutinesApi::class)
class LobbyRepositoryTest {
    private val api: LobbyApi = mockk()
    private lateinit var repository: LobbyRepository

    @Before
    fun setup() {
        repository = LobbyRepository(api)
    }

    @Test
    fun createLobby_shouldCallApiAndReturnResponse() = runTest {
        val username = "Max"

        val response = LobbyResponse(
            lobbyCode = "1234",
            playerList = emptyList()
        )

        coEvery { api.createLobby(username) } returns response

        val result = repository.createLobby(username)

        coVerify { api.createLobby(username) }
        assertEquals(response, result)
    }

    @Test
    fun joinLobby_shouldCallApiWithCorrectParams() = runTest {
        val username = "Max"
        val lobbyCode = "1234"

        val response = LobbyResponse(lobbyCode, emptyList())

        coEvery { api.joinLobby(username, lobbyCode) } returns response

        val result = repository.joinLobby(username, lobbyCode)

        coVerify { api.joinLobby(username, lobbyCode) }
        assertEquals(response, result)
    }

    @Test
    fun leaveLobby_shouldCallApiWithCorrectParams() = runTest {
        val username = "Max"
        val lobbyCode = "1234"

        val response = LobbyResponse(lobbyCode, emptyList())

        coEvery { api.leaveLobby(username, lobbyCode) } returns response

        val result = repository.leaveLobby(username, lobbyCode)

        coVerify { api.leaveLobby(username, lobbyCode) }
        assertEquals(response, result)
    }

    @Test
    fun getLobbyInfo_shouldCallApiWithCorrectParam() = runTest {
        val lobbyCode = "1234"

        val response = LobbyResponse(lobbyCode, emptyList())

        coEvery { api.getLobbyInfo(lobbyCode) } returns response

        val result = repository.getLobbyInfo(lobbyCode)
        assertEquals(response, result)
    }

    @Test
    fun changeRole_shouldCreateCorrectPlayerDto() = runTest {
        val username = "Max"
        val lobbyCode = "1234"
        val role = Role.OPERATIVE
        val team = Team.RED

        val response = LobbyResponse(lobbyCode, emptyList())

        coEvery { api.changeRole(eq(lobbyCode), any()) } returns response

        repository.changeRole(username, lobbyCode, role, team)

        coVerify {
            api.changeRole(
                lobbyCode,
                match {
                    it.username == username &&
                            it.role == role &&
                            it.team == team
                }
            )
        }
    }

    @Test
    fun createLobby_shouldThrowException_whenApiFails() = runTest {
        val username = "Max"

        coEvery { api.createLobby(username) } throws RuntimeException("Network error")

        assertFailsWith<RuntimeException> {
            repository.createLobby(username)
        }
    }

}