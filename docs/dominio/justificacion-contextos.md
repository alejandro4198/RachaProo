# Justificación de bounded contexts

<!-- PISTA2:IDENTITY-DECISION:BEGIN -->

## Decisión posterior — inclusión de Identity en el Context Map

**Estado:** `DECIDIDO`

Identity se incorpora al Context Map vigente de RachaPro.

### Justificación

La decisión se adopta porque:

- Identity posee una responsabilidad diferenciada sobre registro, autenticación y datos de usuario;
- existe una integración intercontexto materializada entre Identity y Activities;
- ADR-003 formalizó el flujo post-registro mediante `UserRegisteredV1`;
- omitir Identity del mapa vigente dejaría sin representar una dependencia arquitectónica materializada.

### Convención de dirección

En el Context Map vigente:

`A → B`

significa que A consume, referencia o depende conceptualmente de información o capacidad de B.

Por tanto, la relación conceptual se representa como:

`Activities → Identity`

Esto no debe confundirse con la dirección técnica de publicación del evento:

- Identity publica `UserRegisteredV1`;
- Activities consume `UserRegisteredV1`.

La inclusión de Identity en el Context Map no implica por sí sola:

- broker externo;
- outbox;
- retry;
- replay;
- despliegue independiente;
- microservicios;
- Shared Kernel;
- ACL;
- patrón Customer/Supplier.

Esas decisiones requieren evidencia y cierre independiente.

<!-- PISTA2:IDENTITY-DECISION:END -->


## 1. Propósito del documento

Este documento justifica las fronteras de bounded context definidas para RachaPro
a partir del contraste entre:

- las responsabilidades conceptuales identificadas;
- la estructura actual observada en el código;
- las relaciones y dependencias existentes entre las partes del sistema;
- el C4 as-is y la trazabilidad hacia código.

Su objetivo no es redefinir los contextos ni proponer nuevos bounded contexts,
sino explicar por qué las fronteras decididas por el equipo resultan coherentes
con la evidencia disponible.

Las decisiones aquí registradas corresponden al modelado del dominio y no
implican, por sí solas, decisiones de despliegue, protocolos o mecanismos
definitivos de integración.

---

## 2. Criterio de análisis

Para cada bounded context se contrastan tres niveles:

```text
responsabilidad conceptual
        ↕
estructura actual
        ↕
relaciones entre partes
```

La responsabilidad conceptual describe la capacidad que el equipo decidió
proteger.

La estructura actual muestra cómo esa capacidad aparece hoy distribuida entre
paquetes, servicios, repositorios, entidades, ViewModels u otros componentes.

Las relaciones observadas muestran qué información consume o referencia cada
contexto y qué dependencias existen con otras capacidades.

La frontera no se deriva automáticamente de la estructura física del código.

Por tanto:

```text
paquete
≠
bounded context

módulo técnico
≠
bounded context

dependencia de código
≠
transferencia de ownership
```

El análisis distingue además entre:

- `HECHO DEL REPOSITORIO`;
- `INFERENCIA DE MODELADO`;
- `DECISIÓN DEL EQUIPO`;
- `INFORMACIÓN FALTANTE`;
- `TENSIÓN DOCUMENTADA` cuando la evidencia muestra diferencias o ambigüedades
  que todavía no deben elevarse a una decisión de dominio.

El C4 de Semana 8 se utiliza como evidencia del estado estructural as-is de esa
fase.

Los cambios introducidos posteriormente durante M5 se tratan como evolución del
sistema y no como errores retroactivos del C4 de Semana 8.

### Fuentes de evidencia

La justificación se apoya en artefactos elaborados durante la inspección del
repositorio y en documentación arquitectónica previa.

Las principales fuentes utilizadas son:

- `docs/dominio/evidencia/activities.md`;
- `docs/dominio/evidencia/focus.md`;
- `docs/dominio/evidencia/reminders.md`;
- `docs/dominio/evidencia/progress.md`;
- `docs/dominio/subdominios.md`;
- `docs/dominio/responsabilidades-contextos.md`;
- `docs/dominio/context-map.puml`;
- `docs/semana8/matriz-trazabilidad.md`;
- documentación C4 disponible en `docs/semana8/c4/`.

Estas fuentes cumplen funciones diferentes:

```text
evidencia/*
→ observaciones y trazabilidad hacia código

C4 Semana 8
→ baseline estructural as-is

matriz de trazabilidad
→ correspondencia entre arquitectura y código

subdominios.md
→ decisiones de clasificación y fronteras

responsabilidades-contextos.md
→ ownership, consumo y responsabilidades

context-map.puml
→ síntesis de las relaciones entre bounded contexts
```

El historial Git se utiliza adicionalmente para distinguir el baseline de
Semana 8 de cambios introducidos posteriormente durante M5.

Por tanto, una observación correspondiente al estado actual del repositorio no
se proyecta automáticamente hacia atrás como evidencia del estado existente en
Semana 8.

---

## 3. Activities

### Responsabilidad conceptual

Activities protege la capacidad de planificar, organizar, descomponer y dar
seguimiento a las actividades y tareas del usuario, incluyendo su clasificación
y su ciclo de vida.

Esta formulación corresponde a una `DECISIÓN DEL EQUIPO`.

---

### Estructura actual observada

**HECHO DEL REPOSITORIO:** la capacidad aparece representada mediante conceptos y
componentes relacionados con:

- `Activity`;
- `Category`;
- `Subtask`;
- estados de Activity;
- prioridad;
- vencimiento;
- completitud;
- eliminación lógica de Activity;
- servicios y repositorios asociados a la gestión de estos conceptos.

La estructura actual también contiene un contrato `ActivityLookup`, expuesto
desde Activities para permitir que otras capacidades validen referencias a una
Activity sin asumir directamente las reglas internas de su ciclo de vida.

El contrato observado contiene:

```text
existsActiveActivityForUser(activityId, userId)
```

**HECHO DEL REPOSITORIO:** la implementación observada comprueba que la Activity:

- exista;
- pertenezca al `userId` indicado;
- no se encuentre eliminada lógicamente.

No se observa una validación explícita del estado funcional de la Activity.

Por tanto:

```text
TENSIÓN / PRECISIÓN

el nombre "active" del contrato
≠
evidencia de que la Activity deba estar en PENDING

el nombre "active" del contrato
≠
evidencia de que COMPLETED u OVERDUE sean excluidos
```

El término `active` presente en el nombre del contrato se conserva como una
tensión semántica y no se eleva a una regla funcional que el código inspeccionado
no demuestra.

La existencia de paquetes, entidades, servicios o repositorios asociados a
Activities constituye evidencia de la estructura actual, pero no es por sí sola
la razón para delimitar el bounded context.

---

### Relaciones observadas

**HECHO DEL REPOSITORIO:** Activities participa en relaciones con Focus,
Reminders y Progress, aunque la semántica de esas relaciones es diferente.

#### Focus → Activities

Focus puede mantener una referencia opcional a `Activity`.

Cuando existe `activityId`, Focus utiliza `ActivityLookup` para validar la
referencia.

La relación observada corresponde a:

```text
Focus --> Activities
→ referencia opcional a Activity
→ validación mediante ActivityLookup
```

En la evidencia inspeccionada no se observó que Focus controle, mediante esta
relación, el ciclo de vida de Activity.

#### Reminders → Activities

Reminders puede mantener una referencia opcional a `Activity`.

Cuando existe `activityId`, Reminders utiliza `ActivityLookup` para validar la
referencia.

La relación observada corresponde a:

```text
Reminders --> Activities
→ referencia opcional a Activity
→ validación mediante ActivityLookup
```

En la evidencia inspeccionada no se observó que Reminders adquiera ownership
sobre Activity mediante esta dependencia.

#### Progress → Activities

Progress consume información producida por Activities relacionada con
completitud.

Entre la información observada se encuentran:

- días de completitud;
- cantidades de Activities completadas;
- hechos y agregados de completitud utilizados para construir métricas.

Esta relación es diferente de las anteriores.

En la evidencia inspeccionada no se observó que Progress utilice
`ActivityLookup` como mecanismo para validar una referencia individual a
Activity.

Consume resultados producidos por Activities y los interpreta dentro de su
propia responsabilidad.

Por tanto:

```text
Progress --> Activities
→ consumo de días de completitud
→ consumo de cantidades
→ consumo de agregados de completitud
```

Las tres relaciones representan consumo o referencia de información y no
transferencia de ownership.

---

### Justificación de frontera

La frontera de Activities se justifica mediante la siguiente secuencia:

```text
HECHO DEL REPOSITORIO
→ Activities concentra conceptos y reglas relacionados con
  Activity, Category y Subtask.

HECHO DEL REPOSITORIO
→ existe ActivityLookup como contrato observado para permitir
  validaciones desde otras capacidades.

HECHO DEL REPOSITORIO
→ Focus y Reminders referencian Activity sin que se haya
  observado control sobre su ciclo de vida.

HECHO DEL REPOSITORIO
→ Progress consume hechos y agregados de completitud
  sin que se haya observado ownership sobre Activity.

INFERENCIA DE MODELADO
→ esas reglas, conceptos y relaciones forman una
  responsabilidad funcional coherente y diferenciable.

DECISIÓN DEL EQUIPO
→ Activities se mantiene como bounded context independiente.
```

Activities concentra el significado y las reglas relacionadas con la
planificación, organización, clasificación, descomposición y ciclo de vida de
`Activity`, `Category` y `Subtask`.

Las relaciones con Focus y Reminders muestran que otras capacidades pueden
necesitar referenciar Activity y validar su existencia sin que en la evidencia
inspeccionada aparezca apropiación de sus reglas internas.

La relación con Progress muestra otro tipo de dependencia: Progress consume
hechos producidos por Activities y les asigna significado dentro de su propia
responsabilidad de progreso.

Por tanto:

```text
referenciar Activity
≠
poseer Activity

validar una referencia
≠
controlar el ciclo de vida de Activity

consumir hechos de completitud
≠
poseer el concepto fuente
```

La frontera tampoco se establece porque exista físicamente un paquete
`activities`.

La estructura del código funciona como evidencia, mientras que la delimitación
del bounded context corresponde a una decisión de modelado basada en
responsabilidad, reglas y ownership.

La posibilidad de que Focus, Reminders o Progress evolucionen internamente sin
modificar necesariamente el ciclo de vida de Activity, siempre que se mantengan
los datos o contratos requeridos, corresponde a una `INFERENCIA DE MODELADO`.

Por tanto:

```text
Activities como bounded context = DECIDIDO
```

**Clasificación:** `DECISIÓN DEL EQUIPO`.

---

## 4. Focus

### Responsabilidad conceptual

Focus protege la capacidad de gestionar el ciclo de vida de las sesiones
Pomodoro del usuario, incluyendo periodos de concentración y descanso, sus
tipos, estados y transiciones.

Esta formulación corresponde a una `DECISIÓN DEL EQUIPO`.

---

### Estructura actual observada

**HECHO DEL REPOSITORIO:** la capacidad aparece representada mediante conceptos y
componentes relacionados con:

- `PomodoroSession`;
- tipos de sesión `FOCUS`, `SHORT_BREAK` y `LONG_BREAK`;
- estados `RUNNING`, `PAUSED`, `COMPLETED` y `CANCELLED`;
- inicio;
- pausa;
- reanudación;
- finalización;
- cancelación;
- duración asociada a la sesión;
- servicios y repositorios relacionados con sesiones Pomodoro.

`PomodoroSessionService` es uno de los componentes observados donde aparecen
operaciones asociadas al ciclo de vida de `PomodoroSession`.

Esta observación no significa que todo el dominio de Focus esté físicamente
concentrado en ese servicio.

La entidad `PomodoroSession` puede almacenar un `activityId` opcional.

Además, el contrato de creación permite que `activityId` sea nulo.

Por tanto:

```text
HECHO DEL REPOSITORIO

activityId == null
→ la sesión puede crearse sin Activity asociada

activityId != null
→ PomodoroSessionService consulta ActivityLookup
→ valida la referencia antes de crear la sesión
```

Esta dependencia no convierte a `Activity` en un concepto poseído por Focus.

La existencia de clases, paquetes, repositorios, servicios o pantallas
relacionadas con Pomodoro constituye evidencia estructural, pero no define por
sí sola la frontera.

---

### Tensión / precisión sobre duración

La duración forma parte de la información asociada a una `PomodoroSession`.

Sin embargo, la evidencia observada en las métricas de Progress utiliza
`plannedDurationSeconds`.

Por tanto:

```text
duración asociada a PomodoroSession
≠
tiempo efectivo de concentración demostrado
```

No se interpreta automáticamente toda duración almacenada o agregada como
tiempo real efectivamente concentrado por el usuario.

Esta precisión no modifica el ownership de Focus sobre la sesión.

---

### Relaciones observadas

**HECHO DEL REPOSITORIO:** Focus participa en relaciones con Activities y
Progress, con semánticas distintas.

#### Focus → Activities

Focus puede mantener una referencia opcional a `Activity`.

Cuando existe `activityId`, utiliza `ActivityLookup` para validar la referencia.

```text
Focus --> Activities
→ referencia opcional a Activity
→ validación mediante ActivityLookup
```

En la evidencia inspeccionada no se observó transferencia de ownership sobre
`Activity` hacia Focus.

Tampoco se observó que Focus controle, mediante esta relación, el ciclo de vida
interno de Activity.

#### Progress → Focus

Progress consume información producida por Focus relacionada con sesiones
`FOCUS` completadas.

Entre la información observada se encuentran:

- días de sesiones `FOCUS` completadas;
- cantidades de sesiones `FOCUS` completadas;
- duración agregada utilizada en métricas;
- hechos utilizados para construir progreso y rachas.

Por tanto:

```text
Progress --> Focus
→ consumo de hechos y agregados de sesiones FOCUS completadas
```

En la evidencia inspeccionada no se observó que Progress controle los estados ni
las transiciones de `PomodoroSession`.

---

### Justificación de frontera

La frontera de Focus se justifica mediante la siguiente secuencia:

```text
HECHO DEL REPOSITORIO
→ Focus contiene conceptos y reglas asociados con
  PomodoroSession, tipos, estados y transiciones.

HECHO DEL REPOSITORIO
→ una PomodoroSession puede existir sin Activity asociada.

HECHO DEL REPOSITORIO
→ cuando existe activityId, Focus valida la referencia
  mediante ActivityLookup.

HECHO DEL REPOSITORIO
→ Progress consume hechos de sesiones FOCUS completadas
  sin que se haya observado ownership sobre PomodoroSession.

INFERENCIA DE MODELADO
→ estos conceptos y reglas forman una responsabilidad
  coherente alrededor del ciclo de vida de sesiones Pomodoro.

DECISIÓN DEL EQUIPO
→ Focus se mantiene como bounded context independiente.
```

Focus concentra el significado y las reglas relacionadas con la creación y
evolución de una `PomodoroSession`.

Activities participa únicamente cuando existe una referencia opcional que debe
ser validada, pero esa dependencia no convierte el ciclo de vida de Activity en
responsabilidad de Focus.

Progress consume resultados producidos por Focus y los interpreta como señales
de avance, pero en la evidencia inspeccionada no se observó que adquiera
ownership sobre la sesión ni sobre sus estados internos.

Por tanto:

```text
referenciar Activity
≠
poseer Activity

validar activityId
≠
controlar el ciclo de vida de Activity

consumir hechos de PomodoroSession
≠
poseer PomodoroSession
```

La posibilidad de que las reglas internas de Focus evolucionen sin exigir
necesariamente cambios en el ciclo de vida interno de Activity o en las reglas
de Progress, siempre que se mantengan los datos o contratos necesarios,
corresponde a una `INFERENCIA DE MODELADO`.

La frontera tampoco se define simplemente porque exista un paquete o módulo
relacionado con Focus o Pomodoro.

La estructura física aporta evidencia; la frontera se decide a partir de la
responsabilidad, las reglas y el ownership.

Por tanto:

```text
Focus como bounded context = DECIDIDO
```

**Clasificación:** `DECISIÓN DEL EQUIPO`.

---

## 5. Reminders

### Responsabilidad conceptual

Reminders protege la capacidad de definir, programar y gestionar el ciclo de
vida de los recordatorios del usuario, conservando la intención de generar un
aviso en un momento determinado y permitiendo opcionalmente asociarlo a una
Activity.

En esta formulación, **programar** significa establecer y conservar el momento
funcional en que debe ocurrir el Reminder.

No significa que mecanismos técnicos como `AlarmManager` formen parte de la
responsabilidad conceptual del bounded context.

Esta formulación corresponde a una `DECISIÓN DEL EQUIPO`.

---

### Estructura actual observada

**HECHO DEL REPOSITORIO:** la capacidad aparece representada mediante conceptos y
componentes relacionados con:

- `Reminder`;
- momento programado;
- `triggerAtMillis`;
- estados funcionales `SCHEDULED`, `DELIVERED` y `CANCELLED`;
- creación;
- entrega;
- cancelación;
- asociación opcional con `Activity`;
- servicios y repositorios relacionados con recordatorios.

La creación de un Reminder admite un `activityId` opcional.

Cuando existe `activityId`, `ReminderService` utiliza `ActivityLookup` para
validar la referencia antes de crear el Reminder.

Por tanto:

```text
HECHO DEL REPOSITORIO

activityId == null
→ no se requiere consulta a Activities

activityId != null
→ se consulta ActivityLookup
→ se valida la referencia a Activity
```

La implementación observada de `ActivityLookup` comprueba que la Activity:

- exista;
- pertenezca al `userId` indicado;
- no se encuentre eliminada lógicamente.

No se interpreta esta validación como evidencia de que la Activity deba estar
necesariamente en `PENDING` ni de que se excluyan estados como `COMPLETED` u
`OVERDUE`.

Esta dependencia no implica, por sí sola, transferencia de ownership sobre
Activity hacia Reminders.

---

### Ciclo funcional e infraestructura técnica

La estructura observada contiene dos niveles distintos.

#### Ciclo funcional

**HECHO DEL REPOSITORIO:** el Reminder utiliza estados funcionales como:

```text
SCHEDULED
DELIVERED
CANCELLED
```

Estos estados representan la evolución funcional del Reminder.

#### Infraestructura / ejecución

**HECHO DEL REPOSITORIO:** en Android se observaron componentes técnicos como:

- `AlarmManager`;
- `PendingIntent`;
- `ReminderScheduler`;
- `ReminderReceiver`;
- `BootReceiver`;
- `NotificationManager`.

También se observaron resultados técnicos como:

- `ScheduledExact`;
- `ScheduledInexact`;
- `InvalidTime`;
- `Error`.

Estos componentes y resultados participan en la ejecución técnica del
comportamiento de recordatorio.

Por tanto:

```text
ciclo funcional del Reminder
SCHEDULED / DELIVERED / CANCELLED

≠

infraestructura y ejecución Android
AlarmManager / ReminderScheduler / Receivers / NotificationManager

≠

resultados técnicos
ScheduledExact / ScheduledInexact / InvalidTime / Error
```

**INFERENCIA DE MODELADO:** estos componentes corresponden a infraestructura de
ejecución y no se modelan como un bounded context adicional.

---

### Tensiones observadas

#### Precondiciones de transición

**TENSIÓN DOCUMENTADA:** Android y backend no muestran exactamente las mismas
precondiciones para modificar el estado de un Reminder.

En Android, la evidencia inspeccionada condiciona determinadas transiciones
desde el estado `SCHEDULED`.

En el backend inspeccionado, las operaciones correspondientes asignan
`DELIVERED` o `CANCELLED`, pero no quedó demostrada una validación equivalente
del estado previo.

Por tanto:

```text
HECHO DEL REPOSITORIO
→ existen estados y operaciones funcionales del Reminder

TENSIÓN DOCUMENTADA
→ las precondiciones observadas no son idénticas
  entre Android y backend
```

Esta diferencia se conserva como tensión de consistencia y no como argumento
para redefinir la frontera.

#### Validación temporal

**HECHO DEL REPOSITORIO:** en Android se observó una validación explícita para
evitar programar recordatorios cuando `triggerAtMillis` no representa un momento
futuro.

**INFORMACIÓN FALTANTE:** no quedó demostrada una validación temporal equivalente
en el backend inspeccionado.

Por tanto:

```text
Android
→ valida explícitamente tiempo futuro

Backend inspeccionado
→ validación equivalente no demostrada
```

No se asume que ambas implementaciones compartan actualmente exactamente la
misma política temporal.

---

### Relaciones observadas

#### Reminders → Activities

**HECHO DEL REPOSITORIO:** Reminders puede mantener una referencia opcional a
`Activity`.

Cuando existe `activityId`:

- utiliza `ActivityLookup`;
- valida que la Activity exista, pertenezca al usuario y no esté eliminada;
- conserva `activityId` como asociación opcional.

Por tanto:

```text
Reminders --> Activities
→ referencia opcional a Activity
→ validación mediante ActivityLookup
```

En la evidencia inspeccionada no se observó que esta relación otorgue a
Reminders control sobre:

- el ciclo de vida de Activity;
- sus estados;
- sus reglas de completitud;
- sus reglas de eliminación.

#### Infraestructura Android → Reminders

**HECHO DEL REPOSITORIO:** en Android se observaron componentes técnicos como
`ReminderScheduler`, `AlarmManager`, `ReminderReceiver`, `BootReceiver` y
`NotificationManager`, que participan en la ejecución del comportamiento de
recordatorio.

**INFERENCIA DE MODELADO:** estos componentes materializan técnicamente la
ejecución requerida por Reminders, pero no constituyen una frontera de dominio
adicional.

Por tanto:

```text
infraestructura Android
→ participa en la ejecución técnica del Reminder

infraestructura Android
≠
bounded context adicional
```

#### Contexto de usuario / Identity

Reminders utiliza `userId` para asociar los recordatorios al usuario
correspondiente.

En la evidencia inspeccionada, el uso de `userId` no demuestra ownership sobre:

- autenticación;
- credenciales;
- ciclo de vida interno de Identity.

La presencia de este dato contextual tampoco constituye por sí sola evidencia
para incorporar Identity dentro de la frontera de Reminders.

---

### Justificación de frontera

La frontera de Reminders se justifica mediante la siguiente secuencia:

```text
HECHO DEL REPOSITORIO
→ Reminder tiene un momento programado,
  estados funcionales y operaciones asociadas
  a su ciclo de vida.

HECHO DEL REPOSITORIO
→ un Reminder puede existir sin Activity asociada.

HECHO DEL REPOSITORIO
→ cuando existe activityId, Reminders valida
  la referencia mediante ActivityLookup.

HECHO DEL REPOSITORIO
→ Android contiene infraestructura técnica específica
  para ejecutar el comportamiento de recordatorio.

TENSIÓN DOCUMENTADA
→ backend y Android no muestran exactamente
  las mismas precondiciones de transición.

INFORMACIÓN FALTANTE
→ no se demostró una política temporal equivalente
  en backend a la observada en Android.

INFERENCIA DE MODELADO
→ el ciclo funcional del Reminder es distinguible
  del mecanismo técnico utilizado para ejecutarlo.

DECISIÓN DEL EQUIPO
→ Reminders se mantiene como bounded context independiente.
```

Reminders concentra el significado funcional del recordatorio: su existencia,
el momento en que debe ocurrir y su evolución entre estados funcionales.

La relación opcional con Activities permite validar una referencia sin que ello
implique transferencia de ownership sobre Activity.

La infraestructura Android participa en la ejecución técnica del comportamiento
requerido, pero la distinción entre dicha infraestructura y la responsabilidad
funcional de Reminder corresponde al plano de modelado.

Por tanto:

```text
referenciar Activity
≠
poseer Activity

programar el momento del Reminder
≠
ejecutar AlarmManager

estado funcional
≠
resultado técnico de scheduling

infraestructura Android
≠
bounded context Reminders
```

La posibilidad de modificar la infraestructura de scheduling o notificación sin
alterar necesariamente el significado funcional de Reminder corresponde a una
`INFERENCIA DE MODELADO`, no a un hecho literal del repositorio.

La frontera tampoco se justifica simplemente porque exista un paquete
`reminders`.

La estructura física aporta evidencia, mientras que la delimitación del bounded
context se fundamenta en responsabilidad, lenguaje, ciclo funcional y
ownership.

Por tanto:

```text
Reminders como bounded context = DECIDIDO
```

**Clasificación:** `DECISIÓN DEL EQUIPO`.

---

## 6. Progress

### Responsabilidad conceptual

Progress protege la capacidad de interpretar y agregar los resultados producidos
por otras capacidades del sistema para medir y representar el avance del usuario
mediante rachas y métricas de progreso.

Esta formulación corresponde a una `DECISIÓN DEL EQUIPO`.

Progress no produce originalmente los hechos de completitud de Activities o
Focus.

Su responsabilidad comienza cuando esos hechos son interpretados como señales de
avance.

---

### Estructura actual observada

**HECHO DEL REPOSITORIO:** la capacidad aparece representada mediante componentes
y reglas relacionadas con:

- racha actual;
- mejor racha;
- consecutividad;
- días de completitud;
- Activities completadas;
- sesiones `FOCUS` completadas;
- cantidades agregadas;
- métricas de progreso;
- cálculo de rachas mediante `StreakCalculator`.

En Android, `ProgressViewModel` depende explícitamente de:

- `ActivityRepository`;
- `PomodoroRepository`;
- `SessionManager`.

La presencia de `SessionManager` constituye evidencia estructural de que Progress
necesita contexto de usuario o sesión para operar.

No se utiliza esta dependencia como argumento central para justificar la
frontera de dominio.

La implementación observada combina información proveniente de Activities y
Focus para construir métricas y resultados derivados.

Por tanto:

```text
HECHO DEL REPOSITORIO

ProgressViewModel
→ consume información de ActivityRepository
→ consume información de PomodoroRepository
→ utiliza contexto de sesión mediante SessionManager
→ combina resultados
→ utiliza StreakCalculator
```

Estas dependencias demuestran consumo de información, pero no constituyen por sí
solas evidencia de transferencia de ownership sobre `Activity`,
`PomodoroSession` o Identity.

---

### Reglas semánticas de racha

**HECHO DEL REPOSITORIO:** `StreakCalculator` aplica reglas propias para
interpretar los días utilizados en el cálculo de rachas.

La implementación observada:

- descarta días futuros;
- elimina duplicados;
- ordena los días;
- calcula secuencias consecutivas;
- considera hoy o ayer para mantener la racha actual;
- calcula la mejor secuencia histórica.

Por tanto:

```text
hechos de completitud
→ entrada para Progress

reglas de StreakCalculator
→ interpretación de esos hechos
```

Estas reglas muestran que Progress no se limita a sumar o presentar datos.

`StreakCalculator` introduce semántica propia sobre los hechos recibidos para
producir resultados derivados como:

- racha actual;
- mejor racha;
- secuencias consecutivas.

Las reglas de racha no forman parte del ciclo de vida de `Activity` ni del ciclo
de vida de `PomodoroSession`.

Por tanto:

```text
producir un día de completitud
≠
interpretar su efecto sobre una racha
```

---

### Criterios de consulta y proyección

La implementación también contiene criterios de consulta como:

- `TODAY`;
- `WEEK`;
- `ALL`.

Estos elementos permiten organizar o proyectar información para diferentes
periodos.

Por tanto:

```text
TODAY / WEEK / ALL
→ criterios de consulta y proyección
```

Su existencia no se utiliza por sí sola como evidencia central de dominio ni
como argumento suficiente para justificar la frontera.

Esta distinción evita considerar automáticamente toda lógica presente en
`ProgressViewModel` como una regla profunda del bounded context.

---

### Relaciones observadas

**HECHO DEL REPOSITORIO:** Progress mantiene relaciones observables con
Activities, Focus y el contexto de usuario/sesión.

#### Progress → Activities

Progress consume información producida por Activities relacionada con
completitud.

Entre la información observada se encuentran:

- días de completitud;
- cantidades de Activities completadas;
- agregados relacionados con Activities completadas.

Por tanto:

```text
Progress --> Activities
→ consume días de completitud
→ consume cantidades de Activities completadas
→ consume agregados de completitud
```

Progress utiliza esos hechos para construir rachas y métricas.

En la evidencia inspeccionada no se observó que Progress controle mediante esta
relación:

- creación de Activity;
- clasificación;
- vencimiento;
- cambios de estado;
- eliminación;
- reglas internas de su ciclo de vida.

Por tanto:

```text
consumir hechos de Activity
≠
poseer Activity
```

#### Progress → Focus

Progress consume información producida por Focus relacionada con sesiones
`FOCUS` completadas.

Entre la información observada se encuentran:

- días con sesiones `FOCUS` completadas;
- cantidades de sesiones `FOCUS` completadas;
- duración agregada utilizada en métricas.

Por tanto:

```text
Progress --> Focus
→ consume días de sesiones FOCUS completadas
→ consume cantidades
→ consume duración agregada usada en métricas
```

En la evidencia inspeccionada no se observó que Progress controle mediante esta
relación:

- inicio de `PomodoroSession`;
- pausa;
- reanudación;
- finalización;
- cancelación;
- estados internos de la sesión.

Por tanto:

```text
consumir hechos de PomodoroSession
≠
poseer PomodoroSession
```

#### Progress → contexto de usuario / sesión

**HECHO DEL REPOSITORIO:** `ProgressViewModel` utiliza `SessionManager` para
obtener el contexto de usuario necesario para realizar sus consultas y cálculos.

Esta dependencia representa una necesidad estructural de contexto.

En la evidencia inspeccionada, su presencia no demuestra ownership sobre:

- autenticación;
- credenciales;
- sesión como responsabilidad funcional;
- ciclo de vida de Identity.

Por tanto:

```text
usar contexto de usuario
≠
poseer Identity
```

---

### Tensión / precisión sobre duración

**HECHO DEL REPOSITORIO:** determinadas métricas utilizadas por Progress se
construyen a partir de `plannedDurationSeconds`.

Por tanto:

```text
plannedDurationSeconds
≠
tiempo efectivo de concentración demostrado
```

La duración agregada consumida desde Focus no se interpreta automáticamente como
tiempo real efectivamente concentrado por el usuario.

Esta diferencia se mantiene como una `TENSIÓN / PRECISIÓN SEMÁNTICA`.

No modifica el ownership entre Focus y Progress.

---

### Achievement

**HECHO DEL REPOSITORIO:** Achievement utiliza información relacionada con
Activities completadas, sesiones `FOCUS` completadas y racha.

Sin embargo, su ownership no forma parte de la decisión de frontera de Progress.

Por tanto:

```text
Achievement ownership = NO DECIDIDO
```

El análisis de la evidencia disponible y de la decisión pendiente se desarrolla
en §7.

---

### Justificación de frontera

La frontera de Progress se justifica mediante la siguiente secuencia:

```text
HECHO DEL REPOSITORIO
→ ProgressViewModel combina información producida
  por Activities y Focus.

HECHO DEL REPOSITORIO
→ StreakCalculator aplica reglas propias de interpretación
  sobre los días de completitud.

HECHO DEL REPOSITORIO
→ se producen resultados derivados como racha actual,
  mejor racha y métricas agregadas.

HECHO DEL REPOSITORIO
→ Progress utiliza contexto de usuario/sesión.

HECHO DEL REPOSITORIO
→ en la evidencia inspeccionada no se observó que
  Progress controle el ciclo de vida de Activity
  ni de PomodoroSession.

INFERENCIA DE MODELADO
→ Progress no se limita a presentar datos;
  interpreta hechos producidos por otras capacidades
  y genera significado nuevo alrededor del avance,
  las rachas y las métricas.

INFERENCIA DE MODELADO
→ Progress posee la interpretación transversal
  de esos hechos, no los hechos fuente.

DECISIÓN DEL EQUIPO
→ Progress se mantiene como bounded context independiente.
```

La separación funcional puede expresarse así:

```text
Activity completada
→ ownership de Activities

PomodoroSession FOCUS completada
→ ownership de Focus

interpretación de esos hechos como progreso
→ responsabilidad de Progress
```

Activities y Focus producen los hechos fuente.

Progress consume esos hechos y aplica reglas propias para convertirlos en
información derivada sobre avance.

Por tanto:

```text
producir el hecho
≠
interpretar el hecho

Activity completada
≠
racha

PomodoroSession completada
≠
métrica de progreso

consumir información
≠
adquirir ownership
```

La posibilidad de modificar las reglas de interpretación de Progress sin alterar
necesariamente los ciclos de vida internos de Activities y Focus, siempre que se
mantengan los hechos requeridos como entrada, corresponde a una
`INFERENCIA DE MODELADO`.

No es un hecho literal del repositorio.

La frontera tampoco se justifica simplemente porque exista un paquete
`progress`, un `ProgressViewModel` o determinadas pantallas.

La estructura actual aporta evidencia.

La decisión del bounded context se fundamenta en que Progress aplica reglas
propias para transformar hechos producidos por otras capacidades en significado
nuevo sobre el avance del usuario.

Por tanto:

```text
Progress como bounded context = DECIDIDO
```

**Clasificación:** `DECISIÓN DEL EQUIPO`.

---

## 7. Achievement

### Evidencia observada

**HECHO DEL REPOSITORIO:** existen componentes y reglas relacionadas con
Achievement que utilizan información producida por otras capacidades del
sistema.

Entre la información observada se encuentran:

- cantidades de Activities completadas;
- cantidades de sesiones `FOCUS` completadas;
- racha actual;
- cantidades acumuladas utilizadas para evaluar condiciones de logro.

También se observaron reglas de elegibilidad implementadas mediante
`AchievementEngine`.

Por tanto:

```text
HECHO DEL REPOSITORIO

Achievement
→ utiliza información relacionada con Activities completadas
→ utiliza información relacionada con sesiones FOCUS completadas
→ utiliza la racha actual como entrada para determinadas reglas
→ aplica reglas de elegibilidad
```

La utilización de la racha actual demuestra que determinadas reglas de
Achievement consumen un valor de racha como entrada.

La procedencia concreta de ese valor debe interpretarse únicamente según la
trazabilidad observada en las llamadas correspondientes.

Por tanto:

```text
consumir un valor de racha
≠
poseer las reglas que calculan la racha
```

La existencia de reglas propias permite reconocer una capacidad funcional
observable, pero todavía no determina dónde debe residir conceptualmente su
ownership.

---

### Información faltante

Todavía no se encuentra cerrado:

- ownership definitivo de Achievement;
- fuente de verdad;
- persistencia principal;
- relación completa entre Android y backend;
- posible duplicación de reglas;
- dirección definitiva de sus dependencias;
- si debe integrarse dentro de otro bounded context o permanecer como capacidad
  separada.

La presencia de clases, repositorios, entidades o reglas de Achievement no
constituye por sí sola evidencia suficiente para resolver estas preguntas.

---

### Decisión pendiente

La evidencia observada permite afirmar que Achievement constituye una capacidad
con reglas de elegibilidad propias y que utiliza información relacionada con
Activities, Focus y racha.

Sin embargo, el equipo todavía no ha cerrado su ownership.

Por tanto:

```text
HECHO DEL REPOSITORIO
→ existen reglas de elegibilidad de Achievement

HECHO DEL REPOSITORIO
→ utiliza métricas o hechos relacionados con
  Activities, Focus y racha

INFORMACIÓN FALTANTE
→ ownership
→ fuente de verdad
→ persistencia
→ relación Android/backend
→ posible duplicación de reglas

DECISIÓN DEL EQUIPO
→ Achievement ownership = NO DECIDIDO
```

La existencia de reglas propias permite reconocer una capacidad observable, pero
no determina por sí sola que Achievement deba pertenecer a Progress ni que deba
convertirse en un bounded context independiente.

Por tanto:

```text
Achievement como bounded context = NO DECIDIDO
```

---

## 8. Contraste con C4 as-is y estructura actual

El C4 desarrollado durante Semana 8 y el Context Map elaborado en M5 representan
perspectivas diferentes del mismo sistema.

Por tanto:

```text
mismo sistema
≠
misma representación
```

El C4 de Semana 8 permite observar principalmente la estructura técnica y
arquitectónica del sistema.

El modelado de M5 utiliza esa estructura, junto con la evidencia del código y
las responsabilidades conceptuales, como insumo para razonar sobre fronteras y
ownership.

La definición final de esas fronteras corresponde a una `DECISIÓN DEL EQUIPO`.

La diferencia principal puede expresarse así:

```text
C4 Semana 8
→ contenedores
→ componentes
→ módulos
→ dependencias estructurales
→ distribución técnica

Context Map M5
→ bounded contexts
→ responsabilidades conceptuales
→ ownership
→ relaciones de información
→ fronteras de dominio
```

El C4 de Semana 8 se considera evidencia del estado `as-is` documentado en esa
fase del proyecto.

No se utiliza como una definición automática de los bounded contexts.

Por tanto:

```text
componente C4
≠
bounded context

módulo técnico
≠
bounded context

dependencia de código
≠
transferencia de ownership
```

---

### Correspondencia entre vistas

El C4 permite observar dónde están implementadas actualmente distintas
responsabilidades y cómo se distribuyen entre contenedores, módulos y
componentes.

También permite identificar elementos como:

- aplicación Android;
- backend;
- persistencia local;
- persistencia del backend;
- servicios;
- repositorios;
- ViewModels;
- DAOs;
- componentes de infraestructura;
- dependencias estructurales entre partes del sistema.

La matriz de trazabilidad de Semana 8 complementa esta vista al relacionar
elementos arquitectónicos con componentes concretos del código.

Esta información se utiliza en M5 como evidencia para identificar:

- dónde aparecen las responsabilidades;
- qué conceptos existen;
- qué dependencias están presentes;
- qué capacidades consumen información de otras;
- cómo están distribuidas técnicamente las reglas.

Sin embargo, el Context Map no reemplaza esa información.

La reorganiza desde una perspectiva de dominio para mostrar qué responsabilidad
pertenece a cada bounded context y qué relaciones relevantes existen entre esas
fronteras.

Por tanto:

```text
estructura actual
→ muestra dónde aparecen los elementos

modelado de dominio
→ razona sobre qué responsabilidad conceptual poseen
```

Una misma responsabilidad conceptual puede aparecer distribuida entre varios
componentes técnicos.

Del mismo modo, un mismo módulo técnico puede contener elementos que no
correspondan exactamente a una única frontera conceptual.

Por tanto:

```text
ubicación física
≠
ownership conceptual
```

La correspondencia entre ambas vistas existe porque describen el mismo sistema,
pero no son equivalentes porque responden preguntas diferentes.

---

### Distribución técnica y fronteras conceptuales

La evidencia inspeccionada muestra que las capacidades del sistema pueden estar
distribuidas entre Android y backend.

Una misma capacidad puede aparecer mediante distintos:

- servicios;
- entidades;
- repositorios;
- ViewModels;
- DAOs;
- componentes de infraestructura.

Sin embargo:

```text
implementación Android
≠
bounded context Android

implementación backend
≠
bounded context backend
```

La separación por plataforma es una separación técnica.

La separación por bounded context responde a criterios como:

```text
responsabilidad
+
lenguaje
+
reglas
+
ownership
+
relaciones con otras capacidades
```

Por esta razón, la existencia de implementaciones relacionadas en Android y
backend no conduce automáticamente a declarar fronteras distintas.

Del mismo modo, compartir una unidad de despliegue tampoco implica compartir
ownership conceptual.

---

### Correspondencia con las fronteras decididas

A partir del contraste entre responsabilidad conceptual, estructura actual y
relaciones observadas, el equipo decidió mantener las siguientes fronteras:

```text
Activities
Focus
Reminders
Progress
```

Estas fronteras no se obtuvieron mediante una conversión directa del C4.

El razonamiento seguido fue:

```text
responsabilidad conceptual
        ↕
estructura actual
        ↕
relaciones entre partes
        ↓
bounded context
```

En algunos casos existe una alineación parcial entre agrupaciones técnicas y
responsabilidades conceptuales.

Sin embargo, esa coincidencia constituye evidencia de apoyo y no la causa de la
frontera.

Por tanto:

```text
HECHO DEL REPOSITORIO
→ existen componentes, módulos y dependencias observables.

INFERENCIA DE MODELADO
→ algunas agrupaciones técnicas se alinean parcialmente
  con responsabilidades conceptuales diferenciables.

DECISIÓN DEL EQUIPO
→ las fronteras finales se expresan mediante Activities,
  Focus, Reminders y Progress.
```

---

### Ejemplos del contraste

#### Activities

La estructura actual contiene entidades, servicios, repositorios y otros
componentes asociados con `Activity`, `Category` y `Subtask`.

Sin embargo, Activities no se define como bounded context simplemente porque
exista esa agrupación técnica.

La frontera se justifica porque concentra el significado y las reglas
relacionadas con planificación, clasificación, descomposición y ciclo de vida de
esos conceptos.

#### Focus

Existen componentes relacionados con sesiones Pomodoro en la estructura del
sistema.

La frontera de Focus se fundamenta en el ciclo de vida y las reglas de
`PomodoroSession`, no únicamente en la presencia de un módulo o paquete técnico.

#### Reminders

La estructura observada contiene elementos funcionales de Reminder junto con
mecanismos técnicos de Android.

Por ejemplo:

```text
Reminder
SCHEDULED / DELIVERED / CANCELLED
→ ciclo funcional

AlarmManager
ReminderScheduler
ReminderReceiver
BootReceiver
NotificationManager
→ infraestructura
```

El contraste permite separar ambos niveles sin crear fronteras de dominio
adicionales a partir de infraestructura técnica.

#### Progress

Progress aparece estructuralmente como consumidor de información proveniente de
Activities y Focus.

Sin embargo, reglas como las implementadas en `StreakCalculator` muestran que no
se limita a presentar información.

Progress interpreta hechos externos y genera resultados derivados como racha
actual, mejor racha y métricas agregadas.

Por tanto:

```text
hechos fuente
→ Activities / Focus

interpretación transversal
→ Progress
```

La frontera se fundamenta en esa interpretación y no simplemente en la
existencia de `ProgressViewModel` o de una pantalla de progreso.

---

### Diferencia temporal entre S8 y M5

El C4 de Semana 8 representa el estado `as-is` documentado en esa fase del
proyecto.

Durante M5 se introdujeron o se hicieron observables cambios posteriores en el
repositorio.

Por tanto:

```text
baseline S8
≠
estado actual M5
```

La comparación entre ambas evidencias debe considerar el momento temporal al que
pertenece cada una.

Una relación observable actualmente no debe utilizarse de manera retroactiva
para concluir automáticamente que el C4 de Semana 8 estaba incompleto o
incorrecto.

Un caso relevante es la relación asociada a:

```text
UserRegisteredV1Listener
```

**HECHO DEL REPOSITORIO:** en el estado actual se observa un listener relacionado
con `UserRegisteredV1` que participa en el aprovisionamiento de categorías por
defecto para Activities.

La interacción observada puede resumirse así:

```text
UserRegisteredV1
→ es consumido por Activities
→ activa DefaultCategoryProvisioning
```

Esta relación aparece después del baseline considerado para Semana 8.

Por tanto:

```text
relación presente actualmente en M5
≠
evidencia de que debía estar necesariamente
  representada en el C4 de Semana 8
```

La diferencia se interpreta como evolución temporal del sistema, no como una
contradicción automática entre ambas documentaciones.

---

### Efecto de la evolución sobre el modelado

La aparición posterior de nuevas relaciones puede aportar información adicional
para el modelado de M5.

Sin embargo, dicha evolución no modifica automáticamente las fronteras ya
justificadas.

Por ejemplo, la interacción asociada a `UserRegisteredV1Listener` aporta
evidencia de que Activities consume información relacionada con el registro de
usuarios.

Eso no implica que Activities adquiera ownership sobre Identity.

Tampoco modifica por sí solo la frontera de Activities.

Por tanto:

```text
nueva dependencia observada
≠
nuevo ownership

nueva dependencia observada
≠
nueva frontera automáticamente
```

La evidencia posterior debe evaluarse con los mismos criterios utilizados para
las demás relaciones.

---

### Síntesis del contraste

El contraste entre ambas perspectivas puede resumirse así:

| Aspecto | C4 Semana 8 | Modelado M5 / Context Map |
|---|---|---|
| Perspectiva | Técnica y estructural | Conceptual y de dominio |
| Elementos principales | Contenedores, módulos y componentes | Bounded contexts |
| Pregunta principal | ¿Cómo está estructurado el sistema? | ¿Qué responsabilidad pertenece a quién? |
| Dependencias | Técnicas y estructurales | Relaciones relevantes de información |
| Código | Implementación y trazabilidad | Evidencia para razonar sobre ownership |
| Plataforma | Android, backend, persistencia, infraestructura | No determina por sí sola fronteras |
| Evolución temporal | Baseline documentado en S8 | Evidencia disponible durante M5 |
| Ownership | No es el objetivo principal | Criterio central |

Por tanto:

```text
C4
→ responde principalmente:
  "¿cómo está estructurado el sistema?"

Context Map
→ responde principalmente:
  "¿qué responsabilidad pertenece a quién
   y cómo se relacionan esas fronteras?"
```

Ambas vistas son complementarias.

El C4 permite comprender la estructura técnica existente.

El modelado de M5 utiliza esa estructura, junto con la evidencia del código y
las responsabilidades conceptuales, como insumo para razonar sobre fronteras y
ownership.

La definición final de las fronteras corresponde a una `DECISIÓN DEL EQUIPO`.

Por ello:

```text
C4 as-is
→ evidencia estructural

código inspeccionado
→ evidencia de comportamiento y dependencias

responsabilidades conceptuales
→ criterio de modelado

bounded contexts
→ decisión del equipo sustentada en el contraste
```

La diferencia entre ambas vistas no representa una inconsistencia.

Representa el uso de dos niveles de abstracción distintos para analizar el mismo
sistema.

---

## 9. Relación con el Context Map

El Context Map sintetiza las fronteras de bounded context decididas y las
relaciones de información observadas entre ellas.

Su propósito no es reproducir el C4, la estructura de paquetes ni todas las
dependencias existentes en el código.

Representa específicamente:

- qué bounded contexts fueron decididos;
- qué información consume o referencia cada uno;
- de qué otro contexto proviene esa información;
- qué dependencias conceptualmente relevantes existen entre las fronteras.

Por tanto:

```text
Context Map
≠
diagrama de clases

Context Map
≠
C4

Context Map
≠
grafo completo de dependencias de código
```

---

### Convención de dirección

El Context Map utiliza la siguiente convención:

```text
A --> B
=
A consume o referencia información de B
```

La flecha se interpreta desde el consumidor hacia el contexto que produce o
posee la información referenciada.

No significa:

- que A posea conceptos de B;
- que B llame necesariamente a A;
- que exista una llamada REST;
- que exista un evento;
- que la integración definitiva sea síncrona;
- que la integración definitiva sea asíncrona;
- que exista `Shared Kernel`;
- que exista `Anti-Corruption Layer`;
- que exista `Published Language`;
- que cada contexto deba desplegarse por separado.

La flecha representa únicamente quién necesita información de quién según la
evidencia disponible.

---

### Relaciones representadas

El mapa representa actualmente cuatro relaciones entre los bounded contexts
decididos:

```text
Focus --> Activities
→ referencia opcional a Activity
→ validación mediante ActivityLookup

Reminders --> Activities
→ referencia opcional a Activity
→ validación mediante ActivityLookup

Progress --> Activities
→ consume hechos y agregados de completitud

Progress --> Focus
→ consume hechos y agregados de sesiones FOCUS completadas
```

Aunque las cuatro relaciones utilizan la misma convención gráfica, no tienen la
misma semántica interna.

Puede resumirse así:

| Relación | Semántica observada |
|---|---|
| `Focus → Activities` | Referencia opcional y validación mediante `ActivityLookup` |
| `Reminders → Activities` | Referencia opcional y validación mediante `ActivityLookup` |
| `Progress → Activities` | Consumo de hechos y agregados de completitud |
| `Progress → Focus` | Consumo de hechos y agregados de sesiones `FOCUS` completadas |

Por tanto:

```text
misma notación de flecha
≠
misma semántica interna
```

Las etiquetas del mapa permiten precisar qué información cruza cada frontera.

---

### Consumo de información y ownership

Una regla central utilizada en el Context Map es:

```text
consumir información
≠
poseer el concepto fuente
```

Por ello:

```text
Focus referencia Activity
≠
Focus posee Activity

Reminders referencia Activity
≠
Reminders posee Activity

Progress consume hechos de Activity
≠
Progress posee Activity

Progress consume hechos de PomodoroSession
≠
Progress posee PomodoroSession
```

El ownership permanece asociado al contexto que concentra el significado y las
reglas principales del concepto.

En el modelado actual:

```text
Activity / Category / Subtask
→ Activities

PomodoroSession
→ Focus

Reminder
→ Reminders

interpretación de progreso y racha
→ Progress
```

---

### Relaciones no elevadas automáticamente al mapa

No toda dependencia observada en el código se convierte automáticamente en una
relación del Context Map.

Para representarla debe existir evidencia suficiente de una dependencia
conceptualmente relevante entre fronteras incluidas en el alcance del mapa.

Por ejemplo, el uso de `SessionManager` por parte de Progress demuestra una
necesidad estructural de contexto de usuario o sesión.

Sin embargo:

```text
dependencia de SessionManager
≠
ownership sobre Identity
```

De forma similar, los componentes Android utilizados para ejecutar recordatorios
no constituyen por sí solos una nueva frontera de dominio.

Por tanto:

```text
dependencia técnica
≠
relación de dominio automáticamente
```

---

### Evolución asociada a Identity

Durante M5 se observó la interacción asociada a `UserRegisteredV1Listener`.

La evidencia actual muestra:

```text
UserRegisteredV1
→ es consumido por Activities
→ activa DefaultCategoryProvisioning
```

Bajo la convención:

```text
A --> B
=
A consume información de B
```

si `UserRegisteredV1` es producido por Identity y consumido por Activities, una
representación conceptual de esa dependencia sería:

```text
Activities --> Identity
```

Sin embargo, esta relación no se incorpora automáticamente al mapa actual.

Su inclusión depende del alcance definido para el Context Map y de la decisión
de representar Identity dentro de ese mismo alcance.

Por tanto:

```text
HECHO DEL REPOSITORIO
→ Activities consume UserRegisteredV1.

INFERENCIA DE MODELADO
→ existe una dependencia conceptual con Identity
  si Identity forma parte del alcance del mapa.

DECISIÓN DE ALCANCE
→ incorporación definitiva al Context Map pendiente.
```

Esta situación no modifica las cuatro fronteras ya decididas.

---

### Achievement y el Context Map

Achievement no aparece actualmente como bounded context confirmado en el mapa.

La razón no es ausencia de comportamiento.

Se observaron reglas de elegibilidad y consumo de información relacionada con
Activities, Focus y racha.

Sin embargo:

```text
Achievement ownership = NO DECIDIDO
```

Por tanto, incorporarlo como bounded context implicaría representar como cerrada
una frontera que todavía no ha sido decidida.

Su ausencia del mapa actual debe interpretarse como una decisión de cautela
metodológica, no como afirmación de que la capacidad no exista.

---

### Decisiones que el Context Map todavía no toma

El mapa actual representa fronteras y dependencias de información.

No decide todavía:

- si la integración será síncrona o asíncrona;
- si se utilizarán llamadas REST;
- si se utilizarán eventos;
- qué contratos serán definitivos;
- si se requiere `Anti-Corruption Layer`;
- si existirá `Published Language`;
- si algún caso requiere `Shared Kernel`;
- si cada bounded context tendrá despliegue independiente;
- si se utilizarán microservicios;
- si las dependencias actuales del código deben mantenerse como mecanismo final.

Por tanto:

```text
bounded context
≠
microservicio

relación en Context Map
≠
REST obligatorio

relación en Context Map
≠
evento obligatorio

relación en Context Map
≠
decisión sync/async
```

La decisión sobre mecanismos de integración corresponde a una etapa posterior.

---

### Síntesis del mapa

El Context Map actual puede resumirse así:

```text
Focus --> Activities
→ referencia opcional a Activity
→ validación mediante ActivityLookup

Reminders --> Activities
→ referencia opcional a Activity
→ validación mediante ActivityLookup

Progress --> Activities
→ consume hechos y agregados de completitud

Progress --> Focus
→ consume hechos y agregados de sesiones FOCUS completadas
```

Y su regla principal de lectura es:

```text
A --> B
→ A consume o referencia información de B

A --> B
≠
A posee los conceptos de B
```

El Context Map representa qué fronteras conceptuales fueron decididas, qué
información cruza entre ellas y cómo esas dependencias pueden existir sin
transferir ownership.

---

## 10. Conclusión

El análisis realizado permitió contrastar las responsabilidades conceptuales del
sistema con la estructura actual, las reglas observadas en el código, las
dependencias entre capacidades y la documentación arquitectónica existente.

La evidencia disponible puede resumirse mediante la siguiente secuencia:

```text
HECHO DEL REPOSITORIO
→ existen reglas, estructuras y dependencias observables
  asociadas con las distintas capacidades del sistema.

INFERENCIA DE MODELADO
→ esas evidencias permiten distinguir responsabilidades
  coherentes a partir de sus reglas, lenguaje y ownership.

DECISIÓN DEL EQUIPO
→ Activities, Focus, Reminders y Progress se mantienen
  como bounded contexts independientes.

INFORMACIÓN FALTANTE
→ ownership definitivo de Achievement.
```

Por tanto, el equipo mantiene las siguientes fronteras:

```text
Activities
Focus
Reminders
Progress
```

La decisión no se fundamenta únicamente en la existencia de paquetes, módulos,
servicios, ViewModels, repositorios o componentes técnicos.

La estructura actual constituye evidencia para el análisis, pero no determina
automáticamente las fronteras de dominio.

Las fronteras se sustentan principalmente en la identificación de:

- responsabilidades conceptuales diferenciables;
- reglas propias;
- lenguaje asociado a cada capacidad;
- ownership de los conceptos principales;
- relaciones de información con otras capacidades;
- separación entre consumo de información y posesión del concepto fuente.

El Context Map representa actualmente relaciones de información sustentadas por
la evidencia disponible:

```text
Focus --> Activities
→ referencia opcional a Activity
→ validación mediante ActivityLookup

Reminders --> Activities
→ referencia opcional a Activity
→ validación mediante ActivityLookup

Progress --> Activities
→ consumo de hechos y agregados de completitud

Progress --> Focus
→ consumo de hechos y agregados de sesiones FOCUS completadas
```

La convención utilizada es:

```text
A --> B
=
A consume o referencia información de B
```

Estas relaciones expresan dependencias de información y no transferencia de
ownership.

Achievement permanece como una capacidad observable con reglas de elegibilidad
propias y uso de información relacionada con Activities, Focus y racha.

Sin embargo:

```text
Achievement ownership = NO DECIDIDO
```

Con la evidencia revisada, el equipo no cerró todavía el ownership de
Achievement ni decidió incorporarlo a uno de los bounded contexts definidos o
declararlo como bounded context independiente.

Las fronteras decididas corresponden al modelado conceptual del dominio.

No implican que cada bounded context deba convertirse en un microservicio
independiente ni determinan todavía el mecanismo definitivo de integración entre
ellos.

Las decisiones sobre REST, eventos, comunicación síncrona o asíncrona y los
contratos entre contextos deberán resolverse posteriormente en los artefactos
específicos de integración.

Por tanto:

```text
bounded context
≠
microservicio

relación en Context Map
≠
mecanismo de integración definitivo
```

El Context Map representa, en esta etapa, fronteras conceptuales y relaciones de
información sustentadas por evidencia, no una arquitectura física definitiva del
sistema.

Con ello, las fronteras de dominio quedan justificadas a partir de evidencia
observable, inferencias explícitas y decisiones del equipo, manteniendo separadas
las decisiones de dominio de las decisiones técnicas de integración.
