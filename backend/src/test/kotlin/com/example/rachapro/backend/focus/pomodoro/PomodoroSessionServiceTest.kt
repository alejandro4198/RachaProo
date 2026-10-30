package com.example.rachapro.backend.focus.pomodoro

import com.example.rachapro.backend.activities.api.ActivityLookup
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*

class PomodoroSessionServiceTest {

    private val repository: PomodoroSessionRepository = mock(PomodoroSessionRepository::class.java)
    private val activityLookup: ActivityLookup = mock(ActivityLookup::class.java)
    private val service = PomodoroSessionService(repository, activityLookup)

    @Test
    fun `complete running session transitions to completed and saves once`() {
        val originalUpdatedAt = 1000L
        val session = PomodoroSessionEntity(
            id = 1L,
            userId = 10L,
            type = "FOCUS",
            plannedDurationSeconds = 1500,
            status = "RUNNING",
            startedAtMillis = 1000L,
            createdAt = 1000L,
            updatedAt = originalUpdatedAt
        )

        `when`(repository.findByIdAndUserId(1L, 10L)).thenReturn(session)
        `when`(repository.save(any(PomodoroSessionEntity::class.java))).thenAnswer { it.arguments[0] }

        val response = service.complete(10L, 1L)

        assertNotNull(response)
        assertEquals("COMPLETED", response?.status)
        assertNotNull(response?.completedAtMillis)
        assertNotNull(response?.completedDateEpochDay)
        assertTrue(response!!.updatedAt >= originalUpdatedAt)
        verify(repository, times(1)).save(any(PomodoroSessionEntity::class.java))
    }

    @Test
    fun `complete already completed session is idempotent and saves zero times`() {
        val originalCompletedAt = 2000L
        val originalEpochDay = 10L
        val originalUpdatedAt = 2000L

        val session = PomodoroSessionEntity(
            id = 1L,
            userId = 10L,
            type = "FOCUS",
            plannedDurationSeconds = 1500,
            status = "COMPLETED",
            startedAtMillis = 1000L,
            completedAtMillis = originalCompletedAt,
            completedDateEpochDay = originalEpochDay,
            createdAt = 1000L,
            updatedAt = originalUpdatedAt
        )

        `when`(repository.findByIdAndUserId(1L, 10L)).thenReturn(session)

        val response = service.complete(10L, 1L)

        assertNotNull(response)
        assertEquals("COMPLETED", response?.status)
        assertEquals(originalCompletedAt, response?.completedAtMillis)
        assertEquals(originalEpochDay, response?.completedDateEpochDay)
        assertEquals(originalUpdatedAt, response?.updatedAt)
        verify(repository, times(0)).save(any(PomodoroSessionEntity::class.java))
    }

    @Test
    fun `complete paused session transitions to completed and updates pause duration`() {
        val originalTotalPaused = 500L
        val session = PomodoroSessionEntity(
            id = 1L,
            userId = 10L,
            type = "FOCUS",
            plannedDurationSeconds = 1500,
            status = "PAUSED",
            startedAtMillis = 1000L,
            pausedAtMillis = 1500L,
            totalPausedMillis = originalTotalPaused,
            createdAt = 1000L,
            updatedAt = 1000L
        )

        `when`(repository.findByIdAndUserId(1L, 10L)).thenReturn(session)
        `when`(repository.save(any(PomodoroSessionEntity::class.java))).thenAnswer { it.arguments[0] }

        val response = service.complete(10L, 1L)

        assertNotNull(response)
        assertEquals("COMPLETED", response?.status)
        assertNull(response?.pausedAtMillis)
        assertTrue(response!!.totalPausedMillis > originalTotalPaused)
        assertNotNull(response?.completedAtMillis)
        verify(repository, times(1)).save(any(PomodoroSessionEntity::class.java))
    }

    @Test
    fun `complete cancelled session throws exception and saves zero times`() {
        val session = PomodoroSessionEntity(
            id = 1L,
            userId = 10L,
            type = "FOCUS",
            plannedDurationSeconds = 1500,
            status = "CANCELLED",
            startedAtMillis = 1000L,
            createdAt = 1000L,
            updatedAt = 1000L
        )

        `when`(repository.findByIdAndUserId(1L, 10L)).thenReturn(session)

        assertThrows<IllegalArgumentException> {
            service.complete(10L, 1L)
        }
        verify(repository, times(0)).save(any(PomodoroSessionEntity::class.java))
    }

    @Test
    fun `complete non-existent session returns null and saves zero times`() {
        `when`(repository.findByIdAndUserId(99L, 10L)).thenReturn(null)

        val response = service.complete(10L, 99L)

        assertNull(response)
        verify(repository, times(0)).save(any(PomodoroSessionEntity::class.java))
    }
}
