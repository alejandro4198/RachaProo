# C4 Nivel 1 â€” Contexto de RachaPro

## 1. IdentificaciÃ³n de la evidencia

- **Sistema:** RachaPro â€” Gestor de Productividad AcadÃ©mica.
- **Vista:** C4 Nivel 1 â€” Contexto del sistema.
- **Estado representado:** arquitectura actual (*as-is*) observada en el repositorio.
- **Fecha de revisiÃ³n:** 04 de septiembre de 2026.
- **Repositorio:** <https://github.com/alejandro4198/RachaProo>
- **LÃ­nea base auditada:** commit `126555982b011d31cb800c776ac1866d7296903e` de `master`.

La vista se construyÃ³ contrastando el modelo con el cÃ³digo, la configuraciÃ³n de Android, el backend y la infraestructura versionada. La existencia de una implementaciÃ³n en el repositorio no se presenta como evidencia de un despliegue productivo permanente.

## 2. PropÃ³sito de la vista

Esta vista responde la pregunta:

> Â¿QuiÃ©n utiliza RachaPro, quÃ© responsabilidad general tiene el sistema y con quÃ© sistema externo se relaciona en su operaciÃ³n actual?

Su propÃ³sito es delimitar RachaPro como sistema de software antes de mostrar sus aplicaciones, almacenes y componentes internos.

## 3. Audiencia

La vista resulta Ãºtil para:

- estudiantes, docentes y evaluadores que necesitan comprender el alcance general;
- integrantes del equipo que deben defender los lÃ­mites del sistema;
- responsables de arquitectura y desarrollo que necesitan distinguir el sistema actual de una soluciÃ³n futura.

## 4. Diagrama de contexto

```mermaid
flowchart LR
    student["Persona: Estudiante<br/>Organiza actividades, subtareas y recordatorios;<br/>usa Pomodoro, progreso, rachas y logros"]
    rachapro["Sistema de software: RachaPro<br/>Permite al estudiante organizar actividades, subtareas y recordatorios,<br/>utilizar Pomodoro y consultar progreso, rachas y logros"]
    android["Sistema externo: Android OS<br/>Alarmas, eventos de arranque y notificaciones"]

    student -->|"Registra, consulta y actualiza su informaciÃ³n de productividad"| rachapro
    rachapro -->|"Programa alarmas y solicita mostrar notificaciones"| android
    android -->|"Entrega recordatorios en el dispositivo"| student

    classDef person fill:#084c61,color:#fff,stroke:#063b4a,stroke-width:2px;
    classDef system fill:#6c4cff,color:#fff,stroke:#432bb5,stroke-width:2px;
    classDef external fill:#e8eef9,color:#17223b,stroke:#63708a,stroke-width:2px;
    class student person;
    class rachapro system;
    class android external;
```

## 5. Elementos representados

| ID | Tipo C4 | Elemento | Responsabilidad | Evidencia principal |
|---|---|---|---|---|
| P-01 | Persona | Estudiante | Utilizar las funciones de productividad acadÃ©mica de RachaPro. | Pantallas Compose en `app/src/main/java/com/example/rachapro/` y navegaciÃ³n en `navigation/RachaProNavHost.kt`. |
| S-01 | Sistema de software | RachaPro | Gestionar usuarios, actividades, categorÃ­as, subtareas, recordatorios, Pomodoro, progreso, rachas, preferencias y logros. | MÃ³dulos `app/`, `backend/` e `infra/postgres/`. |
| SE-01 | Sistema externo | Android OS | Ejecutar alarmas, comunicar el reinicio del dispositivo y mostrar notificaciones locales. | Permisos y *receivers* en `app/src/main/AndroidManifest.xml`; `notifications/ReminderScheduler.kt`, `ReminderReceiver.kt`, `BootReceiver.kt` y `NotificationChannels.kt`. |

## 6. Relaciones verificadas

| Origen | Destino | RelaciÃ³n | Evidencia verificable |
|---|---|---|---|
| Estudiante | RachaPro | InteractÃºa con el sistema mediante la aplicaciÃ³n mÃ³vil. | `MainActivity.kt`, `navigation/RachaProNavHost.kt` y pantallas de `auth/`, `activities/`, `pomodoro/`, `progress/`, `profile/`, `home/` y `calendar/`. |
| RachaPro | Android OS | Programa alarmas exactas o inexactas y registra receptores para recordatorios y reinicio. | `ReminderScheduler.schedule`, `ReminderReceiver.onReceive`, `BootReceiver.onReceive` y declaraciones del manifiesto. |
| Android OS | Estudiante | Presenta las notificaciones locales solicitadas por RachaPro. | `NotificationManagerCompat.notify` en `ReminderReceiver.kt`. |

## 7. LÃ­mites y exclusiones del contexto actual

- El backend y PostgreSQL forman parte interna de RachaPro y se descomponen en el Nivel 2; por eso no aparecen como sistemas externos en esta vista.
- No se identificaron integraciones actuales con Google Calendar, correo, redes sociales, pasarelas de pago, Firebase u otros servicios de terceros.
- No se identificÃ³ un administrador como actor funcional del sistema actual.
- El repositorio contiene una direcciÃ³n HTTP de red local para el backend. Esto demuestra la relaciÃ³n implementada, pero no demuestra un despliegue pÃºblico o disponibilidad continua.
- Los artefactos histÃ³ricos o las ideas de sincronizaciÃ³n futura no se modelan como parte de la arquitectura *as-is* si no tienen una implementaciÃ³n actual verificable.

## 8. CÃ³mo defender esta vista

Desde esta vista se puede navegar al repositorio de la siguiente forma:

1. La interacciÃ³n del estudiante se ubica desde `MainActivity` hasta `RachaProNavHost` y las pantallas Compose.
2. El lÃ­mite de RachaPro se descompone en la aplicaciÃ³n Android, sus almacenes locales, la API y PostgreSQL en `06-c4-contenedores.md`.
3. La relaciÃ³n con Android se demuestra con los permisos, los receptores y las llamadas a `AlarmManager` y `NotificationManagerCompat`.
