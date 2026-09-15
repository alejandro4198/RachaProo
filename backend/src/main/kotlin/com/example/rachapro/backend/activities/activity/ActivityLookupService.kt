package com.example.rachapro.backend.activities.activity

import com.example.rachapro.backend.activities.api.ActivityLookup
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ActivityLookupService(
    private val activityRepository: ActivityRepository
) : ActivityLookup {

    @Transactional(readOnly = true)
    override fun existsActiveActivityForUser(
        activityId: Long,
        userId: Long
    ): Boolean {
        return activityRepository
            .findByIdAndUserIdAndIsDeletedFalse(
                activityId,
                userId
            ) != null
    }
}