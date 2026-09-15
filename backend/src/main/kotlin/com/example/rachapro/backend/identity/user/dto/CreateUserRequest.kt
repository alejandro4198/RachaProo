package com.example.rachapro.backend.identity.user.dto

data class CreateUserRequest(
    val fullName: String,
    val email: String,
    val password: String,
    val semester: Int,
    val acceptedPrivacyPolicy: Boolean
)