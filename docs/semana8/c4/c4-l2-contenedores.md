# C4 L2 — Diagrama de contenedores AS-IS

## 1. Propósito

Este documento describe el nivel 2 del modelo C4 de RachaPro después de la materialización del monolito modular de Semana 8.

Estado representado:

**AS-IS**

No representa una arquitectura futura ni una propuesta TO-BE.

---

## 2. Diagrama

```mermaid
flowchart LR
    user["Usuario de RachaPro"]

    android["Android App<br/>Kotlin + Jetpack Compose<br/>Room local, WorkManager y notificaciones"]

    backend["Backend RachaPro<br/>Kotlin + Spring Boot<br/>Monolito modular"]

    postgres[("PostgreSQL<br/>Persistencia autoritativa del backend")]

    user -->|"Usa la aplicación"| android
    android -->|"HTTP / JSON<br/>JWT Bearer"| backend
    backend -->|"JPA / Hibernate / JDBC"| postgres