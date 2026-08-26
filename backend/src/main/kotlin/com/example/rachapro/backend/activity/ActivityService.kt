package com.example.rachapro.backend.activity

import com.example.rachapro.backend.activity.dto.ActivityResponse
import com.example.rachapro.backend.activity.dto.CreateActivityRequest
import com.example.rachapro.backend.category.CategoryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import com.example.rachapro.backend.activity.dto.UpdateActivityRequest
import java.time.LocalDate

@Service
class ActivityService(
    private val activityRepository: ActivityRepository,
    private val categoryRepository: CategoryRepository
) {

    @Transactional(readOnly = true)
    fun findAllByUserId(userId: Long): List<ActivityResponse> {
        return activityRepository
            .findAllByUserIdAndIsDeletedFalseOrderByDueDateEpochDayAsc(userId)
            .map { it.toResponse() }
    }

    @Transactional(readOnly = true)
    fun findById(userId: Long, activityId: Long): ActivityResponse? {
        return activityRepository
            .findByIdAndUserIdAndIsDeletedFalse(activityId, userId)
            ?.toResponse()
    }

    @Transactional
    fun create(
        userId: Long,
        request: CreateActivityRequest
    ): ActivityResponse {

        val title = request.title.trim()
        val priority = request.priority.trim().uppercase()

        require(title.isNotBlank()) {
            "El titulo de la actividad es obligatorio"
        }

        require(priority in setOf("LOW", "MEDIUM", "HIGH")) {
            "La prioridad debe ser LOW, MEDIUM o HIGH"
        }

        requireNotNull(
            categoryRepository.findByIdAndUserIdAndIsActiveTrue(
                request.categoryId,
                userId
            )
        ) {
            "La categoria no existe o no pertenece al usuario"
        }

        val now = System.currentTimeMillis()

        val activity = ActivityEntity(
            userId = userId,
            categoryId = request.categoryId,
            title = title,
            description = request.description.trim(),
            dueDateEpochDay = request.dueDateEpochDay,
            dueTimeMinutes = request.dueTimeMinutes,
            priority = priority,
            status = "PENDING",
            repeatRule = request.repeatRule,
            createdAt = now,
            updatedAt = now,
            completedAt = null,
            completedDateEpochDay = null,
            isDeleted = false,
            deletedAt = null
        )

        return activityRepository
            .save(activity)
            .toResponse()
    }

    private fun ActivityEntity.toResponse(): ActivityResponse {
        return ActivityResponse(
            id = id,
            categoryId = categoryId,
            title = title,
            description = description,
            dueDateEpochDay = dueDateEpochDay,
            dueTimeMinutes = dueTimeMinutes,
            priority = priority,
            status = status,
            repeatRule = repeatRule,
            createdAt = createdAt,
            updatedAt = updatedAt,
            completedAt = completedAt,
            completedDateEpochDay = completedDateEpochDay,
            isDeleted = isDeleted
        )

    }

    @Transactional
    fun update(
        userId: Long,
        activityId: Long,
        request: UpdateActivityRequest
    ): ActivityResponse? {

        val activity = activityRepository
            .findByIdAndUserIdAndIsDeletedFalse(activityId, userId)
            ?: return null

        val title = request.title.trim()
        val priority = request.priority.trim().uppercase()

        require(title.isNotBlank()) {
            "El titulo de la actividad es obligatorio"
        }

        require(priority in setOf("LOW", "MEDIUM", "HIGH")) {
            "La prioridad debe ser LOW, MEDIUM o HIGH"
        }

        requireNotNull(
            categoryRepository.findByIdAndUserIdAndIsActiveTrue(
                request.categoryId,
                userId
            )
        ) {
            "La categoria no existe o no pertenece al usuario"
        }

        activity.categoryId = request.categoryId
        activity.title = title
        activity.description = request.description.trim()
        activity.dueDateEpochDay = request.dueDateEpochDay
        activity.dueTimeMinutes = request.dueTimeMinutes
        activity.priority = priority
        activity.repeatRule = request.repeatRule
        activity.updatedAt = System.currentTimeMillis()

        return activityRepository
            .save(activity)
            .toResponse()
    }

    @Transactional
    fun complete(
        userId: Long,
        activityId: Long
    ): ActivityResponse? {

        val activity = activityRepository
            .findByIdAndUserIdAndIsDeletedFalse(activityId, userId)
            ?: return null

        val now = System.currentTimeMillis()

        activity.status = "COMPLETED"
        activity.completedAt = now
        activity.completedDateEpochDay = LocalDate.now().toEpochDay()
        activity.updatedAt = now

        return activityRepository
            .save(activity)
            .toResponse()
    }

    @Transactional
    fun delete(
        userId: Long,
        activityId: Long
    ): Boolean {

        val activity = activityRepository
            .findByIdAndUserIdAndIsDeletedFalse(activityId, userId)
            ?: return false

        val now = System.currentTimeMillis()

        activity.isDeleted = true
        activity.deletedAt = now
        activity.updatedAt = now

        activityRepository.save(activity)

        return true
    }
}