git diff --check# Módulo 7 — Semana 13: Modelo de Amenazas y Seguridad Integral RachaPro

## 1. Objetivo

Realizar el análisis integral de seguridad y modelado de amenazas sobre la arquitectura real de RachaPro (Android + Spring Boot Monolito Modular + PostgreSQL), evaluando el sistema frente al estándar OWASP Top 10, definiendo límites de confianza, identificando controles existentes y propuestos, y documentando el riesgo residual del sistema.

---

## 2. Alcance

El análisis abarca los siguientes componentes y superficies del repositorio actual:
- **Cliente Android**: App nativa Kotlin (Jetpack Compose, Retrofit, Room, DataStore, AndroidManifest permissions).
- **Tráfico de Red**: Comunicaciones HTTP REST entre Android y Backend Spring Boot.
- **Backend Monolito Modular**: Módulos `identity`, `activities`, `focus`, `progress`, `reminders` y soporte `shared`.
- **Capa de Autenticación y Autorización**: Spring Security, JWT (HS256), OAuth2 Resource Server y PasswordHasher (PBKDF2).
- **Persistencia**: Base de datos PostgreSQL aislada con variables de entorno.
- **Infraestructura y CI/CD**: Docker Compose local y Workflows de GitHub Actions (`backend-architecture.yml`).

---

## 3. Arquitectura Inspeccionada

La arquitectura inspeccionada responde al monolito modular desplegable en Spring Boot (JVM Java 25):
- **Identidad**: Autenticación stateless mediante tokens JWT firmados con algoritmo HS256 (`JwtService.kt`). Secretos cargados desde la variable de entorno `RACHAPRO_JWT_SECRET`.
- **Almacenamiento de Credenciales**: Hash de contraseña con PBKDF2 (`PBKDF2WithHmacSHA1`), 210.000 iteraciones y salt aleatorio de 16 bytes (`PasswordHasher.kt`).
- **Seguridad Web**: Spring Security (`SecurityConfig.kt`) con política `STATELESS`, CSRF desactivado por ser API REST stateless, endpoints públicos restringidos a `POST /api/users`, `POST /api/auth/login` y `GET /actuator/health`, exigiendo autenticación OAuth2 Resource Server JWT en todo el prefijo `/api/**`.
- **Cliente Android**: Configurado con Retrofit (`RetrofitClient.kt`), interceptor de autenticación Bearer token, `connectTimeout = 3s` e IP de desarrollo en texto plano (`http://192.168.1.20:8080/`) habilitado mediante `android:usesCleartextTraffic="true"` en `AndroidManifest.xml`.

---

## 4. Activos Relevantes

| ID Activo | Activo | Ubicación / Componente | Clasificación | Impacto en caso de compromiso |
| :--- | :--- | :--- | :--- | :--- |
| **ACT-01** | Credenciales de Usuario (Email / Hash) | PostgreSQL (`users`) / Backend (`identity`) | **CRÍTICO** | Suplantación de identidad, acceso no autorizado a datos. |
| **ACT-02** | Tokens de Sesión (JWT) | Android (`SessionManager`) / Red / Memory | **ALTO** | Secuestro de sesión activa durante tiempo de expiración (1 hora). |
| **ACT-03** | Secreto de Firma JWT (`RACHAPRO_JWT_SECRET`) | Variables de Entorno Backend | **CRÍTICO** | Forja masiva de tokens válidos para cualquier usuario. |
| **ACT-04** | Datos de Actividades y Pomodoro | PostgreSQL (`activities`, `focus`) / Room | **MEDIO** | Fuga o alteración de datos personales de productividad. |
| **ACT-05** | Credenciales Base de Datos PostgreSQL | Variables de Entorno (`application.properties`) | **CRÍTICO** | Acceso total y destrucción/modificación de la base de datos. |

---

## 5. Límites de Confianza (Trust Boundaries)

Se identifican 4 fronteras de confianza inter-componente:

```text
[ Usuario / Pantalla ]
         │ (Frontera TB1: Touch UI / Ingestion)
         ▼
[ Cliente Android (Room / DataStore / Retrofit) ]
         │ (Frontera TB2: Red Insegura / Cleartext HTTP)
         ▼
[ Backend Spring Boot (SecurityConfig / JWT Filter) ]
         │ (Frontera TB3: Capa de Aplicación / Persistencia)
         ▼
[ Base de Datos PostgreSQL (JDBC Driver) ]
```

| Frontera | Activo en Tránsito | Entrada | Amenaza Principal | Control Existente | Control Propuesto / Faltante |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **TB1 (Usuario → Android)** | Formulario Login / Token | Interfaz de Usuario | Intercepción de pantalla / Keylogging | Teclado OS estándar | Validaciones de integridad local |
| **TB2 (Android → Backend)** | JWT Bearer, Credenciales | HTTP Requests / REST API | Eavesdropping / Man-in-the-Middle (MitM) | `connectTimeout = 3s` | Migración a TLS/HTTPS y `usesCleartextTraffic="false"` |
| **TB3 (Backend → DB)** | Consultas SQL / Parámetros | JDBC Driver PostgreSQL | Exposición de credenciales DB / SQLi | Spring Data JPA (Prepared Statements) | Conexiones cifradas JDBC en producción |
| **TB4 (CI/CD / Pipeline)** | Código Fuente / Artifacts | GitHub Actions Runner | Inyección de dependencias / Fuga de Secretos | ArchUnit / runner aislado | Escaneo de vulnerabilidades en dependencias |

---

## 6. Método de Análisis

Se aplicó el framework cualitativo de modelado de amenazas y auditoría de seguridad:
1. **Inspección Código Fuente y Configuración**: Revisión de controladores, `SecurityConfig`, `JwtService`, `PasswordHasher`, `AndroidManifest.xml` y `RetrofitClient.kt`.
2. **Contrastación OWASP Top 10:2025**: Evaluación ítem por ítem clasificando la aplicabilidad según evidencia verificada. (Incluye controles como LoginRateLimiter, RequestIdFilter y network_security_config.xml).
3. **Ponderación Cualitativa de Riesgo**: Matriz de riesgo combinando **Impacto** (Alto, Medio, Bajo) y **Factibilidad** (Alta, Media, Baja), descartando amenazas no relacionadas con RachaPro.

---

## 7. OWASP Top 10:2025 Contrastado contra RachaPro

| OWASP Category (2025) | Estado | Evidencia en RachaPro | Riesgo Observado | Control Existente | Acción / Control Propuesto |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **A01:2025 - Broken Access Control** | `CUBIERTO CON EVIDENCIA` | `SecurityConfig.kt` aísla endpoints públicos y exige JWT en `/api/**`. `ResourceOwnershipSecurityTest.kt` valida BOLA/IDOR. | **MEDIO**: Acceso a recursos ajenos si faltara ownership. | `findByIdAndUserId` en repositorios. | Pruebas unitarias de autorización IDOR/BOLA explícitas. |
| **A02:2025 - Security Misconfiguration** | `CUBIERTO CON EVIDENCIA` | Actuator restringido (`when-authorized`), `SecurityConfig` protege métricas. `network_security_config.xml` limita cleartext a IPs locales. | **BAJO**: Exposición de detalles de salud. | `show-details=when-authorized`, Network Security Config. | Mantener restricciones en entornos productivos. |
| **A03:2025 - Software Supply Chain Failures** | `PARCIAL` | Gradle Wrapper y GitHub Actions CI configurados (`rachapro-ci.yml`). | **MEDIO**: Sin escaneo automatizado de vulnerabilidades en dependencias. | Uso de versiones estables en Gradle. | Integrar motor de escaneo de dependencias en CI. |
| **A04:2025 - Cryptographic Failures** | `CUBIERTO CON EVIDENCIA` | PBKDF2 (210k iteraciones), JWT firmados HS256, Network Security Config para desarrollo local. | **ALTO**: Tráfico HTTP en red local de desarrollo. | PBKDF2, HS256, Network Security Config. | Despliegue obligatorio con HTTPS/TLS en producción. |
| **A05:2025 - Injection** | `CUBIERTO CON EVIDENCIA` | Spring Data JPA con consultas parametrizadas y Bean Validation. | **BAJO**: Inyección SQL/JPA. | JPA Prepared Statements. | Prohibición estricta de SQL dinámico concatenado. |
| **A06:2025 - Insecure Design** | `CUBIERTO CON EVIDENCIA` | Monolito Modular con ArchUnit, idempotencia en `complete()` de actividades y Pomodoro (`ESC-01 + OP-06`). | **MEDIO**: Duplicación de efectos por reintentos. | Idempotencia backend y validación de sesión única activa. | Revisión continua de flujos de estado. |
| **A07:2025 - Authentication Failures** | `CUBIERTO CON EVIDENCIA` | `LoginRateLimiter.kt` (límite de 5 intentos fallidos en 5 min, HTTP 429), PBKDF2 con tiempo constante. | **MEDIO**: Fuerza bruta sobre login. | `LoginRateLimiter` en memoria (limitación multi-instancia). | Evaluar rate limiter distribuido (Redis) para multi-instancia. |
| **A08:2025 - Software or Data Integrity Failures** | `PARCIAL` | `spring.jpa.hibernate.ddl-auto=validate` valida esquema, sin verificación integral de supply chain de software o datos. | **MEDIO**: Desalineación o integridad de artefactos. | DDL validate en arranque. | Pruebas de integridad y verificación de dependencias. |
| **A09:2025 - Security Logging and Alerting Failures** | `CUBIERTO CON EVIDENCIA` | `RequestIdFilter.kt` (X-Request-Id), `GlobalExceptionHandler.kt` sanitizando respuestas y registrando trazas. | **BAJO**: Falta de centralización de logs y alerting externo demostrado. | `RequestIdFilter` y MDC logging. | Integrar colector de alertas centralizado. |
| **A10:2025 - Mishandling of Exceptional Conditions** | `CUBIERTO CON EVIDENCIA` | `GlobalExceptionHandler` manejando 400, 409, 500 sin exponer stacktraces internos. | **BAJO**: Fuga de detalles internos de SQL/JPA. | `GlobalExceptionHandler`. | Monitoreo continuo de excepciones no controladas. |

---

## 8. Matriz de Amenazas

| ID | Amenaza | Activo Afectado | Vector de Ataque | Evidencia en Código | Impacto | Factibilidad | Prioridad | Control Existente | Control Propuesto / Mitigación | Riesgo Residual |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **AM-01** | Sniffing de tráfico JWT / Credenciales | ACT-01, ACT-02 | Intercepción en red local/Wi-Fi por HTTP claro | `android:usesCleartextTraffic="true"`, `BASE_URL="http://..."` | ALTO | ALTA | **ALTA** | Interceptor Bearer token | Deshabilitar cleartext y desplegar HTTPS/TLS | **ABIERTO EN ENTORNO DE DEV** |
| **AM-02** | Fuerza bruta sobre autenticación | ACT-01 | Peticiones HTTP POST ilimitadas a `/api/auth/login` | `AuthController.kt` sin rate limiting | MEDIO | ALTA | **MEDIA** | Hashing PBKDF2 (210k iteraciones) | Rate Limiting propuesto en puerta de entrada | **ACEPTADO EN DEV** |
| **AM-03** | Reutilización de JWT robado | ACT-02 | Copia de cabecera `Authorization: Bearer <token>` | `JwtService.kt` con TTL de 3600s sin revocación | MEDIO | MEDIA | **MEDIA** | Expiración a 60 min | Reducción de TTL e implementación de revocación | **ACEPTADO** |
| **AM-04** | Enumeración de usuarios por respuestas | ACT-01 | Peticiones a `/api/auth/login` con emails arbitrarios | `AuthService.kt` retorna `null` para usuario no hallado o clave inválida | BAJO | MEDIA | **BAJA** | Respuesta idéntica HTTP 401 en login | Mantenimiento de respuestas no diferenciadas | **MITIGADO** |
| **AM-05** | Fuga de secreto JWT en repositorio | ACT-03 | Extracción de clave de firma desde código fuente | `JwtConfig.kt` usa `@Value("${RACHAPRO_JWT_SECRET}")` | CRÍTICO | BAJA | **BAJA** | Carga exclusiva vía variable de entorno | Validación en inicio de longitud mínima | **MITIGADO** |

---

## 9. Priorización Criterio Explícito

Criterio cualitativo utilizado:
- **Prioridad ALTA**: Amenazas que comprometen credenciales o tokens en tránsito sin requerir privilegios previos (explotables directamente sobre la red).
- **Prioridad MEDIA**: Amenazas de denegación de servicio o abuso de autenticación que requieren volumen de peticiones o ventanas de tiempo prolongadas.
- **Prioridad BAJA**: Amenazas mitigadas por el diseño del framework (JPA, PBKDF2, Spring Security) o que requieren acceso físico/privilegiado.

---

## 10. Controles Existentes (Verificados en Repositorio)

1. **Spring Security Stateless JWT**: `SecurityConfig.kt` configura `SessionCreationPolicy.STATELESS` y valida tokens en cada petición HTTP protegida.
2. **PBKDF2 Password Hashing**: `PasswordHasher.kt` utiliza 210.000 iteraciones con salt individual de 16 bytes y comparación de hashes con `MessageDigest.isEqual`.
3. **Gestión de Secretos Externa**: `application.properties` y `JwtConfig.kt` leen credenciales y claves de firma exclusivamente desde variables de entorno.
4. **Protección contra SQL Injection**: Uso de Spring Data JPA Repositories y JPQL parametrizado.
5. **Sanitización de Errores**: `GlobalExceptionHandler.kt` intercepta excepciones y devuelve payloads `ApiError` estandarizados sin stack traces.

---

## 11. Controles Propuestos / Faltantes

1. **Encriptación en Tránsito (HTTPS/TLS)**: Configuración propuesta de certificado SSL/TLS en servidor y deshabilitación de `usesCleartextTraffic` en Android.
2. **Rate Limiting**: Control propuesto de frecuencia de peticiones en la ruta `/api/auth/login`.
3. **Security Headers Web**: Cabeceras HSTS, X-Content-Type-Options y CSP propuestas para respuestas HTTP de Spring Boot.

---

## 12. Riesgo Residual

- **Riesgo en Entorno Actual de Desarrollo**: **MEDIO-ALTO** debido al uso explícito de HTTP Cleartext en Android para pruebas locales (`192.168.1.20:8080`).
- **Riesgo en Producción (Sujeto al despliegue de TLS y controles propuestos)**: **PENDIENTE DE EVALUACIÓN FINAL EN ENTORNO PROD**.

---

## 13. Amenazas Parcialmente Mitigadas / No Verificadas

- **Parcialmente Mitigada - SQL Injection**: Uso de ORM JPA parametrizado en repositorios; requiere vigilancia sobre posibles consultas nativas futuras.
- **No Verificada - SSRF**: Sin evidencia de peticiones salientes dinámicas en los controladores inspeccionados, pero pendiente de análisis dinámico.
- **No Verificada - Seguridad en Almacenamiento Android (DataStore/Room)**: Pendiente de auditoría sobre cifrado local (EncryptedSharedPreferences/SQLCipher).

---

## 14. Evidencia Concreta

- [`SecurityConfig.kt`](file:///C:/Users/Lenovo/AndroidStudioProjects/RachaPro/backend/src/main/kotlin/com/example/rachapro/backend/identity/security/SecurityConfig.kt) (Líneas 18–45): Configuración de filtros y rutas.
- [`JwtService.kt`](file:///C:/Users/Lenovo/AndroidStudioProjects/RachaPro/backend/src/main/kotlin/com/example/rachapro/backend/identity/security/JwtService.kt) (Líneas 18–38): Generación de JWT con expiración a 3600s.
- [`PasswordHasher.kt`](file:///C:/Users/Lenovo/AndroidStudioProjects/RachaPro/backend/src/main/kotlin/com/example/rachapro/backend/identity/user/PasswordHasher.kt) (Líneas 12–58): Algoritmo PBKDF2 y timing-attack protection.
- [`AndroidManifest.xml`](file:///C:/Users/Lenovo/AndroidStudioProjects/RachaPro/app/src/main/AndroidManifest.xml) (Línea 16): `android:usesCleartextTraffic="true"`.
- [`RetrofitClient.kt`](file:///C:/Users/Lenovo/AndroidStudioProjects/RachaPro/app/src/main/java/com/example/rachapro/network/RetrofitClient.kt) (Línea 11): `BASE_URL = "http://192.168.1.20:8080/"`.

---

## 15. Limitaciones

- El análisis se realizó por inspección estática de código y configuración. No se ejecutaron escaneos dinámicos (DAST) ni pruebas de penetración en un entorno desplegado con TLS.

---

## 16. Trazabilidad Git

- **Rama**: `modulo7/seguridad-operacion`
- **Commit M7 S13**: "M7 S13: documentar amenazas y controles de seguridad"

---

## 17. Estado del Entregable

`COMPLETADO — ANÁLISIS ESTÁTICO`
