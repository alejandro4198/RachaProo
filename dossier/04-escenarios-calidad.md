# 04 - Escenarios de calidad y evidencia ejecutable

## 1. Propósito

Este documento consolida los escenarios de calidad medidos durante la Semana 4 y los relaciona con la evidencia ejecutable disponible.

No reemplaza los archivos de los experimentos ni duplica sus datos crudos.

---

## 2. EXP-001 - escenario histórico

**Clasificación: ANTECEDENTE HISTÓRICO + EVIDENCIA EXPERIMENTAL**

EXP-001 fue ejecutado el 18/08/2026 sobre una etapa anterior de RachaPro, principalmente local y basada en Room.

| Elemento | Definición |
|---|---|
| Atributo | Rendimiento |
| Estímulo | Carga del listado principal de actividades |
| Entorno | Aplicación Android ejecutada en dispositivo físico |
| Artefacto | `ActivitiesViewModel` y persistencia local |
| Inicio | Inicio de `loadData()` |
| Fin | Primera obtención de `ActivitiesUiState.Success` con 100 actividades |
| Volumen | 100 actividades |
| Medida | Tiempo en milisegundos |
| Criterio usado entonces | RNF02 histórico: listado <= 3 segundos |

### Método

Se utilizó `SystemClock.elapsedRealtime()` y Logcat.

Las condiciones comparables incluyeron:

- mismo commit instrumentado;
- misma semilla de 100 actividades;
- arranque `COLD`;
- `force-stop` antes de cada corrida;
- Logcat limpio;
- ausencia de cambios de código entre corridas comparables.

La corrida 1 fue descartada previamente como calentamiento.

Las corridas 2 a 26 conformaron 25 mediciones válidas.

### Resultado

| Métrica | Resultado |
|---|---:|
| Mediana | 1325 ms |
| P95 | 1621 ms |
| Mínimo | 1305 ms |
| Máximo | 1666 ms |

Bajo las condiciones de EXP-001, los resultados no respaldaron la hipótesis de superar el antecedente histórico de 3 segundos.

### Limitación

El final de la medición fue `ActivitiesUiState.Success`, no el renderizado visual completo.

Por tanto, la comparación con el RNF02 histórico debe interpretarse dentro de esa limitación.

### Evidencia

`experimentos/EXP-001-linea-base/`

---

## 3. EXP-002 - escenario actual

**Clasificación: DECISIÓN ACTUAL DEL EQUIPO + EVIDENCIA EXPERIMENTAL**

El 05/09/2026 el equipo definió como escenario actual de rendimiento la creación de una actividad.

El criterio actual establece que la actividad debe crearse correctamente en un máximo de 1 minuto.

| Elemento | Definición |
|---|---|
| Atributo | Rendimiento |
| Fuente del estímulo | Usuario autenticado |
| Estímulo | Solicitud válida de creación de actividad |
| Entorno | Spring Boot + PostgreSQL ejecutándose localmente |
| Artefacto | API REST `POST /api/activities` |
| Respuesta | Creación correcta y HTTP 201 |
| Medida | Duración HTTP de la creación |
| Criterio actual | <= 60.000 ms |

### Herramienta y configuración

- Herramienta: k6 2.2.0.
- VUs: 1.
- Iteraciones: 25.
- Creaciones por iteración: 1.
- Concurrencia: no evaluada en este experimento.

Esta configuración fue confirmada por el equipo.

### Preparación

Antes de iniciar las iteraciones, el script:

1. realiza login mediante `POST /api/auth/login`;
2. obtiene temporalmente el JWT;
3. consulta `GET /api/categories`;
4. selecciona una categoría activa perteneciente al usuario.

Las credenciales son suministradas mediante variables de entorno y no se almacenan en el repositorio.

### Métrica

La métrica principal es:

`activity_create_duration`

Esta métrica corresponde específicamente a `POST /api/activities`.

No se utiliza `http_req_duration` como medida principal porque incluye solicitudes de preparación y limpieza.

### Resultado

| Métrica | Resultado |
|---|---:|
| Creaciones exitosas | 25 / 25 |
| Tasa de éxito | 100 % |
| Promedio | 11.16 ms |
| Mediana | 8.48 ms |
| P90 | 10.58 ms |
| P95 | 31.50 ms |
| Mínimo | 6.84 ms |
| Máximo | 46.02 ms |

Los 52 checks registrados finalizaron correctamente y no se observaron fallos HTTP.

### Contraste escenario - datos

El criterio actual definido por el equipo es un máximo de 60.000 ms.

El máximo observado para la operación HTTP de creación durante EXP-002 fue 46.02 ms.

**Bajo las condiciones de EXP-002, el componente API evaluado permaneció por debajo del límite temporal definido por el equipo.**

### Alcance de la conclusión

EXP-002 mide el componente HTTP del backend.

No mide por sí solo:

- interacción del usuario en Android;
- procesamiento previo del cliente;
- una red externa;
- actualización posterior del estado de Android;
- renderizado final de la interfaz.

Por ello, esta medición no demuestra por sí sola el cumplimiento del flujo Android end-to-end completo.

### Evidencia

`experimentos/EXP-002-k6-api-activities/`

---

## 4. Condiciones e invalidación

EXP-002 documenta sus condiciones y criterios de invalidación en:

`experimentos/EXP-002-k6-api-activities/condiciones.md`

Entre las condiciones necesarias para mantener una corrida comparable están:

- backend y PostgreSQL disponibles;
- login exitoso;
- categoría válida;
- 1 VU;
- 25 iteraciones;
- mismo endpoint;
- mismo tipo de payload;
- código sin modificaciones durante la ejecución.

Una respuesta funcionalmente incorrecta no se descarta automáticamente: debe conservarse y analizarse para determinar si representa un fallo del sistema o una condición que invalida la corrida.

---

## 5. Reproducción

El procedimiento completo para repetir EXP-002 está documentado en:

`experimentos/EXP-002-k6-api-activities/README.md`

Incluye:

- prerrequisitos;
- comprobación de `/actuator/health`;
- variables de entorno;
- comando de ejecución de k6;
- ubicación de datos crudos;
- métrica que debe interpretarse.

---

## 6. Evidencia adicional

Existe además:

`docs/performance/prueba-carga-500k.md`

Esta evidencia pertenece a la arquitectura Spring Boot + PostgreSQL y evaluó un volumen de 500 usuarios sintéticos con 1000 actividades por usuario, para un total de 500.000 actividades.

También se realizaron pruebas de concurrencia en diferentes niveles mediante PowerShell/runspaces.

Esta prueba se conserva como evidencia adicional y no se presenta como comparación directa con EXP-001 ni como sustituto de EXP-002.

---

## 7. Relación atributo - decisión

La relación entre atributos de calidad y decisiones arquitectónicas está documentada en:

`dossier/03-atributos-calidad.md`

Para rendimiento, el equipo confirmó que la necesidad de manejar mayores volúmenes de información y usuarios manteniendo tiempos adecuados influyó en la evolución hacia:

Android -> Spring Boot -> PostgreSQL

Los experimentos aportan evidencia del comportamiento observado, pero no deben presentarse como si hubieran originado retroactivamente esa decisión.

---

## 8. Cronología

EXP-001 y EXP-002 pertenecen a etapas arquitectónicas diferentes:

- EXP-001: etapa principalmente local/Room.
- EXP-002: backend Spring Boot + PostgreSQL.

Sus resultados se conservan separados y se interpretan únicamente dentro de las condiciones documentadas para cada experimento.
