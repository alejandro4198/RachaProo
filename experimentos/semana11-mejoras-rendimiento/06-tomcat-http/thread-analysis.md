# Análisis de Hilos JVM — Spike 250 VUs (06-tomcat-http)

## 1. Objetivo y Contexto

Este documento detalla el análisis de los estados y comportamiento de hilos de la JVM (`jvm.threads.live` y `jvm.threads.states.*`) observado durante las corridas de alta concurrencia (250 VUs, Pool Hikari 30) en RachaPro.

---

## 2. Hallazgos Empíricos

- **Baseline (Perfil Rápido - R1, R3, R4, R6)**:
  - Threads Live Max: **24**
  - Runnable Max: **9–10**
  - Waiting Max: **10–11**
  - Timed-Waiting Max: **5**
  - Blocked Max: **0**

- **Spike de Hilos (Perfil Lento - R2, R5)**:
  - Threads Live Max: **172–176**
  - Runnable Max: **9**
  - Waiting Max: **145–150**
  - Timed-Waiting Max: **12**
  - Blocked Max: **0**

---

## 3. Análisis de Causalidad

1. **Ausencia de Contención de Monitores (`BLOCKED = 0`)**: Se descarta de forma concluyente que el cuello de botella se deba a bloqueos clásicos de sincronización Java (`synchronized` o monitores bloqueados).
2. **Dominancia de Hilos en Espera (`WAITING`)**: El aumento repentino de ~150 hilos en estado `WAITING` explica la congestión observada en `http_req_waiting`. Esto apunta a encolamiento en pools de ejecución, futures o espera de I/O en red/servidor bajo carga masiva.
3. **Estabilidad de CPU y Hikari**: El proceso backend opera con un uso de CPU moderado (~24-27%) y un pool Hikari con 0 peticiones pendientes (`pending = 0`), confirmando que la limitación no radica en el pool de base de datos ni en el cómputo de la CPU.

---

## 4. Conclusiones

- **Proliferación de Hilos JVM (`WAITING`)**: `DEMOSTRADA` (Salto reproducible de 24 a 176 hilos).
- **Tomcat / Pipeline HTTP**: `INDICIO FUERTE` (Correlación directa entre hilos en espera y latencia de red).
- **Locks y CPU**: `DESCARTADO CON EVIDENCIA`.
