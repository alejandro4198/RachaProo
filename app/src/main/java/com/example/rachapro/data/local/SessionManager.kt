package com.example.rachapro.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import androidx.datastore.preferences.core.stringPreferencesKey

private val Context.sessionDataStore by preferencesDataStore(
    name = "session_preferences"
)

data class SessionState(
    val userId: Long? = null,
    val authToken: String? = null,
    val onboardingCompleted: Boolean = false
) {

    val isLoggedIn: Boolean
        get() = userId != null
}

class SessionManager(
    context: Context
) {

    private val appContext = context.applicationContext

    companion object {

        private val USER_ID =
            longPreferencesKey("logged_user_id")

        private val ONBOARDING_COMPLETED =
            booleanPreferencesKey("onboarding_completed")

        private val AUTH_TOKEN =
            stringPreferencesKey("auth_token")
    }

    val sessionState: Flow<SessionState> =
        appContext.sessionDataStore.data
            .catch { exception ->

                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->

                SessionState(
                    userId = preferences[USER_ID],
                    onboardingCompleted =
                        preferences[ONBOARDING_COMPLETED]
                            ?: false
                )

                SessionState(
                    userId = preferences[USER_ID],
                    authToken = preferences[AUTH_TOKEN],
                    onboardingCompleted =
                        preferences[ONBOARDING_COMPLETED]
                            ?: false
                )
            }

    suspend fun saveLoggedUser(
        userId: Long
    ) {

        appContext.sessionDataStore.edit { preferences ->

            preferences[USER_ID] = userId
        }
    }

    suspend fun clearSession() {

        appContext.sessionDataStore.edit { preferences ->

            preferences.remove(USER_ID)
            preferences.remove(AUTH_TOKEN)
        }
    }

    suspend fun setOnboardingCompleted(
        completed: Boolean = true
    ) {

        appContext.sessionDataStore.edit { preferences ->

            preferences[ONBOARDING_COMPLETED] = completed
        }
    }

    suspend fun saveAuthenticatedSession(
        userId: Long,
        authToken: String
    ) {

        appContext.sessionDataStore.edit { preferences ->

            preferences[USER_ID] = userId
            preferences[AUTH_TOKEN] = authToken
        }
    }
}