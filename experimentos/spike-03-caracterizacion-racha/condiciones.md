# Condiciones de ejecución — SPIKE-03

## 1. Identificación

- Proyecto: RachaPro
- Experimento: SPIKE-03
- Nombre: Caracterización de escalabilidad del cálculo de streak en Progress
- Documento asociado: `00-preregistro.md`
- Estado: CONDICIONES DEFINIDAS — NO EJECUTADO
- Rama de preregistro definitivo: `docs/spike-03-preregistro-definitivo`

Este documento fija las condiciones conocidas y controlables antes de implementar el harness y antes de ejecutar las mediciones formales.

No contiene resultados experimentales.

---

## 2. Trazabilidad Git

### Borrador histórico de SPIKE-03

Commit:

`63d0db27dc3d0b02bc1320b5be09339283fd9fcd`

Mensaje:

`docs(spike-03): agrega borrador de caracterizacion de racha`

Este commit corresponde a una versión previa de trabajo del preregistro.

Fue integrado posteriormente en `master` mediante el PR #46.

No representa la frontera formal definitiva del preregistro utilizado para las futuras mediciones.

---

### Master base del preregistro definitivo

Commit:

`1536d331e96d7f73dfb477252a2a336bff8b6347`

Este commit corresponde al estado de `origin/master` desde el cual se creó:

`docs/spike-03-preregistro-definitivo`

La versión definitiva del preregistro deberá ser posterior a este commit y anterior a cualquier implementación del harness o medición formal.

---

### Commit de preregistro definitivo

El commit que incorpore esta versión revisada de:

- `00-preregistro.md`
- `condiciones.md`

constituirá la frontera formal de preregistro de SPIKE-03.

Su hash será registrado posteriormente junto con la evidencia de trazabilidad del experimento.

No se modificarán retrospectivamente las decisiones preregistradas únicamente para incorporar dicho hash.

---

### Commit experimental

`TODAVÍA NO EXISTE`

Será un commit posterior al preregistro definitivo.

Contendrá la implementación del harness utilizado para ejecutar SPIKE-03.

El commit experimental no podrá anteceder al commit definitivo de preregistro.

---

## 3. Equipo físico

Todas las corridas formales se ejecutarán en el mismo equipo físico.

### Sistema operativo

- Producto: Microsoft Windows 11 Pro for Workstations
- Versión: 10.0.26200
- Build: 26200
- Arquitectura: 64 bits

### Procesador

- Modelo: Intel(R) Xeon(R) E-2224G CPU @ 3.50GHz
- Núcleos físicos: 4
- Procesadores lógicos: 4

### Memoria RAM

- Memoria física total reportada: 31.84 GB

---

## 4. Java y JVM

### JAVA_HOME

`C:\Program Files\Android\Android Studio1\jbr`

### Ejecutable Java

`C:\Program Files\Android\Android Studio1\jbr\bin\java.exe`

### Versión

`OpenJDK 25.0.2`

### Runtime

`OpenJDK Runtime Environment`

Build:

`25.0.2+-15348964-b329.117`

### JVM

`OpenJDK 64-Bit Server VM`

Build:

`25.0.2+-15348964-b329.117`

La misma distribución y versión de Java deberán utilizarse durante las tres corridas formales.

No se cambiará `JAVA_HOME` entre corridas.

---

## 5. Gradle

Versión:

`Gradle 9.5.0`

Build time:

`2026-04-28 12:05:30 UTC`

Revision:

`3fe117d68f3907790f3809f121aa36303a9151f8`

Kotlin reportado por Gradle:

`2.3.20`

Groovy:

`4.0.29`

Ant:

`1.10.15`

Launcher JVM:

`25.0.2`

Proveedor:

`JetBrains s.r.o.`

Sistema operativo reportado por Gradle:

`Windows 11 10.0 amd64`

La misma versión de Gradle deberá utilizarse en todas las corridas formales.

---

## 6. Naturaleza de las corridas

Se realizarán:

`3 corridas formales independientes`

Una corrida independiente significa:

`un proceso JVM nuevo`

Cada corrida seguirá:

`inicio de JVM`

↓

`generación determinista de datasets`

↓

`medición de overhead de System.nanoTime()`

↓

`warm-up`

↓

`mediciones formales`

↓

`registro de resultados`

↓

`fin de JVM`

La corrida siguiente deberá iniciar en otro proceso JVM.

---

## 7. Variable experimental

La variable independiente será:

`N = cantidad de días históricos distintos`

Los valores serán:

- 30
- 365
- 1000
- 2500
- 5000
- 10000

Únicamente:

- 30
- 10000

participarán en la decisión formal de la hipótesis.

Los valores intermedios serán exclusivamente descriptivos.

No podrán modificar ni reinterpretar posteriormente el veredicto.

---

## 8. Fecha de referencia

Se utilizará una fecha fija durante todo el experimento.

Fecha:

`2026-10-07`

El harness obtendrá a partir de esta fecha un:

`referenceEpochDay`

constante.

No se utilizará `LocalDate.now()` como valor variable durante las mediciones formales.

Para un volumen `N`, la secuencia abarcará:

`referenceEpochDay - (N - 1)`

hasta:

`referenceEpochDay`

inclusive.

---

## 9. Semilla

Para cada valor `N`, la unión de:

- `activityDays`
- `pomodoroDays`

contendrá exactamente:

`N días distintos`

Los días serán consecutivos.

La asignación entre fuentes será determinista mediante:

`index % 5`

Regla:

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

Esto produce aproximadamente:

- 40 % solo Activities
- 40 % solo Focus
- 20 % en ambas fuentes

Para valores no divisibles exactamente entre cinco, la diferencia será como máximo de un día por categoría parcial.

No se utilizará generación aleatoria.

---

## 10. Orden de entrada

Antes de T0:

`activityDays`

y:

`pomodoroDays`

estarán ordenadas:

`cronológicamente de forma ascendente`

es decir:

`día más antiguo → día más reciente`

Este orden permanecerá fijo en todas las corridas.

---

## 11. Regeneración de datasets

Cada proceso JVM generará nuevamente sus listas.

La generación será determinista.

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

No se utilizarán archivos serializados como fuente de los datasets.

La generación se realizará antes de T0.

---

## 12. Operación medida

La operación temporal incluida será exactamente:

`activityDays + pomodoroDays`

↓

`distinct()`

↓

`sorted()`

↓

`StreakCalculator.calculate(...)`

↓

`StreakResult`

No se incluirán:

- generación de datasets
- Room
- SQLite
- Flow
- backend
- red
- Compose
- logging
- escritura de resultados
- validación funcional posterior

---

## 13. Instrumentación principal

El reloj utilizado para la operación experimental será:

`System.nanoTime()`

### T0

Se capturará inmediatamente antes de:

`activityDays + pomodoroDays`

### T1

Se capturará inmediatamente después de que:

`StreakCalculator.calculate(...)`

retorne el:

`StreakResult`

La duración principal será:

`elapsedNs = T1 - T0`

La validación funcional se ejecutará después de T1.

Los identificadores:

- `T0`
- `T1`

quedan reservados para la medición de la operación experimental principal.

---

## 14. Resultado funcional esperado

Para cada volumen:

`current = N`

y:

`best = N`

Después de T1 se validará:

`current == N`

y:

`best == N`

Si cualquiera de estas condiciones falla:

`FUNCTIONAL_RESULT = FAIL`

La observación:

- se conservará en los datos crudos
- no participará en los estadísticos temporales válidos

Además:

`cualquier FUNCTIONAL_FAIL`

invalidará la corrida formal completa.

La corrida:

- se conservará
- tendrá una causa documentada
- no participará en el veredicto
- deberá repetirse completamente en un proceso JVM nuevo

---

## 15. Warm-up

Por cada combinación:

`corrida + volumen`

se ejecutarán:

`50 invocaciones`

de warm-up.

Estas observaciones no participarán en:

- mínimo
- mediana
- P95
- máximo
- veredicto

Su finalidad es reducir efectos iniciales asociados con:

- carga de clases
- compilación JIT
- inicialización del runtime

---

## 16. Observaciones formales

Después del warm-up se ejecutarán:

`100 observaciones formales`

por volumen.

Por corrida:

`6 × 100 = 600 observaciones`

En las tres corridas:

`1800 observaciones formales`

antes de considerar invalidaciones.

---

## 17. Medición del overhead del reloj

Antes de las mediciones formales, cada proceso JVM ejecutará:

`10000 mediciones`

del overhead de:

`System.nanoTime()`

Los identificadores utilizados para esta medición serán diferentes de los T0 y T1 de la operación principal.

Cada observación de overhead se obtendrá mediante:

`clockT0 = System.nanoTime()`

seguido inmediatamente por:

`clockT1 = System.nanoTime()`

y:

`clockOverheadNs = clockT1 - clockT0`

Se calculará:

`medianClockOverheadNs`

Esta medición se utilizará únicamente como control de resolución de la instrumentación.

Los valores:

- `clockT0`
- `clockT1`

no representan las fronteras temporales de la operación experimental principal.

---

## 18. Criterio de resolución insuficiente

Se considerará que la medición de 30 días tiene separación insuficiente respecto al overhead del reloj si, en al menos dos de las tres corridas válidas, se cumple:

`median_30 <= 10 × medianClockOverheadNs`

En ese caso:

`SPIKE-03 = INCONCLUSO POR RESOLUCIÓN INSUFICIENTE`

Este factor de diez es una regla experimental elegida y preregistrada específicamente para SPIKE-03.

No se presenta como una regla universal de microbenchmarking.

---

## 19. Orden de ejecución

### Corrida 1

`30 → 365 → 1000 → 2500 → 5000 → 10000`

### Corrida 2

`10000 → 5000 → 2500 → 1000 → 365 → 30`

### Corrida 3

`30 → 365 → 1000 → 2500 → 5000 → 10000`

El orden no podrá cambiar después del inicio de las mediciones formales.

---

## 20. Estadísticos

Para cada combinación:

`corrida + volumen`

se calcularán:

- mínimo
- mediana
- P95
- máximo

### Mediana

Con 100 observaciones válidas:

`mediana = (posición 50 + posición 51) / 2`

usando posiciones numeradas desde 1.

La mediana será el estadístico decisor de la hipótesis.

### P95

Se utilizará:

`Nearest Rank`

Para:

`n = 100`

se obtiene:

`ceil(0.95 × 100) = 95`

Por tanto:

`P95 = posición 95`

P95 será exclusivamente descriptivo.

No podrá sustituir a la mediana en el veredicto.

### Factor de crecimiento

También se calculará:

`growthFactor = median_10000 / median_30`

Este valor será descriptivo y no constituye un umbral arquitectónico.

---

## 21. Regla de decisión

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

Únicamente si:

- no existen suficientes corridas válidas
- existe un incumplimiento del protocolo
- aplica el criterio de resolución insuficiente
- otra condición documentada impide aplicar correctamente la regla

Los volúmenes intermedios no podrán modificar ni reinterpretar el veredicto.

---

## 22. Condiciones que deben permanecer fijas

Durante las tres corridas deberán mantenerse:

- mismo equipo físico
- mismo sistema operativo
- mismo Java
- mismo `JAVA_HOME`
- misma configuración JVM
- mismo Gradle
- mismo commit experimental
- mismo harness
- mismo `StreakCalculator`
- misma transformación observada
- mismo generador
- mismo `referenceEpochDay`
- mismos volúmenes
- mismo patrón de distribución
- mismo solapamiento
- mismo orden de listas
- mismo warm-up
- mismas observaciones
- mismo reloj
- mismo T0
- mismo T1
- mismo método de medición del overhead
- mismos métodos estadísticos

---

## 23. Código que no puede modificarse durante la caracterización

Antes de completar las mediciones formales no podrá modificarse funcionalmente:

- `StreakCalculator`
- la semántica de `current`
- la semántica de `best`
- la transformación:
    - merge
    - `distinct`
    - `sorted`

Solo podrán añadirse elementos estrictamente necesarios para:

- harness
- instrumentación
- generación controlada de datos
- validación
- almacenamiento de evidencia

---

## 24. Criterios de invalidación

Una corrida será inválida si:

- no utiliza un proceso JVM nuevo
- cambia el equipo
- cambia Java
- cambia la configuración JVM
- cambia Gradle
- cambia el commit experimental
- cambia el harness
- cambia `StreakCalculator`
- cambia la transformación observada
- cambia la semilla
- cambia `referenceEpochDay`
- cambia T0
- cambia T1
- cambia el reloj
- cambia el método de medición del overhead
- no se ejecuta el warm-up definido
- faltan observaciones
- cambia el orden preregistrado
- generación, logging o validación entran dentro de T0–T1
- la unión no contiene exactamente `N` días distintos
- ocurre cualquier `FUNCTIONAL_FAIL`
- una interrupción impide completar correctamente el protocolo

Una corrida inválida:

- se conservará
- tendrá una causa documentada
- no participará en el veredicto
- deberá repetirse desde una JVM nueva

Una corrida válida desfavorable para la hipótesis no podrá invalidarse por ese motivo.

---

## 25. Estado

`CONDICIONES DEFINIDAS — NO EJECUTADO`

Todavía no existen:

- commit definitivo de preregistro
- harness
- commit experimental
- mediciones formales
- resultados
- veredicto

Las mediciones formales permanecen bloqueadas hasta que exista el commit definitivo de preregistro.