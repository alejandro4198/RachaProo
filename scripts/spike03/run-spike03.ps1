param(
    [Parameter(
        Mandatory = $true,
        Position = 0
    )]
    [ValidateSet(1, 2, 3)]
    [int]$RunId,

    [switch]$Formal
)

$ErrorActionPreference = "Stop"

if (-not $Formal) {
    Write-Host ""
    Write-Host "SPIKE-03 NO ejecutado."
    Write-Host ""
    Write-Host "Este script requiere confirmación explícita mediante -Formal."
    Write-Host ""
    Write-Host "Ejemplo futuro:"
    Write-Host ".\scripts\spike03\run-spike03.ps1 $RunId -Formal"
    Write-Host ""
    exit 2
}

$repoRoot = (
    Resolve-Path (
        Join-Path $PSScriptRoot "..\.."
    )
).Path

$timestamp = Get-Date -Format "yyyyMMdd-HHmmss-fff"

$outputDirectory = Join-Path `
    $repoRoot `
    "experimentos\spike-03-caracterizacion-racha\evidencia\run-$RunId\$timestamp"

$previousFormal = $env:SPIKE03_FORMAL
$previousRun = $env:SPIKE03_RUN
$previousOutput = $env:SPIKE03_OUTPUT_DIR

$exitCode = 1

try {
    $env:SPIKE03_FORMAL = "true"
    $env:SPIKE03_RUN = $RunId.ToString()
    $env:SPIKE03_OUTPUT_DIR = $outputDirectory

    Write-Host ""
    Write-Host "===== SPIKE-03 ====="
    Write-Host "Corrida: $RunId"
    Write-Host "Salida: $outputDirectory"
    Write-Host ""
    Write-Host "Se iniciará una invocación Gradle independiente."
    Write-Host ""

    Push-Location $repoRoot

    try {
        & .\gradlew.bat `
            :app:testDebugUnitTest `
            --tests "com.example.rachapro.experiment.spike03.Spike03HarnessTest" `
            --rerun-tasks `
            --no-daemon

        $exitCode = $LASTEXITCODE
    }
    finally {
        Pop-Location
    }
}
finally {
    if ($null -eq $previousFormal) {
        Remove-Item Env:\SPIKE03_FORMAL -ErrorAction SilentlyContinue
    }
    else {
        $env:SPIKE03_FORMAL = $previousFormal
    }

    if ($null -eq $previousRun) {
        Remove-Item Env:\SPIKE03_RUN -ErrorAction SilentlyContinue
    }
    else {
        $env:SPIKE03_RUN = $previousRun
    }

    if ($null -eq $previousOutput) {
        Remove-Item Env:\SPIKE03_OUTPUT_DIR -ErrorAction SilentlyContinue
    }
    else {
        $env:SPIKE03_OUTPUT_DIR = $previousOutput
    }
}

exit $exitCode
