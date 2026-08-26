package com.example.rachapro.backend.subtask

import org.springframework.data.jpa.repository.JpaRepository

interface SubtaskRepository : JpaRepository<SubtaskEntity, Long> {

    fun findAllByActivityIdOrderByCreatedAtAsc(
        activityId: Long
    ): List<SubtaskEntity>

    fun findByIdAndActivityId(
        id: Long,
        activityId: Long
    ): SubtaskEntity?
}