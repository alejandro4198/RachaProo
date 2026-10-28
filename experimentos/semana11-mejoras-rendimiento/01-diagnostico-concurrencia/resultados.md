# Resultados del Diagnóstico de Concurrencia S11 — RachaPro

## 1. Identificación y Objetivos

Este documento analiza los resultados consolidados de la campaña diagnóstica ejecutada sobre la operación `POST /api/activities` en la base de datos experimental aislada `rachapro_perf_s11` (127.0.0.1:5433).

---

## 2. Comparativa de Resultados (Campaña Histórica vs. Diagnóstico Nuevo)

| VUs | P95 Histórico (ms) | P95 Diagnóstico Nuevo (ms) | Throughput Diagnóstico (req/s) | Tasa de Éxito (%) | Errores 5xx |
|---:|---:|---:|---:|---:|---:|
| **10** | 70.97 | 64.89 | 8.47 | 100 % | 0 |
| **50** | 140.10 | 139.66 | 26.58 | 100 % | 0 |
| **250** | 279.94 | 278.70 | 42.93 | 100 % | 0 |
| **1000** | 1597.28 | 1358.61 | 1.85 (Encolado masivo) | 100 % | 0 |

---

## 3. Matriz Final de Evaluaciones de Hipótesis

| Hipótesis | Evidencia a Favor | Evidencia en Contra | Estado Final |
| :--- | :--- | :--- | :--- |
| **$H_1$: HikariCP** | Se observó estabilización del throughput sin errores 5xx. | Sin telemetría suficiente de conexiones pendientes en campaña S11. | `NO DEMOSTRADO` |
| **$H_2$: CPU Backend** | P95 crece de forma constante bajo mayor concurrencia. | Sin telemetría de CPU del proceso Spring Boot en la campaña S11. | `NO DEMOSTRADO` |
| **$H_3$: PostgreSQL** | PostgreSQL operó en base aislada sin arrojar errores. | Sin telemetría de locks o I/O de disco durante la ejecución. | `NO DEMOSTRADO` |
| **$H_4$: JVM/GC** | Jitter moderado entre repeticiones. | Sin registros de pausas de GC en logs de la JVM. | `NO DEMOSTRADO` |
| **$H_5$: Thread Pool** | Peticiones son aceptadas y procesadas con $100\%$ de éxito pero mayor latencia. | Sin métricas de hilos activos de Tomcat. | `NO DEMOSTRADO` |
| **$H_6$: Host/Generador k6** | k6 ejecutó todas las peticiones agendadas. | Sin medición de CPU del host generador. | `NO DEMOSTRADO` |

---

## 4. Conclusión Metodológica Final

`CAUSA NO AISLADA POR FALTA DE TELEMETRÍA SÍNCRONA EN LA CAMPAÑA HISTÓRICA`

**Conclusión**: Las mediciones confirman la reproducibilidad de la degradación relativa de la latencia P95 a partir de 50 VUs y la conservación del $100\%$ de éxito funcional sin errores HTTP 5xx. Sin embargo, en estricto apego al método experimental, **la causa raíz específica permanece NO DEMOSTRADA debido a la ausencia de telemetría síncrona completa** de CPU, memoria, hilos, pool HikariCP e indicadores de PostgreSQL durante la campaña.
