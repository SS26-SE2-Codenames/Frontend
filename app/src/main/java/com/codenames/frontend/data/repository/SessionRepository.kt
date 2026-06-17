package com.codenames.frontend.data.repository

import com.codenames.frontend.data.datastore.UserPreferencesDataStore
import com.codenames.frontend.data.model.SessionState
import com.codenames.frontend.data.model.UserState
import com.codenames.frontend.data.model.enums.Role
import com.codenames.frontend.data.model.enums.Team
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRepository
    @Inject
    constructor(
        private val dataStore: UserPreferencesDataStore,
    ) {
        val sessionFlow: Flow<SessionState> = dataStore.sessionData
        val userFlow: Flow<UserState> = dataStore.userData

        suspend fun saveLobby(
            lobbyCode: String,
            lobbyRole: Role,
            lobbyTeam: Team,
        ) {
            dataStore.saveLobbyData(lobbyCode, lobbyRole, lobbyTeam)
        }

        suspend fun saveUser(
            username: String,
            userId: UUID?,
        ) {
            dataStore.saveUserData(username, userId)
        }

        suspend fun clearLobbyData() {
            dataStore.clearSessionData()
        }

        suspend fun clearUserId() {
            dataStore.removeUserId()
        }
    }
