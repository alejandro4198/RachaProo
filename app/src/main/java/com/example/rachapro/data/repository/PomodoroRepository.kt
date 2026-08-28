package com.example.rachapro.data.repository

import com.example.rachapro.data.local.dao.ActivityDao
import com.example.rachapro.data.local.dao.DailyPomodoroStats
import com.example.rachapro.data.local.dao.PomodoroSessionDao
import com.example.rachapro.data.local.entity.PomodoroSessionEntity
import com.example.rachapro.data.local.entity.PomodoroSessionStatus
import com.example.rachapro.data.local.entity.PomodoroSessionType
import com.example.rachapro.network.ApiService
import com.example.rachapro.network.dto.CreatePomodoroSessionRequest
import com.example.rachapro.network.dto.PomodoroSessionResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException

class PomodoroRepository(
    private val pomodoroSessionDao: PomodoroSessionDao,
    private val activityDao: ActivityDao,
    private val apiService: ApiService
) {

    fun observeActiveSession(
        userId: Long
    ): Flow<PomodoroSessionEntity?> {
        return pomodoroSessionDao.observeActiveSession(userId)
    }

    suspend fun getActiveSession(
        userId: Long
    ): PomodoroSessionEntity? {
        return pomodoroSessionDao.getActiveSession(userId)
    }

    fun observeSessions(
        userId: Long
    ): Flow<List<PomodoroSessionEntity>> {
        return pomodoroSessionDao.observeSessions(userId)
    }

    fun observeCompletedFocusDays(
        userId: Long
    ): Flow<List<Long>> {
        return pomodoroSessionDao.observeCompletedFocusDays(userId)
    }

    suspend fun syncRemoteSessions(
        userId: Long
    ): PomodoroSyncResult {
        return try {
            val sessions = apiService.getPomodoroSessions()

            sessions.forEach { response ->
                pomodoroSessionDao.upsertSession(
                    response.toEntity(userId)
                )
            }

            PomodoroSyncResult.Success
        } catch (e: HttpException) {
            if (e.code() == 401) {
                PomodoroSyncResult.Unauthorized
            } else {
                PomodoroSyncResult.Error
            }
        } catch (_: Exception) {
            PomodoroSyncResult.Error
        }
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
                "El tipo de sesión no es válido."
            )
        }

        return try {
            val remoteSessions =
                apiService.getPomodoroSessions()

            remoteSessions.forEach { response ->
                pomodoroSessionDao.upsertSession(
                    response.toEntity(userId)
                )
            }

            val activeSession =
                remoteSessions.firstOrNull {
                    it.status == PomodoroSessionStatus.RUNNING ||
                            it.status == PomodoroSessionStatus.PAUSED
                }

            if (activeSession != null) {
                return PomodoroStartResult.ActiveSessionAlreadyExists
            }

            val response =
                apiService.createPomodoroSession(
                    CreatePomodoroSessionRequest(
                        activityId = activityId,
                        type = type,
                        plannedDurationSeconds =
                            plannedDurationSeconds
                    )
                )

            pomodoroSessionDao.upsertSession(
                response.toEntity(userId)
            )

            PomodoroStartResult.Success(
                sessionId = response.id
            )
        } catch (e: HttpException) {
            when (e.code()) {
                400 ->
                    PomodoroStartResult.ActivityNotFoundOrNotAllowed

                else ->
                    PomodoroStartResult.Error
            }
        } catch (_: Exception) {
            PomodoroStartResult.Error
        }
    }

    suspend fun pauseSession(
        sessionId: Long,
        userId: Long
    ): PomodoroOperationResult {
        return try {
            val response =
                apiService.pausePomodoroSession(sessionId)

            pomodoroSessionDao.upsertSession(
                response.toEntity(userId)
            )

            PomodoroOperationResult.Success
        } catch (e: HttpException) {
            when (e.code()) {
                400, 404 ->
                    PomodoroOperationResult.NotFoundOrInvalidState

                else ->
                    PomodoroOperationResult.Error
            }
        } catch (_: Exception) {
            PomodoroOperationResult.Error
        }
    }

    suspend fun resumeSession(
        sessionId: Long,
        userId: Long
    ): PomodoroOperationResult {
        return try {
            val response =
                apiService.resumePomodoroSession(sessionId)

            pomodoroSessionDao.upsertSession(
                response.toEntity(userId)
            )

            PomodoroOperationResult.Success
        } catch (e: HttpException) {
            when (e.code()) {
                400, 404 ->
                    PomodoroOperationResult.NotFoundOrInvalidState

                else ->
                    PomodoroOperationResult.Error
            }
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
                pomodoroSessionDao.getSessionById(
                    sessionId = sessionId,
                    userId = userId
                )
            } catch (_: Exception) {
                return PomodoroCompleteResult.Error
            }

        if (session == null) {
            return PomodoroCompleteResult.NotFoundOrNotAllowed
        }

        if (
            session.status !=
            PomodoroSessionStatus.RUNNING
        ) {
            return PomodoroCompleteResult.InvalidState
        }

        if (
            calculateRemainingMillis(session) > 0L
        ) {
            return PomodoroCompleteResult.TimeRemaining
        }

        return try {
            val response =
                apiService.completePomodoroSession(sessionId)

            pomodoroSessionDao.upsertSession(
                response.toEntity(userId)
            )

            PomodoroCompleteResult.Success
        } catch (e: HttpException) {
            when (e.code()) {
                400 ->
                    PomodoroCompleteResult.InvalidState

                404 ->
                    PomodoroCompleteResult.NotFoundOrNotAllowed

                else ->
                    PomodoroCompleteResult.Error
            }
        } catch (_: Exception) {
            PomodoroCompleteResult.Error
        }
    }

    suspend fun cancelSession(
        sessionId: Long,
        userId: Long
    ): PomodoroOperationResult {
        return try {
            val response =
                apiService.cancelPomodoroSession(sessionId)

            pomodoroSessionDao.upsertSession(
                response.toEntity(userId)
            )

            PomodoroOperationResult.Success
        } catch (e: HttpException) {
            when (e.code()) {
                400, 404 ->
                    PomodoroOperationResult.NotFoundOrInvalidState

                else ->
                    PomodoroOperationResult.Error
            }
        } catch (_: Exception) {
            PomodoroOperationResult.Error
        }
    }

    fun calculateRemainingMillis(
        session: PomodoroSessionEntity,
        nowMillis: Long = System.currentTimeMillis()
    ): Long {

        val plannedMillis =
            session.plannedDurationSeconds * 1000L

        val effectiveNow =
            if (
                session.status ==
                PomodoroSessionStatus.PAUSED
            ) {
                session.pausedAtMillis ?: nowMillis
            } else {
                nowMillis
            }

        val elapsedMillis =
            (
                    effectiveNow -
                            session.startedAtMillis -
                            session.totalPausedMillis
                    ).coerceAtLeast(0L)

        return (
                plannedMillis -
                        elapsedMillis
                ).coerceAtLeast(0L)
    }

    suspend fun countCompletedFocusSessions(
        userId: Long
    ): Int {
        return try {
            pomodoroSessionDao
                .countCompletedFocusSessions(userId)
        } catch (_: Exception) {
            0
        }
    }

    fun observeCompletedFocusCount(
        userId: Long
    ): Flow<Int> {
        return pomodoroSessionDao
            .observeCompletedFocusCount(userId)
    }

    fun observeCompletedFocusSeconds(
        userId: Long
    ): Flow<Long> {
        return pomodoroSessionDao
            .observeCompletedFocusSeconds(userId)
    }

    fun observeCompletedFocusCountBetween(
        userId: Long,
        startEpochDay: Long,
        endEpochDay: Long
    ): Flow<Int> {
        return pomodoroSessionDao
            .observeCompletedFocusCountBetween(
                userId,
                startEpochDay,
                endEpochDay
            )
    }

    fun observeCompletedFocusSecondsBetween(
        userId: Long,
        startEpochDay: Long,
        endEpochDay: Long
    ): Flow<Long> {
        return pomodoroSessionDao
            .observeCompletedFocusSecondsBetween(
                userId,
                startEpochDay,
                endEpochDay
            )
    }

    fun observeCompletedFocusStatsByDay(
        userId: Long,
        startEpochDay: Long,
        endEpochDay: Long
    ): Flow<List<DailyPomodoroStats>> {
        return pomodoroSessionDao
            .observeCompletedFocusStatsByDay(
                userId,
                startEpochDay,
                endEpochDay
            )
    }

    private fun PomodoroSessionResponse.toEntity(
        userId: Long
    ): PomodoroSessionEntity {
        return PomodoroSessionEntity(
            id = id,
            userId = userId,
            activityId = activityId,
            type = type,
            plannedDurationSeconds =
                plannedDurationSeconds,
            status = status,
            startedAtMillis = startedAtMillis,
            pausedAtMillis = pausedAtMillis,
            totalPausedMillis = totalPausedMillis,
            completedAtMillis = completedAtMillis,
            completedDateEpochDay =
                completedDateEpochDay,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}

sealed interface PomodoroSyncResult {
    data object Success : PomodoroSyncResult
    data object Unauthorized : PomodoroSyncResult
    data object Error : PomodoroSyncResult
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