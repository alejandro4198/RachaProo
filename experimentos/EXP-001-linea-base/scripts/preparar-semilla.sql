WITH RECURSIVE seq(n) AS (
    SELECT 1
    UNION ALL
    SELECT n + 1
    FROM seq
    WHERE n < 100
)
INSERT INTO activities (
    userId,
    categoryId,
    title,
    description,
    dueDateEpochDay,
    dueTimeMinutes,
    priority,
    status,
    repeatRule,
    createdAt,
    updatedAt,
    completedAt,
    completedDateEpochDay,
    isDeleted,
    deletedAt
)
SELECT
    1,
    1,
    printf('Actividad %03d', n),
    '',
    CAST(
        julianday('2030-12-31') - julianday('1970-01-01')
        AS INTEGER
    ),
    NULL,
    'MEDIUM',
    'PENDING',
    NULL,
    CAST(strftime('%s','now') AS INTEGER) * 1000,
    CAST(strftime('%s','now') AS INTEGER) * 1000,
    NULL,
    NULL,
    0,
    NULL
FROM seq;