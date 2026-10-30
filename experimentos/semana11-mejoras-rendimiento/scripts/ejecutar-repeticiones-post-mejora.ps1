param(
    [SecureString]$LoadTestPassword
)

$ErrorActionPreference = "Stop"
$raizRepo = (Resolve-Path (Join-Path $PSScriptRoot "..\..\..")).Path
$repBase = Join-Path $PSScriptRoot "..\10-post-mejora\evidencia-repeticiones"
$restaurar = Join-Path $raizRepo "experimentos\semana11-crecimiento-post-activities\scripts\restaurar-semilla.ps1"
$k6Script = Join-Path $raizRepo "experimentos\semana11-crecimiento-post-activities\scripts\crecimiento-post-activities.js"
$capHost = Join-Path $PSScriptRoot "capturar-host.ps1"
$capPg = Join-Path $PSScriptRoot "capturar-postgres.ps1"
$capBackend = Join-Path $PSScriptRoot "capturar-backend-metrics.ps1"

if (-not $LoadTestPassword) { throw "LoadTestPassword es requerido." }

$bstr = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($LoadTestPassword)
$plainPassword = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($bstr)
[Runtime.InteropServices.Marshal]::ZeroFreeBSTR($bstr)

$repeatList = @(
    @{ vus = 250; run = "R2"; folder = "VU250-R2-REPEAT1" },
    @{ vus = 500; run = "R1"; folder = "VU500-R1-REPEAT1" },
    @{ vus = 500; run = "R3"; folder = "VU500-R3-REPEAT1" },
    @{ vus = 750; run = "R1"; folder = "VU750-R1-REPEAT1" },
    @{ vus = 750; run = "R3"; folder = "VU750-R3-REPEAT1" },
    @{ vus = 1000; run = "R1"; folder = "VU1000-R1-REPEAT1" },
    @{ vus = 1000; run = "R2"; folder = "VU1000-R2-REPEAT1" },
    @{ vus = 1000; run = "R3"; folder = "VU1000-R3-REPEAT1" },
    @{ vus = 1000; run = "R4"; folder = "VU1000-R4-REPEAT1" }
)

New-Item -ItemType Directory -Force -Path $repBase | Out-Null

foreach ($item in $repeatList) {
    $vus = $item.vus
    $rName = $item.run
    $folderName = $item.folder
    $outDir = Join-Path $repBase $folderName

    if (Test-Path $outDir) {
        $resPathCheck = Join-Path $outDir "resumen.json"
        if (Test-Path $resPathCheck) {
            try {
                $jCheck = Get-Content $resPathCheck | ConvertFrom-Json
                if ($jCheck.metrics.activity_create_duration -and $jCheck.metrics.activity_create_success.passes -gt 0) {
                    Write-Host "Omitiendo $folderName (ya existe y es valida)."
                    continue
                }
            } catch {}
        }
    }

    New-Item -ItemType Directory -Force -Path $outDir | Out-Null

    Write-Host "=========================================="
    Write-Host "EJECUTANDO REPETICION DIRIGIDA: $folderName"
    Write-Host "=========================================="

    # 1. Clean backend stop
    $conn = Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue
    if ($conn) {
        Write-Host "Deteniendo backend PID $($conn.OwningProcess)..."
        Stop-Process -Id $conn.OwningProcess -Force -ErrorAction SilentlyContinue
        Start-Sleep -Seconds 3
    }

    if (-not $env:RACHAPRO_DB_PASSWORD) {
        throw "RACHAPRO_DB_PASSWORD no esta configurada."
    }

    # 2. Restore seed
    & $restaurar -LoadTestPassword $LoadTestPassword -ConfirmNoLoad | Out-Null

    # 3. Start backend
    $env:JAVA_HOME = "C:\Program Files\Android\Android Studio1\jbr"
    $env:Path = "$env:JAVA_HOME\bin;$env:Path"
    $env:RACHAPRO_DB_URL = "jdbc:postgresql://127.0.0.1:5433/rachapro_perf_s11"
    $env:RACHAPRO_DB_USER = "rachapro_user_s11"
    $env:RACHAPRO_HIKARI_MAX_POOL_SIZE = "30"

    $jwtLine = Get-Content (Join-Path $raizRepo "backend\.env") | Where-Object { $_ -match '^RACHAPRO_JWT_SECRET=' }
    $env:RACHAPRO_JWT_SECRET = ($jwtLine -split '=', 2)[1].Trim()

    $backendJob = Start-Process powershell -ArgumentList "-Command", "Set-Location '$raizRepo\backend'; .\gradlew.bat bootRun --no-daemon" -WindowStyle Hidden -PassThru

    Write-Host "Esperando inicio del backend ($folderName)..."
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

    if (-not $up) { throw "Backend no inicio para $folderName." }

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
    $env:S11_RUN_ID = "REP-$folderName"
    $env:TEST_PASSWORD = $plainPassword

    $oldErrorPreference = $ErrorActionPreference
    $ErrorActionPreference = "Continue"
    try {
        & k6 run --summary-export $k6Summary --out "json=$k6Json" $k6Script 1>$k6Out 2>$k6Err
    } finally {
        $ErrorActionPreference = $oldErrorPreference
        Remove-Item Env:\TEST_PASSWORD -ErrorAction SilentlyContinue
        Remove-Item Env:\BASE_URL -ErrorAction SilentlyContinue
        Remove-Item Env:\S11_VUS -ErrorAction SilentlyContinue
        Remove-Item Env:\S11_RUN_ID -ErrorAction SilentlyContinue
    }

    Start-Sleep -Seconds 5
    $jobs | Receive-Job -Wait -AutoRemoveJob | Out-Null

    # Validate run
    $resCheck = Join-Path $outDir "resumen.json"
    $isRunValid = $false
    if (Test-Path $resCheck) {
        try {
            $jC = Get-Content $resCheck | ConvertFrom-Json
            if ($jC.metrics.activity_create_duration -and $jC.metrics.activity_create_success.passes -gt 0) {
                $isRunValid = $true
            }
        } catch {}
    }

    # REPEAT2 retry attempt if initial repeat failed setup
    if (-not $isRunValid) {
        Write-Host "ADVERTENCIA: $folderName fallo setup. Ejecutando REPEAT2..."
        $folderName2 = "$folderName-REPEAT2"
        $outDir2 = Join-Path $repBase $folderName2
        New-Item -ItemType Directory -Force -Path $outDir2 | Out-Null

        & $restaurar -LoadTestPassword $LoadTestPassword -ConfirmNoLoad | Out-Null

        $hostOut2 = Join-Path $outDir2 "host.csv"
        $pgOut2 = Join-Path $outDir2 "postgres.csv"
        $beOut2 = Join-Path $outDir2 "backend-metrics.csv"
        $k6Out2 = Join-Path $outDir2 "k6-stdout.txt"
        $k6Err2 = Join-Path $outDir2 "k6-stderr.txt"
        $k6Json2 = Join-Path $outDir2 "muestras.json"
        $k6Summary2 = Join-Path $outDir2 "resumen.json"

        $jobs2 = @()
        $jobs2 += Start-Job -ScriptBlock { param($s,$o,$d) & $s -OutputDir (Split-Path $o) -DurationSeconds $d } -ArgumentList $capHost, $hostOut2, 100
        $jobs2 += Start-Job -ScriptBlock { param($s,$o,$d) & $s -OutputDir (Split-Path $o) -DurationSeconds $d } -ArgumentList $capPg, $pgOut2, 100
        $jobs2 += Start-Job -ScriptBlock { param($s,$o,$d,$b) & $s -BaseUrl $b -DurationSeconds $d -IntervalSeconds 1 -OutputFile $o } -ArgumentList $capBackend, $beOut2, 100, "http://127.0.0.1:8080"

        Start-Sleep -Seconds 3

        $env:BASE_URL = "http://127.0.0.1:8080"
        $env:S11_VUS = "$vus"
        $env:S11_RUN_ID = "REP2-$folderName2"
        $env:TEST_PASSWORD = $plainPassword

        $ErrorActionPreference = "Continue"
        try {
            & k6 run --summary-export $k6Summary2 --out "json=$k6Json2" $k6Script 1>$k6Out2 2>$k6Err2
        } finally {
            $ErrorActionPreference = $oldErrorPreference
            Remove-Item Env:\TEST_PASSWORD -ErrorAction SilentlyContinue
            Remove-Item Env:\BASE_URL -ErrorAction SilentlyContinue
            Remove-Item Env:\S11_VUS -ErrorAction SilentlyContinue
            Remove-Item Env:\S11_RUN_ID -ErrorAction SilentlyContinue
        }

        Start-Sleep -Seconds 5
        $jobs2 | Receive-Job -Wait -AutoRemoveJob | Out-Null
    }

    # Stop backend
    $connStop = Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue
    if ($connStop) {
        Stop-Process -Id $connStop.OwningProcess -Force -ErrorAction SilentlyContinue
    }
    Start-Sleep -Seconds 5
}

Write-Host "REPETICIONES DIRIGIDAS COMPLETADAS CON EXITO"
