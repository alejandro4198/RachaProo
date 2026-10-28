param(
    [string]$OutputDir,
    [int]$IntervalSeconds = 5,
    [int]$DurationSeconds = 120,
    [string]$ContainerName = "rachapro-postgres-s11",
    [string]$DbName = "rachapro_perf_s11",
    [string]$DbUser = "rachapro_user_s11"
)

$ErrorActionPreference = "SilentlyContinue"

if (-not $OutputDir) {
    $OutputDir = Join-Path $PSScriptRoot "..\01-diagnostico-concurrencia\evidencia"
}
New-Item -ItemType Directory -Force -Path $OutputDir | Out-Null

$outFile = Join-Path $OutputDir ("postgres-capture-{0}.csv" -f (Get-Date -Format "yyyyMMdd-HHmmss"))
"timestamp,metric,value,extra" | Out-File -FilePath $outFile -Encoding UTF8

$end = (Get-Date).AddSeconds($DurationSeconds)
while ((Get-Date) -lt $end) {
    $ts = (Get-Date).ToString("o")
    try {
        $sql1 = "SELECT count(*) as active FROM pg_stat_activity WHERE state = 'active';"
        $res1 = docker exec -i $ContainerName psql -U $DbUser -d $DbName -t -A -c $sql1 2>&1
        "$ts,pg_stat_activity_active,$res1," | Out-File -FilePath $outFile -Append -Encoding UTF8
    } catch {}
    try {
        $sql2 = "SELECT count(*) FROM pg_stat_activity;"
        $res2 = docker exec -i $ContainerName psql -U $DbUser -d $DbName -t -A -c $sql2 2>&1
        "$ts,pg_stat_activity_total,$res2," | Out-File -FilePath $outFile -Append -Encoding UTF8
    } catch {}
    try {
        $sql3 = "SELECT count(*) FROM pg_locks WHERE NOT granted;"
        $res3 = docker exec -i $ContainerName psql -U $DbUser -d $DbName -t -A -c $sql3 2>&1
        "$ts,pg_locks_not_granted,$res3," | Out-File -FilePath $outFile -Append -Encoding UTF8
    } catch {}
    try {
        $sql4 = "SELECT count(*) FROM pg_locks;"
        $res4 = docker exec -i $ContainerName psql -U $DbUser -d $DbName -t -A -c $sql4 2>&1
        "$ts,pg_locks_total,$res4," | Out-File -FilePath $outFile -Append -Encoding UTF8
    } catch {}
    Start-Sleep -Seconds $IntervalSeconds
}

Write-Host "POSTGRES_CAPTURE_DONE:$outFile"
