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
| Día válido para racha | `app/src/main/java/com/example/rachapro/progress/ProgressViewModel.kt` → `loadProgress()` / construcción de `streakDaysFlow` | Días obtenidos mediante `activityRepository.observeCompletedDays(userId)` y `pomodoroRepository.observeCompletedFocusDays(userId)` | Lista combinada de días mediante unión, eliminación de duplicados y ordenamiento | Los días de actividades provienen de la responsabilidad Activities; los días de Pomodoro provienen de Focus | `HECHO DEL REPOSITORIO` | Falta bajar hasta los DAO para verificar exactamente qué condiciones determinan que un registro aporte un día desde Activities y Focus |
| Racha actual | `app/src/main/java/com/example/rachapro/progress/ProgressViewModel.kt` → `loadProgress()` → llamada a `StreakCalculator.calculate(...)` | Lista combinada de días y `todayEpochDay` | `streaks.current`, expuesto posteriormente como `currentStreakDays` | Los hechos base proceden de Activities y Focus; la interpretación se delega a `StreakCalculator` | `INFORMACIÓN FALTANTE` | Está demostrado que `StreakCalculator.calculate()` recibe los días y produce `current`, pero todavía no se ha inspeccionado directamente su algoritmo. No se afirma aún cuál es la semántica exacta de la racha actual |
| Mejor racha | `app/src/main/java/com/example/rachapro/progress/ProgressViewModel.kt` → `loadProgress()` → llamada a `StreakCalculator.calculate(...)` | Lista combinada de días y `todayEpochDay` | `streaks.best`, expuesto posteriormente como `bestStreakDays` | Los hechos base proceden de Activities y Focus; la interpretación se delega a `StreakCalculator` | `INFORMACIÓN FALTANTE` | Está demostrado que `StreakCalculator.calculate()` produce `best`, pero todavía no se ha inspeccionado directamente el algoritmo. No se afirma aún que represente necesariamente la máxima secuencia histórica |
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

1. implementación real de `StreakCalculator.calculate()`;
2. semántica exacta de `ActivityDao.observeCompletedDays()`;
3. semántica exacta de `PomodoroSessionDao.observeCompletedFocusDays()`;
4. comportamiento de `AchievementRepository.syncAchievements()`;
5. persistencia realizada mediante `AchievementDao`;
6. comportamiento del módulo `achievement` del backend;
7. relación entre achievements Android y backend;
8. fuente de verdad definitiva del concepto Achievement.

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
| Semántica de racha actual | `INFORMACIÓN FALTANTE` |
| Semántica de mejor racha | `INFORMACIÓN FALTANTE` |
| Periodo WEEK | `HECHO DEL REPOSITORIO` |
| Regla Android de elegibilidad de Achievement | `HECHO DEL REPOSITORIO` |
| Ownership definitivo de Achievement | `INFORMACIÓN FALTANTE` |
| Persistencia definitiva de Achievement | `INFORMACIÓN FALTANTE` |
| Semántica exacta del día fuente de Activities | `INFORMACIÓN FALTANTE` |
| Semántica exacta del día fuente de Focus | `INFORMACIÓN FALTANTE` |
| Progress como bounded context | `NO DECIDIDO` |

---

## 8. Próximo paso de auditoría

Después de registrar esta evidencia se debe inspeccionar, en este orden:

1. `StreakCalculator.kt`;
2. `ActivityDao.observeCompletedDays()`;
3. `PomodoroSessionDao.observeCompletedFocusDays()`.

La nueva evidencia deberá incorporarse mediante un commit posterior para
conservar trazabilidad temporal del cambio entre información faltante y hechos
verificados.
