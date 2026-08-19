# 04 - Medición y línea base

## 1. Sistema y versión medida

EXP-001 se ejecutó sobre la implementación funcional actual de RachaPro para Android.

| Elemento | Valor |
|---|---|
| Línea base funcional | `3b088858061edc47f6cd018a4130a38c2afb0f73` |
| Commit instrumentado medido | `59c182b12c7a2678f6ed09d1b69b399327d097d4` |
| Rama | `master` |
| Fecha de ejecución | 18/08/2026 |

El primer commit corresponde al estado funcional previo a la instrumentación. El segundo incorpora únicamente el registro utilizado para medir EXP-001.

## 2. Escenario e hipótesis previa

El atributo evaluado fue **Rendimiento**, específicamente en el módulo de actividades.

La medición comprende:

- **Inicio:** comienzo de `loadData()` en `ActivitiesViewModel`.
- **Fin:** primera obtención de `ActivitiesUiState.Success` con 100 actividades.
- **Unidad:** milisegundos.
- **Umbral histórico RNF02:** `≤ 3 segundos`.

La hipótesis definida antes de ejecutar el experimento fue:

> Con una semilla de 100 actividades registradas, el tiempo de carga del listado principal podría superar el umbral histórico de 3 segundos.

## 3. Semilla

Se utilizó una semilla controlada de 100 actividades pertenecientes al mismo usuario de prueba.

| Característica | Valor |
|---|---|
| Estado | Pendiente |
| Prioridad | Media |
| Categoría | Estudio |
| Títulos | `Actividad 001` a `Actividad 100` |
| Descripción | Vacía |
| Subtareas | Ninguna |
| Recordatorios | Ninguno |
| Repetición | Ninguna |
| Fecha límite | 31/12/2030 |
| Hora | Ninguna |

La misma semilla se mantuvo durante las corridas comparables.

## 4. Instrumento y condiciones

La medición utilizó `SystemClock.elapsedRealtime()` y los resultados fueron registrados mediante Logcat.

Condiciones principales:

- Dispositivo físico: vivo V2205.
- Android 14, API 34.
- Windows 11 Pro for Workstations.
- Intel Xeon E-2224G @ 3.50 GHz.
- 31.84 GB de RAM.
- Arranque `COLD`.
- Mismo commit instrumentado.
- Misma semilla de 100 actividades.
- `force-stop` antes de cada corrida.
- Logcat limpiado antes de cada ejecución.
- App Inspection / Database Inspector cerrado.
- Sin cambios de código entre corridas comparables.

El detalle completo se encuentra en `experimentos/EXP-001-linea-base/condiciones.md`.

## 5. Ejecución y validación

Antes de las corridas oficiales se validó el instrumento, obteniendo:

`load_ms=1311 activities=100`

Esta ejecución se utilizó únicamente para comprobar el funcionamiento del instrumento y no se incluyó en los resultados oficiales.

El procedimiento originalmente planificado contempló cuatro corridas:

| Corrida | Uso |
|---|---|
| 1 | Calentamiento / descartada |
| 2 | Válida |
| 3 | Válida |
| 4 | Válida |

La corrida 1 fue excluida porque había sido definida previamente como calentamiento.

Posteriormente, EXP-001 fue ampliado hasta obtener **25 mediciones válidas**, correspondientes a las corridas 2 a 26, con el propósito de estimar P95.

## 6. Resultado originalmente planificado

| Corrida | Tiempo |
|---|---:|
| 2 | 1305 ms |
| 3 | 1317 ms |
| 4 | 1308 ms |

Mediana:

`1308 ms = 1.308 s`

Este es el resultado del procedimiento originalmente definido.

## 7. Ampliación estadística

Sobre las 25 mediciones válidas se obtuvieron:

| Métrica | Resultado |
|---|---:|
| Mediana | 1325 ms (1.325 s) |
| P95 | 1621 ms (1.621 s) |
| Mínimo | 1305 ms (1.305 s) |
| Máximo | 1666 ms (1.666 s) |

El P95 se calculó mediante **Nearest Rank**:

`ceil(0.95 × 25) = 24`

Por tanto:

`P95 = 1621 ms`

El cálculo es reproducible mediante `scripts/calcular-resultados.ps1`.

## 8. Comparación con el umbral

El umbral histórico de referencia es `≤ 3 s`.

| Resultado | Tiempo | Comparación |
|---|---:|---|
| Mediana original | 1.308 s | Por debajo |
| Mediana ampliada | 1.325 s | Por debajo |
| P95 | 1.621 s | Por debajo |
| Máximo observado | 1.666 s | Por debajo |

Bajo las condiciones de EXP-001, los resultados observados **no respaldan la hipótesis preliminar** de que la carga con 100 actividades podría superar 3 segundos.

## 9. Alcance

EXP-001 establece una línea base del tiempo transcurrido entre el inicio de `loadData()` y la obtención de `ActivitiesUiState.Success` con 100 actividades.

Los resultados describen únicamente el comportamiento observado bajo las condiciones documentadas y no permiten generalizar a otros dispositivos, versiones de Android, tamaños de semilla o condiciones de ejecución.

La comparación con RNF02 es aproximada porque el experimento no mide directamente el instante exacto en que termina el renderizado visual del listado.

## 10. Limitaciones

- Se utilizó un único dispositivo físico.
- La semilla estuvo limitada a 100 actividades con características controladas.
- No se evaluaron otros tamaños o distribuciones de datos.
- El punto final es `ActivitiesUiState.Success`, no el renderizado visual completo.
- El P95 se estimó a partir de 25 mediciones válidas.
- No se realizaron cambios arquitectónicos ni comparación entre alternativas.
- No se evaluaron seguridad, disponibilidad, usabilidad ni precisión.
- El escenario medido es local; solicitudes HTTP y códigos de respuesta no aplican a EXP-001.
- Las mediciones no permiten atribuir causalmente las variaciones de tiempo a un componente específico.

## 11. Ubicación de las evidencias

La evidencia reproducible se encuentra en:

`experimentos/EXP-001-linea-base/`

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
    └── corrida-01.txt ... corrida-26.txt
```

Los logs contienen la evidencia cruda de las corridas; `resultado-linea-base.md` sintetiza los resultados y `calculo-estadistico.txt` conserva la salida reproducible de mediana, P95, mínimo y máximo.