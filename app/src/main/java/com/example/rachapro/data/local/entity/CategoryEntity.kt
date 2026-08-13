package com.example.rachapro.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "categories",
    indices = [
        Index(
            value = ["userId", "name"],
            unique = true
        )
    ]
)
data class CategoryEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val userId: Long,

    val name: String,

    val icon: String? = null,

    val createdAt: Long,

    val updatedAt: Long,

    val isActive: Boolean = true
)