package com.example.rachapro.backend.identity.auth

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class LoginRateLimiterTest {

    @Test
    fun `allows attempts under threshold`() {
        val limiter = LoginRateLimiter(maxAttempts = 3, lockDurationMs = 60000L)
        val key = "user@test.com"

        assertTrue(limiter.isAllowed(key))
        limiter.recordFailure(key)
        assertTrue(limiter.isAllowed(key))
        limiter.recordFailure(key)
        assertTrue(limiter.isAllowed(key))
    }

    @Test
    fun `blocks attempts exceeding threshold`() {
        val limiter = LoginRateLimiter(maxAttempts = 3, lockDurationMs = 60000L)
        val key = "user@test.com"

        repeat(3) { limiter.recordFailure(key) }
        assertFalse(limiter.isAllowed(key))
    }

    @Test
    fun `successful login resets attempts`() {
        val limiter = LoginRateLimiter(maxAttempts = 3, lockDurationMs = 60000L)
        val key = "user@test.com"

        repeat(2) { limiter.recordFailure(key) }
        limiter.recordSuccess(key)
        assertTrue(limiter.isAllowed(key))

        repeat(2) { limiter.recordFailure(key) }
        assertTrue(limiter.isAllowed(key))
    }
}
