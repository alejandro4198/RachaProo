$logsDir = Join-Path $PSScriptRoot "..\logs"

$values = @()

2..26 | ForEach-Object {

    $numero = "{0:D2}" -f $_
    $archivo = Join-Path $logsDir "corrida-$numero.txt"

    $match =
        Select-String `
            -Path $archivo `
            -Pattern "load_ms=(\d+)" |
        Select-Object -First 1

    if (-not $match) {
        throw "No se encontro load_ms en $archivo"
    }

    $values +=
        [int]$match.Matches[0].Groups[1].Value
}

$sorted = @($values | Sort-Object)

$n = $sorted.Count

$median =
    if ($n % 2 -eq 1) {
        $sorted[[math]::Floor($n / 2)]
    } else {
        ($sorted[($n / 2) - 1] + $sorted[$n / 2]) / 2
    }

$p95Position =
    [math]::Ceiling(0.95 * $n)

$p95 =
    $sorted[$p95Position - 1]

$minimum =
    $sorted[0]

$maximum =
    $sorted[-1]

Write-Output "Mediciones validas: $n"
Write-Output "Valores ordenados: $($sorted -join ', ')"
Write-Output "Mediana: $median ms"
Write-Output "P95 (Nearest Rank): $p95 ms"
Write-Output "Posicion P95: $p95Position"
Write-Output "Minimo: $minimum ms"
Write-Output "Maximo: $maximum ms"