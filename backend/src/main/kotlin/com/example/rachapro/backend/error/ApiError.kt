package com.example.rachapro.backend.error

data class ApiError(
    val status: Int,
    val error: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)