-- EXP-003
-- Reconstruye 500 usuarios sintéticos y 500.000 actividades.
-- loadtest001 debe existir previamente y haber sido creado mediante la API.

WITH template AS (
    SELECT
        password_hash,
        password_salt,
        semester,
        accepted_privacy_policy
    FROM users
    WHERE email = 'loadtest001@rachapro.test'
)
INSERT INTO users (
    full_name,
    email,
    password_hash,
    password_salt,
    semester,
    accepted_privacy_policy,
    created_at,
    updated_at
)
SELECT
    'Load Test ' || LPAD(gs::text, 3, '0'),
    'loadtest' || LPAD(gs::text, 3, '0') || '@rachapro.test',
    t.password_hash,
    t.password_salt,
    t.semester,
    t.accepted_privacy_policy,
    (EXTRACT(EPOCH FROM clock_timestamp()) * 1000)::bigint,
    (EXTRACT(EPOCH FROM clock_timestamp()) * 1000)::bigint
FROM generate_series(2, 500) gs
CROSS JOIN template t;


INSERT INTO categories (
    user_id,
    name,
    created_at,
    updated_at,
    is_active
)
SELECT
    u.id,
    names.name,
    (EXTRACT(EPOCH FROM clock_timestamp()) * 1000)::bigint,
    (EXTRACT(EPOCH FROM clock_timestamp()) * 1000)::bigint,
    TRUE
FROM users u
CROSS JOIN (
    VALUES ('Universidad'), ('Personal'), ('Trabajo')
) AS names(name)
WHERE u.email LIKE 'loadtest%@rachapro.test'
  AND u.email <> 'loadtest001@rachapro.test';


INSERT INTO activities (
    user_id,
    category_id,
    title,
    description,
    due_date_epoch_day,
    due_time_minutes,
    priority,
    status,
    repeat_rule,
    created_at,
    updated_at,
    completed_at,
    completed_date_epoch_day,
    is_deleted,
    deleted_at
)
SELECT
    u.id,
    c.id,
    'Actividad ' || LPAD(gs::text, 4, '0'),
    'Dato sintetico EXP-003',
    (
        (CURRENT_DATE - DATE '1970-01-01')
        + ((gs - 1) % 30)
    )::bigint,
    NULL,
    'MEDIUM',
    'PENDING',
    NULL,
    (EXTRACT(EPOCH FROM clock_timestamp()) * 1000)::bigint,
    (EXTRACT(EPOCH FROM clock_timestamp()) * 1000)::bigint,
    NULL,
    NULL,
    FALSE,
    NULL
FROM users u
JOIN categories c
    ON c.user_id = u.id
   AND c.name = 'Universidad'
CROSS JOIN generate_series(1, 1000) gs
WHERE u.email LIKE 'loadtest%@rachapro.test';
