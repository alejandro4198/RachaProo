# Subdominios y bounded contexts

## 1. Propósito del documento

Este documento consolida las fronteras de dominio identificadas para RachaPro a partir de la evidencia observada en el repositorio y de las decisiones de modelado tomadas por el equipo durante la semana 9.

Su propósito es sintetizar las responsabilidades protegidas, conceptos principales, ownership, dependencias y exclusiones de los bounded contexts identificados, sin repetir el detalle técnico completo registrado en los documentos auxiliares de evidencia.

La trazabilidad detallada de cada candidato se encuentra en:

- `docs/dominio/evidencia/activities.md`
- `docs/dominio/evidencia/focus.md`
- `docs/dominio/evidencia/reminders.md`
- `docs/dominio/evidencia/progress.md`

Este documento no redefine las fronteras ya analizadas. Su función es consolidar las decisiones tomadas y servir como base para los artefactos posteriores:

- `docs/dominio/responsabilidades-contextos.md`
- `docs/dominio/context-map.puml`
- `docs/dominio/context-map.png`

---

## 2. Criterios utilizados para delimitar fronteras

La delimitación de los bounded contexts no se realizó únicamente a partir de nombres de paquetes, clases, tablas, pantallas o módulos técnicos.

El equipo utilizó principalmente las siguientes señales:

- responsabilidad protegida;
- lenguaje propio;
- reglas propias;
- ownership sobre conceptos y decisiones;
- dependencias con otras capacidades;
- capacidad de interpretar o producir significado propio;
- posibilidad de evolución independiente mientras se mantengan los contratos necesarios con otras capacidades.

La existencia de una dependencia entre dos capacidades no implica automáticamente transferencia de ownership.

De igual forma, la identificación de un bounded context no implica que cada contexto deba desplegarse como un microservicio independiente.

Por tanto:

```text
bounded context
≠
microservicio obligatorio
```

Las decisiones consolidadas en este documento corresponden al modelado del dominio.

Las decisiones relacionadas con:

- mecanismos de integración;
- comunicación síncrona o asíncrona;
- contratos API;
- eventos;
- despliegue;

se documentarán posteriormente en los artefactos específicos de integración.

---

## 3. Activities

### Responsabilidad protegida

Activities protege la capacidad de:

**planificar, organizar, descomponer y dar seguimiento a las actividades y tareas del usuario, incluyendo su clasificación y su ciclo de vida.**

Esta responsabilidad corresponde a una `DECISIÓN DEL EQUIPO` sustentada en las reglas y conceptos observados en:

`docs/dominio/evidencia/activities.md`.

Activities no se limita exclusivamente a trabajo académico.

La implementación observada permite clasificar actividades en diferentes ámbitos mediante categorías como:

- Universidad;
- Personal;
- Trabajo.

---

### Conceptos principales

Los principales conceptos identificados dentro de Activities son:

- `Activity`;
- `Category`;
- `Subtask`;
- prioridad;
- fecha y hora de vencimiento;
- estado de Activity;
- completitud;
- eliminación lógica.

También se observan estados funcionales de Activity como:

- `PENDING`;
- `OVERDUE`;
- `COMPLETED`.

Estos conceptos forman parte del lenguaje utilizado por la capacidad para gestionar el ciclo de vida de las actividades del usuario.

---

### Qué posee

Como decisión de modelado del equipo, Activities posee conceptualmente:

- `Activity`;
- `Category`;
- `Subtask`;
- reglas de creación y modificación de Activity;
- clasificación mediante Category;
- ciclo de vida de Activity;
- reglas de vencimiento;
- completitud de Activity;
- gestión de Subtask.

Activities conserva el ownership sobre estos conceptos incluso cuando otros bounded contexts utilizan una referencia a `Activity`.

Por tanto:

```text
referenciar Activity
≠
poseer Activity
```

---

### Qué consume

Activities utiliza información asociada al usuario para determinar ownership y limitar las operaciones a los datos correspondientes a dicho usuario.

En la implementación observada se utiliza `userId` para:

- crear registros asociados al usuario;
- consultar Activities;
- consultar Categories;
- consultar Subtasks;
- validar que determinadas entidades pertenezcan al usuario.

El uso de `userId` no implica que Activities sea responsable de:

- autenticación;
- login;
- credenciales;
- ciclo de vida de Identity.

También se observa una relación con el proceso de creación de categorías predeterminadas para usuarios registrados.

El mecanismo definitivo de integración con Identity no se define en este documento.

---

### Qué queda fuera

Activities no posee:

- ciclo de vida de `PomodoroSession`;
- reglas internas de Focus;
- cálculo de racha actual;
- cálculo de mejor racha;
- métricas agregadas de Progress;
- ciclo de vida de `Reminder`;
- scheduling técnico de notificaciones;
- autenticación;
- credenciales;
- ciclo interno de Identity.

La relación con otros contextos mediante identificadores o contratos de consulta no transfiere esas responsabilidades a Activities.

El ownership definitivo de Achievement tampoco se asigna a Activities en esta etapa.

---

### Estado de frontera

La evidencia de Activities mostró:

- lenguaje propio;
- reglas propias;
- conceptos con ownership definido;
- ciclo de vida propio;
- responsabilidad funcional diferenciable;
- capacidad de ser referenciado por otras responsabilidades sin transferir ownership.

A partir de esa evidencia, el equipo tomó la siguiente decisión:

```text
Activities como bounded context = DECIDIDO
```

**Clasificación:** `DECISIÓN DEL EQUIPO`.

La decisión no se basa únicamente en la existencia de un paquete o módulo llamado `activities`, sino en la cohesión funcional de las reglas y responsabilidades identificadas.

---

## 4. Focus

### Responsabilidad protegida

Focus protege la capacidad de:

**gestionar el ciclo de vida de las sesiones Pomodoro del usuario, incluyendo periodos de concentración y descanso, sus estados y transiciones.**

Esta responsabilidad corresponde a una `DECISIÓN DEL EQUIPO` sustentada en la evidencia registrada en:

`docs/dominio/evidencia/focus.md`.

La frontera se define por las reglas y el lenguaje asociados a las sesiones Pomodoro, no únicamente por la existencia de un paquete o pantalla relacionada con Focus.

---

### Conceptos principales

Los principales conceptos identificados dentro de Focus son:

- `PomodoroSession`;
- `FOCUS`;
- `SHORT_BREAK`;
- `LONG_BREAK`;
- `RUNNING`;
- `PAUSED`;
- `COMPLETED`;
- `CANCELLED`;
- duración asociada a la sesión;
- inicio;
- pausa;
- reanudación;
- finalización;
- cancelación.

La duración forma parte del modelo de la sesión, pero no se asume en este documento que represente necesariamente tiempo real efectivamente consumido en todos los casos.

---

### Qué posee

Como decisión de modelado del equipo, Focus posee conceptualmente:

- `PomodoroSession`;
- tipo de sesión;
- estado de la sesión;
- reglas de inicio;
- reglas de pausa;
- reglas de reanudación;
- reglas de finalización;
- reglas de cancelación;
- duración asociada a la sesión.

Focus conserva el ownership sobre el ciclo de vida de `PomodoroSession`.

Una sesión puede contener una referencia opcional a `Activity`, pero dicha referencia no transfiere ownership sobre Activity a Focus.

Por tanto:

```text
referenciar Activity
≠
poseer Activity
```

---

## 5. Reminders

### Responsabilidad protegida

Reminders protege la capacidad de:

**definir, programar y gestionar el ciclo de vida de los recordatorios del usuario, conservando la intención de generar un aviso en un momento determinado y permitiendo opcionalmente asociarlo a una Activity.**

Esta responsabilidad corresponde a una `DECISIÓN DEL EQUIPO` sustentada en la evidencia registrada en:

`docs/dominio/evidencia/reminders.md`.

En esta formulación, **programar** significa establecer y conservar el momento en que debe ocurrir el Reminder.

No significa ejecutar directamente mecanismos técnicos de Android como `AlarmManager`.

---

### Conceptos principales

Los principales conceptos funcionales identificados dentro de Reminders son:

- `Reminder`;
- momento programado;
- `triggerAtMillis`;
- `SCHEDULED`;
- `DELIVERED`;
- `CANCELLED`;
- asociación opcional con `Activity`;
- entrega;
- cancelación.

Estos conceptos representan el lenguaje funcional asociado al ciclo de vida del Reminder.

Los siguientes elementos no se consideran conceptos funcionales del dominio Reminders:

- `AlarmManager`;
- `PendingIntent`;
- `BootReceiver`;
- `ReminderReceiver`;
- `ScheduledExact`;
- `ScheduledInexact`;
- `InvalidTime`;
- `Error`.

Estos pertenecen a infraestructura o ejecución técnica.

---

### Qué posee

Como decisión de modelado del equipo, Reminders posee conceptualmente:

- `Reminder`;
- momento programado del Reminder;
- ciclo de vida funcional del Reminder;
- estado `SCHEDULED`;
- estado `DELIVERED`;
- estado `CANCELLED`;
- reglas de creación;
- reglas relacionadas con entrega;
- reglas relacionadas con cancelación;
- asociación opcional con `Activity`.

Reminders conserva el ownership sobre el ciclo de vida funcional de `Reminder`.

La referencia opcional a Activity no transfiere ownership sobre Activity a Reminders.

Por tanto:

```text
referenciar Activity
≠
poseer Activity
```

---

## 6. Progress

### Responsabilidad protegida

Progress protege la capacidad de:

**interpretar y agregar los resultados producidos por otras capacidades del sistema para medir y representar el avance del usuario mediante rachas y métricas de progreso.**

Esta responsabilidad corresponde a una `DECISIÓN DEL EQUIPO` sustentada en la evidencia registrada en:

`docs/dominio/evidencia/progress.md`.

Progress no produce originalmente los hechos de completitud de Activities o Focus. Los consume y los interpreta para construir significado adicional relacionado con el avance del usuario.

---

### Conceptos principales

Los principales conceptos identificados dentro de Progress son:

- progreso;
- día válido para progreso;
- racha actual;
- mejor racha;
- consecutividad;
- métricas agregadas de progreso;
- actividades completadas;
- Pomodoros Focus completados;
- tiempo de enfoque;
- progreso semanal.

También se observan criterios de consulta y proyección como:

- `TODAY`;
- `WEEK`;
- `ALL`.

Estos periodos apoyan la construcción de métricas y vistas de progreso, pero no se consideran por sí solos evidencia suficiente de autonomía de dominio.

---

### Qué posee

Como decisión de modelado del equipo, Progress posee conceptualmente:

- interpretación de los días derivados de hechos de completitud;
- cálculo de racha actual;
- cálculo de mejor racha;
- reglas de consecutividad;
- métricas agregadas de progreso;
- estadísticas y proyecciones utilizadas para representar el avance del usuario.

Progress posee la interpretación transversal de los hechos consumidos, no los hechos originales producidos por otras capacidades.

Por tanto:

```text
Activity completada
→ ownership de Activities

PomodoroSession FOCUS completada
→ ownership de Focus

interpretación de esos hechos como progreso
→ DECISIÓN DEL EQUIPO / INFERENCIA DE MODELADO asociada a Progress
```

---

## 7. Capacidades con ownership no decidido

### Achievement

Achievement corresponde a una capacidad observada en la implementación, pero su ownership definitivo no fue asignado durante la delimitación de las fronteras principales.

La evidencia muestra reglas de elegibilidad relacionadas con información proveniente de distintas capacidades, entre ellas:

- Activities completadas;
- Pomodoros `FOCUS` completados;
- racha actual.

Estas dependencias no son suficientes para concluir que Achievement pertenezca a Activities, Focus o Progress.

En particular, el hecho de que algunas reglas de Achievement consuman métricas calculadas o utilizadas por Progress no implica transferencia automática de ownership.

Por tanto:

```text
Achievement ownership = NO DECIDIDO
```

---

## 8. Resumen de fronteras

| Contexto | Responsabilidad protegida | Estado |
|---|---|---|
| Activities | Planificar, organizar, descomponer y dar seguimiento a las actividades y tareas del usuario, incluyendo su clasificación y su ciclo de vida. | `DECIDIDO` |
| Focus | Gestionar el ciclo de vida de las sesiones Pomodoro del usuario, incluyendo periodos de concentración y descanso, sus estados y transiciones. | `DECIDIDO` |
| Reminders | Definir, programar y gestionar el ciclo de vida de los recordatorios del usuario, conservando la intención de generar un aviso en un momento determinado y permitiendo opcionalmente asociarlo a una Activity. | `DECIDIDO` |
| Progress | Interpretar y agregar los resultados producidos por otras capacidades del sistema para medir y representar el avance del usuario mediante rachas y métricas de progreso. | `DECIDIDO` |
| Achievement | Ownership pendiente de una decisión específica sobre su responsabilidad, fuente de verdad, persistencia y relación Android/backend. | `NO DECIDIDO` |