param(
    [string]$Container = "rachapro-postgres-s11",
    [string]$Database  = "rachapro_perf_s11",
    [string]$DbUser    = "rachapro_user_s11",
    [SecureString]$LoadTestPassword,
    [switch]$ConfirmNoLoad
)

$ErrorActionPreference = "Stop"

$ExpectedContainer = "rachapro-postgres-s11"
$ExpectedDatabase = "rachapro_perf_s11"
$ExpectedDbUser = "rachapro_user_s11"
$ExpectedHostPort = "5433"
$ExpectedHostIp = "127.0.0.1"
$ExpectedDataVolume = "rachapro-s11_postgres_s11_data"

if (-not $ConfirmNoLoad) {
    throw "Debe ejecutar el script con -ConfirmNoLoad."
}

if (-not $LoadTestPassword) {
    throw "Debe proporcionar -LoadTestPassword como SecureString."
}

if ($Container -ne $ExpectedContainer) {
    throw "Contenedor no autorizado: $Container"
}

if ($Database -ne $ExpectedDatabase) {
    throw "Base no autorizada: $Database"
}

if ($DbUser -ne $ExpectedDbUser) {
    throw "Usuario PostgreSQL no autorizado: $DbUser"
}

$actualContainer = docker ps -a `
    --filter "name=^/$ExpectedContainer$" `
    --format "{{.Names}}"

if ($LASTEXITCODE -ne 0) {
    throw "Docker no pudo consultar los contenedores."
}

$actualContainer = "$actualContainer".Trim()

if ($actualContainer -ne $ExpectedContainer) {
    throw "No existe el contenedor experimental esperado."
}

$inspectJson = docker inspect $Container

if ($LASTEXITCODE -ne 0) {
    throw "No fue posible inspeccionar el contenedor."
}

$inspect = $inspectJson | ConvertFrom-Json
$containerInfo = @($inspect)[0]

if (-not $containerInfo) {
    throw "Docker no devolvio informacion del contenedor."
}

$running = "$($containerInfo.State.Running)".ToLower().Trim()

if ($running -ne "true") {
    throw "El contenedor experimental no esta ejecutandose."
}

$health = "$($containerInfo.State.Health.Status)".Trim()

if ($health -ne "healthy") {
    throw "PostgreSQL experimental no esta healthy. Estado: $health"
}

$binding = @($containerInfo.NetworkSettings.Ports.'5432/tcp')

if ($binding.Count -ne 1) {
    throw "Se esperaba exactamente una publicacion para 5432/tcp."
}

$hostPort = "$($binding[0].HostPort)".Trim()
$hostIp = "$($binding[0].HostIp)".Trim()

if ($hostPort -ne $ExpectedHostPort) {
    throw "Puerto experimental inesperado: $hostPort"
}

if ($hostIp -ne $ExpectedHostIp) {
    throw "IP publicada inesperada: $hostIp"
}

$dataMount = @(
    $containerInfo.Mounts |
        Where-Object {
            $_.Destination -eq "/var/lib/postgresql/data"
        }
)

if ($dataMount.Count -ne 1) {
    throw "Se esperaba exactamente un montaje para /var/lib/postgresql/data."
}

$dataVolume = "$($dataMount[0].Name)".Trim()

if ($dataVolume -ne $ExpectedDataVolume) {
    throw "Volumen no autorizado: $dataVolume"
}

$identity = docker exec $Container `
    psql `
    -X `
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

$k6Processes = Get-Process `
    -Name "k6" `
    -ErrorAction SilentlyContinue

if ($k6Processes) {
    throw "Se detecto un proceso k6 activo."
}

$activeClients = docker exec $Container `
    psql `
    -X `
    -U $DbUser `
    -d $Database `
    -v ON_ERROR_STOP=1 `
    -tAc "
        SELECT COUNT(*)
        FROM pg_stat_activity
        WHERE datname = current_database()
          AND pid <> pg_backend_pid()
          AND backend_type = 'client backend'
          AND (
                state IS DISTINCT FROM 'idle'
                OR xact_start IS NOT NULL
              );
    "

if ($LASTEXITCODE -ne 0) {
    throw "No fue posible consultar pg_stat_activity."
}

$activeClients = "$activeClients".Trim()

if ($activeClients -notmatch '^\d+$') {
    throw "Resultado inesperado: $activeClients"
}

if ([int]$activeClients -gt 0) {
    throw "Se detecto actividad concurrente en la base experimental."
}

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$seedFile = Join-Path $scriptDir "seed-80-20.sql"
$verifyFile = Join-Path $scriptDir "verificar-semilla.ps1"

if (-not (Test-Path $seedFile)) {
    throw "No existe $seedFile"
}

if (-not (Test-Path $verifyFile)) {
    throw "No existe $verifyFile"
}

$sqlText = Get-Content $seedFile -Raw -Encoding UTF8

$hashMarker = "__S11_PASSWORD_HASH__"
$saltMarker = "__S11_PASSWORD_SALT__"

$hashMarkerCount = ([regex]::Matches(
    $sqlText,
    [regex]::Escape($hashMarker)
)).Count

$saltMarkerCount = ([regex]::Matches(
    $sqlText,
    [regex]::Escape($saltMarker)
)).Count

if ($hashMarkerCount -ne 2) {
    throw "Cantidad inesperada de marcadores de hash: $hashMarkerCount"
}

if ($saltMarkerCount -ne 2) {
    throw "Cantidad inesperada de marcadores de salt: $saltMarkerCount"
}

$saltBytes = [System.Text.Encoding]::UTF8.GetBytes(
    "RachaProS11Salt!"
)

if ($saltBytes.Length -ne 16) {
    throw "El salt experimental no tiene 16 bytes."
}

$bstr = [Runtime.InteropServices.Marshal]::SecureStringToBSTR(
    $LoadTestPassword
)

$plainPassword = $null
$pbkdf2 = $null

try {
    $plainPassword = [Runtime.InteropServices.Marshal]::PtrToStringBSTR(
        $bstr
    )

    $pbkdf2 = [System.Security.Cryptography.Rfc2898DeriveBytes]::new(
        $plainPassword,
        $saltBytes,
        210000,
        [System.Security.Cryptography.HashAlgorithmName]::SHA1
    )

    $hashBytes = $pbkdf2.GetBytes(32)

    $hashBase64 = [Convert]::ToBase64String($hashBytes)
    $saltBase64 = [Convert]::ToBase64String($saltBytes)

    $sqlText = $sqlText.Replace(
        $hashMarker,
        $hashBase64
    )

    $sqlText = $sqlText.Replace(
        $saltMarker,
        $saltBase64
    )

    if ($sqlText.Contains($hashMarker)) {
        throw "El marcador de hash no fue sustituido."
    }

    if ($sqlText.Contains($saltMarker)) {
        throw "El marcador de salt no fue sustituido."
    }

    $sqlText |
        docker exec -i $Container `
            psql `
            -X `
            -U $DbUser `
            -d $Database `
            -v ON_ERROR_STOP=1

    if ($LASTEXITCODE -ne 0) {
        throw "La restauracion SQL fallo. El dataset no esta listo para k6."
    }
}
finally {
    if ($pbkdf2) {
        $pbkdf2.Dispose()
    }

    if ($bstr -ne [IntPtr]::Zero) {
        [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($bstr)
    }

    $plainPassword = $null
    $hashBase64 = $null
    $saltBase64 = $null
    $hashBytes = $null
    $sqlText = $null
}

try {
    & $verifyFile `
        -Container $Container `
        -Database $Database `
        -DbUser $DbUser
}
catch {
    throw "El verificador independiente fallo. El dataset no esta listo para k6. $($_.Exception.Message)"
}

Write-Host "RESTAURACION COMPLETA Y VERIFICADA"
