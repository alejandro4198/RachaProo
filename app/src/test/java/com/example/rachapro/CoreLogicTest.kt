package com.example.rachapro

import com.example.rachapro.domain.AchievementCheckInput
import com.example.rachapro.domain.AchievementEngine
import com.example.rachapro.data.local.entity.AchievementType
import com.example.rachapro.domain.RegistrationValidator
import com.example.rachapro.domain.StreakCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class StreakCalculatorTest {

    @Test
    fun emptyDays_returnsZeroStreaks() {
        val result = StreakCalculator.calculate(
            completedDays = emptyList(),
            todayEpochDay = 100L
        )

        assertEquals(0, result.current)
        assertEquals(0, result.best)
    }

    @Test
    fun activityAlone_countsOneDay() {
        val result = StreakCalculator.calculate(
            completedDays = listOf(100L),
            todayEpochDay = 100L
        )

        assertEquals(1, result.current)
        assertEquals(1, result.best)
    }

    @Test
    fun multipleActionsSameDay_countAsOneDay() {
        val result = StreakCalculator.calculate(
            completedDays = listOf(100L, 100L),
            todayEpochDay = 100L
        )

        assertEquals(1, result.current)
        assertEquals(1, result.best)
    }

    @Test
    fun consecutiveDays_calculateCurrentAndBest() {
        val result = StreakCalculator.calculate(
            completedDays = listOf(98L, 99L, 100L),
            todayEpochDay = 100L
        )

        assertEquals(3, result.current)
        assertEquals(3, result.best)
    }

    @Test
    fun brokenStreak_resetsCurrentButKeepsBest() {
        val result = StreakCalculator.calculate(
            completedDays = listOf(90L, 91L, 92L, 100L),
            todayEpochDay = 100L
        )

        assertEquals(1, result.current)
        assertEquals(3, result.best)
    }

    @Test
    fun yesterdayValidTodayNotYet_countsCurrentFromYesterday() {
        val result = StreakCalculator.calculate(
            completedDays = listOf(98L, 99L),
            todayEpochDay = 100L
        )

        assertEquals(2, result.current)
        assertEquals(2, result.best)
    }
}

class AchievementEngineTest {

    @Test
    fun unlocksFirstActivityAchievement() {
        val types = AchievementEngine.typesToUnlock(
            AchievementCheckInput(
                completedActivitiesCount = 1,
                completedFocusPomodorosCount = 0,
                currentStreakDays = 0
            )
        )

        assertTrue(
            AchievementType.FIRST_ACTIVITY_COMPLETED in types
        )
    }

    @Test
    fun unlocksStreakAndCountAchievements() {
        val types = AchievementEngine.typesToUnlock(
            AchievementCheckInput(
                completedActivitiesCount = 10,
                completedFocusPomodorosCount = 10,
                currentStreakDays = 7
            )
        )

        assertEquals(6, types.size)
    }
}

class RegistrationValidatorTest {

    @Test
    fun validRegistration_returnsNull() {
        val error = RegistrationValidator.validate(
            fullName = "Ana Pérez",
            email = "ana@example.com",
            password = "password123",
            confirmPassword = "password123",
            semester = 3,
            acceptedPrivacyPolicy = true
        )

        assertNull(error)
    }

    @Test
    fun shortPassword_returnsError() {
        val error = RegistrationValidator.validate(
            fullName = "Ana Pérez",
            email = "ana@example.com",
            password = "123",
            confirmPassword = "123",
            semester = 3,
            acceptedPrivacyPolicy = true
        )

        assertEquals(
            "La contraseña debe tener mínimo 8 caracteres.",
            error
        )
    }

    @Test
    fun invalidEmail_returnsError() {
        val error = RegistrationValidator.validate(
            fullName = "Ana Pérez",
            email = "correo-invalido",
            password = "password123",
            confirmPassword = "password123",
            semester = 3,
            acceptedPrivacyPolicy = true
        )

        assertEquals(
            "Ingresa un correo electrónico válido.",
            error
        )
    }
}
