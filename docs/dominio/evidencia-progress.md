# Evidencia de dominio — candidato Progress

## Estado del análisis

Este documento registra evidencia previa a cualquier decisión sobre si `Progress`
constituye o no una frontera de dominio independiente.

El objetivo de esta revisión es identificar reglas, entradas, salidas, procedencia
de los datos y vacíos de información observables en el repositorio.

Las clasificaciones utilizadas son:

- `HECHO DEL REPOSITORIO`: existe evidencia directa en el código inspeccionado.
- `INFERENCIA`: interpretación razonable apoyada en evidencia parcial.
- `INFORMACIÓN FALTANTE`: no existe todavía evidencia suficiente para afirmar la regla.

Este documento **no concluye** que `Progress` sea un bounded context.

---

## 1. Trazabilidad de reglas candidatas

| Regla | Archivo / método observado | Datos que consume | Datos que produce | Procedencia de los datos | Clasificación | Dudas abiertas |
|---|---|---|---|---|---|---|
| Día válido para racha | `app/src/main/java/com/example/rachapro/progress/ProgressViewModel.kt` → `loadProgress()` / construcción de `streakDaysFlow`; fuentes verificadas en `ActivityDao.observeCompletedDays()` y `PomodoroSessionDao.observeCompletedFocusDays()` | Días de actividades `COMPLETED` con `completedDateEpochDay` y días de sesiones `FOCUS` + `COMPLETED` con `completedDateEpochDay` | Lista combinada de días mediante unión, eliminación de duplicados y ordenamiento | Activities produce días de actividades completadas; Focus produce días de sesiones FOCUS completadas | `HECHO DEL REPOSITORIO` | `ActivityDao.observeCompletedDays()` no filtra `isDeleted = 0`; queda pendiente determinar si conservar esos días después de un borrado lógico es una regla intencional de negocio |
| Racha actual | `app/src/main/java/com/example/rachapro/domain/StreakCalculator.kt` → `calculate(completedDays, todayEpochDay)` | Lista combinada de días y `todayEpochDay` | `StreakResult.current` | Los hechos base proceden de Activities y Focus; `StreakCalculator` interpreta su consecutividad temporal | `HECHO DEL REPOSITORIO` | La implementación descarta días futuros, elimina duplicados y ordena. La racha actual termina hoy si hoy es válido, ayer si hoy no es válido pero ayer sí, y es `0` si ninguno de esos dos días pertenece al conjunto |
| Mejor racha | `app/src/main/java/com/example/rachapro/domain/StreakCalculator.kt` → `calculate(completedDays, todayEpochDay)` | Lista combinada de días y `todayEpochDay` | `StreakResult.best` | Los hechos base proceden de Activities y Focus; `StreakCalculator` interpreta su consecutividad temporal | `HECHO DEL REPOSITORIO` | `bestStreak` conserva el máximo tamaño de las secuencias consecutivas encontradas después de descartar días futuros, eliminar duplicados y ordenar los días |
| WEEK | `app/src/main/java/com/example/rachapro/progress/ProgressViewModel.kt` → `loadProgress()` → rama `ProgressPeriod.WEEK` | Fecha actual, conteos de actividades completadas, conteos de Pomodoros completados y segundos de enfoque | Estadísticas del periodo `WEEK`: actividades completadas, Pomodoros completados y tiempo de enfoque | Activities aporta hechos de actividades completadas; Focus aporta Pomodoros completados y tiempo de enfoque; Progress construye el intervalo y combina las métricas | `HECHO DEL REPOSITORIO` | Está pendiente determinar posteriormente si la definición del periodo constituye una regla de dominio o una regla de consulta/presentación |
| Achievement desbloqueado | `app/src/main/java/com/example/rachapro/domain/AchievementEngine.kt` → `typesToUnlock(input)`; utilizado desde el flujo de perfil mediante `AchievementRepository` | Cantidad de actividades completadas, cantidad de Pomodoros Focus completados y racha actual | Tipos de achievements elegibles para desbloqueo | Activities aporta el conteo de actividades; Focus aporta el conteo de Pomodoros; la racha proviene del cálculo que combina hechos de ambos | `HECHO DEL REPOSITORIO` para la regla Android de elegibilidad | Sigue pendiente determinar el ownership y la persistencia definitiva del Achievement, qué hace exactamente `AchievementRepository.syncAchievements()`, cómo interviene `AchievementDao` y qué relación existe con el módulo `achievement` del backend |

---

## 2. Evidencia observable

### 2.1 Composición de días para racha

`ProgressViewModel` obtiene días desde dos fuentes:

- `ActivityRepository`;
- `PomodoroRepository`.

Los conjuntos son combinados antes de delegar el cálculo de racha a
`StreakCalculator`.

Esto demuestra que existe una composición de hechos provenientes de
responsabilidades distintas.

No se documenta todavía la semántica interna de `StreakCalculator`, ya que su
implementación no ha sido inspeccionada en esta revisión.

---

### 2.2 Periodo WEEK

`ProgressViewModel` contiene una rama específica para `ProgressPeriod.WEEK`.

La lógica construye un intervalo temporal correspondiente a la semana y combina
en ese intervalo:

- actividades completadas;
- Pomodoros completados;
- tiempo de enfoque.

La existencia de esta regla está directamente soportada por el código.

Queda pendiente determinar posteriormente si esta lógica representa una regla
propia del dominio de progreso o únicamente una forma de consulta y
presentación.

---

### 2.3 Regla Android de elegibilidad de achievements

`AchievementEngine.typesToUnlock()` recibe información agregada relacionada con:

- actividades completadas;
- Pomodoros Focus completados;
- racha actual.

A partir de esos datos determina tipos de achievements candidatos a desbloqueo.

Esta evidencia permite afirmar que existe una **regla Android de elegibilidad
para desbloqueo**.

No permite afirmar todavía que Android sea la fuente de verdad definitiva de
Achievement ni que el concepto pertenezca necesariamente a Progress.

Siguen pendientes:

- `AchievementRepository.syncAchievements()`;
- `AchievementDao`;
- módulo `achievement` del backend;
- fuente de verdad;
- persistencia;
- posible duplicación de reglas Android/backend.

---

## 3. Evidencia a favor del candidato Progress

Hasta este punto se observa que existen operaciones que interpretan o combinan
hechos provenientes de Activities y Focus, entre ellas:

- composición de días utilizados para racha;
- cálculo delegado de racha actual;
- cálculo delegado de mejor racha;
- agregación temporal mediante `TODAY`, `WEEK` y `ALL`;
- combinación de actividades completadas, Pomodoros completados y tiempo de
  enfoque;
- reglas Android de elegibilidad para achievements basadas en métricas
  provenientes de varias responsabilidades.

Esta evidencia fortalece el análisis de autonomía semántica del candidato
`Progress`, pero no constituye todavía una decisión sobre una frontera de
dominio.

---

## 4. Evidencia en contra o tensiones

Las reglas relacionadas con progreso no están concentradas actualmente en una
única ubicación física.

Se han observado responsabilidades distribuidas entre componentes como:

- `progress/ProgressViewModel.kt`;
- `domain/StreakCalculator`;
- `domain/AchievementEngine`;
- `profile/ProfileViewModel`;
- repositories y DAO locales.

Por tanto, existe una tensión entre la posible capacidad funcional de progreso
y la ubicación física actual de sus reglas.

La estructura de paquetes por sí sola no se utilizará como evidencia para
decidir un bounded context.

---

## 5. Información faltante

Antes de cerrar el análisis de este candidato deben verificarse al menos los
siguientes puntos:

1. comportamiento de `AchievementRepository.syncAchievements()`;
2. persistencia realizada mediante `AchievementDao`;
3. comportamiento del módulo `achievement` del backend;
4. relación entre achievements Android y backend;
5. fuente de verdad definitiva del concepto Achievement;
6. intención de negocio respecto a si una actividad completada posteriormente eliminada debe conservar su aporte histórico a la racha;
7. significado exacto de `plannedDurationSeconds` como métrica de tiempo de enfoque y si representa duración planificada o tiempo efectivamente transcurrido.

---

## 6. Estado de la decisión de frontera

La evidencia recopilada fortalece el análisis de autonomía semántica de
`Progress` al mostrar reglas que interpretan y combinan hechos provenientes de
Activities y Focus.

Sin embargo, esta evidencia **no cierra la decisión** de si `Progress`
constituye un bounded context.

La decisión permanecerá abierta hasta:

- ampliar la evidencia faltante;
- analizar con el mismo criterio Activities, Focus y Reminders;
- realizar una comparación transversal entre los candidatos.

---

## 7. Estado epistemológico de esta revisión

| Elemento | Estado |
|---|---|
| Composición de días Activities + Focus | `HECHO DEL REPOSITORIO` |
| Semántica de racha actual | `HECHO DEL REPOSITORIO` |
| Semántica de mejor racha | `HECHO DEL REPOSITORIO` |
| Periodo WEEK | `HECHO DEL REPOSITORIO` |
| Regla Android de elegibilidad de Achievement | `HECHO DEL REPOSITORIO` |
| Ownership definitivo de Achievement | `INFORMACIÓN FALTANTE` |
| Persistencia definitiva de Achievement | `INFORMACIÓN FALTANTE` |
| Semántica exacta del día fuente de Activities | `HECHO DEL REPOSITORIO` |
| Semántica exacta del día fuente de Focus | `HECHO DEL REPOSITORIO` |
| Progress como bounded context | `NO DECIDIDO` |

---

## 8. Próximo paso de auditoría

La revisión adicional permitió cerrar la semántica de:

1. `StreakCalculator.calculate()`;
2. `ActivityDao.observeCompletedDays()`;
3. `PomodoroSessionDao.observeCompletedFocusDays()`.

El siguiente paso no consiste en declarar todavía una frontera para `Progress`.

Después de registrar esta segunda revisión se debe aplicar el mismo método,
de forma más breve, a:

1. Activities;
2. Focus;
3. Reminders.

Las dudas relacionadas con ownership y persistencia de Achievement permanecen
abiertas y deberán resolverse antes de cualquier conclusión definitiva sobre
esa responsabilidad.

---

## 9. Segunda revisión de evidencia

Esta revisión se realizó después del commit inicial de evidencia
`f4ce3f9`, conservando así la trazabilidad temporal entre información todavía
no verificada y hechos posteriormente comprobados directamente en el código.

### 9.1 StreakCalculator

Archivo:

`app/src/main/java/com/example/rachapro/domain/StreakCalculator.kt`

Método:

`StreakCalculator.calculate(completedDays, todayEpochDay)`

La implementación:

- descarta días posteriores a `todayEpochDay`;
- elimina duplicados;
- ordena los días;
- calcula la mayor secuencia consecutiva como `best`;
- considera hoy como posible final de la racha actual;
- si hoy no es válido, considera ayer;
- si ni hoy ni ayer son válidos, devuelve `current = 0`;
- retrocede consecutivamente desde el día final para calcular `current`.

**Clasificación:** `HECHO DEL REPOSITORIO`.

### 9.2 Día fuente de Activities

Archivo:

`app/src/main/java/com/example/rachapro/data/local/dao/ActivityDao.kt`

Método:

`observeCompletedDays(userId)`

La consulta selecciona valores distintos de `completedDateEpochDay` cuando:

- `userId = :userId`;
- `status = 'COMPLETED'`;
- `completedDateEpochDay IS NOT NULL`.

Los días se ordenan ascendentemente.

**Clasificación:** `HECHO DEL REPOSITORIO`.

#### Tensión observable

La consulta `observeCompletedDays()` no contiene `isDeleted = 0`.

Otras consultas estadísticas de actividades completadas, entre ellas
`observeCompletedActivitiesCount()`,
`observeCompletedActivitiesCountBetween()` y
`observeCompletedActivitiesByDay()`, sí aplican `isDeleted = 0`.

Por tanto, según las consultas observadas, una actividad completada que
posteriormente permanezca marcada como eliminada puede conservar su
`completedDateEpochDay` dentro del conjunto utilizado para calcular rachas.

Este punto se registra como comportamiento observable. No se concluye todavía
si corresponde a una regla de negocio intencional o a una inconsistencia de
implementación.

### 9.3 Día fuente de Focus

Archivo:

`app/src/main/java/com/example/rachapro/data/local/dao/PomodoroSessionDao.kt`

Método:

`observeCompletedFocusDays(userId)`

La consulta selecciona días cuando:

- `userId = :userId`;
- `type = 'FOCUS'`;
- `status = 'COMPLETED'`;
- `completedDateEpochDay IS NOT NULL`.

Los días se ordenan ascendentemente.

**Clasificación:** `HECHO DEL REPOSITORIO`.

Esto permite afirmar directamente que los días aportados por Focus al cálculo
de racha corresponden a sesiones de tipo `FOCUS` con estado `COMPLETED` y fecha
de finalización disponible.

### 9.4 Observación adicional sobre tiempo de enfoque

Las consultas observadas en `PomodoroSessionDao` calculan segundos de enfoque
mediante:

`SUM(plannedDurationSeconds)`

sobre sesiones con:

- `type = 'FOCUS'`;
- `status = 'COMPLETED'`.

Por tanto, la evidencia demuestra que la métrica actualmente agregada utiliza
`plannedDurationSeconds`.

Queda pendiente determinar si este campo representa conceptualmente duración
planificada, duración efectiva o si ambas coinciden por las reglas actuales de
la sesión. Hasta verificarlo no se utilizará la expresión "tiempo real de
enfoque" como afirmación demostrada.

### 9.5 Cambio epistemológico desde la primera revisión

| Elemento | Estado en `f4ce3f9` | Estado después de esta revisión |
|---|---|---|
| Semántica de racha actual | `INFORMACIÓN FALTANTE` | `HECHO DEL REPOSITORIO` |
| Semántica de mejor racha | `INFORMACIÓN FALTANTE` | `HECHO DEL REPOSITORIO` |
| Semántica exacta del día fuente de Activities | `INFORMACIÓN FALTANTE` | `HECHO DEL REPOSITORIO` |
| Semántica exacta del día fuente de Focus | `INFORMACIÓN FALTANTE` | `HECHO DEL REPOSITORIO` |
| Ownership definitivo de Achievement | `INFORMACIÓN FALTANTE` | `INFORMACIÓN FALTANTE` |

### 9.6 Estado de la decisión de frontera

La evidencia adicional fortalece el análisis de autonomía semántica del
candidato `Progress`, porque ahora existe evidencia directa de reglas de
interpretación temporal y composición de hechos procedentes de Activities y
Focus.

Sin embargo, esta revisión **no concluye** que `Progress` sea un bounded
context.

La decisión permanece abierta y será contrastada posteriormente con evidencia
equivalente de Activities, Focus y Reminders.
