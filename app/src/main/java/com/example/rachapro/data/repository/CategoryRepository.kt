package com.example.rachapro.data.repository

import android.database.sqlite.SQLiteConstraintException
import com.example.rachapro.data.local.dao.CategoryDao
import com.example.rachapro.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

class CategoryRepository(
    private val categoryDao: CategoryDao
) {

    fun observeCategories(
        userId: Long
    ): Flow<List<CategoryEntity>> {

        return categoryDao
            .observeActiveCategories(
                userId = userId
            )
            .distinctUntilChanged()
    }

    suspend fun getCategoryById(
        categoryId: Long,
        userId: Long
    ): CategoryEntity? {

        return categoryDao.getCategoryById(
            categoryId = categoryId,
            userId = userId
        )
    }

    suspend fun createCategory(
        userId: Long,
        name: String,
        icon: String? = null
    ): CategoryCreateResult {

        val normalizedName =
            name.trim()

        if (normalizedName.isBlank()) {

            return CategoryCreateResult.InvalidName
        }

        return try {

            val alreadyExists =
                categoryDao.categoryNameExists(
                    userId = userId,
                    name = normalizedName
                )

            if (alreadyExists) {

                CategoryCreateResult.NameAlreadyExists

            } else {

                val currentTime =
                    System.currentTimeMillis()

                val category =
                    CategoryEntity(
                        userId = userId,
                        name = normalizedName,
                        icon = icon,
                        createdAt = currentTime,
                        updatedAt = currentTime,
                        isActive = true
                    )

                val categoryId =
                    categoryDao.insertCategory(
                        category = category
                    )

                CategoryCreateResult.Success(
                    categoryId = categoryId
                )
            }

        } catch (_: SQLiteConstraintException) {

            CategoryCreateResult.NameAlreadyExists

        } catch (_: Exception) {

            CategoryCreateResult.Error
        }
    }
}

sealed interface CategoryCreateResult {

    data class Success(
        val categoryId: Long
    ) : CategoryCreateResult

    data object NameAlreadyExists :
        CategoryCreateResult

    data object InvalidName :
        CategoryCreateResult

    data object Error :
        CategoryCreateResult
}