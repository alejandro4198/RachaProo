# ADR-001 — Adoptar un monolito modular para el backend de RachaPro

- **Estado:** Aceptada
- **Fecha de decisión:** 2026-09-09
- **Formalización:** Semana 8
- **Materialización:** completada durante Semana 8
- **C4 AS-IS post-refactor:** `docs/semana8/c4/c4-l3-backend-modular.md`
- **Ámbito:** Backend Spring Boot
- **Tipo:** Decisión de estilo arquitectónico

---

## 1. Contexto

RachaPro dispone actualmente de un único backend Spring Boot conectado a PostgreSQL.

El backend contiene funcionalidades relacionadas con:

- usuarios;
- autenticación y seguridad;
- actividades;
- categorías;
- subtareas;
- sesiones Pomodoro;
- logros;
- recordatorios.

El sistema ya posee paquetes funcionales, pero la existencia de dichos paquetes no demuestra por sí sola que existan fronteras modulares formalmente controladas.

El estado previo a la materialización de estas fronteras se conserva en:

`docs/semana8/baseline-pre-modular.md`

Las fronteras funcionales y el ownership de datos definidos posteriormente se encuentran en:

`docs/semana8/fronteras-modulares.md`

---

## 2. Problema

El backend debe evolucionar sin perder la simplicidad operacional de una única aplicación desplegable, pero reduciendo el riesgo de que las funcionalidades terminen altamente acopladas entre sí.

Mantener únicamente una organización por responsabilidades técnicas o permitir dependencias libres entre funcionalidades puede provocar:

- crecimiento de dependencias cruzadas;
- límites de negocio poco claros;
- dificultad para localizar responsabilidades;
- mayor impacto de cambios internos;
- riesgo de evolucionar hacia un Big Ball of Mud.

Al mismo tiempo, dividir inmediatamente el sistema en múltiples servicios desplegables introduciría problemas distribuidos que actualmente no han sido demostrados como necesarios.

---

## 3. Drivers arquitectónicos

Los atributos de calidad priorizados por el equipo son:

1. rendimiento;
2. seguridad;
3. usabilidad;
4. disponibilidad.

Para esta decisión también son relevantes:

- mantenibilidad;
- cohesión;
- acoplamiento;
- facilidad de depuración;
- complejidad operacional;
- costo de adopción.

La selección de un estilo arquitectónico no implica que dicho estilo garantice automáticamente estos atributos.

Cada atributo deberá continuar siendo validado con evidencia independiente.

---

## 4. Alternativas consideradas

### 4.1 Monolito por capas

Mantener una única aplicación Spring Boot organizada principalmente alrededor de responsabilidades técnicas.

Ejemplo conceptual:

    Controllers
        ↓
    Services
        ↓
    Repositories
        ↓
    PostgreSQL

#### Ventajas

- estructura sencilla;
- un único despliegue;
- baja complejidad operacional;
- llamadas internas directas;
- seguridad centralizada.

#### Riesgos

- crecimiento de dependencias cruzadas;
- fronteras funcionales débiles;
- servicios con demasiadas responsabilidades;
- mayor riesgo de Big Ball of Mud.

---

### 4.2 Monolito modular

Mantener una única aplicación Spring Boot y un único despliegue, pero estructurar el backend mediante módulos funcionales con:

- responsabilidades explícitas;
- alta cohesión interna;
- dependencias controladas;
- contratos públicos entre módulos;
- ownership lógico de datos.

Se conserva PostgreSQL como infraestructura de persistencia compartida.

---

### 4.3 Microservicios

Separar capacidades del sistema en varios servicios desplegables independientemente.

#### Ventajas potenciales

- despliegue independiente;
- escalado independiente;
- posibilidad de aislamiento de determinadas fallas;
- fronteras fuertes cuando están correctamente diseñadas.

#### Costos y riesgos

- comunicación mediante red;
- fallos parciales;
- reintentos y timeouts;
- observabilidad distribuida;
- autenticación entre servicios;
- mayor superficie de seguridad;
- consistencia distribuida;
- múltiples despliegues;
- mayor complejidad operacional;
- riesgo de construir un monolito distribuido.

Los microservicios no se consideran una alternativa incorrecta en términos generales.

La decisión es que su complejidad adicional no se encuentra actualmente justificada por las necesidades demostradas de RachaPro.

---

## 5. Evaluación cualitativa contra los drivers

| Criterio | Monolito por capas | Monolito modular | Microservicios |
| --- | --- | --- | --- |
| Rendimiento | Mantiene llamadas internas simples | Mantiene llamadas internas y un solo proceso | Puede añadir comunicación remota y latencia entre servicios |
| Seguridad | Centralización sencilla | Mantiene seguridad centralizada y añade límites funcionales internos | Puede aislar responsabilidades, pero incrementa superficie y complejidad |
| Usabilidad | Impacto principalmente indirecto | Impacto indirecto mediante mayor mantenibilidad | No mejora automáticamente la experiencia del estudiante |
| Disponibilidad | Una caída del backend puede afectar todo | Sigue existiendo un único proceso desplegable | Puede permitir aislamiento de fallos si los servicios son realmente independientes |
| Complejidad operacional | Baja | Baja-media | Alta |
| Separación funcional | Media | Alta mediante fronteras internas | Alta mediante servicios independientes |
| Depuración | Sencilla mientras el sistema permanezca manejable | Favorecida por módulos claros dentro de un solo proceso | Más compleja por comunicación y trazas distribuidas |
| Número de despliegues | Uno | Uno | Varios |
| Costo actual de adopción | Bajo | Medio | Alto |

Esta comparación es cualitativa y no representa una medición experimental de rendimiento o disponibilidad.

---

## 6. Decisión

Se adopta un:

**Monolito modular**

como arquitectura objetivo del backend de RachaPro.

El backend continuará siendo:

- una aplicación Spring Boot;
- un único proceso desplegable;
- una unidad de despliegue;
- conectado a PostgreSQL como persistencia central.

La organización arquitectónica principal pasará a basarse en capacidades funcionales.

Los cinco módulos principales definidos son:

    Identity
    Activities
    Focus
    Progress
    Reminders

Los elementos técnicos realmente transversales podrán permanecer en un espacio Shared limitado.

---

## 7. Responsabilidades modulares

### Identity

Responsable de:

- usuarios;
- autenticación;
- seguridad asociada;
- identidad autenticada.

### Activities

Responsable de:

- actividades;
- categorías;
- subtareas.

### Focus

Responsable de:

- sesiones de enfoque;
- Pomodoro.

### Progress

Responsable de:

- progreso;
- logros.

### Reminders

Responsable de:

- recordatorios.

El detalle actualizado de ownership se mantiene en:

`docs/semana8/fronteras-modulares.md`

---

## 8. Justificación

El monolito modular permite introducir fronteras funcionales explícitas sin asumir actualmente los costos operacionales de un sistema distribuido.

La decisión busca:

- mantener funcionalidades relacionadas juntas;
- aumentar cohesión;
- reducir conocimiento de detalles internos entre módulos;
- facilitar mantenimiento;
- facilitar localización de código;
- facilitar depuración;
- conservar un único despliegue.

La decisión no se toma porque un monolito modular sea universalmente superior.

Se toma porque representa el balance considerado más adecuado para el contexto actual de RachaPro.

---

## 9. Consecuencias positivas

Se esperan las siguientes consecuencias estructurales:

- ownership explícito de responsabilidades;
- mejor delimitación funcional;
- dependencias intermodulares controlables;
- posibilidad de proteger reglas mediante fitness functions;
- menor exposición directa de infraestructura interna;
- estructura más trazable hacia diagramas C4;
- conservación de una única unidad de despliegue.

Estas consecuencias corresponden al diseño objetivo.

Deberán verificarse después de la implementación.

---

## 10. Consecuencias negativas

La decisión también introduce costos:

- refactorización del código existente;
- diseño explícito de contratos entre módulos;
- necesidad de resolver dependencias cruzadas;
- necesidad de pruebas de regresión;
- necesidad de mantener reglas arquitectónicas;
- riesgo de crear modularidad únicamente nominal.

El sistema continúa siendo un único proceso.

Por ello, la modularización no elimina el punto único de fallo correspondiente al backend desplegado.

---

## 11. Trade-offs

### Simplicidad operacional vs. aislamiento físico

Se conserva una única unidad desplegable.

Esto reduce complejidad operacional, pero no ofrece el aislamiento de procesos que podría proporcionar una arquitectura distribuida.

### Comunicación directa vs. encapsulamiento

Las llamadas permanecen dentro del mismo proceso.

Sin embargo, no se permitirá utilizar esta proximidad como justificación para acceder libremente a detalles internos de otros módulos.

### PostgreSQL compartido vs. ownership lógico

Los módulos compartirán físicamente PostgreSQL.

Aun así, cada módulo deberá mantener ownership lógico sobre sus datos.

El mecanismo concreto para imponer este aislamiento corresponde a una decisión posterior.

---

## 12. Riesgos

### R-01 — Modularidad nominal

Crear carpetas con nombres de módulos sin controlar dependencias internas produciría únicamente una apariencia modular.

### R-02 — Acoplamiento mediante persistencia

El uso directo de repositories o entidades pertenecientes a otro módulo puede romper las fronteras definidas.

### R-03 — Dependencias circulares

El refactor puede introducir ciclos entre módulos si no se establece una dirección de dependencia explícita.

### R-04 — Regresiones

Mover clases y modificar dependencias puede afectar comportamiento actualmente operativo.

### R-05 — Sobreinterpretar la decisión

El monolito modular no debe presentarse como una garantía automática de:

- mayor rendimiento;
- mayor seguridad;
- mayor disponibilidad.

---

## 13. Mitigaciones

Se utilizarán las siguientes medidas:

- definir ownership antes del refactor;
- documentar dependencias permitidas y prohibidas;
- introducir contratos intermodulares explícitos;
- evitar acceso directo a repositories ajenos;
- mantener cambios incrementales;
- ejecutar pruebas después de cada etapa;
- incorporar fitness functions;
- evaluar ArchUnit para reglas ejecutables;
- actualizar C4 únicamente cuando exista correspondencia con código real.

---

## 14. Evidencia

### Decisión de Semana 7

La evaluación detallada de alternativas y el mapa modular objetivo se encuentran en:

`dossier/08-decision-estilo-arquitectonico.md`

Ese documento constituye el análisis previo utilizado por el equipo para seleccionar el monolito modular.

### Baseline pre-modular

El estado previo a la implementación se encuentra congelado en:

`docs/semana8/baseline-pre-modular.md`

### Fronteras y ownership

La definición posterior de fronteras, ownership de datos y dependencias AS-IS detectadas se encuentra en:

`docs/semana8/fronteras-modulares.md`

### Evidencia histórica de rendimiento

EXP-002 evaluó:

`POST /api/activities`

con:

- 1 VU;
- 25 iteraciones;
- 25/25 creaciones exitosas;
- promedio de 11.16 ms;
- P95 de 31.50 ms;
- máximo de 46.02 ms.

EXP-002 no evaluó concurrencia ni el flujo Android end-to-end.

Por tanto, esta evidencia no demuestra escalabilidad ni constituye por sí sola la razón para seleccionar un monolito modular.

Tampoco aporta evidencia que obligue a adoptar microservicios únicamente por rendimiento.

Evidencia:

`experimentos/EXP-002-k6-api-activities/`

---

## 15. Relación con C4

Los diagramas C4 históricos anteriores a la modularización deberán conservarse como evidencia AS-IS.

Después del refactor, el C4 de componentes deberá actualizarse para representar únicamente módulos y relaciones que existan realmente en el repositorio.

No se representará la arquitectura TO-BE como implementada antes de que el código la materialice.

---

## 16. Verificación de la decisión

La materialización de este ADR deberá poder comprobarse mediante evidencia del repositorio.

Se verificará que:

1. existan fronteras físicas correspondientes a los módulos definidos;
2. las clases estén asignadas al módulo correcto;
3. los repositories pertenezcan al módulo propietario de los datos;
4. las dependencias entre módulos respeten contratos explícitos;
5. no existan accesos directos prohibidos hacia infraestructura de otros módulos;
6. las reglas arquitectónicas puedan comprobarse mediante fitness functions;
7. el backend continúe compilando y superando las pruebas aplicables;
8. el C4 actualizado corresponda al código implementado.

---

## 17. Fuera del alcance de este ADR

Este ADR no decide:

- el mecanismo exacto de comunicación entre módulos;
- si los contratos se implementarán mediante interfaces, facades o eventos;
- la regla detallada de aislamiento de persistencia;
- la solución técnica definitiva de cada dependencia cruzada;
- una separación futura en microservicios;
- bases de datos independientes por módulo.

El aislamiento de persistencia y los mecanismos públicos entre módulos serán tratados específicamente en:

`ADR-002 — Aislamiento de persistencia entre módulos`

---

## 18. Estado de implementación

**Decisión arquitectónica:** ✅ ACEPTADA

**Arquitectura modular:** ✅ IMPLEMENTADA

**Fronteras y ownership:** ✅ IMPLEMENTADOS

**Refactor físico:** ✅ IMPLEMENTADO

**Contratos intermodulares:** ✅ IMPLEMENTADOS

**Fitness functions / ArchUnit:** ✅ IMPLEMENTADAS Y EJECUTADAS

**C4 post-refactor:** ✅ ACTUALIZADO

**Validación funcional y de rendimiento relevante:** ✅ EJECUTADA CON LAS LIMITACIONES DOCUMENTADAS

La implementación resultante conserva una única aplicación Spring Boot y materializa las fronteras Identity, Activities, Focus, Progress y Reminders.

El aislamiento de persistencia y los contratos públicos se detallan en ADR-002.

La evidencia de cierre se conserva en `docs/semana8/`, `experimentos/` y en el tag `semana8-final-validado`.
