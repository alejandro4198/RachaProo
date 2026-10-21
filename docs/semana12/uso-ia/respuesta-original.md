# Respuesta original de IA — Propuesta de microservicios

- **Actividad:** Semana 12 — Actividad 4
- **Herramienta:** ChatGPT
- **Fecha:** 2026-10-21
- **Estado:** Respuesta original sin evaluación posterior

---

## Respuesta original

# Propuesta de arquitectura distribuida para RachaPro

Evaluación arquitectónica | Microservicios, rendimiento, seguridad y disponibilidad

## 1. Resumen ejecutivo y decisión arquitectónica

Mi recomendación es mantener el monolito modular de RachaPro como arquitectura principal por ahora, pero preparar sus módulos para una posible extracción progresiva hacia microservicios.

Desde una perspectiva técnica, RachaPro sí puede distribuirse en microservicios. Sus cinco capacidades funcionales permiten identificar límites de responsabilidad relativamente claros. Sin embargo, la separación funcional por sí sola no demuestra que una arquitectura distribuida vaya a mejorar el rendimiento, la seguridad o la disponibilidad.

La degradación de latencia observada durante las pruebas de concurrencia constituye una señal que debe investigarse, pero no demuestra que el monolito sea el responsable del problema.

Por tanto, propongo distinguir dos arquitecturas:

- Arquitectura distribuida objetivo: cinco microservicios de dominio, API Gateway, comunicación HTTP/JSON y eventos, persistencia aislada, observabilidad centralizada y despliegue independiente.
- Arquitectura recomendada actualmente: conservar un único backend desplegable, reforzar la modularidad, medir los cuellos de botella e introducir gradualmente los mecanismos de resiliencia y observabilidad necesarios.

EVALUACIÓN COMPARATIVA

Estado actual

## Monolito modular

Una aplicación desplegable con cinco capacidades internas.

&#x20;Recomendado ahora

Arquitectura objetivo

## 5 microservicios

Despliegues y persistencia independientes.

&#x20;Evolución condicionada

Esta decisión prioriza los atributos de calidad establecidos, especialmente el rendimiento, sin introducir complejidad distribuida antes de demostrar su necesidad.

## 2. Hechos, inferencias y supuestos

Para mantener el rigor de la evaluación, utilizaré las siguientes categorías durante toda la propuesta.

| Categoría     | Significado                                                        |
| ------------- | ------------------------------------------------------------------ |
| HECHO         | Información expresamente proporcionada sobre RachaPro.             |
| INFERENCIA    | Conclusión arquitectónica razonada a partir de los hechos.         |
| SUPUESTO      | Condición no confirmada que sirve para definir un diseño concreto. |
| RECOMENDACIÓN | Decisión técnica que propongo adoptar o evaluar.                   |

### 2.1. Hechos proporcionados

RachaPro tiene un frontend Android, consume HTTP/JSON, utiliza autenticación JWT y dispone de un backend Kotlin/Spring Boot con PostgreSQL.

El backend es actualmente un monolito modular con cinco capacidades: Identity, Activities, Focus, Progress y Reminders.

Existen dependencias funcionales expresas: Focus consulta Activities; Reminders consulta Activities; y Activities reacciona a información originada en Identity.

Los atributos priorizados son rendimiento, seguridad, usabilidad y disponibilidad.

Se ha observado degradación relativa de latencia al aumentar la concurrencia, sin identificación causal del componente que la produce. También se ha probado una indisponibilidad temporal del backend.

No se dispone en el enunciado de evidencia suficiente para concluir que separar servicios reducirá la latencia o mejorará la disponibilidad efectiva.

### 2.2. Supuestos de diseño

| Código | SUPUESTO                                                                                                                              |
| ------ | ------------------------------------------------------------------------------------------------------------------------------------- |
| S1     | Las capacidades pueden disponer de interfaces de dominio estables sin dependencia de acceso directo a las tablas de otros módulos.    |
| S2     | Las sesiones Pomodoro deben registrarse de manera persistente cuando corresponda al comportamiento funcional de la aplicación.        |
| S3     | El progreso y los logros pueden admitir actualización asíncrona cuando no sea indispensable una respuesta inmediatamente consistente. |
| S4     | Android puede conservar su contrato HTTP/JSON durante una migración.                                                                  |
| S5     | El proyecto podría ejecutar varios contenedores, al menos en un entorno de evaluación.                                                |
| S6     | Es posible modificar gradualmente los mecanismos de integración entre módulos sin reescribir completamente el sistema.                |

Estos supuestos requieren validación durante el diseño detallado y no deben confundirse con características ya implementadas.

## 3. Arquitectura distribuida propuesta

La arquitectura objetivo utiliza microservicios organizados por capacidad de negocio, no por controladores, entidades ni operaciones CRUD.

Diagrama lógico de distribución

Servicios

Persistencia propia

Mensajería asíncrona

Diagrama de componentes lógicos, no de topología física. Las comunicaciones y dependencias específicas se detallan a continuación.

La separación de estos componentes requiere que cada servicio tenga un ciclo de despliegue independiente. Ejecutar cinco módulos en una misma aplicación Spring Boot no constituiría una arquitectura de microservicios.

## 4. Microservicios y responsabilidades

| Microservicio      | Responsabilidad y límites                                                                                                                |
| ------------------ | ---------------------------------------------------------------------------------------------------------------------------------------- |
| Identity Service   | Usuarios, autenticación, emisión y renovación de JWT, credenciales y datos necesarios para la identidad.                                 |
| Activities Service | Creación, consulta, modificación y gestión de actividades, categorías y subtareas. Propietario de su ciclo de vida.                      |
| Focus Service      | Gestión de sesiones Pomodoro, tiempos y resultados de concentración. Consulta Activities cuando necesita validar una actividad asociada. |
| Progress Service   | Cálculo y consulta de progreso, rachas y logros derivados de acciones académicas.                                                        |
| Reminders Service  | Configuración, programación y estado de recordatorios, así como coordinación de notificaciones cuando corresponda.                       |

### Consideraciones sobre los límites de dominio

Identity: no debe convertirse en un servicio central que tenga que ser consultado sincrónicamente para cada solicitud autenticada. Los demás servicios pueden validar JWT localmente mediante firma criptográfica.

Activities: es un servicio central del dominio, porque Focus y Reminders dependen de información que administra. Debe publicar contratos estables y evitar exponer directamente su modelo de persistencia.

Focus: no debe almacenar copias editables de las actividades. Puede conservar una referencia `activityId` y, si hace falta, un conjunto mínimo de datos de lectura.

Progress: es un candidato especialmente interesante para procesar eventos debido a que muchas de sus estadísticas pueden calcularse como proyecciones derivadas. No debe convertirse en propietario de las actividades ni de las sesiones.

Reminders: la programación y la entrega de una notificación son responsabilidades distintas. El servicio puede gestionar las reglas de recordatorio, mientras que Android puede continuar participando en su ejecución según los requisitos de funcionamiento local.

RECOMENDACIÓN: mantener exactamente cinco límites de dominio como arquitectura inicial de referencia. No crearía servicios adicionales para categorías, subtareas, logros o JWT, porque aumentarían la fragmentación sin un beneficio demostrado.

## 5. Comunicación entre microservicios

RECOMENDACIÓN: arquitectura híbrida, con comunicación síncrona para consultas y validaciones inmediatas, y eventos asíncronos para propagar cambios y calcular información derivada.

No recomendaría implementar todas las dependencias mediante HTTP ni utilizar eventos para absolutamente todas las operaciones.

### 5.1. Matriz de comunicación propuesta

| Origen     | Destino     | Comunicación          | Propósito                                                 |
| ---------- | ----------- | --------------------- | --------------------------------------------------------- |
| Android    | API Gateway | HTTPS/JSON            | Acceso a funcionalidades                                  |
| Gateway    | Servicios   | HTTP interno          | Enrutamiento                                              |
| Focus      | Activities  | HTTP GET              | Consultar o validar una actividad                         |
| Reminders  | Activities  | HTTP GET              | Consultar una actividad cuando sea necesario              |
| Identity   | Activities  | Evento                | Informar cambios relevantes del ciclo de vida del usuario |
| Activities | Progress    | Evento                | Informar cambios en el estado de las actividades          |
| Focus      | Progress    | Evento                | Informar sesiones de concentración finalizadas            |
| Activities | Reminders   | Evento                | Informar modificaciones que afectan recordatorios         |
| Servicios  | Identity    | Sin consulta habitual | Validación local de JWT                                   |

Esta matriz constituye un diseño propuesto. No afirma que estas comunicaciones ya existan de esa forma.

### 5.2. Ejemplo de comunicación síncrona

Caso: iniciar una sesión Pomodoro asociada a una actividad

1. Android envía `POST /api/focus/sessions` con un JWT y un identificador de actividad.
2. El Gateway enruta la solicitud a Focus Service.
3. Focus valida la identidad y los permisos asociados a la solicitud.
4. Focus consulta a Activities para verificar que la actividad existe y pertenece al usuario autorizado.
5. Focus crea la sesión y devuelve el resultado a Android.

El contrato interno podría ser:

```
GET /internal/activities/{activityId}
Authorization: Bearer <token-de-servicio>
```

La respuesta debe contener únicamente los datos que Focus necesita.

INFERENCIA: esta consulta introduce una dependencia temporal: si Activities no responde, Focus podría no ser capaz de validar una nueva asociación.

Para reducir ese acoplamiento, se podría usar una proyección local de Activities en Focus, actualizada mediante eventos. Sin embargo, esa optimización introduce consistencia eventual y no debería ser la primera implementación sin medir la necesidad.

### 5.3. Ejemplo de comunicación asíncrona

Caso: una actividad cambia a completada

El cálculo de progreso deja de formar parte del camino crítico de la respuesta de Activities, si la regla funcional admite consistencia eventual.

Ejemplo conceptual del evento:

```
{
  "eventId": "uuid-del-evento",
  "eventType": "ActivityCompleted",
  "eventVersion": 1,
  "aggregateId": "activity-id",
  "userId": "user-id",
  "occurredAt": "fecha-hora-ISO8601",
  "payload": {
    "activityId": "activity-id"
  }
}
```

Para garantizar que la actualización de una actividad y la creación del evento no queden descoordinadas, utilizaría el patrón Transactional Outbox:

1. Activities confirma su cambio de estado y registra el evento pendiente en una única transacción PostgreSQL.
2. Un publicador extrae el evento del Outbox y lo envía al broker.
3. Progress consume el evento y actualiza sus proyecciones.
4. Progress registra el identificador procesado para evitar duplicaciones de efectos.

El consumidor debe ser idempotente. Las colas pueden entregar mensajes más de una vez, por lo que no es adecuado asumir procesamiento exactamente una vez.

Además, deben definirse políticas de reintentos, mensajes fallidos, orden por agregado cuando sea necesario y reconciliación de proyecciones.

## 6. API Gateway

RECOMENDACIÓN: utilizar Spring Cloud Gateway en la arquitectura distribuida objetivo.

Su función sería ofrecer un punto de entrada unificado para Android.

```
Android
   |
   | HTTPS
   v
API Gateway
   |
   +-- /api/auth/**        -> Identity
   +-- /api/activities/**  -> Activities
   +-- /api/focus/**       -> Focus
   +-- /api/progress/**    -> Progress
   +-- /api/reminders/**   -> Reminders
```

Responsabilidades recomendadas:

- Enrutamiento de solicitudes hacia los servicios correspondientes.
- Aplicación de HTTPS en el perímetro.
- Limitación de solicitudes para proteger el backend.
- Identificadores de correlación y trazabilidad.
- Políticas de CORS cuando sean necesarias.
- Validación preliminar de tokens, sin sustituir la seguridad de cada servicio.

Spring Cloud Gateway incorpora mecanismos de limitación de solicitudes, incluidos adaptadores basados en Redis.&#x20;

[image](https://www.google.com/s2/favicons?domain=https://docs.spring.io\&sz=32)

Home

+1



Riesgo: el Gateway puede convertirse en punto único de falla si se despliega como una sola instancia. Para alcanzar alta disponibilidad necesitaría múltiples réplicas y un mecanismo de balanceo.

En el monolito actual no introduciría obligatoriamente Spring Cloud Gateway: un proxy inverso con TLS o la infraestructura de entrada existente puede ser suficiente.

## 7. Broker de mensajería

RECOMENDACIÓN: RabbitMQ, pero solamente cuando la arquitectura necesite eventos entre procesos independientes.

Lo seleccionaría por el tipo de integración planteada: eventos discretos de negocio, procesamiento mediante colas, enrutamiento por tipo de evento y reintentos.

No introduciría Apache Kafka inicialmente, porque no se ha establecido la necesidad de conservar grandes flujos de eventos, reprocesar históricos extensos o disponer de una plataforma de streaming distribuido.

Ejemplo de organización:

```
Exchange: rachapro.domain-events

Routing keys:
  identity.user.created
  identity.user.deactivated
  activities.activity.completed
  activities.activity.updated
  activities.activity.deleted
  focus.session.completed

Queues:
  progress.activity-events
  progress.focus-events
  reminders.activity-events
  activities.identity-events
```

Los consumidores deben confirmar los mensajes procesados correctamente. RabbitMQ dispone de confirmaciones de publicación y de consumidor, además de colas quorum para escenarios que requieren mayor protección frente a fallos.&#x20;

[image](https://www.google.com/s2/favicons?domain=https://www.rabbitmq.com\&sz=32)

RabbitMQ

+1



No obstante, RabbitMQ también incrementa los componentes operativos que podrían fallar.

Por ello, no lo considero obligatorio para RachaPro en su arquitectura actual. Mientras las capacidades se mantengan dentro del monolito, se pueden utilizar eventos internos desacoplados, sin infraestructura de mensajería externa.

## 8. Organización de la persistencia

### 8.1. ¿Una base de datos por microservicio?

Sí, a nivel de propiedad lógica de los datos, si se adopta realmente una arquitectura de microservicios.

Sin embargo, una base de datos por servicio no significa necesariamente cinco servidores PostgreSQL independientes.

Propongo mantener PostgreSQL como tecnología principal e implementar una separación inicial mediante bases de datos lógicas con usuarios y privilegios distintos.

| Servicio   | Persistencia propietaria                                     |
| ---------- | ------------------------------------------------------------ |
| Identity   | Usuarios, identidades, credenciales y datos de autenticación |
| Activities | Actividades, categorías y subtareas                          |
| Focus      | Sesiones y resultados Pomodoro                               |
| Progress   | Proyecciones, estadísticas, rachas y logros                  |
| Reminders  | Configuraciones y programación de recordatorios              |

Cada servicio sería el único autorizado para modificar sus propias tablas. Los demás consumirían su API o eventos.

Separación lógica de datos

Identity DB

Activities DB

Focus DB

Progress DB

Reminders DB

Las cinco bases de datos lógicas pueden residir inicialmente en una misma instancia PostgreSQL, sin proporcionar aislamiento físico de fallos o recursos.

### 8.2. Restricciones arquitectónicas

No permitiría:

- Consultas SQL directas de Focus sobre tablas de Activities.
- Claves foráneas entre bases de datos propietarias de servicios distintos.
- Transacciones ACID distribuidas entre varios microservicios para operaciones normales.
- Que Progress modifique tablas de Activities.
- Que un servicio utilice las credenciales de base de datos de otro.

Para preservar integridad entre servicios, utilizaría referencias por identificador, validaciones mediante contratos de servicio, eventos y procesos de reconciliación cuando corresponda.

INFERENCIA: una instancia PostgreSQL compartida puede seguir siendo un cuello de botella o punto común de indisponibilidad aunque existan cinco servicios independientes.

Por tanto, si el problema de rendimiento está en el motor de base de datos, dividir el backend sin corregir las consultas, índices, bloqueos o capacidad de conexiones podría mantener o empeorar la degradación observada.

### 8.3. Consistencia de datos

La estrategia sería mixta:

- Consistencia fuerte local: creación y actualización de actividades, sesiones, usuarios y recordatorios dentro de cada servicio.
- Consistencia eventual: estadísticas, rachas, logros y otras proyecciones que puedan actualizarse después de la operación principal.
- Validación inmediata: casos en los que iniciar una sesión o crear un recordatorio requiere confirmar la existencia y autorización de una actividad.

No se debe aplicar consistencia eventual a una operación que funcionalmente exija validación inmediata.

## 9. Autenticación y autorización entre servicios

La seguridad se implementaría por capas siguiendo un modelo de confianza cero entre componentes: estar dentro de la red interna no significa que una solicitud sea confiable.

### 9.1. Autenticación de usuarios

Flujo recomendado:

1. Android autentica al usuario a través de Identity.
2. Identity emite un JWT firmado, con tiempo de expiración y claims mínimos.
3. Android envía el JWT a través de HTTPS.
4. El Gateway puede verificar el token.
5. El microservicio destinatario vuelve a verificar firma, emisor, audiencia, expiración y permisos aplicables.
6. La autorización sobre recursos se verifica en el propio servicio.

Utilizaría firmas asimétricas, por ejemplo RS256 o ES256, con distribución controlada de claves públicas mediante JWKS y rotación de claves.

Esta propuesta permite que cada servicio valide tokens sin hacer una llamada remota a Identity en cada petición.

### 9.2. Seguridad entre microservicios

Es indispensable diferenciar dos identidades:

- Identidad del usuario: quién inició una operación.
- Identidad del servicio: qué componente backend está realizando la comunicación.

Para las llamadas internas propondría tokens de servicio con alcance limitado y audiencia específica, sobre conexiones TLS. Como alternativa de mayor madurez, mTLS para autenticar los componentes en la capa de transporte.

Cuando Focus consulta una actividad, necesita además respetar los permisos del usuario original. La identidad del servicio no debe autorizarlo a consultar arbitrariamente datos de cualquier usuario.

Una implementación posible es utilizar un token interno de duración corta que represente al servicio e incluya un contexto de usuario validado, o un mecanismo controlado de propagación de identidad. No se debe aceptar un `userId` libre como prueba de autorización.

### 9.3. Controles mínimos

| Control              | Propuesta                                             |
| -------------------- | ----------------------------------------------------- |
| Comunicación externa | HTTPS obligatorio                                     |
| Comunicación interna | TLS y credenciales de servicio                        |
| Contraseñas          | Hash adaptativo seguro, por ejemplo Argon2id o BCrypt |
| JWT                  | Validación de firma, expiración, emisor y audiencia   |
| Autorización         | Por operación y por propiedad del recurso             |
| Secretos             | Fuera del repositorio y de las imágenes de contenedor |
| Base de datos        | Credenciales distintas y mínimo privilegio            |
| Endpoints internos   | Sin exposición pública                                |
| Logs                 | Sin contraseñas, JWT completos ni secretos            |
| Protección de abuso  | Rate limiting, especialmente en autenticación         |
| Dependencias         | Análisis de vulnerabilidades y actualizaciones        |

RECOMENDACIÓN: estos controles no requieren migrar a microservicios. Deben implementarse igualmente en el monolito modular.

## 10. Resiliencia y tolerancia a fallos

La arquitectura distribuida introduce fallas parciales: un servicio puede funcionar mientras otro se encuentra indisponible.

Por ello no basta con separar procesos; es necesario definir cómo se comporta el sistema durante esas fallas.

### 10.1. Mecanismos recomendados

| Mecanismo              | Aplicación concreta                                                               |
| ---------------------- | --------------------------------------------------------------------------------- |
| Timeouts               | Limitar cuánto espera Focus o Reminders a Activities                              |
| Circuit breaker        | Interrumpir temporalmente llamadas repetidas hacia un servicio que falla          |
| Retries                | Solo en fallos transitorios y operaciones seguras o idempotentes                  |
| Bulkhead               | Evitar que un tipo de operación agote conexiones o hilos de otras funcionalidades |
| Health checks          | Identificar instancias que no pueden prestar servicio                             |
| Graceful shutdown      | Terminar solicitudes activas antes de retirar una instancia                       |
| Idempotencia           | Evitar duplicación de efectos por reintentos o mensajes repetidos                 |
| Dead Letter Queue      | Aislar eventos que no pueden procesarse correctamente                             |
| Backups y restauración | Recuperar datos persistidos ante fallos o errores operativos                      |
| Degradación controlada | Mantener funcionalidad parcial cuando una dependencia no esté disponible          |

Para la comunicación HTTP interna utilizaría Resilience4j, integrado con las capacidades de Spring Boot.

### 10.2. Comportamiento ante fallos

| Falla                    | Comportamiento propuesto                                                                                                                    |
| ------------------------ | ------------------------------------------------------------------------------------------------------------------------------------------- |
| Identity no disponible   | Tokens vigentes pueden seguir validándose localmente; nuevo inicio de sesión o renovación puede fallar                                      |
| Activities no disponible | Consultas y modificaciones de actividades fallan; Focus puede continuar únicamente con operaciones que no necesiten validar Activities      |
| Focus no disponible      | Activities, Progress y Reminders pueden continuar disponibles                                                                               |
| Progress no disponible   | El usuario puede completar actividades; el progreso se actualiza después mediante eventos pendientes                                        |
| Reminders no disponible  | Se difiere el procesamiento de recordatorios del servidor; los recordatorios locales ya programados pueden seguir funcionando según Android |
| RabbitMQ no disponible   | Los eventos pendientes permanecen en el Outbox; se reintenta la publicación cuando el broker se recupera                                    |
| PostgreSQL no disponible | Los servicios dependientes de esa instancia pueden perder sus funciones; se requieren recuperación, respaldos y aislamiento adicional       |

Es importante destacar que el procesamiento asíncrono no elimina la falla: la transforma en una demora recuperable si los mecanismos de persistencia y reintentos funcionan correctamente.

### 10.3. Disponibilidad desde Android

La prueba de indisponibilidad temporal del backend mencionada en el enunciado hace especialmente relevante la experiencia del usuario.

Mantendría una estrategia en Android basada en almacenamiento local y sincronización controlada, siempre que los requisitos funcionales lo permitan.

Por ejemplo, durante una indisponibilidad:

- Mostrar datos previamente sincronizados.
- Permitir que un temporizador Pomodoro local continúe funcionando.
- Informar claramente qué información aún no está sincronizada.
- Evitar reintentos infinitos o duplicación de operaciones.
- Reconciliar cambios pendientes cuando regrese la conectividad.

INFERENCIA: mejorar el funcionamiento sin conexión podría tener un impacto mayor en la usabilidad percibida que separar cinco servicios en el servidor.

La arquitectura de microservicios por sí misma no garantiza continuidad de uso en Android cuando el backend o la conexión de red fallan.

## 11. Observabilidad

Este es uno de los componentes prioritarios de la propuesta porque todavía no se conoce la causa de la degradación de latencia.

RECOMENDACIÓN: introducir observabilidad antes de decidir la migración.

La plataforma propuesta sería:

| Componente                 | Tecnología                        | Finalidad                                 |
| -------------------------- | --------------------------------- | ----------------------------------------- |
| Métricas                   | Spring Boot Actuator + Micrometer | Instrumentar aplicación, JVM y peticiones |
| Almacenamiento de métricas | Prometheus                        | Recolectar series temporales              |
| Visualización              | Grafana                           | Paneles comparativos                      |
| Trazas distribuidas        | OpenTelemetry + Tempo o Jaeger    | Rastrear solicitudes entre servicios      |
| Logs                       | Logs JSON estructurados + Loki    | Centralizar y consultar errores           |
| Pruebas de carga           | k6                                | Ejecutar escenarios comparables           |

Micrometer y OpenTelemetry permiten instrumentar solicitudes y correlacionar operaciones entre componentes Spring.&#x20;

[image](https://www.google.com/s2/favicons?domain=https://www.springframework.org\&sz=32)

Home

+2



### Métricas mínimas

Indicadores que deberían medirse

Latencia

p50, p95, p99

Capacidad

RPS y concurrencia

Errores

Tasa HTTP 4xx/5xx

JVM

CPU, memoria, GC

PostgreSQL

Consultas, conexiones, bloqueos

Dependencias

Latencia HTTP interna y colas

También mediría la duración de transacciones, saturación de pools de conexiones, esperas de hilos, duración de consultas SQL, errores de autenticación y tiempos de procesamiento de eventos.

Los dashboards deben diferenciar cada endpoint y operación; el promedio global puede ocultar degradaciones específicas.

### ¿Cómo identificaría el cuello de botella?

Realizaría una investigación ordenada:

1. Reproducir las pruebas de carga actuales bajo condiciones controladas.
2. Medir latencia y capacidad por endpoint.
3. Correlacionar el incremento de concurrencia con CPU, memoria, GC y pools.
4. Examinar tiempos de SQL, índices, bloqueos y saturación de PostgreSQL.
5. Revisar las llamadas a dependencias y su contribución a la latencia.
6. Identificar el componente dominante antes de rediseñar el despliegue.
7. Aplicar una optimización concreta y repetir las pruebas.

No consideraría suficiente observar que aumenta el p95 con la concurrencia. Hay que determinar qué recurso se está saturando, qué operación se degrada y cuál es el límite de capacidad del despliegue evaluado.

## 12. Despliegue de la arquitectura

La propuesta debe distinguir entre una arquitectura demostrable para un proyecto universitario y una plataforma de operación de mayor complejidad.

### 12.1. Despliegue de referencia con contenedores

Utilizaría Docker para empaquetar cada aplicación Spring Boot y Docker Compose para integrar inicialmente el sistema.

```
Infraestructura de ejecución
│
├── Reverse Proxy / TLS
│   └── API Gateway
│
├── Servicios Kotlin + Spring Boot
│   ├── identity-service
│   ├── activities-service
│   ├── focus-service
│   ├── progress-service
│   └── reminders-service
│
├── RabbitMQ (si se habilitan eventos externos)
│
├── PostgreSQL
│   ├── identity_db
│   ├── activities_db
│   ├── focus_db
│   ├── progress_db
│   └── reminders_db
│
└── Observabilidad
    ├── Prometheus
    ├── Grafana
    ├── Loki
    └── Tempo / Jaeger
```

Esto permitiría probar despliegues separados, contratos y fallas parciales.

Limitación: Docker Compose sobre un único host no aporta alta disponibilidad física, reprogramación automática entre nodos ni escalado distribuido propio de un orquestador. Es una topología apropiada para experimentar, pero no representa por sí sola una plataforma productiva tolerante a fallos.

### 12.2. Despliegue avanzado

Para un escenario que realmente necesitara escalabilidad y mayor disponibilidad, consideraría Kubernetes con:

- Deployments individuales por microservicio.
- Services internos para descubrimiento y comunicación.
- Ingress o Gateway de entrada.
- Readiness, liveness y startup probes.
- Gestión de secretos.
- Rolling updates.
- Horizontal Pod Autoscaler cuando existan métricas y capacidad suficientes.
- PostgreSQL y RabbitMQ con mecanismos de disponibilidad acordes al objetivo operativo.

No implementaría obligatoriamente un clúster Kubernetes en RachaPro sin justificación. Su complejidad podría superar la del problema de rendimiento que se pretende resolver.

## 13. Escalabilidad

La ventaja fundamental de separar microservicios sería la posibilidad de escalar selectivamente las capacidades que realmente lo necesiten.

Por ejemplo, si Activities recibe más carga que los demás servicios, podría incrementarse únicamente el número de sus réplicas, sin replicar también Focus, Progress, Identity y Reminders.

Ejemplo conceptual de escalado independiente

Número de réplicas ilustrativo, no dimensionamiento propuesto ni resultado medido.

Identity

1

Activities

3

Focus

2

Progress

1

Reminders

1

Para que esto funcione, recomendaría que los servicios HTTP sean esencialmente stateless. Los estados duraderos deben persistirse en sistemas externos.

En Focus, la precisión de un temporizador no debería depender de un hilo o contador residente en una réplica determinada. Una opción es que Android controle el temporizador local y que el backend registre y valide los datos persistentes conforme a las reglas del dominio.

En Reminders, múltiples réplicas no deben disparar el mismo recordatorio varias veces. Se necesitarían mecanismos como asignación exclusiva de tareas, bloqueos adecuados, identificación única de ejecuciones o colas con consumidores coordinados.

### Escalabilidad de la persistencia

Aumentar las réplicas HTTP no aumenta automáticamente la capacidad de PostgreSQL.

Habría que controlar:

- Límites de conexiones de cada instancia.
- Tamaño de los pools de conexiones.
- Índices y planes de ejecución.
- Consultas lentas.
- Volumen de escrituras.
- Contención entre transacciones.
- Capacidad de CPU, memoria y almacenamiento de la base de datos.

Por eso, antes de aumentar instancias, es necesario confirmar que el cuello de botella se encuentra en la capa de aplicación y no en un recurso compartido.

## 14. Cambios al CI/CD

La adopción de microservicios requeriría modificar el pipeline para manejar artefactos, validaciones y despliegues independientes.

### 14.1. Estructura del repositorio

Para un equipo universitario, recomendaría inicialmente un monorepositorio con proyectos Gradle separados.

```
rachapro/
├── android/
├── services/
│   ├── identity-service/
│   ├── activities-service/
│   ├── focus-service/
│   ├── progress-service/
│   └── reminders-service/
├── contracts/
├── infrastructure/
│   ├── compose/
│   ├── monitoring/
│   └── deployment/
├── testing/
│   ├── integration/
│   ├── contracts/
│   └── performance/
└── .github/workflows/
```

El monorepositorio facilitaría la coordinación y las revisiones, sin impedir que cada servicio produzca su propia imagen y se despliegue por separado.

### 14.2. Pipeline propuesto

Flujo CI/CD por microservicio

1. Detectar qué servicio cambió y validar dependencias afectadas.
2. Ejecutar análisis estático, tests unitarios y controles de seguridad.
3. Ejecutar pruebas de integración con PostgreSQL y RabbitMQ, cuando corresponda.
4. Validar compatibilidad de contratos HTTP y esquemas de eventos.
5. Construir la imagen de contenedor versionada y escanear vulnerabilidades.
6. Desplegar en un entorno de prueba y ejecutar smoke tests.
7. Aplicar migraciones de base de datos compatibles con despliegues graduales.
8. Desplegar el servicio, comprobar salud y habilitar rollback si falla.

Introduciría herramientas como Flyway para versionar la estructura de las bases de datos y Testcontainers para pruebas de integración reproducibles.

Las pruebas de contrato serían especialmente importantes, porque un cambio en Activities podría afectar Focus y Reminders aunque estos últimos no hayan cambiado de código.

Además, conservaría las pruebas k6 para comparar el comportamiento antes y después de cada modificación importante.

## 15. Ventajas frente al monolito modular actual

La arquitectura propuesta podría producir beneficios concretos, pero deben distinguirse las capacidades estructurales de las mejoras que todavía necesitan demostración.

| Atributo       | Ventaja potencial                                                 | Condición necesaria                                                  |
| -------------- | ----------------------------------------------------------------- | -------------------------------------------------------------------- |
| Rendimiento    | Escalado independiente de Activities o Focus                      | Identificar que su capacidad de procesamiento es el límite relevante |
| Seguridad      | Aislamiento de datos y permisos por servicio                      | Aplicar controles, credenciales y autorización de manera consistente |
| Usabilidad     | Mayor continuidad de funciones no afectadas por una falla parcial | Diseñar degradación controlada y recuperación en Android             |
| Disponibilidad | Fallas de un servicio pueden no afectar a los demás               | Eliminar dependencias críticas innecesarias y puntos únicos de falla |
| Mantenibilidad | Despliegue independiente de funcionalidades                       | Mantener contratos estables y responsabilidades claras               |
| Evolución      | Posibilidad de actualizar y escalar capacidades por separado      | Disponer de automatización, pruebas y observabilidad adecuadas       |

INFERENCIA: probablemente los beneficios más claros para RachaPro estarían en el aislamiento de fallas y la posibilidad de evolucionar componentes de forma independiente, no necesariamente en la latencia.

Esto se debe a que un monolito modular también puede escalar horizontalmente mediante múltiples réplicas y un balanceador, siempre que su estado y dependencias lo permitan.

## 16. Riesgos y costos adicionales

La migración introduce dificultades que el monolito modular resuelve actualmente dentro de un único proceso.

| Riesgo                    | Consecuencia                                                   |
| ------------------------- | -------------------------------------------------------------- |
| Latencia de red           | Llamadas antes locales se convierten en solicitudes HTTP       |
| Fallas parciales          | Aparecen nuevos escenarios de indisponibilidad                 |
| Consistencia eventual     | Algunos cambios dejan de reflejarse inmediatamente             |
| Complejidad transaccional | Mayor dificultad para coordinar operaciones entre servicios    |
| Persistencia distribuida  | Migraciones y consultas transversales más complejas            |
| Seguridad distribuida     | Más credenciales, contratos y superficies de ataque            |
| Operación                 | Mayor número de procesos y componentes que supervisar          |
| CI/CD                     | Más pipelines, artefactos y pruebas de integración             |
| Depuración                | Una operación puede atravesar múltiples componentes            |
| Recursos                  | Más instancias de JVM, conexiones y consumo de infraestructura |

### Costos que deben presupuestarse

Sin asignar valores monetarios, la propuesta incrementaría potencialmente los costos en:

- Infraestructura de ejecución y almacenamiento.
- Operación del Gateway y broker.
- Observabilidad y retención de logs.
- Desarrollo de contratos y pruebas.
- Administración de bases de datos y credenciales.
- Mantenimiento de pipelines y despliegues.
- Tiempo de diagnóstico y resolución de incidentes.

Una separación completa de cinco servicios podría consumir una proporción significativa del esfuerzo académico del equipo sin garantizar mejoras respecto a los atributos de calidad prioritarios.

Por ello, la complejidad operativa debe contabilizarse como un costo arquitectónico y no como un detalle secundario.

## 17. Componentes obligatorios, opcionales y no recomendados inicialmente

Esta clasificación diferencia lo necesario para la arquitectura actual y lo que se necesitaría para ejecutar la arquitectura distribuida propuesta.

| Componente                          | Monolito actual                                    | Cinco microservicios                                                                |
| ----------------------------------- | -------------------------------------------------- | ----------------------------------------------------------------------------------- |
| HTTPS y controles JWT               | Obligatorio                                        | Obligatorio                                                                         |
| Autorización por recurso            | Obligatorio                                        | Obligatorio                                                                         |
| Observabilidad de rendimiento       | Obligatorio para diagnosticar el problema          | Obligatorio                                                                         |
| Pruebas automatizadas               | Obligatorio                                        | Obligatorio                                                                         |
| Health checks                       | Recomendado                                        | Obligatorio                                                                         |
| APIs y contratos internos estables  | Recomendado                                        | Obligatorio                                                                         |
| API Gateway dedicado                | Opcional                                           | Recomendado                                                                         |
| Docker                              | Recomendado                                        | Recomendado                                                                         |
| Persistencia de propiedad exclusiva | Por módulo, recomendable                           | Por servicio, obligatorio                                                           |
| RabbitMQ                            | No necesario inicialmente                          | Condicional a eventos entre servicios                                               |
| Transactional Outbox                | No necesario para eventos internos transaccionales | Obligatorio si se requiere publicación fiable de eventos derivados de cambios en BD |
| Circuit breaker                     | Opcional para dependencias remotas                 | Recomendado                                                                         |
| Trazabilidad distribuida            | No necesaria                                       | Recomendado                                                                         |
| Redis                               | Opcional                                           | Opcional                                                                            |
| Kubernetes                          | No necesario                                       | Opcional                                                                            |
| Service mesh                        | No necesario                                       | No recomendado inicialmente                                                         |
| Cinco servidores PostgreSQL         | No necesario                                       | No obligatorio                                                                      |

Es importante destacar que API Gateway y RabbitMQ no son requisitos inherentes de los microservicios. Son decisiones de infraestructura que se justifican por los patrones de comunicación, seguridad, exposición y operación seleccionados.

En cambio, la independencia de despliegue, la propiedad de los datos, las interfaces explícitas y la posibilidad de manejar fallas parciales son características fundamentales para que la separación tenga valor arquitectónico.

## 18. Plan de evolución recomendado

No ejecutaría una migración completa de una sola vez. Propondría una estrategia incremental con decisiones condicionadas a evidencia.

Fase 1. Diagnosticar el monolito

Incorporar métricas, logs estructurados, análisis de PostgreSQL y pruebas k6 comparables. Identificar qué recurso o módulo explica la degradación de latencia.

Resultado esperado: evidencia reproducible del cuello de botella y de las condiciones de carga.

Fase 2. Fortalecer el monolito modular

Revisar dependencias, impedir accesos arbitrarios entre módulos, establecer interfaces y mejorar el tratamiento de fallas y el comportamiento durante indisponibilidad.

Resultado esperado: arquitectura modular verificable y resiliencia mejorada sin distribución física.

Fase 3. Evaluar la extracción de un servicio piloto

Si existe una justificación medible, extraer un módulo con dependencias y persistencia suficientemente desacopladas.

Progress podría ser un candidato si sus estadísticas se calculan mediante eventos y toleran consistencia eventual. Su elección es condicional, no una conclusión demostrada.

Resultado esperado: comparar una arquitectura híbrida con la línea base, incluyendo latencia, fallas y complejidad operativa.

Fase 4. Tomar una decisión basada en evidencia

Comparar el piloto con el monolito optimizado y decidir si tiene sentido distribuir otras capacidades.

Resultado esperado: conservar, ampliar o revertir la distribución según resultados verificables.

### Criterios para aprobar una migración

Antes de aprobar la extracción de un microservicio, exigiría evidencia de que:

- Existe un problema arquitectónico relevante que no se resuelve razonablemente optimizando el monolito.
- El límite funcional del candidato está suficientemente definido.
- El servicio puede desplegarse y probarse independientemente.
- El cambio no causa una degradación inaceptable del flujo completo.
- Su comportamiento ante fallas es verificable.
- El equipo puede administrar sus contratos, persistencia y observabilidad.
- El beneficio medido justifica el esfuerzo y los recursos adicionales.

No establecería umbrales numéricos sin conocer los requisitos de rendimiento y disponibilidad esperados para el proyecto.

## 19. Matriz de decisión arquitectónica

La siguiente matriz permite comparar de forma cualitativa las tres alternativas principales. Las valoraciones son juicios arquitectónicos preliminares, no mediciones de RachaPro.

| Criterio           | Monolito modular                  | Distribución parcial              | 5 microservicios                  |
| ------------------ | --------------------------------- | --------------------------------- | --------------------------------- |
| Rendimiento        | Favorable si se optimiza          | Por demostrar                     | Por demostrar                     |
| Seguridad          | Más sencilla de centralizar       | Mayor aislamiento posible         | Mayor aislamiento posible         |
| Usabilidad         | Depende de la resiliencia Android | Depende de la resiliencia Android | Depende de la resiliencia Android |
| Disponibilidad     | Falla más amplia por instancia    | Aislamiento parcial               | Aislamiento potencial mayor       |
| Complejidad        | Menor                             | Intermedia                        | Alta                              |
| Costos operativos  | Menores en general                | Mayores en general                | Más componentes a administrar     |
| Escalado selectivo | No por módulo                     | Solo del servicio extraído        | Sí, por servicio                  |
| Adecuación actual  | Más justificada                   | Condicional                       | No demostrada                     |

La tabla no debe interpretarse como una suma de puntos. Los atributos de calidad tienen prioridades diferentes y no se han definido objetivos cuantitativos suficientes para aplicar una evaluación ponderada rigurosa.

## 20. Recomendación final

DECISIÓN ARQUITECTÓNICA PROPUESTA

## No migrar todavía a microservicios completos

Mi evaluación es que RachaPro no cuenta, con los hechos proporcionados, con evidencia suficiente para justificar ahora la migración completa del monolito modular a cinco microservicios.

El incremento de la concurrencia y la degradación de latencia observada no permiten concluir que la separación de servicios sea una solución. El problema podría encontrarse en la aplicación, las consultas a PostgreSQL, la gestión de conexiones, los recursos de ejecución o una combinación de factores.

La distribución también podría mejorar el aislamiento de ciertos fallos, pero agregaría nuevas dependencias de red y componentes de infraestructura. No es razonable dar por garantizada una mejora de disponibilidad sin diseñar y validar su tolerancia a fallos.

Por tanto, adoptaría las siguientes decisiones:

1. Mantener el monolito modular como arquitectura de ejecución mientras se realiza el diagnóstico de rendimiento.
2. Conservar las cinco capacidades como límites funcionales y reforzar sus contratos.
3. Implementar observabilidad y resiliencia, incluyendo pruebas durante fallas y recuperación del backend.
4. Preparar la posibilidad de extracción, evitando dependencias indebidas de base de datos y acoplamientos entre módulos.
5. Evaluar un piloto distribuido solamente si las mediciones o necesidades de evolución lo justifican.

### Conclusión para la evaluación universitaria

La arquitectura de cinco microservicios propuesta es técnicamente viable y constituye una alternativa concreta de evolución de RachaPro. Sus componentes serían Identity, Activities, Focus, Progress y Reminders, con comunicación híbrida HTTP/eventos, datos de propiedad exclusiva, controles de seguridad independientes y capacidad de despliegue selectivo.

Sin embargo, desde el punto de vista de la ingeniería de software, la alternativa preferida actualmente es optimizar y fortalecer el monolito modular, en lugar de introducir una arquitectura distribuida cuya ventaja todavía no ha sido demostrada.

Esto no descarta los microservicios. Establece que su adopción debe responder a una necesidad comprobada, con resultados medibles y una evaluación explícita de los nuevos riesgos.

La decisión correcta para RachaPro no es distribuir por distribuir: es seleccionar la arquitectura menos compleja que satisfaga sus atributos de calidad y evolucionarla cuando exista evidencia de que dejó de ser suficiente.