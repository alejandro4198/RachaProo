package com.example.rachapro.backend.security

import com.example.rachapro.backend.activities.activity.ActivityRepository
import com.example.rachapro.backend.activities.activity.ActivityService
import com.example.rachapro.backend.activities.category.CategoryRepository
import com.example.rachapro.backend.focus.pomodoro.PomodoroSessionRepository
import com.example.rachapro.backend.focus.pomodoro.PomodoroSessionService
import com.example.rachapro.backend.reminders.reminder.ReminderRepository
import com.example.rachapro.backend.reminders.reminder.ReminderService
import com.example.rachapro.backend.activities.api.ActivityLookup
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*

class ResourceOwnershipSecurityTest {

    @Test
    fun `user A cannot complete activity belonging to user B`() {
        val activityRepo = mock(ActivityRepository::class.java)
        val categoryRepo = mock(CategoryRepository::class.java)
        val service = ActivityService(activityRepo, categoryRepo)

        val userA = 10L
        val activityB = 99L

        `when`(activityRepo.findByIdAndUserIdAndIsDeletedFalse(activityB, userA)).thenReturn(null)

        val response = service.complete(userA, activityB)

        assertNull(response)
        verify(activityRepo, times(0)).save(any())
    }

    @Test
    fun `user A cannot complete pomodoro session belonging to user B`() {
        val pomodoroRepo = mock(PomodoroSessionRepository::class.java)
        val activityLookup = mock(ActivityLookup::class.java)
        val service = PomodoroSessionService(pomodoroRepo, activityLookup)

        val userA = 10L
        val sessionB = 99L

        `when`(pomodoroRepo.findByIdAndUserId(sessionB, userA)).thenReturn(null)

        val response = service.complete(userA, sessionB)

        assertNull(response)
        verify(pomodoroRepo, times(0)).save(any())
    }

    @Test
    fun `user A cannot cancel reminder belonging to user B`() {
        val reminderRepo = mock(ReminderRepository::class.java)
        val activityLookup = mock(ActivityLookup::class.java)
        val service = ReminderService(reminderRepo, activityLookup)

        val userA = 10L
        val reminderB = 99L

        `when`(reminderRepo.findByIdAndUserId(reminderB, userA)).thenReturn(null)

        val response = service.cancel(userA, reminderB)

        assertNull(response)
        verify(reminderRepo, times(0)).save(any())
    }
}
