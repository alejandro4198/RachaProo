package com.example.rachapro.backend.reminder

import com.example.rachapro.backend.activity.ActivityRepository
import com.example.rachapro.backend.reminder.dto.CreateReminderRequest
import com.example.rachapro.backend.reminder.dto.ReminderResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ReminderService(
    private val reminderRepository: ReminderRepository,
    private val activityRepository: ActivityRepository
) {

    @Transactional(readOnly = true)
    fun findAllByUserId(userId: Long): List<ReminderResponse> {
        return reminderRepository
            .findAllByUserIdOrderByTriggerAtMillisAsc(userId)
            .map { it.toResponse() }
    }

    @Transactional
    fun create(
        userId: Long,
        request: CreateReminderRequest
    ): ReminderResponse {

        val title = request.title.trim()

        require(title.isNotBlank()) {
            "El titulo del recordatorio es obligatorio"
        }

        if (request.activityId != null) {
            requireNotNull(
                activityRepository.findByIdAndUserIdAndIsDeletedFalse(
                    request.activityId,
                    userId
                )
            ) {
                "La actividad no existe o no pertenece al usuario"
            }
        }

        val now = System.currentTimeMillis()

        val reminder = ReminderEntity(
            userId = userId,
            activityId = request.activityId,
            title = title,
            message = request.message.trim(),
            triggerAtMillis = request.triggerAtMillis,
            status = "SCHEDULED",
            createdAt = now,
            updatedAt = now,
            deliveredAt = null
        )

        return reminderRepository
            .save(reminder)
            .toResponse()
    }

    private fun ReminderEntity.toResponse(): ReminderResponse {
        return ReminderResponse(
            id = id,
            activityId = activityId,
            title = title,
            message = message,
            triggerAtMillis = triggerAtMillis,
            status = status,
            createdAt = createdAt,
            updatedAt = updatedAt,
            deliveredAt = deliveredAt
        )
    }

    @Transactional
    fun markDelivered(
        userId: Long,
        reminderId: Long
    ): ReminderResponse? {

        val reminder = reminderRepository
            .findByIdAndUserId(reminderId, userId)
            ?: return null

        val now = System.currentTimeMillis()

        reminder.status = "DELIVERED"
        reminder.deliveredAt = now
        reminder.updatedAt = now

        return reminderRepository
            .save(reminder)
            .toResponse()
    }

    @Transactional
    fun cancel(
        userId: Long,
        reminderId: Long
    ): ReminderResponse? {

        val reminder = reminderRepository
            .findByIdAndUserId(reminderId, userId)
            ?: return null

        val now = System.currentTimeMillis()

        reminder.status = "CANCELLED"
        reminder.updatedAt = now

        return reminderRepository
            .save(reminder)
            .toResponse()
    }
}