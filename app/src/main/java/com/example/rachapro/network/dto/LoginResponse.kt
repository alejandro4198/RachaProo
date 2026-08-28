package com.example.rachapro.network.dto

data class LoginResponse(
    val token: String,
    val user: UserResponse
)