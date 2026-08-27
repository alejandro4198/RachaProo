package com.example.rachapro.backend.pomodoro

import com.example.rachapro.backend.activity.ActivityRepository
import com.example.rachapro.backend.pomodoro.dto.CreatePomodoroSessionRequest
import com.example.rachapro.backend.pomodoro.dto.PomodoroSessionResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class PomodoroSessionService(
    private val pomodoroSessionRepository: PomodoroSessionRepository,
    private val activityRepository: ActivityRepository
) {

    @Transactional(readOnly = true)
    fun findAllByUserId(userId: Long): List<PomodoroSessionResponse> {
        return pomodoroSessionRepository
            .findAllByUserIdOrderByStartedAtMillisDesc(userId)
            .map { it.toResponse() }
    }

    @Transactional
    fun create(
        userId: Long,
        request: CreatePomodoroSessionRequest
    ): PomodoroSessionResponse {

        val type = request.type.trim().uppercase()

        require(type in setOf("FOCUS", "SHORT_BREAK", "LONG_BREAK")) {
            "El tipo debe ser FOCUS, SHORT_BREAK o LONG_BREAK"
        }

        require(request.plannedDurationSeconds > 0) {
            "La duracion debe ser mayor que cero"
        }

        if (request.activityId != null) {
            requireNotNull(
                activityRepository.findByIdAndUserIdAndIsDeletedFalse(
                    request.activityId,
                    userId
                )
            ) {
                "La actividad no existe o no pertenece al usuario"
            }
        }

        val now = System.currentTimeMillis()

        val session = PomodoroSessionEntity(
            userId = userId,
            activityId = request.activityId,
            type = type,
            plannedDurationSeconds = request.plannedDurationSeconds,
            status = "RUNNING",
            startedAtMillis = now,
            pausedAtMillis = null,
            totalPausedMillis = 0,
            completedAtMillis = null,
            completedDateEpochDay = null,
            createdAt = now,
            updatedAt = now
        )

        return pomodoroSessionRepository
            .save(session)
            .toResponse()
    }

    private fun PomodoroSessionEntity.toResponse(): PomodoroSessionResponse {
        return PomodoroSessionResponse(
            id = id,
            activityId = activityId,
            type = type,
            plannedDurationSeconds = plannedDurationSeconds,
            status = status,
            startedAtMillis = startedAtMillis,
            pausedAtMillis = pausedAtMillis,
            totalPausedMillis = totalPausedMillis,
            completedAtMillis = completedAtMillis,
            completedDateEpochDay = completedDateEpochDay,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    @Transactional
    fun pause(
        userId: Long,
        sessionId: Long
    ): PomodoroSessionResponse? {

        val session = pomodoroSessionRepository
            .findByIdAndUserId(sessionId, userId)
            ?: return null

        require(session.status == "RUNNING") {
            "Solo se puede pausar una sesion en estado RUNNING"
        }

        val now = System.currentTimeMillis()

        session.status = "PAUSED"
        session.pausedAtMillis = now
        session.updatedAt = now

        return pomodoroSessionRepository
            .save(session)
            .toResponse()
    }

    @Transactional
    fun resume(
        userId: Long,
        sessionId: Long
    ): PomodoroSessionResponse? {

        val session = pomodoroSessionRepository
            .findByIdAndUserId(sessionId, userId)
            ?: return null

        require(session.status == "PAUSED") {
            "Solo se puede reanudar una sesion en estado PAUSED"
        }

        val pausedAt = requireNotNull(session.pausedAtMillis) {
            "La sesion pausada no tiene pausedAtMillis"
        }

        val now = System.currentTimeMillis()
        val pausedDuration = now - pausedAt

        session.status = "RUNNING"
        session.totalPausedMillis += pausedDuration
        session.pausedAtMillis = null
        session.updatedAt = now

        return pomodoroSessionRepository
            .save(session)
            .toResponse()
    }

    @Transactional
    fun complete(
        userId: Long,
        sessionId: Long
    ): PomodoroSessionResponse? {

        val session = pomodoroSessionRepository
            .findByIdAndUserId(sessionId, userId)
            ?: return null

        require(session.status in setOf("RUNNING", "PAUSED")) {
            "Solo se puede completar una sesion RUNNING o PAUSED"
        }

        val now = System.currentTimeMillis()

        if (session.status == "PAUSED") {
            val pausedAt = requireNotNull(session.pausedAtMillis)

            session.totalPausedMillis += now - pausedAt
            session.pausedAtMillis = null
        }

        session.status = "COMPLETED"
        session.completedAtMillis = now
        session.completedDateEpochDay = LocalDate.now().toEpochDay()
        session.updatedAt = now

        return pomodoroSessionRepository
            .save(session)
            .toResponse()
    }

    @Transactional
    fun cancel(
        userId: Long,
        sessionId: Long
    ): PomodoroSessionResponse? {

        val session = pomodoroSessionRepository
            .findByIdAndUserId(sessionId, userId)
            ?: return null

        require(session.status in setOf("RUNNING", "PAUSED")) {
            "Solo se puede cancelar una sesion RUNNING o PAUSED"
        }

        val now = System.currentTimeMillis()

        if (session.status == "PAUSED") {
            val pausedAt = requireNotNull(session.pausedAtMillis)

            session.totalPausedMillis += now - pausedAt
            session.pausedAtMillis = null
        }

        session.status = "CANCELLED"
        session.updatedAt = now

        return pomodoroSessionRepository
            .save(session)
            .toResponse()
    }
}