# Hardening y Saneamiento Post-Fixes — Semana 11

## 1. Bloque A — Actuator y Superficie Pública
- **Problema**: `/actuator/metrics` expuesto públicamente y `health.show-details=always`.
- **Cambio**: Restringido `/actuator/metrics` a usuarios autenticados en `SecurityConfig.kt`. Ajustado `management.endpoint.health.show-details=when-authorized` en `application.properties`.
- **Tests**: `compileKotlin` = SUCCESSFUL.

## 2. Bloque B — Rate Limiting en Login
- **Problema**: Endpoint `/api/auth/login` carecía de throttling frente a fuerza bruta.
- **Cambio**: Implementado `LoginRateLimiter.kt` en memoria (límite de 5 intentos fallidos en 5 minutos por identificador) devolviendo HTTP 429 (`TOO_MANY_REQUESTS`).
- **Tests**: `LoginRateLimiterTest.kt` (3 tests PASSED).

## 3. Bloque C — Transporte Android
- **Problema**: `usesCleartextTraffic=true` global y URL LAN fija en `RetrofitClient`.
- **Cambio**: Creado `res/xml/network_security_config.xml` restringiendo cleartext a IPs locales (`10.0.2.2`, `127.0.0.1`, `192.168.1.20`) en `AndroidManifest.xml`. Parametrizado `baseUrl` en `RetrofitClient`.

## 4. Bloque D — Pomodoro: Sesión Única Activa
- **Problema**: Posibilidad de crear múltiples sesiones activas (`RUNNING`/`PAUSED`) para un usuario.
- **Cambio**: `PomodoroSessionRepository.existsByUserIdAndStatusIn()` valida en `PomodoroSessionService.create()` la inexistencia de sesiones activas.

## 5. Bloque E — Actividades: Complete Idempotente
- **Problema**: `ActivityService.complete()` reescribía `completedAt` al recibir llamadas repetidas.
- **Cambio**: Si `activity.status == "COMPLETED"`, se retorna el estado existente sin modificar timestamps ni ejecutar `save()`.

## 6. Bloque F — Recordatorios: Transiciones de Estado
- **Problema**: Transiciones inválidas en recordatorios (ej. `CANCELLED` a `DELIVERED`).
- **Cambio**: Validaciones en `ReminderService` impidiendo entrega de cancelados y cancelación de entregados.
- **Tests**: `ReminderServiceTest.kt` (3 tests PASSED).

## 7. Bloque G — Excepciones no Silenciadas en Android
- **Problema**: Captura genérica silenciosa de `Exception` en `UserRepository` y `AchievementRepository` que ocultaba errores HTTP 401/403.
- **Cambio**: Re-lanzamiento explícito de `HttpException` (401/403) para propagar expiración/invalidez de sesión.
