package com.codenames.frontend.data.repository

import com.codenames.frontend.data.datastore.UserPreferencesDataStore
import com.codenames.frontend.data.model.SessionState
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRepository @Inject constructor(
    private val dataStore: UserPreferencesDataStore
){
    val sessionFlow: Flow<SessionState> = dataStore.sessionData

    suspend fun saveLobby(session: SessionState) {
        if(session.lobbyCode != null && session.lobbyRole != null && session.lobbyTeam != null) {
            dataStore.saveLobbyData(session.lobbyCode, session.lobbyRole, session.lobbyTeam)
        }
    }

    suspend fun saveUser(username: String, userId: UUID) {
        dataStore.saveUserData(username, userId)
    }

    suspend fun clearSessionData() {
        dataStore.clearSessionData()
    }
}