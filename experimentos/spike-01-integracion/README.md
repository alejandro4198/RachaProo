# SPIKE-01 — Integración mediante UserRegisteredV1

## 1. Estado de la evidencia

- Fecha de ejecución: 2026-09-30
- Rama experimental: `exp/spike-01-integracion-eventos`
- Commit base: `b5f5dbd90a821cd9ca2a1a0092777549461da523`
- Backend: Spring Boot
- Persistencia: PostgreSQL
- Base local utilizada: `rachapro_db`
- Contenedor PostgreSQL: `rachapro-postgres`

## 2. Hipótesis

Evaluar si el flujo de registro puede desacoplar la dependencia directa de Identity hacia Activities mediante el evento interno `UserRegisteredV1`, manteniendo el comportamiento funcional esperado.

El criterio experimental establece que las categorías predeterminadas deben estar disponibles dentro de una ventana local máxima de 2000 ms.

## 3. Implementación experimental

El Spike 1 realiza los siguientes cambios:

1. Identity guarda el usuario.
2. `UserService` publica `UserRegisteredV1`.
3. El contrato `UserRegisteredV1` se ubica en `identity.api`.
4. Activities recibe el evento mediante `UserRegisteredV1Listener`.
5. El listener utiliza `@TransactionalEventListener` con fase `AFTER_COMMIT`.
6. El procesamiento se ejecuta mediante `@Async`.
7. Activities crea las categorías predeterminadas:
   - Personal
   - Trabajo
   - Universidad
8. Se habilita ejecución asíncrona en la aplicación Spring.

No se modificaron controladores ni rutas de la API HTTP pública.

No se realizaron cambios al modelo de datos ni migraciones de base de datos.

## 4. Ejecución funcional

Se realizaron cuatro ejecuciones del flujo de registro.

| Corrida | User ID | HTTP | HTTP ms | Evento ms | Categorías exactas | Evento <= 2000 ms | Resultado |
|---|---:|---:|---:|---:|---|---|---|
| 01 | 1508 | 201 | No comparable | 28 | PASS | PASS | PASS |
| 02 | 1509 | 201 | 884 | 65 | PASS | PASS | PASS |
| 03 | 1510 | 201 | 893 | 36 | PASS | PASS | PASS |
| 04 | 1511 | 201 | 875 | 32 | PASS | PASS | PASS |

La medición HTTP de la corrida 01 no se utiliza como dato comparable porque `Invoke-WebRequest` mostró una confirmación interactiva durante la ejecución.

La medición del evento de esa corrida sí fue obtenida del procesamiento registrado por `UserRegisteredV1Listener`.

## 5. Estadísticas del evento

Valores observados:

`28, 65, 36, 32 ms`

- Corridas: 4
- Mínimo: 28 ms
- Promedio: 40,25 ms
- Mediana: 34 ms
- Máximo: 65 ms
- Corridas dentro del umbral de 2000 ms: 4/4

Las tres corridas posteriores a la primera presentaron:

`65, 36, 32 ms`

- Promedio: 44,33 ms
- Mediana: 36 ms
- Máximo: 65 ms

## 6. Verificación funcional en PostgreSQL

Para los usuarios creados se verificó la existencia exacta de las categorías:

- Personal
- Trabajo
- Universidad

No se observaron categorías faltantes dentro de las ejecuciones verificadas.

## 7. Validación estructural

Después de las pruebas funcionales se ejecutaron las validaciones estructurales.

### ArchUnit

Resultado:

`PASS`

Gradle:

`BUILD SUCCESSFUL`

### Suite completa del backend

Resultado:

`PASS`

Gradle:

`BUILD SUCCESSFUL`

### Git

`git diff --check`:

`PASS`

## 8. Criterios de éxito

| Criterio | Resultado |
|---|---|
| El usuario puede registrarse correctamente | PASS |
| Identity elimina la llamada directa a `DefaultCategoryProvisioning` dentro del spike | PASS |
| Activities recibe `UserRegisteredV1` | PASS |
| Se crean las categorías predeterminadas | PASS |
| Categorías disponibles dentro de 2000 ms | PASS |
| API HTTP pública sin cambios de rutas | PASS |
| ArchUnit permanece en verde | PASS |
| Cambio reversible sin modificar el modelo de datos | PASS |
| Complejidad introducida identificable y documentable | PASS |

## 9. Observaciones

El experimento demuestra que, bajo las condiciones locales probadas, el evento interno permite ejecutar el aprovisionamiento de categorías después del commit del usuario sin superar el umbral experimental establecido.

La separación elimina del flujo experimental la llamada directa de Identity hacia `DefaultCategoryProvisioning`.

El mecanismo introduce procesamiento asíncrono y consistencia eventual limitada entre la creación del usuario y la disponibilidad de sus categorías.

## 10. Limitaciones

Los resultados corresponden a ejecución local.

El mecanismo probado utiliza eventos internos de Spring dentro del mismo backend.

No se utilizaron Kafka, RabbitMQ, Pub/Sub ni otro broker externo.

No se evaluó comunicación entre microservicios.

No se evaluó persistencia durable del evento.

No se realizó una prueba específica de fallo intencional del consumidor.

La corrida 01 no proporciona una medición HTTP comparable debido a la confirmación interactiva de PowerShell.

El cumplimiento del Spike 1 no demuestra que RachaPro necesite CQRS ni obliga a adoptar una arquitectura orientada a eventos de forma general.

## 11. Veredicto

**VALIDADA**

La hipótesis experimental queda validada dentro de las condiciones probadas.

`UserRegisteredV1` permitió desacoplar el flujo experimental entre Identity y Activities, conservar el registro del usuario, crear correctamente las categorías predeterminadas y cumplir el umbral local de 2000 ms en las cuatro ejecuciones observadas.

Este veredicto corresponde exclusivamente al Spike 1 y no implica por sí mismo la adopción de mensajería externa, microservicios, CQRS ni eventos para todas las relaciones entre módulos.



## Aclaración metodológica posterior — preregistro y tratamiento de corridas

Esta sección es posterior a la ejecución histórica de SPIKE-01 y no modifica
retroactivamente su hipótesis, datos crudos ni veredicto original.

### Trazabilidad del preregistro

La fuente preregistrada utilizada antes de implementar y medir SPIKE-01 quedó
versionada en la documentación de Semana 9.

Secuencia Git relevante:

`59fdb47`
→ preregistro / definición previa del experimento

`597e9c7`
→ implementación y resultados

`c6ae059`
→ ADR-003

Por tanto, el preregistro existía antes de la implementación, las mediciones y
la decisión arquitectónica posterior.

Esta referencia se incorpora aquí para que el paquete experimental sea
autosuficiente en términos de navegación, sin duplicar ni reescribir el
preregistro histórico.

### Tratamiento estadístico para el criterio de descarte de la primera corrida

Los datos crudos históricos se conservan sin modificación:

| Corrida | Evento |
|---|---:|
| 1 | 28 ms |
| 2 | 65 ms |
| 3 | 36 ms |
| 4 | 32 ms |

El resumen histórico de las cuatro corridas también permanece registrado por
trazabilidad.

Para la evaluación de M5 bajo el criterio que exige descartar la primera corrida
antes de calcular la estadística decisora, el conjunto considerado es:

`65, 36, 32 ms`

Resultado:

- corridas consideradas: 3;
- mediana: 36 ms;
- corridas dentro del umbral de 2000 ms: 3/3.

Esta aclaración no elimina la primera medición ni modifica el CSV original.

Distingue:

`datos crudos históricos`
→ cuatro corridas conservadas

de:

`estadística primaria bajo el criterio de descarte`
→ corridas 2–4
→ mediana 36 ms
→ 3/3 bajo 2000 ms

La aclaración tampoco convierte este resultado en evidencia de escalabilidad
general, tolerancia universal a fallos, CQRS, mensajería externa ni conveniencia
de transformar todas las integraciones en eventos.





## Aclaración posterior sobre trazabilidad de delegación de IA

Durante la verificación final de los entregables de M5 no se recuperó evidencia
suficiente para demostrar un **registro formal contemporáneo de delegación de
IA específico para SPIKE-01**.

Esta ausencia no modifica el preregistro, la implementación, las mediciones ni
el veredicto experimental. Tampoco se reconstruye retrospectivamente un prompt
o una delegación como si hubieran quedado registrados antes del experimento.

La limitación y la evidencia histórica revisada se documentan en:

- [`nota-trazabilidad-delegacion-ia.md`](./nota-trazabilidad-delegacion-ia.md)
