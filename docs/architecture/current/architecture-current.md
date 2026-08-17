# Arquitectura actual de RachaPro

Este documento registra la arquitectura observada directamente en el código fuente actual del proyecto.

## Nota sobre el estado de la revisión

Este documento fue construido de manera incremental durante la revisión del código fuente.

Por esta razón, algunas secciones iniciales contienen elementos marcados como `EVIDENCIA PENDIENTE` que posteriormente fueron revisados y verificados en secciones posteriores del mismo documento.

Las evidencias pendientes vigentes serán consolidadas al finalizar la revisión arquitectónica.

No representa todavía una descripción completa de la arquitectura. Se irá ampliando únicamente a partir de evidencia encontrada en el repositorio.

## 1. Punto de inicialización de la aplicación

### Evidencia revisada

`RachaProApplication.kt`

### HECHOS VERIFICADOS

La aplicación define una clase `RachaProApplication` que extiende `Application`.

Durante la inicialización de la aplicación se crean los canales de notificación mediante:

`NotificationChannels.createNotificationChannels(...)`.

La clase `RachaProApplication` mantiene una instancia central denominada:

`RachaProDatabase`

La implementación concreta de esta base de datos todavía debe revisarse antes de afirmar qué tecnología de persistencia utiliza.

## 2. Repositorios observados

La aplicación construye y expone los siguientes repositorios:

- `UserRepository`
- `CategoryRepository`
- `ActivityRepository`
- `SubtaskRepository`
- `ReminderRepository`
- `PomodoroRepository`
- `AchievementRepository`

### Dependencias observadas

`UserRepository`
- `userDao`

`CategoryRepository`
- `categoryDao`

`ActivityRepository`
- `activityDao`
- `categoryDao`

`SubtaskRepository`
- `subtaskDao`
- `activityDao`

`ReminderRepository`
- `reminderDao`
- `activityDao`

`PomodoroRepository`
- `pomodoroSessionDao`
- `activityDao`

`AchievementRepository`
- `achievementDao`

## 3. Gestión de sesión y preferencias

Se observaron los siguientes componentes:

- `SessionManager`
- `UserPreferencesManager`

Ambos reciben un `Context` de Android para su construcción.

La tecnología utilizada internamente por estos componentes todavía debe verificarse revisando sus respectivos archivos.

## 4. Recordatorios y notificaciones

Se observaron los siguientes componentes:

- `NotificationChannels`
- `ReminderScheduler`

Los canales de notificación se inicializan al iniciar la aplicación.

`ReminderScheduler` recibe el `applicationContext`.

El mecanismo utilizado para programar los recordatorios todavía debe comprobarse revisando su implementación.

## 5. Construcción de dependencias

Los repositorios, la base de datos, el administrador de sesión, las preferencias y el programador de recordatorios son creados dentro de `RachaProApplication` utilizando inicialización `lazy`.

No se ha verificado todavía el uso de un framework externo de inyección de dependencias.

## Evidencia pendiente

Para continuar documentando la arquitectura deben revisarse:

- `RachaProDatabase.kt`
- DAOs de la aplicación
- `SessionManager.kt`
- `UserPreferencesManager.kt`
- `ReminderScheduler.kt`
- Repositories
- ViewModels
- Pantallas Compose
## 6. Persistencia local

### Evidencia revisada

`RachaProDatabase.kt`

### HECHOS VERIFICADOS

La aplicación utiliza Room como mecanismo de persistencia local.

La clase `RachaProDatabase`:

- Extiende `RoomDatabase`.
- Está anotada con `@Database`.
- Utiliza una base de datos llamada `rachapro_database`.
- Se encuentra actualmente en la versión 6.
- Utiliza migraciones explícitas entre versiones.
- Mantiene una única instancia mediante el patrón Singleton.

### Entidades registradas

La base de datos contiene las siguientes entidades:

- `UserEntity`
- `CategoryEntity`
- `ActivityEntity`
- `SubtaskEntity`
- `ReminderEntity`
- `PomodoroSessionEntity`
- `AchievementEntity`

### DAOs registrados

La base de datos expone los siguientes DAOs:

- `UserDao`
- `CategoryDao`
- `ActivityDao`
- `SubtaskDao`
- `ReminderDao`
- `PomodoroSessionDao`
- `AchievementDao`

## 7. Evolución del esquema de datos

### HECHOS VERIFICADOS

El código contiene migraciones explícitas:

- `MIGRATION_1_2`
- `MIGRATION_2_3`
- `MIGRATION_3_4`
- `MIGRATION_4_5`
- `MIGRATION_5_6`

Las migraciones observadas incorporan progresivamente:

### Versión 2
- Categorías.
- Actividades.

### Versión 3
- Subtareas.

### Versión 4
- Recordatorios.

### Versión 5
- Sesiones Pomodoro.

### Versión 6
- Logros.

## 8. Datos asociados al usuario

### HECHO VERIFICADO

Varias tablas incluyen un campo `userId`, entre ellas:

- Categorías.
- Actividades.
- Recordatorios.
- Sesiones Pomodoro.
- Logros.

Esto evidencia que la persistencia actual contempla asociación de información con usuarios.

Todavía debe revisarse la lógica de consulta y los repositorios antes de afirmar que existe aislamiento completo entre usuarios.

## 9. Índices observados

### HECHO VERIFICADO

La base de datos utiliza índices para diferentes consultas y restricciones.

Entre los casos observados se encuentran índices sobre:

- `userId`
- `categoryId`
- `activityId`
- `status`
- `dueDateEpochDay`
- `triggerAtMillis`
- `completedDateEpochDay`

También existen índices compuestos y restricciones únicas, por ejemplo:

- categoría única por `userId` y `name`
- logro único por `userId` y `type`

## 10. Conclusión parcial sobre persistencia

La implementación actual utiliza una base de datos Room embebida dentro de la aplicación Android.

Con esta evidencia todavía no se ha identificado una base de datos remota ni un backend dentro del archivo analizado.

La existencia o ausencia de componentes externos debe verificarse revisando el resto del repositorio antes de realizar una afirmación definitiva.

## 11. Gestión de sesión

### Evidencia revisada

`SessionManager.kt`

### HECHOS VERIFICADOS

La aplicación utiliza Android DataStore Preferences para persistir información de sesión.

El DataStore utilizado se llama:

`session_preferences`

La sesión almacena:

- `logged_user_id`
- `onboarding_completed`

La clase `SessionState` representa el estado de sesión actual y contiene:

- `userId`
- `onboardingCompleted`

Se considera que existe una sesión iniciada cuando `userId` no es nulo.

## 12. Operaciones de sesión

### HECHOS VERIFICADOS

`SessionManager` permite:

- Guardar el identificador del usuario autenticado mediante `saveLoggedUser`.
- Cerrar la sesión eliminando el identificador del usuario mediante `clearSession`.
- Registrar si el onboarding fue completado mediante `setOnboardingCompleted`.

La información se almacena utilizando `DataStore.edit`.

## 13. Manejo de errores de lectura

### HECHO VERIFICADO

La lectura del DataStore contempla errores de tipo `IOException`.

Cuando ocurre este error se emiten preferencias vacías mediante `emptyPreferences()`.

Otros tipos de error son propagados.

## 14. Conclusión parcial sobre sesión

La implementación actual mantiene la sesión localmente mediante Android DataStore.

El estado de autenticación se representa mediante la existencia de un `userId` persistido.

Este archivo no demuestra por sí solo:

- cómo se validan las credenciales;
- cómo se almacenan las contraseñas;
- cómo se comprueba la identidad del usuario durante el login;
- si las contraseñas están cifradas, hasheadas o almacenadas en texto plano.

Estos aspectos deben verificarse revisando la implementación de usuarios y autenticación.

## 15. Gestión de usuarios y autenticación

### Evidencia revisada

`UserRepository.kt`

### HECHOS VERIFICADOS

La gestión de usuarios utiliza `UserRepository`, que depende de `UserDao`.

El repositorio permite:

- Consultar usuarios por identificador.
- Registrar nuevos usuarios.
- Iniciar sesión.
- Actualizar nombre y semestre del perfil.

## 16. Registro de usuarios

Durante el registro:

- El correo se normaliza eliminando espacios y convirtiéndolo a minúsculas.
- El nombre se normaliza eliminando espacios al inicio y al final.
- Se verifica previamente si el correo ya existe.
- La contraseña ingresada no se asigna directamente a `UserEntity`.
- La contraseña se procesa mediante `PasswordHasher.hashPassword`.
- El resultado genera dos valores:
    - `hash`
    - `salt`
- Estos valores se almacenan en:
    - `passwordHash`
    - `passwordSalt`

El procesamiento de la contraseña se ejecuta utilizando `Dispatchers.Default`.

El repositorio también contempla restricciones de correo duplicado mediante el manejo de `SQLiteConstraintException`.

## 17. Inicio de sesión

Durante el inicio de sesión:

1. El correo ingresado se normaliza.
2. Se consulta el usuario mediante `UserDao`.
3. Si el usuario no existe, el resultado es `InvalidCredentials`.
4. Si el usuario existe, la contraseña ingresada se valida mediante:

`PasswordHasher.verifyPassword(...)`

La verificación utiliza:

- contraseña ingresada;
- hash almacenado;
- salt almacenado.

Cuando la verificación es válida se retorna el usuario autenticado.

Cuando no es válida se retorna `InvalidCredentials`.

## 18. Protección de credenciales

### HECHO VERIFICADO

`UserRepository` no almacena directamente la contraseña recibida durante el registro.

Antes de persistirla invoca un componente denominado `PasswordHasher` y almacena los valores `passwordHash` y `passwordSalt`.

### EVIDENCIA PENDIENTE

Todavía debe revisarse `PasswordHasher.kt` para determinar:

- Algoritmo utilizado.
- Generación del salt.
- Número de iteraciones, si aplica.
- Forma de comparación.
- Características concretas de la protección implementada.

Por lo tanto, todavía no se declara cumplido RNF03 únicamente con esta evidencia.

## 19. Manejo de resultados de autenticación

La implementación define resultados explícitos para las operaciones de usuario.

Registro:

- `Success`
- `EmailAlreadyRegistered`
- `Error`

Inicio de sesión:

- `Success`
- `InvalidCredentials`

Actualización de perfil:

- `Success`
- `NotFound`
- `InvalidData`
- `Error`

## 20. Implementación de protección de contraseñas

### Evidencia revisada

`PasswordHasher.kt`

### HECHOS VERIFICADOS

La aplicación utiliza un mecanismo de hashing de contraseñas basado en:

`PBKDF2WithHmacSHA1`

La configuración observada es:

- Iteraciones: `210000`
- Longitud de clave derivada: `256` bits
- Longitud del salt: `16` bytes

## 21. Generación del salt

Para cada contraseña registrada se genera un salt mediante:

`SecureRandom`

El salt se genera de forma independiente antes de calcular el hash de la contraseña.

Tanto el hash como el salt se convierten a Base64 antes de ser almacenados.

## 22. Verificación de contraseña

Durante la validación de credenciales:

1. Se recupera el salt almacenado.
2. Se vuelve a calcular el hash utilizando la contraseña ingresada y el mismo salt.
3. Se compara el hash calculado con el hash almacenado.

La comparación se realiza mediante:

`MessageDigest.isEqual(...)`

## 23. Manejo de la contraseña en memoria

Después de generar el hash, la implementación ejecuta:

`PBEKeySpec.clearPassword()`

para limpiar la contraseña mantenida internamente por el objeto `PBEKeySpec`.

## 24. Clasificación respecto a RNF03

### HECHO VERIFICADO

La contraseña no se almacena directamente en texto plano dentro del flujo observado.

La implementación utiliza:

- Hash de contraseña.
- Salt aleatorio.
- PBKDF2.
- Múltiples iteraciones.
- Comparación mediante `MessageDigest.isEqual`.

### OBSERVACIÓN

El requerimiento histórico RNF03 utiliza el término "cifrado" para referirse a la protección de contraseñas.

La implementación actual observada utiliza hashing de contraseñas y no cifrado reversible.

Por lo tanto, no se debe modificar la redacción histórica del requerimiento para hacerla coincidir con la implementación.

### EVIDENCIA TODAVÍA PENDIENTE

RNF03 todavía no debe declararse completamente cumplido únicamente con esta revisión.

Falta comprobar:

- La estructura real de `UserEntity`.
- Las consultas de `UserDao`.
- Restricción de acceso sin sesión.
- Separación de información entre usuarios.
- Ejecución de pruebas de autenticación.

## 25. Entidad de usuario

### Evidencia revisada

`UserEntity.kt`

### HECHOS VERIFICADOS

La información de los usuarios se almacena en la tabla:

`users`

Cada usuario contiene los siguientes campos:

- `id`
- `fullName`
- `email`
- `passwordHash`
- `passwordSalt`
- `semester`
- `acceptedPrivacyPolicy`
- `createdAt`
- `updatedAt`

El identificador `id` es una clave primaria autogenerada.

## 26. Restricción de correo electrónico

### HECHO VERIFICADO

La tabla `users` define un índice único sobre el campo:

`email`

Esto impide que existan dos registros de usuario con el mismo correo electrónico dentro de la base de datos.

## 27. Persistencia de credenciales

### HECHO VERIFICADO

La entidad de usuario no contiene un campo destinado a almacenar directamente la contraseña original.

Los campos relacionados con credenciales son:

- `passwordHash`
- `passwordSalt`

Esto es consistente con el flujo observado en `UserRepository` y `PasswordHasher`, donde la contraseña se procesa antes de almacenarse.

## 28. Otros datos persistidos del usuario

Además de las credenciales, la aplicación almacena:

- Nombre completo.
- Correo electrónico.
- Semestre.
- Estado de aceptación de la política de privacidad.
- Fecha de creación.
- Fecha de última actualización.

## 29. Estado parcial de RNF03

La evidencia revisada hasta este punto permite comprobar que:

- Existe autenticación mediante correo y contraseña.
- La contraseña no se almacena directamente en texto plano en `UserEntity`.
- Se almacena un hash y un salt.
- El correo tiene una restricción de unicidad.

RNF03 todavía permanece parcialmente verificado.

Falta comprobar:

- Consultas de acceso a usuarios.
- Restricción de acceso sin sesión.
- Separación de datos entre usuarios.
- Pruebas ejecutables de autenticación.

## 30. Acceso a datos de usuarios

### Evidencia revisada

`UserDao.kt`

### HECHOS VERIFICADOS

La aplicación utiliza un DAO de Room denominado `UserDao` para acceder a la tabla `users`.

El DAO permite:

- Insertar usuarios.
- Consultar un usuario por correo electrónico.
- Consultar un usuario por identificador.
- Verificar si un correo ya existe.
- Actualizar un usuario existente.

## 31. Inserción de usuarios

La inserción utiliza:

`OnConflictStrategy.ABORT`

Esto significa que, ante un conflicto de restricciones definido por la base de datos, la operación de inserción se aborta.

Esto es consistente con la restricción de unicidad existente sobre el correo electrónico.

## 32. Consulta por correo electrónico

La consulta:

`getUserByEmail(email)`

busca un usuario por correo electrónico y limita el resultado a un único registro.

Esta operación es utilizada por el flujo de inicio de sesión observado previamente en `UserRepository`.

## 33. Verificación de correo existente

La operación:

`emailExists(email)`

utiliza una consulta `SELECT EXISTS` para determinar si ya existe un usuario registrado con el correo proporcionado.

Esta operación es utilizada durante el registro de nuevos usuarios.

## 34. Consulta por identificador

La operación:

`getUserById(userId)`

permite recuperar un usuario mediante su identificador interno.

La consulta limita el resultado a un único registro.

## 35. Actualización de usuario

El DAO permite actualizar un registro existente mediante la anotación:

`@Update`

Esta operación es utilizada por la lógica de actualización de perfil observada en `UserRepository`.

## 36. Estado parcial de autenticación y RNF03

La evidencia revisada hasta este punto permite confirmar la existencia del siguiente flujo:

`UserDao → UserRepository → PasswordHasher → SessionManager`

Se ha verificado:

- Consulta de usuario por correo.
- Validación de existencia de correo.
- Registro de usuario.
- Actualización de usuario.
- Hashing y salt de contraseña.
- Verificación de contraseña.
- Persistencia local del identificador de usuario autenticado.

Todavía falta comprobar mediante navegación y pruebas ejecutables que un usuario sin sesión no pueda acceder a módulos protegidos.

También falta comprobar de forma directa el aislamiento de datos entre distintos usuarios.

## 37. Navegación principal de la aplicación

### Evidencia revisada

`RachaProNavHost.kt`

### HECHOS VERIFICADOS

La aplicación utiliza Navigation Compose mediante:

- `NavHost`
- `rememberNavController`
- `composable`

La ruta inicial es:

`APP_START`

Las rutas observadas son:

- `APP_START`
- `WELCOME`
- `ONBOARDING`
- `REGISTER`
- `LOGIN`
- `HOME`
- `NEW_ACTIVITY`
- `EDIT_ACTIVITY`

## 38. Control de arranque según estado de sesión

Al iniciar la aplicación, `APP_START` observa el estado proporcionado por `AppStartViewModel`.

Se contemplan los siguientes estados:

- `Loading`
- `NeedsOnboarding`
- `LoggedOut`
- `LoggedIn`

Según el estado, la aplicación navega a:

- `WELCOME`, cuando se necesita onboarding.
- `LOGIN`, cuando no existe sesión iniciada.
- `HOME`, cuando existe una sesión iniciada.

Las navegaciones desde `APP_START` eliminan dicha ruta del back stack mediante `popUpTo(..., inclusive = true)`.

## 39. Flujo de autenticación y navegación

### Registro

Desde la pantalla de registro se utiliza `AuthViewModel.register(...)`.

Después de un registro exitoso, el usuario es dirigido a `LOGIN`.

### Inicio de sesión

Desde `LOGIN` se utiliza:

`AuthViewModel.login(...)`

Después de un inicio de sesión exitoso, se navega a:

`HOME`

### Cierre de sesión

Desde `HOME` se ejecuta:

`authViewModel.logout()`

Cuando el estado de autenticación cambia a:

`LogoutSuccess`

la navegación dirige al usuario hacia `LOGIN` y elimina `HOME` del back stack.

## 40. ViewModels observados en la navegación

La navegación principal crea o utiliza los siguientes ViewModels:

- `AuthViewModel`
- `AppStartViewModel`
- `MainViewModel`
- `ActivitiesViewModel`
- `PomodoroViewModel`
- `ProgressViewModel`
- `ProfileViewModel`
- `SubtasksViewModel`
- `ReminderViewModel`

Esto evidencia que la interfaz delega parte del estado y las operaciones de los módulos a ViewModels específicos.

## 41. Estado de sesión en gestión de actividades

La navegación contempla explícitamente el estado:

`ActivitiesUiState.NoActiveSession`

Cuando este estado ocurre durante la edición de una actividad, la interfaz muestra:

`No hay una sesión activa.`

Esto constituye evidencia de que el módulo de actividades contempla la ausencia de una sesión activa.

## 42. Módulos funcionales observados desde HOME

Desde `HOME` se conectan funcionalidades relacionadas con:

- Actividades.
- Subtareas.
- Recordatorios.
- Pomodoro.
- Progreso.
- Perfil.
- Preferencias.
- Permisos de notificación.

La navegación también permite crear y editar actividades mediante rutas independientes.

## 43. Compartición de estado entre pantallas

Para crear y editar actividades se reutiliza el `ActivitiesViewModel` asociado al back stack de `HOME`.

Esto permite compartir el mismo estado del módulo de actividades entre:

- `HOME`
- `NEW_ACTIVITY`
- `EDIT_ACTIVITY`

Los ViewModels de subtareas y recordatorios utilizados en edición se asocian al `backStackEntry` de la pantalla correspondiente.

## 44. Estado parcial de control de acceso

### HECHOS VERIFICADOS

El flujo normal de inicio de la aplicación utiliza el estado de sesión para decidir entre `LOGIN` y `HOME`.

También se ha verificado que:

- El logout redirige a `LOGIN`.
- `HOME` se elimina del back stack al cerrar sesión.
- El módulo de actividades contempla el estado `NoActiveSession`.

### EVIDENCIA TODAVÍA PENDIENTE

Este archivo por sí solo no permite afirmar que todas las pantallas protegidas validen de manera independiente la sesión antes de acceder a datos.

Para completar esta evidencia deben revisarse:

- `AppStartViewModel.kt`
- `AuthViewModel.kt`
- La lógica de consulta de actividades por usuario.

## 45. Resolución del estado inicial de la aplicación

### Evidencia revisada

`AppStartViewModel.kt`

### HECHOS VERIFICADOS

`AppStartViewModel` depende de `SessionManager`.

El estado inicial de navegación se obtiene observando:

`sessionManager.sessionState`

La información de sesión se transforma en uno de los siguientes estados:

- `Loading`
- `NeedsOnboarding`
- `LoggedOut`
- `LoggedIn`

## 46. Condiciones de acceso inicial

La lógica observada funciona de la siguiente manera:

### Usuario autenticado

Si:

`session.userId != null`

se genera:

`AppStartState.LoggedIn(userId)`

### Usuario sin sesión que ya completó onboarding

Si no existe `userId`, pero:

`session.onboardingCompleted == true`

se genera:

`AppStartState.LoggedOut`

### Usuario nuevo

Si no existe `userId` y el onboarding todavía no ha sido completado, se genera:

`AppStartState.NeedsOnboarding`

## 47. Persistencia del onboarding

La operación:

`completeOnboarding()`

utiliza:

`sessionManager.setOnboardingCompleted(true)`

Esto evidencia que el estado de finalización del onboarding se conserva mediante el mecanismo de sesión ya identificado anteriormente.

## 48. Exposición reactiva del estado

El estado se expone como:

`StateFlow<AppStartState>`

La transformación del estado de sesión se ejecuta utilizando:

- `map`
- `stateIn`
- `viewModelScope`

El valor inicial es:

`AppStartState.Loading`

## 49. Construcción del ViewModel

`AppStartViewModel` obtiene `SessionManager` desde:

`RachaProApplication`

mediante un `ViewModelProvider.Factory`.

Esto confirma la relación:

`RachaProApplication → SessionManager → AppStartViewModel`

## 50. Estado parcial de control de acceso

### HECHO VERIFICADO

La decisión inicial de navegación depende directamente del identificador de usuario almacenado en `SessionManager`.

Cuando existe un `userId`, el estado se considera `LoggedIn`.

Cuando no existe un `userId`, el usuario no recibe el estado `LoggedIn`.

Este mecanismo se utiliza posteriormente en `RachaProNavHost` para decidir si navegar a `HOME` o `LOGIN`.

### EVIDENCIA TODAVÍA PENDIENTE

Todavía falta verificar:

- cómo `AuthViewModel` guarda el usuario después de un login válido;
- cómo elimina la sesión durante logout;
- si los repositorios de datos filtran correctamente la información mediante el `userId` activo;
- si un usuario puede acceder a datos pertenecientes a otro usuario.

## 51. Flujo de autenticación en el ViewModel

### Evidencia revisada

`AuthViewModel.kt`

### HECHOS VERIFICADOS

`AuthViewModel` depende de:

- `UserRepository`
- `SessionManager`
- `ReminderRepository`
- `ReminderScheduler`

El ViewModel gestiona:

- Registro.
- Inicio de sesión.
- Cierre de sesión.
- Restauración de recordatorios.
- Estados de autenticación mostrados por la interfaz.

## 52. Registro

Antes de registrar un usuario se utiliza:

`RegistrationValidator.validate(...)`

Si existe un error de validación, el registro no continúa.

Cuando la validación es correcta, el ViewModel llama a:

`userRepository.registerUser(...)`

Los resultados posibles observados son:

- Registro exitoso.
- Correo ya registrado.
- Error.

## 53. Inicio de sesión

Durante el inicio de sesión:

1. Se valida que correo y contraseña no estén vacíos.
2. Se ejecuta `userRepository.login(...)`.
3. Si las credenciales son inválidas, se genera `InvalidCredentials`.
4. Si las credenciales son válidas, se guarda el identificador del usuario mediante:

`sessionManager.saveLoggedUser(...)`

5. Después se restauran los recordatorios pendientes correspondientes al usuario.
6. Finalmente se genera `LoginSuccess`.

### HECHO VERIFICADO

La sesión solo se guarda después de que `UserRepository` retorna un resultado de login exitoso.

## 54. Cierre de sesión

Durante el cierre de sesión:

1. Se consulta la sesión actual.
2. Se obtiene el `userId` activo.
3. Se consultan los recordatorios programados del usuario.
4. Se intenta cancelar cada alarma asociada al usuario.
5. Se elimina el usuario activo de la sesión mediante:

`sessionManager.clearSession()`

6. Se genera:

`LogoutSuccess`

### HECHO VERIFICADO

El cierre de sesión elimina el identificador del usuario almacenado en `SessionManager`.

## 55. Relación entre sesión y recordatorios

Durante un login exitoso se ejecuta:

`restoreScheduledReminders(userId)`

Esta operación consulta los recordatorios programados pertenecientes al usuario autenticado.

Los recordatorios futuros se vuelven a programar.

Los recordatorios cuyo tiempo ya pasó son cancelados en el repositorio.

Durante logout se intentan cancelar las alarmas programadas del usuario antes de eliminar la sesión.

## 56. Tolerancia a fallos en recordatorios

### HECHO VERIFICADO

Los errores producidos durante la restauración o cancelación de recordatorios no impiden necesariamente:

- iniciar sesión;
- cerrar sesión.

La implementación captura excepciones relacionadas con estos procesos para permitir que el flujo de autenticación continúe.

## 57. Estados de autenticación

`AuthViewModel` expone un `StateFlow<AuthUiState>`.

Los estados observados son:

- `Idle`
- `Loading`
- `RegistrationSuccess`
- `EmailAlreadyRegistered`
- `LoginSuccess`
- `InvalidCredentials`
- `ValidationError`
- `Error`
- `LogoutSuccess`

## 58. Estado parcial de RNF03

### HECHOS VERIFICADOS

Hasta este punto se ha comprobado mediante código que:

- Las credenciales son validadas antes de crear una sesión.
- Una sesión se crea únicamente después de un login válido.
- La sesión almacena el identificador del usuario autenticado.
- El logout elimina ese identificador.
- La navegación utiliza dicho estado para decidir entre LOGIN y HOME.
- Las contraseñas se almacenan mediante hash y salt, no como contraseña original.

### EVIDENCIA TODAVÍA PENDIENTE

Todavía falta comprobar directamente que los datos funcionales de la aplicación se consultan utilizando el `userId` de la sesión activa.

Esto es necesario para verificar el aislamiento entre usuarios.

## 59. Aislamiento de actividades por usuario

### Evidencia revisada

`ActivityRepository.kt`

### HECHOS VERIFICADOS

`ActivityRepository` depende de:

- `ActivityDao`
- `CategoryDao`

Las operaciones principales del repositorio reciben explícitamente un `userId`.

Entre ellas se encuentran:

- Consultar actividades.
- Consultar actividades por fecha.
- Consultar días con actividades completadas.
- Actualizar estados de actividades.
- Consultar una actividad por identificador.
- Crear una actividad.
- Editar una actividad.
- Completar una actividad.
- Reprogramar una actividad.
- Aplicar borrado lógico.
- Consultar estadísticas de actividades completadas.

## 60. Consulta de actividades

Las operaciones:

- `observeActivities(userId)`
- `observeActivitiesByDate(userId, epochDay)`
- `getActivityById(activityId, userId)`

envían el identificador del usuario hacia `ActivityDao`.

Esto evidencia que el repositorio contempla consultas de actividades asociadas a un usuario específico.

## 61. Creación de actividades

Durante la creación de una actividad:

1. Se recibe un `userId`.
2. Se valida que la categoría solicitada exista para ese mismo usuario mediante:

`categoryDao.getCategoryById(categoryId, userId)`

3. Si la categoría no corresponde al usuario, la actividad no se crea.
4. La nueva `ActivityEntity` se construye incluyendo:

`userId = userId`

### HECHO VERIFICADO

Una actividad creada mediante este repositorio queda asociada explícitamente al usuario indicado.

## 62. Modificación de actividades

Las operaciones de:

- edición;
- completado;
- reprogramación;
- borrado lógico;

envían tanto:

- `activityId`
- `userId`

hacia `ActivityDao`.

El resultado depende del número de filas afectadas.

Si ninguna fila es modificada, el repositorio devuelve:

`NotFoundOrNotAllowed`

## 63. Validación de propiedad de categorías

Durante la creación y actualización de actividades se verifica que la categoría utilizada pueda obtenerse mediante:

`categoryId + userId`

Esto evita, a nivel de repositorio, utilizar una categoría que no sea encontrada para el usuario proporcionado.

## 64. Borrado lógico

La eliminación de actividades se realiza mediante:

`softDeleteActivity(...)`

El repositorio no elimina directamente el registro desde este flujo.

La operación recibe:

- `activityId`
- `userId`

y registra además marcas de tiempo relacionadas con la eliminación y actualización.

## 65. Estadísticas por usuario

Las estadísticas de actividades completadas también reciben explícitamente el `userId`.

Se observaron operaciones para:

- Total de actividades completadas.
- Actividades completadas dentro de un periodo.
- Actividades completadas agrupadas por día.

## 66. Estado parcial de aislamiento entre usuarios

### HECHO VERIFICADO

`ActivityRepository` fue diseñado para ejecutar las operaciones del módulo de actividades utilizando un identificador de usuario.

El repositorio no observa una operación principal sobre actividades que, en este archivo, se ejecute sin recibir `userId`.

### EVIDENCIA TODAVÍA PENDIENTE

Este archivo no demuestra por sí solo que el `userId` recibido corresponda siempre al usuario autenticado actualmente.

Para comprobarlo debe revisarse:

`ActivitiesViewModel.kt`

También debe revisarse `ActivityDao.kt` para confirmar que sus consultas SQL utilizan efectivamente el `userId` recibido.

## 67. Obtención del usuario activo en el módulo de actividades

### Evidencia revisada

`ActivitiesViewModel.kt`

### HECHOS VERIFICADOS

`ActivitiesViewModel` depende de:

- `ActivityRepository`
- `CategoryRepository`
- `SessionManager`

Al cargar los datos del módulo se consulta:

`sessionManager.sessionState.first()`

De esta sesión se obtiene:

`session.userId`

Si el identificador es nulo, el módulo cambia su estado a:

`ActivitiesUiState.NoActiveSession`

y no continúa con la carga de actividades.

## 68. Uso del usuario autenticado

Cuando existe una sesión activa, el `userId` obtenido desde `SessionManager` se utiliza para:

- Consultar categorías.
- Consultar actividades.
- Crear categorías iniciales.
- Actualizar estados de actividades.
- Crear actividades.
- Editar actividades.
- Completar actividades.
- Eliminar actividades.

El `userId` utilizado por estas operaciones proviene del estado de sesión activo.

## 69. Flujo observado del módulo de actividades

La cadena observada es:

`SessionManager → ActivitiesViewModel → ActivityRepository / CategoryRepository`

El ViewModel no recibe manualmente el usuario desde la interfaz.

El identificador utilizado para cargar los datos se obtiene directamente desde la sesión persistida.

## 70. Creación de actividades y sesión

Para crear una actividad:

1. El ViewModel requiere que el estado actual sea `ActivitiesUiState.Success`.
2. Este estado contiene el `userId` obtenido previamente desde `SessionManager`.
3. El mismo identificador se envía a:

`activityRepository.createActivity(...)`

## 71. Modificación de actividades y sesión

Las operaciones de:

- Actualización.
- Completado.
- Eliminación.

utilizan:

`currentState.userId`

Este valor corresponde al usuario obtenido durante la carga inicial desde `SessionManager`.

## 72. Categorías asociadas al usuario

Las categorías iniciales:

- Estudio.
- Personal.
- Trabajo.

se crean únicamente cuando el usuario activo no posee categorías.

Las operaciones de consulta y creación de categorías reciben explícitamente el `userId` activo.

## 73. Ausencia de sesión

### HECHO VERIFICADO

Si `session.userId` es nulo, el módulo establece:

`ActivitiesUiState.NoActiveSession`

y termina el proceso de carga mediante `return@launch`.

Por lo tanto, este ViewModel no carga actividades cuando no existe una sesión activa.

## 74. Estado parcial de aislamiento de datos

### HECHOS VERIFICADOS

Hasta este punto se ha comprobado la siguiente cadena:

`SessionManager → userId activo → ActivitiesViewModel → ActivityRepository`

El identificador de usuario utilizado en las operaciones del módulo proviene de la sesión activa.

### EVIDENCIA TODAVÍA PENDIENTE

Falta revisar `ActivityDao.kt` para confirmar que las consultas SQL realmente incluyen el `userId` recibido.

Después de esa revisión podrá determinarse con mayor respaldo si el módulo de actividades aplica aislamiento de datos entre usuarios a nivel de persistencia.

## 75. Filtrado por usuario en la capa de persistencia

### Evidencia revisada

`ActivityDao.kt`

### HECHOS VERIFICADOS

Las consultas y operaciones principales sobre la tabla `activities` incluyen explícitamente el campo:

`userId`

como condición de acceso.

Esto se observa en operaciones de:

- Consulta general de actividades.
- Consulta de actividades por fecha.
- Consulta de actividad por identificador.
- Edición.
- Completado.
- Reprogramación.
- Borrado lógico.
- Actualización de actividades vencidas.
- Restauración de actividades pendientes.
- Consulta de días completados.
- Estadísticas de actividades completadas.

## 76. Consultas de actividades por usuario

La consulta:

`observeActivities(userId)`

incluye la condición:

`WHERE userId = :userId`

Además excluye actividades con borrado lógico mediante:

`isDeleted = 0`

La consulta por fecha también utiliza:

- `userId`
- `dueDateEpochDay`
- `isDeleted`

## 77. Consulta individual protegida por usuario

La operación:

`getActivityById(activityId, userId)`

requiere simultáneamente:

- identificador de actividad;
- identificador de usuario;
- que la actividad no esté eliminada.

Por lo tanto, una actividad no es recuperada únicamente por su `id`.

## 78. Modificación protegida por usuario

Las operaciones de:

- `updateActivity`
- `completeActivity`
- `rescheduleActivity`
- `softDeleteActivity`

incluyen en sus cláusulas `WHERE`:

`userId = :userId`

Además requieren el `activityId` correspondiente.

### HECHO VERIFICADO

Las modificaciones observadas no se realizan únicamente a partir del identificador de la actividad.

También requieren coincidencia con el usuario indicado.

## 79. Estados y estadísticas por usuario

Las operaciones destinadas a:

- marcar actividades vencidas;
- restaurar actividades pendientes;
- contar actividades completadas;
- obtener actividades completadas por periodo;
- agrupar actividades completadas por día;

incluyen explícitamente el `userId` en sus consultas SQL.

## 80. Cadena de aislamiento observada

La evidencia revisada permite confirmar la siguiente cadena:

`SessionManager → ActivitiesViewModel → ActivityRepository → ActivityDao → Room`

El `userId` obtenido desde la sesión activa es utilizado en el ViewModel, transmitido al Repository y finalmente utilizado en las consultas SQL de Room.

## 81. Estado del aislamiento del módulo de actividades

### HECHO VERIFICADO

A nivel de código, el módulo de actividades implementa filtrado por usuario en:

- obtención de sesión;
- ViewModel;
- Repository;
- DAO;
- consultas SQL.

### EVIDENCIA PENDIENTE

Todavía debe ejecutarse una prueba funcional con al menos dos usuarios para comprobar el comportamiento real de aislamiento durante la ejecución de la aplicación.

Esta prueba permitirá contrastar la implementación observada con el comportamiento ejecutable.

## 82. Composición del dashboard principal

### Evidencia revisada

`MainViewModel.kt`

### HECHOS VERIFICADOS

`MainViewModel` depende de:

- `UserRepository`
- `ActivityRepository`
- `PomodoroRepository`
- `SessionManager`

El ViewModel construye el estado principal del dashboard a partir de información proveniente de varios repositorios.

## 83. Validación de sesión en el dashboard

Al cargar el dashboard se consulta:

`sessionManager.sessionState.first()`

Después se obtiene:

`session.userId`

Si el identificador es nulo, el estado cambia a:

`MainUiState.NoActiveSession`

y la carga del dashboard no continúa.

## 84. Consulta del usuario activo

Cuando existe una sesión activa, el usuario se consulta mediante:

`userRepository.getUserById(userId)`

Si no existe un usuario asociado al identificador de sesión, el estado cambia a:

`MainUiState.UserNotFound`.

## 85. Información utilizada por el dashboard

El dashboard combina información de:

### Actividades

- Actividades correspondientes al día actual.
- Días en los que se completaron actividades.

### Pomodoro

- Días con sesiones Focus completadas.
- Cantidad de Pomodoros Focus completados durante el día actual.
- Segundos de concentración acumulados durante el día actual.

### Usuario

- Nombre.
- Correo electrónico.
- Semestre.

## 86. Integración reactiva de datos

La información se combina utilizando `combine`.

El dashboard observa simultáneamente:

- `activityRepository.observeActivitiesByDate(...)`
- `activityRepository.observeCompletedDays(...)`
- `pomodoroRepository.observeCompletedFocusDays(...)`
- `pomodoroRepository.observeCompletedFocusCountBetween(...)`
- `pomodoroRepository.observeCompletedFocusSecondsBetween(...)`

Esto permite actualizar el estado del dashboard cuando cambian los datos observados.

## 87. Cálculo de actividades del día

El ViewModel calcula:

- Total de actividades del día.
- Cantidad de actividades completadas durante el día.

El cálculo se realiza a partir de las actividades obtenidas para el usuario activo y la fecha actual.

## 88. Cálculo de rachas

### HECHO VERIFICADO

Los días válidos para la racha se construyen combinando:

- días con actividades completadas;
- días con sesiones Pomodoro Focus completadas.

Después se eliminan duplicados mediante:

`distinct()`

y se ordenan los días.

El cálculo final se delega a:

`StreakCalculator.calculate(...)`

Por lo tanto, actividad y Pomodoro realizados en el mismo día se consideran dentro del mismo conjunto de días válidos.

## 89. Estado del dashboard

Cuando la carga es exitosa, `MainUiState.Success` contiene:

- `userId`
- `fullName`
- `email`
- `semester`
- `totalActivitiesToday`
- `completedActivitiesToday`
- `todayActivities`
- `currentStreakDays`
- `bestStreakDays`
- `todayCompletedPomodoros`
- `todayFocusSeconds`

## 90. Relaciones arquitectónicas observadas

La evidencia permite identificar la siguiente relación:

`SessionManager → MainViewModel`

`UserRepository → MainViewModel`

`ActivityRepository → MainViewModel`

`PomodoroRepository → MainViewModel`

`StreakCalculator → MainViewModel`

El dashboard funciona como un punto de composición de información proveniente de diferentes componentes de la aplicación.

## 91. Estado parcial de la arquitectura del dashboard

### HECHOS VERIFICADOS

- El dashboard requiere una sesión activa.
- La información se consulta utilizando el usuario de la sesión.
- El dashboard integra actividades y Pomodoro.
- El cálculo de rachas está separado en `StreakCalculator`.
- El estado se expone mediante `StateFlow`.

### EVIDENCIA PENDIENTE

Todavía debe revisarse:

- `PomodoroRepository.kt`
- `StreakCalculator.kt`

para documentar cómo se obtienen las sesiones Pomodoro y cómo se calcula exactamente la racha.

## 92. Cálculo de rachas

### Evidencia revisada

`StreakCalculator.kt`

### HECHOS VERIFICADOS

El cálculo de rachas se encuentra separado de los ViewModels dentro del componente:

`StreakCalculator`

El cálculo recibe:

- una lista de días completados;
- el día actual en formato `epochDay`.

El resultado se representa mediante:

`StreakResult`

con los valores:

- `current`
- `best`

## 93. Normalización de días válidos

Antes de calcular la racha, los días recibidos son:

- filtrados para excluir fechas posteriores al día actual;
- deduplicados mediante `distinct()`;
- ordenados cronológicamente.

Por lo tanto, varios eventos realizados durante el mismo día no incrementan varias veces la racha.

## 94. Mejor racha

La mejor racha se calcula recorriendo los días válidos en orden.

Cuando dos días son consecutivos, la racha en ejecución aumenta en uno.

Cuando existe una interrupción entre fechas, la racha en ejecución vuelve a uno.

El mayor valor encontrado durante el recorrido se almacena como:

`bestStreak`

## 95. Racha actual

La racha actual puede terminar en:

- el día actual;
- el día inmediatamente anterior.

Si existe actividad válida hoy, el cálculo inicia desde el día actual.

Si hoy todavía no existe actividad válida, pero sí existe una correspondiente al día anterior, la racha continúa tomando como último día válido el día anterior.

Si no existe actividad válida ni hoy ni ayer, la racha actual se considera cero.

## 96. Conteo consecutivo hacia atrás

Una vez establecido el último día válido de la racha actual, el algoritmo retrocede día por día mientras las fechas sigan presentes en el conjunto de días válidos.

Cada día consecutivo aumenta:

`currentStreak`

## 97. Caso sin actividad válida

Cuando no existen días completados válidos, el resultado es:

- racha actual: `0`
- mejor racha: `0`

## 98. Relación con el dashboard

`MainViewModel` combina previamente:

- días con actividades completadas;
- días con sesiones Pomodoro Focus completadas.

Después elimina duplicados y entrega esos días a `StreakCalculator`.

Por lo tanto, el cálculo de racha está separado en dos responsabilidades:

1. Los repositorios proporcionan los días que califican.
2. `StreakCalculator` determina la racha actual y la mejor racha.

## 99. Regla observada de continuidad

### HECHO VERIFICADO

La implementación permite conservar la racha durante el día actual aunque el usuario todavía no haya realizado una actividad válida hoy, siempre que exista continuidad hasta el día anterior.

La racha pasa a cero cuando el último día válido es anterior a ayer.

## 100. Repositorio del módulo Pomodoro

### Evidencia revisada

`PomodoroRepository.kt`

### HECHOS VERIFICADOS

`PomodoroRepository` depende de:

- `PomodoroSessionDao`
- `ActivityDao`

El repositorio gestiona:

- Consulta de sesión activa.
- Consulta de sesiones.
- Inicio de sesión Pomodoro.
- Pausa.
- Reanudación.
- Finalización.
- Cancelación.
- Cálculo del tiempo restante.
- Conteo de sesiones Focus completadas.
- Estadísticas de tiempo de enfoque.
- Estadísticas agrupadas por periodo y por día.

## 101. Aislamiento por usuario en Pomodoro

Las operaciones principales reciben explícitamente un:

`userId`

Entre ellas:

- `observeActiveSession(userId)`
- `getActiveSession(userId)`
- `observeSessions(userId)`
- `observeCompletedFocusDays(userId)`
- `startSession(userId, ...)`
- `pauseSession(sessionId, userId)`
- `resumeSession(sessionId, userId)`
- `completeSession(sessionId, userId)`
- `cancelSession(sessionId, userId)`

Las estadísticas también se consultan utilizando el `userId`.

## 102. Inicio de sesión Pomodoro

Antes de iniciar una sesión se valida:

- que `userId` sea válido;
- que la duración sea mayor que cero;
- que el tipo de sesión sea válido;
- que no exista otra sesión activa del mismo usuario.

Los tipos aceptados son:

- `FOCUS`
- `SHORT_BREAK`
- `LONG_BREAK`

## 103. Asociación opcional con una actividad

Una sesión Pomodoro puede estar asociada a una actividad mediante `activityId`.

Cuando existe un `activityId`, el repositorio consulta:

`activityDao.getActivityById(activityId, userId)`

### HECHO VERIFICADO

La actividad asociada al Pomodoro debe ser recuperable utilizando simultáneamente:

- `activityId`
- `userId`

Si la actividad no existe o está eliminada, la sesión no se inicia.

## 104. Persistencia de sesiones

Al iniciar una sesión se crea una `PomodoroSessionEntity` con:

- `userId`
- `activityId`
- tipo de sesión
- duración planificada
- estado
- fecha de inicio
- fecha de creación
- fecha de actualización

El estado inicial es:

`RUNNING`

## 105. Estados de sesión

La evidencia muestra operaciones relacionadas con los estados:

- `RUNNING`
- `PAUSED`
- `COMPLETED`
- `CANCELLED`

La transición efectiva entre estados se delega al `PomodoroSessionDao`.

## 106. Cálculo del tiempo restante

El tiempo restante se calcula a partir de:

- duración planificada;
- momento de inicio;
- tiempo total pausado;
- instante actual;
- instante de pausa cuando la sesión se encuentra pausada.

El cálculo utiliza marcas de tiempo y no depende únicamente de una cuenta regresiva mantenida en la interfaz.

## 107. Finalización de una sesión

Antes de completar una sesión, el repositorio verifica:

1. Que la sesión exista para el `sessionId` y `userId`.
2. Que su estado sea `RUNNING`.
3. Que el tiempo restante sea igual a cero.

Si todavía queda tiempo, se retorna:

`TimeRemaining`

Si la sesión no pertenece al usuario indicado o no existe, se retorna:

`NotFoundOrNotAllowed`

## 108. Fecha de finalización

Cuando una sesión se completa, se calcula el día correspondiente utilizando:

- `Instant`
- `ZoneId.systemDefault()`
- fecha local del dispositivo

El resultado se almacena como:

`completedDateEpochDay`

## 109. Estadísticas de Pomodoro

El repositorio permite consultar por usuario:

- cantidad total de sesiones Focus completadas;
- segundos totales de enfoque;
- cantidad de sesiones Focus dentro de un periodo;
- segundos de enfoque dentro de un periodo;
- estadísticas de Pomodoro agrupadas por día;
- días con sesiones Focus completadas.

Estos datos son utilizados por otros componentes, como el dashboard y el módulo de progreso.

## 110. Relación arquitectónica observada

La evidencia permite identificar la siguiente cadena:

`ViewModel → PomodoroRepository → PomodoroSessionDao → Room`

También existe una relación con:

`ActivityDao`

cuando una sesión Pomodoro se vincula con una actividad.

## 111. Estado parcial del módulo Pomodoro

### HECHOS VERIFICADOS

- El módulo persiste sesiones por usuario.
- El repositorio utiliza `userId` en las operaciones principales.
- Una sesión opcionalmente puede estar asociada a una actividad.
- Existe control de sesión activa.
- El tiempo restante se calcula mediante marcas de tiempo.
- Las sesiones Focus completadas alimentan estadísticas y rachas.

### EVIDENCIA TODAVÍA PENDIENTE

Falta revisar:

- `PomodoroSessionDao.kt`
- `PomodoroViewModel.kt`

para confirmar:

- cómo se implementan las consultas SQL;
- de dónde obtiene el PomodoroViewModel el usuario activo;
- cómo se gestionan las transiciones de estado desde la interfaz.

## 112. Persistencia del módulo Pomodoro

### Evidencia revisada

`PomodoroSessionDao.kt`

### HECHOS VERIFICADOS

La aplicación utiliza `PomodoroSessionDao` como DAO de Room para acceder a la tabla:

`pomodoro_sessions`

El DAO permite:

- Insertar sesiones.
- Consultar una sesión por identificador y usuario.
- Consultar la sesión activa.
- Observar la sesión activa.
- Pausar sesiones.
- Reanudar sesiones.
- Completar sesiones.
- Cancelar sesiones.
- Consultar historial de sesiones.
- Consultar sesiones Focus completadas.
- Consultar días con sesiones Focus completadas.
- Consultar cantidades y tiempo de enfoque.
- Consultar estadísticas dentro de periodos.
- Agrupar estadísticas por día.

## 113. Filtrado por usuario en SQL

### HECHO VERIFICADO

Las consultas principales sobre `pomodoro_sessions` incluyen explícitamente:

`userId = :userId`

Este filtro aparece en operaciones de:

- Consulta individual.
- Consulta de sesión activa.
- Pausa.
- Reanudación.
- Finalización.
- Cancelación.
- Historial de sesiones.
- Sesiones Focus completadas.
- Días completados.
- Conteos.
- Tiempo de enfoque.
- Estadísticas por periodo.
- Estadísticas agrupadas por día.

## 114. Consulta individual de sesión

La operación:

`getSessionById(sessionId, userId)`

requiere coincidencia simultánea de:

- `id`
- `userId`

Por lo tanto, una sesión no se consulta únicamente mediante su identificador.

## 115. Sesión activa

La sesión activa se define como una sesión perteneciente al usuario cuyo estado sea:

- `RUNNING`
- `PAUSED`

La consulta ordena por `startedAtMillis` y limita el resultado a una sola sesión.

## 116. Transición a PAUSED

La operación `pauseSession(...)` modifica una sesión únicamente cuando:

- coincide el `sessionId`;
- coincide el `userId`;
- el estado actual es `RUNNING`.

El nuevo estado es:

`PAUSED`

También se registra:

`pausedAtMillis`

## 117. Reanudación

La operación `resumeSession(...)` modifica únicamente sesiones que:

- coinciden con `sessionId`;
- coinciden con `userId`;
- se encuentran en estado `PAUSED`;
- poseen un valor de `pausedAtMillis`.

Al reanudar:

- el estado vuelve a `RUNNING`;
- el tiempo pausado se suma a `totalPausedMillis`;
- `pausedAtMillis` vuelve a `NULL`.

## 118. Finalización

La operación `completeSession(...)` requiere:

- `sessionId`;
- `userId`;
- estado `RUNNING`.

La sesión pasa a:

`COMPLETED`

y almacena:

- instante de finalización;
- día de finalización;
- fecha de actualización.

## 119. Cancelación

La operación `cancelSession(...)` solamente modifica sesiones que:

- coinciden con `sessionId`;
- coinciden con `userId`;
- se encuentran en estado `RUNNING` o `PAUSED`.

El nuevo estado es:

`CANCELLED`

## 120. Estadísticas de enfoque

Las estadísticas consideran únicamente sesiones que cumplan:

- usuario correspondiente;
- tipo `FOCUS`;
- estado `COMPLETED`.

Se observaron consultas para:

- cantidad total;
- segundos totales;
- cantidad entre fechas;
- segundos entre fechas;
- agrupación diaria.

## 121. Agrupación diaria

`observeCompletedFocusStatsByDay(...)`

agrupa los resultados mediante:

`GROUP BY completedDateEpochDay`

y calcula:

- cantidad de Pomodoros;
- suma de segundos planificados de enfoque.

## 122. Estado del aislamiento del módulo Pomodoro

### HECHO VERIFICADO

El filtrado por usuario está presente en:

- Repository.
- DAO.
- Consultas SQL.

Las operaciones de lectura y modificación observadas requieren un `userId`.

### EVIDENCIA TODAVÍA PENDIENTE

Falta revisar `PomodoroViewModel.kt` para verificar que el `userId` utilizado provenga efectivamente de la sesión activa.

## 123. ViewModel del módulo Pomodoro

### Evidencia revisada

`PomodoroViewModel.kt`

### HECHOS VERIFICADOS

`PomodoroViewModel` depende de:

- `PomodoroRepository`
- `SessionManager`
- `UserPreferencesManager`

El ViewModel administra:

- Inicio de sesiones Focus.
- Inicio de descansos cortos.
- Inicio de descansos largos.
- Pausa.
- Reanudación.
- Cancelación.
- Finalización automática.
- Estado del temporizador.
- Preferencias de duración.

## 124. Obtención del usuario activo

Durante la carga inicial se consulta:

`sessionManager.sessionState.first()`

Después se obtiene:

`session.userId`

Si el identificador es nulo, el estado cambia a:

`PomodoroUiState.NoActiveSession`

y el módulo no continúa con la carga del Pomodoro.

Cuando existe una sesión activa, el identificador se almacena internamente como:

`currentUserId`

## 125. Uso del usuario autenticado

El `userId` obtenido desde `SessionManager` se utiliza para:

- Observar la sesión Pomodoro activa.
- Iniciar sesiones.
- Pausar sesiones.
- Reanudar sesiones.
- Cancelar sesiones.
- Completar sesiones.
- Consultar cantidad de sesiones Focus completadas.
- Consultar las preferencias Pomodoro del usuario.

## 126. Preferencias de Pomodoro

El ViewModel observa las preferencias mediante:

`userPreferencesManager.observePomodoroPreferences(userId)`

Las preferencias utilizadas incluyen:

- duración Focus;
- duración de descanso corto;
- duración de descanso largo.

Las duraciones son utilizadas al iniciar cada tipo de sesión.

## 127. Inicio de sesiones

Para iniciar una sesión:

1. Debe existir un `currentUserId`.
2. El estado debe permitir iniciar una nueva sesión.
3. La duración debe ser mayor que cero.
4. Se llama a:

`pomodoroRepository.startSession(...)`

enviando el `userId` activo.

## 128. Temporizador

El temporizador utiliza un `Job` asociado a `viewModelScope`.

Durante una sesión activa, el tiempo restante se obtiene mediante:

`pomodoroRepository.calculateRemainingMillis(...)`

La interfaz se actualiza aproximadamente cada segundo mediante:

`delay(1000L)`

### HECHO VERIFICADO

El ViewModel no reduce manualmente una variable en un segundo por iteración.

El tiempo restante se vuelve a calcular utilizando la lógica del Repository y las marcas de tiempo persistidas.

## 129. Finalización automática

Cuando:

`remainingMillis <= 0`

y la sesión se encuentra en estado:

`RUNNING`

se ejecuta la finalización automática.

La finalización utiliza:

`pomodoroRepository.completeSession(sessionId, userId)`

## 130. Recomendación de descanso

Después de completar una sesión `FOCUS`, se consulta la cantidad total de sesiones Focus completadas.

Si la cantidad es múltiplo de 4, se recomienda:

`LONG_BREAK`

En caso contrario se recomienda:

`SHORT_BREAK`

Esta recomendación se expone mediante `PomodoroUiState.Completed`.

## 131. Estados de interfaz del Pomodoro

Los estados observados son:

- `Loading`
- `Idle`
- `NoActiveSession`
- `Active`
- `Completed`
- `Error`

Los estados de operación observados son:

- `Idle`
- `Starting`
- `Pausing`
- `Resuming`
- `Cancelling`
- `Error`

## 132. Cadena de aislamiento del módulo Pomodoro

### HECHO VERIFICADO

La evidencia revisada permite confirmar la siguiente cadena:

`SessionManager → PomodoroViewModel → PomodoroRepository → PomodoroSessionDao → Room`

El `userId` utilizado por el ViewModel proviene de la sesión activa.

El Repository y el DAO utilizan ese identificador en las operaciones principales.

## 133. Estado del aislamiento de Pomodoro

### HECHO VERIFICADO EN CÓDIGO

El módulo Pomodoro implementa filtrado por usuario en:

- sesión;
- ViewModel;
- Repository;
- DAO;
- consultas SQL.

### EVIDENCIA PENDIENTE

Todavía debe ejecutarse una prueba funcional con dos usuarios para comprobar el comportamiento observable durante la ejecución de la aplicación.

## 134. Gestión de preferencias del usuario

### Evidencia revisada

`UserPreferencesManager.kt`

### HECHOS VERIFICADOS

La aplicación utiliza Android DataStore Preferences para persistir preferencias de usuario.

El DataStore utilizado se llama:

`user_preferences`

Las preferencias observadas son:

- duración Focus;
- duración de descanso corto;
- duración de descanso largo;
- estado de notificaciones habilitadas.

## 135. Preferencias por usuario

### HECHO VERIFICADO

Las claves de preferencias incorporan explícitamente el identificador del usuario.

Ejemplos observados:

- `focus_minutes_<userId>`
- `short_break_minutes_<userId>`
- `long_break_minutes_<userId>`
- `notifications_enabled_<userId>`

Esto evidencia que las preferencias se almacenan de forma diferenciada por usuario.

## 136. Preferencias Pomodoro por defecto

Los valores por defecto observados son:

- Focus: `25` minutos.
- Descanso corto: `5` minutos.
- Descanso largo: `15` minutos.

## 137. Límites configurables

La implementación define los siguientes rangos:

### Focus

- mínimo: `1` minuto;
- máximo: `120` minutos.

### Descanso corto

- mínimo: `1` minuto;
- máximo: `60` minutos.

### Descanso largo

- mínimo: `1` minuto;
- máximo: `120` minutos.

Antes de guardar las preferencias, estos valores son validados mediante `require(...)`.

## 138. Preferencias de notificaciones

El estado de notificaciones se persiste mediante una preferencia booleana.

Cuando no existe un valor almacenado, el valor por defecto es:

`true`

La operación:

`saveNotificationsEnabled(userId, enabled)`

requiere un `userId` válido.

## 139. Relación arquitectónica observada

La evidencia permite identificar la siguiente relación:

`PomodoroViewModel → UserPreferencesManager → DataStore`

Además, las preferencias quedan asociadas al usuario mediante claves que incluyen el `userId`.

## 140. Estado parcial de preferencias

### HECHOS VERIFICADOS

- Las preferencias se almacenan localmente.
- Se utiliza DataStore.
- Las preferencias Pomodoro están asociadas al usuario.
- El estado de notificaciones también está asociado al usuario.

### EVIDENCIA TODAVÍA PENDIENTE

Falta revisar cómo el módulo de perfil modifica estas preferencias desde la interfaz.

## 141. Módulo de perfil

### Evidencia revisada

`ProfileViewModel.kt`

### HECHOS VERIFICADOS

`ProfileViewModel` depende de:

- `UserRepository`
- `UserPreferencesManager`
- `AchievementRepository`
- `ActivityRepository`
- `PomodoroRepository`
- `SessionManager`

El módulo integra información relacionada con:

- Datos personales del usuario.
- Preferencias Pomodoro.
- Preferencias de notificaciones.
- Logros.
- Actividades completadas.
- Pomodoros completados.
- Racha actual.

## 142. Obtención del usuario activo

Durante la carga del perfil se consulta:

`sessionManager.sessionState.first()`

Después se obtiene:

`session.userId`

Si no existe un usuario activo, el estado cambia a:

`ProfileUiState.NoActiveSession`

y no continúa la carga del perfil.

## 143. Consulta de información del usuario

Cuando existe una sesión activa, los datos personales se consultan mediante:

`userRepository.getUserById(userId)`

Los datos utilizados por el perfil incluyen:

- nombre;
- correo electrónico;
- semestre.

## 144. Edición de perfil

El módulo permite modificar:

- nombre;
- semestre.

Los cambios se envían a:

`userRepository.updateProfile(...)`

utilizando el `userId` correspondiente al usuario activo.

## 145. Gestión de preferencias

El perfil permite modificar:

- duración Focus;
- descanso corto;
- descanso largo;
- estado de notificaciones.

Las preferencias se almacenan mediante:

`UserPreferencesManager`

utilizando el `userId` del usuario activo.

## 146. Integración de información del perfil

El perfil combina mediante `combine` información proveniente de:

- preferencias del usuario;
- logros;
- actividades completadas;
- Pomodoros Focus completados;
- días con actividades completadas;
- días con Pomodoros completados.

## 147. Racha dentro del perfil

La racha se calcula utilizando:

`StreakCalculator`

Los días válidos se construyen combinando:

- días con actividades completadas;
- días con sesiones Pomodoro Focus completadas.

Después se eliminan duplicados antes de calcular la racha.

## 148. Logros

### HECHO VERIFICADO

El perfil observa los logros del usuario mediante:

`achievementRepository.observeAchievements(userId)`

También ejecuta:

`achievementRepository.syncAchievements(...)`

utilizando como entrada:

- cantidad de actividades completadas;
- cantidad de Pomodoros Focus completados;
- racha actual.

Los tipos de logro disponibles se obtienen mediante:

`AchievementEngine.allTypes`

### EVIDENCIA PENDIENTE

Todavía debe revisarse:

- `AchievementRepository.kt`
- `AchievementEngine.kt`

para documentar cuáles son las reglas reales de desbloqueo.

## 149. Estados del perfil

Los estados principales observados son:

- `Loading`
- `Success`
- `NoActiveSession`
- `Error`

Los estados relacionados con guardado son:

- `Idle`
- `Saving`
- `Success`
- `Error`

## 150. Relación arquitectónica del módulo de perfil

La evidencia permite identificar:

`SessionManager → ProfileViewModel`

`UserRepository → ProfileViewModel`

`UserPreferencesManager → ProfileViewModel`

`AchievementRepository → ProfileViewModel`

`ActivityRepository → ProfileViewModel`

`PomodoroRepository → ProfileViewModel`

El perfil actúa como un punto de integración de información proveniente de diferentes módulos del sistema.

## 151. Aislamiento por usuario

### HECHO VERIFICADO EN ESTE NIVEL

El `userId` utilizado por el perfil se obtiene directamente desde la sesión activa.

Ese identificador se utiliza para:

- consultar usuario;
- modificar perfil;
- consultar preferencias;
- guardar preferencias;
- consultar logros;
- sincronizar logros;
- consultar estadísticas de actividades;
- consultar estadísticas Pomodoro.

## 152. Repositorio de logros

### Evidencia revisada

`AchievementRepository.kt`

### HECHOS VERIFICADOS

`AchievementRepository` depende de:

- `AchievementDao`
- `AchievementEngine`

El repositorio tiene dos responsabilidades observadas:

- consultar los logros de un usuario;
- sincronizar los logros que deben desbloquearse.

## 153. Consulta de logros por usuario

Los logros se consultan mediante:

`achievementDao.observeAchievements(userId)`

Por lo tanto, el Repository utiliza explícitamente el `userId` para obtener los logros correspondientes a un usuario.

## 154. Evaluación de logros

Para determinar qué logros deben desbloquearse, el Repository delega la evaluación a:

`AchievementEngine.typesToUnlock(input)`

El Repository no contiene directamente las reglas que determinan cuándo se obtiene cada logro.

### EVIDENCIA PENDIENTE

Las condiciones exactas de desbloqueo deben verificarse en:

`AchievementEngine.kt`

## 155. Persistencia de nuevos logros

Por cada tipo de logro que debe desbloquearse, el Repository consulta:

`achievementDao.getAchievement(userId, type)`

Si el logro todavía no existe, se inserta un nuevo:

`AchievementEntity`

con:

- `userId`
- tipo de logro
- instante de desbloqueo

## 156. Prevención de inserciones repetidas

### HECHO VERIFICADO

Antes de insertar un logro, el Repository comprueba si ya existe para la combinación:

- usuario;
- tipo de logro.

Si ya existe, no vuelve a insertarlo desde esta operación.

## 157. Relación arquitectónica de logros

La evidencia permite identificar la siguiente relación:

`ProfileViewModel → AchievementRepository → AchievementEngine`

y para persistencia:

`AchievementRepository → AchievementDao → Room`

## 158. Aislamiento por usuario en logros

### HECHO VERIFICADO EN EL REPOSITORY

El `userId` se utiliza tanto para:

- observar los logros;
- consultar si un logro ya existe;
- insertar un nuevo logro.

### EVIDENCIA PENDIENTE

Todavía debe revisarse `AchievementDao.kt` para confirmar que las consultas SQL también filtran por usuario.

## 159. Motor de evaluación de logros

### Evidencia revisada

`AchievementEngine.kt`

### HECHOS VERIFICADOS

Las reglas de desbloqueo de logros están centralizadas en:

`AchievementEngine`

El motor recibe un:

`AchievementCheckInput`

que contiene:

- cantidad de actividades completadas;
- cantidad de Pomodoros Focus completados;
- cantidad de días de la racha actual.

## 160. Reglas de desbloqueo observadas

La implementación actual define los siguientes logros:

| Logro | Condición observada |
|---|---|
| Primera actividad | Al menos 1 actividad completada |
| Primer Pomodoro | Al menos 1 Pomodoro Focus completado |
| Racha de 3 días | Racha actual de al menos 3 días |
| Racha de 7 días | Racha actual de al menos 7 días |
| 10 actividades | Al menos 10 actividades completadas |
| 10 Pomodoros | Al menos 10 Pomodoros Focus completados |

Estas condiciones corresponden a la implementación actual observada en código.

## 161. Evaluación acumulativa

### HECHO VERIFICADO

Las condiciones se evalúan de manera independiente.

Por ejemplo, si un usuario posee una racha actual de 7 días, el motor puede devolver simultáneamente los tipos correspondientes a:

- racha de 3 días;
- racha de 7 días.

El Repository posteriormente evita volver a insertar logros ya existentes.

## 162. Presentación de logros

`AchievementEngine` también proporciona:

- título asociado a cada tipo de logro;
- descripción asociada a cada tipo de logro.

Esta información se determina mediante:

- `titleFor(type)`
- `descriptionFor(type)`

## 163. Catálogo de logros

La propiedad:

`AchievementEngine.allTypes`

contiene los tipos de logro definidos actualmente.

Se observaron seis tipos:

1. Primera actividad completada.
2. Primer Pomodoro Focus.
3. Racha de 3 días.
4. Racha de 7 días.
5. 10 actividades completadas.
6. 10 Pomodoros Focus completados.

## 164. Separación de responsabilidades

### HECHO VERIFICADO

La evaluación de las condiciones de logro está separada de:

- `ProfileViewModel`;
- `AchievementRepository`;
- persistencia Room.

La relación observada es:

`ProfileViewModel → AchievementRepository → AchievementEngine`

El motor determina qué tipos cumplen las condiciones y el Repository se encarga de consultar e insertar los logros correspondientes.

## 165. Estado del módulo de logros

### HECHOS VERIFICADOS

- Existen reglas explícitas para desbloquear logros.
- Las reglas utilizan actividades, Pomodoro y racha.
- El catálogo actual contiene seis tipos.
- Las reglas se encuentran centralizadas en un componente de dominio.
- El Repository evita insertar nuevamente un logro ya existente.

### EVIDENCIA PENDIENTE

Falta confirmar en `AchievementDao.kt` que la persistencia de logros también aplique el filtrado por `userId` directamente en las consultas SQL.

## 166. Persistencia de logros

### Evidencia revisada

`AchievementDao.kt`

### HECHOS VERIFICADOS

`AchievementDao` es el componente de Room encargado de acceder a la tabla:

`achievements`

Las operaciones observadas permiten:

- consultar los logros de un usuario;
- consultar un logro específico por usuario y tipo;
- insertar un logro.

## 167. Consulta de logros por usuario

La consulta:

`observeAchievements(userId)`

incluye explícitamente:

`WHERE userId = :userId`

Por lo tanto, los logros recuperados desde Room se filtran directamente por usuario.

Los resultados se ordenan utilizando:

`unlockedAt ASC`

## 168. Consulta de logro específico

La operación:

`getAchievement(userId, type)`

requiere coincidencia simultánea de:

- `userId`;
- `type`.

La consulta limita el resultado a un solo registro.

Esta operación es utilizada por `AchievementRepository` antes de insertar un logro.

## 169. Inserción de logros

La inserción utiliza:

`OnConflictStrategy.IGNORE`

### HECHO VERIFICADO

Room está configurado para ignorar una inserción cuando esta produce un conflicto definido por el esquema de la tabla.

### EVIDENCIA PENDIENTE

A partir únicamente de este DAO no puede afirmarse qué combinación de campos genera dicho conflicto.

Para determinarlo sería necesario revisar la definición de `AchievementEntity`.

## 170. Cadena arquitectónica completa de logros

### HECHO VERIFICADO

La evidencia revisada permite establecer la siguiente cadena:

`ProfileViewModel → AchievementRepository → AchievementDao → Room`

Para la evaluación de reglas también interviene:

`AchievementRepository → AchievementEngine`

## 171. Aislamiento por usuario en logros

### HECHO VERIFICADO EN CÓDIGO

El filtrado por usuario se encuentra presente en:

- `ProfileViewModel`, que obtiene el `userId` desde la sesión activa;
- `AchievementRepository`, que recibe y propaga el `userId`;
- `AchievementDao`, cuyas consultas incluyen `userId`.

Por lo tanto, la cadena observada es:

`SessionManager → ProfileViewModel → AchievementRepository → AchievementDao → Room`

### EVIDENCIA PENDIENTE

Aunque el aislamiento está implementado en código, todavía debe realizarse una prueba funcional con dos usuarios para comprobar el comportamiento durante la ejecución.

## 172. Repositorio de recordatorios

### Evidencia revisada

`ReminderRepository.kt`

### HECHOS VERIFICADOS

`ReminderRepository` depende de:

- `ReminderDao`
- `ActivityDao`

El repositorio permite:

- observar recordatorios;
- observar recordatorios asociados a una actividad;
- crear recordatorios;
- consultar un recordatorio específico;
- consultar recordatorios programados;
- cancelar recordatorios;
- marcar recordatorios como entregados.

## 173. Consulta de recordatorios por usuario

Las operaciones de consulta reciben explícitamente un:

`userId`

Entre ellas:

- `observeReminders(userId)`
- `observeRemindersByActivity(userId, activityId)`
- `getReminderById(reminderId, userId)`
- `getScheduledReminders(userId)`

El Repository propaga este identificador hacia `ReminderDao`.

## 174. Creación de recordatorios

Para crear un recordatorio se reciben:

- `userId`;
- `activityId` opcional;
- título;
- mensaje;
- fecha y hora de ejecución.

Antes de insertar el recordatorio se valida que:

- el título no esté vacío;
- la fecha del recordatorio sea posterior al momento actual.

## 175. Asociación opcional con actividades

Un recordatorio puede estar asociado a una actividad mediante:

`activityId`

Cuando existe esta asociación, el Repository consulta:

`activityDao.getActivityById(activityId, userId)`

Si la actividad:

- no existe;
- no corresponde al usuario indicado;
- o se encuentra eliminada;

el recordatorio no se crea y se retorna:

`ActivityNotFoundOrNotAllowed`

## 176. Persistencia del recordatorio

Cuando las validaciones son satisfactorias se crea un:

`ReminderEntity`

con información que incluye:

- `userId`;
- `activityId`;
- título;
- mensaje;
- fecha y hora de ejecución;
- fecha de creación;
- fecha de actualización.

La inserción se delega a:

`ReminderDao`

## 177. Cancelación de recordatorios

La operación:

`cancelReminder(reminderId, userId)`

envía simultáneamente al DAO:

- identificador del recordatorio;
- identificador del usuario;
- fecha de actualización.

El resultado depende de la cantidad de filas modificadas.

Si no se modifica ninguna fila se retorna:

`NotFoundOrNotAllowed`

## 178. Registro de entrega

La operación:

`markReminderDelivered(reminderId, userId)`

registra:

- identificador del recordatorio;
- identificador del usuario;
- instante de entrega;
- fecha de actualización.

La modificación efectiva del estado se encuentra delegada al `ReminderDao`.

## 179. Relación entre actividades y recordatorios

### HECHO VERIFICADO

Cuando un recordatorio está vinculado con una actividad, la validación utiliza simultáneamente:

`activityId + userId`

Por lo tanto, el Repository verifica que la actividad pueda ser obtenida para el mismo usuario antes de crear el recordatorio.

## 180. Relación arquitectónica observada

La evidencia permite identificar:

`ReminderViewModel → ReminderRepository → ReminderDao → Room`

y, cuando existe una actividad asociada:

`ReminderRepository → ActivityDao`

### EVIDENCIA PENDIENTE

La primera relación todavía debe completarse mediante la revisión de `ReminderViewModel.kt`.

## 181. Estado parcial del aislamiento de recordatorios

### HECHO VERIFICADO EN EL REPOSITORY

El `userId` se utiliza en las operaciones principales de consulta y modificación del Repository.

También se utiliza para validar actividades asociadas.

### EVIDENCIA PENDIENTE

Todavía falta confirmar:

- que `ReminderDao` utilice `userId` directamente en sus consultas SQL;
- que `ReminderViewModel` obtenga el `userId` desde la sesión activa;
- cómo `ReminderScheduler` programa realmente las alarmas;
- cómo el sistema recibe y muestra la notificación.

## 182. Persistencia de recordatorios

### Evidencia revisada

`ReminderDao.kt`

### HECHOS VERIFICADOS

`ReminderDao` es el DAO de Room encargado de acceder a la tabla:

`reminders`

Las operaciones observadas permiten:

- insertar recordatorios;
- consultar recordatorios de un usuario;
- consultar recordatorios asociados a una actividad;
- consultar un recordatorio específico;
- consultar recordatorios programados;
- cancelar recordatorios;
- marcar recordatorios como entregados.

## 183. Filtrado por usuario en SQL

### HECHO VERIFICADO

Las consultas principales incluyen explícitamente:

`userId = :userId`

Este filtro aparece en:

- listado general de recordatorios;
- recordatorios por actividad;
- consulta individual;
- recordatorios programados;
- cancelación;
- marcado como entregado.

## 184. Consulta de recordatorios por actividad

La operación:

`observeRemindersByActivity(userId, activityId)`

requiere coincidencia simultánea de:

- `userId`;
- `activityId`.

Los resultados se ordenan por:

1. `triggerAtMillis`
2. `id`

## 185. Consulta individual

La operación:

`getReminderById(reminderId, userId)`

requiere coincidencia simultánea de:

- identificador del recordatorio;
- identificador del usuario.

La consulta limita el resultado a un registro.

## 186. Recordatorios programados

La operación:

`getScheduledReminders(userId)`

consulta únicamente registros que:

- pertenecen al usuario indicado;
- tienen estado `SCHEDULED`.

Los resultados se ordenan cronológicamente por la fecha de activación.

## 187. Cancelación de recordatorios

La operación:

`cancelReminder(...)`

solo modifica un registro cuando se cumplen simultáneamente:

- `id = reminderId`;
- `userId = userId`;
- `status = 'SCHEDULED'`.

Cuando se cumple la condición, el estado cambia a:

`CANCELLED`

## 188. Registro de entrega

La operación:

`markReminderDelivered(...)`

solo modifica recordatorios que:

- coinciden con el `reminderId`;
- pertenecen al `userId`;
- se encuentran en estado `SCHEDULED`.

Cuando se realiza la modificación:

- el estado cambia a `DELIVERED`;
- se almacena `deliveredAt`;
- se actualiza `updatedAt`.

## 189. Estados observados en recordatorios

A partir de las consultas revisadas se observan los estados:

- `SCHEDULED`
- `CANCELLED`
- `DELIVERED`

### EVIDENCIA PENDIENTE

Este DAO no permite determinar por sí solo todas las reglas mediante las cuales un recordatorio llega a cada estado fuera de las operaciones observadas.

## 190. Cadena de aislamiento del módulo de recordatorios

### HECHO VERIFICADO EN CÓDIGO

La evidencia revisada hasta este punto permite establecer:

`ReminderRepository → ReminderDao → Room`

El Repository propaga el `userId` y las consultas SQL del DAO vuelven a aplicar ese identificador.

Para recordatorios vinculados a actividades también existe:

`ReminderRepository → ActivityDao`

donde se valida la actividad utilizando `activityId + userId`.

## 191. Estado parcial del módulo de recordatorios

### HECHOS VERIFICADOS

- Los recordatorios se almacenan localmente mediante Room.
- Las consultas se filtran por usuario.
- Las modificaciones también requieren `userId`.
- Existen estados para programado, cancelado y entregado.
- Un recordatorio puede estar asociado a una actividad.

### EVIDENCIA PENDIENTE

Todavía falta comprobar:

- cómo se programa la alarma en Android;
- cómo se identifica el recordatorio cuando se dispara;
- cómo se construye la notificación;
- cómo se marca el recordatorio como entregado después de ejecutarse.

## 192. Programación de recordatorios en Android

### Evidencia revisada

`ReminderScheduler.kt`

### HECHOS VERIFICADOS

La programación de recordatorios utiliza:

`AlarmManager`

El componente responsable es:

`ReminderScheduler`

Este componente recibe un `Context` y obtiene el servicio del sistema mediante:

`context.getSystemService(AlarmManager::class.java)`

## 193. Validación de fecha de ejecución

Antes de programar un recordatorio se comprueba que:

`reminder.triggerAtMillis`

sea posterior al momento actual.

Si la fecha ya pasó, se retorna:

`ReminderScheduleResult.InvalidTime`

## 194. Uso de PendingIntent

Cada recordatorio genera un:

`PendingIntent`

de tipo broadcast.

El Intent está dirigido a:

`ReminderReceiver`

y transporta:

- `reminderId`
- `userId`

mediante extras.

## 195. Identificación de la alarma

El código de solicitud utilizado por el `PendingIntent` se deriva del identificador del recordatorio mediante:

`reminderId.toAlarmRequestCode()`

Esto permite asociar la alarma programada con el recordatorio correspondiente.

## 196. Alarmas exactas

En versiones de Android anteriores a Android S, la implementación asume que puede programar la alarma exacta.

En Android S o superior se consulta:

`alarmManager.canScheduleExactAlarms()`

Si Android permite alarmas exactas se utiliza:

`setExactAndAllowWhileIdle(...)`

con:

`AlarmManager.RTC_WAKEUP`

## 197. Alarmas no exactas

Cuando no está permitido programar una alarma exacta, la aplicación utiliza:

`setAndAllowWhileIdle(...)`

El resultado se registra como:

`ScheduledInexact`

## 198. Manejo de restricciones de Android

Si la programación exacta produce una:

`SecurityException`

la implementación intenta nuevamente mediante una alarma no exacta.

Si tampoco es posible programarla, se retorna:

`ReminderScheduleResult.Error`

## 199. Ejecución durante modo idle

Tanto la programación exacta como la no exacta utilizan variantes:

- `setExactAndAllowWhileIdle`
- `setAndAllowWhileIdle`

### HECHO VERIFICADO

La implementación utiliza mecanismos de `AlarmManager` que permiten solicitar la ejecución incluso cuando el dispositivo se encuentra en modo idle.

Esto no demuestra por sí solo que Android garantice una ejecución exacta en todos los dispositivos y configuraciones.

## 200. Cancelación de alarmas

Para cancelar una alarma se reconstruye el mismo `PendingIntent` utilizando:

- `reminderId`
- `userId`

Después se ejecuta:

`alarmManager.cancel(pendingIntent)`

y posteriormente:

`pendingIntent.cancel()`

## 201. Flujo arquitectónico de programación

La evidencia permite establecer:

`ReminderRepository / ReminderViewModel → ReminderScheduler → AlarmManager`

Cuando llega el momento programado:

`AlarmManager → ReminderReceiver`

### EVIDENCIA PENDIENTE

Todavía debe revisarse `ReminderReceiver.kt` para determinar qué ocurre cuando Android dispara el broadcast.

## 202. Información transportada hacia el Receiver

### HECHO VERIFICADO

El broadcast enviado a `ReminderReceiver` contiene:

- identificador del recordatorio;
- identificador del usuario.

Esto permite que el componente receptor conozca qué recordatorio fue disparado y a qué usuario está asociado.

## 203. Resultados de programación

La implementación distingue cuatro resultados:

- `ScheduledExact`
- `ScheduledInexact`
- `InvalidTime`
- `Error`

Por lo tanto, el sistema diferencia entre una programación exacta y una programación no exacta.

## 204. Estado parcial del subsistema de notificaciones

### HECHOS VERIFICADOS

- Se utiliza `AlarmManager`.
- Se utiliza `PendingIntent` de tipo broadcast.
- El destino del broadcast es `ReminderReceiver`.
- Se transportan `reminderId` y `userId`.
- Existe soporte para alarmas exactas y no exactas.
- Existe cancelación de alarmas.
- La aplicación contempla restricciones de alarmas exactas en versiones recientes de Android.

### EVIDENCIA PENDIENTE

Todavía falta comprobar:

- cómo `ReminderReceiver` recupera el recordatorio;
- cómo se construye la notificación;
- cómo se marca el recordatorio como entregado.

## 205. Recepción de alarmas de recordatorio

### Evidencia revisada

`ReminderReceiver.kt`

### HECHOS VERIFICADOS

`ReminderReceiver` extiende:

`BroadcastReceiver`

El componente recibe el broadcast generado previamente por `ReminderScheduler`.

Del `Intent` recupera:

- `reminderId`
- `userId`

Si alguno de estos identificadores no es válido, el procesamiento termina.

## 206. Procesamiento asíncrono

El Receiver utiliza:

`goAsync()`

y ejecuta el procesamiento mediante una corrutina con:

`Dispatchers.IO`

Al finalizar el procesamiento se ejecuta:

`pendingResult.finish()`

## 207. Validación de sesión activa

Antes de mostrar la notificación se consulta:

`application.sessionManager.sessionState.first()`

Después se obtiene:

`session.userId`

### HECHO VERIFICADO

La notificación solamente continúa si:

- existe un usuario con sesión activa;
- el usuario activo coincide con el `userId` recibido en el recordatorio.

Si los identificadores no coinciden, la notificación no se muestra.

## 208. Recuperación del recordatorio

Una vez validado el usuario activo se consulta:

`reminderRepository.getReminderById(reminderId, activeUserId)`

Por lo tanto, la recuperación utiliza simultáneamente:

- `reminderId`;
- `userId`.

## 209. Validación del estado del recordatorio

Antes de generar la notificación se verifica que el recordatorio:

- exista;
- tenga estado `SCHEDULED`.

Si alguna de estas condiciones no se cumple, el procesamiento termina.

## 210. Permiso de notificaciones

En Android 13 o superior se comprueba el permiso:

`POST_NOTIFICATIONS`

Si el permiso no ha sido concedido, el Receiver no continúa con la creación de la notificación.

## 211. Construcción de la notificación

La notificación se construye mediante:

`NotificationCompat.Builder`

y utiliza el canal:

`NotificationChannels.REMINDERS_CHANNEL_ID`

La notificación contiene:

- título del recordatorio;
- mensaje del recordatorio;
- icono;
- categoría `CATEGORY_REMINDER`;
- acción para abrir la aplicación;
- cancelación automática al pulsarla.

Si el mensaje se encuentra vacío se utiliza:

`Tienes un recordatorio pendiente.`

## 212. Apertura de la aplicación

Al pulsar la notificación se genera un `PendingIntent` que abre:

`MainActivity`

El Intent utiliza las banderas:

- `FLAG_ACTIVITY_NEW_TASK`
- `FLAG_ACTIVITY_CLEAR_TOP`

## 213. Identificación de la notificación

El identificador utilizado por `NotificationManagerCompat` se deriva de:

`reminderId`

mediante:

`toNotificationRequestCode()`

Esto permite generar un identificador de notificación asociado al recordatorio.

## 214. Publicación de la notificación

La publicación se realiza mediante:

`NotificationManagerCompat.notify(...)`

Si se produce una `SecurityException`, el procesamiento termina sin continuar con el marcado de entrega.

## 215. Marcado como entregado

Después de publicar la notificación se ejecuta:

`reminderRepository.markReminderDelivered(reminderId, userId)`

Esta operación termina actualizando el estado persistido del recordatorio mediante el Repository y el DAO.

El flujo observado es:

`SCHEDULED → DELIVERED`

## 216. Cadena completa de ejecución del recordatorio

### HECHO VERIFICADO

La evidencia revisada permite establecer:

`ReminderScheduler`
→ `AlarmManager`
→ `PendingIntent`
→ `ReminderReceiver`
→ `ReminderRepository`
→ `ReminderDao`
→ `Room`

Para mostrar la notificación intervienen:

`ReminderReceiver`
→ `NotificationCompat`
→ `NotificationManagerCompat`

## 217. Relación con la sesión

### HECHO VERIFICADO

El recordatorio transporta el `userId` desde su programación.

Cuando se dispara la alarma, el Receiver vuelve a comparar ese identificador con el usuario que tiene una sesión activa.

Esto introduce una segunda validación del usuario antes de consultar y mostrar el recordatorio.

## 218. Estado actual del subsistema de recordatorios

### HECHOS VERIFICADOS

- Los recordatorios se almacenan mediante Room.
- Las consultas se filtran por usuario.
- La programación utiliza `AlarmManager`.
- Se utilizan `PendingIntent` y `BroadcastReceiver`.
- El Receiver valida nuevamente al usuario activo.
- Se comprueba el permiso de notificaciones en Android 13 o superior.
- La notificación se genera mediante `NotificationCompat`.
- El recordatorio se marca como `DELIVERED` después de publicar la notificación.

### EVIDENCIA PENDIENTE

Todavía falta comprobar:

- cómo se crea el canal de notificaciones;
- cómo se recuperan o reprograman alarmas después de reiniciar el dispositivo.

## 219. Canal de notificaciones

### Evidencia revisada

`NotificationChannels.kt`

### HECHOS VERIFICADOS

La aplicación define un canal de notificaciones específico para recordatorios mediante:

`NotificationChannels`

El identificador del canal es:

`racha_pro_reminders`

El nombre visible definido es:

`Recordatorios`

y su descripción es:

`Recordatorios de actividades y tareas de RachaPro`

## 220. Compatibilidad con versiones de Android

El canal se crea únicamente cuando la versión de Android es:

`Android O (API 26)` o superior.

La comprobación se realiza mediante:

`Build.VERSION.SDK_INT >= Build.VERSION_CODES.O`

Esto corresponde al requisito de Android de utilizar canales de notificación desde esa versión.

## 221. Importancia del canal

El canal se configura con:

`NotificationManager.IMPORTANCE_DEFAULT`

Por lo tanto, los recordatorios utilizan la importancia predeterminada definida por Android para este canal.

## 222. Registro del canal

La creación se realiza mediante:

`NotificationManager.createNotificationChannel(...)`

El componente obtiene `NotificationManager` desde el `Context` de Android.

## 223. Relación con ReminderReceiver

### HECHO VERIFICADO

`ReminderReceiver` construye sus notificaciones utilizando:

`NotificationChannels.REMINDERS_CHANNEL_ID`

Por lo tanto, existe la siguiente relación:

`NotificationChannels → canal racha_pro_reminders`

y:

`ReminderReceiver → NotificationCompat.Builder → racha_pro_reminders`

## 224. Momento de inicialización

### HECHO VERIFICADO

Según la evidencia revisada previamente en `RachaProApplication.kt`, durante:

`Application.onCreate()`

se ejecuta:

`NotificationChannels.createNotificationChannels(this)`

Por lo tanto, la creación del canal forma parte de la inicialización de la aplicación.

## 225. Flujo observado del sistema de notificaciones

La evidencia revisada permite establecer:

`RachaProApplication`
→ `NotificationChannels`
→ `NotificationManager`

para la creación del canal.

Y para la entrega:

`AlarmManager`
→ `ReminderReceiver`
→ `NotificationCompat`
→ `NotificationManagerCompat`
→ canal `racha_pro_reminders`

## 226. Estado del canal de notificaciones

### HECHOS VERIFICADOS

- Existe un canal específico para recordatorios.
- El canal se registra mediante `NotificationManager`.
- Se crea en Android O o superior.
- Tiene importancia `DEFAULT`.
- `ReminderReceiver` utiliza ese mismo identificador de canal.
- La creación se ejecuta durante la inicialización de la aplicación.

## 227. Recuperación de recordatorios después del reinicio

### Evidencia revisada

`BootReceiver.kt`

### HECHOS VERIFICADOS

`BootReceiver` extiende:

`BroadcastReceiver`

El componente únicamente continúa su ejecución cuando recibe la acción:

`Intent.ACTION_BOOT_COMPLETED`

Por lo tanto, su lógica está diseñada para ejecutarse después de que Android complete el arranque del dispositivo.

## 228. Procesamiento asíncrono después del arranque

El Receiver utiliza:

`goAsync()`

y ejecuta su trabajo mediante una corrutina con:

`Dispatchers.IO`

Al terminar se ejecuta:

`pendingResult.finish()`

## 229. Recuperación del usuario activo

Después del arranque se consulta:

`sessionManager.sessionState.first()`

y posteriormente:

`session.userId`

Si no existe una sesión activa, el Receiver termina sin recuperar recordatorios.

## 230. Recuperación de recordatorios programados

Cuando existe un usuario activo se ejecuta:

`reminderRepository.getScheduledReminders(userId)`

Por lo tanto, la recuperación se realiza únicamente para los recordatorios en estado `SCHEDULED` correspondientes al usuario de la sesión.

## 231. Reprogramación de recordatorios futuros

Cada recordatorio recuperado se compara con:

`System.currentTimeMillis()`

Si:

`triggerAtMillis > currentTime`

el recordatorio se vuelve a programar mediante:

`reminderScheduler.schedule(reminder)`

## 232. Tratamiento de recordatorios vencidos

Si el momento de activación del recordatorio ya pasó, el Receiver ejecuta:

`reminderRepository.cancelReminder(reminderId, userId)`

Por lo tanto, un recordatorio programado que ya venció durante el reinicio no se vuelve a programar desde esta lógica.

## 233. Flujo de recuperación después del reinicio

### HECHO VERIFICADO

La lógica observada corresponde a:

`BOOT_COMPLETED`
→ `BootReceiver`
→ `SessionManager`
→ `ReminderRepository`
→ recordatorios SCHEDULED

Después:

- recordatorio futuro → `ReminderScheduler → AlarmManager`
- recordatorio vencido → `ReminderRepository → CANCELLED`

## 234. Relación con el aislamiento por usuario

### HECHO VERIFICADO EN CÓDIGO

`BootReceiver` obtiene el `userId` directamente desde la sesión activa.

Ese identificador se utiliza posteriormente para consultar y cancelar recordatorios.

Por lo tanto, la recuperación posterior al reinicio también trabaja sobre el usuario activo.

## 235. Estado del subsistema de recordatorios y notificaciones

### HECHOS VERIFICADOS EN CÓDIGO

El subsistema implementado incluye:

- Persistencia local mediante Room.
- Filtrado de recordatorios por usuario.
- Asociación opcional con actividades.
- Programación mediante `AlarmManager`.
- Alarmas exactas y no exactas.
- `PendingIntent` de tipo broadcast.
- Recepción mediante `ReminderReceiver`.
- Validación del usuario activo antes de mostrar la notificación.
- Validación del permiso de notificaciones en Android 13 o superior.
- Generación de notificaciones mediante `NotificationCompat`.
- Canal específico de recordatorios.
- Estado `DELIVERED` después de publicar una notificación.
- Recuperación de recordatorios programados después de un reinicio.
- Cancelación de recordatorios vencidos durante dicha recuperación.

### EVIDENCIA FALTANTE

Todavía no se ha revisado `AndroidManifest.xml`.

Por lo tanto, todavía debe comprobarse documentalmente:

- el registro de `BootReceiver`;
- el registro de `ReminderReceiver`, cuando corresponda;
- la declaración del permiso necesario para recibir `BOOT_COMPLETED`;
- las declaraciones de permisos relacionadas con notificaciones y alarmas exactas que existan actualmente.

También continúa pendiente la evidencia ejecutable que demuestre el comportamiento real en un dispositivo.

## 236. Módulo de progreso

### Evidencia revisada

`ProgressViewModel.kt`

### HECHOS VERIFICADOS

`ProgressViewModel` depende de:

- `ActivityRepository`
- `PomodoroRepository`
- `SessionManager`
- `StreakCalculator`

El módulo integra información relacionada con:

- actividades completadas;
- Pomodoros Focus completados;
- tiempo total de enfoque;
- racha actual;
- mejor racha;
- progreso semanal.

## 237. Obtención del usuario activo

Durante la carga del módulo se consulta:

`sessionManager.sessionState.first()`

Posteriormente se obtiene:

`session.userId`

Si no existe un usuario activo, el estado cambia a:

`ProgressUiState.NoActiveSession`

y la carga no continúa.

## 238. Periodos de consulta

La implementación permite seleccionar tres periodos:

- `TODAY`
- `WEEK`
- `ALL`

El periodo activo se mantiene mediante:

`MutableStateFlow<ProgressPeriod>`

y los datos se actualizan mediante:

`flatMapLatest`

## 239. Estadísticas del día actual

Para el periodo `TODAY` se consulta:

- cantidad de actividades completadas;
- cantidad de Pomodoros Focus completados;
- segundos de enfoque.

Las consultas utilizan el mismo día como:

- `startEpochDay`
- `endEpochDay`

y siempre incluyen el `userId`.

## 240. Estadísticas semanales

Para el periodo `WEEK`, la implementación calcula el inicio de la semana a partir del día actual.

La semana observada comienza el lunes y cubre siete días.

Dentro de ese periodo se consultan:

- actividades completadas;
- Pomodoros Focus completados;
- segundos de enfoque.

## 241. Estadísticas históricas

Para el periodo `ALL` se consultan:

- total de actividades completadas;
- total de Pomodoros Focus completados;
- total de segundos de enfoque.

Estas consultas también se realizan para el usuario activo.

## 242. Progreso semanal por día

La implementación construye una colección de siete elementos:

`WeeklyProgressDay`

Cada día contiene:

- `epochDay`;
- actividades completadas;
- Pomodoros completados;
- segundos de enfoque.

La información se obtiene combinando estadísticas diarias provenientes de:

- `ActivityRepository`
- `PomodoroRepository`

## 243. Días sin información

### HECHO VERIFICADO

El ViewModel construye explícitamente los siete días de la semana.

Cuando no existen datos para un día determinado, utiliza:

- `0` actividades;
- `0` Pomodoros;
- `0` segundos de enfoque.

Por lo tanto, la estructura semanal mantiene los siete días aunque alguno no tenga actividad registrada.

## 244. Cálculo de rachas en progreso

El módulo combina:

- días con actividades completadas;
- días con Pomodoros Focus completados.

Después:

- elimina duplicados;
- ordena las fechas;
- utiliza `StreakCalculator.calculate(...)`.

El resultado proporciona:

- racha actual;
- mejor racha.

## 245. Composición reactiva del estado

El estado final del módulo se construye combinando:

- estadísticas del periodo seleccionado;
- días válidos para racha;
- progreso semanal.

La implementación utiliza `combine` y `Flow` para observar cambios en los datos.

## 246. Estado expuesto a la interfaz

`ProgressUiState.Success` contiene:

- `completedActivities`
- `completedPomodoros`
- `totalFocusSeconds`
- `currentStreakDays`
- `bestStreakDays`
- `selectedPeriod`
- `weeklyDays`

Los estados adicionales observados son:

- `Loading`
- `NoActiveSession`
- `Error`

## 247. Cadena arquitectónica del módulo de progreso

### HECHO VERIFICADO

La relación observada es:

`SessionManager → ProgressViewModel`

y:

`ProgressViewModel → ActivityRepository`

`ProgressViewModel → PomodoroRepository`

`ProgressViewModel → StreakCalculator`

Los repositorios ya revisados anteriormente delegan posteriormente sus consultas hacia los DAOs y Room.

## 248. Aislamiento por usuario en progreso

### HECHO VERIFICADO EN CÓDIGO

El `userId` utilizado por el módulo proviene de la sesión activa.

Ese identificador se utiliza en las consultas de:

- actividades;
- Pomodoro;
- estadísticas diarias;
- estadísticas semanales;
- estadísticas históricas;
- días utilizados para calcular rachas.

### EVIDENCIA PENDIENTE

Todavía se requiere una prueba ejecutable con usuarios distintos para demostrar el comportamiento observable durante la ejecución.

## 249. Funcionalidad de calendario

### Evidencia revisada

`CalendarActivitiesContent.kt`

### HECHOS VERIFICADOS

La implementación actual contiene una funcionalidad de calendario desarrollada con Jetpack Compose mediante:

`CalendarActivitiesContent`

El componente recibe como entrada:

`List<ActivityEntity>`

Por lo tanto, el calendario trabaja sobre actividades que le son proporcionadas desde otro componente.

## 250. Responsabilidad del componente

`CalendarActivitiesContent` se encarga principalmente de presentación e interacción de interfaz.

Permite:

- visualizar un mes;
- navegar al mes anterior;
- navegar al mes siguiente;
- seleccionar una fecha;
- identificar días que contienen actividades;
- mostrar las actividades correspondientes a la fecha seleccionada.

## 251. Estado local de la interfaz

El componente mantiene localmente:

- mes visible;
- fecha seleccionada.

Para ello utiliza:

`rememberSaveable`

No se observó un `CalendarViewModel` dedicado para esta funcionalidad.

## 252. Organización de actividades por fecha

Las actividades recibidas se agrupan mediante:

`dueDateEpochDay`

utilizando:

`activities.groupBy { it.dueDateEpochDay }`

Después se recuperan las actividades correspondientes al día seleccionado.

## 253. Representación de estados en el calendario

La interfaz distingue actividades según su estado.

Los estados observados son:

- `COMPLETED`
- `OVERDUE`
- otros estados representados como pendientes.

Los días con actividades utilizan indicadores visuales:

- `✓` cuando todas las actividades están completadas;
- `!` cuando existe al menos una actividad vencida;
- `•` cuando existen actividades sin cumplir las condiciones anteriores.

## 254. Presentación de actividades seleccionadas

Cuando se selecciona una fecha, el calendario muestra las actividades correspondientes mediante:

`CalendarActivityCard`

La tarjeta presenta:

- título;
- descripción, cuando existe;
- estado de la actividad.

Si no existen actividades para la fecha se muestra un mensaje indicando que no hay actividades.

## 255. Posición arquitectónica del calendario

### HECHO VERIFICADO

La funcionalidad observada pertenece principalmente a la capa de interfaz:

`Jetpack Compose → CalendarActivitiesContent`

No se observaron dentro de este archivo accesos directos a:

- Room;
- DAO;
- Repository;
- SessionManager.

El componente recibe las actividades ya disponibles mediante parámetros.

### EVIDENCIA FALTANTE

Este archivo no permite determinar por sí solo qué componente obtiene originalmente la lista de actividades entregada al calendario.

Por lo tanto, todavía no puede afirmarse desde esta evidencia cómo se aplica el filtrado por usuario antes de que las actividades lleguen al calendario.

## 256. Configuración principal de Android

### Evidencia revisada

`app/src/main/AndroidManifest.xml`

### HECHOS VERIFICADOS

La aplicación registra como clase de aplicación:

`RachaProApplication`

mediante:

`android:name=".RachaProApplication"`

También se encuentra registrada:

`MainActivity`

como actividad principal de la aplicación.

## 257. Actividad de inicio

`MainActivity` contiene un `intent-filter` con:

- `android.intent.action.MAIN`
- `android.intent.category.LAUNCHER`

Por lo tanto, `MainActivity` es el punto de entrada de la aplicación desde el launcher de Android.

La actividad está configurada con:

`android:exported="true"`

## 258. Permisos declarados

### HECHO VERIFICADO

El Manifest declara los siguientes permisos:

- `android.permission.POST_NOTIFICATIONS`
- `android.permission.SCHEDULE_EXACT_ALARM`
- `android.permission.RECEIVE_BOOT_COMPLETED`

Estos permisos corresponden con componentes previamente observados en el subsistema de recordatorios.

## 259. Permiso de notificaciones

La aplicación declara:

`POST_NOTIFICATIONS`

Esto es consistente con la comprobación realizada previamente en `ReminderReceiver` antes de publicar notificaciones en Android 13 o superior.

## 260. Permiso para alarmas exactas

La aplicación declara:

`SCHEDULE_EXACT_ALARM`

Esto es consistente con `ReminderScheduler`, donde se intenta utilizar:

`setExactAndAllowWhileIdle(...)`

cuando el sistema permite alarmas exactas.

La propia implementación contempla una alternativa no exacta cuando no es posible utilizar una alarma exacta.

## 261. Recepción del reinicio del dispositivo

La aplicación declara:

`RECEIVE_BOOT_COMPLETED`

También registra:

`BootReceiver`

con un `intent-filter` para:

`android.intent.action.BOOT_COMPLETED`

### HECHO VERIFICADO

Queda confirmado que la lógica previamente observada en `BootReceiver` está registrada en el Manifest para recibir el evento de finalización del arranque.

## 262. Registro de ReminderReceiver

El Manifest registra:

`ReminderReceiver`

como `BroadcastReceiver`.

Está configurado con:

- `android:enabled="true"`
- `android:exported="false"`

Este componente coincide con el Receiver utilizado por los `PendingIntent` creados en `ReminderScheduler`.

## 263. Exposición de receivers

### HECHO VERIFICADO

Tanto:

- `BootReceiver`
- `ReminderReceiver`

están configurados con:

`android:exported="false"`

Por lo tanto, el Manifest no los declara como componentes exportados para uso general por otras aplicaciones.

## 264. Configuración de respaldo

La aplicación contiene:

`android:allowBackup="true"`

y referencia:

- `@xml/data_extraction_rules`
- `@xml/backup_rules`

### EVIDENCIA FALTANTE

No se han revisado los contenidos de esos dos archivos XML.

Por lo tanto, a partir únicamente del Manifest no puede determinarse exactamente qué información de la aplicación se incluye o excluye de los mecanismos de respaldo.

## 265. Confirmación del subsistema de recordatorios

### HECHO VERIFICADO

Con la revisión conjunta de:

- `ReminderRepository`
- `ReminderDao`
- `ReminderScheduler`
- `ReminderReceiver`
- `NotificationChannels`
- `BootReceiver`
- `AndroidManifest.xml`

queda documentada en código la siguiente estructura:

`Room`
→ `ReminderRepository`
→ `ReminderScheduler`
→ `AlarmManager`
→ `ReminderReceiver`
→ `NotificationManagerCompat`

y para reinicios:

`BOOT_COMPLETED`
→ `BootReceiver`
→ `ReminderRepository`
→ `ReminderScheduler`

El Manifest confirma los permisos y registros necesarios observados para estos componentes.

### EVIDENCIA PENDIENTE

Continúan pendientes las pruebas ejecutables en dispositivo que demuestren el comportamiento real de recordatorios y notificaciones.

## 266. Punto de entrada de la aplicación

### Evidencia revisada

`MainActivity.kt`

### HECHOS VERIFICADOS

`MainActivity` extiende:

`ComponentActivity`

y constituye la actividad principal registrada previamente en `AndroidManifest.xml`.

Durante su creación se ejecuta:

`enableEdgeToEdge()`

y posteriormente se configura la interfaz mediante Jetpack Compose.

## 267. Inicialización de la interfaz Compose

La interfaz se establece mediante:

`setContent`

Dentro de esta configuración se utiliza:

`RachaProTheme`

y posteriormente:

`RachaProNavHost()`

Por lo tanto, la navegación principal de la aplicación se inicia desde `MainActivity`.

## 268. Relación con la navegación

### HECHO VERIFICADO

La relación observada es:

`AndroidManifest.xml`
→ `MainActivity`
→ `RachaProTheme`
→ `RachaProNavHost`

`RachaProNavHost` es posteriormente responsable de determinar la pantalla inicial según el estado de la aplicación y de la sesión, como fue documentado previamente.

## 269. Pantalla de bienvenida

El mismo archivo contiene el composable:

`WelcomeScreen`

Este componente recibe:

`onStart: () -> Unit`

y contiene elementos visuales como:

- nombre `RachaPro`;
- mensaje introductorio;
- elemento visual provisional;
- botón `Comenzar`.

### HECHO VERIFICADO

`WelcomeScreen` pertenece a la capa de interfaz y no realiza directamente operaciones sobre:

- Repository;
- DAO;
- Room;
- DataStore.

La acción de navegación se delega mediante el callback `onStart`.

## 270. Separación observada del punto de entrada

### HECHO VERIFICADO

`MainActivity` no contiene directamente lógica para:

- autenticación;
- actividades;
- Pomodoro;
- progreso;
- perfil;
- persistencia.

Su responsabilidad observada consiste principalmente en inicializar la interfaz Compose y entregar el flujo principal a `RachaProNavHost`.

## 271. Flujo inicial de la aplicación

La evidencia revisada permite representar el inicio de la aplicación como:

`Android`
→ `MainActivity`
→ `RachaProTheme`
→ `RachaProNavHost`
→ estado inicial de navegación
→ pantalla correspondiente

La determinación posterior entre onboarding, inicio de sesión o aplicación autenticada se encuentra delegada a los componentes de navegación y sesión revisados anteriormente.

## 272. Estado consolidado de la arquitectura actual

Esta sección resume el estado de la revisión después de analizar los principales componentes del código fuente.

### HECHOS VERIFICADOS

A partir de los archivos revisados se ha comprobado que la implementación actual de RachaPro:

- Es una aplicación Android desarrollada con Jetpack Compose.
- Utiliza Navigation Compose para la navegación.
- Utiliza ViewModels para gestionar estado y operaciones de diferentes funcionalidades.
- Utiliza Repositories como intermediarios entre ViewModels y persistencia.
- Utiliza DAOs de Room para acceso a datos.
- Utiliza una base de datos local Room denominada `rachapro_database`.
- Utiliza DataStore para sesión y preferencias.
- Implementa autenticación local mediante correo y contraseña.
- Protege las contraseñas mediante hash PBKDF2 y salt.
- Mantiene el identificador del usuario autenticado mediante `SessionManager`.
- Aplica filtrado por `userId` en actividades.
- Aplica filtrado por `userId` en Pomodoro.
- Aplica filtrado por `userId` en logros.
- Aplica filtrado por `userId` en recordatorios.
- Implementa borrado lógico de actividades.
- Implementa migraciones explícitas de Room.
- Implementa cálculo de rachas mediante `StreakCalculator`.
- Implementa reglas de logros mediante `AchievementEngine`.
- Implementa preferencias Pomodoro y de notificaciones por usuario.
- Implementa estadísticas de progreso a partir de actividades y Pomodoro.
- Contiene una funcionalidad de calendario en la capa de interfaz.
- Implementa recordatorios mediante `AlarmManager`.
- Implementa notificaciones mediante `NotificationCompat`.
- Implementa recuperación de recordatorios después de `BOOT_COMPLETED`.
- Registra los permisos y BroadcastReceivers correspondientes en `AndroidManifest.xml`.

### ESTRUCTURA GENERAL OBSERVADA

La estructura principal identificada en el código puede representarse de forma simplificada como:

`Jetpack Compose`
→ `ViewModels`
→ `Repositories`
→ `DAOs`
→ `Room`

También existen componentes de dominio utilizados por diferentes módulos:

- `StreakCalculator`
- `AchievementEngine`

Para información liviana de sesión y preferencias se utiliza:

`DataStore`

Para recordatorios y notificaciones intervienen servicios del sistema Android:

`ReminderScheduler`
→ `AlarmManager`
→ `ReminderReceiver`
→ `NotificationManagerCompat`

### PERSISTENCIA ACTUAL OBSERVADA

La persistencia identificada en los componentes revisados es local y se realiza mediante:

- Room.
- DataStore.

No se ha identificado en la evidencia revisada hasta este punto una API backend ni una base de datos remota utilizada por la implementación actual.

Esto no debe interpretarse todavía como una demostración exhaustiva de ausencia de cualquier componente externo en todo el repositorio.

### EVIDENCIA PENDIENTE VIGENTE

Después de la revisión de código continúan pendientes principalmente evidencias ejecutables y documentales:

1. Prueba funcional de aislamiento utilizando usuarios diferentes.
2. Evidencia reproducible de compilación del proyecto.
3. Evidencia de instalación y ejecución en dispositivos Android evaluados.
4. Evidencia de RNF01 - Usabilidad.
5. Evidencia de RNF02 - Rendimiento.
6. Evidencia ejecutable complementaria para RNF03 - Seguridad.
7. Comparación entre la arquitectura histórica propuesta y la arquitectura actualmente implementada.
8. Verificación del estado de implementación de las pantallas del prototipo histórico.
9. Registro formal de la versión o commit utilizado como línea base de la evaluación.
10. Revisión de las instrucciones oficiales de Semana 4 para comprobar que las evidencias recopiladas correspondan exactamente a lo solicitado.

### EVIDENCIAS MENORES TODAVÍA NO REVISADAS

Existen elementos que no han sido necesarios para establecer la estructura principal de la arquitectura, pero que permanecen sin revisar de forma específica:

- Reglas de respaldo definidas en `backup_rules.xml`.
- Reglas de extracción definidas en `data_extraction_rules.xml`.
- Componente exacto que entrega la lista de actividades a `CalendarActivitiesContent`.

Estos elementos no se utilizan actualmente para declarar conclusiones que dependan de su contenido.
