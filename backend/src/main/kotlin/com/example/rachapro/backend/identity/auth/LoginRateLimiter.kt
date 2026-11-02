package com.example.rachapro.backend.identity.auth

import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class LoginRateLimiter(
    private val maxAttempts: Int = 5,
    private val lockDurationMs: Long = 300_000L // 5 minutes
) {
    private class AttemptRecord(
        var count: Int,
        var firstAttemptMs: Long
    )

    private val attempts = ConcurrentHashMap<String, AttemptRecord>()

    fun isAllowed(key: String): Boolean {
        val now = System.currentTimeMillis()
        val record = attempts[key] ?: return true

        if (now - record.firstAttemptMs > lockDurationMs) {
            attempts.remove(key)
            return true
        }

        return record.count < maxAttempts
    }

    fun recordFailure(key: String) {
        val now = System.currentTimeMillis()
        attempts.compute(key) { _, existing ->
            if (existing == null || (now - existing.firstAttemptMs > lockDurationMs)) {
                AttemptRecord(1, now)
            } else {
                existing.count++
                existing
            }
        }
    }

    fun recordSuccess(key: String) {
        attempts.remove(key)
    }

    fun reset() {
        attempts.clear()
    }
}
