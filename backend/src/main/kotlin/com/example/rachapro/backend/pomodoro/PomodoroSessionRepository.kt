package com.example.rachapro.backend.pomodoro

import org.springframework.data.jpa.repository.JpaRepository

interface PomodoroSessionRepository : JpaRepository<PomodoroSessionEntity, Long> {

    fun findAllByUserIdOrderByStartedAtMillisDesc(
        userId: Long
    ): List<PomodoroSessionEntity>

    fun findByIdAndUserId(
        id: Long,
        userId: Long
    ): PomodoroSessionEntity?
}