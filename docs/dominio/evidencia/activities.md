# Evidencia de dominio — Activities

<!-- M5:ACTIVITYLOOKUP-SEMANTICS:BEGIN -->

## Actualización posterior — semántica de ActivityLookup

La tensión registrada originalmente alrededor del término `active` fue
formalizada posteriormente en:

- `docs/integracion/contrato-api.md`

Para el contrato vigente documentado, `active` no significa exclusivamente
estado `PENDING` y no excluye por sí mismo `COMPLETED` u `OVERDUE`.

La operación observada comprueba que la actividad:

- existe;
- pertenece al usuario indicado;
- no está eliminada.

El backend muestra consumidores actuales de `ActivityLookup` en Focus y
Reminders.

Esta actualización conserva el registro histórico de la tensión original y
remite al contrato vigente.

<!-- M5:ACTIVITYLOOKUP-SEMANTICS:END -->


## 1. Responsabilidad observada

### HECHO DEL REPOSITORIO

En el backend se observan conceptos, reglas y persistencia diferenciados para:

- `Activity`
- `Category`
- `Subtask`

`ActivityService` controla operaciones relacionadas con creación, modificación,
completitud, vencimiento y eliminación lógica de actividades.

`CategoryService` controla reglas relacionadas con creación y consulta de
categorías activas.

`SubtaskService` controla operaciones sobre subtareas asociadas a una Activity.

### INFERENCIA

Aunque `Activity`, `Category` y `Subtask` poseen reglas internas propias, la
evidencia sugiere que colaboran dentro de una misma capacidad funcional de
organización, descomposición y seguimiento de actividades.

### DECISIÓN DEL EQUIPO

La capacidad que la frontera Activities protege se formula como:

**planificar, organizar, descomponer y dar seguimiento a las actividades y
tareas del usuario, incluyendo su clasificación y su ciclo de vida.**

Esta formulación constituye una decisión de modelado y no un hecho directamente
extraído del repositorio.

No se utiliza la ubicación física de los paquetes como justificación de
frontera.

---

## 2. Conceptos y lenguaje propio

### Conceptos de dominio observados

- `Activity`
- `Category`
- `Subtask`
- `PENDING`
- `OVERDUE`
- `COMPLETED`
- `LOW`
- `MEDIUM`
- `HIGH`
- prioridad
- vencimiento
- completitud
- repetición
- eliminación lógica
- Category activa

### Representaciones de implementación observadas

- `dueDateEpochDay`
- `dueTimeMinutes`
- `repeatRule`
- `completedAt`
- `completedDateEpochDay`
- `isDeleted`
- `isActive`

### HECHO DEL REPOSITORIO

Estos conceptos y representaciones aparecen asociados al modelo y comportamiento
implementado en componentes como:

- `ActivityEntity`
- `ActivityService`
- `CategoryService`
- `SubtaskService`

### INFERENCIA

El vocabulario observado describe planificación, clasificación, descomposición
y seguimiento de tareas y actividades.

Su significado es distinto del utilizado para describir sesiones Pomodoro,
métricas de Progress, autenticación o ejecución de recordatorios.

---

## 3. Reglas propias observadas

### Activity

Archivo principal:

`backend/src/main/kotlin/com/example/rachapro/backend/activities/activity/ActivityService.kt`

#### Creación

Método:

`ActivityService.create(...)`

### HECHO DEL REPOSITORIO

Durante la creación se observa que:

- el título se normaliza mediante `trim()`
- el título no puede quedar vacío
- la prioridad se normaliza a mayúsculas
- la prioridad debe pertenecer a `LOW`, `MEDIUM` o `HIGH`
- la Category indicada debe existir
- la Category debe pertenecer al usuario
- la Category debe estar activa
- una Activity nueva inicia en estado `PENDING`
- `completedAt` comienza sin valor
- `completedDateEpochDay` comienza sin valor
- `isDeleted` comienza en `false`

#### Actualización

Método:

`ActivityService.update(...)`

### HECHO DEL REPOSITORIO

Durante una actualización:

- la Activity debe existir
- debe pertenecer al usuario
- no debe estar eliminada
- el título no puede quedar vacío
- la prioridad debe pertenecer a `LOW`, `MEDIUM` o `HIGH`
- la Category debe existir, pertenecer al usuario y estar activa
- pueden modificarse datos como título, descripción, fecha, hora, prioridad,
  Category y `repeatRule`

#### Completitud

Método:

`ActivityService.complete(...)`

### HECHO DEL REPOSITORIO

Al completar una Activity:

- la Activity debe existir
- debe pertenecer al usuario
- no debe estar eliminada
- su estado pasa a `COMPLETED`
- se registra `completedAt`
- se registra `completedDateEpochDay`
- se actualiza `updatedAt`

#### `PENDING → OVERDUE`

Método:

`ActivityService.refreshStatuses(...)`

### HECHO DEL REPOSITORIO

Una Activity en estado `PENDING` pasa a `OVERDUE` cuando:

- su fecha de vencimiento es anterior al día actual

o cuando:

- vence el día actual
- tiene `dueTimeMinutes`
- la hora límite ya pasó

#### `OVERDUE → PENDING`

Método:

`ActivityService.refreshStatuses(...)`

### HECHO DEL REPOSITORIO

Una Activity en estado `OVERDUE` vuelve a `PENDING` cuando:

- su fecha de vencimiento es posterior al día actual

o cuando:

- corresponde al día actual
- no existe hora límite

o cuando:

- corresponde al día actual
- la hora límite todavía no ha pasado

#### Eliminación

Método:

`ActivityService.delete(...)`

### HECHO DEL REPOSITORIO

El comportamiento de eliminación actualmente implementado para Activity es
lógico:

- `isDeleted` pasa a `true`
- se registra `deletedAt`
- se actualiza `updatedAt`

La entidad permanece persistida.

Esto describe el comportamiento implementado actualmente y no se presenta como
una política de dominio inmutable.

---

### Category

Archivos principales:

- `backend/src/main/kotlin/com/example/rachapro/backend/activities/category/CategoryService.kt`
- `backend/src/main/kotlin/com/example/rachapro/backend/activities/category/DefaultCategoryProvisioningService.kt`

#### Creación

Método:

`CategoryService.create(...)`

### HECHO DEL REPOSITORIO

Durante la creación de una Category:

- el nombre se normaliza mediante `trim()`
- el nombre no puede quedar vacío
- no puede existir otra Category del mismo usuario con el mismo nombre,
  ignorando mayúsculas y minúsculas
- una Category nueva comienza con `isActive = true`

#### Consulta de categorías activas

Método:

`CategoryService.findActiveByUserId(...)`

### HECHO DEL REPOSITORIO

La consulta observada recupera categorías del usuario que se encuentran activas.

#### Aprovisionamiento inicial

Método:

`DefaultCategoryProvisioningService.createDefaultsForUser(...)`

### HECHO DEL REPOSITORIO

La implementación crea las categorías predeterminadas:

- `Universidad`
- `Personal`
- `Trabajo`

La existencia de estas categorías demuestra que el modelo implementado de
Activities no está restringido técnicamente únicamente a actividades
académicas.

El mecanismo mediante el cual se activa este aprovisionamiento debe analizarse
respetando su evolución temporal.

---

### Subtask

Archivo principal:

`backend/src/main/kotlin/com/example/rachapro/backend/activities/subtask/SubtaskService.kt`

#### Validación de Activity padre

Métodos observados:

- `SubtaskService.findAll(...)`
- `SubtaskService.create(...)`
- `SubtaskService.complete(...)`
- `SubtaskService.update(...)`
- `SubtaskService.delete(...)`
- `SubtaskService.uncomplete(...)`

### HECHO DEL REPOSITORIO

Antes de operar sobre Subtasks, el servicio comprueba que la Activity padre:

- exista
- pertenezca al usuario
- no esté eliminada

#### Creación

Método:

`SubtaskService.create(...)`

### HECHO DEL REPOSITORIO

Durante la creación de una Subtask:

- el título se normaliza mediante `trim()`
- el título no puede quedar vacío
- se asocia a una `activityId`
- comienza con `isCompleted = false`
- comienza con `completedAt = null`

#### Completitud

Método:

`SubtaskService.complete(...)`

### HECHO DEL REPOSITORIO

Al completar una Subtask:

- `isCompleted` pasa a `true`
- se registra `completedAt`
- se actualiza `updatedAt`

#### Desmarcar completitud

Método:

`SubtaskService.uncomplete(...)`

### HECHO DEL REPOSITORIO

Al desmarcar su completitud:

- `isCompleted` pasa a `false`
- `completedAt` vuelve a `null`
- se actualiza `updatedAt`

#### Actualización

Método:

`SubtaskService.update(...)`

### HECHO DEL REPOSITORIO

La actualización permite modificar el título después de validar:

- Activity padre válida
- Subtask asociada a dicha Activity
- título no vacío

#### Eliminación

Método:

`SubtaskService.delete(...)`

### HECHO DEL REPOSITORIO

La implementación actual ejecuta:

`subtaskRepository.delete(subtask)`

Por tanto, el comportamiento técnico observado actualmente corresponde a una
eliminación mediante el repositorio, sin marca equivalente a `isDeleted`.

Esto se registra como comportamiento implementado y no como una regla esencial
o permanente del dominio.

---

## 4. Hechos que produce

A partir de las reglas observadas pueden describirse hechos de negocio como:

- Activity creada
- Activity modificada
- Activity completada
- Activity vencida
- Activity restaurada a pendiente
- Activity eliminada lógicamente
- Category creada
- Category predeterminada creada
- Subtask creada
- Subtask modificada
- Subtask completada
- Subtask marcada nuevamente como no completada
- Subtask eliminada

Estos son **hechos observables derivados de cambios de estado del modelo**.

No debe asumirse automáticamente que cada uno corresponde a un evento de
integración explícitamente publicado.

### Baseline temporal

En el baseline de Semana 8 no se ha demostrado en esta revisión que Activities
publicara o consumiera eventos explícitos para cada uno de estos cambios.

Posteriormente, durante el trabajo de integración de M5, aparece:

`UserRegisteredV1Listener`

dentro de Activities.

La existencia actual del listener está demostrada.

Lo que debe mantenerse separado es:

- qué mecanismo pertenecía al baseline S8
- qué mecanismo fue introducido posteriormente durante M5 / Spike

Por tanto, no debe reconstruirse retrospectivamente `UserRegisteredV1Listener`
como parte de la arquitectura original de Semana 8.

---

## 5. Información que consume

### `userId`

### HECHO DEL REPOSITORIO

Activities recibe y utiliza `userId` para operaciones como:

- consultar Activities
- comprobar que una Activity pertenece al usuario
- consultar Categories
- comprobar unicidad de Category
- crear Categories
- validar operaciones sobre Subtasks

El código demuestra el uso de `userId`.

No se concluye a partir de ello que Activities posea:

- autenticación
- credenciales
- sesiones de usuario
- reglas internas de Identity

### Category durante Activity

Para crear o modificar una Activity se utiliza una referencia `categoryId`.

La Category debe:

- existir
- pertenecer al mismo usuario
- estar activa

### Tiempo

Las reglas de vencimiento utilizan información temporal basada en:

- `LocalDate.now()`
- `LocalTime.now()`
- `dueDateEpochDay`
- `dueTimeMinutes`

La completitud también registra información temporal mediante:

- `System.currentTimeMillis()`
- `LocalDate.now().toEpochDay()`

**Clasificación:** `HECHO DEL REPOSITORIO`.

---

## 6. Dependencias con otras responsabilidades

### Focus

Focus consulta actualmente información de Activities mediante el contrato:

`ActivityLookup`

El contrato expone:

`existsActiveActivityForUser(activityId, userId)`

La implementación se encuentra en:

`ActivityLookupService.existsActiveActivityForUser(...)`

y comprueba mediante Activities que la Activity:

- exista
- pertenezca al usuario
- no esté eliminada

La relación AS-IS puede representarse como:

```text
Focus ──> ActivityLookup ──> Activities
```

Focus no necesita acceder directamente a `ActivityRepository`.

**Clasificación:** `HECHO DEL REPOSITORIO`.

El mecanismo definitivo de integración se evaluará posteriormente y no forma
parte de la decisión de frontera de este documento.

---

### Progress

Progress consume información producida por Activities para construir métricas
derivadas.

Entre la información observada se encuentran:

- días de Activities completadas
- cantidad de Activities completadas
- Activities completadas por periodo
- estadísticas de completitud por día

Activities produce los datos fuente.

Progress interpreta y agrega esos datos para calcular métricas distintas.

**Clasificación:** `HECHO DEL REPOSITORIO`.

El consumo de información por Progress no transfiere a Progress el ownership del
ciclo de vida de Activity.

---

### Reminders

Existe evidencia arquitectónica previa de una relación entre Reminders y
Activities mediante `ActivityLookup`.

Sin embargo, para mantener este documento estrictamente trazado al código
inspeccionado específicamente durante esta revisión, queda pendiente auditar
directamente el consumidor actual dentro de Reminders.

**Clasificación en esta revisión:** `INFORMACIÓN FALTANTE`.

La relación deberá cerrarse durante la auditoría específica de Reminders.

---

### Identity

Debe distinguirse entre dos momentos temporales.

#### S8 AS-IS

El baseline de Semana 8 debe analizarse independientemente de modificaciones
introducidas posteriormente durante M5.

No debe atribuirse automáticamente al baseline S8 el mecanismo de integración
por `UserRegisteredV1`.

#### M5 / Spike de integración

En el repositorio actual existe:

`UserRegisteredV1Listener`

dentro de Activities.

La trazabilidad Git revisada muestra que ese archivo aparece asociado al trabajo
posterior del Spike de integración y que no existía en el baseline de Semana 8
inspeccionado.

En la versión actual, el listener utiliza:

`DefaultCategoryProvisioning`

para ejecutar el aprovisionamiento de categorías predeterminadas.

Por tanto, en la arquitectura posterior al Spike se observa:

```text
Identity
   |
   | UserRegisteredV1
   v
Activities
   |
   v
DefaultCategoryProvisioning
```

**Clasificación del mecanismo actual:** `HECHO DEL REPOSITORIO`.

**Clasificación respecto al baseline S8:** mecanismo posterior al estado
inspeccionado de esa semana.

---

## 7. Qué explícitamente no pertenece

Según las reglas observadas y la decisión de frontera adoptada, Activities no
protege:

- el ciclo de vida de `PomodoroSession`
- estados como `RUNNING`, `PAUSED` o `CANCELLED` de Focus
- cálculo de racha actual
- cálculo de mejor racha
- agregación e interpretación de estadísticas de Progress
- programación y ejecución de Reminders
- autenticación del usuario
- gestión de credenciales
- ciclo de vida interno de Identity

Respecto a Achievement:

- Activities produce información de completitud que puede ser utilizada por
  reglas relacionadas con Achievement
- no se observa que Activities controle su ownership definitivo

El ownership definitivo de Achievement permanece abierto.

Producir información posteriormente consumida por otro contexto **no transfiere
ownership de la responsabilidad consumidora**.

---

## 8. Tensiones y contradicciones

### Semántica de `active` en `ActivityLookup`

El contrato expone:

`existsActiveActivityForUser(activityId, userId)`

Sin embargo, la implementación observada en:

`ActivityLookupService.existsActiveActivityForUser(...)`

utiliza:

`findByIdAndUserIdAndIsDeletedFalse(...)`

Por tanto, comprueba que la Activity:

- exista
- pertenezca al usuario
- no esté eliminada

No se observa una comprobación explícita de que:

- esté en estado `PENDING`
- no esté `COMPLETED`
- no esté `OVERDUE`

Por tanto, en la implementación actual:

`active`

parece significar principalmente:

`existente + del usuario + no eliminada`

y no necesariamente una Activity activa según su estado funcional.

No se determina todavía si esto constituye:

- una decisión intencional del contrato
- una ambigüedad de naming
- una regla incompleta

**Clasificación:** `TENSIÓN DOCUMENTADA`.

---

### Activity completada y posteriormente eliminada

En Android, `ActivityDao.observeCompletedDays()` selecciona días de Activities
con:

- `status = 'COMPLETED'`
- `completedDateEpochDay IS NOT NULL`

pero no aplica:

`isDeleted = 0`

Otras consultas estadísticas de Activities completadas sí excluyen registros
eliminados.

Por tanto, una Activity completada y posteriormente eliminada puede continuar
aportando un día al conjunto utilizado para rachas mientras dejaría de participar
en otras estadísticas.

No se concluye todavía si esto representa:

- una regla histórica intencional
- una inconsistencia entre consultas

**Clasificación:** `TENSIÓN DOCUMENTADA`.

---

### Semántica temporal

`ActivityService` utiliza distintas representaciones temporales:

- `System.currentTimeMillis()`
- `LocalDate.now().toEpochDay()`
- `LocalTime.now()`

La lógica de vencimiento depende del día y hora local.

La completitud produce `completedDateEpochDay`, dato posteriormente utilizado
por Progress.

Queda pendiente establecer explícitamente la política de zona horaria compartida
entre productores y consumidores de esta información.

**Clasificación:** `INFORMACIÓN FALTANTE`.

---

### Baseline S8 vs Spike M5

`UserRegisteredV1Listener` existe en el repositorio actual, pero la trazabilidad
Git revisada indica que fue incorporado posteriormente durante el trabajo del
Spike de integración.

Por tanto, deben mantenerse separadas las afirmaciones:

```text
S8 AS-IS
```

y:

```text
arquitectura posterior a M5 / Spike
```

No hacerlo produciría una reconstrucción histórica incorrecta de la arquitectura.

**Clasificación:** `TENSIÓN TEMPORAL DOCUMENTADA`.

---

### Category y Subtask dentro de la misma frontera

Category y Subtask poseen algunas reglas internas capaces de cambiar sin
modificar directamente todas las reglas de Activity.

Sin embargo:

- Activity necesita una Category válida para crearse y modificarse
- Subtask no opera sin una Activity padre válida
- ambos conceptos participan en la misma capacidad de organización y
  descomposición de actividades

La separación interna de reglas no constituye por sí sola evidencia suficiente
para crear bounded contexts independientes.

**Clasificación:** `INFERENCIA DE MODELADO`.

---

## 9. Información faltante

### Preguntas de dominio

Queda pendiente determinar:

1. significado contractual definitivo del término `active` en `ActivityLookup`
2. intención de negocio para Activities completadas y posteriormente eliminadas
3. semántica completa de `repeatRule`
4. comportamiento esperado de repetición después de completar una Activity
5. si completar una Activity debe depender del estado de sus Subtasks
6. comportamiento esperado de Categories inactivas ya asociadas a Activities
7. ownership definitivo de Achievement

### Preguntas de integración e implementación

Queda pendiente verificar o decidir:

1. política temporal y de zona horaria para `completedDateEpochDay`
2. mecanismo definitivo de integración entre Activities y Focus
3. mecanismo definitivo de integración entre Activities y Reminders
4. relación definitiva entre Activities e Identity después de evaluar el Spike
5. permanencia de `UserRegisteredV1` como mecanismo arquitectónico definitivo
6. eventos de dominio o integración adicionales que Activities deba publicar
7. correspondencia completa entre las reglas Android y backend
8. consumidor actual exacto de `ActivityLookup` dentro de Reminders

---

## 10. Estado epistemológico

| Elemento | Estado |
|---|---|
| Ciclo de vida de Activity | `HECHO DEL REPOSITORIO` |
| Estados `PENDING / OVERDUE / COMPLETED` | `HECHO DEL REPOSITORIO` |
| Reglas de prioridad | `HECHO DEL REPOSITORIO` |
| Validación de Category durante creación/modificación | `HECHO DEL REPOSITORIO` |
| Reglas de Category | `HECHO DEL REPOSITORIO` |
| Existencia del aprovisionamiento de categorías predeterminadas | `HECHO DEL REPOSITORIO` |
| Mecanismo de activación histórico del aprovisionamiento | `DEPENDIENTE DEL BASELINE TEMPORAL` |
| Subtask subordinada a una Activity válida | `HECHO DEL REPOSITORIO` |
| Ciclo de completitud de Subtask | `HECHO DEL REPOSITORIO` |
| Eliminación lógica de Activity | `HECHO DEL REPOSITORIO` |
| Eliminación actual de Subtask mediante repositorio | `HECHO DEL REPOSITORIO` |
| Uso de `ActivityLookup` por Focus | `HECHO DEL REPOSITORIO` |
| Significado real de `active` en `ActivityLookup` | `TENSIÓN DOCUMENTADA` |
| Tratamiento de Activities eliminadas en métricas | `TENSIÓN DOCUMENTADA` |
| Política de zona horaria | `INFORMACIÓN FALTANTE` |
| Relación actual con Reminders | `INFORMACIÓN FALTANTE` en esta revisión |
| `UserRegisteredV1Listener` posterior al baseline S8 | `HECHO DEL REPOSITORIO / TRAZABILIDAD GIT` |
| Integración definitiva con otros contextos | `NO DECIDIDA` |
| Ownership definitivo de Achievement | `INFORMACIÓN FALTANTE` |
| Cohesión Activity + Category + Subtask | `INFERENCIA DE MODELADO` |
| Activities como bounded context | `DECISIÓN DEL EQUIPO` |

---

## 11. Decisión sobre la frontera

### Evidencia considerada

La decisión considera que:

- Activity posee ciclo de vida y reglas propias
- Category participa directamente en la clasificación requerida por Activity
- Activity exige una Category válida y activa al crearse o modificarse
- Subtask está subordinada a la existencia de una Activity válida
- Category y Subtask poseen reglas internas propias, pero colaboran con Activity
  dentro de la misma capacidad funcional
- las reglas internas de Activities son distintas de las reglas de Focus,
  Progress, Reminders e Identity
- otros contextos pueden consumir información de Activity sin controlar su ciclo
  de vida interno
- `ActivityLookup` permite exponer una capacidad limitada sin entregar acceso
  directo al repositorio interno de Activities

### Decisión del equipo

El equipo decide modelar `Activities` como un bounded context independiente.

La frontera contiene:

- `Activity`
- `Category`
- `Subtask`

La decisión no se basa en que estos conceptos estén agrupados físicamente bajo
un mismo paquete.

Se basa en:

- cohesión funcional
- lenguaje compartido
- colaboración alrededor de la planificación y seguimiento de actividades
- dependencia funcional de Category y Subtask respecto a Activity
- separación semántica frente a Focus, Progress, Reminders e Identity

**Clasificación:** `DECISIÓN DEL EQUIPO`.

### Responsabilidad protegida

Activities protege la capacidad de:

**planificar, organizar, descomponer y dar seguimiento a las actividades y
tareas del usuario, incluyendo su clasificación y su ciclo de vida.**

Esta formulación no restringe la frontera únicamente al ámbito académico.

El producto RachaPro mantiene una orientación principal hacia productividad
académica, pero el modelo actual de Activities soporta también categorías como:

- `Universidad`
- `Personal`
- `Trabajo`

Por tanto, la responsabilidad protegida se formula alrededor de actividades y
tareas del usuario y no únicamente alrededor de trabajo académico.

### Qué queda fuera de esta frontera

No pertenece a Activities:

- gestión del ciclo de vida de `PomodoroSession`
- reglas internas de Focus
- cálculo de rachas
- interpretación de métricas y estadísticas de Progress
- programación y ejecución de Reminders
- autenticación
- credenciales
- gestión interna de Identity

El ownership definitivo de Achievement permanece abierto.

Activities puede producir información consumida por Achievement o Progress sin
adquirir por ello ownership sobre esas responsabilidades.

### Dependencias relevantes

#### Focus

Focus consulta Activities mediante `ActivityLookup` cuando necesita validar una
referencia a Activity.

La dependencia no transfiere a Focus las reglas internas de Activity.

#### Progress

Progress consume información relacionada con completitud de Activities y la
interpreta para producir métricas derivadas.

Progress no controla el ciclo de vida de Activity.

#### Reminders

Existe una relación arquitectónica conocida entre Reminders y Activities.

La forma exacta observada en el código actual deberá cerrarse durante la
auditoría específica de Reminders antes de utilizarla como evidencia definitiva
en el Context Map.

#### Identity

Activities utiliza `userId`, pero no posee autenticación ni identidad.

La integración mediante `UserRegisteredV1` corresponde a una evolución posterior
al baseline S8 y debe representarse respetando esa trazabilidad temporal.

### Condiciones de revisión

Como **decisión del equipo**, la frontera deberá reconsiderarse si en el futuro
ocurre alguno de los siguientes cambios:

1. Category desarrolla reglas, lenguaje y ciclo de vida propios que puedan
   evolucionar de manera independiente y dejen de estar principalmente
   subordinados a la organización de Activities
2. Subtask adquiere un ciclo de vida autónomo que pueda existir y evolucionar sin
   una Activity padre
3. Activity deja de requerir Category como parte de su organización y ambas
   capacidades evolucionan hacia objetivos de negocio distintos
4. reglas pertenecientes actualmente a Activities pasan a estar controladas por
   otra responsabilidad y Activities deja de proteger su propio ciclo funcional
5. nuevas reglas de negocio muestran que Activity, Category o Subtask poseen
   ritmos de cambio, lenguaje y responsabilidades suficientemente distintos para
   justificar fronteras separadas
6. Activities deja de representar una capacidad de planificación y seguimiento y
   se convierte únicamente en almacenamiento técnico de datos consumidos por
   otros contextos

Mientras estas condiciones no se presenten, **como decisión arquitectónica del
equipo**, se mantiene `Activity`, `Category` y `Subtask` dentro de una misma
frontera.

Esta decisión no implica que Activities deba desplegarse como microservicio.

La frontera de dominio y la unidad física de despliegue son decisiones
arquitectónicas distintas.

### Estado de frontera

`Activities como bounded context = DECIDIDO`

**Decisión del equipo:** bounded context independiente que contiene `Activity`,
`Category` y `Subtask`.
