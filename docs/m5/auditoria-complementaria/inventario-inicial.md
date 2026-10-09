# Inventario inicial — auditoría complementaria M5

## 1. Baseline

`259fb740abe3651b3925cbf6e93a3399ce905dbe`

Este inventario fue generado desde los archivos versionados presentes en la baseline de inicio de la auditoría complementaria.

Su inclusión en el inventario no implica validación semántica.

## 2. Resumen

Artefactos documentales identificados: **131**.

| Categoría | Cantidad |
|---|---:|
| ADR | 3 |
| Documentación general | 7 |
| Dominio | 9 |
| Experimentos | 79 |
| Integración | 6 |
| M5 | 8 |
| Navegación principal | 1 |
| Semanas | 18 |

## 3. Corpus por categoría

### ADR

- `docs/adr/0001-decision-estilo.md`
- `docs/adr/0002-aislamiento-persistencia.md`
- `docs/adr/0003-integracion-eventos-internos.md`

### Documentación general

- `docs/architecture/comparison-historical-current.md`
- `docs/architecture/current/architecture-current.md`
- `docs/asyncapi/rachapro-events-v1.yaml`
- `docs/ejecucion-local.md`
- `docs/performance/prueba-carga-500k.md`
- `docs/performance/resultados-carga-500k.csv`
- `docs/uso-ia/auditoria-eventos.md`

### Dominio

- `docs/dominio/README.md`
- `docs/dominio/context-map.puml`
- `docs/dominio/evidencia/activities.md`
- `docs/dominio/evidencia/focus.md`
- `docs/dominio/evidencia/progress.md`
- `docs/dominio/evidencia/reminders.md`
- `docs/dominio/justificacion-contextos.md`
- `docs/dominio/responsabilidades-contextos.md`
- `docs/dominio/subdominios.md`

### Experimentos

- `experimentos/EXP-001-linea-base/README.md`
- `experimentos/EXP-001-linea-base/condiciones.md`
- `experimentos/EXP-001-linea-base/resultados/resultado-linea-base.md`
- `experimentos/EXP-002-k6-api-activities/README.md`
- `experimentos/EXP-002-k6-api-activities/condiciones.md`
- `experimentos/EXP-002-k6-api-activities/resultados/raw-post-activities.json`
- `experimentos/EXP-002-k6-api-activities/resultados/resultado-linea-base.md`
- `experimentos/EXP-002-k6-api-activities/resultados/revalidacion-semana6.json`
- `experimentos/EXP-002-k6-api-activities/revalidacion-2026-09-17/condiciones.md`
- `experimentos/EXP-002-k6-api-activities/revalidacion-2026-09-17/resultado.md`
- `experimentos/EXP-002-k6-api-activities/revalidacion-2026-09-17/resultados/corrida-01-raw.json`
- `experimentos/EXP-002-k6-api-activities/revalidacion-2026-09-17/resultados/corrida-01-summary.json`
- `experimentos/EXP-002-k6-api-activities/revalidacion-2026-09-17/resultados/corrida-02-raw.json`
- `experimentos/EXP-002-k6-api-activities/revalidacion-2026-09-17/resultados/corrida-02-summary.json`
- `experimentos/EXP-002-k6-api-activities/revalidacion-2026-09-17/resultados/corrida-03-raw.json`
- `experimentos/EXP-002-k6-api-activities/revalidacion-2026-09-17/resultados/corrida-03-summary.json`
- `experimentos/EXP-002-k6-api-activities/revalidacion-2026-09-17/resultados/corrida-04-raw.json`
- `experimentos/EXP-002-k6-api-activities/revalidacion-2026-09-17/resultados/corrida-04-summary.json`
- `experimentos/EXP-002-k6-api-activities/revalidacion-2026-09-17/resultados/resumen-corridas.csv`
- `experimentos/EXP-003-k6-carga-activities/README.md`
- `experimentos/EXP-003-k6-carga-activities/resultados/k6-10-vus.json`
- `experimentos/EXP-003-k6-carga-activities/resultados/k6-100-vus.json`
- `experimentos/EXP-003-k6-carga-activities/resultados/k6-250-vus.json`
- `experimentos/EXP-003-k6-carga-activities/resultados/k6-50-vus.json`
- `experimentos/EXP-003-k6-carga-activities/resultados/k6-500-vus.json`
- `experimentos/EXP-003-k6-carga-activities/resultados/resumen-exp-003.csv`
- `experimentos/EXP-004-android-bajo-carga/README.md`
- `experimentos/EXP-004-android-bajo-carga/resultados/android-bajo-carga.csv`
- `experimentos/EXP-004-android-bajo-carga/resultados/baseline-android-sin-carga.csv`
- `experimentos/EXP-004-android-bajo-carga/resultados/k6-formal-499vus-600s.json`
- `experimentos/EXP-004-android-bajo-carga/resultados/smoke-k6-2vus.json`
- `experimentos/EXP-004-android-bajo-carga/resultados/solapamiento-formal.csv`
- `experimentos/semana8-diagnostico-activities/README.md`
- `experimentos/semana8-diagnostico-activities/resultados/postgres-connections-during-load.csv`
- `experimentos/semana8-diagnostico-activities/resultados/postgres-wait-events.csv`
- `experimentos/semana8-diagnostico-activities/resultados/timing-http-db-observation.json`
- `experimentos/semana8-diagnostico-activities/resultados/timing-http-pg-waits.json`
- `experimentos/semana8-diagnostico-activities/resultados/timing-http-thread-observation.json`
- `experimentos/semana8-diagnostico-activities/resultados/timing-http.json`
- `experimentos/semana8-paginacion-activities/README.md`
- `experimentos/semana8-paginacion-activities/resultados/formal-control-03.json`
- `experimentos/semana8-paginacion-activities/resultados/formal-control-04.json`
- `experimentos/semana8-paginacion-activities/resultados/formal-control-05.json`
- `experimentos/semana8-paginacion-activities/resultados/formal-paged-01.json`
- `experimentos/semana8-paginacion-activities/resultados/formal-paged-02.json`
- `experimentos/semana8-paginacion-activities/resultados/smoke-control.json`
- `experimentos/semana8-paginacion-activities/resultados/smoke-paged.json`
- `experimentos/semana8-paginacion-activities/resultados/wrapper-smoke-direct.json`
- `experimentos/semana8-post-modular-validation/README.md`
- `experimentos/semana8-post-modular-validation/resultados/reverse-post-control.json`
- `experimentos/semana8-post-modular-validation/resultados/reverse-post-paged.json`
- `experimentos/semana8-post-modular-validation/resultados/reverse-pre-control.json`
- `experimentos/semana8-post-modular-validation/resultados/reverse-pre-paged.json`
- `experimentos/semana8-post-modular-validation/resultados/same-session-post-control-01.json`
- `experimentos/semana8-post-modular-validation/resultados/same-session-post-control-02.json`
- `experimentos/semana8-post-modular-validation/resultados/same-session-post-paged-01.json`
- `experimentos/semana8-post-modular-validation/resultados/same-session-post-paged-02.json`
- `experimentos/semana8-post-modular-validation/resultados/same-session-pre-control-01.json`
- `experimentos/semana8-post-modular-validation/resultados/same-session-pre-control-02.json`
- `experimentos/semana8-post-modular-validation/resultados/same-session-pre-paged-01.json`
- `experimentos/semana8-post-modular-validation/resultados/same-session-pre-paged-02.json`
- `experimentos/semana8-regresion-funcional/README.md`
- `experimentos/spike-01-integracion/README.md`
- `experimentos/spike-01-integracion/resultados.csv`
- `experimentos/spike-02-resiliencia/README.md`
- `experimentos/spike-03-caracterizacion-racha/00-preregistro.md`
- `experimentos/spike-03-caracterizacion-racha/condiciones.md`
- `experimentos/spike-03-caracterizacion-racha/evidencia/run-1/20261007-213425-135/clock-overhead.csv`
- `experimentos/spike-03-caracterizacion-racha/evidencia/run-1/20261007-213425-135/metadata.csv`
- `experimentos/spike-03-caracterizacion-racha/evidencia/run-1/20261007-213425-135/observations.csv`
- `experimentos/spike-03-caracterizacion-racha/evidencia/run-2/20261007-213812-117/clock-overhead.csv`
- `experimentos/spike-03-caracterizacion-racha/evidencia/run-2/20261007-213812-117/metadata.csv`
- `experimentos/spike-03-caracterizacion-racha/evidencia/run-2/20261007-213812-117/observations.csv`
- `experimentos/spike-03-caracterizacion-racha/evidencia/run-3/20261007-214224-649/clock-overhead.csv`
- `experimentos/spike-03-caracterizacion-racha/evidencia/run-3/20261007-214224-649/metadata.csv`
- `experimentos/spike-03-caracterizacion-racha/evidencia/run-3/20261007-214224-649/observations.csv`
- `experimentos/spike-03-caracterizacion-racha/registro-delegacion-ia.md`
- `experimentos/spike-03-caracterizacion-racha/resultados.md`
- `experimentos/spike-03-caracterizacion-racha/veredicto.md`

### Integración

- `docs/integracion/README.md`
- `docs/integracion/aplicabilidad-cqrs-event-sourcing.md`
- `docs/integracion/cobertura-interacciones-sync-async.md`
- `docs/integracion/contrato-api.md`
- `docs/integracion/eventos-candidatos.md`
- `docs/integracion/sincrono-vs-asincrono.md`

### M5

- `docs/m5/auditoria-externa-post-freeze.md`
- `docs/m5/auditoria-final.md`
- `docs/m5/auditoria-semantica-semanas9-10.md`
- `docs/m5/cierre-pista2.md`
- `docs/m5/correcciones-auditoria-final.md`
- `docs/m5/correcciones-post-freeze.md`
- `docs/m5/freeze-final.md`
- `docs/m5/veredicto-semantico-final.md`

### Navegación principal

- `README.md`

### Semanas

- `docs/checkpoint-semana2.md`
- `docs/riesgos-semana2.md`
- `docs/semana-04/README.md`
- `docs/semana-04/evidence-register.md`
- `docs/semana-04/pending-evidence.md`
- `docs/semana8/antes-despues.md`
- `docs/semana8/baseline-disponibilidad-as-is.md`
- `docs/semana8/baseline-pre-modular.md`
- `docs/semana8/c4/README.md`
- `docs/semana8/c4/c4-l2-contenedores.md`
- `docs/semana8/c4/c4-l3-backend-modular.md`
- `docs/semana8/defensa-comite.md`
- `docs/semana8/edav.md`
- `docs/semana8/fronteras-modulares.md`
- `docs/semana8/matriz-trazabilidad.md`
- `docs/semana8/resumen-ejecutivo.md`
- `docs/semana8/riesgos-tradeoffs-costos.md`
- `docs/semana9/09-api-eventos-integracion.md`

## 4. Artefactos protegidos en esta fase

- `docs/adr/0003-integracion-eventos-internos.md`
- `docs/dominio/context-map.puml`

## 5. Uso del inventario

Este archivo fija el universo documental inicial para las siguientes pistas de auditoría.

No clasifica todavía ningún archivo como correcto o incorrecto.

Los hallazgos semánticos deberán referenciar evidencia concreta y no podrán derivarse únicamente de la presencia de un archivo en este listado.
