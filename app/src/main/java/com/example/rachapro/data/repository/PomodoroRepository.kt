package com.example.rachapro.data.repository

import com.example.rachapro.data.local.dao.ActivityDao
import com.example.rachapro.data.local.dao.PomodoroSessionDao
import com.example.rachapro.data.local.entity.PomodoroSessionEntity
import com.example.rachapro.data.local.entity.PomodoroSessionStatus
import com.example.rachapro.data.local.entity.PomodoroSessionType
import kotlinx.coroutines.flow.Flow
import java.time.Instant
import java.time.ZoneId
import com.example.rachapro.data.local.dao.DailyPomodoroStats


class PomodoroRepository(
    private val pomodoroSessionDao: PomodoroSessionDao,
    private val activityDao: ActivityDao
) {

    fun observeActiveSession(
        userId: Long
    ): Flow<PomodoroSessionEntity?> {

        return pomodoroSessionDao
            .observeActiveSession(
                userId = userId
            )
    }

    suspend fun getActiveSession(
        userId: Long
    ): PomodoroSessionEntity? {

        return pomodoroSessionDao
            .getActiveSession(
                userId = userId
            )
    }

    fun observeSessions(
        userId: Long
    ): Flow<List<PomodoroSessionEntity>> {

        return pomodoroSessionDao
            .observeSessions(
                userId = userId
            )
    }

    fun observeCompletedFocusDays(
        userId: Long
    ): Flow<List<Long>> {

        return pomodoroSessionDao
            .observeCompletedFocusDays(
                userId = userId
            )
    }

    suspend fun startSession(
        userId: Long,
        activityId: Long? = null,
        type: String,
        plannedDurationSeconds: Int
    ): PomodoroStartResult {

        if (
            userId <= 0L ||
            plannedDurationSeconds <= 0
        ) {

            return PomodoroStartResult.InvalidData(
                message =
                    "Los datos de la sesión no son válidos."
            )
        }

        if (
            type !in setOf(
                PomodoroSessionType.FOCUS,
                PomodoroSessionType.SHORT_BREAK,
                PomodoroSessionType.LONG_BREAK
            )
        ) {

            return PomodoroStartResult.InvalidData(
                message =
                    "El tipo de sesión no es válido."
            )
        }

        val activeSession =
            pomodoroSessionDao
                .getActiveSession(
                    userId = userId
                )

        if (activeSession != null) {

            return PomodoroStartResult
                .ActiveSessionAlreadyExists
        }

        if (activityId != null) {

            val activity =
                activityDao
                    .getActivityById(
                        activityId = activityId,
                        userId = userId
                    )

            if (
                activity == null ||
                activity.isDeleted
            ) {

                return PomodoroStartResult
                    .ActivityNotFoundOrNotAllowed
            }
        }

        val now =
            System.currentTimeMillis()

        return try {

            val sessionId =
                pomodoroSessionDao
                    .insertSession(
                        PomodoroSessionEntity(
                            userId = userId,
                            activityId = activityId,
                            type = type,
                            plannedDurationSeconds =
                                plannedDurationSeconds,
                            status =
                                PomodoroSessionStatus.RUNNING,
                            startedAtMillis = now,
                            createdAt = now,
                            updatedAt = now
                        )
                    )

            PomodoroStartResult.Success(
                sessionId = sessionId
            )

        } catch (_: Exception) {

            PomodoroStartResult.Error
        }
    }

    suspend fun pauseSession(
        sessionId: Long,
        userId: Long
    ): PomodoroOperationResult {

        val now =
            System.currentTimeMillis()

        return try {

            val rowsAffected =
                pomodoroSessionDao
                    .pauseSession(
                        sessionId = sessionId,
                        userId = userId,
                        pausedAtMillis = now,
                        updatedAt = now
                    )

            resultFromRowsAffected(
                rowsAffected
            )

        } catch (_: Exception) {

            PomodoroOperationResult.Error
        }
    }

    suspend fun resumeSession(
        sessionId: Long,
        userId: Long
    ): PomodoroOperationResult {

        val now =
            System.currentTimeMillis()

        return try {

            val rowsAffected =
                pomodoroSessionDao
                    .resumeSession(
                        sessionId = sessionId,
                        userId = userId,
                        resumedAtMillis = now,
                        updatedAt = now
                    )

            resultFromRowsAffected(
                rowsAffected
            )

        } catch (_: Exception) {

            PomodoroOperationResult.Error
        }
    }

    suspend fun completeSession(
        sessionId: Long,
        userId: Long
    ): PomodoroCompleteResult {

        val session =
            try {

                pomodoroSessionDao
                    .getSessionById(
                        sessionId = sessionId,
                        userId = userId
                    )

            } catch (_: Exception) {

                return PomodoroCompleteResult.Error
            }

        if (session == null) {

            return PomodoroCompleteResult
                .NotFoundOrNotAllowed
        }

        if (
            session.status !=
            PomodoroSessionStatus.RUNNING
        ) {

            return PomodoroCompleteResult
                .InvalidState
        }

        val now =
            System.currentTimeMillis()

        if (
            calculateRemainingMillis(
                session = session,
                nowMillis = now
            ) > 0L
        ) {

            return PomodoroCompleteResult
                .TimeRemaining
        }

        val completedDateEpochDay =
            Instant
                .ofEpochMilli(now)
                .atZone(
                    ZoneId.systemDefault()
                )
                .toLocalDate()
                .toEpochDay()

        return try {

            val rowsAffected =
                pomodoroSessionDao
                    .completeSession(
                        sessionId = sessionId,
                        userId = userId,
                        completedAtMillis = now,
                        completedDateEpochDay =
                            completedDateEpochDay,
                        updatedAt = now
                    )

            if (rowsAffected > 0) {

                PomodoroCompleteResult.Success

            } else {

                PomodoroCompleteResult
                    .InvalidState
            }

        } catch (_: Exception) {

            PomodoroCompleteResult.Error
        }
    }

    suspend fun cancelSession(
        sessionId: Long,
        userId: Long
    ): PomodoroOperationResult {

        val now =
            System.currentTimeMillis()

        return try {

            val rowsAffected =
                pomodoroSessionDao
                    .cancelSession(
                        sessionId = sessionId,
                        userId = userId,
                        updatedAt = now
                    )

            resultFromRowsAffected(
                rowsAffected
            )

        } catch (_: Exception) {

            PomodoroOperationResult.Error
        }
    }

    fun calculateRemainingMillis(
        session: PomodoroSessionEntity,
        nowMillis: Long =
            System.currentTimeMillis()
    ): Long {

        val plannedMillis =
            session.plannedDurationSeconds *
                    1000L

        val effectiveNow =
            if (
                session.status ==
                PomodoroSessionStatus.PAUSED
            ) {

                session.pausedAtMillis
                    ?: nowMillis

            } else {

                nowMillis
            }

        val elapsedMillis =
            (
                    effectiveNow -
                            session.startedAtMillis -
                            session.totalPausedMillis
                    )
                .coerceAtLeast(0L)

        return (
                plannedMillis -
                        elapsedMillis
                )
            .coerceAtLeast(0L)
    }

    private fun resultFromRowsAffected(
        rowsAffected: Int
    ): PomodoroOperationResult {

        return if (rowsAffected > 0) {

            PomodoroOperationResult.Success

        } else {

            PomodoroOperationResult
                .NotFoundOrInvalidState
        }
    }

    suspend fun countCompletedFocusSessions(
        userId: Long
    ): Int {

        return try {

            pomodoroSessionDao
                .countCompletedFocusSessions(
                    userId = userId
                )

        } catch (_: Exception) {

            0
        }
    }

    fun observeCompletedFocusCount(
        userId: Long
    ): Flow<Int> {

        return pomodoroSessionDao
            .observeCompletedFocusCount(
                userId = userId
            )
    }

    fun observeCompletedFocusSeconds(
        userId: Long
    ): Flow<Long> {

        return pomodoroSessionDao
            .observeCompletedFocusSeconds(
                userId = userId
            )
    }

    fun observeCompletedFocusCountBetween(
        userId: Long,
        startEpochDay: Long,
        endEpochDay: Long
    ): Flow<Int> {

        return pomodoroSessionDao
            .observeCompletedFocusCountBetween(
                userId = userId,
                startEpochDay = startEpochDay,
                endEpochDay = endEpochDay
            )
    }

    fun observeCompletedFocusSecondsBetween(
        userId: Long,
        startEpochDay: Long,
        endEpochDay: Long
    ): Flow<Long> {

        return pomodoroSessionDao
            .observeCompletedFocusSecondsBetween(
                userId = userId,
                startEpochDay = startEpochDay,
                endEpochDay = endEpochDay
            )
    }

    fun observeCompletedFocusStatsByDay(
        userId: Long,
        startEpochDay: Long,
        endEpochDay: Long
    ): Flow<List<DailyPomodoroStats>> {

        return pomodoroSessionDao
            .observeCompletedFocusStatsByDay(
                userId = userId,
                startEpochDay = startEpochDay,
                endEpochDay = endEpochDay
            )
    }


}

sealed interface PomodoroStartResult {

    data class Success(
        val sessionId: Long
    ) : PomodoroStartResult

    data class InvalidData(
        val message: String
    ) : PomodoroStartResult

    data object ActiveSessionAlreadyExists :
        PomodoroStartResult

    data object ActivityNotFoundOrNotAllowed :
        PomodoroStartResult

    data object Error :
        PomodoroStartResult
}

sealed interface PomodoroOperationResult {

    data object Success :
        PomodoroOperationResult

    data object NotFoundOrInvalidState :
        PomodoroOperationResult

    data object Error :
        PomodoroOperationResult
}

sealed interface PomodoroCompleteResult {

    data object Success :
        PomodoroCompleteResult

    data object TimeRemaining :
        PomodoroCompleteResult

    data object InvalidState :
        PomodoroCompleteResult

    data object NotFoundOrNotAllowed :
        PomodoroCompleteResult

    data object Error :
        PomodoroCompleteResult
}