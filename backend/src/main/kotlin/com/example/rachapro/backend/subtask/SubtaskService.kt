package com.example.rachapro.backend.subtask

import com.example.rachapro.backend.activity.ActivityRepository
import com.example.rachapro.backend.subtask.dto.CreateSubtaskRequest
import com.example.rachapro.backend.subtask.dto.SubtaskResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import com.example.rachapro.backend.subtask.dto.UpdateSubtaskRequest

@Service
class SubtaskService(
    private val subtaskRepository: SubtaskRepository,
    private val activityRepository: ActivityRepository
) {

    @Transactional(readOnly = true)
    fun findAll(
        userId: Long,
        activityId: Long
    ): List<SubtaskResponse>? {

        activityRepository
            .findByIdAndUserIdAndIsDeletedFalse(activityId, userId)
            ?: return null

        return subtaskRepository
            .findAllByActivityIdOrderByCreatedAtAsc(activityId)
            .map { it.toResponse() }
    }

    @Transactional
    fun create(
        userId: Long,
        activityId: Long,
        request: CreateSubtaskRequest
    ): SubtaskResponse? {

        activityRepository
            .findByIdAndUserIdAndIsDeletedFalse(activityId, userId)
            ?: return null

        val title = request.title.trim()

        require(title.isNotBlank()) {
            "El titulo de la subtarea es obligatorio"
        }

        val now = System.currentTimeMillis()

        val subtask = SubtaskEntity(
            activityId = activityId,
            title = title,
            isCompleted = false,
            createdAt = now,
            updatedAt = now,
            completedAt = null
        )

        return subtaskRepository
            .save(subtask)
            .toResponse()
    }

    private fun SubtaskEntity.toResponse(): SubtaskResponse {
        return SubtaskResponse(
            id = id,
            activityId = activityId,
            title = title,
            isCompleted = isCompleted,
            createdAt = createdAt,
            updatedAt = updatedAt,
            completedAt = completedAt
        )
    }

    @Transactional
    fun complete(
        userId: Long,
        activityId: Long,
        subtaskId: Long
    ): SubtaskResponse? {

        activityRepository
            .findByIdAndUserIdAndIsDeletedFalse(activityId, userId)
            ?: return null

        val subtask = subtaskRepository
            .findByIdAndActivityId(subtaskId, activityId)
            ?: return null

        val now = System.currentTimeMillis()

        subtask.isCompleted = true
        subtask.completedAt = now
        subtask.updatedAt = now

        return subtaskRepository
            .save(subtask)
            .toResponse()
    }

    @Transactional
    fun update(
        userId: Long,
        activityId: Long,
        subtaskId: Long,
        request: UpdateSubtaskRequest
    ): SubtaskResponse? {

        activityRepository
            .findByIdAndUserIdAndIsDeletedFalse(activityId, userId)
            ?: return null

        val subtask = subtaskRepository
            .findByIdAndActivityId(subtaskId, activityId)
            ?: return null

        val title = request.title.trim()

        require(title.isNotBlank()) {
            "El titulo de la subtarea es obligatorio"
        }

        subtask.title = title
        subtask.updatedAt = System.currentTimeMillis()

        return subtaskRepository
            .save(subtask)
            .toResponse()
    }

    @Transactional
    fun delete(
        userId: Long,
        activityId: Long,
        subtaskId: Long
    ): Boolean {

        activityRepository
            .findByIdAndUserIdAndIsDeletedFalse(activityId, userId)
            ?: return false

        val subtask = subtaskRepository
            .findByIdAndActivityId(subtaskId, activityId)
            ?: return false

        subtaskRepository.delete(subtask)

        return true
    }

    @Transactional
    fun uncomplete(
        userId: Long,
        activityId: Long,
        subtaskId: Long
    ): SubtaskResponse? {

        activityRepository
            .findByIdAndUserIdAndIsDeletedFalse(
                activityId,
                userId
            )
            ?: return null

        val subtask =
            subtaskRepository
                .findByIdAndActivityId(
                    subtaskId,
                    activityId
                )
                ?: return null

        subtask.isCompleted = false
        subtask.completedAt = null
        subtask.updatedAt = System.currentTimeMillis()

        return subtaskRepository
            .save(subtask)
            .toResponse()
    }
}