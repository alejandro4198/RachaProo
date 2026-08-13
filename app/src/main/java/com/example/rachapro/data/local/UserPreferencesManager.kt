package com.example.rachapro.data.local

import android.content.Context
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.userPreferencesDataStore by preferencesDataStore(
    name = "user_preferences"
)

data class PomodoroPreferences(
    val focusMinutes: Int = 25,
    val shortBreakMinutes: Int = 5,
    val longBreakMinutes: Int = 15
)

class UserPreferencesManager(
    private val context: Context
) {

    fun observePomodoroPreferences(
        userId: Long
    ): Flow<PomodoroPreferences> {

        return context
            .userPreferencesDataStore
            .data
            .map { preferences ->

                PomodoroPreferences(
                    focusMinutes =
                        preferences[
                            focusKey(userId)
                        ] ?: 25,

                    shortBreakMinutes =
                        preferences[
                            shortBreakKey(userId)
                        ] ?: 5,

                    longBreakMinutes =
                        preferences[
                            longBreakKey(userId)
                        ] ?: 15
                )
            }
    }

    suspend fun savePomodoroPreferences(
        userId: Long,
        focusMinutes: Int,
        shortBreakMinutes: Int,
        longBreakMinutes: Int
    ) {

        require(userId > 0L)
        require(focusMinutes in 1..120)
        require(shortBreakMinutes in 1..60)
        require(longBreakMinutes in 1..120)

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