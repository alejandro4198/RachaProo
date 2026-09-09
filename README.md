# RachaPro

## 1. Identificación del proyecto

| Campo | Información |
|---|---|
| Proyecto | RachaPro — Gestor de Productividad Académica |
| Integrante | Alejandro Villamizar Rodríguez |
| Curso | Arquitectura de Software |
| Estado académico documentado | Semanas 1–7 completadas |
| Próxima etapa | Semana 8 — ADR y mini-comité arquitectónico |
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

La arquitectura objetivo seleccionada durante Semana 7 es un:

**Monolito modular**

con los módulos conceptuales:

    Identity
    Activities
    Focus
    Progress
    Reminders

Este diseño se considera TO-BE y no se presenta como si ya estuviera completamente implementado.

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

Los documentos de semanas anteriores pueden contener estados o pendientes que eran válidos en el momento en que fueron escritos. Cuando existe evidencia posterior, debe interpretarse junto con los documentos más recientes y no como una descripción del estado vigente.

---

## 4. Arquitectura vigente — referencias canónicas

Para conocer el estado actual del sistema se debe consultar, en este orden:

1. `dossier/05-c4-contexto.md` — C4 Nivel 1.
2. `dossier/06-c4-contenedores.md` — C4 Nivel 2.
3. `dossier/07-c4-componentes.md` — C4 Nivel 3.
4. `dossier/08-decision-estilo-arquitectonico.md` — decisión TO-BE de Semana 7.

Los siguientes documentos se conservan como trazabilidad histórica y **no deben utilizarse por sí solos como representación del AS-IS vigente**:

- `docs/architecture/current/architecture-current.md`
- `docs/architecture/comparison-historical-current.md`

---

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

## 9. Estado al cierre de Semana 7

El equipo ha definido como arquitectura objetivo:

**Monolito modular**

con cinco módulos principales:

- Identity;
- Activities;
- Focus;
- Progress;
- Reminders.

Las reglas técnicas exactas para imponer los límites entre módulos, los mecanismos de comunicación interna y la estrategia de migración quedan como decisiones posteriores y podrán formalizarse mediante ADR.

El siguiente bloque académico corresponde a Semana 8.
