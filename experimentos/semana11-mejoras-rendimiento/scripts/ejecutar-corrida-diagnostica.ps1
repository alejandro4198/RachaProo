param(
    [Parameter(Mandatory = $true)]
    [ValidateSet(10, 50, 250, 1000)]
    [int]$Vus,

    [Parameter(Mandatory = $true)]
    [ValidateSet(1, 2, 3)]
    [int]$Run,

    [int]$DurationSeconds = 60,

    [SecureString]$LoadTestPassword,

    [string]$BaseUrl = "http://127.0.0.1:8080"
)

$ErrorActionPreference = "Stop"

$raizRepo = (Resolve-Path (Join-Path $PSScriptRoot "..\..\..")).Path
$expRaiz = Join-Path $PSScriptRoot ".."
$evidBase = Join-Path $expRaiz "01-diagnostico-concurrencia\evidencia-instrumentada"
$k6Script = Join-Path $raizRepo "experimentos\semana11-crecimiento-post-activities\scripts\crecimiento-post-activities.js"
$restaurar = Join-Path $raizRepo "experimentos\semana11-crecimiento-post-activities\scripts\restaurar-semilla.ps1"
$capHost = Join-Path $PSScriptRoot "capturar-host.ps1"
$capPg = Join-Path $PSScriptRoot "capturar-postgres.ps1"
$capBackend = Join-Path $PSScriptRoot "capturar-backend-metrics.ps1"

if (-not (Get-Command k6 -ErrorAction SilentlyContinue)) { throw "k6 no disponible" }
if (-not (Test-Path $k6Script)) { throw "k6 script no encontrado: $k6Script" }
if ($Run -ne 1 -and -not $LoadTestPassword) { throw "LoadTestPassword requerido para R>=2" }

$runId = "VU$Vus-R$Run"
$outDir = Join-Path $evidBase $runId
if (Test-Path $outDir) { throw "Evidencia ya existe: $outDir" }
New-Item -ItemType Directory -Force -Path $outDir | Out-Null

$inicio = (Get-Date).ToString("o")
$commit = (git -C $raizRepo rev-parse HEAD).Trim()
try { $javaVer = (java -version 2>&1 | Select-Object -First 1).ToString() } catch { $javaVer = $env:JAVA_HOME }
try { $k6Ver = (k6 version 2>&1 | Select-Object -First 1).ToString() } catch { $k6Ver = "unknown" }

# start captures
$hostOut = Join-Path $outDir "host.csv"
$pgOut = Join-Path $outDir "postgres.csv"
$beOut = Join-Path $outDir "backend-metrics.csv"

$jobs = @()
$jobs += Start-Job -ScriptBlock { param($s,$o,$d) & $s -OutputDir (Split-Path $o) -DurationSeconds $d } -ArgumentList $capHost, $hostOut, ($DurationSeconds + 40)
$jobs += Start-Job -ScriptBlock { param($s,$o,$d) & $s -OutputDir (Split-Path $o) -DurationSeconds $d } -ArgumentList $capPg, $pgOut, ($DurationSeconds + 40)
$jobs += Start-Job -ScriptBlock { param($s,$o,$d,$b) & $s -BaseUrl $b -DurationSeconds $d -IntervalSeconds 1 -OutputFile $o } -ArgumentList $capBackend, $beOut, ($DurationSeconds + 40), $BaseUrl

Start-Sleep -Seconds 3

if ($Run -ne 1 -and $LoadTestPassword) {
    try {
        & $restaurar -LoadTestPassword $LoadTestPassword -ConfirmNoLoad | Out-Null
    } catch {
        throw $_
    }
}

$env:BASE_URL = $BaseUrl
$env:S11_VUS = "$Vus"
$env:S11_RUN_ID = "S11-VU$Vus-R$Run"
$k6Out = Join-Path $outDir "k6-stdout.txt"
$k6Err = Join-Path $outDir "k6-stderr.txt"
$k6Json = Join-Path $outDir "muestras.json"
$k6Summary = Join-Path $outDir "resumen.json"

$codigoSalida = -1
if ($LoadTestPassword) {
    $bstr = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($LoadTestPassword)
    try {
        $env:TEST_PASSWORD = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($bstr)
        & k6 run --summary-export $k6Summary --out "json=$k6Json" $k6Script 1>$k6Out 2>$k6Err
        $codigoSalida = $LASTEXITCODE
    } finally {
        Remove-Item Env:\TEST_PASSWORD -ErrorAction SilentlyContinue
        [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($bstr)
    }
} else {
    & k6 run --summary-export $k6Summary --out "json=$k6Json" $k6Script 1>$k6Out 2>$k6Err
    $codigoSalida = $LASTEXITCODE
}

Remove-Item Env:\BASE_URL -ErrorAction SilentlyContinue
Remove-Item Env:\S11_VUS -ErrorAction SilentlyContinue
Remove-Item Env:\S11_RUN_ID -ErrorAction SilentlyContinue

Start-Sleep -Seconds 15
$jobs | Receive-Job -Wait -AutoRemoveJob | Out-Null

$fin = (Get-Date).ToString("o")
@{
    runId = $runId
    vus = $Vus
    run = $Run
    inicio = $inicio
    fin = $fin
    commit = $commit
    baseUrl = $BaseUrl
    javaVersion = $javaVer
    k6Version = $k6Ver
    durationSeconds = $DurationSeconds
    codigoSalidaK6 = $codigoSalida
    evidenciaDir = $outDir
} | ConvertTo-Json -Depth 4 | Out-File (Join-Path $outDir "metadata.json") -Encoding UTF8

Write-Host "RUN_DONE:$runId:exit:$codigoSalida"
exit $codigoSalida
