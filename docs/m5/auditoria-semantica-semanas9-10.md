# Paquete de evidencia para auditoría semántica — Semanas 9 y 10

HEAD auditado:

305dd61465c7a7e53fd7e1fdab76e73cd575bc72

Branch:

fix/m5-correcciones-documentales

## Tipo de artefacto

Este archivo es un **paquete de evidencia**, no el veredicto de la auditoría.

> **Contexto temporal:** esta auditoría y su paquete de evidencia corresponden
> al trabajo realizado durante las **Semanas 9 y 10** del proyecto.
> Los identificadores `AS-01` a `AS-15` representan controles internos de la
> auditoría semántica y **no corresponden a semanas académicas**.
> El proyecto no ha alcanzado todavía las Semanas 11 a 15.


Su función es reunir fragmentos y trazas relevantes para AS-01–S15 de forma
reproducible. La aparición de una línea en este archivo no implica que la
afirmación haya sido aceptada, refutada o corregida.

El juicio semántico posterior se registra separadamente en:

`docs/m5/veredicto-semantico-final.md`

Esto evita confundir extracción automatizada de evidencia con revisión semántica
y preserva la separación entre:

- evidencia;
- análisis;
- hallazgo;
- decisión del equipo.

## Regla

Este informe recopila evidencia primaria para una lectura semántica independiente.

No reutiliza como veredicto automático los estados de auditorías previas.

No modifica decisiones arquitectónicas pendientes.

## AS-01 — Dominio y fronteras

### docs/dominio/subdominios.md
L1: # Subdominios y bounded contexts
L7: Su propósito es sintetizar las responsabilidades protegidas, conceptos principales, ownership, dependencias y exclusiones de los bounded contexts identificados, sin repetir el detalle técnico completo registrado en los documentos auxiliares de evidencia.
L18: - `docs/dominio/responsabilidades-contextos.md`
L26: La delimitación de los bounded contexts no se realizó únicamente a partir de nombres de paquetes, clases, tablas, pantallas o módulos técnicos.
L30: - responsabilidad protegida;
L33: - ownership sobre conceptos y decisiones;
L36: - posibilidad de evolución independiente mientras se mantengan los contratos necesarios con otras capacidades.
L38: La existencia de una dependencia entre dos capacidades no implica automáticamente transferencia de ownership.
L40: De igual forma, la identificación de un bounded context no implica que cada contexto deba desplegarse como un microservicio independiente.
L45: bounded context
L66: ### Responsabilidad protegida
L72: Esta responsabilidad corresponde a una `DECISIÓN DEL EQUIPO` sustentada en las reglas y conceptos observados en:
L123: Activities conserva el ownership sobre estos conceptos incluso cuando otros bounded contexts utilizan una referencia a `Activity`.
L137: Activities utiliza información asociada al usuario para determinar ownership y limitar las operaciones a los datos correspondientes a dicho usuario.
L175: La relación con otros contextos mediante identificadores o contratos de consulta no transfiere esas responsabilidades a Activities.
L177: El ownership definitivo de Achievement tampoco se asigna a Activities en esta etapa.
L187: - conceptos con ownership definido;
L189: - responsabilidad funcional diferenciable;
L190: - capacidad de ser referenciado por otras responsabilidades sin transferir ownership.
L195: Activities como bounded context = DECIDIDO
L200: La decisión no se basa únicamente en la existencia de un paquete o módulo llamado `activities`, sino en la cohesión funcional de las reglas y responsabilidades identificadas.
L206: ### Responsabilidad protegida
L212: Esta responsabilidad corresponde a una `DECISIÓN DEL EQUIPO` sustentada en la evidencia registrada en:
L257: Focus conserva el ownership sobre el ciclo de vida de `PomodoroSession`.
L259: Una sesión puede contener una referencia opcional a `Activity`, pero dicha referencia no transfiere ownership sobre Activity a Focus.
L273: ### Responsabilidad protegida
L279: Esta responsabilidad corresponde a una `DECISIÓN DEL EQUIPO` sustentada en la evidencia registrada en:
L335: Reminders conserva el ownership sobre el ciclo de vida funcional de `Reminder`.
L337: La referencia opcional a Activity no transfiere ownership sobre Activity a Reminders.
L351: ### Responsabilidad protegida
L357: Esta responsabilidad corresponde a una `DECISIÓN DEL EQUIPO` sustentada en la evidencia registrada en:
L407: → ownership de Activities
L410: → ownership de Focus
L418: ## 7. Capacidades con ownership no decidido
L422: Achievement corresponde a una capacidad observada en la implementación, pero su ownership definitivo no fue asignado durante la delimitación de las fronteras principales.

### docs/dominio/responsabilidades-contextos.md
L1: # Responsabilidades por bounded context
L5: Este documento compara las responsabilidades de los bounded contexts ya definidos para RachaPro, haciendo explícito qué conceptos y reglas posee cada contexto, qué información consume o referencia de otras capacidades y qué responsabilidades deben permanecer fuera de su frontera.
L7: Su propósito es servir como insumo para la construcción posterior del `context-map.puml`. Por tanto, este documento describe límites de responsabilidad y ownership, pero no decide todavía mecanismos concretos de integración, protocolos de comunicación ni patrones de relación entre contextos.
L13: En este documento, **Posee** identifica los conceptos y reglas cuya responsabilidad pertenece conceptualmente al bounded context. **Consume / referencia** identifica información proveniente de otras capacidades que el contexto utiliza sin adquirir ownership sobre ella. **No posee** hace explícitas las responsabilidades que deben permanecer fuera de su frontera.
L19: `Achievement` permanece fuera de la matriz principal porque su ownership continúa como `NO DECIDIDO`; por ello, no se considera todavía un bounded context confirmado dentro de este documento.
L23: ## 3. Matriz comparativa de responsabilidades
L25: | Contexto | Responsabilidad protegida | Posee | Consume / referencia | No posee |
L30: | Progress | Interpretar y agregar los resultados producidos por otras capacidades del sistema para medir y representar el avance del usuario mediante rachas y métricas de progreso. | Interpretación de progreso, racha actual, mejor racha y métricas agregadas. | Hechos producidos por Activities y Focus. | `Activity`, `PomodoroSession`, `Reminder`, Identity y Achievement (`ownership` no decidido; no se asigna a Progress en esta etapa). |
L36: ### Responsabilidad protegida
L62: - ownership definitivo de Achievement.
L68: ### Responsabilidad protegida
L85: La referencia a Activity no transfiere ownership sobre ella a Focus.
L98: - ownership definitivo de Achievement.
L104: ### Responsabilidad protegida
L125: La consulta de Activity no transfiere ownership sobre Activities a Reminders.
L138: - ownership definitivo de Achievement.
L144: ### Responsabilidad protegida
L174: El consumo de esta información no transfiere ownership sobre `Activity` ni sobre `PomodoroSession`.
L190: - Achievement (`ownership` no decidido; no se asigna a Progress en esta etapa).
L198: Achievement corresponde a una **capacidad observada** en la implementación, pero su ownership definitivo permanece `NO DECIDIDO`.
L209: Sin embargo, esta evidencia no permite concluir todavía que Achievement constituya un bounded context independiente ni que deba pertenecer a Activities, Focus o Progress.
L222: ownership
L223: → NO DECIDIDO
L226: La decisión sobre Achievement deberá cerrarse en un análisis específico de ownership, fuente de verdad, persistencia y relación entre Android y backend.
L237: - consumir o referenciar información de otro contexto no transfiere ownership;
L242: - las relaciones del mapa deberán distinguir entre ownership del dato o concepto y uso de información producida por otro contexto;
L243: - `Achievement` no aparecerá como bounded context confirmado mientras su ownership permanezca `NO DECIDIDO`.

### docs/dominio/justificacion-contextos.md
L1: # Justificación de bounded contexts
L5: Este documento justifica las fronteras de bounded context definidas para RachaPro
L8: - las responsabilidades conceptuales identificadas;
L13: Su objetivo no es redefinir los contextos ni proponer nuevos bounded contexts,
L25: Para cada bounded context se contrastan tres niveles:
L28: responsabilidad conceptual
L35: La responsabilidad conceptual describe la capacidad que el equipo decidió
L51: bounded context
L55: bounded context
L59: transferencia de ownership
L88: - `docs/dominio/subdominios.md`;
L89: - `docs/dominio/responsabilidades-contextos.md`;
L106: subdominios.md
L109: responsabilidades-contextos.md
L110: → ownership, consumo y responsabilidades
L113: → síntesis de las relaciones entre bounded contexts
L127: ### Responsabilidad conceptual
L190: la razón para delimitar el bounded context.
L232: En la evidencia inspeccionada no se observó que Reminders adquiera ownership
L253: propia responsabilidad.
L265: transferencia de ownership.
L288:   sin que se haya observado ownership sobre Activity.
L292:   responsabilidad funcional coherente y diferenciable.
L295: → Activities se mantiene como bounded context independiente.
L308: responsabilidad de progreso.
L330: del bounded context corresponde a una decisión de modelado basada en
L331: responsabilidad, reglas y ownership.
L340: Activities como bounded context = DECIDIDO
L349: ### Responsabilidad conceptual
L424: Esta precisión no modifica el ownership de Focus sobre la sesión.
L445: En la evidencia inspeccionada no se observó transferencia de ownership sobre
L493:   sin que se haya observado ownership sobre PomodoroSession.
L496: → estos conceptos y reglas forman una responsabilidad
L500: → Focus se mantiene como bounded context independiente.
L508: responsabilidad de Focus.


## AS-02 — Context Map y relaciones

L7: rectangle "Activities\n<<Bounded Context>>" as Activities {
L10: rectangle "Focus\n<<Bounded Context>>" as Focus {
L13: rectangle "Reminders\n<<Bounded Context>>" as Reminders {
L16: rectangle "Progress\n<<Bounded Context>>" as Progress {
L23: Focus --> Activities : consulta y valida referencia opcional a Activity\ncuando existe activityId
L25: Reminders --> Activities : consulta y valida referencia opcional a Activity\nmediante ActivityLookup cuando existe activityId
L27: Progress --> Activities : consume hechos y agregados de completitud producidos por Activities\n(días y cantidades de Activities completadas)
L29: Progress --> Focus : consume hechos y agregados de completitud producidos por Focus\n(días, cantidades y duración agregada de sesiones FOCUS)
L37:   | A --> B | A consume o referencia información de B |
L42:   La dirección de la flecha indica quién consume
L45:   Consumir o referenciar información
L57:   Achievement no se representa como bounded context


## AS-03 — ActivityLookup y contratos

### docs/integracion/contrato-api.md
L1: # Contrato de API intermodular de Activities para Focus y Reminders
L3:
L7: En el estado actual observado del backend, `ActivityLookup` es consumido por al
L10: - Focus;
L11: - Reminders.
L19:
L25: bounded context Activities ofrece a Focus una capacidad de validación de
L49: Focus → Activities
L58: Focus
L62: El objetivo del contrato es permitir que Focus determine si una Activity
L93: Focus
L96: Focus consume la capacidad ofrecida por Activities cuando necesita crear una
L99: Focus no adquiere ownership sobre Activity ni sobre su persistencia.
L112: Esta capacidad se utiliza únicamente cuando Focus ya dispone de un
L115: La opcionalidad de la asociación pertenece al flujo de creación de
L124: ActivityLookup.activityId
L128: Si la `PomodoroSession` no contiene `activityId`, Focus no invoca este contrato.
L137: interface ActivityLookup {
L138:     fun existsActiveActivityForUser(
L148: existsActiveActivityForUser
L155: 2. pertenece al usuario indicado
L156: 3. no está eliminada
L159: El término `Active` presente en el nombre del método no debe interpretarse como
L181: pertenece al usuario
L183: no está eliminada
L201: `activityId` identifica la Activity cuya referencia desea validar Focus.
L206: activityId debe estar presente cuando se invoca ActivityLookup
L215: → Focus no invoca ActivityLookup
L218: → Focus puede invocar ActivityLookup
L266: pertenece al usuario
L268: no está eliminada
L271: Por tanto, Focus puede considerar válida la referencia desde el punto de vista
L288: Activity eliminada
L333: → Focus no considera exitosa la operación que dependía de la validación
L353: Este contrato define la semántica que debe observar Focus cuando ocurre un fallo
L406: - determinar si la Activity pertenece al usuario.
L407: - determinar si la Activity no está eliminada.
L416: Activities no transfiere ownership de Activity a Focus.
L420: ## 9. Responsabilidades de Focus
L422: Focus es responsable de:
L425: - no invocar `ActivityLookup` cuando no existe `activityId`.
L436: Focus no debe reproducir por su cuenta la lógica interna de Activities para
L481: Focus
L482: → invoca ActivityLookup
L484: → Focus recibe el resultado o el fallo
L502: Un cambio es compatible cuando puede introducirse sin obligar a Focus a cambiar
L539: Se considera incompatible un cambio que obligue a Focus a modificar la forma en
L549: - agregar una nueva entrada obligatoria que Focus deba proporcionar.
L553:   cuya interpretación exija modificar Focus.
L555: - exigir a Focus conocer detalles internos de Activities.

### docs/dominio/evidencia/activities.md
L3:
L5: ## Actualización posterior — semántica de ActivityLookup
L7: La tensión registrada originalmente alrededor del término `active` fue
L12: Para el contrato vigente documentado, `active` no significa exclusivamente
L18: - pertenece al usuario indicado;
L19: - no está eliminada.
L21: El backend muestra consumidores actuales de `ActivityLookup` en Focus y
L22: Reminders.
L27:
L97: - `isActive`
L140: - la prioridad debe pertenecer a `LOW`, `MEDIUM` o `HIGH`
L142: - la Category debe pertenecer al usuario
L160: - debe pertenecer al usuario
L161: - no debe estar eliminada
L163: - la prioridad debe pertenecer a `LOW`, `MEDIUM` o `HIGH`
L164: - la Category debe existir, pertenecer al usuario y estar activa
L179: - debe pertenecer al usuario
L180: - no debe estar eliminada
L269: - una Category nueva comienza con `isActive = true`
L275: `CategoryService.findActiveByUserId(...)`
L327: - no esté eliminada
L416: - Activity eliminada lógicamente
L423: - Subtask eliminada
L462: - comprobar que una Activity pertenece al usuario
L484: - pertenecer al mismo usuario
L507: ### Focus
L509: Focus consulta actualmente información de Activities mediante el contrato:
L511: `ActivityLookup`
L515: `existsActiveActivityForUser(activityId, userId)`
L519: `ActivityLookupService.existsActiveActivityForUser(...)`
L525: - no esté eliminada
L530: Focus ──> ActivityLookup ──> Activities
L533: Focus no necesita acceder directamente a `ActivityRepository`.
L565: ### Reminders
L567: Existe evidencia arquitectónica previa de una relación entre Reminders y
L568: Activities mediante `ActivityLookup`.
L572: directamente el consumidor actual dentro de Reminders.
L576: La relación deberá cerrarse durante la auditoría específica de Reminders.
L630: ## 7. Qué explícitamente no pertenece
L636: - estados como `RUNNING`, `PAUSED` o `CANCELLED` de Focus
L640: - programación y ejecución de Reminders
L660: ### Semántica de `active` en `ActivityLookup`
L664: `existsActiveActivityForUser(activityId, userId)`
L668: `ActivityLookupService.existsActiveActivityForUser(...)`
L678: - no esté eliminada
L688: `active`
L692: `existente + del usuario + no eliminada`
L706: ### Activity completada y posteriormente eliminada
L719: eliminados.
L721: Por tanto, una Activity completada y posteriormente eliminada puede continuar

### docs/dominio/evidencia/focus.md
L1: # Evidencia de dominio — Focus
L5: Focus gestiona el ciclo de vida de sesiones Pomodoro y sus estados asociados.
L12: - una sesión puede ser `FOCUS`, `SHORT_BREAK` o `LONG_BREAK`
L15: - cuando existe `activityId`, Focus consulta Activities mediante `ActivityLookup`
L35: - `FOCUS`
L46: Estos conceptos aparecen asociados al comportamiento de Focus en
L71: - cuando se proporciona `activityId`, se consulta `ActivityLookup`
L73: También se observó en `PomodoroSessionDao.observeCompletedFocusDays(userId)` que
L77: - `type = 'FOCUS'`
L105: No se ha demostrado que Focus publique eventos explícitos de integración como
L114: Focus utiliza:
L118: - `ActivityLookup` cuando existe una referencia a Activity
L133: `ActivityLookup`
L140: Focus ──> ActivityLookup ──> Activities
L143: Focus no necesita acceder directamente a `ActivityRepository` para realizar esa
L156: Progress utiliza información derivada de sesiones `FOCUS + COMPLETED`,
L160: - cantidad de sesiones Focus completadas
L164: Focus produce los datos fuente y Progress les aplica reglas de agregación e
L171: ## 7. Qué explícitamente no parece pertenecerle
L173: Según la ubicación y comportamiento de las reglas observadas, Focus no controla:
L182: - gestión de Reminders
L186: - no se observa que Focus controle su ciclo de vida ni sus reglas de desbloqueo
L205: por Focus coincide con la utilizada posteriormente por Progress para interpretar
L210: ### Duración de Focus
L218: - `type = 'FOCUS'`
L244: 4. relación entre tiempo pausado y métricas de Focus
L245: 5. reglas específicas diferenciadas entre `FOCUS`, `SHORT_BREAK` y `LONG_BREAK`
L246: 6. existencia de eventos de integración explícitos producidos por Focus
L261: | Uso de `ActivityLookup` | `HECHO DEL REPOSITORIO` |
L262: | Días `FOCUS + COMPLETED` usados por Progress | `HECHO DEL REPOSITORIO` |
L267: | Eventos explícitos publicados por Focus | `INFORMACIÓN FALTANTE` |
L277: - Focus mantiene reglas propias para el ciclo de vida de `PomodoroSession`
L278: - los tipos `FOCUS`, `SHORT_BREAK` y `LONG_BREAK`, junto con estados como
L285:   Activities no define por completo la existencia de Focus
L291: El equipo decide modelar `Focus` como un bounded context independiente.
L294: llamado `focus`, sino en la presencia de:
L299: - una responsabilidad protegida diferenciable de Activities, Progress y Reminders
L305: Focus protege la gestión del ciclo de vida de una sesión Pomodoro.
L323: No pertenece a Focus:
L330: - gestión de Reminders
L334: Por tanto, este documento solamente establece que Focus no protege actualmente
L342: `ActivityLookup` para consultar información perteneciente a Activities.
L344: Esta dependencia no transfiere a Focus el ownership de Activity.
L351: Progress consume datos producidos por Focus, especialmente información de
L352: sesiones `FOCUS + COMPLETED`.
L368: 4. las reglas propias de Focus desaparecen hasta convertirse únicamente en una
L371:    pertenecen semánticamente a otra responsabilidad
L374: equipo**, se mantiene la separación de Focus como frontera de dominio.
L378: `Focus como bounded context = DECIDIDO`

### docs/dominio/evidencia/reminders.md
L1: # Evidencia de dominio — Reminders
L10: - `ReminderService`
L23: `ReminderService` controla operaciones relacionadas con:
L26: - consulta de Reminders del usuario
L31: recuperar y materializar Reminders.
L37: - `ReminderScheduler`
L51: forman una capacidad funcional coherente y distinguible de Activities, Focus,
L56: La capacidad que la frontera Reminders protege se formula como:
L107: - `ReminderScheduler`
L147: `backend/src/main/kotlin/com/example/rachapro/backend/reminders/reminder/ReminderService.kt`
L153: `ReminderService.create(...)`
L161: - cuando existe `activityId`, se consulta `ActivityLookup`
L179: del comportamiento de `ReminderService.create(...)`.
L183: Cuando `activityId` tiene valor, `ReminderService` invoca:
L185: `ActivityLookup.existsActiveActivityForUser(activityId, userId)`
L195: → Reminders no consulta Activities
L198: → Reminders consulta ActivityLookup
L207: `ReminderService.markDelivered(...)`
L230: `ReminderService.cancel(...)`
L310: ### ReminderScheduler
L314: `app/src/main/java/com/example/rachapro/notifications/ReminderScheduler.kt`
L318: `ReminderScheduler.schedule(...)` comprueba que:
L326: `ReminderScheduleResult.InvalidTime`
L360: - recupera Reminders con estado `SCHEDULED`
L363: - vuelve a programar los Reminders cuyo momento todavía está en el futuro
L396: `ReminderReceiver` pertenece al mecanismo técnico mediante el cual Android
L400: Reminders.
L458: Reminders utiliza `userId` para:
L460: - consultar Reminders
L464: - recuperar Reminders programados
L466: El uso de `userId` no implica que Reminders posea autenticación o identidad.
L477: `ReminderService.create(...)`.
L485: Cuando existe referencia a una Activity, Reminders consulta:
L487: `ActivityLookup`
L491: Esto no transfiere a Reminders ownership sobre Activity.
L497: Reminders consume un momento de activación representado mediante:
L525: Reminders depende condicionalmente del contrato:
L527: `ActivityLookup`
L532: Reminders ──> ActivityLookup ──> Activities
L549: Reminders depende siempre de Activities
L555: Reminders consulta Activities únicamente cuando necesita validar
L566: Reminders utiliza `userId`.
L579: Esto no demuestra que Reminders posea:
L589: ### Focus
L592: directa entre Reminders y Focus necesaria para el ciclo funcional del Reminder.
L612: ## 7. Qué explícitamente no pertenece
L616: Según la decisión de modelado adoptada, Reminders no protege:
L629: de Reminders.
L684: Tanto `ReminderRepository` como `ReminderScheduler` comprueban el valor de
L728: En `ReminderService` del backend inspeccionado, los métodos:


## AS-04 — UserRegisteredV1 y AsyncAPI

### docs/integracion/eventos-candidatos.md
L5: ## Actualización posterior — estado de UserRegisteredV1
L11: Posteriormente, ADR-003 adoptó `UserRegisteredV1` específicamente para la
L12: integración post-registro entre Identity y Activities.
L16: - `UserRegisteredV1`: adoptado e implementado para ese caso específico;
L31: - `occurredAt`.
L124: Activities
L139: Focus → Activities
L140: Reminders → Activities
L141: Progress → Activities
L150: A consume o referencia información de B
L348: nadie consume actualmente el hecho
L355: # 6. Activities
L361: Una nueva `Activity` pasó a existir dentro de Activities.
L370: Activities
L409: Activities mientras no exista evidencia de otro contexto que necesite reaccionar
L422: La operación de completitud de Activities registra información asociada al
L425: Progress consume información derivada de la completitud de Activities para sus
L431: Activities
L474: Progress consume información derivada de esa completitud
L480: No se concluye todavía que la relación Activities → Progress deba implementarse
L493: La lógica de Activities contempla actualización del estado de una Activity
L499: Activities
L528: El cambio de estado está respaldado dentro de Activities.
L548: Activities
L576: La transición existe en el dominio de Activities, pero no existe evidencia de
L589: Activities utiliza eliminación lógica mediante una condición equivalente a:
L598: Activities
L634: Focus utiliza actualmente una capacidad síncrona de Activities para validar una
L650: Existe una operación real de completitud de Subtask dentro de Activities.
L655: Activities
L684: El hecho existe dentro del dominio de Activities.
L704: Activities
L732: El cambio pertenece al dominio de Activities.
L742: Una `Category` pasó a existir dentro de Activities.
L746: Activities posee operaciones relacionadas con creación y aprovisionamiento de
L752: Activities
L781: La creación de una Category es un hecho válido de Activities.
L944: Progress consume información correspondiente a sesiones que cumplen:
L1057: Progress consume sesiones que cumplen:
L1430: Progress agrega información proveniente de Activities y Focus.
L1659: # 10. Identity
L1661: ## 10.1 UserRegisteredV1
L1669: `UserRegisteredV1` existe actualmente como evento en el sistema.
L1671: Activities lo consume mediante:
L1674: UserRegisteredV1Listener
L1690: consumo por Activities
L1704: Activities
L1730: A diferencia de la mayoría de candidatos generados, `UserRegisteredV1` no es
L1733: Existe actualmente como evento y Activities reacciona a él.
L1745: `UserRegisteredV1` pertenece al estado actual de M5.
L1770: UserRegisteredV1
L1975: # 13. Relación con Focus → Activities
L1981: Focus → Activities
L2012: Activities ni reaccionar a una publicación asíncrona.
L2021: Progress consume información proveniente de:
L2024: Activities
L2031: completitud de Activities
L2058: Progress consume información agregada de sesiones de Focus.
L2108: quién lo consume
L2118: # 17. Precisión sobre UserRegisteredV1

### docs/asyncapi/rachapro-events-v1.yaml
L7:     UserRegisteredV1 es actualmente un Spring ApplicationEvent en proceso;
L15:     title: UserRegisteredV1
L17:       Canal lógico que representa el evento interno UserRegisteredV1.
L18:       La implementación actual utiliza ApplicationEventPublisher de Spring
L21:       UserRegisteredV1:
L22:         $ref: '#/components/messages/UserRegisteredV1'
L25:   publishUserRegisteredV1:
L27:     title: Identity publica UserRegisteredV1
L30:       UserService publica UserRegisteredV1 como parte del flujo de registro.
L31:       El consumidor está configurado para ejecutarse AFTER_COMMIT.
L35:       - $ref: '#/channels/userRegistered/messages/UserRegisteredV1'
L37:   receiveUserRegisteredV1:
L39:     title: Activities consume UserRegisteredV1
L42:       UserRegisteredV1Listener recibe el evento mediante
L43:       TransactionalEventListener con fase AFTER_COMMIT y ejecución Async.
L48:       - $ref: '#/channels/userRegistered/messages/UserRegisteredV1'
L52:     UserRegisteredV1:
L53:       name: UserRegisteredV1
L55:       summary: Informa que Identity confirmó el registro de un usuario.
L62:           - occurredAt
L68:           occurredAt:
L73:         - name: UserRegisteredV1Example
L77:             occurredAt: '2026-09-30T22:00:00Z'
L80:   implementation: Spring ApplicationEventPublisher
L85: x-rachapro-consumer:
L86:   module: Activities
L87:   listener: UserRegisteredV1Listener
L88:   transactionPhase: AFTER_COMMIT

### docs/adr/0003-integracion-eventos-internos.md
L16:     Identity
L17:     Activities
L26: En particular, ADR-002 definió inicialmente que Identity solicitaría a Activities la creación de categorías predeterminadas mediante el contrato síncrono:
L40: Cuando se registra un usuario, Activities debe crear sus categorías predeterminadas.
L44:     Identity
L51:     Activities
L53: Aunque la dependencia atravesaba una API pública válida y no violaba el ownership de persistencia, Identity continuaba coordinando directamente una operación perteneciente a Activities.
L57: > ¿Puede Identity comunicar el hecho de que un usuario fue registrado sin solicitar directamente a Activities el aprovisionamiento de categorías, manteniendo el comportamiento funcional esperado?
L87: Identity continúa invocando directamente el contrato público de Activities.
L99: - Identity continúa coordinando explícitamente una operación de Activities;
L109: Identity publica un evento de dominio o integración dentro del mismo proceso y Activities lo consume sin ejecución asíncrona.
L129: Identity persiste el usuario y publica:
L131:     UserRegisteredV1
L133: Activities reacciona al evento mediante un listener ejecutado después del commit.
L137:     @TransactionalEventListener(AFTER_COMMIT)
L142: - Identity deja de solicitar directamente el aprovisionamiento;
L143: - Activities conserva ownership sobre las categorías;
L189: La hipótesis evaluada fue comprobar si el registro podía desacoplar la dependencia directa de Identity hacia Activities mediante `UserRegisteredV1`, manteniendo el comportamiento funcional esperado.
L260: Se adopta `UserRegisteredV1` como mecanismo interno para comunicar desde Identity hacia Activities que un nuevo usuario ha sido registrado.
L264:     Identity
L268:        | publica UserRegisteredV1
L273:     Activities
L275:        | AFTER_COMMIT + @Async
L279: Identity deja de invocar directamente `DefaultCategoryProvisioning` para este flujo.
L281: Activities continúa siendo propietario de:
L287: El evento comunica un hecho ocurrido y no transfiere ownership de negocio hacia Identity.
L295:     Identity -> UserRegisteredV1 -> Activities
L308: continúan utilizando el contrato público síncrono de Activities.
L330:     Identity -> DefaultCategoryProvisioning -> Activities
L334:     Identity -> UserRegisteredV1 -> Activities
L363: La introducción de `UserRegisteredV1` resuelve un caso específico de integración entre módulos y no constituye por sí sola CQRS.
L377: - menor coordinación directa desde Identity hacia Activities;
L378: - ownership de categorías conservado en Activities;
L418: Validar `UserRegisteredV1` no demuestra que todas las relaciones entre módulos deban convertirse en eventos.
L502: **UserRegisteredV1:** IMPLEMENTADO
L504: **Listener en Activities:** IMPLEMENTADO
L506: **Procesamiento AFTER_COMMIT:** IMPLEMENTADO
L603: SPIKE-01 evaluó si el registro podía desacoplar la dependencia directa de Identity hacia Activities mediante:
L606: UserRegisteredV1
L646: feat(exp): validate activities pagination performance
L761: Identity
L763: UserRegisteredV1
L765: Activities
L790: UserRegisteredV1
L796: Identity
L797: → publica UserRegisteredV1
L799: Activities
L800: → consume UserRegisteredV1
L806: AFTER_COMMIT
L845: UserRegisteredV1
L863: no se observó replay de UserRegisteredV1
L924: UserRegisteredV1
L929: `UserRegisteredV1` posee una situación distinta a candidatos meramente potenciales porque existe evidencia del repositorio de:
L934: consumo por Activities
L942: UserRegisteredV1 adoptado
L1025: UserRegisteredV1
L1119: - cambios en `UserRegisteredV1`.
L1131: 1. sustituir para este flujo la publicación y consumo de `UserRegisteredV1`;
L1132: 2. restablecer una invocación síncrona mediante una API pública de Activities;
L1133: 3. volver a conectar Identity con el mecanismo equivalente a `DefaultCategoryProvisioning`;


## AS-05 — ADR-003 y evidencia previa

L4: - **Fecha de decisión:** 2026-09-30
L7: - **Evidencia principal:** `experimentos/spike-01-integracion/`
L32: ADR-002 también estableció explícitamente que una modificación futura hacia procesamiento asíncrono o consistencia eventual requeriría una nueva decisión arquitectónica sustentada en evidencia.
L61: ## 3. Drivers de la decisión
L63: Para esta decisión se consideran relevantes:
L77: La decisión debe permanecer compatible con el monolito modular establecido en ADR-001.
L131:     UserRegisteredV1
L167: No existe evidencia actual que justifique introducir:
L176: **Decisión:** no adoptada para este caso.
L180: ## 5. Evidencia experimental
L182: La decisión se apoya en:
L184:     experimentos/spike-01-integracion/README.md
L185:     experimentos/spike-01-integracion/resultados.csv
L189: La hipótesis evaluada fue comprobar si el registro podía desacoplar la dependencia directa de Identity hacia Activities mediante `UserRegisteredV1`, manteniendo el comportamiento funcional esperado.
L258: ## 8. Decisión
L260: Se adopta `UserRegisteredV1` como mecanismo interno para comunicar desde Identity hacia Activities que un nuevo usuario ha sido registrado.
L268:        | publica UserRegisteredV1
L291: ## 9. Alcance de la decisión
L293: Esta decisión se aplica específicamente a:
L295:     Identity -> UserRegisteredV1 -> Activities
L326: ADR-003 modifica únicamente la decisión correspondiente al aprovisionamiento de categorías después del registro.
L334:     Identity -> UserRegisteredV1 -> Activities
L342: La decisión introduce una ventana de consistencia eventual entre:
L363: La introducción de `UserRegisteredV1` resuelve un caso específico de integración entre módulos y no constituye por sí sola CQRS.
L375: La decisión produce:
L389: La decisión introduce:
L414: No existe evidencia de recuperación automática del evento después de una caída del proceso.
L418: Validar `UserRegisteredV1` no demuestra que todas las relaciones entre módulos deban convertirse en eventos.
L435: - evaluar reintentos, idempotencia o persistencia durable únicamente si evidencia futura demuestra su necesidad.
L439: ## 17. Limitaciones de la evidencia
L456: Por tanto, ADR-003 no puede utilizarse como evidencia de escalabilidad, tolerancia general a fallos o necesidad de arquitectura distribuida.
L460: ## 18. Criterios de re-decisión
L462: La decisión deberá revisarse si aparece evidencia de:
L475: Deberá realizarse una nueva evaluación basada en evidencia.
L498: **Decisión:** ACEPTADA
L502: **UserRegisteredV1:** IMPLEMENTADO
L522: La decisión queda respaldada por la evidencia del Spike 1 y limitada expresamente a las condiciones y alcance documentados.
L526: ## 21. Revisión posterior de evidencia — 2026-10-08
L532: La decisión original se conserva en el commit:
L535: c6ae059
L539: Esta sección fue incorporada posteriormente con el propósito de registrar evidencia, documentación y análisis producidos después de la decisión original.
L544: - no afirma que la evidencia posterior estuviera disponible el 2026-09-30;
L545: - no sustituye la evidencia utilizada para tomar la decisión original;
L546: - no convierte los análisis posteriores en justificación causal de la decisión original;
L547: - permite evaluar si evidencia obtenida después confirma, cuestiona o activa los criterios de re-decisión ya documentados.
L552: evidencia original
L554: SPIKE-01
L558: evidencia posterior
L566: evidencia posterior
L575: La evidencia experimental utilizada originalmente para ADR-003 corresponde a SPIKE-01.
L577: El cierre de dicha evidencia se encuentra en:
L580: 597e9c7
L587: experimentos/spike-01-integracion/README.md
L588: experimentos/spike-01-integracion/resultados.csv
L591: SPIKE-01 fue registrado antes de ADR-003.
L596: 597e9c7
L597: SPIKE-01
L599: c6ae059
L603: SPIKE-01 evaluó si el registro podía desacoplar la dependencia directa de Identity hacia Activities mediante:
L606: UserRegisteredV1
L622: SPIKE-01
L623: → evidencia experimental previa a ADR-003
L626: → decisión arquitectónica posterior a dicha evidencia
L629: La incorporación de nueva evidencia no altera esta relación histórica.
L660: c6ae059
L670: c6ae059
L676: git merge-base --is-ancestor 8d23268 c6ae059
L683: git merge-base --is-ancestor c6ae059 8d23268
L687: Por tanto, una versión de `context-map.puml` ya existía antes de la decisión original de ADR-003.
L689: Sin embargo, la existencia previa del archivo no demuestra por sí sola que el Context Map hubiera sido utilizado como evidencia causal para tomar ADR-003.
L691: En la documentación histórica de ADR-003, la evidencia principal declarada es:
L694: experimentos/spike-01-integracion/
L738: Los refinamientos posteriores del Context Map se utilizan como trazabilidad adicional para revisar las fronteras y dependencias entre contextos, pero no como justificación retrospectiva de la decisión tomada el 2026-09-30.
L763: UserRegisteredV1
L790: UserRegisteredV1
L797: → publica UserRegisteredV1
L800: → consume UserRegisteredV1
L816: implementación y decisión
L823: El AsyncAPI mejora la trazabilidad y explicitud del contrato existente, pero no se interpreta como evidencia que hubiera participado en la decisión original del 2026-09-30.
L827: ### 21.5 Evidencia posterior de resiliencia — SPIKE-02


## AS-06 — SPIKE-01

### experimentos/spike-01-integracion/README.md
L13: ## 2. Hipótesis
L17: El criterio experimental establece que las categorías predeterminadas deben estar disponibles dentro de una ventana local máxima de 2000 ms.
L43: | Corrida | User ID | HTTP | HTTP ms | Evento ms | Categorías exactas | Evento <= 2000 ms | Resultado |
L45: | 01 | 1508 | 201 | No comparable | 28 | PASS | PASS | PASS |
L46: | 02 | 1509 | 201 | 884 | 65 | PASS | PASS | PASS |
L47: | 03 | 1510 | 201 | 893 | 36 | PASS | PASS | PASS |
L48: | 04 | 1511 | 201 | 875 | 32 | PASS | PASS | PASS |
L58: `28, 65, 36, 32 ms`
L61: - Mínimo: 28 ms
L64: - Máximo: 65 ms
L65: - Corridas dentro del umbral de 2000 ms: 4/4
L69: `65, 36, 32 ms`
L72: - Mediana: 36 ms
L73: - Máximo: 65 ms
L91: Resultado:
L101: Resultado:
L115: ## 8. Criterios de éxito
L117: | Criterio | Resultado |
L123: | Categorías disponibles dentro de 2000 ms | PASS |
L139: Los resultados corresponden a ejecución local.
L155: ## 11. Veredicto
L159: La hipótesis experimental queda validada dentro de las condiciones probadas.
L161: `UserRegisteredV1` permitió desacoplar el flujo experimental entre Identity y Activities, conservar el registro del usuario, crear correctamente las categorías predeterminadas y cumplir el umbral local de 2000 ms en las cuatro ejecuciones observadas.
L163: Este veredicto corresponde exclusivamente al Spike 1 y no implica por sí mismo la adopción de mensajería externa, microservicios, CQRS ni eventos para todas las relaciones entre módulos.

### experimentos/spike-01-integracion/resultados.csv
L1: Run,UserId,HttpStatus,HttpMs,EventElapsedMs,CategoriesExact,EventUnder2000,Result,Notes
L2: 1,1508,201,,28,PASS,PASS,PASS,"HTTP no comparable por confirmacion interactiva de Invoke-WebRequest"
L3: 2,1509,201,884,65,PASS,PASS,PASS,""
L4: 3,1510,201,893,36,PASS,PASS,PASS,""
L5: 4,1511,201,875,32,PASS,PASS,PASS,""


## AS-07 — SPIKE-02

### experimentos/spike-02-resiliencia/README.md
L5: Evaluar experimentalmente qué ocurre cuando el consumidor de `UserRegisteredV1` falla después de que la transacción que registra al usuario ya fue confirmada.
L7: El objetivo específico es observar si la implementación actual ofrece recuperación automática mediante retry o replay del evento.
L42: Si el consumidor asíncrono falla después del commit del usuario y el evento no cuenta con persistencia durable ni mecanismo explícito de retry/replay, el usuario puede permanecer persistido mientras el efecto secundario esperado en Activities queda incompleto.
L53: - Fallo inducido únicamente sobre la inserción de categorías del usuario experimental.
L54: - El fallo fue retirado después de observar el comportamiento del consumidor.
L55: - Ventana de observación posterior a retirar el fallo: 5 segundos.
L56: - Segunda observación después de reiniciar el backend: 5 segundos.
L66: ### 5.2 Fallo controlado
L70: El objetivo fue provocar el fallo del consumidor sin revertir la transacción de creación del usuario.
L72: ### 5.3 Observación después del fallo
L81: ### 5.4 Observación después de retirar el fallo
L83: Se esperaron 5 segundos con PostgreSQL nuevamente disponible.
L85: Resultado:
L89: - retry automático observado: no.
L91: ### 5.5 Reinicio del backend
L95: Después de confirmar que el puerto 8080 estaba nuevamente disponible se esperaron 5 segundos adicionales.
L97: Resultado:
L101: - replay del evento observado: no.
L103: ## 6. Resultados
L105: | Observación | Resultado |
L109: | Usuario persistido después del fallo | Sí |
L110: | Categorías después del fallo | 0 |
L111: | Categorías 5 s después de retirar el fallo | 0 |
L112: | Retry automático observado | No |
L113: | Usuario persistido después del reinicio | Sí |
L114: | Categorías 5 s después del reinicio | 0 |
L115: | Replay después del reinicio observado | No |
L117: ## 7. Resultado experimental
L119: En las condiciones probadas, el fallo del consumidor ocurrió después del commit del usuario. La persistencia del usuario no fue revertida y el efecto secundario esperado en Activities no se completó.
L121: No se observó retry automático durante los 5 segundos posteriores a retirar el fallo y tampoco se observó replay de `UserRegisteredV1` durante los 5 segundos posteriores al reinicio del backend.
L125: La evidencia respalda el riesgo arquitectónico de que un evento interno procesado de forma asíncrona después del commit pueda dejar al sistema en un estado parcialmente actualizado cuando el consumidor falla.
L127: La implementación evaluada no demostró un mecanismo durable de recuperación del evento fallido en las ventanas observadas.
L131: Este experimento no demuestra que el evento sea irrecuperable bajo cualquier condición.
L139: - políticas explícitas de retry;
L140: - recuperación manual;
L148: `UserRegisteredV1` sigue siendo útil para desacoplar Identity de Activities, pero la asincronía introduce una ventana de consistencia eventual y un riesgo de pérdida del efecto secundario cuando el consumidor falla.
L150: Si el efecto asociado al evento se vuelve crítico, deberá evaluarse un mecanismo durable como transactional outbox, mensajería persistente o una estrategia explícita de retry/reprocesamiento.
L154: El resultado no invalida ADR-003.
L156: Complementa la decisión mostrando experimentalmente uno de sus trade-offs: desacoplar mediante eventos internos reduce dependencia directa entre módulos, pero la implementación actual no aporta por sí sola entrega durable ni recuperación automática ante fallos del consumidor.
L162: **RESULTADO EXPERIMENTAL:** no se observó retry ni replay automático en las ventanas probadas.

### experimentos/spike-02-resiliencia/resultado-fase2.txt
L2: Prueba de recuperacion despues de reinicio
L6: Estado inmediatamente antes del reinicio:
L10: Estado despues del reinicio:
L15: 5 segundos
L17: Recuperacion observada:
L24: El primer intento de reinicio utilizo incorrectamente :backend:bootRun
L25: desde el proyecto Gradle raiz y fallo antes de iniciar la aplicacion.


## AS-08 — SPIKE-03

### experimentos/spike-03-caracterizacion-racha/00-preregistro.md
L1: # SPIKE-03 — Caracterización de escalabilidad del cálculo de streak en Progress
L7: - Nombre: Caracterización de escalabilidad del cálculo de streak en Progress
L13: - Rama de preregistro definitivo: `docs/spike-03-preregistro-definitivo`
L24: El experimento solo se considerará formalmente preregistrado cuando esta versión de:
L26: - `00-preregistro.md`
L34: - los resultados
L35: - el veredicto
L92: El resultado es entregado a:
L105: - `currentStreak`
L106: - `bestStreak`
L176: Entre los resultados históricos documentados se encuentran aproximadamente:
L201: Entre los resultados históricos documentados se encuentran aproximadamente:
L311: `ProgressViewModel` combina ambas colecciones y realiza operaciones de unión, eliminación de duplicados y ordenamiento antes de entregar el resultado a `StreakCalculator`.
L325: > ¿Cómo cambia el tiempo de ejecución de la implementación actual del cálculo de `currentStreak` y `bestStreak` a medida que aumenta la cantidad de días históricos distintos que debe procesar?
L341: ## 8. Hipótesis
L345: Las condiciones de ejecución mencionadas en esta hipótesis corresponden exactamente a las fijadas en:
L349: La hipótesis evalúa exclusivamente:
L357: Por tanto, una diferencia positiva pequeña puede respaldar formalmente la hipótesis.
L391: participarán en la regla formal de respaldo o refutación de la hipótesis.
L411: - cambiar el veredicto
L412: - redefinir la hipótesis
L413: - sustituir los extremos preregistrados
L477: - ambas listas deberán utilizar el orden preregistrado
L556: ## 11. Criterio que respalda la hipótesis
L564: La hipótesis quedará:
L598: ## 12. Criterio que refuta la hipótesis
L600: La hipótesis quedará:
L612: Una corrida válida que contradiga la hipótesis:
L618: Una hipótesis refutada permitirá concluir únicamente:
L620: > La tendencia preregistrada no apareció de forma reproducible bajo las condiciones evaluadas.
L665: `registro de resultados`
L687: - no participarán en el veredicto
L729: La mediana será el estadístico utilizado para el veredicto de la hipótesis.
L784: No podrá modificarse después de observar resultados.
L788: ### Resultado inconcluso
L790: El resultado podrá clasificarse como:
L952: ### Resultado funcional esperado
L992: - no participará en el veredicto
L1023: Esta medición no forma parte de la hipótesis.
L1041: `una regla experimental preregistrada específicamente para SPIKE-03`

### experimentos/spike-03-caracterizacion-racha/condiciones.md
L7: - Nombre: Caracterización de escalabilidad del cálculo de streak en Progress
L8: - Documento asociado: `00-preregistro.md`
L10: - Rama de preregistro definitivo: `docs/spike-03-preregistro-definitivo`
L14: No contiene resultados experimentales.
L30: Este commit corresponde a una versión previa de trabajo del preregistro.
L34: No representa la frontera formal definitiva del preregistro utilizado para las futuras mediciones.
L38: ### Master base del preregistro definitivo
L46: `docs/spike-03-preregistro-definitivo`
L48: La versión definitiva del preregistro deberá ser posterior a este commit y anterior a cualquier implementación del harness o medición formal.
L52: ### Commit de preregistro definitivo
L56: - `00-preregistro.md`
L59: constituirá la frontera formal de preregistro de SPIKE-03.
L63: No se modificarán retrospectivamente las decisiones preregistradas únicamente para incorporar dicho hash.
L71: Será un commit posterior al preregistro definitivo.
L75: El commit experimental no podrá anteceder al commit definitivo de preregistro.
L212: `registro de resultados`
L242: participarán en la decisión formal de la hipótesis.
L246: No podrán modificar ni reinterpretar posteriormente el veredicto.
L415: - escritura de resultados
L457: ## 14. Resultado funcional esperado
L494: - no participará en el veredicto
L517: - veredicto
L596: Este factor de diez es una regla experimental elegida y preregistrada específicamente para SPIKE-03.
L641: La mediana será el estadístico decisor de la hipótesis.
L663: No podrá sustituir a la mediana en el veredicto.
L710: Los volúmenes intermedios no podrán modificar ni reinterpretar el veredicto.
L787: - cambia el orden preregistrado
L797: - no participará en el veredicto
L800: Una corrida válida desfavorable para la hipótesis no podrá invalidarse por ese motivo.
L810: - commit definitivo de preregistro
L814: - resultados
L815: - veredicto
L817: Las mediciones formales permanecen bloqueadas hasta que exista el commit definitivo de preregistro.

### experimentos/spike-03-caracterizacion-racha/registro-delegacion-ia.md
L7: - Nombre: Caracterización de escalabilidad del cálculo de streak en Progress
L18: Commit definitivo de preregistro:
L22: Merge del preregistro en `master`:
L26: La rama de implementación fue creada posteriormente desde el estado de `master` que ya contenía el preregistro.
L32: - hipótesis
L125: - modificar el orden preregistrado de las corridas
L126: - interpretar resultados
L128: - declarar la hipótesis respaldada, refutada o inconclusa
L144: - CA-08: capturar T0 y T1 únicamente alrededor de la operación preregistrada
L153: No sustituyen ni modifican el preregistro formal.
L176: - resultados funcionales
L192: - ejecutar la ruta preregistrada
L193: - validar resultados después de T1
L269: - no se modifica el preregistro después de esta implementación
L280: - que la ruta T0–T1 implementada coincide literalmente con el preregistro
L285: - que la distribución entre fuentes es la preregistrada
L301: 2. auditoría del harness contra el preregistro
L303: 4. commit experimental independiente y posterior al preregistro

### experimentos/spike-03-caracterizacion-racha/resultados.md
L1: # Resultados — SPIKE-03
L7: - Nombre: Caracterización de escalabilidad del cálculo de streak en Progress
L9: - Resultado de la hipótesis: RESPALDADA
L20: `docs(spike-03): preregistrar caracterizacion de streak`
L22: ### Merge del preregistro definitivo
L30: `Merge pull request #47 from alejandro4198/docs/spike-03-preregistro-definitivo`
L89: Después de las corridas y antes del commit de evidencia y resultados se
L171: Los datasets fueron deterministas según el protocolo preregistrado.
L290: La ruta medida por SPIKE-03 fue la preregistrada:
L332: ## 7. Resultados decisores por corrida
L334: La hipótesis preregistrada utiliza para el veredicto únicamente la comparación
L354: No intervienen en el veredicto.
L358: ## 8. Criterio preregistrado de resolución insuficiente
L360: El preregistro estableció el siguiente criterio:
L365: resultado de SPIKE-03 debía clasificarse como:
L380: Resultado:
L395: Resultado:
L410: Resultado:
L420: Por tanto, el criterio preregistrado de resolución insuficiente:
L436: preregistrado.
L445: La hipótesis preregistrada estableció que quedaría respaldada si:
L459: Resultado:
L467: Resultado:
L475: Resultado:
L489: El mínimo preregistrado era:
L493: Por tanto, la condición direccional preregistrada quedó satisfecha.
L524: No participan en el veredicto.
L528: - cambiar la hipótesis
L530: - sustituir los extremos preregistrados
L536: ## 11. Veredicto
L538: ### Resultado
L542: La hipótesis preregistrada de SPIKE-03 queda respaldada porque:
L549: El criterio preregistrado requería que la relación:
L565: Bajo las condiciones preregistradas de SPIKE-03, la mediana del tiempo de
L569: La relación direccional definida por la hipótesis se observó de forma
L575: Este resultado no demuestra por sí mismo que exista:
L601: Los resultados de SPIKE-03 deben interpretarse teniendo en cuenta que:
L622:   preregistro.
L631: ## 14. Separación entre resultado experimental y decisión arquitectónica
L637: El resultado:

### experimentos/spike-03-caracterizacion-racha/veredicto.md
L1: # SPIKE-03 — Veredicto experimental
L5: **Spike:** SPIKE-03 — Caracterización de escalabilidad del cálculo de streak en Progress
L7: **Preregistro definitivo:** `34234e4`
L9: **Merge del preregistro en `master`:** `d7f8b50`
L13: **Commit definitivo de evidencia y resultados:** `9e51c0d`
L19: Este documento registra exclusivamente el **veredicto experimental** de SPIKE-03 a partir del protocolo preregistrado y de la evidencia obtenida durante las corridas formales.
L26: 2. la hipótesis preregistrada;
L30: 6. y el resultado experimental obtenido.
L32: La evaluación de la relevancia arquitectónica de este resultado corresponde a un artefacto de decisión posterior.
L38: > ¿Cómo cambia el tiempo de ejecución de la implementación actual del cálculo de `currentStreak` y `bestStreak` a medida que aumenta la cantidad de días históricos distintos que debe procesar?
L42: ## 4. Hipótesis preregistrada
L46: Esta hipótesis fue registrada antes de implementar y ejecutar las mediciones formales.
L56: Los volúmenes preregistrados fueron:
L65: Para la decisión de la hipótesis se utilizaron exclusivamente los extremos:
L116: Por tanto, los resultados corresponden únicamente a la operación local definida en el protocolo y no representan por sí mismos el tiempo end-to-end del módulo Progress.
L154: Resultado funcional:
L167: Resultado funcional:
L180: Resultado funcional:
L189: El protocolo preregistrado no establecía el descarte de la primera corrida. Las tres corridas participan en la regla experimental de decisión.
L195: Después de `T1`, cada observación fue validada contra el resultado funcional esperado.
L199: `currentStreak == N`
L203: `bestStreak == N`
L260: - resultado funcional;
L341: El estadístico preregistrado para decidir la hipótesis fue la **mediana**.
L357: ## 14. Resultados decisores
L359: Los resultados correspondientes a los dos extremos utilizados para decidir la hipótesis fueron:
L371: Los valores intermedios de `N` se conservaron como parte de la caracterización completa, pero no participaron directamente en la regla de aceptación o refutación de la hipótesis.
L405: El protocolo preregistró la siguiente condición:
L423: Resultado:
L433: Resultado:
L443: Resultado:
L451: Por tanto, el criterio preregistrado de resolución insuficiente **no se activó**.
L457: representa exclusivamente la mediana observada mediante el procedimiento preregistrado.
L461: La regla de resolución no fue modificada después de observar los resultados.
L467: La regla direccional preregistrada fue:
L471: La hipótesis debía considerarse respaldada si la condición se cumplía en al menos:
L481: Resultado:
L489: Resultado:
L497: Resultado:
L505: El mínimo preregistrado era:


## AS-09 — CQRS / Event Sourcing

L1: # Aplicabilidad de CQRS y Event Sourcing
L3:
L5: ## Actualización posterior — SPIKE-03
L8: SPIKE-03.
L13: Posteriormente, SPIKE-03 caracterizó específicamente el cálculo de:
L24: constituye por sí sola una justificación de CQRS.
L26: SPIKE-03 dejó la decisión arquitectónica como:
L28: `NO DETERMINADA`
L34: - no se ha demostrado Event Sourcing;
L39: - `experimentos/spike-03-caracterizacion-racha/`
L41:
L46: Este documento analiza CQRS y Event Sourcing como opciones arquitectónicas para
L54: información adicional sería necesaria antes de justificar su adopción.
L68: ¿qué problema verificable de RachaPro justificaría introducirlo?
L85: 8. estado de justificación
L88: Cuando la evidencia disponible no permite justificar la introducción del patrón,
L92: NO JUSTIFICADO TODAVÍA POR LA EVIDENCIA DISPONIBLE
L100: para justificar la complejidad adicional
L163: directamente CQRS ni Event Sourcing.
L224: Esta diferencia por sí sola no demuestra que CQRS sea necesario.
L407: RachaPro debe adoptar Event Sourcing
L412: ## 4. CQRS
L564: problema causado por no utilizar CQRS
L571: CQRS podría permitir construir representaciones de lectura orientadas
L597: CQRS podría simplificar determinadas lecturas
L600: como para justificar una representación específica
L640: Estas características no son obligatorias para CQRS.
L645: CQRS
L653: CQRS
L655: Event Sourcing
L661: CQRS
L701: Estos riesgos dependen de la forma concreta en que CQRS fuera implementado.
L710: sola para justificar CQRS.
L716: Para que CQRS gane justificación en RachaPro tendría que demostrarse al menos
L857: ### 4.8 Estado de justificación de CQRS
L862: NO JUSTIFICADO TODAVÍA POR LA EVIDENCIA DISPONIBLE
L865: Esto no descarta CQRS.
L874: adicional asociada con CQRS.
L878: ## 5. Event Sourcing
L882: Event Sourcing podría resultar relevante si RachaPro necesitara reconstruir el
L895: recalcular una proyección mediante replay
L972: reconstruir PomodoroSession mediante replay
L989: replay
L1002: no implica Event Sourcing.
L1024: necesidad demostrada de Event Sourcing
L1031: Event Sourcing podría proporcionar determinadas capacidades si RachaPro llegara
L1046: recalcular vistas derivadas mediante replay
L1064: replay funcional
L1076: Event Sourcing podría aportar reconstrucción y trazabilidad completa
L1085: Introducir Event Sourcing añadiría nuevas responsabilidades de diseño,
L1091: event store
L1099: estrategia de replay
L1100: manejo de errores durante replay
L1138: como para que el replay completo produjera un problema medible, podrían
L1155: La magnitud real del coste de introducir Event Sourcing en RachaPro tampoco está
L1191: de replay.
L1198: lógica de replay incorrecta
L1210: replay costoso
L1211: crecimiento continuo del event store
L1218: Si Event Sourcing se combinara con proyecciones asíncronas, aparecerían además
L1224: Event Sourcing
L1232: Event Sourcing
L1234: CQRS obligatorio
L1243: Para que Event Sourcing comenzara a ganar justificación en RachaPro tendría que
L1257: existe una necesidad real de regenerar información mediante replay
L1310: qué entidades requieren replay
L1336: No sería metodológicamente correcto implementar primero un event store para
L1358: elegir Event Sourcing
L1365: ### 5.8 Estado de justificación de Event Sourcing
L1370: NO JUSTIFICADO TODAVÍA POR LA EVIDENCIA DISPONIBLE
L1376: Event Sourcing es incorrecto
L1382: RachaPro nunca debe utilizar Event Sourcing
L1388: reconstrucción mediante replay
L1389: event store como fuente de verdad
L1400: ### 6.1 CQRS no implica Event Sourcing
L1403: CQRS
L1405: Event Sourcing
L1408: CQRS puede separar responsabilidades o representaciones de lectura y escritura
L1413: ### 6.2 CQRS no implica asincronía
L1416: CQRS
L1425: ### 6.3 CQRS no implica bases de datos separadas
L1428: CQRS
L1438: ### 6.4 Historial no implica Event Sourcing
L1443: Event Sourcing
L1451: ### 6.5 Eventos de dominio no implican Event Sourcing
L1456: Event Sourcing
L1464: ### 6.6 Integración basada en eventos no implica Event Sourcing
L1469: Event Sourcing
L1477: ### 6.7 Eventos candidatos de RachaPro no justifican Event Sourcing
L1504: deben persistirse en un event store
L1510: RachaPro necesita Event Sourcing
L1517: | Aspecto | CQRS | Event Sourcing |
L1522: | Principal evidencia faltante | Medición directa del coste de las consultas de Progress y su impacto sobre el sistema | Requisito funcional de replay, reconstrucción completa o event log como fuente de verdad |
L1523: | Complejidad adicional principal | Modelos separados, actualización y consistencia entre representaciones | Event store, replay, versionado y reconstrucción de agregados |
L1524: | Estado actual | `NO JUSTIFICADO TODAVÍA POR LA EVIDENCIA DISPONIBLE` | `NO JUSTIFICADO TODAVÍA POR LA EVIDENCIA DISPONIBLE` |
L1530: Aunque ambos patrones tienen actualmente el mismo estado de justificación, la
L1533: Para CQRS:
L1543: que justifique CQRS
L1546: Para Event Sourcing:
L1561: de reconstrucción mediante replay


## AS-10 — Temporalidad Git

305dd61 | 2026-10-08T20:42:59-05:00 | docs(readme): enlazar revision externa post-freeze
4576e73 | 2026-10-08T20:42:57-05:00 | docs(m5): registrar correcciones posteriores al freeze
0a162c1 | 2026-10-08T20:42:57-05:00 | docs(m5): registrar auditoria externa post-freeze
e01cbfb | 2026-10-08T20:42:56-05:00 | docs(api): alinear titulo con consumidores actuales
b2e90cc | 2026-10-08T20:33:29-05:00 | docs(m5): congelar estado documental auditado
cd1c9b0 | 2026-10-08T12:54:58-05:00 | docs(spike-03): cerrar veredicto experimental
9e51c0d | 2026-10-07T22:04:43-05:00 | docs(spike-03): registrar evidencia y resultados
4da5923 | 2026-10-07T21:31:55-05:00 | test(spike-03): implementar harness de caracterizacion de streak
34234e4 | 2026-10-07T19:14:52-05:00 | docs(spike-03): preregistrar caracterizacion de streak
c6ae059 | 2026-09-30T17:41:34-05:00 | docs(semana10): registra ADR-003 de integracion por eventos
597e9c7 | 2026-09-30T17:19:25-05:00 | feat(semana9): valida spike de integracion por eventos
59fdb47 | 2026-09-30T16:20:52-05:00 | docs(semana9): define integracion API eventos y Spike 1


## AS-11 — Coherencia entre fuentes primarias

### ActivityLookup consumidores
- docs/integracion/contrato-api.md: ActivityLookup, Focus, Reminders
- docs/dominio/evidencia/activities.md: ActivityLookup, Focus, Reminders
- docs/semana9/09-api-eventos-integracion.md: ActivityLookup, Focus, Reminders

### UserRegisteredV1
- docs/integracion/eventos-candidatos.md: UserRegisteredV1, occurredAt
- docs/asyncapi/rachapro-events-v1.yaml: UserRegisteredV1, occurredAt
- docs/adr/0003-integracion-eventos-internos.md: UserRegisteredV1
- docs/semana9/09-api-eventos-integracion.md: UserRegisteredV1, occurredAt

### CQRS / ES
- docs/integracion/aplicabilidad-cqrs-event-sourcing.md: CQRS, Event Sourcing
- docs/adr/0003-integracion-eventos-internos.md: CQRS, Event Sourcing


## AS-12 — Afirmaciones potencialmente más fuertes que la evidencia

### docs/adr/0003-integracion-eventos-internos.md
L359: El Spike 1 no demuestra una necesidad de adoptar CQRS.
L418: Validar `UserRegisteredV1` no demuestra que todas las relaciones entre módulos deban convertirse en eventos.
L435: - evaluar reintentos, idempotencia o persistencia durable únicamente si evidencia futura demuestra su necesidad.
L467: - necesidad de reintentos garantizados;
L490: - garantizar entrega durable;
L491: - garantizar escalabilidad;
L689: Sin embargo, la existencia previa del archivo no demuestra por sí sola que el Context Map hubiera sido utilizado como evidencia causal para tomar ADR-003.
L747: evento obligatorio
L751: asincronía obligatoria
L883: retry nunca puede ocurrir
L889: replay nunca puede existir
L927: Esta clasificación no significa que todos deban implementarse obligatoriamente ni que exista una política general de integración por eventos.
L989: CQRS es incorrecto
L995: Event Sourcing es incorrecto
L1001: RachaPro nunca debe utilizarlos
L1042: preregistro definitivo
L1048: evidencia y resultados definitivos
L1112: En particular, su resultado no demuestra automáticamente que RachaPro necesite:
L1276: pero no demuestran que el Context Map haya causado ADR-003.
L1322: | No se requiere entrega durable para `UserRegisteredV1`. | El contrato actual documenta `durable: false`; no se encontró en las fuentes revisadas un requisito explícito de entrega durable para este flujo. | Se observan pérdidas del efecto secundario que, bajo un requisito o escenario vigente, requieran recuperación garantizada, o aparece un requisito explícito de entrega durable. |
L1323: | No se requiere retry garantizado actualmente. | SPIKE-02 no observó retry automático en la ventana de 5 segundos evaluada; no se encontró en las fuentes revisadas un requisito explícito de retry garantizado para este flujo. | Aparecen fallos del consumidor cuyo impacto sea incompatible con un requisito vigente y cuya recuperación requiera reintentos automáticos verificables. |
L1324: | No se requiere replay garantizado actualmente. | SPIKE-02 no observó replay durante los 5 segundos posteriores al reinicio evaluado; no se encontró en las fuentes revisadas un requisito explícito de replay para este flujo. | Se establece un requisito de recuperación posterior a reinicio o reconstrucción de efectos secundarios perdidos. |
L1327: | CQRS continúa sin estar justificado por la evidencia disponible. | El análisis de aplicabilidad no demuestra todavía un problema que requiera separar modelos de comando y lectura. | Se mide un problema concreto de lectura/escritura cuya resolución requiera evaluar explícitamente una separación de modelos. |
L1348: | Resultados definitivos SPIKE-03 | evidencia y resultados versionados del SPIKE-03 | `9e51c0d` |
L1358: La evidencia de SPIKE-02 demuestra que, bajo un fallo intencional del consumidor posterior al commit, el aprovisionamiento de categorías puede quedar incompleto y que no se observó retry automático durante los 5 segundos posteriores a retirar el fallo ni replay durante los 5 segundos posteriores al reinicio del backend.
L1368: necesidad de reintentos garantizados
L1384: necesidad de reintentos garantizados
L1389: SPIKE-02 permite caracterizar limitaciones del mecanismo relevantes para evaluar esos criterios, pero no demuestra por sí mismo que dichas necesidades estén activadas.
L1401: → demuestra un modo de fallo bajo las condiciones evaluadas
L1402: → demuestra una limitación concreta del mecanismo vigente
L1407: no demuestra por sí solo
L1411: → necesidad demostrada de retry o replay garantizados
L1412: → obligación de sustituir el mecanismo adoptado
L1419: La existencia previa del Context Map no demuestra que haya sido utilizado como evidencia causal para ADR-003.
L1441: Por consiguiente, con el corpus revisado hasta esta fecha, la evidencia posterior no demuestra que alguno de los supuestos actuales de ADR-003 haya dejado necesariamente de sostenerse.
L1443: Sí demuestra que el riesgo de fallo posterior al commit es observable bajo condiciones inducidas y que la ausencia de recuperación automática dentro de las ventanas evaluadas debe permanecer documentada como limitación conocida.
L1455: - que `UserRegisteredV1` sea la mejor alternativa posible;
L1481: - necesidad verificable de retry o replay garantizados;
L1514: cambio arquitectónico obligatorio

### docs/integracion/contrato-api.md
L169: ni demuestra que deban excluirse estados como:
L367: la arquitectura actual siempre que no convierta silenciosamente el fallo en
L385: Debe preservarse siempre la diferencia entre:
L502: Un cambio es compatible cuando puede introducirse sin obligar a Focus a cambiar
L549: - agregar una nueva entrada obligatoria que Focus deba proporcionar.

### docs/integracion/aplicabilidad-cqrs-event-sourcing.md
L106: el patrón es incorrecto
L112: el patrón nunca podrá utilizarse
L224: Esta diferencia por sí sola no demuestra que CQRS sea necesario.
L401: el evento debe publicarse obligatoriamente
L513: #### Evidencia que todavía no demuestra el problema
L572: específicamente a las necesidades de Progress si posteriormente se demuestra
L599: esas lecturas demuestran ser suficientemente costosas o diferentes
L640: Estas características no son obligatorias para CQRS.
L647: asincronía obligatoria
L663: bases de datos separadas obligatoriamente
L706: La prueba de carga disponible demuestra que el sistema mantuvo corrección
L771: No representan requisitos actuales del sistema ni niveles obligatorios de
L961: Estos elementos demuestran que existen cambios, hechos e información histórica.
L965: #### Evidencia que todavía no demuestra el problema
L1190: Otro riesgo sería reconstruir un estado incorrecto debido a errores en la lógica
L1226: asincronía obligatoria
L1234: CQRS obligatorio
L1249: es obligatorio reconstruir estados anteriores
L1336: No sería metodológicamente correcto implementar primero un event store para
L1376: Event Sourcing es incorrecto
L1382: RachaPro nunca debe utilizar Event Sourcing
L1418: asincronía obligatoria
L1430: bases de datos separadas obligatoriamente
L1433: La separación conceptual entre lectura y escritura no obliga a utilizar
L1598: replay funcional obligatorio
L1604: regeneración obligatoria de proyecciones desde hechos históricos
L1704: El análisis no adopta ni descarta definitivamente CQRS o Event Sourcing.

### docs/integracion/eventos-candidatos.md
L93: evento obligatorio a implementar
L96: La decisión definitiva sobre si una interacción debe implementarse mediante
L308: debe implementarse obligatoriamente como evento
L390: si el modelo actual no lo demuestra.
L406: La existencia del hecho no demuestra una necesidad de integración.
L451: Si el evento llegara posteriormente a adoptarse, el contrato definitivo deberá
L469: Aquí se demuestran dos elementos diferentes:
L606: Sin embargo, esa posibilidad no demuestra que actualmente reaccionen mediante
L637: El contrato actual no demuestra una reacción basada en eventos ante la
L995: Esto no demuestra todavía que la interacción Focus → Progress deba implementarse
L996: obligatoriamente mediante un evento.
L1478: No significa que los valores utilizados por Progress nunca cambien.
L1503: su ownership sigue sin decisión definitiva.
L1596: el ownership de Achievement continúa sin decisión definitiva.
L2028: La evidencia actual demuestra interés de Progress en:
L2049: evento obligatorio
L2193: deben implementarse obligatoriamente como eventos publicados
L2196: La adopción definitiva de cualquiera de estos candidatos requiere una decisión

### docs/dominio/justificacion-contextos.md
L19: definitivos de integración.
L186: no demuestra.
L334: modificar necesariamente el ciclo de vida de Activity, siempre que se mantengan
L532: de Progress, siempre que se mantengan los datos o contratos necesarios,
L785: En la evidencia inspeccionada, el uso de `userId` no demuestra ownership sobre:
L945: Estas dependencias demuestran consumo de información, pero no constituyen por sí
L1113: En la evidencia inspeccionada, su presencia no demuestra ownership sobre:
L1248: necesariamente los ciclos de vida internos de Activities y Focus, siempre que se
L1303: La utilización de la racha actual demuestra que determinadas reglas de
L1327: - ownership definitivo de Achievement;
L1332: - dirección definitiva de sus dependencias;
L1701: incorrecto.
L1883: - que la integración definitiva sea síncrona;
L1884: - que la integración definitiva sea asíncrona;
L1999: Por ejemplo, el uso de `SessionManager` por parte de Progress demuestra una
L2066: → incorporación definitiva al Context Map pendiente.
L2105: - qué contratos serán definitivos;
L2122: REST obligatorio
L2126: evento obligatorio
L2196: → ownership definitivo de Achievement.
L2269: independiente ni determinan todavía el mecanismo definitivo de integración entre
L2285: mecanismo de integración definitivo
L2289: información sustentadas por evidencia, no una arquitectura física definitiva del


## AS-13 — Pendientes auténticos

### docs/dominio/subdominios.md
L36: - posibilidad de evolución independiente mientras se mantengan los contratos necesarios con otras capacidades.
L40: De igual forma, la identificación de un bounded context no implica que cada contexto deba desplegarse como un microservicio independiente.
L418: ## 7. Capacidades con ownership no decidido
L437: Achievement ownership = NO DECIDIDO
L450: | Achievement | Ownership pendiente de una decisión específica sobre su responsabilidad, fuente de verdad, persistencia y relación Android/backend. | `NO DECIDIDO` |

### docs/dominio/responsabilidades-contextos.md
L19: `Achievement` permanece fuera de la matriz principal porque su ownership continúa como `NO DECIDIDO`; por ello, no se considera todavía un bounded context confirmado dentro de este documento.
L30: | Progress | Interpretar y agregar los resultados producidos por otras capacidades del sistema para medir y representar el avance del usuario mediante rachas y métricas de progreso. | Interpretación de progreso, racha actual, mejor racha y métricas agregadas. | Hechos producidos por Activities y Focus. | `Activity`, `PomodoroSession`, `Reminder`, Identity y Achievement (`ownership` no decidido; no se asigna a Progress en esta etapa). |
L190: - Achievement (`ownership` no decidido; no se asigna a Progress en esta etapa).
L198: Achievement corresponde a una **capacidad observada** en la implementación, pero su ownership definitivo permanece `NO DECIDIDO`.
L209: Sin embargo, esta evidencia no permite concluir todavía que Achievement constituya un bounded context independiente ni que deba pertenecer a Activities, Focus o Progress.
L223: → NO DECIDIDO
L243: - `Achievement` no aparecerá como bounded context confirmado mientras su ownership permanezca `NO DECIDIDO`.

### docs/dominio/justificacion-contextos.md
L295: → Activities se mantiene como bounded context independiente.
L500: → Focus se mantiene como bounded context independiente.
L830: → Reminders se mantiene como bounded context independiente.
L1162: Achievement ownership = NO DECIDIDO
L1165: El análisis de la evidencia disponible y de la decisión pendiente se desarrolla
L1206: → Progress se mantiene como bounded context independiente.
L1341: ### Decisión pendiente
L1367: → Achievement ownership = NO DECIDIDO
L1372: convertirse en un bounded context independiente.
L1377: Achievement como bounded context = NO DECIDIDO
L2066: → incorporación definitiva al Context Map pendiente.
L2085: Achievement ownership = NO DECIDIDO
L2109: - si cada bounded context tendrá despliegue independiente;
L2193:   como bounded contexts independientes.
L2259: Achievement ownership = NO DECIDIDO
L2264: declararlo como bounded context independiente.
L2269: independiente ni determinan todavía el mecanismo definitivo de integración entre

### docs/dominio/evidencia/activities.md
L415: - Activity restaurada a pendiente
L571: inspeccionado específicamente durante esta revisión, queda pendiente auditar
L586: El baseline de Semana 8 debe analizarse independientemente de modificaciones
L747: Queda pendiente establecer explícitamente la política de zona horaria compartida
L791: para crear bounded contexts independientes.
L801: Queda pendiente determinar:
L813: Queda pendiente verificar o decidir:
L836: | Mecanismo de activación histórico del aprovisionamiento | `DEPENDIENTE DEL BASELINE TEMPORAL` |
L875: El equipo decide modelar `Activities` como un bounded context independiente.
L970:    evolucionar de manera independiente y dejen de estar principalmente
L998: **Decisión del equipo:** bounded context independiente que contiene `Activity`,

### docs/integracion/aplicabilidad-cqrs-event-sourcing.md
L28: `NO DETERMINADA`
L134: contexto deba utilizar una base de datos independiente ni un mecanismo de
L545: necesidad de escalar lecturas y escrituras de manera independiente
L1472: La decisión de publicar eventos entre módulos es independiente de decidir que


## AS-14 — Navegación y fuentes canónicas

L20: ## 2. Estado actual de la arquitectura
L70: | Semana 9 | Evaluación de integración mediante contratos síncronos y evento interno; preregistro y ejecución de SPIKE-01. | `docs/semana9/`, `experimentos/spike-01-integracion/` |
L71: | Trabajo posterior / M5 | Modelado de dominio, Context Map, contrato API, AsyncAPI, auditoría de eventos, CQRS / Event Sourcing y experimentos posteriores. | `docs/dominio/`, `docs/integracion/`, `docs/asyncapi/`, `experimentos/spike-02-resiliencia/`, `experimentos/spike-03-caracterizacion-racha/`, `docs/adr/0003-integracion-eventos-internos.md` |
L79: Para conocer el estado actual del sistema se debe consultar, en este orden:
L87: 7. `docs/dominio/` — modelado de subdominios, bounded contexts, responsabilidades, ownership y Context Map correspondiente a M5.
L88: 8. `docs/integracion/` — contratos y análisis posteriores de integración.
L89: 9. `docs/asyncapi/rachapro-events-v1.yaml` — contrato técnico vigente del evento `UserRegisteredV1`.
L93: - `experimentos/spike-01-integracion/`
L94: - `experimentos/spike-02-resiliencia/`
L95: - `experimentos/spike-03-caracterizacion-racha/`
L114: - `docs/dominio/` — análisis de subdominios, bounded contexts, responsabilidades, ownership y Context Map de M5.
L115: - `docs/integracion/` — contratos, evaluación síncrono/asíncrono, eventos candidatos y análisis de aplicabilidad de CQRS / Event Sourcing.
L116: - `docs/asyncapi/rachapro-events-v1.yaml` — formalización técnica del contrato vigente de `UserRegisteredV1`.
L117: - `experimentos/spike-01-integracion/` — evidencia experimental de la integración mediante evento interno.
L118: - `experimentos/spike-02-resiliencia/` — evidencia posterior sobre comportamiento ante fallo del consumidor.
L119: - `experimentos/spike-03-caracterizacion-racha/` — caracterización experimental posterior del cálculo de rachas en Progress.
L156: > - `experimentos/spike-01-integracion/` — evaluación de la integración interna mediante `UserRegisteredV1`;
L157: > - `experimentos/spike-02-resiliencia/` — caracterización posterior del comportamiento ante fallo del consumidor;
L158: > - `experimentos/spike-03-caracterizacion-racha/` — caracterización del cálculo de rachas en Progress.
L261: Tampoco debe interpretarse un documento histórico como descripción automática del estado actual.
L298: ## Auditoría externa post-freeze
L306: - `docs/m5/correcciones-post-freeze.md`


## AS-15 — Insumos para veredicto metodológico final

Este archivo NO contiene un veredicto arquitectónico automático.

Debe utilizarse para responder, por cada afirmación:

1. ¿Qué afirma el artefacto?
2. ¿La afirmación es hecho, análisis o decisión?
3. ¿Qué evidencia concreta la sostiene?
4. ¿La evidencia existía antes de la decisión?
5. ¿La inferencia excede las condiciones observadas?
6. ¿Existe otra fuente vigente incompatible?
7. ¿La conclusión pertenece al equipo o puede verificarse mecánicamente?

Decisiones que NO deben cerrarse por este informe:

- Identity en Context Map;
- ownership de Achievement;
- eventual renombre contractual de ActivityLookup;
- política definitiva de .idea;
- cualquier nueva adopción de CQRS / Event Sourcing / retry / replay / Outbox / broker.
