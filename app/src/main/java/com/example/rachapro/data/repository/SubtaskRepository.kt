package com.example.rachapro.data.repository

import com.example.rachapro.data.local.dao.ActivityDao
import com.example.rachapro.data.local.dao.SubtaskDao
import com.example.rachapro.data.local.entity.SubtaskEntity
import com.example.rachapro.network.ApiService
import com.example.rachapro.network.dto.CreateSubtaskRequest
import com.example.rachapro.network.dto.SubtaskResponse
import com.example.rachapro.network.dto.UpdateSubtaskRequest
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import java.io.IOException

class SubtaskRepository(
    private val subtaskDao: SubtaskDao,
    private val activityDao: ActivityDao,
    private val apiService: ApiService
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
                message = "Escribe el nombre de la subtarea."
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
                    updatedAt = System.currentTimeMillis()
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

    suspend fun fetchRemoteSubtasks(
        activityId: Long
    ): RemoteSubtasksResult {

        return try {

            val subtasks =
                apiService.getSubtasks(
                    activityId = activityId
                )

            RemoteSubtasksResult.Success(
                subtasks = subtasks
            )

        } catch (exception: HttpException) {

            when (exception.code()) {

                401 ->
                    RemoteSubtasksResult.Unauthorized

                404 ->
                    RemoteSubtasksResult.NotFound

                else ->
                    RemoteSubtasksResult.Error
            }

        } catch (_: IOException) {

            RemoteSubtasksResult.Error

        } catch (_: Exception) {

            RemoteSubtasksResult.Error
        }
    }

    suspend fun createRemoteSubtask(
        activityId: Long,
        title: String
    ): RemoteSubtaskOperationResult {

        val normalizedTitle =
            title.trim()

        if (normalizedTitle.isBlank()) {
            return RemoteSubtaskOperationResult.InvalidData
        }

        return try {

            val subtask =
                apiService.createSubtask(
                    activityId = activityId,
                    request =
                        CreateSubtaskRequest(
                            title = normalizedTitle
                        )
                )

            RemoteSubtaskOperationResult.Success(
                subtask = subtask
            )

        } catch (exception: HttpException) {

            when (exception.code()) {

                400 ->
                    RemoteSubtaskOperationResult.InvalidData

                401 ->
                    RemoteSubtaskOperationResult.Unauthorized

                404 ->
                    RemoteSubtaskOperationResult.NotFound

                else ->
                    RemoteSubtaskOperationResult.Error
            }

        } catch (_: IOException) {

            RemoteSubtaskOperationResult.Error

        } catch (_: Exception) {

            RemoteSubtaskOperationResult.Error
        }
    }

    suspend fun updateRemoteSubtask(
        activityId: Long,
        subtaskId: Long,
        title: String
    ): RemoteSubtaskOperationResult {

        val normalizedTitle =
            title.trim()

        if (normalizedTitle.isBlank()) {
            return RemoteSubtaskOperationResult.InvalidData
        }

        return try {

            val subtask =
                apiService.updateSubtask(
                    activityId = activityId,
                    subtaskId = subtaskId,
                    request =
                        UpdateSubtaskRequest(
                            title = normalizedTitle
                        )
                )

            RemoteSubtaskOperationResult.Success(
                subtask = subtask
            )

        } catch (exception: HttpException) {

            when (exception.code()) {

                400 ->
                    RemoteSubtaskOperationResult.InvalidData

                401 ->
                    RemoteSubtaskOperationResult.Unauthorized

                404 ->
                    RemoteSubtaskOperationResult.NotFound

                else ->
                    RemoteSubtaskOperationResult.Error
            }

        } catch (_: IOException) {

            RemoteSubtaskOperationResult.Error

        } catch (_: Exception) {

            RemoteSubtaskOperationResult.Error
        }
    }

    suspend fun completeRemoteSubtask(
        activityId: Long,
        subtaskId: Long
    ): RemoteSubtaskOperationResult {

        return try {

            val subtask =
                apiService.completeSubtask(
                    activityId = activityId,
                    subtaskId = subtaskId
                )

            RemoteSubtaskOperationResult.Success(
                subtask = subtask
            )

        } catch (exception: HttpException) {

            when (exception.code()) {

                401 ->
                    RemoteSubtaskOperationResult.Unauthorized

                404 ->
                    RemoteSubtaskOperationResult.NotFound

                else ->
                    RemoteSubtaskOperationResult.Error
            }

        } catch (_: IOException) {

            RemoteSubtaskOperationResult.Error

        } catch (_: Exception) {

            RemoteSubtaskOperationResult.Error
        }
    }

    suspend fun setRemoteSubtaskCompleted(
        activityId: Long,
        subtaskId: Long,
        isCompleted: Boolean
    ): RemoteSubtaskOperationResult {

        return try {

            val subtask =
                if (isCompleted) {

                    apiService.completeSubtask(
                        activityId = activityId,
                        subtaskId = subtaskId
                    )

                } else {

                    apiService.uncompleteSubtask(
                        activityId = activityId,
                        subtaskId = subtaskId
                    )
                }

            RemoteSubtaskOperationResult.Success(
                subtask = subtask
            )

        } catch (exception: HttpException) {

            when (exception.code()) {

                401 ->
                    RemoteSubtaskOperationResult.Unauthorized

                404 ->
                    RemoteSubtaskOperationResult.NotFound

                else ->
                    RemoteSubtaskOperationResult.Error
            }

        } catch (_: IOException) {

            RemoteSubtaskOperationResult.Error

        } catch (_: Exception) {

            RemoteSubtaskOperationResult.Error
        }
    }

    suspend fun deleteRemoteSubtask(
        activityId: Long,
        subtaskId: Long
    ): RemoteSubtaskDeleteResult {

        return try {

            val response =
                apiService.deleteSubtask(
                    activityId = activityId,
                    subtaskId = subtaskId
                )

            when (response.code()) {

                204 ->
                    RemoteSubtaskDeleteResult.Success

                401 ->
                    RemoteSubtaskDeleteResult.Unauthorized

                404 ->
                    RemoteSubtaskDeleteResult.NotFound

                else ->
                    RemoteSubtaskDeleteResult.Error
            }

        } catch (_: IOException) {

            RemoteSubtaskDeleteResult.Error

        } catch (_: Exception) {

            RemoteSubtaskDeleteResult.Error
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
        val subtasks: Flow<List<SubtaskEntity>>
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

sealed interface RemoteSubtasksResult {

    data class Success(
        val subtasks: List<SubtaskResponse>
    ) : RemoteSubtasksResult

    data object NotFound :
        RemoteSubtasksResult

    data object Unauthorized :
        RemoteSubtasksResult

    data object Error :
        RemoteSubtasksResult
}

sealed interface RemoteSubtaskOperationResult {

    data class Success(
        val subtask: SubtaskResponse
    ) : RemoteSubtaskOperationResult

    data object InvalidData :
        RemoteSubtaskOperationResult

    data object NotFound :
        RemoteSubtaskOperationResult

    data object Unauthorized :
        RemoteSubtaskOperationResult

    data object Error :
        RemoteSubtaskOperationResult
}

sealed interface RemoteSubtaskDeleteResult {

    data object Success :
        RemoteSubtaskDeleteResult

    data object NotFound :
        RemoteSubtaskDeleteResult

    data object Unauthorized :
        RemoteSubtaskDeleteResult

    data object Error :
        RemoteSubtaskDeleteResult
}