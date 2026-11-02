package com.example.rachapro.backend.reminders.reminder

import com.example.rachapro.backend.activities.api.ActivityLookup
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*

class ReminderServiceTest {

    private val repository: ReminderRepository = mock(ReminderRepository::class.java)
    private val activityLookup: ActivityLookup = mock(ActivityLookup::class.java)
    private val service = ReminderService(repository, activityLookup)

    @Test
    fun `markDelivered scheduled reminder transitions to delivered`() {
        val reminder = ReminderEntity(
            id = 1L,
            userId = 10L,
            title = "Test Reminder",
            message = "Msg",
            triggerAtMillis = 1000L,
            status = "SCHEDULED",
            createdAt = 1000L,
            updatedAt = 1000L
        )

        `when`(repository.findByIdAndUserId(1L, 10L)).thenReturn(reminder)
        `when`(repository.save(any(ReminderEntity::class.java))).thenAnswer { it.arguments[0] }

        val response = service.markDelivered(10L, 1L)

        assertNotNull(response)
        assertEquals("DELIVERED", response?.status)
        assertNotNull(response?.deliveredAt)
        verify(repository, times(1)).save(any(ReminderEntity::class.java))
    }

    @Test
    fun `markDelivered cancelled reminder throws exception`() {
        val reminder = ReminderEntity(
            id = 1L,
            userId = 10L,
            title = "Test Reminder",
            message = "Msg",
            triggerAtMillis = 1000L,
            status = "CANCELLED",
            createdAt = 1000L,
            updatedAt = 1000L
        )

        `when`(repository.findByIdAndUserId(1L, 10L)).thenReturn(reminder)

        assertThrows<IllegalArgumentException> {
            service.markDelivered(10L, 1L)
        }
    }

    @Test
    fun `cancel delivered reminder throws exception`() {
        val reminder = ReminderEntity(
            id = 1L,
            userId = 10L,
            title = "Test Reminder",
            message = "Msg",
            triggerAtMillis = 1000L,
            status = "DELIVERED",
            createdAt = 1000L,
            updatedAt = 1000L
        )

        `when`(repository.findByIdAndUserId(1L, 10L)).thenReturn(reminder)

        assertThrows<IllegalArgumentException> {
            service.cancel(10L, 1L)
        }
    }
}
