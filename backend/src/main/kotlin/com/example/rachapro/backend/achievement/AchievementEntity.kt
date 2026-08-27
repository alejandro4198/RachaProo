package com.example.rachapro.backend.achievement

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(
    name = "achievements",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uq_achievements_user_type",
            columnNames = ["user_id", "type"]
        )
    ]
)
class AchievementEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(name = "user_id", nullable = false)
    var userId: Long = 0,

    @Column(nullable = false)
    var type: String = "",

    @Column(name = "unlocked_at", nullable = false)
    var unlockedAt: Long = 0
)