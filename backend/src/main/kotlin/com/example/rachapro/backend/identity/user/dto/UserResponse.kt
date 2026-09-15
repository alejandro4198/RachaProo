package com.example.rachapro.backend.identity.user.dto

data class UserResponse(
    val id: Long,
    val fullName: String,
    val email: String,
    val semester: Int,
    val acceptedPrivacyPolicy: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)