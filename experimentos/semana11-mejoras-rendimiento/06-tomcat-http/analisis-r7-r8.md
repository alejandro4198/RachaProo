# Análisis de Causalidad R7, R8 y R9 — Diagnóstico S11

## 1. Problema Investigado

Determinar la diferencia entre la ejecución R8 (donde el arnés finalizó en la fase de preparación registrando 0 llamadas a `POST /api/activities`) y las ejecuciones R7 y R9 (donde la preparación completó e inició la prueba sostenida del endpoint de creación de actividades).

---

## 2. Conteos Basados en `"metric":"http_reqs"` + `"type":"Point"` y Timestamps

| Métrica / Fase | R7 (Prueba Completa) | R8 (Setup Incompleto) | R9 (Setup Paralelizado) |
| :--- | :---: | :---: | :---: |
| **`SETUP login` count** | 250 | **111** | 250 |
| **`SETUP categories` count** | 250 | **111** | 250 |
| **`POST /api/activities` count** | **13,901** | **0** | **13,499** |
| **`http_reqs` Total** | 14,401 | **222** | 13,999 |
| **Inicio Real Setup** | 19:11:54.955 | 19:19:05.850 | 21:29:34.052 |
| **Fin Real Setup** | 19:16:21.686 | 19:21:07.491 | 21:31:42.694 |
| **Duración Real Setup** | **266.73 s** | **121.64 s** | **128.64 s** |
| **Duración Escenario Actividades** | **59.05 s** | **0 s (No iniciado)** | **59.32 s** |
| **Tasa Observada `POST /api/activities`** | **235.40 req/s** | **0 req/s** | **227.50 req/s** |
| **Throughput Global (`http_reqs.rate`)** | 43.72 req/s | 1.79 req/s | 72.43 req/s |
| **P95 `POST /api/activities`** | 152.01 ms | **NA** | 327.29 ms |

---

## 3. Diagnóstico del Harness Experimental

1. **R8 Incompleto en `setup()`**:
   - R8 ejecutó únicamente 222 solicitudes HTTP pertenecientes a `SETUP login` (111) y `SETUP categories` (111).
   - A los 121.64 segundos, la ejecución k6 finalizó en la fase de preparación previa al escenario principal de creación de actividades (`POST /api/activities`).
   - El mecanismo exacto que interrumpió R8 a los ~121.64 s es `NO DETERMINABLE` al no existir un registro de error explícito en `k6-stderr.txt`.

2. **Costo de Autenticación PBKDF2**:
   - `AuthService.login()` procesa 210,000 iteraciones PBKDF2WithHmacSHA1 (~1.06–1.10s por login).
   - Autenticar secuencialmente a 250 usuarios en `setup()` requiere ~260 segundos de preparación previa.

3. **Mejora del Harness en R9**:
   - Mediante `http.batch()` en `crecimiento-post-activities.js`, las peticiones de `setup()` se enviaron en lotes paralelos.
   - Completada la preparación, R9 procesó **13,499 creaciones de actividades** a una tasa observada de **227.50 req/s** durante la ventana del escenario con P95 de **327.29 ms** y 0 peticiones pendientes en Hikari Pool = 30.

---

## 4. Matriz de Causalidad Consolidada

| Hipótesis | Estado | Evidencia |
| :--- | :--- | :--- |
| **R8 como ejecución incompleta en `setup()`** | `DEMOSTRADO` | 0 llamadas a `POST /api/activities`; 100% de peticiones en `::setup`. |
| **Mecanismo exacto de terminación de R8** | `NO DETERMINABLE` | Ausencia de log de error o excepción en `stderr`. |
| **Costo CPU de PBKDF2 210k en Login** | `DEMOSTRADO` | `SETUP login` promedia 1,065 ms a 1,102 ms por petición en la JVM. |
| **Desempeño de `POST /api/activities` tras Setup** | `DEMOSTRADO` | >13,400 operaciones procesadas a >227 req/s durante el escenario objetivo en R7 y R9. |
| **HikariCP Pool = 30** | `DEMOSTRADO ESTABLE` | 0 peticiones pendientes (`pending = 0`) a 250 VUs. |
| **Bloqueos Java (`BLOCKED`)** | `EVIDENCIA EN CONTRA` | `jvm.threads.states.blocked = 0` en todas las mediciones. |
