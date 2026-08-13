package com.example.rachapro.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.userPreferencesDataStore by preferencesDataStore(
    name = "user_preferences"
)

data class PomodoroPreferences(
    val focusMinutes: Int = DEFAULT_FOCUS_MINUTES,
    val shortBreakMinutes: Int = DEFAULT_SHORT_BREAK_MINUTES,
    val longBreakMinutes: Int = DEFAULT_LONG_BREAK_MINUTES
) {
    companion object {
        const val DEFAULT_FOCUS_MINUTES = 25
        const val DEFAULT_SHORT_BREAK_MINUTES = 5
        const val DEFAULT_LONG_BREAK_MINUTES = 15
        const val MIN_FOCUS_MINUTES = 1
        const val MAX_FOCUS_MINUTES = 120
        const val MIN_SHORT_BREAK_MINUTES = 1
        const val MAX_SHORT_BREAK_MINUTES = 60
        const val MIN_LONG_BREAK_MINUTES = 1
        const val MAX_LONG_BREAK_MINUTES = 120
    }
}

data class UserPreferences(
    val pomodoro: PomodoroPreferences =
        PomodoroPreferences(),
    val notificationsEnabled: Boolean = true
)

class UserPreferencesManager(
    private val context: Context
) {

    fun observeUserPreferences(
        userId: Long
    ): Flow<UserPreferences> {

        return context
            .userPreferencesDataStore
            .data
            .map { preferences ->

                UserPreferences(
                    pomodoro = PomodoroPreferences(
                        focusMinutes =
                            preferences[focusKey(userId)]
                                ?: PomodoroPreferences.DEFAULT_FOCUS_MINUTES,

                        shortBreakMinutes =
                            preferences[shortBreakKey(userId)]
                                ?: PomodoroPreferences.DEFAULT_SHORT_BREAK_MINUTES,

                        longBreakMinutes =
                            preferences[longBreakKey(userId)]
                                ?: PomodoroPreferences.DEFAULT_LONG_BREAK_MINUTES
                    ),

                    notificationsEnabled =
                        preferences[notificationsKey(userId)]
                            ?: true
                )
            }
    }

    fun observePomodoroPreferences(
        userId: Long
    ): Flow<PomodoroPreferences> {

        return observeUserPreferences(userId)
            .map { it.pomodoro }
    }

    suspend fun savePomodoroPreferences(
        userId: Long,
        focusMinutes: Int,
        shortBreakMinutes: Int,
        longBreakMinutes: Int
    ) {

        require(userId > 0L)
        require(focusMinutes in PomodoroPreferences.MIN_FOCUS_MINUTES..PomodoroPreferences.MAX_FOCUS_MINUTES)
        require(shortBreakMinutes in PomodoroPreferences.MIN_SHORT_BREAK_MINUTES..PomodoroPreferences.MAX_SHORT_BREAK_MINUTES)
        require(longBreakMinutes in PomodoroPreferences.MIN_LONG_BREAK_MINUTES..PomodoroPreferences.MAX_LONG_BREAK_MINUTES)

        context
            .userPreferencesDataStore
            .edit { preferences ->

                preferences[focusKey(userId)] =
                    focusMinutes

                preferences[shortBreakKey(userId)] =
                    shortBreakMinutes

                preferences[longBreakKey(userId)] =
                    longBreakMinutes
            }
    }

    suspend fun saveNotificationsEnabled(
        userId: Long,
        enabled: Boolean
    ) {

        require(userId > 0L)

        context
            .userPreferencesDataStore
            .edit { preferences ->

                preferences[notificationsKey(userId)] =
                    enabled
            }
    }

    private fun notificationsKey(
        userId: Long
    ) =
        booleanPreferencesKey(
            "notifications_enabled_$userId"
        )

    private fun focusKey(
        userId: Long
    ) =
        intPreferencesKey(
            "focus_minutes_$userId"
        )

    private fun shortBreakKey(
        userId: Long
    ) =
        intPreferencesKey(
            "short_break_minutes_$userId"
        )

    private fun longBreakKey(
        userId: Long
    ) =
        intPreferencesKey(
            "long_break_minutes_$userId"
        )
}