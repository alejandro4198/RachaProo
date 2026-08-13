package com.example.rachapro

import android.app.Application
import com.example.rachapro.data.local.SessionManager
import com.example.rachapro.data.local.RachaProDatabase
import com.example.rachapro.data.repository.UserRepository
import com.example.rachapro.data.repository.ActivityRepository
import com.example.rachapro.data.repository.CategoryRepository
import com.example.rachapro.data.repository.SubtaskRepository
import com.example.rachapro.data.repository.ReminderRepository
import com.example.rachapro.notifications.NotificationChannels
import com.example.rachapro.notifications.ReminderScheduler
import com.example.rachapro.data.repository.PomodoroRepository
import com.example.rachapro.data.local.UserPreferencesManager

class RachaProApplication : Application() {

    override fun onCreate() {

        super.onCreate()

        NotificationChannels
            .createNotificationChannels(
                context = this
            )
    }

    val database: RachaProDatabase by lazy {
        RachaProDatabase.getDatabase(this)
    }

    val userRepository: UserRepository by lazy {
        UserRepository(
            userDao = database.userDao()
        )
    }

    val categoryRepository: CategoryRepository by lazy {

        CategoryRepository(
            categoryDao =
                database.categoryDao()
        )
    }

    val activityRepository: ActivityRepository by lazy {

        ActivityRepository(
            activityDao =
                database.activityDao(),
            categoryDao =
                database.categoryDao()
        )
    }

    val sessionManager: SessionManager by lazy {
        SessionManager(this)
    }

    val subtaskRepository by lazy {

        SubtaskRepository(
            subtaskDao =
                database.subtaskDao(),

            activityDao =
                database.activityDao()
        )
    }

    val reminderRepository by lazy {

        ReminderRepository(
            reminderDao =
                database.reminderDao(),

            activityDao =
                database.activityDao()
        )
    }

    val reminderScheduler by lazy {

        ReminderScheduler(
            context =
                applicationContext
        )
    }

    val pomodoroRepository by lazy {
        PomodoroRepository(
            pomodoroSessionDao = database.pomodoroSessionDao(),
            activityDao = database.activityDao()
        )
    }

    val userPreferencesManager by lazy {
        UserPreferencesManager(
            context = applicationContext
        )
    }

}