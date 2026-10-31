# Corrección y Auditoría — Solicitudes Duplicadas de Logros (Achievements)

## 1. Hallazgo

Se investigó la causa potencial de llamadas repetidas al endpoint HTTP `GET /api/achievements` desde la aplicación Android.

En `ProfileViewModel.loadProfile()`, el `combine` que construía el estado de Perfil incluía tanto las preferencias del usuario como la lista local de achievements (`achievementRepository.observeAchievements(userId)`), y ejecutaba `achievementRepository.syncAchievements(...)` dentro del mismo collector que alimentaba la UI.

---

## 2. Evidencia

- El trigger de sincronización estaba acoplado a una fuente (`observeAchievements`) que también era modificada por la propia sincronización.
- Cada emisión del Flow combinado podía ejecutar una nueva sincronización remota, con independencia de que los criterios de desbloqueo hubieran cambiado.
- Una escritura sobre la tabla achievements observada por Room puede provocar una nueva emisión del Flow, haciendo que el collector anterior pudiera volver a ejecutar la sincronización.

No se reprodujo en runtime el comportamiento original durante esta auditoría; la evidencia disponible es estática sobre el flujo reactivo.

---

## 3. Corrección

1. **Separación de responsabilidades (`ProfileViewModel.kt`)**:
   - El collector de UI sigue observando preferencias y achievements locales para refrescar la pantalla.
   - La evaluación y sincronización de achievements se aisló en un collector dedicado.
2. **Trigger acotado**:
   - El collector de sincronización depende únicamente de:
     - `completedActivities`;
     - `completedPomodoros`;
     - `streak`.
   - No observa achievements locales ni preferencias, por lo que las escrituras sobre la tabla achievements ya no forman parte de su entrada.
3. **Serialización de sincronizaciones**:
   - Se aplica `distinctUntilChanged()` antes del collector para evitar reevaluaciones con entradas idénticas.
   - Se usa `collect` (no `collectLatest`) porque `syncAchievements()` tiene efectos secundarios remotos y locales: no debe cancelarse a mitad de ejecución cuando llega un nuevo `AchievementCheckInput`. `collect` procesa las entradas de forma secuencial, sin cancelar la operación en curso.

---

## 4. Lifecycle

- `loadProfile()` usa `loadJob`.
- Cada nueva ejecución de `loadProfile()` cancela el `loadJob` anterior (`loadJob?.cancel()`).
- Ambos collectors (UI y sincronización) son hijos de un `coroutineScope` dentro de ese `loadJob`.
- Por tanto, `retry()` cancela los collectors anteriores antes de crear los nuevos: no quedan collectors huérfanos ni sincronizaciones concurrentes de dos generaciones distintas.

---

## 5. Precisión

- No se reprodujo en runtime el comportamiento original durante esta auditoría.
- La corrección se apoya en evidencia estática del flujo reactivo, no en una medición runtime del número de solicitudes antes/después.

---

## 6. Auditoría de Git

- Rama: `fix/achievements-duplicate-requests`
- Archivos que componen el fix:
  - `app/src/main/java/com/example/rachapro/profile/ProfileViewModel.kt`
  - `docs/semana11/achievements-duplicate-requests-fix.md`
- Las modificaciones preexistentes en `.idea/` y los directorios de `experimentos/` no forman parte de este fix y no fueron tocadas.
- El commit de este fix incluye únicamente estos dos archivos; `.idea/` y los directorios de `experimentos/` permanecen fuera del commit.
