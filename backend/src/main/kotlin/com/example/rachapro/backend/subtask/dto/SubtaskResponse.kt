package com.example.rachapro.backend.subtask.dto

data class SubtaskResponse(
    val id: Long,
    val activityId: Long,
    val title: String,
    val isCompleted: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
    val completedAt: Long?
)