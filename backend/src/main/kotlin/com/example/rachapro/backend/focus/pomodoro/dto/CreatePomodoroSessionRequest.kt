package com.example.rachapro.backend.focus.pomodoro.dto

data class CreatePomodoroSessionRequest(
    val activityId: Long? = null,
    val type: String,
    val plannedDurationSeconds: Int
)