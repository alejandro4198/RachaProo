package com.example.rachapro.backend.identity.api

import java.time.Instant

data class UserRegisteredV1(
    val userId: Long,
    val occurredAt: Instant
)