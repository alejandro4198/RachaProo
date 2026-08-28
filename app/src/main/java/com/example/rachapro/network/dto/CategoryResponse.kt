package com.example.rachapro.network.dto

data class CategoryResponse(
    val id: Long,
    val name: String,
    val icon: String?,
    val createdAt: Long,
    val updatedAt: Long,
    val isActive: Boolean
)