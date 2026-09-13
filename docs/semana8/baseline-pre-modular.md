# Baseline pre-modular — Semana 8

## 1. Propósito

Este documento congela el estado de RachaPro inmediatamente antes de iniciar la materialización de la arquitectura objetivo de monolito modular definida durante Semana 7.

Su finalidad es conservar una referencia verificable para comparar posteriormente:

- arquitectura;
- dependencias;
- comportamiento;
- rendimiento;
- disponibilidad;
- gobernanza arquitectónica;

antes y después de los cambios de Semana 8.

Este baseline no representa la arquitectura modular ya implementada.

---

## 2. Referencia Git

Tag:

`semana8-baseline-pre-modular`

Commit corto:

`c2b7a88`

Commit completo:

`c2b7a889ad3cd83e81436216db111be4614d6ef6`

El tag apunta al estado de `master` posterior al cierre y auditoría documental de Semanas 1 a 7.

---

## 3. Arquitectura AS-IS

En este baseline el backend continúa siendo una única aplicación Spring Boot.

Los paquetes funcionales principales son:

- `achievement`
- `activity`
- `auth`
- `category`
- `error`
- `pomodoro`
- `reminder`
- `security`
- `subtask`
- `user`

La existencia de estos paquetes funcionales no implica todavía que las fronteras del monolito modular estén físicamente impuestas.

---

## 4. Arquitectura objetivo TO-BE

Durante Semana 7 el equipo seleccionó como arquitectura objetivo:

**Monolito modular**

con los módulos conceptuales:

- Identity
- Activities
- Focus
- Progress
- Reminders

Correspondencia conceptual actual:

| Módulo TO-BE | Implementación AS-IS relacionada |
|---|---|
| Identity | `auth`, `security`, `user` |
| Activities | `activity`, `category`, `subtask` |
| Focus | `pomodoro` |
| Progress | `achievement` backend y funcionalidades de progreso distribuidas principalmente en Android |
| Reminders | `reminder` backend y mecanismos de notificación Android |

`error` se mantiene como responsabilidad transversal mientras no exista una decisión diferente.

---

## 5. Dependencias relevantes del AS-IS

Entre las dependencias identificadas se encuentran:

### Internas respecto al mapa modular objetivo

- `activity -> category`
- `subtask -> activity`
- `auth -> security`
- `auth -> user`
- `security -> user`

### Cruces entre módulos conceptuales

- `pomodoro -> activity`
- `reminder -> activity`
- `user -> category`

Estas dependencias se conservan como evidencia del estado anterior a la materialización de las fronteras modulares.

Su aceptación, encapsulamiento o eliminación será materia de las decisiones arquitectónicas posteriores, especialmente ADR-002.

---

## 6. C4 conservado como evidencia AS-IS

Los siguientes documentos permanecen como representación histórica verificable previa a la modularización:

- `dossier/05-c4-contexto.md`
- `dossier/06-c4-contenedores.md`
- `dossier/07-c4-componentes.md`

No serán reemplazados retroactivamente.

Si la arquitectura física cambia durante Semana 8, se crearán nuevas vistas para representar el nuevo estado.

---

## 7. Persistencia AS-IS

RachaPro utiliza:

- PostgreSQL como persistencia central del backend.
- Room/SQLite como persistencia local Android.
- DataStore para preferencias y datos de sesión.

No se ha demostrado un mecanismo general y uniforme de sincronización entre Room y PostgreSQL para todas las funcionalidades.

Los comportamientos varían según el módulo.

---

## 8. Evidencia de rendimiento previa

### EXP-001

Escenario:

100 actividades en Android.

Hipótesis histórica:

la carga del listado podría superar 3 segundos.

Resultados aproximados:

- mediana: 1325 ms
- p95: 1621 ms
- máximo: 1666 ms

Conclusión:

la evidencia de este experimento no respaldó la hipótesis de superar 3 segundos bajo las condiciones probadas.

---

### EXP-002

Operación:

`POST /api/activities`

Configuración:

- 1 VU
- 25 operaciones consecutivas
- sin concurrencia multiusuario

Resultados de Semana 4:

- promedio: 11,16 ms
- mediana: 8,48 ms
- p90: 10,58 ms
- p95: 31,50 ms
- máximo: 46,02 ms
- 25/25 operaciones correctas
- 0 % fallos HTTP

---

### EXP-003

Operación:

`GET /api/activities`

Dataset:

- 500 usuarios sintéticos
- 1000 actividades por usuario
- 500.000 actividades totales

Resultados principales:

| VUs | Promedio | p95 | Resultado |
|---:|---:|---:|---:|
| 10 | 101,21 ms | 113,79 ms | 10/10 |
| 50 | 255,32 ms | 362,50 ms | 50/50 |
| 100 | 439,13 ms | 745,43 ms | 100/100 |
| 250 | 1173,90 ms | 1867,42 ms | 250/250 |
| 500 | 1863,11 ms | 3112,53 ms | 500/500 |

El escenario de 500 VUs constituye una señal de degradación de latencia bajo carga, pero este baseline no atribuye todavía el costo a PostgreSQL, JPA, Spring Boot, serialización, pool de conexiones u otro componente concreto.

Se requiere profiling adicional.

---

## 9. Disponibilidad

Existe un criterio definido:

Si el backend está detenido o inaccesible y Android intenta utilizar una funcionalidad dependiente del backend, la aplicación debe detectar la indisponibilidad y mostrar un error en máximo 5 minutos.

En este baseline el criterio está definido, pero todavía debe conservarse evidencia experimental específica del escenario de backend apagado.

Los cinco minutos representan un criterio de aceptación, no un timeout que necesariamente esté implementado con ese valor.

---

## 10. Gobernanza arquitectónica

En este baseline:

- la arquitectura modular está definida como TO-BE;
- las fronteras todavía no están materializadas completamente;
- existen dependencias directas entre futuros módulos;
- ArchUnit todavía no forma parte de la evidencia del baseline;
- no debe afirmarse que las fronteras modulares estén automatizadas.

---

## 11. Objetivo de comparación posterior

Después de Semana 8 se deberá construir una comparación entre este baseline y el nuevo estado.

La comparación deberá separar claramente los efectos de:

- refactorización modular;
- aislamiento de persistencia;
- reglas ArchUnit;
- modificaciones de disponibilidad;
- optimizaciones SQL/JPA;
- cambios de API;
- cambios de configuración.

No se atribuirán mejoras de rendimiento a la modularización si fueron producidas por una optimización diferente.

La comparación final deberá utilizar, siempre que sea posible, las mismas condiciones experimentales antes y después.

---

## 12. Estado

**BASELINE CONGELADO — PRE-MODULARIZACIÓN**

Referencia oficial:

`semana8-baseline-pre-modular`
