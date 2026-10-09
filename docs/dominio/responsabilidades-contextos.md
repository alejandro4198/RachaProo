# Responsabilidades por bounded context

## 1. Propósito del documento

Este documento compara las responsabilidades de los bounded contexts ya definidos para RachaPro, haciendo explícito qué conceptos y reglas posee cada contexto, qué información consume o referencia de otras capacidades y qué responsabilidades deben permanecer fuera de su frontera.

Su propósito es servir como insumo para la construcción posterior del `context-map.puml`. Por tanto, este documento describe límites de responsabilidad y ownership, pero no decide todavía mecanismos concretos de integración, protocolos de comunicación ni patrones de relación entre contextos.

---

## 2. Criterio de lectura

En este documento, **Posee** identifica los conceptos y reglas cuya responsabilidad pertenece conceptualmente al bounded context. **Consume / referencia** identifica información proveniente de otras capacidades que el contexto utiliza sin adquirir ownership sobre ella. **No posee** hace explícitas las responsabilidades que deben permanecer fuera de su frontera.

Estas categorías corresponden al modelado del dominio y no se derivan automáticamente de la ubicación física de clases, paquetes, tablas o componentes dentro del código.

Una dependencia observada entre contextos no determina por sí sola cómo deberán integrarse arquitectónicamente. Por tanto, referencias o consultas existentes no implican todavía decisiones sobre mecanismos como REST o eventos, ni sobre patrones de relación como ACL, Published Language o Shared Kernel.

`Achievement` permanece fuera de la matriz principal porque su ownership continúa como `NO DECIDIDO`; por ello, no se considera todavía un bounded context confirmado dentro de este documento.

---

## 3. Matriz comparativa de responsabilidades

| Contexto | Responsabilidad protegida | Posee | Consume / referencia | No posee |
|---|---|---|---|---|
| Activities | Planificar, organizar, descomponer y dar seguimiento a las actividades y tareas del usuario, incluyendo su clasificación y su ciclo de vida. | `Activity`, `Category`, `Subtask`, ciclo de vida de `Activity`. | `userId` y datos necesarios para asociar las entidades al usuario correspondiente. | `PomodoroSession`, rachas y métricas de Progress, `Reminder`, Identity. |
| Focus | Gestionar el ciclo de vida de las sesiones Pomodoro del usuario, incluyendo periodos de concentración y descanso, sus estados y transiciones. | `PomodoroSession`, tipo de sesión, estado de sesión y transiciones de su ciclo de vida. | `userId` y referencia opcional a `Activity`. | `Activity`, Progress, `Reminder`, Identity. |
| Reminders | Definir, programar y gestionar el ciclo de vida de los recordatorios del usuario, conservando la intención de generar un aviso en un momento determinado y permitiendo opcionalmente asociarlo a una Activity. | `Reminder`, momento programado y ciclo funcional `SCHEDULED / DELIVERED / CANCELLED`. | `userId` y referencia opcional a `Activity`, validada mediante `ActivityLookup` cuando aplica. | `Activity`, Focus, Progress, Identity e infraestructura Android como semántica de dominio. |
| Progress | Interpretar y agregar los resultados producidos por otras capacidades del sistema para medir y representar el avance del usuario mediante rachas y métricas de progreso. | Interpretación de progreso, racha actual, mejor racha y métricas agregadas. | Hechos producidos por Activities y Focus. | `Activity`, `PomodoroSession`, `Reminder`, Identity y Achievement (`ownership` no decidido; no se asigna a Progress en esta etapa). |

---

## 4. Activities

### Responsabilidad protegida

Planificar, organizar, descomponer y dar seguimiento a las actividades y tareas del usuario, incluyendo su clasificación y su ciclo de vida.

### Posee

- `Activity`;
- `Category`;
- `Subtask`;
- ciclo de vida de `Activity`;
- reglas asociadas a clasificación, prioridad, vencimiento y completitud.

### Consume o referencia

- `userId`;
- datos necesarios para asociar las entidades al usuario correspondiente.

### No posee

- `PomodoroSession`;
- ciclo de vida de Focus;
- rachas y métricas de Progress;
- `Reminder`;
- ciclo funcional de Reminders;
- autenticación;
- reglas internas de Identity;
- ownership definitivo de Achievement.

---

## 5. Focus

### Responsabilidad protegida

Gestionar el ciclo de vida de las sesiones Pomodoro del usuario, incluyendo periodos de concentración y descanso, sus estados y transiciones.

### Posee

- `PomodoroSession`;
- tipo de sesión;
- estado de sesión;
- transiciones del ciclo de vida;
- reglas asociadas a inicio, pausa, reanudación, finalización y cancelación.

### Consume o referencia

- `userId`;
- referencia opcional a `Activity`.

La referencia a Activity no transfiere ownership sobre ella a Focus.

### No posee

- `Activity`;
- `Category`;
- `Subtask`;
- interpretación de progreso;
- rachas y métricas agregadas de Progress;
- `Reminder`;
- ciclo funcional de Reminders;
- autenticación;
- reglas internas de Identity;
- ownership definitivo de Achievement.

---

## 6. Reminders

### Responsabilidad protegida

Definir, programar y gestionar el ciclo de vida de los recordatorios del usuario, conservando la intención de generar un aviso en un momento determinado y permitiendo opcionalmente asociarlo a una Activity.

### Posee

- `Reminder`;
- momento programado;
- ciclo de vida funcional;
- estados `SCHEDULED`, `DELIVERED` y `CANCELLED`;
- reglas funcionales relacionadas con creación, entrega y cancelación;
- asociación opcional con `Activity`.

La descripción de estas reglas no implica que backend y Android implementen exactamente las mismas precondiciones de transición. Las diferencias observadas permanecen documentadas en la evidencia específica de Reminders.

### Consume o referencia

- `userId`;
- `activityId` opcional;
- validación de la referencia mediante `ActivityLookup` cuando existe `activityId`.

La consulta de Activity no transfiere ownership sobre Activities a Reminders.

### No posee

- `Activity`;
- ciclo de vida de Activities;
- `PomodoroSession`;
- ciclo de vida de Focus;
- rachas y métricas de Progress;
- autenticación;
- reglas internas de Identity;
- infraestructura Android como semántica de dominio;
- resultados técnicos como `ScheduledExact`, `ScheduledInexact`, `InvalidTime` o `Error`;
- ownership definitivo de Achievement.

---

## 7. Progress

### Responsabilidad protegida

Interpretar y agregar los resultados producidos por otras capacidades del sistema para medir y representar el avance del usuario mediante rachas y métricas de progreso.

### Posee

- interpretación de progreso;
- racha actual;
- mejor racha;
- reglas de consecutividad;
- interpretación de los días derivados de hechos de completitud;
- métricas agregadas utilizadas para representar el avance del usuario.

Progress posee la interpretación de los hechos consumidos, no los hechos fuente.

### Consume o referencia

Desde Activities:

- hechos de Activities completadas;
- días de completitud;
- conteos y estadísticas asociadas.

Desde Focus:

- hechos de sesiones `FOCUS` completadas;
- días de completitud;
- duración agregada utilizada en métricas;
- conteos y estadísticas asociadas.

El consumo de esta información no transfiere ownership sobre `Activity` ni sobre `PomodoroSession`.

La duración agregada utilizada en las métricas no se interpreta automáticamente como tiempo efectivo de enfoque, dado que la evidencia observada utiliza `plannedDurationSeconds`.

### No posee

- `Activity`;
- `Category`;
- `Subtask`;
- ciclo de vida de Activities;
- `PomodoroSession`;
- ciclo de vida de Focus;
- `Reminder`;
- ciclo funcional de Reminders;
- autenticación;
- reglas internas de Identity;
- Achievement (`ownership` no decidido; no se asigna a Progress en esta etapa).

---

## 8. Capacidades todavía no asignadas

### Achievement

Achievement corresponde a una **capacidad observada** en la implementación, pero su ownership definitivo permanece `NO DECIDIDO`.

La evidencia observada muestra reglas de elegibilidad relacionadas con información proveniente de otras capacidades, entre ellas:

- Activities completadas;
- Pomodoros `FOCUS` completados;
- racha actual;
- cantidades acumuladas asociadas a completitud y sesiones Focus.

La existencia de estas reglas constituye evidencia de que Achievement consume información producida o interpretada por otras capacidades.

Sin embargo, esta evidencia no permite concluir todavía que Achievement constituya un bounded context independiente ni que deba pertenecer a Activities, Focus o Progress.

Tampoco se asigna a Reminders.

Por tanto:

```text
Achievement
→ capacidad observada

reglas de elegibilidad
→ observadas

ownership
→ NO DECIDIDO
```

La decisión sobre Achievement deberá cerrarse en un análisis específico de ownership, fuente de verdad, persistencia y relación entre Android y backend.

---

## 9. Reglas para el Context Map

La construcción posterior del `context-map.puml` deberá respetar las siguientes reglas:

- toda relación dibujada debe corresponder a una dependencia, consulta o intercambio sustentado por evidencia;
- la dirección de cada relación debe tener una semántica explícita;
- cada relación deberá indicar qué información cruza la frontera o qué dependencia representa;
- consumir o referenciar información de otro contexto no transfiere ownership;
- una dependencia observada en la implementación actual no determina por sí sola el mecanismo arquitectónico definitivo de integración;
- no se asumirá automáticamente que una relación actual de código implica REST, eventos u otro mecanismo específico;
- tampoco se asignarán patrones de relación como ACL, Published Language o Shared Kernel sin una decisión explícita y justificada;
- las decisiones sobre comunicación síncrona o asíncrona, contratos, APIs y eventos se documentarán en los artefactos específicos de integración;
- las relaciones del mapa deberán distinguir entre ownership del dato o concepto y uso de información producida por otro contexto;
- `Achievement` no aparecerá como bounded context confirmado mientras su ownership permanezca `NO DECIDIDO`.

El Context Map deberá representar fronteras y relaciones de dominio sin convertir automáticamente la estructura actual del código en la arquitectura objetivo.