# EXP-001 - Línea base

## 1. Identificación del experimento

| Elemento | Definición |
|---|---|
| Experimento | EXP-001 |
| Propósito | Medición de línea base del rendimiento del módulo de actividades |
| Sistema | RachaPro |
| Commit de línea base funcional | `3b088858061edc47f6cd018a4130a38c2afb0f73` |
| Commit instrumentado utilizado para medir | `59c182b12c7a2678f6ed09d1b69b399327d097d4` |
| Rama | `master` |
| Estado | Ejecutado |
| Fecha de ejecución | 18/08/2026 |

El commit de línea base funcional corresponde al estado del sistema previo a incorporar la instrumentación utilizada en EXP-001.

El commit instrumentado incorpora el registro necesario para observar el tiempo definido para el experimento.

## 2. Escenario medido

| Elemento | Definición |
|---|---|
| Atributo de calidad | Rendimiento |
| Área evaluada | Módulo de actividades |
| Semilla | 100 actividades |
| Inicio de la medición | Inicio de `loadData()` en `ActivitiesViewModel` |
| Fin de la medición | Obtención de `ActivitiesUiState.Success` con las 100 actividades cargadas |
| Métrica | Tiempo transcurrido |
| Unidad | Milisegundos, con conversión a segundos |
| Umbral histórico de referencia | ≤ 3 segundos |
| Instrumento definido | `SystemClock.elapsedRealtime()` |
| Registro de evidencia | Logcat |

La medición utilizada en EXP-001 finaliza al obtener `ActivitiesUiState.Success`. Por lo tanto, no representa directamente el instante exacto en que termina el renderizado visual del listado.

## 3. Hipótesis previa

Antes de ejecutar EXP-001 se definió la siguiente hipótesis:

> Con una semilla de 100 actividades registradas, el tiempo de carga del listado principal podría superar el umbral histórico de 3 segundos.

Al momento de formular esta hipótesis todavía no existían resultados experimentales de EXP-001.

## 4. Semilla del experimento

| Característica | Definición |
|---|---|
| Cantidad | 100 actividades |
| Usuario propietario | Un mismo usuario de prueba |
| Estado | Pendiente |
| Prioridad | Media |
| Categoría | Estudio |
| Títulos | `Actividad 001` hasta `Actividad 100` |
| Descripción | Vacía |
| Subtareas | Ninguna |
| Recordatorios | Ninguno |
| Repetición | Ninguna |
| Fecha límite | 31/12/2030 |
| Hora | Ninguna |

La misma semilla se mantuvo durante las corridas comparables del experimento.

Las condiciones detalladas de la semilla y del entorno se encuentran en `condiciones.md`.

## 5. Procedimiento originalmente planificado

Antes de ejecutar las mediciones se definió un procedimiento de cuatro corridas utilizando la misma versión instrumentada, la misma semilla y condiciones comparables.

| Corrida | Uso |
|---|---|
| Corrida 1 | Calentamiento / descartada del resultado |
| Corrida 2 | Medición válida |
| Corrida 3 | Medición válida |
| Corrida 4 | Medición válida |

Antes de conocer los resultados se definió que la línea base sería calculada mediante la mediana de las corridas 2, 3 y 4.

La corrida 1 fue definida previamente como calentamiento, por lo que su exclusión no depende del valor obtenido durante su ejecución.

### Criterios de validez de una corrida

Una corrida se considera válida para EXP-001 cuando:

- Utiliza el commit instrumentado definido para el experimento.
- Se ejecuta con la semilla activa de 100 actividades.
- El arranque corresponde a `COLD`.
- Logcat registra un resultado de EXP-001 con `load_ms`.
- El registro contiene `activities=100`.
- No se realizan cambios de código entre corridas comparables.

## 6. Ejecución del experimento

La medición se ejecutó el 18/08/2026 sobre la versión instrumentada del sistema.

Condiciones principales:

- Commit de línea base funcional: `3b088858061edc47f6cd018a4130a38c2afb0f73`
- Commit instrumentado utilizado para medir: `59c182b12c7a2678f6ed09d1b69b399327d097d4`
- Dispositivo: vivo V2205
- Android: 14
- API: 34
- Semilla activa: 100 actividades
- Instrumento: `SystemClock.elapsedRealtime()`
- Evidencia de medición: Logcat
- Tipo de arranque utilizado: `COLD`

### 6.1. Validación del instrumento

Antes de las corridas oficiales se realizó una ejecución de validación con la semilla definitiva.

El registro obtenido fue:

`load_ms=1311 activities=100`

Esta ejecución fue utilizada únicamente para verificar el funcionamiento del instrumento y no fue incluida en el cálculo de la línea base.

La evidencia se conserva en:

`logs/validacion-instrumento.txt`

### 6.2. Procedimiento originalmente ejecutado

Las cuatro corridas inicialmente previstas produjeron:

| Corrida | Tiempo | Uso |
|---|---:|---|
| 1 | 1310 ms | Calentamiento / descartada |
| 2 | 1305 ms | Válida |
| 3 | 1317 ms | Válida |
| 4 | 1308 ms | Válida |

Los valores utilizados para el cálculo original fueron:

`1305 ms, 1317 ms, 1308 ms`

Ordenados:

`1305 ms, 1308 ms, 1317 ms`

La mediana originalmente planificada fue:

`1308 ms = 1.308 s`

Este valor corresponde al resultado del procedimiento definido antes de ejecutar EXP-001.

### 6.3. Ampliación estadística para P95

Después de completar el procedimiento originalmente planificado, EXP-001 fue ampliado con el propósito de obtener una estimación del percentil 95.

La ampliación continuó hasta disponer de 25 mediciones válidas, correspondientes a las corridas 2 a 26.

La corrida 1 continuó excluida por haber sido definida previamente como calentamiento.

Los resultados de la ampliación fueron:

| Métrica | Resultado |
|---|---:|
| Mediciones válidas | 25 |
| Mediana | 1325 ms (1.325 s) |
| P95 | 1621 ms (1.621 s) |
| Mínimo | 1305 ms (1.305 s) |
| Máximo | 1666 ms (1.666 s) |

El P95 se calculó mediante el método **Nearest Rank**.

Para 25 mediciones:

`ceil(0.95 × 25) = 24`

Por lo tanto, el valor ubicado en la posición 24 de las mediciones ordenadas corresponde a:

`P95 = 1621 ms = 1.621 s`

El cálculo puede reproducirse mediante:

`scripts/calcular-resultados.ps1`

La salida obtenida se conserva en:

`resultados/calculo-estadistico.txt`

### 6.4. Comparación con el umbral histórico

El antecedente histórico RNF02 establece un tiempo de referencia de:

`≤ 3 segundos`

| Resultado | Tiempo | Comparación |
|---|---:|---|
| Mediana originalmente planificada | 1.308 s | Por debajo del umbral |
| Mediana de 25 mediciones | 1.325 s | Por debajo del umbral |
| P95 | 1.621 s | Por debajo del umbral |
| Máximo observado | 1.666 s | Por debajo del umbral |

Bajo las condiciones de EXP-001, los resultados obtenidos no respaldan la hipótesis preliminar de que la carga con 100 actividades podría superar los 3 segundos.

Esta comparación debe interpretarse dentro de las condiciones específicas del experimento.

### 6.5. Alcance y limitaciones

EXP-001 establece una línea base del tiempo transcurrido desde el inicio de `loadData()` hasta la obtención de `ActivitiesUiState.Success` con 100 actividades.

El experimento no mide directamente el instante exacto en que el listado termina de renderizarse visualmente en pantalla. Por esta razón, la comparación con RNF02 constituye una aproximación al antecedente histórico.

Además:

- Se utilizó un único dispositivo físico.
- Se utilizó una semilla fija de 100 actividades.
- No se evaluaron otros tamaños o distribuciones de datos.
- El P95 se estimó a partir de 25 mediciones válidas.
- No se realizaron cambios arquitectónicos ni comparaciones entre alternativas.
- Los resultados no permiten atribuir causalmente las variaciones observadas a un componente específico.
- La ampliación estadística fue realizada después del procedimiento originalmente definido y no modifica retroactivamente la línea base inicial.

## 7. Reproducción de una corrida

Para reproducir una corrida se requiere:

- Dispositivo conectado y autorizado mediante ADB.
- Versión instrumentada instalada.
- Semilla definida en `condiciones.md`.
- Mismas condiciones de ejecución documentadas para EXP-001.

Las corridas fueron ejecutadas desde PowerShell utilizando ADB.

Primero se definió la ubicación de ADB:

```powershell
$adb = "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe"
```

Para cada corrida se utilizó el siguiente procedimiento:

```powershell
& $adb logcat -c
& $adb shell am force-stop com.example.rachapro

Start-Sleep -Seconds 3

$launch = & $adb shell am start -W -n com.example.rachapro/.MainActivity

Start-Sleep -Seconds 5

$exp = & $adb logcat -d | Select-String "EXP-001.*load_ms="
```

Una corrida comparable debe producir:

- Arranque identificado como `COLD`.
- Registro correspondiente a `EXP-001`.
- Valor `load_ms`.
- Valor `activities=100`.

Las condiciones completas del dispositivo, semilla y entorno están documentadas en:

`condiciones.md`

### Reproducción del cálculo estadístico

Desde el directorio:

`experimentos/EXP-001-linea-base/`

se pueden recalcular las métricas de las 25 corridas válidas ejecutando:

```powershell
.\scripts\calcular-resultados.ps1
```

El script obtiene:

- Número de mediciones válidas.
- Valores ordenados.
- Mediana.
- P95 mediante Nearest Rank.
- Posición utilizada para P95.
- Mínimo.
- Máximo.

La salida reproducible del cálculo se encuentra en:

`resultados/calculo-estadistico.txt`

## 8. Evidencias

La evidencia de EXP-001 se encuentra organizada de la siguiente manera:

```text
EXP-001-linea-base/
├── README.md
├── condiciones.md
├── scripts/
│   └── calcular-resultados.ps1
├── resultados/
│   ├── resultado-linea-base.md
│   └── calculo-estadistico.txt
└── logs/
    ├── validacion-instrumento.txt
    ├── corrida-01.txt
    ├── corrida-02.txt
    ├── ...
    └── corrida-26.txt
```

### Relación de evidencias

- `condiciones.md`: condiciones del entorno, dispositivo, semilla y procedimiento.
- `logs/validacion-instrumento.txt`: validación previa del instrumento.
- `logs/corrida-01.txt`: corrida de calentamiento descartada del cálculo.
- `logs/corrida-02.txt` a `logs/corrida-26.txt`: evidencia individual de las corridas utilizadas en EXP-001 y su ampliación.
- `resultados/resultado-linea-base.md`: síntesis de los resultados.
- `resultados/calculo-estadistico.txt`: salida reproducible del cálculo estadístico.
- `scripts/calcular-resultados.ps1`: cálculo automático de mediana, P95, mínimo y máximo.