# Condiciones de ejecución - EXP-002

## 1. Identificación

- Fecha: 05/09/2026
- Herramienta: k6 2.2.0
- Arquitectura: Spring Boot + PostgreSQL
- Endpoint medido: `POST /api/activities`
- Base URL: `http://localhost:8080`

## 2. Estado previo

Antes de ejecutar la medición se verificó:

- backend accesible;
- PostgreSQL accesible desde el backend;
- `/actuator/health` con estado `UP`;
- componente `db` con estado `UP`;
- usuario de prueba válido;
- al menos una categoría activa perteneciente al usuario.

## 3. Configuración de carga

- VUs: 1
- Iteraciones: 25
- Solicitudes de creación por iteración: 1
- Concurrencia: no evaluada

## 4. Autenticación

El script obtiene un JWT mediante:

`POST /api/auth/login`

El token se mantiene solamente durante la ejecución.

Las credenciales son suministradas mediante:

- `TEST_EMAIL`
- `TEST_PASSWORD`

No se almacenan en el script ni en los resultados versionados.

## 5. Categoría

El identificador de categoría no se fija manualmente.

El script consulta:

`GET /api/categories`

y utiliza una categoría activa perteneciente al usuario autenticado.

## 6. Métrica principal

La métrica principal es:

`activity_create_duration`

Corresponde exclusivamente a la duración HTTP de las solicitudes:

`POST /api/activities`

## 7. Criterios de validez

Una ejecución se considera válida cuando:

- se utiliza el script documentado para EXP-002;
- el backend se encuentra disponible;
- PostgreSQL se encuentra disponible;
- el login devuelve HTTP 200;
- existe al menos una categoría activa;
- se ejecutan las 25 iteraciones previstas;
- cada creación devuelve HTTP 201;
- cada respuesta de creación contiene un identificador;
- no se modifica el código durante la ejecución;
- la configuración permanece en 1 VU y 25 iteraciones.

## 8. Criterios de invalidación

La ejecución se considera inválida para esta línea base si:

- el backend o PostgreSQL no estaban disponibles al comenzar;
- falla el login;
- no existe una categoría válida para el usuario;
- alguna iteración queda interrumpida;
- el número de iteraciones es diferente de 25;
- se modifica el número de VUs;
- se modifica el código durante la ejecución;
- se cambia el endpoint medido;
- se modifica el payload de manera que deje de representar el mismo escenario;
- existen fallos de preparación que impiden ejecutar las 25 solicitudes comparables.

Una respuesta HTTP diferente de 201 durante una creación debe conservarse como resultado del experimento y no descartarse automáticamente. Debe analizarse antes de decidir si corresponde a un fallo del sistema o a una condición que invalida la corrida.

## 9. Limitaciones

- Se utilizó un único proceso de k6.
- Cliente, backend y PostgreSQL se ejecutaron en el mismo entorno local.
- Se utilizó un único usuario.
- No se evaluó concurrencia.
- No se evaluaron múltiples dispositivos ni redes.
- No se midió el renderizado de Android.
- No se midió el flujo end-to-end completo.
- El borrado posterior de las actividades es lógico y no elimina físicamente las filas de PostgreSQL.
