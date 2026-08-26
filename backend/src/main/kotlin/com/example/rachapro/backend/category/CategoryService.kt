package com.example.rachapro.backend.category

import com.example.rachapro.backend.category.dto.CategoryResponse
import com.example.rachapro.backend.category.dto.CreateCategoryRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CategoryService(
    private val categoryRepository: CategoryRepository
) {

    @Transactional(readOnly = true)
    fun findActiveByUserId(userId: Long): List<CategoryResponse> {
        return categoryRepository
            .findAllByUserIdAndIsActiveTrueOrderByNameAsc(userId)
            .map { it.toResponse() }
    }

    @Transactional
    fun create(userId: Long, request: CreateCategoryRequest): CategoryResponse {
        val name = request.name.trim()

        require(name.isNotBlank()) {
            "El nombre de la categoria es obligatorio"
        }

        require(!categoryRepository.existsByUserIdAndNameIgnoreCase(userId, name)) {
            "La categoria ya existe"
        }

        val now = System.currentTimeMillis()

        val category = CategoryEntity(
            userId = userId,
            name = name,
            icon = request.icon,
            createdAt = now,
            updatedAt = now,
            isActive = true
        )

        return categoryRepository.save(category).toResponse()
    }

    private fun CategoryEntity.toResponse(): CategoryResponse {
        return CategoryResponse(
            id = id,
            name = name,
            icon = icon,
            createdAt = createdAt,
            updatedAt = updatedAt,
            isActive = isActive
        )
    }
}