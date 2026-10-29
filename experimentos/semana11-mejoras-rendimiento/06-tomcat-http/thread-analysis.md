# Análisis de Hilos JVM y Diagnóstico Causal — Semana 11

## 1. Objetivo y Resumen

Este documento consolida el análisis de hilos JVM e identidades de solicitudes durante las pruebas de rendimiento con 250 VUs y Pool Hikari = 30.

---

## 2. Hallazgos Empíricos

1. **Estado de Hilos en la JVM**:
   - `jvm.threads.states.blocked`: **0** en todas las corridas (descarte de contención de monitores sincronizados en Java).
   - `jvm.threads.live`: Mantiene un baseline de 24 hilos durante la ejecución del escenario principal de actividades.

2. **Diagnóstico de la Ejecución R8**:
   - R8 no constituyó un fallo de rendimiento en el endpoint `POST /api/activities`.
   - La ejecución finalizó a los 121.64s durante la fase `setup()` de k6 procesando peticiones `POST /api/auth/login`.

3. **Capacidad del Endpoint `POST /api/activities` (R7 y R9)**:
   - Con `setup()` completado, el backend procesó exitosamente **13,901 actividades en R7** (P95 = 152.01 ms) y **13,499 actividades en R9** (P95 = 327.29 ms) con 0 peticiones pendientes en Hikari Pool = 30.

---

## 3. Matriz Causal Consolidada

| Componente / Hipótesis | Estado | Evidencia |
| :--- | :--- | :--- |
| **Setup Incompleto en k6 (R8)** | `DEMOSTRADO` | 0 peticiones a `POST /api/activities` en R8. |
| **Capacidad de `POST /api/activities`** | `DEMOSTRADO` | R7 y R9 procesan >13,400 actividades a >227 req/s durante el escenario objetivo. |
| **HikariCP Pool = 30** | `DEMOSTRADO ESTABLE` | 0 peticiones pendientes (`pending = 0`) a 250 VUs. |
| **Bloqueos Java (`BLOCKED`)** | `EVIDENCIA EN CONTRA` | `jvm.threads.states.blocked = 0` en todas las mediciones. |
| **CPU del Backend** | `EVIDENCIA EN CONTRA DE SATURACIÓN` | Proceso JVM estable (~24–27% uso CPU). |
