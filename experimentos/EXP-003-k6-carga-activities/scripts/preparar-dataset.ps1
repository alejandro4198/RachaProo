param(
    [string]$BaseUrl = "http://localhost:8080"
)

$ErrorActionPreference = "Stop"

if ([string]::IsNullOrWhiteSpace($env:LOADTEST_PASSWORD)) {
    throw "LOADTEST_PASSWORD no esta configurada."
}

Write-Host "`nLimpiando dataset EXP-003..." -ForegroundColor Cyan

docker exec rachapro-postgres psql `
    -U rachapro_user `
    -d rachapro_db `
    -v ON_ERROR_STOP=1 `
    -c "DELETE FROM users WHERE email LIKE 'loadtest%@rachapro.test';"

Write-Host "`nCreando usuario plantilla..." -ForegroundColor Cyan

$body = @{
    fullName = "Load Test 001"
    email = "loadtest001@rachapro.test"
    password = $env:LOADTEST_PASSWORD
    semester = 1
    acceptedPrivacyPolicy = $true
} | ConvertTo-Json

Invoke-RestMethod `
    -Method Post `
    -Uri "$BaseUrl/api/users" `
    -ContentType "application/json" `
    -Body $body | Out-Null

Write-Host "Usuario plantilla creado." -ForegroundColor Green

Write-Host "`nGenerando 499 usuarios y 500.000 actividades..." -ForegroundColor Cyan

Get-Content "$PSScriptRoot\seed-500k.sql" -Raw |
    docker exec -i rachapro-postgres psql `
        -U rachapro_user `
        -d rachapro_db `
        -v ON_ERROR_STOP=1

Write-Host "`nVerificando dataset..." -ForegroundColor Cyan

docker exec rachapro-postgres psql `
    -U rachapro_user `
    -d rachapro_db `
    -c "SELECT
        (SELECT COUNT(*) FROM users
         WHERE email LIKE 'loadtest%@rachapro.test') AS usuarios,
        (SELECT COUNT(*) FROM categories c
         JOIN users u ON u.id = c.user_id
         WHERE u.email LIKE 'loadtest%@rachapro.test') AS categorias,
        (SELECT COUNT(*) FROM activities a
         JOIN users u ON u.id = a.user_id
         WHERE u.email LIKE 'loadtest%@rachapro.test') AS actividades;"

docker exec rachapro-postgres psql `
    -U rachapro_user `
    -d rachapro_db `
    -c "SELECT
        MIN(cantidad) AS minimo,
        MAX(cantidad) AS maximo
    FROM (
        SELECT u.id, COUNT(a.id) AS cantidad
        FROM users u
        JOIN activities a ON a.user_id = u.id
        WHERE u.email LIKE 'loadtest%@rachapro.test'
        GROUP BY u.id
    ) t;"
