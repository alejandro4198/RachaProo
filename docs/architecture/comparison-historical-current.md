# Comparación entre arquitectura histórica y arquitectura actual de RachaPro

Este documento compara la arquitectura propuesta previamente durante la asignatura Ingeniería de Software con la arquitectura observada en la implementación actual de RachaPro.

La comparación se limita a identificar coincidencias, diferencias y elementos cuya correspondencia todavía requiere evidencia.

No se establecen en este documento las causas de los cambios arquitectónicos ni se determina si una alternativa es mejor que otra.

## 1. Fuentes utilizadas

### Arquitectura histórica

Fuente principal:

`RachaPro_ Gestor Inteligente de Productividad Académica(3).pdf`

Este documento corresponde al diseño elaborado previamente durante Ingeniería de Software.

### Arquitectura actual

Fuente principal:

`docs/architecture/current/architecture-current.md`

Este documento fue construido mediante revisión directa del código fuente actual de RachaPro.

## 2. Arquitectura histórica documentada

### HECHO VERIFICADO EN DOCUMENTACIÓN HISTÓRICA

El diseño histórico planteaba una arquitectura modular y por capas.

También planteaba un enfoque cliente-servidor con el siguiente flujo general:

`Aplicación móvil`
→ `HTTPS`
→ `API Backend`
→ `Servicios`
→ `Base de datos en servidor`

Dentro del backend se planteaban servicios relacionados con:

- autenticación;
- actividades;
- Pomodoro;
- progreso;
- recompensas;
- recordatorios.

También se planteaba la integración con un servicio de notificaciones push.

## 3. Arquitectura actual observada

### HECHO VERIFICADO EN CÓDIGO

La implementación actual revisada presenta como estructura general:

`Jetpack Compose`
→ `ViewModels`
→ `Repositories`
→ `DAOs`
→ `Room`

Además utiliza:

`DataStore`

para información de sesión y preferencias.

Los recordatorios se gestionan mediante componentes locales de Android:

`ReminderScheduler`
→ `AlarmManager`
→ `ReminderReceiver`
→ `NotificationManagerCompat`

Hasta la evidencia revisada actualmente no se ha identificado una API Backend ni una base de datos remota utilizada por la implementación.

## 4. Diferencia general observada

### HECHO VERIFICADO

La arquitectura histórica planteaba una separación física entre:

- aplicación móvil;
- backend;
- base de datos de servidor.

La implementación actual revisada concentra las funcionalidades observadas dentro de la aplicación Android y utiliza persistencia local.

### EVIDENCIA FALTANTE

No se ha establecido documentalmente la causa de esta diferencia.

Por lo tanto, no debe afirmarse que el cambio se produjo por:

- rendimiento;
- costos;
- facilidad de implementación;
- tiempo disponible;
- seguridad;
- decisión del equipo;
- limitaciones tecnológicas;

a menos que exista evidencia que lo demuestre.

## 5. Comparación por componente

La siguiente tabla contrasta únicamente elementos documentados en la arquitectura histórica con componentes identificados en la implementación actual.

| Aspecto                       | Arquitectura histórica                                                                | Implementación actual observada                                                                                                                                  | Clasificación                                   |
|-------------------------------|---------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------|
| Aplicación móvil              | Aplicación móvil utilizada por el estudiante como cliente del sistema.                | Aplicación Android nativa desarrollada con Jetpack Compose.                                                                                                      | Coincidencia funcional                          |
| Organización interna          | Arquitectura modular y por capas.                                                     | Se observan capas y responsabilidades diferenciadas mediante Compose, ViewModels, Repositories, DAOs y componentes de dominio.                                   | Coincidencia parcial                            |
| Comunicación cliente-servidor | La aplicación móvil se comunica mediante HTTPS con una API Backend.                   | No se ha identificado comunicación con una API Backend en los componentes revisados.                                                                             | Diferencia                                      |
| Backend                       | API Backend con servicios independientes para diferentes funcionalidades.             | Las funcionalidades revisadas se ejecutan dentro de la aplicación Android.                                                                                       | Diferencia                                      |
| Persistencia                  | Base de datos ubicada en un servidor.                                                 | Base de datos local Room denominada `rachapro_database`.                                                                                                         | Diferencia                                      |
| Sesión                        | Asociada conceptualmente al servicio de autenticación del backend.                    | Sesión local mediante `SessionManager` y DataStore.                                                                                                              | Diferencia de implementación                    |
| Autenticación                 | Servicio de autenticación encargado del registro, login y validación de credenciales. | `AuthViewModel`, `UserRepository`, `UserDao`, `PasswordHasher` y `SessionManager`.                                                                               | Misma responsabilidad, implementación diferente |
| Gestión de actividades        | Servicio de actividades dentro del backend.                                           | `ActivitiesViewModel → ActivityRepository → ActivityDao → Room`.                                                                                                 | Misma responsabilidad, implementación diferente |
| Pomodoro                      | Servicio Pomodoro dentro del backend.                                                 | `PomodoroViewModel → PomodoroRepository → PomodoroSessionDao → Room`.                                                                                            | Misma responsabilidad, implementación diferente |
| Progreso                      | Servicio de progreso dentro del backend.                                              | `ProgressViewModel` combina información de `ActivityRepository`, `PomodoroRepository` y `StreakCalculator`.                                                      | Misma responsabilidad, implementación diferente |
| Rachas                        | Relacionadas con progreso, estadísticas y recompensas.                                | Cálculo local mediante `StreakCalculator`.                                                                                                                       | Implementación concreta observada               |
| Recompensas / logros          | Servicio de recompensas dentro del backend.                                           | `AchievementEngine`, `AchievementRepository` y `AchievementDao`.                                                                                                 | Misma responsabilidad, implementación diferente |
| Recordatorios                 | Servicio de recordatorios y servicio externo de notificaciones push.                  | Persistencia local más `ReminderScheduler`, `AlarmManager`, `ReminderReceiver` y notificaciones Android.                                                         | Diferencia                                      |
| Notificaciones                | Servicio de notificaciones push.                                                      | Notificaciones locales mediante `NotificationCompat` y `NotificationManagerCompat`.                                                                              | Diferencia                                      |
| Repository                    | Considerado conceptualmente como patrón para separar acceso a datos y lógica.         | Existen Repositories concretos para usuarios, actividades, categorías, subtareas, recordatorios, Pomodoro y logros.                                              | Coincidencia                                    |
| Service Layer                 | Considerado conceptualmente para centralizar reglas en servicios del backend.         | No se ha identificado una capa denominada `Service Layer`; parte de la lógica observada se distribuye entre ViewModels, Repositories y componentes de dominio.   | Correspondencia no directa                      |
| Observer                      | Considerado conceptualmente para eventos y actualizaciones.                           | La implementación utiliza `Flow` y `StateFlow` para observar cambios de datos y estado.                                                                          | Correspondencia parcial                         |

## 6. Interpretación de la comparación

### HECHO VERIFICADO

Varias responsabilidades funcionales previstas en el diseño histórico continúan presentes en la implementación actual, entre ellas:

- autenticación;
- gestión de actividades;
- Pomodoro;
- progreso;
- rachas;
- logros;
- recordatorios.

Sin embargo, la ubicación tecnológica de esas responsabilidades es diferente.

En el diseño histórico estaban planteadas principalmente como servicios asociados a un backend.

En la implementación actual revisada, esas responsabilidades se encuentran principalmente dentro de la aplicación Android.

### INFERENCIA NO REALIZADA

La comparación no establece que la arquitectura histórica haya sido formalmente reemplazada, descartada o migrada.

Tampoco establece la causa del cambio observado.

Para realizar cualquiera de esas afirmaciones sería necesaria evidencia adicional sobre las decisiones tomadas durante la implementación.

## 7. Elementos con continuidad entre ambos estados

### HECHOS VERIFICADOS

Aunque la distribución tecnológica es diferente, varias responsabilidades funcionales aparecen tanto en la documentación histórica como en la implementación actual.

Se observa continuidad en:

- Aplicación móvil como medio principal de interacción.
- Registro e inicio de sesión de usuarios.
- Gestión de actividades.
- Uso de Pomodoro.
- Seguimiento del progreso.
- Cálculo o manejo de rachas.
- Sistema de logros o recompensas.
- Recordatorios.
- Separación de responsabilidades entre diferentes componentes.

### ACLARACIÓN

La existencia de una misma responsabilidad funcional en ambos estados no implica que se haya conservado exactamente el mismo diseño técnico.

Por ejemplo, autenticación existe en ambos casos, pero su ubicación y mecanismo de implementación son diferentes.

## 8. Diferencias de implementación observadas

### HECHOS VERIFICADOS

Se identificaron las siguientes diferencias entre el diseño histórico y la implementación actual revisada:

### Comunicación

Histórico:

`Aplicación móvil → HTTPS → API Backend`

Actual observado:

Las funcionalidades revisadas se ejecutan dentro de la aplicación Android y no se ha identificado una API Backend en los componentes analizados.

### Persistencia

Histórico:

`Base de datos en servidor`

Actual observado:

`Room + DataStore`

dentro del dispositivo Android.

### Autenticación

Histórico:

Responsabilidad planteada dentro de un servicio de autenticación asociado al backend.

Actual observado:

`AuthViewModel`
→ `UserRepository`
→ `UserDao`
→ `Room`

con:

`PasswordHasher`

para la protección de contraseñas y:

`SessionManager → DataStore`

para conservar la sesión.

### Actividades

Histórico:

Servicio de actividades dentro del backend.

Actual observado:

`ActivitiesViewModel`
→ `ActivityRepository`
→ `ActivityDao`
→ `Room`

### Pomodoro

Histórico:

Servicio Pomodoro dentro del backend.

Actual observado:

`PomodoroViewModel`
→ `PomodoroRepository`
→ `PomodoroSessionDao`
→ `Room`

### Progreso

Histórico:

Servicio de progreso dentro del backend.

Actual observado:

`ProgressViewModel`

combina información procedente de actividades, Pomodoro y `StreakCalculator`.

### Logros

Histórico:

Servicio de recompensas.

Actual observado:

`AchievementEngine`
→ `AchievementRepository`
→ `AchievementDao`
→ `Room`

### Recordatorios

Histórico:

Servicio de recordatorios integrado con un servicio de notificaciones push.

Actual observado:

`ReminderRepository`
→ `ReminderScheduler`
→ `AlarmManager`
→ `ReminderReceiver`
→ notificación local Android.

## 9. Elementos que no pueden compararse directamente

### EVIDENCIA INSUFICIENTE

Con la evidencia revisada no es posible comparar de manera concluyente aspectos como:

- Escalabilidad real de ambas arquitecturas.
- Rendimiento de una arquitectura frente a la otra.
- Costos de infraestructura.
- Facilidad de mantenimiento.
- Seguridad global de una arquitectura frente a la otra.
- Disponibilidad del sistema.
- Comportamiento con múltiples dispositivos.
- Sincronización de información entre dispositivos.
- Capacidad real para soportar múltiples usuarios simultáneos.
- Impacto de no utilizar actualmente el backend planteado históricamente.

Realizar afirmaciones sobre estos aspectos requeriría evidencia adicional o mediciones específicas.

## 10. Causas de las diferencias

### EVIDENCIA FALTANTE

La documentación revisada permite comprobar que existen diferencias entre la arquitectura histórica y la implementación actual.

Sin embargo, no se ha encontrado evidencia que establezca formalmente por qué se produjeron esas diferencias.

Por lo tanto, no se atribuyen a:

- decisiones de simplificación;
- falta de tiempo;
- costos;
- restricciones académicas;
- facilidad de desarrollo;
- rendimiento;
- seguridad;
- decisiones personales del desarrollador;

sin una fuente que lo respalde.

## 11. Conclusión de la comparación

### HECHO VERIFICADO

La implementación actual conserva varias de las responsabilidades funcionales definidas previamente para RachaPro.

La diferencia principal observada se encuentra en su distribución tecnológica.

La arquitectura histórica planteaba una solución distribuida:

`Aplicación móvil`
→ `API Backend`
→ `Servicios`
→ `Base de datos en servidor`

La implementación actual revisada presenta principalmente una solución local dentro de Android:

`Jetpack Compose`
→ `ViewModels`
→ `Repositories`
→ `DAOs`
→ `Room`

acompañada de `DataStore` y servicios propios del sistema Android.

### EVIDENCIA FALTANTE

Esta comparación no determina:

- cuál arquitectura debe conservarse;
- cuál es mejor;
- cuál debería utilizarse en el futuro;
- si la arquitectura actual debe modificarse;
- si la arquitectura histórica debe recuperarse.

Estas decisiones requieren el análisis arquitectónico correspondiente y no se derivan automáticamente de la comparación documental.
