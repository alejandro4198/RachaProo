# RachaPro

## 1. Identificación del proyecto

| Campo | Información |
|---|---|
| Proyecto | RachaPro — Gestor de Productividad Académica |
| Integrante | Alejandro Villamizar Rodríguez |
| Curso | Arquitectura de Software |
| Estado académico documentado | Semanas 1–8 cerradas; documentación posterior de Semana 9, Semana 10 y M5 incorporada al repositorio |
| Estado de Semana 8 | Cerrada y validada en `semana8-final-validado` |
| Repositorio | https://github.com/alejandro4198/RachaProo.git |

RachaPro es una aplicación móvil orientada a productividad académica que integra gestión de actividades, subtareas, categorías, recordatorios, sesiones Pomodoro, progreso, rachas y logros.

El repositorio contiene tanto la implementación actual como documentación histórica conservada para mantener trazabilidad sobre la evolución del sistema.

---

## 2. Estado actual de la arquitectura

La arquitectura AS-IS vigente documentada está compuesta por:

    Aplicación Android
        |
        | HTTP/JSON + JWT
        v
    API backend Spring Boot
        |
        | Spring Data JPA
        v
    PostgreSQL

La aplicación Android mantiene además componentes locales:

- Room / SQLite;
- DataStore;
- servicios Android para alarmas y notificaciones.

El papel exacto de cada componente y las relaciones verificadas se encuentran documentados en las vistas C4.

La arquitectura seleccionada durante Semana 7 y materializada durante Semana 8 es un:

**Monolito modular**

con los módulos implementados:

    Identity
    Activities
    Focus
    Progress
    Reminders

Esta arquitectura se encuentra materializada en el backend. Sus fronteras se protegen mediante fitness functions con ArchUnit y su representación C4 post-refactor se mantiene en `docs/semana8/c4/`.

---

## 3. Navegación por semanas

| Semana | Trabajo principal | Evidencia |
|---|---|---|
| Semana 1 | Recuperación de contexto, documentación histórica y antecedentes del proyecto. | `docs/sources/historical/`, `dossier/01-contexto-sistema.md` |
| Semana 2 | Stakeholders, drivers, riesgos, checkpoint técnico y ejecución local. | `dossier/02-stakeholders-drivers.md`, `docs/riesgos-semana2.md`, `docs/checkpoint-semana2.md`, `docs/ejecucion-local.md` |
| Semana 3 | Priorización y formalización de atributos de calidad. | `dossier/03-atributos-calidad.md` |
| Semana 4 | Escenarios de calidad y experimentos ejecutables. | `dossier/04-escenarios-calidad.md`, `experimentos/EXP-001-linea-base/`, `experimentos/EXP-002-k6-api-activities/` |
| Semana 5 | Arquitectura C4 Nivel 1 y Nivel 2. | `dossier/05-c4-contexto.md`, `dossier/06-c4-contenedores.md` |
| Semana 6 | Arquitectura C4 Nivel 3 y verificación de componentes. | `dossier/07-c4-componentes.md` |
| Semana 7 | Comparación de estilos, decisión arquitectónica, mapa modular y crítica de IA. | `dossier/08-decision-estilo-arquitectonico.md` |
| Semana 8 | Materialización del monolito modular, ADR, ArchUnit, C4 post-refactor, rendimiento, regresión funcional y hardening. | `docs/semana8/`, `docs/adr/`, `experimentos/semana8-regresion-funcional/` |
| Semana 9 | Evaluación de integración mediante contratos síncronos y evento interno; preregistro y ejecución de SPIKE-01. | `docs/semana9/`, `experimentos/spike-01-integracion/` |
| Trabajo posterior / M5 | Modelado de dominio, Context Map, contrato API, AsyncAPI, auditoría de eventos, CQRS / Event Sourcing y experimentos posteriores. | `docs/dominio/`, `docs/integracion/`, `docs/asyncapi/`, `experimentos/spike-02-resiliencia/`, `experimentos/spike-03-caracterizacion-racha/`, `docs/adr/0003-integracion-eventos-internos.md` |

Los documentos de semanas anteriores pueden contener estados o pendientes que eran válidos en el momento en que fueron escritos. Cuando existe evidencia posterior, debe interpretarse junto con los documentos más recientes y no como una descripción del estado vigente.

---

## 4. Arquitectura vigente — referencias canónicas

Para conocer el estado actual del sistema se debe consultar, en este orden:

1. `dossier/05-c4-contexto.md` — C4 Nivel 1 y contexto del sistema.
2. `docs/semana8/c4/c4-l2-contenedores.md` — C4 Nivel 2 AS-IS post-refactor.
3. `docs/semana8/c4/c4-l3-backend-modular.md` — C4 Nivel 3 AS-IS del backend modular.
4. `docs/adr/0001-decision-estilo.md` — decisión de monolito modular y estado de materialización.
5. `docs/adr/0002-aislamiento-persistencia.md` — aislamiento de persistencia y contratos intermodulares.
6. `docs/adr/0003-integracion-eventos-internos.md` — decisión vigente para el flujo post-registro Identity → Activities mediante `UserRegisteredV1`.
7. `docs/dominio/` — modelado de subdominios, bounded contexts, responsabilidades, ownership y Context Map correspondiente a M5.
8. `docs/integracion/` — contratos y análisis posteriores de integración.
9. `docs/asyncapi/rachapro-events-v1.yaml` — contrato técnico vigente del evento `UserRegisteredV1`.

Para evidencia experimental asociada a estas decisiones y análisis posteriores, consultar además:

- `experimentos/spike-01-integracion/`
- `experimentos/spike-02-resiliencia/`
- `experimentos/spike-03-caracterizacion-racha/`

Los siguientes documentos se conservan como trazabilidad histórica y **no deben utilizarse por sí solos como representación del AS-IS vigente**:
- `dossier/06-c4-contenedores.md` — baseline anterior a la materialización de Semana 8.
- `dossier/07-c4-componentes.md` — C4 Nivel 3 histórico de Semana 6.
- `dossier/08-decision-estilo-arquitectonico.md` — decisión TO-BE previa a su materialización.

- `docs/architecture/current/architecture-current.md`
- `docs/architecture/comparison-historical-current.md`

---

## 4.1 Evolución posterior a Semana 8 — Semana 9, Semana 10 y M5

La documentación de Semana 8 conserva el estado arquitectónico materializado en ese corte temporal.

Para reconstruir el estado posterior y las decisiones tomadas después de Semana 8, deben consultarse también los siguientes artefactos:

- `docs/adr/0003-integracion-eventos-internos.md` — decisión arquitectónica posterior sobre `UserRegisteredV1` para el flujo Identity → Activities.
- `docs/dominio/` — análisis de subdominios, bounded contexts, responsabilidades, ownership y Context Map de M5.
- `docs/integracion/` — contratos, evaluación síncrono/asíncrono, eventos candidatos y análisis de aplicabilidad de CQRS / Event Sourcing.
- `docs/asyncapi/rachapro-events-v1.yaml` — formalización técnica del contrato vigente de `UserRegisteredV1`.
- `experimentos/spike-01-integracion/` — evidencia experimental de la integración mediante evento interno.
- `experimentos/spike-02-resiliencia/` — evidencia posterior sobre comportamiento ante fallo del consumidor.
- `experimentos/spike-03-caracterizacion-racha/` — caracterización experimental posterior del cálculo de rachas en Progress.

Los C4 de Semana 8 deben interpretarse como representación AS-IS de ese corte temporal y no como una sustitución automática de decisiones posteriores.

La documentación posterior no reescribe retrospectivamente los artefactos históricos; los complementa con nuevas decisiones, contratos, experimentos y análisis.

Las decisiones todavía marcadas como `PENDIENTE`, `NO DECIDIDO` o equivalentes permanecen abiertas hasta que el equipo las cierre explícitamente.

## 5. Dossier principal

| Documento | Propósito |
|---|---|
| `01-contexto-sistema.md` | Contexto consolidado del sistema |
| `02-stakeholders-drivers.md` | Stakeholders, restricciones y drivers |
| `03-atributos-calidad.md` | Priorización y escenarios de atributos de calidad |
| `04-escenarios-calidad.md` | Escenarios de calidad |
| `05-c4-contexto.md` | C4 Nivel 1 |
| `06-c4-contenedores.md` | C4 Nivel 2 |
| `07-c4-componentes.md` | C4 Nivel 3 |
| `08-decision-estilo-arquitectonico.md` | Decisión de estilo y arquitectura objetivo |

Documentos anteriores como `01-contexto-y-drivers.md`, `02-escenarios-de-calidad.md` y `04-evidencia-ejecutable.md` se conservan por trazabilidad histórica cuando corresponda.

---

## 6. Evidencia experimental

<!-- M5:EVIDENCIA-EXPERIMENTAL:BEGIN -->

> **Alcance de esta sección**
>
> Los experimentos EXP-001, EXP-002 y EXP-003 conservados a continuación
> corresponden a la evidencia experimental documentada en el corte temporal
> original de esta sección y se mantienen por trazabilidad histórica.
>
> La evidencia experimental posterior del proyecto incluye además:
>
> - `experimentos/spike-01-integracion/` — evaluación de la integración interna mediante `UserRegisteredV1`;
> - `experimentos/spike-02-resiliencia/` — caracterización posterior del comportamiento ante fallo del consumidor;
> - `experimentos/spike-03-caracterizacion-racha/` — caracterización del cálculo de rachas en Progress.
>
> La existencia de estos experimentos posteriores no implica por sí sola una
> decisión arquitectónica nueva. Sus resultados deben interpretarse junto con
> sus prerregistros, limitaciones, veredictos y ADR relacionados.

<!-- M5:EVIDENCIA-EXPERIMENTAL:END -->

### EXP-001 — Línea base Android

Experimento histórico orientado a medir el flujo de carga de actividades en la aplicación Android.

Ruta:

`experimentos/EXP-001-linea-base/`

Sus resultados deben interpretarse dentro de las condiciones registradas y no como una medición universal del sistema.

### EXP-002 — API de actividades

Experimento k6 sobre operaciones de creación de actividades en el backend.

Ruta:

`experimentos/EXP-002-k6-api-activities/`

Evalúa solicitudes consecutivas bajo las condiciones documentadas y no representa una prueba Android end-to-end ni un escenario independiente de concurrencia.

### EXP-003 — Carga concurrente

Experimento k6 para observar:

`GET /api/activities`

con un dataset sintético de:

- 500 usuarios;
- 1.000 actividades por usuario;
- 500.000 actividades totales.

Escenarios:

- 10 VUs;
- 50 VUs;
- 100 VUs;
- 250 VUs;
- 500 VUs.

Ruta:

`experimentos/EXP-003-k6-carga-activities/`

En los escenarios ejecutados se obtuvo corrección funcional completa de los GET evaluados. Los resultados no implican escalabilidad ilimitada ni representan condiciones de producción.

---

## 7. Implementación técnica actual

### Android

- Kotlin
- Jetpack Compose
- Navigation Compose
- ViewModels
- Coroutines / Flow
- Retrofit
- OkHttp
- Room
- DataStore
- AlarmManager
- BroadcastReceiver

### Backend

- Kotlin
- Spring Boot
- Spring Security
- JWT
- Spring Data JPA
- Actuator

### Persistencia central

- PostgreSQL 17
- Docker para infraestructura local reproducible

---

## 8. Trazabilidad arquitectónica

La evolución del repositorio mantiene separación entre:

- evidencia histórica;
- hechos verificados;
- decisiones actuales del equipo;
- inferencias de auditoría;
- arquitectura AS-IS;
- arquitectura TO-BE;
- limitaciones experimentales;
- decisiones todavía pendientes.

No debe interpretarse una limitación documentada en un experimento como una falla del sistema salvo que exista evidencia que la demuestre.

Tampoco debe interpretarse un documento histórico como descripción automática del estado actual.

---

## 9. Estado al cierre de Semana 8

Semana 8 materializó la decisión arquitectónica tomada durante Semana 7.

El backend mantiene una única aplicación Spring Boot y una única unidad de despliegue, organizada como monolito modular con las capacidades:

- Identity;
- Activities;
- Focus;
- Progress;
- Reminders.

Las interacciones que atraviesan fronteras utilizan contratos explícitos como `ActivityLookup` y `DefaultCategoryProvisioning`.

Las fronteras arquitectónicas se protegen mediante fitness functions con ArchUnit.

La validación de Semana 8 incluye:

- compilación y pruebas aplicables del backend;
- verificación arquitectónica con ArchUnit;
- C4 post-refactor;
- experimentos de rendimiento PRE/POST;
- regresión funcional Android;
- corrección de RF-01 y RF-03;
- hardening de sincronización Room mediante `@Upsert`, transacción y marcado lógico;
- eliminación del polling remoto periódico para el cambio visual PENDING/OVERDUE.

La referencia técnica congelada del cierre es:

`semana8-final-validado`

Los documentos anteriores continúan en el repositorio como trazabilidad histórica y deben interpretarse de acuerdo con la fecha y baseline que representan.

## Auditoría externa post-freeze

Después del freeze final se realizó una revisión semántica independiente que
detectó hallazgos documentales y metodológicos posteriores.

Fuentes vigentes de esta evolución:

- `docs/m5/auditoria-externa-post-freeze.md`
- `docs/m5/correcciones-post-freeze.md`

Estas fuentes no reescriben retrospectivamente el freeze original; documentan
evolución posterior.
