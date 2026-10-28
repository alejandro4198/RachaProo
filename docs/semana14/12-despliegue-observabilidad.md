# Módulo 7 — Semana 14: Despliegue Conceptual, Observabilidad y Resiliencia RachaPro

## 1. Objetivo

Definir la arquitectura conceptual de despliegue, el esquema de comparativa de costos relativos entre alternativas, el plan de observabilidad unificado (métricas, logs, trazas y alertas) integrado con los escenarios de fallo del Módulo 6 (especialmente F-01), y la automatización de fitness functions en CI/CD para RachaPro.

---

## 2. Estado Actual

RachaPro se encuentra consolidado como un **Monolito Modular** en Spring Boot (Java 25 / Kotlin 2.1) con 5 capacidades funcionales bien delimitadas (`identity`, `activities`, `focus`, `progress`, `reminders`) y un soporte transversal `shared`.
- **Backend**: Compilado en un único ejecutable JAR/Contenedor Docker.
- **Persistencia**: Base de datos PostgreSQL única con schemas y tablas mapeados vía Spring Data JPA.
- **Aislamiento**: Verificado mecánicamente en CI mediante ArchUnit (`ModularArchitectureTest.kt` y `.github/workflows/backend-architecture.yml`).
- **Comité 2**: `VEREDICTO DEL COMITÉ 2: PENDIENTE POR APLAZAMIENTO` (Manteniendo la arquitectura Monolito Modular como línea base operativa).

---

## 3. Alternativas de Despliegue Comparadas

Se analizan dos alternativas de despliegue para la fase operacional:

### Alternativa A: Monolito Modular Único (Línea Base Actual)
Despliegue de un único artefacto ejecutable Spring Boot en un contenedor Docker/Pod sobre infraestructura gestionada, conectado a un clúster de PostgreSQL de un solo nodo con réplica de lectura.

### Alternativa B: Monolito Modular Distribuido / Multi-Instancia con Balanceador
Despliegue de $N$ instancias idénticas del Monolito Modular (p. ej., 2 o 3 réplicas) detrás de un Load Balancer (NGINX / Cloud Load Balancer), compartiendo una base de datos PostgreSQL gestionada (p. ej. Cloud SQL / RDS) con pool de conexiones ajustado.

---

## 4. Diagrama Conceptual de Despliegue

```text
[ Cliente Android ] ──(HTTP/REST)──► [ Load Balancer / NGINX ]
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

| Dimensión | Alternativa A (Monolito Único) | Alternativa B (Monolito Multi-Instancia) | Justificación / Evidencia |
| :--- | :--- | :--- | :--- |
| **Unidades desplegables** | 1 ejecutable + DB | $N$ ejecutables + Load Balancer + DB | Alt A minimiza la complejidad de empaquetado. |
| **Infraestructura** | 1 VM / Contenedor pequeño | $N$ Contenedores + Balanceador de Carga | Alt B requiere balanceador adicional. |
| **Complejidad operativa** | Baja | Media | Alt A simplifica despliegue y monitoreo. |
| **Seguridad** | Superficie de ataque reducida (1 host) | Requiere seguridad inter-nodo y en balanceador | Mismos controles de Spring Security en ambas. |
| **Observabilidad** | Logs y métricas en 1 sola instancia | Requiere centralización de logs y trazas distribuidas | Alt B exige agregador de logs (Loki/ELK). |
| **Operación y Mantenimiento**| Despliegues sencillos | Permite despliegues Zero-Downtime (Rolling Updates)| Alt B mejora la disponibilidad operacional. |
| **Tolerancia a fallos** | Punto único de fallo en la app | Resiliente ante caída de 1 nodo backend | Alt B soporta fallo F-01 en una instancia. |
| **Persistencia** | PostgreSQL compartido | PostgreSQL compartido | Misma capa de persistencia en ambas. |
| **Escalabilidad** | Escalado vertical (CPU/RAM) | Escalado horizontal (más réplicas) | Alt B soporta picos de tráfico en `POST /api/activities`.|
| **Reversibilidad** | Inmediata | Inmediata (cambio de configuración de réplicas) | Misma base de código Java/Kotlin. |
| **Costo inicial** | Bajo (1x) | Medio ($\sim 2.2x$) | Basado en recursos de cómputo y balanceador. |
| **Costo recurrente** | `NO ESTIMABLE CON LA EVIDENCIA ACTUAL` | `NO ESTIMABLE CON LA EVIDENCIA ACTUAL` | Sin cotización de proveedor cloud específico. |

---

## 6. Supuestos

1. La capa de persistencia (PostgreSQL) se mantiene como la única fuente autoritativa de datos.
2. Todas las instancias del backend son totalmente stateless (las sesiones se validan únicamente vía firma JWT sin estado en memoria).
3. No se requiere migración a microservicios independientes, preservando las fronteras modulares mediante ArchUnit.

---

## 7. Plan de Observabilidad — Métricas

Se definen las métricas esenciales para monitorear la salud y capacidad del backend:

| Métrica | Propósito / Qué mide | Fuente | Umbral / Criterio |
| :--- | :--- | :--- | :--- |
| `http.server.requests.latency` | Latencia P95/P99 de las respuestas HTTP | Spring Boot Actuator / Micrometer | P95 $< 500\text{ ms}$ (`UMBRAL PENDIENTE DE CALIBRACIÓN`) |
| `http.server.requests.errors` | Tasa de errores HTTP 5xx y 4xx | Actuator Metrics | Error rate $5xx < 1\%$ |
| `jvm.memory.used` | Uso de memoria Heap/Non-Heap de la JVM Java 25 | JVM Micrometer | Usado $< 85\%$ del Max Heap |
| `hikaricp.connections.active` | Conexiones activas en el pool de base de datos | HikariCP Metrics | Active connections $< 80\%$ del max-pool |
| `process.cpu.usage` | Porcentaje de uso de CPU del proceso Spring Boot | OS / Actuator | CPU $< 80\%$ en carga sostenida |

---

## 8. Plan de Observabilidad — Logs Structurados

- **Estructura**: JSON formateado mediante Logback.
- **Eventos Obligatorios a Registrar**:
  1. `AUTH_SUCCESS`: Inicio de sesión correcto (registra `userId`, `timestamp`, `ip_origen`).
  2. `AUTH_FAILURE`: Intento fallido de autenticación (registra `email_hash`, `reason`, `ip_origen`).
  3. `ACCESS_DENIED`: Intento de acceso a recurso no autorizado (HTTP 403).
  4. `DATABASE_TIMEOUT`: Excepción de timeout de conexión a PostgreSQL.
  5. `UNHANDLED_EXCEPTION`: Error interno no capturado (HTTP 500).
- **Prohibición de Datos Sensibles**: **NUNCA** registrar contraseñas en texto plano, tokens JWT completos, secretos de firmas ni datos personales sensibles en las trazas de log.

---

## 9. Plan de Observabilidad — Trazas

Flujo de correlación propagado desde la petición del cliente:

```text
Android Client (Retrofit)
   │  [Header: X-Correlation-ID / Trace-ID]
   ▼
Spring Boot Controller
   │  [MDC Context Logging]
   ▼
Service Layer (Identity / Activities / Focus)
   │  [Spring Transactional]
   ▼
Spring Data JPA Repository
   │  [HikariCP Connection]
   ▼
PostgreSQL Database
```

*Nota*: `X-Correlation-ID` queda documentado como el estándar de trazabilidad E2E recomendado para la fase operacional.

---

## 10. Alertas Accionables

| Señal de Alerta | Condición de Disparo | Impacto en Negocio | Acción de Respuesta Esperada |
| :--- | :--- | :--- | :--- |
| `ALERT-F01-BACKEND-DOWN` | `/actuator/health` responde $Non-200$ por $> 30\text{ s}$ | Indisponibilidad total de la API para Android | Reiniciar contenedor / Failover de instancia backend. |
| `ALERT-DB-POOL-EXHAUSTED` | `hikaricp.connections.pending > 10` durante $1\text{ min}$ | Latencia alta o HTTP 500 en creación de actividades | Revisar consultas lentas en PG / Ampliar max-pool. |
| `ALERT-HIGH-ERROR-RATE` | Tasa de errores HTTP 5xx $> 5\%$ en $5\text{ min}$ | Degradación de experiencia de usuario | Inspeccionar logs de `UNHANDLED_EXCEPTION`. |
| `ALERT-AUTH-BRUTE-FORCE` | $> 20$ eventos `AUTH_FAILURE` desde misma IP en $1\text{ min}$ | Riesgo de ataque de fuerza bruta sobre login | Bloqueo temporal de IP en firewall / Load Balancer. |

---

## 11. Relación con Escenarios de Fallo M6 (Especialmente F-01, A2 y A3)

- **Escenario F-01 (Indisponibilidad Temporal Backend)**:
  - **Evidencia M6**: Documentado en `matrices-fallos-resiliencia.md`. Ante la caída del backend, el cliente Android lanza `SocketTimeoutException` tras 3 segundos (`connectTimeout = 3s`).
  - **Detección Observabilidad**: La alerta `ALERT-F01-BACKEND-DOWN` se dispara al fallar el health check.
  - **Resiliencia Demostrada**: El cliente Android captura el timeout y notifica al usuario sin crash de la app (`A3-matriz-resiliencia.md`).

---

## 12. Operación y Mantenimiento

- **Health Check Path**: `GET /actuator/health` (público).
- **Graceful Shutdown**: Habilitado en Spring Boot para permitir que las peticiones HTTP en vuelo completen antes de apagar el contenedor.
- **Rollback Strategy**: Despliegue atómico mediante imágenes de Docker etiquetadas por versión SemVer (`rachapro-backend:v1.x.x`). En caso de fallo, se revierte al tag anterior en el balanceador.

---

## 13. Matriz de Riesgos Operativos

| ID | Riesgo Operativo | Componente | Causa Raíz | Consecuencia | Evidencia | Control Existente / Mitigación | Riesgo Residual | Estado |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **RO-01** | Indisponibilidad por caída del nodo backend (F-01) | Backend Spring Boot | Fallo de proceso Java / OOM / Reinicio host | App Android muestra error de conexión | `matrices-fallos-resiliencia.md` | `connectTimeout = 3s` en Android + réplicas detras de Load Balancer | **MITIGADO** | `OBSERVADO` |
| **RO-02** | Agotamiento de pool de conexiones PostgreSQL | HikariCP / DB | Carga alta en `POST /api/activities` sin paginación | Peticiones HTTP bloqueadas en timeout DB | `prueba-carga-500k.md` | Paginación implementada en `activities` | **MITIGADO** | `OBSERVADO` |
| **RO-03** | Acoplamiento ilegal entre módulos por refactor | Modulos Backend | Import directo de paquete interno de otro módulo | Violación de fronteras de arquitectura | `ModularArchitectureTest.kt` | Fitness Function ArchUnit en CI (`backend-architecture.yml`) | **CONTROLADO** | `POTENCIAL` |
| **RO-04** | Aumento de latencia en red móvil Android | Cliente Retrofit | Latencia alta o pérdida de paquetes en 4G/5G | Demora en renderizado de actividades | `EXP-004-android-bajo-carga` | Timeouts cortos (3s) y reintentos manuales UI | **ACEPTADO** | `OBSERVADO` |

---

## 14. Reversibilidad

La estrategia de despliegue y observabilidad no introduce dependencias propietarias ni cambios destructivos en la base de datos.
- Revertir un despliegue requiere únicamente redirigir el tráfico del Load Balancer a la versión del contenedor anterior.
- Desactivar las reglas de ArchUnit o alertas se realiza por configuración sin impacto en el código de producción.

---

## 15. Evidencia Concreta

- **Fitness Function en CI**: [`.github/workflows/backend-architecture.yml`](file:///C:/Users/Lenovo/AndroidStudioProjects/RachaPro/.github/workflows/backend-architecture.yml)
- **Validación PASS-FAIL-PASS**: [`experimentos/semana14-fitness-function/README.md`](file:///C:/Users/Lenovo/AndroidStudioProjects/RachaPro/experimentos/semana14-fitness-function/README.md)
- **Timeouts Android**: [`RetrofitClient.kt`](file:///C:/Users/Lenovo/AndroidStudioProjects/RachaPro/app/src/main/java/com/example/rachapro/network/RetrofitClient.kt)
- **Actuator Health Config**: [`SecurityConfig.kt`](file:///C:/Users/Lenovo/AndroidStudioProjects/RachaPro/backend/src/main/kotlin/com/example/rachapro/backend/identity/security/SecurityConfig.kt)

---

## 16. Limitaciones

- No se realizó un despliegue real en una nube comercial (AWS/GCP/Azure) para la medición directa de costos monetarios finales, utilizando en su lugar la métrica de costo relativo ($1x$ vs $\sim 2.2x$).

---

## 17. Trazabilidad Git

- **Rama**: `modulo7/seguridad-operacion`
- **Commits M7 S14**:
  - `M7 S14: registrar evidencia pass-fail-pass de fitness function`
  - `M7 S14: documentar despliegue y observabilidad`

---

## 18. Estado del Entregable

`COMPLETADO Y VERIFICADO`
