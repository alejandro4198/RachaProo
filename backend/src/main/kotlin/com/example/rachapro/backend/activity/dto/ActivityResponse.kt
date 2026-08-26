package com.example.rachapro.backend.activity.dto

data class ActivityResponse(
    val id: Long,
    val categoryId: Long,
    val title: String,
    val description: String,
    val dueDateEpochDay: Long,
    val dueTimeMinutes: Int?,
    val priority: String,
    val status: String,
    val repeatRule: String?,
    val createdAt: Long,
    val updatedAt: Long,
    val completedAt: Long?,
    val completedDateEpochDay: Long?,
    val isDeleted: Boolean
)