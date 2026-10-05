# Evidencia de dominio — Reminders

## 1. Responsabilidad observada

### HECHO DEL REPOSITORIO

En el backend se observa el concepto `Reminder`, representado mediante:

- `ReminderEntity`
- `ReminderService`
- `ReminderRepository`
- `ReminderController`

Un Reminder contiene información como:

- `userId`
- `activityId` opcional
- contenido asociado al aviso
- `triggerAtMillis`
- `status`
- marcas temporales de creación y actualización

`ReminderService` controla operaciones relacionadas con:

- creación de Reminder
- consulta de Reminders del usuario
- cambio a estado `DELIVERED`
- cambio a estado `CANCELLED`

En Android también existen componentes encargados de persistir, programar,
recuperar y materializar Reminders.

Entre ellos:

- `ReminderRepository`
- `ReminderDao`
- `ReminderScheduler`
- `ReminderReceiver`
- `BootReceiver`

### INFERENCIA

Las reglas observadas alrededor de:

- momento programado
- estado del Reminder
- entrega
- cancelación
- asociación opcional con Activity

forman una capacidad funcional coherente y distinguible de Activities, Focus,
Progress e Identity.

### DECISIÓN DEL EQUIPO

La capacidad que la frontera Reminders protege se formula como:

**definir, programar y gestionar el ciclo de vida de los recordatorios del
usuario, conservando la intención de generar un aviso en un momento determinado
y permitiendo opcionalmente asociarlo a una Activity.**

En esta formulación, **programar** significa establecer y conservar el momento en
que el Reminder debe ocurrir.

No significa que mecanismos técnicos concretos como `AlarmManager` formen parte
del dominio.

---

## 2. Conceptos y lenguaje propio

### Lenguaje funcional del Reminder

Se observan los conceptos:

- `Reminder`
- momento programado
- `triggerAtMillis`
- `SCHEDULED`
- `DELIVERED`
- `CANCELLED`
- entrega
- cancelación
- asociación opcional con Activity

### HECHO DEL REPOSITORIO

`ReminderEntity` y las operaciones asociadas utilizan:

- `triggerAtMillis`
- `status`
- `activityId`
- `userId`

Los estados funcionales observados son:

- `SCHEDULED`
- `DELIVERED`
- `CANCELLED`

### Infraestructura Android observada

También aparecen conceptos técnicos como:

- `AlarmManager`
- `PendingIntent`
- `ReminderScheduler`
- `ReminderReceiver`
- `BootReceiver`
- `NotificationManager`
- programación exacta
- programación inexacta
- `ScheduledExact`
- `ScheduledInexact`
- `InvalidTime`
- `Error`

### INFERENCIA

Los estados:

- `SCHEDULED`
- `DELIVERED`
- `CANCELLED`

describen el ciclo funcional del Reminder.

En cambio:

- `ScheduledExact`
- `ScheduledInexact`
- `InvalidTime`
- `Error`

describen el resultado técnico de intentar programar el aviso en Android.

Por tanto, ambas familias de conceptos no representan el mismo nivel semántico.

---

## 3. Reglas propias observadas

### Backend

Archivo principal:

`backend/src/main/kotlin/com/example/rachapro/backend/reminders/reminder/ReminderService.kt`

### Creación

Método:

`ReminderService.create(...)`

#### HECHO DEL REPOSITORIO

Durante la creación se observa que:

- el Reminder se asocia a un `userId`
- `activityId` puede ser `null`
- cuando existe `activityId`, se consulta `ActivityLookup`
- la referencia se valida para el usuario correspondiente
- se almacena `triggerAtMillis`
- el Reminder nuevo inicia con estado `SCHEDULED`
- se registran marcas temporales de creación y actualización

La opcionalidad de Activity se encuentra además reflejada en:

`CreateReminderRequest`

donde:

```text
activityId: Long? = null
```

Por tanto, la posibilidad de crear un Reminder sin Activity no se infiere
únicamente de la nulabilidad de `ReminderEntity`, sino del contrato de creación y
del comportamiento de `ReminderService.create(...)`.

### Asociación opcional con Activity

Cuando `activityId` tiene valor, `ReminderService` invoca:

`ActivityLookup.existsActiveActivityForUser(activityId, userId)`

### HECHO DEL REPOSITORIO

La consulta a Activities es condicional.

Por tanto:

```text
activityId == null
→ Reminders no consulta Activities

activityId != null
→ Reminders consulta ActivityLookup
```

---

### Entrega

Método:

`ReminderService.markDelivered(...)`

### HECHO DEL REPOSITORIO

La implementación observada:

- localiza el Reminder por `reminderId` y `userId`
- cambia `status` a `DELIVERED`
- actualiza la marca temporal correspondiente
- persiste el Reminder

No se observa en el fragmento inspeccionado una validación explícita del estado
previo antes de asignar `DELIVERED`.

Por tanto, no se eleva todavía como regla de backend que únicamente un Reminder
`SCHEDULED` pueda pasar a `DELIVERED`.

---

### Cancelación

Método:

`ReminderService.cancel(...)`

### HECHO DEL REPOSITORIO

La implementación observada:

- localiza el Reminder por `reminderId` y `userId`
- cambia `status` a `CANCELLED`
- actualiza su marca temporal
- persiste el Reminder

No se observa en el fragmento inspeccionado una validación explícita del estado
previo antes de asignar `CANCELLED`.

Por tanto, no se afirma todavía como regla de backend que únicamente un Reminder
`SCHEDULED` pueda cancelarse.

---

## Reglas observadas en Android

### ReminderRepository

Archivo:

`app/src/main/java/com/example/rachapro/data/repository/ReminderRepository.kt`

### HECHO DEL REPOSITORIO

Durante la creación local se observa una validación explícita de:

`triggerAtMillis`

respecto al tiempo actual.

Si:

```text
triggerAtMillis <= System.currentTimeMillis()
```

la operación se considera inválida.

También se observa que `activityId` es opcional.

Cuando existe `activityId`, Android realiza validaciones relacionadas con la
Activity correspondiente.

---

### ReminderDao

Archivo:

`app/src/main/java/com/example/rachapro/data/local/dao/ReminderDao.kt`

### HECHO DEL REPOSITORIO

Las operaciones locales observadas para cambiar un Reminder a:

- `CANCELLED`
- `DELIVERED`

incluyen condición:

```text
status = 'SCHEDULED'
```

Por tanto, en la persistencia local Android sí se observa explícitamente el ciclo:

```text
SCHEDULED ──> DELIVERED
SCHEDULED ──> CANCELLED
```

Esto deberá compararse con el comportamiento del backend.

---

### ReminderScheduler

Archivo:

`app/src/main/java/com/example/rachapro/notifications/ReminderScheduler.kt`

### HECHO DEL REPOSITORIO

`ReminderScheduler.schedule(...)` comprueba que:

```text
triggerAtMillis > System.currentTimeMillis()
```

Si el momento no está en el futuro, devuelve:

`ReminderScheduleResult.InvalidTime`

Cuando el tiempo es válido, el scheduler evalúa las capacidades del sistema
Android para determinar si puede realizar programación exacta.

Los resultados técnicos observados son:

- `ScheduledExact`
- `ScheduledInexact`
- `InvalidTime`
- `Error`

La programación puede realizarse de forma exacta o inexacta dependiendo de las
capacidades y restricciones del dispositivo.

### CLASIFICACIÓN

La elección exacta/inexacta constituye comportamiento técnico de Android.

No se interpreta como un estado funcional adicional del Reminder.

---

### BootReceiver

Archivo:

`app/src/main/java/com/example/rachapro/notifications/BootReceiver.kt`

### HECHO DEL REPOSITORIO

Después de un reinicio del dispositivo, `BootReceiver`:

- obtiene el usuario activo
- recupera Reminders con estado `SCHEDULED`
- obtiene el tiempo actual
- compara cada `triggerAtMillis` con el tiempo actual
- vuelve a programar los Reminders cuyo momento todavía está en el futuro

Por tanto, Android contiene un mecanismo técnico de recuperación de programación
después del reinicio del dispositivo.

### INFERENCIA

La reprogramación posterior al reinicio materializa la intención previamente
persistida en el Reminder.

No crea una nueva responsabilidad de dominio.

---

### ReminderReceiver

Archivo:

`app/src/main/java/com/example/rachapro/notifications/ReminderReceiver.kt`

### HECHO DEL REPOSITORIO

Cuando Android recibe el disparo asociado al Reminder, se observa que el
receiver:

- obtiene información del usuario
- valida que exista una sesión de usuario compatible
- recupera información del Reminder
- comprueba que el Reminder continúe en estado `SCHEDULED`
- participa en la materialización de la notificación

### INFERENCIA

`ReminderReceiver` pertenece al mecanismo técnico mediante el cual Android
materializa el aviso.

Su existencia no implica que `BroadcastReceiver` sea un concepto del dominio
Reminders.

---

## 4. Hechos que produce

A partir de las reglas observadas pueden describirse hechos funcionales como:

- Reminder creado
- Reminder establecido para un momento determinado
- Reminder entregado
- Reminder cancelado

También pueden observarse resultados técnicos en Android como:

- programación exacta realizada
- programación inexacta realizada
- programación rechazada por tiempo inválido
- error técnico de programación
- Reminder reprogramado después de reinicio

### HECHO DEL REPOSITORIO

Los cambios de estado funcional:

```text
SCHEDULED
DELIVERED
CANCELLED
```

se encuentran persistidos en el modelo.

### INFERENCIA

Los resultados técnicos de Android no deben interpretarse automáticamente como
eventos de dominio.

### Eventos explícitos de integración

No se ha demostrado en esta revisión que:

- creación
- entrega
- cancelación

publiquen eventos explícitos de integración hacia otros bounded contexts.

**Clasificación:** `INFORMACIÓN FALTANTE`.

---

## 5. Información que consume

### `userId`

### HECHO DEL REPOSITORIO

Reminders utiliza `userId` para:

- consultar Reminders
- localizar un Reminder específico
- validar ownership
- persistir información local
- recuperar Reminders programados

El uso de `userId` no implica que Reminders posea autenticación o identidad.

---

### `activityId`

`activityId` es opcional.

### HECHO DEL REPOSITORIO

La opcionalidad se observa tanto en el contrato de creación como en
`ReminderService.create(...)`.

Un Reminder puede crearse con:

```text
activityId = null
```

Cuando existe referencia a una Activity, Reminders consulta:

`ActivityLookup`

para validarla.

Esto no transfiere a Reminders ownership sobre Activity.

---

### `triggerAtMillis`

Reminders consume un momento de activación representado mediante:

`triggerAtMillis`

### HECHO DEL REPOSITORIO

El valor se utiliza para:

- persistencia
- ordenamiento
- programación en Android
- recuperación después de reinicio
- decisión de si el Reminder sigue siendo futuro

### Precisión semántica

`triggerAtMillis` es una representación técnica del momento programado.

El concepto funcional es:

**momento en que debe producirse el aviso.**

---

## 6. Dependencias con otras responsabilidades

### Activities

Reminders depende condicionalmente del contrato:

`ActivityLookup`

La relación observada es:

```text
Reminders ──> ActivityLookup ──> Activities
```

### HECHO DEL REPOSITORIO

La dependencia ocurre únicamente cuando:

```text
activityId != null
```

Si el Reminder no referencia una Activity, puede crearse sin consultar
Activities.

Por tanto, no sería preciso afirmar simplemente:

```text
Reminders depende siempre de Activities
```

La formulación observada es:

```text
Reminders consulta Activities únicamente cuando necesita validar
una referencia opcional a Activity.
```

El mecanismo definitivo de integración podrá revisarse posteriormente en la
evidencia específica de integración.

---

### Identity

Reminders utiliza `userId`.

Android además consulta información de la sesión activa en procesos como:

- entrega
- reprogramación
- restauración

### HECHO DEL REPOSITORIO

El código inspeccionado demuestra el uso de `userId` y de información de sesión
en determinadas operaciones.

Esto no demuestra que Reminders posea:

- autenticación
- credenciales
- registro de usuarios
- login
- reglas internas de Identity

---

### Focus

No se observó en el código inspeccionado durante esta revisión una dependencia
directa entre Reminders y Focus necesaria para el ciclo funcional del Reminder.

Esto no demuestra la inexistencia de cualquier relación en todo el repositorio.

**Clasificación:** `NO OBSERVADO EN EL CÓDIGO INSPECCIONADO / POR VERIFICAR`.

---

### Progress

No se observó en el código inspeccionado durante esta revisión que Progress
controle estados o reglas internas de Reminder ni una dependencia directa
necesaria para su ciclo funcional.

Esto no demuestra la inexistencia de cualquier relación en todo el repositorio.

**Clasificación:** `NO OBSERVADO EN EL CÓDIGO INSPECCIONADO / POR VERIFICAR`.

---

## 7. Qué explícitamente no pertenece

### Responsabilidades externas

Según la decisión de modelado adoptada, Reminders no protege:

- ciclo de vida de Activity
- Category
- Subtask
- autenticación
- credenciales
- ciclo interno de Identity
- sesiones Pomodoro
- cálculo de rachas
- interpretación de estadísticas de Progress

Una referencia opcional mediante `activityId` no convierte Activity en propiedad
de Reminders.

---

### Infraestructura Android

Los siguientes elementos no se consideran parte del lenguaje funcional del
bounded context:

- `AlarmManager`
- `PendingIntent`
- `BroadcastReceiver`
- `ReminderReceiver`
- `BootReceiver`
- `NotificationManager`

Tampoco se consideran estados funcionales del Reminder:

- `ScheduledExact`
- `ScheduledInexact`
- `InvalidTime`
- `Error`

### INFERENCIA DE MODELADO

La infraestructura Android materializa técnicamente la intención conservada por
el Reminder, pero no define su semántica funcional.

La frontera de dominio puede mantenerse aunque el mecanismo técnico de entrega
sea reemplazado por otro.

Por ejemplo, conceptualmente podría cambiar:

```text
AlarmManager
```

por otro mecanismo técnico sin que necesariamente desaparezcan conceptos como:

```text
Reminder
SCHEDULED
DELIVERED
CANCELLED
momento programado
```

---

## 8. Tensiones y contradicciones

### Validación temporal: backend vs Android

En Android se observa explícitamente que el momento debe estar en el futuro.

Tanto `ReminderRepository` como `ReminderScheduler` comprueban el valor de
`triggerAtMillis` respecto a `System.currentTimeMillis()`.

En el código de backend inspeccionado durante esta revisión no se ha demostrado
la misma precondición de forma explícita.

Por tanto:

```text
Android
triggerAtMillis > now
→ demostrado

Backend
triggerAtMillis > now
→ no demostrado todavía
```

No se concluye automáticamente que el backend tenga un defecto.

Puede tratarse de:

- una diferencia intencional
- validación realizada en otra capa todavía no inspeccionada
- una regla incompleta
- una inconsistencia entre plataformas

**Clasificación:** `TENSIÓN DOCUMENTADA`.

---

### Precondición de estado: backend vs Android

En Android, las operaciones locales para marcar un Reminder como:

- `DELIVERED`
- `CANCELLED`

incluyen condición:

```text
status = 'SCHEDULED'
```

En `ReminderService` del backend inspeccionado, los métodos:

- `markDelivered(...)`
- `cancel(...)`

localizan el Reminder y asignan directamente el nuevo estado.

No se observa en el fragmento revisado una validación explícita equivalente sobre
el estado previo.

Por tanto, la correspondencia completa del ciclo de vida entre Android y backend
no está cerrada.

**Clasificación:** `TENSIÓN DOCUMENTADA`.

---

### Ciclo funcional vs resultado técnico

Los estados:

```text
SCHEDULED
DELIVERED
CANCELLED
```

representan el ciclo funcional persistido del Reminder.

Los resultados:

```text
ScheduledExact
ScheduledInexact
InvalidTime
Error
```

representan el resultado técnico de intentar programar el aviso mediante
Android.

No deben mezclarse en un único diagrama de estados de dominio.

**Clasificación:** `DISTINCIÓN SEMÁNTICA DOCUMENTADA`.

---

### Estado persistido vs programación física

### HECHO DEL REPOSITORIO

La persistencia de un Reminder y la programación técnica de Android ocurren en
fases diferenciables.

`ReminderScheduler` puede producir los resultados:

- `ScheduledExact`
- `ScheduledInexact`
- `InvalidTime`
- `Error`

### INFERENCIA

A partir de esa separación es posible plantear que:

```text
Reminder persistido como SCHEDULED
```

y:

```text
alarma técnica correctamente registrada en Android
```

no sean necesariamente el mismo hecho.

No se afirma que dicha divergencia ocurra siempre ni que exista actualmente en
todos los flujos.

Queda pendiente determinar bajo qué condiciones concretas pueden divergir y cómo
se realiza la reconciliación.

**Clasificación:** `INFERENCIA / TENSIÓN DOCUMENTADA`.

---

### Reinicio del dispositivo

Android debe recuperar y volver a programar Reminders futuros después de un
reinicio.

Esto introduce una dependencia técnica entre:

- estado persistido
- sesión activa
- tiempo actual
- capacidad de programación del sistema operativo

### INFERENCIA

La necesidad de reprogramación muestra que el estado funcional del Reminder y el
estado del mecanismo técnico de scheduling tienen ciclos de vida relacionados,
pero no idénticos.

**Clasificación:** `TENSIÓN DE IMPLEMENTACIÓN`.

---

### Identidad activa durante entrega

`ReminderReceiver` comprueba información relacionada con el usuario activo antes
de continuar con la entrega.

Queda pendiente determinar la semántica funcional esperada si:

- el Reminder pertenece a un usuario diferente al usuario actualmente activo
- no existe sesión activa
- el Reminder vence mientras el usuario no está autenticado en la aplicación

**Clasificación:** `INFORMACIÓN FALTANTE`.

---

## 9. Información faltante

### Preguntas de dominio

Queda pendiente determinar:

1. si un Reminder solo puede pasar a `DELIVERED` desde `SCHEDULED`
2. si un Reminder solo puede pasar a `CANCELLED` desde `SCHEDULED`
3. comportamiento esperado si una Activity asociada es eliminada después de
   crear el Reminder
4. comportamiento esperado si la Activity asociada pasa a `COMPLETED`
5. si un Reminder entregado puede volver a programarse
6. si un Reminder cancelado puede reactivarse
7. si existen reglas adicionales sobre título, mensaje o contenido del Reminder

### Preguntas de integración e implementación

Queda pendiente verificar o decidir:

1. si backend valida explícitamente que `triggerAtMillis` esté en el futuro
2. correspondencia completa entre ciclo de vida backend y Android
3. qué ocurre si la persistencia queda en `SCHEDULED` pero la programación técnica
   no se realiza correctamente
4. estrategia de reconciliación después de `InvalidTime` o `Error`
5. cuándo exactamente debe marcarse un Reminder como `DELIVERED`
6. qué ocurre si la notificación no puede mostrarse por permisos del sistema
7. qué ocurre si el dispositivo está apagado durante `triggerAtMillis`
8. política temporal y de zona horaria para interpretar el momento programado
9. comportamiento si el usuario no está autenticado en el momento de entrega
10. existencia de eventos explícitos de integración producidos por Reminders
11. mecanismo definitivo de integración con Activities
12. responsabilidad definitiva de las preferencias globales de notificaciones
13. comportamiento completo de recuperación después de reinicios o cambios del
    sistema
14. existencia de relaciones adicionales con Focus o Progress fuera del código
    inspeccionado en esta revisión

---

## 10. Estado epistemológico

| Elemento | Estado |
|---|---|
| Existencia de `Reminder` | `HECHO DEL REPOSITORIO` |
| `activityId` opcional en contrato de creación | `HECHO DEL REPOSITORIO` |
| Creación permitida sin Activity | `HECHO DEL REPOSITORIO` |
| Uso de `triggerAtMillis` | `HECHO DEL REPOSITORIO` |
| Estado inicial `SCHEDULED` | `HECHO DEL REPOSITORIO` |
| Estado `DELIVERED` | `HECHO DEL REPOSITORIO` |
| Estado `CANCELLED` | `HECHO DEL REPOSITORIO` |
| Uso condicional de `ActivityLookup` | `HECHO DEL REPOSITORIO` |
| Validación de tiempo futuro en Android | `HECHO DEL REPOSITORIO` |
| Validación equivalente en backend | `INFORMACIÓN FALTANTE` |
| `SCHEDULED → DELIVERED` condicionado en Android | `HECHO DEL REPOSITORIO` |
| `SCHEDULED → CANCELLED` condicionado en Android | `HECHO DEL REPOSITORIO` |
| Misma precondición de estado en backend | `INFORMACIÓN FALTANTE / TENSIÓN DOCUMENTADA` |
| Reprogramación tras reinicio | `HECHO DEL REPOSITORIO` |
| `ScheduledExact` como resultado técnico | `HECHO DEL REPOSITORIO` |
| `ScheduledInexact` como resultado técnico | `HECHO DEL REPOSITORIO` |
| `InvalidTime` como resultado técnico | `HECHO DEL REPOSITORIO` |
| `Error` como resultado técnico | `HECHO DEL REPOSITORIO` |
| Posible divergencia `SCHEDULED` / scheduling físico | `INFERENCIA / TENSIÓN DOCUMENTADA` |
| Separación dominio / infraestructura Android | `INFERENCIA DE MODELADO` |
| Autonomía semántica respecto de Activities | `INFERENCIA DE MODELADO` |
| Relación directa con Focus | `NO OBSERVADO / POR VERIFICAR` |
| Relación directa con Progress | `NO OBSERVADO / POR VERIFICAR` |
| Correspondencia completa backend ↔ Android | `INFORMACIÓN FALTANTE` |
| Eventos explícitos de integración | `INFORMACIÓN FALTANTE` |
| Mecanismo definitivo de integración con Activities | `NO DECIDIDO` |
| Reminders como bounded context | `DECISIÓN DEL EQUIPO` |

---

## 11. Decisión sobre la frontera

### Evidencia considerada

La decisión considera que:

- Reminder posee estados y reglas propias
- el Reminder conserva un momento programado mediante `triggerAtMillis`
- puede crearse sin una Activity asociada
- la consulta a Activities solo ocurre cuando existe `activityId`
- sus reglas internas no dependen del ciclo de vida completo de Activity
- posee lenguaje propio alrededor de programación, entrega y cancelación
- Android implementa mecanismos técnicos para materializar la intención del
  Reminder
- los mecanismos Android pueden distinguirse del ciclo funcional persistido
- Activities no controla directamente los estados internos de Reminder en la
  implementación inspeccionada
- Identity proporciona contexto de usuario, pero no controla el ciclo funcional
  del Reminder

### Decisión del equipo

El equipo decide modelar `Reminders` como un bounded context independiente.

La decisión no se basa únicamente en la existencia de un paquete llamado:

`reminders`

Se basa en:

- reglas propias
- lenguaje propio
- ciclo de vida propio
- asociación opcional con Activity
- posibilidad de existir sin Activity
- capacidad de evolución independiente
- responsabilidad funcional diferenciable de Activities, Focus, Progress e
  Identity

**Clasificación:** `DECISIÓN DEL EQUIPO`.

---

### Responsabilidad protegida

Reminders protege la capacidad de:

**definir, programar y gestionar el ciclo de vida de los recordatorios del
usuario, conservando la intención de generar un aviso en un momento determinado
y permitiendo opcionalmente asociarlo a una Activity.**

En esta definición:

**programar** significa establecer el momento en que debe ocurrir el Reminder y
mantener esa intención dentro de su ciclo funcional.

No significa ejecutar directamente una tecnología concreta de scheduling.

---

### Qué queda fuera de esta frontera

No pertenece a Reminders:

- gestión del ciclo de vida de Activity
- Category
- Subtask
- autenticación
- credenciales
- reglas internas de Identity
- sesiones Pomodoro
- cálculo de rachas
- estadísticas de Progress

Tampoco forman parte del lenguaje funcional de la frontera:

- `AlarmManager`
- `PendingIntent`
- `BroadcastReceiver`
- `ReminderReceiver`
- `BootReceiver`
- `NotificationManager`

Estos elementos pertenecen al mecanismo técnico mediante el cual Android
materializa el Reminder.

Los resultados:

- `ScheduledExact`
- `ScheduledInexact`
- `InvalidTime`
- `Error`

tampoco se consideran estados funcionales del Reminder.

---

### Dependencias relevantes

#### Activities

Cuando existe `activityId`, Reminders consulta Activities mediante:

`ActivityLookup`

La relación observada es:

```text
Reminders
    |
    | ActivityLookup
    v
Activities
```

La dependencia es condicional.

Un Reminder sin `activityId` no necesita consultar Activities para crearse.

La referencia a Activity no transfiere ownership de Activity a Reminders.

El mecanismo definitivo de integración se revisará posteriormente en la
evidencia de integración.

---

#### Identity

Reminders utiliza `userId`.

Android también consulta información sobre la sesión activa para determinadas
operaciones técnicas de entrega y recuperación.

Esto no transfiere a Reminders ownership sobre:

- autenticación
- login
- credenciales
- ciclo de vida del usuario

---

#### Focus y Progress

No se observaron durante esta revisión dependencias directas necesarias con
Focus o Progress para controlar el ciclo funcional de Reminder.

Esta observación está limitada al código inspeccionado y no se utiliza como prueba
de inexistencia absoluta de relaciones en el repositorio.

---

#### Infraestructura Android

Reminders necesita que una infraestructura concreta materialice físicamente el
aviso en la aplicación Android actual.

La implementación utiliza componentes como:

- `ReminderScheduler`
- `ReminderReceiver`
- `BootReceiver`
- `AlarmManager`
- `NotificationManager`

Esta relación técnica no cambia la responsabilidad protegida por el bounded
context.

---

### Condiciones de revisión

Como **decisión del equipo**, la frontera deberá reconsiderarse si en el futuro
ocurre alguno de los siguientes cambios:

1. Reminder deja de poder existir de forma autónoma y pasa a depender
   obligatoriamente del ciclo de vida de otra capacidad
2. otra responsabilidad pasa a controlar directamente la creación, estados,
   transiciones, entrega o cancelación del Reminder
3. desaparecen los estados, lenguaje y reglas propias de Reminder
4. la responsabilidad de Reminders se reduce únicamente a ejecutar una operación
   técnica de notificación
5. `activityId` deja de ser opcional y las reglas de Reminder pasan a estar
   determinadas completamente por las reglas internas de Activity
6. aparece una nueva capacidad de negocio que absorba semánticamente el ciclo
   completo del Reminder

Mientras estas condiciones no se presenten, **como decisión arquitectónica del
equipo**, se mantiene Reminders como frontera independiente.

Esta decisión no implica que Reminders deba desplegarse como microservicio.

La frontera de dominio y la unidad física de despliegue son decisiones
arquitectónicas distintas.

---

### Estado de frontera

`Reminders como bounded context = DECIDIDO`

**Decisión del equipo:** bounded context independiente responsable del ciclo
funcional de los recordatorios del usuario.
