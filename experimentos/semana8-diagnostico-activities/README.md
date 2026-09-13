# Diagnóstico Semana 8 — GET /api/activities bajo carga

## 1. Objetivo

Este diagnóstico se realizó después de EXP-004 para investigar dónde se concentra la latencia observada en `GET /api/activities` bajo alta concurrencia.

No constituye un nuevo benchmark formal ni reemplaza EXP-004.

Su finalidad fue observar el comportamiento AS-IS antes de proponer o implementar optimizaciones.

## 2. Alcance

Ruta investigada:

```text
k6
  -> GET /api/activities
  -> ActivityController
  -> ActivityService.findAllByUserId()
  -> ActivityRepository / JPA
  -> HikariCP
  -> PostgreSQL
  -> respuesta HTTP
```

No se modificó código productivo ni configuración de rendimiento durante este diagnóstico.

## 3. Evidencias recopiladas

Se recopilaron las siguientes evidencias:

- thread dump del backend sin carga;
- thread dumps durante carga;
- observación de conexiones PostgreSQL;
- observación de estados y wait events de PostgreSQL;
- configuración AS-IS de Spring/JPA;
- estructura del endpoint `GET /api/activities`;
- volumen de actividades por usuario;
- índices existentes en PostgreSQL;
- plan de ejecución real mediante `EXPLAIN (ANALYZE, BUFFERS)`.

## 4. Comportamiento del backend bajo carga

Durante alta concurrencia se observaron aproximadamente 200 threads HTTP.

Una cantidad importante de ellos se encontraba esperando en:

```text
com.zaxxer.hikari.util.ConcurrentBag.borrow
com.zaxxer.hikari.HikariPool.getConnection
com.zaxxer.hikari.HikariDataSource.getConnection
```

Esto demuestra contención en la obtención de conexiones desde HikariCP.

## 5. Pool de conexiones observado

Durante las mediciones PostgreSQL se observaron 10 conexiones utilizadas por la aplicación.

PostgreSQL tiene configurado:

```text
max_connections = 100
```

Por lo tanto, el límite global de PostgreSQL no fue alcanzado durante el diagnóstico.

La contención observada ocurre antes de ese límite, en el pool de conexiones utilizado por la aplicación.

## 6. Estado de las conexiones PostgreSQL

Durante la carga se observaron repetidamente conexiones en estado:

```text
idle in transaction
```

con:

```text
wait_event_type = Client
wait_event = ClientRead
```

En algunas muestras hasta 9 de las 10 conexiones observadas estaban en estado `idle in transaction`.

Esto indica que PostgreSQL mantenía transacciones abiertas mientras esperaba nuevas acciones del cliente, en lugar de encontrarse ejecutando continuamente consultas SQL.

También se observaron pocas conexiones `active` en comparación con las conexiones abiertas.

## 7. Alcance transaccional del endpoint

El endpoint utiliza:

```kotlin
@Transactional(readOnly = true)
fun findAllByUserId(userId: Long): List<ActivityResponse> {
    return activityRepository
        .findAllByUserIdAndIsDeletedFalseOrderByDueDateEpochDayAsc(userId)
        .map { it.toResponse() }
}
```

Además:

```properties
spring.jpa.open-in-view=false
```

Por lo tanto, la transacción se encuentra delimitada principalmente por la ejecución del método de servicio y no por toda la serialización de la respuesta HTTP.

La materialización de entidades y su transformación a `ActivityResponse` ocurre dentro del método transaccional.

## 8. Volumen de datos

La base utilizada para el diagnóstico contiene:

```text
500060 actividades totales
500008 actividades activas
505 usuarios
```

Los usuarios sintéticos de la prueba contienen aproximadamente:

```text
1000 actividades activas por usuario
```

El repositorio devuelve:

```kotlin
List<ActivityEntity>
```

sin paginación.

Por lo tanto, una llamada típica de los usuarios sintéticos a:

```text
GET /api/activities
```

recupera aproximadamente 1000 registros antes de construir la respuesta HTTP.

## 9. Índices existentes

La tabla `activities` dispone, entre otros, de:

```text
idx_activities_user_id
idx_activities_due_date
idx_activities_status
idx_activities_user_deleted_due_date
```

El índice compuesto existente es:

```text
(user_id, is_deleted, due_date_epoch_day)
```

No se agregó ningún índice durante este diagnóstico.

## 10. Plan de ejecución PostgreSQL

Se ejecutó:

```sql
EXPLAIN (ANALYZE, BUFFERS)
SELECT *
FROM activities
WHERE user_id = 1024
  AND is_deleted = false
ORDER BY due_date_epoch_day ASC;
```

El usuario analizado contiene 1000 actividades activas.

Resultado principal:

```text
Bitmap Index Scan on idx_activities_user_id
Bitmap Heap Scan on activities
Sort Method: quicksort
Sort Memory: 157kB
rows = 1000
Planning Time = 0.383 ms
Execution Time = 2.118 ms
```

PostgreSQL eligió `idx_activities_user_id` y posteriormente ordenó las 1000 filas.

Aunque existe un índice compuesto compatible con filtro y ordenamiento, el optimizador no lo seleccionó para esta ejecución.

No se considera esto por sí mismo un defecto, ya que el tiempo total observado de ejecución fue aproximadamente 2.1 ms.

## 11. Hallazgos

### Confirmado

- `GET /api/activities` no utiliza paginación.
- Los usuarios sintéticos consultados poseen aproximadamente 1000 actividades activas.
- El endpoint materializa esas actividades como entidades JPA y posteriormente las transforma a DTO.
- Durante alta concurrencia muchos threads HTTP esperan una conexión de HikariCP.
- Durante las pruebas se observaron 10 conexiones de aplicación hacia PostgreSQL.
- PostgreSQL permite hasta 100 conexiones.
- Muchas conexiones aparecieron como `idle in transaction`.
- PostgreSQL frecuentemente esperaba al cliente mediante `ClientRead`.
- La consulta SQL analizada recuperó 1000 registros en aproximadamente 2.1 ms.
- `spring.jpa.open-in-view=false`.

### No demostrado

Este diagnóstico no demuestra de forma aislada que:

- PostgreSQL sea el cuello de botella;
- el tamaño del pool Hikari sea incorrecto;
- `@Transactional(readOnly = true)` sea un error;
- el mapeo Entity -> DTO sea por sí solo el cuello de botella;
- aumentar el número de conexiones solucione el problema;
- agregar otro índice mejore significativamente el endpoint.

## 12. Interpretación

La evidencia disponible no identifica la ejecución SQL observada como causa principal de la degradación.

La consulta representativa de 1000 actividades fue resuelta por PostgreSQL en aproximadamente 2.1 ms, mientras que durante la carga una gran cantidad de threads HTTP esperaba obtener conexiones desde HikariCP.

Al mismo tiempo, PostgreSQL mostraba numerosas conexiones `idle in transaction`, lo que indica que varias conexiones permanecían asociadas a transacciones abiertas mientras el servidor esperaba al cliente.

Por lo tanto, la contención observada se localiza principalmente en la interacción entre la capa de aplicación, el alcance transaccional y el pool de conexiones bajo alta concurrencia.

El volumen no paginado de aproximadamente 1000 actividades por solicitud es un factor relevante y constituye una hipótesis de optimización que debe validarse mediante un experimento controlado.

## 13. Conclusión

El diagnóstico permite descartar, para el escenario observado, una consulta SQL intrínsecamente lenta como explicación principal de la degradación de `GET /api/activities`.

El cuello observable se manifiesta en la espera por conexiones HikariCP bajo alta concurrencia.

Sin embargo, el diagnóstico no atribuye esa contención únicamente al tamaño del pool.

La evidencia muestra simultáneamente:

```text
alta concurrencia
  +
pool limitado observado
  +
transacciones abiertas
  +
respuestas no paginadas de aproximadamente 1000 registros
```

Antes de modificar el tamaño del pool o agregar índices, se recomienda realizar un experimento controlado que reduzca el volumen recuperado por solicitud y compare nuevamente latencia, throughput, uso de conexiones y estados PostgreSQL.

## 14. Próximo paso

El siguiente experimento deberá probar una optimización específica manteniendo constantes las demás condiciones.

La primera candidata es introducir paginación o limitar explícitamente el volumen retornado por `GET /api/activities`.

Luego se deberá repetir una carga comparable y contrastar sus resultados contra EXP-004 y este diagnóstico.

No se deben presentar mejoras como efectivas hasta contar con evidencia experimental.
