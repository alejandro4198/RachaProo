# Registro de delegación a IA — SPIKE-03

## 1. Identificación

- Proyecto: RachaPro
- Experimento: SPIKE-03
- Nombre: Caracterización de escalabilidad del cálculo de streak en Progress
- Tipo de evidencia: registro de trabajo delegado a IA
- Rama de implementación: `implementación-y-registro-del-spike`
- Estado al iniciar la delegación: `PRERREGISTRADO — NO EJECUTADO`

---

## 2. Frontera previa a la delegación

El protocolo experimental fue definido y versionado antes de solicitar a la IA la implementación del harness.

Commit definitivo de preregistro:

`34234e4f82b48637bdc70972cc073c2ded55f2d8`

Merge del preregistro en `master`:

`d7f8b5050f90b9315abb877198d8816e6eb18158`

La rama de implementación fue creada posteriormente desde el estado de `master` que ya contenía el preregistro.

Por tanto, la IA no recibió autoridad para modificar retrospectivamente:

- problema experimental
- pregunta experimental
- hipótesis
- variable independiente
- variable dependiente
- semilla
- fecha de referencia
- T0
- T1
- cantidad de warm-ups
- cantidad de observaciones
- cantidad de corridas
- orden de los volúmenes
- reglas de respaldo o refutación
- criterio de resolución insuficiente

---

## 3. Trabajo delegado

Se delegó a IA exclusivamente la mecánica de implementación del harness experimental correspondiente al protocolo previamente definido.

La implementación solicitada comprende:

- generación determinista de datasets
- instrumentación mediante `System.nanoTime()`
- medición separada del overhead del reloj
- warm-up
- captura de observaciones formales
- validación funcional posterior a T1
- persistencia de evidencia cruda
- selección explícita de corrida
- script de lanzamiento de una corrida independiente
- registro de identidad del proceso JVM

---

## 4. Archivos autorizados

Se autorizó crear exclusivamente:

- `app/src/test/java/com/example/rachapro/experiment/spike03/Spike03DatasetFactory.kt`
- `app/src/test/java/com/example/rachapro/experiment/spike03/Spike03RawRecord.kt`
- `app/src/test/java/com/example/rachapro/experiment/spike03/Spike03CsvWriter.kt`
- `app/src/test/java/com/example/rachapro/experiment/spike03/Spike03HarnessTest.kt`
- `scripts/spike03/run-spike03.ps1`
- `experimentos/spike-03-caracterizacion-racha/registro-delegacion-ia.md`

---

## 5. Archivos productivos autorizados para modificación

`NINGUNO`

Quedaron explícitamente fuera de modificación:

- `StreakCalculator.kt`
- `ProgressViewModel.kt`
- `ActivityRepository`
- `PomodoroRepository`
- `ActivityDao`
- `PomodoroSessionDao`
- entidades productivas
- Room
- backend
- UI
- navegación
- autenticación
- contratos intermodulares
- ADR
- Context Map

---

## 6. Restricciones impuestas a la IA

La IA no fue autorizada para:

- rediseñar el sistema
- optimizar `StreakCalculator`
- eliminar `distinct()`
- eliminar `sorted()`
- cambiar la fecha de referencia
- cambiar los volúmenes
- introducir aleatoriedad
- modificar la regla `index % 5`
- modificar T0
- modificar T1
- incluir generación de datos dentro de T0–T1
- incluir logging dentro de T0–T1
- incluir escritura de archivos dentro de T0–T1
- incluir validación funcional dentro de T0–T1
- cambiar el número de warm-ups
- cambiar el número de observaciones
- cambiar el número de mediciones de overhead
- modificar el orden preregistrado de las corridas
- interpretar resultados
- declarar un cuello de botella
- declarar la hipótesis respaldada, refutada o inconclusa
- ejecutar las corridas formales durante la fase de implementación

---

## 7. Criterios utilizados para auditar la implementación

Se utilizarán los siguientes identificadores operativos:

- CA-01: reproducir exactamente `merge → distinct → sorted → calculate`
- CA-02: usar `N = 30, 365, 1000, 2500, 5000, 10000`
- CA-03: usar fecha fija `2026-10-07`
- CA-04: generar semilla determinista mediante `index % 5` y listas ascendentes
- CA-05: medir 10000 overheads mediante `clockT0` y `clockT1`
- CA-06: ejecutar 50 warm-ups por volumen
- CA-07: ejecutar 100 observaciones formales por volumen
- CA-08: capturar T0 y T1 únicamente alrededor de la operación preregistrada
- CA-09: validar después de T1 `current == N && best == N`
- CA-10: registrar evidencia fuera de T0–T1
- CA-11: respetar el orden específico de corrida 1, 2 y 3
- CA-12: ejecutar cada corrida en un proceso JVM nuevo y conservar evidencia de identidad del proceso
- CA-13: registrar explícitamente el trabajo delegado a IA

Estos identificadores facilitan la auditoría de implementación.

No sustituyen ni modifican el preregistro formal.

---

## 8. Implementación propuesta por IA

La IA propuso separar las responsabilidades en:

### `Spike03DatasetFactory.kt`

Responsable exclusivamente de:

- fecha fija
- generación de días consecutivos
- distribución Activities / Focus
- determinismo
- validación estructural de la semilla

### `Spike03RawRecord.kt`

Responsable exclusivamente de representar:

- observaciones temporales
- resultados funcionales
- overhead
- metadatos de corrida

### `Spike03CsvWriter.kt`

Responsable exclusivamente de persistir evidencia después de las mediciones.

### `Spike03HarnessTest.kt`

Responsable de:

- seleccionar la corrida
- medir overhead
- realizar warm-up
- realizar observaciones formales
- ejecutar la ruta preregistrada
- validar resultados después de T1
- preservar evidencia
- registrar identidad JVM

### `run-spike03.ps1`

Responsable de:

- seleccionar una única corrida
- iniciar una invocación Gradle independiente
- proporcionar variables de entorno
- proteger contra una ejecución formal accidental

---

## 9. Decisiones de seguridad experimental añadidas por implementación

Se incorporó una protección para impedir que una ejecución ordinaria de tests inicie accidentalmente una corrida formal.

El harness solo ejecutará la corrida cuando exista:

`SPIKE03_FORMAL=true`

y un identificador válido:

`SPIKE03_RUN=1`

o:

`SPIKE03_RUN=2`

o:

`SPIKE03_RUN=3`

Esta protección no modifica:

- la semilla
- la operación medida
- T0
- T1
- los volúmenes
- las observaciones
- los warm-ups
- el orden de ejecución

Su finalidad exclusiva es evitar una ejecución experimental accidental durante compilación y auditoría.

---

## 10. Evidencia de independencia entre JVM

Cada corrida registrará fuera de T0–T1:

- `runId`
- PID del proceso JVM
- hora de inicio de la JVM
- hora de inicio de la corrida
- `java.version`
- `java.vendor`
- sistema operativo

Después de ejecutar las tres corridas, estos datos deberán utilizarse para comprobar factual y posteriormente que las corridas fueron realizadas en procesos JVM distintos.

La independencia no se dará por demostrada únicamente por haber ejecutado tres comandos.

---

## 11. Supuestos de la implementación

La implementación parte de los siguientes supuestos:

- `StreakCalculator` continúa disponible desde el source set de tests JVM
- `:app:testDebugUnitTest` continúa siendo la task utilizada
- el proceso de test JVM dispone de Java compatible con `ProcessHandle`
- el sistema de archivos permite escribir la evidencia en la ruta indicada
- no se modifica el preregistro después de esta implementación
- el mismo commit experimental será utilizado durante las corridas formales

---

## 12. Elementos todavía no verificados al momento de delegar

Antes de ejecutar una corrida formal todavía deberá verificarse:

- que todos los nuevos archivos compilan
- que los tests JVM existentes continúan pasando
- que la ruta T0–T1 implementada coincide literalmente con el preregistro
- que ninguna escritura de archivos ocurre dentro de T0–T1
- que la generación del dataset ocurre antes de T0
- que la validación funcional ocurre después de T1
- que los seis datasets contienen exactamente N días distintos
- que la distribución entre fuentes es la preregistrada
- que cada corrida produce exactamente 600 observaciones formales
- que se generan exactamente 10000 observaciones de overhead
- que una invocación formal genera evidencia únicamente para una corrida
- que el script no ejecuta automáticamente las tres corridas
- que los PID registrados en las corridas formales son distintos

---

## 13. Estado posterior a la implementación propuesta

La existencia del código del harness no constituye una ejecución experimental.

Antes de realizar mediciones formales deberá existir:

1. compilación satisfactoria
2. auditoría del harness contra el preregistro
3. verificación de que no se modificó código productivo
4. commit experimental independiente y posterior al preregistro

Hasta completar estos controles, el estado continúa siendo:

`PRERREGISTRADO — NO EJECUTADO`
