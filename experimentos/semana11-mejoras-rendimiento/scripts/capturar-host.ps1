param(
    [string]$OutputDir,
    [int]$IntervalSeconds = 5,
    [int]$DurationSeconds = 120
)

$ErrorActionPreference = "SilentlyContinue"

if (-not $OutputDir) {
    $OutputDir = Join-Path $PSScriptRoot "..\01-diagnostico-concurrencia\evidencia"
}
New-Item -ItemType Directory -Force -Path $OutputDir | Out-Null

$outFile = Join-Path $OutputDir ("host-capture-{0}.csv" -f (Get-Date -Format "yyyyMMdd-HHmmss"))
"timestamp,cpu_percent,mem_used_mb,mem_total_mb,mem_free_mb" | Out-File -FilePath $outFile -Encoding UTF8

$end = (Get-Date).AddSeconds($DurationSeconds)
while ((Get-Date) -lt $end) {
    $ts = (Get-Date).ToString("o")
    try {
        $cpu = (Get-Counter '\Processor Information(_Total)\% Processor Time' -ErrorAction SilentlyContinue).CounterSamples.CookedValue
    } catch { $cpu = "" }
    try {
        $os = Get-CimInstance Win32_OperatingSystem
        $total = [math]::Round($os.TotalVisibleMemorySize / 1024, 2)
        $free = [math]::Round($os.FreePhysicalMemory / 1024, 2)
        $used = [math]::Round($total - $free, 2)
        "$ts,$cpu,$used,$total,$free" | Out-File -FilePath $outFile -Append -Encoding UTF8
    } catch {
        "$ts,$cpu,," | Out-File -FilePath $outFile -Append -Encoding UTF8
    }
    Start-Sleep -Seconds $IntervalSeconds
}

Write-Host "HOST_CAPTURE_DONE:$outFile"
