package com.example.rachapro.data.repository

import com.example.rachapro.data.local.dao.ActivityDao
import com.example.rachapro.data.local.dao.ReminderDao
import com.example.rachapro.data.local.entity.ReminderEntity
import kotlinx.coroutines.flow.Flow

class ReminderRepository(
    private val reminderDao: ReminderDao,
    private val activityDao: ActivityDao
) {

    /*
     * ---------------------------------------------------------
     * OBSERVAR TODOS LOS RECORDATORIOS DEL USUARIO
     * ---------------------------------------------------------
     */

    fun observeReminders(
        userId: Long
    ): Flow<List<ReminderEntity>> {

        return reminderDao.observeReminders(
            userId = userId
        )
    }

    /*
     * ---------------------------------------------------------
     * OBSERVAR RECORDATORIOS DE UNA ACTIVIDAD
     * ---------------------------------------------------------
     */

    fun observeRemindersByActivity(
        userId: Long,
        activityId: Long
    ): Flow<List<ReminderEntity>> {

        return reminderDao.observeRemindersByActivity(
            userId = userId,
            activityId = activityId
        )
    }

    /*
     * ---------------------------------------------------------
     * CREAR RECORDATORIO
     * ---------------------------------------------------------
     */

    suspend fun createReminder(
        userId: Long,
        activityId: Long?,
        title: String,
        message: String,
        triggerAtMillis: Long
    ): ReminderCreateResult {

        val normalizedTitle =
            title.trim()

        val normalizedMessage =
            message.trim()

        if (normalizedTitle.isBlank()) {

            return ReminderCreateResult.InvalidData(
                message =
                    "El recordatorio debe tener un título."
            )
        }

        val currentTime =
            System.currentTimeMillis()

        if (triggerAtMillis <= currentTime) {

            return ReminderCreateResult.InvalidData(
                message =
                    "Selecciona una fecha y hora futuras."
            )
        }

        return try {

            /*
             * Si está asociado a una actividad,
             * comprobamos que pertenezca al usuario.
             */
            if (activityId != null) {

                val activity =
                    activityDao.getActivityById(
                        activityId = activityId,
                        userId = userId
                    )

                if (
                    activity == null ||
                    activity.isDeleted
                ) {

                    return ReminderCreateResult
                        .ActivityNotFoundOrNotAllowed
                }
            }

            val reminderId =
                reminderDao.insertReminder(
                    ReminderEntity(
                        userId = userId,
                        activityId = activityId,
                        title = normalizedTitle,
                        message = normalizedMessage,
                        triggerAtMillis =
                            triggerAtMillis,
                        createdAt = currentTime,
                        updatedAt = currentTime
                    )
                )

            ReminderCreateResult.Success(
                reminderId = reminderId
            )

        } catch (_: Exception) {

            ReminderCreateResult.Error
        }
    }

    /*
     * ---------------------------------------------------------
     * OBTENER RECORDATORIO
     * ---------------------------------------------------------
     */

    suspend fun getReminderById(
        reminderId: Long,
        userId: Long
    ): ReminderEntity? {

        return try {

            reminderDao.getReminderById(
                reminderId = reminderId,
                userId = userId
            )

        } catch (_: Exception) {

            null
        }
    }

    /*
     * ---------------------------------------------------------
     * OBTENER RECORDATORIOS PROGRAMADOS
     * ---------------------------------------------------------
     */

    suspend fun getScheduledReminders(
        userId: Long
    ): List<ReminderEntity> {

        return try {

            reminderDao.getScheduledReminders(
                userId = userId
            )

        } catch (_: Exception) {

            emptyList()
        }
    }

    /*
     * ---------------------------------------------------------
     * CANCELAR
     * ---------------------------------------------------------
     */

    suspend fun cancelReminder(
        reminderId: Long,
        userId: Long
    ): ReminderOperationResult {

        return try {

            val rowsAffected =
                reminderDao.cancelReminder(
                    reminderId = reminderId,
                    userId = userId,
                    updatedAt =
                        System.currentTimeMillis()
                )

            resultFromRowsAffected(
                rowsAffected
            )

        } catch (_: Exception) {

            ReminderOperationResult.Error
        }
    }

    /*
     * ---------------------------------------------------------
     * MARCAR COMO ENTREGADO
     * ---------------------------------------------------------
     */

    suspend fun markReminderDelivered(
        reminderId: Long,
        userId: Long
    ): ReminderOperationResult {

        return try {

            val currentTime =
                System.currentTimeMillis()

            val rowsAffected =
                reminderDao.markReminderDelivered(
                    reminderId = reminderId,
                    userId = userId,
                    deliveredAt = currentTime,
                    updatedAt = currentTime
                )

            resultFromRowsAffected(
                rowsAffected
            )

        } catch (_: Exception) {

            ReminderOperationResult.Error
        }
    }

    /*
     * ---------------------------------------------------------
     * RESULTADO UPDATE
     * ---------------------------------------------------------
     */

    private fun resultFromRowsAffected(
        rowsAffected: Int
    ): ReminderOperationResult {

        return if (rowsAffected > 0) {

            ReminderOperationResult.Success

        } else {

            ReminderOperationResult
                .NotFoundOrNotAllowed
        }
    }
}

/*
 * =============================================================
 * RESULTADO AL CREAR
 * =============================================================
 */

sealed interface ReminderCreateResult {

    data class Success(
        val reminderId: Long
    ) : ReminderCreateResult

    data class InvalidData(
        val message: String
    ) : ReminderCreateResult

    data object ActivityNotFoundOrNotAllowed :
        ReminderCreateResult

    data object Error :
        ReminderCreateResult
}

/*
 * =============================================================
 * RESULTADO DE OPERACIONES
 * =============================================================
 */

sealed interface ReminderOperationResult {

    data object Success :
        ReminderOperationResult

    data object NotFoundOrNotAllowed :
        ReminderOperationResult

    data object Error :
        ReminderOperationResult
}