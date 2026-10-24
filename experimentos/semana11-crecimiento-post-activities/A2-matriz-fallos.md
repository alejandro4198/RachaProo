# A2 — Matriz de fallos

## 1. Propósito

Este documento consolida el entregable A2 del Módulo 6 de RachaPro.

Su propósito es registrar los fallos considerados por el equipo distinguiendo explícitamente entre:

- fallos observados experimentalmente;
- fallos hipotéticos no ejecutados.

La matriz no atribuye causas que no hayan sido demostradas y no convierte comportamientos esperados en resultados observados.

La evidencia histórica detallada permanece en:

`experimentos/semana11-crecimiento-post-activities/matrices-fallos-resiliencia.md`

---

## 2. Fuentes utilizadas

### Arquitectura y sistema

- aplicación Android de RachaPro;
- backend Kotlin + Spring Boot;
- PostgreSQL;
- documentación C4 disponible en el repositorio;
- contratos e integración documentados en los ADR del proyecto.

### Evidencia experimental

- `experimentos/semana11-crecimiento-post-activities/matrices-fallos-resiliencia.md`
- evidencia de las repeticiones R1, R2 y R3;
- Logcat privado conservado localmente;
- comprobaciones de persistencia realizadas sobre PostgreSQL.

La evidencia privada no se publica cuando contiene información sensible.

---

## 3. Criterio de clasificación

Cada escenario se clasifica como:

### OBSERVADO

Existe una ejecución experimental documentada y evidencia del comportamiento producido.

### HIPOTÉTICO

El escenario ha sido considerado por el equipo, pero no existe una ejecución experimental que permita presentarlo como comportamiento observado.

No se incorporan nuevos fallos hipotéticos únicamente para completar la matriz.

---

## 4. Trazabilidad arquitectónica

El escenario F-01 involucra los siguientes elementos del sistema:

```text
Android
   |
   | HTTP/JSON
   v
Backend Spring Boot
   |
   v
PostgreSQL
```
