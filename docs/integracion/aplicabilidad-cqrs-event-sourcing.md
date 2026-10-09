# Aplicabilidad de CQRS y Event Sourcing



## Actualización posterior — SPIKE-03

Este análisis fue producido antes de la evidencia posterior registrada en
SPIKE-03.

En el corte original se identificó como evidencia faltante una medición directa
de operaciones asociadas a Progress.

Posteriormente, SPIKE-03 caracterizó específicamente el cálculo de:

- `currentStreak`;
- `bestStreak`;

al aumentar la cantidad de días históricos procesados.

La hipótesis experimental correspondiente resultó respaldada bajo las
condiciones registradas por el experimento.

Esta evidencia actualiza parcialmente la base empírica disponible, pero no
constituye por sí sola una justificación de CQRS.

SPIKE-03 dejó la decisión arquitectónica como:

`NO DETERMINADA`

Por tanto:

- existe evidencia directa sobre una ruta de cálculo de Progress;
- no se ha demostrado por ello una necesidad de separar lectura y escritura;
- no se ha demostrado Event Sourcing;
- la conclusión histórica debe interpretarse junto con esta evidencia posterior.

Evidencia:

- `experimentos/spike-03-caracterizacion-racha/`




## 1. Propósito

Este documento analiza CQRS y Event Sourcing como opciones arquitectónicas para
RachaPro.

El objetivo no es recomendar ni descartar automáticamente ninguno de los dos
patrones.

El análisis busca determinar qué problema concreto podrían resolver, qué
evidencia existe actualmente en el sistema, qué complejidad introducirían y qué
información adicional sería necesaria antes de justificar su adopción.

La evaluación se realiza utilizando exclusivamente evidencia disponible del
proyecto.

La pregunta central no es:

```text
¿podría utilizarse el patrón?
```

sino:

```text
¿qué problema verificable de RachaPro justificaría introducirlo?
```

---

## 2. Regla metodológica

Para cada patrón se analizan los siguientes puntos:

```text
1. problema concreto que podría resolver
2. evidencia de que ese problema existe o no existe
3. beneficios potenciales
4. complejidad nueva
5. riesgos
6. supuestos necesarios
7. evidencia faltante
8. estado de justificación
```

Cuando la evidencia disponible no permite justificar la introducción del patrón,
se utiliza explícitamente:

```text
NO JUSTIFICADO TODAVÍA POR LA EVIDENCIA DISPONIBLE
```

Esta clasificación significa:

```text
con los datos observados hasta este corte
todavía no existe evidencia suficiente
para justificar la complejidad adicional
```

No significa:

```text
el patrón es incorrecto
```

ni:

```text
el patrón nunca podrá utilizarse
```

---

## 3. Evidencia base utilizada

El análisis parte de las responsabilidades, decisiones y experimentos ya
documentados en RachaPro.

### 3.1 Arquitectura actual

La arquitectura actual se mantiene como:

```text
monolito modular
```

El backend se ejecuta dentro de un proceso Spring Boot y utiliza PostgreSQL para
persistencia.

La existencia de bounded contexts y fronteras modulares no implica que cada
contexto deba utilizar una base de datos independiente ni un mecanismo de
integración asíncrono.

---

### 3.2 Drivers de calidad

Los atributos de calidad priorizados actualmente son:

```text
1. rendimiento
2. seguridad
3. usabilidad
4. disponibilidad
```

Los escenarios de calidad existentes incluyen:

```text
crear una Activity en ≤ 1 minuto

rechazar acceso privado de usuarios no autenticados

crear una Activity desde Home en ≤ 5 minutos

detectar e informar backend inaccesible en ≤ 5 minutos
```

Estos escenarios forman parte del contexto arquitectónico, pero ninguno exige
directamente CQRS ni Event Sourcing.

---

### 3.3 Evidencia de escritura

Activities y Focus contienen operaciones que modifican el estado operacional.

Entre los cambios observados se encuentran:

```text
crear Activity
modificar Activity
completar Activity
eliminar lógicamente Activity

crear PomodoroSession
pausar PomodoroSession
reanudar PomodoroSession
completar PomodoroSession
cancelar PomodoroSession
```

Al completar una Activity se registra información como:

```text
status = COMPLETED
completedAt
completedDateEpochDay
```

Al completar una PomodoroSession se registra información equivalente relacionada
con la finalización de la sesión.

---

### 3.4 Necesidades de lectura de Progress

Progress consume información proveniente de Activities y Focus.

Entre sus necesidades se encuentran:

```text
días con Activities completadas
conteos de Activities completadas
estadísticas por periodo
sesiones FOCUS completadas
duración agregada
racha actual
mejor racha
```

Progress también aplica reglas propias mediante:

```text
StreakCalculator
```

Por tanto, existen necesidades de lectura e interpretación diferentes de las
operaciones que modifican el estado de Activities y Focus.

Esta diferencia por sí sola no demuestra que CQRS sea necesario.

---

### 3.5 Evidencia de rendimiento de escritura

EXP-002 evaluó:

```text
POST /api/activities
```

con:

```text
1 VU
25 iteraciones
25 de 25 solicitudes exitosas
```

Los resultados históricos incluyeron:

```text
promedio = 11,16 ms
mediana = 8,48 ms
p95 = 31,50 ms
```

El experimento no evaluó concurrencia significativa ni el flujo Android
end-to-end.

Por tanto, sus resultados describen únicamente las condiciones evaluadas.

---

### 3.6 Evidencia de carga de lectura

Existe una prueba con:

```text
500 usuarios sintéticos
1.000 Activities por usuario
500.000 Activities almacenadas
PostgreSQL en Docker
Spring Boot ejecutándose localmente
```

Se probaron escenarios de:

```text
10 usuarios concurrentes
50 usuarios concurrentes
100 usuarios concurrentes
250 usuarios concurrentes
500 usuarios concurrentes
```

Cada usuario debía recuperar:

```text
1.000 Activities
```

Las solicitudes terminaron correctamente en los escenarios ejecutados.

En la segunda corrida documentada de 500 usuarios concurrentes se obtuvo:

```text
solicitudes exitosas = 500/500
actividades servidas = 500.000
mínimo = 94 ms
promedio = 1.107,86 ms
mediana = 1.066,5 ms
p95 = 2.263 ms
máximo = 2.662 ms
tiempo total del lote = 23.473 ms
```

Después de la prueba se realizó una solicitud individual autenticada:

```text
POST-STRESS
115 ms
1.000 Activities
```

El servicio continuó operativo después de la carga.

---

### 3.7 Condiciones del experimento de 500k

La prueba fue ejecutada con:

```text
generador PowerShell
+
Spring Boot
+
PostgreSQL en Docker
+
misma máquina
```

Por tanto, los resultados incluyen competencia por los recursos del mismo
equipo.

Los tiempos observados:

```text
no representan infraestructura distribuida de producción
```

y:

```text
no permiten generalizar el comportamiento fuera de las condiciones ensayadas
```

---

### 3.8 Context Map relevante

Las relaciones relevantes para este análisis incluyen:

```text
Progress → Activities
Progress → Focus
```

La convención utilizada es:

```text
A → B
=
A consume o referencia información de B
```

Estas relaciones no determinan automáticamente el mecanismo de integración.

Actualmente no está decidido que Progress deba consumir esa información mediante:

```text
eventos
consultas
API
proyecciones
otro mecanismo
```

---

### 3.9 Eventos candidatos

La auditoría de eventos candidatos permitió continuar evaluando:

```text
ActivityCompleted
PomodoroSessionCompleted
UserRegisteredV1
```

La clasificación:

```text
ACEPTAR
```

utilizada en esa auditoría significa:

```text
el candidato continúa a evaluación arquitectónica
```

No significa:

```text
el evento debe publicarse obligatoriamente
```

ni:

```text
RachaPro debe adoptar Event Sourcing
```

---

## 4. CQRS

### 4.1 Problema concreto que podría resolver

RachaPro presenta una diferencia observable entre las operaciones que modifican
estado y las necesidades de consulta e interpretación de Progress.

Activities y Focus realizan operaciones como:

```text
crear
modificar
completar
cambiar estados operacionales
```

Progress requiere información como:

```text
días completados
conteos
estadísticas
periodos
sesiones FOCUS completadas
duración agregada
racha actual
mejor racha
```

El problema potencial puede formularse como:

> Las necesidades de lectura de Progress difieren de las operaciones de
> escritura de Activities y Focus, por lo que debe evaluarse si el crecimiento
> o complejidad de esas lecturas está generando un coste verificable de
> rendimiento, mantenimiento o evolución que justifique separar modelos de
> lectura y escritura.

La diferencia está demostrada.

El problema derivado de esa diferencia todavía no lo está.

---

### 4.2 Evidencia de que el problema existe o no existe

#### Evidencia que sugiere investigarlo

Progress realiza:

```text
agregaciones
conteos
interpretación temporal
cálculo de rachas
cálculo de mejor racha
estadísticas por periodo
```

Estas necesidades no son iguales a las operaciones que escriben estado en
Activities y Focus.

También existe evidencia de volumen:

```text
500.000 Activities almacenadas
```

y concurrencia:

```text
hasta 500 usuarios concurrentes
```

recuperando:

```text
1.000 Activities por usuario
```

En el escenario máximo documentado se mantuvo:

```text
500/500 solicitudes exitosas
```

y en la segunda corrida de 500 usuarios concurrentes:

```text
promedio = 1.107,86 ms
mediana = 1.066,5 ms
p95 = 2.263 ms
máximo = 2.662 ms
```

En las corridas documentadas se observan mayores latencias en algunos escenarios
de mayor concurrencia, aunque también existe variabilidad entre corridas.

Esa observación no permite atribuir causalidad por sí sola.

---

#### Evidencia que todavía no demuestra el problema

La prueba de 500k evaluó:

```text
GET /api/activities
```

No evaluó directamente las consultas propias de Progress.

Actualmente no existe una medición específica que demuestre un problema en:

```text
StreakCalculator
estadísticas por periodo
conteos de completitud
cálculo de mejor racha
agregaciones de Focus
agregaciones de Activities
```

Tampoco se ha demostrado:

```text
contención relevante entre lectura y escritura

bloqueos producidos por consultas de Progress

degradación de escrituras causada por las lecturas

saturación atribuible a la forma actual de consultar Progress

necesidad de escalar lecturas y escrituras de manera independiente
```

Además, la prueba de 500k compartió recursos físicos entre:

```text
PowerShell
Spring Boot
Docker
PostgreSQL
```

Por tanto:

```text
lecturas diferentes
+
variación de latencia bajo distintos niveles de concurrencia
≠
problema causado por no utilizar CQRS
```

---

### 4.3 Beneficios potenciales

CQRS podría permitir construir representaciones de lectura orientadas
específicamente a las necesidades de Progress si posteriormente se demuestra
que las consultas actuales tienen un coste relevante.

Una representación de lectura podría preparar información como:

```text
racha actual
mejor racha
actividades completadas por periodo
sesiones FOCUS completadas
duración agregada
```

Esto podría reducir el trabajo necesario durante determinadas consultas si el
problema de cálculo repetido llegara a demostrarse.

También podría permitir que las representaciones utilizadas para consultar
Progress evolucionaran de forma diferente a las operaciones que modifican
Activities y Focus.

Estos beneficios permanecen condicionados.

Por tanto:

```text
CQRS podría simplificar determinadas lecturas
si
esas lecturas demuestran ser suficientemente costosas o diferentes
como para justificar una representación específica
```

No existe evidencia de que estos beneficios sean necesarios actualmente.

---

### 4.4 Complejidad nueva

Separar explícitamente lectura y escritura introduciría nuevas
responsabilidades.

Entre ellas podrían encontrarse:

```text
modelo de escritura
modelo de lectura
mecanismo de actualización del modelo de lectura
sincronización entre representaciones
manejo de errores durante actualización
consistencia entre representaciones
pruebas adicionales
observabilidad de la sincronización
posible reconstrucción de proyecciones
```

La magnitud real de este coste en RachaPro no está medida.

Si la actualización del modelo de lectura se implementara de forma asíncrona,
también sería necesario resolver aspectos como:

```text
consistencia eventual
reintentos
idempotencia
orden de actualizaciones
duplicados
tiempo aceptable de convergencia
```

Estas características no son obligatorias para CQRS.

Por tanto:

```text
CQRS
≠
asincronía obligatoria
```

También:

```text
CQRS
≠
Event Sourcing
```

y:

```text
CQRS
≠
bases de datos separadas obligatoriamente
```

Una implementación síncrona seguiría introduciendo coordinación adicional entre
las representaciones de lectura y escritura.

---

### 4.5 Riesgos

Si se utilizara una proyección actualizada de manera asíncrona, Progress podría
observar temporalmente información distinta al estado más reciente de
Activities o Focus.

Ejemplo:

```text
Activity se completa
↓
la escritura termina
↓
la proyección todavía no fue actualizada
↓
Progress puede observar temporalmente el estado anterior
```

Otros riesgos potenciales incluyen:

```text
duplicación de información
errores de sincronización
proyecciones incompletas
dificultad adicional de depuración
mayor superficie de pruebas
inconsistencias entre representaciones
necesidad de reconstruir una proyección dañada
```

Estos riesgos dependen de la forma concreta en que CQRS fuera implementado.

También existe el riesgo de introducir responsabilidades adicionales para
resolver un problema cuya magnitud todavía no está demostrada.

La prueba de carga disponible demuestra que el sistema mantuvo corrección
funcional durante el escenario máximo evaluado.

Por ello, la diferencia conceptual entre lecturas y escrituras no basta por sí
sola para justificar CQRS.

---

### 4.6 Supuestos necesarios

Para que CQRS gane justificación en RachaPro tendría que demostrarse al menos
alguno de los siguientes supuestos:

```text
las consultas de Progress tienen un coste relevante bajo carga

el coste de las consultas crece de forma problemática con el volumen

las agregaciones de Progress afectan negativamente operaciones de escritura

mantener las consultas actuales produce complejidad verificable

Progress necesita representaciones de lectura que no se adapten adecuadamente
a las representaciones operacionales actuales

lecturas y escrituras tienen necesidades de escalabilidad suficientemente
diferentes

una proyección específica produce una mejora medible frente al mecanismo actual
```

Actualmente estos elementos deben considerarse hipótesis.

---

### 4.7 Evidencia faltante

La principal evidencia faltante es una medición directa de las operaciones de
Progress.

Un experimento futuro podría medir operaciones como:

```text
estadísticas TODAY
estadísticas WEEK
conteos de Activities completadas
cálculo de current streak
cálculo de best streak
agregaciones de sesiones FOCUS
```

También podría evaluarse su comportamiento con diferentes volúmenes.

Valores como:

```text
100
1.000
10.000
100.000
500.000
```

pueden considerarse niveles experimentales propuestos.

No representan requisitos actuales del sistema ni niveles obligatorios de
evaluación.

También sería útil una carga mixta:

```text
lecturas de Progress
+
creación de Activities
+
completitud de Activities
+
completitud de PomodoroSession
```

Las métricas podrían incluir:

```text
p50
p95
p99
throughput
errores
uso de conexiones
esperas de PostgreSQL
CPU
impacto sobre escrituras
```

Antes de ejecutar el experimento debe establecerse un criterio de evaluación.

No sería suficiente medir:

```text
p95 = X
```

y decidir posteriormente si X es alto.

Primero debería definirse:

```text
qué latencia se considera aceptable

para qué operación

con qué volumen

con qué concurrencia

bajo qué condiciones
```

Una comparación posterior podría enfrentar:

```text
implementación actual
vs.
proyección de lectura experimental
```

bajo condiciones equivalentes.

Si este experimento se realiza, el preregistro debe versionarse antes de
ejecutar o incorporar los resultados.

El preregistro debería incluir:

```text
pregunta
hipótesis falsable
consulta exacta
semilla
volumen
concurrencia
métricas
umbral
condiciones
criterio de aceptación o refutación
```

Los resultados deberían incorporarse posteriormente en un commit separado para
mantener trazabilidad cronológica.

---

### 4.8 Estado de justificación de CQRS

Con la evidencia disponible:

```text
NO JUSTIFICADO TODAVÍA POR LA EVIDENCIA DISPONIBLE
```

Esto no descarta CQRS.

Existe una diferencia observable entre determinadas necesidades de escritura y
lectura.

También existe evidencia real de volumen y concurrencia.

Sin embargo, todavía no está demostrado un problema de rendimiento, contención,
mantenimiento o escalabilidad cuya solución requiera introducir la separación
adicional asociada con CQRS.

---

## 5. Event Sourcing

### 5.1 Problema concreto que podría resolver

Event Sourcing podría resultar relevante si RachaPro necesitara reconstruir el
estado de una entidad a partir de la secuencia completa de hechos que
modificaron su estado.

El problema potencial sería una necesidad como:

```text
conocer exactamente cómo una Activity llegó a su estado actual

reproducir todas las transiciones de una PomodoroSession

reconstruir un estado anterior

recalcular una proyección mediante replay

auditar cada transición funcional como parte de la fuente oficial de verdad
```

El problema puede formularse como:

> Necesidad de reconstruir y auditar la evolución completa de una entidad a
> partir de todos los hechos que modificaron su estado, si RachaPro llegara a
> requerir esa capacidad como parte funcional del sistema.

Con los inputs actuales, esa necesidad no está demostrada.

---

### 5.2 Evidencia de que el problema existe o no existe

#### Evidencia que podría motivar investigar la opción

RachaPro conserva información temporal e histórica.

Entre la evidencia disponible aparecen elementos como:

```text
historial de sesiones
estadísticas históricas
mejor racha histórica
fechas de completitud
marcas temporales de creación
marcas temporales de actualización
```

También existen cambios funcionales de estado.

Por ejemplo:

```text
Activity
PENDING → COMPLETED
```

Focus contempla estados como:

```text
RUNNING
PAUSED
COMPLETED
CANCELLED
```

y Reminders contempla:

```text
SCHEDULED
DELIVERED
CANCELLED
```

La auditoría de eventos también identificó hechos candidatos como:

```text
ActivityCompleted
PomodoroSessionCompleted
UserRegisteredV1
```

Estos elementos demuestran que existen cambios, hechos e información histórica.

---

#### Evidencia que todavía no demuestra el problema

No se ha identificado un requisito que obligue a:

```text
reconstruir Activity reproduciendo todos sus eventos

reconstruir PomodoroSession mediante replay

consultar el estado exacto de cualquier entidad en cualquier instante pasado

rehidratar agregados a partir de eventos

reprocesar todos los hechos para corregir el estado

usar un event log como fuente primaria de verdad
```

Tampoco se ha demostrado infraestructura actual equivalente a:

```text
EventStore
appendEvent
loadEvents
replay
rehydrate
snapshot
aggregateVersion
sequenceNumber
```

La existencia de:

```text
historial de sesiones
```

no implica Event Sourcing.

Una colección o tabla convencional de sesiones puede satisfacer un historial
sin convertir eventos en la fuente de verdad.

De la misma manera:

```text
estadísticas históricas
```

pueden obtenerse a partir de registros persistidos y consultas convencionales.

Por tanto:

```text
historial
+
cambios de estado
+
eventos candidatos
≠
necesidad demostrada de Event Sourcing
```

---

### 5.3 Beneficios potenciales

Event Sourcing podría proporcionar determinadas capacidades si RachaPro llegara
a necesitar conservar la secuencia completa de hechos como parte esencial del
modelo.

Podría permitir:

```text
reconstruir el estado desde eventos

reproducir la evolución de un agregado

mantener trazabilidad funcional de las transiciones

crear nuevas proyecciones utilizando hechos históricos

recalcular vistas derivadas mediante replay
```

También podría permitir responder preguntas como:

```text
qué ocurrió antes de llegar al estado actual

qué secuencia de cambios produjo determinado resultado

qué estado puede reconstruirse en un punto anterior
```

Estos beneficios son condicionales.

Actualmente no se ha demostrado una necesidad de:

```text
replay funcional

reconstrucción de agregados

consultas temporales completas

auditoría exhaustiva de cada transición
```

Por tanto:

```text
Event Sourcing podría aportar reconstrucción y trazabilidad completa
si
esas capacidades llegan a convertirse en necesidades reales
```

---

### 5.4 Complejidad nueva

Introducir Event Sourcing añadiría nuevas responsabilidades de diseño,
persistencia y operación.

Entre ellas podrían encontrarse:

```text
event store
formato de eventos
identificador del agregado
orden de eventos
versionado de eventos
reconstrucción de agregados
manejo de concurrencia
proyecciones
estrategia de replay
manejo de errores durante replay
evolución de esquemas de eventos
pruebas de reconstrucción
```

También sería necesario establecer claramente qué constituye la fuente de
verdad.

Por ejemplo:

```text
estado actual persistido
```

o:

```text
secuencia de eventos
```

Si los eventos fueran la fuente de verdad, conceptualmente el estado actual se
obtendría mediante:

```text
estado inicial
+
evento 1
+
evento 2
+
evento 3
+
...
=
estado actual
```

Si en un escenario futuro un agregado llegara a acumular suficientes eventos
como para que el replay completo produjera un problema medible, podrían
evaluarse mecanismos como snapshots.

Esto es únicamente una posibilidad arquitectónica futura.

Actualmente no existe evidencia de que:

```text
el volumen de eventos por agregado sea problemático
```

ni de que:

```text
RachaPro necesite snapshots
```

La magnitud real del coste de introducir Event Sourcing en RachaPro tampoco está
medida.

---

### 5.5 Riesgos

Uno de los riesgos potenciales es que los eventos persistidos se conviertan en
contratos históricos cuya evolución deba administrarse cuidadosamente.

Ejemplo hipotético:

```text
ActivityCompletedV1
```

Este nombre se utiliza únicamente para ilustrar un posible escenario de
versionado.

No constituye evidencia de que exista actualmente un evento
`ActivityCompletedV1` en el repositorio de RachaPro.

Si un evento histórico cambiara de estructura en una versión posterior, sería
necesario decidir cómo interpretar las versiones anteriores.

Esto podría requerir mecanismos como:

```text
versionado
upcasting
compatibilidad hacia atrás
migraciones
adaptación de consumidores
```

Otro riesgo sería reconstruir un estado incorrecto debido a errores en la lógica
de replay.

Por ejemplo:

```text
eventos históricos válidos
+
lógica de replay incorrecta
↓
estado reconstruido incorrectamente
```

También podrían aparecer riesgos como:

```text
eventos duplicados
eventos fuera de orden
eventos faltantes
proyecciones desactualizadas
replay costoso
crecimiento continuo del event store
mayor dificultad de depuración
mayor superficie de pruebas
```

Algunos de estos riesgos dependen de la implementación concreta.

Si Event Sourcing se combinara con proyecciones asíncronas, aparecerían además
consideraciones de consistencia eventual.

Sin embargo:

```text
Event Sourcing
≠
asincronía obligatoria
```

También:

```text
Event Sourcing
≠
CQRS obligatorio
```

aunque ambos patrones puedan utilizarse juntos.

---

### 5.6 Supuestos necesarios

Para que Event Sourcing comenzara a ganar justificación en RachaPro tendría que
demostrarse al menos alguno de los siguientes supuestos:

```text
el estado actual no es suficiente para responder necesidades funcionales

es obligatorio reconstruir estados anteriores

se necesita reproducir la secuencia completa de hechos de una entidad

se requiere una auditoría funcional completa de cada transición

las proyecciones deben poder recalcularse utilizando hechos históricos

existe una necesidad real de regenerar información mediante replay

la secuencia de hechos constituye información de negocio que debe conservarse
como fuente de verdad
```

También tendría que evaluarse si mecanismos convencionales como:

```text
estado actual
+
timestamps
+
tablas históricas específicas
+
registros de auditoría
```

pueden satisfacer adecuadamente el requisito.

Actualmente estos supuestos no están demostrados.

---

### 5.7 Evidencia faltante

La evidencia principal que falta es un requisito funcional explícito que exija
reconstrucción histórica completa.

Por ejemplo, una necesidad equivalente a:

```text
El sistema debe reconstruir el estado de una Activity
en cualquier instante pasado
```

o:

```text
El sistema debe conservar cada transición funcional de una PomodoroSession
como fuente oficial de su estado
```

o:

```text
El sistema debe recalcular determinadas proyecciones
reproduciendo todos los hechos históricos
```

También sería necesario conocer:

```text
qué entidades requieren replay

con qué frecuencia

qué cantidad de eventos podría acumular un agregado

qué tiempo de reconstrucción sería aceptable

qué información debe conservarse

qué política de versionado sería necesaria
```

Solo si el volumen histórico demostrara posteriormente un problema real de
reconstrucción tendría sentido evaluar técnicas adicionales como snapshots.

Un eventual experimento podría comparar:

```text
persistencia convencional
vs.
reconstrucción mediante eventos
```

pero únicamente después de demostrar la necesidad que pretende resolver.

No sería metodológicamente correcto implementar primero un event store para
buscar posteriormente un problema que lo justifique.

La secuencia apropiada sería:

```text
requisito real
↓
problema verificable
↓
hipótesis
↓
experimento
↓
resultado
↓
decisión arquitectónica
```

y no:

```text
elegir Event Sourcing
↓
buscar después un problema que lo justifique
```

---

### 5.8 Estado de justificación de Event Sourcing

Con la evidencia disponible:

```text
NO JUSTIFICADO TODAVÍA POR LA EVIDENCIA DISPONIBLE
```

Esto no significa:

```text
Event Sourcing es incorrecto
```

ni:

```text
RachaPro nunca debe utilizar Event Sourcing
```

Significa que no se ha demostrado una necesidad de:

```text
reconstrucción mediante replay
event store como fuente de verdad
auditoría completa de cada transición
rehidratación del estado desde eventos
```

que justifique actualmente las responsabilidades adicionales del patrón.

---

## 6. Distinciones importantes

### 6.1 CQRS no implica Event Sourcing

```text
CQRS
≠
Event Sourcing
```

CQRS puede separar responsabilidades o representaciones de lectura y escritura
sin convertir eventos históricos en la fuente de verdad.

---

### 6.2 CQRS no implica asincronía

```text
CQRS
≠
asincronía obligatoria
```

La sincronización entre representaciones depende de la solución concreta.

---

### 6.3 CQRS no implica bases de datos separadas

```text
CQRS
≠
bases de datos separadas obligatoriamente
```

La separación conceptual entre lectura y escritura no obliga a utilizar
tecnologías de persistencia diferentes.

---

### 6.4 Historial no implica Event Sourcing

```text
historial
≠
Event Sourcing
```

Una aplicación puede conservar datos históricos utilizando persistencia
convencional.

---

### 6.5 Eventos de dominio no implican Event Sourcing

```text
eventos de dominio
≠
Event Sourcing
```

Un sistema puede utilizar eventos para comunicar hechos sin utilizarlos como
fuente primaria de verdad.

---

### 6.6 Integración basada en eventos no implica Event Sourcing

```text
integración basada en eventos
≠
Event Sourcing
```

La decisión de publicar eventos entre módulos es independiente de decidir que
la secuencia completa de eventos reconstruya el estado de los agregados.

---

### 6.7 Eventos candidatos de RachaPro no justifican Event Sourcing

Los candidatos:

```text
ActivityCompleted
PomodoroSessionCompleted
UserRegisteredV1
```

superaron una auditoría inicial para continuar siendo evaluados.

Esto significa:

```text
merecen continuar a evaluación arquitectónica
```

No significa:

```text
deben publicarse
```

ni:

```text
deben persistirse en un event store
```

ni:

```text
RachaPro necesita Event Sourcing
```

---

## 7. Comparación consolidada

| Aspecto | CQRS | Event Sourcing |
|---|---|---|
| Problema central evaluado | Diferencia entre necesidades de lectura y escritura que pudiera producir un coste verificable | Necesidad de reconstruir el estado desde la secuencia completa de hechos |
| Evidencia parcialmente favorable | Progress realiza agregaciones e interpretaciones distintas y existe evidencia de volumen y concurrencia de lectura | Existen cambios de estado, información histórica y eventos candidatos |
| Problema demostrado actualmente | No | No |
| Principal evidencia faltante | Medición directa del coste de las consultas de Progress y su impacto sobre el sistema | Requisito funcional de replay, reconstrucción completa o event log como fuente de verdad |
| Complejidad adicional principal | Modelos separados, actualización y consistencia entre representaciones | Event store, replay, versionado y reconstrucción de agregados |
| Estado actual | `NO JUSTIFICADO TODAVÍA POR LA EVIDENCIA DISPONIBLE` | `NO JUSTIFICADO TODAVÍA POR LA EVIDENCIA DISPONIBLE` |

---

## 8. Diferencia entre las dos conclusiones

Aunque ambos patrones tienen actualmente el mismo estado de justificación, la
razón no es idéntica.

Para CQRS:

```text
existe una diferencia observable
entre determinadas necesidades de lectura y escritura

pero

todavía no existe evidencia suficiente
de que esa diferencia produzca un problema
que justifique CQRS
```

Para Event Sourcing:

```text
existen estados
+
historial
+
hechos
+
eventos candidatos

pero

todavía no está demostrada
la necesidad funcional central
de reconstrucción mediante replay
```

Esta diferencia debe conservarse para evitar interpretar ambos resultados como
el mismo tipo de ausencia de evidencia.

---

## 9. Evidencia futura que podría cambiar la evaluación

### 9.1 CQRS

La evaluación podría revisarse si aparece evidencia como:

```text
latencias problemáticas en consultas reales de Progress

contención entre lecturas y escrituras

coste excesivo de agregaciones

dificultad demostrable de evolución del modelo de lectura

necesidad de escalar perfiles de lectura y escritura de manera diferente

mejora experimental verificable mediante una proyección especializada
```

---

### 9.2 Event Sourcing

La evaluación podría revisarse si aparece un requisito como:

```text
reconstrucción exacta de estados anteriores

replay funcional obligatorio

auditoría completa de cada transición

event log requerido como fuente oficial de verdad

regeneración obligatoria de proyecciones desde hechos históricos
```

La aparición de nuevos eventos de integración por sí sola no sería suficiente.

---

## 10. Regla para experimentación futura

La experimentación no debe utilizarse para buscar retrospectivamente una
justificación para un patrón ya escogido.

La secuencia esperada es:

```text
problema
↓
evidencia inicial
↓
hipótesis falsable
↓
criterio previo
↓
experimento
↓
resultado
↓
decisión
```

Si se realiza un experimento relacionado con CQRS o Event Sourcing, antes de
ejecutarlo debe quedar versionado un preregistro que indique:

```text
pregunta
hipótesis
alcance
semilla
dataset
operación evaluada
concurrencia
métricas
umbral
criterio de aceptación
criterio de refutación
condiciones del entorno
```

El preregistro debe preceder cronológicamente a los resultados.

Los resultados y su interpretación deben incorporarse posteriormente en un
commit diferente.

Esto permite distinguir:

```text
criterio definido antes de observar resultados
```

de:

```text
criterio construido después de conocer los resultados
```

---

## 11. Estado final

### CQRS

```text
NO JUSTIFICADO TODAVÍA POR LA EVIDENCIA DISPONIBLE
```

Existe una diferencia observable entre determinadas necesidades de lectura y
escritura.

Sin embargo, todavía no se ha demostrado que esa diferencia produzca un problema
de rendimiento, contención, mantenimiento o escalabilidad que justifique
introducir CQRS.

---

### Event Sourcing

```text
NO JUSTIFICADO TODAVÍA POR LA EVIDENCIA DISPONIBLE
```

Existen cambios de estado, información histórica y candidatos de eventos.

Sin embargo, todavía no se ha demostrado una necesidad funcional de replay,
reconstrucción completa del estado o utilización de un event log como fuente de
verdad que justifique introducir Event Sourcing.

---

### Conclusión

El análisis no adopta ni descarta definitivamente CQRS o Event Sourcing.

Con el corte de evidencia actual:

```text
CQRS
→ opción arquitectónica evaluada
→ problema potencial identificado
→ justificación todavía insuficiente

Event Sourcing
→ opción arquitectónica evaluada
→ capacidad central identificada
→ necesidad funcional todavía no demostrada
```

Ambos patrones permanecen disponibles para reconsideración si aparecen nuevas
evidencias que demuestren problemas concretos que sus capacidades permitan
resolver.
