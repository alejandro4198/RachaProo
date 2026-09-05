# EXP-002 - Línea base k6 para creación de actividades

## 1. Identificación

- Sistema: RachaPro
- Arquitectura evaluada: Spring Boot + PostgreSQL
- Herramienta: k6
- Fecha de ejecución: 05/09/2026
- Operación evaluada: `POST /api/activities`
- Estado: ejecutado

EXP-002 corresponde a una etapa arquitectónica posterior a EXP-001.

EXP-001 evaluó la carga del listado de actividades en la aplicación Android sobre una etapa principalmente local/Room.

EXP-002 evalúa el tiempo HTTP del backend actual al crear actividades utilizando Spring Boot y PostgreSQL.

Los resultados de ambos experimentos no deben presentarse como una comparación directa.

## 2. Escenario evaluado

Atributo:

Rendimiento.

Operación:

Creación de una actividad.

Fuente del estímulo:

Usuario autenticado.

Estímulo:

Solicitud válida para crear una actividad.

Artefacto:

API REST del backend RachaPro, endpoint `POST /api/activities`.

Entorno:

Backend Spring Boot y PostgreSQL ejecutándose localmente.

Respuesta:

El backend crea la actividad y devuelve HTTP 201 junto con el identificador de la actividad creada.

Medida:

Duración HTTP de cada solicitud `POST /api/activities`, expresada en milisegundos.

## 3. Configuración decidida por el equipo

- VUs: 1
- Iteraciones: 25
- Creaciones por iteración: 1
- Concurrencia evaluada: ninguna

La configuración fue decidida para observar creaciones consecutivas realizadas por un único usuario sin introducir todavía un escenario independiente de concurrencia.

## 4. Preparación

El `setup()` del script realiza:

1. Login mediante `POST /api/auth/login`.
2. Obtención del JWT.
3. Consulta de categorías mediante `GET /api/categories`.
4. Selección de una categoría activa perteneciente al usuario autenticado.

Las credenciales se proporcionan mediante variables de entorno y no se almacenan en el repositorio.

## 5. Medición

Cada una de las 25 iteraciones realiza una solicitud:

`POST /api/activities`

El script registra específicamente la duración mediante:

`activity_create_duration`

También registra la tasa:

`activity_create_success`

La métrica global `http_req_duration` no se utiliza como medida principal del escenario porque incluye solicitudes realizadas durante preparación y limpieza.

## 6. Limpieza

Después de las iteraciones, el `teardown()` identifica las actividades creadas por EXP-002 y ejecuta `DELETE /api/activities/{id}`.

En la implementación actual este endpoint realiza borrado lógico.

## 7. Resultado

Se completaron correctamente las 25 iteraciones.

- Creaciones exitosas: 25 de 25
- Tasa de éxito: 100 %
- Promedio: 11.16 ms
- Mediana: 8.48 ms
- P90: 10.58 ms
- P95: 31.50 ms
- Mínimo: 6.84 ms
- Máximo: 46.02 ms

No se observaron fallos HTTP durante la ejecución.

## 8. Contraste con el escenario actual

El criterio actual definido por el equipo para rendimiento es:

Crear correctamente una actividad en un tiempo máximo de 1 minuto.

El mayor tiempo HTTP observado para `POST /api/activities` durante EXP-002 fue 46.02 ms.

Bajo las condiciones de este experimento, el componente API evaluado se mantuvo por debajo del límite temporal actual.

EXP-002 no mide el flujo completo de Android desde la interacción del usuario hasta el renderizado final de la interfaz, por lo que no debe utilizarse por sí solo para afirmar que el escenario end-to-end completo quedó validado.

## 9. Evidencia

- Script: `scripts/baseline-activities.js`
- Salida de consola: `logs/exp-002-console.txt`
- Versión de k6: `logs/k6-version.txt`
- Datos crudos: `resultados/raw-post-activities.json`
- Síntesis: `resultados/resultado-linea-base.md`
- Condiciones y criterios de invalidación: `condiciones.md`

## 10. Pruebas exploratorias anteriores

Antes de la medición oficial se realizaron ejecuciones exploratorias sobre `GET /api/activities`.

Sus logs se conservan únicamente por trazabilidad en:

`logs/exploratorio-get/`

No forman parte de los resultados oficiales de EXP-002.

## 11. Reproducción

### Prerrequisitos

Antes de ejecutar EXP-002 deben estar disponibles:

- PostgreSQL;
- backend Spring Boot;
- usuario de prueba válido;
- al menos una categoría activa para dicho usuario;
- k6 instalado.

La disponibilidad del backend puede comprobarse mediante:

```powershell
Invoke-RestMethod "http://localhost:8080/actuator/health"
```

La respuesta debe indicar estado `UP`.

### Variables de entorno

Las credenciales no se almacenan en el repositorio.

En PowerShell:

```powershell
$env:BASE_URL = "http://localhost:8080"
$env:TEST_EMAIL = "CORREO_DEL_USUARIO_DE_PRUEBA"
$env:TEST_PASSWORD = "PASSWORD_DEL_USUARIO_DE_PRUEBA"
```

### Ejecutar la medición

Desde la raíz del repositorio:

```powershell
k6 run `
    --out "json=.\experimentos\EXP-002-k6-api-activities\resultados\raw-post-activities.json" `
    ".\experimentos\EXP-002-k6-api-activities\scripts\baseline-activities.js" `
    2>&1 |
    Tee-Object `
        -FilePath ".\experimentos\EXP-002-k6-api-activities\logs\exp-002-console.txt"
```

La ejecución esperada corresponde a:

- 1 VU;
- 25 iteraciones;
- una creación de actividad por iteración.

Para considerar comparable una nueva ejecución deben conservarse las condiciones y criterios indicados en `condiciones.md`.

### Resultado que debe observarse

La métrica principal es:

`activity_create_duration`

También debe revisarse:

`activity_create_success`

Las métricas generales de k6, como `http_req_duration`, incluyen llamadas de preparación y limpieza y no representan exclusivamente la creación de actividades.
