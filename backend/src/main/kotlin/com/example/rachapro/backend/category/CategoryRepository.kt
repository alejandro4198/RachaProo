package com.example.rachapro.backend.category

import org.springframework.data.jpa.repository.JpaRepository

interface CategoryRepository : JpaRepository<CategoryEntity, Long> {

    fun findAllByUserIdAndIsActiveTrueOrderByNameAsc(userId: Long): List<CategoryEntity>

    fun existsByUserIdAndNameIgnoreCase(userId: Long, name: String): Boolean
}