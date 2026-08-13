package com.example.rachapro.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rachapro.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Insert(
        onConflict = OnConflictStrategy.ABORT
    )
    suspend fun insertCategory(
        category: CategoryEntity
    ): Long

    @Query(
        """
        SELECT *
        FROM categories
        WHERE userId = :userId
        AND isActive = 1
        ORDER BY name COLLATE NOCASE ASC
        """
    )
    fun observeActiveCategories(
        userId: Long
    ): Flow<List<CategoryEntity>>

    @Query(
        """
        SELECT *
        FROM categories
        WHERE id = :categoryId
        AND userId = :userId
        LIMIT 1
        """
    )
    suspend fun getCategoryById(
        categoryId: Long,
        userId: Long
    ): CategoryEntity?

    @Query(
        """
        SELECT EXISTS(
            SELECT 1
            FROM categories
            WHERE userId = :userId
            AND name = :name COLLATE NOCASE
            AND isActive = 1
        )
        """
    )
    suspend fun categoryNameExists(
        userId: Long,
        name: String
    ): Boolean
}