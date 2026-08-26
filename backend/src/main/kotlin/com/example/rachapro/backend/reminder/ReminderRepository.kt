package com.example.rachapro.backend.reminder

import org.springframework.data.jpa.repository.JpaRepository

interface ReminderRepository : JpaRepository<ReminderEntity, Long> {

    fun findAllByUserIdOrderByTriggerAtMillisAsc(
        userId: Long
    ): List<ReminderEntity>

    fun findByIdAndUserId(
        id: Long,
        userId: Long
    ): ReminderEntity?
}