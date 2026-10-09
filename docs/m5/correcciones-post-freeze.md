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

## Auditoría semántica posterior — H-SEM

Después de la auditoría externa post-freeze se realizó una lectura semántica
AS-01–S15 sobre fuentes primarias, experimentos y temporalidad Git.

Paquete de evidencia:

`docs/m5/auditoria-semantica-semanas9-10.md`

Veredicto:

`docs/m5/veredicto-semantico-final.md`

### H-SEM-01 — alcance semántico de ActivityLookup

Estado:

`CORREGIDO`

Aunque el título del contrato ya mencionaba Focus y Reminders, parte sustancial
del cuerpo describía exclusivamente el flujo de Focus.

Se añadió una precisión que distingue:

- semántica compartida del contrato;
- flujo específico de Focus;
- uso del mismo contrato por Reminders cuando existe `activityId`.

No se modificó código ni el mecanismo de integración.

Commit:

739fa7a836546852badfaa833f3cfff9ea8c2c0d

### H-SEM-02 — fuerza de las afirmaciones sobre resiliencia

Estado:

`ACLARADO POSTERIORMENTE`

Las expresiones `no se requiere` utilizadas en la revisión posterior de ADR-003
deben interpretarse de acuerdo con la evidencia que las acompaña:

`no se encontró en las fuentes revisadas un requisito explícito que obligue a
incorporar la capacidad evaluada`.

Esta precisión evita convertir ausencia de requisito encontrado en una
afirmación universal.

ADR-003 permanece sin modificación.

### H-SEM-03 — evidencia versus veredicto

Estado:

`CORREGIDO`

La extracción automática de la auditoría de Semanas 9 y 10 se registra como paquete de evidencia.

El juicio semántico se registra separadamente en:

`docs/m5/veredicto-semantico-final.md`

Esto evita atribuir a la extracción automatizada un alcance metodológico que no
tiene por sí sola.

Commit del paquete y veredicto:

4dc0f903f8758ce94900042d1b587651794ed7d6

### Decisiones no cerradas

Esta revisión no decide:

- Identity en Context Map;
- Achievement ownership;
- renombre de ActivityLookup;
- política de `.idea`;
- adopción futura de CQRS;
- Event Sourcing;
- retry;
- replay;
- Outbox;
- broker externo.
