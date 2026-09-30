# Semana 9 — API, eventos e integración entre contextos

## 1. Propósito

Definir cómo se relacionan los contextos de negocio de RachaPro después de la materialización del monolito modular de Semana 8.

Los objetivos de esta semana son:

- mapear subdominios y bounded contexts;

- identificar ownership y dependencias entre contextos;

- documentar los contratos síncronos existentes;

- proponer y auditar eventos candidatos;

- decidir qué relaciones deben permanecer síncronas y cuál merece ser evaluada de forma asíncrona;

- definir la hipótesis, alcance y criterios de éxito del Spike 1 de Semana 10.

Este documento describe el estado vigente y las decisiones de Semana 9. No implica todavía que la arquitectura productiva haya sido modificada para utilizar eventos.

---

## 2. Evidencia de partida

La arquitectura actual corresponde a un monolito modular con un único backend Spring Boot.

Los módulos de negocio materializados son:

- Identity;

- Activities;

- Focus;

- Progress;

- Reminders.

Existe además:

- Shared, utilizado únicamente para soporte técnico transversal y sin ownership de datos de negocio.

El ownership actual de datos es:

| Contexto / módulo | Datos propios |

|---|---|

| Identity | `users` |

| Activities | `activities`, `categories`, `subtasks` |

| Focus | `pomodoro_sessions` |

| Progress | `achievements` |

| Reminders | `reminders` |

| Shared | no posee tablas de negocio |

Las dependencias intermodulares actuales se realizan mediante contratos públicos de Activities.

---

## 3. Mapa de subdominios y bounded contexts

## 3.1 Identity

### Responsabilidad

Gestionar:

- usuarios;

- registro;

- autenticación;

- infraestructura de seguridad asociada a la identidad.

### Ownership

- `users`

### Límite

Identity no debe crear ni persistir directamente entidades pertenecientes a Activities.

### Relación externa relevante

Después del registro de un usuario, Identity solicita actualmente a Activities la creación de las categorías predeterminadas.

Contrato utilizado:

`DefaultCategoryProvisioning`

---

## 3.2 Activities

### Responsabilidad

Gestionar:

- actividades;

- categorías;

- subtareas;

- reglas asociadas a estas entidades.

### Ownership

- `activities`

- `categories`

- `subtasks`

### API pública actual

Activities publica los contratos:

- `ActivityLookup`

- `DefaultCategoryProvisioning`

### Justificación del límite

Activity, Category y Subtask forman parte de una misma capacidad funcional: organizar y estructurar el trabajo del usuario.

La persistencia y las reglas internas de estas entidades permanecen encapsuladas dentro de Activities.

---

## 3.3 Focus

### Responsabilidad

Gestionar las sesiones Pomodoro y la capacidad de concentración asociada.

### Ownership

- `pomodoro_sessions`

### Relación externa relevante

Focus necesita comprobar que una actividad asociada:

- existe;

- pertenece al usuario;

- continúa activa.

Para ello utiliza:

`ActivityLookup`

Focus no accede directamente a `ActivityRepository`.

---

## 3.4 Progress

### Responsabilidad

Gestionar el dominio backend asociado a:

- logros;

- progreso persistido en backend.

### Ownership

- `achievements`

### Consideración AS-IS

El progreso funcional de la aplicación también depende del estado de las actividades.

Sin embargo, el flujo que se ha validado actualmente para parte de las métricas de progreso ocurre en Android:

`Activities remotas -> snapshot local en Room -> lectura por Progress`

Por tanto, no se introduce en este documento una dependencia backend:

`Progress -> Activities API`

porque actualmente no existe evidencia de que ese sea el mecanismo implementado entre los módulos backend.

La dependencia funcional entre progreso y actividades no debe confundirse con una dependencia intermodular backend.

---

## 3.5 Reminders

### Responsabilidad

Gestionar recordatorios asociados al usuario y, opcionalmente, a actividades.

### Ownership

- `reminders`

### Relación externa relevante

Cuando un recordatorio referencia una actividad, Reminders necesita validar que dicha actividad pertenece al usuario.

Utiliza:

`ActivityLookup`

Reminders no accede directamente a `ActivityRepository`.

La entrega final de notificaciones mediante AlarmManager y mecanismos Android pertenece al contexto Android/local.

---

## 3.6 Shared

Shared no se clasifica como bounded context de negocio.

Su responsabilidad es:

- soporte técnico transversal;

- elementos reutilizables sin ownership de negocio.

Shared:

- no posee tablas de negocio;

- no debe contener repositorios de negocio;

- no debe contener entidades de negocio;

- no debe convertirse en un mecanismo para evitar las fronteras entre módulos.

---

## 4. Mapa de relaciones entre contextos

La integración backend vigente se resume mediante los contratos públicos expuestos por Activities:

- Activities expone `DefaultCategoryProvisioning`, utilizado por Identity.
- Activities expone `ActivityLookup`, utilizado por Focus.
- Activities expone `ActivityLookup`, utilizado por Reminders.

Relaciones actuales:

| Consumidor | Proveedor | Contrato | Tipo actual |
|---|---|---|---|
| Identity | Activities | `DefaultCategoryProvisioning` | síncrono |
| Focus | Activities | `ActivityLookup` | síncrono |
| Reminders | Activities | `ActivityLookup` | síncrono |

Los consumidores utilizan contratos públicos de Activities y no acceden directamente a sus repositorios o entidades internas.

No se identifica actualmente una dependencia backend equivalente entre Progress y Activities. La relación funcional entre actividades completadas y parte del progreso también ocurre en Android/local y no debe representarse como una dependencia backend que no está implementada.

---

## 5. Contratos síncronos actuales

### 5.1 ActivityLookup

`ActivityLookup` es un contrato público de Activities.

Operación actual:

`existsActiveActivityForUser(activityId, userId)`

Consumidores:

- Focus;
- Reminders.

Focus y Reminders requieren conocer inmediatamente si la actividad asociada existe, pertenece al usuario y continúa activa.

Por esta razón, durante Semana 9 este contrato permanece síncrono.

### 5.2 DefaultCategoryProvisioning

`DefaultCategoryProvisioning` es un contrato público de Activities.

Operación actual:

`createDefaultsForUser(userId)`

Consumidor:

- Identity.

Después del registro de un usuario, Identity solicita mediante este contrato el aprovisionamiento de las categorías iniciales administradas por Activities.

A diferencia de `ActivityLookup`, esta operación representa un efecto posterior al registro y no entrega a Identity información de negocio necesaria para tomar una decisión inmediata.

Por esta razón se selecciona como candidata para el Spike 1 de Semana 10.

---
## 6. Decisión síncrono / asíncrono

### 6.1 Relaciones que permanecen síncronas

#### Focus -> ActivityLookup

Se mantiene síncrona porque Focus necesita conocer inmediatamente si la actividad asociada es válida.

#### Reminders -> ActivityLookup

Se mantiene síncrona porque Reminders necesita conocer inmediatamente si puede asociar el recordatorio a la actividad solicitada.

Convertir estas consultas en eventos no resulta apropiado para el comportamiento actual porque el consumidor requiere una respuesta inmediata.

### 6.2 Relación candidata a integración asíncrona

Actualmente:

`Identity -> DefaultCategoryProvisioning -> Activities`

Se evaluará experimentalmente:

`Identity -> UserRegisteredV1 -> Activities`

La arquitectura productiva no cambia todavía.

La sustitución se realizará únicamente dentro del Spike 1 hasta conocer su resultado.

---

## 7. Catálogo de eventos candidatos

### 7.1 UserRegisteredV1

**Hecho de negocio**

Un nuevo usuario fue registrado correctamente.

**Estado actual**

El hecho de negocio existe.

El evento no está implementado actualmente.

**Productor candidato**

Identity.

**Consumidor candidato**

Activities.

**Reacción esperada**

Crear las categorías predeterminadas del usuario.

**Payload mínimo propuesto**

- `userId`
- `occurredAt`

No se requiere incluir correo ni otros datos personales para realizar el aprovisionamiento.

**Estado**

**ACEPTADO PARA SPIKE**

### 7.2 ActivityCompleted

**Hecho de negocio**

Una actividad pasa al estado completado.

**Evaluación**

El hecho funcional existe y afecta conceptualmente el progreso.

Sin embargo, no se identificó actualmente un consumidor backend que utilice un evento equivalente para mantener Progress.

Parte del flujo de progreso validado ocurre mediante Room en Android.

**Estado**

**EVENTO POTENCIAL — NO SE UTILIZA EN EL SPIKE 1**

Requiere evidencia adicional antes de convertirse en contrato arquitectónico.

### 7.3 PomodoroCompleted

**Hecho de negocio**

Una sesión Pomodoro finaliza.

**Evaluación**

El hecho pertenece a Focus.

No se identificó todavía una integración backend que necesite reaccionar de forma asíncrona a este hecho.

**Estado**

**NO JUSTIFICADO PARA EL SPIKE 1**

### 7.4 AchievementUnlocked

**Evaluación**

Puede representar un hecho válido dentro de Progress.

Sin embargo, no se identificó un consumidor externo que necesite actualmente recibirlo.

**Estado**

**REDUNDANTE PARA LA INTEGRACIÓN ACTUAL**

### 7.5 ReminderTriggered

**Evaluación**

El concepto es ambiguo en la arquitectura actual porque la entrega de la notificación ocurre mediante mecanismos Android/local.

Modelarlo directamente como evento backend podría mezclar responsabilidades del backend con la infraestructura de notificaciones Android.

**Estado**

**RECHAZADO PARA EL CATÁLOGO ACTUAL**

---

## 8. Auditoría de propuestas generadas con IA

Durante el análisis se consideraron varios eventos candidatos.

No se aceptaron automáticamente.

| Propuesta IA | Resultado de verificación | Decisión |
|---|---|---|
| `UserRegisteredV1` | el registro de usuario existe y actualmente produce un efecto en Activities | aceptar para Spike 1 |
| `ActivityCompleted` | el hecho existe, pero no se demostró un consumidor backend actual | mantener como candidato futuro |
| `PomodoroCompleted` | hecho plausible de Focus, sin integración necesaria demostrada | no usar |
| `AchievementUnlocked` | pertenece a Progress, pero no resuelve una dependencia actual | descartar para integración |
| `ReminderTriggered` | mezcla potencialmente backend con entrega de notificación Android | rechazar |

La auditoría permite distinguir entre:

- hechos reales del dominio;
- eventos técnicamente posibles;
- eventos útiles para integración;
- eventos inventados o innecesarios para el problema actual.

---

## 9. Contrato experimental UserRegisteredV1

El contrato propuesto para el Spike 1 es:

`UserRegisteredV1`

Contenido mínimo:

| Campo | Tipo conceptual | Propósito |
|---|---|---|
| `userId` | Long | identificar el usuario registrado |
| `occurredAt` | Instant | registrar el momento del hecho |

Semántica:

> Un usuario fue persistido correctamente por Identity.

El evento no debe publicarse antes de que la creación del usuario haya sido confirmada.

Activities será responsable de reaccionar al evento y realizar el aprovisionamiento de categorías.

---

## 10. Spike 1 — integración UserRegisteredV1

### 10.1 Pregunta del spike

¿La creación de categorías predeterminadas puede desacoplarse de Identity mediante un evento interno sin introducir una complejidad o una inconsistencia funcional que no se justifique para RachaPro?

### 10.2 Hipótesis

Si la llamada síncrona:

`Identity -> DefaultCategoryProvisioning`

se reemplaza experimentalmente por la publicación de:

`UserRegisteredV1`

y Activities procesa ese evento después del registro del usuario, entonces será posible eliminar la dependencia directa de Identity hacia el contrato de Activities manteniendo el comportamiento observable de creación de categorías predeterminadas.

El experimento acepta una ventana corta de consistencia eventual.

### 10.3 Alcance

El Spike 1 modifica exclusivamente el flujo:

`registro de usuario -> creación de categorías predeterminadas`

Incluye:

- publicación de un evento interno;
- consumidor dentro de Activities;
- medición de la aparición de las categorías;
- observación de la consistencia eventual;
- ejecución de ArchUnit.

### 10.4 Fuera de alcance

El Spike 1 no incluye:

- Kafka;
- RabbitMQ;
- Pub/Sub;
- microservicios;
- comunicación de red entre módulos;
- separación de bases de datos;
- cambios al cliente Android;
- cambios a Focus;
- cambios a Reminders;
- cambios a Progress;
- reemplazar todos los contratos síncronos por eventos;
- convertir el spike directamente en arquitectura productiva.

---

## 11. Criterios de éxito

El Spike 1 se considerará técnicamente favorable si se observa que:

1. el usuario puede registrarse correctamente;
2. Identity deja de depender directamente de `DefaultCategoryProvisioning` dentro del spike;
3. Activities recibe el hecho `UserRegisteredV1`;
4. se crean las categorías predeterminadas;
5. las categorías aparecen dentro de una ventana local experimental de máximo 2 segundos;
6. no se modifica la API HTTP pública;
7. ArchUnit permanece en verde;
8. el cambio puede revertirse sin modificar el modelo de datos;
9. la complejidad introducida puede describirse y justificarse.

El umbral de 2 segundos es un criterio experimental definido para el spike y no una métrica histórica del sistema.

El cumplimiento de estos puntos no obliga a adoptar la solución.

---

## 12. Criterios de ajuste o reversión

La hipótesis podrá declararse **AJUSTADA** o **REVERTIDA** aunque el código funcione.

Debe reconsiderarse la solución si se observa:

- complejidad desproporcionada frente al acoplamiento eliminado;
- categorías no disponibles cuando el flujo funcional las necesita;
- dificultad para manejar fallos del consumidor;
- riesgo de procesamiento duplicado;
- necesidad de infraestructura adicional no justificada;
- comportamiento menos comprensible que la llamada síncrona actual.

---

## 13. Mediciones del Spike 1

Se registrará como mínimo:

- commit evaluado;
- fecha;
- entorno;
- respuesta del registro de usuario;
- instante de creación del usuario;
- instante en que aparecen las categorías;
- diferencia temporal observada;
- categorías creadas;
- resultado ArchUnit;
- dependencia Identity -> Activities antes;
- dependencia Identity -> Activities durante el spike;
- comportamiento observado ante error del consumidor si puede probarse dentro del tiempo disponible.

Se propone realizar al menos tres ejecuciones válidas del flujo principal.

---

## 14. Rama y evidencia prevista

El Spike 1 se ejecutará en una rama separada:

`spike/01-user-registered-integracion`

La evidencia se conservará en:

`experimentos/spike-01-integracion/`

El experimento deberá registrar:

- hipótesis;
- implementación realizada;
- ejecución;
- resultados;
- observaciones;
- limitaciones;
- veredicto.

El veredicto deberá ser uno de:

- VALIDADA;
- AJUSTADA;
- REVERTIDA.

---

## 15. Relación con CQRS y consistencia eventual

Semana 9 no adopta CQRS.

El Spike 1 permitirá evaluar una forma limitada de consistencia eventual:

1. Identity confirma la creación del usuario.
2. Activities procesa posteriormente `UserRegisteredV1`.
3. Las categorías aparecen después del commit del usuario.

Esta diferencia temporal deberá medirse.

La existencia de este evento no demuestra por sí sola que RachaPro necesite CQRS.

La aplicabilidad real de CQRS y consistencia eventual se evaluará después de observar el resultado del spike.

---

## 16. Decisión de Semana 9

Se mantienen síncronos:

- `Focus -> ActivityLookup`
- `Reminders -> ActivityLookup`

porque ambos consumidores necesitan una respuesta inmediata.

Se mantiene como arquitectura productiva vigente:

- `Identity -> DefaultCategoryProvisioning`

hasta conocer el resultado del Spike 1.

Se selecciona para evaluación experimental:

- `UserRegisteredV1`

como posible sustitución de la dependencia síncrona de aprovisionamiento inicial.

No se adopta todavía una arquitectura orientada a eventos.

---

## 17. Estado al cierre de Semana 9

### Hecho verificado

- existen módulos alineados con las capacidades principales del monolito modular;
- Activities posee los contratos públicos actualmente utilizados entre módulos;
- Focus y Reminders necesitan consultas síncronas;
- Identity utiliza Activities para aprovisionar categorías iniciales.

### Decisión actual

- conservar las integraciones síncronas vigentes;
- experimentar únicamente con el flujo Identity -> Activities.

### Hipótesis pendiente de prueba

`UserRegisteredV1` podría reducir la dependencia directa de Identity hacia Activities sin deteriorar el comportamiento esperado.

### Evidencia siguiente

`experimentos/spike-01-integracion/`

### Decisión definitiva

Pendiente del resultado del Spike 1 de Semana 10.
