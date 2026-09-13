$ErrorActionPreference = "Stop"
Set-StrictMode -Version Latest

$adb = "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe"

$exp004 =
    ".\experimentos\EXP-004-android-bajo-carga"

$k6Script =
    "$exp004\scripts\carga-sostenida-activities.js"

$baselineCsv =
    "$exp004\resultados\baseline-android-sin-carga.csv"

$finalAndroidCsv =
    "$exp004\resultados\android-bajo-carga.csv"

$finalOverlapCsv =
    "$exp004\resultados\solapamiento-formal.csv"

$finalK6Json =
    "$exp004\resultados\k6-formal-499vus-600s.json"

$finalK6Stdout =
    "$exp004\logs\k6-formal-499vus-600s.stdout.txt"

$finalK6Stderr =
    "$exp004\logs\k6-formal-499vus-600s.stderr.txt"

$finalAndroidLogDir =
    "$exp004\logs\android-bajo-carga"

$failedRoot =
    "$exp004\intentos-fallidos"


$runId =
    Get-Date -Format "yyyyMMdd-HHmmss"

$workDir =
    "$exp004\_tmp\$runId"

$workAndroidLogDir =
    "$workDir\android"

$workAndroidCsv =
    "$workDir\android-bajo-carga.csv"

$workOverlapCsv =
    "$workDir\solapamiento-formal.csv"

$workK6Json =
    "$workDir\k6-formal.json"

$workK6Stdout =
    "$workDir\k6.stdout.txt"

$workK6Stderr =
    "$workDir\k6.stderr.txt"


$k6Process = $null

$securePassword = $null
$plainPassword = $null
$ptr = [IntPtr]::Zero


function Clear-K6Environment {

    Remove-Item Env:K6_PASSWORD `
        -ErrorAction SilentlyContinue

    Remove-Item Env:BASE_URL `
        -ErrorAction SilentlyContinue

    Remove-Item Env:VUS `
        -ErrorAction SilentlyContinue

    Remove-Item Env:DURATION `
        -ErrorAction SilentlyContinue

    Remove-Item Env:RESULT_JSON `
        -ErrorAction SilentlyContinue
}


function Get-UiXml {

    param(
        [string]$RemoteFile
    )

    & $adb shell uiautomator dump $RemoteFile |
        Out-Null

    $raw =
        (& $adb shell cat $RemoteFile) -join ""

    return [xml]$raw
}


function Get-TabTarget {

    param(
        [string]$Label,
        [string]$RemoteFile
    )

    $doc =
        Get-UiXml `
            -RemoteFile $RemoteFile

    $nodes =
        $doc.SelectNodes(
            "//*[@text='$Label' or contains(@content-desc,'$Label')]"
        )

    foreach ($node in $nodes) {

        $current = $node

        while (
            $null -ne $current -and
            $current.Name -ne "hierarchy"
        ) {

            if (
                $current.clickable -eq "true" -and
                $current.bounds -match
                '\[(\d+),(\d+)\]\[(\d+),(\d+)\]'
            ) {

                $x1 = [int]$Matches[1]
                $y1 = [int]$Matches[2]
                $x2 = [int]$Matches[3]
                $y2 = [int]$Matches[4]

                return [PSCustomObject]@{
                    X = [int](($x1 + $x2) / 2)
                    Y = [int](($y1 + $y2) / 2)
                }
            }

            $current =
                $current.ParentNode
        }


        if (
            $node.bounds -match
            '\[(\d+),(\d+)\]\[(\d+),(\d+)\]'
        ) {

            $x1 = [int]$Matches[1]
            $y1 = [int]$Matches[2]
            $x2 = [int]$Matches[3]
            $y2 = [int]$Matches[4]

            return [PSCustomObject]@{
                X = [int](($x1 + $x2) / 2)
                Y = [int](($y1 + $y2) / 2)
            }
        }
    }

    throw "No se encontró la pestaña '$Label'."
}


function Test-HomeVisible {

    $doc =
        Get-UiXml `
            -RemoteFile "/sdcard/exp004-runner-home.xml"

    $xmlText =
        $doc.OuterXml

    return (
        $xmlText -match "Actividades de hoy" -and
        $xmlText -match "Progreso de hoy"
    )
}


function Test-ActivitiesVisible {

    $doc =
        Get-UiXml `
            -RemoteFile "/sdcard/exp004-runner-activities.xml"

    $xmlText =
        $doc.OuterXml

    return (
        $xmlText -match "Buscar actividades" -or
        $xmlText -match "Tus actividades"
    )
}


function Go-Home {

    if (Test-HomeVisible) {
        return
    }

    $target =
        Get-TabTarget `
            -Label "Inicio" `
            -RemoteFile "/sdcard/exp004-runner-find-home.xml"

    & $adb shell input tap `
        $target.X `
        $target.Y

    Start-Sleep -Seconds 2

    if (-not (Test-HomeVisible)) {
        throw "No se pudo confirmar Home."
    }
}


function Go-Activities {

    $target =
        Get-TabTarget `
            -Label "Actividades" `
            -RemoteFile "/sdcard/exp004-runner-find-activities.xml"

    & $adb shell input tap `
        $target.X `
        $target.Y
}


function Get-Duration {

    param(
        [string[]]$Lines,
        [string]$Marker
    )

    foreach ($line in $Lines) {

        $match =
            [regex]::Match(
                $line,
                [regex]::Escape($Marker) +
                "=(\d+)"
            )

        if ($match.Success) {

            return [int]$match.Groups[1].Value
        }
    }

    return $null
}


function Get-K6Epoch {

    param(
        [string]$Marker
    )

    $paths =
        @(
            $workK6Stdout,
            $workK6Stderr
        ) |
        Where-Object {
            Test-Path $_
        }

    if ($paths.Count -eq 0) {
        return $null
    }

    $pattern =
        [regex]::Escape($Marker) +
        " epochMs=(\d+)"

    $found =
        Select-String `
            -Path $paths `
            -Pattern $pattern `
            -ErrorAction SilentlyContinue |
        Select-Object -Last 1

    if ($null -eq $found) {
        return $null
    }

    $match =
        [regex]::Match(
            $found.Line,
            $pattern
        )

    if (-not $match.Success) {
        return $null
    }

    return [int64]$match.Groups[1].Value
}


function Read-K6Error {

    if (Test-Path $workK6Stderr) {

        return (
            Get-Content `
                $workK6Stderr `
                -Raw `
                -Encoding UTF8
        )
    }

    return "Sin stderr disponible."
}


function Stop-K6Safely {

    if ($null -eq $k6Process) {
        return
    }

    try {

        if (-not $k6Process.HasExited) {

            Stop-Process `
                -Id $k6Process.Id `
                -Force `
                -ErrorAction SilentlyContinue
        }
    }
    catch {
    }
}


function Archive-FailedAttempt {

    if (-not (Test-Path $workDir)) {
        return
    }

    New-Item `
        -ItemType Directory `
        -Force `
        -Path $failedRoot |
        Out-Null

    $failedPath =
        Join-Path `
            $failedRoot `
            "runner-$runId"

    Move-Item `
        -Path $workDir `
        -Destination $failedPath `
        -Force

    Write-Host ""
    Write-Host `
        "Intento conservado en:" `
        -ForegroundColor Yellow

    Write-Host $failedPath
}


try {

    Write-Host ""
    Write-Host "==========================================" -ForegroundColor Cyan
    Write-Host " EXP-004 - EJECUCIÓN FORMAL" -ForegroundColor Cyan
    Write-Host " 499 VUs x 600 s + Android" -ForegroundColor Cyan
    Write-Host "==========================================" -ForegroundColor Cyan


    Write-Host "`n========== 1. PREPARAR EJECUCIÓN ==========" -ForegroundColor Cyan

    if (-not (Test-Path $k6Script)) {
        throw "No existe el script k6."
    }

    if (-not (Test-Path $baselineCsv)) {
        throw "No existe el baseline Android sin carga."
    }


    $formalFiles =
        @(
            $finalAndroidCsv,
            $finalOverlapCsv,
            $finalK6Json,
            $finalK6Stdout,
            $finalK6Stderr
        )

    foreach ($file in $formalFiles) {

        if (Test-Path $file) {

            throw (
                "Ya existe un artefacto formal: " +
                $file +
                ". No se sobrescribirá."
            )
        }
    }


    New-Item `
        -ItemType Directory `
        -Force `
        -Path $workAndroidLogDir |
        Out-Null


    Write-Host "Run ID:" $runId


    Write-Host "`n========== 2. PRECONDICIONES ==========" -ForegroundColor Cyan

    $health =
        Invoke-RestMethod `
            -Uri "http://localhost:8080/actuator/health" `
            -TimeoutSec 5

    if ($health.status -ne "UP") {
        throw "Backend no está UP."
    }

    Write-Host "Backend: UP" -ForegroundColor Green


    if (
        $null -ne
        (Get-Process -Name k6 -ErrorAction SilentlyContinue)
    ) {

        throw "Ya existe un proceso k6 ejecutándose."
    }

    $k6Command =
        Get-Command k6 -ErrorAction Stop

    Write-Host "k6:" $k6Command.Source


    if (-not (Test-Path $adb)) {
        throw "No se encontró adb."
    }


    $initialPid =
        (& $adb shell pidof com.example.rachapro).Trim()

    if ([string]::IsNullOrWhiteSpace($initialPid)) {
        throw "RachaPro no está ejecutándose."
    }

    Write-Host "PID Android:" $initialPid


    Write-Host "`n========== 3. CONFIRMAR SESIÓN ANDROID ==========" -ForegroundColor Cyan

    Go-Home

    $homeDoc =
        Get-UiXml `
            -RemoteFile "/sdcard/exp004-runner-user.xml"

    if ($homeDoc.OuterXml -notmatch "Hola, Load") {

        throw (
            "No se pudo confirmar la sesión " +
            "del usuario loadtest en Android."
        )
    }

    Write-Host `
        "Sesión Android confirmada." `
        -ForegroundColor Green


    Write-Host "`n========== 4. CONTRASEÑA LOCAL ==========" -ForegroundColor Cyan

    $securePassword =
        Read-Host `
            "Contraseña de los usuarios loadtest" `
            -AsSecureString

    $ptr =
        [Runtime.InteropServices.Marshal]::SecureStringToBSTR(
            $securePassword
        )

    $plainPassword =
        [Runtime.InteropServices.Marshal]::PtrToStringBSTR(
            $ptr
        )

    if ([string]::IsNullOrEmpty($plainPassword)) {
        throw "La contraseña está vacía."
    }


    $env:K6_PASSWORD =
        $plainPassword

    $env:BASE_URL =
        "http://localhost:8080"

    $env:VUS =
        "499"

    $env:DURATION =
        "600s"

    $env:RESULT_JSON =
        (
            [IO.Path]::GetFullPath($workK6Json) `
                -replace "\\","/"
        )


    $absoluteK6Script =
        [IO.Path]::GetFullPath($k6Script)

    $absoluteStdout =
        [IO.Path]::GetFullPath($workK6Stdout)

    $absoluteStderr =
        [IO.Path]::GetFullPath($workK6Stderr)


    Write-Host "`n========== 5. INICIAR K6 ==========" -ForegroundColor Cyan

    Write-Host "VUs:       499"
    Write-Host "Duración:  600 s"
    Write-Host "Android:   loadtest001"
    Write-Host "k6:        loadtest002..loadtest500"


    $k6Process =
        Start-Process `
            -FilePath $k6Command.Source `
            -ArgumentList @(
                "run",
                $absoluteK6Script
            ) `
            -RedirectStandardOutput $absoluteStdout `
            -RedirectStandardError $absoluteStderr `
            -PassThru `
            -NoNewWindow

    $null = $k6Process.Handle


    Clear-K6Environment

    $plainPassword = $null
    $securePassword = $null

    if ($ptr -ne [IntPtr]::Zero) {

        [Runtime.InteropServices.Marshal]::ZeroFreeBSTR(
            $ptr
        )

        $ptr =
            [IntPtr]::Zero
    }

    [GC]::Collect()


    Write-Host "k6 PID:" $k6Process.Id


    Write-Host "`n========== 6. ESPERAR FIN DEL SETUP ==========" -ForegroundColor Cyan

    $setupDeadline =
        (Get-Date).AddMinutes(5).AddSeconds(15)

    $loadStartEpoch =
        $null


    do {

        Start-Sleep -Seconds 1

        $loadStartEpoch =
            Get-K6Epoch `
                -Marker "EXP004_LOAD_PHASE_START"


        if ($k6Process.HasExited) {

            $reason =
                Read-K6Error

            throw (
                "k6 terminó durante setup o antes " +
                "de iniciar la fase sostenida.`n" +
                $reason
            )
        }

    }
    while (
        $null -eq $loadStartEpoch -and
        (Get-Date) -lt $setupDeadline
    )


    if ($null -eq $loadStartEpoch) {

        throw (
            "No apareció EXP004_LOAD_PHASE_START " +
            "dentro de la ventana operativa del setup."
        )
    }


    Write-Host `
        "Carga sostenida iniciada." `
        -ForegroundColor Green

    Write-Host "START epochMs:" $loadStartEpoch


    Write-Host "`n========== 7. CINCO CORRIDAS ANDROID ==========" -ForegroundColor Cyan

    $results =
        @()


    for ($run = 1; $run -le 5; $run++) {

        Write-Host ""
        Write-Host `
            "----- ANDROID $run / 5 -----" `
            -ForegroundColor Yellow


        if ($k6Process.HasExited) {

            throw (
                "k6 terminó antes de comenzar " +
                "la corrida Android $run."
            )
        }


        Go-Home

        Start-Sleep -Seconds 2


        & $adb logcat -c -b all


        $startedAt =
            [DateTimeOffset]::Now

        $startedEpoch =
            $startedAt.ToUnixTimeMilliseconds()


        Go-Activities


        $finished =
            $false

        $perfLines =
            @()

        $refreshMs =
            $null

        $getMs =
            $null


        do {

            Start-Sleep -Milliseconds 500


            $perfLines =
                @(
                    & $adb logcat `
                        -d `
                        -b all `
                        -v time |
                    Select-String `
                        -Pattern "ANDROID_ACTIVITY_GET_START|ANDROID_ACTIVITY_GET durationMs|ANDROID_REFRESH_STATUSES_START|ANDROID_REFRESH_STATUSES durationMs" |
                    ForEach-Object {
                        $_.Line
                    }
                )


            $refreshMs =
                Get-Duration `
                    -Lines $perfLines `
                    -Marker "ANDROID_REFRESH_STATUSES durationMs"


            $getMs =
                Get-Duration `
                    -Lines $perfLines `
                    -Marker "ANDROID_ACTIVITY_GET durationMs"


            $finished =
                (
                    $null -ne $refreshMs -and
                    $null -ne $getMs
                )


            if (
                -not $finished -and
                $k6Process.HasExited
            ) {

                throw (
                    "k6 terminó mientras se ejecutaba " +
                    "la corrida Android $run."
                )
            }

        }
        while (-not $finished)


        $finishedAt =
            [DateTimeOffset]::Now

        $finishedEpoch =
            $finishedAt.ToUnixTimeMilliseconds()


        $activitiesVisible =
            Test-ActivitiesVisible


        $getCount =
            @(
                $perfLines |
                Select-String `
                    "ANDROID_ACTIVITY_GET durationMs="
            ).Count


        $refreshCount =
            @(
                $perfLines |
                Select-String `
                    "ANDROID_REFRESH_STATUSES durationMs="
            ).Count


        $currentPid =
            (& $adb shell pidof com.example.rachapro).Trim()


        $perfLog =
            Join-Path `
                $workAndroidLogDir `
                ("corrida-{0:00}.txt" -f $run)


        $perfLines |
            Set-Content `
                -Path $perfLog `
                -Encoding UTF8


        if (-not $activitiesVisible) {

            throw (
                "No se confirmó la UI de Actividades " +
                "en corrida $run."
            )
        }


        if (
            $getCount -ne 1 -or
            $refreshCount -ne 1
        ) {

            throw (
                "Corrida $run produjo una cantidad " +
                "inesperada de mediciones."
            )
        }


        if ($currentPid -ne $initialPid) {

            throw (
                "El PID Android cambió durante EXP-004. " +
                "Inicial=$initialPid Actual=$currentPid"
            )
        }


        $results +=
            [PSCustomObject]@{

                corrida =
                    $run

                inicio_iso =
                    $startedAt.ToString("o")

                fin_iso =
                    $finishedAt.ToString("o")

                inicio_epoch_ms =
                    $startedEpoch

                fin_epoch_ms =
                    $finishedEpoch

                refresh_ms =
                    $refreshMs

                activity_get_ms =
                    $getMs

                get_observados =
                    $getCount

                refresh_observados =
                    $refreshCount

                activities_ui =
                    $activitiesVisible

                pid =
                    $currentPid

                completada =
                    $true
            }


        Write-Host "Refresh:        $refreshMs ms"
        Write-Host "GET activities: $getMs ms"
        Write-Host "UI:             $activitiesVisible"
        Write-Host "PID:            $currentPid"


        Go-Home

        Start-Sleep -Seconds 2
    }


    if ($results.Count -ne 5) {

        throw (
            "Se esperaban 5 corridas Android " +
            "y se obtuvieron $($results.Count)."
        )
    }


    $results |
        Export-Csv `
            -Path $workAndroidCsv `
            -NoTypeInformation `
            -Encoding UTF8


    Write-Host "`n========== 8. ESPERAR FIN DE K6 ==========" -ForegroundColor Cyan

    $k6Process.WaitForExit()
    $k6Process.Refresh()

    $k6Exit =
        $k6Process.ExitCode

    Write-Host "k6 exit code:" $k6Exit


    if ($k6Exit -ne 0) {

        $reason =
            Read-K6Error

        throw (
            "k6 terminó con código $k6Exit.`n" +
            $reason
        )
    }


    $loadEndEpoch =
        Get-K6Epoch `
            -Marker "EXP004_LOAD_PHASE_END"


    if ($null -eq $loadEndEpoch) {

        throw (
            "No se encontró EXP004_LOAD_PHASE_END."
        )
    }


    Write-Host "END epochMs:" $loadEndEpoch


    Write-Host "`n========== 9. VALIDAR SOLAPAMIENTO ==========" -ForegroundColor Cyan

    $overlap =
        foreach ($row in $results) {

            $inside =
                (
                    [int64]$row.inicio_epoch_ms -ge
                        $loadStartEpoch -and

                    [int64]$row.fin_epoch_ms -le
                        $loadEndEpoch
                )


            [PSCustomObject]@{

                corrida =
                    $row.corrida

                inicio_epoch_ms =
                    $row.inicio_epoch_ms

                fin_epoch_ms =
                    $row.fin_epoch_ms

                dentro_de_carga =
                    $inside
            }
        }


    $overlap |
        Export-Csv `
            -Path $workOverlapCsv `
            -NoTypeInformation `
            -Encoding UTF8


    $overlap |
        Format-Table -AutoSize


    if ($overlap.Count -ne 5) {

        throw (
            "El análisis temporal no contiene " +
            "5 corridas."
        )
    }


    $outsideCount =
        @(
            $overlap |
            Where-Object {
                -not $_.dentro_de_carga
            }
        ).Count


    if ($outsideCount -ne 0) {

        throw (
            "$outsideCount corridas Android " +
            "quedaron fuera de la ventana k6."
        )
    }


    Write-Host `
        "5/5 corridas dentro de la ventana k6." `
        -ForegroundColor Green


    Write-Host "`n========== 10. VALIDAR JSON K6 ==========" -ForegroundColor Cyan

    if (-not (Test-Path $workK6Json)) {
        throw "k6 no generó el JSON formal."
    }


    $k6Json =
        Get-Content `
            $workK6Json `
            -Raw `
            -Encoding UTF8 |
        ConvertFrom-Json


    if ([int]$k6Json.workload.vus -ne 499) {
        throw "El JSON no reporta 499 VUs."
    }


    if ($k6Json.workload.duration -ne "600s") {
        throw "El JSON no reporta duración 600s."
    }


    if (
        $k6Json.workload.androidUser -ne
        "loadtest001@rachapro.test"
    ) {

        throw "Usuario Android inesperado en JSON."
    }


    if (
        $k6Json.workload.k6Users -ne
        "loadtest002..loadtest500@rachapro.test"
    ) {

        throw "Rango de usuarios k6 inesperado."
    }


    if (
        $null -eq
        $k6Json.metrics.activity_get_duration
    ) {

        throw (
            "No existe métrica activity_get_duration."
        )
    }


    Write-Host `
        "JSON k6 formal válido." `
        -ForegroundColor Green


    Write-Host "`n========== 11. SEGURIDAD ==========" -ForegroundColor Cyan

    $secretMatches =
        @(
            Get-ChildItem `
                $workDir `
                -Recurse `
                -File |
            Select-String `
                -Pattern "eyJ[a-zA-Z0-9_-]+\." `
                -CaseSensitive:$false
        )


    Write-Host `
        "JWT encontrados:" `
        $secretMatches.Count


    if ($secretMatches.Count -gt 0) {

        throw (
            "Se encontraron posibles JWT. " +
            "Los artefactos no serán promovidos."
        )
    }


    Write-Host "`n========== 12. ESTADÍSTICAS ANDROID ==========" -ForegroundColor Cyan

    $getValues =
        @(
            $results |
            ForEach-Object {
                [int]$_.activity_get_ms
            } |
            Sort-Object
        )


    $stats =
        $getValues |
        Measure-Object `
            -Minimum `
            -Maximum `
            -Average


    $median =
        $getValues[2]


    Write-Host `
        "Valores:" `
        ($getValues -join ", ")

    Write-Host `
        "Mínimo:" `
        $stats.Minimum "ms"

    Write-Host `
        "Mediana:" `
        $median "ms"

    Write-Host `
        "Promedio:" `
        ([math]::Round($stats.Average, 2)) "ms"

    Write-Host `
        "Máximo:" `
        $stats.Maximum "ms"


    Write-Host "`n========== 13. MÉTRICAS K6 ==========" -ForegroundColor Cyan

    Write-Host "GET duration:"
    $k6Json.metrics.activity_get_duration |
        Format-List

    Write-Host "GET success:"
    $k6Json.metrics.activity_get_success |
        Format-List

    Write-Host "Conteo 1000:"
    $k6Json.metrics.activity_count_ok |
        Format-List

    Write-Host "Checks:"
    $k6Json.metrics.checks |
        Format-List

    Write-Host "HTTP failed:"
    $k6Json.metrics.http_req_failed |
        Format-List

    Write-Host "HTTP requests:"
    $k6Json.metrics.http_reqs |
        Format-List


    Write-Host "`n========== 14. PROMOVER RESULTADOS FORMALES ==========" -ForegroundColor Cyan

    New-Item `
        -ItemType Directory `
        -Force `
        -Path "$exp004\resultados" |
        Out-Null

    New-Item `
        -ItemType Directory `
        -Force `
        -Path "$exp004\logs" |
        Out-Null

    New-Item `
        -ItemType Directory `
        -Force `
        -Path $finalAndroidLogDir |
        Out-Null


    Move-Item `
        $workAndroidCsv `
        $finalAndroidCsv

    Move-Item `
        $workOverlapCsv `
        $finalOverlapCsv

    Move-Item `
        $workK6Json `
        $finalK6Json

    Move-Item `
        $workK6Stdout `
        $finalK6Stdout

    Move-Item `
        $workK6Stderr `
        $finalK6Stderr


    Get-ChildItem `
        $workAndroidLogDir `
        -File |
    ForEach-Object {

        Move-Item `
            $_.FullName `
            $finalAndroidLogDir
    }


    Remove-Item `
        $workDir `
        -Recurse `
        -Force `
        -ErrorAction SilentlyContinue


    Write-Host ""
    Write-Host "==========================================" -ForegroundColor Green
    Write-Host " EXP-004 FORMAL COMPLETADO" -ForegroundColor Green
    Write-Host "==========================================" -ForegroundColor Green

    Write-Host ""
    Write-Host "Android: 5/5 corridas válidas"
    Write-Host "Solapamiento k6: 5/5"
    Write-Host "k6 exit code: 0"
    Write-Host "JWT encontrados: 0"
    Write-Host "PID Android conservado:" $initialPid


    Write-Host "`n========== 15. GIT ==========" -ForegroundColor Cyan

    git status --short

    Write-Host "`n========== 16. DIFF CHECK ==========" -ForegroundColor Cyan

    git diff --check
}
catch {

    Write-Host ""
    Write-Host "==========================================" -ForegroundColor Red
    Write-Host " EXP-004 ABORTADO" -ForegroundColor Red
    Write-Host "==========================================" -ForegroundColor Red

    Write-Host ""
    Write-Host $_.Exception.Message -ForegroundColor Red

    Stop-K6Safely
    Clear-K6Environment

    Archive-FailedAttempt

    Write-Host ""
    Write-Host "No se promovieron resultados formales." -ForegroundColor Yellow

    exit 1
}
finally {

    Clear-K6Environment

    $plainPassword = $null
    $securePassword = $null

    if ($ptr -ne [IntPtr]::Zero) {

        try {

            [Runtime.InteropServices.Marshal]::ZeroFreeBSTR(
                $ptr
            )
        }
        catch {
        }
    }

    [GC]::Collect()
}
