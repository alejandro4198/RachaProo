package com.example.rachapro.backend.activities.category

import com.example.rachapro.backend.activities.api.DefaultCategoryProvisioning
import com.example.rachapro.backend.identity.api.UserRegisteredV1
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener
import java.time.Duration
import java.time.Instant

@Component
class UserRegisteredV1Listener(
    private val defaultCategoryProvisioning: DefaultCategoryProvisioning
) {

    private val logger =
        LoggerFactory.getLogger(UserRegisteredV1Listener::class.java)

    @Async
    @TransactionalEventListener(
        phase = TransactionPhase.AFTER_COMMIT
    )
    fun onUserRegistered(event: UserRegisteredV1) {
        defaultCategoryProvisioning.createDefaultsForUser(
            event.userId
        )

        val completedAt = Instant.now()
        val elapsedMs =
            Duration.between(
                event.occurredAt,
                completedAt
            ).toMillis()

        logger.info(
            "SPIKE-01 UserRegisteredV1 processed userId={} occurredAt={} completedAt={} elapsedMs={}",
            event.userId,
            event.occurredAt,
            completedAt,
            elapsedMs
        )
    }
}