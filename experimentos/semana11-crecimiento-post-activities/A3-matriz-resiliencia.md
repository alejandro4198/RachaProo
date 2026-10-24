# A3 — Matriz de resiliencia

## 1. Propósito

Este documento consolida el entregable A3 del Módulo 6 de RachaPro.

Su propósito es relacionar los fallos identificados en A2 con:

- el objetivo de resiliencia;
- la táctica seleccionada por el equipo;
- el criterio verificable;
- la evidencia experimental;
- el estado real de validación.

Este documento no presenta como probadas tácticas que no hayan sido ejecutadas experimentalmente.

---

## 2. Relación con A2

El fallo utilizado como entrada para este entregable es:

`F-01 — indisponibilidad temporal del backend Spring Boot`

Fuente:

`experimentos/semana11-crecimiento-post-activities/A2-matriz-fallos.md`

F-01 fue clasificado en A2 como:

`OBSERVADO`

Durante las tres repeticiones del escenario se observaron errores de comunicación `SocketTimeoutException` cuando Android intentó crear una actividad mientras Spring Boot se encontraba indisponible.

Android no presentó esas operaciones como creaciones remotas confirmadas exitosamente.

---

## 3. Fuentes de evidencia

La evidencia utilizada para A3 se encuentra en:

### Fallo y recuperación

`experimentos/semana11-crecimiento-post-activities/matrices-fallos-resiliencia.md`

### Prerregistro de la táctica

`experimentos/semana11-crecimiento-post-activities/prerregistro-spike-timeout.md`

### Resultados de la táctica

`experimentos/semana11-crecimiento-post-activities/resultados-spike-timeout.md`

---

## 4. Criterios de estado

Las tácticas o respuestas se clasifican como:

### PROBADA

Existe un protocolo previo, una intervención ejecutada, un criterio verificable y resultados experimentales suficientes para evaluar dicho criterio dentro del escenario definido.

### PROPUESTA

La táctica ha sido considerada por el equipo, pero todavía no existe evidencia experimental suficiente para presentarla como validada.

### NO PROBADA

No existe una ejecución controlada que permita atribuir esa capacidad al sistema.

---

## 5. Matriz de resiliencia

| ID | Fallo relacionado | Objetivo verificable | Táctica seleccionada | Criterio de prueba | Evidencia | Resultado | Estado |
|---|---|---|---|---|---|---|---|
| T-01 | F-01 | Reducir el tiempo que Android espera antes de informar un fallo cuando no puede establecer conexión con Spring Boot | Configurar explícitamente `connectTimeout = 3 segundos` en el cliente Android | La mediana posterior debe reducirse al menos un `50 %` respecto de la mediana de línea base | `prerregistro-spike-timeout.md` y `resultados-spike-timeout.md` | `10 028 ms -> 3 014 ms`, reducción `69,94 %` | **PROBADA EN EL ESCENARIO EVALUADO** |

---

## 6. Táctica T-01 — connectTimeout

### Fallo relacionado

`F-01`

### Problema observado

Durante la indisponibilidad de Spring Boot, Android presentó errores de comunicación asociados con `SocketTimeoutException`.

La intervención seleccionada no buscó hacer que el backend estuviera disponible.

Su objetivo fue reducir el tiempo durante el cual el cliente Android esperaba antes de informar el fallo de conexión.

### Táctica seleccionada

Se configuró explícitamente:

`connectTimeout = 3 segundos`

en el cliente HTTP utilizado por Android.

### Prerregistro

El experimento fue definido previamente en:

`experimentos/semana11-crecimiento-post-activities/prerregistro-spike-timeout.md`

Commit de prerregistro:

`94dcc32`

La intervención, los criterios de evaluabilidad, el número de corridas principales, las reglas de reemplazo y el umbral de mejora fueron definidos antes de cerrar los resultados experimentales.

---

## 7. Línea base

Las tres mediciones admitidas de línea base fueron:

```text
B1 = 10 028 ms
B2 = 10 033 ms
B3 = 10 023 ms
