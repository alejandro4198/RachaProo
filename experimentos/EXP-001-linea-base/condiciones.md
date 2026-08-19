# Condiciones de ejecución - EXP-001

## 1. Identificación

- Experimento: EXP-001
- Sistema: RachaPro
- Rama: `master`
- Fecha de ejecución: 18/08/2026
- Commit de línea base funcional: `3b088858061edc47f6cd018a4130a38c2afb0f73`
- Commit instrumentado utilizado en la medición: `59c182b12c7a2678f6ed09d1b69b399327d097d4`

## 2. Equipo de desarrollo

| Elemento | Valor |
|---|---|
| Sistema operativo | Microsoft Windows 11 Pro for Workstations 10.0.26200 |
| Procesador | Intel(R) Xeon(R) E-2224G CPU @ 3.50GHz |
| Memoria RAM | 31.84 GB |
| Versión de Android Studio | Android Studio Quail 3 \| 2026.1.3 - Build #AI-261.26222.65.2613.15948027 |

## 3. Dispositivo de ejecución

| Elemento | Valor |
|---|---|
| Tipo | Dispositivo físico |
| Fabricante / modelo | vivo V2205 |
| Versión de Android | Android 14 |
| Nivel de API | 34 |

## 4. Condiciones de la aplicación

| Elemento | Valor |
|---|---|
| Cantidad de actividades | 100 |
| Usuario | Un mismo usuario de prueba |
| Estado de las actividades | Pendiente |
| Prioridad | Media |
| Categoría | Estudio |
| Fecha límite | 31/12/2030 |
| Hora | Ninguna |
| Descripción | Vacía |
| Subtareas | Ninguna |
| Recordatorios | Ninguno |
| Repetición | Ninguna |
| Instrumento | `SystemClock.elapsedRealtime()` |
| Registro | Logcat |
| Procedimiento originalmente planificado | 4 corridas |
| Corrida originalmente descartada | Corrida 1 |
| Corridas originalmente válidas | Corridas 2, 3 y 4 |
| Resultado originalmente planificado | Mediana de las corridas 2, 3 y 4 |
| Ampliación posterior | Corridas 2 a 26: 25 mediciones válidas |
| Resultado de la ampliación | Mediana, P95 mediante Nearest Rank, mínimo y máximo |

## 5. Condiciones mantenidas entre corridas

- Se utilizó el mismo dispositivo físico: vivo V2205.
- Se mantuvo Android 14 y API 34.
- Se utilizó el mismo commit instrumentado durante todas las corridas comparables.
- Se mantuvo la misma semilla activa de 100 actividades.
- Se utilizó un arranque `COLD` para cada corrida.
- La aplicación fue detenida mediante `force-stop` antes de cada ejecución.
- Se limpió Logcat antes de iniciar cada corrida.
- App Inspection / Database Inspector permaneció cerrado durante las corridas.
- La aplicación no fue manipulada durante cada medición.
- No se realizaron cambios de código entre las corridas comparables.

## 6. Procedimiento de medición

- Instrumento: `SystemClock.elapsedRealtime()`.
- Registro de evidencia: Logcat.
- Inicio de la medición: comienzo de `loadData()` en `ActivitiesViewModel`.
- Fin de la medición: primera obtención de `ActivitiesUiState.Success` con 100 actividades.
- Corrida 1: calentamiento / descartada.
- Corridas 2, 3 y 4: mediciones válidas del procedimiento originalmente planificado.
- Métrica originalmente planificada: mediana de las corridas 2, 3 y 4.
- Ampliación posterior: corridas 2 a 26, para un total de 25 mediciones válidas.
- Métricas de la ampliación: mediana, P95 mediante Nearest Rank, mínimo y máximo.

## 7. Método de cálculo de P95

El P95 de la ampliación estadística se calculó mediante el método Nearest Rank.

Para 25 mediciones válidas:

`ceil(0.95 × 25) = 24`

Por lo tanto, el P95 corresponde al valor ubicado en la posición 24 de las mediciones ordenadas.

El cálculo se realiza mediante:

`scripts/calcular-resultados.ps1`

La evidencia generada por el script se encuentra en:

`resultados/calculo-estadistico.txt`

## 8. Consideraciones sobre la medición

La medición comprende el tiempo transcurrido desde el inicio de `loadData()` hasta la obtención de `ActivitiesUiState.Success` con las 100 actividades.

El punto final utilizado no corresponde al instante exacto en el que el listado termina de renderizarse visualmente en pantalla.

Por esta razón, la medición debe interpretarse como una aproximación al antecedente histórico RNF02 y no como una medición directa del tiempo total de renderizado de la interfaz.

La ampliación a 25 corridas se realizó después del procedimiento originalmente planificado con el propósito de obtener una estimación del P95. Esta ampliación no modifica retroactivamente el procedimiento inicial.