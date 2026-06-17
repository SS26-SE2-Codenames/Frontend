package com.codenames.frontend.viewmodel

import com.codenames.frontend.data.model.RejoinState
import com.codenames.frontend.data.model.SessionState
import com.codenames.frontend.data.model.UserState
import com.codenames.frontend.data.model.enums.Role
import com.codenames.frontend.data.model.enums.Team
import com.codenames.frontend.data.repository.SessionRepository
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class SessionViewModelTest {
    private lateinit var viewModel: SessionViewModel
    private lateinit var sessionRepository: SessionRepository

    private val dispatcher = StandardTestDispatcher()

    private lateinit var userFlow: MutableStateFlow<UserState>
    private lateinit var sessionFlow: MutableStateFlow<SessionState>

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)

        userFlow = MutableStateFlow(UserState("", null))
        sessionFlow =
            MutableStateFlow(
                SessionState(
                    lobbyCode = null,
                    lobbyRole = null,
                    lobbyTeam = null,
                    consumed = false,
                ),
            )

        sessionRepository = mockk(relaxed = true)

        every { sessionRepository.userFlow } returns userFlow
        every { sessionRepository.sessionFlow } returns sessionFlow

        viewModel = SessionViewModel(sessionRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial username is empty`() {
        assertEquals("", viewModel.userState.value.username)
    }

    @Test
    fun `setUsername updates username state`() {
        viewModel.setUsername("Max")

        assertEquals("Max", viewModel.userState.value.username)
    }

    @Test
    fun `persistUserState saves username`() =
        runTest {
            val uuid = UUID.randomUUID()

            viewModel.persistUserId(uuid)

            viewModel.setUsername("Max")

            viewModel.persistUserState()

            advanceUntilIdle()

            coVerify {
                sessionRepository.saveUser("Max", any())
            }
        }

    @Test
    fun `persistUserState does nothing when userId is null`() =
        runTest {
            viewModel.setUsername("Max")

            viewModel.persistUserState()

            advanceUntilIdle()

            coVerify(exactly = 0) {
                sessionRepository.saveUser(any(), any())
            }
        }

    @Test
    fun `persistLobbyState saves lobby`() =
        runTest {
            viewModel.persistLobbyState(
                lobbyCode = "ABCD",
                role = Role.OPERATIVE,
                team = Team.RED,
            )

            advanceUntilIdle()

            coVerify {
                sessionRepository.saveLobby(
                    "ABCD",
                    Role.OPERATIVE,
                    Team.RED,
                )
            }
        }

    @Test
    fun `clearLobby clears repository lobby data`() =
        runTest {
            viewModel.clearLobby()

            advanceUntilIdle()

            coVerify {
                sessionRepository.clearLobbyData()
            }
        }

    @Test
    fun `observeUser updates state when username is not blank`() =
        runTest {
            val uuid = UUID.randomUUID()

            userFlow.value =
                UserState(
                    username = "Anna",
                    userId = uuid,
                )

            advanceUntilIdle()

            assertEquals("Anna", viewModel.userState.value.username)
            assertEquals(uuid, viewModel.userState.value.userId)
        }

    @Test
    fun `observeUser ignores blank username`() =
        runTest {
            userFlow.value =
                UserState(
                    username = "",
                    userId = UUID.randomUUID(),
                )

            advanceUntilIdle()

            assertEquals("", viewModel.userState.value.username)
        }

    @Test
    fun `observeSession creates Available state when lobby data exists`() =
        runTest {
            val session =
                SessionState(
                    lobbyCode = "ABCD",
                    lobbyRole = Role.OPERATIVE,
                    lobbyTeam = Team.RED,
                    consumed = false,
                )

            sessionFlow.value = session

            advanceUntilIdle()

            assertTrue(viewModel.rejoinSessionState.value is RejoinState.Available)
        }

    @Test
    fun `observeSession creates None state when lobby data incomplete`() =
        runTest {
            sessionFlow.value =
                SessionState(
                    lobbyCode = null,
                    lobbyRole = Role.OPERATIVE,
                    lobbyTeam = Team.RED,
                    consumed = false,
                )

            advanceUntilIdle()

            assertEquals(
                RejoinState.None,
                viewModel.rejoinSessionState.value,
            )
        }

    @Test
    fun `setRejoinStateConsumed sets consumed to true`() =
        runTest {
            sessionFlow.value =
                SessionState(
                    lobbyCode = "ABCD",
                    lobbyRole = Role.OPERATIVE,
                    lobbyTeam = Team.RED,
                    consumed = false,
                )

            advanceUntilIdle()

            viewModel.setRejoinStateConsumed()

            val result =
                viewModel.rejoinSessionState.value as RejoinState.Available

            assertTrue(result.sessionState.consumed)
        }

    @Test
    fun `setRejoinStateConsumed does nothing for None state`() =
        runTest {
            viewModel.setRejoinStateConsumed()

            assertEquals(
                RejoinState.None,
                viewModel.rejoinSessionState.value,
            )
        }
}
