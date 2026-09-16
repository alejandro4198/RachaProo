# Regresión funcional posterior a Semana 8

## Objetivo

Validar que la aplicación Android continúe funcionando correctamente después del refactor modular del backend y distinguir entre regresiones introducidas por Semana 8, defectos preexistentes y mejoras funcionales.

## Contexto

Baseline previo al refactor:

- `semana8-baseline-pre-modular`

Baseline posterior al refactor:

- `semana8-baseline-post-modular`

Cierre funcional:

- Fix commit: `a9e16b3`
- Merge PR #25: `353b7ed`

## Metodología

La validación combinó:

- ejecución manual en dispositivo Android;
- Logcat;
- llamadas HTTP Android → backend;
- revisión de código Android;
- comparación histórica mediante Git;
- compilación y pruebas unitarias.

No se atribuyó causalidad al refactor modular sin evidencia histórica en Git.

## Hallazgos

### RF-01 — estado vencido no se actualizaba permaneciendo en Actividades

Estado inicial:

- El estado remoto podía actualizarse correctamente.
- La pantalla ejecutaba `refreshStatuses()` al entrar.
- No existía actualización temporal mientras la pantalla permanecía visible.
- El usuario debía navegar fuera y regresar para visualizar el nuevo estado.

Clasificación:

- defecto funcional preexistente;
- no introducido por el refactor modular de Semana 8.

Corrección:

- se agregó actualización periódica mientras `ActivitiesScreen` permanece compuesto;
- la actualización se alinea con el cambio de minuto;
- el efecto se cancela al abandonar la pantalla.

Validación:

- actividad creada con vencimiento próximo;
- se permaneció en la pantalla Actividades;
- el estado cambió a vencida sin abandonar la pantalla;
- `PATCH /api/activities/refresh-statuses` respondió correctamente;
- posteriormente se recargaron las actividades.

Resultado:

✅ CORREGIDO Y VALIDADO

---

### RF-02 — categorías visibles pero no seleccionables

Observación:

- las categorías aparecen visualmente en la pantalla de actividades;
- no funcionan como filtro seleccionable.

Comparación histórica:

- la implementación previa también utilizaba `CategoryCard(category = category)`;
- `CategoryCard` no recibía callback de selección;
- `ActivityFilter` solo contempla:
  - ALL
  - TODAY
  - PENDING
  - OVERDUE
  - COMPLETED

Clasificación:

- no corresponde a una regresión;
- no fue introducido por Semana 8;
- se considera mejora funcional futura.

Resultado:

⚪ MEJORA FUNCIONAL PENDIENTE

---

### RF-03 — actividad completada no aparecía en Progreso

Diagnóstico:

La pantalla Actividades consumía datos del backend y los mantenía en memoria, mientras que Progreso obtenía sus métricas desde Room.

El flujo remoto:

Backend → ActivityResponse → ActivitiesViewModel

no sincronizaba las actividades recibidas con Room.

Como consecuencia:

- una actividad podía aparecer completada en Actividades;
- el backend podía registrar correctamente la finalización;
- Progreso podía continuar leyendo datos locales desactualizados.

Comparación histórica:

- `fetchRemoteActivities()` ya funcionaba de esta manera antes del refactor modular;
- `completeRemoteActivity()` tampoco persistía la respuesta en Room;
- los cambios Android de Semana 8 en `ActivityRepository` correspondían a instrumentación de rendimiento.

Clasificación:

- defecto funcional preexistente;
- no introducido por el refactor modular de Semana 8.

Corrección:

- se agregó sincronización Backend → Room;
- las respuestas remotas se convierten a `ActivityEntity`;
- Room mantiene una copia coherente de las actividades del usuario;
- Progreso continúa observando Room mediante `Flow`.

Validación:

- se creó una actividad;
- se completó desde Android;
- `PATCH /api/activities/{id}/complete` respondió 200;
- posteriormente `GET /api/activities` respondió 200;
- Progreso reflejó la actividad completada;
- la aplicación fue cerrada completamente y abierta nuevamente;
- el progreso permaneció correcto.

Resultado:

✅ CORREGIDO Y VALIDADO

## Validación técnica

Android:

- `:app:compileDebugKotlin` → BUILD SUCCESSFUL
- `:app:testDebugUnitTest` → BUILD SUCCESSFUL

Validación manual:

- RF-01 → satisfactoria
- RF-03 → satisfactoria
- persistencia después de reiniciar la aplicación → satisfactoria

## Conclusión

La regresión funcional posterior al refactor modular no encontró evidencia de que RF-01, RF-02 o RF-03 hayan sido introducidos por la modularización de Semana 8.

RF-01 y RF-03 correspondían a defectos funcionales preexistentes detectados durante la validación posterior y fueron corregidos.

RF-02 corresponde a una capacidad no implementada históricamente y queda registrada como mejora futura.

El estado resultante mantiene:

- backend modular integrado;
- compilación Android correcta;
- pruebas unitarias Android correctas;
- sincronización coherente entre actividades remotas y métricas locales;
- actualización automática de estados vencidos mientras la pantalla permanece visible.
