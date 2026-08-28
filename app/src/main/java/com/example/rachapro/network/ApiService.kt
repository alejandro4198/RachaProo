package com.example.rachapro.network

import com.example.rachapro.network.dto.LoginRequest
import com.example.rachapro.network.dto.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST
import com.example.rachapro.network.dto.CategoryResponse
import retrofit2.http.GET
import com.example.rachapro.network.dto.CreateCategoryRequest
import com.example.rachapro.network.dto.ActivityResponse
import com.example.rachapro.network.dto.CreateActivityRequest
import com.example.rachapro.network.dto.UpdateActivityRequest
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.PATCH
import retrofit2.http.PUT
import retrofit2.http.Path
import com.example.rachapro.network.dto.SubtaskResponse
import com.example.rachapro.network.dto.CreateSubtaskRequest
import com.example.rachapro.network.dto.UpdateSubtaskRequest
import com.example.rachapro.network.dto.CreateReminderRequest
import com.example.rachapro.network.dto.ReminderResponse
import com.example.rachapro.network.dto.CreatePomodoroSessionRequest
import com.example.rachapro.network.dto.PomodoroSessionResponse
import com.example.rachapro.network.dto.AchievementResponse
import com.example.rachapro.network.dto.CreateAchievementRequest
import com.example.rachapro.network.dto.CreateUserRequest
import com.example.rachapro.network.dto.UserResponse
import com.example.rachapro.network.dto.UpdateUserRequest

interface ApiService {

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse

    @POST("api/users")
    suspend fun createUser(
        @Body request: CreateUserRequest
    ): UserResponse

    @GET("api/categories")
    suspend fun getCategories(): List<CategoryResponse>

    @POST("api/categories")
    suspend fun createCategory(
        @Body request: CreateCategoryRequest
    ): CategoryResponse

    @GET("api/activities")
    suspend fun getActivities(): List<ActivityResponse>

    @GET("api/activities/{id}")
    suspend fun getActivityById(
        @Path("id") activityId: Long
    ): ActivityResponse

    @POST("api/activities")
    suspend fun createActivity(
        @Body request: CreateActivityRequest
    ): ActivityResponse

    @PATCH("api/activities/refresh-statuses")
    suspend fun refreshActivityStatuses(): List<ActivityResponse>

    @PUT("api/activities/{id}")
    suspend fun updateActivity(
        @Path("id") activityId: Long,
        @Body request: UpdateActivityRequest
    ): ActivityResponse

    @PATCH("api/activities/{id}/complete")
    suspend fun completeActivity(
        @Path("id") activityId: Long
    ): ActivityResponse

    @DELETE("api/activities/{id}")
    suspend fun deleteActivity(
        @Path("id") activityId: Long
    ): Response<Unit>

    @GET("api/activities/{activityId}/subtasks")
    suspend fun getSubtasks(
        @Path("activityId") activityId: Long
    ): List<SubtaskResponse>

    @POST("api/activities/{activityId}/subtasks")
    suspend fun createSubtask(
        @Path("activityId") activityId: Long,
        @Body request: CreateSubtaskRequest
    ): SubtaskResponse

    @PUT("api/activities/{activityId}/subtasks/{subtaskId}")
    suspend fun updateSubtask(
        @Path("activityId") activityId: Long,
        @Path("subtaskId") subtaskId: Long,
        @Body request: UpdateSubtaskRequest
    ): SubtaskResponse

    @PATCH("api/activities/{activityId}/subtasks/{subtaskId}/complete")
    suspend fun completeSubtask(
        @Path("activityId") activityId: Long,
        @Path("subtaskId") subtaskId: Long
    ): SubtaskResponse

    @DELETE("api/activities/{activityId}/subtasks/{subtaskId}")
    suspend fun deleteSubtask(
        @Path("activityId") activityId: Long,
        @Path("subtaskId") subtaskId: Long
    ): Response<Unit>

    @PATCH("api/activities/{activityId}/subtasks/{subtaskId}/uncomplete")
    suspend fun uncompleteSubtask(
        @Path("activityId") activityId: Long,
        @Path("subtaskId") subtaskId: Long
    ): SubtaskResponse

    @GET("api/reminders")
    suspend fun getReminders(): List<ReminderResponse>

    @POST("api/reminders")
    suspend fun createReminder(
        @Body request: CreateReminderRequest
    ): ReminderResponse

    @PATCH("api/reminders/{id}/delivered")
    suspend fun markReminderDelivered(
        @Path("id") reminderId: Long
    ): ReminderResponse

    @PATCH("api/reminders/{id}/cancel")
    suspend fun cancelReminder(
        @Path("id") reminderId: Long
    ): ReminderResponse

    @GET("api/pomodoro-sessions")
    suspend fun getPomodoroSessions(): List<PomodoroSessionResponse>

    @POST("api/pomodoro-sessions")
    suspend fun createPomodoroSession(
        @Body request: CreatePomodoroSessionRequest
    ): PomodoroSessionResponse

    @PATCH("api/pomodoro-sessions/{id}/pause")
    suspend fun pausePomodoroSession(
        @Path("id") sessionId: Long
    ): PomodoroSessionResponse

    @PATCH("api/pomodoro-sessions/{id}/resume")
    suspend fun resumePomodoroSession(
        @Path("id") sessionId: Long
    ): PomodoroSessionResponse

    @PATCH("api/pomodoro-sessions/{id}/complete")
    suspend fun completePomodoroSession(
        @Path("id") sessionId: Long
    ): PomodoroSessionResponse

    @PATCH("api/pomodoro-sessions/{id}/cancel")
    suspend fun cancelPomodoroSession(
        @Path("id") sessionId: Long
    ): PomodoroSessionResponse

    @GET("api/achievements")
    suspend fun getAchievements(): List<AchievementResponse>

    @POST("api/achievements")
    suspend fun createAchievement(
        @Body request: CreateAchievementRequest
    ): AchievementResponse

    @GET("api/auth/me")
    suspend fun getCurrentUser(): UserResponse

    @PATCH("api/users/me")
    suspend fun updateCurrentUser(
        @Body request: UpdateUserRequest
    ): UserResponse
}