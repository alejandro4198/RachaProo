# C4 — RachaPro Semana 8

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