# Eventos candidatos de integración

<!-- M5:USERREGISTERED-STATUS:BEGIN -->

## Actualización posterior — estado de UserRegisteredV1

La clasificación `ACEPTAR` conservada en este análisis corresponde al estado
del candidato en el corte temporal original y no debe reinterpretarse
retroactivamente como una adopción ya decidida en ese momento.

Posteriormente, ADR-003 adoptó `UserRegisteredV1` específicamente para la
integración post-registro entre Identity y Activities.

Para el estado posterior documentado:

- `UserRegisteredV1`: adoptado e implementado para ese caso específico;
- `ActivityCompleted`: candidato aceptado para evaluación, no adoptado;
- `PomodoroSessionCompleted`: candidato aceptado para evaluación, no adoptado.

Decisión arquitectónica:

- `docs/adr/0003-integracion-eventos-internos.md`

Contrato técnico:

- `docs/asyncapi/rachapro-events-v1.yaml`

El payload implementado contiene:

- `userId`;
- `occurredAt`.

Esta actualización no reescribe la clasificación histórica original.

<!-- M5:USERREGISTERED-STATUS:END -->


## 1. Propósito

Este documento consolida los eventos candidatos identificados durante el análisis
del dominio de RachaPro para el Módulo 5.

La generación inicial fue realizada con apoyo de IA a partir exclusivamente de:

- responsabilidades de dominio
- flujos reales observados
- Context Map
- operaciones y cambios de estado
- código actual
- modelo actual
- decisiones arquitectónicas ya documentadas

El objetivo de esta actividad no es adoptar automáticamente todos los hechos
identificados como eventos de integración.

La IA fue utilizada para producir un conjunto amplio de propuestas que luego fue
contrastado con la evidencia disponible del dominio.

Se mantiene explícitamente la siguiente distinción:

```text
hecho del dominio
≠
evento de integración

evento candidato
≠
evento adoptado

consumidor potencial
≠
consumidor demostrado

cambio interno de estado
≠
necesidad intercontexto

propuesta de IA
≠
decisión arquitectónica
```

La clasificación `ACEPTAR` utilizada en este documento significa:

```text
candidato que supera la auditoría inicial
y merece continuar siendo evaluado
```

No significa:

```text
evento obligatorio a implementar
```

La decisión definitiva sobre si una interacción debe implementarse mediante
eventos pertenece a una etapa arquitectónica posterior.

---

## 2. Base utilizada para la generación

La generación de candidatos se realizó tomando como entrada:

```text
responsabilidades de dominio
+
flujos reales
+
Context Map
+
operaciones y cambios de estado
+
código actual
+
modelo actual
+
decisiones arquitectónicas existentes
```

Los bounded contexts considerados durante este análisis son:

```text
Activities
Focus
Reminders
Progress
```

El ownership de Achievement permanece:

```text
NO DECIDIDO
```

El Context Map utilizado como referencia contempla las siguientes relaciones:

```text
Focus → Activities
Reminders → Activities
Progress → Activities
Progress → Focus
```

La convención utilizada es:

```text
A → B
=
A consume o referencia información de B
```

Esta relación no implica automáticamente:

```text
ownership
REST
HTTP
eventos
interacción síncrona
interacción asíncrona
microservicios
```

---

## 3. Criterios de generación

Para cada candidato se evaluó:

- hecho que representa
- evidencia que sugiere su existencia
- productor potencial
- posibles interesados
- información mínima candidata
- nivel de respaldo
- clasificación final de la auditoría

Durante la generación se evitó convertir automáticamente en eventos de dominio:

```text
métodos CRUD
clics de UI
pantallas
repositories
INSERT
UPDATE
consultas SQL
llamadas técnicas
resultados de infraestructura
notificaciones Android
```

La existencia de un método o una operación técnica no constituye por sí sola un
evento de dominio.

También se mantuvo la distinción:

```text
comando
≠
evento
```

Por ejemplo:

```text
Completar Activity
→ intención u operación

ActivityCompleted
→ hecho que ya ocurrió
```

---

## 4. Nivel de respaldo de los candidatos

Los candidatos generados fueron clasificados inicialmente según el respaldo
disponible.

### 4.1 Nivel A

```text
respaldado directamente por evidencia
```

Significa que el hecho subyacente puede encontrarse directamente en el dominio,
flujo o código actual.

### 4.2 Nivel B

```text
inferencia plausible
```

Significa que la propuesta puede deducirse razonablemente del comportamiento
observado, pero no está representada de forma suficiente como una necesidad de
integración demostrada.

### 4.3 Nivel C

```text
especulativo
```

Significa que la propuesta requiere supuestos adicionales que no están
suficientemente respaldados por la evidencia disponible.

Puede corresponder a una interpretación especulativa del dominio, a una
generalización no demostrada o a la elevación incorrecta de un detalle técnico
a un evento funcional.

Por tanto:

```text
C
≠
necesariamente técnico
```

Un candidato técnico puede terminar clasificado como `C`, pero un candidato
`C` también puede ser especulativo por otras razones.

La clasificación de respaldo no equivale a la decisión de auditoría.

Por tanto:

```text
A
≠
ACEPTAR automáticamente
```

Un candidato puede representar un hecho totalmente demostrado y aun así no tener
una necesidad intercontexto demostrada.

---

## 5. Clasificaciones de auditoría

Después de la generación inicial, cada candidato fue evaluado utilizando una de
las siguientes etiquetas:

```text
ACEPTAR
RECHAZAR
REDUNDANTE
TÉCNICO
NO DEMOSTRADO
```

### 5.1 ACEPTAR

El candidato representa un hecho respaldado y existe evidencia suficiente para
mantenerlo como posible elemento de integración.

Significa:

```text
el candidato supera esta auditoría inicial
y merece continuar siendo evaluado
```

No significa:

```text
debe implementarse obligatoriamente como evento
```

La elección del mecanismo de integración pertenece a una decisión
arquitectónica posterior.

### 5.2 RECHAZAR

El candidato, tal como está formulado, no representa adecuadamente un hecho útil
para continuar el análisis.

El rechazo de un candidato no necesariamente niega que alguna información
relacionada pueda cambiar dentro del sistema.

### 5.3 REDUNDANTE

El candidato representa información que ya puede expresarse mediante otro
candidato sin introducir un hecho conceptualmente diferente.

### 5.4 TÉCNICO

El candidato representa detalles de infraestructura, mecanismos de ejecución,
diagnóstico o scheduling y no un hecho funcional del dominio.

### 5.5 NO DEMOSTRADO

El hecho puede existir y estar respaldado, pero actualmente no existe evidencia
suficiente de que otro contexto necesite conocerlo mediante una integración.

Por tanto:

```text
NO DEMOSTRADO
≠
hecho inexistente
```

También:

```text
nadie consume actualmente el hecho
≠
el hecho no existe
```

---

# 6. Activities

## 6.1 ActivityCreated

### Hecho representado

Una nueva `Activity` pasó a existir dentro de Activities.

### Evidencia

Existe una operación real de creación de `Activity`.

### Productor potencial

```text
Activities
```

### Posibles interesados

No existe un interesado intercontexto demostrado actualmente.

Progress fue considerado únicamente como posibilidad durante la generación
inicial, pero no existe evidencia suficiente de que deba reaccionar ante cada
creación.

### Información mínima candidata

```text
activityId
userId
instante de creación si forma parte del modelo disponible
```

El dato temporal se mantiene condicionado porque no debe asumirse su existencia
si el modelo actual no lo demuestra.

### Nivel de respaldo

```text
A
```

### Clasificación final

```text
NO DEMOSTRADO
```

### Justificación

La existencia del hecho no demuestra una necesidad de integración.

La creación de una Activity puede mantenerse como evolución interna de
Activities mientras no exista evidencia de otro contexto que necesite reaccionar
a ella.

---

## 6.2 ActivityCompleted

### Hecho representado

Una `Activity` pasó a estado de completitud.

### Evidencia

La operación de completitud de Activities registra información asociada al
estado completado.

Progress consume información derivada de la completitud de Activities para sus
cálculos.

### Productor potencial

```text
Activities
```

### Posibles interesados

```text
Progress
```

### Información mínima candidata

```text
activityId
userId
completedAt
completedDateEpochDay
```

La información anterior es candidata.

Si el evento llegara posteriormente a adoptarse, el contrato definitivo deberá
formalizar exactamente qué datos existen en el modelo y cuáles necesita el
consumidor.

### Nivel de respaldo

```text
A
```

### Clasificación final

```text
ACEPTAR
```

### Justificación

Aquí se demuestran dos elementos diferentes:

```text
existe el hecho de completitud
+
Progress consume información derivada de esa completitud
```

Esto permite que `ActivityCompleted` continúe hacia una evaluación
arquitectónica posterior.

No se concluye todavía que la relación Activities → Progress deba implementarse
mediante eventos.

---

## 6.3 ActivityBecameOverdue

### Hecho representado

Una `Activity` pasó de `PENDING` a `OVERDUE`.

### Evidencia

La lógica de Activities contempla actualización del estado de una Activity
cuando las condiciones temporales determinan que quedó vencida.

### Productor potencial

```text
Activities
```

### Posibles interesados

No demostrado.

### Información mínima candidata

```text
activityId
userId
fecha o instante relevante si forma parte del modelo disponible
```

### Nivel de respaldo

```text
A
```

### Clasificación final

```text
NO DEMOSTRADO
```

### Justificación

El cambio de estado está respaldado dentro de Activities.

No se ha demostrado que otro contexto necesite reaccionar ante dicha transición.

---

## 6.4 ActivityReturnedToPending

### Hecho representado

Una `Activity` pasó de `OVERDUE` nuevamente a `PENDING`.

### Evidencia

La lógica observada permite transiciones entre `PENDING` y `OVERDUE` de acuerdo
con las condiciones temporales evaluadas.

### Productor potencial

```text
Activities
```

### Posibles interesados

No demostrado.

### Información mínima candidata

```text
activityId
userId
```

### Nivel de respaldo

```text
A
```

### Clasificación final

```text
NO DEMOSTRADO
```

### Justificación

La transición existe en el dominio de Activities, pero no existe evidencia de
una necesidad intercontexto asociada.

---

## 6.5 ActivityDeleted

### Hecho representado

Una `Activity` quedó lógicamente eliminada.

### Evidencia

Activities utiliza eliminación lógica mediante una condición equivalente a:

```text
isDeleted = true
```

### Productor potencial

```text
Activities
```

### Posibles interesados

Focus y Reminders podrían verse afectados posteriormente al intentar validar una
referencia a esa Activity.

Sin embargo, esa posibilidad no demuestra que actualmente reaccionen mediante
un evento.

### Información mínima candidata

```text
activityId
userId
```

### Nivel de respaldo

```text
A
```

### Clasificación final

```text
NO DEMOSTRADO
```

### Justificación

La eliminación lógica está demostrada.

La necesidad de publicar esa eliminación como integración no lo está.

Focus utiliza actualmente una capacidad síncrona de Activities para validar una
referencia a Activity cuando corresponde.

El contrato actual no demuestra una reacción basada en eventos ante la
eliminación.

---

## 6.6 SubtaskCompleted

### Hecho representado

Una `Subtask` pasó a estar completada.

### Evidencia

Existe una operación real de completitud de Subtask dentro de Activities.

### Productor potencial

```text
Activities
```

### Posibles interesados

No demostrado.

### Información mínima candidata

```text
subtaskId
activityId
userId si corresponde al contrato futuro
```

### Nivel de respaldo

```text
A
```

### Clasificación final

```text
NO DEMOSTRADO
```

### Justificación

El hecho existe dentro del dominio de Activities.

No existe evidencia de otro bounded context que necesite reaccionar ante la
completitud individual de una Subtask.

---

## 6.7 SubtaskUncompleted

### Hecho representado

Una `Subtask` dejó de estar completada.

### Evidencia

Existe una operación real para revertir la completitud de una Subtask.

### Productor potencial

```text
Activities
```

### Posibles interesados

No demostrado.

### Información mínima candidata

```text
subtaskId
activityId
```

### Nivel de respaldo

```text
A
```

### Clasificación final

```text
NO DEMOSTRADO
```

### Justificación

El cambio pertenece al dominio de Activities.

No existe evidencia de una necesidad de integración intercontexto asociada.

---

## 6.8 CategoryCreated

### Hecho representado

Una `Category` pasó a existir dentro de Activities.

### Evidencia

Activities posee operaciones relacionadas con creación y aprovisionamiento de
categorías.

### Productor potencial

```text
Activities
```

### Posibles interesados

No demostrado.

### Información mínima candidata

```text
categoryId
userId
nombre si fuera necesario para un contrato futuro
```

### Nivel de respaldo

```text
A
```

### Clasificación final

```text
NO DEMOSTRADO
```

### Justificación

La creación de una Category es un hecho válido de Activities.

No está demostrada una necesidad intercontexto que justifique mantenerlo como
candidato de integración.

---

# 7. Focus

## 7.1 PomodoroSessionStarted

### Hecho representado

Una nueva `PomodoroSession` comenzó en estado `RUNNING`.

### Evidencia

Focus crea sesiones cuyo ciclo de vida comienza en `RUNNING`.

### Productor potencial

```text
Focus
```

### Posibles interesados

No demostrado.

### Información mínima candidata

```text
sessionId
userId
type
activityId opcional
plannedDurationSeconds
```

### Nivel de respaldo

```text
A
```

### Clasificación final

```text
NO DEMOSTRADO
```

### Justificación

El inicio de la sesión es un hecho real de Focus.

No existe evidencia de otro contexto que necesite reaccionar ante cada inicio.

---

## 7.2 PomodoroSessionPaused

### Hecho representado

Una `PomodoroSession` pasó de `RUNNING` a `PAUSED`.

### Evidencia

La transición está contemplada en el ciclo de vida actual de Focus.

### Productor potencial

```text
Focus
```

### Posibles interesados

No demostrado.

### Información mínima candidata

```text
sessionId
userId
instante de pausa si forma parte del modelo disponible
```

### Nivel de respaldo

```text
A
```

### Clasificación final

```text
NO DEMOSTRADO
```

### Justificación

La transición es un hecho real de Focus, pero no existe evidencia de una
necesidad intercontexto.

---

## 7.3 PomodoroSessionResumed

### Hecho representado

Una `PomodoroSession` pasó de `PAUSED` nuevamente a `RUNNING`.

### Evidencia

La transición forma parte del ciclo observado de Focus.

### Productor potencial

```text
Focus
```

### Posibles interesados

No demostrado.

### Información mínima candidata

```text
sessionId
userId
```

### Nivel de respaldo

```text
A
```

### Clasificación final

```text
NO DEMOSTRADO
```

### Justificación

El hecho pertenece al ciclo interno de Focus.

Actualmente no tiene un consumidor intercontexto demostrado.

---

## 7.4 PomodoroSessionCompleted

### Hecho representado

Una `PomodoroSession` pasó a `COMPLETED`.

### Evidencia

Focus registra sesiones completadas.

Progress consume información correspondiente a sesiones que cumplen:

```text
type = FOCUS
status = COMPLETED
```

### Productor potencial

```text
Focus
```

### Posibles interesados

```text
Progress
```

### Información mínima candidata

```text
sessionId
userId
type
completedDateEpochDay
plannedDurationSeconds
```

`plannedDurationSeconds` debe conservar su significado actual.

No debe reinterpretarse automáticamente como tiempo efectivo de concentración.

### Nivel de respaldo

```text
A
```

### Clasificación final

```text
ACEPTAR
```

### Justificación

Existe un hecho respaldado y existe un interesado intercontexto demostrado.

Por esta razón el candidato merece continuar a una evaluación arquitectónica.

Esto no demuestra todavía que la interacción Focus → Progress deba implementarse
obligatoriamente mediante un evento.

---

## 7.5 PomodoroSessionCancelled

### Hecho representado

Una `PomodoroSession` pasó a `CANCELLED`.

### Evidencia

`CANCELLED` forma parte de los estados funcionales observados de
`PomodoroSession`.

### Productor potencial

```text
Focus
```

### Posibles interesados

No demostrado.

### Información mínima candidata

```text
sessionId
userId
type
```

### Nivel de respaldo

```text
A
```

### Clasificación final

```text
NO DEMOSTRADO
```

### Justificación

La cancelación está respaldada como cambio funcional de Focus.

No existe evidencia de otro contexto que necesite reaccionar ante ella.

---

## 7.6 FocusSessionCompleted

### Hecho representado

Una sesión de tipo `FOCUS` quedó completada.

### Evidencia

Progress consume sesiones que cumplen:

```text
type = FOCUS
status = COMPLETED
```

### Productor potencial

```text
Focus
```

### Posibles interesados

```text
Progress
```

### Información mínima candidata

```text
sessionId
userId
completedDateEpochDay
plannedDurationSeconds
```

### Nivel de respaldo

```text
B
```

### Clasificación final

```text
REDUNDANTE
```

### Justificación

Con la evidencia actual, este candidato no representa un hecho diferente de:

```text
PomodoroSessionCompleted
+
type = FOCUS
```

`PomodoroSessionCompleted` ya puede comunicar que la sesión completada pertenece
al tipo `FOCUS`.

No se demostró una semántica adicional que justifique mantener simultáneamente
un evento especializado.

---

# 8. Reminders

## 8.1 ReminderScheduled

### Hecho representado

Un `Reminder` quedó registrado con intención temporal y estado `SCHEDULED`.

### Evidencia

El modelo funcional de Reminders contempla:

```text
triggerAtMillis
status = SCHEDULED
```

### Productor potencial

```text
Reminders
```

### Posibles interesados

No demostrado.

### Información mínima candidata

```text
reminderId
userId
triggerAtMillis
activityId opcional
```

### Nivel de respaldo

```text
A
```

### Clasificación final

```text
NO DEMOSTRADO
```

### Justificación

El hecho pertenece al dominio funcional de Reminders.

No se ha identificado otro contexto que necesite reaccionar ante cada Reminder
programado.

---

## 8.2 ReminderDelivered

### Hecho representado

Un `Reminder` pasó a estado `DELIVERED`.

### Evidencia

`DELIVERED` forma parte del ciclo funcional del Reminder.

### Productor potencial

```text
Reminders
```

### Posibles interesados

No demostrado.

### Información mínima candidata

```text
reminderId
userId
instante de entrega si forma parte del modelo disponible
```

### Nivel de respaldo

```text
A
```

### Clasificación final

```text
NO DEMOSTRADO
```

### Justificación

La transición es un hecho funcional válido.

No existe actualmente una necesidad intercontexto demostrada.

---

## 8.3 ReminderCancelled

### Hecho representado

Un `Reminder` pasó a `CANCELLED`.

### Evidencia

`CANCELLED` forma parte del ciclo funcional observado de Reminders.

### Productor potencial

```text
Reminders
```

### Posibles interesados

No demostrado.

### Información mínima candidata

```text
reminderId
userId
```

### Nivel de respaldo

```text
A
```

### Clasificación final

```text
NO DEMOSTRADO
```

### Justificación

La cancelación es un hecho funcional del contexto Reminders.

No está demostrada su necesidad como integración intercontexto.

---

## 8.4 ReminderScheduledExactly

### Hecho representado por la propuesta

La infraestructura Android consiguió programar una alarma exacta.

### Evidencia

Existe un resultado técnico equivalente a:

```text
ScheduledExact
```

### Productor potencial

```text
infraestructura Android
```

### Posibles interesados

Componentes técnicos de scheduling, observabilidad o diagnóstico.

### Información mínima candidata

Datos técnicos relacionados con el proceso de programación.

### Nivel de respaldo

```text
C
```

como evento de dominio.

### Clasificación final

```text
TÉCNICO
```

### Justificación

Describe cómo la infraestructura Android consiguió programar una alarma.

No representa un hecho funcional nuevo del dominio de Reminders.

---

## 8.5 ReminderScheduledInexactly

### Hecho representado por la propuesta

La infraestructura Android consiguió programar una alarma de manera inexacta.

### Evidencia

Existe un resultado técnico equivalente a:

```text
ScheduledInexact
```

### Productor potencial

```text
infraestructura Android
```

### Posibles interesados

Componentes técnicos de scheduling o diagnóstico.

### Información mínima candidata

Datos técnicos relacionados con el resultado del scheduler.

### Nivel de respaldo

```text
C
```

como evento de dominio.

### Clasificación final

```text
TÉCNICO
```

### Justificación

La propuesta describe el comportamiento del mecanismo utilizado para programar
el Reminder.

No pertenece al lenguaje funcional del dominio de Reminders.

---

## 8.6 ReminderSchedulingFailed

### Hecho representado por la propuesta

El mecanismo técnico de programación de una alarma no consiguió completar la
operación.

### Evidencia

La infraestructura contempla resultados técnicos como:

```text
InvalidTime
Error
```

### Productor potencial

```text
infraestructura Android
```

### Posibles interesados

Componentes técnicos de diagnóstico, logging u observabilidad.

### Información mínima candidata

Información técnica relacionada con el fallo.

### Nivel de respaldo

```text
C
```

como evento de dominio.

### Clasificación final

```text
TÉCNICO
```

### Justificación

El fallo pertenece al mecanismo técnico utilizado para programar el Reminder.

No debe elevarse automáticamente a evento de dominio.

---

# 9. Progress y Achievement

## 9.1 ProgressUpdated

### Hecho representado por la propuesta

Alguna representación de progreso del usuario cambió.

### Evidencia que originó el candidato

Progress agrega información proveniente de Activities y Focus.

### Productor potencial

```text
Progress
```

### Posibles interesados

No demostrado.

### Información mínima candidata

No puede determinarse de manera precisa porque el candidato agrupa múltiples
conceptos diferentes.

### Nivel de respaldo

```text
B
```

### Clasificación final

```text
RECHAZAR
```

### Justificación

`ProgressUpdated` es demasiado genérico.

Podría representar:

```text
cambio de racha
cambio de mejor racha
cambio de conteos
cambio de duración agregada
cambio de actividades completadas
cambio de sesiones completadas
```

El nombre no identifica un hecho de dominio suficientemente preciso.

El rechazo se refiere a la formulación concreta del candidato.

No significa que los valores utilizados por Progress nunca cambien.

---

## 9.2 StreakChanged

### Hecho representado

El valor calculado de la racha actual cambió.

### Evidencia

Progress calcula rachas utilizando información derivada de días con completitud.

### Productor potencial

```text
Progress
```

### Posibles interesados

No demostrado.

Achievement fue considerado como posible interesado durante la generación, pero
su ownership sigue sin decisión definitiva.

### Información mínima candidata

```text
userId
nuevo valor de racha
```

### Nivel de respaldo

```text
B
```

### Clasificación final

```text
NO DEMOSTRADO
```

### Justificación

El cambio es conceptualmente plausible y específico.

No se ha demostrado que otro contexto necesite reaccionar ante cada cambio de
racha.

Además, debe mantenerse presente que la racha es un valor derivado de
información consumida por Progress.

---

## 9.3 BestStreakChanged

### Hecho representado

Cambió la mejor racha histórica calculada para un usuario.

### Evidencia

Progress posee cálculo de mejor racha.

### Productor potencial

```text
Progress
```

### Posibles interesados

No demostrado.

### Información mínima candidata

```text
userId
nueva mejor racha
```

### Nivel de respaldo

```text
B
```

### Clasificación final

```text
NO DEMOSTRADO
```

### Justificación

La mejor racha es un valor derivado que puede cambiar.

No está demostrada una necesidad de integración intercontexto asociada a ese
cambio.

---

## 9.4 AchievementUnlocked

### Hecho representado

Un logro pasó de no cumplido a desbloqueado.

### Evidencia

Existe una capacidad relacionada con evaluación de logros y reglas de
elegibilidad.

La propuesta representa una inferencia razonable a partir de esa capacidad, pero
el ownership de Achievement continúa sin decisión definitiva.

### Productor potencial

```text
NO DECIDIDO
```

### Posibles interesados

Potencialmente UI u otras capacidades.

No existe una relación intercontexto demostrada.

### Información mínima candidata

```text
userId
achievementId o achievementType
```

### Nivel de respaldo

```text
B
```

### Clasificación final

```text
NO DEMOSTRADO
```

### Justificación

`AchievementUnlocked` es una inferencia plausible como hecho del dominio.

Sin embargo, el ownership de Achievement permanece:

```text
NO DECIDIDO
```

Por tanto, no deben fijarse automáticamente:

```text
productor
bounded context propietario
consumidores
mecanismo de integración
```

La clasificación `B` expresa que el candidato es plausible.

La clasificación `NO DEMOSTRADO` expresa que todavía no existe evidencia
suficiente para establecer una necesidad intercontexto ni una frontera de
integración.

El candidato puede revisarse nuevamente cuando exista una decisión explícita
sobre ownership.

---

# 10. Identity

## 10.1 UserRegisteredV1

### Hecho representado

Un usuario fue registrado.

### Estado actual

`UserRegisteredV1` existe actualmente como evento en el sistema.

Activities lo consume mediante:

```text
UserRegisteredV1Listener
```

y existe una reacción concreta asociada:

```text
DefaultCategoryProvisioning
```

### Evidencia

Se encuentran demostrados:

```text
existencia del evento
+
consumo por Activities
+
reacción concreta
```

### Productor potencial

El productor conceptual requiere todavía trazabilidad explícita.

> **Nota temporal:** este estado de productor pendiente corresponde al corte histórico original de esta sección. Para el estado posterior materializado de `UserRegisteredV1`, consulte la aclaración posterior del productor incluida más adelante en este mismo documento.

No debe fijarse únicamente a partir del nombre del evento.

### Interesado demostrado

```text
Activities
```

### Información mínima observada

```text
userId
```

La información exacta deberá verificarse contra la definición actual del evento
si posteriormente se formaliza como contrato.

### Nivel de respaldo

```text
A
```

### Clasificación final

```text
ACEPTAR
```

### Justificación

A diferencia de la mayoría de candidatos generados, `UserRegisteredV1` no es
solamente una propuesta conceptual.

Existe actualmente como evento y Activities reacciona a él.

Por tanto, supera esta auditoría.

### Pendiente

```text
trazabilidad explícita del productor conceptual
```

### Cronología

`UserRegisteredV1` pertenece al estado actual de M5.

No aparecía en el baseline S8.

Esta diferencia se registra como:

```text
evolución posterior del sistema
```

y no como:

```text
contradicción retroactiva del baseline
```

---

# 11. Consolidación de resultados

## 11.1 Candidatos que continúan en evaluación

```text
ActivityCompleted
PomodoroSessionCompleted
UserRegisteredV1
```

Estos candidatos superan la auditoría inicial.

La clasificación `ACEPTAR` significa:

```text
merece continuar a evaluación arquitectónica
```

No significa:

```text
evento de integración adoptado
```

Cada interacción deberá evaluarse posteriormente considerando:

- necesidad real de integración
- mecanismo existente
- consistencia requerida
- dependencia temporal
- acoplamiento
- complejidad
- observabilidad
- recuperación ante fallos
- atributos de calidad
- beneficios frente a mecanismos alternativos
- costo de operación y mantenimiento

---

## 11.2 Candidatos rechazados

```text
ProgressUpdated
```

### Motivo

```text
formulación demasiado genérica
```

El rechazo no significa que Progress no cambie.

Significa que `ProgressUpdated` no identifica un hecho suficientemente concreto.

---

## 11.3 Candidatos redundantes

```text
FocusSessionCompleted
```

### Motivo

```text
PomodoroSessionCompleted
+
type = FOCUS
```

permite representar el mismo hecho relevante con la evidencia actual.

No se demostró una semántica adicional que justifique mantener ambos
candidatos.

---

## 11.4 Candidatos técnicos

```text
ReminderScheduledExactly
ReminderScheduledInexactly
ReminderSchedulingFailed
```

Estos candidatos describen detalles del mecanismo Android de programación de
alarmas y no hechos funcionales del dominio.

La diferencia central es:

```text
SCHEDULED
DELIVERED
CANCELLED
→ estados funcionales

ScheduledExact
ScheduledInexact
InvalidTime
Error
→ resultados técnicos
```

---

## 11.5 Candidatos con necesidad intercontexto no demostrada

```text
ActivityCreated
ActivityBecameOverdue
ActivityReturnedToPending
ActivityDeleted
SubtaskCompleted
SubtaskUncompleted
CategoryCreated
PomodoroSessionStarted
PomodoroSessionPaused
PomodoroSessionResumed
PomodoroSessionCancelled
ReminderScheduled
ReminderDelivered
ReminderCancelled
StreakChanged
BestStreakChanged
AchievementUnlocked
```

Estos candidatos no se invalidan necesariamente como hechos de dominio.

La clasificación significa:

```text
el hecho puede existir
pero
su necesidad como integración intercontexto
no está demostrada
```

---

# 12. Casos importantes detectados durante la auditoría

## 12.1 Hecho existente sin necesidad intercontexto demostrada

Ejemplos:

```text
ActivityBecameOverdue
PomodoroSessionPaused
ReminderDelivered
SubtaskCompleted
```

Estos hechos existen o son respaldados por el modelo.

Sin embargo, no existe evidencia suficiente de que otro bounded context necesite
reaccionar ante ellos.

---

## 12.2 Redundancia semántica

El análisis detectó la posible duplicación:

```text
PomodoroSessionCompleted
FocusSessionCompleted
```

La segunda propuesta fue clasificada como redundante porque:

```text
PomodoroSessionCompleted
+
type = FOCUS
```

permite identificar el hecho que interesa a Progress.

---

## 12.3 Detalles técnicos elevados incorrectamente a dominio

Los candidatos:

```text
ReminderScheduledExactly
ReminderScheduledInexactly
ReminderSchedulingFailed
```

fueron generados deliberadamente dentro del conjunto amplio de propuestas.

La auditoría permitió determinar que pertenecen al mecanismo de infraestructura
y no al dominio funcional de Reminders.

---

## 12.4 Candidato excesivamente genérico

```text
ProgressUpdated
```

fue rechazado porque no permite saber qué hecho concreto ocurrió.

Un evento de dominio debe expresar un hecho suficientemente preciso.

---

# 13. Relación con Focus → Activities

Durante este análisis debe conservarse la decisión previamente formalizada para
la relación:

```text
Focus → Activities
```

Focus utiliza actualmente una API pública intermodular local:

```text
ActivityLookup
```

con una operación equivalente a:

```text
activityId
+
userId
→
Boolean
```

La interacción formalizada actualmente es síncrona.

La existencia de candidatos de eventos en este documento no sustituye
automáticamente ese contrato.

En particular:

```text
ActivityDeleted
```

no implica que Focus deba comenzar a mantener una copia local del estado de
Activities ni reaccionar a una publicación asíncrona.

La relación actual debe permanecer separada del ejercicio exploratorio de
eventos.

---

# 14. Relación con Progress

Progress consume información proveniente de:

```text
Activities
Focus
```

La evidencia actual demuestra interés de Progress en:

```text
completitud de Activities
sesiones FOCUS completadas
```

Por esta razón:

```text
ActivityCompleted
PomodoroSessionCompleted
```

continúan en evaluación.

Sin embargo:

```text
consumidor demostrado
≠
evento obligatorio
```

La forma concreta de integración deberá decidirse posteriormente.

---

# 15. Precisión sobre plannedDurationSeconds

Progress consume información agregada de sesiones de Focus.

La duración observada corresponde a:

```text
plannedDurationSeconds
```

Este valor no debe reinterpretarse como:

```text
tiempo efectivo de concentración
```

sin evidencia adicional.

Por tanto, cualquier contrato futuro basado en
`PomodoroSessionCompleted` deberá conservar explícitamente esa semántica.

---

# 16. Precisión sobre Achievement

El análisis generó:

```text
AchievementUnlocked
```

como candidato plausible.

Su nivel de respaldo se mantiene como:

```text
B
```

porque representa una inferencia plausible a partir de las capacidades de
evaluación de logros observadas.

Sin embargo, el ownership de Achievement permanece:

```text
NO DECIDIDO
```

Por tanto, este documento no determina:

```text
qué bounded context lo produce
quién lo consume
si debe publicarse
qué contrato tendría
qué mecanismo de integración utilizaría
```

La decisión deberá retomarse cuando el ownership esté definido.

---

# 17. Precisión sobre UserRegisteredV1

`UserRegisteredV1` tiene una situación diferente a los otros candidatos.

En el estado actual:

```text
el evento existe
Activities lo consume
Activities ejecuta DefaultCategoryProvisioning
```

Lo pendiente no es demostrar su existencia.

Lo pendiente es:

```text
trazar explícitamente su productor conceptual
```

También debe preservarse su cronología:

```text
baseline S8
→ no estaba presente

estado actual M5
→ está presente
```

Esto se interpreta como evolución del sistema.

---

# 18. Resultado final del ejercicio

La generación inicial con IA produjo candidatos pertenecientes a:

```text
Activities
Focus
Reminders
Progress
Achievement
Identity
```

La auditoría permitió separar:

```text
hechos reales del dominio
candidatos con interés intercontexto
propuestas redundantes
resultados técnicos
formulaciones demasiado genéricas
hechos sin necesidad intercontexto demostrada
```

El conjunto que continúa a evaluación arquitectónica posterior es:

```text
ActivityCompleted
PomodoroSessionCompleted
UserRegisteredV1
```

Esto significa:

```text
superaron la auditoría inicial
```

No significa:

```text
deben implementarse obligatoriamente como eventos publicados
```

La adopción definitiva de cualquiera de estos candidatos requiere una decisión
arquitectónica posterior.

---

# 19. Estado del análisis

El inventario queda consolidado de la siguiente forma:

```text
ACEPTAR

ActivityCompleted
PomodoroSessionCompleted
UserRegisteredV1
```

```text
RECHAZAR

ProgressUpdated
```

```text
REDUNDANTE

FocusSessionCompleted
```

```text
TÉCNICO

ReminderScheduledExactly
ReminderScheduledInexactly
ReminderSchedulingFailed
```

```text
NO DEMOSTRADO

ActivityCreated
ActivityBecameOverdue
ActivityReturnedToPending
ActivityDeleted
SubtaskCompleted
SubtaskUncompleted
CategoryCreated
PomodoroSessionStarted
PomodoroSessionPaused
PomodoroSessionResumed
PomodoroSessionCancelled
ReminderScheduled
ReminderDelivered
ReminderCancelled
StreakChanged
BestStreakChanged
AchievementUnlocked
```

La fase de generación y filtrado de eventos candidatos queda cerrada.

Los candidatos aceptados continúan a evaluación arquitectónica y no se consideran automáticamente eventos de integración adoptados.

<!-- M5:FINAL-AUDIT-USERREGISTERED-PRODUCER:BEGIN -->

## Aclaración posterior — trazabilidad del productor de UserRegisteredV1

En el corte original del catálogo existían apartados en los que la trazabilidad
del productor conceptual de `UserRegisteredV1` todavía aparecía como pendiente.

Ese estado histórico no se reescribe.

Posteriormente, la implementación, ADR-003 y el contrato AsyncAPI permitieron
trazar el flujo vigente de forma explícita:

`Identity`
→ `UserService`
→ publica `UserRegisteredV1`
→ Activities consume mediante `UserRegisteredV1Listener`

Por tanto:

`productor pendiente`
→ describe el corte histórico de análisis en el que fue escrito ese apartado

mientras que:

`Identity / UserService`
→ corresponde al estado posterior materializado y documentado.

Esta aclaración actualiza la lectura temporal del catálogo sin convertir el
estado posterior en evidencia causal retrospectiva de las clasificaciones
originales.

<!-- M5:FINAL-AUDIT-USERREGISTERED-PRODUCER:END -->
