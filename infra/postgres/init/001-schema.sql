CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    full_name TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    password_salt TEXT NOT NULL,
    semester INTEGER NOT NULL,
    accepted_privacy_policy BOOLEAN NOT NULL,
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL
);

CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name TEXT NOT NULL,
    icon TEXT,
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT fk_categories_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_categories_user_name
        UNIQUE (user_id, name)
);

CREATE TABLE activities (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    title TEXT NOT NULL,
    description TEXT NOT NULL DEFAULT '',
    due_date_epoch_day BIGINT NOT NULL,
    due_time_minutes INTEGER,
    priority TEXT NOT NULL,
    status TEXT NOT NULL,
    repeat_rule TEXT,
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL,
    completed_at BIGINT,
    completed_date_epoch_day BIGINT,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at BIGINT,

    CONSTRAINT fk_activities_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_activities_category
        FOREIGN KEY (category_id)
        REFERENCES categories(id),

    CONSTRAINT chk_activity_priority
        CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH')),

    CONSTRAINT chk_activity_status
        CHECK (status IN ('PENDING', 'OVERDUE', 'COMPLETED'))
);

CREATE TABLE subtasks (
    id BIGSERIAL PRIMARY KEY,
    activity_id BIGINT NOT NULL,
    title TEXT NOT NULL,
    is_completed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL,
    completed_at BIGINT,

    CONSTRAINT fk_subtasks_activity
        FOREIGN KEY (activity_id)
        REFERENCES activities(id)
        ON DELETE CASCADE
);

CREATE TABLE reminders (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    activity_id BIGINT,
    title TEXT NOT NULL,
    message TEXT NOT NULL DEFAULT '',
    trigger_at_millis BIGINT NOT NULL,
    status TEXT NOT NULL DEFAULT 'SCHEDULED',
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL,
    delivered_at BIGINT,

    CONSTRAINT fk_reminders_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_reminders_activity
        FOREIGN KEY (activity_id)
        REFERENCES activities(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_reminder_status
        CHECK (status IN ('SCHEDULED', 'DELIVERED', 'CANCELLED'))
);

CREATE TABLE pomodoro_sessions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    activity_id BIGINT,
    type TEXT NOT NULL DEFAULT 'FOCUS',
    planned_duration_seconds INTEGER NOT NULL,
    status TEXT NOT NULL DEFAULT 'RUNNING',
    started_at_millis BIGINT NOT NULL,
    paused_at_millis BIGINT,
    total_paused_millis BIGINT NOT NULL DEFAULT 0,
    completed_at_millis BIGINT,
    completed_date_epoch_day BIGINT,
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL,

    CONSTRAINT fk_pomodoro_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_pomodoro_activity
        FOREIGN KEY (activity_id)
        REFERENCES activities(id)
        ON DELETE SET NULL,

    CONSTRAINT chk_pomodoro_type
        CHECK (type IN ('FOCUS', 'SHORT_BREAK', 'LONG_BREAK')),

    CONSTRAINT chk_pomodoro_status
        CHECK (status IN ('RUNNING', 'PAUSED', 'COMPLETED', 'CANCELLED'))
);

CREATE TABLE achievements (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type TEXT NOT NULL,
    unlocked_at BIGINT NOT NULL,

    CONSTRAINT fk_achievements_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_achievements_user_type
        UNIQUE (user_id, type),

    CONSTRAINT chk_achievement_type
        CHECK (
            type IN (
                'FIRST_ACTIVITY_COMPLETED',
                'FIRST_FOCUS_POMODORO',
                'STREAK_3_DAYS',
                'STREAK_7_DAYS',
                'ACTIVITIES_10_COMPLETED',
                'POMODOROS_10_COMPLETED'
            )
        )
);

CREATE INDEX idx_activities_user_id
    ON activities(user_id);

CREATE INDEX idx_activities_category_id
    ON activities(category_id);

CREATE INDEX idx_activities_status
    ON activities(status);

CREATE INDEX idx_activities_due_date
    ON activities(due_date_epoch_day);

CREATE INDEX idx_activities_user_deleted_due_date
    ON activities(user_id, is_deleted, due_date_epoch_day);

CREATE INDEX idx_subtasks_activity_id
    ON subtasks(activity_id);

CREATE INDEX idx_subtasks_activity_completed
    ON subtasks(activity_id, is_completed);

CREATE INDEX idx_reminders_user_id
    ON reminders(user_id);

CREATE INDEX idx_reminders_activity_id
    ON reminders(activity_id);

CREATE INDEX idx_reminders_status
    ON reminders(status);

CREATE INDEX idx_reminders_trigger
    ON reminders(trigger_at_millis);

CREATE INDEX idx_reminders_user_status_trigger
    ON reminders(user_id, status, trigger_at_millis);

CREATE INDEX idx_pomodoro_user_id
    ON pomodoro_sessions(user_id);

CREATE INDEX idx_pomodoro_activity_id
    ON pomodoro_sessions(activity_id);

CREATE INDEX idx_pomodoro_status
    ON pomodoro_sessions(status);

CREATE INDEX idx_pomodoro_completed_date
    ON pomodoro_sessions(completed_date_epoch_day);

CREATE INDEX idx_pomodoro_user_status
    ON pomodoro_sessions(user_id, status);

CREATE INDEX idx_achievements_user_id
    ON achievements(user_id);