# Experimento Semana 14 — Validación Negativa de Fitness Function (PASS → FAIL → PASS)

## 1. Objetivo

Demostrar de forma ejecutable la efectividad de las reglas de arquitectura **EXISTENTES** en ArchUnit (`ModularArchitectureTest.kt`) y su ejecución en la integración continua (GitHub Actions), sometiendo la suite actual a la secuencia `PASS → FAIL → PASS` para validar la protección del Monolito Modular:
> Ninguna capacidad funcional o módulo compartido puede depender directamente de las implementaciones internas de otra capacidad; el acoplamiento está restringido exclusivamente a las APIs públicas (`.api..`).

Nota: Las reglas de ArchUnit probadas ya existían previamente en `ModularArchitectureTest.kt`; no se modificó ni creó código nuevo en el archivo de prueba.

---

## 2. Procedimiento de Prueba Realizado

### Paso 1: Ejecución Estado Limpio (PASS Inicial)
- **Comando**:
  ```powershell
  $env:JAVA_HOME="C:\Program Files\Android\Android Studio1\jbr"
  cd backend
  .\gradlew.bat test --tests "com.example.rachapro.backend.architecture.ModularArchitectureTest" --no-daemon
  ```
- **Resultado**: `BUILD SUCCESSFUL` (0 violaciones, 9 pruebas de ArchUnit aprobadas exitosamente).
- **Evidencia**: [`evidencia/pass-before.txt`](file:///C:/Users/Lenovo/AndroidStudioProjects/RachaPro/experimentos/semana14-fitness-function/evidencia/pass-before.txt)

### Paso 2: Inyección de Violación Modular Controlada (FAIL)
- **Inyección**: Se agregó una dependencia directa prohibida en `GlobalExceptionHandler.kt` (`shared`) hacia `ActivityEntity` (`activities.activity`):
  ```kotlin
  // Inyección temporal de acoplamiento prohibido en shared
  private val illegalDependency: ActivityEntity? = null
  ```
- **Comando**:
  ```powershell
  $env:JAVA_HOME="C:\Program Files\Android\Android Studio1\jbr"
  cd backend
  .\gradlew.bat test --tests "com.example.rachapro.backend.architecture.ModularArchitectureTest" --no-daemon
  ```
- **Resultado**: `BUILD FAILED` (se detectaron fallos en 3 reglas de ArchUnit al introducir la dependencia prohibida).
  - Reglas de ArchUnit que fallaron:
    1. `shared does not depend on business modules` FAILED
    2. `activities internals are isolated` FAILED
    3. `modules are free of dependency cycles` FAILED
- **Evidencia**: [`evidencia/fail-negative-test.txt`](file:///C:/Users/Lenovo/AndroidStudioProjects/RachaPro/experimentos/semana14-fitness-function/evidencia/fail-negative-test.txt)

### Paso 3: Restauración y Re-validación (PASS Final)
- **Acción**: Se removió la dependencia ilegal en `GlobalExceptionHandler.kt` restaurando el archivo a su estado original limpio.
- **Comando**:
  ```powershell
  $env:JAVA_HOME="C:\Program Files\Android\Android Studio1\jbr"
  cd backend
  .\gradlew.bat test --tests "com.example.rachapro.backend.architecture.ModularArchitectureTest" --no-daemon
  ```
- **Resultado**: `BUILD SUCCESSFUL` (9/9 pruebas de ArchUnit pasadas exitosamente).
- **Evidencia**: [`evidencia/pass-after.txt`](file:///C:/Users/Lenovo/AndroidStudioProjects/RachaPro/experimentos/semana14-fitness-function/evidencia/pass-after.txt)

---

## 3. Trazabilidad en CI/CD

El workflow existente de GitHub Actions [`backend-architecture.yml`](file:///C:/Users/Lenovo/AndroidStudioProjects/RachaPro/.github/workflows/backend-architecture.yml) ejecuta de forma automatizada este mismo comando en cada Push y Pull Request sobre `master`. Si cualquier desarrollador introduce una dependencia directa prohibida entre módulos en el futuro, la suite fallará automáticamente en el runner de CI, bloqueando el merge del PR.
