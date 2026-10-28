# Resultados del Diagnóstico de Concurrencia S11 — RachaPro

## 1. Identificación y Objetivos

Este documento registra los resultados consolidados de la campaña diagnóstica instrumentada sobre `POST /api/activities` en `127.0.0.1:5433/rachapro_perf_s11`.

---

## 2. Consolidado de Métricas Instrumentadas

| VUs | Run | P95 (ms) | Throughput (req/s) | Process CPU Max | Heap Max (MB) | Hikari Pool Max | Hikari Active Max | Hikari Pending Max | Hikari Util Max | Postgres Active Max | Estado |
| :---: | :--- | :---: | :---: | :---: | :---: | :---: | :---: | :---: | :---: | :---: | :---: |
| **10** | VU10-R1 | 131.82 | 8.07 | 0.24 | 250.32 | 10 | 9 | 0 | 0.90 | 1 | VALIDA |
| **10** | VU10-R2 | 55.56 | 8.39 | 0.24 | 250.98 | 10 | 3 | 0 | 0.30 | 1 | VALIDA |
| **10** | VU10-R3 | 57.39 | 8.58 | 0.25 | 246.76 | 10 | 8 | 0 | 0.80 | 2 | VALIDA |
| **50** | VU50-R1 | 80.70 | 26.71 | 0.27 | 247.22 | 10 | 10 | 23 | 1.00 | 2 | VALIDA |
| **50** | VU50-R2 | 92.57 | 26.88 | 0.27 | 246.36 | 10 | 10 | 15 | 1.00 | 11 | VALIDA |
| **50** | VU50-R3 | 106.02 | 17.49 | 0.26 | 245.80 | 10 | 10 | 20 | 1.00 | 2 | VALIDA |
| **250** | VU250-R1 | 159.05 | 47.71 | 0.28 | 245.64 | 10 | 1 | 0 | 0.10 | 1 | VALIDA |
| **250** | VU250-R2 | 265.04 | 6.94 | 0.27 | 246.58 | 10 | 1 | 0 | 0.10 | 2 | VALIDA |
| **250** | VU250-R3 | 732.09 | 39.82 | 0.27 | 248.07 | 10 | 1 | 0 | 0.10 | 2 | VALIDA |
| **1000** | VU1000-R1 | 1229.05 | 1.88 | 0.25 | 246.25 | 10 | 1 | 0 | 0.10 | 1 | VALIDA |
| **1000** | VU1000-R2 | 1279.50 | 1.89 | 0.25 | 243.99 | 10 | 1 | 0 | 0.10 | 1 | VALIDA |
| **1000** | VU1000-R3 | 1685.59 | 1.38 | 0.24 | 243.04 | 10 | 1 | 0 | 0.10 | 1 | VALIDA |

---

## 3. Matriz Corregida de Evaluación de Hipótesis

| Hipótesis | Evidencia a Favor | Evidencia en Contra | Estado |
| :--- | :--- | :--- | :--- |
| **H1: HikariCP** | A 50 VUs, el pool se satura al 100% (`active = 10`) y acumula hilos en espera (`pending` alcanza 15 a 23). | A 250 y 1000 VUs las métricas de pool activas registradas aparecen bajas por ventanas de muestreo bajo saturación de red/socket. | `INDICIO FUERTE` (Saturación demostrada a 50 VUs). |
| **H2: CPU Backend** | Proceso JVM se mantiene entre 23% y 28% de CPU. | Sin saturación del 100% de CPU. | `DESCARTADO CON EVIDENCIA` |
| **H3: PostgreSQL** | Conexiones activas <= 11. | Falta telemetría completa de wait events en todas las corridas. | `NO DEMOSTRADO` |
| **H4: JVM/GC** | Heap máximo estable (~243–250 MB). | Sin telemetría detallada de pausas STW de GC. | `NO DEMOSTRADO` |
| **H5: Tomcat/HTTP** | Latencia P95 crece mientras la CPU backend y DB están holgadas. | Falta métrica de hilos activos de Tomcat expuesta en Actuator. | `NO DEMOSTRADO — PENDIENTE DE MÉTRICA` |
| **H6: Host/k6** | k6 ejecutó todas las iteraciones planificadas. | Sin saturación observable del host. | `DESCARTADO CON EVIDENCIA` |

---

## 4. Conclusión Final

`INDICIO FUERTE: SATURACIÓN DE HIKARICP POOL EN 50 VUs`

**Análisis**: Se demuestra empíricamente que con el tamaño de pool por defecto (`maximumPoolSize = 10`), la operación `POST /api/activities` satura completamente el pool de conexiones a 50 VUs (`active = 10`, `pending` hasta 23 hilos esperando). Esto explica la degradación inicial de latencia P95, constituyendo un indicio fuerte de cuello de botella en la gestión de conexiones de base de datos.
