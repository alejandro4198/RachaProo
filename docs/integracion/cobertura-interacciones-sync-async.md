# Cobertura de interacciones síncronas y asíncronas

## 1. Propósito

Este documento responde al hallazgo I-01 de la auditoría final.

Su objetivo no es elegir un mecanismo nuevo para cada relación, sino hacer
explícito:

- qué interacciones relevantes aparecen actualmente en el corpus M5;
- cuáles cuentan con una comparación o evaluación específica;
- dónde se encuentra dicha evaluación;
- cuáles continúan sin una decisión adicional.

La existencia de una relación en esta matriz no implica que deba cambiarse su
mecanismo actual.

## 2. Matriz de cobertura

| Interacción | Evidencia / mecanismo observado | Evaluación sync/async | Estado de cobertura |
|---|---|---|---|
| Focus → Activities | `ActivityLookup` como contrato síncrono observado | Comparación explícita en `docs/integracion/sincrono-vs-asincrono.md` | EVALUADA |
| Reminders → Activities | `ActivityLookup` como contrato síncrono observado | No se identificó una comparación completa independiente equivalente a la de Focus; comparte semántica contractual con Focus | COBERTURA PARCIAL / SIN CAMBIO DECIDIDO |
| Identity → Activities | `UserRegisteredV1` como evento interno; Activities consume después del commit mediante listener | Evaluada mediante SPIKE-01 y decidida específicamente por ADR-003 | EVALUADA PARA ESE FLUJO |
| Progress → Activities | Relación de consumo/referencia documentada en el Context Map | No se identificó en el corpus revisado una comparación sync/async dedicada | NO EVALUADA ESPECÍFICAMENTE |
| Progress → Focus | Relación de consumo/referencia documentada en el Context Map | No se identificó en el corpus revisado una comparación sync/async dedicada | NO EVALUADA ESPECÍFICAMENTE |

## 3. Interpretación

`EVALUADA`

significa que existe un artefacto específico que analiza alternativas o una
cadena experimental/ADR aplicable al flujo indicado.

`COBERTURA PARCIAL`

significa que existe evidencia del mecanismo observado, pero no una comparación
completa independiente equivalente a la desarrollada para Focus.

`NO EVALUADA ESPECÍFICAMENTE`

no significa:

- que la relación esté mal;
- que deba migrarse a eventos;
- que deba permanecer síncrona;
- que exista un incumplimiento arquitectónico demostrado.

Significa únicamente que la auditoría no identificó una comparación dedicada de
alternativas síncrona/asíncrona para esa interacción.

## 4. Decisiones que este documento no toma

Esta matriz no decide:

- incorporar Identity al Context Map;
- modificar `ActivityLookup`;
- convertir Reminders a eventos;
- convertir Progress a eventos;
- adoptar broker externo;
- adoptar retry, replay u Outbox;
- adoptar CQRS;
- adoptar Event Sourcing.

Su función es hacer visible la cobertura documental disponible y los huecos de
evaluación sin resolverlos automáticamente.
