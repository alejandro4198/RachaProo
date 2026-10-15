param(
    [string]$Container = "rachapro-postgres-s11",
    [string]$Database  = "rachapro_perf_s11",
    [string]$DbUser    = "rachapro_user_s11"
)

$ErrorActionPreference = "Stop"

$ExpectedContainer = "rachapro-postgres-s11"
$ExpectedDatabase  = "rachapro_perf_s11"
$ExpectedDbUser    = "rachapro_user_s11"

if ($Container -ne $ExpectedContainer) {
    throw "Contenedor no autorizado: $Container"
}

if ($Database -ne $ExpectedDatabase) {
    throw "Base no autorizada: $Database"
}

if ($DbUser -ne $ExpectedDbUser) {
    throw "Usuario PostgreSQL no autorizado: $DbUser"
}

$running = docker inspect $Container `
    --format '{{.State.Running}}'

if ($LASTEXITCODE -ne 0) {
    throw "No fue posible inspeccionar el contenedor."
}

if ("$running".Trim() -ne "true") {
    throw "El contenedor experimental no esta ejecutandose."
}

$identity = docker exec $Container `
    psql `
    -U $DbUser `
    -d $Database `
    -v ON_ERROR_STOP=1 `
    -tAc "SELECT current_database() || '|' || current_user;"

if ($LASTEXITCODE -ne 0) {
    throw "No fue posible verificar la identidad PostgreSQL."
}

$identity = "$identity".Trim()

if ($identity -ne "$ExpectedDatabase|$ExpectedDbUser") {
    throw "Destino PostgreSQL inesperado: $identity"
}

$sql = @'
\set ON_ERROR_STOP on

SELECT
    CASE
        WHEN
            (SELECT COUNT(*) FROM users) = 0
            AND (SELECT COUNT(*) FROM categories) = 0
            AND (SELECT COUNT(*) FROM activities) = 0
        THEN 'VACIA'

        WHEN
            (SELECT COUNT(*) FROM users) = 1000
            AND (SELECT COUNT(*) FROM categories) = 1000
            AND (SELECT COUNT(*) FROM activities) = 100000
        THEN 'CANDIDATA_COMPLETA'

        ELSE 'PARCIAL_O_INVALIDA'
    END AS estado_dataset,

    (SELECT COUNT(*) FROM users) AS usuarios,
    (SELECT COUNT(*) FROM categories) AS categorias,
    (SELECT COUNT(*) FROM activities) AS actividades;

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
    INTO categories_total
    FROM categories;

    SELECT COUNT(*)
    INTO activities_total
    FROM activities;

    IF users_total = 0
       AND categories_total = 0
       AND activities_total = 0 THEN
        RAISE EXCEPTION
            'SEMILLA AUSENTE: la base experimental esta vacia';
    END IF;

    SELECT COUNT(*)
    INTO users_expected
    FROM users
    WHERE
        email ~ '^loadtest-high-[0-9]{4}@rachapro[.]test$'
        OR email ~ '^loadtest-low-[0-9]{4}@rachapro[.]test$';

    SELECT COUNT(*)
    INTO users_high
    FROM users
    WHERE email ~ '^loadtest-high-[0-9]{4}@rachapro[.]test$';

    SELECT COUNT(*)
    INTO users_low
    FROM users
    WHERE email ~ '^loadtest-low-[0-9]{4}@rachapro[.]test$';

    SELECT COUNT(*)
    INTO users_outside_groups
    FROM users
    WHERE NOT (
        email ~ '^loadtest-high-[0-9]{4}@rachapro[.]test$'
        OR email ~ '^loadtest-low-[0-9]{4}@rachapro[.]test$'
    );

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
            u.id
        FROM users u
        LEFT JOIN categories c
            ON c.user_id = u.id
        GROUP BY u.id
        HAVING COUNT(c.id) FILTER (WHERE c.is_active = TRUE) <> 1
    ) q;

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
        u.email ~ '^loadtest-high-[0-9]{4}@rachapro[.]test$'
        AND a.is_deleted = FALSE;

    SELECT COUNT(*)
    INTO activities_low
    FROM activities a
    JOIN users u
        ON u.id = a.user_id
    WHERE
        u.email ~ '^loadtest-low-[0-9]{4}@rachapro[.]test$'
        AND a.is_deleted = FALSE;

    SELECT COUNT(*)
    INTO activities_outside_groups
    FROM activities a
    JOIN users u
        ON u.id = a.user_id
    WHERE NOT (
        u.email ~ '^loadtest-high-[0-9]{4}@rachapro[.]test$'
        OR u.email ~ '^loadtest-low-[0-9]{4}@rachapro[.]test$'
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
            COUNT(a.id) FILTER (
                WHERE a.is_deleted = FALSE
            ) AS activity_count
        FROM users u
        LEFT JOIN activities a
            ON a.user_id = u.id
        WHERE
            u.email ~ '^loadtest-high-[0-9]{4}@rachapro[.]test$'
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
            COUNT(a.id) FILTER (
                WHERE a.is_deleted = FALSE
            ) AS activity_count
        FROM users u
        LEFT JOIN activities a
            ON a.user_id = u.id
        WHERE
            u.email ~ '^loadtest-low-[0-9]{4}@rachapro[.]test$'
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
            'SEMILLA PARCIAL O INVALIDA: usuarios totales = %, esperado 1000',
            users_total;
    END IF;

    IF users_expected <> 1000 THEN
        RAISE EXCEPTION
            'SEMILLA PARCIAL O INVALIDA: usuarios A/B = %, esperado 1000',
            users_expected;
    END IF;

    IF users_outside_groups <> 0 THEN
        RAISE EXCEPTION
            'SEMILLA INVALIDA: usuarios fuera de A/B = %, esperado 0',
            users_outside_groups;
    END IF;

    IF users_high <> 200 THEN
        RAISE EXCEPTION
            'SEMILLA INVALIDA: usuarios grupo A = %, esperado 200',
            users_high;
    END IF;

    IF users_low <> 800 THEN
        RAISE EXCEPTION
            'SEMILLA INVALIDA: usuarios grupo B = %, esperado 800',
            users_low;
    END IF;

    IF categories_total <> 1000 THEN
        RAISE EXCEPTION
            'SEMILLA PARCIAL O INVALIDA: categorias = %, esperado 1000',
            categories_total;
    END IF;

    IF categories_active <> 1000 THEN
        RAISE EXCEPTION
            'SEMILLA INVALIDA: categorias activas = %, esperado 1000',
            categories_active;
    END IF;

    IF categories_inactive <> 0 THEN
        RAISE EXCEPTION
            'SEMILLA INVALIDA: categorias inactivas = %, esperado 0',
            categories_inactive;
    END IF;

    IF users_bad_categories <> 0 THEN
        RAISE EXCEPTION
            'SEMILLA INVALIDA: usuarios sin exactamente una categoria activa = %',
            users_bad_categories;
    END IF;

    IF activities_total <> 100000 THEN
        RAISE EXCEPTION
            'SEMILLA PARCIAL O INVALIDA: actividades = %, esperado 100000',
            activities_total;
    END IF;

    IF activities_active <> 100000 THEN
        RAISE EXCEPTION
            'SEMILLA INVALIDA: actividades activas = %, esperado 100000',
            activities_active;
    END IF;

    IF activities_deleted <> 0 THEN
        RAISE EXCEPTION
            'SEMILLA INVALIDA: actividades eliminadas = %, esperado 0',
            activities_deleted;
    END IF;

    IF activities_outside_groups <> 0 THEN
        RAISE EXCEPTION
            'SEMILLA INVALIDA: actividades fuera de A/B = %, esperado 0',
            activities_outside_groups;
    END IF;

    IF activities_high <> 80000 THEN
        RAISE EXCEPTION
            'SEMILLA INVALIDA: actividades grupo A = %, esperado 80000',
            activities_high;
    END IF;

    IF activities_low <> 20000 THEN
        RAISE EXCEPTION
            'SEMILLA INVALIDA: actividades grupo B = %, esperado 20000',
            activities_low;
    END IF;

    IF high_min <> 400 OR high_max <> 400 THEN
        RAISE EXCEPTION
            'SEMILLA INVALIDA: grupo A min/max = %/%, esperado 400/400',
            high_min,
            high_max;
    END IF;

    IF low_min <> 25 OR low_max <> 25 THEN
        RAISE EXCEPTION
            'SEMILLA INVALIDA: grupo B min/max = %/%, esperado 25/25',
            low_min,
            low_max;
    END IF;

    IF bad_category_owner <> 0 THEN
        RAISE EXCEPTION
            'SEMILLA INVALIDA: actividades con categoria de otro usuario = %',
            bad_category_owner;
    END IF;
END
$$;

SELECT
    'COMPLETA_Y_VALIDA' AS estado_dataset,
    (SELECT COUNT(*) FROM users) AS usuarios,
    (SELECT COUNT(*) FROM categories) AS categorias,
    (SELECT COUNT(*) FROM activities) AS actividades,

    (SELECT COUNT(*)
     FROM users
     WHERE email ~ '^loadtest-high-[0-9]{4}@rachapro[.]test$')
        AS usuarios_grupo_a,

    (SELECT COUNT(*)
     FROM users
     WHERE email ~ '^loadtest-low-[0-9]{4}@rachapro[.]test$')
        AS usuarios_grupo_b,

    (SELECT COUNT(*)
     FROM activities a
     JOIN users u ON u.id = a.user_id
     WHERE u.email ~ '^loadtest-high-[0-9]{4}@rachapro[.]test$')
        AS actividades_grupo_a,

    (SELECT COUNT(*)
     FROM activities a
     JOIN users u ON u.id = a.user_id
     WHERE u.email ~ '^loadtest-low-[0-9]{4}@rachapro[.]test$')
        AS actividades_grupo_b;
'@

$sql |
    docker exec -i $Container `
        psql `
        -X `
        -U $DbUser `
        -d $Database `
        -v ON_ERROR_STOP=1

if ($LASTEXITCODE -ne 0) {
    throw "La semilla experimental no supera la verificacion independiente."
}

Write-Host "SEMILLA 80/20 VERIFICADA"