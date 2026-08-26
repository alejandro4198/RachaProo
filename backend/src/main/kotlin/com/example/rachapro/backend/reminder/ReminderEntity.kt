package com.example.rachapro.backend.reminder

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "reminders")
class ReminderEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(name = "user_id", nullable = false)
    var userId: Long = 0,

    @Column(name = "activity_id")
    var activityId: Long? = null,

    @Column(nullable = false)
    var title: String = "",

    @Column(nullable = false)
    var message: String = "",

    @Column(name = "trigger_at_millis", nullable = false)
    var triggerAtMillis: Long = 0,

    @Column(nullable = false)
    var status: String = "SCHEDULED",

    @Column(name = "created_at", nullable = false)
    var createdAt: Long = 0,

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Long = 0,

    @Column(name = "delivered_at")
    var deliveredAt: Long? = null
)