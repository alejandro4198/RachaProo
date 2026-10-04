# Evidencia de dominio — Progress

## 1. Responsabilidad observada

`Progress` combina información proveniente de Activities y Focus para construir
métricas derivadas como:

- racha actual
- mejor racha
- estadísticas por periodo
- estadísticas semanales
- insumos utilizados por reglas de achievements

La evidencia observada corresponde principalmente al cliente Android.

---

## 2. Conceptos y lenguaje propio

Conceptos observados:

- current streak
- best streak
- completed day
- `TODAY`
- `WEEK`
- `ALL`
- weekly progress
- completed activities
- completed Focus Pomodoros
- focus seconds
- achievement eligibility

**Clasificación:** `HECHO DEL REPOSITORIO`.

---

## 3. Reglas propias observadas

### Racha actual

`StreakCalculator.calculate(completedDays, todayEpochDay)`:

- descarta días futuros
- elimina duplicados
- ordena los días
- considera hoy como final posible
- si hoy no es válido, considera ayer
- si ni hoy ni ayer son válidos, devuelve `current = 0`
- recorre hacia atrás los días consecutivos para obtener la racha actual

**Clasificación:** `HECHO DEL REPOSITORIO`.

### Mejor racha

El mismo cálculo obtiene la mayor secuencia histórica de días consecutivos
válidos.

**Clasificación:** `HECHO DEL REPOSITORIO`.

### Periodo WEEK

`ProgressViewModel` construye un intervalo semanal y combina:

- actividades completadas
- Pomodoros Focus completados
- segundos de Focus

Está pendiente determinar si esta definición pertenece a una regla de dominio o
a una regla de consulta/presentación.

**Clasificación de la implementación:** `HECHO DEL REPOSITORIO`.

---

## 4. Hechos que produce

Se observan resultados derivados como:

- racha actual
- mejor racha
- métricas agregadas por periodo
- métricas diarias/semanales
- datos utilizados para determinar elegibilidad de achievements

No se ha demostrado que Progress publique eventos explícitos de integración.

**Eventos explícitos producidos:** `INFORMACIÓN FALTANTE`.

---

## 5. Información que consume

Progress consume hechos derivados de:

### Activities

Días de actividades que cumplen:

- `userId = :userId`
- `status = 'COMPLETED'`
- `completedDateEpochDay IS NOT NULL`

`ActivityDao.observeCompletedDays()` no aplica `isDeleted = 0`.

También consume conteos y estadísticas de actividades completadas.

### Focus

Días de sesiones que cumplen:

- `userId = :userId`
- `type = 'FOCUS'`
- `status = 'COMPLETED'`
- `completedDateEpochDay IS NOT NULL`

También consume:

- conteos de sesiones Focus completadas
- estadísticas por día
- suma de `plannedDurationSeconds`

**Clasificación:** `HECHO DEL REPOSITORIO`.

---

## 6. Dependencias con otras responsabilidades

El cálculo observado depende de información producida por:

```text
Activities ──┐
             ├──> Progress
Focus ───────┘

```

Esto representa dependencia de información observada en el AS-IS.

No se decide todavía cuál debe ser el mecanismo definitivo de integración entre
estas responsabilidades.

**Clasificación:** `HECHO DEL REPOSITORIO` para la dependencia actual.

---

## 7. Qué explícitamente no parece pertenecerle

Con la evidencia actual, Progress no controla:

- creación o edición de Activity
- ciclo de vida `PENDING / OVERDUE / COMPLETED` de Activity
- ciclo de vida de PomodoroSession
- asociación opcional entre PomodoroSession y Activity
- autenticación del usuario

Progress interpreta hechos provenientes de otras responsabilidades.

El ownership definitivo de Achievement tampoco está demostrado todavía.

---

## 8. Tensiones y contradicciones

### Activities eliminadas

`ActivityDao.observeCompletedDays()` no aplica:

`isDeleted = 0`

mientras otras consultas estadísticas de actividades completadas sí lo hacen.

Por tanto, una Activity completada y posteriormente eliminada puede conservar
su día dentro del conjunto utilizado para calcular rachas.

No se concluye si esto representa:

- una regla de negocio intencional
- o una inconsistencia de implementación

### Tiempo de Focus

Las métricas observadas suman:

`plannedDurationSeconds`

de sesiones `FOCUS` con estado `COMPLETED`.

No se ha demostrado que represente necesariamente tiempo real efectivamente
enfocado.

### Achievement

Existe una regla Android de elegibilidad de achievements, pero permanece
pendiente determinar:

- ownership definitivo
- persistencia definitiva
- relación con el módulo `achievement` del backend

---

## 9. Información faltante

Queda pendiente verificar:

1. comportamiento de `AchievementRepository.syncAchievements()`
2. persistencia mediante `AchievementDao`
3. comportamiento del módulo `achievement` del backend
4. relación entre achievements Android y backend
5. fuente de verdad definitiva del concepto Achievement
6. intención de negocio respecto a Activities completadas y posteriormente eliminadas
7. significado exacto de `plannedDurationSeconds`
8. política temporal y de zona horaria compartida entre productores de `completedDateEpochDay` y el cálculo de rachas

---

## 10. Estado epistemológico

| Elemento | Estado |
|---|---|
| Composición de días Activities + Focus | `HECHO DEL REPOSITORIO` |
| Semántica de racha actual | `HECHO DEL REPOSITORIO` |
| Semántica de mejor racha | `HECHO DEL REPOSITORIO` |
| Periodo WEEK | `HECHO DEL REPOSITORIO` |
| Día fuente de Activities | `HECHO DEL REPOSITORIO` |
| Día fuente de Focus | `HECHO DEL REPOSITORIO` |
| Uso de `plannedDurationSeconds` | `HECHO DEL REPOSITORIO` |
| Regla Android de elegibilidad de Achievement | `HECHO DEL REPOSITORIO` |
| Ownership definitivo de Achievement | `INFORMACIÓN FALTANTE` |
| Persistencia definitiva de Achievement | `INFORMACIÓN FALTANTE` |
| Semántica de Activities eliminadas en rachas | `INFORMACIÓN FALTANTE` |
| Significado de tiempo efectivo | `INFORMACIÓN FALTANTE` |

---

## 11. Estado de frontera

La evidencia muestra una responsabilidad clara de interpretación y agregación
de hechos provenientes principalmente de Activities y Focus.

Sin embargo:

`Progress como bounded context = NO DECIDIDO`

La decisión se tomará únicamente después de comparar este candidato con
Activities, Focus y Reminders utilizando el mismo criterio.
