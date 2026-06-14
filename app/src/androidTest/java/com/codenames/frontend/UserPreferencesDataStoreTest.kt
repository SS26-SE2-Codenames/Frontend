package com.codenames.frontend

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.codenames.frontend.data.datastore.UserPreferencesDataStore
import com.codenames.frontend.data.model.enums.Role
import com.codenames.frontend.data.model.enums.Team
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class UserPreferencesDataStoreTest {
    private lateinit var dataStore: UserPreferencesDataStore

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()

        dataStore = UserPreferencesDataStore(context)

        runBlocking {
            dataStore.clearSessionData()
            dataStore.saveUserData("", null)
        }
    }

    @After
    fun cleanup() {
        runBlocking {
            dataStore.clearSessionData()
        }
    }

    @Test
    fun saveUserData_persistsUserData() =
        runBlocking {
            val uuid = UUID.randomUUID()

            dataStore.saveUserData(
                username = "Anna",
                userId = uuid,
            )

            val result = dataStore.userData.first()

            assertEquals("Anna", result.username)
            assertEquals(uuid, result.userId)
        }

    @Test
    fun saveLobbyData_persistsLobbyData() =
        runBlocking {
            dataStore.saveLobbyData(
                lobbyCode = "ABCD",
                lobbyRole = Role.OPERATIVE,
                lobbyTeam = Team.RED,
            )

            val result = dataStore.sessionData.first()

            assertEquals("ABCD", result.lobbyCode)
            assertEquals(Role.OPERATIVE, result.lobbyRole)
            assertEquals(Team.RED, result.lobbyTeam)
        }

    @Test
    fun clearSessionData_removesLobbyInformation() =
        runBlocking {
            dataStore.saveLobbyData(
                lobbyCode = "ABCD",
                lobbyRole = Role.OPERATIVE,
                lobbyTeam = Team.RED,
            )

            dataStore.clearSessionData()

            val result = dataStore.sessionData.first()

            assertNull(result.lobbyCode)
            assertNull(result.lobbyRole)
            assertNull(result.lobbyTeam)
        }
}
