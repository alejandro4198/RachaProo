package com.example.rachapro.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.rachapro.data.local.dao.UserDao
import com.example.rachapro.data.local.entity.UserEntity
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.rachapro.data.local.dao.ActivityDao
import com.example.rachapro.data.local.dao.CategoryDao
import com.example.rachapro.data.local.entity.ActivityEntity
import com.example.rachapro.data.local.entity.CategoryEntity
import com.example.rachapro.data.local.entity.SubtaskEntity
import com.example.rachapro.data.local.dao.SubtaskDao
import com.example.rachapro.data.local.entity.ReminderEntity
import com.example.rachapro.data.local.dao.ReminderDao
import com.example.rachapro.data.local.entity.PomodoroSessionEntity
import com.example.rachapro.data.local.dao.PomodoroSessionDao


@Database(
    entities = [
        UserEntity::class,
        CategoryEntity::class,
        ActivityEntity::class,
        SubtaskEntity::class,
        ReminderEntity::class,
        PomodoroSessionEntity::class,
    ],
    version = 5,
    exportSchema = false
)

abstract class RachaProDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    abstract fun categoryDao(): CategoryDao

    abstract fun activityDao(): ActivityDao

    abstract fun subtaskDao(): SubtaskDao

    abstract fun reminderDao(): ReminderDao

    abstract fun pomodoroSessionDao(): PomodoroSessionDao

    companion object {

        val MIGRATION_4_5 =
            object : Migration(
                4,
                5
            ) {

                override fun migrate(
                    db: SupportSQLiteDatabase
                ) {

                    db.execSQL(
                        """
                CREATE TABLE IF NOT EXISTS `pomodoro_sessions` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `userId` INTEGER NOT NULL,
                    `activityId` INTEGER,
                    `type` TEXT NOT NULL,
                    `plannedDurationSeconds` INTEGER NOT NULL,
                    `status` TEXT NOT NULL,
                    `startedAtMillis` INTEGER NOT NULL,
                    `pausedAtMillis` INTEGER,
                    `totalPausedMillis` INTEGER NOT NULL,
                    `completedAtMillis` INTEGER,
                    `completedDateEpochDay` INTEGER,
                    `createdAt` INTEGER NOT NULL,
                    `updatedAt` INTEGER NOT NULL
                )
                """.trimIndent()
                    )

                    db.execSQL(
                        """
                CREATE INDEX IF NOT EXISTS
                `index_pomodoro_sessions_userId`
                ON `pomodoro_sessions` (`userId`)
                """.trimIndent()
                    )

                    db.execSQL(
                        """
                CREATE INDEX IF NOT EXISTS
                `index_pomodoro_sessions_activityId`
                ON `pomodoro_sessions` (`activityId`)
                """.trimIndent()
                    )

                    db.execSQL(
                        """
                CREATE INDEX IF NOT EXISTS
                `index_pomodoro_sessions_status`
                ON `pomodoro_sessions` (`status`)
                """.trimIndent()
                    )

                    db.execSQL(
                        """
                CREATE INDEX IF NOT EXISTS
                `index_pomodoro_sessions_completedDateEpochDay`
                ON `pomodoro_sessions` (`completedDateEpochDay`)
                """.trimIndent()
                    )

                    db.execSQL(
                        """
                CREATE INDEX IF NOT EXISTS
                `index_pomodoro_sessions_userId_status`
                ON `pomodoro_sessions` (`userId`, `status`)
                """.trimIndent()
                    )
                }
            }

        private val MIGRATION_3_4 =
            object : Migration(
                3,
                4
            ) {

                override fun migrate(
                    db: SupportSQLiteDatabase
                ) {

                    db.execSQL(
                        """
                CREATE TABLE IF NOT EXISTS `reminders` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `userId` INTEGER NOT NULL,
                    `activityId` INTEGER,
                    `title` TEXT NOT NULL,
                    `message` TEXT NOT NULL,
                    `triggerAtMillis` INTEGER NOT NULL,
                    `status` TEXT NOT NULL,
                    `createdAt` INTEGER NOT NULL,
                    `updatedAt` INTEGER NOT NULL,
                    `deliveredAt` INTEGER
                )
                """.trimIndent()
                    )

                    db.execSQL(
                        """
                CREATE INDEX IF NOT EXISTS
                `index_reminders_userId`
                ON `reminders` (`userId`)
                """.trimIndent()
                    )

                    db.execSQL(
                        """
                CREATE INDEX IF NOT EXISTS
                `index_reminders_activityId`
                ON `reminders` (`activityId`)
                """.trimIndent()
                    )

                    db.execSQL(
                        """
                CREATE INDEX IF NOT EXISTS
                `index_reminders_status`
                ON `reminders` (`status`)
                """.trimIndent()
                    )

                    db.execSQL(
                        """
                CREATE INDEX IF NOT EXISTS
                `index_reminders_triggerAtMillis`
                ON `reminders` (`triggerAtMillis`)
                """.trimIndent()
                    )

                    db.execSQL(
                        """
                CREATE INDEX IF NOT EXISTS
                `index_reminders_userId_status_triggerAtMillis`
                ON `reminders`
                (`userId`, `status`, `triggerAtMillis`)
                """.trimIndent()
                    )
                }
            }

        private val MIGRATION_2_3 =
            object : Migration(
                2,
                3
            ) {

                override fun migrate(
                    db: SupportSQLiteDatabase
                ) {

                    db.execSQL(
                        """
                CREATE TABLE IF NOT EXISTS `subtasks` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `activityId` INTEGER NOT NULL,
                    `title` TEXT NOT NULL,
                    `isCompleted` INTEGER NOT NULL,
                    `createdAt` INTEGER NOT NULL,
                    `updatedAt` INTEGER NOT NULL,
                    `completedAt` INTEGER
                )
                """.trimIndent()
                    )

                    db.execSQL(
                        """
                CREATE INDEX IF NOT EXISTS
                `index_subtasks_activityId`
                ON `subtasks` (`activityId`)
                """.trimIndent()
                    )

                    db.execSQL(
                        """
                CREATE INDEX IF NOT EXISTS
                `index_subtasks_activityId_isCompleted`
                ON `subtasks`
                (`activityId`, `isCompleted`)
                """.trimIndent()
                    )
                }
            }

        @Volatile
        private var INSTANCE: RachaProDatabase? = null

        val MIGRATION_1_2 =
            object : Migration(1, 2) {

                override fun migrate(
                    database: SupportSQLiteDatabase
                ) {

                    /*
                     * -----------------------------------------------------
                     * CATEGORIES
                     * -----------------------------------------------------
                     */

                    database.execSQL(
                        """
                CREATE TABLE IF NOT EXISTS `categories` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `userId` INTEGER NOT NULL,
                    `name` TEXT NOT NULL,
                    `icon` TEXT,
                    `createdAt` INTEGER NOT NULL,
                    `updatedAt` INTEGER NOT NULL,
                    `isActive` INTEGER NOT NULL
                )
                """.trimIndent()
                    )

                    database.execSQL(
                        """
                CREATE UNIQUE INDEX IF NOT EXISTS
                `index_categories_userId_name`
                ON `categories` (`userId`, `name`)
                """.trimIndent()
                    )

                    /*
                     * -----------------------------------------------------
                     * ACTIVITIES
                     * -----------------------------------------------------
                     */

                    database.execSQL(
                        """
                CREATE TABLE IF NOT EXISTS `activities` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `userId` INTEGER NOT NULL,
                    `categoryId` INTEGER NOT NULL,
                    `title` TEXT NOT NULL,
                    `description` TEXT NOT NULL,
                    `dueDateEpochDay` INTEGER NOT NULL,
                    `dueTimeMinutes` INTEGER,
                    `priority` TEXT NOT NULL,
                    `status` TEXT NOT NULL,
                    `repeatRule` TEXT,
                    `createdAt` INTEGER NOT NULL,
                    `updatedAt` INTEGER NOT NULL,
                    `completedAt` INTEGER,
                    `completedDateEpochDay` INTEGER,
                    `isDeleted` INTEGER NOT NULL,
                    `deletedAt` INTEGER
                )
                """.trimIndent()
                    )

                    /*
                     * Índices de activities
                     */

                    database.execSQL(
                        """
                CREATE INDEX IF NOT EXISTS
                `index_activities_userId`
                ON `activities` (`userId`)
                """.trimIndent()
                    )

                    database.execSQL(
                        """
                CREATE INDEX IF NOT EXISTS
                `index_activities_categoryId`
                ON `activities` (`categoryId`)
                """.trimIndent()
                    )

                    database.execSQL(
                        """
                CREATE INDEX IF NOT EXISTS
                `index_activities_status`
                ON `activities` (`status`)
                """.trimIndent()
                    )

                    database.execSQL(
                        """
                CREATE INDEX IF NOT EXISTS
                `index_activities_dueDateEpochDay`
                ON `activities` (`dueDateEpochDay`)
                """.trimIndent()
                    )

                    database.execSQL(
                        """
                CREATE INDEX IF NOT EXISTS
                `index_activities_userId_isDeleted_dueDateEpochDay`
                ON `activities`
                (`userId`, `isDeleted`, `dueDateEpochDay`)
                """.trimIndent()
                    )
                }
            }

        fun getDatabase(context: Context): RachaProDatabase {

            return INSTANCE ?: synchronized(this) {

                val instance =
                    Room.databaseBuilder(
                        context.applicationContext,
                        RachaProDatabase::class.java,
                        "rachapro_database"
                    )
                        .addMigrations(
                            MIGRATION_1_2,
                            MIGRATION_2_3,
                            MIGRATION_3_4,
                            MIGRATION_4_5,
                        )
                        .build()

                INSTANCE = instance

                instance
            }
        }
    }
}