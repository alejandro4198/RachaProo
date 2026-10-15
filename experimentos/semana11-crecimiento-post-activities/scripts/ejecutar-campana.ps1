param(
    [Parameter(Mandatory = $true)]
    [ValidateSet(1, 10, 50, 100, 250, 500, 750, 1000)]
    [int]$Vus,

    [Parameter(Mandatory = $true)]
    [ValidateRange(1, 4)]
    [int]$Corrida,

    [Parameter(Mandatory = $true)]
    [SecureString]$LoadTestPassword
)

$ErrorActionPreference = "Stop"

$raiz = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
$scriptK6 = Join-Path $PSScriptRoot "crecimiento-post-activities.js"
$scriptRestauracion = Join-Path $PSScriptRoot "restaurar-semilla.ps1"

$tipo = if ($Corrida -eq 1) { "calentamiento" } else { "medicion" }
$runId = "S11-VU$Vus-R$Corrida"

$carpetaCorrida = Join-Path $raiz "evidencia\oficial\$runId"

Write-Host "Corrida: $runId"
Write-Host "Tipo: $tipo"
Write-Host "Carpeta de evidencia: $carpetaCorrida"

if (Test-Path -LiteralPath $carpetaCorrida) {
    throw "La corrida $runId ya tiene evidencia. No se restaurara ni sobrescribira."
}

foreach ($ruta in @($scriptK6, $scriptRestauracion)) {
    if (-not (Test-Path -LiteralPath $ruta -PathType Leaf)) {
        throw "Archivo requerido no encontrado: $ruta"
    }
}

if (-not (Get-Command k6 -ErrorAction SilentlyContinue)) {
    throw "k6 no esta disponible."
}

if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
    throw "Docker no esta disponible."
}

$procesosK6 = @(
    Get-CimInstance Win32_Process |
        Where-Object { $_.Name -eq "k6.exe" }
)

if ($procesosK6.Count -gt 0) {
    throw "Hay una ejecucion k6 activa. No se restaurara la semilla."
}

$estado = docker inspect `
    --format "{{.State.Health.Status}}" `
    rachapro-postgres-s11 2>&1

if ($LASTEXITCODE -ne 0 -or "$estado".Trim() -ne "healthy") {
    throw "PostgreSQL experimental no esta saludable."
}

$escucha = @(
    Get-NetTCPConnection `
        -State Listen `
        -LocalPort 8080 `
        -ErrorAction SilentlyContinue
)

if ($escucha.Count -ne 1) {
    throw "No se encontro un unico proceso escuchando en el puerto 8080."
}

$pidBackend = $escucha[0].OwningProcess

$conexiones5433 = @(
    Get-NetTCPConnection `
        -OwningProcess $pidBackend `
        -State Established `
        -ErrorAction SilentlyContinue |
        Where-Object { $_.RemotePort -eq 5433 }
)

$conexiones5432 = @(
    Get-NetTCPConnection `
        -OwningProcess $pidBackend `
        -State Established `
        -ErrorAction SilentlyContinue |
        Where-Object { $_.RemotePort -eq 5432 }
)

if ($conexiones5433.Count -eq 0) {
    throw "No se observo conexion establecida del backend hacia PostgreSQL experimental 5433."
}

if ($conexiones5432.Count -gt 0) {
    throw "El backend mantiene conexiones hacia PostgreSQL normal 5432."
}

Write-Host "BACKEND_CONECTADO_A_5433"
Write-Host "PRECONDICIONES_BASICAS_OK"
Write-Host "Iniciando restauracion para $runId"

try {
    & $scriptRestauracion `
        -LoadTestPassword $LoadTestPassword `
        -ConfirmNoLoad
}
catch {
    throw "Restauracion fallida para $runId. $($_.Exception.Message)"
}

Write-Host "RESTAURACION_VERIFICADA=$runId"

if (Test-Path -LiteralPath $carpetaCorrida) {
    throw "La carpeta de evidencia ya existe. No se sobrescribira: $carpetaCorrida"
}

New-Item `
    -ItemType Directory `
    -Path $carpetaCorrida |
    Out-Null

$resumen = Join-Path $carpetaCorrida "resumen.json"
$muestras = Join-Path $carpetaCorrida "muestras.json"
$metadatos = Join-Path $carpetaCorrida "metadatos.json"

$commit = (git rev-parse HEAD).Trim()

if ($LASTEXITCODE -ne 0) {
    throw "No se pudo identificar el commit actual."
}

$inicio = (Get-Date).ToString("o")

$env:BASE_URL = "http://127.0.0.1:8080"
$env:S11_VUS = "$Vus"
$env:S11_RUN_ID = $runId

$bstr = [Runtime.InteropServices.Marshal]::SecureStringToBSTR(
    $LoadTestPassword
)

$codigoSalida = -1

try {
    $env:TEST_PASSWORD =
        [Runtime.InteropServices.Marshal]::PtrToStringBSTR($bstr)

    & k6 run `
        --summary-export $resumen `
        --out "json=$muestras" `
        $scriptK6

    $codigoSalida = $LASTEXITCODE
}
finally {
    Remove-Item Env:\TEST_PASSWORD -ErrorAction SilentlyContinue
    Remove-Item Env:\BASE_URL -ErrorAction SilentlyContinue
    Remove-Item Env:\S11_VUS -ErrorAction SilentlyContinue
    Remove-Item Env:\S11_RUN_ID -ErrorAction SilentlyContinue

    [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($bstr)
}

$fin = (Get-Date).ToString("o")

[PSCustomObject]@{
    runId            = $runId
    tipo             = $tipo
    vusConfigurados  = $Vus
    corrida          = $Corrida
    inicio           = $inicio
    fin              = $fin
    commit           = $commit
    baseUrl          = "http://127.0.0.1:8080"
    baseExperimental = "rachapro_perf_s11"
    puertoPostgres   = 5433
    duracionObjetivo = "60s"
    codigoSalidaK6   = $codigoSalida
    resumenExiste    = (Test-Path -LiteralPath $resumen)
    muestrasExisten  = (Test-Path -LiteralPath $muestras)
} |
    ConvertTo-Json -Depth 4 |
    Set-Content -LiteralPath $metadatos -Encoding UTF8

Write-Host "CODIGO_SALIDA_K6=$codigoSalida"
Write-Host "EVIDENCIA_GUARDADA=$carpetaCorrida"

if ($codigoSalida -ne 0) {
    throw "k6 termino con error. Revisar evidencia de $runId."
}