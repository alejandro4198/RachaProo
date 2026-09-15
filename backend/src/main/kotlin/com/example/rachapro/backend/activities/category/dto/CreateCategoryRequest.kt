package com.example.rachapro.backend.activities.category.dto

data class CreateCategoryRequest(
    val name: String,
    val icon: String? = null
)