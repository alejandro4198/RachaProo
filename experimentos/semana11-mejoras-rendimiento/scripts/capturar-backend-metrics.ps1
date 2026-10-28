param(
    [string]$BaseUrl = "http://localhost:8080",
    [int]$DurationSeconds = 60,
    [int]$IntervalSeconds = 1,
    [string]$OutputFile = ""
)

$ErrorActionPreference = "SilentlyContinue"

if (-not $OutputFile) {
    $outDir = Join-Path $PSScriptRoot "..\01-diagnostico-concurrencia\evidencia"
    New-Item -ItemType Directory -Force -Path $outDir | Out-Null
    $OutputFile = Join-Path $outDir ("backend-metrics-{0}.csv" -f (Get-Date -Format "yyyyMMdd-HHmmss"))
} else {
    $dir = Split-Path -Parent $OutputFile
    if ($dir -and -not (Test-Path $dir)) { New-Item -ItemType Directory -Force -Path $dir | Out-Null }
}

"timestamp,metric_name,statistic,value,unit,status" | Out-File -FilePath $OutputFile -Encoding UTF8

$end = (Get-Date).AddSeconds($DurationSeconds)
while ((Get-Date) -lt $end) {
    $ts = (Get-Date).ToString("o")

    # Standard scalar metrics
    $scalarCands = @(
        "process.cpu.usage",
        "system.cpu.usage",
        "jvm.memory.used",
        "jvm.memory.max",
        "jvm.threads.live",
        "jvm.threads.peak",
        "jvm.gc.pause",
        "hikaricp.connections.active",
        "hikaricp.connections.idle",
        "hikaricp.connections.pending",
        "hikaricp.connections.max"
    )

    foreach ($c in $scalarCands) {
        try {
            $mr = Invoke-RestMethod -Uri "$BaseUrl/actuator/metrics/$c" -TimeoutSec 3
            if ($mr.measurements) {
                foreach ($me in $mr.measurements) {
                    $stat = if ($me.statistic) { $me.statistic } else { "VALUE" }
                    $val = $me.value
                    if ($val -ne $null) {
                        "$ts,$c,$stat,$val,$($mr.baseUnit),ok" | Out-File -FilePath $OutputFile -Append -Encoding UTF8
                    }
                }
            }
        } catch {
            "$ts,$c,VALUE,NA,NA,err" | Out-File -FilePath $OutputFile -Append -Encoding UTF8
        }
    }

    # Multi-measurement / active requests
    try {
        $mr = Invoke-RestMethod -Uri "$BaseUrl/actuator/metrics/http.server.requests.active" -TimeoutSec 3
        if ($mr.measurements) {
            foreach ($me in $mr.measurements) {
                $stat = $me.statistic
                $val = $me.value
                if ($val -ne $null) {
                    "$ts,http.server.requests.active,$stat,$val,$($mr.baseUnit),ok" | Out-File -FilePath $OutputFile -Append -Encoding UTF8
                }
            }
        }
    } catch {
        "$ts,http.server.requests.active,VALUE,NA,NA,err" | Out-File -FilePath $OutputFile -Append -Encoding UTF8
    }

    # Thread states (runnable, blocked, waiting, timed-waiting)
    $states = @("runnable", "blocked", "waiting", "timed-waiting")
    foreach ($st in $states) {
        try {
            $mr = Invoke-RestMethod -Uri "$BaseUrl/actuator/metrics/jvm.threads.states?tag=state:$st" -TimeoutSec 3
            if ($mr.measurements) {
                foreach ($me in $mr.measurements) {
                    $val = $me.value
                    if ($val -ne $null) {
                        "$ts,jvm.threads.states.$st,VALUE,$val,$($mr.baseUnit),ok" | Out-File -FilePath $OutputFile -Append -Encoding UTF8
                    }
                }
            }
        } catch {
            "$ts,jvm.threads.states.$st,VALUE,NA,NA,err" | Out-File -FilePath $OutputFile -Append -Encoding UTF8
        }
    }

    Start-Sleep -Seconds $IntervalSeconds
}

Write-Host "BACKEND_METRICS_DONE:$OutputFile"
