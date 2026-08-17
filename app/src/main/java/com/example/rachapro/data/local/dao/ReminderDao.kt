package com.example.rachapro.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rachapro.data.local.entity.ReminderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {


    @Insert(
        onConflict = OnConflictStrategy.ABORT
    )
    suspend fun insertReminder(
        reminder: ReminderEntity
    ): Long


    @Query(
        """
        SELECT *
        FROM reminders
        WHERE userId = :userId
        ORDER BY
            triggerAtMillis ASC,
            id ASC
        """
    )
    fun observeReminders(
        userId: Long
    ): Flow<List<ReminderEntity>>


    @Query(
        """
        SELECT *
        FROM reminders
        WHERE userId = :userId
        AND activityId = :activityId
        ORDER BY
            triggerAtMillis ASC,
            id ASC
        """
    )
    fun observeRemindersByActivity(
        userId: Long,
        activityId: Long
    ): Flow<List<ReminderEntity>>

    @Query(
        """
        SELECT *
        FROM reminders
        WHERE id = :reminderId
        AND userId = :userId
        LIMIT 1
        """
    )
    suspend fun getReminderById(
        reminderId: Long,
        userId: Long
    ): ReminderEntity?


    @Query(
        """
        SELECT *
        FROM reminders
        WHERE userId = :userId
        AND status = 'SCHEDULED'
        ORDER BY
            triggerAtMillis ASC,
            id ASC
        """
    )
    suspend fun getScheduledReminders(
        userId: Long
    ): List<ReminderEntity>


    @Query(
        """
        UPDATE reminders
        SET
            status = 'CANCELLED',
            updatedAt = :updatedAt
        WHERE id = :reminderId
        AND userId = :userId
        AND status = 'SCHEDULED'
        """
    )
    suspend fun cancelReminder(
        reminderId: Long,
        userId: Long,
        updatedAt: Long
    ): Int


    @Query(
        """
        UPDATE reminders
        SET
            status = 'DELIVERED',
            deliveredAt = :deliveredAt,
            updatedAt = :updatedAt
        WHERE id = :reminderId
        AND userId = :userId
        AND status = 'SCHEDULED'
        """
    )
    suspend fun markReminderDelivered(
        reminderId: Long,
        userId: Long,
        deliveredAt: Long,
        updatedAt: Long
    ): Int
}