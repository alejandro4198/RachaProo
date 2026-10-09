# Pista 1 — evidencia para consistencia transversal

## 1. Naturaleza del artefacto

Este documento es un paquete de extracción de evidencia.

No constituye por sí mismo un veredicto semántico ni una decisión arquitectónica.

Una coincidencia textual no demuestra equivalencia conceptual, causalidad, contradicción ni incumplimiento.

Los hallazgos de la auditoría complementaria deberán construirse posteriormente a partir de la lectura contextual de estas evidencias.

## 2. Baseline

`259fb740abe3651b3925cbf6e93a3399ce905dbe`

## 3. Estado de la auditoría

HEAD inicial de Pista 1: `9c987d758d1689bf125ec19cc712ac59be738581`.

Esta pista no modifica ADR-003, Context Map, código ni decisiones abiertas.

## 4. Resumen de cobertura

| Tema | Coincidencias | Archivos con evidencia |
|---|---:|---:|
| P1-IDENTITY | 500 | 48 |
| P1-ACTIVITYLOOKUP | 210 | 33 |
| P1-USERREGISTERED | 226 | 23 |
| P1-FOCUS-ACTIVITIES | 152 | 34 |
| P1-REMINDERS-ACTIVITIES | 56 | 26 |
| P1-PROGRESS-ACTIVITIES | 58 | 19 |
| P1-PROGRESS-FOCUS | 71 | 19 |
| P1-CQRS | 147 | 20 |
| P1-EVENT-SOURCING | 125 | 17 |
| P1-RESILIENCIA | 143 | 14 |
| P1-SPIKE01 | 188 | 17 |
| P1-ESTADOS | 194 | 34 |

## 5. Evidencia por tema

### P1-IDENTITY

Coincidencias encontradas: **500**.

#### `README.md`

- L48: Identity
- L71: \| Trabajo posterior / M5 \| Modelado de dominio, Context Map, contrato API, AsyncAPI, auditoría de eventos, CQRS / Event Sourcing y experimentos posteriores. \| 'docs/dominio/', 'docs/integracion/', 'docs/asyncapi/', 'experimentos/spike-02-resiliencia/', 'experimentos/spike-03-caracterizacion-racha/', 'docs/adr/0003-integracion-eventos-internos.md' \|
- L86: 6. 'docs/adr/0003-integracion-eventos-internos.md' — decisión vigente para el flujo post-registro Identity → Activities mediante 'UserRegisteredV1'.
- L87: 7. 'docs/dominio/' — modelado de subdominios, bounded contexts, responsabilidades, ownership y Context Map correspondiente a M5.
- L113: - 'docs/adr/0003-integracion-eventos-internos.md' — decisión arquitectónica posterior sobre 'UserRegisteredV1' para el flujo Identity → Activities.
- L114: - 'docs/dominio/' — análisis de subdominios, bounded contexts, responsabilidades, ownership y Context Map de M5.
- L271: - Identity;
- L343: - distinción entre módulos/capacidades implementadas y bounded contexts
- L346: Estas correcciones no deciden Identity, Achievement ni otras decisiones

#### `docs/adr/0001-decision-estilo.md`

- L194: Identity
- L206: ### Identity
- L213: - identidad autenticada.
- L483: La implementación resultante conserva una única aplicación Spring Boot y materializa las fronteras Identity, Activities, Focus, Progress y Reminders.

#### `docs/adr/0002-aislamiento-persistencia.md`

- L17: Identity
- L60: \| 'user' \| 'CategoryRepository' \| Cruce Identity -> Activities \|
- L61: \| 'user' \| 'CategoryEntity' \| Cruce Identity -> Activities \|
- L64: \| 'auth' \| 'UserRepository' \| Interna a Identity \|
- L92: ### 4.3 Identity
- L100: Actualmente Identity construye 'CategoryEntity' y utiliza 'CategoryRepository' directamente.
- L104: Identity necesita solicitar la creación de las categorías, pero no necesita conocer cómo Activities las construye o persiste.
- L308: Identity únicamente proporcionará el identificador del usuario.
- L310: Identity dejará de construir 'CategoryEntity' y dejará de importar 'CategoryRepository'.
- L318: Identity -> Activities API
- L327: Dentro de Identity seguirá siendo válida:
- L341: Identity -> Activities Repository
- L342: Identity -> Activities Entity
- L555: 3. Identity deje de importar 'CategoryRepository';
- L556: 4. Identity deje de construir 'CategoryEntity';

#### `docs/adr/0003-integracion-eventos-internos.md`

- L16: Identity
- L26: En particular, ADR-002 definió inicialmente que Identity solicitaría a Activities la creación de categorías predeterminadas mediante el contrato síncrono:
- L44: Identity
- L53: Aunque la dependencia atravesaba una API pública válida y no violaba el ownership de persistencia, Identity continuaba coordinando directamente una operación perteneciente a Activities.
- L57: > ¿Puede Identity comunicar el hecho de que un usuario fue registrado sin solicitar directamente a Activities el aprovisionamiento de categorías, manteniendo el comportamiento funcional esperado?
- L87: Identity continúa invocando directamente el contrato público de Activities.
- L99: - Identity continúa coordinando explícitamente una operación de Activities;
- L109: Identity publica un evento de dominio o integración dentro del mismo proceso y Activities lo consume sin ejecución asíncrona.
- L129: Identity persiste el usuario y publica:
- L142: - Identity deja de solicitar directamente el aprovisionamiento;
- L189: La hipótesis evaluada fue comprobar si el registro podía desacoplar la dependencia directa de Identity hacia Activities mediante 'UserRegisteredV1', manteniendo el comportamiento funcional esperado.
- L260: Se adopta 'UserRegisteredV1' como mecanismo interno para comunicar desde Identity hacia Activities que un nuevo usuario ha sido registrado.
- L264: Identity
- L279: Identity deja de invocar directamente 'DefaultCategoryProvisioning' para este flujo.
- L287: El evento comunica un hecho ocurrido y no transfiere ownership de negocio hacia Identity.
- L295: Identity -> UserRegisteredV1 -> Activities
- L330: Identity -> DefaultCategoryProvisioning -> Activities
- L334: Identity -> UserRegisteredV1 -> Activities
- L377: - menor coordinación directa desde Identity hacia Activities;
- L603: SPIKE-01 evaluó si el registro podía desacoplar la dependencia directa de Identity hacia Activities mediante:
- L633: ### 21.3 Evolución del Context Map y relación temporal con ADR-003
- L635: La representación PlantUML del Context Map se encuentra en:
- L654: docs(domain): agregar context map de bounded contexts
- L689: Sin embargo, la existencia previa del archivo no demuestra por sí sola que el Context Map hubiera sido utilizado como evidencia causal para tomar ADR-003.
- L699: Después de ADR-003, el Context Map fue refinado en:
- L719: docs(domain): agregar context map de bounded contexts
- L734: uso causal del Context Map en ADR-003
- L738: Los refinamientos posteriores del Context Map se utilizan como trazabilidad adicional para revisar las fronteras y dependencias entre contextos, pero no como justificación retrospectiva de la decisión tomada el 2026-09-30.
- L740: La existencia de una relación en el Context Map tampoco determina por sí misma el mecanismo técnico de integración.
- L761: Identity
- L796: Identity
- L1133: 3. volver a conectar Identity con el mecanismo equivalente a 'DefaultCategoryProvisioning';
- L1146: - transferencia de persistencia desde Activities hacia Identity.
- L1250: de Identity sobre una operación perteneciente a Activities.
- L1274: los refinamientos posteriores del Context Map son útiles
- L1276: pero no demuestran que el Context Map haya causado ADR-003.
- L1325: \| No se requiere broker externo para este flujo. \| El sistema mantiene la interacción dentro de una única aplicación Spring Boot y el mecanismo interno cumple el alcance funcional actualmente documentado. \| Identity y Activities requieren procesos o despliegues independientes, entrega durable, múltiples consumidores independientes o garantías de me […]
- L1339: \| Context Map fuente PlantUML \| 'docs/dominio/context-map.puml' \| versión previa a ADR-003 en '8d23268'; refinamientos posteriores en '4b0acd7' y '64aeb42' \|
- L1340: \| Context Map renderizado PNG \| 'docs/dominio/context-map.png' \| '64aeb42' \|
- L1341: \| Relación temporal Context Map / ADR-003 \| historial Git de 'context-map.puml' \| '8d23268' es ancestro de 'c6ae059'; '4b0acd7' y '64aeb42' son posteriores \|
- L1415: Los análisis posteriores del Context Map, AsyncAPI, eventos candidatos y aplicabilidad de CQRS/Event Sourcing tampoco introducen por sí mismos una contradicción con la decisión original.
- L1417: Respecto del Context Map, una versión de 'context-map.puml' ya existía antes de ADR-003, mientras que sus refinamientos posteriores y la representación PNG aportan trazabilidad adicional.
- L1419: La existencia previa del Context Map no demuestra que haya sido utilizado como evidencia causal para ADR-003.

#### `docs/architecture/current/architecture-current.md`

- L309: - cómo se comprueba la identidad del usuario durante el login;

#### `docs/asyncapi/rachapro-events-v1.yaml`

- L27: title: Identity publica UserRegisteredV1
- L55: summary: Informa que Identity confirmó el registro de un usuario.

#### `docs/dominio/README.md`

- L8: ### Subdominios y bounded contexts
- L20: ### Context Map
- L30: El Context Map representa fronteras y dependencias conceptuales de información.
- L51: El alcance definitivo del Context Map y los ownership todavía abiertos deben

#### `docs/dominio/context-map.puml`

- L2: title RachaPro - Context Map
- L7: rectangle "Activities\n<<Bounded Context>>" as Activities {
- L10: rectangle "Focus\n<<Bounded Context>>" as Focus {
- L13: rectangle "Reminders\n<<Bounded Context>>" as Reminders {
- L16: rectangle "Progress\n<<Bounded Context>>" as Progress {
- L57: Achievement no se representa como bounded context

#### `docs/dominio/evidencia/activities.md`

- L475: - reglas internas de Identity
- L580: ### Identity
- L613: Identity
- L643: - ciclo de vida interno de Identity
- L791: para crear bounded contexts independientes.
- L818: 4. relación definitiva entre Activities e Identity después de evaluar el Spike
- L850: \| Activities como bounded context \| 'DECISIÓN DEL EQUIPO' \|
- L867: Progress, Reminders e Identity
- L875: El equipo decide modelar 'Activities' como un bounded context independiente.
- L892: - separación semántica frente a Focus, Progress, Reminders e Identity
- L926: - gestión interna de Identity
- L955: en el Context Map.
- L957: #### Identity
- L959: Activities utiliza 'userId', pero no posee autenticación ni identidad.
- L996: 'Activities como bounded context = DECIDIDO'
- L998: **Decisión del equipo:** bounded context independiente que contiene 'Activity',

#### `docs/dominio/evidencia/focus.md`

- L181: - autenticación y gestión de identidad del usuario
- L291: El equipo decide modelar 'Focus' como un bounded context independiente.
- L329: - autenticación y gestión de identidad del usuario
- L378: 'Focus como bounded context = DECIDIDO'
- L380: **Decisión del equipo:** bounded context independiente.

#### `docs/dominio/evidencia/progress.md`

- L338: No demuestra todavía que Achievement pertenezca al bounded context Progress.
- L536: ### Identity
- L573: - ciclo interno de Identity
- L739: La estructura de paquetes no se utiliza como prueba de bounded context.
- L787: 4. si Progress tendrá contratos explícitos con otros bounded contexts
- L825: \| Progress como bounded context \| 'DECISIÓN DEL EQUIPO' \|
- L861: El equipo decide modelar 'Progress' como un bounded context independiente.
- L931: - ciclo de vida interno de Identity
- L1033: 'Progress como bounded context = DECIDIDO'
- L1035: **Decisión del equipo:** bounded context independiente responsable de interpretar

#### `docs/dominio/evidencia/reminders.md`

- L52: Progress e Identity.
- L446: publiquen eventos explícitos de integración hacia otros bounded contexts.
- L466: El uso de 'userId' no implica que Reminders posea autenticación o identidad.
- L564: ### Identity
- L585: - reglas internas de Identity
- L623: - ciclo interno de Identity
- L636: bounded context:
- L837: ### Identidad activa durante entrega
- L921: \| Reminders como bounded context \| 'DECISIÓN DEL EQUIPO' \|
- L942: - Identity proporciona contexto de usuario, pero no controla el ciclo funcional
- L947: El equipo decide modelar 'Reminders' como un bounded context independiente.
- L962: Identity
- L994: - reglas internas de Identity
- L1051: #### Identity
- L1124: 'Reminders como bounded context = DECIDIDO'
- L1126: **Decisión del equipo:** bounded context independiente responsable del ciclo

#### `docs/dominio/justificacion-contextos.md`

- L1: # Justificación de bounded contexts
- L5: Este documento justifica las fronteras de bounded context definidas para RachaPro
- L13: Su objetivo no es redefinir los contextos ni proponer nuevos bounded contexts,
- L25: Para cada bounded context se contrastan tres niveles:
- L51: bounded context
- L55: bounded context
- L113: → síntesis de las relaciones entre bounded contexts
- L190: la razón para delimitar el bounded context.
- L295: → Activities se mantiene como bounded context independiente.
- L330: del bounded context corresponde a una decisión de modelado basada en
- L340: Activities como bounded context = DECIDIDO
- L500: → Focus se mantiene como bounded context independiente.
- L544: Focus como bounded context = DECIDIDO
- L564: responsabilidad conceptual del bounded context.
- L673: ejecución y no se modelan como un bounded context adicional.
- L777: bounded context adicional
- L780: #### Contexto de usuario / Identity
- L789: - ciclo de vida interno de Identity.
- L792: para incorporar Identity dentro de la frontera de Reminders.
- L830: → Reminders se mantiene como bounded context independiente.
- L860: bounded context Reminders
- L877: Reminders como bounded context = DECIDIDO
- L947: 'PomodoroSession' o Identity.
- L1019: 'ProgressViewModel' como una regla profunda del bounded context.
- L1118: - ciclo de vida de Identity.
- L1125: poseer Identity
- L1206: → Progress se mantiene como bounded context independiente.
- L1259: La decisión del bounded context se fundamenta en que Progress aplica reglas
- L1266: Progress como bounded context = DECIDIDO
- L1333: - si debe integrarse dentro de otro bounded context o permanecer como capacidad
- L1372: convertirse en un bounded context independiente.
- L1377: Achievement como bounded context = NO DECIDIDO
- L1384: El C4 desarrollado durante Semana 8 y el Context Map elaborado en M5 representan
- L1414: Context Map M5
- L1415: → bounded contexts
- L1425: No se utiliza como una definición automática de los bounded contexts.
- L1432: bounded context
- L1436: bounded context
- L1475: Sin embargo, el Context Map no reemplaza esa información.
- L1478: pertenece a cada bounded context y qué relaciones relevantes existen entre esas
- L1529: bounded context Android
- L1533: bounded context backend
- L1538: La separación por bounded context responde a criterios como:
- L1583: bounded context
- L1616: Sin embargo, Activities no se define como bounded context simplemente porque
- L1749: Eso no implica que Activities adquiera ownership sobre Identity.
- L1774: \| Aspecto \| C4 Semana 8 \| Modelado M5 / Context Map \|
- L1777: \| Elementos principales \| Contenedores, módulos y componentes \| Bounded contexts \|
- L1792: Context Map
- L1820: bounded contexts
- L1831: ## 9. Relación con el Context Map
- L1833: El Context Map sintetiza las fronteras de bounded context decididas y las
- L1841: - qué bounded contexts fueron decididos;
- L1849: Context Map
- L1853: Context Map
- L1857: Context Map
- L1866: El Context Map utiliza la siguiente convención:
- L1897: El mapa representa actualmente cuatro relaciones entre los bounded contexts
- L1942: Una regla central utilizada en el Context Map es:
- L1994: relación del Context Map.
- L2007: ownership sobre Identity
- L2023: ### Evolución asociada a Identity
- L2043: si 'UserRegisteredV1' es producido por Identity y consumido por Activities, una
- L2047: Activities --> Identity
- L2052: Su inclusión depende del alcance definido para el Context Map y de la decisión
- L2053: de representar Identity dentro de ese mismo alcance.
- L2062: → existe una dependencia conceptual con Identity
- L2063: si Identity forma parte del alcance del mapa.
- L2066: → incorporación definitiva al Context Map pendiente.
- L2073: ### Achievement y el Context Map
- L2075: Achievement no aparece actualmente como bounded context confirmado en el mapa.
- L2088: Por tanto, incorporarlo como bounded context implicaría representar como cerrada
- L2096: ### Decisiones que el Context Map todavía no toma
- L2109: - si cada bounded context tendrá despliegue independiente;
- L2116: bounded context
- L2120: relación en Context Map
- L2124: relación en Context Map
- L2128: relación en Context Map
- L2139: El Context Map actual puede resumirse así:
- L2168: El Context Map representa qué fronteras conceptuales fueron decididas, qué
- L2193: como bounded contexts independientes.
- L2223: El Context Map representa actualmente relaciones de información sustentadas por
- L2263: Achievement ni decidió incorporarlo a uno de los bounded contexts definidos o
- L2264: declararlo como bounded context independiente.
- L2268: No implican que cada bounded context deba convertirse en un microservicio
- L2279: bounded context
- L2283: relación en Context Map
- L2288: El Context Map representa, en esta etapa, fronteras conceptuales y relaciones de

#### `docs/dominio/responsabilidades-contextos.md`

- L1: # Responsabilidades por bounded context
- L5: Este documento compara las responsabilidades de los bounded contexts ya definidos para RachaPro, haciendo explícito qué conceptos y reglas posee cada contexto, qué información consume o referencia de otras capacidades y qué responsabilidades deben permanecer fuera de su frontera.
- L13: En este documento, **Posee** identifica los conceptos y reglas cuya responsabilidad pertenece conceptualmente al bounded context. **Consume / referencia** identifica información proveniente de otras capacidades que el contexto utiliza sin adquirir ownership sobre ella. **No posee** hace explícitas las responsabilidades que deben permanecer fuera de […]
- L19: 'Achievement' permanece fuera de la matriz principal porque su ownership continúa como 'NO DECIDIDO'; por ello, no se considera todavía un bounded context confirmado dentro de este documento.
- L27: \| Activities \| Planificar, organizar, descomponer y dar seguimiento a las actividades y tareas del usuario, incluyendo su clasificación y su ciclo de vida. \| 'Activity', 'Category', 'Subtask', ciclo de vida de 'Activity'. \| 'userId' y datos necesarios para asociar las entidades al usuario correspondiente. \| 'PomodoroSession', rachas y métricas de P […]
- L28: \| Focus \| Gestionar el ciclo de vida de las sesiones Pomodoro del usuario, incluyendo periodos de concentración y descanso, sus estados y transiciones. \| 'PomodoroSession', tipo de sesión, estado de sesión y transiciones de su ciclo de vida. \| 'userId' y referencia opcional a 'Activity'. \| 'Activity', Progress, 'Reminder', Identity. \|
- L29: \| Reminders \| Definir, programar y gestionar el ciclo de vida de los recordatorios del usuario, conservando la intención de generar un aviso en un momento determinado y permitiendo opcionalmente asociarlo a una Activity. \| 'Reminder', momento programado y ciclo funcional 'SCHEDULED / DELIVERED / CANCELLED'. \| 'userId' y referencia opcional a 'Activ […]
- L30: \| Progress \| Interpretar y agregar los resultados producidos por otras capacidades del sistema para medir y representar el avance del usuario mediante rachas y métricas de progreso. \| Interpretación de progreso, racha actual, mejor racha y métricas agregadas. \| Hechos producidos por Activities y Focus. \| 'Activity', 'PomodoroSession', 'Reminder', I […]
- L61: - reglas internas de Identity;
- L97: - reglas internas de Identity;
- L135: - reglas internas de Identity;
- L189: - reglas internas de Identity;
- L209: Sin embargo, esta evidencia no permite concluir todavía que Achievement constituya un bounded context independiente ni que deba pertenecer a Activities, Focus o Progress.
- L230: ## 9. Reglas para el Context Map
- L243: - 'Achievement' no aparecerá como bounded context confirmado mientras su ownership permanezca 'NO DECIDIDO'.
- L245: El Context Map deberá representar fronteras y relaciones de dominio sin convertir automáticamente la estructura actual del código en la arquitectura objetivo.

#### `docs/dominio/subdominios.md`

- L1: # Subdominios y bounded contexts
- L7: Su propósito es sintetizar las responsabilidades protegidas, conceptos principales, ownership, dependencias y exclusiones de los bounded contexts identificados, sin repetir el detalle técnico completo registrado en los documentos auxiliares de evidencia.
- L26: La delimitación de los bounded contexts no se realizó únicamente a partir de nombres de paquetes, clases, tablas, pantallas o módulos técnicos.
- L40: De igual forma, la identificación de un bounded context no implica que cada contexto deba desplegarse como un microservicio independiente.
- L45: bounded context
- L123: Activities conserva el ownership sobre estos conceptos incluso cuando otros bounded contexts utilizan una referencia a 'Activity'.
- L152: - ciclo de vida de Identity.
- L156: El mecanismo definitivo de integración con Identity no se define en este documento.
- L173: - ciclo interno de Identity.
- L195: Activities como bounded context = DECIDIDO

#### `docs/integracion/aplicabilidad-cqrs-event-sourcing.md`

- L133: La existencia de bounded contexts y fronteras modulares no implica que cada
- L345: ### 3.8 Context Map relevante

#### `docs/integracion/cobertura-interacciones-sync-async.md`

- L24: \| Identity → Activities \| 'UserRegisteredV1' como evento interno; Activities consume después del commit mediante listener \| Evaluada mediante SPIKE-01 y decidida específicamente por ADR-003 \| EVALUADA PARA ESE FLUJO \|
- L25: \| Progress → Activities \| Relación de consumo/referencia documentada en el Context Map \| No se identificó en el corpus revisado una comparación sync/async dedicada \| NO EVALUADA ESPECÍFICAMENTE \|
- L26: \| Progress → Focus \| Relación de consumo/referencia documentada en el Context Map \| No se identificó en el corpus revisado una comparación sync/async dedicada \| NO EVALUADA ESPECÍFICAMENTE \|
- L56: - incorporar Identity al Context Map;

#### `docs/integracion/contrato-api.md`

- L53: bounded context Activities ofrece a Focus una capacidad de validación de
- L740: ## 15. Trazabilidad hacia ADR-002 y Context Map
- L742: ### 15.1 Relación con el Context Map
- L744: El Context Map establece la relación:

#### `docs/integracion/eventos-candidatos.md`

- L12: integración post-registro entre Identity y Activities.
- L47: - Context Map
- L110: Context Map
- L121: Los bounded contexts considerados durante este análisis son:
- L136: El Context Map utilizado como referencia contempla las siguientes relaciones:
- L686: No existe evidencia de otro bounded context que necesite reaccionar ante la
- L1643: bounded context propietario
- L1659: # 10. Identity
- L1920: Sin embargo, no existe evidencia suficiente de que otro bounded context necesite
- L2107: qué bounded context lo produce
- L2162: Identity
- L2271: 'Identity'
- L2283: 'Identity / UserService'

#### `docs/integracion/sincrono-vs-asincrono.md`

- L6: concreta entre bounded contexts de RachaPro:

#### `docs/m5/auditoria-complementaria/README.md`

- L74: - cambiar el Context Map;
- L117: - Context Map.

#### `docs/m5/auditoria-externa-post-freeze.md`

- L127: - inclusión de Identity en el Context Map;

#### `docs/m5/auditoria-final.md`

- L37: \| A04 Convencion Context Map \| BIEN \| Direccion de consumo/referencia explicitada. \|
- L38: \| A05 Identity \| DECISION PENDIENTE \| No se detecta cierre automatico. La evidencia existe, pero el alcance permanece abierto. \|
- L61: - alcance de Identity en el Context Map;

#### `docs/m5/auditoria-semantica-semanas9-10.md`

- L42: L1: # Subdominios y bounded contexts
- L43: L7: Su propósito es sintetizar las responsabilidades protegidas, conceptos principales, ownership, dependencias y exclusiones de los bounded contexts identificados, sin repetir el detalle técnico completo registrado en los documentos auxiliares de evidencia.
- L45: L26: La delimitación de los bounded contexts no se realizó únicamente a partir de nombres de paquetes, clases, tablas, pantallas o módulos técnicos.
- L50: L40: De igual forma, la identificación de un bounded context no implica que cada contexto deba desplegarse como un microservicio independiente.
- L51: L45: bounded context
- L54: L123: Activities conserva el ownership sobre estos conceptos incluso cuando otros bounded contexts utilizan una referencia a 'Activity'.
- L61: L195: Activities como bounded context = DECIDIDO
- L79: L1: # Responsabilidades por bounded context
- L80: L5: Este documento compara las responsabilidades de los bounded contexts ya definidos para RachaPro, haciendo explícito qué conceptos y reglas posee cada contexto, qué información consume o referencia de otras capacidades y qué responsabilidades deben permanecer fuera de su frontera.
- L82: L13: En este documento, **Posee** identifica los conceptos y reglas cuya responsabilidad pertenece conceptualmente al bounded context. **Consume / referencia** identifica información proveniente de otras capacidades que el contexto utiliza sin adquirir ownership sobre ella. **No posee** hace explícitas las responsabilidades que deben permanecer fue […]
- L83: L19: 'Achievement' permanece fuera de la matriz principal porque su ownership continúa como 'NO DECIDIDO'; por ello, no se considera todavía un bounded context confirmado dentro de este documento.
- L86: L30: \| Progress \| Interpretar y agregar los resultados producidos por otras capacidades del sistema para medir y representar el avance del usuario mediante rachas y métricas de progreso. \| Interpretación de progreso, racha actual, mejor racha y métricas agregadas. \| Hechos producidos por Activities y Focus. \| 'Activity', 'PomodoroSession', 'Reminde […]
- L99: L209: Sin embargo, esta evidencia no permite concluir todavía que Achievement constituya un bounded context independiente ni que deba pertenecer a Activities, Focus o Progress.
- L105: L243: - 'Achievement' no aparecerá como bounded context confirmado mientras su ownership permanezca 'NO DECIDIDO'.
- L108: L1: # Justificación de bounded contexts
- L109: L5: Este documento justifica las fronteras de bounded context definidas para RachaPro
- L111: L13: Su objetivo no es redefinir los contextos ni proponer nuevos bounded contexts,
- L112: L25: Para cada bounded context se contrastan tres niveles:
- L115: L51: bounded context
- L116: L55: bounded context
- L123: L113: → síntesis de las relaciones entre bounded contexts
- L125: L190: la razón para delimitar el bounded context.
- L131: L295: → Activities se mantiene como bounded context independiente.
- L133: L330: del bounded context corresponde a una decisión de modelado basada en
- L135: L340: Activities como bounded context = DECIDIDO
- L141: L500: → Focus se mantiene como bounded context independiente.
- L146: ## AS-02 — Context Map y relaciones
- L148: L7: rectangle "Activities\n<<Bounded Context>>" as Activities {
- L149: L10: rectangle "Focus\n<<Bounded Context>>" as Focus {
- L150: L13: rectangle "Reminders\n<<Bounded Context>>" as Reminders {
- L151: L16: rectangle "Progress\n<<Bounded Context>>" as Progress {
- L159: L57:   Achievement no se representa como bounded context
- L171: L25: bounded context Activities ofrece a Focus una capacidad de validación de
- L303: L291: El equipo decide modelar 'Focus' como un bounded context independiente.
- L317: L378: 'Focus como bounded context = DECIDIDO'
- L350: L466: El uso de 'userId' no implica que Reminders posea autenticación o identidad.
- L378: L12: integración post-registro entre Identity y Activities.
- L416: L1659: # 10. Identity
- L445: L27:     title: Identity publica UserRegisteredV1
- L456: L55:       summary: Informa que Identity confirmó el registro de un usuario.
- L468: L16:     Identity
- L470: L26: En particular, ADR-002 definió inicialmente que Identity solicitaría a Activities la creación de categorías predeterminadas mediante el contrato síncrono:
- L472: L44:     Identity
- L474: L53: Aunque la dependencia atravesaba una API pública válida y no violaba el ownership de persistencia, Identity continuaba coordinando directamente una operación perteneciente a Activities.
- L475: L57: > ¿Puede Identity comunicar el hecho de que un usuario fue registrado sin solicitar directamente a Activities el aprovisionamiento de categorías, manteniendo el comportamiento funcional esperado?
- L476: L87: Identity continúa invocando directamente el contrato público de Activities.
- L477: L99: - Identity continúa coordinando explícitamente una operación de Activities;
- L478: L109: Identity publica un evento de dominio o integración dentro del mismo proceso y Activities lo consume sin ejecución asíncrona.
- L479: L129: Identity persiste el usuario y publica:
- L483: L142: - Identity deja de solicitar directamente el aprovisionamiento;
- L485: L189: La hipótesis evaluada fue comprobar si el registro podía desacoplar la dependencia directa de Identity hacia Activities mediante 'UserRegisteredV1', manteniendo el comportamiento funcional esperado.
- L486: L260: Se adopta 'UserRegisteredV1' como mecanismo interno para comunicar desde Identity hacia Activities que un nuevo usuario ha sido registrado.
- L487: L264:     Identity
- L491: L279: Identity deja de invocar directamente 'DefaultCategoryProvisioning' para este flujo.
- L493: L287: El evento comunica un hecho ocurrido y no transfiere ownership de negocio hacia Identity.
- L494: L295:     Identity -> UserRegisteredV1 -> Activities
- L496: L330:     Identity -> DefaultCategoryProvisioning -> Activities
- L497: L334:     Identity -> UserRegisteredV1 -> Activities
- L499: L377: - menor coordinación directa desde Identity hacia Activities;
- L505: L603: SPIKE-01 evaluó si el registro podía desacoplar la dependencia directa de Identity hacia Activities mediante:
- L508: L761: Identity
- L512: L796: Identity
- L527: L1133: 3. volver a conectar Identity con el mecanismo equivalente a 'DefaultCategoryProvisioning';
- L546: L189: La hipótesis evaluada fue comprobar si el registro podía desacoplar la dependencia directa de Identity hacia Activities mediante 'UserRegisteredV1', manteniendo el comportamiento funcional esperado.
- L548: L260: Se adopta 'UserRegisteredV1' como mecanismo interno para comunicar desde Identity hacia Activities que un nuevo usuario ha sido registrado.
- L552: L295:     Identity -> UserRegisteredV1 -> Activities
- L554: L334:     Identity -> UserRegisteredV1 -> Activities
- L591: L603: SPIKE-01 evaluó si el registro podía desacoplar la dependencia directa de Identity hacia Activities mediante:
- L602: L689: Sin embargo, la existencia previa del archivo no demuestra por sí sola que el Context Map hubiera sido utilizado como evidencia causal para tomar ADR-003.
- L605: L738: Los refinamientos posteriores del Context Map se utilizan como trazabilidad adicional para revisar las fronteras y dependencias entre contextos, pero no como justificación retrospectiva de la decisión tomada el 2026-09-30.
- L640: L161: 'UserRegisteredV1' permitió desacoplar el flujo experimental entre Identity y Activities, conservar el registro del usuario, crear correctamente las categorías predeterminadas y cumplir el umbral local de 2000 ms en las cuatro ejecuciones observadas.
- L690: L148: 'UserRegisteredV1' sigue siendo útil para desacoplar Identity de Activities, pero la asincronía introduce una ventana de consistencia eventual y un riesgo de pérdida del efecto secundario cuando el consumidor falla.
- L1040: L689: Sin embargo, la existencia previa del archivo no demuestra por sí sola que el Context Map hubiera sido utilizado como evidencia causal para tomar ADR-003.
- L1052: L1276: pero no demuestran que el Context Map haya causado ADR-003.
- L1067: L1419: La existencia previa del Context Map no demuestra que haya sido utilizado como evidencia causal para ADR-003.
- L1146: L2066: → incorporación definitiva al Context Map pendiente.
- L1161: L40: De igual forma, la identificación de un bounded context no implica que cada contexto deba desplegarse como un microservicio independiente.
- L1167: L19: 'Achievement' permanece fuera de la matriz principal porque su ownership continúa como 'NO DECIDIDO'; por ello, no se considera todavía un bounded context confirmado dentro de este documento.
- L1168: L30: \| Progress \| Interpretar y agregar los resultados producidos por otras capacidades del sistema para medir y representar el avance del usuario mediante rachas y métricas de progreso. \| Interpretación de progreso, racha actual, mejor racha y métricas agregadas. \| Hechos producidos por Activities y Focus. \| 'Activity', 'PomodoroSession', 'Reminde […]
- L1171: L209: Sin embargo, esta evidencia no permite concluir todavía que Achievement constituya un bounded context independiente ni que deba pertenecer a Activities, Focus o Progress.
- L1173: L243: - 'Achievement' no aparecerá como bounded context confirmado mientras su ownership permanezca 'NO DECIDIDO'.
- L1176: L295: → Activities se mantiene como bounded context independiente.
- L1177: L500: → Focus se mantiene como bounded context independiente.
- L1178: L830: → Reminders se mantiene como bounded context independiente.
- L1181: L1206: → Progress se mantiene como bounded context independiente.
- L1184: L1372: convertirse en un bounded context independiente.
- L1185: L1377: Achievement como bounded context = NO DECIDIDO
- L1186: L2066: → incorporación definitiva al Context Map pendiente.
- L1188: L2109: - si cada bounded context tendrá despliegue independiente;
- L1189: L2193:   como bounded contexts independientes.
- L1191: L2264: declararlo como bounded context independiente.
- L1199: L791: para crear bounded contexts independientes.
- L1203: L875: El equipo decide modelar 'Activities' como un bounded context independiente.
- L1205: L998: **Decisión del equipo:** bounded context independiente que contiene 'Activity',
- L1219: L71: \| Trabajo posterior / M5 \| Modelado de dominio, Context Map, contrato API, AsyncAPI, auditoría de eventos, CQRS / Event Sourcing y experimentos posteriores. \| 'docs/dominio/', 'docs/integracion/', 'docs/asyncapi/', 'experimentos/spike-02-resiliencia/', 'experimentos/spike-03-caracterizacion-racha/', 'docs/adr/0003-integracion-eventos-internos. […]
- L1221: L87: 7. 'docs/dominio/' — modelado de subdominios, bounded contexts, responsabilidades, ownership y Context Map correspondiente a M5.
- L1227: L114: - 'docs/dominio/' — análisis de subdominios, bounded contexts, responsabilidades, ownership y Context Map de M5.
- L1257: - Identity en Context Map;

#### `docs/m5/cierre-pista2.md`

- L37: \| 11 \| Identity en Context Map \| DECISIÓN PENDIENTE \|
- L52: ## 4. Identity
- L56: La convención del Context Map está confirmada:
- L60: La evidencia permite evaluar Identity con los criterios usados para las demás fronteras.
- L64: - todos los bounded contexts vigentes;
- L67: No se modifica el Context Map hasta que exista una decisión explícita.
- L80: - bounded context independiente.
- L164: - Identity sin cierre automático;
- L175: 1. alcance de Identity en el Context Map;

#### `docs/m5/correcciones-auditoria-final.md`

- L81: 'Identity / UserService'
- L107: ## 7. A-02 — módulos/capacidades versus bounded contexts
- L113: ADR-003 utiliza históricamente una formulación en la que Identity, Activities,
- L120: 'bounded contexts formalmente cerrados'
- L129: 'bounded contexts formalmente decididos'
- L132: En particular, esta aclaración no decide el alcance de Identity en el Context
- L137: ## 8. D-01 / D-02 — Identity
- L143: La auditoría confirma que Identity existe como capacidad implementada y participa
- L146: Eso no autoriza a esta corrección documental a decidir si Identity debe
- L147: incorporarse como bounded context al Context Map.
- L153: - Identity en Context Map;

#### `docs/m5/correcciones-post-freeze.md`

- L44: - Context Map;
- L78: - Identity en Context Map;
- L167: - Identity en Context Map;

#### `docs/m5/freeze-final.md`

- L31: - alcance de Identity en el Context Map;
- L53: - Identity permanece pendiente;

#### `docs/m5/veredicto-semantico-final.md`

- L47: La revisión no decide el alcance de Identity dentro del Context Map.
- L49: ## 4. AS-02 — Context Map
- L64: Achievement continúa fuera del mapa como bounded context confirmado mientras su
- L67: Esta revisión no incorpora Identity automáticamente.
- L108: - Identity produce 'UserRegisteredV1';
- L272: - alcance de Identity en el Context Map;
- L326: - que Identity deba incorporarse o excluirse definitivamente del Context Map;

#### `docs/semana8/antes-despues.md`

- L50: - 'identity'
- L61: \| auth \| identity/auth \|
- L62: \| user \| identity/user \|
- L63: \| security \| identity/security \|
- L89: - Identity -> Activities mediante 'DefaultCategoryProvisioning';
- L107: - Identity -> User;

#### `docs/semana8/baseline-pre-modular.md`

- L69: - Identity
- L79: \| Identity \| 'auth', 'security', 'user' \|

#### `docs/semana8/c4/README.md`

- L44: - Identity
- L53: - Identity -> Activities mediante 'DefaultCategoryProvisioning'

#### `docs/semana8/c4/c4-l3-backend-modular.md`

- L24: identity["Identity<br/>Auth + User + Security"]
- L34: android -->\|"HTTP / JSON + JWT"\| identity
- L40: identity -->\|"DefaultCategoryProvisioning"\| activities
- L45: identity --> shared
- L48: identity -->\|"UserRepository"\| postgres

#### `docs/semana8/defensa-comite.md`

- L55: - Identity -> Activities con 'DefaultCategoryProvisioning'

#### `docs/semana8/edav.md`

- L27: - Identity;
- L70: - Identity, Activities, Focus, Progress y Reminders representan capacidades reconocibles;
- L138: - Identity utiliza 'DefaultCategoryProvisioning';

#### `docs/semana8/fronteras-modulares.md`

- L46: \| Identity \| Usuarios, autenticación e infraestructura de seguridad asociada \|
- L65: \| Identity \| users \|
- L106: ### 5.3 Identity hacia Activities
- L117: Identity -> CategoryRepository
- L118: Identity -> CategoryEntity
- L120: Esto hace que Identity conozca detalles internos pertenecientes a Activities.
- L172: Identity necesita solicitar la creación de categorías iniciales después de registrar un usuario.
- L190: Identity -> Activities API
- L208: Identity -> Activities Repository
- L210: Identity -> Activities Entity

#### `docs/semana8/matriz-trazabilidad.md`

- L119: \| TR-002 \| L3 \| Frontera modular \| Organizar el backend por capacidades de negocio \| 'backend/src/main/kotlin/com/example/rachapro/backend/' \| paquetes 'identity', 'activities', 'focus', 'progress', 'reminders', 'shared' \| 🏗 AS-IS \|
- L122: \| TR-005 \| L3 \| API intermodular \| Permitir a Identity crear categorías iniciales sin importar 'CategoryRepository' \| 'activities/api/DefaultCategoryProvisioning.kt' \| 'DefaultCategoryProvisioningService' + 'UserService' \| ✅ CONCUERDA \|

#### `docs/semana8/resumen-ejecutivo.md`

- L21: - Identity
- L65: - auth, user y security -> Identity
- L79: - Identity -> Activities mediante 'DefaultCategoryProvisioning'
- L117: - Identity

#### `docs/semana8/riesgos-tradeoffs-costos.md`

- L89: - Identity -> Activities;

#### `docs/semana9/09-api-eventos-integracion.md`

- L33: - mapear subdominios y bounded contexts;
- L55: - Identity;
- L75: \| Identity \| 'users' \|
- L91: ## 3. Mapa de subdominios y bounded contexts
- L93: ## 3.1 Identity
- L105: - infraestructura de seguridad asociada a la identidad.
- L113: Identity no debe crear ni persistir directamente entidades pertenecientes a Activities.
- L117: Después del registro de un usuario, Identity solicita actualmente a Activities la creación de las categorías predeterminadas.
- L249: Shared no se clasifica como bounded context de negocio.
- L273: - Activities expone 'DefaultCategoryProvisioning', utilizado por Identity.
- L281: \| Identity \| Activities \| 'DefaultCategoryProvisioning' \| síncrono \|
- L320: - Identity.
- L322: Después del registro de un usuario, Identity solicita mediante este contrato el aprovisionamiento de las categorías iniciales administradas por Activities.
- L324: A diferencia de 'ActivityLookup', esta operación representa un efecto posterior al registro y no entrega a Identity información de negocio necesaria para tomar una decisión inmediata.
- L347: 'Identity -> DefaultCategoryProvisioning -> Activities'
- L351: 'Identity -> UserRegisteredV1 -> Activities'
- L375: Identity.
- L496: > Un usuario fue persistido correctamente por Identity.
- L508: ¿La creación de categorías predeterminadas puede desacoplarse de Identity mediante un evento interno sin introducir una complejidad o una inconsistencia funcional que no se justifique para RachaPro?
- L514: 'Identity -> DefaultCategoryProvisioning'
- L520: y Activities procesa ese evento después del registro del usuario, entonces será posible eliminar la dependencia directa de Identity hacia el contrato de Activities manteniendo el comportamiento observable de creación de categorías predeterminadas.
- L562: 2. Identity deja de depender directamente de 'DefaultCategoryProvisioning' dentro del spike;
- L605: - dependencia Identity -> Activities antes;
- L606: - dependencia Identity -> Activities durante el spike;
- L647: 1. Identity confirma la creación del usuario.
- L670: - 'Identity -> DefaultCategoryProvisioning'
- L691: - Identity utiliza Activities para aprovisionar categorías iniciales.
- L696: - experimentar únicamente con el flujo Identity -> Activities.
- L700: 'UserRegisteredV1' podría reducir la dependencia directa de Identity hacia Activities sin deteriorar el comportamiento esperado.

#### `docs/uso-ia/auditoria-eventos.md`

- L54: Identity
- L74: Context Map
- L87: Los bounded contexts considerados son:
- L102: El Context Map utilizado como referencia contiene las relaciones:
- L401: Sin embargo, no existe evidencia suficiente de que otro bounded context necesite
- L538: No existe evidencia de que otro bounded context deba reaccionar ante cada
- L721: Sin embargo, no se ha demostrado que otro bounded context necesite conocer ese
- L977: bounded context propietario
- L994: ## 13. Auditoría de Identity
- L1239: bounded contexts.
- L1450: dominio, los flujos, el Context Map y el código actual.
- L1464: Identity

#### `experimentos/semana8-post-modular-validation/README.md`

- L69: - Identity;

#### `experimentos/spike-01-integracion/README.md`

- L15: Evaluar si el flujo de registro puede desacoplar la dependencia directa de Identity hacia Activities mediante el evento interno 'UserRegisteredV1', manteniendo el comportamiento funcional esperado.
- L23: 1. Identity guarda el usuario.
- L25: 3. El contrato 'UserRegisteredV1' se ubica en 'identity.api'.
- L120: \| Identity elimina la llamada directa a 'DefaultCategoryProvisioning' dentro del spike \| PASS \|
- L133: La separación elimina del flujo experimental la llamada directa de Identity hacia 'DefaultCategoryProvisioning'.
- L161: 'UserRegisteredV1' permitió desacoplar el flujo experimental entre Identity y Activities, conservar el registro del usuario, crear correctamente las categorías predeterminadas y cumplir el umbral local de 2000 ms en las cuatro ejecuciones observadas.

#### `experimentos/spike-02-resiliencia/README.md`

- L14: Identity / UserService
- L148: 'UserRegisteredV1' sigue siendo útil para desacoplar Identity de Activities, pero la asincronía introduce una ventana de consistencia eventual y un riesgo de pérdida del efecto secundario cuando el consumidor falla.

#### `experimentos/spike-03-caracterizacion-racha/00-preregistro.md`

- L256: - Identity
- L262: La convención utilizada en el Context Map es:
- L286: SPIKE-03 no modifica el Context Map.

#### `experimentos/spike-03-caracterizacion-racha/registro-delegacion-ia.md`

- L63: - registro de identidad del proceso JVM
- L100: - Context Map
- L148: - CA-12: ejecutar cada corrida en un proceso JVM nuevo y conservar evidencia de identidad del proceso
- L195: - registrar identidad JVM

#### `experimentos/spike-03-caracterizacion-racha/resultados.md`

- L175: ## 4. Identidad y validez de las corridas

#### `experimentos/spike-03-caracterizacion-racha/veredicto.md`

- L263: ### 11.1. Identidad de las JVM utilizadas

### P1-ACTIVITYLOOKUP

Coincidencias encontradas: **210**.

#### `README.md`

- L277: Las interacciones que atraviesan fronteras utilizan contratos explícitos como 'ActivityLookup' y 'DefaultCategoryProvisioning'.

#### `docs/adr/0002-aislamiento-persistencia.md`

- L259: ### 8.1 ActivityLookup
- L263: interface ActivityLookup {
- L264: fun existsActiveActivityForUser(
- L405: ActivityLookup
- L557: 5. Activities proporcione el contrato equivalente a 'ActivityLookup';

#### `docs/adr/0003-integracion-eventos-internos.md`

- L305: Focus -> ActivityLookup
- L306: Reminders -> ActivityLookup

#### `docs/dominio/context-map.puml`

- L25: Reminders --> Activities : consulta y valida referencia opcional a Activity\nmediante ActivityLookup cuando existe activityId

#### `docs/dominio/evidencia/activities.md`

- L3:
- L5: ## Actualización posterior — semántica de ActivityLookup
- L21: El backend muestra consumidores actuales de 'ActivityLookup' en Focus y
- L27:
- L511: 'ActivityLookup'
- L515: 'existsActiveActivityForUser(activityId, userId)'
- L519: 'ActivityLookupService.existsActiveActivityForUser(...)'
- L530: Focus ──> ActivityLookup ──> Activities
- L568: Activities mediante 'ActivityLookup'.
- L660: ### Semántica de 'active' en 'ActivityLookup'
- L664: 'existsActiveActivityForUser(activityId, userId)'
- L668: 'ActivityLookupService.existsActiveActivityForUser(...)'
- L803: 1. significado contractual definitivo del término 'active' en 'ActivityLookup'
- L822: 8. consumidor actual exacto de 'ActivityLookup' dentro de Reminders
- L841: \| Uso de 'ActivityLookup' por Focus \| 'HECHO DEL REPOSITORIO' \|
- L842: \| Significado real de 'active' en 'ActivityLookup' \| 'TENSIÓN DOCUMENTADA' \|
- L870: - 'ActivityLookup' permite exponer una capacidad limitada sin entregar acceso
- L937: Focus consulta Activities mediante 'ActivityLookup' cuando necesita validar una

#### `docs/dominio/evidencia/focus.md`

- L15: - cuando existe 'activityId', Focus consulta Activities mediante 'ActivityLookup'
- L71: - cuando se proporciona 'activityId', se consulta 'ActivityLookup'
- L118: - 'ActivityLookup' cuando existe una referencia a Activity
- L133: 'ActivityLookup'
- L140: Focus ──> ActivityLookup ──> Activities
- L261: \| Uso de 'ActivityLookup' \| 'HECHO DEL REPOSITORIO' \|
- L342: 'ActivityLookup' para consultar información perteneciente a Activities.

#### `docs/dominio/evidencia/reminders.md`

- L161: - cuando existe 'activityId', se consulta 'ActivityLookup'
- L185: 'ActivityLookup.existsActiveActivityForUser(activityId, userId)'
- L198: → Reminders consulta ActivityLookup
- L487: 'ActivityLookup'
- L527: 'ActivityLookup'
- L532: Reminders ──> ActivityLookup ──> Activities
- L902: \| Uso condicional de 'ActivityLookup' \| 'HECHO DEL REPOSITORIO' \|
- L1028: 'ActivityLookup'
- L1035: \| ActivityLookup

#### `docs/dominio/justificacion-contextos.md`

- L152: La estructura actual también contiene un contrato 'ActivityLookup', expuesto
- L159: existsActiveActivityForUser(activityId, userId)
- L203: Cuando existe 'activityId', Focus utiliza 'ActivityLookup' para validar la
- L211: → validación mediante ActivityLookup
- L221: Cuando existe 'activityId', Reminders utiliza 'ActivityLookup' para validar la
- L229: → validación mediante ActivityLookup
- L249: 'ActivityLookup' como mecanismo para validar una referencia individual a
- L279: → existe ActivityLookup como contrato observado para permitir
- L394: → PomodoroSessionService consulta ActivityLookup
- L437: Cuando existe 'activityId', utiliza 'ActivityLookup' para validar la referencia.
- L442: → validación mediante ActivityLookup
- L489: mediante ActivityLookup.
- L587: Cuando existe 'activityId', 'ReminderService' utiliza 'ActivityLookup' para
- L599: → se consulta ActivityLookup
- L603: La implementación observada de 'ActivityLookup' comprueba que la Activity:
- L738: - utiliza 'ActivityLookup';
- L747: → validación mediante ActivityLookup
- L811: la referencia mediante ActivityLookup.
- L1903: → validación mediante ActivityLookup
- L1907: → validación mediante ActivityLookup
- L1923: \| 'Focus → Activities' \| Referencia opcional y validación mediante 'ActivityLookup' \|
- L1924: \| 'Reminders → Activities' \| Referencia opcional y validación mediante 'ActivityLookup' \|
- L2144: → validación mediante ActivityLookup
- L2148: → validación mediante ActivityLookup
- L2229: → validación mediante ActivityLookup
- L2233: → validación mediante ActivityLookup

#### `docs/dominio/responsabilidades-contextos.md`

- L29: \| Reminders \| Definir, programar y gestionar el ciclo de vida de los recordatorios del usuario, conservando la intención de generar un aviso en un momento determinado y permitiendo opcionalmente asociarlo a una Activity. \| 'Reminder', momento programado y ciclo funcional 'SCHEDULED / DELIVERED / CANCELLED'. \| 'userId' y referencia opcional a 'Activ […]
- L123: - validación de la referencia mediante 'ActivityLookup' cuando existe 'activityId'.

#### `docs/integracion/README.md`

- L13: Formaliza la semántica contractual vigente documentada para 'ActivityLookup'.

#### `docs/integracion/cobertura-interacciones-sync-async.md`

- L22: \| Focus → Activities \| 'ActivityLookup' como contrato síncrono observado \| Comparación explícita en 'docs/integracion/sincrono-vs-asincrono.md' \| EVALUADA \|
- L23: \| Reminders → Activities \| 'ActivityLookup' como contrato síncrono observado \| No se identificó una comparación completa independiente equivalente a la de Focus; comparte semántica contractual con Focus \| COBERTURA PARCIAL / SIN CAMBIO DECIDIDO \|
- L57: - modificar 'ActivityLookup';

#### `docs/integracion/contrato-api.md`

- L3:
- L7: En el estado actual observado del backend, 'ActivityLookup' es consumido por al
- L19:
- L21:
- L25: 'ActivityLookup' es una capacidad pública de Activities utilizada actualmente
- L44: Compartir 'ActivityLookup' no implica que Focus y Reminders posean el mismo
- L47:
- L152: ActivityLookup.activityId
- L165: interface ActivityLookup {
- L166: fun existsActiveActivityForUser(
- L176: existsActiveActivityForUser
- L234: activityId debe estar presente cuando se invoca ActivityLookup
- L243: → Focus no invoca ActivityLookup
- L246: → Focus puede invocar ActivityLookup
- L453: - no invocar 'ActivityLookup' cuando no existe 'activityId'.
- L510: → invoca ActivityLookup
- L776: 'ActivityLookup' es el contrato público utilizado para preservar esta frontera.
- L811: ActivityLookup.existsActiveActivityForUser
- L822: Focus no invoca ActivityLookup

#### `docs/integracion/eventos-candidatos.md`

- L1987: ActivityLookup

#### `docs/integracion/sincrono-vs-asincrono.md`

- L90: interface ActivityLookup {
- L91: fun existsActiveActivityForUser(
- L418: ActivityLookup
- L602: → ActivityLookup
- L744: Por tanto, es plausible que el tiempo utilizado por 'ActivityLookup' contribuya
- L752: específica de la latencia de ActivityLookup
- L806: y no una incapacidad específica de ejecutar 'ActivityLookup'.
- L856: - los puntos donde Focus consume 'ActivityLookup'.
- L907: → la latencia de ActivityLookup es suficientemente significativa
- L1015: ActivityLookup
- L1193: ActivityLookup
- L1236: → Focus utiliza ActivityLookup como API pública.
- L1301: → latencia específica de ActivityLookup.
- L1372: - latencia real de 'ActivityLookup'.

#### `docs/m5/auditoria-externa-post-freeze.md`

- L43: - ActivityLookup;
- L129: - renombre de ActivityLookup;

#### `docs/m5/auditoria-final.md`

- L40: \| A07 ActivityLookup \| BIEN \| Semantica documentada y consumidores Focus/Reminders trazables. \|
- L63: - eventual renombre contractual de ActivityLookup;

#### `docs/m5/auditoria-semantica-semanas9-10.md`

- L153: L25: Reminders --> Activities : consulta y valida referencia opcional a Activity\nmediante ActivityLookup cuando existe activityId
- L162: ## AS-03 — ActivityLookup y contratos
- L166: L3:
- L167: L7: En el estado actual observado del backend, 'ActivityLookup' es consumido por al
- L170: L19:
- L180: L124: ActivityLookup.activityId
- L182: L137: interface ActivityLookup {
- L183: L138:     fun existsActiveActivityForUser(
- L184: L148: existsActiveActivityForUser
- L191: L206: activityId debe estar presente cuando se invoca ActivityLookup
- L192: L215: → Focus no invoca ActivityLookup
- L193: L218: → Focus puede invocar ActivityLookup
- L205: L425: - no invocar 'ActivityLookup' cuando no existe 'activityId'.
- L208: L482: → invoca ActivityLookup
- L217: L3:
- L218: L5: ## Actualización posterior — semántica de ActivityLookup
- L223: L21: El backend muestra consumidores actuales de 'ActivityLookup' en Focus y
- L225: L27:
- L244: L511: 'ActivityLookup'
- L245: L515: 'existsActiveActivityForUser(activityId, userId)'
- L246: L519: 'ActivityLookupService.existsActiveActivityForUser(...)'
- L248: L530: Focus ──> ActivityLookup ──> Activities
- L252: L568: Activities mediante 'ActivityLookup'.
- L258: L660: ### Semántica de 'active' en 'ActivityLookup'
- L259: L664: 'existsActiveActivityForUser(activityId, userId)'
- L260: L668: 'ActivityLookupService.existsActiveActivityForUser(...)'
- L272: L15: - cuando existe 'activityId', Focus consulta Activities mediante 'ActivityLookup'
- L275: L71: - cuando se proporciona 'activityId', se consulta 'ActivityLookup'
- L280: L118: - 'ActivityLookup' cuando existe una referencia a Activity
- L281: L133: 'ActivityLookup'
- L282: L140: Focus ──> ActivityLookup ──> Activities
- L297: L261: \| Uso de 'ActivityLookup' \| 'HECHO DEL REPOSITORIO' \|
- L310: L342: 'ActivityLookup' para consultar información perteneciente a Activities.
- L331: L161: - cuando existe 'activityId', se consulta 'ActivityLookup'
- L334: L185: 'ActivityLookup.existsActiveActivityForUser(activityId, userId)'
- L336: L198: → Reminders consulta ActivityLookup
- L353: L487: 'ActivityLookup'
- L357: L527: 'ActivityLookup'
- L358: L532: Reminders ──> ActivityLookup ──> Activities
- L1014: ### ActivityLookup consumidores
- L1015: - docs/integracion/contrato-api.md: ActivityLookup, Focus, Reminders
- L1016: - docs/dominio/evidencia/activities.md: ActivityLookup, Focus, Reminders
- L1017: - docs/semana9/09-api-eventos-integracion.md: ActivityLookup, Focus, Reminders
- L1259: - eventual renombre contractual de ActivityLookup;

#### `docs/m5/cierre-pista2.md`

- L39: \| 13 \| Nombre ActivityLookup \| SIN CAMBIO JUSTIFICADO \|
- L82: ## 6. ActivityLookup
- L96: Focus y Reminders consumen ActivityLookup.
- L167: - ActivityLookup trazable;
- L177: 3. eventual renombre de ActivityLookup;

#### `docs/m5/correcciones-auditoria-final.md`

- L155: - renombre de ActivityLookup;

#### `docs/m5/correcciones-post-freeze.md`

- L24: ActivityLookup.
- L39: - ActivityLookup;
- L80: - eventual renombre de ActivityLookup;
- L106: ### H-SEM-01 — alcance semántico de ActivityLookup
- L169: - renombre de ActivityLookup;

#### `docs/m5/freeze-final.md`

- L33: - eventual renombre contractual de ActivityLookup;
- L58: - ActivityLookup conserva trazabilidad;

#### `docs/m5/veredicto-semantico-final.md`

- L69: ## 5. AS-03 — ActivityLookup
- L71: La evidencia revisada permite sostener que 'ActivityLookup' comprueba
- L101: El eventual renombre de 'ActivityLookup' continúa siendo una decisión
- L235: - consumidores de ActivityLookup;
- L274: - eventual renombre contractual de ActivityLookup;
- L328: - que ActivityLookup deba conservar su nombre para siempre;

#### `docs/semana8/antes-despues.md`

- L84: - 'ActivityLookup'
- L90: - Focus -> Activities mediante 'ActivityLookup';
- L91: - Reminders -> Activities mediante 'ActivityLookup'.

#### `docs/semana8/c4/README.md`

- L54: - Focus -> Activities mediante 'ActivityLookup'
- L55: - Reminders -> Activities mediante 'ActivityLookup'

#### `docs/semana8/c4/c4-l3-backend-modular.md`

- L41: focus -->\|"ActivityLookup"\| activities
- L42: reminders -->\|"ActivityLookup"\| activities

#### `docs/semana8/defensa-comite.md`

- L56: - Focus -> Activities con 'ActivityLookup'
- L57: - Reminders -> Activities con 'ActivityLookup'

#### `docs/semana8/edav.md`

- L139: - Focus y Reminders utilizan 'ActivityLookup';

#### `docs/semana8/fronteras-modulares.md`

- L160: ActivityLookup
- L164: existsActiveActivityForUser(activityId, userId)
- L287: - 'ActivityLookup' y 'DefaultCategoryProvisioning' fueron utilizados como contratos explícitos entre capacidades.

#### `docs/semana8/matriz-trazabilidad.md`

- L121: \| TR-004 \| L3 \| API intermodular \| Permitir a Focus y Reminders consultar Activities sin importar su persistencia \| 'activities/api/ActivityLookup.kt' \| 'ActivityLookupService', 'PomodoroSessionService', 'ReminderService' \| ✅ CONCUERDA \|

#### `docs/semana8/resumen-ejecutivo.md`

- L74: - 'ActivityLookup'
- L80: - Focus -> Activities mediante 'ActivityLookup'
- L81: - Reminders -> Activities mediante 'ActivityLookup'

#### `docs/semana8/riesgos-tradeoffs-costos.md`

- L113: - 'ActivityLookup';

#### `docs/semana9/09-api-eventos-integracion.md`

- L151: - 'ActivityLookup'
- L185: 'ActivityLookup'
- L239: 'ActivityLookup'
- L274: - Activities expone 'ActivityLookup', utilizado por Focus.
- L275: - Activities expone 'ActivityLookup', utilizado por Reminders.
- L282: \| Focus \| Activities \| 'ActivityLookup' \| síncrono \|
- L283: \| Reminders \| Activities \| 'ActivityLookup' \| síncrono \|
- L293: ### 5.1 ActivityLookup
- L295: 'ActivityLookup' es un contrato público de Activities.
- L299: 'existsActiveActivityForUser(activityId, userId)'
- L324: A diferencia de 'ActivityLookup', esta operación representa un efecto posterior al registro y no entrega a Identity información de negocio necesaria para tomar una decisión inmediata.
- L333: #### Focus -> ActivityLookup
- L337: #### Reminders -> ActivityLookup
- L663: - 'Focus -> ActivityLookup'
- L664: - 'Reminders -> ActivityLookup'

#### `docs/uso-ia/auditoria-eventos.md`

- L1256: ActivityLookup

### P1-USERREGISTERED

Coincidencias encontradas: **226**.

#### `README.md`

- L86: 6. 'docs/adr/0003-integracion-eventos-internos.md' — decisión vigente para el flujo post-registro Identity → Activities mediante 'UserRegisteredV1'.
- L89: 9. 'docs/asyncapi/rachapro-events-v1.yaml' — contrato técnico vigente del evento 'UserRegisteredV1'.
- L113: - 'docs/adr/0003-integracion-eventos-internos.md' — decisión arquitectónica posterior sobre 'UserRegisteredV1' para el flujo Identity → Activities.
- L116: - 'docs/asyncapi/rachapro-events-v1.yaml' — formalización técnica del contrato vigente de 'UserRegisteredV1'.
- L156: > - 'experimentos/spike-01-integracion/' — evaluación de la integración interna mediante 'UserRegisteredV1';
- L341: - actualización temporal del productor de 'UserRegisteredV1';

#### `docs/adr/0002-aislamiento-persistencia.md`

- L94: Durante la creación de un usuario, 'UserService' crea las categorías predeterminadas:
- L540: - 'UserService.kt';

#### `docs/adr/0003-integracion-eventos-internos.md`

- L131: UserRegisteredV1
- L189: La hipótesis evaluada fue comprobar si el registro podía desacoplar la dependencia directa de Identity hacia Activities mediante 'UserRegisteredV1', manteniendo el comportamiento funcional esperado.
- L260: Se adopta 'UserRegisteredV1' como mecanismo interno para comunicar desde Identity hacia Activities que un nuevo usuario ha sido registrado.
- L268: \| publica UserRegisteredV1
- L295: Identity -> UserRegisteredV1 -> Activities
- L334: Identity -> UserRegisteredV1 -> Activities
- L363: La introducción de 'UserRegisteredV1' resuelve un caso específico de integración entre módulos y no constituye por sí sola CQRS.
- L418: Validar 'UserRegisteredV1' no demuestra que todas las relaciones entre módulos deban convertirse en eventos.
- L502: **UserRegisteredV1:** IMPLEMENTADO
- L606: UserRegisteredV1
- L763: UserRegisteredV1
- L790: UserRegisteredV1
- L797: → publica UserRegisteredV1
- L800: → consume UserRegisteredV1
- L845: UserRegisteredV1
- L863: no se observó replay de UserRegisteredV1
- L924: UserRegisteredV1
- L929: 'UserRegisteredV1' posee una situación distinta a candidatos meramente potenciales porque existe evidencia del repositorio de:
- L942: UserRegisteredV1 adoptado
- L1025: UserRegisteredV1
- L1119: - cambios en 'UserRegisteredV1'.
- L1131: 1. sustituir para este flujo la publicación y consumo de 'UserRegisteredV1';
- L1137: 7. revisar el AsyncAPI si 'UserRegisteredV1' deja de formar parte de la integración vigente.
- L1152: - múltiples consumidores dependientes de 'UserRegisteredV1';
- L1183: UserRegisteredV1 existe en el código productivo.
- L1188: UserService publica UserRegisteredV1.
- L1193: Activities posee UserRegisteredV1Listener.
- L1249: UserRegisteredV1 reduce la coordinación directa
- L1294: para UserRegisteredV1.
- L1322: \| No se requiere entrega durable para 'UserRegisteredV1'. \| El contrato actual documenta 'durable: false'; no se encontró en las fuentes revisadas un requisito explícito de entrega durable para este flujo. \| Se observan pérdidas del efecto secundario que, bajo un requisito o escenario vigente, requieran recuperación garantizada, o aparece un requis […]
- L1326: \| La decisión puede continuar limitada a 'UserRegisteredV1'. \| La auditoría posterior de eventos candidatos no adopta automáticamente otros eventos. \| Una interacción adicional cuenta con problema concreto, alternativas evaluadas y evidencia suficiente para justificar una nueva decisión arquitectónica. \|
- L1391: En las fuentes revisadas no se encontró un requisito explícito que establezca que, después de un fallo post-commit del consumidor, 'UserRegisteredV1' deba recuperarse automáticamente mediante retry o replay, ni un umbral temporal específico para dicha recuperación.
- L1423: La auditoría de eventos no convierte 'UserRegisteredV1' en una política general de integración mediante eventos.
- L1455: - que 'UserRegisteredV1' sea la mejor alternativa posible;

#### `docs/asyncapi/rachapro-events-v1.yaml`

- L7: UserRegisteredV1 es actualmente un Spring ApplicationEvent en proceso;
- L15: title: UserRegisteredV1
- L17: Canal lógico que representa el evento interno UserRegisteredV1.
- L21: UserRegisteredV1:
- L22: $ref: '#/components/messages/UserRegisteredV1'
- L27: title: Identity publica UserRegisteredV1
- L30: UserService publica UserRegisteredV1 como parte del flujo de registro.
- L35: - $ref: '#/channels/userRegistered/messages/UserRegisteredV1'
- L39: title: Activities consume UserRegisteredV1
- L42: UserRegisteredV1Listener recibe el evento mediante
- L48: - $ref: '#/channels/userRegistered/messages/UserRegisteredV1'
- L52: UserRegisteredV1:
- L53: name: UserRegisteredV1
- L87: listener: UserRegisteredV1Listener

#### `docs/dominio/evidencia/activities.md`

- L437: 'UserRegisteredV1Listener'
- L448: Por tanto, no debe reconstruirse retrospectivamente 'UserRegisteredV1Listener'
- L590: por 'UserRegisteredV1'.
- L596: 'UserRegisteredV1Listener'
- L615: \| UserRegisteredV1
- L756: 'UserRegisteredV1Listener' existe en el repositorio actual, pero la trazabilidad
- L819: 5. permanencia de 'UserRegisteredV1' como mecanismo arquitectónico definitivo
- L846: \| 'UserRegisteredV1Listener' posterior al baseline S8 \| 'HECHO DEL REPOSITORIO / TRAZABILIDAD GIT' \|
- L961: La integración mediante 'UserRegisteredV1' corresponde a una evolución posterior

#### `docs/dominio/justificacion-contextos.md`

- L1706: UserRegisteredV1Listener
- L1710: con 'UserRegisteredV1' que participa en el aprovisionamiento de categorías por
- L1716: UserRegisteredV1
- L1745: Por ejemplo, la interacción asociada a 'UserRegisteredV1Listener' aporta
- L2025: Durante M5 se observó la interacción asociada a 'UserRegisteredV1Listener'.
- L2030: UserRegisteredV1
- L2043: si 'UserRegisteredV1' es producido por Identity y consumido por Activities, una
- L2059: → Activities consume UserRegisteredV1.

#### `docs/integracion/README.md`

- L30: ## UserRegisteredV1

#### `docs/integracion/aplicabilidad-cqrs-event-sourcing.md`

- L383: UserRegisteredV1
- L958: UserRegisteredV1
- L1484: UserRegisteredV1

#### `docs/integracion/cobertura-interacciones-sync-async.md`

- L24: \| Identity → Activities \| 'UserRegisteredV1' como evento interno; Activities consume después del commit mediante listener \| Evaluada mediante SPIKE-01 y decidida específicamente por ADR-003 \| EVALUADA PARA ESE FLUJO \|

#### `docs/integracion/eventos-candidatos.md`

- L5: ## Actualización posterior — estado de UserRegisteredV1
- L11: Posteriormente, ADR-003 adoptó 'UserRegisteredV1' específicamente para la
- L16: - 'UserRegisteredV1': adoptado e implementado para ese caso específico;
- L1661: ## 10.1 UserRegisteredV1
- L1669: 'UserRegisteredV1' existe actualmente como evento en el sistema.
- L1674: UserRegisteredV1Listener
- L1730: A diferencia de la mayoría de candidatos generados, 'UserRegisteredV1' no es
- L1745: 'UserRegisteredV1' pertenece al estado actual de M5.
- L1770: UserRegisteredV1
- L2118: # 17. Precisión sobre UserRegisteredV1
- L2120: 'UserRegisteredV1' tiene una situación diferente a los otros candidatos.
- L2181: UserRegisteredV1
- L2210: UserRegisteredV1
- L2261: ## Aclaración posterior — trazabilidad del productor de UserRegisteredV1
- L2264: del productor conceptual de 'UserRegisteredV1' todavía aparecía como pendiente.
- L2272: → 'UserService'
- L2273: → publica 'UserRegisteredV1'
- L2274: → Activities consume mediante 'UserRegisteredV1Listener'
- L2283: 'Identity / UserService'

#### `docs/m5/auditoria-final.md`

- L41: \| A08 UserRegisteredV1 \| BIEN \| Estado posterior y payload userId/occurredAt son trazables. \|

#### `docs/m5/auditoria-semantica-semanas9-10.md`

- L373: ## AS-04 — UserRegisteredV1 y AsyncAPI
- L376: L5: ## Actualización posterior — estado de UserRegisteredV1
- L377: L11: Posteriormente, ADR-003 adoptó 'UserRegisteredV1' específicamente para la
- L379: L16: - 'UserRegisteredV1': adoptado e implementado para ese caso específico;
- L417: L1661: ## 10.1 UserRegisteredV1
- L418: L1669: 'UserRegisteredV1' existe actualmente como evento en el sistema.
- L420: L1674: UserRegisteredV1Listener
- L423: L1730: A diferencia de la mayoría de candidatos generados, 'UserRegisteredV1' no es
- L425: L1745: 'UserRegisteredV1' pertenece al estado actual de M5.
- L426: L1770: UserRegisteredV1
- L435: L2118: # 17. Precisión sobre UserRegisteredV1
- L438: L7:     UserRegisteredV1 es actualmente un Spring ApplicationEvent en proceso;
- L439: L15:     title: UserRegisteredV1
- L440: L17:       Canal lógico que representa el evento interno UserRegisteredV1.
- L442: L21:       UserRegisteredV1:
- L443: L22:         $ref: '#/components/messages/UserRegisteredV1'
- L445: L27:     title: Identity publica UserRegisteredV1
- L446: L30:       UserService publica UserRegisteredV1 como parte del flujo de registro.
- L448: L35:       - $ref: '#/channels/userRegistered/messages/UserRegisteredV1'
- L450: L39:     title: Activities consume UserRegisteredV1
- L451: L42:       UserRegisteredV1Listener recibe el evento mediante
- L453: L48:       - $ref: '#/channels/userRegistered/messages/UserRegisteredV1'
- L454: L52:     UserRegisteredV1:
- L455: L53:       name: UserRegisteredV1
- L464: L87:   listener: UserRegisteredV1Listener
- L480: L131:     UserRegisteredV1
- L485: L189: La hipótesis evaluada fue comprobar si el registro podía desacoplar la dependencia directa de Identity hacia Activities mediante 'UserRegisteredV1', manteniendo el comportamiento funcional esperado.
- L486: L260: Se adopta 'UserRegisteredV1' como mecanismo interno para comunicar desde Identity hacia Activities que un nuevo usuario ha sido registrado.
- L488: L268:        \| publica UserRegisteredV1
- L494: L295:     Identity -> UserRegisteredV1 -> Activities
- L497: L334:     Identity -> UserRegisteredV1 -> Activities
- L498: L363: La introducción de 'UserRegisteredV1' resuelve un caso específico de integración entre módulos y no constituye por sí sola CQRS.
- L501: L418: Validar 'UserRegisteredV1' no demuestra que todas las relaciones entre módulos deban convertirse en eventos.
- L502: L502: **UserRegisteredV1:** IMPLEMENTADO
- L506: L606: UserRegisteredV1
- L509: L763: UserRegisteredV1
- L511: L790: UserRegisteredV1
- L513: L797: → publica UserRegisteredV1
- L515: L800: → consume UserRegisteredV1
- L517: L845: UserRegisteredV1
- L518: L863: no se observó replay de UserRegisteredV1
- L519: L924: UserRegisteredV1
- L520: L929: 'UserRegisteredV1' posee una situación distinta a candidatos meramente potenciales porque existe evidencia del repositorio de:
- L522: L942: UserRegisteredV1 adoptado
- L523: L1025: UserRegisteredV1
- L524: L1119: - cambios en 'UserRegisteredV1'.
- L525: L1131: 1. sustituir para este flujo la publicación y consumo de 'UserRegisteredV1';
- L539: L131:     UserRegisteredV1
- L546: L189: La hipótesis evaluada fue comprobar si el registro podía desacoplar la dependencia directa de Identity hacia Activities mediante 'UserRegisteredV1', manteniendo el comportamiento funcional esperado.
- L548: L260: Se adopta 'UserRegisteredV1' como mecanismo interno para comunicar desde Identity hacia Activities que un nuevo usuario ha sido registrado.
- L549: L268:        \| publica UserRegisteredV1
- L552: L295:     Identity -> UserRegisteredV1 -> Activities
- L554: L334:     Identity -> UserRegisteredV1 -> Activities
- L556: L363: La introducción de 'UserRegisteredV1' resuelve un caso específico de integración entre módulos y no constituye por sí sola CQRS.
- L560: L418: Validar 'UserRegisteredV1' no demuestra que todas las relaciones entre módulos deban convertirse en eventos.
- L568: L502: **UserRegisteredV1:** IMPLEMENTADO
- L592: L606: UserRegisteredV1
- L606: L763: UserRegisteredV1
- L607: L790: UserRegisteredV1
- L608: L797: → publica UserRegisteredV1
- L609: L800: → consume UserRegisteredV1
- L640: L161: 'UserRegisteredV1' permitió desacoplar el flujo experimental entre Identity y Activities, conservar el registro del usuario, crear correctamente las categorías predeterminadas y cumplir el umbral local de 2000 ms en las cuatro ejecuciones observadas.
- L655: L5: Evaluar experimentalmente qué ocurre cuando el consumidor de 'UserRegisteredV1' falla después de que la transacción que registra al usuario ya fue confirmada.
- L684: L121: No se observó retry automático durante los 5 segundos posteriores a retirar el fallo y tampoco se observó replay de 'UserRegisteredV1' durante los 5 segundos posteriores al reinicio del backend.
- L690: L148: 'UserRegisteredV1' sigue siendo útil para desacoplar Identity de Activities, pero la asincronía introduce una ventana de consistencia eventual y un riesgo de pérdida del efecto secundario cuando el consumidor falla.
- L1019: ### UserRegisteredV1
- L1020: - docs/integracion/eventos-candidatos.md: UserRegisteredV1, occurredAt
- L1021: - docs/asyncapi/rachapro-events-v1.yaml: UserRegisteredV1, occurredAt
- L1022: - docs/adr/0003-integracion-eventos-internos.md: UserRegisteredV1
- L1023: - docs/semana9/09-api-eventos-integracion.md: UserRegisteredV1, occurredAt
- L1035: L418: Validar 'UserRegisteredV1' no demuestra que todas las relaciones entre módulos deban convertirse en eventos.
- L1053: L1322: \| No se requiere entrega durable para 'UserRegisteredV1'. \| El contrato actual documenta 'durable: false'; no se encontró en las fuentes revisadas un requisito explícito de entrega durable para este flujo. \| Se observan pérdidas del efecto secundario que, bajo un requisito o escenario vigente, requieran recuperación garantizada, o aparece un […]
- L1070: L1455: - que 'UserRegisteredV1' sea la mejor alternativa posible;
- L1223: L89: 9. 'docs/asyncapi/rachapro-events-v1.yaml' — contrato técnico vigente del evento 'UserRegisteredV1'.
- L1229: L116: - 'docs/asyncapi/rachapro-events-v1.yaml' — formalización técnica del contrato vigente de 'UserRegisteredV1'.
- L1233: L156: > - 'experimentos/spike-01-integracion/' — evaluación de la integración interna mediante 'UserRegisteredV1';

#### `docs/m5/cierre-pista2.md`

- L166: - UserRegisteredV1 trazable;

#### `docs/m5/correcciones-auditoria-final.md`

- L72: ## 5. E-01 — productor de UserRegisteredV1
- L81: 'Identity / UserService'
- L82: → produce 'UserRegisteredV1'

#### `docs/m5/freeze-final.md`

- L57: - UserRegisteredV1 conserva trazabilidad;

#### `docs/m5/veredicto-semantico-final.md`

- L104: ## 6. AS-04 — UserRegisteredV1 y AsyncAPI
- L108: - Identity produce 'UserRegisteredV1';
- L236: - UserRegisteredV1;

#### `docs/semana8/fronteras-modulares.md`

- L108: UserService utiliza directamente:
- L130: - AuthController -> UserService

#### `docs/semana8/matriz-trazabilidad.md`

- L122: \| TR-005 \| L3 \| API intermodular \| Permitir a Identity crear categorías iniciales sin importar 'CategoryRepository' \| 'activities/api/DefaultCategoryProvisioning.kt' \| 'DefaultCategoryProvisioningService' + 'UserService' \| ✅ CONCUERDA \|

#### `docs/semana9/09-api-eventos-integracion.md`

- L351: 'Identity -> UserRegisteredV1 -> Activities'
- L361: ### 7.1 UserRegisteredV1
- L466: \| 'UserRegisteredV1' \| el registro de usuario existe y actualmente produce un efecto en Activities \| aceptar para Spike 1 \|
- L481: ## 9. Contrato experimental UserRegisteredV1
- L485: 'UserRegisteredV1'
- L504: ## 10. Spike 1 — integración UserRegisteredV1
- L518: 'UserRegisteredV1'
- L563: 3. Activities recibe el hecho 'UserRegisteredV1';
- L648: 2. Activities procesa posteriormente 'UserRegisteredV1'.
- L676: - 'UserRegisteredV1'
- L700: 'UserRegisteredV1' podría reducir la dependencia directa de Identity hacia Activities sin deteriorar el comportamiento esperado.
- L712: En las cuatro ejecuciones observadas, 'UserRegisteredV1' permitió conservar el registro del usuario y crear las categorías predeterminadas dentro del umbral experimental de 2000 ms. Los tiempos observados del evento fueron 28 ms, 65 ms, 36 ms y 32 ms, con mediana de 34 ms.

#### `docs/uso-ia/auditoria-eventos.md`

- L996: ### 13.1 UserRegisteredV1
- L1001: UserRegisteredV1
- L1021: UserRegisteredV1
- L1029: UserRegisteredV1Listener
- L1057: productor conceptual de UserRegisteredV1
- L1066: 'UserRegisteredV1' pertenece al estado actual de M5.
- L1101: UserRegisteredV1
- L1351: ### 19.1 Productor conceptual de UserRegisteredV1
- L1473: UserRegisteredV1
- L1501: UserRegisteredV1

#### `experimentos/spike-01-integracion/README.md`

- L1: # SPIKE-01 — Integración mediante UserRegisteredV1
- L15: Evaluar si el flujo de registro puede desacoplar la dependencia directa de Identity hacia Activities mediante el evento interno 'UserRegisteredV1', manteniendo el comportamiento funcional esperado.
- L24: 2. 'UserService' publica 'UserRegisteredV1'.
- L25: 3. El contrato 'UserRegisteredV1' se ubica en 'identity.api'.
- L26: 4. Activities recibe el evento mediante 'UserRegisteredV1Listener'.
- L52: La medición del evento de esa corrida sí fue obtenida del procesamiento registrado por 'UserRegisteredV1Listener'.
- L121: \| Activities recibe 'UserRegisteredV1' \| PASS \|
- L161: 'UserRegisteredV1' permitió desacoplar el flujo experimental entre Identity y Activities, conservar el registro del usuario, crear correctamente las categorías predeterminadas y cumplir el umbral local de 2000 ms en las cuatro ejecuciones observadas.

#### `experimentos/spike-02-resiliencia/README.md`

- L5: Evaluar experimentalmente qué ocurre cuando el consumidor de 'UserRegisteredV1' falla después de que la transacción que registra al usuario ya fue confirmada.
- L14: Identity / UserService
- L18: UserRegisteredV1
- L23: Activities / UserRegisteredV1Listener
- L32: data class UserRegisteredV1(
- L38: 'UserService' publica el evento dentro del flujo transaccional de registro. El listener de Activities se ejecuta después del commit y delega el aprovisionamiento en 'DefaultCategoryProvisioning'.
- L49: - Evento evaluado: 'UserRegisteredV1'.
- L50: - Consumidor: 'UserRegisteredV1Listener'.
- L121: No se observó retry automático durante los 5 segundos posteriores a retirar el fallo y tampoco se observó replay de 'UserRegisteredV1' durante los 5 segundos posteriores al reinicio del backend.
- L148: 'UserRegisteredV1' sigue siendo útil para desacoplar Identity de Activities, pero la asincronía introduce una ventana de consistencia eventual y un riesgo de pérdida del efecto secundario cuando el consumidor falla.

#### `experimentos/spike-03-caracterizacion-racha/00-preregistro.md`

- L295: - ADR-003 relacionado con el caso específico de 'UserRegisteredV1'

### P1-FOCUS-ACTIVITIES

Coincidencias encontradas: **152**.

#### `docs/adr/0001-decision-estilo.md`

- L483: La implementación resultante conserva una única aplicación Spring Boot y materializa las fronteras Identity, Activities, Focus, Progress y Reminders.

#### `docs/adr/0002-aislamiento-persistencia.md`

- L58: \| 'pomodoro' \| 'ActivityRepository' \| Cruce Focus -> Activities \|
- L84: Focus no necesita convertirse en propietario de la persistencia de Activities.
- L319: Focus -> Activities API
- L339: Focus -> Activities Repository

#### `docs/dominio/context-map.puml`

- L23: Focus --> Activities : consulta y valida referencia opcional a Activity\ncuando existe activityId

#### `docs/dominio/evidencia/activities.md`

- L509: Focus consulta actualmente información de Activities mediante el contrato:
- L530: Focus ──> ActivityLookup ──> Activities
- L816: 2. mecanismo definitivo de integración entre Activities y Focus
- L866: - las reglas internas de Activities son distintas de las reglas de Focus,
- L937: Focus consulta Activities mediante 'ActivityLookup' cuando necesita validar una

#### `docs/dominio/evidencia/focus.md`

- L15: - cuando existe 'activityId', Focus consulta Activities mediante 'ActivityLookup'
- L140: Focus ──> ActivityLookup ──> Activities
- L285: Activities no define por completo la existencia de Focus

#### `docs/dominio/evidencia/progress.md`

- L8: información proveniente principalmente de Activities y Focus para construir
- L110: poseen significado distinto de los conceptos internos de Activities y Focus.
- L143: - une los días provenientes de Activities y Focus
- L271: semana y combina métricas provenientes de Activities y Focus.
- L804: \| Progress combina información de Activities y Focus \| 'HECHO DEL REPOSITORIO' \|
- L824: \| Integración definitiva con Activities y Focus \| 'NO DECIDIDA' \|
- L835: - Progress combina hechos provenientes de Activities y Focus
- L844: Activities y Focus
- L875: - razonamiento arquitectónico: las reglas de interpretación de progreso pueden evaluarse y modificarse separadamente de los ciclos de vida internos de Activities y Focus, siempre que se mantengan los hechos necesarios de entrada
- L905: - interpretación de los días derivados de hechos de completitud producidos por Activities y Focus
- L1011: 5. Activities o Focus pasan a controlar directamente la semántica transversal
- L1027: separadamente de los ciclos internos de Activities y Focus.

#### `docs/dominio/evidencia/reminders.md`

- L51: forman una capacidad funcional coherente y distinguible de Activities, Focus,
- L961: - responsabilidad funcional diferenciable de Activities, Focus, Progress e

#### `docs/dominio/justificacion-contextos.md`

- L196: **HECHO DEL REPOSITORIO:** Activities participa en relaciones con Focus,
- L199: #### Focus → Activities
- L209: Focus --> Activities
- L430: **HECHO DEL REPOSITORIO:** Focus participa en relaciones con Activities y
- L433: #### Focus → Activities
- L440: Focus --> Activities
- L1026: Activities, Focus y el contexto de usuario/sesión.
- L1155: Activities completadas, sesiones 'FOCUS' completadas y racha.
- L1177: por Activities y Focus.
- L1222: Activities y Focus producen los hechos fuente.
- L1248: necesariamente los ciclos de vida internos de Activities y Focus, siempre que se
- L1345: Activities, Focus y racha.
- L1357: Activities, Focus y racha
- L1657: Activities y Focus.
- L1669: → Activities / Focus
- L1901: Focus --> Activities
- L1923: \| 'Focus → Activities' \| Referencia opcional y validación mediante 'ActivityLookup' \|
- L2080: Activities, Focus y racha.
- L2142: Focus --> Activities
- L2192: → Activities, Focus, Reminders y Progress se mantienen
- L2227: Focus --> Activities
- L2254: propias y uso de información relacionada con Activities, Focus y racha.

#### `docs/dominio/responsabilidades-contextos.md`

- L30: \| Progress \| Interpretar y agregar los resultados producidos por otras capacidades del sistema para medir y representar el avance del usuario mediante rachas y métricas de progreso. \| Interpretación de progreso, racha actual, mejor racha y métricas agregadas. \| Hechos producidos por Activities y Focus. \| 'Activity', 'PomodoroSession', 'Reminder', I […]
- L209: Sin embargo, esta evidencia no permite concluir todavía que Achievement constituya un bounded context independiente ni que deba pertenecer a Activities, Focus o Progress.

#### `docs/dominio/subdominios.md`

- L361: Progress no produce originalmente los hechos de completitud de Activities o Focus. Los consume y los interpreta para construir significado adicional relacionado con el avance del usuario.
- L430: Estas dependencias no son suficientes para concluir que Achievement pertenezca a Activities, Focus o Progress.

#### `docs/integracion/aplicabilidad-cqrs-event-sourcing.md`

- L169: Activities y Focus contienen operaciones que modifican el estado operacional.
- L201: Progress consume información proveniente de Activities y Focus.
- L222: operaciones que modifican el estado de Activities y Focus.
- L419: Activities y Focus realizan operaciones como:
- L444: > escritura de Activities y Focus, por lo que debe evaluarse si el crecimiento
- L471: Activities y Focus.
- L590: Activities y Focus.
- L675: Activities o Focus.

#### `docs/integracion/cobertura-interacciones-sync-async.md`

- L22: \| Focus → Activities \| 'ActivityLookup' como contrato síncrono observado \| Comparación explícita en 'docs/integracion/sincrono-vs-asincrono.md' \| EVALUADA \|
- L23: \| Reminders → Activities \| 'ActivityLookup' como contrato síncrono observado \| No se identificó una comparación completa independiente equivalente a la de Focus; comparte semántica contractual con Focus \| COBERTURA PARCIAL / SIN CAMBIO DECIDIDO \|

#### `docs/integracion/contrato-api.md`

- L1: # Contrato de API intermodular de Activities para Focus y Reminders
- L53: bounded context Activities ofrece a Focus una capacidad de validación de
- L77: Focus → Activities
- L124: Focus consume la capacidad ofrecida por Activities cuando necesita crear una
- L444: Activities no transfiere ownership de Activity a Focus.
- L464: Focus no debe reproducir por su cuenta la lógica interna de Activities para
- L583: - exigir a Focus conocer detalles internos de Activities.
- L651: Mientras Activities y Focus:
- L684: - una separación futura de Activities y Focus en procesos independientes.
- L747: Focus → Activities
- L760: Focus consume una capacidad de Activities sin adquirir ownership sobre sus
- L864: requieren evolución coordinada de Activities y Focus

#### `docs/integracion/eventos-candidatos.md`

- L139: Focus → Activities
- L634: Focus utiliza actualmente una capacidad síncrona de Activities para validar una
- L1430: Progress agrega información proveniente de Activities y Focus.
- L1975: # 13. Relación con Focus → Activities
- L1981: Focus → Activities

#### `docs/integracion/sincrono-vs-asincrono.md`

- L9: Focus → Activities
- L53: Focus → Activities
- L272: Focus → Activities
- L281: sobre la interacción Focus → Activities
- L556: Dado que Focus y Activities pertenecen al mismo backend y al mismo proceso,
- L678: 'Focus → Activities' como:
- L924: interacción 'Focus → Activities'.
- L1151: Focus → Activities síncrono
- L1153: Focus → Activities asíncrono
- L1199: Focus → Activities
- L1216: sync o async en Focus → Activities
- L1229: → Focus y Activities forman parte del mismo monolito modular.
- L1375: - mecanismos actuales de observabilidad de 'Focus → Activities'.

#### `docs/m5/auditoria-externa-post-freeze.md`

- L36: Sin embargo, el título seguía expresando únicamente Activities → Focus.

#### `docs/m5/auditoria-semantica-semanas9-10.md`

- L86: L30: \| Progress \| Interpretar y agregar los resultados producidos por otras capacidades del sistema para medir y representar el avance del usuario mediante rachas y métricas de progreso. \| Interpretación de progreso, racha actual, mejor racha y métricas agregadas. \| Hechos producidos por Activities y Focus. \| 'Activity', 'PomodoroSession', 'Reminde […]
- L99: L209: Sin embargo, esta evidencia no permite concluir todavía que Achievement constituya un bounded context independiente ni que deba pertenecer a Activities, Focus o Progress.
- L152: L23: Focus --> Activities : consulta y valida referencia opcional a Activity\ncuando existe activityId
- L165: L1: # Contrato de API intermodular de Activities para Focus y Reminders
- L171: L25: bounded context Activities ofrece a Focus una capacidad de validación de
- L172: L49: Focus → Activities
- L176: L96: Focus consume la capacidad ofrecida por Activities cuando necesita crear una
- L202: L416: Activities no transfiere ownership de Activity a Focus.
- L206: L436: Focus no debe reproducir por su cuenta la lógica interna de Activities para
- L214: L555: - exigir a Focus conocer detalles internos de Activities.
- L243: L509: Focus consulta actualmente información de Activities mediante el contrato:
- L248: L530: Focus ──> ActivityLookup ──> Activities
- L272: L15: - cuando existe 'activityId', Focus consulta Activities mediante 'ActivityLookup'
- L282: L140: Focus ──> ActivityLookup ──> Activities
- L302: L285:   Activities no define por completo la existencia de Focus
- L326: L51: forman una capacidad funcional coherente y distinguible de Activities, Focus,
- L382: L139: Focus → Activities
- L403: L634: Focus utiliza actualmente una capacidad síncrona de Activities para validar una
- L415: L1430: Progress agrega información proveniente de Activities y Focus.
- L427: L1975: # 13. Relación con Focus → Activities
- L428: L1981: Focus → Activities
- L1016: - docs/dominio/evidencia/activities.md: ActivityLookup, Focus, Reminders
- L1138: L1248: necesariamente los ciclos de vida internos de Activities y Focus, siempre que se
- L1168: L30: \| Progress \| Interpretar y agregar los resultados producidos por otras capacidades del sistema para medir y representar el avance del usuario mediante rachas y métricas de progreso. \| Interpretación de progreso, racha actual, mejor racha y métricas agregadas. \| Hechos producidos por Activities y Focus. \| 'Activity', 'PomodoroSession', 'Reminde […]
- L1171: L209: Sin embargo, esta evidencia no permite concluir todavía que Achievement constituya un bounded context independiente ni que deba pertenecer a Activities, Focus o Progress.

#### `docs/m5/cierre-pista2.md`

- L40: \| 14 \| Focus → Activities \| MECANISMO ACTUAL VALIDADO \|
- L94: ## 7. Focus y Reminders hacia Activities

#### `docs/m5/correcciones-post-freeze.md`

- L22: El título mencionaba únicamente Activities → Focus aunque el cuerpo del
- L30: Contrato de API intermodular de Activities para Focus y Reminders

#### `docs/m5/veredicto-semantico-final.md`

- L37: Las fronteras confirmadas de Activities, Focus, Reminders y Progress están
- L57: - Focus → Activities;

#### `docs/semana8/antes-despues.md`

- L90: - Focus -> Activities mediante 'ActivityLookup';

#### `docs/semana8/c4/README.md`

- L54: - Focus -> Activities mediante 'ActivityLookup'

#### `docs/semana8/c4/c4-l3-backend-modular.md`

- L41: focus -->\|"ActivityLookup"\| activities

#### `docs/semana8/defensa-comite.md`

- L56: - Focus -> Activities con 'ActivityLookup'

#### `docs/semana8/edav.md`

- L70: - Identity, Activities, Focus, Progress y Reminders representan capacidades reconocibles;

#### `docs/semana8/fronteras-modulares.md`

- L78: ### 5.1 Focus hacia Activities
- L192: Focus -> Activities API
- L204: Focus -> Activities Repository

#### `docs/semana8/matriz-trazabilidad.md`

- L119: \| TR-002 \| L3 \| Frontera modular \| Organizar el backend por capacidades de negocio \| 'backend/src/main/kotlin/com/example/rachapro/backend/' \| paquetes 'identity', 'activities', 'focus', 'progress', 'reminders', 'shared' \| 🏗 AS-IS \|
- L121: \| TR-004 \| L3 \| API intermodular \| Permitir a Focus y Reminders consultar Activities sin importar su persistencia \| 'activities/api/ActivityLookup.kt' \| 'ActivityLookupService', 'PomodoroSessionService', 'ReminderService' \| ✅ CONCUERDA \|

#### `docs/semana8/resumen-ejecutivo.md`

- L80: - Focus -> Activities mediante 'ActivityLookup'

#### `docs/semana8/riesgos-tradeoffs-costos.md`

- L90: - Focus -> Activities;

#### `docs/semana9/09-api-eventos-integracion.md`

- L274: - Activities expone 'ActivityLookup', utilizado por Focus.
- L282: \| Focus \| Activities \| 'ActivityLookup' \| síncrono \|

#### `docs/uso-ia/auditoria-eventos.md`

- L105: Focus → Activities
- L498: La relación actual Focus → Activities utiliza una capacidad síncrona de
- L1243: ## 16. Relación con el contrato Focus → Activities
- L1248: Focus → Activities
- L1251: Focus consume una API pública intermodular local proporcionada por Activities.

#### `experimentos/spike-03-caracterizacion-racha/00-preregistro.md`

- L78: 'ProgressViewModel' obtiene los días completados provenientes de Activities y Focus.
- L309: En la implementación actual de Progress, el cálculo de la racha utiliza los días históricos completados provenientes de Activities y Focus.
- L854: ### Distribución entre Activities y Focus
- L868: → Activities + Focus
- L1183: Cambiar el solapamiento entre Activities y Focus puede alterar el trabajo realizado por 'distinct()'.

#### `experimentos/spike-03-caracterizacion-racha/condiciones.md`

- L299: → Activities + Focus

#### `experimentos/spike-03-caracterizacion-racha/registro-delegacion-ia.md`

- L167: - distribución Activities / Focus

### P1-REMINDERS-ACTIVITIES

Coincidencias encontradas: **56**.

#### `docs/adr/0001-decision-estilo.md`

- L483: La implementación resultante conserva una única aplicación Spring Boot y materializa las fronteras Identity, Activities, Focus, Progress y Reminders.

#### `docs/adr/0002-aislamiento-persistencia.md`

- L59: \| 'reminder' \| 'ActivityRepository' \| Cruce Reminders -> Activities \|
- L320: Reminders -> Activities API
- L340: Reminders -> Activities Repository

#### `docs/dominio/context-map.puml`

- L25: Reminders --> Activities : consulta y valida referencia opcional a Activity\nmediante ActivityLookup cuando existe activityId

#### `docs/dominio/evidencia/activities.md`

- L817: 3. mecanismo definitivo de integración entre Activities y Reminders
- L951: Existe una relación arquitectónica conocida entre Reminders y Activities.

#### `docs/dominio/evidencia/focus.md`

- L299: - una responsabilidad protegida diferenciable de Activities, Progress y Reminders

#### `docs/dominio/evidencia/reminders.md`

- L195: → Reminders no consulta Activities
- L532: Reminders ──> ActivityLookup ──> Activities
- L549: Reminders depende siempre de Activities
- L555: Reminders consulta Activities únicamente cuando necesita validar
- L1026: Cuando existe 'activityId', Reminders consulta Activities mediante:

#### `docs/dominio/justificacion-contextos.md`

- L217: #### Reminders → Activities
- L227: Reminders --> Activities
- L731: #### Reminders → Activities
- L745: Reminders --> Activities
- L1905: Reminders --> Activities
- L1924: \| 'Reminders → Activities' \| Referencia opcional y validación mediante 'ActivityLookup' \|
- L2146: Reminders --> Activities
- L2192: → Activities, Focus, Reminders y Progress se mantienen
- L2231: Reminders --> Activities

#### `docs/dominio/responsabilidades-contextos.md`

- L125: La consulta de Activity no transfiere ownership sobre Activities a Reminders.

#### `docs/integracion/cobertura-interacciones-sync-async.md`

- L23: \| Reminders → Activities \| 'ActivityLookup' como contrato síncrono observado \| No se identificó una comparación completa independiente equivalente a la de Focus; comparte semántica contractual con Focus \| COBERTURA PARCIAL / SIN CAMBIO DECIDIDO \|

#### `docs/integracion/contrato-api.md`

- L1: # Contrato de API intermodular de Activities para Focus y Reminders

#### `docs/integracion/eventos-candidatos.md`

- L140: Reminders → Activities

#### `docs/m5/auditoria-semantica-semanas9-10.md`

- L93: L125: La consulta de Activity no transfiere ownership sobre Activities a Reminders.
- L153: L25: Reminders --> Activities : consulta y valida referencia opcional a Activity\nmediante ActivityLookup cuando existe activityId
- L165: L1: # Contrato de API intermodular de Activities para Focus y Reminders
- L305: L299: - una responsabilidad protegida diferenciable de Activities, Progress y Reminders
- L335: L195: → Reminders no consulta Activities
- L358: L532: Reminders ──> ActivityLookup ──> Activities
- L359: L549: Reminders depende siempre de Activities
- L360: L555: Reminders consulta Activities únicamente cuando necesita validar
- L383: L140: Reminders → Activities
- L1016: - docs/dominio/evidencia/activities.md: ActivityLookup, Focus, Reminders

#### `docs/m5/cierre-pista2.md`

- L41: \| 15 \| Reminders → Activities \| MECANISMO ACTUAL VALIDADO \|
- L94: ## 7. Focus y Reminders hacia Activities

#### `docs/m5/correcciones-post-freeze.md`

- L30: Contrato de API intermodular de Activities para Focus y Reminders

#### `docs/m5/veredicto-semantico-final.md`

- L37: Las fronteras confirmadas de Activities, Focus, Reminders y Progress están
- L58: - Reminders → Activities;

#### `docs/semana8/antes-despues.md`

- L91: - Reminders -> Activities mediante 'ActivityLookup'.

#### `docs/semana8/c4/README.md`

- L55: - Reminders -> Activities mediante 'ActivityLookup'

#### `docs/semana8/c4/c4-l3-backend-modular.md`

- L42: reminders -->\|"ActivityLookup"\| activities

#### `docs/semana8/defensa-comite.md`

- L57: - Reminders -> Activities con 'ActivityLookup'

#### `docs/semana8/edav.md`

- L70: - Identity, Activities, Focus, Progress y Reminders representan capacidades reconocibles;

#### `docs/semana8/fronteras-modulares.md`

- L94: ### 5.2 Reminders hacia Activities
- L194: Reminders -> Activities API
- L206: Reminders -> Activities Repository

#### `docs/semana8/matriz-trazabilidad.md`

- L119: \| TR-002 \| L3 \| Frontera modular \| Organizar el backend por capacidades de negocio \| 'backend/src/main/kotlin/com/example/rachapro/backend/' \| paquetes 'identity', 'activities', 'focus', 'progress', 'reminders', 'shared' \| 🏗 AS-IS \|
- L121: \| TR-004 \| L3 \| API intermodular \| Permitir a Focus y Reminders consultar Activities sin importar su persistencia \| 'activities/api/ActivityLookup.kt' \| 'ActivityLookupService', 'PomodoroSessionService', 'ReminderService' \| ✅ CONCUERDA \|

#### `docs/semana8/resumen-ejecutivo.md`

- L81: - Reminders -> Activities mediante 'ActivityLookup'

#### `docs/semana8/riesgos-tradeoffs-costos.md`

- L91: - Reminders -> Activities.

#### `docs/semana9/09-api-eventos-integracion.md`

- L275: - Activities expone 'ActivityLookup', utilizado por Reminders.
- L283: \| Reminders \| Activities \| 'ActivityLookup' \| síncrono \|

#### `docs/uso-ia/auditoria-eventos.md`

- L106: Reminders → Activities

### P1-PROGRESS-ACTIVITIES

Coincidencias encontradas: **58**.

#### `docs/adr/0001-decision-estilo.md`

- L483: La implementación resultante conserva una única aplicación Spring Boot y materializa las fronteras Identity, Activities, Focus, Progress y Reminders.

#### `docs/dominio/context-map.puml`

- L27: Progress --> Activities : consume hechos y agregados de completitud producidos por Activities\n(días y cantidades de Activities completadas)

#### `docs/dominio/evidencia/activities.md`

- L544: Progress consume información producida por Activities para construir métricas
- L930: Activities puede producir información consumida por Achievement o Progress sin
- L944: Progress consume información relacionada con completitud de Activities y la

#### `docs/dominio/evidencia/focus.md`

- L299: - una responsabilidad protegida diferenciable de Activities, Progress y Reminders

#### `docs/dominio/evidencia/progress.md`

- L460: Progress obtiene información de completitud producida por Activities.
- L747: 'AchievementEngine' utiliza información relacionada con Progress, Activities y
- L784: 1. mecanismo definitivo mediante el cual Progress obtiene hechos de Activities
- L804: \| Progress combina información de Activities y Focus \| 'HECHO DEL REPOSITORIO' \|
- L835: - Progress combina hechos provenientes de Activities y Focus
- L955: Progress consume hechos relacionados con Activities completadas.

#### `docs/dominio/evidencia/reminders.md`

- L961: - responsabilidad funcional diferenciable de Activities, Focus, Progress e

#### `docs/dominio/justificacion-contextos.md`

- L235: #### Progress → Activities
- L237: Progress consume información producida por Activities relacionada con
- L258: Progress --> Activities
- L894: Progress no produce originalmente los hechos de completitud de Activities o
- L1028: #### Progress → Activities
- L1030: Progress consume información producida por Activities relacionada con
- L1042: Progress --> Activities
- L1909: Progress --> Activities
- L1925: \| 'Progress → Activities' \| Consumo de hechos y agregados de completitud \|
- L2150: Progress --> Activities
- L2192: → Activities, Focus, Reminders y Progress se mantienen
- L2235: Progress --> Activities

#### `docs/dominio/responsabilidades-contextos.md`

- L27: \| Activities \| Planificar, organizar, descomponer y dar seguimiento a las actividades y tareas del usuario, incluyendo su clasificación y su ciclo de vida. \| 'Activity', 'Category', 'Subtask', ciclo de vida de 'Activity'. \| 'userId' y datos necesarios para asociar las entidades al usuario correspondiente. \| 'PomodoroSession', rachas y métricas de P […]
- L30: \| Progress \| Interpretar y agregar los resultados producidos por otras capacidades del sistema para medir y representar el avance del usuario mediante rachas y métricas de progreso. \| Interpretación de progreso, racha actual, mejor racha y métricas agregadas. \| Hechos producidos por Activities y Focus. \| 'Activity', 'PomodoroSession', 'Reminder', I […]
- L209: Sin embargo, esta evidencia no permite concluir todavía que Achievement constituya un bounded context independiente ni que deba pertenecer a Activities, Focus o Progress.

#### `docs/dominio/subdominios.md`

- L361: Progress no produce originalmente los hechos de completitud de Activities o Focus. Los consume y los interpreta para construir significado adicional relacionado con el avance del usuario.
- L430: Estas dependencias no son suficientes para concluir que Achievement pertenezca a Activities, Focus o Progress.

#### `docs/integracion/aplicabilidad-cqrs-event-sourcing.md`

- L201: Progress consume información proveniente de Activities y Focus.
- L350: Progress → Activities

#### `docs/integracion/cobertura-interacciones-sync-async.md`

- L25: \| Progress → Activities \| Relación de consumo/referencia documentada en el Context Map \| No se identificó en el corpus revisado una comparación sync/async dedicada \| NO EVALUADA ESPECÍFICAMENTE \|

#### `docs/integracion/eventos-candidatos.md`

- L141: Progress → Activities
- L425: Progress consume información derivada de la completitud de Activities para sus
- L480: No se concluye todavía que la relación Activities → Progress deba implementarse
- L1430: Progress agrega información proveniente de Activities y Focus.

#### `docs/m5/auditoria-semantica-semanas9-10.md`

- L86: L30: \| Progress \| Interpretar y agregar los resultados producidos por otras capacidades del sistema para medir y representar el avance del usuario mediante rachas y métricas de progreso. \| Interpretación de progreso, racha actual, mejor racha y métricas agregadas. \| Hechos producidos por Activities y Focus. \| 'Activity', 'PomodoroSession', 'Reminde […]
- L99: L209: Sin embargo, esta evidencia no permite concluir todavía que Achievement constituya un bounded context independiente ni que deba pertenecer a Activities, Focus o Progress.
- L154: L27: Progress --> Activities : consume hechos y agregados de completitud producidos por Activities\n(días y cantidades de Activities completadas)
- L305: L299: - una responsabilidad protegida diferenciable de Activities, Progress y Reminders
- L384: L141: Progress → Activities
- L392: L425: Progress consume información derivada de la completitud de Activities para sus
- L395: L480: No se concluye todavía que la relación Activities → Progress deba implementarse
- L415: L1430: Progress agrega información proveniente de Activities y Focus.
- L1168: L30: \| Progress \| Interpretar y agregar los resultados producidos por otras capacidades del sistema para medir y representar el avance del usuario mediante rachas y métricas de progreso. \| Interpretación de progreso, racha actual, mejor racha y métricas agregadas. \| Hechos producidos por Activities y Focus. \| 'Activity', 'PomodoroSession', 'Reminde […]
- L1171: L209: Sin embargo, esta evidencia no permite concluir todavía que Achievement constituya un bounded context independiente ni que deba pertenecer a Activities, Focus o Progress.

#### `docs/m5/veredicto-semantico-final.md`

- L37: Las fronteras confirmadas de Activities, Focus, Reminders y Progress están
- L59: - Progress → Activities;

#### `docs/semana8/edav.md`

- L70: - Identity, Activities, Focus, Progress y Reminders representan capacidades reconocibles;

#### `docs/semana8/matriz-trazabilidad.md`

- L119: \| TR-002 \| L3 \| Frontera modular \| Organizar el backend por capacidades de negocio \| 'backend/src/main/kotlin/com/example/rachapro/backend/' \| paquetes 'identity', 'activities', 'focus', 'progress', 'reminders', 'shared' \| 🏗 AS-IS \|

#### `docs/semana9/09-api-eventos-integracion.md`

- L211: 'Activities remotas -> snapshot local en Room -> lectura por Progress'
- L215: 'Progress -> Activities API'
- L287: No se identifica actualmente una dependencia backend equivalente entre Progress y Activities. La relación funcional entre actividades completadas y parte del progreso también ocurre en Android/local y no debe representarse como una dependencia backend que no está implementada.

#### `docs/uso-ia/auditoria-eventos.md`

- L107: Progress → Activities

#### `experimentos/spike-03-caracterizacion-racha/00-preregistro.md`

- L78: 'ProgressViewModel' obtiene los días completados provenientes de Activities y Focus.
- L272: 'Progress → Activities'
- L309: En la implementación actual de Progress, el cálculo de la racha utiliza los días históricos completados provenientes de Activities y Focus.

### P1-PROGRESS-FOCUS

Coincidencias encontradas: **71**.

#### `docs/adr/0001-decision-estilo.md`

- L483: La implementación resultante conserva una única aplicación Spring Boot y materializa las fronteras Identity, Activities, Focus, Progress y Reminders.

#### `docs/dominio/context-map.puml`

- L29: Progress --> Focus : consume hechos y agregados de completitud producidos por Focus\n(días, cantidades y duración agregada de sesiones FOCUS)

#### `docs/dominio/evidencia/activities.md`

- L892: - separación semántica frente a Focus, Progress, Reminders e Identity

#### `docs/dominio/evidencia/focus.md`

- L156: Progress utiliza información derivada de sesiones 'FOCUS + COMPLETED',
- L164: Focus produce los datos fuente y Progress les aplica reglas de agregación e
- L205: por Focus coincide con la utilizada posteriormente por Progress para interpretar
- L262: \| Días 'FOCUS + COMPLETED' usados por Progress \| 'HECHO DEL REPOSITORIO' \|
- L351: Progress consume datos producidos por Focus, especialmente información de

#### `docs/dominio/evidencia/progress.md`

- L491: Progress utiliza datos producidos por Focus.
- L653: Focus también produce días utilizados posteriormente por Progress.
- L785: 2. mecanismo definitivo mediante el cual Progress obtiene hechos de Focus
- L804: \| Progress combina información de Activities y Focus \| 'HECHO DEL REPOSITORIO' \|
- L835: - Progress combina hechos provenientes de Activities y Focus
- L973: Progress consume hechos relacionados con Pomodoros Focus completados y duración.

#### `docs/dominio/evidencia/reminders.md`

- L886: 14. existencia de relaciones adicionales con Focus o Progress fuera del código
- L961: - responsabilidad funcional diferenciable de Activities, Focus, Progress e
- L1067: #### Focus y Progress
- L1070: Focus o Progress para controlar el ciclo funcional de Reminder.

#### `docs/dominio/justificacion-contextos.md`

- L333: La posibilidad de que Focus, Reminders o Progress evolucionen internamente sin
- L451: #### Progress → Focus
- L453: Progress consume información producida por Focus relacionada con sesiones
- L466: Progress --> Focus
- L492: → Progress consume hechos de sesiones FOCUS completadas
- L510: Progress consume resultados producidos por Focus y los interpreta como señales
- L1068: #### Progress → Focus
- L1070: Progress consume información producida por Focus relacionada con sesiones
- L1082: Progress --> Focus
- L1148: No modifica el ownership entre Focus y Progress.
- L1604: Focus, Reminders y Progress.
- L1912: Progress --> Focus
- L1926: \| 'Progress → Focus' \| Consumo de hechos y agregados de sesiones 'FOCUS' completadas \|
- L2153: Progress --> Focus
- L2192: → Activities, Focus, Reminders y Progress se mantienen
- L2238: Progress --> Focus

#### `docs/dominio/responsabilidades-contextos.md`

- L28: \| Focus \| Gestionar el ciclo de vida de las sesiones Pomodoro del usuario, incluyendo periodos de concentración y descanso, sus estados y transiciones. \| 'PomodoroSession', tipo de sesión, estado de sesión y transiciones de su ciclo de vida. \| 'userId' y referencia opcional a 'Activity'. \| 'Activity', Progress, 'Reminder', Identity. \|
- L29: \| Reminders \| Definir, programar y gestionar el ciclo de vida de los recordatorios del usuario, conservando la intención de generar un aviso en un momento determinado y permitiendo opcionalmente asociarlo a una Activity. \| 'Reminder', momento programado y ciclo funcional 'SCHEDULED / DELIVERED / CANCELLED'. \| 'userId' y referencia opcional a 'Activ […]
- L30: \| Progress \| Interpretar y agregar los resultados producidos por otras capacidades del sistema para medir y representar el avance del usuario mediante rachas y métricas de progreso. \| Interpretación de progreso, racha actual, mejor racha y métricas agregadas. \| Hechos producidos por Activities y Focus. \| 'Activity', 'PomodoroSession', 'Reminder', I […]
- L209: Sin embargo, esta evidencia no permite concluir todavía que Achievement constituya un bounded context independiente ni que deba pertenecer a Activities, Focus o Progress.

#### `docs/dominio/subdominios.md`

- L361: Progress no produce originalmente los hechos de completitud de Activities o Focus. Los consume y los interpreta para construir significado adicional relacionado con el avance del usuario.
- L430: Estas dependencias no son suficientes para concluir que Achievement pertenezca a Activities, Focus o Progress.

#### `docs/integracion/aplicabilidad-cqrs-event-sourcing.md`

- L201: Progress consume información proveniente de Activities y Focus.
- L351: Progress → Focus

#### `docs/integracion/cobertura-interacciones-sync-async.md`

- L26: \| Progress → Focus \| Relación de consumo/referencia documentada en el Context Map \| No se identificó en el corpus revisado una comparación sync/async dedicada \| NO EVALUADA ESPECÍFICAMENTE \|

#### `docs/integracion/eventos-candidatos.md`

- L142: Progress → Focus
- L995: Esto no demuestra todavía que la interacción Focus → Progress deba implementarse
- L1430: Progress agrega información proveniente de Activities y Focus.
- L2058: Progress consume información agregada de sesiones de Focus.

#### `docs/m5/auditoria-semantica-semanas9-10.md`

- L86: L30: \| Progress \| Interpretar y agregar los resultados producidos por otras capacidades del sistema para medir y representar el avance del usuario mediante rachas y métricas de progreso. \| Interpretación de progreso, racha actual, mejor racha y métricas agregadas. \| Hechos producidos por Activities y Focus. \| 'Activity', 'PomodoroSession', 'Reminde […]
- L99: L209: Sin embargo, esta evidencia no permite concluir todavía que Achievement constituya un bounded context independiente ni que deba pertenecer a Activities, Focus o Progress.
- L155: L29: Progress --> Focus : consume hechos y agregados de completitud producidos por Focus\n(días, cantidades y duración agregada de sesiones FOCUS)
- L284: L156: Progress utiliza información derivada de sesiones 'FOCUS + COMPLETED',
- L286: L164: Focus produce los datos fuente y Progress les aplica reglas de agregación e
- L291: L205: por Focus coincide con la utilizada posteriormente por Progress para interpretar
- L298: L262: \| Días 'FOCUS + COMPLETED' usados por Progress \| 'HECHO DEL REPOSITORIO' \|
- L312: L351: Progress consume datos producidos por Focus, especialmente información de
- L415: L1430: Progress agrega información proveniente de Activities y Focus.
- L433: L2058: Progress consume información agregada de sesiones de Focus.
- L1120: L995: Esto no demuestra todavía que la interacción Focus → Progress deba implementarse
- L1168: L30: \| Progress \| Interpretar y agregar los resultados producidos por otras capacidades del sistema para medir y representar el avance del usuario mediante rachas y métricas de progreso. \| Interpretación de progreso, racha actual, mejor racha y métricas agregadas. \| Hechos producidos por Activities y Focus. \| 'Activity', 'PomodoroSession', 'Reminde […]
- L1171: L209: Sin embargo, esta evidencia no permite concluir todavía que Achievement constituya un bounded context independiente ni que deba pertenecer a Activities, Focus o Progress.

#### `docs/m5/correcciones-auditoria-final.md`

- L114: Focus, Progress y Reminders aparecen como contextos principales del monolito

#### `docs/m5/veredicto-semantico-final.md`

- L37: Las fronteras confirmadas de Activities, Focus, Reminders y Progress están
- L60: - Progress → Focus.

#### `docs/semana8/edav.md`

- L70: - Identity, Activities, Focus, Progress y Reminders representan capacidades reconocibles;

#### `docs/semana8/matriz-trazabilidad.md`

- L119: \| TR-002 \| L3 \| Frontera modular \| Organizar el backend por capacidades de negocio \| 'backend/src/main/kotlin/com/example/rachapro/backend/' \| paquetes 'identity', 'activities', 'focus', 'progress', 'reminders', 'shared' \| 🏗 AS-IS \|

#### `docs/uso-ia/auditoria-eventos.md`

- L108: Progress → Focus
- L558: \| 'PomodoroSessionCompleted' \| A \| 'ACEPTAR' \| Progress consume sesiones 'FOCUS + COMPLETED' \|
- L641: No se concluye todavía que Focus → Progress deba implementarse mediante eventos.

#### `experimentos/spike-03-caracterizacion-racha/00-preregistro.md`

- L78: 'ProgressViewModel' obtiene los días completados provenientes de Activities y Focus.
- L274: 'Progress → Focus'
- L309: En la implementación actual de Progress, el cálculo de la racha utiliza los días históricos completados provenientes de Activities y Focus.

### P1-CQRS

Coincidencias encontradas: **147**.

#### `README.md`

- L71: \| Trabajo posterior / M5 \| Modelado de dominio, Context Map, contrato API, AsyncAPI, auditoría de eventos, CQRS / Event Sourcing y experimentos posteriores. \| 'docs/dominio/', 'docs/integracion/', 'docs/asyncapi/', 'experimentos/spike-02-resiliencia/', 'experimentos/spike-03-caracterizacion-racha/', 'docs/adr/0003-integracion-eventos-internos.md' \|
- L115: - 'docs/integracion/' — contratos, evaluación síncrono/asíncrono, eventos candidatos y análisis de aplicabilidad de CQRS / Event Sourcing.

#### `docs/adr/0003-integracion-eventos-internos.md`

- L357: ## 12. CQRS
- L359: El Spike 1 no demuestra una necesidad de adoptar CQRS.
- L363: La introducción de 'UserRegisteredV1' resuelve un caso específico de integración entre módulos y no constituye por sí sola CQRS.
- L367: **CQRS no se adopta como parte de ADR-003.**
- L487: - adoptar CQRS;
- L520: **CQRS:** NO ADOPTADO
- L959: ### 21.7 Evaluación posterior de CQRS / Event Sourcing
- L961: Posteriormente se documentó el análisis de aplicabilidad de CQRS y Event Sourcing en:
- L964: docs/integracion/aplicabilidad-cqrs-event-sourcing.md
- L971: docs(integration): analizar aplicabilidad de CQRS y Event Sourcing
- L974: El estado documentado para CQRS es:
- L989: CQRS es incorrecto
- L1011: CQRS
- L1017: CQRS
- L1114: - CQRS;
- L1327: \| CQRS continúa sin estar justificado por la evidencia disponible. \| El análisis de aplicabilidad no demuestra todavía un problema que requiera separar modelos de comando y lectura. \| Se mide un problema concreto de lectura/escritura cuya resolución requiera evaluar explícitamente una separación de modelos. \|
- L1345: \| Evaluación CQRS / Event Sourcing \| 'docs/integracion/aplicabilidad-cqrs-event-sourcing.md' \| 'd5f356f' \|
- L1415: Los análisis posteriores del Context Map, AsyncAPI, eventos candidatos y aplicabilidad de CQRS/Event Sourcing tampoco introducen por sí mismos una contradicción con la decisión original.
- L1425: CQRS y Event Sourcing permanecen como alternativas:

#### `docs/integracion/README.md`

- L26: ## CQRS y Event Sourcing
- L28: - 'aplicabilidad-cqrs-event-sourcing.md'

#### `docs/integracion/aplicabilidad-cqrs-event-sourcing.md`

- L1: # Aplicabilidad de CQRS y Event Sourcing
- L3:
- L24: constituye por sí sola una justificación de CQRS.
- L41:
- L46: Este documento analiza CQRS y Event Sourcing como opciones arquitectónicas para
- L163: directamente CQRS ni Event Sourcing.
- L224: Esta diferencia por sí sola no demuestra que CQRS sea necesario.
- L412: ## 4. CQRS
- L564: problema causado por no utilizar CQRS
- L571: CQRS podría permitir construir representaciones de lectura orientadas
- L597: CQRS podría simplificar determinadas lecturas
- L640: Estas características no son obligatorias para CQRS.
- L645: CQRS
- L653: CQRS
- L661: CQRS
- L701: Estos riesgos dependen de la forma concreta en que CQRS fuera implementado.
- L710: sola para justificar CQRS.
- L716: Para que CQRS gane justificación en RachaPro tendría que demostrarse al menos
- L857: ### 4.8 Estado de justificación de CQRS
- L865: Esto no descarta CQRS.
- L874: adicional asociada con CQRS.
- L1234: CQRS obligatorio
- L1400: ### 6.1 CQRS no implica Event Sourcing
- L1403: CQRS
- L1408: CQRS puede separar responsabilidades o representaciones de lectura y escritura
- L1413: ### 6.2 CQRS no implica asincronía
- L1416: CQRS
- L1425: ### 6.3 CQRS no implica bases de datos separadas
- L1428: CQRS
- L1517: \| Aspecto \| CQRS \| Event Sourcing \|
- L1533: Para CQRS:
- L1543: que justifique CQRS
- L1571: ### 9.1 CQRS
- L1634: Si se realiza un experimento relacionado con CQRS o Event Sourcing, antes de
- L1673: ### CQRS
- L1684: introducir CQRS.
- L1704: El análisis no adopta ni descarta definitivamente CQRS o Event Sourcing.
- L1709: CQRS

#### `docs/integracion/cobertura-interacciones-sync-async.md`

- L62: - adoptar CQRS;

#### `docs/m5/auditoria-complementaria/README.md`

- L76: - introducir CQRS;

#### `docs/m5/auditoria-complementaria/inventario-inicial.md`

- L141: - 'docs/integracion/aplicabilidad-cqrs-event-sourcing.md'

#### `docs/m5/auditoria-externa-post-freeze.md`

- L130: - CQRS;

#### `docs/m5/auditoria-final.md`

- L46: \| A13 CQRS \| NO ADOPTADO / NO DETERMINADO \| SPIKE-03 reconocido sin convertirlo en decision automatica. \|

#### `docs/m5/auditoria-semantica-semanas9-10.md`

- L498: L363: La introducción de 'UserRegisteredV1' resuelve un caso específico de integración entre módulos y no constituye por sí sola CQRS.
- L556: L363: La introducción de 'UserRegisteredV1' resuelve un caso específico de integración entre módulos y no constituye por sí sola CQRS.
- L641: L163: Este veredicto corresponde exclusivamente al Spike 1 y no implica por sí mismo la adopción de mensajería externa, microservicios, CQRS ni eventos para todas las relaciones entre módulos.
- L892: ## AS-09 — CQRS / Event Sourcing
- L894: L1: # Aplicabilidad de CQRS y Event Sourcing
- L895: L3:
- L899: L24: constituye por sí sola una justificación de CQRS.
- L904: L41:
- L905: L46: Este documento analiza CQRS y Event Sourcing como opciones arquitectónicas para
- L912: L163: directamente CQRS ni Event Sourcing.
- L913: L224: Esta diferencia por sí sola no demuestra que CQRS sea necesario.
- L915: L412: ## 4. CQRS
- L916: L564: problema causado por no utilizar CQRS
- L917: L571: CQRS podría permitir construir representaciones de lectura orientadas
- L918: L597: CQRS podría simplificar determinadas lecturas
- L920: L640: Estas características no son obligatorias para CQRS.
- L921: L645: CQRS
- L922: L653: CQRS
- L924: L661: CQRS
- L925: L701: Estos riesgos dependen de la forma concreta en que CQRS fuera implementado.
- L926: L710: sola para justificar CQRS.
- L927: L716: Para que CQRS gane justificación en RachaPro tendría que demostrarse al menos
- L928: L857: ### 4.8 Estado de justificación de CQRS
- L930: L865: Esto no descarta CQRS.
- L931: L874: adicional asociada con CQRS.
- L956: L1234: CQRS obligatorio
- L968: L1400: ### 6.1 CQRS no implica Event Sourcing
- L969: L1403: CQRS
- L971: L1408: CQRS puede separar responsabilidades o representaciones de lectura y escritura
- L972: L1413: ### 6.2 CQRS no implica asincronía
- L973: L1416: CQRS
- L974: L1425: ### 6.3 CQRS no implica bases de datos separadas
- L975: L1428: CQRS
- L985: L1517: \| Aspecto \| CQRS \| Event Sourcing \|
- L990: L1533: Para CQRS:
- L991: L1543: que justifique CQRS
- L1025: ### CQRS / ES
- L1026: - docs/integracion/aplicabilidad-cqrs-event-sourcing.md: CQRS, Event Sourcing
- L1027: - docs/adr/0003-integracion-eventos-internos.md: CQRS, Event Sourcing
- L1034: L359: El Spike 1 no demuestra una necesidad de adoptar CQRS.
- L1046: L989: CQRS es incorrecto
- L1056: L1327: \| CQRS continúa sin estar justificado por la evidencia disponible. \| El análisis de aplicabilidad no demuestra todavía un problema que requiera separar modelos de comando y lectura. \| Se mide un problema concreto de lectura/escritura cuya resolución requiera evaluar explícitamente una separación de modelos. \|
- L1081: ### docs/integracion/aplicabilidad-cqrs-event-sourcing.md
- L1084: L224: Esta diferencia por sí sola no demuestra que CQRS sea necesario.
- L1089: L640: Estas características no son obligatorias para CQRS.
- L1098: L1234: CQRS obligatorio
- L1108: L1704: El análisis no adopta ni descarta definitivamente CQRS o Event Sourcing.
- L1207: ### docs/integracion/aplicabilidad-cqrs-event-sourcing.md
- L1219: L71: \| Trabajo posterior / M5 \| Modelado de dominio, Context Map, contrato API, AsyncAPI, auditoría de eventos, CQRS / Event Sourcing y experimentos posteriores. \| 'docs/dominio/', 'docs/integracion/', 'docs/asyncapi/', 'experimentos/spike-02-resiliencia/', 'experimentos/spike-03-caracterizacion-racha/', 'docs/adr/0003-integracion-eventos-internos. […]
- L1228: L115: - 'docs/integracion/' — contratos, evaluación síncrono/asíncrono, eventos candidatos y análisis de aplicabilidad de CQRS / Event Sourcing.
- L1261: - cualquier nueva adopción de CQRS / Event Sourcing / retry / replay / Outbox / broker.

#### `docs/m5/cierre-pista2.md`

- L42: \| 16 \| CQRS \| NO ADOPTADO / DECISIÓN NO DETERMINADA \|
- L102: ## 8. CQRS
- L106: No implica adopción automática de CQRS.
- L168: - CQRS y Event Sourcing sin adopción automática;

#### `docs/m5/correcciones-auditoria-final.md`

- L157: - CQRS;

#### `docs/m5/correcciones-post-freeze.md`

- L83: CQRS y Event Sourcing permanecen no adoptados automáticamente.
- L171: - adopción futura de CQRS;

#### `docs/m5/freeze-final.md`

- L55: - CQRS no fue adoptado automáticamente;

#### `docs/m5/veredicto-semantico-final.md`

- L158: - necesidad de CQRS;
- L198: ## 11. AS-09 — CQRS y Event Sourcing
- L204: Estado de CQRS:
- L238: - estado no adoptado de CQRS / Event Sourcing.
- L329: - que CQRS, Event Sourcing, retry, replay, Outbox o broker estén prohibidos.

#### `docs/semana9/09-api-eventos-integracion.md`

- L641: ## 15. Relación con CQRS y consistencia eventual
- L643: Semana 9 no adopta CQRS.
- L653: La existencia de este evento no demuestra por sí sola que RachaPro necesite CQRS.
- L655: La aplicabilidad real de CQRS y consistencia eventual se evaluará después de observar el resultado del spike.
- L718: Este resultado valida la hipótesis únicamente bajo las condiciones probadas y no implica adoptar CQRS, microservicios ni mensajería externa.

#### `experimentos/spike-01-integracion/README.md`

- L153: El cumplimiento del Spike 1 no demuestra que RachaPro necesite CQRS ni obliga a adoptar una arquitectura orientada a eventos de forma general.
- L163: Este veredicto corresponde exclusivamente al Spike 1 y no implica por sí mismo la adopción de mensajería externa, microservicios, CQRS ni eventos para todas las relaciones entre módulos.
- L235: general, tolerancia universal a fallos, CQRS, mensajería externa ni conveniencia

#### `experimentos/spike-03-caracterizacion-racha/00-preregistro.md`

- L283: - CQRS
- L299: 'CQRS → NO JUSTIFICADO TODAVÍA POR LA EVIDENCIA DISPONIBLE'
- L335: - necesidad de CQRS
- L1108: - adoptar CQRS
- L1408: - que CQRS quede justificado

#### `experimentos/spike-03-caracterizacion-racha/resultados.md`

- L586: - CQRS;
- L618: - no evaluó CQRS;
- L650: - adoptar CQRS;

#### `experimentos/spike-03-caracterizacion-racha/veredicto.md`

- L598: - que deba introducirse CQRS;

### P1-EVENT-SOURCING

Coincidencias encontradas: **125**.

#### `README.md`

- L71: \| Trabajo posterior / M5 \| Modelado de dominio, Context Map, contrato API, AsyncAPI, auditoría de eventos, CQRS / Event Sourcing y experimentos posteriores. \| 'docs/dominio/', 'docs/integracion/', 'docs/asyncapi/', 'experimentos/spike-02-resiliencia/', 'experimentos/spike-03-caracterizacion-racha/', 'docs/adr/0003-integracion-eventos-internos.md' \|
- L115: - 'docs/integracion/' — contratos, evaluación síncrono/asíncrono, eventos candidatos y análisis de aplicabilidad de CQRS / Event Sourcing.

#### `docs/adr/0003-integracion-eventos-internos.md`

- L959: ### 21.7 Evaluación posterior de CQRS / Event Sourcing
- L961: Posteriormente se documentó el análisis de aplicabilidad de CQRS y Event Sourcing en:
- L971: docs(integration): analizar aplicabilidad de CQRS y Event Sourcing
- L980: El estado documentado para Event Sourcing es:
- L995: Event Sourcing es incorrecto
- L1015: Event Sourcing
- L1019: Event Sourcing
- L1115: - Event Sourcing;
- L1328: \| Event Sourcing continúa sin estar justificado por la evidencia disponible. \| No se encontró una necesidad demostrada de replay completo, event store como fuente de verdad o reconstrucción de estado desde eventos. \| Aparece un requisito verificable de reconstrucción histórica, auditoría completa de transiciones o rehidratación desde un event log.  […]
- L1345: \| Evaluación CQRS / Event Sourcing \| 'docs/integracion/aplicabilidad-cqrs-event-sourcing.md' \| 'd5f356f' \|
- L1415: Los análisis posteriores del Context Map, AsyncAPI, eventos candidatos y aplicabilidad de CQRS/Event Sourcing tampoco introducen por sí mismos una contradicción con la decisión original.
- L1425: CQRS y Event Sourcing permanecen como alternativas:

#### `docs/integracion/README.md`

- L26: ## CQRS y Event Sourcing

#### `docs/integracion/aplicabilidad-cqrs-event-sourcing.md`

- L1: # Aplicabilidad de CQRS y Event Sourcing
- L34: - no se ha demostrado Event Sourcing;
- L46: Este documento analiza CQRS y Event Sourcing como opciones arquitectónicas para
- L163: directamente CQRS ni Event Sourcing.
- L407: RachaPro debe adoptar Event Sourcing
- L655: Event Sourcing
- L878: ## 5. Event Sourcing
- L882: Event Sourcing podría resultar relevante si RachaPro necesitara reconstruir el
- L1002: no implica Event Sourcing.
- L1024: necesidad demostrada de Event Sourcing
- L1031: Event Sourcing podría proporcionar determinadas capacidades si RachaPro llegara
- L1076: Event Sourcing podría aportar reconstrucción y trazabilidad completa
- L1085: Introducir Event Sourcing añadiría nuevas responsabilidades de diseño,
- L1155: La magnitud real del coste de introducir Event Sourcing en RachaPro tampoco está
- L1218: Si Event Sourcing se combinara con proyecciones asíncronas, aparecerían además
- L1224: Event Sourcing
- L1232: Event Sourcing
- L1243: Para que Event Sourcing comenzara a ganar justificación en RachaPro tendría que
- L1358: elegir Event Sourcing
- L1365: ### 5.8 Estado de justificación de Event Sourcing
- L1376: Event Sourcing es incorrecto
- L1382: RachaPro nunca debe utilizar Event Sourcing
- L1400: ### 6.1 CQRS no implica Event Sourcing
- L1405: Event Sourcing
- L1438: ### 6.4 Historial no implica Event Sourcing
- L1443: Event Sourcing
- L1451: ### 6.5 Eventos de dominio no implican Event Sourcing
- L1456: Event Sourcing
- L1464: ### 6.6 Integración basada en eventos no implica Event Sourcing
- L1469: Event Sourcing
- L1477: ### 6.7 Eventos candidatos de RachaPro no justifican Event Sourcing
- L1510: RachaPro necesita Event Sourcing
- L1517: \| Aspecto \| CQRS \| Event Sourcing \|
- L1546: Para Event Sourcing:
- L1591: ### 9.2 Event Sourcing
- L1634: Si se realiza un experimento relacionado con CQRS o Event Sourcing, antes de
- L1688: ### Event Sourcing
- L1698: verdad que justifique introducir Event Sourcing.
- L1704: El análisis no adopta ni descarta definitivamente CQRS o Event Sourcing.
- L1714: Event Sourcing

#### `docs/integracion/cobertura-interacciones-sync-async.md`

- L63: - adoptar Event Sourcing.

#### `docs/m5/auditoria-complementaria/README.md`

- L77: - introducir Event Sourcing;

#### `docs/m5/auditoria-externa-post-freeze.md`

- L131: - Event Sourcing;

#### `docs/m5/auditoria-final.md`

- L47: \| A14 Event Sourcing \| NO ADOPTADO \| No se detecta adopcion automatica. \|

#### `docs/m5/auditoria-semantica-semanas9-10.md`

- L892: ## AS-09 — CQRS / Event Sourcing
- L894: L1: # Aplicabilidad de CQRS y Event Sourcing
- L902: L34: - no se ha demostrado Event Sourcing;
- L905: L46: Este documento analiza CQRS y Event Sourcing como opciones arquitectónicas para
- L912: L163: directamente CQRS ni Event Sourcing.
- L914: L407: RachaPro debe adoptar Event Sourcing
- L923: L655: Event Sourcing
- L932: L878: ## 5. Event Sourcing
- L933: L882: Event Sourcing podría resultar relevante si RachaPro necesitara reconstruir el
- L937: L1002: no implica Event Sourcing.
- L938: L1024: necesidad demostrada de Event Sourcing
- L939: L1031: Event Sourcing podría proporcionar determinadas capacidades si RachaPro llegara
- L942: L1076: Event Sourcing podría aportar reconstrucción y trazabilidad completa
- L943: L1085: Introducir Event Sourcing añadiría nuevas responsabilidades de diseño,
- L948: L1155: La magnitud real del coste de introducir Event Sourcing en RachaPro tampoco está
- L953: L1218: Si Event Sourcing se combinara con proyecciones asíncronas, aparecerían además
- L954: L1224: Event Sourcing
- L955: L1232: Event Sourcing
- L957: L1243: Para que Event Sourcing comenzara a ganar justificación en RachaPro tendría que
- L961: L1358: elegir Event Sourcing
- L962: L1365: ### 5.8 Estado de justificación de Event Sourcing
- L964: L1376: Event Sourcing es incorrecto
- L965: L1382: RachaPro nunca debe utilizar Event Sourcing
- L968: L1400: ### 6.1 CQRS no implica Event Sourcing
- L970: L1405: Event Sourcing
- L976: L1438: ### 6.4 Historial no implica Event Sourcing
- L977: L1443: Event Sourcing
- L978: L1451: ### 6.5 Eventos de dominio no implican Event Sourcing
- L979: L1456: Event Sourcing
- L980: L1464: ### 6.6 Integración basada en eventos no implica Event Sourcing
- L981: L1469: Event Sourcing
- L982: L1477: ### 6.7 Eventos candidatos de RachaPro no justifican Event Sourcing
- L984: L1510: RachaPro necesita Event Sourcing
- L985: L1517: \| Aspecto \| CQRS \| Event Sourcing \|
- L992: L1546: Para Event Sourcing:
- L1026: - docs/integracion/aplicabilidad-cqrs-event-sourcing.md: CQRS, Event Sourcing
- L1027: - docs/adr/0003-integracion-eventos-internos.md: CQRS, Event Sourcing
- L1047: L995: Event Sourcing es incorrecto
- L1101: L1376: Event Sourcing es incorrecto
- L1102: L1382: RachaPro nunca debe utilizar Event Sourcing
- L1108: L1704: El análisis no adopta ni descarta definitivamente CQRS o Event Sourcing.
- L1219: L71: \| Trabajo posterior / M5 \| Modelado de dominio, Context Map, contrato API, AsyncAPI, auditoría de eventos, CQRS / Event Sourcing y experimentos posteriores. \| 'docs/dominio/', 'docs/integracion/', 'docs/asyncapi/', 'experimentos/spike-02-resiliencia/', 'experimentos/spike-03-caracterizacion-racha/', 'docs/adr/0003-integracion-eventos-internos. […]
- L1228: L115: - 'docs/integracion/' — contratos, evaluación síncrono/asíncrono, eventos candidatos y análisis de aplicabilidad de CQRS / Event Sourcing.
- L1261: - cualquier nueva adopción de CQRS / Event Sourcing / retry / replay / Outbox / broker.

#### `docs/m5/cierre-pista2.md`

- L43: \| 17 \| Event Sourcing \| NO ADOPTADO \|
- L110: ## 9. Event Sourcing
- L112: No se encontró evidencia suficiente para adoptar Event Sourcing.
- L168: - CQRS y Event Sourcing sin adopción automática;

#### `docs/m5/correcciones-auditoria-final.md`

- L158: - Event Sourcing;

#### `docs/m5/correcciones-post-freeze.md`

- L83: CQRS y Event Sourcing permanecen no adoptados automáticamente.
- L172: - Event Sourcing;

#### `docs/m5/freeze-final.md`

- L56: - Event Sourcing no fue adoptado automáticamente;

#### `docs/m5/veredicto-semantico-final.md`

- L198: ## 11. AS-09 — CQRS y Event Sourcing
- L210: Event Sourcing.
- L212: Estado de Event Sourcing:
- L238: - estado no adoptado de CQRS / Event Sourcing.
- L329: - que CQRS, Event Sourcing, retry, replay, Outbox o broker estén prohibidos.

#### `experimentos/spike-03-caracterizacion-racha/00-preregistro.md`

- L284: - Event Sourcing
- L301: 'Event Sourcing → NO JUSTIFICADO TODAVÍA POR LA EVIDENCIA DISPONIBLE'
- L336: - necesidad de Event Sourcing
- L1109: - adoptar Event Sourcing
- L1409: - que Event Sourcing quede justificado

#### `experimentos/spike-03-caracterizacion-racha/resultados.md`

- L587: - Event Sourcing.
- L619: - no evaluó Event Sourcing;
- L651: - adoptar Event Sourcing;

#### `experimentos/spike-03-caracterizacion-racha/veredicto.md`

- L599: - que deba introducirse Event Sourcing;

### P1-RESILIENCIA

Coincidencias encontradas: **143**.

#### `docs/adr/0003-integracion-eventos-internos.md`

- L146: - no requiere broker externo;
- L163: ### 4.4 Broker externo
- L165: Otra alternativa sería utilizar infraestructura como Kafka, RabbitMQ, Pub/Sub u otro broker.
- L170: - operación de un broker;
- L412: El mecanismo actual utiliza eventos internos de Spring y no un broker durable.
- L473: La existencia futura de alguno de estos requisitos no implica automáticamente adoptar un broker o microservicios.
- L518: **Broker externo:** NO IMPLEMENTADO
- L855: no se observó retry automático
- L863: no se observó replay de UserRegisteredV1
- L873: retry automático observado en esa ventana
- L876: replay observado en esa ventana
- L883: retry nunca puede ocurrir
- L889: replay nunca puede existir
- L1142: - reconstrucción histórica mediante replay;
- L1227: SPIKE-02 no observó retry ni replay dentro de
- L1255: la ausencia de un broker externo reduce complejidad
- L1300: de utilizar un broker externo para este flujo.
- L1306: no hacen necesaria una estrategia de replay.
- L1323: \| No se requiere retry garantizado actualmente. \| SPIKE-02 no observó retry automático en la ventana de 5 segundos evaluada; no se encontró en las fuentes revisadas un requisito explícito de retry garantizado para este flujo. \| Aparecen fallos del consumidor cuyo impacto sea incompatible con un requisito vigente y cuya recuperación requiera reinten […]
- L1324: \| No se requiere replay garantizado actualmente. \| SPIKE-02 no observó replay durante los 5 segundos posteriores al reinicio evaluado; no se encontró en las fuentes revisadas un requisito explícito de replay para este flujo. \| Se establece un requisito de recuperación posterior a reinicio o reconstrucción de efectos secundarios perdidos. \|
- L1325: \| No se requiere broker externo para este flujo. \| El sistema mantiene la interacción dentro de una única aplicación Spring Boot y el mecanismo interno cumple el alcance funcional actualmente documentado. \| Identity y Activities requieren procesos o despliegues independientes, entrega durable, múltiples consumidores independientes o garantías de me […]
- L1328: \| Event Sourcing continúa sin estar justificado por la evidencia disponible. \| No se encontró una necesidad demostrada de replay completo, event store como fuente de verdad o reconstrucción de estado desde eventos. \| Aparece un requisito verificable de reconstrucción histórica, auditoría completa de transiciones o rehidratación desde un event log.  […]
- L1358: La evidencia de SPIKE-02 demuestra que, bajo un fallo intencional del consumidor posterior al commit, el aprovisionamiento de categorías puede quedar incompleto y que no se observó retry automático durante los 5 segundos posteriores a retirar el fallo ni replay durante los 5 segundos posteriores al reinicio del backend.
- L1391: En las fuentes revisadas no se encontró un requisito explícito que establezca que, después de un fallo post-commit del consumidor, 'UserRegisteredV1' deba recuperarse automáticamente mediante retry o replay, ni un umbral temporal específico para dicha recuperación.
- L1410: → necesidad demostrada de broker externo
- L1411: → necesidad demostrada de retry o replay garantizados
- L1481: - necesidad verificable de retry o replay garantizados;

#### `docs/asyncapi/rachapro-events-v1.yaml`

- L8: este documento no implica la existencia de Kafka, RabbitMQ ni otro broker externo.
- L19: dentro del mismo backend y no posee una dirección de broker.
- L94: En las condiciones del Spike 2 no se observó retry automático
- L95: después del fallo ni replay después del reinicio del backend.

#### `docs/integracion/aplicabilidad-cqrs-event-sourcing.md`

- L895: recalcular una proyección mediante replay
- L972: reconstruir PomodoroSession mediante replay
- L989: replay
- L1046: recalcular vistas derivadas mediante replay
- L1064: replay funcional
- L1099: estrategia de replay
- L1100: manejo de errores durante replay
- L1138: como para que el replay completo produjera un problema medible, podrían
- L1191: de replay.
- L1198: lógica de replay incorrecta
- L1210: replay costoso
- L1257: existe una necesidad real de regenerar información mediante replay
- L1310: qué entidades requieren replay
- L1388: reconstrucción mediante replay
- L1522: \| Principal evidencia faltante \| Medición directa del coste de las consultas de Progress y su impacto sobre el sistema \| Requisito funcional de replay, reconstrucción completa o event log como fuente de verdad \|
- L1523: \| Complejidad adicional principal \| Modelos separados, actualización y consistencia entre representaciones \| Event store, replay, versionado y reconstrucción de agregados \|
- L1561: de reconstrucción mediante replay
- L1598: replay funcional obligatorio
- L1696: Sin embargo, todavía no se ha demostrado una necesidad funcional de replay,

#### `docs/integracion/cobertura-interacciones-sync-async.md`

- L60: - adoptar broker externo;
- L61: - adoptar retry, replay u Outbox;

#### `docs/m5/auditoria-complementaria/README.md`

- L78: - introducir broker;
- L79: - introducir retry;
- L80: - introducir replay;
- L81: - introducir Outbox;

#### `docs/m5/auditoria-externa-post-freeze.md`

- L132: - retry;
- L133: - replay;
- L134: - Outbox;
- L135: - broker externo;

#### `docs/m5/auditoria-semantica-semanas9-10.md`

- L518: L863: no se observó replay de UserRegisteredV1
- L641: L163: Este veredicto corresponde exclusivamente al Spike 1 y no implica por sí mismo la adopción de mensajería externa, microservicios, CQRS ni eventos para todas las relaciones entre módulos.
- L656: L7: El objetivo específico es observar si la implementación actual ofrece recuperación automática mediante retry o replay del evento.
- L657: L42: Si el consumidor asíncrono falla después del commit del usuario y el evento no cuenta con persistencia durable ni mecanismo explícito de retry/replay, el usuario puede permanecer persistido mientras el efecto secundario esperado en Activities queda incompleto.
- L668: L89: - retry automático observado: no.
- L672: L101: - replay del evento observado: no.
- L678: L112: \| Retry automático observado \| No \|
- L681: L115: \| Replay después del reinicio observado \| No \|
- L684: L121: No se observó retry automático durante los 5 segundos posteriores a retirar el fallo y tampoco se observó replay de 'UserRegisteredV1' durante los 5 segundos posteriores al reinicio del backend.
- L688: L139: - políticas explícitas de retry;
- L691: L150: Si el efecto asociado al evento se vuelve crítico, deberá evaluarse un mecanismo durable como transactional outbox, mensajería persistente o una estrategia explícita de retry/reprocesamiento.
- L694: L162: **RESULTADO EXPERIMENTAL:** no se observó retry ni replay automático en las ventanas probadas.
- L934: L895: recalcular una proyección mediante replay
- L935: L972: reconstruir PomodoroSession mediante replay
- L936: L989: replay
- L940: L1046: recalcular vistas derivadas mediante replay
- L941: L1064: replay funcional
- L945: L1099: estrategia de replay
- L946: L1100: manejo de errores durante replay
- L947: L1138: como para que el replay completo produjera un problema medible, podrían
- L949: L1191: de replay.
- L950: L1198: lógica de replay incorrecta
- L951: L1210: replay costoso
- L958: L1257: existe una necesidad real de regenerar información mediante replay
- L959: L1310: qué entidades requieren replay
- L966: L1388: reconstrucción mediante replay
- L986: L1522: \| Principal evidencia faltante \| Medición directa del coste de las consultas de Progress y su impacto sobre el sistema \| Requisito funcional de replay, reconstrucción completa o event log como fuente de verdad \|
- L987: L1523: \| Complejidad adicional principal \| Modelos separados, actualización y consistencia entre representaciones \| Event store, replay, versionado y reconstrucción de agregados \|
- L993: L1561: de reconstrucción mediante replay
- L1043: L883: retry nunca puede ocurrir
- L1044: L889: replay nunca puede existir
- L1054: L1323: \| No se requiere retry garantizado actualmente. \| SPIKE-02 no observó retry automático en la ventana de 5 segundos evaluada; no se encontró en las fuentes revisadas un requisito explícito de retry garantizado para este flujo. \| Aparecen fallos del consumidor cuyo impacto sea incompatible con un requisito vigente y cuya recuperación requiera  […]
- L1055: L1324: \| No se requiere replay garantizado actualmente. \| SPIKE-02 no observó replay durante los 5 segundos posteriores al reinicio evaluado; no se encontró en las fuentes revisadas un requisito explícito de replay para este flujo. \| Se establece un requisito de recuperación posterior a reinicio o reconstrucción de efectos secundarios perdidos. \|
- L1058: L1358: La evidencia de SPIKE-02 demuestra que, bajo un fallo intencional del consumidor posterior al commit, el aprovisionamiento de categorías puede quedar incompleto y que no se observó retry automático durante los 5 segundos posteriores a retirar el fallo ni replay durante los 5 segundos posteriores al reinicio del backend.
- L1065: L1411: → necesidad demostrada de retry o replay garantizados
- L1071: L1481: - necesidad verificable de retry o replay garantizados;
- L1106: L1598: replay funcional obligatorio
- L1261: - cualquier nueva adopción de CQRS / Event Sourcing / retry / replay / Outbox / broker.

#### `docs/m5/cierre-pista2.md`

- L45: \| 19 \| Retry \| NO JUSTIFICADO ACTUALMENTE \|
- L46: \| 20 \| Replay \| NO JUSTIFICADO ACTUALMENTE \|
- L47: \| 21 \| Outbox / entrega durable \| NO JUSTIFICADO ACTUALMENTE \|
- L48: \| 22 \| Broker externo \| NO JUSTIFICADO ACTUALMENTE \|
- L100: No implica automáticamente una obligación de migrar a eventos, REST, broker u otro mecanismo.
- L126: ## 11. Retry
- L128: En las fuentes revisadas no se encontró un requisito explícito que obligue a incorporar retry automático al flujo estudiado.
- L132: ## 12. Replay
- L134: En las fuentes revisadas no se encontró un requisito explícito que obligue a incorporar replay.
- L138: ## 13. Outbox y entrega durable
- L140: En las fuentes revisadas no se encontró un requisito explícito que obligue a introducir Outbox o entrega durable.
- L144: ## 14. Broker externo
- L146: La evidencia revisada no demuestra necesidad actual de introducir un broker externo.

#### `docs/m5/correcciones-auditoria-final.md`

- L159: - retry;
- L160: - replay;
- L161: - Outbox;
- L162: - broker externo.

#### `docs/m5/correcciones-post-freeze.md`

- L85: Retry, replay, Outbox y broker externo continúan sin requisito explícito
- L173: - retry;
- L174: - replay;
- L175: - Outbox;
- L176: - broker externo.

#### `docs/m5/veredicto-semantico-final.md`

- L159: - necesidad de broker externo;
- L168: - no se observó retry automático durante los 5 segundos posteriores a retirar
- L170: - no se observó replay durante los 5 segundos posteriores al reinicio.
- L172: El experimento no demuestra que retry o replay sean imposibles bajo cualquier
- L209: reconstrucción completa del estado o replay funcional obligatorio que justifique
- L250: - 'No se requiere retry garantizado actualmente';
- L251: - 'No se requiere replay garantizado actualmente'.
- L329: - que CQRS, Event Sourcing, retry, replay, Outbox o broker estén prohibidos.

#### `docs/semana9/09-api-eventos-integracion.md`

- L718: Este resultado valida la hipótesis únicamente bajo las condiciones probadas y no implica adoptar CQRS, microservicios ni mensajería externa.

#### `experimentos/spike-01-integracion/README.md`

- L143: No se utilizaron Kafka, RabbitMQ, Pub/Sub ni otro broker externo.
- L163: Este veredicto corresponde exclusivamente al Spike 1 y no implica por sí mismo la adopción de mensajería externa, microservicios, CQRS ni eventos para todas las relaciones entre módulos.
- L235: general, tolerancia universal a fallos, CQRS, mensajería externa ni conveniencia

#### `experimentos/spike-02-resiliencia/README.md`

- L7: El objetivo específico es observar si la implementación actual ofrece recuperación automática mediante retry o replay del evento.
- L42: Si el consumidor asíncrono falla después del commit del usuario y el evento no cuenta con persistencia durable ni mecanismo explícito de retry/replay, el usuario puede permanecer persistido mientras el efecto secundario esperado en Activities queda incompleto.
- L89: - retry automático observado: no.
- L101: - replay del evento observado: no.
- L112: \| Retry automático observado \| No \|
- L115: \| Replay después del reinicio observado \| No \|
- L121: No se observó retry automático durante los 5 segundos posteriores a retirar el fallo y tampoco se observó replay de 'UserRegisteredV1' durante los 5 segundos posteriores al reinicio del backend.
- L136: - transactional outbox;
- L139: - políticas explícitas de retry;
- L150: Si el efecto asociado al evento se vuelve crítico, deberá evaluarse un mecanismo durable como transactional outbox, mensajería persistente o una estrategia explícita de retry/reprocesamiento.
- L162: **RESULTADO EXPERIMENTAL:** no se observó retry ni replay automático en las ventanas probadas.

### P1-SPIKE01

Coincidencias encontradas: **188**.

#### `README.md`

- L70: \| Semana 9 \| Evaluación de integración mediante contratos síncronos y evento interno; preregistro y ejecución de SPIKE-01. \| 'docs/semana9/', 'experimentos/spike-01-integracion/' \|
- L93: - 'experimentos/spike-01-integracion/'
- L117: - 'experimentos/spike-01-integracion/' — evidencia experimental de la integración mediante evento interno.
- L156: > - 'experimentos/spike-01-integracion/' — evaluación de la integración interna mediante 'UserRegisteredV1';
- L339: - referencia explícita al preregistro histórico de SPIKE-01;

#### `docs/adr/0003-integracion-eventos-internos.md`

- L7: - **Evidencia principal:** 'experimentos/spike-01-integracion/'
- L8: - **Implementación:** Materializada mediante Spike 1
- L103: Esta alternativa continúa siendo válida técnicamente, pero el Spike 1 evaluó si el acoplamiento podía reducirse sin afectar el comportamiento requerido.
- L123: Esta alternativa no corresponde al mecanismo probado por el Spike 1.
- L159: Esta es la alternativa evaluada mediante el Spike 1.
- L184: experimentos/spike-01-integracion/README.md
- L185: experimentos/spike-01-integracion/resultados.csv
- L187: El Spike 1 fue ejecutado el 2026-09-30.
- L199: ## 6. Resultados del Spike 1
- L216: 28, 65, 36, 32 ms
- L220: - mínimo: 28 ms;
- L222: - mediana: 34 ms;
- L223: - máximo: 65 ms;
- L224: - cumplimiento del umbral: 4/4.
- L347: El Spike 1 observó una ventana de entre 28 y 65 ms bajo las condiciones locales probadas.
- L359: El Spike 1 no demuestra una necesidad de adoptar CQRS.
- L408: El Spike 1 no ejecutó una prueba específica de fallo intencional del consumidor.
- L441: El Spike 1 fue ejecutado localmente.
- L500: **Spike 1:** VALIDADO
- L522: La decisión queda respaldada por la evidencia del Spike 1 y limitada expresamente a las condiciones y alcance documentados.
- L535: c6ae059
- L554: SPIKE-01
- L575: La evidencia experimental utilizada originalmente para ADR-003 corresponde a SPIKE-01.
- L580: 597e9c7
- L587: experimentos/spike-01-integracion/README.md
- L588: experimentos/spike-01-integracion/resultados.csv
- L591: SPIKE-01 fue registrado antes de ADR-003.
- L596: 597e9c7
- L597: SPIKE-01
- L599: c6ae059
- L603: SPIKE-01 evaluó si el registro podía desacoplar la dependencia directa de Identity hacia Activities mediante:
- L622: SPIKE-01
- L660: c6ae059
- L670: c6ae059
- L676: git merge-base --is-ancestor 8d23268 c6ae059
- L683: git merge-base --is-ancestor c6ae059 8d23268
- L694: experimentos/spike-01-integracion/
- L1097: c6ae059
- L1173: SPIKE-01 fue versionado en 597e9c7.
- L1178: ADR-003 fue registrado posteriormente en c6ae059.
- L1321: \| La consistencia eventual continúa siendo aceptable para el aprovisionamiento posterior al registro. \| SPIKE-01 cumplió el criterio experimental definido; ADR-003 documentó la ventana de consistencia eventual. \| Se observan categorías faltantes, tiempos de convergencia incompatibles con el flujo esperado o un requisito funcional exige disponibilid […]
- L1336: \| Evidencia experimental original de ADR-003 \| 'experimentos/spike-01-integracion/README.md' \| '597e9c7' \|
- L1337: \| Resultados de SPIKE-01 \| 'experimentos/spike-01-integracion/resultados.csv' \| '597e9c7' \|
- L1338: \| Decisión original ADR-003 \| 'docs/adr/0003-integracion-eventos-internos.md' \| 'c6ae059' \|
- L1341: \| Relación temporal Context Map / ADR-003 \| historial Git de 'context-map.puml' \| '8d23268' es ancestro de 'c6ae059'; '4b0acd7' y '64aeb42' son posteriores \|
- L1356: La revisión posterior distingue entre la validez histórica de la decisión tomada en 'c6ae059' y la nueva evidencia incorporada después de esa decisión.
- L1393: El criterio experimental de '2000 ms' corresponde al flujo normal evaluado por SPIKE-01 y no fue definido como criterio de recuperación ante fallos.

#### `docs/integracion/README.md`

- L42: - '../../experimentos/spike-01-integracion/'

#### `docs/integracion/cobertura-interacciones-sync-async.md`

- L24: \| Identity → Activities \| 'UserRegisteredV1' como evento interno; Activities consume después del commit mediante listener \| Evaluada mediante SPIKE-01 y decidida específicamente por ADR-003 \| EVALUADA PARA ESE FLUJO \|

#### `docs/m5/auditoria-complementaria/inventario-inicial.md`

- L120: - 'experimentos/spike-01-integracion/README.md'
- L121: - 'experimentos/spike-01-integracion/resultados.csv'

#### `docs/m5/auditoria-final.md`

- L35: \| A02 Navegacion README raiz \| BIEN \| Semana 9, M5, ADR-003 y SPIKE-01/02/03 son navegables. \|
- L43: \| A10 Temporalidad SPIKE-01 / ADR-003 \| BIEN \| Preregistro -> resultados -> ADR-003 mantiene orden temporal. \|
- L48: \| A15 Evidencia experimental \| BIEN \| SPIKE-01, SPIKE-02 y SPIKE-03 contienen artefactos. \|

#### `docs/m5/auditoria-semantica-semanas9-10.md`

- L505: L603: SPIKE-01 evaluó si el registro podía desacoplar la dependencia directa de Identity hacia Activities mediante:
- L534: L7: - **Evidencia principal:** 'experimentos/spike-01-integracion/'
- L544: L184:     experimentos/spike-01-integracion/README.md
- L545: L185:     experimentos/spike-01-integracion/resultados.csv
- L569: L522: La decisión queda respaldada por la evidencia del Spike 1 y limitada expresamente a las condiciones y alcance documentados.
- L572: L535: c6ae059
- L579: L554: SPIKE-01
- L582: L575: La evidencia experimental utilizada originalmente para ADR-003 corresponde a SPIKE-01.
- L584: L580: 597e9c7
- L585: L587: experimentos/spike-01-integracion/README.md
- L586: L588: experimentos/spike-01-integracion/resultados.csv
- L587: L591: SPIKE-01 fue registrado antes de ADR-003.
- L588: L596: 597e9c7
- L589: L597: SPIKE-01
- L590: L599: c6ae059
- L591: L603: SPIKE-01 evaluó si el registro podía desacoplar la dependencia directa de Identity hacia Activities mediante:
- L593: L622: SPIKE-01
- L597: L660: c6ae059
- L598: L670: c6ae059
- L599: L676: git merge-base --is-ancestor 8d23268 c6ae059
- L600: L683: git merge-base --is-ancestor c6ae059 8d23268
- L604: L694: experimentos/spike-01-integracion/
- L615: ## AS-06 — SPIKE-01
- L617: ### experimentos/spike-01-integracion/README.md
- L625: L58: '28, 65, 36, 32 ms'
- L626: L61: - Mínimo: 28 ms
- L627: L64: - Máximo: 65 ms
- L628: L65: - Corridas dentro del umbral de 2000 ms: 4/4
- L629: L69: '65, 36, 32 ms'
- L630: L72: - Mediana: 36 ms
- L631: L73: - Máximo: 65 ms
- L641: L163: Este veredicto corresponde exclusivamente al Spike 1 y no implica por sí mismo la adopción de mensajería externa, microservicios, CQRS ni eventos para todas las relaciones entre módulos.
- L643: ### experimentos/spike-01-integracion/resultados.csv
- L1007: c6ae059 \| 2026-09-30T17:41:34-05:00 \| docs(semana10): registra ADR-003 de integracion por eventos
- L1008: 597e9c7 \| 2026-09-30T17:19:25-05:00 \| feat(semana9): valida spike de integracion por eventos
- L1009: 59fdb47 \| 2026-09-30T16:20:52-05:00 \| docs(semana9): define integracion API eventos y Spike 1
- L1034: L359: El Spike 1 no demuestra una necesidad de adoptar CQRS.
- L1218: L70: \| Semana 9 \| Evaluación de integración mediante contratos síncronos y evento interno; preregistro y ejecución de SPIKE-01. \| 'docs/semana9/', 'experimentos/spike-01-integracion/' \|
- L1224: L93: - 'experimentos/spike-01-integracion/'
- L1230: L117: - 'experimentos/spike-01-integracion/' — evidencia experimental de la integración mediante evento interno.
- L1233: L156: > - 'experimentos/spike-01-integracion/' — evaluación de la integración interna mediante 'UserRegisteredV1';

#### `docs/m5/correcciones-auditoria-final.md`

- L31: ## 3. S-01 — localización del preregistro de SPIKE-01
- L37: El README de SPIKE-01 ahora referencia explícitamente la secuencia:
- L39: '59fdb47'
- L42: '597e9c7'
- L45: 'c6ae059'
- L63: '65, 36, 32 ms'
- L67: - mediana de 36 ms;
- L68: - 3/3 corridas bajo 2000 ms.

#### `docs/m5/freeze-final.md`

- L41: - preregistro de SPIKE-01;
- L42: - resultados históricos de SPIKE-01;
- L59: - SPIKE-01, SPIKE-02 y SPIKE-03 permanecen disponibles como evidencia.

#### `docs/m5/veredicto-semantico-final.md`

- L124: '59fdb47'
- L125: → preregistro / definición previa de SPIKE-01
- L127: '597e9c7'
- L128: → implementación y resultados de SPIKE-01
- L130: 'c6ae059'
- L138: ## 8. AS-06 — SPIKE-01
- L142: - 28 ms;
- L143: - 65 ms;
- L144: - 36 ms;
- L145: - 32 ms.
- L154: SPIKE-01 no demuestra:
- L287: - SPIKE-01;

#### `docs/semana8/baseline-pre-modular.md`

- L206: \| 50 \| 255,32 ms \| 362,50 ms \| 50/50 \|

#### `docs/semana9/09-api-eventos-integracion.md`

- L9: > Parte de su contenido fue preregistrado antes de SPIKE-01 y otras secciones
- L14: > - preregistro: '59fdb47';
- L15: > - implementación y resultados: '597e9c7';
- L16: > - decisión arquitectónica posterior: ADR-003, 'c6ae059'.
- L43: - definir la hipótesis, alcance y criterios de éxito del Spike 1 de Semana 10.
- L326: Por esta razón se selecciona como candidata para el Spike 1 de Semana 10.
- L355: La sustitución se realizará únicamente dentro del Spike 1 hasta conocer su resultado.
- L412: **EVENTO POTENCIAL — NO SE UTILIZA EN EL SPIKE 1**
- L430: **NO JUSTIFICADO PARA EL SPIKE 1**
- L466: \| 'UserRegisteredV1' \| el registro de usuario existe y actualmente produce un efecto en Activities \| aceptar para Spike 1 \|
- L483: El contrato propuesto para el Spike 1 es:
- L504: ## 10. Spike 1 — integración UserRegisteredV1
- L526: El Spike 1 modifica exclusivamente el flujo:
- L540: El Spike 1 no incluye:
- L559: El Spike 1 se considerará técnicamente favorable si se observa que:
- L592: ## 13. Mediciones del Spike 1
- L615: El Spike 1 se ejecutará en una rama separada:
- L617: 'exp/spike-01-integracion-eventos'
- L621: 'experimentos/spike-01-integracion/'
- L645: El Spike 1 permitirá evaluar una forma limitada de consistencia eventual:
- L672: hasta conocer el resultado del Spike 1.
- L698: ### Hipótesis probada en Spike 1
- L704: 'experimentos/spike-01-integracion/'
- L708: El Spike 1 fue ejecutado el 30/09/2026.
- L712: En las cuatro ejecuciones observadas, 'UserRegisteredV1' permitió conservar el registro del usuario y crear las categorías predeterminadas dentro del umbral experimental de 2000 ms. Los tiempos observados del evento fueron 28 ms, 65 ms, 36 ms y 32 ms, con mediana de 34 ms.
- L716: La evidencia reproducible y las limitaciones del experimento se conservan en 'experimentos/spike-01-integracion/'.

#### `experimentos/EXP-004-android-bajo-carga/README.md`

- L110: - mediana: 3483 ms
- L173: - mediana: 3483 ms
- L263: En las cinco corridas Android realizadas durante la carga sostenida de 499 VUs, el GET de actividades presentó una mediana de 3483 ms y un promedio de 3466.4 ms, frente a 178 ms y 196 ms respectivamente en las cinco corridas sin carga.

#### `experimentos/semana8-paginacion-activities/README.md`

- L235: \| Mediana reportada \| 3401.13 ms \| 629.42 ms \| -81.49 % \|

#### `experimentos/semana8-post-modular-validation/README.md`

- L90: \| same-session-pre-paged-02 \| PAGED \| 721.50 ms \| 701.95 ms \| 959.88 ms \| 1023.32 ms \| 41439 \|
- L122: \| reverse-post-control \| CONTROL \| 3407.22 ms \| 3458.51 ms \| 3742.00 ms \| 4747.36 ms \| 8960 \|
- L129: \| reverse-pre-control \| CONTROL \| 3473.96 ms \| 3553.32 ms \| 4014.30 ms \| 4901.94 ms \| 8783 \|
- L136: \| CONTROL \| 3473.96 ms \| 3407.22 ms \| -1.92 % \| 4901.94 ms \| 4747.36 ms \|

#### `experimentos/spike-01-integracion/README.md`

- L1: # SPIKE-01 — Integración mediante UserRegisteredV1
- L6: - Rama experimental: 'exp/spike-01-integracion-eventos'
- L21: El Spike 1 realiza los siguientes cambios:
- L58: '28, 65, 36, 32 ms'
- L61: - Mínimo: 28 ms
- L63: - Mediana: 34 ms
- L64: - Máximo: 65 ms
- L65: - Corridas dentro del umbral de 2000 ms: 4/4
- L69: '65, 36, 32 ms'
- L72: - Mediana: 36 ms
- L73: - Máximo: 65 ms
- L153: El cumplimiento del Spike 1 no demuestra que RachaPro necesite CQRS ni obliga a adoptar una arquitectura orientada a eventos de forma general.
- L163: Este veredicto corresponde exclusivamente al Spike 1 y no implica por sí mismo la adopción de mensajería externa, microservicios, CQRS ni eventos para todas las relaciones entre módulos.
- L169: Esta sección es posterior a la ejecución histórica de SPIKE-01 y no modifica
- L174: La fuente preregistrada utilizada antes de implementar y medir SPIKE-01 quedó
- L179: '59fdb47'
- L182: '597e9c7'
- L185: 'c6ae059'
- L201: \| 1 \| 28 ms \|
- L202: \| 2 \| 65 ms \|
- L203: \| 3 \| 36 ms \|
- L204: \| 4 \| 32 ms \|
- L212: '65, 36, 32 ms'
- L217: - mediana: 36 ms;
- L218: - corridas dentro del umbral de 2000 ms: 3/3.
- L231: → mediana 36 ms
- L232: → 3/3 bajo 2000 ms

#### `experimentos/spike-03-caracterizacion-racha/veredicto.md`

- L573: 9. la regla direccional se cumplió en '3/3';
- L697: runValid                         true en 3/3
- L702: Regla direccional                3/3

### P1-ESTADOS

Coincidencias encontradas: **194**.

#### `README.md`

- L11: \| Estado de Semana 8 \| Cerrada y validada en 'semana8-final-validado' \|
- L125: Las decisiones todavía marcadas como 'PENDIENTE', 'NO DECIDIDO' o equivalentes permanecen abiertas hasta que el equipo las cierre explícitamente.
- L294: 'semana8-final-validado'

#### `docs/adr/0001-decision-estilo.md`

- L76: Cada atributo deberá continuar siendo validado con evidencia independiente.
- L487: La evidencia de cierre se conserva en 'docs/semana8/', 'experimentos/' y en el tag 'semana8-final-validado'.

#### `docs/adr/0003-integracion-eventos-internos.md`

- L262: El flujo adoptado es:
- L430: - mantener el alcance del evento limitado al flujo validado;
- L500: **Spike 1:** VALIDADO
- L520: **CQRS:** NO ADOPTADO
- L530: ADR-003 fue decidido y registrado originalmente el 2026-09-30.
- L900: No modifica retrospectivamente la evidencia disponible cuando ADR-003 fue decidido.
- L942: UserRegisteredV1 adoptado
- L952: evento arquitectónicamente adoptado
- L977: NO JUSTIFICADO TODAVÍA POR LA EVIDENCIA DISPONIBLE
- L983: NO JUSTIFICADO TODAVÍA POR LA EVIDENCIA DISPONIBLE
- L1412: → obligación de sustituir el mecanismo adoptado

#### `docs/architecture/current/architecture-current.md`

- L2315: Antes de guardar las preferencias, estos valores son validados mediante 'require(...)'.
- L3402: Una vez validado el usuario activo se consulta:

#### `docs/dominio/README.md`

- L48: No convierte asuntos marcados como 'PENDIENTE', 'NO DECIDIDO' o equivalentes

#### `docs/dominio/context-map.puml`

- L58: porque su ownership permanece NO DECIDIDO.

#### `docs/dominio/evidencia/activities.md`

- L996: 'Activities como bounded context = DECIDIDO'

#### `docs/dominio/evidencia/focus.md`

- L269: \| Mecanismo de integración definitivo con Activities \| 'NO DECIDIDO' \|
- L378: 'Focus como bounded context = DECIDIDO'

#### `docs/dominio/evidencia/progress.md`

- L340: **Ownership de Achievement:** 'NO DECIDIDO'.
- L522: 'Ownership de Achievement = NO DECIDIDO'
- L603: **Clasificación:** 'NO DECIDIDO'.
- L759: **Clasificación:** 'OWNERSHIP NO DECIDIDO'.
- L822: \| Ownership definitivo de Achievement \| 'NO DECIDIDO' \|
- L947: 'Achievement ownership = NO DECIDIDO'
- L1033: 'Progress como bounded context = DECIDIDO'
- L1039: 'Achievement ownership = NO DECIDIDO'

#### `docs/dominio/evidencia/reminders.md`

- L920: \| Mecanismo definitivo de integración con Activities \| 'NO DECIDIDO' \|
- L1124: 'Reminders como bounded context = DECIDIDO'

#### `docs/dominio/justificacion-contextos.md`

- L340: Activities como bounded context = DECIDIDO
- L544: Focus como bounded context = DECIDIDO
- L877: Reminders como bounded context = DECIDIDO
- L1162: Achievement ownership = NO DECIDIDO
- L1165: El análisis de la evidencia disponible y de la decisión pendiente se desarrolla
- L1266: Progress como bounded context = DECIDIDO
- L1341: ### Decisión pendiente
- L1367: → Achievement ownership = NO DECIDIDO
- L1377: Achievement como bounded context = NO DECIDIDO
- L1841: - qué bounded contexts fueron decididos;
- L1898: decididos:
- L2085: Achievement ownership = NO DECIDIDO
- L2259: Achievement ownership = NO DECIDIDO

#### `docs/dominio/responsabilidades-contextos.md`

- L19: 'Achievement' permanece fuera de la matriz principal porque su ownership continúa como 'NO DECIDIDO'; por ello, no se considera todavía un bounded context confirmado dentro de este documento.
- L30: \| Progress \| Interpretar y agregar los resultados producidos por otras capacidades del sistema para medir y representar el avance del usuario mediante rachas y métricas de progreso. \| Interpretación de progreso, racha actual, mejor racha y métricas agregadas. \| Hechos producidos por Activities y Focus. \| 'Activity', 'PomodoroSession', 'Reminder', I […]
- L190: - Achievement ('ownership' no decidido; no se asigna a Progress en esta etapa).
- L198: Achievement corresponde a una **capacidad observada** en la implementación, pero su ownership definitivo permanece 'NO DECIDIDO'.
- L223: → NO DECIDIDO
- L243: - 'Achievement' no aparecerá como bounded context confirmado mientras su ownership permanezca 'NO DECIDIDO'.

#### `docs/dominio/subdominios.md`

- L195: Activities como bounded context = DECIDIDO
- L418: ## 7. Capacidades con ownership no decidido
- L437: Achievement ownership = NO DECIDIDO
- L446: \| Activities \| Planificar, organizar, descomponer y dar seguimiento a las actividades y tareas del usuario, incluyendo su clasificación y su ciclo de vida. \| 'DECIDIDO' \|
- L447: \| Focus \| Gestionar el ciclo de vida de las sesiones Pomodoro del usuario, incluyendo periodos de concentración y descanso, sus estados y transiciones. \| 'DECIDIDO' \|
- L448: \| Reminders \| Definir, programar y gestionar el ciclo de vida de los recordatorios del usuario, conservando la intención de generar un aviso en un momento determinado y permitiendo opcionalmente asociarlo a una Activity. \| 'DECIDIDO' \|
- L449: \| Progress \| Interpretar y agregar los resultados producidos por otras capacidades del sistema para medir y representar el avance del usuario mediante rachas y métricas de progreso. \| 'DECIDIDO' \|
- L450: \| Achievement \| Ownership pendiente de una decisión específica sobre su responsabilidad, fuente de verdad, persistencia y relación Android/backend. \| 'NO DECIDIDO' \|

#### `docs/integracion/README.md`

- L56: - AsyncAPI: contrato técnico del evento adoptado.

#### `docs/integracion/aplicabilidad-cqrs-event-sourcing.md`

- L92: NO JUSTIFICADO TODAVÍA POR LA EVIDENCIA DISPONIBLE
- L364: Actualmente no está decidido que Progress deba consumir esa información mediante:
- L862: NO JUSTIFICADO TODAVÍA POR LA EVIDENCIA DISPONIBLE
- L1370: NO JUSTIFICADO TODAVÍA POR LA EVIDENCIA DISPONIBLE
- L1524: \| Estado actual \| 'NO JUSTIFICADO TODAVÍA POR LA EVIDENCIA DISPONIBLE' \| 'NO JUSTIFICADO TODAVÍA POR LA EVIDENCIA DISPONIBLE' \|
- L1676: NO JUSTIFICADO TODAVÍA POR LA EVIDENCIA DISPONIBLE
- L1691: NO JUSTIFICADO TODAVÍA POR LA EVIDENCIA DISPONIBLE

#### `docs/integracion/cobertura-interacciones-sync-async.md`

- L23: \| Reminders → Activities \| 'ActivityLookup' como contrato síncrono observado \| No se identificó una comparación completa independiente equivalente a la de Focus; comparte semántica contractual con Focus \| COBERTURA PARCIAL / SIN CAMBIO DECIDIDO \|

#### `docs/integracion/contrato-api.md`

- L789: Esta formalización describe el contrato actualmente adoptado para la relación

#### `docs/integracion/eventos-candidatos.md`

- L16: - 'UserRegisteredV1': adoptado e implementado para ese caso específico;
- L17: - 'ActivityCompleted': candidato aceptado para evaluación, no adoptado;
- L18: - 'PomodoroSessionCompleted': candidato aceptado para evaluación, no adoptado.
- L68: evento adoptado
- L133: NO DECIDIDO
- L1601: NO DECIDIDO
- L1636: NO DECIDIDO
- L1784: evento de integración adoptado
- L2101: NO DECIDIDO
- L2257: Los candidatos aceptados continúan a evaluación arquitectónica y no se consideran automáticamente eventos de integración adoptados.

#### `docs/m5/auditoria-complementaria/README.md`

- L89: 'Estado \| Evidencia \| Hecho \| Problema \| Severidad \| Decisión pendiente \| Acción verificable \| Trazabilidad'
- L93: - 'CORREGIDO'
- L94: - 'VALIDADO — SIN CAMBIO'
- L95: - 'DECIDIDO POR EL EQUIPO'
- L96: - 'NO JUSTIFICADO'
- L97: - 'DECISIÓN PENDIENTE'

#### `docs/m5/auditoria-externa-post-freeze.md`

- L52: CORREGIDO.
- L77: ACLARADO POSTERIORMENTE.
- L102: ### H-EXT-04 — uso del término VALIDADO
- L108: MECANISMO ACTUAL VALIDADO.
- L114: La palabra validado no debe entenderse como aprobación arquitectónica,
- L121: ACLARADO POSTERIORMENTE.
- L154: H-EXT-01 = CORREGIDO.

#### `docs/m5/auditoria-final.md`

- L21: Un control satisfactorio no convierte una decisión pendiente en una decisión
- L38: \| A05 Identity \| DECISION PENDIENTE \| No se detecta cierre automatico. La evidencia existe, pero el alcance permanece abierto. \|
- L39: \| A06 Achievement ownership \| NO DECIDIDO \| El ownership permanece explicitamente abierto. \|
- L46: \| A13 CQRS \| NO ADOPTADO / NO DETERMINADO \| SPIKE-03 reconocido sin convertirlo en decision automatica. \|
- L47: \| A14 Event Sourcing \| NO ADOPTADO \| No se detecta adopcion automatica. \|

#### `docs/m5/auditoria-semantica-semanas9-10.md`

- L61: L195: Activities como bounded context = DECIDIDO
- L75: L418: ## 7. Capacidades con ownership no decidido
- L83: L19: 'Achievement' permanece fuera de la matriz principal porque su ownership continúa como 'NO DECIDIDO'; por ello, no se considera todavía un bounded context confirmado dentro de este documento.
- L86: L30: \| Progress \| Interpretar y agregar los resultados producidos por otras capacidades del sistema para medir y representar el avance del usuario mediante rachas y métricas de progreso. \| Interpretación de progreso, racha actual, mejor racha y métricas agregadas. \| Hechos producidos por Activities y Focus. \| 'Activity', 'PomodoroSession', 'Reminde […]
- L97: L190: - Achievement ('ownership' no decidido; no se asigna a Progress en esta etapa).
- L98: L198: Achievement corresponde a una **capacidad observada** en la implementación, pero su ownership definitivo permanece 'NO DECIDIDO'.
- L101: L223: → NO DECIDIDO
- L105: L243: - 'Achievement' no aparecerá como bounded context confirmado mientras su ownership permanezca 'NO DECIDIDO'.
- L135: L340: Activities como bounded context = DECIDIDO
- L317: L378: 'Focus como bounded context = DECIDIDO'
- L379: L16: - 'UserRegisteredV1': adoptado e implementado para ese caso específico;
- L522: L942: UserRegisteredV1 adoptado
- L910: L92: NO JUSTIFICADO TODAVÍA POR LA EVIDENCIA DISPONIBLE
- L929: L862: NO JUSTIFICADO TODAVÍA POR LA EVIDENCIA DISPONIBLE
- L963: L1370: NO JUSTIFICADO TODAVÍA POR LA EVIDENCIA DISPONIBLE
- L988: L1524: \| Estado actual \| 'NO JUSTIFICADO TODAVÍA POR LA EVIDENCIA DISPONIBLE' \| 'NO JUSTIFICADO TODAVÍA POR LA EVIDENCIA DISPONIBLE' \|
- L1066: L1412: → obligación de sustituir el mecanismo adoptado
- L1162: L418: ## 7. Capacidades con ownership no decidido
- L1163: L437: Achievement ownership = NO DECIDIDO
- L1164: L450: \| Achievement \| Ownership pendiente de una decisión específica sobre su responsabilidad, fuente de verdad, persistencia y relación Android/backend. \| 'NO DECIDIDO' \|
- L1167: L19: 'Achievement' permanece fuera de la matriz principal porque su ownership continúa como 'NO DECIDIDO'; por ello, no se considera todavía un bounded context confirmado dentro de este documento.
- L1168: L30: \| Progress \| Interpretar y agregar los resultados producidos por otras capacidades del sistema para medir y representar el avance del usuario mediante rachas y métricas de progreso. \| Interpretación de progreso, racha actual, mejor racha y métricas agregadas. \| Hechos producidos por Activities y Focus. \| 'Activity', 'PomodoroSession', 'Reminde […]
- L1169: L190: - Achievement ('ownership' no decidido; no se asigna a Progress en esta etapa).
- L1170: L198: Achievement corresponde a una **capacidad observada** en la implementación, pero su ownership definitivo permanece 'NO DECIDIDO'.
- L1172: L223: → NO DECIDIDO
- L1173: L243: - 'Achievement' no aparecerá como bounded context confirmado mientras su ownership permanezca 'NO DECIDIDO'.
- L1179: L1162: Achievement ownership = NO DECIDIDO
- L1180: L1165: El análisis de la evidencia disponible y de la decisión pendiente se desarrolla
- L1182: L1341: ### Decisión pendiente
- L1183: L1367: → Achievement ownership = NO DECIDIDO
- L1185: L1377: Achievement como bounded context = NO DECIDIDO
- L1187: L2085: Achievement ownership = NO DECIDIDO
- L1190: L2259: Achievement ownership = NO DECIDIDO

#### `docs/m5/cierre-pista2.md`

- L37: \| 11 \| Identity en Context Map \| DECISIÓN PENDIENTE \|
- L38: \| 12 \| Achievement ownership \| NO DECIDIDO \|
- L40: \| 14 \| Focus → Activities \| MECANISMO ACTUAL VALIDADO \|
- L41: \| 15 \| Reminders → Activities \| MECANISMO ACTUAL VALIDADO \|
- L42: \| 16 \| CQRS \| NO ADOPTADO / DECISIÓN NO DETERMINADA \|
- L43: \| 17 \| Event Sourcing \| NO ADOPTADO \|
- L45: \| 19 \| Retry \| NO JUSTIFICADO ACTUALMENTE \|
- L46: \| 20 \| Replay \| NO JUSTIFICADO ACTUALMENTE \|
- L47: \| 21 \| Outbox / entrega durable \| NO JUSTIFICADO ACTUALMENTE \|
- L48: \| 22 \| Broker externo \| NO JUSTIFICADO ACTUALMENTE \|
- L54: Estado: DECISIÓN PENDIENTE.
- L71: Estado: Achievement ownership = NO DECIDIDO.
- L108: Estado: NO ADOPTADO / DECISIÓN NO DETERMINADA.
- L114: Estado: NO ADOPTADO.
- L130: Estado: NO JUSTIFICADO ACTUALMENTE.
- L136: Estado: NO JUSTIFICADO ACTUALMENTE.
- L142: Estado: NO JUSTIFICADO ACTUALMENTE.
- L148: Estado: NO JUSTIFICADO ACTUALMENTE.

#### `docs/m5/correcciones-auditoria-final.md`

- L35: 'CORREGIDO'
- L56: 'ACLARADO POSTERIORMENTE'
- L76: 'CORREGIDO'
- L94: 'CORREGIDO DOCUMENTALMENTE'
- L111: 'ACLARADO POSTERIORMENTE'
- L129: 'bounded contexts formalmente decididos'
- L141: 'DECISIÓN PENDIENTE'

#### `docs/m5/correcciones-post-freeze.md`

- L69: La expresión MECANISMO ACTUAL VALIDADO se interpreta como MECANISMO ACTUAL
- L83: CQRS y Event Sourcing permanecen no adoptados automáticamente.
- L110: 'CORREGIDO'
- L131: 'ACLARADO POSTERIORMENTE'
- L148: 'CORREGIDO'

#### `docs/m5/freeze-final.md`

- L54: - Achievement permanece NO DECIDIDO;
- L55: - CQRS no fue adoptado automáticamente;
- L56: - Event Sourcing no fue adoptado automáticamente;

#### `docs/m5/veredicto-semantico-final.md`

- L45: 'NO DECIDIDO'
- L90: 'CORREGIDO'
- L206: 'NO ADOPTADO / DECISIÓN NO DETERMINADA'
- L214: 'NO ADOPTADO'
- L238: - estado no adoptado de CQRS / Event Sourcing.
- L263: 'ACLARADO POSTERIORMENTE'
- L304: 'CORREGIDO'
- L319: - H-SEM-01: corregido;
- L320: - H-SEM-02: aclarado posteriormente sin modificar ADR-003;
- L321: - H-SEM-03: corregido separando evidencia y veredicto.
- L327: - que Achievement tenga ownership decidido;

#### `docs/semana9/09-api-eventos-integracion.md`

- L209: Sin embargo, el flujo que se ha validado actualmente para parte de las métricas de progreso ocurre en Android:
- L408: Parte del flujo de progreso validado ocurre mediante Room en Android.
- L430: **NO JUSTIFICADO PARA EL SPIKE 1**

#### `docs/uso-ia/auditoria-eventos.md`

- L99: NO DECIDIDO
- L295: La clasificación A, B o C no decide si un candidato debe ser adoptado.
- L869: \| 'AchievementUnlocked' \| B \| 'NO DEMOSTRADO' \| Ownership de Achievement no decidido \|
- L970: NO DECIDIDO
- L1372: NO DECIDIDO
- L1389: No se ha decidido todavía si la relación debe implementarse mediante:
- L1402: No se ha decidido todavía que el mecanismo definitivo deba ser un evento.
- L1550: adoptados.

#### `experimentos/EXP-002-k6-api-activities/README.md`

- L121: EXP-002 no mide el flujo completo de Android desde la interacción del usuario hasta el renderizado final de la interfaz, por lo que no debe utilizarse por sí solo para afirmar que el escenario end-to-end completo quedó validado.

#### `experimentos/semana8-paginacion-activities/README.md`

- L450: **✅ CANDIDATO VALIDADO EXPERIMENTALMENTE PARA CONTINUAR**

#### `experimentos/semana8-post-modular-validation/README.md`

- L216: La optimización por paginación permanece como candidato validado experimentalmente y separado de la decisión arquitectónica de modularización.

#### `experimentos/semana8-regresion-funcional/README.md`

- L68: ✅ CORREGIDO Y VALIDADO
- L151: ✅ CORREGIDO Y VALIDADO
- L170: RF-01 y RF-03 correspondían a defectos funcionales preexistentes detectados durante la validación posterior y fueron corregidos.

#### `experimentos/spike-03-caracterizacion-racha/00-preregistro.md`

- L299: 'CQRS → NO JUSTIFICADO TODAVÍA POR LA EVIDENCIA DISPONIBLE'
- L301: 'Event Sourcing → NO JUSTIFICADO TODAVÍA POR LA EVIDENCIA DISPONIBLE'

## 6. Reglas de interpretación

La siguiente etapa deberá distinguir explícitamente:

- estado histórico frente a estado vigente;
- observación frente a decisión;
- evidencia frente a inferencia;
- implementación frente a bounded context formal;
- limitación demostrada frente a requisito incumplido;
- ausencia de requisito identificado frente a prohibición;
- decisión pendiente frente a error.

## 7. Prohibiciones de esta pista

Este artefacto no autoriza automáticamente a:

- modificar Identity en el Context Map;
- cambiar ownership de Achievement;
- renombrar ActivityLookup;
- adoptar CQRS;
- adoptar Event Sourcing;
- adoptar retry;
- adoptar replay;
- adoptar Outbox;
- adoptar broker externo;
- reescribir ADR históricos;
- reinterpretar evidencia posterior como causa de decisiones anteriores.

## 8. Siguiente actividad

La evidencia de esta Pista 1 debe someterse a lectura semántica manual antes de clasificar hallazgos.
