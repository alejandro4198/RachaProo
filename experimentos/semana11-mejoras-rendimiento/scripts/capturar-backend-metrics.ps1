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

"timestamp,metric_name,value,unit,status" | Out-File -FilePath $OutputFile -Encoding UTF8

$end = (Get-Date).AddSeconds($DurationSeconds)
while ((Get-Date) -lt $end) {
    $ts = (Get-Date).ToString("o")
    # health
    try {
        $health = Invoke-WebRequest -Uri "$BaseUrl/actuator/health" -UseBasicParsing -TimeoutSec 5
        if ($health.StatusCode -eq 200) {
            "$ts,actuator_health,UP,http,status:200" | Out-File -FilePath $OutputFile -Append -Encoding UTF8
        } else {
            "$ts,actuator_health,NA,http,status:$($health.StatusCode)" | Out-File -FilePath $OutputFile -Append -Encoding UTF8
        }
    } catch {
        "$ts,actuator_health,NA,http,err" | Out-File -FilePath $OutputFile -Append -Encoding UTF8
    }
    # metrics list
    try {
        $m = Invoke-RestMethod -Uri "$BaseUrl/actuator/metrics" -TimeoutSec 5
        if ($m.names) {
            "$ts,actuator_metrics_count,$($m.names.Count),count,ok" | Out-File -FilePath $OutputFile -Append -Encoding UTF8
            $names = $m.names
            $cands = @("process.cpu.usage","system.cpu.usage","jvm.memory.used","jvm.memory.max","jvm.threads.live","jvm.threads.peak","jvm.gc.pause","hikaricp.connections.active","hikaricp.connections.idle","hikaricp.connections.pending","hikaricp.connections.max","hikaricp.connections.min","http.server.requests")
            foreach ($c in $cands) {
                if ($names -contains $c) {
                    try {
                        $mr = Invoke-RestMethod -Uri "$BaseUrl/actuator/metrics/$c" -TimeoutSec 5
                        $val = $null
                        if ($mr.measurements) {
                            foreach ($me in $mr.measurements) {
                                if ($me.statistic -eq "VALUE") { $val = $me.value; break }
                                if (-not $val -and $me.value -ne $null) { $val = $me.value }
                            }
                        }
                        if ($val -ne $null) {
                            "$ts,$c,$val,$($mr.baseUnit),ok" | Out-File -FilePath $OutputFile -Append -Encoding UTF8
                        } else {
                            "$ts,$c,NA,$($mr.baseUnit),no_value" | Out-File -FilePath $OutputFile -Append -Encoding UTF8
                        }
                    } catch {
                        "$ts,$c,NA,NA,err" | Out-File -FilePath $OutputFile -Append -Encoding UTF8
                    }
                } else {
                    "$ts,$c,NA,NA,not_exposed" | Out-File -FilePath $OutputFile -Append -Encoding UTF8
                }
            }
        } else {
            "$ts,actuator_metrics_count,NA,count,err" | Out-File -FilePath $OutputFile -Append -Encoding UTF8
        }
    } catch {
        "$ts,actuator_metrics_err,NA,NA,err" | Out-File -FilePath $OutputFile -Append -Encoding UTF8
    }
    Start-Sleep -Seconds $IntervalSeconds
}

Write-Host "BACKEND_METRICS_DONE:$OutputFile"
