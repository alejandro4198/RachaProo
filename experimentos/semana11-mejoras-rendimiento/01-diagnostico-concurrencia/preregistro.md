# Prerregistro — Diagnóstico de Concurrencia y Aislamiento de Cuello de Botella

## 1. Identificación y Pregunta Experimental

- **Proyecto**: RachaPro
- **Módulo**: Semana 11 — Mejoras de Rendimiento y Resiliencia
- **Operación Evaluada**: `POST /api/activities` (Creación de Actividades)
- **Pregunta Experimental**:
  > ¿Cuál es el recurso que coincide temporalmente con la degradación de latencia observada a partir de 50 VUs en `POST /api/activities`? ¿Puede aislarse con telemetría sincronizada (backend/JVM, HikariCP, PostgreSQL, host)?

**Estado**: `CAUSA NO AISLADA`

---

## 2. Hipótesis Candidatas

1. **$H_1$ (HikariCP)**: Agotamiento/colas en pool (active cercano a max / pending > 0) coinciden temporalmente con aumento de P95.
2. **$H_2$ (CPU Backend / Thread Pool Tomcat)**: Saturación de CPU/threads coincide temporalmente sin predominio de esperas DB.
3. **$H_3$ (PostgreSQL / Locks / Waits)**: Aumento de waits/locks/active queries coincide temporalmente con degradación.
4. **$H_4$ (JVM GC)**: Pausas GC coinciden temporalmente con picos de latencia.
5. **$H_5$ (Host Generador k6)**: Saturación del host afectando medición (documentar si backend+k6 en mismo equipo).

---

## 3. Campaña Diagnóstica (Nueva)

- **Niveles**: 10, 50, 250, 1000 VUs (10 = estable histórico; 50 = primera degradación; 250 = intermedio; 1000 = alta)
- **Corridas por nivel**: R1 = warmup, R2 = medición, R3 = medición (sin R4 obligatorio)
- **Duración**: 60s por corrida, `gracefulStop` 30s (mantener comparabilidad)
- **Restauración/condiciones**: usar semilla/configuración coherente con campaña histórica cuando aplique
- **Sincronización**: capturar timestamps para k6/JVM/HikariCP/Postgres/host

---

## 4. Telemetría a Capturar

- **k6**: http_reqs, latencia (p50/p90/p95/p99 si disponible), errores, VUs
- **Backend/JVM (Actuator/Micrometer si disponible)**: process CPU, system CPU, heap used/max, non-heap, live/peak threads, GC count/pause, HTTP metrics
- **HikariCP**: active, idle, pending, max, min idle (si disponible)
- **PostgreSQL**: pg_stat_activity (active/total), pg_locks (granted/not granted), transacciones/sesiones relevantes (vía script reproducible)
- **Host**: CPU/memoria (script PowerShell); documentar co-localización backend+k6

---

## 5. Criterios (Sin Umbrales Arbitrarios)

- **Correlación temporal**: cambio coincide en misma ventana
- **Saturación/agotamiento observable**: colas pendientes, active cercano a max, waits/locks
- **Contraste entre niveles**: patrón se acentúa
- **Aislamiento**: preferir evidencia directa del recurso sobre inferencia
- **Estados**: `DEMOSTRADO` | `INDICIO` | `NO DEMOSTRADO` | `DESCARTADO CON EVIDENCIA` | `CAUSA NO AISLADA`

---

## 6. Metadatos Obligatorios

Registrar por corrida: commit, fecha/hora inicio/fin, VUs/corrida, host, SO, Java version, k6 version, DB, backend URL, configuración relevante.

---

## 7. Estado

`PREREGISTRO ACTUALIZADO PARA CAMPAÑA DIAGNÓSTICA SINCRONIZADA`
