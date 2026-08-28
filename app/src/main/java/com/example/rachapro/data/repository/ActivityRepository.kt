package com.example.rachapro.data.repository

import com.example.rachapro.data.local.dao.ActivityDao
import com.example.rachapro.data.local.dao.CategoryDao
import com.example.rachapro.data.local.dao.DailyActivityCount
import com.example.rachapro.data.local.entity.ActivityEntity
import com.example.rachapro.data.local.entity.ActivityPriority
import com.example.rachapro.data.local.entity.ActivityStatus
import com.example.rachapro.network.ApiService
import com.example.rachapro.network.dto.ActivityResponse
import com.example.rachapro.network.dto.CreateActivityRequest
import com.example.rachapro.network.dto.UpdateActivityRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException

class ActivityRepository(
    private val activityDao: ActivityDao,
    private val categoryDao: CategoryDao,
    private val apiService: ApiService
) {

    fun observeActivities(
        userId: Long
    ): Flow<List<ActivityEntity>> {

        return activityDao
            .observeActivities(
                userId = userId
            )
            .distinctUntilChanged()
    }

    fun observeActivitiesByDate(
        userId: Long,
        epochDay: Long
    ): Flow<List<ActivityEntity>> {

        return activityDao
            .observeActivitiesByDate(
                userId = userId,
                epochDay = epochDay
            )
            .distinctUntilChanged()
    }

    fun observeCompletedDays(
        userId: Long
    ): Flow<List<Long>> {

        return activityDao
            .observeCompletedDays(
                userId = userId
            )
            .map { days ->
                days
                    .filterNotNull()
                    .distinct()
                    .sorted()
            }
            .distinctUntilChanged()
    }

    suspend fun refreshActivityStatuses(
        userId: Long,
        todayEpochDay: Long,
        currentTimeMinutes: Int
    ): ActivityStatusRefreshResult {

        if (currentTimeMinutes !in 0..1439) {
            return ActivityStatusRefreshResult.Error
        }

        return try {

            val currentTime =
                System.currentTimeMillis()

            val markedOverdue =
                activityDao.markOverdueActivities(
                    userId = userId,
                    todayEpochDay = todayEpochDay,
                    currentTimeMinutes = currentTimeMinutes,
                    updatedAt = currentTime
                )

            val restoredPending =
                activityDao.restorePendingActivities(
                    userId = userId,
                    todayEpochDay = todayEpochDay,
                    currentTimeMinutes = currentTimeMinutes,
                    updatedAt = currentTime
                )

            ActivityStatusRefreshResult.Success(
                markedOverdue = markedOverdue,
                restoredPending = restoredPending
            )

        } catch (_: Exception) {

            ActivityStatusRefreshResult.Error
        }
    }

    suspend fun getActivityById(
        activityId: Long,
        userId: Long
    ): ActivityEntity? {

        return activityDao.getActivityById(
            activityId = activityId,
            userId = userId
        )
    }

    suspend fun createActivity(
        userId: Long,
        categoryId: Long,
        title: String,
        description: String,
        dueDateEpochDay: Long,
        dueTimeMinutes: Int?,
        priority: String,
        repeatRule: String? = null
    ): ActivityCreateResult {

        val normalizedTitle =
            title.trim()

        val normalizedDescription =
            description.trim()

        if (normalizedTitle.isBlank()) {

            return ActivityCreateResult.InvalidData(
                message =
                    "El título de la actividad es obligatorio."
            )
        }

        if (
            dueTimeMinutes != null &&
            dueTimeMinutes !in 0..1439
        ) {

            return ActivityCreateResult.InvalidData(
                message =
                    "La hora seleccionada no es válida."
            )
        }

        if (!isValidPriority(priority)) {

            return ActivityCreateResult.InvalidData(
                message =
                    "La prioridad seleccionada no es válida."
            )
        }

        return try {

            val category =
                categoryDao.getCategoryById(
                    categoryId = categoryId,
                    userId = userId
                )

            if (category == null) {

                ActivityCreateResult.CategoryNotFound

            } else {

                val currentTime =
                    System.currentTimeMillis()

                val activity =
                    ActivityEntity(
                        userId = userId,
                        categoryId = categoryId,
                        title = normalizedTitle,
                        description = normalizedDescription,
                        dueDateEpochDay = dueDateEpochDay,
                        dueTimeMinutes = dueTimeMinutes,
                        priority = priority,
                        status = ActivityStatus.PENDING,
                        repeatRule = repeatRule,
                        createdAt = currentTime,
                        updatedAt = currentTime
                    )

                val activityId =
                    activityDao.insertActivity(
                        activity = activity
                    )

                ActivityCreateResult.Success(
                    activityId = activityId
                )
            }

        } catch (_: Exception) {

            ActivityCreateResult.Error
        }
    }

    suspend fun updateActivity(
        activityId: Long,
        userId: Long,
        categoryId: Long,
        title: String,
        description: String,
        dueDateEpochDay: Long,
        dueTimeMinutes: Int?,
        priority: String,
        repeatRule: String? = null
    ): ActivityOperationResult {

        val normalizedTitle =
            title.trim()

        val normalizedDescription =
            description.trim()

        if (normalizedTitle.isBlank()) {
            return ActivityOperationResult.InvalidData
        }

        if (
            dueTimeMinutes != null &&
            dueTimeMinutes !in 0..1439
        ) {
            return ActivityOperationResult.InvalidData
        }

        if (!isValidPriority(priority)) {
            return ActivityOperationResult.InvalidData
        }

        return try {

            val category =
                categoryDao.getCategoryById(
                    categoryId = categoryId,
                    userId = userId
                )

            if (category == null) {

                ActivityOperationResult.InvalidData

            } else {

                val currentTime =
                    System.currentTimeMillis()

                val rowsAffected =
                    activityDao.updateActivity(
                        activityId = activityId,
                        userId = userId,
                        categoryId = categoryId,
                        title = normalizedTitle,
                        description = normalizedDescription,
                        dueDateEpochDay = dueDateEpochDay,
                        dueTimeMinutes = dueTimeMinutes,
                        priority = priority,
                        repeatRule = repeatRule,
                        updatedAt = currentTime
                    )

                resultFromRowsAffected(
                    rowsAffected = rowsAffected
                )
            }

        } catch (_: Exception) {

            ActivityOperationResult.Error
        }
    }

    suspend fun completeActivity(
        activityId: Long,
        userId: Long,
        completedDateEpochDay: Long
    ): ActivityOperationResult {

        return try {

            val currentTime =
                System.currentTimeMillis()

            val rowsAffected =
                activityDao.completeActivity(
                    activityId = activityId,
                    userId = userId,
                    completedAt = currentTime,
                    completedDateEpochDay =
                        completedDateEpochDay,
                    updatedAt = currentTime
                )

            resultFromRowsAffected(
                rowsAffected = rowsAffected
            )

        } catch (_: Exception) {

            ActivityOperationResult.Error
        }
    }

    suspend fun rescheduleActivity(
        activityId: Long,
        userId: Long,
        newDueDateEpochDay: Long,
        newDueTimeMinutes: Int?
    ): ActivityOperationResult {

        if (
            newDueTimeMinutes != null &&
            newDueTimeMinutes !in 0..1439
        ) {
            return ActivityOperationResult.InvalidData
        }

        return try {

            val currentTime =
                System.currentTimeMillis()

            val rowsAffected =
                activityDao.rescheduleActivity(
                    activityId = activityId,
                    userId = userId,
                    newDueDateEpochDay =
                        newDueDateEpochDay,
                    newDueTimeMinutes =
                        newDueTimeMinutes,
                    updatedAt = currentTime
                )

            resultFromRowsAffected(
                rowsAffected = rowsAffected
            )

        } catch (_: Exception) {

            ActivityOperationResult.Error
        }
    }

    suspend fun softDeleteActivity(
        activityId: Long,
        userId: Long
    ): ActivityOperationResult {

        return try {

            val currentTime =
                System.currentTimeMillis()

            val rowsAffected =
                activityDao.softDeleteActivity(
                    activityId = activityId,
                    userId = userId,
                    deletedAt = currentTime,
                    updatedAt = currentTime
                )

            resultFromRowsAffected(
                rowsAffected = rowsAffected
            )

        } catch (_: Exception) {

            ActivityOperationResult.Error
        }
    }

    private fun isValidPriority(
        priority: String
    ): Boolean {

        return priority == ActivityPriority.LOW ||
                priority == ActivityPriority.MEDIUM ||
                priority == ActivityPriority.HIGH
    }

    private fun resultFromRowsAffected(
        rowsAffected: Int
    ): ActivityOperationResult {

        return if (rowsAffected > 0) {

            ActivityOperationResult.Success

        } else {

            ActivityOperationResult.NotFoundOrNotAllowed
        }
    }

    fun observeCompletedActivitiesCount(
        userId: Long
    ): Flow<Int> {

        return activityDao
            .observeCompletedActivitiesCount(
                userId = userId
            )
    }

    fun observeCompletedActivitiesCountBetween(
        userId: Long,
        startEpochDay: Long,
        endEpochDay: Long
    ): Flow<Int> {

        return activityDao
            .observeCompletedActivitiesCountBetween(
                userId = userId,
                startEpochDay = startEpochDay,
                endEpochDay = endEpochDay
            )
    }

    fun observeCompletedActivitiesByDay(
        userId: Long,
        startEpochDay: Long,
        endEpochDay: Long
    ): Flow<List<DailyActivityCount>> {

        return activityDao
            .observeCompletedActivitiesByDay(
                userId = userId,
                startEpochDay = startEpochDay,
                endEpochDay = endEpochDay
            )
    }

    suspend fun fetchRemoteActivities(): RemoteActivitiesResult {

        return try {

            val activities =
                apiService.getActivities()

            RemoteActivitiesResult.Success(
                activities = activities
            )

        } catch (exception: HttpException) {

            if (exception.code() == 401) {
                RemoteActivitiesResult.Unauthorized
            } else {
                RemoteActivitiesResult.Error
            }

        } catch (_: IOException) {

            RemoteActivitiesResult.Error

        } catch (_: Exception) {

            RemoteActivitiesResult.Error
        }
    }

    suspend fun refreshRemoteActivityStatuses(): RemoteActivitiesResult {

        return try {

            val activities =
                apiService.refreshActivityStatuses()

            RemoteActivitiesResult.Success(
                activities = activities
            )

        } catch (exception: HttpException) {

            if (exception.code() == 401) {
                RemoteActivitiesResult.Unauthorized
            } else {
                RemoteActivitiesResult.Error
            }

        } catch (_: IOException) {

            RemoteActivitiesResult.Error

        } catch (_: Exception) {

            RemoteActivitiesResult.Error
        }
    }

    suspend fun createRemoteActivity(
        categoryId: Long,
        title: String,
        description: String,
        dueDateEpochDay: Long,
        dueTimeMinutes: Int?,
        priority: String,
        repeatRule: String? = null
    ): RemoteActivityOperationResult {

        return try {

            val activity =
                apiService.createActivity(
                    CreateActivityRequest(
                        categoryId = categoryId,
                        title = title.trim(),
                        description = description.trim(),
                        dueDateEpochDay = dueDateEpochDay,
                        dueTimeMinutes = dueTimeMinutes,
                        priority = priority,
                        repeatRule = repeatRule
                    )
                )

            RemoteActivityOperationResult.Success(
                activity = activity
            )

        } catch (exception: HttpException) {

            when (exception.code()) {

                400 ->
                    RemoteActivityOperationResult.InvalidData

                401 ->
                    RemoteActivityOperationResult.Unauthorized

                404 ->
                    RemoteActivityOperationResult.NotFound

                else ->
                    RemoteActivityOperationResult.Error
            }

        } catch (_: IOException) {

            RemoteActivityOperationResult.Error

        } catch (_: Exception) {

            RemoteActivityOperationResult.Error
        }
    }

    suspend fun updateRemoteActivity(
        activityId: Long,
        categoryId: Long,
        title: String,
        description: String,
        dueDateEpochDay: Long,
        dueTimeMinutes: Int?,
        priority: String,
        repeatRule: String? = null
    ): RemoteActivityOperationResult {

        return try {

            val activity =
                apiService.updateActivity(
                    activityId = activityId,
                    request = UpdateActivityRequest(
                        categoryId = categoryId,
                        title = title.trim(),
                        description = description.trim(),
                        dueDateEpochDay = dueDateEpochDay,
                        dueTimeMinutes = dueTimeMinutes,
                        priority = priority,
                        repeatRule = repeatRule
                    )
                )

            RemoteActivityOperationResult.Success(
                activity = activity
            )

        } catch (exception: HttpException) {

            when (exception.code()) {

                400 ->
                    RemoteActivityOperationResult.InvalidData

                401 ->
                    RemoteActivityOperationResult.Unauthorized

                404 ->
                    RemoteActivityOperationResult.NotFound

                else ->
                    RemoteActivityOperationResult.Error
            }

        } catch (_: IOException) {

            RemoteActivityOperationResult.Error

        } catch (_: Exception) {

            RemoteActivityOperationResult.Error
        }
    }

    suspend fun completeRemoteActivity(
        activityId: Long
    ): RemoteActivityOperationResult {

        return try {

            val activity =
                apiService.completeActivity(
                    activityId = activityId
                )

            RemoteActivityOperationResult.Success(
                activity = activity
            )

        } catch (exception: HttpException) {

            when (exception.code()) {

                401 ->
                    RemoteActivityOperationResult.Unauthorized

                404 ->
                    RemoteActivityOperationResult.NotFound

                else ->
                    RemoteActivityOperationResult.Error
            }

        } catch (_: IOException) {

            RemoteActivityOperationResult.Error

        } catch (_: Exception) {

            RemoteActivityOperationResult.Error
        }
    }

    suspend fun deleteRemoteActivity(
        activityId: Long
    ): RemoteActivityDeleteResult {

        return try {

            val response =
                apiService.deleteActivity(
                    activityId = activityId
                )

            when (response.code()) {

                204 ->
                    RemoteActivityDeleteResult.Success

                401 ->
                    RemoteActivityDeleteResult.Unauthorized

                404 ->
                    RemoteActivityDeleteResult.NotFound

                else ->
                    RemoteActivityDeleteResult.Error
            }

        } catch (_: IOException) {

            RemoteActivityDeleteResult.Error

        } catch (_: Exception) {

            RemoteActivityDeleteResult.Error
        }
    }
}

sealed interface ActivityCreateResult {

    data class Success(
        val activityId: Long
    ) : ActivityCreateResult

    data class InvalidData(
        val message: String
    ) : ActivityCreateResult

    data object CategoryNotFound :
        ActivityCreateResult

    data object Error :
        ActivityCreateResult
}

sealed interface ActivityOperationResult {

    data object Success :
        ActivityOperationResult

    data object NotFoundOrNotAllowed :
        ActivityOperationResult

    data object InvalidData :
        ActivityOperationResult

    data object Error :
        ActivityOperationResult
}

sealed interface ActivityStatusRefreshResult {

    data class Success(
        val markedOverdue: Int,
        val restoredPending: Int
    ) : ActivityStatusRefreshResult

    data object Error :
        ActivityStatusRefreshResult
}

sealed interface RemoteActivitiesResult {

    data class Success(
        val activities: List<ActivityResponse>
    ) : RemoteActivitiesResult

    data object Unauthorized :
        RemoteActivitiesResult

    data object Error :
        RemoteActivitiesResult
}

sealed interface RemoteActivityOperationResult {

    data class Success(
        val activity: ActivityResponse
    ) : RemoteActivityOperationResult

    data object InvalidData :
        RemoteActivityOperationResult

    data object NotFound :
        RemoteActivityOperationResult

    data object Unauthorized :
        RemoteActivityOperationResult

    data object Error :
        RemoteActivityOperationResult
}

sealed interface RemoteActivityDeleteResult {

    data object Success :
        RemoteActivityDeleteResult

    data object NotFound :
        RemoteActivityDeleteResult

    data object Unauthorized :
        RemoteActivityDeleteResult

    data object Error :
        RemoteActivityDeleteResult
}