# Validación de entrada en el backend — Registro de usuarios

Rama: `fix/backend-input-validation` (creada desde `master` `cba2524`).

Alcance: `POST /api/users` y traducción de violaciones `UNIQUE` a HTTP 409.
No se modificó autenticación JWT, hashing PBKDF2, login ni ownership.

---

## 1. Hallazgo

La validación del registro vivía principalmente en el cliente Android
(`RegistrationValidator.kt`). El backend recibía el `CreateUserRequest` sin
anotaciones de Bean Validation y `UserService.create` solo normalizaba el correo
y ejecutaba el pre-check `existsByEmail`. Un cliente podía omitir la app Android y
enviar directamente a la API valores que el dominio consideraba inválidos
(`fullName` en blanco, correo sin formato, contraseña corta, semestre fuera de
1..10 o política de privacidad no aceptada).

Además, `GlobalExceptionHandler` no manejaba `DataIntegrityViolationException`.
Si dos registros concurrentes superaban el pre-check `existsByEmail`, el
constraint `UNIQUE` de `users.email` terminaba como HTTP 500.

---

## 2. Riesgo

- El backend aceptaba y persistía datos inválidos según las reglas del dominio.
- Una carrera entre la comprobación previa y el `INSERT` producía un 500 en lugar
  de un conflicto comprensible para el cliente.
- El cliente podía crear cuentas sin aceptación explícita de la política de
  privacidad, aunque el dominio lo exige.

---

## 3. Corrección

### 3.1 DTO (`CreateUserRequest.kt`)

Se aplicaron anotaciones Jakarta sobre los campos (`@field:` para que Hibernate
Validator las lea en el backing field):

| Campo | Anotaciones |
|---|---|
| `fullName` | `@NotBlank` |
| `email` | `@NotBlank`, `@Email` |
| `password` | `@NotBlank`, `@Size(min = 8)` |
| `semester` | `@Min(1)`, `@Max(10)` |
| `acceptedPrivacyPolicy` | `@AssertTrue` |

No se añadieron reglas de complejidad de contraseña porque el dominio Android no
las exige.

### 3.2 Controller (`UserController.kt`)

`@Valid` se aplicó únicamente al body de `POST /api/users`. La URL, el método, la
estructura del request y la respuesta exitosa `201 CREATED` no cambiaron.
`PATCH /me` no se modificó.

### 3.3 Error 400 (`GlobalExceptionHandler.kt`)

Se agregó el manejo de `MethodArgumentNotValidException` reutilizando
`ApiError(status, error, message, timestamp)`. La respuesta es
`400 Bad Request` sin stack trace, con los errores de campo ordenados y unidos:
`campo: mensaje; campo: mensaje`. Para un solo campo inválido el mensaje es
estable y mostrable por el frontend.

### 3.4 Conflictos de unicidad

- Se conservó el pre-check `existsByEmail` en `UserService.create` (respuesta
  rápida y amigable mediante `ConflictException` → 409).
- El `UNIQUE` de la base de datos sigue siendo la última barrera.
- Se agregó `@ExceptionHandler(DataIntegrityViolationException)` que solo responde
  `409` cuando reconoce un constraint `UNIQUE` demostrado en el esquema:
  - `users_email_key` (`users.email`)
  - `uq_achievements_user_type` (`achievements (user_id, type)`)
  - `uq_categories_user_name` (`categories (user_id, name)`)
- La clasificación (`KnownUniqueConstraint.kt`) recorre la cadena de causas y
  busca esos identificadores en los mensajes del driver, aceptando también los
  nombres por defecto del DDL automático.
- Cualquier otra violación de integridad (por ejemplo `NOT NULL`) **no** se
  disfraza de conflicto: se registra como error de servidor y responde
  `500` con un mensaje genérico y sin stack trace.

---

## 4. No modificado

- JWT (`JwtConfig`, `JwtService`, `SecurityConfig`).
- PBKDF2 (`PasswordHasher`, salt, iteraciones).
- Login (`AuthService.login`, `AuthController`).
- Ownership y repositorios.
- Variables, URL, método y contrato de respuesta de `POST /api/users`.
- `PATCH /api/users/me`.
- `.idea/` y `experimentos/`.

---

## 5. Evidencia real de tests

Comando ejecutado desde `backend`:

```
.\gradlew.bat test --no-daemon
```

Resultado: `27 tests completed, 1 failed`.

| Suite | Tests | Fallos |
|---|---|---|
| `UserRegistrationValidationTest` (nueva) | 12 | 0 |
| `PomodoroSessionServiceTest` | 5 | 0 |
| `ModularArchitectureTest` | 9 | 0 |
| `RachaproBackendApplicationTests` (`contextLoads`) | 1 | 1 |

Tests nuevos (12/12 pasan):

- registro válido → `201`
- `fullName` en blanco → `400`
- email inválido → `400`
- password de 7 caracteres → `400`
- `semester = 0` → `400`
- `semester = 11` → `400`
- `acceptedPrivacyPolicy = false` → `400`
- email duplicado (`ConflictException`) → `409`
- `DataIntegrityViolationException` con `users_email_key` → `409`
- `DataIntegrityViolationException` con `uq_achievements_user_type` → `409`
- `DataIntegrityViolationException` con `uq_categories_user_name` → `409`
- `DataIntegrityViolationException` no clasificada (`NOT NULL`) → `500`, no `409`

Los tests usan MockMvc standalone sobre `UserController` con el
`GlobalExceptionHandler` real, por lo que verifican el `@Valid`, el 400 y el
mapeo de excepciones a 409/500 de forma determinista, sin base de datos.

### Fallo contextual conocido

`RachaproBackendApplicationTests.contextLoads` falla por entorno, no por este
cambio: no hay datasource configurado en la sesión (`RACHAPRO_DB_URL`,
`RACHAPRO_DB_USER`, `RACHAPRO_DB_PASSWORD` vacíos) y Hibernate no puede
determinar el dialecto:

```
Unable to determine Dialect without JDBC metadata
```

No se modificó la configuración para forzar su éxito.

### Compilación

```
.\gradlew.bat compileKotlin --no-daemon   -> BUILD SUCCESSFUL
.\gradlew.bat compileTestKotlin (vía test) -> OK
```

### Pruebas HTTP

No se ejecutaron contra un servidor real: el backend no puede arrancar sin las
variables `RACHAPRO_DB_*` y `RACHAPRO_JWT_SECRET`. No se cambió configuración
permanente para habilitarlo.

---

## 6. Limitaciones

- La detección de unicidad se basa en los nombres de constraint presentes en el
  mensaje del driver. Es suficiente y verificada para PostgreSQL con el esquema
  actual, pero un cambio de motor o de nombres requeriría actualizar
  `KnownUniqueConstraint`.
- Las violaciones de integridad no clasificadas responden `500` genérico y se
  registran en logs; deliberadamente no se convierten en `409`.
- La validación `@Email` se aplica al valor tal como llega en el body. El cliente
  Android ya normaliza (`trim` + `lowercase`) antes de enviar.
