package com.example.rachapro.network.dto

data class CreateCategoryRequest(
    val name: String,
    val icon: String? = null
)