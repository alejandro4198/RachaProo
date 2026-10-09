# Integración — RachaPro M5

Este directorio reúne contratos y análisis relacionados con la integración
entre capacidades del monolito modular de RachaPro.

Los documentos tienen propósitos distintos y no deben interpretarse como
fuentes equivalentes.

## Contrato síncrono

- `contrato-api.md`

Formaliza la semántica contractual vigente documentada para `ActivityLookup`.

## Evaluación síncrono vs asíncrono

- `sincrono-vs-asincrono.md`

Compara alternativas para la interacción estudiada. Evaluar una alternativa no
implica automáticamente adoptarla.

## Eventos candidatos

- `eventos-candidatos.md`

## CQRS y Event Sourcing

- `aplicabilidad-cqrs-event-sourcing.md`

## UserRegisteredV1

Decisión arquitectónica:

- `../adr/0003-integracion-eventos-internos.md`

Contrato técnico:

- `../asyncapi/rachapro-events-v1.yaml`

Evidencia experimental:

- `../../experimentos/spike-01-integracion/`
- `../../experimentos/spike-02-resiliencia/`

## Evidencia posterior de Progress

- `../../experimentos/spike-03-caracterizacion-racha/`

La existencia de evidencia experimental posterior no constituye por sí sola una
decisión arquitectónica nueva.

## Jerarquía documental

- ADR: decisión arquitectónica y alcance.
- Contrato: semántica pública vigente.
- AsyncAPI: contrato técnico del evento adoptado.
- Análisis: evaluación o comparación.
- Experimento: evidencia bajo condiciones explícitamente registradas.

Este README es un índice y no sustituye los documentos fuente.

<!-- M5:SYNC-ASYNC-COVERAGE:BEGIN -->

## Cobertura transversal de interacciones

Para distinguir qué relaciones cuentan con evaluación síncrona/asíncrona
específica y cuáles no, consultar:

`docs/integracion/cobertura-interacciones-sync-async.md`

La matriz es de cobertura documental y no adopta mecanismos nuevos por sí sola.

<!-- M5:SYNC-ASYNC-COVERAGE:END -->
