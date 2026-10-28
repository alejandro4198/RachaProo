# Prerregistro — Diagnóstico de Concurrencia y Aislamiento de Cuello de Botella

## 1. Identificación y Pregunta Experimental

- **Proyecto**: RachaPro
- **Módulo**: Semana 11 — Mejoras de Rendimiento y Resiliencia
- **Operación Evaluada**: `POST /api/activities` (Creación de Actividades)
- **Pregunta Experimental**:
  > ¿Cuál es la causa de la degradación observada en la latencia P95 a partir de 50 VUs concurrentes en la operación `POST /api/activities`? ¿Qué evidencia correlacionada (CPU backend/JVM, HikariCP, PostgreSQL, locks/waits, Tomcat/thread pool, host k6) permite aislarla?

**Estado actual conocido**: `CAUSA NO AISLADA`. No se asume causa raíz sin evidencia sincronizada.

---

## 2. Hipótesis Candidatas

1. **$H_1$ (Saturación de Conexiones HikariCP)**: Ocurren colas de espera sostenidas para obtener conexiones a PostgreSQL (conexiones activas cercanas a máximo / conexiones pendientes) mientras otros recursos no muestran saturación equivalente.
2. **$H_2$ (Saturación de CPU Backend / Thread Pool)**: El procesador del host backend o los hilos de Tomcat/Spring MVC alcanzan saturación con crecimiento sostenido paralelo a latencia P95, sin acumulación predominante de conexiones pendientes en DB.
3. **$H_3$ (Contención en Base de Datos / Locks)**: Las transacciones de escritura sobre `activities` generan bloqueo/waits en PostgreSQL correlacionados temporalmente con el aumento de latencia.
4. **$H_4$ (Pausas de Garbage Collector JVM)**: Las pausas GC (Stop-The-World) introducen picos de latencia correlacionados temporalmente con eventos observables.
5. **$H_5$ (Saturación del Host Generador k6)**: El host generador satura CPU/memoria/red local afectando la medición.

---

## 3. Variables Experimentales

- **Variable Independiente**: Nivel de usuarios virtuales concurrentes ($VU$).
- **Variables Dependientes Medidas**:
  1. **Latencia P95** (ms)
  2. **Latencia P99** (ms, si existe en evidencia; de lo contrario `P99 NO DISPONIBLE EN LA EVIDENCIA HISTÓRICA`)
  3. **Throughput** ($T = \text{solicitudes completadas} / \text{segundo}$) — calculado solo desde `http_reqs` con ventana temporal observable; R1 = warmup, usar R2-R4 para representativo.
  4. **Jitter Post-Hoc** ($J = \text{desviación estándar de P95 entre R2,R3,R4}$) — **ANÁLISIS SECUNDARIO POST HOC**, no métrica original.
  5. **Tasa de Éxito Funcional** (%)
  6. **Tasa de Errores HTTP 4xx/5xx**

---

## 4. Diseño Experimental

- **Herramienta de Carga**: k6 (`constant-vus`).
- **Duración por Corrida**: 60 segundos con `gracefulStop = 30s` (según diseño histórico).
- **Repeticiones**: R1 (calentamiento), R2-R4 (mediciones). Solo R2-R4 para valor representativo.
- **Telemetría obligatoria (nueva campaña)**: timestamps sincronizados para k6, backend/JVM (CPU/memoria/heap/threads/GC si disponible), HikariCP (active/idle/pending/max), PostgreSQL (conexiones activas/sesiones/waits/locks/transacciones), host k6.

---

## 5. Criterios para Atribuir Causalidad

Los criterios son **observables y correlacionados temporalmente**, **sin umbrales porcentuales arbitrarios**:

- **Correlación temporal**: el aumento de latencia coincide en el tiempo con el cambio en la métrica candidata (misma ventana de corrida).
- **Saturación/agotamiento**: evidencia de colas pendientes, conexiones activas cercanas al máximo configurado, o comportamiento de thread pool bajo presión.
- **Contraste entre niveles**: el patrón se acentúa al aumentar VUs (o cambia abruptamente) coherente con saturación.
- **Comportamiento de throughput/latencia**: meseta de throughput con crecimiento de latencia puede indicar encolamiento; requiere soporte con telemetría del recurso implicado.
- **HikariCP**: se considera evidencia fuerte solo si el crecimiento de latencia coincide temporalmente con conexiones activas cercanas al máximo y/o conexiones pendientes, mientras otros recursos no muestran saturación equivalente.
- **CPU Backend**: se considera evidencia fuerte solo si hay crecimiento sostenido de utilización con correlación temporal y sin evidencia predominante de agotamiento de conexiones/esperas en DB.
- **PostgreSQL/Locks**: requiere evidencia de waits/locks o contención correlacionada temporalmente.
- **Host k6**: debe evaluarse si hay saturación del generador que afecte la medición.

La atribución es por **correlación temporal + evidencia directa del recurso**, no por exclusión genérica.

---

## 6. Estado del Prerregistro

`PREREGISTRO CORREGIDO — SIN UMBRALES PORCENTUALES ARBITRARIOS — CAUSA NO AISLADA HASTA EVIDENCIA SINCRONIZADA`
