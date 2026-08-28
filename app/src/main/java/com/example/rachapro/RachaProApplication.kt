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
import com.example.rachapro.data.repository.AchievementRepository
import com.example.rachapro.data.local.UserPreferencesManager
import com.example.rachapro.network.RetrofitClient
import com.example.rachapro.network.ApiService

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
            userDao = database.userDao(),
            apiService = apiService
        )
    }

    val categoryRepository: CategoryRepository by lazy {
        CategoryRepository(
            categoryDao = database.categoryDao(),
            apiService = apiService
        )
    }

    val activityRepository: ActivityRepository by lazy {
        ActivityRepository(
            activityDao = database.activityDao(),
            categoryDao = database.categoryDao(),
            apiService = apiService
        )
    }

    val sessionManager: SessionManager by lazy {
        SessionManager(this)
    }

    val apiService: ApiService by lazy {
        RetrofitClient.create(
            sessionManager = sessionManager
        )
    }

    val subtaskRepository by lazy {
        SubtaskRepository(
            subtaskDao = database.subtaskDao(),
            activityDao = database.activityDao(),
            apiService = apiService
        )
    }

    val reminderRepository by lazy {

        ReminderRepository(
            reminderDao =
                database.reminderDao(),

            activityDao =
                database.activityDao(),

            apiService =
                apiService
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
            activityDao = database.activityDao(),
            apiService = apiService
        )
    }

    val userPreferencesManager by lazy {
        UserPreferencesManager(
            context = applicationContext
        )
    }

    val achievementRepository by lazy {
        AchievementRepository(
            achievementDao = database.achievementDao(),
            apiService = apiService
        )
    }

}