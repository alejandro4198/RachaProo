# Resultados — SPIKE-03

## 1. Identificación

- Proyecto: RachaPro
- Experimento: SPIKE-03
- Nombre: Caracterización de escalabilidad del cálculo de streak en Progress
- Estado experimental: EJECUTADO
- Resultado de la hipótesis: RESPALDADA
- Decisión arquitectónica derivada automáticamente: NINGUNA

### Prerregistro definitivo

Commit:

`34234e4f82b48637bdc70972cc073c2ded55f2d8`

Mensaje:

`docs(spike-03): preregistrar caracterizacion de streak`

### Merge del preregistro definitivo

Commit:

`d7f8b5050f90b9315abb877198d8816e6eb18158`

Mensaje:

`Merge pull request #47 from alejandro4198/docs/spike-03-preregistro-definitivo`

### Commit experimental

Commit:

`4da5923a2c03c854953d266e52b277cae81a9c11`

Mensaje:

`test(spike-03): implementar harness de caracterizacion de streak`

El commit experimental contiene el harness utilizado posteriormente para las
corridas formales de SPIKE-03.

---

## 2. Evidencia cruda

Las tres corridas formales generaron evidencia separada en archivos CSV.

### Corrida 1

Ruta:

`experimentos/spike-03-caracterizacion-racha/evidencia/run-1/20261007-213425-135/`

Archivos:

- `metadata.csv`
- `clock-overhead.csv`
- `observations.csv`

### Corrida 2

Ruta:

`experimentos/spike-03-caracterizacion-racha/evidencia/run-2/20261007-213812-117/`

Archivos:

- `metadata.csv`
- `clock-overhead.csv`
- `observations.csv`

### Corrida 3

Ruta:

`experimentos/spike-03-caracterizacion-racha/evidencia/run-3/20261007-214224-649/`

Archivos:

- `metadata.csv`
- `clock-overhead.csv`
- `observations.csv`

### Línea base de integridad

Después de las corridas y antes del commit de evidencia y resultados se
calculó SHA-256 sobre los nueve CSV.

Los hashes registrados constituyen una línea base de integridad a partir de
ese momento.

Estos hashes permiten detectar modificaciones posteriores respecto del estado
registrado.

No se utilizan para afirmar retroactivamente que los archivos nunca fueron
modificados entre el instante exacto de su generación y el momento en el que
se calcularon los hashes.

El manifiesto se conserva en:

`experimentos/spike-03-caracterizacion-racha/evidencia/SHA256SUMS.txt`

### SHA-256 — Corrida 1

#### `clock-overhead.csv`

`54151E2E7EDDDE0681D9B8F73834F74EA156B4267D970E608DECB4C603599BD4`

#### `metadata.csv`

`C0652DDE7499D9ED0CAAEDC9A82BA3E68675508E6242457D089049614679FCE8`

#### `observations.csv`

`69329490D9D0A64B19947E3A3EC4C0FE507222935F5F4F35AA7ACF7B0FA13380`

### SHA-256 — Corrida 2

#### `clock-overhead.csv`

`C74402FAAFDF64F1B68E5D7781C948CC6F90318312495425B940D09A9CA4FC92`

#### `metadata.csv`

`3D86B706F80453E44E288DB4CD57C93F6D9FA94A7A8BF3D24745300C04EEE1AE`

#### `observations.csv`

`EC9256AFC602B30B3D66E81C439FCEF38C3F6043D06A3C550FF7A187F9B2E0F7`

### SHA-256 — Corrida 3

#### `clock-overhead.csv`

`42874AB7EB8CA2FF6E7FF831D6FE39101B4AFFD2A7068294951A64AAF9DB6034`

#### `metadata.csv`

`CDA37ED340E039B64BAF8F04D1A29DFA85A4F49EDB8B7FE57C844079807768B9`

#### `observations.csv`

`2CCCA6B18188B49690E9FA1A9248C56005C19C7935D8375053C295BB69F41E3D`

---

## 3. Condiciones comunes de ejecución

Las tres corridas registraron las siguientes condiciones comunes:

- Java: 25.0.2
- proveedor Java: JetBrains s.r.o.
- fecha de referencia: 2026-10-07
- `referenceEpochDay`: 20733
- warm-ups por volumen: 50
- observaciones formales por volumen: 100
- observaciones de overhead por corrida: 10000

Los volúmenes evaluados fueron:

- 30
- 365
- 1000
- 2500
- 5000
- 10000

Los datasets fueron deterministas según el protocolo preregistrado.

---

## 4. Identidad y validez de las corridas

### Corrida 1

- `runId`: 1
- PID: 18976
- inicio JVM en epoch ms: 1791426901750
- inicio de corrida en epoch ms: 1791426902482
- Java: 25.0.2
- proveedor Java: JetBrains s.r.o.
- fecha de referencia: 2026-10-07
- `referenceEpochDay`: 20733
- warm-ups por volumen: 50
- observaciones por volumen: 100
- observaciones de overhead: 10000
- `functionalFailCount`: 0
- `runValid`: true
- observaciones formales totales: 600
- filas de overhead: 10000

### Corrida 2

- `runId`: 2
- PID: 6560
- inicio JVM en epoch ms: 1791427122641
- inicio de corrida en epoch ms: 1791427123381
- Java: 25.0.2
- proveedor Java: JetBrains s.r.o.
- fecha de referencia: 2026-10-07
- `referenceEpochDay`: 20733
- warm-ups por volumen: 50
- observaciones por volumen: 100
- observaciones de overhead: 10000
- `functionalFailCount`: 0
- `runValid`: true
- observaciones formales totales: 600
- filas de overhead: 10000

### Corrida 3

- `runId`: 3
- PID: 5236
- inicio JVM en epoch ms: 1791427375033
- inicio de corrida en epoch ms: 1791427375768
- Java: 25.0.2
- proveedor Java: JetBrains s.r.o.
- fecha de referencia: 2026-10-07
- `referenceEpochDay`: 20733
- warm-ups por volumen: 50
- observaciones por volumen: 100
- observaciones de overhead: 10000
- `functionalFailCount`: 0
- `runValid`: true
- observaciones formales totales: 600
- filas de overhead: 10000

### Independencia entre corridas

Los PID registrados fueron:

- corrida 1 → 18976
- corrida 2 → 6560
- corrida 3 → 5236

Los tres PID son distintos.

Los tiempos de inicio de JVM registrados fueron:

- corrida 1 → 1791426901750
- corrida 2 → 1791427122641
- corrida 3 → 1791427375033

Los tres tiempos de inicio de JVM son distintos.

La evidencia registrada es consistente con la ejecución de cada corrida formal
en un proceso JVM independiente.

---

## 5. Integridad funcional

Cada corrida produjo exactamente:

`600 observaciones formales`

distribuidas de la siguiente forma:

| N | Observaciones por corrida |
| ---: | ---: |
| 30 | 100 |
| 365 | 100 |
| 1000 | 100 |
| 2500 | 100 |
| 5000 | 100 |
| 10000 | 100 |

Para cada una de las tres corridas:

- observaciones `PASS`: 600
- observaciones `FUNCTIONAL_FAIL`: 0
- `functionalFailCount`: 0
- `runValid`: true

Por tanto:

- corrida 1 → válida
- corrida 2 → válida
- corrida 3 → válida

Las tres corridas registradas y preservadas no presentaron fallos funcionales.

---

## 6. Operación medida

La ruta medida por SPIKE-03 fue la preregistrada:

`activityDays + pomodoroDays`

↓

`distinct()`

↓

`sorted()`

↓

`StreakCalculator.calculate(...)`

↓

`StreakResult`

La frontera temporal estuvo definida por:

`T0 = System.nanoTime()`

inmediatamente antes de:

`activityDays + pomodoroDays`

y:

`T1 = System.nanoTime()`

inmediatamente después del retorno de:

`StreakCalculator.calculate(...)`

La generación de datasets, la validación funcional, la escritura de evidencia
y el cálculo posterior de estadísticas quedaron fuera de esta frontera
temporal.

---

## 7. Resultados decisores por corrida

La hipótesis preregistrada utiliza para el veredicto únicamente la comparación
entre:

`N = 30`

y:

`N = 10000`

mediante la mediana del tiempo de ejecución.

| Corrida | Mediana N=30 | Mediana N=10000 | Overhead mediano | `growthFactor` | `median_10000 > median_30` |
| --- | ---: | ---: | ---: | ---: | --- |
| 1 | 3600 ns | 712050 ns | 0 ns | 197.791666666667 | true |
| 2 | 3400 ns | 627650 ns | 0 ns | 184.602941176471 | true |
| 3 | 3500 ns | 633350 ns | 0 ns | 180.957142857143 | true |

Los valores de `growthFactor` se incluyen únicamente como métricas
descriptivas.

No intervienen en el veredicto.

---

## 8. Criterio preregistrado de resolución insuficiente

El preregistro estableció el siguiente criterio:

`median_30 <= 10 × medianClockOverheadNs`

Si esta condición se cumplía en al menos dos de las tres corridas válidas, el
resultado de SPIKE-03 debía clasificarse como:

`INCONCLUSO POR RESOLUCIÓN INSUFICIENTE`

### Corrida 1

Valores:

- `median_30 = 3600 ns`
- `medianClockOverheadNs = 0 ns`

Aplicación:

`3600 <= 10 × 0`

Resultado:

`false`

### Corrida 2

Valores:

- `median_30 = 3400 ns`
- `medianClockOverheadNs = 0 ns`

Aplicación:

`3400 <= 10 × 0`

Resultado:

`false`

### Corrida 3

Valores:

- `median_30 = 3500 ns`
- `medianClockOverheadNs = 0 ns`

Aplicación:

`3500 <= 10 × 0`

Resultado:

`false`

### Conteo

La condición de resolución insuficiente se cumplió en:

`0 de 3 corridas válidas`

Por tanto, el criterio preregistrado de resolución insuficiente:

`NO SE ACTIVÓ`

### Observación sobre el overhead

La mediana de las 10000 observaciones de overhead fue:

`0 ns`

en las tres corridas.

Este valor se conserva exactamente como fue registrado por el procedimiento
experimental.

No se sustituye por otro valor ni se redefine retrospectivamente el criterio
preregistrado.

El valor de `0 ns` forma parte de las condiciones y limitaciones de esta
medición.

---

## 9. Aplicación de la regla direccional

La hipótesis preregistrada estableció que quedaría respaldada si:

`median_10000 > median_30`

en al menos:

`2 de 3 corridas formales válidas`

siempre que no se activara el criterio de resolución insuficiente.

### Corrida 1

`712050 > 3600`

Resultado:

`true`

### Corrida 2

`627650 > 3400`

Resultado:

`true`

### Corrida 3

`633350 > 3500`

Resultado:

`true`

### Conteo

La condición:

`median_10000 > median_30`

se cumplió en:

`3 de 3 corridas válidas`

El mínimo preregistrado era:

`2 de 3 corridas válidas`

Por tanto, la condición direccional preregistrada quedó satisfecha.

---

## 10. Métricas descriptivas

### `growthFactor`

Los factores de crecimiento obtenidos fueron:

- corrida 1 → 197.791666666667
- corrida 2 → 184.602941176471
- corrida 3 → 180.957142857143

Estos valores son:

`DESCRIPTIVOS`

No participan en la regla formal de respaldo o refutación.

### Volúmenes intermedios

Los volúmenes:

- 365
- 1000
- 2500
- 5000

también tienen finalidad descriptiva.

No participan en el veredicto.

Los valores descriptivos no fueron utilizados para:

- cambiar la hipótesis
- redefinir el criterio de decisión
- sustituir los extremos preregistrados
- cambiar el umbral de decisión
- modificar retrospectivamente las reglas del experimento

---

## 11. Veredicto

### Resultado

`RESPALDADA`

La hipótesis preregistrada de SPIKE-03 queda respaldada porque:

- las tres corridas formales fueron válidas;
- no se registraron `FUNCTIONAL_FAIL`;
- el criterio de resolución insuficiente se cumplió en 0 de 3 corridas;
- `median_10000 > median_30` se cumplió en 3 de 3 corridas válidas.

El criterio preregistrado requería que la relación:

`median_10000 > median_30`

se cumpliera en al menos:

`2 de 3 corridas válidas`

sin que se activara el criterio de resolución insuficiente.

La evidencia obtenida cumplió ese criterio.

---

## 12. Conclusión limitada

Bajo las condiciones preregistradas de SPIKE-03, la mediana del tiempo de
ejecución de la ruta actual de cálculo de streak fue mayor para 10000 días
históricos distintos que para 30 días en las tres corridas formales válidas.

La relación direccional definida por la hipótesis se observó de forma
reproducible bajo las condiciones evaluadas.

SPIKE-03 caracteriza específicamente el comportamiento temporal de la ruta
medida frente a diferentes cantidades de días históricos distintos.

Este resultado no demuestra por sí mismo que exista:

- un cuello de botella relevante para el producto;
- una degradación perceptible para el usuario;
- un incumplimiento de un SLO;
- un impacto operativo relevante;
- una necesidad inmediata de optimización.

SPIKE-03 tampoco evaluó alternativas arquitectónicas como:

- proyecciones;
- CQRS;
- Event Sourcing.

Por tanto, este experimento:

- no prueba que esas alternativas sean necesarias;
- no prueba que esas alternativas no sean necesarias;
- no constituye por sí solo una selección de solución arquitectónica.

Cualquier decisión de intervención requiere evidencia y análisis adicionales.

---

## 13. Limitaciones

Los resultados de SPIKE-03 deben interpretarse teniendo en cuenta que:

- el experimento fue ejecutado en un entorno local;
- utilizó una JVM y un equipo específicos;
- caracterizó una ruta concreta;
- utilizó datasets deterministas;
- no midió Room;
- no midió SQLite;
- no midió Flow;
- no midió backend;
- no midió red;
- no midió Compose;
- no midió el tiempo end-to-end percibido por el usuario;
- no evaluó una implementación alternativa;
- no evaluó una optimización;
- no comparó la implementación actual contra una solución SQL alternativa;
- no comparó la implementación actual contra una proyección;
- no evaluó CQRS;
- no evaluó Event Sourcing;
- la mediana del overhead registrada fue 0 ns en las tres corridas;
- el criterio de resolución aplicado fue exclusivamente el definido en el
  preregistro.

Los volúmenes evaluados fueron los definidos en el protocolo experimental.

En este documento no se introduce una clasificación retrospectiva adicional
de dichos volúmenes.

---

## 14. Separación entre resultado experimental y decisión arquitectónica

SPIKE-03 responde exclusivamente a la pregunta experimental sobre cómo cambia
el tiempo de ejecución de la ruta actual cuando cambia la cantidad de días
históricos distintos procesados.

El resultado:

`RESPALDADA`

corresponde al estado de la hipótesis experimental.

No constituye automáticamente una decisión arquitectónica.

En particular, el experimento no establece por sí mismo una decisión respecto
de:

- optimizar la implementación;
- introducir una proyección;
- adoptar CQRS;
- adoptar Event Sourcing;
- sustituir la estrategia actual de persistencia;
- modificar la arquitectura de Progress.

Una eventual decisión arquitectónica deberá considerar evidencia adicional,
incluyendo la relevancia del comportamiento observado respecto de los
atributos de calidad y necesidades reales del producto.

Estado de decisión arquitectónica derivado exclusivamente de SPIKE-03:

`NO DETERMINADA`

---

## 15. Trazabilidad histórica versionada

### Borrador histórico

Commit:

`63d0db2`

Mensaje:

`docs(spike-03): agrega borrador de caracterizacion de racha`

Este commit corresponde al borrador histórico inicial de SPIKE-03.

↓

### Merge del borrador histórico

Commit:

`1536d33`

Mensaje:

`Merge pull request #46 from alejandro4198/exp/spike-03-caracterizacion-racha`

Este commit corresponde al merge del PR #46 que incorporó el borrador
histórico de SPIKE-03.

No constituye el preregistro definitivo.

↓

### Prerregistro definitivo

Commit:

`34234e4f82b48637bdc70972cc073c2ded55f2d8`

Mensaje:

`docs(spike-03): preregistrar caracterizacion de streak`

Este commit establece el preregistro definitivo utilizado como referencia para
la ejecución del experimento.

↓

### Merge del preregistro definitivo

Commit:

`d7f8b5050f90b9315abb877198d8816e6eb18158`

Mensaje:

`Merge pull request #47 from alejandro4198/docs/spike-03-preregistro-definitivo`

Este commit incorporó el preregistro definitivo en `master`.

↓

### Implementación experimental

Commit:

`4da5923a2c03c854953d266e52b277cae81a9c11`

Mensaje:

`test(spike-03): implementar harness de caracterizacion de streak`

Este commit contiene el harness experimental utilizado posteriormente para
las corridas formales.

---

## 16. Trazabilidad de ejecución

Después del commit experimental se registraron las siguientes corridas:

### Corrida formal 1

Ruta:

`experimentos/spike-03-caracterizacion-racha/evidencia/run-1/20261007-213425-135/`

- PID: 18976
- inicio JVM: 1791426901750
- `runValid`: true
- `functionalFailCount`: 0

### Corrida formal 2

Ruta:

`experimentos/spike-03-caracterizacion-racha/evidencia/run-2/20261007-213812-117/`

- PID: 6560
- inicio JVM: 1791427122641
- `runValid`: true
- `functionalFailCount`: 0

### Corrida formal 3

Ruta:

`experimentos/spike-03-caracterizacion-racha/evidencia/run-3/20261007-214224-649/`

- PID: 5236
- inicio JVM: 1791427375033
- `runValid`: true
- `functionalFailCount`: 0

Los directorios de evidencia y los metadatos generados por el harness
documentan estas ejecuciones.

La secuencia metodológica registrada es:

`preregistro definitivo`

↓

`merge del preregistro definitivo`

↓

`commit experimental del harness`

↓

`corrida formal 1`

↓

`corrida formal 2`

↓

`corrida formal 3`

↓

`aplicación de reglas preregistradas`

↓

`RESPALDADA`

↓

`registro de evidencia y resultados`

La historia Git existente demuestra directamente la relación temporal entre
el preregistro definitivo y el commit experimental.

Los archivos de evidencia documentan las ejecuciones posteriores realizadas
con el harness.

El commit que incorpore los archivos de evidencia, `SHA256SUMS.txt` y este
documento cerrará la trazabilidad versionada de la fase de resultados.

---

## 17. Protección frente a reinterpretación retrospectiva

Los resultados fueron interpretados utilizando las reglas establecidas en el
preregistro definitivo.

No se utilizaron los resultados obtenidos para:

- cambiar los volúmenes decisores;
- cambiar de mediana a otra estadística decisora;
- alterar el mínimo de 2 de 3 corridas;
- cambiar la regla de resolución insuficiente;
- incorporar `growthFactor` como criterio decisor;
- utilizar los volúmenes intermedios para cambiar el veredicto.

El valor de overhead mediano de `0 ns` se mantuvo tal como fue registrado y se
aplicó mediante la regla preregistrada existente.

No se introdujo después de las corridas un criterio alternativo de resolución.

---

## 18. Estado de SPIKE-03

Estado experimental:

`EJECUTADO`

Estado de las tres corridas:

`VÁLIDAS`

Resultado de la hipótesis:

`RESPALDADA`

Condición direccional:

`3 de 3`

Condición de resolución insuficiente:

`0 de 3`

Estado de decisión arquitectónica derivada exclusivamente de SPIKE-03:

`NO DETERMINADA`

La evidencia queda preservada para revisión, análisis posterior y eventual
evaluación arquitectónica.