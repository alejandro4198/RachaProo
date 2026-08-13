package com.example.rachapro.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rachapro.data.local.entity.ActivityEntity
import kotlinx.coroutines.flow.Flow


data class DailyActivityCount(
    val epochDay: Long,
    val count: Int
)
@Dao
interface ActivityDao {

    @Insert(
        onConflict = OnConflictStrategy.ABORT
    )
    suspend fun insertActivity(
        activity: ActivityEntity
    ): Long

    @Query(
        """
        SELECT *
        FROM activities
        WHERE userId = :userId
        AND isDeleted = 0
        ORDER BY
            CASE
                WHEN status = 'COMPLETED' THEN 1
                ELSE 0
            END ASC,
            dueDateEpochDay ASC,
            COALESCE(dueTimeMinutes, 1440) ASC,
            createdAt DESC
        """
    )
    fun observeActivities(
        userId: Long
    ): Flow<List<ActivityEntity>>

    @Query(
        """
        SELECT *
        FROM activities
        WHERE userId = :userId
        AND dueDateEpochDay = :epochDay
        AND isDeleted = 0
        ORDER BY
            CASE
                WHEN status = 'COMPLETED' THEN 1
                ELSE 0
            END ASC,
            COALESCE(dueTimeMinutes, 1440) ASC,
            createdAt DESC
        """
    )
    fun observeActivitiesByDate(
        userId: Long,
        epochDay: Long
    ): Flow<List<ActivityEntity>>

    @Query(
        """
        SELECT *
        FROM activities
        WHERE id = :activityId
        AND userId = :userId
        AND isDeleted = 0
        LIMIT 1
        """
    )
    suspend fun getActivityById(
        activityId: Long,
        userId: Long
    ): ActivityEntity?

    @Query(
        """
        UPDATE activities
        SET
            categoryId = :categoryId,
            title = :title,
            description = :description,
            dueDateEpochDay = :dueDateEpochDay,
            dueTimeMinutes = :dueTimeMinutes,
            priority = :priority,
            repeatRule = :repeatRule,
            updatedAt = :updatedAt
        WHERE id = :activityId
        AND userId = :userId
        AND isDeleted = 0
        """
    )
    suspend fun updateActivity(
        activityId: Long,
        userId: Long,
        categoryId: Long,
        title: String,
        description: String,
        dueDateEpochDay: Long,
        dueTimeMinutes: Int?,
        priority: String,
        repeatRule: String?,
        updatedAt: Long
    ): Int

    @Query(
        """
        UPDATE activities
        SET
            status = 'COMPLETED',
            completedAt = :completedAt,
            completedDateEpochDay = :completedDateEpochDay,
            updatedAt = :updatedAt
        WHERE id = :activityId
        AND userId = :userId
        AND isDeleted = 0
        AND status != 'COMPLETED'
        """
    )
    suspend fun completeActivity(
        activityId: Long,
        userId: Long,
        completedAt: Long,
        completedDateEpochDay: Long,
        updatedAt: Long
    ): Int

    @Query(
        """
        UPDATE activities
        SET
            dueDateEpochDay = :newDueDateEpochDay,
            dueTimeMinutes = :newDueTimeMinutes,
            status = 'PENDING',
            updatedAt = :updatedAt
        WHERE id = :activityId
        AND userId = :userId
        AND isDeleted = 0
        AND status != 'COMPLETED'
        """
    )
    suspend fun rescheduleActivity(
        activityId: Long,
        userId: Long,
        newDueDateEpochDay: Long,
        newDueTimeMinutes: Int?,
        updatedAt: Long
    ): Int

    @Query(
        """
        UPDATE activities
        SET
            isDeleted = 1,
            deletedAt = :deletedAt,
            updatedAt = :updatedAt
        WHERE id = :activityId
        AND userId = :userId
        AND isDeleted = 0
        """
    )
    suspend fun softDeleteActivity(
        activityId: Long,
        userId: Long,
        deletedAt: Long,
        updatedAt: Long
    ): Int

    @Query(
        """
    SELECT DISTINCT completedDateEpochDay
    FROM activities
    WHERE userId = :userId
    AND status = 'COMPLETED'
    AND completedDateEpochDay IS NOT NULL
    ORDER BY completedDateEpochDay ASC
    """
    )
    fun observeCompletedDays(
        userId: Long
    ): Flow<List<Long?>>


    @Query(
        """
    UPDATE activities
    SET
        status = 'OVERDUE',
        updatedAt = :updatedAt
    WHERE userId = :userId
    AND isDeleted = 0
    AND status = 'PENDING'
    AND (
        dueDateEpochDay < :todayEpochDay
        OR (
            dueDateEpochDay = :todayEpochDay
            AND dueTimeMinutes IS NOT NULL
            AND dueTimeMinutes < :currentTimeMinutes
        )
    )
    """
    )
    suspend fun markOverdueActivities(
        userId: Long,
        todayEpochDay: Long,
        currentTimeMinutes: Int,
        updatedAt: Long
    ): Int

    @Query(
        """
    UPDATE activities
    SET
        status = 'PENDING',
        updatedAt = :updatedAt
    WHERE userId = :userId
    AND isDeleted = 0
    AND status = 'OVERDUE'
    AND (
        dueDateEpochDay > :todayEpochDay
        OR (
            dueDateEpochDay = :todayEpochDay
            AND (
                dueTimeMinutes IS NULL
                OR dueTimeMinutes >= :currentTimeMinutes
            )
        )
    )
    """
    )
    suspend fun restorePendingActivities(
        userId: Long,
        todayEpochDay: Long,
        currentTimeMinutes: Int,
        updatedAt: Long
    ): Int

    @Query(
        """
    SELECT COUNT(*)
    FROM activities
    WHERE userId = :userId
      AND status = 'COMPLETED'
      AND isDeleted = 0
    """
    )
    fun observeCompletedActivitiesCount(
        userId: Long
    ): Flow<Int>

    @Query(
        """
    SELECT COUNT(*)
    FROM activities
    WHERE userId = :userId
      AND status = 'COMPLETED'
      AND isDeleted = 0
      AND completedDateEpochDay BETWEEN :startEpochDay AND :endEpochDay
    """
    )
    fun observeCompletedActivitiesCountBetween(
        userId: Long,
        startEpochDay: Long,
        endEpochDay: Long
    ): Flow<Int>

    @Query(
        """
    SELECT
        completedDateEpochDay AS epochDay,
        COUNT(*) AS count
    FROM activities
    WHERE userId = :userId
      AND status = 'COMPLETED'
      AND isDeleted = 0
      AND completedDateEpochDay BETWEEN :startEpochDay AND :endEpochDay
    GROUP BY completedDateEpochDay
    ORDER BY completedDateEpochDay ASC
    """
    )
    fun observeCompletedActivitiesByDay(
        userId: Long,
        startEpochDay: Long,
        endEpochDay: Long
    ): Flow<List<DailyActivityCount>>

}

