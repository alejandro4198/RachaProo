# Spike 2 - Resiliencia de la integración por eventos

## 1. Propósito

Evaluar experimentalmente qué ocurre cuando el consumidor de `UserRegisteredV1` falla después de que la transacción que registra al usuario ya fue confirmada.

El objetivo específico es observar si la implementación actual ofrece recuperación automática mediante retry o replay del evento.

## 2. Contexto arquitectónico

La integración evaluada corresponde a:

```text
Identity / UserService
        |
        | publica
        v
UserRegisteredV1
        |
        | @TransactionalEventListener(AFTER_COMMIT)
        | @Async
        v
Activities / UserRegisteredV1Listener
        |
        v
DefaultCategoryProvisioning
```

El contrato del evento implementado es:

```kotlin
data class UserRegisteredV1(
    val userId: Long,
    val occurredAt: Instant
)
```

`UserService` publica el evento dentro del flujo transaccional de registro. El listener de Activities se ejecuta después del commit y delega el aprovisionamiento en `DefaultCategoryProvisioning`.

## 3. Hipótesis

Si el consumidor asíncrono falla después del commit del usuario y el evento no cuenta con persistencia durable ni mecanismo explícito de retry/replay, el usuario puede permanecer persistido mientras el efecto secundario esperado en Activities queda incompleto.

## 4. Condiciones del experimento

- Rama: `extra/spike2-resiliencia-asyncapi`.
- Backend Spring Boot local.
- PostgreSQL ejecutándose en Docker.
- Evento evaluado: `UserRegisteredV1`.
- Consumidor: `UserRegisteredV1Listener`.
- Efecto observado: creación de categorías por defecto.
- Usuario experimental: `1512`.
- Fallo inducido únicamente sobre la inserción de categorías del usuario experimental.
- El fallo fue retirado después de observar el comportamiento del consumidor.
- Ventana de observación posterior a retirar el fallo: 5 segundos.
- Segunda observación después de reiniciar el backend: 5 segundos.

## 5. Procedimiento

### 5.1 Estado inicial

Se registró un usuario mediante `POST /api/users`.

La solicitud respondió HTTP 201 y el usuario quedó persistido.

### 5.2 Fallo controlado

Se instaló temporalmente un trigger en PostgreSQL para rechazar las inserciones en `categories` correspondientes únicamente a usuarios creados durante el experimento.

El objetivo fue provocar el fallo del consumidor sin revertir la transacción de creación del usuario.

### 5.3 Observación después del fallo

Después de ejecutar el listener:

- el usuario seguía persistido;
- el usuario experimental tenía 0 categorías.

El trigger temporal fue posteriormente eliminado.

### 5.4 Observación después de retirar el fallo

Se esperaron 5 segundos con PostgreSQL nuevamente disponible.

Resultado:

- usuario persistido: sí;
- categorías: 0;
- retry automático observado: no.

### 5.5 Reinicio del backend

El backend fue detenido completamente y posteriormente iniciado nuevamente desde el proyecto Gradle ubicado en `backend`.

Después de confirmar que el puerto 8080 estaba nuevamente disponible se esperaron 5 segundos adicionales.

Resultado:

- usuario persistido: sí;
- categorías: 0;
- replay del evento observado: no.

## 6. Resultados

| Observación | Resultado |
| --- | --- |
| Registro HTTP | 201 |
| Usuario experimental | 1512 |
| Usuario persistido después del fallo | Sí |
| Categorías después del fallo | 0 |
| Categorías 5 s después de retirar el fallo | 0 |
| Retry automático observado | No |
| Usuario persistido después del reinicio | Sí |
| Categorías 5 s después del reinicio | 0 |
| Replay después del reinicio observado | No |

## 7. Resultado experimental

En las condiciones probadas, el fallo del consumidor ocurrió después del commit del usuario. La persistencia del usuario no fue revertida y el efecto secundario esperado en Activities no se completó.

No se observó retry automático durante los 5 segundos posteriores a retirar el fallo y tampoco se observó replay de `UserRegisteredV1` durante los 5 segundos posteriores al reinicio del backend.

## 8. Interpretación

La evidencia respalda el riesgo arquitectónico de que un evento interno procesado de forma asíncrona después del commit pueda dejar al sistema en un estado parcialmente actualizado cuando el consumidor falla.

La implementación evaluada no demostró un mecanismo durable de recuperación del evento fallido en las ventanas observadas.

## 9. Limitaciones

Este experimento no demuestra que el evento sea irrecuperable bajo cualquier condición.

Tampoco evalúa:

- brokers externos;
- transactional outbox;
- colas persistentes;
- dead-letter queues;
- políticas explícitas de retry;
- recuperación manual;
- múltiples instancias del backend;
- comportamiento distribuido.

Las conclusiones se limitan a la implementación y condiciones ejecutadas.

## 10. Consecuencia arquitectónica

`UserRegisteredV1` sigue siendo útil para desacoplar Identity de Activities, pero la asincronía introduce una ventana de consistencia eventual y un riesgo de pérdida del efecto secundario cuando el consumidor falla.

Si el efecto asociado al evento se vuelve crítico, deberá evaluarse un mecanismo durable como transactional outbox, mensajería persistente o una estrategia explícita de retry/reprocesamiento.

## 11. Relación con ADR-003

El resultado no invalida ADR-003.

Complementa la decisión mostrando experimentalmente uno de sus trade-offs: desacoplar mediante eventos internos reduce dependencia directa entre módulos, pero la implementación actual no aporta por sí sola entrega durable ni recuperación automática ante fallos del consumidor.

## 12. Estado

**PRUEBA EJECUTADA:** sí.

**RESULTADO EXPERIMENTAL:** no se observó retry ni replay automático en las ventanas probadas.

**DECISIÓN:** conservar la integración experimental documentada y registrar explícitamente su limitación de resiliencia.

**EVIDENCIA FALTANTE PARA UNA EVOLUCIÓN FUTURA:** comparar esta implementación con una alternativa durable antes de adoptar mecanismos adicionales.
