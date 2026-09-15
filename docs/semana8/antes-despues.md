# Arquitectura antes vs. después — Semana 8

## 1. Propósito

Mostrar qué cambió realmente durante la materialización del monolito modular.

La comparación no presenta el estado anterior como incorrecto.

Su objetivo es documentar la evolución entre el baseline previo y el AS-IS posterior.

---

## 2. Antes — baseline pre-modular

Referencia:

`semana8-baseline-pre-modular`

El backend ya era una aplicación Spring Boot única.

Las capacidades existían funcionalmente, pero la organización principal no expresaba todavía las fronteras de módulo adoptadas en Semana 8.

Paquetes relevantes incluían, entre otros:

- auth;
- user;
- security;
- activity;
- category;
- subtask;
- pomodoro;
- achievement;
- reminder;
- error.

Existían dependencias que permitían que algunos servicios utilizaran directamente persistencia correspondiente a otra capacidad.

Este estado constituye el antecedente histórico del refactor.

---

## 3. Después — monolito modular AS-IS

Referencia del refactor:

`7213440`

Estructura principal:

- `identity`
- `activities`
- `focus`
- `progress`
- `reminders`
- `shared`

Correspondencia:

| Antes | Después |
|---|---|
| auth | identity/auth |
| user | identity/user |
| security | identity/security |
| activity | activities/activity |
| category | activities/category |
| subtask | activities/subtask |
| pomodoro | focus/pomodoro |
| achievement | progress/achievement |
| reminder | reminders/reminder |
| error | shared/error |

---

## 4. Cambio de dependencia

### Antes

El límite entre capacidades no estaba protegido por contratos intermodulares explícitos para todos los accesos relevantes.

### Después

Activities publica:

- `ActivityLookup`
- `DefaultCategoryProvisioning`

Consumidores:

- Identity -> Activities mediante `DefaultCategoryProvisioning`;
- Focus -> Activities mediante `ActivityLookup`;
- Reminders -> Activities mediante `ActivityLookup`.

Los consumidores ya no necesitan importar directamente la persistencia interna de Activities para estas operaciones.

---

## 5. Persistencia

### Antes

La organización no expresaba todavía la propiedad modular de persistencia adoptada en ADR-002.

### Después

Cada módulo posee sus propios repositorios y entidades:

- Identity -> User;
- Activities -> Activity, Category y Subtask;
- Focus -> PomodoroSession;
- Progress -> Achievement;
- Reminders -> Reminder.

Shared no posee persistencia de negocio.

La instancia física de PostgreSQL continúa siendo compartida.

---

## 6. Protección arquitectónica

### Antes

La organización dependía principalmente de disciplina manual.

### Después

ArchUnit verifica reglas de arquitectura relacionadas con:

- aislamiento entre módulos;
- acceso a APIs públicas;
- ausencia de ciclos;
- ausencia de repositorios y entidades de negocio en Shared.

Esto no elimina la necesidad de revisión de código, pero automatiza parte de la protección estructural.

---

## 7. Despliegue

### Antes

Un backend Spring Boot.

### Después

Un backend Spring Boot.

No se introdujeron:

- microservicios;
- despliegues independientes;
- comunicación HTTP entre módulos;
- mensajería entre módulos;
- bases de datos separadas por módulo.

Por tanto, el cambio es principalmente estructural y de dependencias internas.

---

## 8. Rendimiento

La modularización no se diseñó como una optimización de rendimiento.

La validación PRE/POST mostró:

- una primera secuencia donde POST fue más lento;
- una réplica con orden inverso donde POST fue ligeramente más rápido.

La diferencia no fue reproducible en una misma dirección.

Conclusión:

No se obtuvo evidencia consistente para atribuir una regresión de rendimiento al refactor modular.

Por separado, el experimento de paginación continúa mostrando ventaja de limitar la primera respuesta frente al endpoint que retorna aproximadamente 1000 actividades en el dataset sintético utilizado.

---

## 9. Resumen

| Aspecto | Antes | Después |
|---|---|---|
| Unidad de despliegue | Monolito | Monolito |
| Organización | paquetes funcionales separados | módulos por capacidad |
| Persistencia física | PostgreSQL | PostgreSQL |
| Propiedad de persistencia | menos explícita | explícita por módulo |
| Comunicación interna | dependencias directas posibles | contratos públicos para relaciones materializadas |
| Protección de fronteras | principalmente manual | ArchUnit |
| Ciclos | sin fitness function modular específica | regla automatizada |
| Shared | error técnico | Shared técnico protegido |
| Microservicios | no | no |
| Objetivo principal | funcionalidad existente | estructura y aislamiento |

---

## 10. Conclusión

Semana 8 no reemplaza el monolito por una arquitectura distribuida.

Transforma la organización interna del backend hacia un monolito modular con fronteras explícitas, propiedad de persistencia y reglas automatizadas.

El cambio principal demostrado es arquitectónico y estructural, no una mejora automática de rendimiento o escalabilidad.