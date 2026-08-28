package com.example.rachapro.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rachapro.data.local.entity.PomodoroSessionEntity
import kotlinx.coroutines.flow.Flow


data class DailyPomodoroStats(
    val epochDay: Long,
    val pomodoroCount: Int,
    val focusSeconds: Long
)

@Dao
interface PomodoroSessionDao {

    @Insert(
        onConflict = OnConflictStrategy.ABORT
    )
    suspend fun insertSession(
        session: PomodoroSessionEntity
    ): Long

    @Insert(
        onConflict = OnConflictStrategy.REPLACE
    )
    suspend fun upsertSession(
        session: PomodoroSessionEntity
    )

    @Query(
        """
        SELECT *
        FROM pomodoro_sessions
        WHERE id = :sessionId
          AND userId = :userId
        LIMIT 1
        """
    )
    suspend fun getSessionById(
        sessionId: Long,
        userId: Long
    ): PomodoroSessionEntity?

    @Query(
        """
        SELECT *
        FROM pomodoro_sessions
        WHERE userId = :userId
          AND status IN ('RUNNING', 'PAUSED')
        ORDER BY startedAtMillis DESC
        LIMIT 1
        """
    )
    suspend fun getActiveSession(
        userId: Long
    ): PomodoroSessionEntity?

    @Query(
        """
        SELECT *
        FROM pomodoro_sessions
        WHERE userId = :userId
          AND status IN ('RUNNING', 'PAUSED')
        ORDER BY startedAtMillis DESC
        LIMIT 1
        """
    )
    fun observeActiveSession(
        userId: Long
    ): Flow<PomodoroSessionEntity?>


    @Query(
        """
        UPDATE pomodoro_sessions
        SET status = 'PAUSED',
            pausedAtMillis = :pausedAtMillis,
            updatedAt = :updatedAt
        WHERE id = :sessionId
          AND userId = :userId
          AND status = 'RUNNING'
        """
    )
    suspend fun pauseSession(
        sessionId: Long,
        userId: Long,
        pausedAtMillis: Long,
        updatedAt: Long
    ): Int


    @Query(
        """
        UPDATE pomodoro_sessions
        SET status = 'RUNNING',
            totalPausedMillis =
                totalPausedMillis +
                (:resumedAtMillis - pausedAtMillis),
            pausedAtMillis = NULL,
            updatedAt = :updatedAt
        WHERE id = :sessionId
          AND userId = :userId
          AND status = 'PAUSED'
          AND pausedAtMillis IS NOT NULL
        """
    )
    suspend fun resumeSession(
        sessionId: Long,
        userId: Long,
        resumedAtMillis: Long,
        updatedAt: Long
    ): Int


    @Query(
        """
        UPDATE pomodoro_sessions
        SET status = 'COMPLETED',
            completedAtMillis = :completedAtMillis,
            completedDateEpochDay = :completedDateEpochDay,
            pausedAtMillis = NULL,
            updatedAt = :updatedAt
        WHERE id = :sessionId
          AND userId = :userId
          AND status = 'RUNNING'
        """
    )
    suspend fun completeSession(
        sessionId: Long,
        userId: Long,
        completedAtMillis: Long,
        completedDateEpochDay: Long,
        updatedAt: Long
    ): Int

    @Query(
        """
        UPDATE pomodoro_sessions
        SET status = 'CANCELLED',
            pausedAtMillis = NULL,
            updatedAt = :updatedAt
        WHERE id = :sessionId
          AND userId = :userId
          AND status IN ('RUNNING', 'PAUSED')
        """
    )
    suspend fun cancelSession(
        sessionId: Long,
        userId: Long,
        updatedAt: Long
    ): Int

    @Query(
        """
        SELECT *
        FROM pomodoro_sessions
        WHERE userId = :userId
        ORDER BY startedAtMillis DESC
        """
    )
    fun observeSessions(
        userId: Long
    ): Flow<List<PomodoroSessionEntity>>

    @Query(
        """
        SELECT *
        FROM pomodoro_sessions
        WHERE userId = :userId
          AND type = 'FOCUS'
          AND status = 'COMPLETED'
        ORDER BY completedAtMillis DESC
        """
    )
    fun observeCompletedFocusSessions(
        userId: Long
    ): Flow<List<PomodoroSessionEntity>>

    @Query(
        """
        SELECT DISTINCT completedDateEpochDay
        FROM pomodoro_sessions
        WHERE userId = :userId
          AND type = 'FOCUS'
          AND status = 'COMPLETED'
          AND completedDateEpochDay IS NOT NULL
        ORDER BY completedDateEpochDay ASC
        """
    )
    fun observeCompletedFocusDays(
        userId: Long
    ): Flow<List<Long>>

    @Query(
        """
    SELECT COUNT(*)
    FROM pomodoro_sessions
    WHERE userId = :userId
      AND type = 'FOCUS'
      AND status = 'COMPLETED'
    """
    )
    suspend fun countCompletedFocusSessions(
        userId: Long
    ): Int

    @Query(
        """
    SELECT COUNT(*)
    FROM pomodoro_sessions
    WHERE userId = :userId
      AND type = 'FOCUS'
      AND status = 'COMPLETED'
    """
    )
    fun observeCompletedFocusCount(
        userId: Long
    ): Flow<Int>

    @Query(
        """
    SELECT COALESCE(
        SUM(plannedDurationSeconds),
        0
    )
    FROM pomodoro_sessions
    WHERE userId = :userId
      AND type = 'FOCUS'
      AND status = 'COMPLETED'
    """
    )
    fun observeCompletedFocusSeconds(
        userId: Long
    ): Flow<Long>

    @Query(
        """
    SELECT COUNT(*)
    FROM pomodoro_sessions
    WHERE userId = :userId
      AND type = 'FOCUS'
      AND status = 'COMPLETED'
      AND completedDateEpochDay BETWEEN :startEpochDay AND :endEpochDay
    """
    )
    fun observeCompletedFocusCountBetween(
        userId: Long,
        startEpochDay: Long,
        endEpochDay: Long
    ): Flow<Int>

    @Query(
        """
    SELECT COALESCE(
        SUM(plannedDurationSeconds),
        0
    )
    FROM pomodoro_sessions
    WHERE userId = :userId
      AND type = 'FOCUS'
      AND status = 'COMPLETED'
      AND completedDateEpochDay BETWEEN :startEpochDay AND :endEpochDay
    """
    )
    fun observeCompletedFocusSecondsBetween(
        userId: Long,
        startEpochDay: Long,
        endEpochDay: Long
    ): Flow<Long>

    @Query(
        """
    SELECT
        completedDateEpochDay AS epochDay,
        COUNT(*) AS pomodoroCount,
        COALESCE(
            SUM(plannedDurationSeconds),
            0
        ) AS focusSeconds
    FROM pomodoro_sessions
    WHERE userId = :userId
      AND type = 'FOCUS'
      AND status = 'COMPLETED'
      AND completedDateEpochDay BETWEEN :startEpochDay AND :endEpochDay
    GROUP BY completedDateEpochDay
    ORDER BY completedDateEpochDay ASC
    """
    )
    fun observeCompletedFocusStatsByDay(
        userId: Long,
        startEpochDay: Long,
        endEpochDay: Long
    ): Flow<List<DailyPomodoroStats>>

}