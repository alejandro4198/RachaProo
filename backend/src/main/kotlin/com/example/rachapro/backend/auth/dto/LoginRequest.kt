package com.example.rachapro.backend.auth.dto

data class LoginRequest(
    val email: String,
    val password: String
)