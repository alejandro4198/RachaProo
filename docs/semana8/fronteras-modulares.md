# Fronteras modulares del backend RachaPro

## 1. Propósito

Este documento define las fronteras propuestas para transformar el backend de RachaPro desde su organización AS-IS por paquetes funcionales independientes hacia un monolito modular orientado a capacidades de negocio.

La definición precede al refactor físico. Su objetivo es establecer ownership de datos, dependencias permitidas y dependencias que deberán eliminarse antes de mover clases o introducir reglas automáticas con ArchUnit.

---

## 2. Estado AS-IS

El backend se encuentra actualmente distribuido principalmente en los siguientes paquetes:

- achievement
- activity
- auth
- category
- pomodoro
- reminder
- security
- subtask
- user
- error

Aunque los paquetes agrupan funcionalidades relacionadas, las fronteras arquitectónicas entre capacidades de negocio no están formalmente protegidas.

Se observaron dependencias directas entre servicios y repositorios pertenecientes a capacidades diferentes.

---

## 3. Arquitectura TO-BE

El backend evolucionará hacia un monolito modular organizado por capacidades de negocio.

Los módulos definidos son:

| Módulo | Responsabilidad principal |
| --- | --- |
| Identity | Usuarios, autenticación e infraestructura de seguridad asociada |
| Activities | Actividades, categorías y subtareas |
| Focus | Sesiones Pomodoro y concentración |
| Progress | Logros y progreso |
| Reminders | Recordatorios |
| Shared | Elementos técnicos realmente transversales |

La separación por módulos será la estructura arquitectónica principal.

Dentro de cada módulo podrán existir subdivisiones internas como API, aplicación, dominio e infraestructura cuando sean necesarias.

---

## 4. Ownership de datos

Cada módulo es responsable de sus propios datos y de los repositorios que permiten acceder a ellos.

| Módulo | Tablas / datos propios |
| --- | --- |
| Identity | users |
| Activities | activities, categories, subtasks |
| Focus | pomodoro_sessions |
| Progress | achievements |
| Reminders | reminders |
| Shared | No posee tablas de negocio |

El ownership implica que otro módulo no debe utilizar directamente las entidades o repositorios internos del módulo propietario.

---

## 5. Dependencias AS-IS detectadas

### 5.1 Focus hacia Activities

PomodoroSessionService utiliza directamente ActivityRepository para comprobar que una actividad:

- existe;
- pertenece al usuario;
- no está eliminada.

AS-IS:

Focus -> ActivityRepository

Este acceso atraviesa la frontera propuesta de Activities.

---

### 5.2 Reminders hacia Activities

ReminderService utiliza directamente ActivityRepository para validar una actividad opcional asociada al recordatorio.

AS-IS:

Reminders -> ActivityRepository

Este acceso también atraviesa la frontera propuesta de Activities.

---

### 5.3 Identity hacia Activities

UserService utiliza directamente:

- CategoryRepository
- CategoryEntity

al crear las categorías iniciales Universidad, Personal y Trabajo durante el registro de un usuario.

AS-IS:

Identity -> CategoryRepository
Identity -> CategoryEntity

Esto hace que Identity conozca detalles internos pertenecientes a Activities.

---

## 6. Dependencias internas aceptadas

Las siguientes dependencias permanecen dentro de una misma frontera modular:

- ActivityService -> CategoryRepository
- SubtaskService -> ActivityRepository
- AuthController -> UserService
- AuthService -> UserRepository
- AuthService -> PasswordHasher
- AuthService -> JwtService
- JwtService -> UserEntity

Después del refactor físico estas clases pertenecerán al mismo módulo que sus dependencias.

---

## 7. Regla de encapsulamiento

Un módulo consumidor no podrá acceder directamente a:

- repositorios de otro módulo;
- entidades internas de otro módulo;
- infraestructura interna de otro módulo.

La interacción entre módulos deberá ocurrir mediante contratos públicos explícitos.

---

## 8. Contratos requeridos

### 8.1 Consulta de actividades

Focus y Reminders necesitan validar la existencia de una actividad activa perteneciente a un usuario.

Se requiere un contrato público equivalente conceptualmente a:

ActivityLookup

Capacidad:

existsActiveActivityForUser(activityId, userId)

La implementación permanecerá dentro de Activities y podrá utilizar ActivityRepository internamente.

---

### 8.2 Aprovisionamiento de categorías iniciales

Identity necesita solicitar la creación de categorías iniciales después de registrar un usuario.

Se requiere un contrato público equivalente conceptualmente a:

DefaultCategoryProvisioning

Capacidad:

createDefaultsForUser(userId)

La construcción y persistencia de CategoryEntity quedará dentro de Activities.

---

## 9. Dependencias permitidas TO-BE

Se permiten las siguientes dependencias cuando atraviesen una API pública del módulo destino:

Identity -> Activities API

Focus -> Activities API

Reminders -> Activities API

Los módulos podrán utilizar Shared cuando exista una necesidad técnica transversal real.

---

## 10. Dependencias prohibidas TO-BE

Deberán impedirse dependencias como:

Focus -> Activities Repository

Reminders -> Activities Repository

Identity -> Activities Repository

Identity -> Activities Entity

En general:

Módulo A -> infraestructura interna de Módulo B

Módulo A -> repositorio interno de Módulo B

Módulo A -> entidad interna de Módulo B

---

## 11. Estrategia de migración

La migración se realizará preservando inicialmente el comportamiento funcional existente.

Orden previsto:

1. Definir contratos públicos entre módulos.
2. Introducir las implementaciones correspondientes.
3. Sustituir los accesos directos entre módulos.
4. Reorganizar físicamente los paquetes.
5. Verificar compilación y pruebas.
6. Incorporar fitness functions con ArchUnit.
7. Validar que las reglas modulares se cumplen.
8. Repetir verificaciones funcionales y de rendimiento relevantes.

---

## 12. Decisiones deliberadamente no tomadas

Esta definición no implica por ahora:

- separar el sistema en microservicios;
- utilizar una base de datos independiente por módulo;
- introducir comunicación de red entre módulos;
- reemplazar las llamadas síncronas por eventos;
- modificar contratos HTTP existentes;
- modificar la semántica funcional de creación de usuarios;
- modificar el comportamiento del cliente Android.

La primera meta es lograr modularidad estructural y control de dependencias dentro del monolito existente.

---

## 13. Criterio de éxito

La modularización se considerará estructuralmente lograda cuando:

1. cada clase pertenezca a una frontera modular definida;
2. cada tabla y repositorio tenga un único módulo propietario;
3. ningún módulo acceda directamente a repositorios o entidades internas de otro módulo;
4. las dependencias intermodulares utilicen contratos explícitos;
5. las reglas puedan verificarse automáticamente mediante fitness functions;
6. el backend continúe compilando y mantenga su comportamiento funcional esperado.

---

## 14. Estado

Clasificación actual:

🏗 AS-IS:
las dependencias cruzadas identificadas existen actualmente en el código.

🎯 TO-BE:
monolito modular orientado a capacidades de negocio con fronteras y ownership explícitos.

⚪ CRITERIO DEFINIDO:
el acceso directo a repositorios y entidades internas entre módulos deberá eliminarse durante el refactor.

🧪 PRUEBA PENDIENTE:
las reglas todavía no están implementadas ni verificadas mediante ArchUnit.
