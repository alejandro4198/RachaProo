param(
    [SecureString]$LoadTestPassword
)

$ErrorActionPreference = "Stop"
$raizRepo = (Resolve-Path (Join-Path $PSScriptRoot "..\..\..")).Path
$hikariRaiz = Join-Path $PSScriptRoot "..\04-hikari-pool\evidencia"
$restaurar = Join-Path $raizRepo "experimentos\semana11-crecimiento-post-activities\scripts\restaurar-semilla.ps1"
$k6Script = Join-Path $raizRepo "experimentos\semana11-crecimiento-post-activities\scripts\crecimiento-post-activities.js"
$capHost = Join-Path $PSScriptRoot "capturar-host.ps1"
$capPg = Join-Path $PSScriptRoot "capturar-postgres.ps1"
$capBackend = Join-Path $PSScriptRoot "capturar-backend-metrics.ps1"

if (-not $LoadTestPassword) { throw "LoadTestPassword es requerido." }

$bstr = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($LoadTestPassword)
$plainPassword = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($bstr)
[Runtime.InteropServices.Marshal]::ZeroFreeBSTR($bstr)

$pools = @(10, 20, 30)
$vusList = @(50, 250)
$runsList = @(1, 2, 3)

foreach ($pool in $pools) {
    Write-Host "=========================================="
    Write-Host "INICIANDO EXPERIMENTO CON POOL SIZE: $pool"
    Write-Host "=========================================="

    # 1. Stop existing backend listening on port 8080 precisely
    $conn = Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue
    if ($conn) {
        $pidToStop = $conn.OwningProcess
        Write-Host "Deteniendo proceso backend PID $pidToStop en puerto 8080..."
        Stop-Process -Id $pidToStop -Force -ErrorAction SilentlyContinue
        Start-Sleep -Seconds 3
    }

    # 2. Start backend with new pool size
    $env:JAVA_HOME = "C:\Program Files\Android\Android Studio1\jbr"
    $env:Path = "$env:JAVA_HOME\bin;$env:Path"
    $env:RACHAPRO_DB_URL = "jdbc:postgresql://127.0.0.1:5433/rachapro_perf_s11"
    $env:RACHAPRO_DB_USER = "rachapro_user_s11"
    $env:RACHAPRO_DB_PASSWORD = $env:RACHAPRO_DB_PASSWORD # loaded from session environment
    if (-not $env:RACHAPRO_DB_PASSWORD) {
        throw "RACHAPRO_DB_PASSWORD no está configurada."
    }
    $env:RACHAPRO_HIKARI_MAX_POOL_SIZE = "$pool"

    $jwtLine = Get-Content (Join-Path $raizRepo "backend\.env") | Where-Object { $_ -match '^RACHAPRO_JWT_SECRET=' }
    $env:RACHAPRO_JWT_SECRET = ($jwtLine -split '=', 2)[1].Trim()

    # Start backend process in background
    $backendJob = Start-Process powershell -ArgumentList "-Command", "cd '$raizRepo\backend'; `$env:JAVA_HOME='$env:JAVA_HOME'; `$env:RACHAPRO_DB_URL='$env:RACHAPRO_DB_URL'; `$env:RACHAPRO_DB_USER='$env:RACHAPRO_DB_USER'; `$env:RACHAPRO_DB_PASSWORD='$env:RACHAPRO_DB_PASSWORD'; `$env:RACHAPRO_HIKARI_MAX_POOL_SIZE='$env:RACHAPRO_HIKARI_MAX_POOL_SIZE'; `$env:RACHAPRO_JWT_SECRET='$env:RACHAPRO_JWT_SECRET'; .\gradlew.bat bootRun --no-daemon" -WindowStyle Hidden -PassThru

    # Wait for Actuator to be UP and hikaricp.connections.max to match pool size
    Write-Host "Esperando inicio del backend (Pool $pool)..."
    $up = $false
    for ($i = 0; $i -le 30; $i++) {
        Start-Sleep -Seconds 5
        try {
            $health = Invoke-RestMethod -Uri "http://127.0.0.1:8080/actuator/health" -Method Get -TimeoutSec 3
            if ($health.status -eq "UP") {
                $metric = Invoke-RestMethod -Uri "http://127.0.0.1:8080/actuator/metrics/hikaricp.connections.max" -Method Get -TimeoutSec 3
                $val = [int]$metric.measurements[0].value
                if ($val -eq $pool) {
                    $up = $true
                    Write-Host "Backend UP y Hikari Max verificado: $val"
                    break
                }
            }
        } catch {}
        Write-Host "Intentando conectar ($i/30)..."
    }

    if (-not $up) {
        throw "Backend no inició correctamente para Pool $pool"
    }

    $poolDir = Join-Path $hikariRaiz "POOL$pool"

    foreach ($vus in $vusList) {
        foreach ($run in $runsList) {
            $runId = "VU$vus-R$run"
            $outDir = Join-Path $poolDir $runId
            if (Test-Path $outDir) {
                Write-Host "Omitiendo $runId (ya existe)"
                continue
            }
            New-Item -ItemType Directory -Force -Path $outDir | Out-Null

            Write-Host "Ejecutando corrida: POOL $pool | $runId"

            # Restore seed before run
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
            $env:S11_RUN_ID = "POOL$pool-$runId"
            $env:TEST_PASSWORD = $plainPassword

            try {
                & k6 run --summary-export $k6Summary --out "json=$k6Json" $k6Script 1>$k6Out 2>$k6Err
            } finally {
                Remove-Item Env:\TEST_PASSWORD -ErrorAction SilentlyContinue
                Remove-Item Env:\BASE_URL -ErrorAction SilentlyContinue
                Remove-Item Env:\S11_VUS -ErrorAction SilentlyContinue
                Remove-Item Env:\S11_RUN_ID -ErrorAction SilentlyContinue
            }

            Start-Sleep -Seconds 10
            $jobs | Receive-Job -Wait -AutoRemoveJob | Out-Null
            Write-Host "Corrida $runId completada."
        }
    }

    # Stop backend after pool campaign using precise port 8080 lookup
    $connStop = Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue
    if ($connStop) {
        Stop-Process -Id $connStop.OwningProcess -Force -ErrorAction SilentlyContinue
    }
    Start-Sleep -Seconds 5
}

Write-Host "EXPERIMENTO DE POOL HIKARI COMPLETADO EXITOSAMENTE"
