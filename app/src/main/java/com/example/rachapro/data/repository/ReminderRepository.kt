package com.example.rachapro.data.repository

import com.example.rachapro.data.local.dao.ActivityDao
import com.example.rachapro.data.local.dao.ReminderDao
import com.example.rachapro.data.local.entity.ReminderEntity
import com.example.rachapro.network.ApiService
import com.example.rachapro.network.dto.CreateReminderRequest
import com.example.rachapro.network.dto.ReminderResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import java.io.IOException

class ReminderRepository(
    private val reminderDao: ReminderDao,
    private val activityDao: ActivityDao,
    private val apiService: ApiService
) {

    fun observeReminders(
        userId: Long
    ): Flow<List<ReminderEntity>> {

        return reminderDao.observeReminders(
            userId = userId
        )
    }

    fun observeRemindersByActivity(
        userId: Long,
        activityId: Long
    ): Flow<List<ReminderEntity>> {

        return reminderDao.observeRemindersByActivity(
            userId = userId,
            activityId = activityId
        )
    }

    suspend fun createReminder(
        userId: Long,
        activityId: Long?,
        title: String,
        message: String,
        triggerAtMillis: Long
    ): ReminderCreateResult {

        val normalizedTitle = title.trim()
        val normalizedMessage = message.trim()

        if (normalizedTitle.isBlank()) {

            return ReminderCreateResult.InvalidData(
                message = "El recordatorio debe tener un título."
            )
        }

        val currentTime =
            System.currentTimeMillis()

        if (triggerAtMillis <= currentTime) {

            return ReminderCreateResult.InvalidData(
                message = "Selecciona una fecha y hora futuras."
            )
        }

        return try {

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
                        triggerAtMillis = triggerAtMillis,
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

    suspend fun fetchRemoteReminders(
        userId: Long,
        activityId: Long
    ): RemoteRemindersResult {

        return try {

            val reminders =
                apiService
                    .getReminders()
                    .filter { response ->
                        response.activityId == activityId
                    }
                    .map { response ->
                        response.toEntity(
                            userId = userId
                        )
                    }

            reminders.forEach { reminder ->
                reminderDao.upsertReminder(
                    reminder
                )
            }

            RemoteRemindersResult.Success(
                reminders = reminders
            )

        } catch (exception: HttpException) {

            when (exception.code()) {

                401 ->
                    RemoteRemindersResult.Unauthorized

                else ->
                    RemoteRemindersResult.Error
            }

        } catch (_: IOException) {

            RemoteRemindersResult.Error

        } catch (_: Exception) {

            RemoteRemindersResult.Error
        }
    }

    suspend fun createRemoteReminder(
        userId: Long,
        activityId: Long?,
        title: String,
        message: String,
        triggerAtMillis: Long
    ): RemoteReminderCreateResult {

        val normalizedTitle =
            title.trim()

        val normalizedMessage =
            message.trim()

        if (normalizedTitle.isBlank()) {

            return RemoteReminderCreateResult.InvalidData(
                message =
                    "El recordatorio debe tener un título."
            )
        }

        if (
            triggerAtMillis <=
            System.currentTimeMillis()
        ) {

            return RemoteReminderCreateResult.InvalidData(
                message =
                    "Selecciona una fecha y hora futuras."
            )
        }

        return try {

            val response =
                apiService.createReminder(
                    request =
                        CreateReminderRequest(
                            activityId = activityId,
                            title = normalizedTitle,
                            message = normalizedMessage,
                            triggerAtMillis =
                                triggerAtMillis
                        )
                )

            val reminder =
                response.toEntity(
                    userId = userId
                )

            reminderDao.upsertReminder(
                reminder
            )

            RemoteReminderCreateResult.Success(
                reminder = reminder
            )

        } catch (exception: HttpException) {

            when (exception.code()) {

                400 ->
                    RemoteReminderCreateResult
                        .ActivityNotFoundOrNotAllowed

                401 ->
                    RemoteReminderCreateResult.Unauthorized

                else ->
                    RemoteReminderCreateResult.Error
            }

        } catch (_: IOException) {

            RemoteReminderCreateResult.Error

        } catch (_: Exception) {

            RemoteReminderCreateResult.Error
        }
    }

    suspend fun cancelRemoteReminder(
        reminderId: Long,
        userId: Long
    ): RemoteReminderOperationResult {

        return try {

            val response =
                apiService.cancelReminder(
                    reminderId = reminderId
                )

            val reminder =
                response.toEntity(
                    userId = userId
                )

            reminderDao.upsertReminder(
                reminder
            )

            RemoteReminderOperationResult.Success(
                reminder = reminder
            )

        } catch (exception: HttpException) {

            when (exception.code()) {

                401 ->
                    RemoteReminderOperationResult.Unauthorized

                404 ->
                    RemoteReminderOperationResult.NotFound

                else ->
                    RemoteReminderOperationResult.Error
            }

        } catch (_: IOException) {

            RemoteReminderOperationResult.Error

        } catch (_: Exception) {

            RemoteReminderOperationResult.Error
        }
    }

    suspend fun markRemoteReminderDelivered(
        reminderId: Long,
        userId: Long
    ): RemoteReminderOperationResult {

        return try {

            val response =
                apiService.markReminderDelivered(
                    reminderId = reminderId
                )

            val reminder =
                response.toEntity(
                    userId = userId
                )

            reminderDao.upsertReminder(
                reminder
            )

            RemoteReminderOperationResult.Success(
                reminder = reminder
            )

        } catch (exception: HttpException) {

            when (exception.code()) {

                401 ->
                    RemoteReminderOperationResult.Unauthorized

                404 ->
                    RemoteReminderOperationResult.NotFound

                else ->
                    RemoteReminderOperationResult.Error
            }

        } catch (_: IOException) {

            RemoteReminderOperationResult.Error

        } catch (_: Exception) {

            RemoteReminderOperationResult.Error
        }
    }

    private fun ReminderResponse.toEntity(
        userId: Long
    ): ReminderEntity {

        return ReminderEntity(
            id = id,
            userId = userId,
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

sealed interface ReminderOperationResult {

    data object Success :
        ReminderOperationResult

    data object NotFoundOrNotAllowed :
        ReminderOperationResult

    data object Error :
        ReminderOperationResult
}

sealed interface RemoteRemindersResult {

    data class Success(
        val reminders: List<ReminderEntity>
    ) : RemoteRemindersResult

    data object Unauthorized :
        RemoteRemindersResult

    data object Error :
        RemoteRemindersResult
}

sealed interface RemoteReminderCreateResult {

    data class Success(
        val reminder: ReminderEntity
    ) : RemoteReminderCreateResult

    data class InvalidData(
        val message: String
    ) : RemoteReminderCreateResult

    data object ActivityNotFoundOrNotAllowed :
        RemoteReminderCreateResult

    data object Unauthorized :
        RemoteReminderCreateResult

    data object Error :
        RemoteReminderCreateResult
}

sealed interface RemoteReminderOperationResult {

    data class Success(
        val reminder: ReminderEntity
    ) : RemoteReminderOperationResult

    data object NotFound :
        RemoteReminderOperationResult

    data object Unauthorized :
        RemoteReminderOperationResult

    data object Error :
        RemoteReminderOperationResult
}