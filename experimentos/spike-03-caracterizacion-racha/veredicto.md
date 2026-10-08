# SPIKE-03 — Veredicto experimental

## 1. Identificación

**Spike:** SPIKE-03 — Caracterización de escalabilidad del cálculo de streak en Progress

**Preregistro definitivo:** `34234e4`

**Merge del preregistro en `master`:** `d7f8b50`

**Commit del harness experimental medido:** `4da5923`

**Commit definitivo de evidencia y resultados:** `9e51c0d`

---

## 2. Propósito de este documento

Este documento registra exclusivamente el **veredicto experimental** de SPIKE-03 a partir del protocolo preregistrado y de la evidencia obtenida durante las corridas formales.

No constituye por sí mismo una decisión arquitectónica.

Su propósito es cerrar de manera trazable la relación entre:

1. la pregunta experimental;
2. la hipótesis preregistrada;
3. las condiciones de ejecución;
4. la evidencia preservada;
5. las reglas de decisión;
6. y el resultado experimental obtenido.

La evaluación de la relevancia arquitectónica de este resultado corresponde a un artefacto de decisión posterior.

---

## 3. Pregunta experimental

> ¿Cómo cambia el tiempo de ejecución de la implementación actual del cálculo de `currentStreak` y `bestStreak` a medida que aumenta la cantidad de días históricos distintos que debe procesar?

---

## 4. Hipótesis preregistrada

> Bajo las mismas condiciones de ejecución, al aumentar la entrada desde 30 hasta 10000 días históricos distintos, la mediana del tiempo de ejecución de la ruta actual de cálculo de streak será mayor para 10000 días que para 30 días en al menos dos de tres corridas formales independientes.

Esta hipótesis fue registrada antes de implementar y ejecutar las mediciones formales.

---

## 5. Variable experimental

La variable de entrada fue:

`N = cantidad de días históricos distintos`

Los volúmenes preregistrados fueron:

- `30`
- `365`
- `1000`
- `2500`
- `5000`
- `10000`

Para la decisión de la hipótesis se utilizaron exclusivamente los extremos:

- `N = 30`
- `N = 10000`

Los volúmenes intermedios se conservaron con propósito descriptivo y de caracterización.

---

## 6. Operación medida

La frontera temporal medida fue la ruta local:

`activityDays + pomodoroDays`
→ `distinct()`
→ `sorted()`
→ `StreakCalculator.calculate(...)`
→ `StreakResult`

El instante `T0` fue tomado inmediatamente antes de:

`activityDays + pomodoroDays`

El instante `T1` fue tomado inmediatamente después del retorno de:

`StreakCalculator.calculate(...)`

El tiempo de cada observación se calculó como:

`elapsedNs = T1 - T0`

---

## 7. Operaciones excluidas de la medición

La frontera `T0–T1` no incluyó:

- generación del dataset;
- Room;
- SQLite;
- consultas a base de datos;
- `Flow`;
- backend;
- red;
- Compose;
- renderizado de interfaz;
- escritura de archivos;
- generación de logs;
- validación funcional posterior;
- cálculo posterior de estadísticas.

Por tanto, los resultados corresponden únicamente a la operación local definida en el protocolo y no representan por sí mismos el tiempo end-to-end del módulo Progress.

---

## 8. Condiciones experimentales relevantes

Las tres corridas utilizaron las condiciones definidas previamente en el protocolo.

Entre las condiciones comunes se encuentran:

- fecha de referencia fija: `2026-10-07`;
- `referenceEpochDay = 20733`;
- dataset determinista;
- ausencia de generación aleatoria;
- mismos volúmenes en las tres corridas;
- 50 warm-ups por volumen;
- 100 observaciones formales por volumen;
- 10000 observaciones de overhead del reloj por corrida;
- validación funcional posterior a cada medición;
- una JVM independiente por corrida;
- mismo harness experimental versionado antes de las corridas.

El commit de instrumentación utilizado como frontera experimental fue:

`4da5923`

---

## 9. Corridas formales

Se realizaron exactamente tres corridas formales independientes.

### Corrida 1

Orden de volúmenes:

`30 → 365 → 1000 → 2500 → 5000 → 10000`

Resultado funcional:

- 600 observaciones formales;
- 600 `PASS`;
- 0 `FUNCTIONAL_FAIL`;
- `runValid = true`.

### Corrida 2

Orden de volúmenes:

`10000 → 5000 → 2500 → 1000 → 365 → 30`

Resultado funcional:

- 600 observaciones formales;
- 600 `PASS`;
- 0 `FUNCTIONAL_FAIL`;
- `runValid = true`.

### Corrida 3

Orden de volúmenes:

`30 → 365 → 1000 → 2500 → 5000 → 10000`

Resultado funcional:

- 600 observaciones formales;
- 600 `PASS`;
- 0 `FUNCTIONAL_FAIL`;
- `runValid = true`.

No se descartó ninguna de las tres corridas.

El protocolo preregistrado no establecía el descarte de la primera corrida. Las tres corridas participan en la regla experimental de decisión.

---

## 10. Validación funcional

Después de `T1`, cada observación fue validada contra el resultado funcional esperado.

Para cada volumen `N` se exigió:

`currentStreak == N`

y:

`bestStreak == N`

Una observación incorrecta debía registrarse como:

`FUNCTIONAL_FAIL`

y cualquier `FUNCTIONAL_FAIL` invalidaba la corrida completa según el protocolo.

En las tres corridas formales se registró:

`functionalFailCount = 0`

Por tanto:

- Corrida 1: válida.
- Corrida 2: válida.
- Corrida 3: válida.

---

## 11. Evidencia preservada

La evidencia cruda se conserva en:

`experimentos/spike-03-caracterizacion-racha/evidencia/`

La estructura incluye:

```text
evidencia/
├── SHA256SUMS.txt
├── run-1/
│   └── 20261007-213425-135/
│       ├── clock-overhead.csv
│       ├── metadata.csv
│       └── observations.csv
├── run-2/
│   └── 20261007-213812-117/
│       ├── clock-overhead.csv
│       ├── metadata.csv
│       └── observations.csv
└── run-3/
    └── 20261007-214224-649/
        ├── clock-overhead.csv
        ├── metadata.csv
        └── observations.csv
```

Cada corrida conserva:

- metadatos de ejecución;
- mediciones de overhead;
- observaciones formales;
- identificación de corrida;
- PID;
- tiempo de inicio de JVM;
- parámetros experimentales;
- resultado funcional;
- estado de validez.

### 11.1. Identidad de las JVM utilizadas

| Corrida | PID | `JvmStartTimeEpochMs` |
|---|---:|---:|
| 1 | 18976 | 1791426901750 |
| 2 | 6560 | 1791427122641 |
| 3 | 5236 | 1791427375033 |

Los PID y los tiempos de inicio de JVM son distintos entre las tres corridas, lo que respalda que cada corrida formal fue ejecutada en un proceso JVM independiente.

---

## 12. Integridad SHA-256 de la evidencia

Se generó el archivo:

`experimentos/spike-03-caracterizacion-racha/evidencia/SHA256SUMS.txt`

Este archivo contiene una línea base SHA-256 para los nueve archivos CSV preservados correspondientes a las tres corridas formales.

Los hashes registrados son:

### Corrida 1

**clock-overhead.csv**

`54151E2E7EDDDE0681D9B8F73834F74EA156B4267D970E608DECB4C603599BD4`

**metadata.csv**

`C0652DDE7499D9ED0CAAEDC9A82BA3E68675508E6242457D089049614679FCE8`

**observations.csv**

`69329490D9D0A64B19947E3A3EC4C0FE507222935F5F4F35AA7ACF7B0FA13380`

### Corrida 2

**clock-overhead.csv**

`C74402FAAFDF64F1B68E5D7781C948CC6F90318312495425B940D09A9CA4FC92`

**metadata.csv**

`3D86B706F80453E44E288DB4CD57C93F6D9FA94A7A8BF3D24745300C04EEE1AE`

**observations.csv**

`EC9256AFC602B30B3D66E81C439FCEF38C3F6043D06A3C550FF7A187F9B2E0F7`

### Corrida 3

**clock-overhead.csv**

`42874AB7EB8CA2FF6E7FF831D6FE39101B4AFFD2A7068294951A64AAF9DB6034`

**metadata.csv**

`CDA37ED340E039B64BAF8F04D1A29DFA85A4F49EDB8B7FE57C844079807768B9`

**observations.csv**

`2CCCA6B18188B49690E9FA1A9248C56005C19C7935D8375053C295BB69F41E3D`

Antes de versionar definitivamente la evidencia, los hashes existentes fueron recalculados y comparados con los registrados en `experimentos/spike-03-caracterizacion-racha/evidencia/SHA256SUMS.txt`.

La comparación no presentó diferencias.

Por tanto, en el momento de dicha verificación, los nueve archivos CSV coincidían con la línea base SHA-256 registrada.

`experimentos/spike-03-caracterizacion-racha/evidencia/SHA256SUMS.txt` debe interpretarse como una **línea base de integridad desde el momento en que los hashes fueron calculados**.

No constituye evidencia retrospectiva de que los archivos nunca hubieran podido ser modificados antes de la creación de dicha línea base.

---

## 13. Estadístico decisor

El estadístico preregistrado para decidir la hipótesis fue la **mediana**.

Cada volumen contó con:

`100 observaciones formales por corrida`

Para 100 observaciones ordenadas, la mediana fue calculada mediante las posiciones centrales 50 y 51.

También se calculó como métrica descriptiva adicional:

- factor de crecimiento (`growthFactor`).

Esta métrica complementa la caracterización, pero no sustituye a la mediana como estadístico decisor del protocolo.

---

## 14. Resultados decisores

Los resultados correspondientes a los dos extremos utilizados para decidir la hipótesis fueron:

| Corrida | Mediana `N=30` | Mediana `N=10000` | `median_10000 > median_30` |
|---|---:|---:|---|
| 1 | 3600 ns | 712050 ns | Sí |
| 2 | 3400 ns | 627650 ns | Sí |
| 3 | 3500 ns | 633350 ns | Sí |

La condición direccional se cumplió en:

`3 de 3 corridas formales válidas`

Los valores intermedios de `N` se conservaron como parte de la caracterización completa, pero no participaron directamente en la regla de aceptación o refutación de la hipótesis.

---

## 15. Métrica descriptiva adicional

Como medida descriptiva se calculó:

`growthFactor = median_10000 / median_30`

Los valores observados fueron:

| Corrida | Factor de crecimiento |
|---|---:|
| 1 | 197.791666666667× |
| 2 | 184.602941176471× |
| 3 | 180.957142857143× |

Estos factores describen la relación entre las medianas de los dos extremos evaluados.

No forman parte de la regla formal de decisión.

Por tanto, no deben interpretarse aisladamente como evidencia de:

- un problema de rendimiento del producto;
- una degradación perceptible para el usuario;
- un cuello de botella end-to-end;
- una violación de un SLO;
- o la necesidad de una modificación arquitectónica.

---

## 16. Criterio de resolución insuficiente

El protocolo preregistró la siguiente condición:

`median_30 <= 10 × medianClockOverheadNs`

Si esta condición se cumplía en al menos dos de las tres corridas válidas, SPIKE-03 debía declararse:

`INCONCLUSO POR RESOLUCIÓN INSUFICIENTE`

Las tres corridas registraron:

`medianClockOverheadNs = 0`

### Corrida 1

`3600 <= 10 × 0`

`3600 <= 0`

Resultado:

`false`

### Corrida 2

`3400 <= 10 × 0`

`3400 <= 0`

Resultado:

`false`

### Corrida 3

`3500 <= 10 × 0`

`3500 <= 0`

Resultado:

`false`

Conteo:

`0 de 3 corridas`

Por tanto, el criterio preregistrado de resolución insuficiente **no se activó**.

El valor:

`medianClockOverheadNs = 0`

representa exclusivamente la mediana observada mediante el procedimiento preregistrado.

No significa que `System.nanoTime()` tenga costo físico nulo ni demuestra que el reloj posea resolución temporal perfecta.

La regla de resolución no fue modificada después de observar los resultados.

---

## 17. Regla direccional

La regla direccional preregistrada fue:

`median_10000 > median_30`

La hipótesis debía considerarse respaldada si la condición se cumplía en al menos:

`2 de 3 corridas formales válidas`

siempre que no se hubiera activado previamente el criterio de resolución insuficiente.

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

Conteo:

`3 de 3 corridas`

El mínimo preregistrado era:

`2 de 3 corridas`

Por tanto, la regla direccional fue satisfecha.

---

## 18. Aplicación conjunta de las reglas preregistradas

El resultado conjunto fue:

```text
Resolución insuficiente
0 de 3 corridas
→ NO SE ACTIVA

Regla direccional
3 de 3 corridas
→ SE CUMPLE

Mínimo requerido
2 de 3 corridas
→ SUPERADO
```

No fue necesario modificar, sustituir o reinterpretar posteriormente ninguna de las reglas de decisión preregistradas.

---

## 19. Veredicto experimental

# RESPALDADA

La hipótesis experimental de SPIKE-03 queda:

**RESPALDADA**

Bajo las condiciones preregistradas, la mediana del tiempo de ejecución de la ruta actual de cálculo de streak para `N=10000` fue mayor que para `N=30` en las tres corridas formales válidas.

La condición se cumplió en:

`3 de 3 corridas`

superando el mínimo preregistrado de:

`2 de 3 corridas`

La regla de resolución insuficiente se presentó en:

`0 de 3 corridas`

por lo que no se activó la condición de inconclusión por resolución insuficiente.

---

## 20. Afirmaciones soportadas por la evidencia

La evidencia permite afirmar que:

1. las tres corridas formales preservadas fueron funcionalmente válidas;
2. cada corrida contó con 600 observaciones formales;
3. cada uno de los seis volúmenes contó con 100 observaciones por corrida;
4. no se registraron `FUNCTIONAL_FAIL`;
5. `runValid = true` en las tres corridas;
6. bajo las condiciones del experimento, la mediana para `N=10000` fue mayor que para `N=30`;
7. esa dirección se presentó en las tres corridas independientes;
8. la regla de resolución insuficiente no se activó;
9. la regla direccional se cumplió en `3/3`;
10. la hipótesis cumplió el criterio preregistrado necesario para ser declarada `RESPALDADA`.

---

## 21. Límites de inferencia

SPIKE-03 caracteriza exclusivamente la frontera local preregistrada.

Sus resultados no deben extrapolarse automáticamente al comportamiento completo de RachaPro.

En particular, SPIKE-03 **no demuestra por sí mismo**:

- una degradación perceptible para el usuario;
- un problema de rendimiento del producto;
- un cuello de botella end-to-end;
- un incumplimiento de un escenario de calidad;
- un incumplimiento de un SLO;
- que el cálculo de streak domine el tiempo total de Progress;
- que `N=10000` represente un tamaño histórico habitual o realista del producto;
- cuál es la frecuencia real de ejecución de `StreakCalculator.calculate(...)`;
- qué proporción del tiempo end-to-end corresponde al cálculo de streak;
- que la implementación actual deba optimizarse;
- que una optimización concreta sea necesaria;
- que deba introducirse una proyección;
- que deba introducirse CQRS;
- que deba introducirse Event Sourcing;
- que deba modificarse la arquitectura vigente;
- que cualquiera de esas alternativas sea incorrecta de manera general.

La ausencia de evidencia suficiente para justificar una alternativa no equivale a haber demostrado que dicha alternativa sea innecesaria.

Que la hipótesis experimental haya sido respaldada tampoco implica automáticamente una necesidad arquitectónica de intervención.

---

## 22. Comparabilidad con evidencia previa

SPIKE-03 está relacionado con el atributo de calidad de rendimiento.

Sin embargo, su frontera experimental corresponde a una micro-medición local específica.

Por tanto, la evidencia previa del proyecto puede utilizarse como contexto, pero no debe asumirse automáticamente como una baseline numérica directamente comparable.

Antes de realizar una comparación cuantitativa deben comprobarse como mínimo:

- frontera medida;
- operación observada;
- escenario;
- unidad experimental;
- dataset;
- condiciones de ejecución;
- instrumentación;
- estadístico utilizado;
- criterio de validez.

Mientras dicha equivalencia no haya sido demostrada:

`Comparabilidad numérica con S4 = NO DEMOSTRADA`

Esto no implica que la evidencia previa sea irrelevante.

Implica que sus valores no deben mezclarse directamente con los nanosegundos obtenidos en SPIKE-03 sin una justificación metodológica adicional.

---

## 23. Separación entre resultado experimental y decisión arquitectónica

SPIKE-03 responde una pregunta experimental.

No decide por sí mismo qué arquitectura debe adoptar RachaPro.

La trazabilidad queda:

```text
34234e4
Preregistro definitivo
        ↓
d7f8b50
Merge del preregistro en master
        ↓
4da5923
Harness experimental versionado
        ↓
3 corridas formales independientes
        ↓
9e51c0d
Evidencia y resultados definitivos
        ↓
veredicto.md
Veredicto experimental
        ↓
Evaluación arquitectónica posterior
        ↓
ADR relacionado, si corresponde
```

El presente documento puede ser referenciado posteriormente por el ADR que documente una decisión arquitectónica relacionada con esta evidencia.

Este documento:

- no presupone cuál será ese ADR;
- no presupone que deba existir un nuevo ADR;
- no modifica retrospectivamente un ADR anterior;
- no determina cuál alternativa arquitectónica debe seleccionarse.

Por tanto:

**Hipótesis experimental:** `RESPALDADA`

**Decisión arquitectónica:** `NO DETERMINADA`

---

## 24. Estado final de la auditoría experimental

```text
Preregistro definitivo          ✅ 34234e4
Merge del preregistro           ✅ d7f8b50
Harness experimental             ✅ 4da5923
3 corridas formales              ✅
600 observaciones por corrida    ✅
Validación funcional             ✅
FUNCTIONAL_FAIL                  0
runValid                         true en 3/3
Evidencia cruda                  ✅
Integridad SHA-256               ✅
Resultados definitivos           ✅ 9e51c0d
Resolución insuficiente          0/3
Regla direccional                3/3
Hipótesis experimental           RESPALDADA
Comparabilidad numérica S4       NO DEMOSTRADA
Decisión arquitectónica          NO DETERMINADA
```

---

## 25. Cierre formal

SPIKE-03 queda experimentalmente cerrado una vez este artefacto de veredicto sea versionado en Git.

El cierre formal es:

**Hipótesis experimental:** `RESPALDADA`

**Decisión arquitectónica:** `NO DETERMINADA`

La hipótesis queda respaldada porque:

`median_10000 > median_30`

se presentó en las tres corridas formales válidas, superando el mínimo preregistrado de dos de tres corridas, y la regla preregistrada de resolución insuficiente no se activó.

Este veredicto se limita estrictamente a:

- la pregunta experimental;
- la hipótesis preregistrada;
- las condiciones experimentales;
- la frontera de medición;
- la evidencia preservada;
- y las reglas de decisión definidas para SPIKE-03.

No constituye por sí mismo una justificación para introducir, eliminar o modificar una decisión arquitectónica.

Cualquier decisión arquitectónica posterior deberá evaluar este resultado junto con los escenarios de calidad, restricciones, contexto de uso, costos, beneficios, riesgos y demás evidencia relevante del sistema.
