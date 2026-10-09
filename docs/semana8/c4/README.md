# C4 — RachaPro Semana 8

<!-- M5:C4-CORTE-TEMPORAL:BEGIN -->

> **CORTE TEMPORAL — AS-IS AL CIERRE DE SEMANA 8**
>
> Este directorio conserva la arquitectura materializada y documentada al
> cierre de Semana 8.
>
> No debe utilizarse aisladamente como representación completa de las
> evoluciones posteriores del sistema.
>
> Para decisiones y contratos posteriores deben consultarse, entre otros:
>
> - `docs/adr/0003-integracion-eventos-internos.md`;
> - `docs/dominio/`;
> - `docs/integracion/`;
> - `docs/asyncapi/rachapro-events-v1.yaml`.
>
> Las relaciones originales de Semana 8 se conservan sin reescritura
> retroactiva por trazabilidad histórica.

<!-- M5:C4-CORTE-TEMPORAL:END -->


Estado:

**AS-IS**

Este directorio contiene la documentación C4 actualizada después de materializar el monolito modular del backend.

## Documentos

- `c4-l2-contenedores.md`
- `c4-l3-backend-modular.md`

## Diagramas Mermaid

- `diagramas/c4-l2-contenedores.mmd`
- `diagramas/c4-l3-backend-modular.mmd`

## Módulos del backend

- Identity
- Activities
- Focus
- Progress
- Reminders
- Shared

## Relaciones intermodulares

- Identity -> Activities mediante `DefaultCategoryProvisioning`
- Focus -> Activities mediante `ActivityLookup`
- Reminders -> Activities mediante `ActivityLookup`

El backend sigue siendo una única aplicación Spring Boot.

Los módulos representan fronteras internas dentro del monolito y no microservicios independientes.
