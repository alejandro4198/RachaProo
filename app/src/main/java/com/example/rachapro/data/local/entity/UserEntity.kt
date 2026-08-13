package com.example.rachapro.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [
        Index(
            value = ["email"],
            unique = true
        )
    ]
)
data class UserEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val fullName: String,

    val email: String,

    val passwordHash: String,

    val passwordSalt: String,

    val semester: Int,

    val acceptedPrivacyPolicy: Boolean,

    val createdAt: Long,

    val updatedAt: Long
)