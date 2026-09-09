# 08 - Decisión de estilo arquitectónico y mapa modular objetivo

## 1. Identificación

- **Sistema:** RachaPro — Gestor de Productividad Académica.
- **Semana:** 7.
- **Fecha de decisión:** 09 de septiembre de 2026.
- **Estado:** decisión arquitectónica actual del equipo.
- **Estilo seleccionado:** monolito modular.
- **Tipo de arquitectura:** objetivo TO-BE; no se presenta como si ya estuviera completamente implementada.

---

## 2. Propósito

Este documento registra la decisión de estilo arquitectónico para la evolución de RachaPro.

Incluye:

1. arquitectura actual AS-IS;
2. drivers arquitectónicos priorizados;
3. comparación de tres estilos;
4. decisión tomada por el equipo;
5. mapa modular objetivo;
6. responsabilidades de los módulos;
7. dependencias actuales verificadas;
8. reglas de dependencia objetivo;
9. análisis de cohesión y acoplamiento;
10. antipatrones y riesgos;
11. evaluación crítica de propuestas realizadas con apoyo de IA.

Se mantiene una separación explícita entre hechos observados en el código actual y decisiones de arquitectura futura.

---

## 3. Arquitectura actual AS-IS

La arquitectura actual verificada en las semanas anteriores está compuesta por:

- aplicación Android;
- Room / SQLite;
- DataStore;
- Retrofit y OkHttp;
- API backend Spring Boot;
- Spring Security y JWT;
- Spring Data JPA;
- PostgreSQL.

La vista C4 de componentes documentada en:

`dossier/07-c4-componentes.md`

representa actualmente el backend principalmente mediante responsabilidades técnicas:

```
    API REST
        |
        v
    Servicios de aplicación
        |
        v
    Repositories JPA
        |
        v
    PostgreSQL
```

La seguridad y autenticación constituyen además un componente transversal del backend.

La presencia de paquetes funcionales como `activity`, `pomodoro`, `achievement` o `reminder` no demuestra por sí sola que exista actualmente un monolito modular con fronteras formalmente controladas.

Por esta razón, la modularización planteada en este documento se trata como arquitectura TO-BE.

---

## 4. Drivers arquitectónicos priorizados

El equipo mantiene la priorización registrada anteriormente en:

`dossier/03-atributos-calidad.md`

Orden:

1. Rendimiento.
2. Seguridad.
3. Usabilidad.
4. Disponibilidad.

### 4.1 Rendimiento

La fluidez es fundamental para RachaPro.

Si cargar o registrar información tarda demasiado, la aplicación pierde utilidad para el estudiante.

La necesidad de manejar mayores volúmenes de información y usuarios manteniendo tiempos adecuados influyó anteriormente en la evolución hacia:

```
    Android
       |
       v
    Spring Boot
       |
       v
    PostgreSQL
```

Los experimentos realizados posteriormente aportan evidencia sobre el comportamiento observado, pero no deben presentarse como si hubieran originado retroactivamente esa decisión.

### 4.2 Seguridad

El equipo busca proteger especialmente:

- correo;
- nombre;
- semestre;
- información que permita observar rutinas;
- credenciales de autenticación.

La arquitectura actual utiliza Spring Security y JWT.

### 4.3 Usabilidad

RachaPro está dirigida principalmente a estudiantes.

Debe ser:

- simple;
- comprensible;
- fácil de utilizar.

El estilo interno del backend no mejora automáticamente la interfaz, pero una organización mantenible puede facilitar la evolución de funcionalidades y correcciones.

### 4.4 Disponibilidad

La disponibilidad es necesaria, aunque el equipo considera actualmente más determinantes rendimiento, seguridad y usabilidad.

Existen mecanismos como:

- healthcheck de PostgreSQL;
- `/actuator/health` del backend.

Estos mecanismos permiten observar el estado de los componentes, pero no garantizan disponibilidad continua.

---

## 5. Alternativas arquitectónicas evaluadas

El equipo comparó tres alternativas.

### 5.1 Alternativa A — Monolito por capas

Mantener una única aplicación Spring Boot organizada principalmente por responsabilidades técnicas:

```
    Controllers
        |
        v
    Services
        |
        v
    Repositories
        |
        v
    PostgreSQL
```

**Ventajas:**

- estructura sencilla;
- un único despliegue;
- baja complejidad operacional;
- llamadas internas directas;
- seguridad centralizada.

**Riesgos:**

- crecimiento de dependencias cruzadas;
- límites de negocio poco claros;
- servicios con demasiadas responsabilidades;
- riesgo de evolucionar hacia un Big Ball of Mud.

---

### 5.2 Alternativa B — Monolito modular

Mantener una sola aplicación Spring Boot, pero organizada mediante módulos funcionales con:

- responsabilidades claras;
- límites explícitos;
- dependencias controladas;
- mecanismos públicos de comunicación;
- propiedad lógica de sus datos.

Se conserva:

- un backend;
- un proceso principal;
- un despliegue;
- PostgreSQL como persistencia central.

La principal diferencia consiste en introducir fronteras funcionales dentro del backend.

---

### 5.3 Alternativa C — Microservicios

Separar capacidades del sistema en servicios desplegables de manera independiente.

Ejemplo conceptual:

```
    User Service
    Activity Service
    Pomodoro Service
    Achievement Service
    Reminder Service
```

**Ventajas potenciales:**

- despliegue independiente;
- posibilidad de escalado independiente;
- aislamiento de determinadas fallas;
- fronteras funcionales fuertes cuando están correctamente diseñadas.

**Costos y riesgos:**

- comunicación mediante red;
- fallos parciales;
- mayor superficie de seguridad;
- autenticación entre servicios;
- observabilidad distribuida;
- reintentos y timeouts;
- consistencia distribuida;
- múltiples despliegues;
- mayor complejidad operacional;
- riesgo de crear un monolito distribuido.

---

## 6. Comparación contra los drivers

| Criterio | Monolito por capas | Monolito modular | Microservicios |
|---|---|---|---|
| Rendimiento | Mantiene llamadas internas simples. | Mantiene llamadas internas y un solo proceso. | Puede añadir comunicación remota y latencia entre servicios. |
| Seguridad | Centralización sencilla. | Mantiene seguridad centralizada y añade límites funcionales internos. | Puede aislar responsabilidades, pero incrementa superficie y complejidad. |
| Usabilidad | Impacto principalmente indirecto. | Impacto indirecto mediante mayor mantenibilidad. | No mejora automáticamente la experiencia del estudiante. |
| Disponibilidad | Una caída del backend puede afectar todo. | Sigue existiendo un único proceso desplegable. | Puede permitir aislamiento de fallos si los servicios son realmente independientes. |
| Complejidad | Baja. | Baja a media. | Alta. |
| Separación funcional | Media. | Alta mediante fronteras internas. | Alta mediante servicios independientes. |
| Depuración | Sencilla mientras el sistema permanezca manejable. | Favorecida por módulos claros dentro de un solo proceso. | Más compleja por comunicación y trazas distribuidas. |
| Despliegue | Uno. | Uno. | Varios. |
| Costo actual de adopción | Bajo. | Medio. | Alto. |

Esta comparación es cualitativa y no representa una medición experimental de rendimiento o disponibilidad.

---

## 7. Decisión del equipo

El equipo selecciona:

**Monolito modular.**

### 7.1 Justificación

El equipo considera que es una opción completa y manejable para RachaPro.

Dividir el backend en módulos con responsabilidades claras facilita:

- organización;
- localización de funcionalidades;
- mantenimiento;
- depuración.

Al mismo tiempo, se conserva un único backend desplegable y se evita introducir actualmente la complejidad operacional propia de microservicios.

Esta decisión no significa que un monolito modular garantice automáticamente mejor rendimiento, seguridad o disponibilidad.

Los atributos de calidad deben continuar siendo evaluados de manera independiente.

---

## 8. Dependencias actuales verificadas

Se realizó una inspección de los imports entre paquetes funcionales del backend.

Se observaron las siguientes dependencias AS-IS:

| Origen | Destino | Archivo observado |
|---|---|---|
| `achievement` | `error` | `AchievementService.kt` |
| `activity` | `category` | `ActivityService.kt` |
| `auth` | `security` | `AuthService.kt` |
| `auth` | `user` | `AuthController.kt`, `AuthService.kt`, `LoginResponse.kt` |
| `category` | `error` | `CategoryService.kt` |
| `pomodoro` | `activity` | `PomodoroSessionService.kt` |
| `reminder` | `activity` | `ReminderService.kt` |
| `security` | `user` | `JwtService.kt` |
| `subtask` | `activity` | `SubtaskService.kt` |
| `user` | `category` | `UserService.kt` |
| `user` | `error` | `UserService.kt` |

Esta tabla describe evidencia estática del estado actual.

No representa todavía las reglas del diseño modular objetivo.

---

## 9. Mapa modular objetivo TO-BE

El equipo define cinco módulos principales:

```
    RachaPro Backend
    |
    +-- Identity
    |   +-- usuarios
    |   +-- autenticación
    |   +-- seguridad / JWT
    |
    +-- Activities
    |   +-- actividades
    |   +-- subtareas
    |   +-- categorías
    |
    +-- Focus
    |   +-- sesiones Pomodoro
    |
    +-- Progress
    |   +-- progreso
    |   +-- logros
    |
    +-- Reminders
        +-- recordatorios
```

---

## 10. Responsabilidad de cada módulo

| Módulo | Responsabilidad principal |
|---|---|
| Identity | Gestionar usuarios, autenticación e identidad autenticada. |
| Activities | Gestionar actividades, subtareas y categorías. |
| Focus | Gestionar sesiones de enfoque y Pomodoro. |
| Progress | Gestionar progreso y logros. |
| Reminders | Gestionar recordatorios. |

El equipo decidió agrupar actividades, subtareas y categorías dentro del mismo módulo debido a su cohesión funcional.

---

## 11. Correspondencia AS-IS hacia TO-BE

### 11.1 Identity

Agrupa conceptualmente:

- `auth`;
- `security`;
- `user`.

Por tanto, dependencias actuales como:

```
    auth -> security
    auth -> user
    security -> user
```

pasarían a ser relaciones internas del módulo Identity.

### 11.2 Activities

Agrupa:

- `activity`;
- `subtask`;
- `category`.

Las dependencias:

```
    activity -> category
    subtask -> activity
```

quedarían dentro del mismo módulo.

### 11.3 Focus

Corresponde principalmente a la funcionalidad actual de:

`pomodoro`.

### 11.4 Progress

Corresponde conceptualmente a:

- logros;
- progreso;
- reglas relacionadas con seguimiento.

La implementación actual de progreso no está concentrada exclusivamente en un único módulo backend, por lo que Progress se considera especialmente una definición objetivo.

### 11.5 Reminders

Corresponde principalmente a:

`reminder`.

---

## 12. Dependencias entre módulos

### 12.1 Focus hacia Activities

Actualmente existe evidencia de:

```
    pomodoro -> activity
```

En el diseño objetivo esta relación se interpreta conceptualmente como:

```
    Focus -> Activities
```

Una sesión Pomodoro puede estar relacionada con una actividad.

El módulo Focus no debe acceder libremente a detalles internos de persistencia de Activities.

---

### 12.2 Reminders hacia Activities

Actualmente existe:

```
    reminder -> activity
```

En el diseño objetivo equivale a:

```
    Reminders -> Activities
```

Un recordatorio puede relacionarse con una actividad.

Reminders deberá utilizar solamente mecanismos públicos permitidos por Activities.

---

### 12.3 Identity hacia Activities

Actualmente existe:

```
    user -> category
```

En la organización objetivo esto cruza la frontera entre:

```
    Identity -> Activities
```

Esta dependencia requiere tratamiento durante una futura modularización.

El documento no decide todavía si deberá:

- conservarse mediante una interfaz pública;
- reorganizarse;
- resolverse mediante otro mecanismo.

---

## 13. Progress y flujo de información

El equipo decidió que Progress podrá recibir información proveniente de Activities y Focus.

Dirección funcional objetivo:

```
    Activities ------> Progress
    Focus -----------> Progress
```

El propósito es permitir que Progress gestione información necesaria para progreso y logros.

Las flechas expresan flujo conceptual de información.

No significan que `ActivityService` o `PomodoroSessionService` deban acceder directamente a repositories de Progress.

Actualmente no se verificó en el backend una dependencia:

```
    achievement -> activity
```

ni:

```
    achievement -> pomodoro
```

Por lo tanto, esta relación se considera TO-BE y no debe documentarse como un hecho actual.

El mecanismo técnico definitivo de comunicación queda pendiente.

---

## 14. Regla principal de encapsulamiento

Cada módulo debe ser dueño de:

- su responsabilidad;
- su lógica interna;
- su persistencia lógica;
- los contratos que expone hacia otros módulos.

Otro módulo puede utilizar únicamente mecanismos públicos permitidos.

Ejemplo conceptual deseado:

```
    Reminders
        |
        v
    Contrato público de Activities
        |
        v
    lógica interna de Activities
        |
        v
    ActivityRepository
```

Ejemplo no deseado:

```
    ReminderService
        |
        v
    ActivityRepository
```

si `ActivityRepository` se considera interno de Activities.

---

## 15. PostgreSQL compartido

La arquitectura seleccionada mantiene PostgreSQL como persistencia central.

Sin embargo, compartir físicamente la misma base de datos no significa que cualquier módulo pueda acceder libremente a cualquier estructura lógica perteneciente a otro módulo.

Una solución con carpetas separadas, pero con acceso indiscriminado a repositories y datos, produciría modularidad únicamente nominal.

Por ello se establece como principio objetivo:

> Cada módulo conserva propiedad lógica sobre sus datos aunque la infraestructura PostgreSQL sea compartida.

El mecanismo concreto para hacer cumplir esta regla aún no se ha definido.

---

## 16. Cohesión

Se busca mantener juntas las funcionalidades que poseen una relación funcional fuerte.

Por ejemplo:

```
    Activities
    |
    +-- actividades
    +-- subtareas
    +-- categorías
```

Estas responsabilidades pertenecen a la organización académica de actividades y fueron agrupadas por decisión del equipo.

Esta agrupación busca mantener alta cohesión dentro del módulo.

---

## 17. Acoplamiento

Se busca reducir el conocimiento que un módulo posee sobre detalles internos de otro.

Ejemplo que debe evitarse:

```
    Focus
       |
       v
    ActivityRepository
```

Ejemplo conceptual objetivo:

```
    Focus
       |
       v
    contrato permitido de Activities
```

Esto busca limitar el impacto de cambios internos.

---

## 18. Antipatrones considerados

### 18.1 Big Ball of Mud

Ocurre cuando las dependencias pierden límites y cualquier parte del sistema puede depender de cualquier otra.

El diseño modular busca evitar esta evolución.

### 18.2 God Service

Debe evitarse que un único servicio concentre responsabilidades como:

- actividades;
- Pomodoro;
- progreso;
- logros;
- recordatorios;
- usuarios.

Las responsabilidades deben permanecer delimitadas.

### 18.3 Microservicios prematuros

El equipo no identificó evidencia actual que obligue a separar RachaPro en múltiples servicios desplegables.

Adoptar microservicios únicamente porque son una alternativa moderna introduciría complejidad sin una necesidad demostrada.

### 18.4 Monolito distribuido

Separar servicios físicamente mientras mantienen dependencias fuertes entre sí introduciría la complejidad de sistemas distribuidos sin conseguir independencia real.

Este riesgo fue considerado al no seleccionar microservicios.

---

## 19. Relación con EXP-003

EXP-003 evaluó:

`GET /api/activities`

con un dataset sintético de:

- 500 usuarios;
- 1.000 actividades por usuario;
- 500.000 actividades totales.

Se utilizaron escenarios de:

- 10 VUs;
- 50 VUs;
- 100 VUs;
- 250 VUs;
- 500 VUs.

En todos los escenarios:

- 100 % de los GET evaluados resultaron exitosos;
- 100 % de los usuarios recibió exactamente 1.000 actividades.

En 500 VUs:

- promedio: 1.863,11 ms;
- P95: 3.112,53 ms;
- 500/500 respuestas correctas.

La evidencia completa se encuentra en:

`experimentos/EXP-003-k6-carga-activities/README.md`

Estos resultados:

- no demuestran escalabilidad ilimitada;
- no representan producción;
- no demuestran que modularizar mejore automáticamente el rendimiento;
- no demuestran una necesidad inmediata de adoptar microservicios únicamente por rendimiento.

---

## 20. Implicaciones sobre disponibilidad

Un monolito modular sigue siendo un único backend desplegable.

Aunque internamente existan:

```
    Identity
    Activities
    Focus
    Progress
    Reminders
```

una caída del proceso Spring Boot puede afectar todas las funcionalidades remotas.

Por tanto, la modularización lógica:

- no garantiza alta disponibilidad;
- no elimina el punto único de fallo del backend;
- no sustituye redundancia;
- no sustituye estrategias de recuperación.

---

## 21. Implicaciones sobre seguridad

El sistema actual ya utiliza:

- Spring Security;
- JWT;
- autenticación;
- extracción de identidad autenticada.

El diseño modular no implica sustituir automáticamente dichos mecanismos.

Identity representa conceptualmente las responsabilidades de autenticación, seguridad y usuarios.

Esto tampoco significa que cada módulo deba realizar llamadas explícitas hacia Identity en cada operación.

El diseño debe respetar los mecanismos actuales salvo que una decisión posterior establezca un cambio.

---

## 22. Evaluación crítica de propuestas de IA

Durante Semana 7 se utilizó IA para:

- proponer estilos candidatos;
- explicar sus diferencias;
- sugerir un posible mapa modular;
- identificar riesgos y antipatrones.

Las propuestas fueron evaluadas por el equipo y no se adoptaron automáticamente.

| Propuesta o supuesto de IA | Evaluación del equipo |
|---|---|
| Seleccionar monolito modular | Aceptada después de comparar alternativas. |
| Utilizar Identity, Activities, Focus, Progress y Reminders | Aceptada como mapa objetivo. |
| Agrupar actividades, subtareas y categorías | Aceptada por cohesión. |
| Permitir que Progress reciba información de Activities y Focus | Aceptada como dirección funcional objetivo. |
| La presencia actual de paquetes demuestra que ya existe un monolito modular | Rechazada. |
| Modularizar garantiza mejor rendimiento | Rechazada por falta de evidencia. |
| Modularizar elimina problemas de disponibilidad | Rechazada. Continúa existiendo un único backend. |
| Una base PostgreSQL compartida no genera riesgo de acoplamiento | Considerada incompleta. |
| La migración al diseño objetivo es sencilla | No demostrado. |
| Los eventos internos deben ser el mecanismo definitivo | No decidido. |

---

## 23. Riesgos de la decisión

### R-01 — Modularidad solo nominal

Puede ocurrir si se crean módulos o carpetas, pero los servicios continúan accediendo libremente a implementaciones internas de otros módulos.

### R-02 — Acoplamiento mediante PostgreSQL

La base compartida puede convertirse en un mecanismo de acoplamiento si cualquier módulo utiliza libremente datos que pertenecen lógicamente a otro.

### R-03 — Regresiones durante la reorganización

Modificar paquetes, servicios y dependencias puede introducir errores sobre funcionalidades actualmente operativas.

### R-04 — Dependencias circulares

Debe evitarse una relación como:

```
    Activities -> Progress -> Activities
```

La dirección técnica de dependencias deberá diseñarse para impedir ciclos.

### R-05 — Confundir modularidad con disponibilidad

La separación lógica interna no evita que una caída del proceso Spring Boot afecte el backend completo.

---

## 24. AS-IS frente a TO-BE

| Elemento | AS-IS | TO-BE |
|---|---|---|
| Backend | Una aplicación Spring Boot | Una aplicación Spring Boot |
| Organización principal documentada | Capas técnicas | Módulos funcionales |
| Despliegue | Uno | Uno |
| PostgreSQL | Compartido | Compartido con propiedad lógica por módulos |
| Auth, Security y User | Paquetes separados | Identity |
| Activity, Subtask y Category | Paquetes separados | Activities |
| Pomodoro | Paquete funcional | Focus |
| Achievement / progreso | Responsabilidades actualmente distribuidas | Progress |
| Reminder | Paquete funcional | Reminders |
| Reglas formales entre módulos | No demostradas | Deben existir |
| Focus -> Activities | Dependencia actual comprobada | Permitida mediante límite controlado |
| Reminders -> Activities | Dependencia actual comprobada | Permitida mediante límite controlado |
| Activities/Focus -> Progress | No comprobada actualmente en backend | Flujo objetivo confirmado |
| Mecanismo de comunicación modular | Implementación actual directa según clases | Pendiente de decisión |

---

## 25. Decisiones pendientes

Este documento no decide todavía:

1. mecanismo técnico exacto de comunicación entre módulos;
2. uso de interfaces internas o eventos;
3. herramienta para verificar reglas de dependencia;
4. solución definitiva para `user -> category`;
5. estructura física final de paquetes;
6. estrategia de migración incremental;
7. mecanismos adicionales de alta disponibilidad.

Estas decisiones pueden documentarse posteriormente mediante ADR.

---

## 26. Conclusión

El equipo seleccionó un monolito modular como estilo arquitectónico objetivo para el backend de RachaPro.

Los módulos definidos son:

```
    Identity
    Activities
    Focus
    Progress
    Reminders
```

La decisión busca combinar:

- manejabilidad;
- organización;
- cohesión funcional;
- control del acoplamiento;
- facilidad de mantenimiento y depuración;

sin introducir actualmente la complejidad operacional de microservicios.

La arquitectura modular se considera un diseño TO-BE.

No se afirma que esté completamente implementada en el estado actual del repositorio ni que garantice automáticamente rendimiento, seguridad o disponibilidad.