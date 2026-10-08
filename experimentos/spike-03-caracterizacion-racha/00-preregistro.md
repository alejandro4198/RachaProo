# SPIKE-03 — Caracterización de escalabilidad del cálculo de streak en Progress

## 1. Identificación

- Proyecto: RachaPro
- Experimento: SPIKE-03
- Nombre: Caracterización de escalabilidad del cálculo de streak en Progress
- Tipo: Spike de caracterización
- Estado: LISTO PARA PRERREGISTRO — NO EJECUTADO
- Arquitectura vigente: monolito modular
- Aplicación observada: Android
- Capacidad observada: Progress
- Rama de preregistro definitivo: `docs/spike-03-preregistro-definitivo`
- Documento complementario: `condiciones.md`

Este documento define el protocolo experimental de SPIKE-03 antes de implementar el harness y antes de realizar cualquier medición formal.

El objetivo del spike es caracterizar el comportamiento temporal de la ruta actual utilizada para calcular la racha de Progress.

SPIKE-03 no parte de la afirmación de que exista un problema de rendimiento.

Tampoco parte de una solución arquitectónica previamente elegida.

El experimento solo se considerará formalmente preregistrado cuando esta versión de:

- `00-preregistro.md`
- `condiciones.md`

forme parte de un commit anterior a:

- la implementación del harness
- el commit experimental
- las mediciones formales
- los resultados
- el veredicto

Hasta que exista dicho commit, el estado correcto es:

`LISTO PARA PRERREGISTRO — NO EJECUTADO`

---

## 2. Evidencia de partida

La inspección de la implementación actual de Progress muestra que sus métricas no siguen todas la misma ruta de cálculo.

Activities dispone de consultas Room especializadas para operaciones como:

- contar Activities completadas
- contar Activities completadas dentro de un intervalo
- agrupar Activities completadas por día

Estas operaciones utilizan mecanismos de agregación directamente en persistencia, incluyendo:

- `COUNT`
- `BETWEEN`
- `GROUP BY`

Focus dispone igualmente de consultas especializadas para operaciones como:

- contar sesiones FOCUS completadas
- sumar segundos de sesiones FOCUS completadas
- contar sesiones dentro de intervalos
- sumar segundos dentro de intervalos
- agrupar estadísticas por día

Estas consultas utilizan mecanismos como:

- `COUNT`
- `SUM`
- `BETWEEN`
- `GROUP BY`

Por tanto, la evidencia actual no permite afirmar que Progress, en general, cargue todos los registros para realizar posteriormente todas sus agregaciones en Kotlin.

La ruta de cálculo de streak presenta un comportamiento diferente.

`ProgressViewModel` obtiene los días completados provenientes de Activities y Focus.

Posteriormente combina las colecciones y realiza una normalización equivalente a:

`activityDays + pomodoroDays`

↓

`distinct()`

↓

`sorted()`

El resultado es entregado a:

`StreakCalculator.calculate(...)`

Dentro de `StreakCalculator` se realizan nuevamente operaciones relacionadas con:

- filtrado
- eliminación de duplicados
- ordenamiento
- recorrido de los días válidos

para producir:

- `currentStreak`
- `bestStreak`

Por tanto, esta ruta opera sobre una colección histórica cuyo tamaño depende de la cantidad de días distintos entregados al cálculo.

La evidencia disponible todavía no demuestra:

- que la ruta sea lenta
- que constituya un cuello de botella
- que afecte perceptiblemente al usuario
- que requiera optimización
- que requiera modificar la arquitectura

SPIKE-03 se crea precisamente para obtener evidencia experimental antes de realizar cualquiera de esas afirmaciones.

---

## 3. Escenario de calidad relacionado

SPIKE-03 se relaciona con el atributo de calidad:

`Rendimiento`

Rendimiento ha sido priorizado previamente dentro de los atributos de calidad del proyecto RachaPro.

Sin embargo, este spike no parte de una violación demostrada de un escenario temporal existente.

El propósito es caracterizar una operación concreta de la implementación vigente.

La secuencia metodológica será:

`medir implementación actual`

↓

`caracterizar comportamiento`

↓

`analizar magnitud`

↓

`determinar si existe evidencia suficiente para estudiar posteriormente una intervención`

Por tanto, SPIKE-03 es un experimento de:

`caracterización`

No es todavía un experimento de:

`optimización`

---

## 4. Comparabilidad con evidencia experimental previa

RachaPro dispone de experimentos anteriores relacionados con rendimiento.

### EXP-001

EXP-001 observó la carga de Activities en Android y Room.

Su intervalo de medición fue aproximadamente:

`inicio de loadData()`

hasta:

`ActivitiesUiState.Success`

Entre los resultados históricos documentados se encuentran aproximadamente:

- mediana: 1325 ms
- P95: 1621 ms
- mínimo: 1305 ms
- máximo: 1666 ms

Esta variable no corresponde a la ruta aislada de cálculo de streak.

Por tanto:

`EXP-001 ≠ BASELINE DIRECTAMENTE COMPARABLE`

---

### EXP-002

EXP-002 observó:

`POST /api/activities`

mediante la métrica:

`activity_create_duration`

Entre los resultados históricos documentados se encuentran aproximadamente:

- promedio: 11.16 ms
- mediana: 8.48 ms
- P95: 31.50 ms
- máximo: 46.02 ms

Esta operación tampoco corresponde al cálculo de streak.

Por tanto:

`EXP-002 ≠ BASELINE DIRECTAMENTE COMPARABLE`

---

### Otros experimentos

Otros experimentos del proyecto han utilizado variables relacionadas con:

- cantidad de Activities
- cantidad de usuarios
- solicitudes HTTP
- concurrencia
- carga del backend

Estas variables tampoco equivalen a:

`cantidad de días históricos distintos procesados por la ruta de streak`

---

### Conclusión de comparabilidad

Para SPIKE-03:

`NO EXISTE UNA LÍNEA BASE DIRECTAMENTE COMPARABLE`

La evidencia experimental anterior se conservará como:

`ANTECEDENTE METODOLÓGICO`

No se utilizará como baseline numérica directa.

SPIKE-03 pretende crear una primera caracterización controlada de esta operación específica.

---

## 5. Contexto arquitectónico y decisiones previas

La arquitectura vigente de RachaPro continúa siendo:

`monolito modular`

Los contextos principales identificados incluyen:

- Identity
- Activities
- Focus
- Progress
- Reminders

La convención utilizada en el Context Map es:

`A → B`

significa:

`A consume o referencia información proveniente de B`

Para Progress se mantienen conceptualmente:

`Progress → Activities`

`Progress → Focus`

Estas relaciones no implican automáticamente:

- transferencia de ownership
- REST
- asincronía
- eventos
- microservicios
- CQRS
- Event Sourcing

SPIKE-03 no modifica el Context Map.

Las decisiones arquitectónicas previas continúan vigentes.

En particular, este experimento no modifica:

- la decisión de utilizar monolito modular
- las fronteras de persistencia existentes
- los contratos intermodulares existentes
- ADR-003 relacionado con el caso específico de `UserRegisteredV1`

Las conclusiones actuales respecto a patrones avanzados continúan siendo:

`CQRS → NO JUSTIFICADO TODAVÍA POR LA EVIDENCIA DISPONIBLE`

`Event Sourcing → NO JUSTIFICADO TODAVÍA POR LA EVIDENCIA DISPONIBLE`

SPIKE-03 no evalúa ni intenta justificar dichos patrones.

---

## 6. Problema verificable

En la implementación actual de Progress, el cálculo de la racha utiliza los días históricos completados provenientes de Activities y Focus.

`ProgressViewModel` combina ambas colecciones y realiza operaciones de unión, eliminación de duplicados y ordenamiento antes de entregar el resultado a `StreakCalculator`.

A su vez, `StreakCalculator` vuelve a procesar la colección mediante operaciones de filtrado, eliminación de duplicados y ordenamiento antes de recorrer los días necesarios para obtener la racha actual y la mejor racha.

La evidencia del código permite comprobar que esta ruta procesa una colección cuyo tamaño depende de la cantidad de días históricos distintos entregados al cálculo.

Sin embargo, actualmente no existe una medición experimental específica que permita determinar cómo cambia el tiempo de ejecución de esta ruta cuando aumenta el número de días históricos distintos.

Por tanto, todavía no está demostrado que el cálculo actual de streak represente un problema de rendimiento ni que requiera algún cambio en su implementación.

---

## 7. Pregunta experimental

> ¿Cómo cambia el tiempo de ejecución de la implementación actual del cálculo de `currentStreak` y `bestStreak` a medida que aumenta la cantidad de días históricos distintos que debe procesar?

La pregunta busca caracterizar el comportamiento actual.

No presupone:

- degradación relevante
- cuello de botella
- necesidad de optimización
- necesidad de una proyección
- necesidad de CQRS
- necesidad de Event Sourcing
- necesidad de modificar la arquitectura

---

## 8. Hipótesis

> Bajo las mismas condiciones de ejecución, al aumentar la entrada desde 30 hasta 10000 días históricos distintos, la mediana del tiempo de ejecución de la ruta actual de cálculo de streak será mayor para 10000 días que para 30 días en al menos dos de tres corridas formales independientes.

Las condiciones de ejecución mencionadas en esta hipótesis corresponden exactamente a las fijadas en:

`condiciones.md`

La hipótesis evalúa exclusivamente:

`dirección de crecimiento`

No evalúa:

`relevancia arquitectónica del crecimiento`

Por tanto, una diferencia positiva pequeña puede respaldar formalmente la hipótesis.

Esto no significa automáticamente que:

- exista un problema de rendimiento
- el impacto sea relevante
- el usuario pueda percibirlo
- se necesite una optimización
- se necesite una modificación arquitectónica

---

## 9. Variables experimentales

### Variable independiente

La variable independiente será:

`N = cantidad de días históricos distintos`

Los valores evaluados serán:

- 30
- 365
- 1000
- 2500
- 5000
- 10000

Únicamente:

- 30
- 10000

participarán en la regla formal de respaldo o refutación de la hipótesis.

Los valores:

- 365
- 1000
- 2500
- 5000

serán exclusivamente descriptivos.

Estos valores podrán utilizarse para:

- observar la forma de la curva
- visualizar comportamiento intermedio
- contextualizar la magnitud
- complementar el análisis descriptivo

No podrán utilizarse posteriormente para:

- cambiar el veredicto
- redefinir la hipótesis
- sustituir los extremos preregistrados
- reinterpretar retrospectivamente la regla de decisión

---

### Variable dependiente

La variable dependiente será:

`tiempo de ejecución de la ruta actual de cálculo de streak`

La unidad cruda utilizada será:

`nanosegundos`

El reloj de medición será:

`System.nanoTime()`

---

## 10. Operación observada y frontera temporal

La operación temporal observada será exactamente:

`activityDays + pomodoroDays`

↓

`distinct()`

↓

`sorted()`

↓

`StreakCalculator.calculate(...)`

↓

`StreakResult`

La intención es reproducir la transformación relevante de la ruta actual y no medir únicamente `StreakCalculator` de forma aislada.

---

### T0

`T0`

se capturará mediante:

`System.nanoTime()`

inmediatamente antes de iniciar:

`activityDays + pomodoroDays`

Antes de T0:

- `activityDays` deberá existir completamente
- `pomodoroDays` deberá existir completamente
- ambas listas deberán estar construidas
- ambas listas deberán utilizar el orden preregistrado
- la generación de datos deberá haber terminado

---

### T1

`T1`

se capturará mediante:

`System.nanoTime()`

inmediatamente después de que:

`StreakCalculator.calculate(...)`

retorne un:

`StreakResult`

La secuencia será:

`T0`

↓

`activityDays + pomodoroDays`

↓

`distinct()`

↓

`sorted()`

↓

`StreakCalculator.calculate(...)`

↓

`StreakResult`

↓

`T1`

↓

`validación funcional`

↓

`registro de evidencia`

---

### Fuera de T0–T1

Quedarán fuera del intervalo medido:

- generación del dataset
- construcción inicial de listas
- Room
- SQLite
- Flow
- backend
- red
- Compose
- logging
- impresión en consola
- validación funcional
- escritura de archivos
- cálculo de estadísticas

---

## 11. Criterio que respalda la hipótesis

Se realizarán:

`3 corridas formales independientes`

Cada corrida producirá una mediana para cada volumen.

La hipótesis quedará:

`RESPALDADA`

si en al menos:

`2 de 3 corridas válidas`

se cumple:

`median_10000 > median_30`

También se calculará:

`growthFactor = median_10000 / median_30`

El `growthFactor` será una métrica descriptiva.

Un:

`growthFactor > 1.0`

puede respaldar formalmente la dirección prevista.

No demuestra por sí mismo:

- relevancia práctica
- degradación problemática
- violación de un escenario de calidad
- necesidad de optimización
- necesidad de una intervención arquitectónica

---

## 12. Criterio que refuta la hipótesis

La hipótesis quedará:

`REFUTADA`

si existen tres corridas formales válidas y en al menos:

`2 de 3 corridas`

se cumple:

`median_10000 <= median_30`

Una corrida válida que contradiga la hipótesis:

- se conservará
- participará obligatoriamente en la regla
- no podrá descartarse por ser desfavorable

Una hipótesis refutada permitirá concluir únicamente:

> La tendencia preregistrada no apareció de forma reproducible bajo las condiciones evaluadas.

No permitirá concluir que:

- la implementación sea óptima
- el algoritmo tenga costo constante
- nunca pueda existir una alternativa más eficiente
- no exista ningún otro problema de rendimiento

---

## 13. Regla entre corridas y tratamiento estadístico

### Corridas independientes

Se realizarán:

`3 corridas formales independientes`

En SPIKE-03, una corrida independiente significa:

`una ejecución completa dentro de un proceso JVM nuevo`

Cada corrida seguirá:

`inicio JVM`

↓

`generación determinista de datasets`

↓

`medición del overhead del reloj`

↓

`warm-up`

↓

`mediciones formales`

↓

`registro de resultados`

↓

`fin JVM`

La corrida siguiente deberá comenzar mediante un nuevo proceso JVM.

---

### Warm-up

Para cada volumen se realizarán:

`50 invocaciones de warm-up`

Estas invocaciones:

- no participarán en la mediana
- no participarán en el P95
- no participarán en mínimo
- no participarán en máximo
- no participarán en el veredicto

Su finalidad será reducir el efecto inicial relacionado con:

- carga de clases
- compilación JIT
- inicialización del runtime

El número de invocaciones permanecerá fijo durante SPIKE-03.

---

### Observaciones formales

Por cada combinación:

`corrida + volumen`

se ejecutarán:

`100 observaciones formales`

Por cada corrida:

`6 × 100 = 600 observaciones`

En tres corridas:

`1800 observaciones formales`

antes de considerar invalidaciones.

---

### Mediana

Con 100 observaciones temporalmente válidas y ordenadas:

`mediana = (posición 50 + posición 51) / 2`

Las posiciones se numerarán desde 1.

La mediana será el estadístico utilizado para el veredicto de la hipótesis.

---

### P95

P95 será exclusivamente descriptivo.

Se calculará mediante:

`Nearest Rank`

Para:

`n = 100`

se obtiene:

`ceil(0.95 × 100) = 95`

Por tanto:

`P95 = observación ordenada en la posición 95`

P95 no podrá sustituir posteriormente a la mediana como criterio de decisión.

---

### Mínimo y máximo

También se conservarán:

- mínimo
- máximo

como métricas descriptivas.

---

### Orden de ejecución

La corrida 1 utilizará:

`30 → 365 → 1000 → 2500 → 5000 → 10000`

La corrida 2 utilizará:

`10000 → 5000 → 2500 → 1000 → 365 → 30`

La corrida 3 utilizará:

`30 → 365 → 1000 → 2500 → 5000 → 10000`

El orden queda fijado antes de las mediciones formales.

No podrá modificarse después de observar resultados.

---

### Resultado inconcluso

El resultado podrá clasificarse como:

`INCONCLUSO`

únicamente cuando no exista evidencia formal válida suficiente para aplicar las reglas de respaldo o refutación.

Esto podrá ocurrir cuando:

- exista un error de instrumentación
- exista un dataset incorrecto
- una corrida quede incompleta
- se modifique accidentalmente el objeto medido
- las condiciones entre corridas dejen de ser comparables
- exista resolución temporal insuficiente
- ocurra otro incumplimiento documentado del protocolo

`INCONCLUSO`

no podrá utilizarse para evitar una refutación obtenida con datos válidos.

---

## 14. Semilla y condiciones experimentales

La variable controlada será:

`cantidad de días históricos distintos`

No se utilizará como variable experimental:

`cantidad bruta de Activities`

Para cada valor `N`, la unión lógica de las fuentes deberá contener exactamente:

`N días distintos`

---

### Patrón temporal

Los días utilizados serán consecutivos.

Se utilizará un:

`referenceEpochDay`

fijo.

Para cada volumen `N`, la secuencia abarcará desde:

`referenceEpochDay - (N - 1)`

hasta:

`referenceEpochDay`

inclusive.

El valor concreto de `referenceEpochDay` se encuentra fijado en:

`condiciones.md`

---

### Distribución entre Activities y Focus

La distribución lógica será aproximadamente:

- 40 % solo Activities
- 40 % solo Focus
- 20 % presente en ambas fuentes

La asignación será completamente determinista.

Para cada posición `index` de la secuencia:

`index % 5 == 0`

→ Activities + Focus

`index % 5 == 1`

→ solo Activities

`index % 5 == 2`

→ solo Activities

`index % 5 == 3`

→ solo Focus

`index % 5 == 4`

→ solo Focus

Esta regla garantiza:

- participación de ambas fuentes
- solapamiento controlado
- determinismo
- aproximadamente 40 / 40 / 20
- unión lógica igual a `N`

Para volúmenes no divisibles exactamente entre cinco, las diferencias parciales serán como máximo de un día respecto a la distribución ideal.

No se utilizará aleatoriedad.

---

### Orden interno de las listas

Antes de T0:

`activityDays`

y:

`pomodoroDays`

estarán ordenadas:

`cronológicamente de forma ascendente`

es decir:

`día más antiguo → día más reciente`

El orden permanecerá idéntico para datasets equivalentes.

---

### Regeneración entre procesos JVM

Cada proceso JVM regenerará sus propias listas mediante el mismo generador determinista.

La equivalencia lógica se define mediante:

`mismo referenceEpochDay`

+

`mismo N`

+

`misma regla index % 5`

+

`mismo orden ascendente`

=

`mismas listas lógicas`

No se utilizarán archivos serializados para cargar la semilla.

La generación de datos ocurrirá antes de T0.

---

### Resultado funcional esperado

Como los días forman una secuencia consecutiva que termina en `referenceEpochDay`, para cada volumen se espera:

`current = N`

y:

`best = N`

Después de T1 se comprobará:

`current == N`

y:

`best == N`

Si alguna de estas condiciones falla:

`FUNCTIONAL_RESULT = FAIL`

La observación:

- se conservará
- permanecerá en los datos crudos
- no participará en los estadísticos temporales válidos

Además:

`cualquier FUNCTIONAL_FAIL`

dentro de una corrida formal invalidará:

`la corrida formal completa`

La corrida inválida:

- se conservará
- tendrá una causa documentada
- no participará en el veredicto
- deberá repetirse completamente mediante un nuevo proceso JVM

---

### Medición del overhead del reloj

Cada proceso JVM realizará antes de las mediciones formales:

`10000 mediciones`

del overhead de:

`System.nanoTime()`

Cada medición utilizará:

`clockT0 = System.nanoTime()`

seguido inmediatamente por:

`clockT1 = System.nanoTime()`

y:

`clockOverheadNs = clockT1 - clockT0`

Se calculará:

`medianClockOverheadNs`

Esta medición no forma parte de la hipótesis.

Se utilizará únicamente como control de resolución de la instrumentación.

---

### Criterio de resolución insuficiente

Se considerará que el escenario de 30 días presenta separación temporal insuficiente respecto al overhead del reloj cuando, en al menos dos de las tres corridas válidas, se cumpla:

`median_30 <= 10 × medianClockOverheadNs`

En ese caso:

`SPIKE-03 = INCONCLUSO POR RESOLUCIÓN INSUFICIENTE`

El factor diez constituye:

`una regla experimental preregistrada específicamente para SPIKE-03`

No se presenta como una regla universal de microbenchmarking.

---

### Volúmenes de estrés

Los escenarios:

- 2500
- 5000
- 10000

se consideran:

`escenarios de estrés algorítmico`

No se presentarán como representación de un historial habitual de un usuario de RachaPro.

---

## 15. Alcance

SPIKE-03 incluye exclusivamente:

- construir un harness experimental
- generar datasets deterministas
- reproducir la ruta actual de cálculo seleccionada
- combinar `activityDays` y `pomodoroDays`
- ejecutar `distinct()`
- ejecutar `sorted()`
- invocar `StreakCalculator.calculate(...)`
- medir mediante `System.nanoTime()`
- medir el overhead del reloj
- realizar warm-up
- ejecutar observaciones formales
- validar `current`
- validar `best`
- registrar datos crudos
- conservar evidencia funcional
- calcular mínimo
- calcular mediana
- calcular P95
- calcular máximo
- calcular `growthFactor`
- aplicar las reglas preregistradas
- documentar limitaciones
- producir un veredicto experimental

---

## 16. Fuera de alcance

SPIKE-03 no incluye:

- optimizar `StreakCalculator`
- eliminar `distinct()`
- eliminar `sorted()`
- modificar la semántica funcional de streak
- mover el cálculo a SQL
- modificar consultas Room productivas
- crear consultas productivas nuevas
- crear caché
- crear una proyección de lectura
- mover Progress al backend
- crear `/api/progress`
- adoptar CQRS
- adoptar Event Sourcing
- adoptar Kafka
- adoptar RabbitMQ
- adoptar Google Pub/Sub
- migrar a microservicios
- cambiar el modelo de persistencia
- modificar funcionalmente Activities
- modificar funcionalmente Focus
- modificar Reminders
- decidir ownership de Achievement
- medir Room
- medir SQLite
- medir Flow
- medir backend
- medir red
- medir Compose
- medir rendimiento end-to-end de Progress

Cualquier posible intervención deberá evaluarse posteriormente mediante otra decisión o experimento.

---

## 17. Riesgos y supuestos

### R-01 — JIT

La JVM puede modificar el comportamiento de las primeras ejecuciones.

Control:

- 50 invocaciones de warm-up por volumen
- nuevo proceso JVM por corrida formal

---

### R-02 — Ruido del sistema

Otros procesos del sistema operativo pueden introducir variabilidad temporal.

Control:

- mismo equipo
- mismas condiciones documentadas
- tres corridas independientes
- conservación de mínimo, mediana, P95 y máximo

---

### R-03 — Costo del reloj

Una operación muy corta puede encontrarse demasiado cerca del costo de la propia instrumentación.

Control:

- 10000 mediciones de overhead en cada JVM
- cálculo de `medianClockOverheadNs`
- aplicación del criterio:
  `median_30 <= 10 × medianClockOverheadNs`

---

### R-04 — Semilla incorrecta

Una gran cantidad de registros concentrados en pocos días no ejercitaría correctamente la variable elegida.

Control:

- controlar días históricos distintos
- verificar exactamente `N` días en la unión

---

### R-05 — Solapamiento variable

Cambiar el solapamiento entre Activities y Focus puede alterar el trabajo realizado por `distinct()`.

Control:

- distribución fija mediante `index % 5`

---

### R-06 — Orden de entrada variable

Modificar el orden de las listas puede alterar el trabajo necesario para `sorted()`.

Control:

- listas siempre ascendentes antes de T0

---

### R-07 — Regeneración inconsistente

Generadores distintos entre procesos JVM podrían producir entradas diferentes.

Control:

- mismo algoritmo determinista
- mismo `referenceEpochDay`
- misma regla `index % 5`
- mismo orden

---

### R-08 — Contaminación del intervalo

Generación, logging, validación u otras operaciones podrían incluirse accidentalmente dentro de T0–T1.

Control:

- generación antes de T0
- validación después de T1
- escritura de evidencia después de T1
- frontera temporal fija

---

### R-09 — Fallo funcional

Un tiempo aparentemente favorable podría corresponder a un resultado incorrecto.

Control:

- comprobar `current == N`
- comprobar `best == N`
- registrar `FUNCTIONAL_FAIL`
- invalidar la corrida completa ante cualquier fallo funcional

---

### R-10 — Interpretación excesiva

Una diferencia positiva podría presentarse incorrectamente como evidencia de un problema arquitectónico.

Control:

- hipótesis exclusivamente direccional
- ausencia de umbral de relevancia arquitectónica
- `growthFactor` descriptivo
- separación entre caracterización y decisión posterior

---

### R-11 — Escenarios extremos

10000 días no representan necesariamente un historial habitual.

Control:

- declararlo explícitamente como estrés algorítmico

---

### Supuestos

Se asume que:

- las listas generadas representan adecuadamente la semántica de los días que llegan a la ruta estudiada
- el harness conservará la transformación funcional observada
- `StreakCalculator` permanecerá funcionalmente sin cambios
- se utilizará el mismo equipo
- se utilizará el mismo sistema operativo
- se utilizará la misma JVM
- se utilizará el mismo Gradle
- se utilizará el mismo commit experimental
- `referenceEpochDay` permanecerá fijo
- el mismo generador será utilizado
- el orden de las listas permanecerá fijo
- los datasets se generarán antes de T0
- `System.nanoTime()` se utilizará de manera idéntica en todas las corridas

---

## 18. Evidencia a conservar

SPIKE-03 conservará como mínimo:

- `00-preregistro.md`
- `condiciones.md`
- commit histórico del borrador
- commit base del preregistro definitivo
- commit definitivo de preregistro
- commit experimental
- código del harness
- generador de datos
- scripts utilizados para análisis
- datos crudos
- mediciones de overhead
- resultados por observación
- resultados funcionales
- estadísticas por corrida
- estadísticas por volumen
- `growthFactor`
- registros de `FUNCTIONAL_FAIL`
- corridas inválidas
- causas de invalidación
- logs necesarios
- limitaciones
- resultado de la hipótesis
- veredicto final

Una corrida inválida no será eliminada.

Su evidencia deberá conservar:

- identificador
- causa de invalidación
- resultados obtenidos
- relación con su repetición posterior

---

## 19. Resultados

**VACÍO ANTES DEL EXPERIMENTO**

No existen todavía resultados formales de SPIKE-03.

Esta sección no deberá completarse antes de:

- existir el commit definitivo de preregistro
- existir la implementación del harness
- existir el commit experimental
- ejecutar las corridas formales

No deberán registrarse aquí mediciones exploratorias como si fueran evidencia formal del spike.

---

## 20. Veredicto

**VACÍO ANTES DEL EXPERIMENTO**

Estados permitidos:

- RESPALDADA
- REFUTADA
- INCONCLUSA

El veredicto deberá derivarse exclusivamente de las reglas preregistradas.

### RESPALDADA

Si:

`median_10000 > median_30`

en al menos:

`2 de 3 corridas válidas`

y no aplica el criterio de resolución insuficiente.

### REFUTADA

Si:

`median_10000 <= median_30`

en al menos:

`2 de 3 corridas válidas`

y no aplica el criterio de resolución insuficiente.

### INCONCLUSA

Únicamente cuando:

- no existan suficientes corridas válidas
- un incumplimiento del protocolo impida aplicar las reglas
- aplique el criterio de resolución insuficiente
- otra condición documentada invalide la comparación

Los volúmenes:

- 365
- 1000
- 2500
- 5000

serán descriptivos.

No podrán modificar ni reinterpretar retrospectivamente el veredicto.

---

### Límites de interpretación

Un resultado:

`RESPALDADA`

no significa automáticamente:

- que exista un problema de rendimiento
- que exista una violación de un escenario de calidad
- que sea necesaria una optimización
- que CQRS quede justificado
- que Event Sourcing quede justificado
- que sea necesaria una proyección
- que deba modificarse la arquitectura

Un resultado:

`REFUTADA`

no significa:

- que la implementación sea óptima
- que su costo sea constante
- que no pueda existir una implementación alternativa
- que no exista ningún otro problema de rendimiento

Un resultado:

`INCONCLUSA`

no podrá utilizarse para evitar una refutación producida mediante evidencia válida.

---

## 21. Trazabilidad Git

SPIKE-03 conserva explícitamente la diferencia entre:

- borrador histórico
- base del preregistro definitivo
- commit definitivo de preregistro
- commit experimental
- resultados

---

### Borrador histórico

El commit:

`63d0db27dc3d0b02bc1320b5be09339283fd9fcd`

corresponde al borrador previo de SPIKE-03.

Mensaje:

`docs(spike-03): agrega borrador de caracterizacion de racha`

Este commit fue posteriormente integrado en `master` mediante el PR #46.

No se reinterpretará retrospectivamente como el preregistro definitivo.

---

### Master base de la frontera definitiva

La rama:

`docs/spike-03-preregistro-definitivo`

fue creada desde:

`1536d331e96d7f73dfb477252a2a336bff8b6347`

correspondiente al `origin/master` actualizado después de integrar el borrador histórico.

Este commit constituye:

`la base Git de la versión definitiva del preregistro`

No constituye todavía:

`el commit formal de preregistro`

---

### Commit definitivo de preregistro

El commit que incorpore esta versión revisada de:

- `00-preregistro.md`
- `condiciones.md`

constituirá:

`la frontera formal de preregistro de SPIKE-03`

Su hash será registrado posteriormente dentro de la evidencia de trazabilidad del experimento.

No se modificarán retrospectivamente las decisiones preregistradas únicamente para incorporar dicho hash.

---

### Commit experimental

Actualmente:

`TODAVÍA NO EXISTE`

El commit experimental deberá ser posterior al commit definitivo de preregistro.

Contendrá el harness utilizado para ejecutar las mediciones formales.

---

### Secuencia obligatoria

La secuencia temporal será:

`63d0db2`

↓

`borrador histórico de SPIKE-03`

↓

`1536d33`

↓

`master base del preregistro definitivo`

↓

`rama docs/spike-03-preregistro-definitivo`

↓

`00-preregistro.md + condiciones.md`

↓

`commit definitivo de preregistro`

↓

`implementación del harness`

↓

`commit experimental`

↓

`ejecución formal`

↓

`datos crudos`

↓

`análisis`

↓

`veredicto`

↓

`commit de resultados`

No podrán existir mediciones formales de SPIKE-03 anteriores al commit definitivo de preregistro.

La frontera temporal deberá poder demostrarse mediante Git.

---

## Estado previo al commit definitivo

El estado metodológico actual es:

`LISTO PARA PRERREGISTRO — NO EJECUTADO`

Todavía no existen:

- commit definitivo de preregistro
- harness formal
- commit experimental
- mediciones formales
- resultados
- veredicto
- decisión de optimización

SPIKE-03 permanece bloqueado para ejecución hasta que exista el commit definitivo de preregistro.