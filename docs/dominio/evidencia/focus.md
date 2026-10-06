# Evidencia de dominio — Focus

## 1. Responsabilidad observada

Focus gestiona el ciclo de vida de sesiones Pomodoro y sus estados asociados.

### HECHO DEL REPOSITORIO

Se observa que:

- existen sesiones `PomodoroSession`
- una sesión puede ser `FOCUS`, `SHORT_BREAK` o `LONG_BREAK`
- una sesión maneja estados como `RUNNING`, `PAUSED`, `COMPLETED` y `CANCELLED`
- una sesión puede existir sin una Activity asociada
- cuando existe `activityId`, Focus consulta Activities mediante `ActivityLookup`

La responsabilidad principal se observa en:

- `PomodoroSessionService`
- `PomodoroSessionEntity`
- `PomodoroSessionRepository`

### INFERENCIA

El conjunto anterior sugiere una responsabilidad funcional cohesionada alrededor
de la sesión Pomodoro y su ciclo de vida.

---

## 2. Conceptos y lenguaje propio

Conceptos observados:

- `PomodoroSession`
- `FOCUS`
- `SHORT_BREAK`
- `LONG_BREAK`
- `RUNNING`
- `PAUSED`
- `COMPLETED`
- `CANCELLED`
- duración planificada
- tiempo pausado
- día de finalización

Estos conceptos aparecen asociados al comportamiento de Focus en
`PomodoroSessionService` y `PomodoroSessionEntity`.

**Clasificación:** `HECHO DEL REPOSITORIO`.

### INFERENCIA

El vocabulario observado tiene significado propio y distinto del ciclo de vida
de Activity y de las métricas interpretadas por Progress.

---

## 3. Reglas propias observadas

En `PomodoroSessionService` se observan reglas relacionadas con la creación y
transición de sesiones.

### HECHO DEL REPOSITORIO

- una sesión nueva comienza en `RUNNING`
- solo una sesión `RUNNING` puede pausarse
- solo una sesión `PAUSED` puede reanudarse
- una sesión `RUNNING` o `PAUSED` puede completarse
- una sesión `RUNNING` o `PAUSED` puede cancelarse
- una sesión puede existir sin `activityId`
- cuando se proporciona `activityId`, se consulta `ActivityLookup`

También se observó en `PomodoroSessionDao.observeCompletedFocusDays(userId)` que
los días utilizados posteriormente por Progress provienen de sesiones:

- `userId = :userId`
- `type = 'FOCUS'`
- `status = 'COMPLETED'`
- `completedDateEpochDay IS NOT NULL`

**Clasificación:** `HECHO DEL REPOSITORIO`.

Queda pendiente documentar con precisión cualquier restricción adicional de
duración y diferencias de comportamiento entre los tipos de sesión.

---

## 4. Hechos que produce

A partir del ciclo de vida implementado en `PomodoroSessionService` pueden
observarse cambios equivalentes a:

- sesión iniciada
- sesión pausada
- sesión reanudada
- sesión completada
- sesión cancelada

Al completar una sesión se registra información de finalización, incluyendo datos
posteriormente utilizados por Progress.

Estos elementos se consideran **hechos de negocio observables en el estado del
modelo actual**.

No se ha demostrado que Focus publique eventos explícitos de integración como
resultado de estos cambios.

**Eventos explícitos producidos:** `INFORMACIÓN FALTANTE`.

---

## 5. Información que consume

Focus utiliza:

- `userId` correspondiente al usuario autenticado
- opcionalmente un `activityId`
- `ActivityLookup` cuando existe una referencia a Activity

La validación contra Activities únicamente ocurre cuando la sesión referencia
una Activity.

**Clasificación:** `HECHO DEL REPOSITORIO`.

---

## 6. Dependencias con otras responsabilidades

### Activities

`PomodoroSessionService` utiliza actualmente el contrato:

`ActivityLookup`

cuando existe `activityId`.

Por tanto, el AS-IS observado contiene la relación:

```text
Focus ──> ActivityLookup ──> Activities
```

Focus no necesita acceder directamente a `ActivityRepository` para realizar esa
validación.

**Clasificación:** `HECHO DEL REPOSITORIO`.

No se concluye todavía que este mecanismo de integración deba conservarse en el
diseño final de M5.

La comparación entre alternativas síncronas y asíncronas corresponde a la
evidencia de integración.

### Progress

Progress utiliza información derivada de sesiones `FOCUS + COMPLETED`,
incluyendo:

- días de sesiones completadas
- cantidad de sesiones Focus completadas
- estadísticas por día
- suma de `plannedDurationSeconds`

Focus produce los datos fuente y Progress les aplica reglas de agregación e
interpretación.

**Clasificación:** `HECHO DEL REPOSITORIO`.

---

## 7. Qué explícitamente no parece pertenecerle

Según la ubicación y comportamiento de las reglas observadas, Focus no controla:

- creación o edición de Activity
- ciclo de vida `PENDING / OVERDUE / COMPLETED` de Activity
- creación y administración de Categories y Subtasks
- cálculo de racha actual
- cálculo de mejor racha
- agregación e interpretación de estadísticas de Progress
- autenticación y gestión de identidad del usuario
- gestión de Reminders

Respecto a Achievement:

- no se observa que Focus controle su ciclo de vida ni sus reglas de desbloqueo
- el ownership definitivo de Achievement sigue abierto y no se asigna en este documento

**Clasificación:** `HECHO DEL REPOSITORIO` respecto a la ubicación actual de las
reglas observadas.

La asignación definitiva de ownership entre fronteras sigue siendo una decisión
de modelado.

---

## 8. Tensiones y contradicciones

### Semántica temporal

Al completar una sesión se produce un `completedDateEpochDay` basado en una
noción de día local.

Queda pendiente verificar si la política temporal y de zona horaria utilizada
por Focus coincide con la utilizada posteriormente por Progress para interpretar
días y calcular rachas.

**Clasificación:** `INFORMACIÓN FALTANTE`.

### Duración de Focus

Las consultas de `PomodoroSessionDao` utilizadas para métricas agregan:

`plannedDurationSeconds`

sobre sesiones:

- `type = 'FOCUS'`
- `status = 'COMPLETED'`

**Clasificación de la implementación:** `HECHO DEL REPOSITORIO`.

No está demostrado todavía que `plannedDurationSeconds` represente tiempo
efectivamente enfocado.

**Significado definitivo de la métrica:** `INFORMACIÓN FALTANTE`.

### Android vs backend

Queda pendiente verificar si el ciclo de vida del temporizador Android coincide
completamente con el ciclo de vida persistido en backend.

**Clasificación:** `INFORMACIÓN FALTANTE`.

---

## 9. Información faltante

Queda pendiente comprobar o decidir:

1. ownership de preferencias Pomodoro
2. política de zona horaria utilizada para `completedDateEpochDay`
3. significado exacto de `plannedDurationSeconds`
4. relación entre tiempo pausado y métricas de Focus
5. reglas específicas diferenciadas entre `FOCUS`, `SHORT_BREAK` y `LONG_BREAK`
6. existencia de eventos de integración explícitos producidos por Focus
7. correspondencia completa entre Android y backend
8. mecanismo de integración definitivo con Activities
9. ownership definitivo de Achievement

---

## 10. Estado epistemológico

| Elemento | Estado |
|---|---|
| Tipos de sesión | `HECHO DEL REPOSITORIO` |
| Estados de sesión | `HECHO DEL REPOSITORIO` |
| Reglas de transición | `HECHO DEL REPOSITORIO` |
| Activity opcional | `HECHO DEL REPOSITORIO` |
| Uso de `ActivityLookup` | `HECHO DEL REPOSITORIO` |
| Días `FOCUS + COMPLETED` usados por Progress | `HECHO DEL REPOSITORIO` |
| Uso de `plannedDurationSeconds` en métricas | `HECHO DEL REPOSITORIO` |
| Significado de tiempo efectivo | `INFORMACIÓN FALTANTE` |
| Política de zona horaria | `INFORMACIÓN FALTANTE` |
| Correspondencia Android ↔ backend | `INFORMACIÓN FALTANTE` |
| Eventos explícitos publicados por Focus | `INFORMACIÓN FALTANTE` |
| Ownership definitivo de Achievement | `INFORMACIÓN FALTANTE` |
| Mecanismo de integración definitivo con Activities | `NO DECIDIDO` |

---

## 11. Decisión sobre la frontera

### Evidencia considerada

- Focus mantiene reglas propias para el ciclo de vida de `PomodoroSession`
- los tipos `FOCUS`, `SHORT_BREAK` y `LONG_BREAK`, junto con estados como
  `RUNNING`, `PAUSED`, `COMPLETED` y `CANCELLED`, forman parte de un lenguaje
  propio de esta responsabilidad
- sus reglas internas pueden modificarse sin necesidad de cambiar las reglas
  internas de Activities, siempre que se mantenga el contrato externo requerido
  entre ambas responsabilidades
- una sesión puede existir sin una Activity asociada, por lo que la relación con
  Activities no define por completo la existencia de Focus
- Progress utiliza información producida por sesiones completadas, pero no
  controla el ciclo de vida de `PomodoroSession`

### Decisión del equipo

El equipo decide modelar `Focus` como un bounded context independiente.

La decisión no se basa únicamente en la existencia de un módulo o paquete
llamado `focus`, sino en la presencia de:

- reglas propias
- lenguaje propio
- capacidad de evolución independiente
- una responsabilidad protegida diferenciable de Activities, Progress y Reminders

**Clasificación:** `DECISIÓN DEL EQUIPO`.

### Responsabilidad protegida

Focus protege la gestión del ciclo de vida de una sesión Pomodoro.

Esto comprende principalmente:

- iniciar una sesión
- controlar su estado
- pausar una sesión
- reanudar una sesión
- completar una sesión
- cancelar una sesión
- distinguir el tipo de sesión
- mantener la información temporal asociada a la sesión

La referencia a una Activity puede formar parte del contexto de una sesión, pero
no determina su ciclo de vida interno.

### Qué queda fuera de esta frontera

No pertenece a Focus:

- gestión del ciclo de vida de Activity
- creación y administración de Categories y Subtasks
- cálculo e interpretación de rachas
- agregación e interpretación de estadísticas de Progress
- autenticación y gestión de identidad del usuario
- gestión de Reminders

El ownership definitivo de Achievement permanece abierto.

Por tanto, este documento solamente establece que Focus no protege actualmente
esa responsabilidad; no la asigna todavía a otro contexto.

### Dependencias relevantes

#### Activities

Cuando una sesión contiene `activityId`, el AS-IS observado utiliza
`ActivityLookup` para consultar información perteneciente a Activities.

Esta dependencia no transfiere a Focus el ownership de Activity.

El mecanismo definitivo de integración entre ambos contextos se analizará
posteriormente en la evidencia de integración.

#### Progress

Progress consume datos producidos por Focus, especialmente información de
sesiones `FOCUS + COMPLETED`.

Progress interpreta y agrega esos resultados, pero no controla las reglas
internas ni las transiciones de `PomodoroSession`.

### Condiciones de revisión

Como **decisión del equipo**, esta frontera deberá reconsiderarse si en el
futuro ocurre alguno de los siguientes cambios:

1. el ciclo de vida de `PomodoroSession` pasa a depender directamente de reglas
   internas de Activity y deja de poder evolucionar de forma independiente
2. una sesión deja de tener sentido fuera de una Activity y la relación deja de
   ser opcional
3. otra responsabilidad, como Progress, comienza a controlar directamente los
   estados y transiciones de las sesiones
4. las reglas propias de Focus desaparecen hasta convertirse únicamente en una
   operación técnica subordinada a otro contexto
5. aparecen nuevas reglas de negocio que demuestren que las sesiones Pomodoro
   pertenecen semánticamente a otra responsabilidad

Mientras estas condiciones no se presenten, **como decisión arquitectónica del
equipo**, se mantiene la separación de Focus como frontera de dominio.

### Estado de frontera

`Focus como bounded context = DECIDIDO`

**Decisión del equipo:** bounded context independiente.
