package com.example.rachapro.data.repository

import com.example.rachapro.data.local.dao.ActivityDao
import com.example.rachapro.data.local.dao.SubtaskDao
import com.example.rachapro.data.local.entity.SubtaskEntity
import kotlinx.coroutines.flow.Flow

class SubtaskRepository(
    private val subtaskDao: SubtaskDao,
    private val activityDao: ActivityDao
) {

    suspend fun observeSubtasks(
        userId: Long,
        activityId: Long
    ): SubtaskObserveResult {

        return try {

            if (
                !userOwnsActivity(
                    userId = userId,
                    activityId = activityId
                )
            ) {

                return SubtaskObserveResult.NotFoundOrNotAllowed
            }

            SubtaskObserveResult.Success(
                subtasks =
                    subtaskDao.observeSubtasksByActivity(
                        activityId = activityId
                    )
            )

        } catch (_: Exception) {

            SubtaskObserveResult.Error
        }
    }


    suspend fun createSubtask(
        userId: Long,
        activityId: Long,
        title: String
    ): SubtaskCreateResult {

        val normalizedTitle =
            title.trim()

        if (normalizedTitle.isBlank()) {

            return SubtaskCreateResult.InvalidData(
                message =
                    "Escribe el nombre de la subtarea."
            )
        }

        return try {

            if (
                !userOwnsActivity(
                    userId = userId,
                    activityId = activityId
                )
            ) {

                return SubtaskCreateResult.NotFoundOrNotAllowed
            }

            val currentTime =
                System.currentTimeMillis()

            val subtaskId =
                subtaskDao.insertSubtask(
                    SubtaskEntity(
                        activityId = activityId,
                        title = normalizedTitle,
                        isCompleted = false,
                        createdAt = currentTime,
                        updatedAt = currentTime,
                        completedAt = null
                    )
                )

            SubtaskCreateResult.Success(
                subtaskId = subtaskId
            )

        } catch (_: Exception) {

            SubtaskCreateResult.Error
        }
    }


    suspend fun updateSubtaskTitle(
        userId: Long,
        activityId: Long,
        subtaskId: Long,
        title: String
    ): SubtaskOperationResult {

        val normalizedTitle =
            title.trim()

        if (normalizedTitle.isBlank()) {

            return SubtaskOperationResult.InvalidData
        }

        return try {

            if (
                !userOwnsActivity(
                    userId = userId,
                    activityId = activityId
                )
            ) {

                return SubtaskOperationResult.NotFoundOrNotAllowed
            }

            val rowsAffected =
                subtaskDao.updateSubtaskTitle(
                    subtaskId = subtaskId,
                    activityId = activityId,
                    title = normalizedTitle,
                    updatedAt =
                        System.currentTimeMillis()
                )

            resultFromRowsAffected(
                rowsAffected = rowsAffected
            )

        } catch (_: Exception) {

            SubtaskOperationResult.Error
        }
    }


    suspend fun setSubtaskCompleted(
        userId: Long,
        activityId: Long,
        subtaskId: Long,
        isCompleted: Boolean
    ): SubtaskOperationResult {

        return try {

            if (
                !userOwnsActivity(
                    userId = userId,
                    activityId = activityId
                )
            ) {

                return SubtaskOperationResult.NotFoundOrNotAllowed
            }

            val currentTime =
                System.currentTimeMillis()

            val completedAt =
                if (isCompleted) {
                    currentTime
                } else {
                    null
                }

            val rowsAffected =
                subtaskDao.setSubtaskCompleted(
                    subtaskId = subtaskId,
                    activityId = activityId,
                    isCompleted = isCompleted,
                    completedAt = completedAt,
                    updatedAt = currentTime
                )

            resultFromRowsAffected(
                rowsAffected = rowsAffected
            )

        } catch (_: Exception) {

            SubtaskOperationResult.Error
        }
    }


    suspend fun deleteSubtask(
        userId: Long,
        activityId: Long,
        subtaskId: Long
    ): SubtaskOperationResult {

        return try {

            if (
                !userOwnsActivity(
                    userId = userId,
                    activityId = activityId
                )
            ) {

                return SubtaskOperationResult.NotFoundOrNotAllowed
            }

            val rowsAffected =
                subtaskDao.deleteSubtask(
                    subtaskId = subtaskId,
                    activityId = activityId
                )

            resultFromRowsAffected(
                rowsAffected = rowsAffected
            )

        } catch (_: Exception) {

            SubtaskOperationResult.Error
        }
    }


    private suspend fun userOwnsActivity(
        userId: Long,
        activityId: Long
    ): Boolean {

        val activity =
            activityDao.getActivityById(
                activityId = activityId,
                userId = userId
            )

        return activity != null &&
                !activity.isDeleted
    }


    private fun resultFromRowsAffected(
        rowsAffected: Int
    ): SubtaskOperationResult {

        return if (rowsAffected > 0) {

            SubtaskOperationResult.Success

        } else {

            SubtaskOperationResult.NotFoundOrNotAllowed
        }
    }
}


sealed interface SubtaskObserveResult {

    data class Success(
        val subtasks:
        Flow<List<SubtaskEntity>>
    ) : SubtaskObserveResult

    data object NotFoundOrNotAllowed :
        SubtaskObserveResult

    data object Error :
        SubtaskObserveResult
}


sealed interface SubtaskCreateResult {

    data class Success(
        val subtaskId: Long
    ) : SubtaskCreateResult

    data class InvalidData(
        val message: String
    ) : SubtaskCreateResult

    data object NotFoundOrNotAllowed :
        SubtaskCreateResult

    data object Error :
        SubtaskCreateResult
}


sealed interface SubtaskOperationResult {

    data object Success :
        SubtaskOperationResult

    data object NotFoundOrNotAllowed :
        SubtaskOperationResult

    data object InvalidData :
        SubtaskOperationResult

    data object Error :
        SubtaskOperationResult
}