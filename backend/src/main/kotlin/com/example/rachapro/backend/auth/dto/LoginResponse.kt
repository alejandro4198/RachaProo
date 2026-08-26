package com.example.rachapro.backend.auth.dto

import com.example.rachapro.backend.user.dto.UserResponse

data class LoginResponse(
    val token: String,
    val user: UserResponse
)