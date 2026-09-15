package com.example.rachapro.backend.identity.auth.dto

data class LoginRequest(
    val email: String,
    val password: String
)