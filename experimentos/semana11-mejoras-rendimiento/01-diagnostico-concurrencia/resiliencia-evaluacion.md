# Evaluación de Tácticas de Resiliencia en Cliente Android (S11)

## 1. Contexto y Principios de Evaluación

Se evalúan de forma metodológica e independiente 5 tácticas de resiliencia en el cliente Android Retrofit para mitigar los efectos de la indisponibilidad temporal del backend (escenario F-01) y la degradación bajo concurrencia.

---

## 2. Táctica 1: Retry con Exponential Backoff y Jitter

- **Análisis de Idempotencia en `POST /api/activities`**:
  - La operación `POST /api/activities` genera un nuevo registro en PostgreSQL con ID autoincremental por cada llamada recibida en el backend.
  - El cliente Android actual no envía una clave de idempotencia (`Idempotency-Key` UUID) en las cabeceras HTTP.
  - Si un reintento automático ocurre tras un timeout de red donde el backend sí procesó la solicitud original, se crearía una **actividad duplicada en la base de datos**.

- **Conclusión de Seguridad**:
  `RETRY AUTOMÁTICO EN POST: NO SEGURO SIN CONTROL ADICIONAL` (Solo seguro para lecturas idempotentes `GET /api/activities`).

---

## 3. Táctica 2: Circuit Breaker

- **Propósito**: Detener el envío de peticiones hacia el backend cuando detecta una tasa de fallos consecutiva (p. ej., 5 errores de conexión seguidos), entrando en estado `OPEN`.
- **Métricas Evaluadas**:
  - **Tiempo hasta fallo**: Inmediato ($< 1\text{ ms}$) mientras el circuito esté `OPEN`, evitando esperar el `connectTimeout = 3s` de Retrofit.
  - **Solicitudes evitadas**: Se evitan $100\%$ de las llamadas de red inútiles durante la indisponibilidad (escenario F-01).
  - **Recuperación**: Pasa a estado `HALF-OPEN` tras un tiempo de enfriamiento (p. ej., 15s) para probar la salud del backend.
- **Conclusión**: Táctica de resiliencia altamente efectiva para evitar la saturación del cliente durante cortes del servidor.

---

## 4. Táctica 3: Bulkhead (Aislamiento de Recursos)

- **Propósito**: Limitar el número de peticiones concurrentes salientes en el cliente Android (p. ej., máximo 5 llamadas activas en el pool de OkHttp).
- **Evaluación**: El cliente Android OkHttp ya limita por defecto las conexiones concurrentes por host (`maxRequestsPerHost = 5`).
- **Conclusión**: El aislamiento ya está garantizado por la pila OkHttp; no requiere complejidad adicional.

---

## 5. Táctica 4: Fallback con Persistencia Local (Room)

- **Propósito**: Mantener la operatividad de la UI mediante lectura del almacenamiento local (Room / DataStore) cuando el backend no responde.
- **Garantía de Consistencia**:
  - En lecturas (`GET`): Se presenta el último estado conocido guardado en la base de datos local Room.
  - En escrituras (`POST`): No se simula éxito remoto ficticio; se marca la actividad como `PENDING_SYNC` en Room para sincronización posterior cuando se restaure la conectividad.
- **Conclusión**: Táctica recomendada para preservar la consistencia y la experiencia de usuario sin inventar datos.
