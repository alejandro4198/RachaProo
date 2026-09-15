package com.example.rachapro.backend.activities.activity

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface ActivityRepository : JpaRepository<ActivityEntity, Long> {

    fun findAllByUserIdAndIsDeletedFalseOrderByDueDateEpochDayAsc(
        userId: Long
    ): List<ActivityEntity>

    fun findAllByUserIdAndIsDeletedFalseOrderByDueDateEpochDayAsc(
        userId: Long,
        pageable: Pageable
    ): List<ActivityEntity>

    fun findByIdAndUserIdAndIsDeletedFalse(
        id: Long,
        userId: Long
    ): ActivityEntity?
}