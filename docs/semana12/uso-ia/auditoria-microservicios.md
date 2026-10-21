# Auditoría crítica de propuesta de microservicios generada por IA

- **Actividad:** Semana 12 — Actividad 4
- **Proyecto:** RachaPro
- **Objeto:** Evaluar si la estrategia de microservicios propuesta por una IA añade complejidad innecesaria frente a la arquitectura actual.
- **Fecha de evaluación:** 2026-10-21
- **Autor de la evaluación y decisión:** Equipo de RachaPro
- **Herramienta de IA auditada:** ChatGPT
- **Estado:** Auditoría crítica posterior a la preservación de la propuesta original
- **Prompt original:** `docs/semana12/uso-ia/prompt-original.md`
- **Respuesta original:** `docs/semana12/uso-ia/respuesta-original.md`
- **Documento de auditoría:** `docs/semana12/uso-ia/auditoria-microservicios.md`

---

## 1. Objeto y alcance

El objetivo de esta auditoría es evaluar críticamente una propuesta de arquitectura distribuida generada por IA para RachaPro y determinar qué elementos:

- están respaldados por evidencia actual del proyecto;
- están parcialmente respaldados;
- dependen de condiciones futuras;
- o no cuentan todavía con evidencia suficiente para justificar su adopción.

La auditoría no pretende determinar si los microservicios son apropiados o inapropiados de manera general.

La pregunta evaluada es específica para RachaPro:

> **¿La evidencia actual del proyecto justifica transformar su monolito modular en una arquitectura distribuida de microservicios y asumir las responsabilidades adicionales asociadas con esa distribución?**

Para responderla se contrastan las afirmaciones y recomendaciones de la IA con:

- el estado actual del código;
- las decisiones arquitectónicas documentadas;
- el Spike 1 de integración;
- los experimentos de crecimiento de carga de Semana 11;
- el escenario de caída y recuperación;
- el experimento de timeout;
- la estimación relativa de costos de Semana 12;
- la configuración de seguridad inspeccionada;
- y las limitaciones expresamente identificadas en cada evidencia.

Esta auditoría **no implementa cinco microservicios** y tampoco realiza una comparación experimental directa entre:

```text
monolito modular
vs.
cinco microservicios
```

Por tanto, ninguna conclusión debe presentarse como evidencia experimental de superioridad de uno de esos dos estilos.

---

## 2. Separación entre propuesta de IA y decisión del equipo

La respuesta original de IA no debe interpretarse como una instrucción de migrar inmediatamente a microservicios.

La IA distingue entre:

1. una **arquitectura distribuida objetivo**, que describe cómo podría evolucionar RachaPro si llegara a justificarse la distribución;
2. una **arquitectura recomendada actualmente**, consistente en mantener el monolito modular mientras se obtiene mejor evidencia.

La arquitectura objetivo propuesta incluye, entre otros elementos:

- Identity Service;
- Activities Service;
- Focus Service;
- Progress Service;
- Reminders Service;
- API Gateway;
- comunicación HTTP y mediante eventos;
- persistencia con ownership independiente;
- mecanismos adicionales de resiliencia;
- observabilidad;
- despliegues independientes;
- y modificaciones al CI/CD.

Sin embargo, la propia respuesta original concluye que RachaPro no cuenta todavía con evidencia suficiente para justificar la migración completa.

Por tanto, esta auditoría no atribuye a la IA una recomendación que no realizó.

La evaluación se concentra en:

> **qué partes de la arquitectura propuesta responden a necesidades actuales demostradas y cuáles únicamente cobrarían sentido después de justificar la distribución física.**

---

## 3. Evidencia original preservada

La propuesta generada por IA fue preservada antes de registrar la evaluación crítica posterior.

### 3.1. Prompt original

Ruta:

```text
docs/semana12/uso-ia/prompt-original.md
```

El prompt solicita explícitamente una propuesta concreta y pide evaluar:

- microservicios;
- responsabilidades;
- comunicación;
- eventos;
- API Gateway;
- broker;
- persistencia;
- base de datos por servicio;
- seguridad;
- resiliencia;
- observabilidad;
- despliegue;
- escalado;
- CI/CD;
- costos;
- riesgos;
- componentes obligatorios y opcionales;
- y una recomendación final.

Por ello, la aparición de estas tecnologías o mecanismos en la respuesta no debe interpretarse automáticamente como una imposición espontánea de la IA.

Varios de ellos fueron solicitados expresamente como dimensiones de análisis.

---

### 3.2. Respuesta original

Ruta:

```text
docs/semana12/uso-ia/respuesta-original.md
```

Este archivo conserva la propuesta antes de la auditoría del equipo.

La respuesta original:

- describe una posible arquitectura de cinco microservicios;
- analiza componentes asociados;
- identifica costos y riesgos;
- y finalmente recomienda no realizar todavía una migración completa.

La auditoría actual constituye un documento posterior e independiente.

---

## 4. Fuentes utilizadas para contrastar la propuesta

### 4.1. Decisiones arquitectónicas

Se utilizaron directamente:

```text
docs/adr/0002-aislamiento-persistencia.md
docs/adr/0003-integracion-eventos-internos.md
docs/adr/0004-redecision-distribucion.md
```

ADR-002 permite analizar:

- ownership de persistencia;
- contratos;
- acceso a repositories;
- límites entre módulos.

ADR-003 permite analizar:

- eventos internos;
- alcance del Spike 1;
- exclusiones experimentales;
- criterios de re-decisión sobre mecanismos de integración.

ADR-004 contiene la propuesta de re-decisión de distribución de Semana 12 y contrasta la evidencia posterior con la decisión de distribución previa.

No se incluye una ruta incompleta o inferida para ADR-001 en esta lista de fuentes directas.

---

### 4.2. Integración y Spike 1

Se utiliza:

```text
docs/semana9/09-api-eventos-integracion.md
```

junto con la evidencia correspondiente del Spike 1 disponible en el repositorio.

Estas fuentes permiten delimitar:

- qué mecanismo de eventos se evaluó;
- bajo qué arquitectura;
- qué resultado fue observado;
- y qué tecnologías quedaron fuera del alcance.

---

### 4.3. Campaña de crecimiento de Semana 11

Prerregistro:

```text
experimentos/semana11-crecimiento-post-activities/00-preregistro.md
```

Resultados:

```text
experimentos/semana11-crecimiento-post-activities/resultados-crecimiento.md
```

Scripts y elementos relacionados con la ejecución se encuentran bajo:

```text
experimentos/semana11-crecimiento-post-activities/
```

Estas rutas constituyen la referencia primaria para las afirmaciones de esta auditoría relativas a:

- niveles de carga;
- número de corridas;
- criterio de P95;
- degradación observada;
- éxito funcional;
- errores HTTP;
- y límites de interpretación.

---

### 4.4. Matriz de fallos y resiliencia

Ruta:

```text
experimentos/semana11-crecimiento-post-activities/matrices-fallos-resiliencia.md
```

Esta evidencia se utiliza para delimitar:

- escenario de indisponibilidad;
- recuperación posterior;
- alcance de la operación observada;
- ambigüedades o condiciones operativas de algunas repeticiones.

---

### 4.5. Experimento de timeout

Resultados:

```text
experimentos/semana11-crecimiento-post-activities/resultados-spike-timeout.md
```

Intervención de código:

```text
app/src/main/java/com/example/rachapro/network/RetrofitClient.kt
```

Estas fuentes permiten rastrear:

- línea base;
- intervención;
- mediciones posteriores;
- umbral;
- y conclusión experimental.

---

### 4.6. Costos relativos

Ruta:

```text
docs/semana12/costos-relativos-distribucion.md
```

La cuantificación directa utilizada en esta auditoría es:

```text
1 unidad backend desplegable
vs.
5 unidades backend desplegables
```

Por tanto:

```text
5 / 1 = 5×
```

Esta relación representa **exclusivamente el conteo de unidades backend desplegables**.

No representa:

- costo monetario;
- horas;
- mantenimiento;
- infraestructura total;
- complejidad total;
- esfuerzo humano.

---

### 4.7. Fitness function

Ruta:

```text
docs/semana12/fitness-function.md
```

El documento define una regla y un umbral propuestos para proteger fronteras modulares.

La existencia de esa definición no debe confundirse con una afirmación de que la nueva fitness function ya fue implementada, ejecutada y validada completamente en CI.

---

### 4.8. Seguridad

Se inspeccionaron directamente:

```text
backend/src/main/kotlin/com/example/rachapro/backend/identity/security/SecurityConfig.kt
backend/src/main/kotlin/com/example/rachapro/backend/identity/security/JwtConfig.kt
backend/src/main/kotlin/com/example/rachapro/backend/identity/security/JwtService.kt
```

También se inspeccionaron referencias a:

```text
@AuthenticationPrincipal
jwt.subject
userId
```

en controladores, servicios y repositories.

Esta evidencia demuestra estructura y configuración.

No constituye por sí sola una auditoría exhaustiva de seguridad en ejecución.

---

## 5. Método de clasificación

Se utilizan cuatro categorías principales.

### 5.1. RESPALDADO

Existe evidencia suficiente para sostener la afirmación dentro del alcance evaluado.

No implica una garantía universal.

---

### 5.2. PARCIALMENTE RESPALDADO

Existe evidencia relacionada, pero no suficiente para extender la afirmación a todo el sistema o a todos los escenarios.

---

### 5.3. CONDICIONADO

La propuesta podría justificarse si aparece:

- nueva evidencia;
- un driver arquitectónico;
- una nueva restricción;
- o una decisión previa que haga necesario el mecanismo.

No representa una obligación actual.

---

### 5.4. SIN EVIDENCIA SUFICIENTE

La evidencia disponible no permite justificar actualmente la adopción o el beneficio afirmado.

No significa que la alternativa sea técnicamente incorrecta.

---

## 6. Reglas metodológicas de interpretación

### 6.1. Configurado no significa probado

Encontrar una configuración demuestra que existe una configuración.

No demuestra automáticamente todo su comportamiento en ejecución.

---

### 6.2. Implementado no significa exhaustivamente validado

Una clase o mecanismo implementado no acredita por sí solo:

- cobertura completa;
- ausencia de vulnerabilidades;
- comportamiento correcto bajo cualquier condición.

---

### 6.3. Documentado no significa implementado

Una regla en un ADR demuestra una decisión documentada.

Su cumplimiento debe contrastarse con código, pruebas u otra evidencia cuando corresponda.

---

### 6.4. Ausencia de evidencia no equivale a evidencia de ausencia

Cuando una característica no ha sido comprobada, la formulación correcta es:

> **no demostrada con la evidencia inspeccionada.**

---

### 6.5. Medición no implica causalidad

Observar degradación de una métrica no identifica automáticamente qué componente la provoca.

La investigación causal requiere contrastar hipótesis mediante:

- mediciones;
- condiciones controladas;
- intervenciones;
- repetición;
- comparación.

---

### 6.6. Capacidad arquitectónica no equivale a necesidad

Por ejemplo:

```text
Los microservicios permiten escalado independiente.
```

no implica:

```text
RachaPro necesita escalado independiente actualmente.
```

---

### 6.7. Necesidad no equivale a producto

Por ejemplo:

```text
necesidad de mensajería durable
```

no implica automáticamente:

```text
RabbitMQ
```

y:

```text
necesidad de observabilidad
```

no implica automáticamente:

```text
Prometheus + Grafana + Loki + Tempo
```

---

# 7. Evaluación 1 — Separación en cinco microservicios

## 7.1. Propuesta de IA

La arquitectura objetivo propone:

```text
Identity
Activities
Focus
Progress
Reminders
```

como unidades desplegables independientes.

Los límites propuestos corresponden aproximadamente con las capacidades funcionales ya identificadas en el backend.

---

## 7.2. Arquitectura actual

Actualmente RachaPro posee:

```text
1 backend Kotlin + Spring Boot desplegable
```

con cinco capacidades funcionales principales.

La arquitectura actual contiene:

- paquetes funcionales;
- contratos públicos;
- ownership;
- reglas ArchUnit;
- restricciones entre módulos.

Por tanto, la comparación adecuada es:

```text
monolito modular
vs.
arquitectura distribuida
```

y no:

```text
monolito sin estructura
vs.
microservicios
```

---

## 7.3. Evidencia de crecimiento

Las condiciones y resultados deben rastrearse principalmente en:

```text
experimentos/semana11-crecimiento-post-activities/00-preregistro.md
experimentos/semana11-crecimiento-post-activities/resultados-crecimiento.md
```

La campaña utilizó niveles de concurrencia:

```text
1
10
50
100
250
500
750
1000 VUs
```

Se registraron:

```text
32 corridas
```

correspondientes a:

```text
8 niveles × 4 repeticiones
```

Dentro de cada nivel:

```text
R1 = calentamiento
R2-R4 = mediciones utilizadas para el valor representativo
```

El P95 representativo se calculó como:

```text
mediana(P95_R2, P95_R3, P95_R4)
```

La primera condición preregistrada de degradación relativa se cumplió en:

```text
50 VUs
```

Esto permite afirmar:

> **bajo las condiciones probadas se observó una señal de degradación relativa de latencia desde 50 VUs según el criterio preregistrado.**

La campaña no identificó causalmente si el origen dominante era:

- Activities;
- otro módulo;
- PostgreSQL;
- consultas;
- conexiones;
- CPU;
- memoria;
- GC;
- threads;
- configuración;
- infraestructura;
- o una combinación de factores.

Por tanto:

> **la evidencia no permite afirmar que el monolito modular haya causado la degradación.**

Tampoco se ejecutó una versión equivalente distribuida.

Por ello:

> **la campaña no demuestra que cinco microservicios reduzcan el P95 observado.**

---

## 7.4. Relación de unidades desplegables

La alternativa actual utiliza:

```text
1 unidad backend desplegable
```

La alternativa de cinco microservicios utilizaría, por definición:

```text
5 unidades backend desplegables
```

Por tanto:

```text
R = 5 / 1 = 5×
```

El significado es exclusivamente:

> **cinco veces el número de unidades backend desplegables.**

No significa:

```text
5× costo económico
5× esfuerzo
5× horas
5× complejidad total
5× mantenimiento
```

Además:

```text
5× < 10×
```

por lo que tampoco representa un orden decimal completo en esta dimensión.

---

## 7.5. Evidencia que podría justificar la distribución

La decisión debería reconsiderarse ante evidencia de:

- escalado independiente necesario;
- despliegue independiente necesario;
- aislamiento de fallos necesario;
- saturación causalmente localizada;
- requisito de seguridad que exija aislamiento;
- persistencia con necesidades incompatibles;
- equipos independientes;
- o evidencia experimental favorable de un piloto distribuido.

---

## 7.6. Clasificación

```text
Cinco límites funcionales:
RESPALDADOS

Cinco unidades desplegables independientes:
CONDICIONADAS

Migración completa actualmente:
SIN EVIDENCIA SUFICIENTE

Viabilidad técnica:
POSIBLE

Necesidad actual:
NO DEMOSTRADA
```

### Conclusión del bloque

La separación funcional está respaldada.

La distribución física completa permanece condicionada a evidencia futura.

---

# 8. Evaluación 2 — Observabilidad

## 8.1. Problema identificado

Los resultados de:

```text
experimentos/semana11-crecimiento-post-activities/resultados-crecimiento.md
```

permiten observar degradación relativa de latencia, pero no localizar causalmente su origen.

La necesidad demostrada es:

> **obtener información diagnóstica adicional para investigar la causa.**

No es:

> **instalar obligatoriamente un producto específico.**

---

## 8.2. Pregunta diagnóstica

La investigación debería intentar responder:

> **¿qué recurso, operación o componente cambia de manera relevante cuando aparece la degradación?**

Variables candidatas incluyen:

- p50;
- p95;
- p99;
- latencia por endpoint;
- RPS;
- concurrencia;
- errores;
- CPU;
- memoria;
- GC;
- pools;
- conexiones;
- bloqueos;
- tiempos SQL;
- duración de transacciones.

Estas variables sirven para investigar hipótesis.

No garantizan identificar causalidad.

---

## 8.3. Stack propuesto por IA

La IA plantea como posibles tecnologías:

```text
Spring Boot Actuator
Micrometer
Prometheus
Grafana
OpenTelemetry
Tempo o Jaeger
Loki
k6
```

La necesidad de observabilidad está respaldada.

La obligatoriedad de este stack no lo está.

---

## 8.4. Clasificación

```text
Mayor observabilidad:
RESPALDADA

Necesidad de investigar el cuello de botella:
RESPALDADA

Variables de aplicación/JVM/PostgreSQL:
RESPALDADAS COMO OBJETOS DE MEDICIÓN

Prometheus:
CONDICIONADO

Grafana:
CONDICIONADO

Loki:
CONDICIONADO

OpenTelemetry:
CONDICIONADO

Tempo / Jaeger:
CONDICIONADOS

Stack completo:
SIN EVIDENCIA SUFICIENTE PARA DECLARARLO OBLIGATORIO
```

### Conclusión

RachaPro necesita mejorar la capacidad diagnóstica.

Eso no determina todavía una herramienta concreta.

---

# 9. Evaluación 3 — Comunicación distribuida y broker

## 9.1. Dependencias observadas

La inspección estructural permitió identificar relaciones como:

```text
focus -> activities.api.ActivityLookup
reminders -> activities.api.ActivityLookup
activities -> identity.api.UserRegisteredV1
```

Estas referencias demuestran dependencias estáticas entre módulos.

No demuestran transporte remoto.

---

## 9.2. Dirección de dependencia y flujo de información

Debe diferenciarse:

```text
activities -> identity.api.UserRegisteredV1
```

como dependencia estática,

de:

> Identity origina conceptualmente la información del evento y Activities la consume.

El sentido de una importación no debe confundirse automáticamente con el sentido semántico de publicación.

---

## 9.3. Propuesta distribuida de IA

La IA plantea relaciones futuras como:

```text
Focus -> Activities       HTTP
Reminders -> Activities   HTTP
Identity -> Activities    evento
Activities -> Progress    evento
Focus -> Progress         evento
Activities -> Reminders   evento
```

Estas relaciones forman parte de la arquitectura objetivo.

No describen el estado actual.

---

## 9.4. Alcance del Spike 1

Las principales referencias para delimitar el Spike son:

```text
docs/semana9/09-api-eventos-integracion.md
docs/adr/0003-integracion-eventos-internos.md
```

El Spike 1 evaluó eventos internos dentro del alcance documentado.

No evaluó de forma equivalente:

- RabbitMQ;
- Kafka;
- Google Pub/Sub;
- broker externo;
- comunicación entre microservicios;
- entrega durable entre procesos;
- recuperación de consumidores independientes;
- DLQ;
- procesamiento distribuido exactamente una vez;
- fallo de broker.

Por tanto:

> **un evento interno funcionando no demuestra necesidad de mensajería externa.**

---

## 9.5. Condiciones para evaluar un broker

Un broker podría justificarse si aparece una necesidad demostrada de:

- comunicación entre procesos independientes;
- entrega durable;
- desacoplamiento temporal;
- múltiples consumidores independientes;
- reintentos persistentes;
- procesamiento diferido;
- absorción de diferencias de ritmo;
- recuperación posterior.

Aun demostrando esa necesidad:

> **RabbitMQ seguiría siendo una alternativa a evaluar, no una consecuencia automática.**

---

## 9.6. Transactional Outbox

El patrón cobra sentido cuando existe:

```text
cambio transaccional local
+
publicación fiable hacia otro proceso
```

Como la mensajería distribuida no ha sido justificada actualmente:

```text
Transactional Outbox:
CONDICIONADO
```

---

## 9.7. Clasificación

```text
Focus -> Activities como dependencia funcional:
RESPALDADA

Reminders -> Activities como dependencia funcional:
RESPALDADA

Identity / Activities como flujo de información:
RESPALDADO EN EL ALCANCE DOCUMENTADO

HTTP interservicio:
CONDICIONADO

Eventos internos:
RESPALDADOS EN EL ALCANCE DEL SPIKE

Eventos entre procesos:
SIN EVIDENCIA SUFICIENTE

Broker externo:
CONDICIONADO

RabbitMQ:
CONDICIONADO

Transactional Outbox:
CONDICIONADO

DLQ:
CONDICIONADA
```

### Conclusión

La evidencia permite defender integración interna.

No obliga actualmente a introducir mensajería externa.

---

# 10. Evaluación 4 — Persistencia por servicio

## 10.1. Evidencia estructural

La inspección del backend encontró las siguientes entidades y repositories.

### Activities

```text
ActivityEntity
ActivityRepository
CategoryEntity
CategoryRepository
SubtaskEntity
SubtaskRepository
```

### Focus

```text
PomodoroSessionEntity
PomodoroSessionRepository
```

### Identity

```text
UserEntity
UserRepository
```

### Progress

```text
AchievementEntity
AchievementRepository
```

### Reminders

```text
ReminderEntity
ReminderRepository
```

Esto proporciona evidencia estructural de separación por capacidad.

---

## 10.2. ADR-002

Fuente:

```text
docs/adr/0002-aislamiento-persistencia.md
```

ADR-002 establece explícitamente que el aislamiento perseguido no requiere inicialmente bases o procesos independientes.

La decisión se concentra en:

- ownership lógico;
- contratos;
- internals;
- acceso controlado a repositories.

El ADR documentó cruces como:

```text
Focus -> ActivityRepository
Reminders -> ActivityRepository
Identity -> CategoryRepository
```

y estableció como criterio que ningún módulo acceda directamente al repository interno de otro.

Esto permite preservar ownership aun utilizando:

```text
una única base PostgreSQL
```

---

## 10.3. Alcance de la evidencia actual

Puede sostenerse:

```text
ownership lógico:
RESPALDADO
```

No puede sostenerse con la evidencia inspeccionada:

```text
una base por módulo:
NO DEMOSTRADO

un esquema por módulo:
NO DEMOSTRADO

un usuario PostgreSQL por módulo:
NO DEMOSTRADO

una instancia PostgreSQL por módulo:
NO DEMOSTRADO
```

---

## 10.4. Propuesta de IA

La arquitectura distribuida propone conceptualmente:

```text
Identity DB
Activities DB
Focus DB
Progress DB
Reminders DB
```

La propia propuesta aclara que estas separaciones lógicas podrían compartir una misma instancia PostgreSQL.

Por tanto:

```text
base lógica por servicio
!=
servidor PostgreSQL por servicio
```

---

## 10.5. Tres niveles de separación

### Nivel 1 — Ownership lógico

```text
Módulo A posee sus datos.
```

Respaldado actualmente.

### Nivel 2 — Separación lógica

```text
base / esquema / usuario independiente
```

No demostrada actualmente.

### Nivel 3 — Separación física

```text
infraestructura PostgreSQL independiente
```

No demostrada ni justificada actualmente.

---

## 10.6. Condiciones para mayor aislamiento

Podría reconsiderarse ante:

- aislamiento de fallos;
- escalado independiente de persistencia;
- requisitos de seguridad;
- requisitos regulatorios;
- tecnologías incompatibles;
- despliegues independientes;
- saturación localizada;
- necesidad de migraciones independientes.

---

## 10.7. Clasificación

```text
Ownership lógico:
RESPALDADO

Restricción de acceso directo a repository ajeno:
RESPALDADA COMO DECISIÓN ARQUITECTÓNICA

Base lógica por servicio:
CONDICIONADA

Esquemas separados:
CONDICIONADOS

Credenciales separadas:
CONDICIONADAS

Cinco instancias PostgreSQL:
SIN EVIDENCIA SUFICIENTE

Aislamiento físico:
SIN EVIDENCIA SUFICIENTE
```

### Conclusión

Ownership y aislamiento físico no son equivalentes.

RachaPro puede mantener ownership modular sin distribuir físicamente PostgreSQL.

---

# 11. Evaluación 5 — Seguridad distribuida

## 11.1. Configuración de rutas observada

La inspección de:

```text
backend/src/main/kotlin/com/example/rachapro/backend/identity/security/SecurityConfig.kt
```

muestra reglas equivalentes a:

```text
POST /api/users              -> permitAll
POST /api/auth/login         -> permitAll
/actuator/health             -> permitAll
/actuator/health/**          -> permitAll
/api/**                      -> authenticated
otras rutas                  -> permitAll
```

Debido al orden de reglas:

- registro es público;
- login es público;
- health es público;
- el resto de `/api/**` requiere autenticación;
- rutas no capturadas por los patrones anteriores alcanzan `anyRequest().permitAll()`.

Esto demuestra configuración.

No constituye una prueba exhaustiva de cada endpoint.

---

## 11.2. JWT observado

En:

```text
backend/src/main/kotlin/com/example/rachapro/backend/identity/security/JwtConfig.kt
```

se observan:

```text
NimbusJwtEncoder
NimbusJwtDecoder
HS256
```

La clave se obtiene mediante:

```text
RACHAPRO_JWT_SECRET
```

y el código exige un secreto decodificado de al menos:

```text
32 bytes
```

En:

```text
backend/src/main/kotlin/com/example/rachapro/backend/identity/security/JwtService.kt
```

los tokens se construyen con:

```text
issuer
issuedAt
expiresAt
subject
email
```

incluyendo:

```text
issuer = "rachapro-backend"
```

y expiración de:

```text
3600 segundos
```

---

## 11.3. Validaciones no demostradas

Se observó:

```text
issuer incluido al emitir
HS256 configurado
decoder configurado
expiresAt incluido
```

No se observó en los fragmentos inspeccionados un validator explícito de:

```text
issuer
audience
```

Por tanto:

```text
issuer emitido:
OBSERVADO

validación explícita de issuer:
NO DEMOSTRADA EN LOS ARCHIVOS INSPECCIONADOS

audience:
NO DEMOSTRADA
```

Esta conclusión no significa que el sistema sea necesariamente vulnerable.

Significa únicamente que esa garantía no quedó demostrada mediante la inspección realizada.

---

## 11.4. Identidad y ownership

Se observó uso de:

```text
@AuthenticationPrincipal
jwt.subject
```

en distintos controladores.

También se identificaron operaciones como:

```text
findByIdAndUserId(...)
findByIdAndUserIdAndIsDeletedFalse(...)
findAllByUserId(...)
```

Esto permite afirmar:

> **existen controles de acceso ligados al `userId` en múltiples operaciones inspeccionadas.**

No permite afirmar:

> **todos los endpoints han sido exhaustivamente auditados y autorizan correctamente todos los recursos.**

---

## 11.5. Configuración sensible

Se observaron referencias:

```text
RACHAPRO_JWT_SECRET
RACHAPRO_DB_URL
RACHAPRO_DB_USER
RACHAPRO_DB_PASSWORD
```

Esto demuestra externalización de configuración sensible en los archivos inspeccionados.

No certifica:

- dónde se almacenan los secretos en despliegue;
- quién puede acceder a ellos;
- cómo se rotan;
- ni qué mecanismo de secret management se utiliza.

---

## 11.6. Seguridad adicional asociada a distribución

La IA plantea mecanismos como:

```text
identidad de servicio
tokens servicio-servicio
TLS interno
audience por servicio
propagación de identidad
RS256 / ES256
JWKS
rotación de claves
mTLS
```

Estos mecanismos cobran mayor relevancia cuando una interacción como:

```text
Focus -> Activities
```

deja de ejecutarse dentro del mismo proceso y se convierte en:

```text
Focus Service
    |
    | red
    v
Activities Service
```

Entonces aparecen responsabilidades adicionales:

- autenticar servicios;
- conservar contexto de usuario;
- proteger transporte;
- gestionar credenciales;
- gestionar claves;
- autorizar llamadas internas.

---

## 11.7. HS256 frente a distribución

Actualmente se observó:

```text
HS256
clave simétrica
```

Una arquitectura distribuida podría beneficiarse de mecanismos de validación mediante claves públicas para evitar compartir una misma capacidad de firma entre múltiples servicios.

Sin embargo:

> **esta posibilidad no demuestra que RachaPro deba adoptar actualmente RS256, ES256 o JWKS.**

---

## 11.8. Clasificación

```text
Spring Security:
IMPLEMENTADO EN CÓDIGO

Reglas de /api/**:
CONFIGURADAS

JWT:
IMPLEMENTADO EN CÓDIGO

HS256:
CONFIGURADO

jwt.subject:
OBSERVADO

Ownership por userId:
OBSERVADO EN MÚLTIPLES OPERACIONES

Cobertura completa de autorización:
NO CERTIFICADA

Issuer emitido:
OBSERVADO

Validación explícita de issuer:
NO DEMOSTRADA

Audience:
NO DEMOSTRADA

Externalización de configuración sensible:
OBSERVADA

Gestión completa de secretos:
NO AUDITADA

Identidad servicio-servicio:
CONDICIONADA A DISTRIBUCIÓN

Tokens de servicio:
CONDICIONADOS

TLS interno:
CONDICIONADO

JWKS / firma asimétrica:
CONDICIONADOS

mTLS:
SIN EVIDENCIA SUFICIENTE PARA ADOPTARLO ACTUALMENTE
```

### Conclusión

La distribución puede permitir mayor aislamiento.

También introduce nuevas identidades, credenciales, canales y superficies de ataque.

No puede afirmarse que microservicios produzcan automáticamente mayor seguridad.

---

# 12. Evaluación 6 — Resiliencia y tolerancia a fallos

## 12.1. Evidencia primaria

Para este bloque se utilizan:

```text
experimentos/semana11-crecimiento-post-activities/matrices-fallos-resiliencia.md
experimentos/semana11-crecimiento-post-activities/resultados-spike-timeout.md
```

y la intervención aplicada en:

```text
app/src/main/java/com/example/rachapro/network/RetrofitClient.kt
```

---

## 12.2. Experimento de timeout

La intervención fue:

```text
connectTimeout(3, TimeUnit.SECONDS)
```

La línea base registrada fue:

```text
M_antes = 10028 ms
```

Las mediciones posteriores válidas utilizadas fueron:

```text
3024 ms
3011 ms
3014 ms
```

Mediana posterior:

```text
M_después = 3014 ms
```

Reducción:

```text
69,94 %
```

Umbral experimental:

```text
50 %
```

Resultado documentado:

```text
HIPÓTESIS RESPALDADA
```

bajo las condiciones del experimento.

---

## 12.3. Alcance del resultado de timeout

La conclusión respaldada es:

> **la modificación de `connectTimeout` en el cliente Android redujo el tiempo de espera observado ante el escenario probado de falla de conexión.**

No demuestra:

- mayor disponibilidad del backend;
- recuperación más rápida del servidor;
- ejecución exitosa de la operación fallida;
- beneficio de microservicios;
- comportamiento de futuros timeouts entre servicios.

La intervención se realizó en:

```text
cliente Android
```

y no en una arquitectura de servicios distribuidos.

---

## 12.4. Caída y recuperación

Según la matriz experimental, bajo el escenario probado se observó que, después de restaurar Spring Boot, fue posible ejecutar una **nueva operación** desde una sesión Android ya existente.

La formulación respaldada es:

```text
recuperación operativa para una nueva operación:
OBSERVADA BAJO EL ESCENARIO PROBADO
```

No:

```text
recuperación automática de la operación original fallida:
DEMOSTRADA
```

Algunas repeticiones incluyeron condiciones operativas como:

- reintentos manuales;
- desviaciones nominales;
- y al menos una situación ambigua asociada a una segunda operación POST.

Por tanto, la matriz no certifica recuperación automática ni idempotencia general.

---

## 12.5. Circuit breaker

No existe evidencia experimental suficiente de que actualmente sea necesario.

Una prueba futura tendría que comparar:

```text
sin circuit breaker
vs.
con circuit breaker
```

bajo una dependencia deliberadamente degradada.

Podrían medirse:

- latencia;
- errores;
- threads;
- conexiones;
- solicitudes enviadas;
- recuperación;
- impacto en otras operaciones.

---

## 12.6. Retries

Los reintentos solo deben evaluarse como mejora si:

1. la falla puede ser transitoria;
2. repetir la operación puede tener éxito;
3. repetirla no produce efectos incorrectos.

Por tanto:

```text
retry
!=
idempotencia demostrada
```

El escenario de Semana 11 con reintentos manuales no constituye una demostración general de que reintentos automáticos sean seguros.

---

## 12.7. Bulkhead

Un bulkhead requeriría evidencia de contención de recursos, por ejemplo:

- agotamiento de threads;
- agotamiento de conexiones;
- saturación de pools;
- interferencia entre operaciones.

Esta necesidad no fue demostrada en la evidencia revisada.

---

## 12.8. Health checks

Se observó configuración pública de:

```text
/actuator/health
```

Esto demuestra disponibilidad de un mecanismo de reporte de estado.

No demuestra:

- reinicio automático;
- failover;
- balanceo;
- retirada automática;
- alta disponibilidad.

Por tanto:

> **detectar estado no equivale a recuperar servicio.**

---

## 12.9. Graceful shutdown

Es una táctica potencialmente útil para retirar instancias sin interrumpir operaciones activas.

No fue evaluada experimentalmente en esta actividad.

---

## 12.10. Idempotencia

La idempotencia debe evaluarse por operación.

No debe afirmarse:

```text
RachaPro es idempotente
```

como propiedad global.

Para una operación concreta sería necesario comprobar que repeticiones equivalentes no:

- crean duplicados;
- duplican efectos;
- alteran incorrectamente el estado.

---

## 12.11. Dead Letter Queue

Una DLQ depende de una arquitectura de mensajería externa.

Como esa arquitectura no está actualmente justificada:

```text
DLQ:
CONDICIONADA
```

---

## 12.12. Degradación controlada

La distribución podría permitir que:

```text
Progress falle
mientras Activities continúa
```

o:

```text
Focus falle
mientras Activities continúa
```

pero estos escenarios no fueron probados con procesos independientes.

Por tanto, el aislamiento de fallos permanece como beneficio potencial.

---

## 12.13. Clasificación

```text
connectTimeout Android:
RESPALDADO EXPERIMENTALMENTE

Reducción del tiempo de espera:
RESPALDADA BAJO EL ESCENARIO PROBADO

Mayor disponibilidad del servidor:
NO DEMOSTRADA

Recuperación de nueva operación:
RESPALDADA BAJO EL ESCENARIO PROBADO

Recuperación automática de operación fallida:
SIN EVIDENCIA SUFICIENTE

Circuit breaker:
CONDICIONADO

Retries:
CONDICIONADOS

Bulkhead:
CONDICIONADO

Health check:
IMPLEMENTADO/CONFIGURADO COMO MECANISMO DE ESTADO

Alta disponibilidad derivada de health checks:
NO DEMOSTRADA

Graceful shutdown:
CONDICIONADO

Idempotencia:
REQUIERE EVALUACIÓN POR OPERACIÓN

DLQ:
CONDICIONADA

Degradación controlada:
CONDICIONADA
```

### Conclusión

Semana 11 respalda resultados específicos de timeout y recuperación limitada.

No valida automáticamente todo el conjunto de tácticas propuestas por la IA.

---

# 13. Matriz consolidada de auditoría

| Componente o afirmación | Evidencia actual | Limitación | Clasificación |
|---|---|---|---|
| Cinco límites funcionales | Código, ADR y estructura modular | Frontera lógica no implica proceso | **RESPALDADO** |
| Cinco microservicios independientes | Alternativa técnicamente definible | Necesidad no demostrada | **CONDICIONADO** |
| Escalado independiente | Capacidad estructural de microservicios | No existe cuello de botella localizado por capacidad | **CONDICIONADO** |
| Mayor observabilidad | Semana 11 no identificó causa | Medir no garantiza causalidad | **RESPALDADO** |
| Prometheus | Alternativa de implementación | Producto concreto no justificado | **CONDICIONADO** |
| Grafana | Alternativa de implementación | Producto concreto no justificado | **CONDICIONADO** |
| Loki | Alternativa de implementación | Producto concreto no justificado | **CONDICIONADO** |
| OpenTelemetry | Alternativa de instrumentación | Su alcance dependerá de la arquitectura | **CONDICIONADO** |
| Tempo / Jaeger | Alternativas de tracing | Distribución no adoptada | **CONDICIONADO** |
| Focus → Activities HTTP | Existe dependencia funcional | HTTP remoto no existe actualmente | **CONDICIONADO** |
| Reminders → Activities HTTP | Existe dependencia funcional | HTTP remoto no existe actualmente | **CONDICIONADO** |
| Eventos internos | Spike 1 / ADR-003 | Alcance intraproceso | **RESPALDADO EN SU ALCANCE** |
| Eventos entre procesos | No existe ensayo equivalente | Distribución no implementada | **SIN EVIDENCIA SUFICIENTE** |
| Broker externo | No existe necesidad demostrada | Requiere driver adicional | **CONDICIONADO** |
| RabbitMQ | Alternativa propuesta | Necesidad y selección no demostradas | **CONDICIONADO** |
| Transactional Outbox | Patrón para publicación fiable | Depende de mensajería distribuida | **CONDICIONADO** |
| DLQ | Patrón de mensajería fallida | Depende de broker | **CONDICIONADA** |
| Ownership lógico | ADR-002 + estructura | No equivale a aislamiento físico | **RESPALDADO** |
| Base lógica por servicio | Compatible con microservicios | Distribución no adoptada | **CONDICIONADA** |
| Credenciales DB por servicio | Posible aislamiento | Necesidad no demostrada | **CONDICIONADAS** |
| Cinco instancias PostgreSQL | Sin driver actual | Mayor costo estructural | **SIN EVIDENCIA SUFICIENTE** |
| Spring Security | Código inspeccionado | Configuración ≠ certificación | **RESPALDADO COMO IMPLEMENTACIÓN** |
| JWT HS256 | Código inspeccionado | No certifica toda la política JWT | **RESPALDADO COMO IMPLEMENTACIÓN** |
| Ownership por `userId` | Varias operaciones | Cobertura total no certificada | **PARCIALMENTE RESPALDADO** |
| Tokens servicio-servicio | Relevantes con procesos separados | Distribución no adoptada | **CONDICIONADOS** |
| TLS interservicio | Relevante con red interna | No existe escenario actual equivalente | **CONDICIONADO** |
| JWKS / firma asimétrica | Alternativa para validación distribuida | Actual configuración usa HS256 | **CONDICIONADO** |
| mTLS | Alternativa de aislamiento | Driver no demostrado | **SIN EVIDENCIA SUFICIENTE** |
| `connectTimeout` Android | Experimento Semana 11 | Solo cliente Android | **RESPALDADO** |
| Circuit breaker | No ensayado | Necesidad no demostrada | **CONDICIONADO** |
| Retry automático | No ensayado controladamente | Requiere seguridad/idempotencia por operación | **CONDICIONADO** |
| Bulkhead | No existe ensayo de contención | Necesidad no demostrada | **CONDICIONADO** |
| Health check | `/actuator/health` configurado | No implica recuperación | **PARCIALMENTE RESPALDADO** |
| Graceful shutdown | Táctica posible | No ensayada | **CONDICIONADO** |
| Degradación controlada | Beneficio potencial | No probada entre procesos independientes | **CONDICIONADA** |
| Docker | Herramienta de empaquetado | No demuestra necesidad de microservicios | **CONDICIONADO** |
| Docker Compose | Útil para un piloto | Un host no demuestra HA | **CONDICIONADO** |
| Kubernetes | Alternativa avanzada | Necesidad no demostrada | **SIN EVIDENCIA SUFICIENTE** |
| CI/CD por servicio | Consecuencia de despliegues separados | No requerido actualmente | **CONDICIONADO** |

---

# 14. Complejidad adicional de la arquitectura distribuida

Transformar fronteras lógicas en fronteras físicas podría introducir:

```text
más unidades desplegables
comunicación de red
fallas parciales
contratos remotos
timeouts interservicio
autenticación servicio-servicio
autorización distribuida
propagación de identidad
observabilidad entre procesos
compatibilidad de contratos
consistencia eventual
mensajería externa, si se adopta
idempotencia entre procesos
reintentos
DLQ, si se adopta broker
migraciones independientes
más artefactos
más pipelines
más configuración
más credenciales
más componentes operativos
```

Esta lista no debe interpretarse como:

> **todos estos componentes son obligatorios actualmente.**

Cada uno conserva la clasificación indicada en la matriz de la sección 13.

La lista representa responsabilidades que pueden aparecer dependiendo del diseño distribuido seleccionado.

La conclusión es:

> **convertir fronteras internas en fronteras de red crea problemas adicionales que deben resolverse explícitamente.**

---

# 15. Elementos útiles sin migrar a microservicios

La auditoría no rechaza todas las recomendaciones de la IA.

Algunas tienen valor dentro del monolito modular.

### 15.1. Fronteras explícitas

Mantener contratos bajo:

```text
<module>.api..
```

permite controlar dependencias sin distribución física.

---

### 15.2. Ownership de persistencia

ADR-002 demuestra que ownership lógico puede preservarse utilizando una sola base PostgreSQL.

---

### 15.3. Observabilidad

La campaña de Semana 11 dejó una incertidumbre causal que justifica mejorar la información diagnóstica.

---

### 15.4. Seguridad

Autenticación, autorización, ownership y gestión responsable de secretos son necesidades independientes del estilo de distribución.

---

### 15.5. Timeouts

Existe evidencia concreta sobre el `connectTimeout` del cliente Android.

Su resultado no se extrapola automáticamente a futuras comunicaciones entre servicios.

---

### 15.6. Ensayos de fallos

Continuar realizando experimentos controlados puede reducir incertidumbre antes de una futura re-decisión.

---

# 16. Afirmaciones no respaldadas actualmente

La evidencia disponible no permite afirmar:

```text
El monolito causó la degradación de Semana 11.
```

Tampoco:

```text
Los microservicios reducirán el P95.
```

Ni:

```text
Los microservicios aumentarán automáticamente la disponibilidad.
```

Ni:

```text
RabbitMQ es necesario actualmente.
```

Ni:

```text
Kubernetes es necesario actualmente.
```

Ni:

```text
RachaPro necesita cinco instancias PostgreSQL.
```

Ni:

```text
La arquitectura distribuida será necesariamente más segura.
```

Ni:

```text
Todos los endpoints poseen autorización de ownership exhaustivamente validada.
```

Ni:

```text
El issuer JWT está explícitamente validado por la configuración inspeccionada.
```

Ni:

```text
Todos los retries serían seguros.
```

Ni:

```text
La operación originalmente fallida se recuperó automáticamente.
```

Ni:

```text
Docker Compose demuestra alta disponibilidad.
```

Ni:

```text
5× unidades desplegables significa 5× costo económico.
```

---

# 17. Condiciones de re-decisión

La posición actual deberá revisarse si aparece evidencia relevante de alguno de los siguientes drivers.

## C1. Escalado independiente

Una capacidad demuestra un patrón de carga que hace necesario escalarla sin replicar todo el backend.

---

## C2. Saturación causalmente localizada

Una investigación reproducible identifica un recurso o capacidad específica como factor dominante de una limitación.

---

## C3. Despliegue independiente

Existe una necesidad real de desplegar o revertir una capacidad sin afectar el resto del backend.

---

## C4. Aislamiento de fallos

Una capacidad provoca consecuencias que podrían reducirse mediante aislamiento efectivo.

---

## C5. Persistencia

Una capacidad requiere:

- tecnología distinta;
- escalado diferente;
- aislamiento diferente;
- o ciclo de migración independiente.

---

## C6. Seguridad o regulación

Surge un requisito que obligue a aislar:

- datos;
- credenciales;
- procesos;
- privilegios.

---

## C7. Organización

Equipos realmente independientes necesitan ciclos autónomos de construcción, despliegue y operación.

---

## C8. Evidencia experimental

Un piloto distribuido demuestra beneficios relevantes frente al monolito modular optimizado.

La comparación debería considerar:

- rendimiento;
- disponibilidad;
- errores;
- complejidad;
- seguridad;
- recursos;
- operación;
- mantenimiento;
- costo relativo.

---

# 18. Estrategia de evolución

## Fase 1 — Diagnosticar

Mantener:

```text
monolito modular
```

mientras se reduce la incertidumbre mediante mejor observabilidad.

---

## Fase 2 — Fortalecer fronteras

Continuar aplicando:

```text
ownership
interfaces públicas
ArchUnit
fitness functions
```

para evitar acoplamiento indebido.

---

## Fase 3 — Identificar el driver

El orden de decisión debe ser:

```text
problema
↓
evidencia
↓
alternativas
↓
experimento
↓
decisión
```

No:

```text
tecnología
↓
buscar después un problema que la justifique
```

---

## Fase 4 — Piloto condicionado

Si aparece un driver suficiente, podría extraerse una capacidad como experimento controlado.

El piloto debe producir evidencia comparable.

---

## Fase 5 — Re-decidir

Comparar:

```text
monolito modular optimizado
vs.
alternativa parcialmente distribuida
```

y decidir:

```text
mantener
ampliar
revertir
```

según los resultados obtenidos.

---

# 19. Limitaciones de esta auditoría

No se implementó una arquitectura completa de cinco microservicios.

Por tanto, no existe comparación experimental directa:

```text
monolito modular
vs.
cinco microservicios
```

Tampoco se implementaron específicamente para A4:

- RabbitMQ;
- Kafka;
- Google Pub/Sub;
- Transactional Outbox distribuido;
- DLQ;
- mTLS;
- service mesh;
- Kubernetes;
- cinco instancias PostgreSQL;
- cinco pipelines de despliegue independientes;
- circuit breakers interservicio;
- bulkheads interservicio.

La campaña de crecimiento no identificó causalmente la fuente de degradación.

La inspección de seguridad fue principalmente:

- estructural;
- de configuración;
- de referencias de autorización.

No fue:

- pentesting;
- auditoría ofensiva;
- certificación formal.

Los costos económicos exactos de la alternativa distribuida no fueron calculados.

La única relación cuantificada directamente utilizada en la comparación de distribución fue:

```text
1 -> 5 unidades backend desplegables
```

equivalente a:

```text
5× en el conteo de unidades desplegables
```

y únicamente en esa dimensión.

---

# 20. Resultado global de la auditoría de IA

La arquitectura distribuida propuesta por IA es técnicamente coherente como posible dirección futura.

La propia respuesta reconoce:

- fallas parciales;
- incremento de complejidad;
- necesidad de observabilidad;
- costos operativos;
- necesidad de evidencia;
- conveniencia actual de mantener el monolito.

Por tanto, esta auditoría no concluye:

> **la propuesta de IA es incorrecta.**

La conclusión es:

> **una parte importante de la infraestructura distribuida propuesta solo se vuelve necesaria o justificable después de demostrar la necesidad de convertir las fronteras lógicas actuales en fronteras físicas.**

Elementos como:

```text
API Gateway dedicado
RabbitMQ
Transactional Outbox
tokens servicio-servicio
TLS interservicio
tracing distribuido
pipelines por servicio
persistencia independiente
orquestación
```

mantienen su clasificación individual como:

```text
CONDICIONADOS
```

o:

```text
SIN EVIDENCIA SUFICIENTE
```

según corresponda.

No se convierten en obligaciones actuales por aparecer en la arquitectura objetivo.

En cambio, recomendaciones como:

- mejorar observabilidad;
- reforzar ownership;
- controlar dependencias;
- mantener seguridad;
- medir fallos;
- mejorar diagnóstico;
- conservar capacidad de evolución;

pueden aportar valor sin distribuir físicamente el backend.

---

# 21. Conclusión del equipo

Después de contrastar la propuesta de IA con:

- código;
- ADR;
- evidencia experimental;
- costos relativos;
- estructura modular;
- configuración inspeccionada;
- y limitaciones conocidas;

el equipo concluye:

> **RachaPro no presenta actualmente evidencia suficiente para justificar una migración completa desde su monolito modular hacia cinco microservicios independientes.**

La campaña de crecimiento de Semana 11 observó degradación relativa de latencia bajo determinadas condiciones de concurrencia.

Sin embargo:

> **el experimento no identificó causalmente qué componente o recurso produjo esa degradación.**

Además:

> **no se realizó una comparación experimental equivalente entre el monolito modular y cinco microservicios.**

Por tanto, la evidencia no permite concluir que:

- el monolito sea la causa;
- los microservicios sean la solución;
- o la distribución reduzca el P95.

RachaPro ya posee capacidades funcionales diferenciadas y ha avanzado en:

- ownership;
- contratos;
- restricciones estructurales;
- seguridad;
- experimentación;
- definición de fitness functions.

Una arquitectura distribuida podría habilitar:

- escalado independiente;
- despliegue independiente;
- aislamiento de fallos;
- independencia de persistencia.

Sin embargo, esos beneficios continúan siendo potenciales para el proyecto.

No han sido demostrados como necesidades actuales.

La distribución también introduciría responsabilidades adicionales sobre:

- red;
- fallas parciales;
- seguridad interservicio;
- observabilidad distribuida;
- persistencia;
- consistencia;
- CI/CD;
- despliegue;
- operación.

Por tanto, la posición actual del equipo es:

> **mantener el monolito modular como arquitectura de distribución actual y continuar fortaleciendo modularidad, observabilidad, seguridad y resiliencia antes de introducir distribución física.**

Esta posición no descarta los microservicios.

Establece que:

> **la distribución debe responder a una necesidad demostrada y no únicamente a la disponibilidad del patrón arquitectónico.**

La conclusión coincide parcialmente con la recomendación final de la IA, pero no se adopta por autoridad de la herramienta.

Se sostiene mediante contraste posterior con evidencia del proyecto.

En resumen:

```text
MICROSERVICIOS:
alternativa técnicamente viable

MIGRACIÓN COMPLETA ACTUAL:
no justificada por la evidencia disponible

MONOLITO MODULAR:
arquitectura de distribución seleccionada actualmente

RE-DECISIÓN:
abierta y condicionada a nueva evidencia
```

---

# 22. Trazabilidad Git de la Actividad 4

La secuencia de Git busca demostrar que la propuesta de IA fue preservada antes de registrar la evaluación crítica.

## 22.1. Preservación inicial de evidencia de IA

Commit:

```text
645702a
```

Mensaje:

```text
S12 A4: conservar prompt y respuesta original de IA
```

En ese momento se registró la respuesta original y se creó la estructura documental correspondiente.

Sin embargo, la trazabilidad posterior mostró que el contenido completo del prompt todavía no había quedado incorporado correctamente en esa versión.

Por tanto, **no se atribuye a `645702a` la preservación definitiva del contenido completo del prompt**.

---

## 22.2. Corrección de trazabilidad del prompt

Commit:

```text
72448c5
```

Mensaje:

```text
S12 A4: completar trazabilidad del prompt original de IA
```

Este commit incorpora y publica el contenido completo de:

```text
docs/semana12/uso-ia/prompt-original.md
```

La verificación local posterior mostró:

```text
107 líneas
```

para el prompt.

La secuencia relevante queda:

```text
645702a
    ↓
preservación inicial de respuesta/evidencia IA

72448c5
    ↓
completitud y trazabilidad del prompt original

commit posterior
    ↓
auditoría crítica del equipo
```

---

## 22.3. Auditoría crítica

Archivo:

```text
docs/semana12/uso-ia/auditoria-microservicios.md
```

Estado al redactar esta versión:

```text
COMMIT PENDIENTE
```

Mensaje previsto:

```text
S12 A4: registrar auditoria critica de propuesta IA
```

El hash definitivo:

> **no debe incorporarse antes de ejecutar realmente el commit.**

Después de crearlo deberá comprobarse con:

```text
git log -1 --oneline
```

---

# 23. Estado de cierre de la Actividad 4

El documento consolida seis bloques principales:

```text
1. separación en cinco microservicios
2. observabilidad
3. comunicación distribuida y broker
4. persistencia
5. seguridad distribuida
6. resiliencia y tolerancia a fallos
```

También contiene:

- objeto y alcance;
- separación entre IA y decisión del equipo;
- fuentes concretas;
- método de clasificación;
- reglas metodológicas;
- matriz consolidada;
- complejidad adicional;
- afirmaciones no respaldadas;
- condiciones de re-decisión;
- estrategia de evolución;
- limitaciones;
- conclusión del equipo;
- trazabilidad Git.

Las cifras experimentales de Semana 11 quedan vinculadas a fuentes concretas del repositorio:

```text
experimentos/semana11-crecimiento-post-activities/00-preregistro.md

experimentos/semana11-crecimiento-post-activities/resultados-crecimiento.md

experimentos/semana11-crecimiento-post-activities/matrices-fallos-resiliencia.md

experimentos/semana11-crecimiento-post-activities/resultados-spike-timeout.md
```

y la intervención del timeout queda asociada a:

```text
app/src/main/java/com/example/rachapro/network/RetrofitClient.kt
```

Por tanto, el contenido de la auditoría queda preparado para cierre documental.

La Actividad 4 quedará formalmente registrada cuando:

1. esta versión se encuentre guardada en el repositorio local;
2. `git diff --cached --check` no reporte problemas;
3. únicamente `auditoria-microservicios.md` quede preparado;
4. se cree el commit correspondiente;
5. se publique en el remoto;
6. se verifique el hash resultante y el estado del repositorio.

Hasta completar esos pasos:

```text
CONTENIDO DE A4:
CONSOLIDADO

COMMIT FINAL DE A4:
PENDIENTE
```