param(
    [SecureString]$LoadTestPassword,
    [string]$RunId = "R7"
)

$ErrorActionPreference = "Stop"
$raizRepo = (Resolve-Path (Join-Path $PSScriptRoot "..\..\..")).Path
$spikeRaiz = Join-Path $PSScriptRoot "..\06-tomcat-http\evidencia"
$restaurar = Join-Path $raizRepo "experimentos\semana11-crecimiento-post-activities\scripts\restaurar-semilla.ps1"
$k6Script = Join-Path $raizRepo "experimentos\semana11-crecimiento-post-activities\scripts\crecimiento-post-activities.js"
$capHost = Join-Path $PSScriptRoot "capturar-host.ps1"
$capPg = Join-Path $PSScriptRoot "capturar-postgres.ps1"
$capBackend = Join-Path $PSScriptRoot "capturar-backend-metrics.ps1"

if (-not $LoadTestPassword) { throw "LoadTestPassword es requerido." }

$bstr = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($LoadTestPassword)
$plainPassword = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($bstr)
[Runtime.InteropServices.Marshal]::ZeroFreeBSTR($bstr)

$jcmd = Join-Path $env:JAVA_HOME "bin\jcmd.exe"
if (-not (Test-Path $jcmd)) {
    $jcmd = "C:\Program Files\Android\Android Studio1\jbr\bin\jcmd.exe"
}
if (-not (Test-Path $jcmd)) {
    throw "jcmd no disponible."
}

# 1. Check for k6 residual process
$k6Proc = Get-Process k6 -ErrorAction SilentlyContinue
if ($k6Proc) {
    throw "Proceso k6 residual detectado. Deténgalo antes de continuar."
}

# 2. Stop existing backend listening on 8080 precisely
$conn = Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue
if ($conn) {
    Stop-Process -Id $conn.OwningProcess -Force -ErrorAction SilentlyContinue
    Start-Sleep -Seconds 3
}

# 3. Require environment variable for DB password
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

$backendJob = Start-Process powershell -ArgumentList "-Command", "Set-Location '$raizRepo\backend'; .\gradlew.bat bootRun --no-daemon" -WindowStyle Hidden -PassThru

Write-Host "Esperando inicio del backend (Pool 30 para $RunId con Thread Dumps)..."
$up = $false
$backendPid = $null
for ($i = 0; $i -le 30; $i++) {
    Start-Sleep -Seconds 5
    try {
        $health = Invoke-RestMethod -Uri "http://127.0.0.1:8080/actuator/health" -Method Get -TimeoutSec 3
        if ($health.status -eq "UP") {
            $connListener = Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue
            if ($connListener) {
                $backendPid = $connListener.OwningProcess
                $up = $true
                Write-Host "Backend UP (PID: $backendPid) verificado."
                break
            }
        }
    } catch {}
}

if (-not $up -or -not $backendPid) { throw "Backend no inició correctamente." }

$outDir = Join-Path $spikeRaiz $RunId
if (Test-Path $outDir) {
    throw "La carpeta $outDir ya existe. Abortando para no sobrescribir evidencia existente."
}

New-Item -ItemType Directory -Path $outDir | Out-Null

# Capture Baseline Thread Dump
Write-Host "Capturando baseline thread dump..."
$baselineDumpPath = Join-Path $outDir "thread-dump-baseline.txt"
& $jcmd $backendPid Thread.print | Out-File -FilePath $baselineDumpPath -Encoding UTF8

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
$env:S11_VUS = "250"
$env:S11_RUN_ID = "TOMCAT-250-$RunId"
$env:TEST_PASSWORD = $plainPassword

# Background job to monitor threads for spike (>80 threads for 2 consecutive samples)
$monitorJob = Start-Job -ScriptBlock {
    param($pidVal, $jcmdPath, $dumpPath)
    $consecutive = 0
    $captured = $false
    for ($k = 0; $k -lt 70; $k++) {
        Start-Sleep -Seconds 1
        try {
            $m = Invoke-RestMethod -Uri "http://127.0.0.1:8080/actuator/metrics/jvm.threads.live" -TimeoutSec 2
            $val = [int]$m.measurements[0].value
            if ($val -gt 80) {
                $consecutive++
                if ($consecutive -ge 2 -and -not $captured) {
                    & $jcmdPath $pidVal Thread.print | Out-File -FilePath $dumpPath -Encoding UTF8
                    $captured = $true
                    break
                }
            } else {
                $consecutive = 0
            }
        } catch {}
    }
} -ArgumentList $backendPid, $jcmd, (Join-Path $outDir "thread-dump-spike.txt")

try {
    & k6 run --summary-export $k6Summary --out "json=$k6Json" $k6Script 1>$k6Out 2>$k6Err
} finally {
    Remove-Item Env:\TEST_PASSWORD -ErrorAction SilentlyContinue
    Remove-Item Env:\BASE_URL -ErrorAction SilentlyContinue
    Remove-Item Env:\S11_VUS -ErrorAction SilentlyContinue
    Remove-Item Env:\S11_RUN_ID -ErrorAction SilentlyContinue
}

Start-Sleep -Seconds 5
Stop-Job $monitorJob -ErrorAction SilentlyContinue
Remove-Job $monitorJob -ErrorAction SilentlyContinue

$jobs | Receive-Job -Wait -AutoRemoveJob | Out-Null

# Stop backend precisely via port 8080
$connStop = Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue
if ($connStop) {
    Stop-Process -Id $connStop.OwningProcess -Force -ErrorAction SilentlyContinue
}
Start-Sleep -Seconds 5

Write-Host "RUN $RunId COMPLETADO CON THREAD DUMPS"
