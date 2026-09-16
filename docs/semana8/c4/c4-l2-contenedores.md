# C4 L2 â€” Diagrama de contenedores AS-IS

## 1. PropÃ³sito

Este documento describe el nivel 2 del modelo C4 de RachaPro despuÃ©s de la materializaciÃ³n del monolito modular de Semana 8.

Estado representado:

**AS-IS**

No representa una arquitectura futura ni una propuesta TO-BE.

---

## 2. Diagrama

```mermaid
flowchart LR
    user["Usuario de RachaPro"]

    android["Android App<br/>Kotlin + Jetpack Compose<br/>Room local, AlarmManager y notificaciones"]

    backend["Backend RachaPro<br/>Kotlin + Spring Boot<br/>Monolito modular"]

    postgres[("PostgreSQL<br/>Persistencia autoritativa del backend")]

    user -->|"Usa la aplicaciÃ³n"| android
    android -->|"HTTP / JSON<br/>JWT Bearer"| backend
    backend -->|"JPA / Hibernate / JDBC"| postgres