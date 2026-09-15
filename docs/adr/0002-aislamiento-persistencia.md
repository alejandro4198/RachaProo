# ADR-002 — Aislamiento de persistencia y contratos entre módulos

- **Estado:** Aceptada
- **Fecha de decisión:** 2026-09-14
- **Ámbito:** Backend Spring Boot
- **Relacionada con:** ADR-001
- **Implementación:** Materializada

---

## 1. Contexto

ADR-001 establece un monolito modular como arquitectura objetivo del backend de RachaPro.

El mapa modular definido es:

    Identity
    Activities
    Focus
    Progress
    Reminders

El sistema continuará utilizando una única aplicación Spring Boot y PostgreSQL compartido.

Por tanto, el aislamiento buscado no consiste en crear bases de datos o procesos independientes, sino en establecer ownership lógico de datos y controlar qué elementos pueden utilizar otros módulos.

Las fronteras y ownership se encuentran definidos en:

`docs/semana8/fronteras-modulares.md`

---

## 2. Problema

La inspección del estado AS-IS demuestra que algunos paquetes acceden directamente a repositories o entidades que, según el mapa modular objetivo, pertenecerán a otro módulo.

Esto genera acoplamiento con detalles internos de persistencia.

Si dicho patrón continúa después del refactor físico, existirían carpetas con nombres de módulos pero no fronteras arquitectónicas reales.

ADR-002 debe definir:

- quién puede utilizar un repository;
- quién puede utilizar una entidad JPA;
- cómo se realizan consultas necesarias entre módulos;
- cómo se solicitan operaciones pertenecientes a otro módulo;
- qué dependencias internas siguen siendo válidas;
- cómo podrán verificarse estas reglas automáticamente.

---

## 3. Evidencia AS-IS

La inspección de imports y servicios identificó los siguientes accesos relevantes:

| Origen actual | Dependencia | Clasificación TO-BE |
| --- | --- | --- |
| `pomodoro` | `ActivityRepository` | Cruce Focus -> Activities |
| `reminder` | `ActivityRepository` | Cruce Reminders -> Activities |
| `user` | `CategoryRepository` | Cruce Identity -> Activities |
| `user` | `CategoryEntity` | Cruce Identity -> Activities |
| `activity` | `CategoryRepository` | Interna a Activities |
| `subtask` | `ActivityRepository` | Interna a Activities |
| `auth` | `UserRepository` | Interna a Identity |

No todos los imports entre paquetes actuales representan violaciones futuras.

La clasificación debe hacerse respecto al módulo de negocio al que pertenecerá cada clase.

---

## 4. Necesidades funcionales observadas

### 4.1 Focus

`PomodoroSessionService` consulta `ActivityRepository` cuando se proporciona un `activityId`.

La necesidad observada es comprobar que la actividad:

- existe;
- pertenece al usuario;
- no está eliminada.

Focus no necesita convertirse en propietario de la persistencia de Activities.

### 4.2 Reminders

`ReminderService` realiza una comprobación equivalente cuando un recordatorio contiene `activityId`.

La necesidad observada también es validar una actividad perteneciente al usuario y no eliminada.

### 4.3 Identity

Durante la creación de un usuario, `UserService` crea las categorías predeterminadas:

- Universidad;
- Personal;
- Trabajo.

Actualmente Identity construye `CategoryEntity` y utiliza `CategoryRepository` directamente.

Según el ownership definido, las categorías pertenecen a Activities.

Identity necesita solicitar la creación de las categorías, pero no necesita conocer cómo Activities las construye o persiste.

---

## 5. Drivers de la decisión

La solución debe favorecer:

- encapsulamiento;
- cohesión;
- bajo acoplamiento;
- trazabilidad;
- facilidad de pruebas;
- preservación del comportamiento actual;
- simplicidad operacional;
- posibilidad de verificación automática.

También debe evitar introducir complejidad distribuida o asíncrona sin una necesidad demostrada.

---

## 6. Alternativas consideradas

### 6.1 Mantener acceso directo a repositories

Cada módulo podría continuar importando repositories de otros módulos.

#### Ventaja

- mínimo cambio inicial.

#### Problemas

- rompe el ownership de persistencia;
- expone detalles internos;
- aumenta acoplamiento;
- permite que cambios internos afecten consumidores externos;
- impediría demostrar fronteras modulares reales.

**Decisión:** rechazada para dependencias intermodulares.

---

### 6.2 Repositories compartidos

Los repositories podrían trasladarse a un paquete común accesible por todos los módulos.

#### Ventaja

- acceso sencillo.

#### Problemas

- elimina ownership claro;
- convierte persistencia en un punto de acoplamiento común;
- permite que cualquier módulo consulte datos ajenos;
- debilita las fronteras definidas en ADR-001.

**Decisión:** rechazada.

---

### 6.3 Servicios concretos públicos

Un módulo podría exponer directamente determinadas clases de servicio para que otros módulos las utilicen.

#### Ventajas

- implementación simple;
- llamadas síncronas dentro del mismo proceso.

#### Riesgos

- puede exponer más operaciones de las necesarias;
- puede hacer que consumidores dependan de detalles de implementación;
- la superficie pública puede crecer sin control.

**Decisión:** no será el mecanismo principal.

---

### 6.4 Contratos públicos mediante interfaces

El módulo propietario define interfaces pequeñas que expresan únicamente las capacidades necesarias para otros módulos.

La implementación permanece dentro del módulo propietario y puede utilizar sus repositories internamente.

#### Ventajas

- superficie pública explícita;
- ocultamiento de repositories y entidades;
- contratos pequeños;
- facilidad para probar consumidores;
- permite cambiar implementación interna sin modificar consumidores;
- mantiene llamadas locales y síncronas.

#### Costo

- requiere introducir contratos adicionales y adaptar dependencias existentes.

**Decisión:** seleccionada.

---

### 6.5 Eventos internos

Un módulo podría publicar eventos consumidos por otros módulos.

#### Ventajas potenciales

- menor acoplamiento temporal;
- múltiples consumidores;
- útil para reacciones posteriores a un hecho de negocio.

#### Costos

- semántica adicional;
- manejo de errores;
- orden;
- posible asincronía;
- mayor dificultad de depuración;
- efectos sobre límites transaccionales.

Los cruces actuales identificados requieren validaciones inmediatas o preservar una operación de creación existente.

**Decisión:** diferida. No se adopta como mecanismo inicial para resolver los cruces actuales.

---

## 7. Decisión

Cada módulo será propietario de:

- sus entidades de persistencia;
- sus repositories;
- las reglas asociadas al acceso de sus datos.

Un módulo externo no podrá importar directamente:

- repositories de otro módulo;
- entidades JPA internas de otro módulo;
- implementaciones internas de persistencia.

Cuando un módulo requiera una capacidad perteneciente a otro, deberá utilizar un contrato público definido por el módulo propietario.

Los contratos públicos serán inicialmente síncronos.

Las interfaces serán propiedad del módulo que ofrece la capacidad, no del consumidor.

---

## 8. API pública requerida de Activities

Los cruces actualmente observados requieren dos capacidades públicas.

### 8.1 ActivityLookup

Contrato conceptual:

    interface ActivityLookup {
        fun existsActiveActivityForUser(
            activityId: Long,
            userId: Long
        ): Boolean
    }

Responsabilidad:

comprobar si una actividad determinada existe, pertenece al usuario indicado y continúa activa según las reglas de Activities.

Su implementación pertenece a Activities y podrá utilizar `ActivityRepository`.

El contrato no expone:

- `ActivityRepository`;
- `ActivityEntity`;
- consultas JPA;
- detalles internos de persistencia.

Consumidores iniciales:

- Focus;
- Reminders.

---

### 8.2 DefaultCategoryProvisioning

Contrato conceptual:

    interface DefaultCategoryProvisioning {
        fun createDefaultsForUser(userId: Long)
    }

Responsabilidad:

crear las categorías predeterminadas correspondientes a un nuevo usuario.

La implementación pertenece a Activities y será responsable de:

- construir las categorías;
- definir los nombres predeterminados;
- persistirlas mediante el repository correspondiente.

Identity únicamente proporcionará el identificador del usuario.

Identity dejará de construir `CategoryEntity` y dejará de importar `CategoryRepository`.

---

## 9. Dependencias permitidas

Después del refactor serán válidas, entre otras, las siguientes relaciones:

    Identity -> Activities API
    Focus -> Activities API
    Reminders -> Activities API

Dentro de Activities seguirán siendo válidas:

    ActivityService -> CategoryRepository
    SubtaskService -> ActivityRepository

Dentro de Identity seguirá siendo válida:

    AuthService -> UserRepository

Estas últimas no son cruces intermodulares porque consumidor y repository pertenecen al mismo módulo.

---

## 10. Dependencias prohibidas

Se prohíben relaciones equivalentes a:

    Focus -> Activities Repository
    Reminders -> Activities Repository
    Identity -> Activities Repository
    Identity -> Activities Entity

Regla general:

    Módulo A -> repository interno del Módulo B

y:

    Módulo A -> entidad interna del Módulo B

son dependencias prohibidas.

La existencia de PostgreSQL compartido no constituye autorización para ignorar estas fronteras.

---

## 11. Referencias entre módulos

Cuando un módulo necesite conservar una referencia hacia información propiedad de otro módulo se preferirán identificadores escalares.

Ejemplos:

- `userId`;
- `activityId`;
- `categoryId`.

No se introducirán asociaciones JPA entre entidades de módulos diferentes como mecanismo para atravesar fronteras.

Esto permite conservar ownership lógico incluso utilizando una única base PostgreSQL.

---

## 12. Semántica síncrona y transaccional

La primera implementación de los contratos será síncrona.

Esto busca reducir cambios semánticos durante la refactorización.

En particular, la creación actual de usuario y categorías ocurre dentro de una operación transaccional.

El refactor deberá preservar ese comportamiento observable.

`DefaultCategoryProvisioning` será invocado dentro de la operación de creación del usuario.

La implementación inicial no deberá introducir:

- procesamiento asíncrono;
- `REQUIRES_NEW`;
- colas;
- consistencia eventual.

Una modificación futura de esta semántica requerirá una nueva decisión arquitectónica y evidencia que la justifique.

---

## 13. Superficie pública de los módulos

Un módulo podrá exponer explícitamente un espacio de API pública.

Conceptualmente:

    activities
        api
            ActivityLookup
            DefaultCategoryProvisioning
        ...
        implementación interna

Otros módulos podrán depender de los contratos públicos, pero no de los detalles internos.

No es obligatorio crear subpaquetes vacíos o capas que no aporten valor.

La estructura interna podrá evolucionar mientras preserve las fronteras definidas.

---

## 14. Shared

Shared se reserva para responsabilidades técnicas realmente transversales.

No se utilizará Shared para alojar:

- repositories de negocio;
- entidades de negocio;
- contratos creados únicamente para evitar ownership;
- acceso común a tablas pertenecientes a módulos específicos.

Mover una dependencia a Shared no se considerará una solución válida si solamente oculta un acoplamiento de negocio existente.

---

## 15. Fitness functions

Después de materializar los módulos deberán existir reglas ejecutables capaces de comprobar como mínimo que:

1. ningún módulo accede directamente al repository de otro módulo;
2. ningún módulo accede directamente a entidades internas de otro módulo;
3. las dependencias intermodulares atraviesan APIs públicas autorizadas;
4. no existen ciclos entre módulos;
5. Shared no contiene repositories o entidades de negocio pertenecientes a un módulo;
6. las dependencias internas válidas no son reportadas erróneamente como violaciones.

ArchUnit será evaluado como mecanismo para implementar estas reglas.

La existencia de este ADR no implica todavía que dichas fitness functions estén implementadas.

---

## 16. Consecuencias positivas

Se espera:

- ownership de persistencia explícito;
- menor exposición de detalles internos;
- contratos intermodulares pequeños;
- mejor trazabilidad de dependencias;
- posibilidad de automatizar reglas arquitectónicas;
- menor impacto cuando cambie una implementación interna.

Estas consecuencias deberán ser verificadas después del refactor.

---

## 17. Consecuencias negativas

La decisión introduce:

- interfaces adicionales;
- implementaciones adaptadoras;
- cambios en inyección de dependencias;
- trabajo de refactorización;
- necesidad de mantener una superficie pública deliberada.

También requiere evitar que las APIs públicas crezcan hasta convertirse en nuevos puntos de acoplamiento excesivo.

---

## 18. Riesgos y mitigaciones

### R-01 — Interfaces demasiado amplias

**Riesgo:** convertir la API pública en una exposición indirecta de toda la implementación.

**Mitigación:** contratos pequeños orientados a capacidades concretas.

### R-02 — Falsa modularidad

**Riesgo:** mantener imports directos y limitarse a mover carpetas.

**Mitigación:** fitness functions y revisión de imports después del refactor.

### R-03 — Cambiar comportamiento durante el refactor

**Riesgo:** modificar validaciones o semántica transaccional accidentalmente.

**Mitigación:** refactor incremental y pruebas de regresión.

### R-04 — Ciclos entre módulos

**Riesgo:** introducir dependencias bidireccionales.

**Mitigación:** contratos de dirección explícita y fitness function de ciclos.

### R-05 — Abusar de Shared

**Riesgo:** utilizar Shared para evitar decidir ownership.

**Mitigación:** Shared no poseerá persistencia de negocio.

---

## 19. Alternativas futuras

Eventos internos podrán reconsiderarse cuando exista una necesidad demostrada como:

- múltiples consumidores independientes;
- procesamiento posterior al commit;
- desacoplamiento temporal;
- efectos secundarios que no deban formar parte de la operación principal.

Una separación futura en microservicios tampoco queda impedida por esta decisión.

Sin embargo, ninguna de estas alternativas es requerida para resolver los cruces actualmente observados.

---

## 20. Evidencia

Esta decisión se apoya en:

`docs/adr/0001-decision-estilo.md`

`docs/semana8/fronteras-modulares.md`

`docs/semana8/baseline-pre-modular.md`

y en la inspección del código actual de:

- `UserService.kt`;
- `PomodoroSessionService.kt`;
- `ReminderService.kt`;
- `SubtaskService.kt`;
- `ActivityService.kt`;
- `AuthService.kt`.

---

## 21. Criterios de validación

ADR-002 se considerará materializado cuando:

1. Focus deje de importar `ActivityRepository`;
2. Reminders deje de importar `ActivityRepository`;
3. Identity deje de importar `CategoryRepository`;
4. Identity deje de construir `CategoryEntity`;
5. Activities proporcione el contrato equivalente a `ActivityLookup`;
6. Activities proporcione el contrato equivalente a `DefaultCategoryProvisioning`;
7. las dependencias internas válidas continúen funcionando;
8. el backend compile;
9. las pruebas aplicables permanezcan exitosas;
10. las reglas de aislamiento puedan comprobarse mediante fitness functions;
11. no existan dependencias circulares entre módulos.

---

## 22. Estado de implementación

**Decisión:** ✅ ACEPTADA

**Ownership:** ⚪ CRITERIO DEFINIDO

**Contratos públicos:** ✅ IMPLEMENTADOS

**Cruces directos identificados:** ✅ ELIMINADOS

**Refactor físico:** ✅ IMPLEMENTADO

**Fitness functions:** ✅ IMPLEMENTADAS

Este ADR define una regla materializada en código mediante contratos públicos entre módulos y verificada mediante fitness functions con ArchUnit.