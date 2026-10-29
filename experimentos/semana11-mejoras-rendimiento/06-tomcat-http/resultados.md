# Resultados del Spike — Análisis de Conector HTTP e Hilos JVM (06-tomcat-http)

## 1. Identificación y Resumen

Este documento registra los resultados de las corridas R1 a R6 ejecutadas con Pool Hikari = 30 y 250 VUs sobre `POST /api/activities`, incorporando la instrumentación ampliada de `http.server.requests.active` y estados de hilos JVM (`jvm.threads.states.*`).

---

## 2. Consolidado de Corridas (R1 a R6)

| Corrida | Perfil | P95 (ms) | Throughput (req/s) | Waiting Avg (ms) | Active Tasks Max | Threads Live Max | Runnable Max | Waiting Threads Max |
| :---: | :---: | :---: | :---: | :---: | :---: | :---: | :---: | :---: |
| **R1** | Rápido | 111.61 | 47.20 | 68.54 | 0 | 24 | 0 | 11 |
| **R2** | Lento | 1134.30 | 1.97 | 504.63 | 0 | 172 | 0 | 145 |
| **R3** | Rápido | 296.23 | 45.44 | 90.72 | 0 | 24 | 0 | 11 |
| **R4** | Rápido | 146.70 | 39.42 | 90.35 | 2 | 24 | 10 | 10 |
| **R5** | Lento | 2032.83 | 1.29 | 771.29 | 2 | 176 | 9 | 150 |
| **R6** | Rápido | 153.02 | 46.01 | 80.75 | 2 | 24 | 9 | 10 |

---

## 3. Análisis Causal y Correlación

1. **Bimodalidad Demostrada**: Las corridas rápidas (R1, R3, R4, R6) operan con un baseline estable de **24 hilos vivos** y un throughput de ~39–47 req/s. Las corridas lentas (R2, R5) exhiben una proliferación drástica a **172–176 hilos vivos**, coincidiendo con la caída del throughput a ~1.3–2.0 req/s.
2. **Distribución de Estados de Hilos**: El incremento masivo en hilos vivos durante el perfil lento se concentra principalmente en estados de espera (`WAITING`/`TIMED_WAITING` alcanzando hasta 150 hilos), sugiriendo contención o encolamiento en recursos compartidos del runtime o red antes de llegar a la persistencia.

---

## 4. Conclusiones

- **Proliferación de Hilos JVM**: `DEMOSTRADA` (Salto reproducible de 24 a 176 hilos en perfil lento).
- **Tomcat / Pipeline HTTP**: `INDICIO FUERTE` (Correlación directa entre congestión de latencia `http_req_waiting` y explosión de hilos).
- **HikariCP / PostgreSQL**: `DESCARTADO CON EVIDENCIA COMO CAUSA PRINCIPAL` (Hikari active/pending y sesiones DB se mantienen holgados sin saturación).
