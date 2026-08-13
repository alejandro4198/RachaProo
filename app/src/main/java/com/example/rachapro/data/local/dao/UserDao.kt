package com.example.rachapro.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rachapro.data.local.entity.UserEntity

@Dao
interface UserDao {

    @Insert(
        onConflict = OnConflictStrategy.ABORT
    )
    suspend fun insertUser(
        user: UserEntity
    ): Long

    @Query(
        """
        SELECT * 
        FROM users
        WHERE email = :email
        LIMIT 1
        """
    )
    suspend fun getUserByEmail(
        email: String
    ): UserEntity?

    @Query(
        """
        SELECT EXISTS(
            SELECT 1
            FROM users
            WHERE email = :email
        )
        """
    )
    suspend fun emailExists(
        email: String
    ): Boolean

    @Query(
        """
        SELECT *
        FROM users
        WHERE id = :userId
        LIMIT 1
        """
    )
    suspend fun getUserById(
        userId: Long
    ): UserEntity?
}