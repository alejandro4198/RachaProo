package com.example.rachapro.backend.user

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "users")
class UserEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(name = "full_name", nullable = false)
    var fullName: String = "",

    @Column(nullable = false, unique = true)
    var email: String = "",

    @Column(name = "password_hash", nullable = false)
    var passwordHash: String = "",

    @Column(name = "password_salt", nullable = false)
    var passwordSalt: String = "",

    @Column(nullable = false)
    var semester: Int = 0,

    @Column(name = "accepted_privacy_policy", nullable = false)
    var acceptedPrivacyPolicy: Boolean = false,

    @Column(name = "created_at", nullable = false)
    var createdAt: Long = 0,

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Long = 0
)