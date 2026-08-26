package com.example.rachapro.backend.activity

import org.springframework.data.jpa.repository.JpaRepository

interface ActivityRepository : JpaRepository<ActivityEntity, Long> {

    fun findAllByUserIdAndIsDeletedFalseOrderByDueDateEpochDayAsc(
        userId: Long
    ): List<ActivityEntity>

    fun findByIdAndUserIdAndIsDeletedFalse(
        id: Long,
        userId: Long
    ): ActivityEntity?
}