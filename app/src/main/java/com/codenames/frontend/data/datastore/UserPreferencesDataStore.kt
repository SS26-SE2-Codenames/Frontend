package com.codenames.frontend.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.codenames.frontend.data.model.SessionState
import com.codenames.frontend.data.model.UserState
import com.codenames.frontend.data.model.enums.Role
import com.codenames.frontend.data.model.enums.Team
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesDataStore
    @Inject
    constructor(
        @param:ApplicationContext private val context: Context,
    ) {
        private object PreferencesKeys {
            val USERNAME = stringPreferencesKey("username")
            val USER_ID = stringPreferencesKey("player_id")
            val LOBBY_CODE = stringPreferencesKey("lobby_code")
            val LOBBY_ROLE = stringPreferencesKey("lobby_role")
            val LOBBY_TEAM = stringPreferencesKey("lobby_team")
        }

        suspend fun saveLobbyData(
            lobbyCode: String,
            lobbyRole: Role,
            lobbyTeam: Team,
        ) {
            context.dataStore.edit { prefs ->
                prefs[PreferencesKeys.LOBBY_CODE] = lobbyCode
                prefs[PreferencesKeys.LOBBY_ROLE] = lobbyRole.name
                prefs[PreferencesKeys.LOBBY_TEAM] = lobbyTeam.name
            }
        }

        suspend fun saveUserData(
            username: String,
            userId: UUID?,
        ) {
            context.dataStore.edit { prefs ->
                prefs[PreferencesKeys.USERNAME] = username
                prefs[PreferencesKeys.USER_ID] = userId.toString()
            }
        }

        val sessionData: Flow<SessionState> =
            context.dataStore.data.map { prefs ->
                SessionState(
                    lobbyCode = prefs[PreferencesKeys.LOBBY_CODE],
                    lobbyRole =
                        prefs[PreferencesKeys.LOBBY_ROLE]?.let {
                            runCatching { Role.valueOf(it) }.getOrNull()
                        },
                    lobbyTeam =
                        prefs[PreferencesKeys.LOBBY_TEAM]?.let {
                            runCatching { Team.valueOf(it) }.getOrNull()
                        },
                )
            }

        val userData: Flow<UserState> =
            context.dataStore.data.map { prefs ->
                UserState(
                    username = prefs[PreferencesKeys.USERNAME] ?: "",
                    userId = prefs[PreferencesKeys.USER_ID]?.let(UUID::fromString),
                )
            }

        suspend fun clearSessionData() {
            context.dataStore.edit { prefs ->
                prefs.remove(PreferencesKeys.LOBBY_CODE)
                prefs.remove(PreferencesKeys.LOBBY_ROLE)
                prefs.remove(PreferencesKeys.LOBBY_TEAM)
            }
        }

        suspend fun removeUserId() {
            context.dataStore.edit { prefs ->
                prefs.remove(PreferencesKeys.USER_ID)
            }
        }
    }
