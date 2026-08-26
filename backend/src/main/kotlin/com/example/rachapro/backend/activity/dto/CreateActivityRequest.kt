package com.example.rachapro.backend.activity.dto

data class CreateActivityRequest(
    val categoryId: Long,
    val title: String,
    val description: String = "",
    val dueDateEpochDay: Long,
    val dueTimeMinutes: Int? = null,
    val priority: String = "MEDIUM",
    val repeatRule: String? = null
)