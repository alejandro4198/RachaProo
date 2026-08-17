# EXP-001 - Línea base

## 1. Identificación del experimento

| Elemento | Definición |
|---|---|
| Experimento | EXP-001 |
| Propósito | Medición de línea base del rendimiento del módulo de actividades |
| Sistema | RachaPro |
| Commit de línea base | `3b088858061edc47f6cd018a4130a38c2afb0f73` |
| Rama | `master` |
| Estado | Preparación |

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
| Instrumento planificado | `SystemClock.elapsedRealtime()` |
| Registro planificado | Logcat |

## 3. Hipótesis previa

Con una semilla de 100 actividades registradas, el tiempo de carga del listado principal podría superar el umbral histórico de 3 segundos.

Hasta este punto la hipótesis no ha sido comprobada experimentalmente.

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

La misma semilla deberá mantenerse en todas las corridas comparables del experimento.

## 5. Procedimiento planificado

El experimento se ejecutará cuatro veces utilizando la misma versión del sistema, la misma semilla y condiciones comparables.

| Corrida | Uso |
|---|---|
| Corrida 1 | Calentamiento. Se descarta del resultado final. |
| Corrida 2 | Medición válida |
| Corrida 3 | Medición válida |
| Corrida 4 | Medición válida |

El resultado de línea base se obtendrá calculando la mediana de las corridas 2, 3 y 4.

Hasta este punto las corridas no han sido ejecutadas y no existen resultados experimentales registrados.
