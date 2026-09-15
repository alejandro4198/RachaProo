package com.example.rachapro.backend.activities.category

import com.example.rachapro.backend.activities.api.DefaultCategoryProvisioning
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DefaultCategoryProvisioningService(
    private val categoryRepository: CategoryRepository
) : DefaultCategoryProvisioning {

    @Transactional
    override fun createDefaultsForUser(userId: Long) {
        val now = System.currentTimeMillis()

        val categories = listOf(
            CategoryEntity(
                userId = userId,
                name = "Universidad",
                createdAt = now,
                updatedAt = now,
                isActive = true
            ),
            CategoryEntity(
                userId = userId,
                name = "Personal",
                createdAt = now,
                updatedAt = now,
                isActive = true
            ),
            CategoryEntity(
                userId = userId,
                name = "Trabajo",
                createdAt = now,
                updatedAt = now,
                isActive = true
            )
        )

        categoryRepository.saveAll(categories)
    }
}