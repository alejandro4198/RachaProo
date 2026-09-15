package com.example.rachapro.backend.identity.auth.dto

import com.example.rachapro.backend.identity.user.dto.UserResponse

data class LoginResponse(
    val token: String,
    val user: UserResponse
)