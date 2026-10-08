# ADR-003 — Evento interno para el aprovisionamiento post-registro

- **Estado:** Aceptada
- **Fecha de decisión:** 2026-09-30
- **Ámbito:** Backend Spring Boot
- **Relacionada con:** ADR-001 y ADR-002
- **Evidencia principal:** `experimentos/spike-01-integracion/`
- **Implementación:** Materializada mediante Spike 1

---

## 1. Contexto

RachaPro utiliza un monolito modular compuesto por los contextos principales:

    Identity
    Activities
    Focus
    Progress
    Reminders

ADR-001 adoptó el monolito modular como estilo arquitectónico del backend.

ADR-002 estableció ownership lógico de persistencia y contratos públicos entre módulos.

En particular, ADR-002 definió inicialmente que Identity solicitaría a Activities la creación de categorías predeterminadas mediante el contrato síncrono:

    DefaultCategoryProvisioning

La implementación síncrona preservaba la operación existente durante la materialización inicial de las fronteras modulares.

ADR-002 también estableció explícitamente que una modificación futura hacia procesamiento asíncrono o consistencia eventual requeriría una nueva decisión arquitectónica sustentada en evidencia.

Semana 9 definió un Spike para evaluar precisamente esa alternativa.

---

## 2. Problema

Cuando se registra un usuario, Activities debe crear sus categorías predeterminadas.

La solución anterior generaba la relación:

    Identity
       |
       | llamada síncrona
       v
    DefaultCategoryProvisioning
       |
       v
    Activities

Aunque la dependencia atravesaba una API pública válida y no violaba el ownership de persistencia, Identity continuaba coordinando directamente una operación perteneciente a Activities.

La pregunta arquitectónica evaluada fue:

> ¿Puede Identity comunicar el hecho de que un usuario fue registrado sin solicitar directamente a Activities el aprovisionamiento de categorías, manteniendo el comportamiento funcional esperado?

---

## 3. Drivers de la decisión

Para esta decisión se consideran relevantes:

- desacoplamiento entre módulos;
- ownership de responsabilidades;
- preservación del registro de usuario;
- simplicidad operacional;
- trazabilidad;
- mantenibilidad;
- facilidad de pruebas;
- impacto transaccional;
- consistencia de datos;
- reversibilidad;
- complejidad introducida.

La decisión debe permanecer compatible con el monolito modular establecido en ADR-001.

No existe una necesidad demostrada de introducir infraestructura distribuida de mensajería.

---

## 4. Alternativas consideradas

### 4.1 Mantener DefaultCategoryProvisioning síncrono

Identity continúa invocando directamente el contrato público de Activities.

#### Ventajas

- semántica sencilla;
- ejecución inmediata;
- ausencia de consistencia eventual;
- depuración directa;
- mecanismo ya materializado por ADR-002.

#### Costos

- Identity continúa coordinando explícitamente una operación de Activities;
- existe acoplamiento temporal entre ambos módulos;
- el registro depende de completar el aprovisionamiento dentro del mismo flujo.

Esta alternativa continúa siendo válida técnicamente, pero el Spike 1 evaluó si el acoplamiento podía reducirse sin afectar el comportamiento requerido.

---

### 4.2 Evento interno síncrono

Identity publica un evento de dominio o integración dentro del mismo proceso y Activities lo consume sin ejecución asíncrona.

#### Ventajas

- expresa explícitamente un hecho ocurrido;
- reduce conocimiento directo del consumidor;
- permite agregar consumidores posteriormente.

#### Costos

- mantiene acoplamiento temporal;
- el productor continúa esperando el procesamiento;
- aporta menor separación respecto a la alternativa experimental evaluada.

Esta alternativa no corresponde al mecanismo probado por el Spike 1.

---

### 4.3 Evento interno asíncrono después del commit

Identity persiste el usuario y publica:

    UserRegisteredV1

Activities reacciona al evento mediante un listener ejecutado después del commit.

El mecanismo probado utiliza:

    @TransactionalEventListener(AFTER_COMMIT)
    @Async

#### Ventajas

- Identity deja de solicitar directamente el aprovisionamiento;
- Activities conserva ownership sobre las categorías;
- el evento representa un hecho ya ocurrido;
- el registro del usuario no espera la creación de categorías;
- no requiere broker externo;
- mantiene una única aplicación desplegable;
- el cambio es reversible.

#### Costos

- introduce consistencia eventual;
- introduce ejecución asíncrona;
- requiere considerar fallos posteriores al commit;
- la depuración es menos directa;
- el evento no posee persistencia durable;
- un fallo del consumidor después del commit no revierte la creación del usuario.

Esta es la alternativa evaluada mediante el Spike 1.

---

### 4.4 Broker externo

Otra alternativa sería utilizar infraestructura como Kafka, RabbitMQ, Pub/Sub u otro broker.

No existe evidencia actual que justifique introducir:

- infraestructura adicional;
- operación de un broker;
- entrega distribuida;
- serialización externa;
- reintentos distribuidos;
- observabilidad adicional.

**Decisión:** no adoptada para este caso.

---

## 5. Evidencia experimental

La decisión se apoya en:

    experimentos/spike-01-integracion/README.md
    experimentos/spike-01-integracion/resultados.csv

El Spike 1 fue ejecutado el 2026-09-30.

La hipótesis evaluada fue comprobar si el registro podía desacoplar la dependencia directa de Identity hacia Activities mediante `UserRegisteredV1`, manteniendo el comportamiento funcional esperado.

El criterio experimental estableció una ventana local máxima de:

    2000 ms

para disponer de las categorías predeterminadas.

---

## 6. Resultados del Spike 1

Se realizaron cuatro ejecuciones.

| Corrida | HTTP | Evento ms | Categorías exactas | <= 2000 ms | Resultado |
|---|---:|---:|---|---|---|
| 01 | 201 | 28 | PASS | PASS | PASS |
| 02 | 201 | 65 | PASS | PASS | PASS |
| 03 | 201 | 36 | PASS | PASS | PASS |
| 04 | 201 | 32 | PASS | PASS | PASS |

La medición HTTP de la primera corrida no se considera comparable debido a una confirmación interactiva de PowerShell durante `Invoke-WebRequest`.

La medición del evento de dicha corrida sí fue registrada.

Tiempos observados del evento:

    28, 65, 36, 32 ms

Estadísticas:

- mínimo: 28 ms;
- promedio: 40,25 ms;
- mediana: 34 ms;
- máximo: 65 ms;
- cumplimiento del umbral: 4/4.

Las categorías verificadas fueron:

- Personal;
- Trabajo;
- Universidad.

No se observaron categorías faltantes en las cuatro ejecuciones verificadas.

---

## 7. Validación estructural

Después de la ejecución funcional se verificó:

### ArchUnit

    PASS
    BUILD SUCCESSFUL

### Suite completa del backend

    PASS
    BUILD SUCCESSFUL

### git diff --check

    PASS

Por tanto, el experimento no introdujo una violación detectada por las fitness functions existentes ni una regresión detectada por la suite ejecutada.

---

## 8. Decisión

Se adopta `UserRegisteredV1` como mecanismo interno para comunicar desde Identity hacia Activities que un nuevo usuario ha sido registrado.

El flujo adoptado es:

    Identity
       |
       | guarda usuario
       |
       | publica UserRegisteredV1
       v
    commit de la transacción
       |
       v
    Activities
       |
       | AFTER_COMMIT + @Async
       v
    creación de categorías predeterminadas

Identity deja de invocar directamente `DefaultCategoryProvisioning` para este flujo.

Activities continúa siendo propietario de:

- categorías;
- reglas de creación de categorías;
- persistencia de categorías.

El evento comunica un hecho ocurrido y no transfiere ownership de negocio hacia Identity.

---

## 9. Alcance de la decisión

Esta decisión se aplica específicamente a:

    Identity -> UserRegisteredV1 -> Activities

para el aprovisionamiento de categorías posterior al registro.

No establece que todas las relaciones intermodulares deban utilizar eventos.

En particular, permanecen síncronas las capacidades que requieren una respuesta inmediata.

Actualmente:

    Focus -> ActivityLookup
    Reminders -> ActivityLookup

continúan utilizando el contrato público síncrono de Activities.

---

## 10. Relación con ADR-002

ADR-002 continúa vigente.

Se mantienen sus decisiones sobre:

- ownership lógico de persistencia;
- prohibición de acceder a repositories de otros módulos;
- prohibición de acceder a entidades internas de otros módulos;
- uso de APIs públicas;
- referencias mediante identificadores escalares;
- protección mediante fitness functions;
- alcance restringido de Shared.

ADR-003 modifica únicamente la decisión correspondiente al aprovisionamiento de categorías después del registro.

La relación:

    Identity -> DefaultCategoryProvisioning -> Activities

es reemplazada para este flujo por:

    Identity -> UserRegisteredV1 -> Activities

Por tanto, la regla de ADR-002 que mantenía este flujo síncrono queda superada únicamente para este caso específico.

---

## 11. Consistencia eventual

La decisión introduce una ventana de consistencia eventual entre:

1. el commit del nuevo usuario;
2. la creación de sus categorías predeterminadas.

El Spike 1 observó una ventana de entre 28 y 65 ms bajo las condiciones locales probadas.

Estos valores no constituyen una garantía para producción.

El criterio experimental utilizado fue de 2000 ms y se cumplió en las cuatro ejecuciones.

La API y los consumidores no deben asumir atomicidad entre la creación del usuario y la disponibilidad inmediata de sus categorías.

---

## 12. CQRS

El Spike 1 no demuestra una necesidad de adoptar CQRS.

RachaPro no separa actualmente de forma arquitectónica general los modelos de comandos y consultas.

La introducción de `UserRegisteredV1` resuelve un caso específico de integración entre módulos y no constituye por sí sola CQRS.

Por tanto:

**CQRS no se adopta como parte de ADR-003.**

Podrá reconsiderarse únicamente si aparece una necesidad concreta y medible que justifique la separación adicional de modelos de lectura y escritura.

---

## 13. Consecuencias positivas

La decisión produce:

- menor coordinación directa desde Identity hacia Activities;
- ownership de categorías conservado en Activities;
- comunicación basada en un hecho de negocio explícito;
- reducción del acoplamiento temporal en este flujo;
- ausencia de infraestructura externa adicional;
- conservación de un único despliegue;
- reversibilidad sin migración del modelo de datos.

---

## 14. Consecuencias negativas

La decisión introduce:

- asincronía;
- consistencia eventual;
- mayor complejidad de diagnóstico;
- posibilidad de fallo del consumidor después del commit;
- ausencia de persistencia durable del evento;
- necesidad de considerar recuperación o reintento si el riesgo lo exige posteriormente.

El éxito del Spike no elimina estas consecuencias.

---

## 15. Riesgos

### R-01 — Fallo del consumidor después del commit

El usuario puede quedar creado aunque el aprovisionamiento posterior falle.

El Spike 1 no ejecutó una prueba específica de fallo intencional del consumidor.

### R-02 — Pérdida del evento ante fallo del proceso

El mecanismo actual utiliza eventos internos de Spring y no un broker durable.

No existe evidencia de recuperación automática del evento después de una caída del proceso.

### R-03 — Generalización injustificada

Validar `UserRegisteredV1` no demuestra que todas las relaciones entre módulos deban convertirse en eventos.

### R-04 — Dependencia de disponibilidad inmediata

Un consumidor que consulte las categorías inmediatamente después del registro puede observar temporalmente un estado aún no convergido.

---

## 16. Mitigaciones

Se establecen las siguientes medidas:

- mantener el alcance del evento limitado al flujo validado;
- conservar contratos síncronos cuando sea necesaria una respuesta inmediata;
- mantener ArchUnit y las pruebas del backend en CI;
- documentar explícitamente la consistencia eventual;
- medir nuevamente si cambian las condiciones operativas;
- evaluar reintentos, idempotencia o persistencia durable únicamente si evidencia futura demuestra su necesidad.

---

## 17. Limitaciones de la evidencia

El Spike 1 fue ejecutado localmente.

No se evaluó:

- Kafka;
- RabbitMQ;
- Pub/Sub;
- brokers externos;
- comunicación entre microservicios;
- persistencia durable del evento;
- caída intencional del consumidor;
- recuperación automática;
- carga concurrente específica sobre este flujo;
- comportamiento en producción.

Por tanto, ADR-003 no puede utilizarse como evidencia de escalabilidad, tolerancia general a fallos o necesidad de arquitectura distribuida.

---

## 18. Criterios de re-decisión

La decisión deberá revisarse si aparece evidencia de:

- categorías faltantes después del registro;
- fallos relevantes del listener;
- necesidad de entrega durable;
- necesidad de reintentos garantizados;
- múltiples consumidores con requisitos independientes;
- tiempos de convergencia incompatibles con el comportamiento esperado;
- necesidad demostrada de desacoplamiento entre procesos;
- necesidad de despliegue independiente.

La existencia futura de alguno de estos requisitos no implica automáticamente adoptar un broker o microservicios.

Deberá realizarse una nueva evaluación basada en evidencia.

---

## 19. Fuera del alcance

ADR-003 no decide:

- adoptar microservicios;
- adoptar Kafka;
- adoptar RabbitMQ;
- adoptar Pub/Sub;
- adoptar CQRS;
- convertir todas las integraciones a eventos;
- separar bases de datos por módulo;
- garantizar entrega durable;
- garantizar escalabilidad;
- eliminar los contratos síncronos existentes.

---

## 20. Estado de implementación

**Decisión:** ACEPTADA

**Spike 1:** VALIDADO

**UserRegisteredV1:** IMPLEMENTADO

**Listener en Activities:** IMPLEMENTADO

**Procesamiento AFTER_COMMIT:** IMPLEMENTADO

**Procesamiento asíncrono:** IMPLEMENTADO

**Pruebas funcionales:** PASS

**ArchUnit:** PASS

**Suite backend:** PASS

**Persistencia durable del evento:** NO IMPLEMENTADA

**Broker externo:** NO IMPLEMENTADO

**CQRS:** NO ADOPTADO

La decisión queda respaldada por la evidencia del Spike 1 y limitada expresamente a las condiciones y alcance documentados.

---

## 21. Revisión posterior de evidencia — 2026-10-08

### 21.1 Naturaleza de esta revisión

ADR-003 fue decidido y registrado originalmente el 2026-09-30.

La decisión original se conserva en el commit:

```text
c6ae059
docs(semana10): registra ADR-003 de integracion por eventos
```

Esta sección fue incorporada posteriormente con el propósito de registrar evidencia, documentación y análisis producidos después de la decisión original.

Por tanto, esta revisión:

- no modifica retrospectivamente la fecha de ADR-003;
- no afirma que la evidencia posterior estuviera disponible el 2026-09-30;
- no sustituye la evidencia utilizada para tomar la decisión original;
- no convierte los análisis posteriores en justificación causal de la decisión original;
- permite evaluar si evidencia obtenida después confirma, cuestiona o activa los criterios de re-decisión ya documentados.

La separación temporal utilizada es:

```text
evidencia original
        ↓
SPIKE-01
        ↓
ADR-003
        ↓
evidencia posterior
        ↓
revisión posterior de ADR-003
```

y no:

```text
evidencia posterior
        ↓
ADR-003 original
```

---

### 21.2 Trazabilidad original reforzada

La evidencia experimental utilizada originalmente para ADR-003 corresponde a SPIKE-01.

El cierre de dicha evidencia se encuentra en:

```text
597e9c7
feat(semana9): valida spike de integracion por eventos
```

Los artefactos principales son:

```text
experimentos/spike-01-integracion/README.md
experimentos/spike-01-integracion/resultados.csv
```

SPIKE-01 fue registrado antes de ADR-003.

La secuencia temporal verificada es:

```text
597e9c7
SPIKE-01
        ↓
c6ae059
ADR-003
```

SPIKE-01 evaluó si el registro podía desacoplar la dependencia directa de Identity hacia Activities mediante:

```text
UserRegisteredV1
```

manteniendo el comportamiento funcional esperado.

El criterio experimental utilizó una ventana local máxima de:

```text
2000 ms
```

y las cuatro corridas documentadas cumplieron el criterio definido.

Por tanto:

```text
SPIKE-01
→ evidencia experimental previa a ADR-003

ADR-003
→ decisión arquitectónica posterior a dicha evidencia
```

La incorporación de nueva evidencia no altera esta relación histórica.

---

### 21.3 Evolución del Context Map y relación temporal con ADR-003

La representación PlantUML del Context Map se encuentra en:

```text
docs/dominio/context-map.puml
```

El historial Git observado para este archivo incluye:

```text
8d23268
2026-09-13T19:35:06-05:00
feat(exp): validate activities pagination performance

4b0acd7
2026-10-05T13:07:52-05:00
docs(domain): consolidar responsabilidades por contexto

64aeb42
2026-10-05T13:26:33-05:00
docs(domain): agregar context map de bounded contexts
```

ADR-003 fue registrado en:

```text
c6ae059
2026-09-30T17:41:34-05:00
docs(semana10): registra ADR-003 de integracion por eventos
```

La relación de ancestros verificada mediante Git es:

```text
8d23268
        ↓
c6ae059
```

porque:

```text
git merge-base --is-ancestor 8d23268 c6ae059
→ exit code 0
```

La relación inversa fue comprobada con:

```text
git merge-base --is-ancestor c6ae059 8d23268
→ exit code 1
```

Por tanto, una versión de `context-map.puml` ya existía antes de la decisión original de ADR-003.

Sin embargo, la existencia previa del archivo no demuestra por sí sola que el Context Map hubiera sido utilizado como evidencia causal para tomar ADR-003.

En la documentación histórica de ADR-003, la evidencia principal declarada es:

```text
experimentos/spike-01-integracion/
```

Por tanto, la existencia previa de `context-map.puml` no se utiliza para reconstruir retrospectivamente una causalidad que no está demostrada en los artefactos revisados.

Después de ADR-003, el Context Map fue refinado en:

```text
4b0acd7
64aeb42
```

ambos del 2026-10-05.

La representación renderizada:

```text
docs/dominio/context-map.png
```

aparece en el historial observado en:

```text
64aeb42
2026-10-05T13:26:33-05:00
docs(domain): agregar context map de bounded contexts
```

Por tanto, para esta revisión se distingue entre:

```text
context-map.puml inicial
→ existente antes de ADR-003

refinamientos de context-map.puml
→ posteriores a ADR-003

context-map.png
→ posterior a ADR-003

uso causal del Context Map en ADR-003
→ NO DEMOSTRADO
```

Los refinamientos posteriores del Context Map se utilizan como trazabilidad adicional para revisar las fronteras y dependencias entre contextos, pero no como justificación retrospectiva de la decisión tomada el 2026-09-30.

La existencia de una relación en el Context Map tampoco determina por sí misma el mecanismo técnico de integración.

Por tanto:

```text
relación entre contextos
≠
evento obligatorio

relación entre contextos
≠
asincronía obligatoria

relación entre contextos
≠
microservicio
```

ADR-003 continúa limitado al caso específico:

```text
Identity
    ↓
UserRegisteredV1
    ↓
Activities
```

para el aprovisionamiento posterior al registro.

---

### 21.4 Formalización AsyncAPI posterior

Posteriormente se añadió una formalización AsyncAPI del contrato utilizado por la integración:

```text
docs/asyncapi/rachapro-events-v1.yaml
```

El artefacto fue registrado en:

```text
65e39aa
docs(extra): documenta resiliencia y contrato AsyncAPI
```

El contrato documenta:

```text
UserRegisteredV1
```

junto con la relación de publicación y consumo correspondiente al flujo:

```text
Identity
→ publica UserRegisteredV1

Activities
→ consume UserRegisteredV1
```

También documenta características del mecanismo implementado, entre ellas:

```text
AFTER_COMMIT
durable: false
externalBroker: false
```

Esta formalización fue creada después de ADR-003.

Por tanto:

```text
implementación y decisión
→ anteriores

formalización AsyncAPI
→ posterior
```

El AsyncAPI mejora la trazabilidad y explicitud del contrato existente, pero no se interpreta como evidencia que hubiera participado en la decisión original del 2026-09-30.

---

### 21.5 Evidencia posterior de resiliencia — SPIKE-02

La resiliencia del mecanismo fue evaluada posteriormente en:

```text
experimentos/spike-02-resiliencia/README.md
```

dentro del commit:

```text
65e39aa
docs(extra): documenta resiliencia y contrato AsyncAPI
```

SPIKE-02 evaluó qué ocurría cuando el consumidor de:

```text
UserRegisteredV1
```

fallaba después de confirmarse la transacción que registra al usuario.

Bajo las condiciones probadas se observó que el usuario podía quedar persistido mientras el efecto secundario correspondiente al consumidor quedaba incompleto durante el fallo evaluado.

Además:

```text
no se observó retry automático
durante los 5 segundos posteriores
a retirar el fallo
```

y:

```text
no se observó replay de UserRegisteredV1
durante los 5 segundos posteriores
al reinicio del backend
```

Estas observaciones se encuentran acotadas a las condiciones y ventanas temporales del experimento.

Por tanto, la evidencia permite afirmar:

```text
retry automático observado en esa ventana
→ NO

replay observado en esa ventana
→ NO
```

pero no permite afirmar universalmente:

```text
retry nunca puede ocurrir
```

ni:

```text
replay nunca puede existir
```

SPIKE-02 aporta evidencia posterior relevante para riesgos que ADR-003 ya había identificado respecto de:

- fallos posteriores al commit;
- consistencia eventual;
- recuperación;
- necesidad potencial de reintentos;
- persistencia durable.

No modifica retrospectivamente la evidencia disponible cuando ADR-003 fue decidido.

---

### 21.6 Evaluación posterior de eventos candidatos

Posteriormente se realizó una evaluación de eventos candidatos documentada en:

```text
docs/integracion/eventos-candidatos.md
```

El documento está trazado al commit:

```text
e03d9b1
docs(integration): auditar eventos candidatos con IA
```

El análisis mantuvo como candidatos para evaluación arquitectónica posterior, entre otros:

```text
ActivityCompleted
PomodoroSessionCompleted
UserRegisteredV1
```

Esta clasificación no significa que todos deban implementarse obligatoriamente ni que exista una política general de integración por eventos.

`UserRegisteredV1` posee una situación distinta a candidatos meramente potenciales porque existe evidencia del repositorio de:

```text
existencia del evento
+
consumo por Activities
+
reacción concreta
```

Sin embargo:

```text
UserRegisteredV1 adoptado
≠
todos los hechos de dominio deben publicarse
```

y:

```text
evento aceptado para evaluación
≠
evento arquitectónicamente adoptado
```

Por tanto, este análisis posterior limita y contextualiza ADR-003, pero no amplía retrospectivamente su alcance.

---

### 21.7 Evaluación posterior de CQRS / Event Sourcing

Posteriormente se documentó el análisis de aplicabilidad de CQRS y Event Sourcing en:

```text
docs/integracion/aplicabilidad-cqrs-event-sourcing.md
```

El documento está trazado al commit:

```text
d5f356f
docs(integration): analizar aplicabilidad de CQRS y Event Sourcing
```

El estado documentado para CQRS es:

```text
NO JUSTIFICADO TODAVÍA POR LA EVIDENCIA DISPONIBLE
```

El estado documentado para Event Sourcing es:

```text
NO JUSTIFICADO TODAVÍA POR LA EVIDENCIA DISPONIBLE
```

Esta formulación no significa:

```text
CQRS es incorrecto
```

ni:

```text
Event Sourcing es incorrecto
```

ni:

```text
RachaPro nunca debe utilizarlos
```

Significa únicamente que, con la evidencia disponible en el momento del análisis, no se había demostrado un problema o requisito suficiente para justificar las responsabilidades y complejidades adicionales de dichos patrones.

También se mantiene la separación conceptual:

```text
eventos de integración
≠
CQRS

eventos de integración
≠
Event Sourcing

CQRS
≠
Event Sourcing
```

Por tanto, la utilización de:

```text
UserRegisteredV1
```

en ADR-003 no constituye evidencia suficiente para adoptar ninguno de esos patrones.

La evaluación posterior sirve como límite de inferencia sobre ADR-003 y no como redefinición de su decisión original.

---

### 21.8 SPIKE-03

SPIKE-03 fue ejecutado después de la decisión original de ADR-003.

Su trazabilidad principal es:

```text
34234e4
preregistro definitivo
        ↓
4da5923
harness experimental medido
        ↓
9e51c0d
evidencia y resultados definitivos
        ↓
cd1c9b0
veredicto experimental
        ↓
59e8f59
integración del cierre en master
```

SPIKE-03 caracterizó la escalabilidad del cálculo actual de:

```text
currentStreak
bestStreak
```

en Progress a medida que aumentaba la cantidad de días históricos distintos procesados.

La hipótesis experimental fue respaldada bajo las condiciones preregistradas.

Las tres corridas formales válidas presentaron:

```text
median_10000 > median_30
```

y la regla direccional se cumplió en:

```text
3 de 3 corridas
```

La condición de resolución insuficiente no se activó.

El veredicto experimental quedó registrado como:

```text
Hipótesis experimental: RESPALDADA
```

mientras que la decisión arquitectónica quedó expresamente como:

```text
NO DETERMINADA
```

SPIKE-03 fue realizado después de:

```text
c6ae059
```

Por tanto:

```text
SPIKE-03
≠
evidencia causal de ADR-003 original
```

y no se utiliza para afirmar que la decisión del 2026-09-30 fue tomada con base en sus resultados.

SPIKE-03 aporta evidencia posterior y límites de inferencia.

En particular, su resultado no demuestra automáticamente que RachaPro necesite:

- CQRS;
- Event Sourcing;
- microservicios;
- integración generalizada mediante eventos;
- proyecciones independientes;
- cambios en `UserRegisteredV1`.

Su inclusión en esta revisión preserva la secuencia temporal real del repositorio.

---

### 21.9 Reversibilidad explícita

La reversibilidad descrita en esta subsección corresponde a un análisis arquitectónico del estado vigente y no a una medición experimental.

Volver al mecanismo síncrono implicaría, como mínimo:

1. sustituir para este flujo la publicación y consumo de `UserRegisteredV1`;
2. restablecer una invocación síncrona mediante una API pública de Activities;
3. volver a conectar Identity con el mecanismo equivalente a `DefaultCategoryProvisioning`;
4. retirar o dejar sin uso el listener correspondiente si deja de tener consumidores;
5. actualizar las pruebas asociadas al flujo;
6. actualizar la documentación del contrato;
7. revisar el AsyncAPI si `UserRegisteredV1` deja de formar parte de la integración vigente.

Como inferencia derivada del estado actualmente documentado, la reversión no requeriría por sí sola:

- migración de un event store;
- reconstrucción histórica mediante replay;
- separación o unificación de bases de datos;
- migración de datos entre microservicios;
- cambio de ownership de Category;
- transferencia de persistencia desde Activities hacia Identity.

La base de esta inferencia es que Activities continúa siendo propietario de las categorías y de su persistencia.

El costo de reversión podría aumentar si en el futuro aparecen:

- múltiples consumidores dependientes de `UserRegisteredV1`;
- contratos externos basados en el evento;
- persistencia durable del evento;
- proyecciones dependientes de su historial;
- procesos independientes;
- garantías de entrega asociadas al contrato.

Por tanto, la reversibilidad actual se interpreta respecto del estado vigente del sistema y no como garantía de costo constante ante evoluciones futuras.

---

### 21.10 Hechos, inferencias y supuestos

Para esta revisión se utiliza la siguiente distinción.

#### HECHOS

Se consideran hechos verificables en el repositorio y en la evidencia experimental revisada:

```text
HECHO
SPIKE-01 fue versionado en 597e9c7.
```

```text
HECHO
ADR-003 fue registrado posteriormente en c6ae059.
```

```text
HECHO
UserRegisteredV1 existe en el código productivo.
```

```text
HECHO
UserService publica UserRegisteredV1.
```

```text
HECHO
Activities posee UserRegisteredV1Listener.
```

```text
HECHO
el listener utiliza AFTER_COMMIT y @Async.
```

```text
HECHO
una versión de context-map.puml ya existía en 8d23268,
antes de ADR-003.
```

```text
HECHO
context-map.puml fue refinado posteriormente en
4b0acd7 y 64aeb42.
```

```text
HECHO
context-map.png aparece en el historial observado
en 64aeb42.
```

```text
HECHO
el contrato AsyncAPI posterior se encuentra en
docs/asyncapi/rachapro-events-v1.yaml.
```

```text
HECHO
SPIKE-02 no observó retry ni replay dentro de
las ventanas temporales documentadas.
```

```text
HECHO
SPIKE-03 respaldó su hipótesis experimental bajo
las condiciones preregistradas.
```

```text
HECHO
SPIKE-03 dejó la decisión arquitectónica como
NO DETERMINADA.
```

#### INFERENCIAS

Se consideran inferencias arquitectónicas:

```text
INFERENCIA
UserRegisteredV1 reduce la coordinación directa
de Identity sobre una operación perteneciente a Activities.
```

```text
INFERENCIA
la ausencia de un broker externo reduce complejidad
operativa respecto de introducir infraestructura distribuida.
```

```text
INFERENCIA
la reversión al mecanismo síncrono no requeriría,
bajo el estado actualmente documentado,
migrar un event store o separar bases de datos.
```

```text
INFERENCIA
la existencia de múltiples consumidores futuros podría
incrementar el costo de reversión.
```

```text
INFERENCIA
los refinamientos posteriores del Context Map son útiles
para revisar la coherencia de las fronteras documentadas,
pero no demuestran que el Context Map haya causado ADR-003.
```

Estas inferencias corresponden a razonamiento arquitectónico derivado de la estructura y estado documentado del sistema, no a métricas obtenidas directamente de los experimentos.

#### SUPUESTOS

Se consideran supuestos actuales:

```text
SUPUESTO
la consistencia eventual sigue siendo aceptable
para el aprovisionamiento posterior al registro.
```

```text
SUPUESTO
no se requiere actualmente entrega durable
para UserRegisteredV1.
```

```text
SUPUESTO
no existe actualmente una necesidad demostrada
de utilizar un broker externo para este flujo.
```

```text
SUPUESTO
el número y naturaleza actuales de consumidores
no hacen necesaria una estrategia de replay.
```

```text
SUPUESTO
el monolito modular continúa siendo el contexto
de despliegue aplicable a esta decisión.
```

---

### 21.11 Supuestos y condiciones de revisión

| Supuesto | Evidencia actual | Condición observable de revisión |
|---|---|---|
| La consistencia eventual continúa siendo aceptable para el aprovisionamiento posterior al registro. | SPIKE-01 cumplió el criterio experimental definido; ADR-003 documentó la ventana de consistencia eventual. | Se observan categorías faltantes, tiempos de convergencia incompatibles con el flujo esperado o un requisito funcional exige disponibilidad inmediata después del registro. |
| No se requiere entrega durable para `UserRegisteredV1`. | El contrato actual documenta `durable: false`; no se encontró en las fuentes revisadas un requisito explícito de entrega durable para este flujo. | Se observan pérdidas del efecto secundario que, bajo un requisito o escenario vigente, requieran recuperación garantizada, o aparece un requisito explícito de entrega durable. |
| No se requiere retry garantizado actualmente. | SPIKE-02 no observó retry automático en la ventana de 5 segundos evaluada; no se encontró en las fuentes revisadas un requisito explícito de retry garantizado para este flujo. | Aparecen fallos del consumidor cuyo impacto sea incompatible con un requisito vigente y cuya recuperación requiera reintentos automáticos verificables. |
| No se requiere replay garantizado actualmente. | SPIKE-02 no observó replay durante los 5 segundos posteriores al reinicio evaluado; no se encontró en las fuentes revisadas un requisito explícito de replay para este flujo. | Se establece un requisito de recuperación posterior a reinicio o reconstrucción de efectos secundarios perdidos. |
| No se requiere broker externo para este flujo. | El sistema mantiene la interacción dentro de una única aplicación Spring Boot y el mecanismo interno cumple el alcance funcional actualmente documentado. | Identity y Activities requieren procesos o despliegues independientes, entrega durable, múltiples consumidores independientes o garantías de mensajería que el mecanismo interno no proporciona. |
| La decisión puede continuar limitada a `UserRegisteredV1`. | La auditoría posterior de eventos candidatos no adopta automáticamente otros eventos. | Una interacción adicional cuenta con problema concreto, alternativas evaluadas y evidencia suficiente para justificar una nueva decisión arquitectónica. |
| CQRS continúa sin estar justificado por la evidencia disponible. | El análisis de aplicabilidad no demuestra todavía un problema que requiera separar modelos de comando y lectura. | Se mide un problema concreto de lectura/escritura cuya resolución requiera evaluar explícitamente una separación de modelos. |
| Event Sourcing continúa sin estar justificado por la evidencia disponible. | No se encontró una necesidad demostrada de replay completo, event store como fuente de verdad o reconstrucción de estado desde eventos. | Aparece un requisito verificable de reconstrucción histórica, auditoría completa de transiciones o rehidratación desde un event log. |

---

### 21.12 Fuentes verificables

| Afirmación / artefacto | Archivo / referencia | Commit |
|---|---|---|
| Evidencia experimental original de ADR-003 | `experimentos/spike-01-integracion/README.md` | `597e9c7` |
| Resultados de SPIKE-01 | `experimentos/spike-01-integracion/resultados.csv` | `597e9c7` |
| Decisión original ADR-003 | `docs/adr/0003-integracion-eventos-internos.md` | `c6ae059` |
| Context Map fuente PlantUML | `docs/dominio/context-map.puml` | versión previa a ADR-003 en `8d23268`; refinamientos posteriores en `4b0acd7` y `64aeb42` |
| Context Map renderizado PNG | `docs/dominio/context-map.png` | `64aeb42` |
| Relación temporal Context Map / ADR-003 | historial Git de `context-map.puml` | `8d23268` es ancestro de `c6ae059`; `4b0acd7` y `64aeb42` son posteriores |
| Contrato AsyncAPI | `docs/asyncapi/rachapro-events-v1.yaml` | `65e39aa` |
| Evidencia de resiliencia | `experimentos/spike-02-resiliencia/README.md` | `65e39aa` |
| Evaluación de eventos candidatos | `docs/integracion/eventos-candidatos.md` | `e03d9b1` |
| Evaluación CQRS / Event Sourcing | `docs/integracion/aplicabilidad-cqrs-event-sourcing.md` | `d5f356f` |
| Preregistro SPIKE-03 | `experimentos/spike-03-caracterizacion-racha/00-preregistro.md` | `34234e4` |
| Harness medido SPIKE-03 | artefactos ejecutables del SPIKE-03 | `4da5923` |
| Resultados definitivos SPIKE-03 | evidencia y resultados versionados del SPIKE-03 | `9e51c0d` |
| Veredicto experimental SPIKE-03 | veredicto versionado del SPIKE-03 | `cd1c9b0` |
| Integración del cierre de SPIKE-03 en `master` | historial Git | `59e8f59` |

---

### 21.13 Resultado de la revisión posterior

La revisión posterior distingue entre la validez histórica de la decisión tomada en `c6ae059` y la nueva evidencia incorporada después de esa decisión.

La evidencia de SPIKE-02 demuestra que, bajo un fallo intencional del consumidor posterior al commit, el aprovisionamiento de categorías puede quedar incompleto y que no se observó retry automático durante los 5 segundos posteriores a retirar el fallo ni replay durante los 5 segundos posteriores al reinicio del backend.

Esta observación aporta evidencia concreta sobre riesgos que ADR-003 ya había identificado para el mecanismo asíncrono.

Entre los criterios de re-decisión definidos originalmente se encuentran:

```text
categorías faltantes después del registro
fallos relevantes del listener
necesidad de entrega durable
necesidad de reintentos garantizados
```

SPIKE-02 aporta evidencia directamente relacionada con:

```text
categorías faltantes después del registro
fallos relevantes del listener
```

porque el experimento provocó un fallo del consumidor después del commit y observó que el efecto posterior podía quedar incompleto bajo las condiciones ensayadas.

En cambio:

```text
necesidad de entrega durable
necesidad de reintentos garantizados
```

continúan siendo necesidades arquitectónicas potenciales cuya existencia no queda demostrada únicamente por observar el modo de fallo.

SPIKE-02 permite caracterizar limitaciones del mecanismo relevantes para evaluar esos criterios, pero no demuestra por sí mismo que dichas necesidades estén activadas.

En las fuentes revisadas no se encontró un requisito explícito que establezca que, después de un fallo post-commit del consumidor, `UserRegisteredV1` deba recuperarse automáticamente mediante retry o replay, ni un umbral temporal específico para dicha recuperación.

El criterio experimental de `2000 ms` corresponde al flujo normal evaluado por SPIKE-01 y no fue definido como criterio de recuperación ante fallos.

Asimismo, el escenario de disponibilidad de máximo `5 minutos` se refiere a detectar la indisponibilidad del backend e informar al usuario, por lo que no constituye un criterio equivalente para la recuperación del aprovisionamiento posterior al registro.

Por tanto, la revisión posterior permite establecer la siguiente cadena:

```text
SPIKE-02
→ demuestra un modo de fallo bajo las condiciones evaluadas
→ demuestra una limitación concreta del mecanismo vigente
→ aporta evidencia adicional a riesgos ya reconocidos por ADR-003

PERO

no demuestra por sí solo
→ incumplimiento de un requisito vigente identificado en las fuentes revisadas
→ necesidad demostrada de entrega durable
→ necesidad demostrada de broker externo
→ necesidad demostrada de retry o replay garantizados
→ obligación de sustituir el mecanismo adoptado
```

Los análisis posteriores del Context Map, AsyncAPI, eventos candidatos y aplicabilidad de CQRS/Event Sourcing tampoco introducen por sí mismos una contradicción con la decisión original.

Respecto del Context Map, una versión de `context-map.puml` ya existía antes de ADR-003, mientras que sus refinamientos posteriores y la representación PNG aportan trazabilidad adicional.

La existencia previa del Context Map no demuestra que haya sido utilizado como evidencia causal para ADR-003.

AsyncAPI formaliza posteriormente el contrato vigente.

La auditoría de eventos no convierte `UserRegisteredV1` en una política general de integración mediante eventos.

CQRS y Event Sourcing permanecen como alternativas:

```text
NO JUSTIFICADAS TODAVÍA POR LA EVIDENCIA DISPONIBLE
```

y esa condición no significa que sean incorrectas ni que no puedan volver a evaluarse en el futuro.

SPIKE-03 tampoco contradice directamente ADR-003.

Su objeto experimental corresponde al comportamiento del cálculo de rachas en Progress y su propio veredicto dejó la decisión arquitectónica como:

```text
NO DETERMINADA
```

Por consiguiente, con el corpus revisado hasta esta fecha, la evidencia posterior no demuestra que alguno de los supuestos actuales de ADR-003 haya dejado necesariamente de sostenerse.

Sí demuestra que el riesgo de fallo posterior al commit es observable bajo condiciones inducidas y que la ausencia de recuperación automática dentro de las ventanas evaluadas debe permanecer documentada como limitación conocida.

El resultado de esta revisión se registra como:

```text
CONFIRMA LA DECISIÓN VIGENTE
CON LIMITACIONES POSTERIORES EXPLICITADAS
```

En esta revisión, **CONFIRMA** no significa:

- que el mecanismo sea óptimo;
- que `UserRegisteredV1` sea la mejor alternativa posible;
- que el mecanismo garantice tolerancia general a fallos;
- que no existan alternativas arquitectónicas potencialmente válidas;
- que la decisión no deba volver a revisarse.

Significa únicamente que, aplicando:

```text
criterios de re-decisión de ADR-003
+
requisitos y escenarios encontrados en las fuentes revisadas
+
evidencia posterior disponible
```

no se identificó una contradicción que obligue, con la evidencia actual, a sustituir la decisión vigente o iniciar automáticamente una nueva decisión arquitectónica.

La suficiencia de esa conclusión corresponde a un juicio arquitectónico sustentado en esos tres elementos y no a una afirmación de optimalidad universal.

ADR-003 deberá volver a revisarse si evidencia futura permite relacionar alguno de sus criterios de re-decisión con un requisito, escenario o impacto verificable.

Entre otros casos:

- categorías faltantes bajo condiciones operativas cuya relevancia esté sustentada por evidencia o por un requisito vigente;
- fallos del listener con impacto funcional considerado no aceptable por un requisito, escenario de calidad o decisión explícita del equipo;
- necesidad verificable de entrega durable;
- necesidad verificable de retry o replay garantizados;
- tiempos de convergencia incompatibles con un requisito vigente;
- múltiples consumidores con requisitos independientes;
- necesidad demostrada de separación entre procesos;
- necesidad demostrada de despliegue independiente.

Los términos:

```text
condiciones operativas relevantes
impacto funcional no aceptable
```

no representan actualmente umbrales cuantificados.

Su activación futura requerirá evidencia que permita relacionarlos con:

- un requisito vigente;
- un escenario de calidad;
- una decisión explícita del equipo;
- una observación operacional verificable.

La decisión original permanece vigente, mientras que SPIKE-02 y los análisis posteriores amplían explícitamente el conocimiento sobre sus límites y condiciones de reconsideración.

---

## Regla de interpretación de la revisión

```text
limitación demostrada
≠
requisito incumplido
≠
cambio arquitectónico obligatorio
```
