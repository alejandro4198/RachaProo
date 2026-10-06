# Evidencia de dominio — Progress

## 1. Responsabilidad observada

### HECHO DEL REPOSITORIO

En el cliente Android se observan componentes que combinan e interpretan
información proveniente principalmente de Activities y Focus para construir
resultados derivados de progreso.

Los componentes principales observados son:

- `ProgressViewModel`
- `StreakCalculator`

También existen reglas relacionadas con Achievement que consumen información de
progreso mediante:

- `AchievementEngine`
- `AchievementRepository`
- `ProfileViewModel`

Sin embargo, el ownership definitivo de Achievement no se encuentra cerrado en
esta revisión.

`ProgressViewModel` construye resultados como:

- racha actual
- mejor racha
- métricas de actividades completadas
- métricas de Pomodoros Focus completados
- tiempo de enfoque
- estadísticas por periodo
- estadísticas semanales

### INFERENCIA

Progress no controla los ciclos de vida internos de Activity ni
PomodoroSession.

Su comportamiento observado consiste principalmente en consumir hechos
producidos por otras capacidades y otorgarles un significado adicional mediante
reglas de cálculo, combinación y agregación.

Esta interpretación constituye una capacidad funcional diferenciable de las
capacidades que producen los hechos originales.

### DECISIÓN DEL EQUIPO

La responsabilidad protegida se formula como:

**interpretar y agregar los resultados producidos por otras capacidades del
sistema para medir y representar el avance del usuario mediante rachas y
métricas de progreso.**

Esta formulación constituye una decisión de modelado del equipo y no un hecho
literal extraído del repositorio.

---

## 2. Conceptos y lenguaje propio

### Conceptos funcionales observados

- progreso
- día completado
- racha actual
- mejor racha
- consecutividad
- métricas de progreso
- actividades completadas
- Pomodoros Focus completados
- tiempo de enfoque
- progreso semanal
- estadísticas por periodo

También aparecen los periodos:

- `TODAY`
- `WEEK`
- `ALL`

### Representaciones observadas

- `currentStreakDays`
- `bestStreakDays`
- `completedDateEpochDay`
- `completedActivities`
- `completedPomodoros`
- `focusSeconds`

### HECHO DEL REPOSITORIO

Estos conceptos aparecen en componentes como:

- `ProgressViewModel`
- `StreakCalculator`
- consultas de `ActivityDao`
- consultas de `PomodoroSessionDao`

### INFERENCIA

Los términos:

- racha actual
- mejor racha
- día válido para racha
- progreso agregado

poseen significado distinto de los conceptos internos de Activities y Focus.

Una Activity puede conocer su propio estado de completitud y una
PomodoroSession puede conocer que finalizó, pero la interpretación de esos
hechos como parte de una racha o como métrica transversal de progreso ocurre en
otra responsabilidad.

Los periodos `TODAY`, `WEEK` y `ALL` pueden representar principalmente criterios
de consulta o presentación y no se utilizan, por sí solos, como evidencia de
autonomía de dominio.

---

## 3. Reglas propias observadas

### Composición de días para racha

Archivo principal:

`app/src/main/java/com/example/rachapro/progress/ProgressViewModel.kt`

### HECHO DEL REPOSITORIO

`ProgressViewModel` obtiene días completados desde:

- `ActivityRepository`
- `PomodoroRepository`

y construye un conjunto combinado antes de delegar el cálculo a
`StreakCalculator`.

La composición:

- une los días provenientes de Activities y Focus
- elimina duplicados
- ordena los días

Por tanto, un mismo día producido por ambas capacidades no se contabiliza dos
veces como dos días diferentes de racha.

---

### Día proveniente de Activities

La información utilizada por Progress se obtiene mediante:

`ActivityDao.observeCompletedDays(...)`

### HECHO DEL REPOSITORIO

La consulta observada utiliza condiciones equivalentes a:

```text
userId = :userId
status = 'COMPLETED'
completedDateEpochDay IS NOT NULL
```

y obtiene días distintos ordenados ascendentemente.

La consulta observada no incluye:

```text
isDeleted = 0
```

Esta diferencia respecto de otras consultas estadísticas se registra
posteriormente como tensión.

---

### Día proveniente de Focus

La información utilizada por Progress se obtiene mediante una consulta
equivalente a:

`PomodoroSessionDao.observeCompletedFocusDays(...)`

### HECHO DEL REPOSITORIO

Para aportar un día se consideran sesiones que cumplen:

```text
userId = :userId
type = 'FOCUS'
status = 'COMPLETED'
completedDateEpochDay IS NOT NULL
```

Por tanto, no cualquier sesión Pomodoro aporta automáticamente a la racha.

---

### Racha actual

Archivo:

`app/src/main/java/com/example/rachapro/domain/StreakCalculator.kt`

Método:

`StreakCalculator.calculate(...)`

### HECHO DEL REPOSITORIO

El cálculo observado:

- descarta días posteriores a `todayEpochDay`
- elimina días duplicados
- ordena los días
- considera hoy como posible final de la racha
- si hoy no es válido, considera ayer
- si ni hoy ni ayer están presentes, devuelve racha actual igual a `0`
- desde el día final válido recorre hacia atrás buscando días consecutivos

Por tanto, la racha actual no equivale simplemente a contar todos los días
completados.

Existe una regla de consecutividad.

---

### Mejor racha

El mismo método:

`StreakCalculator.calculate(...)`

calcula además la mayor secuencia histórica de días consecutivos presentes en el
conjunto válido.

### HECHO DEL REPOSITORIO

La mejor racha se deriva del histórico de días válidos y no necesariamente
coincide con la racha actual.

---

### Periodos de progreso

Archivo:

`ProgressViewModel.kt`

Se observan:

- `TODAY`
- `WEEK`
- `ALL`

### HECHO DEL REPOSITORIO

Progress utiliza estos periodos para seleccionar o agregar:

- actividades completadas
- Pomodoros Focus completados
- tiempo de enfoque

### Periodo WEEK

Para `WEEK`, `ProgressViewModel` construye el intervalo correspondiente a la
semana y combina métricas provenientes de Activities y Focus.

También se observan estadísticas diarias que permiten construir una
representación semanal con valores como:

- `completedActivities`
- `completedPomodoros`
- `focusSeconds`

### INFERENCIA

La existencia de `TODAY`, `WEEK` y `ALL` demuestra comportamiento de agregación
y consulta.

No se afirma que cada una de estas opciones represente por sí misma una regla
central del dominio.

---

### Tiempo de enfoque

### HECHO DEL REPOSITORIO

Las métricas observadas de tiempo de Focus utilizan información basada en:

`plannedDurationSeconds`

de sesiones que cumplen las condiciones de Focus utilizadas por las consultas.

Por tanto, con la evidencia actual no debe describirse esta métrica como tiempo
efectivo exacto de concentración si el código está agregando duración
planificada.

---

### Achievement

Archivo:

`app/src/main/java/com/example/rachapro/domain/AchievementEngine.kt`

Método:

`AchievementEngine.typesToUnlock(...)`

### HECHO DEL REPOSITORIO

La lógica Android observada recibe información agregada relacionada con:

- cantidad de Activities completadas
- cantidad de Pomodoros Focus completados
- racha actual

y determina tipos de Achievement elegibles para desbloqueo.

Entre las reglas observadas se encuentran umbrales relacionados con:

- primera Activity completada
- primer Pomodoro Focus
- rachas
- cantidades acumuladas de Activities
- cantidades acumuladas de Pomodoros

### Precisión epistemológica

Esto demuestra que Achievement consume métricas relacionadas con Progress.

No demuestra todavía que Achievement pertenezca al bounded context Progress.

**Ownership de Achievement:** `NO DECIDIDO`.

---

## 4. Hechos que produce

A partir de las reglas observadas pueden describirse resultados derivados como:

- racha actual calculada
- mejor racha calculada
- día considerado válido para progreso
- métricas de Activities completadas
- métricas de Pomodoros Focus completados
- tiempo de enfoque agregado
- métricas por periodo
- progreso semanal
- estadísticas por día

Estos resultados son derivados de hechos producidos originalmente por otras
capacidades.

### HECHO DEL REPOSITORIO

Activities produce hechos relacionados con completitud de Activity.

Focus produce hechos relacionados con completitud de PomodoroSession.

Progress combina e interpreta esos hechos.

### Achievement

Las métricas de Progress pueden formar parte de las entradas utilizadas por
reglas de elegibilidad de Achievement.

Esto no transfiere automáticamente ownership de Achievement a Progress.

### Eventos explícitos de integración

No se ha demostrado en esta revisión que Progress publique eventos explícitos de
integración derivados de:

- cambios de racha
- cambios de estadísticas
- cambios de métricas

**Clasificación:** `INFORMACIÓN FALTANTE`.

---

## 5. Información que consume

### Activities

Progress consume información relacionada con:

- Activities completadas
- días en que existieron Activities completadas
- cantidad de Activities completadas
- estadísticas de completitud por día
- completitud dentro de determinados rangos temporales

### HECHO DEL REPOSITORIO

Activities es responsable del ciclo de vida de Activity.

Progress utiliza información resultante de ese ciclo sin controlar las reglas
internas de Activity.

---

### Focus

Progress consume información relacionada con:

- Pomodoros `FOCUS` completados
- días en los que existieron sesiones `FOCUS` completadas
- cantidades de Pomodoros completados
- estadísticas por día
- duración agregada de Focus

### HECHO DEL REPOSITORIO

Focus conserva el ciclo de vida de `PomodoroSession`.

Progress consume resultados derivados de ese ciclo para construir métricas
propias.

---

### Tiempo

Progress utiliza referencias temporales como:

- `todayEpochDay`
- `completedDateEpochDay`
- intervalos temporales para `TODAY`
- intervalos temporales para `WEEK`

La interpretación correcta de estos datos depende de una política temporal
coherente entre productores y consumidor.

---

## 6. Dependencias con otras responsabilidades

### Activities

La relación conceptual observada es:

```text
Activities
    |
    | Activities completadas
    | completedDateEpochDay
    v
Progress
```

### HECHO DEL REPOSITORIO

Progress obtiene información de completitud producida por Activities.

Progress no controla:

- creación de Activity
- modificación de Activity
- vencimiento
- Category
- Subtask
- eliminación de Activity

La dependencia de información no transfiere ownership de Activity a Progress.

---

### Focus

La relación conceptual observada es:

```text
Focus
    |
    | FOCUS completados
    | completedDateEpochDay
    | duración
    v
Progress
```

### HECHO DEL REPOSITORIO

Progress utiliza datos producidos por Focus.

Progress no controla:

- inicio de PomodoroSession
- pausa
- reanudación
- cancelación
- finalización interna de la sesión

---

### Achievement

Existe una relación entre métricas utilizadas en Progress y reglas Android de
Achievement.

Puede representarse conceptualmente como:

```text
Activities ──┐
             |
Focus ───────┼──> métricas / racha ──> AchievementEngine
             |
Progress ────┘
```

Sin embargo, esta representación no define ownership.

### Estado

`Ownership de Achievement = NO DECIDIDO`

La relación completa entre:

- `AchievementEngine`
- `AchievementRepository`
- `AchievementDao`
- backend `achievement`

debe mantenerse separada de la decisión de frontera de Progress hasta completar
su análisis específico.

---

### Identity

Progress utiliza métricas asociadas a un `userId`.

Esto no significa que Progress posea:

- autenticación
- credenciales
- registro
- login
- ciclo de vida del usuario

---

### Reminders

No se observó durante esta revisión una dependencia directa necesaria entre
Reminders y las reglas principales de Progress.

Esto no se utiliza como prueba de inexistencia absoluta de relaciones en todo el
repositorio.

**Clasificación:** `NO OBSERVADO EN EL CÓDIGO INSPECCIONADO / POR VERIFICAR`.

---

## 7. Qué explícitamente no pertenece

Según la decisión de frontera adoptada, Progress no posee:

- ciclo de vida de Activity
- Category
- Subtask
- ciclo de vida de PomodoroSession
- programación o entrega de Reminder
- autenticación
- credenciales
- ciclo interno de Identity

Progress tampoco adquiere ownership de los hechos originales únicamente porque
los utilice para construir métricas.

Por tanto:

```text
Activity completada
→ pertenece funcionalmente a Activities

PomodoroSession completada
→ pertenece funcionalmente a Focus

interpretación transversal de esos hechos como progreso
→ DECISIÓN DEL EQUIPO / INFERENCIA DE MODELADO asociada a Progress
```

### Achievement

Achievement no se incorpora todavía explícitamente dentro de la frontera.

Aunque consume:

- Activities completadas
- Pomodoros completados
- racha

su ownership definitivo permanece abierto.

**Clasificación:** `NO DECIDIDO`.

---

## 8. Tensiones y contradicciones

### Activities eliminadas y semilla de racha

`ActivityDao.observeCompletedDays()` utiliza:

```text
status = 'COMPLETED'
completedDateEpochDay IS NOT NULL
```

pero no aplica:

```text
isDeleted = 0
```

Otras consultas estadísticas relacionadas con Activities completadas sí excluyen
Activities eliminadas.

Por tanto, una Activity:

1. completada
2. posteriormente eliminada

puede continuar aportando su día a la semilla utilizada para racha mientras deja
de participar en otras métricas.

No se concluye todavía si esto representa:

- conservación histórica intencional
- diferencia semántica deliberada
- inconsistencia entre consultas

**Clasificación:** `TENSIÓN DOCUMENTADA`.

---

### Semántica temporal

Activities registra información como:

`completedDateEpochDay`

utilizando una noción de fecha local.

Focus también produce días utilizados posteriormente por Progress.

Progress combina esas fechas y utiliza:

`todayEpochDay`

para decidir continuidad.

Queda pendiente establecer una política explícita y consistente de:

- zona horaria
- cambio de día
- usuario que cambia de zona horaria
- productor y consumidor ejecutándose con contextos temporales distintos

**Clasificación:** `INFORMACIÓN FALTANTE / TENSIÓN DOCUMENTADA`.

---

### Tiempo planificado vs tiempo efectivo de Focus

Las métricas observadas de Focus agregan:

`plannedDurationSeconds`

### TENSIÓN

El nombre funcional:

`focusSeconds`

podría interpretarse como tiempo efectivamente concentrado.

Sin embargo, la implementación observada utiliza duración planificada.

Por tanto, debe evitarse afirmar que representa necesariamente tiempo real
efectivo de concentración.

**Clasificación:** `TENSIÓN SEMÁNTICA DOCUMENTADA`.

---

### Regla de dominio vs proyección

`ProgressViewModel` contiene:

- cálculo de intervalos
- agregaciones
- selección de periodos
- construcción de datos para visualización

Algunas de estas operaciones pueden corresponder a reglas semánticas del progreso
y otras a proyecciones/read models.

Ejemplo:

```text
StreakCalculator
→ contiene una interpretación semántica clara

TODAY / WEEK / ALL
→ pueden representar principalmente formas de consulta/presentación
```

La coexistencia de ambos tipos de comportamiento no invalida la frontera, pero
impide asumir que todo el contenido de `ProgressViewModel` representa dominio.

**Clasificación:** `TENSIÓN DE MODELADO`.

---

### Distribución física de las reglas

Las reglas relacionadas con progreso se encuentran distribuidas entre:

- `ProgressViewModel`
- `StreakCalculator`
- DAO
- repositories
- componentes relacionados con Achievement

### INFERENCIA

La posible capacidad funcional Progress no coincide exactamente con una única
ubicación física del código.

La estructura de paquetes no se utiliza como prueba de bounded context.

**Clasificación:** `TENSIÓN ARQUITECTÓNICA / DE IMPLEMENTACIÓN`.

---

### Achievement

`AchievementEngine` utiliza información relacionada con Progress, Activities y
Focus.

Sin embargo, también existe infraestructura propia de Achievement:

- `AchievementRepository`
- `AchievementDao`
- backend `achievement`

Por tanto, incluir Achievement automáticamente dentro de Progress sería una
decisión prematura.

**Clasificación:** `OWNERSHIP NO DECIDIDO`.

---

## 9. Información faltante

### Preguntas de dominio

Queda pendiente determinar:

1. si toda métrica actualmente expuesta desde Progress pertenece realmente a su
   dominio o algunas son exclusivamente de presentación
2. semántica definitiva del tiempo de enfoque mostrado al usuario
3. comportamiento esperado de la racha cuando una Activity completada es
   posteriormente eliminada
4. comportamiento esperado ante cambios de zona horaria
5. si la definición de `WEEK` constituye una regla funcional relevante o
   solamente una proyección
6. ownership definitivo de Achievement
7. relación semántica definitiva entre Achievement y Progress

### Preguntas de integración e implementación

Queda pendiente verificar o decidir:

1. mecanismo definitivo mediante el cual Progress obtiene hechos de Activities
2. mecanismo definitivo mediante el cual Progress obtiene hechos de Focus
3. si el diseño final continuará consultando snapshots locales de Room
4. si Progress tendrá contratos explícitos con otros bounded contexts
5. si existirán eventos de integración para hechos consumidos por Progress
6. si Progress publicará eventos derivados de sus propios resultados
7. correspondencia completa Android/backend para Achievement
8. fuente de verdad de Achievement
9. persistencia definitiva de Achievement
10. posible duplicación de reglas de Achievement entre Android y backend
11. política compartida de fecha y zona horaria
12. estrategia de consistencia de métricas derivadas ante cambios en sus hechos
    fuente

---

## 10. Estado epistemológico

| Elemento | Estado |
|---|---|
| Progress combina información de Activities y Focus | `HECHO DEL REPOSITORIO` |
| Composición de días para racha | `HECHO DEL REPOSITORIO` |
| `StreakCalculator` descarta días futuros | `HECHO DEL REPOSITORIO` |
| Eliminación de días duplicados | `HECHO DEL REPOSITORIO` |
| Regla de consecutividad | `HECHO DEL REPOSITORIO` |
| Cálculo de racha actual | `HECHO DEL REPOSITORIO` |
| Cálculo de mejor racha | `HECHO DEL REPOSITORIO` |
| `TODAY / WEEK / ALL` | `HECHO DEL REPOSITORIO` |
| Agregación de Activities completadas | `HECHO DEL REPOSITORIO` |
| Agregación de Pomodoros Focus completados | `HECHO DEL REPOSITORIO` |
| Agregación de duración de Focus | `HECHO DEL REPOSITORIO` |
| Uso de `plannedDurationSeconds` en métricas observadas | `HECHO DEL REPOSITORIO` |
| `observeCompletedDays()` sin `isDeleted = 0` | `HECHO DEL REPOSITORIO` |
| Diferencia de tratamiento de eliminadas | `TENSIÓN DOCUMENTADA` |
| Política temporal / zona horaria | `INFORMACIÓN FALTANTE` |
| Progress interpreta hechos de otras capacidades | `INFERENCIA DE MODELADO` |
| Algunas métricas son proyecciones/read models | `INFERENCIA DE MODELADO` |
| Regla Android de elegibilidad de Achievement | `HECHO DEL REPOSITORIO` |
| Ownership definitivo de Achievement | `NO DECIDIDO` |
| Eventos explícitos de integración de Progress | `INFORMACIÓN FALTANTE` |
| Integración definitiva con Activities y Focus | `NO DECIDIDA` |
| Progress como bounded context | `DECISIÓN DEL EQUIPO` |

---

## 11. Decisión sobre la frontera

### Evidencia considerada

La decisión considera que:

- Progress combina hechos provenientes de Activities y Focus
- los hechos fuente conservan ownership en sus capacidades de origen
- `StreakCalculator` aplica reglas propias sobre esos hechos
- la racha actual no corresponde simplemente a un conteo de registros
- la mejor racha representa una interpretación histórica adicional
- Progress genera significado que no pertenece individualmente a Activity ni a
  PomodoroSession
- posee lenguaje propio alrededor de rachas, días válidos y métricas de progreso
- sus reglas de interpretación pueden distinguirse del ciclo de vida interno de
  Activities y Focus
- las métricas pueden incluir proyecciones/read models sin que toda la frontera
  deba reducirse a presentación
- Achievement consume información relacionada, pero su ownership permanece
  abierto

### Inferencia de modelado

La evidencia permite inferir que existe autonomía semántica alrededor de la
interpretación del progreso.

Esta autonomía no constituye por sí sola un hecho del repositorio.

Es la base utilizada por el equipo para tomar una decisión arquitectónica.

### Decisión del equipo

El equipo decide modelar `Progress` como un bounded context independiente.

La decisión no se basa en:

- existencia de `ProgressViewModel`
- nombre de paquetes
- ubicación física de `StreakCalculator`

Se basa en:

- lenguaje propio
- reglas propias
- interpretación de hechos provenientes de otras capacidades
- producción de significado nuevo
- razonamiento arquitectónico: las reglas de interpretación de progreso pueden evaluarse y modificarse separadamente de los ciclos de vida internos de Activities y Focus, siempre que se mantengan los hechos necesarios de entrada
- responsabilidad funcional diferenciable

**Clasificación:** `DECISIÓN DEL EQUIPO`.

---

### Responsabilidad protegida

Progress protege la capacidad de:

**interpretar y agregar los resultados producidos por otras capacidades del
sistema para medir y representar el avance del usuario mediante rachas y
métricas de progreso.**

La responsabilidad no consiste en producir originalmente:

- Activities completadas
- sesiones Focus completadas

Esos hechos continúan perteneciendo a sus respectivos contextos.

Progress posee su interpretación como señales del avance del usuario.

---

### Qué queda dentro de esta frontera

La frontera incluye conceptualmente:

- interpretación de los días derivados de hechos de completitud producidos por Activities y Focus
- cálculo de racha actual
- cálculo de mejor racha
- métricas agregadas de progreso
- estadísticas y proyecciones utilizadas para representar progreso

La presencia de proyecciones no significa que todas las consultas sean reglas
profundas de dominio.

Su inclusión responde a que sirven a la capacidad protegida de medir y
representar progreso.

---

### Qué queda fuera de esta frontera

No pertenece a Progress:

- ciclo de vida de Activity
- Category
- Subtask
- ciclo de vida de PomodoroSession
- programación de Reminder
- entrega de Reminder
- autenticación
- credenciales
- ciclo de vida interno de Identity

### Achievement

Achievement **no se incluye todavía como parte decidida de Progress**.

La evidencia demuestra que sus reglas consumen métricas relacionadas con:

- Activities
- Focus
- racha

pero no demuestra todavía su ownership definitivo.

Por tanto:

`Achievement ownership = NO DECIDIDO`

---

### Dependencias relevantes

#### Activities

Progress consume hechos relacionados con Activities completadas.

La relación conceptual es:

```text
Activities
    |
    | hechos de completitud
    v
Progress
```

Activities conserva ownership sobre Activity.

---

#### Focus

Progress consume hechos relacionados con Pomodoros Focus completados y duración.

La relación conceptual es:

```text
Focus
    |
    | hechos de sesiones completadas
    v
Progress
```

Focus conserva ownership sobre `PomodoroSession`.

---

#### Achievement

Achievement consume métricas relacionadas con Progress, pero la dirección final
de dependencia y el ownership deberán cerrarse durante su análisis específico.

No se incorpora todavía Achievement dentro de la frontera Progress.

---

### Condiciones de revisión

Como **decisión del equipo**, la frontera deberá reconsiderarse si en el futuro
ocurre alguno de los siguientes cambios:

1. las reglas de racha dejan de ser responsabilidad de Progress y pasan a estar
   controladas completamente por otra capacidad
2. Progress deja de interpretar hechos y queda reducido únicamente a mostrar
   información producida íntegramente por otros contextos
3. desaparecen el lenguaje y las reglas propias relacionadas con rachas y
   progreso
4. otra capacidad absorbe completamente las reglas de cálculo e interpretación
   de las métricas
5. Activities o Focus pasan a controlar directamente la semántica transversal
   del progreso global
6. las métricas actualmente agrupadas bajo Progress evolucionan hacia
   responsabilidades independientes con lenguaje y reglas suficientemente
   distintas para justificar nuevas fronteras

Los siguientes cambios hipotéticos podrían utilizarse como pruebas de evolución,
pero **no constituyen evidencia del comportamiento actual**:

- cambiar los requisitos para que un día cuente para racha
- introducir una cantidad mínima de Activities
- introducir un mínimo de tiempo de Focus
- permitir días de gracia
- modificar la política de continuidad

Estos escenarios únicamente ilustran que las reglas de Progress pueden evaluarse
separadamente de los ciclos internos de Activities y Focus.

---

### Estado de frontera

`Progress como bounded context = DECIDIDO`

**Decisión del equipo:** bounded context independiente responsable de interpretar
y agregar resultados producidos por otras capacidades para medir y representar
el avance del usuario mediante rachas y métricas de progreso.

`Achievement ownership = NO DECIDIDO`
