# Cierre de Pista 2 — decisiones y validaciones M5

## 1. Propósito

Este documento consolida el estado de las fases 11 a 24 revisadas después de las correcciones de Pista 1.

El objetivo es separar:

- evidencia observable;
- análisis;
- decisiones del equipo;
- decisiones pendientes;
- alternativas no justificadas por la evidencia disponible.

Este documento no convierte evidencia en decisión arquitectónica.

## 2. Regla metodológica

HECHO / EVIDENCIA:
puede observarse o trazarse.

ANÁLISIS:
permite comparar alternativas.

DECISIÓN:
requiere cierre explícito del equipo.

AUSENCIA DE JUSTIFICACIÓN:
no equivale a prohibición futura.

Una limitación experimental tampoco implica automáticamente un requisito incumplido ni un cambio arquitectónico obligatorio.

## 3. Estado consolidado

| Fase | Tema | Estado |
|---:|---|---|
| 11 | Identity en Context Map | DECISIÓN PENDIENTE |
| 12 | Achievement ownership | NO DECIDIDO |
| 13 | Nombre ActivityLookup | SIN CAMBIO JUSTIFICADO |
| 14 | Focus → Activities | MECANISMO ACTUAL VALIDADO |
| 15 | Reminders → Activities | MECANISMO ACTUAL VALIDADO |
| 16 | CQRS | NO ADOPTADO / DECISIÓN NO DETERMINADA |
| 17 | Event Sourcing | NO ADOPTADO |
| 18 | SPIKE-02 resiliencia | EVIDENCIA POSTERIOR, NO OBLIGA CAMBIO |
| 19 | Retry | NO JUSTIFICADO ACTUALMENTE |
| 20 | Replay | NO JUSTIFICADO ACTUALMENTE |
| 21 | Outbox / entrega durable | NO JUSTIFICADO ACTUALMENTE |
| 22 | Broker externo | NO JUSTIFICADO ACTUALMENTE |
| 23 | Política .idea | DECISIÓN DEL EQUIPO / SIN CAMBIO ACADÉMICO |
| 24 | Coherencia transversal | VALIDADA PARA PRE-AUDITORÍA |

## 4. Identity

Estado: DECISIÓN PENDIENTE.

La convención del Context Map está confirmada:

A --> B significa que A consume o referencia información de B.

La evidencia permite evaluar Identity con los criterios usados para las demás fronteras.

Sigue pendiente definir el alcance del artefacto:

- todos los bounded contexts vigentes;
- o solo los cerrados dentro del alcance original de M5.

No se modifica el Context Map hasta que exista una decisión explícita.

## 5. Achievement

Estado: Achievement ownership = NO DECIDIDO.

Achievement sigue siendo una capacidad observable con reglas propias.

La evidencia actual no determina automáticamente:

- pertenencia a Progress;
- pertenencia a Activities;
- pertenencia a Focus;
- bounded context independiente.

## 6. ActivityLookup

La semántica documentada indica que la Activity:

- existe;
- pertenece al usuario;
- no está eliminada.

El nombre actual puede resultar más amplio que la semántica observada, pero renombrarlo sería un cambio técnico y contractual.

Por tanto, no se realiza un renombre automático.

## 7. Focus y Reminders hacia Activities

Focus y Reminders consumen ActivityLookup.

Esto documenta el mecanismo actual observado.

No implica automáticamente una obligación de migrar a eventos, REST, broker u otro mecanismo.

## 8. CQRS

SPIKE-03 amplía la evidencia sobre operaciones de Progress.

No implica adopción automática de CQRS.

Estado: NO ADOPTADO / DECISIÓN NO DETERMINADA.

## 9. Event Sourcing

No se encontró evidencia suficiente para adoptar Event Sourcing.

Estado: NO ADOPTADO.

Esto no constituye una prohibición futura.

## 10. SPIKE-02 y resiliencia

SPIKE-02 aporta evidencia posterior sobre comportamiento ante fallos.

Sus resultados deben limitarse a las condiciones observadas.

Una limitación demostrada no equivale automáticamente a requisito incumplido ni a cambio arquitectónico obligatorio.

## 11. Retry

En las fuentes revisadas no se encontró un requisito explícito que obligue a incorporar retry automático al flujo estudiado.

Estado: NO JUSTIFICADO ACTUALMENTE.

## 12. Replay

En las fuentes revisadas no se encontró un requisito explícito que obligue a incorporar replay.

Estado: NO JUSTIFICADO ACTUALMENTE.

## 13. Outbox y entrega durable

En las fuentes revisadas no se encontró un requisito explícito que obligue a introducir Outbox o entrega durable.

Estado: NO JUSTIFICADO ACTUALMENTE.

## 14. Broker externo

La evidencia revisada no demuestra necesidad actual de introducir un broker externo.

Estado: NO JUSTIFICADO ACTUALMENTE.

## 15. Política .idea

Los cambios del IDE no forman parte de las correcciones académicas M5.

La política definitiva de versionado de .idea pertenece al equipo.

Durante estas correcciones no se modifican ni committean archivos .idea.

## 16. Coherencia transversal pre-auditoría

Se verificó:

- código sin cambios;
- ADR-003 sin reescritura;
- Identity sin cierre automático;
- Achievement abierto;
- UserRegisteredV1 trazable;
- ActivityLookup trazable;
- CQRS y Event Sourcing sin adopción automática;
- working tree limpio.

## 17. Pendientes humanos

Requieren decisión explícita:

1. alcance de Identity en el Context Map;
2. ownership de Achievement;
3. eventual renombre de ActivityLookup;
4. política definitiva de .idea.

## 18. Próximo paso

El siguiente paso es ejecutar una auditoría profunda desde cero sobre el repositorio actual.

La nueva auditoría deberá verificar el estado real sin reutilizar automáticamente los veredictos anteriores.
