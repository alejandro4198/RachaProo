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
- Rama prevista: `exp/spike-03-streak-characterization`

Este documento define el protocolo experimental antes de implementar el harness y antes de realizar mediciones formales.

El experimento solo se considerará formalmente preregistrado cuando este documento y `condiciones.md` formen parte de un commit anterior a:

- la implementación del harness
- la instrumentación experimental
- la ejecución formal
- la obtención de resultados
- el veredicto

Hasta que exista dicho commit, el estado correcto del experimento es:

`LISTO PARA PRERREGISTRO — NO EJECUTADO`

---

## 2. Evidencia de partida

La inspección del código actual de RachaPro demostró que Progress no realiza todas sus estadísticas cargando colecciones completas y agregándolas posteriormente en Kotlin.

En Activities existen consultas Room especializadas para:

- contar Activities completadas
- contar Activities completadas dentro de un rango
- agrupar Activities completadas por día

Estas consultas utilizan directamente operaciones como:

- `COUNT`
- `BETWEEN`
- `GROUP BY`

En Focus existen consultas especializadas para:

- contar sesiones FOCUS completadas
- sumar segundos de sesiones FOCUS completadas
- contar sesiones dentro de un rango
- sumar segundos dentro de un rango
- agrupar estadísticas por día

Estas consultas utilizan:

- `COUNT`
- `SUM`
- `BETWEEN`
- `GROUP BY`

Por tanto, la evidencia actual no permite afirmar que Progress, en general, necesite una representación de lectura adicional.

La ruta de cálculo de streak sí presenta un comportamiento diferente.

`ProgressViewModel` obtiene los días completados provenientes de Activities y Focus mediante:

`ActivityRepository.observeCompletedDays(userId)`

y:

`PomodoroRepository.observeCompletedFocusDays(userId)`

Posteriormente combina ambas colecciones mediante:

`activityDays + pomodoroDays`

y aplica:

`distinct()`

`sorted()`

El resultado se entrega a:

`StreakCalculator.calculate()`

A su vez, `StreakCalculator` vuelve a ejecutar:

`filter`

`distinct`

`sorted`

y recorre los días para producir:

- `currentStreak`
- `bestStreak`

La evidencia del código permite afirmar que esta ruta procesa una colección histórica cuyo tamaño depende de la cantidad de días históricos distintos que llegan al cálculo.

La evidencia actual no demuestra que esta ruta sea lenta ni que represente un problema de rendimiento.

---

## 3. Escenario de calidad relacionado

SPIKE-03 se relaciona con el atributo de calidad:

`Rendimiento`

Rendimiento ha sido priorizado previamente dentro de los atributos de calidad del proyecto.

Sin embargo, este spike no parte de una violación demostrada de un escenario temporal existente.

El propósito es producir evidencia experimental sobre una operación concreta antes de determinar si existe un problema que amerite una intervención posterior.

Por tanto, SPIKE-03 es un experimento de:

`caracterización`

y no un experimento de:

`optimización`

La secuencia metodológica será:

`medir implementación actual`

↓

`caracterizar comportamiento`

↓

`evaluar magnitud`

↓

`solo si existe evidencia suficiente, considerar otro experimento`

---

## 4. Comparabilidad con evidencia previa

RachaPro dispone de evidencia experimental previa.

### EXP-001

EXP-001 evaluó carga de Activities en Android y Room.

La variable observada fue:

`inicio de loadData()`

hasta:

`ActivitiesUiState.Success`

La ampliación histórica documentó:

- mediana aproximada de 1325 ms
- P95 aproximado de 1621 ms
- mínimo aproximado de 1305 ms
- máximo aproximado de 1666 ms

EXP-001 no mide la ruta aislada de cálculo de streak.

Por tanto:

`EXP-001 ≠ BASELINE DIRECTAMENTE COMPARABLE`

---

### EXP-002

EXP-002 evaluó:

`POST /api/activities`

mediante la métrica:

`activity_create_duration`

Sus resultados históricos incluyen:

- promedio aproximado de 11.16 ms
- mediana aproximada de 8.48 ms
- P95 aproximado de 31.50 ms
- máximo aproximado de 46.02 ms

EXP-002 tampoco mide el cálculo de streak.

Por tanto:

`EXP-002 ≠ BASELINE DIRECTAMENTE COMPARABLE`

---

### Otros experimentos

Otros experimentos y pruebas posteriores han utilizado variables como:

- cantidad de Activities
- usuarios
- solicitudes
- concurrencia
- operaciones HTTP

Estas variables tampoco equivalen a:

`cantidad de días históricos distintos`

que es la entrada relevante para el cálculo estudiado en SPIKE-03.

---

### Conclusión de comparabilidad

Para SPIKE-03:

`NO EXISTE UNA LÍNEA BASE DIRECTAMENTE COMPARABLE`

La evidencia previa se utilizará como:

`ANTECEDENTE METODOLÓGICO`

y no como referencia numérica directa.

SPIKE-03 pretende producir la primera baseline controlada de esta operación específica.

---

## 5. Contexto arquitectónico y decisiones previas

La arquitectura vigente de RachaPro continúa siendo:

`monolito modular`

Los contextos principales documentados incluyen:

- Identity
- Activities
- Focus
- Progress
- Reminders

La convención del Context Map es:

`A → B`

significa:

`A consume o referencia información proveniente de B`

Para Progress se mantienen conceptualmente las relaciones:

`Progress → Activities`

`Progress → Focus`

Estas relaciones no implican automáticamente:

- transferencia de ownership
- HTTP
- eventos
- asincronía
- microservicios
- CQRS
- Event Sourcing

SPIKE-03 no modifica el Context Map.

Las decisiones ADR previas continúan vigentes.

En particular, SPIKE-03 no modifica:

- la decisión de monolito modular
- las fronteras de persistencia
- las relaciones intermodulares existentes
- ADR-003 relacionado con el caso específico de `UserRegisteredV1`

La evaluación arquitectónica previa concluyó:

`CQRS → NO JUSTIFICADO TODAVÍA POR LA EVIDENCIA DISPONIBLE`

y:

`Event Sourcing → NO JUSTIFICADO TODAVÍA POR LA EVIDENCIA DISPONIBLE`

SPIKE-03 no intenta justificar ninguno de estos patrones.

---

## 6. Problema verificable

En la implementación actual de Progress, el cálculo de la racha utiliza los días históricos completados provenientes de Activities y Focus.

`ProgressViewModel` combina ambas colecciones y realiza operaciones de unión, eliminación de duplicados y ordenamiento antes de entregar el resultado a `StreakCalculator`.

A su vez, `StreakCalculator` vuelve a filtrar, eliminar duplicados y ordenar los días antes de recorrerlos para obtener la racha actual y la mejor racha.

La evidencia del código permite comprobar que esta ruta procesa una colección cuyo tamaño depende de la cantidad de días históricos distintos registrados para el usuario.

Sin embargo, actualmente no existe una medición experimental que permita determinar cómo cambia el tiempo de ejecución de esta operación cuando aumenta el número de días históricos distintos.

Por tanto, todavía no está demostrado que el cálculo actual de streak represente un problema de rendimiento ni que requiera algún cambio en su implementación.

---

## 7. Pregunta experimental

> ¿Cómo cambia el tiempo de ejecución de la implementación actual del cálculo de `currentStreak` y `bestStreak` a medida que aumenta la cantidad de días históricos distintos que debe procesar?

Esta pregunta busca caracterizar el comportamiento actual.

No presupone:

- degradación relevante
- cuello de botella
- necesidad de optimización
- necesidad de CQRS
- necesidad de una proyección
- necesidad de modificar la arquitectura

---

## 8. Hipótesis

> Bajo las mismas condiciones de ejecución, al aumentar la entrada desde 30 hasta 10000 días históricos distintos, la mediana del tiempo de ejecución de la ruta actual de cálculo de streak será mayor para 10000 días que para 30 días en al menos dos de tres corridas formales independientes.

Las condiciones de ejecución mencionadas en esta hipótesis corresponden exactamente a las definidas en:

`condiciones.md`

La hipótesis evalúa exclusivamente:

`dirección de crecimiento`

No evalúa:

`relevancia arquitectónica del crecimiento`

Una diferencia positiva mínima puede respaldar formalmente la hipótesis.

Eso no significa que exista un problema de rendimiento.

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

`30`

y:

`10000`

participarán en la regla formal de respaldo o refutación.

Los valores:

- 365
- 1000
- 2500
- 5000

serán exclusivamente descriptivos.

No podrán utilizarse posteriormente para:

- cambiar el veredicto
- redefinir la hipótesis
- reinterpretar retrospectivamente la regla de decisión

Su finalidad será:

- mostrar la forma de la curva
- contextualizar el comportamiento
- identificar tendencias intermedias
- apoyar análisis descriptivo

---

### Variable dependiente

La variable dependiente será:

`tiempo de ejecución de la ruta actual de cálculo de streak`

La unidad cruda será:

`nanosegundos`

El reloj utilizado será:

`System.nanoTime()`

---

## 10. Operación observada y T0 / T1

La operación medida incluirá exactamente:

`activityDays + pomodoroDays`

↓

`distinct()`

↓

`sorted()`

↓

`StreakCalculator.calculate(...)`

↓

`StreakResult`

---

### T0

`T0`

se capturará mediante:

`System.nanoTime()`

inmediatamente antes de ejecutar:

`activityDays + pomodoroDays`

Antes de T0:

- `activityDays` ya deberá existir
- `pomodoroDays` ya deberá existir
- ambas listas deberán estar completamente construidas
- ambas listas deberán estar en el orden preregistrado
- no deberá ejecutarse generación de datos dentro del intervalo

---

### T1

`T1`

se capturará mediante:

`System.nanoTime()`

inmediatamente después de que:

`StreakCalculator.calculate(...)`

retorne un:

`StreakResult`

La secuencia formal será:

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

`registro de resultados`

---

### Operaciones fuera de T0–T1

Quedarán fuera del intervalo temporal:

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

Cada corrida producirá una mediana para:

- 30 días
- 10000 días

La hipótesis quedará:

`RESPALDADA`

si en al menos:

`2 de 3 corridas válidas`

se cumple:

`median_10000 > median_30`

También se calculará:

`growthFactor = median_10000 / median_30`

El `growthFactor` será descriptivo.

Un:

`growthFactor > 1.0`

puede respaldar la dirección de crecimiento prevista.

No demuestra por sí mismo:

- degradación relevante
- problema de rendimiento
- necesidad de optimización
- necesidad de modificar arquitectura

---

## 12. Criterio que refuta la hipótesis

La hipótesis quedará:

`REFUTADA`

si existen tres corridas formales válidas y en al menos:

`2 de 3`

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
- el algoritmo sea de costo constante
- no pueda existir una alternativa más eficiente

---

## 13. Regla entre corridas y tratamiento estadístico

### Corridas independientes

Se realizarán:

`3 corridas formales independientes`

En SPIKE-03 una corrida independiente significa:

`un proceso JVM nuevo`

Cada corrida seguirá:

`inicio JVM`

↓

`generación determinista de datasets`

↓

`medición de overhead del reloj`

↓

`warm-up`

↓

`mediciones formales`

↓

`registro de resultados`

↓

`fin JVM`

La siguiente corrida se ejecutará mediante:

`un nuevo proceso JVM`

---

### Warm-up

Para cada volumen se ejecutarán:

`50 invocaciones de warm-up`

Estas invocaciones:

- no participarán en mediana
- no participarán en P95
- no participarán en mínimo
- no participarán en máximo
- no participarán en el veredicto

Su finalidad será reducir efectos iniciales asociados a:

- carga de clases
- JIT
- inicialización del runtime

---

### Observaciones formales

Por cada:

`corrida + volumen`

se ejecutarán:

`100 observaciones formales`

Por corrida:

`6 volúmenes × 100 observaciones = 600 observaciones`

En las tres corridas:

`1800 observaciones formales`

antes de considerar invalidaciones.

---

### Mediana

Con 100 observaciones válidas ordenadas:

`mediana = (posición 50 + posición 51) / 2`

Las posiciones se numeran desde 1.

La mediana será el estadístico utilizado por la hipótesis.

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

`P95 = observación ordenada en posición 95`

P95 no podrá sustituir a la mediana como criterio de veredicto.

---

### Mínimo y máximo

También se conservarán:

- mínimo
- máximo

como métricas descriptivas.

---

### Orden de volúmenes

La corrida 1 ejecutará:

`30 → 365 → 1000 → 2500 → 5000 → 10000`

La corrida 2 ejecutará:

`10000 → 5000 → 2500 → 1000 → 365 → 30`

La corrida 3 ejecutará:

`30 → 365 → 1000 → 2500 → 5000 → 10000`

El orden queda fijado antes de medir.

---

### Resultado inconcluso

El resultado será:

`INCONCLUSO`

únicamente cuando no exista evidencia válida suficiente para aplicar las reglas de respaldo o refutación.

Esto podrá ocurrir por:

- error de instrumentación
- dataset incorrecto
- corrida incompleta
- modificación del objeto medido
- entorno no comparable
- resolución temporal insuficiente
- otro incumplimiento documentado del protocolo

`INCONCLUSO`

no podrá utilizarse para evitar una refutación obtenida mediante datos válidos.

---

## 14. Semilla y condiciones experimentales

La variable controlada será:

`cantidad de días históricos distintos`

No se utilizará como variable principal:

`cantidad bruta de Activities`

Para cada valor `N`, la unión de las fuentes deberá contener exactamente:

`N días distintos`

---

### Patrón temporal

Los días serán consecutivos.

Para un:

`referenceEpochDay`

fijo, la unión abarcará:

`referenceEpochDay - (N - 1)`

hasta:

`referenceEpochDay`

inclusive.

---

### Distribución entre fuentes

La distribución lógica será aproximadamente:

- 40 % de días solo en Activities
- 40 % de días solo en Focus
- 20 % de días presentes en ambas fuentes

La asignación será determinista mediante el índice de cada día.

Para cada día de la secuencia:

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
- unión total equivalente a `N`

Las diferencias producidas por volúmenes no divisibles entre cinco serán como máximo de un día en cada distribución parcial.

---

### Orden de las listas

Antes de T0:

`activityDays`

y:

`pomodoroDays`

estarán ordenadas:

`cronológicamente de forma ascendente`

es decir:

`día más antiguo → día más reciente`

El orden será idéntico entre corridas equivalentes.

---

### Regeneración entre procesos JVM

Cada corrida JVM regenerará sus propias listas mediante el mismo generador determinista.

La equivalencia estará definida por:

`mismo referenceEpochDay`

+

`mismo N`

+

`misma regla index % 5`

+

`mismo orden ascendente`

=

`mismas listas lógicas`

No se utilizarán archivos serializados como fuente de datos.

La generación ocurrirá antes de T0.

---

### Resultado funcional esperado

Debido a que los días forman una secuencia consecutiva que termina en `referenceEpochDay`:

`current = N`

y:

`best = N`

Después de T1 se comprobará:

`current == N`

y:

`best == N`

Si cualquiera falla:

`FUNCTIONAL_RESULT = FAIL`

La observación:

- se conservará
- aparecerá en datos crudos
- no participará en estadísticos temporales válidos

Además:

`cualquier FUNCTIONAL_FAIL`

dentro de una corrida formal hará que:

`la corrida completa sea INVALIDA`

La corrida inválida deberá:

- conservarse
- registrar el motivo
- no participar en el veredicto
- repetirse completamente en una JVM nueva

---

### Medición del overhead del reloj

Cada proceso JVM ejecutará antes de las mediciones formales:

`10000 mediciones de overhead`

mediante:

`T0 = System.nanoTime()`

seguido inmediatamente por:

`T1 = System.nanoTime()`

Para cada medición:

`clockOverheadNs = T1 - T0`

Se calculará:

`medianClockOverheadNs`

Esta medición no forma parte de la hipótesis.

Su función es detectar una posible falta de separación entre:

- el costo del fenómeno medido
- el costo del reloj utilizado

---

### Criterio de resolución insuficiente

Se considerará que la medición de 30 días tiene separación insuficiente respecto al overhead del reloj si, en al menos dos de las tres corridas válidas, se cumple:

`median_30 <= 10 × medianClockOverheadNs`

En ese caso:

`SPIKE-03 = INCONCLUSO POR RESOLUCIÓN INSUFICIENTE`

Esta regla es un criterio experimental preregistrado para SPIKE-03.

No se presenta como una regla universal sobre microbenchmarks.

---

### Volúmenes extremos

Los escenarios:

- 2500
- 5000
- 10000

se consideran:

`estrés algorítmico`

No se presentarán como:

`uso habitual esperado de RachaPro`

---

## 15. Alcance

SPIKE-03 incluye exclusivamente:

- construir un harness experimental
- generar datasets deterministas
- reproducir la ruta actual:
  - merge
  - `distinct`
  - `sorted`
  - `StreakCalculator.calculate`
- instrumentar mediante `System.nanoTime()`
- medir overhead del reloj
- ejecutar warm-up
- ejecutar observaciones formales
- verificar `current`
- verificar `best`
- registrar datos crudos
- calcular:
  - mínimo
  - mediana
  - P95
  - máximo
  - `growthFactor`
- aplicar las reglas preregistradas
- documentar limitaciones
- producir veredicto

---

## 16. Fuera de alcance

SPIKE-03 no incluye:

- optimizar `StreakCalculator`
- eliminar `distinct`
- eliminar `sorted`
- modificar la semántica de streak
- mover cálculo a SQL
- modificar consultas Room
- crear consultas productivas nuevas
- crear caché
- mover Progress al backend
- crear `/api/progress`
- crear una proyección
- adoptar CQRS
- adoptar Event Sourcing
- utilizar Kafka
- utilizar RabbitMQ
- utilizar Google Pub/Sub
- migrar a microservicios
- modificar persistencia productiva
- modificar Activities funcionalmente
- modificar Focus funcionalmente
- modificar Reminders
- decidir ownership de Achievement
- medir Room
- medir SQLite
- medir Flow
- medir backend
- medir red
- medir Compose
- medir rendimiento end-to-end de Progress

---

## 17. Riesgos y supuestos

### R-01 — JIT

La JVM puede modificar el comportamiento de las primeras ejecuciones.

Control:

- 50 warm-ups por volumen
- nuevo proceso JVM por corrida formal

---

### R-02 — Ruido del sistema

Otros procesos del sistema operativo pueden introducir variabilidad.

Control:

- mismo equipo
- mismas condiciones conocidas
- tres corridas independientes
- conservación de mínimo, mediana, P95 y máximo

---

### R-03 — Costo del reloj

Una operación muy rápida puede quedar demasiado cerca del costo de `System.nanoTime()`.

Control:

- 10000 mediciones de overhead por JVM
- cálculo de `medianClockOverheadNs`
- criterio:
  `median_30 <= 10 × medianClockOverheadNs`

---

### R-04 — Semilla incorrecta

Una gran cantidad de Activities concentradas en pocos días no ejercitaría la variable estudiada.

Control:

- controlar `N días distintos`
- verificar la unión exacta antes de medir

---

### R-05 — Solapamiento variable

Cambiar la cantidad de días compartidos entre Activities y Focus alteraría el trabajo de `distinct()`.

Control:

- regla fija `index % 5`

---

### R-06 — Orden variable

Cambiar el orden de entrada podría alterar el costo de `sorted()`.

Control:

- listas siempre ascendentes antes de T0

---

### R-07 — Regeneración diferente

Generadores diferentes entre JVM podrían producir entradas distintas.

Control:

- mismo algoritmo determinista
- mismo `referenceEpochDay`
- misma regla de asignación

---

### R-08 — Medición accidental de tareas externas

Generación, logging o validación podrían contaminar T0–T1.

Control:

- generación previa
- logging posterior
- validación posterior a T1

---

### R-09 — Fallo funcional

Una medición rápida podría corresponder a un resultado incorrecto.

Control:

- verificar `current == N`
- verificar `best == N`
- invalidar corrida completa ante cualquier `FUNCTIONAL_FAIL`

---

### R-10 — Interpretación excesiva

Una diferencia positiva podría presentarse incorrectamente como problema arquitectónico.

Control:

- hipótesis solo direccional
- sin umbral de relevancia
- `growthFactor` descriptivo
- no derivar automáticamente optimización

---

### R-11 — Escenarios extremos

10000 días no representan necesariamente uso habitual.

Control:

- declararlo explícitamente como estrés algorítmico

---

### Supuestos

Se asume que:

- las listas generadas representan adecuadamente la semántica de días recibidos por Progress
- la ruta reproducida conserva la semántica observada en `ProgressViewModel`
- `StreakCalculator` permanece sin cambios funcionales
- el mismo equipo se utilizará en las tres corridas
- la misma JVM será utilizada
- el mismo commit experimental será utilizado
- `referenceEpochDay` permanecerá fijo
- el mismo generador será utilizado
- el orden de las listas permanecerá fijo
- los datos se generarán antes de T0
- `System.nanoTime()` será utilizado de manera idéntica

---

## 18. Evidencia a conservar

Se conservará como mínimo:

- `00-preregistro.md`
- `condiciones.md`
- commit base
- commit de preregistro
- commit experimental
- código del harness
- generador de datos
- scripts de análisis
- datos crudos
- mediciones de overhead
- estadísticas por corrida
- estadísticas por volumen
- resultados funcionales
- registros de `FUNCTIONAL_FAIL`
- evidencia de corridas inválidas
- causas de invalidación
- logs necesarios
- `growthFactor`
- limitaciones
- resultado de la hipótesis
- veredicto

Una corrida inválida no se eliminará.

Se conservará con:

- identificador
- causa
- evidencia asociada

---

## 19. Resultados

**VACÍO ANTES DEL EXPERIMENTO**

No existen todavía resultados formales de SPIKE-03.

Esta sección no deberá completarse antes de:

- commit de preregistro
- implementación del harness
- commit experimental
- ejecución formal

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

y no aplica la condición de resolución insuficiente.

### REFUTADA

Si:

`median_10000 <= median_30`

en al menos:

`2 de 3 corridas válidas`

y no aplica la condición de resolución insuficiente.

### INCONCLUSA

Si:

- no existen suficientes corridas válidas
- existe un incumplimiento del protocolo que impide comparar
- aplica el criterio de resolución insuficiente
- otra condición documentada impide aplicar correctamente las reglas

Un veredicto RESPALDADA no significa:

- problema de rendimiento
- necesidad de optimización
- necesidad de CQRS
- necesidad de Event Sourcing

Un veredicto REFUTADA no significa:

- implementación óptima
- costo constante
- imposibilidad de mejora

---

## 21. Trazabilidad Git

La secuencia obligatoria será:

`master vigente`

↓

`rama exp/spike-03-streak-characterization`

↓

`00-preregistro.md`

+

`condiciones.md`

↓

`commit de preregistro`

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

No podrán existir mediciones formales previas al commit de preregistro.

El commit base deberá registrarse en `condiciones.md`.

El commit experimental deberá ser posterior al commit de preregistro.

El commit de resultados deberá ser posterior a la ejecución.

La frontera temporal deberá poder demostrarse mediante Git.

El estado actual es:

`LISTO PARA PRERREGISTRO — NO EJECUTADO`

Solo después de que exista el commit correspondiente podrá cambiarse metodológicamente a:

`PRERREGISTRADO — NO EJECUTADO`

Todavía no existen:

- harness formal
- commit experimental
- mediciones formales
- resultados
- veredicto
- decisión de optimización

SPIKE-03 permanece bloqueado para ejecución hasta que se complete y versione el preregistro.