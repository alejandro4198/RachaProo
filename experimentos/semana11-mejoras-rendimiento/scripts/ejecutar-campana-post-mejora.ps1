param(
    [SecureString]$LoadTestPassword
)

$ErrorActionPreference = "Stop"
$raizRepo = (Resolve-Path (Join-Path $PSScriptRoot "..\..\..")).Path
$evidBase = Join-Path $PSScriptRoot "..\10-post-mejora\evidencia"
$restaurar = Join-Path $raizRepo "experimentos\semana11-crecimiento-post-activities\scripts\restaurar-semilla.ps1"
$k6Script = Join-Path $raizRepo "experimentos\semana11-crecimiento-post-activities\scripts\crecimiento-post-activities.js"
$capHost = Join-Path $PSScriptRoot "capturar-host.ps1"
$capPg = Join-Path $PSScriptRoot "capturar-postgres.ps1"
$capBackend = Join-Path $PSScriptRoot "capturar-backend-metrics.ps1"

if (-not $LoadTestPassword) { throw "LoadTestPassword es requerido." }

$bstr = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($LoadTestPassword)
$plainPassword = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($bstr)
[Runtime.InteropServices.Marshal]::ZeroFreeBSTR($bstr)

$vusLevels = @(1, 10, 50, 100, 250, 500, 750, 1000)
$runs = @("R1", "R2", "R3", "R4")

New-Item -ItemType Directory -Force -Path $evidBase | Out-Null

foreach ($vus in $vusLevels) {
    Write-Host "=========================================="
    Write-Host "INICIANDO NIVEL POST-MEJORA: $vus VUs"
    Write-Host "=========================================="

    # 1. Stop backend if running on 8080
    $conn = Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue
    if ($conn) {
        Write-Host "Deteniendo backend existente PID $($conn.OwningProcess)..."
        Stop-Process -Id $conn.OwningProcess -Force -ErrorAction SilentlyContinue
        Start-Sleep -Seconds 3
    }

    # 2. Require DB password in environment
    if (-not $env:RACHAPRO_DB_PASSWORD) {
        throw "RACHAPRO_DB_PASSWORD no está configurada en el entorno."
    }

    $env:JAVA_HOME = "C:\Program Files\Android\Android Studio1\jbr"
    $env:Path = "$env:JAVA_HOME\bin;$env:Path"
    $env:RACHAPRO_DB_URL = "jdbc:postgresql://127.0.0.1:5433/rachapro_perf_s11"
    $env:RACHAPRO_DB_USER = "rachapro_user_s11"
    $env:RACHAPRO_HIKARI_MAX_POOL_SIZE = "30"

    $jwtLine = Get-Content (Join-Path $raizRepo "backend\.env") | Where-Object { $_ -match '^RACHAPRO_JWT_SECRET=' }
    $env:RACHAPRO_JWT_SECRET = ($jwtLine -split '=', 2)[1].Trim()

    # Start backend
    $backendJob = Start-Process powershell -ArgumentList "-Command", "Set-Location '$raizRepo\backend'; .\gradlew.bat bootRun --no-daemon" -WindowStyle Hidden -PassThru

    Write-Host "Esperando inicio del backend ($vus VUs, Pool 30)..."
    $up = $false
    for ($i = 0; $i -le 30; $i++) {
        Start-Sleep -Seconds 5
        try {
            $health = Invoke-RestMethod -Uri "http://127.0.0.1:8080/actuator/health" -Method Get -TimeoutSec 3
            if ($health.status -eq "UP") {
                $metric = Invoke-RestMethod -Uri "http://127.0.0.1:8080/actuator/metrics/hikaricp.connections.max" -Method Get -TimeoutSec 3
                if ([int]$metric.measurements[0].value -eq 30) {
                    $up = $true
                    Write-Host "Backend UP y Hikari Max = 30 verificado."
                    break
                }
            }
        } catch {}
    }

    if (-not $up) { throw "Backend no inició correctamente para $vus VUs." }

    foreach ($r in $runs) {
        $runFolder = "VU$vus-$r"
        $outDir = Join-Path $evidBase $runFolder
        if (Test-Path $outDir) {
            Write-Host "Omitiendo $runFolder (ya existe)"
            continue
        }
        New-Item -ItemType Directory -Force -Path $outDir | Out-Null

        Write-Host "Ejecutando corrida: $runFolder (VUs=$vus, Pool=30)"

        # Restore seed
        & $restaurar -LoadTestPassword $LoadTestPassword -ConfirmNoLoad | Out-Null

        $hostOut = Join-Path $outDir "host.csv"
        $pgOut = Join-Path $outDir "postgres.csv"
        $beOut = Join-Path $outDir "backend-metrics.csv"
        $k6Out = Join-Path $outDir "k6-stdout.txt"
        $k6Err = Join-Path $outDir "k6-stderr.txt"
        $k6Json = Join-Path $outDir "muestras.json"
        $k6Summary = Join-Path $outDir "resumen.json"

        $jobs = @()
        $jobs += Start-Job -ScriptBlock { param($s,$o,$d) & $s -OutputDir (Split-Path $o) -DurationSeconds $d } -ArgumentList $capHost, $hostOut, 100
        $jobs += Start-Job -ScriptBlock { param($s,$o,$d) & $s -OutputDir (Split-Path $o) -DurationSeconds $d } -ArgumentList $capPg, $pgOut, 100
        $jobs += Start-Job -ScriptBlock { param($s,$o,$d,$b) & $s -BaseUrl $b -DurationSeconds $d -IntervalSeconds 1 -OutputFile $o } -ArgumentList $capBackend, $beOut, 100, "http://127.0.0.1:8080"

        Start-Sleep -Seconds 3

        $env:BASE_URL = "http://127.0.0.1:8080"
        $env:S11_VUS = "$vus"
        $env:S11_RUN_ID = "POST-$runFolder"
        $env:TEST_PASSWORD = $plainPassword

        try {
            & k6 run --summary-export $k6Summary --out "json=$k6Json" $k6Script 1>$k6Out 2>$k6Err
        } finally {
            Remove-Item Env:\TEST_PASSWORD -ErrorAction SilentlyContinue
            Remove-Item Env:\BASE_URL -ErrorAction SilentlyContinue
            Remove-Item Env:\S11_VUS -ErrorAction SilentlyContinue
            Remove-Item Env:\S11_RUN_ID -ErrorAction SilentlyContinue
        }

        Start-Sleep -Seconds 5
        $jobs | Receive-Job -Wait -AutoRemoveJob | Out-Null
        Write-Host "Corrida $runFolder completada."
    }

    # Stop backend between VU levels
    $connStop = Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue
    if ($connStop) {
        Stop-Process -Id $connStop.OwningProcess -Force -ErrorAction SilentlyContinue
    }
    Start-Sleep -Seconds 5
}

Write-Host "CAMPAÑA POST-MEJORA COMPLETADA CON ÉXITO (32 CORRIDAS)"
