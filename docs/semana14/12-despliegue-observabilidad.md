# Módulo 7 — Semana 14: Despliegue Conceptual, Observabilidad y Resiliencia RachaPro

## 1. Objetivo

Definir la propuesta conceptual de arquitectura de despliegue, el esquema de comparativa de costos relativos entre alternativas, la propuesta de plan de observabilidad (métricas, logs, trazas y alertas) vinculado a los escenarios de fallo del Módulo 6 (especialmente F-01), y la verificación de fitness functions en CI/CD para RachaPro.

---

## 2. Estado Actual

RachaPro se encuentra consolidado como un **Monolito Modular** en Spring Boot (Java 25 / Kotlin 2.1) con 5 capacidades funcionales bien delimitadas (`identity`, `activities`, `focus`, `progress`, `reminders`) y un soporte transversal `shared`.
- **Backend**: Compilado en un único ejecutable JAR/Contenedor Docker.
- **Persistencia**: Base de datos PostgreSQL única con schemas y tablas mapeados vía Spring Data JPA.
- **Aislamiento**: Verificado mecánicamente en CI mediante ArchUnit (`ModularArchitectureTest.kt` y `.github/workflows/backend-architecture.yml`).
- **Comité 2**: `VEREDICTO DEL COMITÉ 2: PENDIENTE POR APLAZAMIENTO` (Manteniendo la arquitectura Monolito Modular como línea base operativa).

---

## 3. Alternativas de Despliegue Comparadas (Propuesta Conceptual)

Se analizan dos alternativas conceptuales de despliegue para evaluación futura:

### Alternativa A: Monolito Modular Único (Línea Base Actual)
Despliegue de un único artefacto ejecutable Spring Boot en un contenedor Docker/Pod sobre infraestructura gestionada, conectado a PostgreSQL.

### Alternativa B: Monolito Modular Multi-Instancia con Balanceador (Propuesta Técnica)
Propuesta de despliegue de $N$ instancias del Monolito Modular detrás de un Load Balancer (NGINX / Cloud Load Balancer), compartiendo una base de datos PostgreSQL gestionada con pool de conexiones ajustado.

---

## 4. Diagrama Conceptual de Despliegue (Propuesta)

```text
[ Cliente Android ] ──(HTTP/REST)──► [ Load Balancer / NGINX (Propuesto) ]
                                           │
                        ┌──────────────────┴──────────────────┐
                        ▼                                     ▼
           [ Monolito Instancia 1 ]               [ Monolito Instancia 2 ]
           (Spring Boot App JAR)                 (Spring Boot App JAR)
           ┌──────────────────────┐              ┌──────────────────────┐
           │ - identity           │              │ - identity           │
           │ - activities         │              │ - activities         │
           │ - focus              │              │ - focus              │
           │ - progress           │              │ - progress           │
           │ - reminders          │              │ - reminders          │
           └──────────┬───────────┘              └──────────┬───────────┘
                      │                                     │
                      └──────────────────┬──────────────────┘
                                         ▼
                             [ PostgreSQL Managed DB ]
                             (Tables: users, activities,
                              pomodoro_sessions, etc.)
```

---

## 5. Comparación de Costos Relativos y Dimensiones

| Dimensión | Alternativa A (Monolito Único) | Alternativa B (Multi-Instancia Propuesta) | Justificación / Evidencia |
| :--- | :--- | :--- | :--- |
| **Unidades desplegables** | 1 ejecutable + DB | $N$ ejecutables + Load Balancer + DB | Alt A minimiza la complejidad de empaquetado. |
| **Infraestructura** | 1 VM / Contenedor | $N$ Contenedores + Balanceador | Alt B requiere balanceador adicional. |
| **Complejidad operativa** | Baja | Media | Alt A simplifica despliegue y monitoreo. |
| **Seguridad** | Superficie reducida a 1 host | Requiere seguridad inter-nodo y en balanceador | Mismos controles de Spring Security. |
| **Observabilidad** | Logs y métricas en 1 sola instancia | Requiere centralización de logs y trazas distribuidas | Alt B exigiría agregador de logs. |
| **Operación y Mantenimiento**| Despliegues sencillos | Permitiría despliegues Rolling Updates | Alt B mejoraría disponibilidad operacional. |
| **Tolerancia a fallos** | Punto único de fallo en la app | Resiliente ante caída de 1 nodo backend | Alt B soportaría fallo F-01 en 1 instancia. |
| **Persistencia** | PostgreSQL compartido | PostgreSQL compartido | Misma capa de persistencia en ambas. |
| **Escalabilidad** | Escalado vertical (CPU/RAM) | Escalado horizontal (más réplicas) | Alt B soportaría picos de tráfico. |
| **Reversibilidad** | Inmediata | Inmediata (ajuste de réplicas en balanceador) | Misma base de código Java/Kotlin. |
| **Costo inicial** | `NO ESTIMABLE CON LA EVIDENCIA ACTUAL` | `NO ESTIMABLE CON LA EVIDENCIA ACTUAL` | Sin cotización de proveedor cloud específico. |
| **Costo recurrente** | `NO ESTIMABLE CON LA EVIDENCIA ACTUAL` | `NO ESTIMABLE CON LA EVIDENCIA ACTUAL` | Sin cotización de proveedor cloud específico. |

---

## 6. Supuestos

1. La capa de persistencia (PostgreSQL) se mantiene como la única fuente autoritativa de datos.
2. Las instancias del backend son stateless (sesiones validadas exclusivamente vía firma JWT sin estado en memoria).
3. No se requiere migración a microservicios independientes, preservando las fronteras modulares mediante ArchUnit.

---

## 7. Plan de Observabilidad — Propuesta de Métricas

Propuesta de métricas para monitorear la salud y capacidad del backend:

| Métrica | Propósito / Qué mide | Fuente | Umbral / Criterio |
| :--- | :--- | :--- | :--- |
| `http.server.requests.latency` | Latencia P95/P99 de respuestas HTTP | Spring Boot Actuator / Micrometer | `UMBRAL PENDIENTE DE CALIBRACIÓN` |
| `http.server.requests.errors` | Tasa de errores HTTP 5xx y 4xx | Actuator Metrics | `UMBRAL PENDIENTE DE CALIBRACIÓN` |
| `jvm.memory.used` | Uso de memoria Heap/Non-Heap de JVM | JVM Micrometer | `UMBRAL PENDIENTE DE CALIBRACIÓN` |
| `hikaricp.connections.active` | Conexiones activas en pool DB | HikariCP Metrics | `UMBRAL PENDIENTE DE CALIBRACIÓN` |
| `process.cpu.usage` | Porcentaje de uso de CPU de Spring Boot | OS / Actuator | `UMBRAL PENDIENTE DE CALIBRACIÓN` |

---

## 8. Plan de Observabilidad — Propuesta de Logs Estructurados

- **Estructura**: JSON formateado mediante Logback.
- **Eventos Propuestos a Registrar**:
  1. `AUTH_SUCCESS`: Inicio de sesión correcto (registra `userId`, `timestamp`).
  2. `AUTH_FAILURE`: Intento fallido de autenticación (registra `email_hash`, `reason`).
  3. `ACCESS_DENIED`: Intento de acceso a recurso no autorizado (HTTP 403).
  4. `DATABASE_TIMEOUT`: Excepción de timeout de conexión a PostgreSQL.
  5. `UNHANDLED_EXCEPTION`: Error interno no capturado (HTTP 500).
- **Prohibición de Datos Sensibles**: **NUNCA** registrar contraseñas en texto plano, tokens JWT completos, secretos ni datos personales sensibles en las trazas.

---

## 9. Plan de Observabilidad — Propuesta de Trazas

Flujo de correlación propuesto desde la petición del cliente:

```text
Android Client (Retrofit)
   │  [Header propuesto: X-Correlation-ID / Trace-ID]
   ▼
Spring Boot Controller
   │  [MDC Context Logging propuesto]
   ▼
Service Layer (Identity / Activities / Focus)
   │  [Spring Transactional]
   ▼
Spring Data JPA Repository
   │  [HikariCP Connection]
   ▼
PostgreSQL Database
```

---

## 10. Propuesta de Alertas Accionables

| Señal de Alerta | Condición Propuesta de Disparo | Impacto en Negocio | Acción de Respuesta Esperada |
| :--- | :--- | :--- | :--- |
| `ALERT-F01-BACKEND-DOWN` | `/actuator/health` responde Non-200 (`UMBRAL PENDIENTE DE CALIBRACIÓN`) | Indisponibilidad de API para Android | Reiniciar contenedor / Failover de instancia backend. |
| `ALERT-DB-POOL-EXHAUSTED` | `hikaricp.connections.pending` elevado (`UMBRAL PENDIENTE DE CALIBRACIÓN`) | Latencia alta o HTTP 500 en creación de actividades | Revisar consultas lentas en PG / Ampliar max-pool. |
| `ALERT-HIGH-ERROR-RATE` | Tasa de errores HTTP 5xx elevada (`UMBRAL PENDIENTE DE CALIBRACIÓN`) | Degradación de experiencia de usuario | Inspeccionar logs de `UNHANDLED_EXCEPTION`. |
| `ALERT-AUTH-BRUTE-FORCE` | Eventos `AUTH_FAILURE` repetidos desde misma IP (`UMBRAL PENDIENTE DE CALIBRACIÓN`) | Riesgo de ataque de fuerza bruta sobre login | Bloqueo temporal de IP en firewall / Load Balancer. |

---

## 11. Relación con Escenarios de Fallo M6 (Especialmente F-01, A2 y A3)

- **Escenario F-01 (Indisponibilidad Temporal Backend)**:
  - **Evidencia M6**: Documentado en `matrices-fallos-resiliencia.md`. Ante la caída del backend, el cliente Android lanza `SocketTimeoutException` tras 3 segundos (`connectTimeout = 3s`).
  - **Propuesta de Detección**: Disparo de alerta al fallar el health check `/actuator/health`.
  - **Resiliencia Demostrada**: El cliente Android captura el timeout y notifica al usuario sin crash de la app (`A3-matriz-resiliencia.md`).

---

## 12. Operación y Mantenimiento (Propuestas de Diseño)

- **Health Check Path**: `GET /actuator/health` (público existente en `SecurityConfig.kt`).
- **Graceful Shutdown**: Propuesto como configuración de Spring Boot para completar peticiones en vuelo antes de apagar el contenedor.
- **Rollback Strategy**: Propuesta mediante imágenes de Docker etiquetadas por versión SemVer (`rachapro-backend:v1.x.x`).

---

## 13. Matriz de Riesgos Operativos

| ID | Riesgo Operativo | Componente | Causa Raíz | Consecuencia | Evidencia | Control Existente / Propuesto | Riesgo Residual | Estado |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **RO-01** | Indisponibilidad por caída del nodo backend (F-01) | Backend Spring Boot | Fallo de proceso Java / OOM / Reinicio host | App Android muestra error de conexión | `matrices-fallos-resiliencia.md` | `connectTimeout = 3s` en Android; propuesta de réplicas detrás de Load Balancer | **MITIGADO EN CLIENTE / ABIERTO EN SERVIDOR** | `OBSERVADO` |
| **RO-02** | Agotamiento de pool de conexiones PostgreSQL | HikariCP / DB | Carga alta en `POST /api/activities` sin paginación | Peticiones HTTP bloqueadas en timeout DB | `prueba-carga-500k.md` | Paginación implementada en `activities` | **CONTROLADO** | `POTENCIAL` |
| **RO-03** | Acoplamiento ilegal entre módulos por refactor | Módulos Backend | Import directo de paquete interno de otro módulo | Violación de fronteras de arquitectura | `ModularArchitectureTest.kt` | Fitness Function ArchUnit en CI (`backend-architecture.yml`) | **CONTROLADO** | `POTENCIAL` |
| **RO-04** | Aumento de latencia en red móvil Android | Cliente Retrofit | Latencia alta o pérdida de paquetes en red móvil | Demora en renderizado de actividades | `EXP-004-android-bajo-carga` | Timeouts cortos (3s) y reintentos manuales UI | **ACEPTADO** | `POTENCIAL` |

---

## 14. Reversibilidad

La propuesta conceptual de despliegue y observabilidad no introduce dependencias propietarias ni cambios destructivos en la base de datos.
- Desactivar las reglas de ArchUnit o alertas se realiza por configuración sin impacto en el código de producción.

---

## 15. Evidencia Concreta

- **Fitness Function en CI**: [`.github/workflows/backend-architecture.yml`](file:///C:/Users/Lenovo/AndroidStudioProjects/RachaPro/.github/workflows/backend-architecture.yml)
- **Validación PASS-FAIL-PASS**: [`experimentos/semana14-fitness-function/README.md`](file:///C:/Users/Lenovo/AndroidStudioProjects/RachaPro/experimentos/semana14-fitness-function/README.md)
- **Timeouts Android**: [`RetrofitClient.kt`](file:///C:/Users/Lenovo/AndroidStudioProjects/RachaPro/app/src/main/java/com/example/rachapro/network/RetrofitClient.kt)
- **Actuator Health Config**: [`SecurityConfig.kt`](file:///C:/Users/Lenovo/AndroidStudioProjects/RachaPro/backend/src/main/kotlin/com/example/rachapro/backend/identity/security/SecurityConfig.kt)

---

## 16. Limitaciones

- No se realizó un despliegue real en una nube comercial (AWS/GCP/Azure) para la medición directa de costos monetarios finales (`NO ESTIMABLE CON LA EVIDENCIA ACTUAL`).

---

## 17. Trazabilidad Git

- **Rama**: `modulo7/seguridad-operacion`
- **Commits M7 S14**:
  - `M7 S14: registrar evidencia pass-fail-pass de fitness function`
  - `M7 S14: documentar despliegue y observabilidad`

---

## 18. Estado del Entregable

`PROPUESTA DOCUMENTADA`
