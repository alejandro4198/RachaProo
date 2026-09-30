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
