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
2. **Contrastación OWASP Top 10 (v2021)**: Evaluación ítem por ítem clasificando la aplicabilidad según evidencia verificada.
3. **Ponderación Cualitativa de Riesgo**: Matriz de riesgo combinando **Impacto** (Alto, Medio, Bajo) y **Factibilidad** (Alta, Media, Baja), descartando amenazas no relacionadas con RachaPro.

---

## 7. OWASP Top 10 Contrastado contra RachaPro

| OWASP Category | Estado | Evidencia en RachaPro | Riesgo Observado | Control Existente | Acción / Control Propuesto |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **A01:2021 - Broken Access Control** | `APLICA PARCIALMENTE` | `SecurityConfig.kt` aísla endpoints públicos (`/api/users`, `/api/auth/login`) y exige JWT en `/api/**`. En controladores como `ActivityController`, las operaciones se filtran por el `userId` del JWT. | **MEDIO**: Riesgo de BOLA/IDOR si un controlador omitiera validar la pertenencia del recurso. | Exigencia global de JWT y verificación en controladores. | Pruebas unitarias de autorización IDOR/BOLA explícitas. |
| **A02:2021 - Cryptographic Failures** | `APLICA` | Android usa `android:usesCleartextTraffic="true"` y Retrofit apunta a `http://192.168.1.20:8080/`. En backend se usa PBKDF2 y HS256. | **ALTO**: Tráfico HTTP en texto plano susceptible de intercepción en red local. | PBKDF2 (210k iteraciones), JWT firmados HS256. | Configuración propuesta de HTTPS/TLS en cliente y servidor. |
| **A03:2021 - Injection** | `APLICA PARCIALMENTE` | Uso de Spring Data JPA Repositories con mapeo objeto-relacional y consultas parametrizadas. | **BAJO**: Mitigado a nivel de ORM JPA; requiere mantener vigilancia sobre consultas nativas. | JPA Prepared Statements en repositorios Spring Data. | Prohibición estricta de SQL dinámico concatenado. |
| **A04:2021 - Insecure Design** | `APLICA PARCIALMENTE` | Monolito Modular delimitado con ArchUnit (`ModularArchitectureTest.kt`). Token JWT no soporta lista de revocación inmediata. | **MEDIO**: Un token comprometido permanece válido durante su tiempo de expiración (1 hora). | Firma HS256 y expiración a 1 hora. | Reducción propuesta de TTL del token e integración de listas de revocación. |
| **A05:2021 - Security Misconfiguration** | `APLICA PARCIALMENTE` | `/actuator/health` expuesto con `show-details=always`. `application.properties` carga credenciales mediante variables de entorno. | **BAJO**: Exposición leve de detalles de salud en Actuator. | Secretos cargados vía variables de entorno (`RACHAPRO_DB_*`). | Restringir `show-details` en entornos no locales. |
| **A06:2021 - Vulnerable/Outdated Components** | `NO VERIFICADO` | Uso de Java 25, Spring Boot 3.4.x y Kotlin 2.1.0 en Gradle. | **PENDIENTE**: Requiere escaneo automatizado de dependencias. | Uso de versiones recientes en Gradle. | Integrar motor de escaneo de dependencias en CI. |
| **A07:2021 - Identification & Auth Failures** | `APLICA PARCIALMENTE` | Login valida usuario/hash con `PasswordHasher.verify` en tiempo constante (`MessageDigest.isEqual`). Sin rate limiting verificado en `/api/auth/login`. | **MEDIO**: Posibilidad de intentos repetidos sobre el endpoint de login. | PBKDF2 (210k iteraciones), comparación resistente a timing-attacks. | Rate-limiting propuesto para `/api/auth/login`. |
| **A08:2021 - Software & Data Integrity Failures** | `NO VERIFICADO` | Construcción de artefactos desde GitHub Actions usando Gradle Wrapper. | **PENDIENTE**: Sin verificación explícita de firmas de artefactos de terceros. | Gradle Wrapper oficial en CI. | Verificación de firmas y checksums de dependencias. |
| **A09:2021 - Security Logging & Failures** | `APLICA PARCIALMENTE` | `GlobalExceptionHandler.kt` sanitiza respuestas para `IllegalArgumentException` y `ConflictException`. `HttpLoggingInterceptor` en Android usa nivel `BASIC`. | **BAJO**: Logs de HTTP no vuelcan credenciales ni JWTs. | Payloads `ApiError` estandarizados. | Configurar registro de auditoría estructurada para eventos de autenticación. |
| **A10:2021 - Server-Side Request Forgery (SSRF)** | `NO VERIFICADO` | No se observan peticiones HTTP salientes dinámicas en los controladores actuales. | **PENDIENTE**: Sin evidencia de llamadas HTTP salientes, pero no auditado dinámicamente. | Control de peticiones restringidas al dominio local. | Validación de dominios en caso de integrar servicios salientes futuros. |

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
