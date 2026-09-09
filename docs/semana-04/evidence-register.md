# Registro de evidencias - Semana 4

Este archivo registra las evidencias disponibles del proyecto RachaPro y distingue entre material histórico, implementación actual y pruebas todavía pendientes.

| ID | Evidencia | Tipo              | Estado | Qué demuestra |
|---|---|-------------------|---|---|
| EV-01 | `ProyectoAS.docx` | Historica         | Disponible | Contexto inicial del proyecto RachaPro para la asignatura Arquitectura de Software y adopción del proyecto desarrollado previamente en Ingeniería de Software. |
| EV-02 | `RachaPro_ Gestor Inteligente de Productividad Académica(3).pdf` | Histórica         | Disponible | Requerimientos, historias de usuario, trazabilidad, diagramas UML, arquitectura propuesta y plan de pruebas elaborado previamente. |
| EV-03 | `Prototipo móvil RachaPro.make` | Histórica         | Disponible | Prototipo visual diseñado para RachaPro y referencia de las pantallas planteadas durante el desarrollo previo. |
| EV-04 | Repositorio GitHub de RachaPro | Actual            | Disponible | Existencia del código fuente correspondiente a la implementación actual del proyecto. |
| EV-05 | MVP Android | Actual            | Disponible, pendiente de documentar | Existencia de una implementación funcional del sistema. Falta registrar formalmente la versión y el commit evaluado. |
| EV-06 | Arquitectura implementada actualmente | Actual            | Pendiente de documentar | Permitirá establecer cómo está organizado realmente el sistema a partir del código fuente actual. |
| EV-07 | Comparación entre arquitectura previa y actual | Análisis          | Pendiente | Permitirá identificar diferencias entre la arquitectura propuesta anteriormente y la implementación actual, sin asumir las causas de esos cambios. |
| EV-08 | RNF01 - Usabilidad | Prueba            | Pendiente | Permitirá comprobar las métricas de usabilidad definidas previamente para el módulo de actividades. |
| EV-09 | RNF02 - Rendimiento | Prueba            | Pendiente | Permitirá comprobar los tiempos de respuesta y comportamiento con la carga definida previamente. |
| EV-10 | RNF03 - Seguridad | Prueba / revisión | Pendiente | Permitirá verificar autenticación, protección de credenciales y control de acceso según el requerimiento definido. |
| EV-11 | Compilación del proyecto | Técnica           | Pendiente de registrar | Permitirá demostrar que una versión específica del proyecto puede compilarse correctamente bajo condiciones documentadas. |
| EV-12 | Ejecución en dispositivo Android | Técnica           | Pendiente de registrar | Permitirá demostrar que una versión específica del MVP se instala y ejecuta correctamente en los dispositivos evaluados. |

---

## Actualización posterior de evidencias

Esta sección fue añadida después del cierre original de Semana 4 para evitar que los estados históricos `Pendiente` se interpreten como pendientes vigentes.

La tabla original anterior se conserva sin modificación porque representa correctamente el conocimiento disponible en ese momento.

| ID | Estado en Semana 4 | Estado posterior | Evidencia posterior |
|---|---|---|---|
| EV-05 | Disponible, pendiente de documentar | **Resuelto posteriormente** | La implementación Android, backend y persistencia quedaron documentadas en los C4 y en los checkpoints técnicos posteriores. |
| EV-06 | Pendiente de documentar | **Resuelto posteriormente** | `dossier/05-c4-contexto.md`, `dossier/06-c4-contenedores.md` y `dossier/07-c4-componentes.md`. |
| EV-07 | Pendiente | **Resuelto como análisis histórico; posteriormente complementado** | `docs/architecture/comparison-historical-current.md` se conserva como comparación histórica. El AS-IS vigente está documentado en los C4 posteriores. |
| EV-08 | Pendiente | **Pendiente vigente** | Los escenarios de usabilidad fueron formalizados, pero no se registra todavía la ejecución completa de la prueba con usuarios. |
| EV-09 | Pendiente | **Evidencia experimental posterior disponible** | EXP-001, EXP-002 y EXP-003 aportan mediciones bajo alcances y condiciones diferentes. No se presentan como una validación idéntica de todas las condiciones históricas de RNF02. |
| EV-10 | Pendiente | **Parcialmente resuelto** | Se verificaron Spring Security, JWT, autenticación y protección de endpoints. Esto no equivale por sí solo a demostrar todos los escenarios de seguridad o aislamiento multiusuario. |
| EV-11 | Pendiente de registrar | **Resuelto posteriormente** | `docs/checkpoint-semana2.md`, `docs/ejecucion-local.md` y ejecuciones posteriores del proyecto. |
| EV-12 | Pendiente de registrar | **Resuelto posteriormente bajo el entorno documentado** | La aplicación fue ejecutada y validada en dispositivo durante checkpoints posteriores. No se generaliza a todos los dispositivos posibles. |

### Regla de interpretación

Un estado marcado como `Pendiente` en la tabla original corresponde al corte temporal de Semana 4.

Para determinar el estado vigente debe consultarse esta actualización y la evidencia posterior enlazada.
