package com.codenames.frontend.data.repository

import com.codenames.frontend.data.datastore.UserPreferencesDataStore
import com.codenames.frontend.data.model.enums.Role
import com.codenames.frontend.data.model.enums.Team
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class SessionRepositoryTest {
    private lateinit var repository: SessionRepository
    private lateinit var dataStore: UserPreferencesDataStore

    @Before
    fun setup() {
        dataStore = mockk(relaxed = true)
        repository = SessionRepository(dataStore)
    }

    @Test
    fun `saveLobby delegates to datastore`() =
        runTest {
            repository.saveLobby(
                "ABCD",
                Role.OPERATIVE,
                Team.RED,
            )

            coVerify {
                dataStore.saveLobbyData(
                    "ABCD",
                    Role.OPERATIVE,
                    Team.RED,
                )
            }
        }

    @Test
    fun `saveUser delegates to datastore`() =
        runTest {
            val uuid = UUID.randomUUID()

            repository.saveUser("Anna", uuid)

            coVerify {
                dataStore.saveUserData(
                    "Anna",
                    uuid,
                )
            }
        }

    @Test
    fun `clearLobbyData delegates to datastore`() =
        runTest {
            repository.clearLobbyData()

            coVerify {
                dataStore.clearSessionData()
            }
        }

    @Test
    fun `clearUserId delegates to datastore`() =
        runTest {
            repository.clearUserId()

            coVerify {
                dataStore.removeUserId()
            }
        }
}
