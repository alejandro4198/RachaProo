# Evaluación OWASP Top 10:2025 — RachaPro

| OWASP | Aplicabilidad | Evidencia RachaPro | Estado | Brecha/limitación |
|---|---|---|---|---|
| A01 Broken Access Control | Alta | JWT validado, `@AuthenticationPrincipal`, consultas con `findByIdAndUserId`, tests `ResourceOwnershipSecurityTest` | CUBIERTO | Ninguna |
| A02 Security Misconfiguration | Alta | `SecurityConfig`, Actuator con health restringido y metrics protegidas | CUBIERTO | Ninguna |
| A03 Software Supply Chain Failures | Media | GitHub Actions CI configurado para compilación y tests | PARCIAL | Escaneo automatizado de vulnerabilidades en dependencias pendiente |
| A04 Cryptographic Failures | Alta | PBKDF2 con 210,000 iteraciones, JWT firmado con SecretKey cifrada/codificada Base64 | CUBIERTO | Ninguna |
| A05 Injection | Alta | JPA/Hibernate con consultas parametrizadas, validación estricta de DTOs con Bean Validation | CUBIERTO | Ninguna |
| A06 Insecure Design | Media | Idempotencia en completado de sesiones/actividades, validación de estado único activo para Pomodoro | CUBIERTO | Ninguna |
| A07 Authentication Failures | Alta | Autenticación JWT sin estado, LoginRateLimiter (429 Too Many Requests), control de intentos fallidos | CUBIERTO | Rate limiter en memoria (limitación multi-instancia) |
| A08 Software or Data Integrity Failures | Media | `spring.jpa.hibernate.ddl-auto=validate`, integridad referencial en JPA/DB | CUBIERTO | Ninguna |
| A09 Security Logging and Alerting Failures | Media | `RequestIdFilter` (X-Request-Id), logging estructurado de excepciones y eventos sin datos sensibles | CUBIERTO | Alerting externo automatizado no implementado |
| A10 Mishandling of Exceptional Conditions | Alta | `GlobalExceptionHandler` centralizado con respuestas seguras y manejo controlado de excepciones | CUBIERTO | Ninguna |
