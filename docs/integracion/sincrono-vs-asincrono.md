# Comparación de interacción síncrona vs. asíncrona

## 1. Propósito

Este documento compara dos alternativas de integración para una interacción
concreta entre bounded contexts de RachaPro:

```text
Focus → Activities
```

Las alternativas analizadas son:

```text
A. Interacción síncrona
B. Interacción asíncrona
```

El objetivo de este análisis no es seleccionar automáticamente una alternativa,
sino contrastarlas a partir de:

- la interacción actualmente observada.
- los drivers arquitectónicos vigentes.
- los escenarios de calidad actuales.
- las restricciones del proyecto.
- ADR-001.
- ADR-002.
- las decisiones explícitas adoptadas por el equipo para este ejercicio.

La comparación analiza:

1. dependencia temporal.
2. acoplamiento.
3. consistencia.
4. disponibilidad.
5. complejidad.
6. observabilidad.
7. impacto en atributos de calidad.
8. reversibilidad.
9. supuestos no verificados.

Este documento no introduce requisitos funcionales nuevos ni utiliza resultados
experimentales de otros escenarios como prueba automática de superioridad de una
alternativa.

---

## 2. Interacción analizada

La interacción seleccionada es:

```text
Focus → Activities
```

El origen es:

```text
Focus
```

y el contexto que ofrece la información requerida es:

```text
Activities
```

La operación observada corresponde a la creación de una `PomodoroSession` que
puede incluir opcionalmente una referencia a una Activity.

Cuando existe `activityId`, Focus requiere validar la referencia antes de
completar la operación.

La información utilizada por la interacción es:

```text
activityId
userId
```

La necesidad funcional documentada es comprobar que la Activity:

- existe.
- pertenece al usuario.
- no está eliminada.

El contrato público actualmente utilizado es:

```kotlin
interface ActivityLookup {
    fun existsActiveActivityForUser(
        activityId: Long,
        userId: Long
    ): Boolean
}
```

Focus no necesita conocer:

- `ActivityRepository`.
- entidades JPA de Activities.
- consultas de persistencia.
- implementación interna de Activities.

El contrato constituye la superficie pública mediante la cual Focus accede a la
capacidad requerida.

---

## 3. Contexto arquitectónico vigente

ADR-001 adopta un monolito modular para el backend de RachaPro.

Por tanto, el backend continúa siendo:

```text
una aplicación Spring Boot
+
un único proceso desplegable
+
una única unidad de despliegue
+
PostgreSQL compartido
```

Los módulos poseen ownership lógico de sus responsabilidades y datos, aunque
formen parte del mismo proceso.

En consecuencia, durante este análisis no debe interpretarse a Activities como
un microservicio desplegado independientemente de Focus.

La pregunta relevante ante un fallo no es necesariamente:

```text
¿Qué ocurre si Activities se cae?
```

sino:

```text
¿Qué ocurre si Focus no puede obtener correctamente
la validación ofrecida por Activities?
```

---

## 4. Antecedente de ADR-002

ADR-002 estableció como mecanismo inicial de integración entre módulos:

```text
contratos públicos mediante interfaces
+
llamadas locales síncronas
```

El objetivo de esa decisión fue preservar las fronteras de ownership sin
introducir acceso directo a repositories o entidades internas.

ADR-002 también dejó los eventos internos como una alternativa futura que puede
reconsiderarse si aparece una necesidad demostrada.

Entre las situaciones mencionadas se encuentran:

- múltiples consumidores independientes.
- procesamiento posterior al commit.
- desacoplamiento temporal.
- efectos secundarios que no deban formar parte de la operación principal.

Este antecedente no determina la decisión actual.

Por tanto:

```text
implementación síncrona existente
≠
decisión automática de mantener síncrono

eventos como alternativa futura
≠
evidencia de que asíncrono sea superior
```

---

## 5. Drivers y restricciones relevantes

Los atributos de calidad priorizados actualmente son:

```text
1. Rendimiento
2. Seguridad
3. Usabilidad
4. Disponibilidad
```

También son relevantes para las decisiones arquitectónicas actuales:

- mantenibilidad.
- cohesión.
- acoplamiento.
- facilidad de depuración.
- complejidad operacional.
- costo de adopción.
- trazabilidad.
- facilidad de pruebas.
- preservación del comportamiento actual.

Entre los condicionantes actuales se encuentran:

- Android como cliente.
- backend Spring Boot.
- un único proceso desplegable.
- PostgreSQL compartido.
- equipo de un solo integrante.
- ownership lógico de los datos.
- contratos públicos entre módulos.
- prohibición de acceder directamente a repositories o entidades internas de
  otros módulos.
- evitar complejidad distribuida o asíncrona sin necesidad demostrada.

---

## 6. Escenarios de calidad vigentes

Los escenarios actuales utilizados como referencia son:

### Rendimiento

```text
Operación:
creación de una Activity

Criterio:
máximo 1 minuto
```

### Seguridad

```text
Operación:
intento de acceso no autorizado a información privada

Criterio:
el acceso debe rechazarse y no entregar información privada
```

### Usabilidad

```text
Operación:
crear una Activity desde Home

Criterio:
completar correctamente el flujo en máximo 5 minutos
```

### Disponibilidad

```text
Condición:
backend inaccesible

Criterio:
detectar la indisponibilidad e informar al usuario
en máximo 5 minutos
```

Ninguno de estos escenarios mide directamente:

```text
Focus → Activities
```

Por tanto:

```text
escenario de calidad vigente
≠
evidencia experimental directa
sobre la interacción Focus → Activities
```

---

## 7. Decisiones del equipo e información pendiente para esta comparación

Para poder comparar ambas alternativas se registran las decisiones explícitas
adoptadas por el equipo para este ejercicio y, de forma separada, aquellos
aspectos que todavía no han sido definidos.

### 7.1 Validez de creación

Cuando existe `activityId`, la validación debe completarse antes de considerar
exitosa la creación de la `PomodoroSession`.

Por tanto:

```text
activityId != null
→ validar referencia
→ solo después puede considerarse exitosa la creación
```

Cuando:

```text
activityId == null
```

la sesión puede crearse sin realizar esta validación.

Clasificación:

```text
DECISIÓN DEL EQUIPO
```

### 7.2 Fallo de validación

Si Focus no puede obtener la información necesaria para completar la
validación, la creación asociada no se considera exitosa.

No se debe asumir automáticamente que la Activity es válida.

Clasificación:

```text
DECISIÓN DEL EQUIPO
```

### 7.3 Tolerancia de desfase

En la evidencia revisada no se identificó una decisión que establezca cuánto
tiempo podría estar desactualizada una representación diferida de la información
antes de dejar de ser aceptable para esta validación.

Estado:

```text
DECISIÓN FALTANTE
```

Por tanto:

```text
tolerancia máxima de staleness
→ NO DEFINIDA
```

Esta ausencia no se interpreta como una decisión arquitectónica.

Representa un aspecto que tendría que definirse si una alternativa basada en
información diferida o replicada necesitara una tolerancia explícita de
desactualización.

---

## 8. Comparación de alternativas

### 8.1 Dependencia temporal

#### A. Interacción síncrona

Focus obtiene el resultado de la validación durante el mismo flujo de creación
de la `PomodoroSession`.

Cuando existe `activityId`, la operación no puede considerarse exitosa hasta
obtener dicho resultado.

Por tanto, existe dependencia temporal entre:

```text
creación de PomodoroSession
        ↓
validación mediante Activities
        ↓
resultado de la creación
```

Esta dependencia es consistente con la decisión del equipo de completar la
validación antes del éxito.

#### B. Interacción asíncrona

Una alternativa asíncrona tendría que definir cómo Focus obtiene la certeza
necesaria antes de considerar exitosa la creación.

La asincronía podría cambiar la forma en que se produce la dependencia temporal,
pero no se ha demostrado que elimine la necesidad funcional de obtener la
validación.

Actualmente no está definido si la alternativa utilizaría:

- un evento.
- una respuesta diferida.
- una representación local.
- un estado pendiente.
- otro mecanismo.

Por tanto:

```text
INFORMACIÓN FALTANTE
→ mecanismo concreto mediante el cual una solución asíncrona
  permitiría cumplir la regla de validación previa al éxito
```

---

### 8.2 Acoplamiento

#### A. Interacción síncrona

Focus depende del contrato público:

```text
ActivityLookup
```

y de la semántica asociada a esa capacidad:

```text
comprobar existencia
+
pertenencia al usuario
+
no eliminación
```

Focus no depende directamente de:

- `ActivityRepository`.
- entidades JPA.
- consultas de persistencia.
- detalles internos de Activities.

Por tanto, el acoplamiento observado está asociado a:

```text
capacidad pública
+
semántica funcional del contrato
```

y no a la implementación interna de persistencia.

#### B. Interacción asíncrona

Una alternativa asíncrona cambiaría la forma del contrato entre ambos
contextos.

Dependiendo del diseño concreto, Focus podría necesitar conocer elementos como:

- tipo de mensaje.
- esquema.
- versión.
- identificadores utilizados.
- correlación.
- resultado diferido.
- representación local del estado.

No todos estos elementos son necesariamente obligatorios.

Su necesidad dependerá del mecanismo asíncrono finalmente diseñado.

La posibilidad de reducir el acoplamiento temporal se mantiene como:

```text
INFERENCIA / SUPUESTO A EVALUAR
```

y no como un efecto demostrado para esta interacción.

Por tanto:

```text
async
≠
ausencia de acoplamiento
```

La asincronía podría sustituir una forma de acoplamiento por otra.

---

### 8.3 Consistencia

#### A. Interacción síncrona

La validación utiliza el estado consultado por Activities durante la ejecución
de la operación.

Activities comprueba mediante su lógica:

- existencia.
- pertenencia al usuario.
- no eliminación.

En la evidencia revisada no se identificó que Focus mantenga actualmente una
copia diferida de esa información para realizar la validación.

La interacción actual permite que la decisión de creación utilice el resultado
obtenido directamente de la capacidad propietaria de la información durante la
validación.

Esto no implica por sí solo una garantía transaccional adicional que no haya
sido documentada.

#### B. Interacción asíncrona

Si Focus utilizara información:

- diferida.
- replicada.
- recibida previamente.
- mantenida localmente.

podría existir una diferencia temporal respecto al estado que Activities
consultaría durante una validación directa.

En la evidencia revisada no se identificó una decisión sobre:

```text
staleness máximo permitido
```

Por tanto, todavía no puede determinarse si una representación diferida sería
compatible con la regla funcional establecida.

La pregunta pendiente es:

```text
¿Qué antigüedad máxima puede tener la información
para seguir considerándose válida para esta decisión?
```

---

### 8.4 Disponibilidad

#### A. Interacción síncrona

Cuando existe `activityId`, Focus necesita obtener correctamente el resultado de
la capacidad ofrecida por Activities antes de completar la operación.

Si no puede completar esa validación, la decisión del equipo establece:

```text
la creación asociada falla
```

Esto produce una dependencia operacional de la capacidad de validación durante
el flujo.

Dado que Focus y Activities pertenecen al mismo backend y al mismo proceso,
este comportamiento no debe describirse automáticamente como:

```text
caída independiente de Activities
```

Debe analizarse como:

```text
incapacidad de obtener correctamente
la validación requerida
```

#### B. Interacción asíncrona

Una alternativa asíncrona podría modificar la forma en que se manifiesta esta
dependencia.

Sin embargo, permanece la decisión funcional:

```text
validación completada
antes del éxito
```

Por tanto, deben definirse todavía:

- comportamiento mientras el resultado no esté disponible.
- comportamiento ante retraso.
- comportamiento ante fallo.
- momento en que se considera imposible completar la validación.

En la evidencia revisada no se identificó información que permita afirmar que
una solución asíncrona mejoraría la disponibilidad de esta interacción.

---

### 8.5 Complejidad

#### A. Interacción síncrona

La implementación actual utiliza:

```text
Focus
→ ActivityLookup
→ implementación de Activities
```

dentro del mismo proceso.

ADR-002 ya materializó contratos públicos mediante interfaces.

Entre las características documentadas de este mecanismo están:

- contratos pequeños.
- ocultamiento de repositories.
- ocultamiento de entidades.
- llamadas locales.
- facilidad para probar consumidores.
- conservación de una única unidad de despliegue.

Respecto al estado actual, no se requiere introducir un mecanismo nuevo de
comunicación para mantener esta alternativa.

Los errores pueden propagarse dentro del mismo flujo de la operación.

#### B. Interacción asíncrona

Para mantener la regla:

```text
validar antes del éxito
```

una alternativa asíncrona tendría que definir cómo se obtiene el resultado de
la validación antes de finalizar la creación.

Dependiendo de la solución concreta podrían aparecer necesidades relacionadas
con:

- publicación.
- consumo.
- correlación.
- manejo de errores.
- persistencia.
- estados pendientes.
- duplicados.
- orden.
- timeout.
- reintentos.

Estos elementos no se consideran automáticamente requisitos.

Por tanto:

```text
posible mecanismo
≠
mecanismo obligatorio
```

Debe determinarse cuáles serían realmente necesarios una vez exista un diseño
asíncrono concreto.

---

### 8.6 Observabilidad

#### A. Interacción síncrona

Actualmente existe una llamada local dentro del mismo proceso.

A partir de esta estructura puede inferirse que el flujo posee menos saltos
arquitectónicos que una interacción que introdujera procesamiento diferido.

Esta afirmación corresponde a una inferencia estructural y no demuestra que el
sistema disponga de instrumentación suficiente para reconstruir operativamente
la interacción.

En la evidencia revisada no se identificaron mecanismos específicos para
`Focus → Activities` como:

- logs dedicados.
- correlation IDs.
- métricas de la validación.
- tracing específico.

Por tanto:

```text
llamada local
≠
observabilidad demostrada
```

#### B. Interacción asíncrona

Si la validación deja de corresponder a una respuesta directa dentro de la misma
llamada, sería necesario poder relacionar el resultado de la validación con la
creación que la originó.

Dependiendo de la solución podrían necesitarse mecanismos para distinguir:

- solicitud.
- procesamiento.
- pendiente.
- éxito.
- fallo.

La forma concreta de conseguirlo permanece sin definir.

En la evidencia revisada no se identificaron:

- correlation IDs.
- tracing.
- métricas.
- logs.

correspondientes a una versión asíncrona de esta interacción.

Esto se registra como información necesaria para una comparación futura y no
como prueba de que dichos mecanismos sean obligatorios en toda solución
asíncrona posible.

---

### 8.7 Impacto en atributos de calidad

Los cuatro atributos priorizados son:

```text
Rendimiento
Seguridad
Usabilidad
Disponibilidad
```

Sin embargo, ninguno de sus escenarios vigentes mide directamente la interacción
analizada.

#### A. Interacción síncrona

##### Rendimiento

La validación forma parte del flujo de creación cuando existe `activityId`.

Por tanto, es plausible que el tiempo utilizado por `ActivityLookup` contribuya
al tiempo total de la operación.

Sin embargo:

```text
EVIDENCIA FALTANTE
→ en la evidencia revisada no se identificó una medición
  específica de la latencia de ActivityLookup
```

No se ha demostrado que dicha latencia represente actualmente un problema
observable.

##### Seguridad

La validación utiliza:

```text
activityId
+
userId
```

y comprueba pertenencia al usuario.

Esto está relacionado funcionalmente con evitar asociaciones hacia Activities
que no correspondan al usuario.

Sin embargo, el escenario vigente de seguridad evalúa acceso no autorizado a
información privada y no compara mecanismos síncronos y asíncronos de
integración.

##### Usabilidad

Un fallo o retraso en la validación podría influir en la experiencia del usuario
durante la creación de una sesión Pomodoro asociada.

Esta relación constituye una inferencia.

El escenario vigente de usabilidad mide:

```text
Home → creación de Activity
```

y no:

```text
creación de PomodoroSession
```

##### Disponibilidad

La creación asociada depende de completar la validación.

El escenario actual de disponibilidad evalúa:

```text
backend completo inaccesible
```

y no una incapacidad específica de ejecutar `ActivityLookup`.

Por tanto, el escenario existente no permite concluir el impacto concreto de
esta alternativa sobre disponibilidad.

#### B. Interacción asíncrona

Una alternativa asíncrona podría modificar:

- dependencia temporal.
- propagación de fallos.
- tiempos.
- forma de obtener la información.
- comportamiento visible al usuario.

Sin embargo, en la evidencia revisada no se identificó información que demuestre
que estos cambios mejoren o empeoren:

- rendimiento.
- seguridad.
- usabilidad.
- disponibilidad.

Si la alternativa utilizara información diferida, además debería verificarse
que conserva:

```text
validación antes del éxito
```

y que el desfase utilizado sea aceptable según una tolerancia que todavía no ha
sido definida.

Por tanto:

```text
impacto plausible
≠
impacto medido
```

---

### 8.8 Reversibilidad

#### A. Interacción síncrona

Si posteriormente se decidiera abandonar la interacción síncrona actual,
deberían revisarse:

- los puntos donde Focus consume `ActivityLookup`.
- el contrato o su forma de uso, según la alternativa concreta.
- la implementación pública ofrecida por Activities cuando resulte afectada.
- las pruebas relacionadas.
- las reglas arquitectónicas afectadas.
- las fitness functions relacionadas con los cruces intermodulares.

No se asume que necesariamente deba eliminarse toda la interfaz conceptual.

Una futura solución podría conservar parte de la abstracción y modificar
únicamente su mecanismo de integración.

Por tanto, el costo real de reversión depende de la alternativa que la sustituya.

#### B. Interacción asíncrona

Si se implementara una alternativa asíncrona y posteriormente se abandonara,
deberían revisarse los artefactos introducidos específicamente por ella.

Estos podrían incluir, únicamente si forman parte del diseño:

- contratos de mensajes.
- productores.
- consumidores.
- mecanismos de correlación.
- representaciones locales.
- persistencia adicional.
- estados pendientes.
- pruebas específicas.
- instrumentación de observabilidad.

Como todavía no se ha definido un diseño asíncrono concreto:

```text
costo real de reversión async
=
NO DETERMINADO
```

No es posible afirmar todavía cuál alternativa posee mayor reversibilidad.

---

### 8.9 Supuestos no verificados

#### A. Interacción síncrona

Entre los supuestos que podrían influir en una decisión se encuentran:

```text
SUPUESTO
→ la latencia de ActivityLookup es suficientemente significativa
  como para justificar cambiar el mecanismo actual
```

```text
SUPUESTO
→ el volumen o frecuencia de validaciones podría representar
  un problema para el mecanismo actual
```

```text
SUPUESTO
→ la interacción síncrona podría generar un impacto observable
  sobre rendimiento o disponibilidad
```

Estos supuestos no han sido demostrados mediante una medición específica de la
interacción `Focus → Activities`.

La ausencia de una medición específica no se clasifica como supuesto.

Se registra separadamente como:

```text
EVIDENCIA FALTANTE
```

#### B. Interacción asíncrona

Entre los supuestos posibles se encuentran:

```text
SUPUESTO
→ existe una necesidad real de desacoplamiento temporal
```

```text
SUPUESTO
→ una alternativa asíncrona podría mejorar algún atributo
  de calidad relevante
```

```text
SUPUESTO
→ una representación diferida de Activities podría cumplir
  la regla de validación previa al éxito
```

```text
SUPUESTO
→ el costo adicional de implementación, mantenimiento
  y observabilidad sería aceptable
```

```text
SUPUESTO
→ el volumen actual justificaría introducir
  un mecanismo diferente
```

Estos supuestos tampoco han sido verificados.

---

## 9. Evidencia e información faltante

Durante la comparación se identificaron aspectos para los cuales la evidencia
revisada todavía no permite cerrar una decisión.

No todos los pendientes poseen la misma naturaleza.

Algunos requieren:

```text
inspección
```

otros requieren:

```text
medición o prueba
```

otros requieren:

```text
una decisión funcional o arquitectónica
```

y otros requieren:

```text
especificar primero una alternativa concreta
```

---

### 9.1 Latencia específica de la interacción

Clasificación:

```text
EVIDENCIA MEDIBLE FALTANTE
```

En la evidencia revisada no se identificó una medición aislada de:

```text
ActivityLookup
```

Por tanto, podría obtenerse evidencia sobre:

- duración típica de la validación.
- distribución de tiempos.
- comportamiento bajo condiciones comparables.
- contribución al tiempo total de creación de una `PomodoroSession`.

---

### 9.2 Volumen y frecuencia

Clasificación:

```text
EVIDENCIA MEDIBLE FALTANTE
```

En la evidencia revisada no se identificaron mediciones específicas sobre:

- cantidad de validaciones realizadas.
- frecuencia por usuario.
- concurrencia.
- comportamiento con diferentes cargas.

Por tanto, no puede utilizarse actualmente el volumen como justificación
demostrada para cambiar el mecanismo de integración.

---

### 9.3 Comportamiento ante fallos

Clasificación:

```text
EVIDENCIA EJECUTABLE FALTANTE
```

Falta evidencia reproducible sobre situaciones en las que Focus no pueda obtener
correctamente la validación.

Debe identificarse qué fallos son realmente posibles dentro de la arquitectura
actual y observar el comportamiento producido.

---

### 9.4 Tolerancia de staleness

Clasificación:

```text
DECISIÓN FALTANTE
```

En la evidencia revisada no se identificó una decisión sobre:

```text
antigüedad máxima aceptable
de una representación diferida de Activities
```

Este pendiente no se resuelve únicamente mediante una medición.

El equipo tendría que definir qué grado de desactualización sería funcionalmente
aceptable si una alternativa concreta requiriera información diferida.

---

### 9.5 Observabilidad específica

Clasificación:

```text
EVIDENCIA O INSPECCIÓN FALTANTE
```

En la evidencia revisada no se identificaron mecanismos específicos de
observabilidad para esta interacción como:

- logs específicos.
- correlation IDs.
- métricas.
- tracing.
- mecanismos para reconstruir el flujo de validación.

La existencia o ausencia de estos mecanismos puede verificarse mediante
inspección del código, configuración y herramientas de observabilidad utilizadas
por el proyecto.

---

### 9.6 Diseño asíncrono concreto

Clasificación:

```text
DISEÑO / DECISIÓN ARQUITECTÓNICA PENDIENTE
```

Todavía no se ha definido una alternativa asíncrona específica.

Por tanto, permanecen abiertos elementos como:

- mecanismo.
- contrato.
- semántica de entrega.
- manejo de errores.
- necesidad de persistencia.
- correlación.
- estados intermedios.
- tratamiento de duplicados.
- orden.
- timeout.
- reintentos.

Estos elementos no deben considerarse automáticamente necesarios.

Solo podrán determinarse después de especificar una alternativa asíncrona
concreta.

---

### 9.7 Comparación experimental

Clasificación:

```text
EVIDENCIA COMPARATIVA FALTANTE
```

En la evidencia revisada no se identificó un experimento que compare
directamente:

```text
Focus → Activities síncrono
vs.
Focus → Activities asíncrono
```

bajo condiciones equivalentes.

Por tanto, la evidencia revisada no demuestra experimentalmente la superioridad
de una alternativa sobre la otra para esta interacción.

---

## 10. Relación con experimentos existentes

EXP-001 evaluó un escenario histórico relacionado con:

```text
carga del listado de Activities
```

sobre una etapa principalmente local basada en Room.

EXP-002 evaluó:

```text
POST /api/activities
```

sobre Spring Boot y PostgreSQL.

EXP-002 utilizó:

```text
1 VU
25 iteraciones
```

y midió específicamente la creación HTTP de Activities.

Estos experimentos no fueron diseñados para medir:

```text
ActivityLookup
```

ni:

```text
Focus → Activities
```

ni para realizar:

```text
comparación síncrona vs. asíncrona
```

Por tanto:

```text
EXP-001
+
EXP-002
≠
evidencia para elegir automáticamente
sync o async en Focus → Activities
```

Sus resultados se mantienen como evidencia válida dentro de los escenarios para
los cuales fueron diseñados.

---

## 11. Síntesis epistemológica

### 11.1 Hechos documentados

```text
→ Focus y Activities forman parte del mismo monolito modular.

→ el backend corresponde a un único proceso desplegable.

→ Activities posee ownership de la información utilizada
  para validar la referencia.

→ Focus utiliza ActivityLookup como API pública.

→ activityId es opcional.

→ cuando existe activityId, el flujo actual realiza la validación.

→ la validación observada comprueba existencia,
  pertenencia al usuario y no eliminación.

→ el mecanismo actualmente implementado es síncrono.
```

### 11.2 Decisiones del equipo para esta comparación

```text
→ con activityId, la validación debe completarse
  antes de considerar exitosa la creación.

→ si no puede completarse la validación,
  la creación asociada falla.
```

### 11.3 Decisión faltante

```text
→ no se ha definido una tolerancia máxima de staleness
  para una representación diferida de Activities.
```

### 11.4 Inferencias analizadas

```text
→ async podría cambiar la dependencia temporal.

→ async podría cambiar la forma del acoplamiento,
  pero no necesariamente eliminarlo.

→ ambos mecanismos podrían afectar atributos de calidad.

→ una interacción con procesamiento diferido
  requeriría alguna forma de relacionar
  el resultado con la creación correspondiente.
```

### 11.5 Supuestos no verificados

```text
→ la latencia actual constituye un problema relevante.

→ existe una necesidad real de desacoplamiento temporal.

→ async mejoraría alguno de los atributos priorizados.

→ una representación diferida podría satisfacer
  la regla funcional.

→ el volumen de validaciones justificaría cambiar
  el mecanismo actual.

→ el costo adicional de una solución async sería aceptable.
```

### 11.6 Evidencia faltante

```text
→ latencia específica de ActivityLookup.

→ volumen y frecuencia de validaciones.

→ comportamiento reproducible ante fallos.

→ observabilidad específica.

→ comparación experimental entre alternativas.
```

Además existen pendientes que no corresponden únicamente a evidencia medible:

```text
DECISIÓN FALTANTE
→ tolerancia de staleness.

DISEÑO PENDIENTE
→ alternativa asíncrona concreta.
```

---

## 12. Estado de la comparación

Las alternativas:

```text
A. interacción síncrona
B. interacción asíncrona
```

fueron contrastadas respecto a:

```text
dependencia temporal        COMPLETADA
acoplamiento                COMPLETADO
consistencia                COMPLETADA
disponibilidad              COMPLETADA
complejidad                 COMPLETADA
observabilidad              COMPLETADA
atributos de calidad        COMPLETADOS
reversibilidad              COMPLETADA
supuestos no verificados    IDENTIFICADOS
```

La comparación permite identificar diferencias, preguntas, supuestos y
evidencia faltante, pero no demuestra todavía que una alternativa sea superior.

Por tanto:

```text
alternativas comparadas
→ SÍ

alternativa ganadora
→ NO DECIDIDA

decisión arquitectónica
→ PENDIENTE
```

---

## 13. Evidencias y definiciones necesarias antes de decidir

Antes de cerrar una decisión arquitectónica sobre esta interacción permanecen
pendientes elementos de distinta naturaleza.

### Evidencia que puede obtenerse mediante inspección, medición o prueba

- latencia real de `ActivityLookup`.
- frecuencia y volumen de validaciones.
- comportamiento observable ante fallos de la validación.
- mecanismos actuales de observabilidad de `Focus → Activities`.
- impacto verificable de cada alternativa sobre los atributos de calidad
  relevantes.
- comparación experimental entre ambas alternativas, si la decisión requiere
  evidencia cuantitativa.

### Decisión todavía faltante

- tolerancia aceptable de información desactualizada, si una alternativa futura
  requiere una representación diferida de Activities.

### Diseño todavía pendiente

- definición de una alternativa asíncrona concreta que permita determinar:
    - contrato.
    - mecanismo de interacción.
    - semántica de fallo.
    - correlación.
    - persistencia si fuera necesaria.
    - estados intermedios si fueran necesarios.
    - tratamiento de duplicados si fuera necesario.
    - orden si fuera relevante.
    - timeout si fuera requerido.
    - reintentos si fueran necesarios.
    - mecanismos de observabilidad.
    - costo de implementación, prueba, mantenimiento y reversión.

Estos pendientes deben mantenerse diferenciados porque:

```text
evidencia faltante
≠
decisión faltante
≠
diseño faltante
```

---

## 14. Conclusión

El análisis no selecciona una alternativa.

La interacción actualmente implementada utiliza un contrato síncrono dentro del
monolito modular, pero esa condición se conserva como antecedente y no como
justificación suficiente para mantenerla indefinidamente.

De igual forma, la posibilidad de utilizar eventos o mecanismos asíncronos no se
interpreta como una mejora demostrada.

La situación actual puede resumirse así:

```text
interacción actual
→ síncrona

alternativa asíncrona
→ posible objeto de evaluación

comparación
→ completada conceptualmente

evidencia
→ todavía incompleta para determinados criterios

decisión funcional pendiente
→ tolerancia de staleness, si resulta necesaria

diseño pendiente
→ alternativa asíncrona concreta

decisión final
→ pendiente
```

La selección posterior deberá sustentarse en las evidencias obtenidas, las
decisiones explícitas del equipo y las prioridades arquitectónicas del proyecto,
evitando convertir ventajas potenciales, inferencias o supuestos en hechos no
verificados.