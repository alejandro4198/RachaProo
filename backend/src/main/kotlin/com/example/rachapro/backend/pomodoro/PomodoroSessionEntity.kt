package com.example.rachapro.backend.pomodoro

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "pomodoro_sessions")
class PomodoroSessionEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(name = "user_id", nullable = false)
    var userId: Long = 0,

    @Column(name = "activity_id")
    var activityId: Long? = null,

    @Column(nullable = false)
    var type: String = "FOCUS",

    @Column(name = "planned_duration_seconds", nullable = false)
    var plannedDurationSeconds: Int = 0,

    @Column(nullable = false)
    var status: String = "RUNNING",

    @Column(name = "started_at_millis", nullable = false)
    var startedAtMillis: Long = 0,

    @Column(name = "paused_at_millis")
    var pausedAtMillis: Long? = null,

    @Column(name = "total_paused_millis", nullable = false)
    var totalPausedMillis: Long = 0,

    @Column(name = "completed_at_millis")
    var completedAtMillis: Long? = null,

    @Column(name = "completed_date_epoch_day")
    var completedDateEpochDay: Long? = null,

    @Column(name = "created_at", nullable = false)
    var createdAt: Long = 0,

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Long = 0
)