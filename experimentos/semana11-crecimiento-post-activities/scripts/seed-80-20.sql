\set ON_ERROR_STOP on

SELECT current_database() = 'rachapro_perf_s11' AS db_ok
\gset

SELECT current_user = 'rachapro_user_s11' AS user_ok
\gset

\if :db_ok
\else
    \echo 'ERROR: base de datos no autorizada'
    \quit 20
\endif

\if :user_ok
\else
    \echo 'ERROR: usuario PostgreSQL no autorizado'
    \quit 21
\endif

BEGIN;

SET LOCAL lock_timeout = '5s';

SELECT pg_try_advisory_xact_lock(11002026) AS advisory_ok
\gset

\if :advisory_ok
\else
    \echo 'ERROR: otra restauracion posee el advisory lock'
    \quit 22
\endif

LOCK TABLE achievements
    IN ACCESS EXCLUSIVE MODE;

LOCK TABLE pomodoro_sessions
    IN ACCESS EXCLUSIVE MODE;

LOCK TABLE reminders
    IN ACCESS EXCLUSIVE MODE;

LOCK TABLE subtasks
    IN ACCESS EXCLUSIVE MODE;

LOCK TABLE activities
    IN ACCESS EXCLUSIVE MODE;

LOCK TABLE categories
    IN ACCESS EXCLUSIVE MODE;

LOCK TABLE users
    IN ACCESS EXCLUSIVE MODE;

TRUNCATE TABLE
    achievements,
    pomodoro_sessions,
    reminders,
    subtasks,
    activities,
    categories,
    users
RESTART IDENTITY;

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
    'Load Test High ' || LPAD(gs::text, 4, '0'),
    'loadtest-high-' || LPAD(gs::text, 4, '0') || '@rachapro.test',
    '__S11_PASSWORD_HASH__',
    '__S11_PASSWORD_SALT__',
    1,
    TRUE,
    1760400000000,
    1760400000000
FROM generate_series(1, 200) AS gs;

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
    'Load Test Low ' || LPAD(gs::text, 4, '0'),
    'loadtest-low-' || LPAD(gs::text, 4, '0') || '@rachapro.test',
    '__S11_PASSWORD_HASH__',
    '__S11_PASSWORD_SALT__',
    1,
    TRUE,
    1760400000000,
    1760400000000
FROM generate_series(1, 800) AS gs;

INSERT INTO categories (
    user_id,
    name,
    icon,
    created_at,
    updated_at,
    is_active
)
SELECT
    id,
    'Universidad',
    NULL,
    1760400000000,
    1760400000000,
    TRUE
FROM users
WHERE
    email LIKE 'loadtest-high-%@rachapro.test'
    OR email LIKE 'loadtest-low-%@rachapro.test';

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
    'S11-' ||
        SPLIT_PART(u.email, '@', 1) ||
        '-' ||
        LPAD(gs::text, 4, '0'),
    'Dato sintetico Semana 11',
    20740 + ((gs - 1) % 30),
    NULL,
    'MEDIUM',
    'PENDING',
    NULL,
    1760400000000,
    1760400000000,
    NULL,
    NULL,
    FALSE,
    NULL
FROM users u
JOIN categories c
    ON c.user_id = u.id
   AND c.name = 'Universidad'
   AND c.is_active = TRUE
CROSS JOIN LATERAL generate_series(
    1,
    CASE
        WHEN u.email LIKE 'loadtest-high-%@rachapro.test'
            THEN 400
        WHEN u.email LIKE 'loadtest-low-%@rachapro.test'
            THEN 25
    END
) AS gs
WHERE
    u.email LIKE 'loadtest-high-%@rachapro.test'
    OR u.email LIKE 'loadtest-low-%@rachapro.test';

DO $$
DECLARE
    users_total BIGINT;
    users_expected BIGINT;
    users_high BIGINT;
    users_low BIGINT;
    users_outside_groups BIGINT;

    categories_total BIGINT;
    categories_active BIGINT;
    categories_inactive BIGINT;
    users_bad_categories BIGINT;

    activities_total BIGINT;
    activities_active BIGINT;
    activities_deleted BIGINT;
    activities_high BIGINT;
    activities_low BIGINT;
    activities_outside_groups BIGINT;

    high_min BIGINT;
    high_max BIGINT;

    low_min BIGINT;
    low_max BIGINT;

    bad_category_owner BIGINT;
BEGIN
    SELECT COUNT(*)
    INTO users_total
    FROM users;

    SELECT COUNT(*)
    INTO users_expected
    FROM users
    WHERE
        email LIKE 'loadtest-high-%@rachapro.test'
        OR email LIKE 'loadtest-low-%@rachapro.test';

    SELECT COUNT(*)
    INTO users_high
    FROM users
    WHERE email LIKE 'loadtest-high-%@rachapro.test';

    SELECT COUNT(*)
    INTO users_low
    FROM users
    WHERE email LIKE 'loadtest-low-%@rachapro.test';

    SELECT COUNT(*)
    INTO users_outside_groups
    FROM users
    WHERE NOT (
        email LIKE 'loadtest-high-%@rachapro.test'
        OR email LIKE 'loadtest-low-%@rachapro.test'
    );

    SELECT COUNT(*)
    INTO categories_total
    FROM categories;

    SELECT COUNT(*)
    INTO categories_active
    FROM categories
    WHERE is_active = TRUE;

    SELECT COUNT(*)
    INTO categories_inactive
    FROM categories
    WHERE is_active = FALSE;

    SELECT COUNT(*)
    INTO users_bad_categories
    FROM (
        SELECT
            u.id,
            COUNT(c.id) FILTER (WHERE c.is_active = TRUE) AS active_category_count
        FROM users u
        LEFT JOIN categories c
            ON c.user_id = u.id
        GROUP BY u.id
        HAVING COUNT(c.id) FILTER (WHERE c.is_active = TRUE) <> 1
    ) q;

    SELECT COUNT(*)
    INTO activities_total
    FROM activities;

    SELECT COUNT(*)
    INTO activities_active
    FROM activities
    WHERE is_deleted = FALSE;

    SELECT COUNT(*)
    INTO activities_deleted
    FROM activities
    WHERE is_deleted = TRUE;

    SELECT COUNT(*)
    INTO activities_high
    FROM activities a
    JOIN users u
        ON u.id = a.user_id
    WHERE
        u.email LIKE 'loadtest-high-%@rachapro.test'
        AND a.is_deleted = FALSE;

    SELECT COUNT(*)
    INTO activities_low
    FROM activities a
    JOIN users u
        ON u.id = a.user_id
    WHERE
        u.email LIKE 'loadtest-low-%@rachapro.test'
        AND a.is_deleted = FALSE;

    SELECT COUNT(*)
    INTO activities_outside_groups
    FROM activities a
    JOIN users u
        ON u.id = a.user_id
    WHERE NOT (
        u.email LIKE 'loadtest-high-%@rachapro.test'
        OR u.email LIKE 'loadtest-low-%@rachapro.test'
    );

    SELECT
        MIN(activity_count),
        MAX(activity_count)
    INTO
        high_min,
        high_max
    FROM (
        SELECT
            u.id,
            COUNT(a.id) FILTER (WHERE a.is_deleted = FALSE) AS activity_count
        FROM users u
        LEFT JOIN activities a
            ON a.user_id = u.id
        WHERE
            u.email LIKE 'loadtest-high-%@rachapro.test'
        GROUP BY u.id
    ) q;

    SELECT
        MIN(activity_count),
        MAX(activity_count)
    INTO
        low_min,
        low_max
    FROM (
        SELECT
            u.id,
            COUNT(a.id) FILTER (WHERE a.is_deleted = FALSE) AS activity_count
        FROM users u
        LEFT JOIN activities a
            ON a.user_id = u.id
        WHERE
            u.email LIKE 'loadtest-low-%@rachapro.test'
        GROUP BY u.id
    ) q;

    SELECT COUNT(*)
    INTO bad_category_owner
    FROM activities a
    JOIN categories c
        ON c.id = a.category_id
    WHERE a.user_id <> c.user_id;

    IF users_total <> 1000 THEN
        RAISE EXCEPTION
            'Usuarios totales = %, esperado 1000',
            users_total;
    END IF;

    IF users_expected <> 1000 THEN
        RAISE EXCEPTION
            'Usuarios pertenecientes a grupos A/B = %, esperado 1000',
            users_expected;
    END IF;

    IF users_outside_groups <> 0 THEN
        RAISE EXCEPTION
            'Usuarios fuera de los grupos A/B = %, esperado 0',
            users_outside_groups;
    END IF;

    IF users_high <> 200 THEN
        RAISE EXCEPTION
            'Usuarios grupo A = %, esperado 200',
            users_high;
    END IF;

    IF users_low <> 800 THEN
        RAISE EXCEPTION
            'Usuarios grupo B = %, esperado 800',
            users_low;
    END IF;

    IF categories_total <> 1000 THEN
        RAISE EXCEPTION
            'Categorias totales = %, esperado 1000',
            categories_total;
    END IF;

    IF categories_active <> 1000 THEN
        RAISE EXCEPTION
            'Categorias activas = %, esperado 1000',
            categories_active;
    END IF;

    IF categories_inactive <> 0 THEN
        RAISE EXCEPTION
            'Categorias inactivas = %, esperado 0',
            categories_inactive;
    END IF;

    IF users_bad_categories <> 0 THEN
        RAISE EXCEPTION
            'Usuarios sin exactamente una categoria activa = %',
            users_bad_categories;
    END IF;

    IF activities_total <> 100000 THEN
        RAISE EXCEPTION
            'Actividades totales = %, esperado 100000',
            activities_total;
    END IF;

    IF activities_active <> 100000 THEN
        RAISE EXCEPTION
            'Actividades activas = %, esperado 100000',
            activities_active;
    END IF;

    IF activities_deleted <> 0 THEN
        RAISE EXCEPTION
            'Actividades eliminadas = %, esperado 0',
            activities_deleted;
    END IF;

    IF activities_outside_groups <> 0 THEN
        RAISE EXCEPTION
            'Actividades fuera de los grupos A/B = %, esperado 0',
            activities_outside_groups;
    END IF;

    IF activities_high <> 80000 THEN
        RAISE EXCEPTION
            'Actividades grupo A = %, esperado 80000',
            activities_high;
    END IF;

    IF activities_low <> 20000 THEN
        RAISE EXCEPTION
            'Actividades grupo B = %, esperado 20000',
            activities_low;
    END IF;

    IF high_min <> 400 OR high_max <> 400 THEN
        RAISE EXCEPTION
            'Grupo A min/max = %/%, esperado 400/400',
            high_min,
            high_max;
    END IF;

    IF low_min <> 25 OR low_max <> 25 THEN
        RAISE EXCEPTION
            'Grupo B min/max = %/%, esperado 25/25',
            low_min,
            low_max;
    END IF;

    IF bad_category_owner <> 0 THEN
        RAISE EXCEPTION
            'Actividades asociadas a categoria de otro usuario = %',
            bad_category_owner;
    END IF;
END
$$;

COMMIT;

ANALYZE users;
ANALYZE categories;
ANALYZE activities;

SELECT
    (SELECT COUNT(*) FROM users) AS users,
    (SELECT COUNT(*) FROM categories) AS categories,
    (SELECT COUNT(*) FROM activities) AS activities;
