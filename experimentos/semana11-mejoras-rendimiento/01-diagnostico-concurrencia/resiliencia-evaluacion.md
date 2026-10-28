# Evaluación de Tácticas de Resiliencia en Cliente Android (S11)

## 1. Contexto y Principios de Evaluación

Se evalúan de forma conceptual y metodológica 4 tácticas de resiliencia en el cliente Android Retrofit para mitigar los efectos de la indisponibilidad temporal del backend (escenario F-01) y la degradación bajo concurrencia.

---

## 2. Táctica 1: Retry con Exponential Backoff y Jitter

- **Análisis de Idempotencia en `POST /api/activities`**:
  - La operación `POST /api/activities` genera un nuevo registro en PostgreSQL con ID autoincremental por cada llamada recibida en el backend.
  - El cliente Android actual no envía una clave de idempotencia (`Idempotency-Key` UUID) en las cabeceras HTTP.
  - Si un reintento automático ocurre tras un timeout de red donde el backend sí procesó la solicitud original, se crearía una **actividad duplicada en la base de datos**.

- **Conclusión de Seguridad**:
  `RETRY AUTOMÁTICO EN POST: NO SEGURO SIN CONTROL DE IDEMPOTENCIA` (Solo seguro para lecturas idempotentes `GET /api/activities`).

---

## 3. Táctica 2: Circuit Breaker

- **Propósito Propuesto**: Detener el envío de peticiones hacia el backend cuando detecte fallos consecutivos de red, pasando a estado `OPEN`.
- **Evaluación Conceptual**:
  - **Comportamiento `OPEN`**: Fallo inmediato sin agotar el `connectTimeout = 3s` de Retrofit.
  - **Solicitudes evitadas**: Evita desperdiciar recursos del cliente durante la indisponibilidad (escenario F-01).
- **Conclusión**: `PROPUESTO` como mecanismo de protección en el cliente.

---

## 4. Táctica 3: Bulkhead (Aislamiento de Recursos)

- **Propósito**: Limitar el número de peticiones concurrentes salientes en OkHttp.
- **Evaluación**: OkHttp limita por defecto la concurrencia a nivel de host.
- **Conclusión**: `NO JUSTIFICADO ACTUALMENTE` como desarrollo adicional por existir manejo nativo en la pila OkHttp.

---

## 5. Táctica 4: Fallback en Cliente

- **Garantía de Consistencia**:
  - **Lecturas (`GET`)**: Muestra del último estado guardado en Room (`IMPLEMENTADO` en la app Android).
  - **Escrituras (`POST`)**: No se simula éxito remoto falso. La sincronización local diferida mediante cola en Room se clasifica como `PROPUESTO` hasta su implementación y prueba formal.
- **Conclusión**: Se mantiene la UI informando al usuario del fallo sin alterar la consistencia remota.
