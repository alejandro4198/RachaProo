# Correcciones post-freeze M5

## Propósito

Este documento registra cambios realizados después del freeze final de M5.

El freeze original permanece como evidencia histórica reproducible del estado
que existía en ese momento.

## Corrección 1 — H-EXT-01

Archivo:

docs/integracion/contrato-api.md

Tipo:

CORRECCIÓN MECÁNICA / DOCUMENTAL.

Problema:

El título mencionaba únicamente Activities → Focus aunque el cuerpo del
documento ya reconocía Focus y Reminders como consumidores actuales de
ActivityLookup.

Acción:

Se actualizó el título a:

Contrato de API intermodular de Activities para Focus y Reminders

Impacto arquitectónico:

NINGUNO.

No cambió:

- código;
- ActivityLookup;
- consumidores;
- semántica;
- mecanismo de integración;
- ownership;
- Context Map;
- ADR-003.

Commit de corrección:

e01cbfb25cfcc854ac37e71b9fa886244a18f88e

## Auditoría externa

Registro:

docs/m5/auditoria-externa-post-freeze.md

Commit:

0a162c1e77fdbe141a17a63cb3bd8be7eab2d278

## Precisiones metodológicas posteriores

Fase 25 se interpreta como auditoría automatizada de regresión y coherencia
documental.

Fase 26 conserva su conclusión histórica para los controles disponibles en ese
momento.

La expresión MECANISMO ACTUAL VALIDADO se interpreta como MECANISMO ACTUAL
VERIFICADO EN EL ESTADO OBSERVADO.

Estas precisiones no alteran retrospectivamente los artefactos congelados.

## Estado de decisiones abiertas

Permanecen abiertas:

- Identity en Context Map;
- Achievement ownership;
- eventual renombre de ActivityLookup;
- política definitiva de .idea.

CQRS y Event Sourcing permanecen no adoptados automáticamente.

Retry, replay, Outbox y broker externo continúan sin requisito explícito
demostrado en las fuentes revisadas.

## Regla

Todo hallazgo posterior al freeze debe incorporarse como evolución posterior y
no como reescritura del estado histórico congelado.
