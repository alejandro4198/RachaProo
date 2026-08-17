package com.example.rachapro.domain

import com.example.rachapro.data.local.entity.AchievementType

data class AchievementCheckInput(
    val completedActivitiesCount: Int,
    val completedFocusPomodorosCount: Int,
    val currentStreakDays: Int
)

object AchievementEngine {

    fun typesToUnlock(
        input: AchievementCheckInput
    ): List<String> {

        val unlocked = mutableListOf<String>()

        if (input.completedActivitiesCount >= 1) {
            unlocked += AchievementType.FIRST_ACTIVITY_COMPLETED
        }

        if (input.completedFocusPomodorosCount >= 1) {
            unlocked += AchievementType.FIRST_FOCUS_POMODORO
        }

        if (input.currentStreakDays >= 3) {
            unlocked += AchievementType.STREAK_3_DAYS
        }

        if (input.currentStreakDays >= 7) {
            unlocked += AchievementType.STREAK_7_DAYS
        }

        if (input.completedActivitiesCount >= 10) {
            unlocked += AchievementType.ACTIVITIES_10_COMPLETED
        }

        if (input.completedFocusPomodorosCount >= 10) {
            unlocked += AchievementType.POMODOROS_10_COMPLETED
        }

        return unlocked
    }

    fun titleFor(
        type: String
    ): String {

        return when (type) {

            AchievementType.FIRST_ACTIVITY_COMPLETED ->
                "Primera actividad"

            AchievementType.FIRST_FOCUS_POMODORO ->
                "Primer Pomodoro"

            AchievementType.STREAK_3_DAYS ->
                "Racha de 3 días"

            AchievementType.STREAK_7_DAYS ->
                "Racha de 7 días"

            AchievementType.ACTIVITIES_10_COMPLETED ->
                "10 actividades"

            AchievementType.POMODOROS_10_COMPLETED ->
                "10 Pomodoros"

            else ->
                "Logro"
        }
    }

    fun descriptionFor(
        type: String
    ): String {

        return when (type) {

            AchievementType.FIRST_ACTIVITY_COMPLETED ->
                "Completaste tu primera actividad."

            AchievementType.FIRST_FOCUS_POMODORO ->
                "Completaste tu primer Pomodoro de enfoque."

            AchievementType.STREAK_3_DAYS ->
                "Mantuviste una racha de 3 días consecutivos."

            AchievementType.STREAK_7_DAYS ->
                "Mantuviste una racha de 7 días consecutivos."

            AchievementType.ACTIVITIES_10_COMPLETED ->
                "Completaste 10 actividades en total."

            AchievementType.POMODOROS_10_COMPLETED ->
                "Completaste 10 Pomodoros de enfoque."

            else ->
                "Logro desbloqueado."
        }
    }

    val allTypes: List<String> =
        listOf(
            AchievementType.FIRST_ACTIVITY_COMPLETED,
            AchievementType.FIRST_FOCUS_POMODORO,
            AchievementType.STREAK_3_DAYS,
            AchievementType.STREAK_7_DAYS,
            AchievementType.ACTIVITIES_10_COMPLETED,
            AchievementType.POMODOROS_10_COMPLETED
        )
}
