package com.example.rachapro.backend.activity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "activities")
class ActivityEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(name = "user_id", nullable = false)
    var userId: Long = 0,

    @Column(name = "category_id", nullable = false)
    var categoryId: Long = 0,

    @Column(nullable = false)
    var title: String = "",

    @Column(nullable = false)
    var description: String = "",

    @Column(name = "due_date_epoch_day", nullable = false)
    var dueDateEpochDay: Long = 0,

    @Column(name = "due_time_minutes")
    var dueTimeMinutes: Int? = null,

    @Column(nullable = false)
    var priority: String = "MEDIUM",

    @Column(nullable = false)
    var status: String = "PENDING",

    @Column(name = "repeat_rule")
    var repeatRule: String? = null,

    @Column(name = "created_at", nullable = false)
    var createdAt: Long = 0,

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Long = 0,

    @Column(name = "completed_at")
    var completedAt: Long? = null,

    @Column(name = "completed_date_epoch_day")
    var completedDateEpochDay: Long? = null,

    @Column(name = "is_deleted", nullable = false)
    var isDeleted: Boolean = false,

    @Column(name = "deleted_at")
    var deletedAt: Long? = null
)