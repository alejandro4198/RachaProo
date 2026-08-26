package com.example.rachapro.backend.category.dto

data class CreateCategoryRequest(
    val name: String,
    val icon: String? = null
)