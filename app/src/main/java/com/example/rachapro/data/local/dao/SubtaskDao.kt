package com.example.rachapro.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rachapro.data.local.entity.SubtaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SubtaskDao {


    @Insert(
        onConflict = OnConflictStrategy.ABORT
    )
    suspend fun insertSubtask(
        subtask: SubtaskEntity
    ): Long


    @Query(
        """
        SELECT *
        FROM subtasks
        WHERE activityId = :activityId
        ORDER BY
            isCompleted ASC,
            createdAt ASC,
            id ASC
        """
    )
    fun observeSubtasksByActivity(
        activityId: Long
    ): Flow<List<SubtaskEntity>>

    @Query(
        """
        SELECT *
        FROM subtasks
        WHERE id = :subtaskId
        AND activityId = :activityId
        LIMIT 1
        """
    )
    suspend fun getSubtaskById(
        subtaskId: Long,
        activityId: Long
    ): SubtaskEntity?


    @Query(
        """
        UPDATE subtasks
        SET
            title = :title,
            updatedAt = :updatedAt
        WHERE id = :subtaskId
        AND activityId = :activityId
        """
    )
    suspend fun updateSubtaskTitle(
        subtaskId: Long,
        activityId: Long,
        title: String,
        updatedAt: Long
    ): Int


    @Query(
        """
        UPDATE subtasks
        SET
            isCompleted = :isCompleted,
            completedAt = :completedAt,
            updatedAt = :updatedAt
        WHERE id = :subtaskId
        AND activityId = :activityId
        """
    )
    suspend fun setSubtaskCompleted(
        subtaskId: Long,
        activityId: Long,
        isCompleted: Boolean,
        completedAt: Long?,
        updatedAt: Long
    ): Int


    @Query(
        """
        DELETE FROM subtasks
        WHERE id = :subtaskId
        AND activityId = :activityId
        """
    )
    suspend fun deleteSubtask(
        subtaskId: Long,
        activityId: Long
    ): Int
}