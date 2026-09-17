# Matriz de trazabilidad arquitectónica — Semanas 1 a 8

## 1. Propósito

Relacionar la evolución académica y arquitectónica de RachaPro con evidencia verificable del repositorio.

El documento tiene dos objetivos complementarios:

1. mantener trazabilidad académica de los principales entregables realizados entre las Semanas 1 y 8;
2. relacionar las decisiones, arquitectura, código, fitness functions y experimentos materializados en Semana 8.

La trazabilidad distingue entre:

- evidencia histórica;
- evidencia posterior;
- decisiones arquitectónicas;
- estructura implementada;
- contratos intermodulares;
- fitness functions;
- experimentos;
- documentación C4;
- elementos AS-IS;
- elementos TO-BE o todavía pendientes.

Estados utilizados:

- ✅ CONCUERDA
- ✅ TRAZABLE
- 🟡 PARCIAL
- 🔴 CONTRADICCIÓN
- 🧪 PRUEBA PENDIENTE
- ⚪ CRITERIO DEFINIDO
- 🕰 HISTÓRICO
- 🏗 AS-IS
- 🎯 TO-BE

---

## 2. Trazabilidad académica — Semanas 1 a 8

Esta sección relaciona los principales entregables académicos con evidencia versionada existente en el repositorio.

La tabla no reconstruye retrospectivamente la historia del proyecto. Cuando una evidencia fue completada, corregida o revalidada después de la semana correspondiente, se identifica expresamente como evidencia posterior.

| Semana | Alcance principal | Evidencia versionada | PR / integración verificable | Estado y observaciones |
|---|---|---|---|---|
| Semana 1 | Contexto del sistema y preparación de ejecución local | documentación inicial y `docs/ejecucion-local.md` | PR #1; merge `0b8cdea`; commit fuente `e6ce494` | 🕰 HISTÓRICO. La evidencia original se conserva. El procedimiento reproducible del backend fue reforzado posteriormente y no se presenta como evidencia producida originalmente en Semana 1. |
| Semana 2 | Stakeholders, drivers, riesgos y checkpoint arquitectónico | documentación de riesgos, drivers priorizados y checkpoint | PR #2, merge `1265559`; PR #3, merge `42f021c` | ✅ TRAZABLE. Existen integraciones históricas verificables para los principales artefactos de la semana. |
| Semana 3 | Atributos y escenarios de calidad | documentación de atributos de calidad y escenarios | PR #4; merge `42806fa`; commit fuente `9640427` | ✅ TRAZABLE. Las reformulaciones posteriores se consideran ajustes posteriores y no evidencia original de la semana. |
| Semana 4 | Experimentación de rendimiento: EXP-001 y EXP-002 | `experimentos/` y evidencia histórica de EXP-002 | PR #5; merge `28089d4`; revalidación posterior integrada en `c2195d1` | ✅ TRAZABLE. EXP-002 conserva su resultado histórico de P95 = 31.50 ms. El 17/09/2026 se realizó una revalidación formal separada con cuatro corridas, primera descartada como warm-up y tres válidas; P95 representativo = 16.61 ms. La revalidación no reemplaza ni reinterpreta causalmente el resultado histórico. |
| Semana 5 | Modelado C4 Nivel 1 y Nivel 2 | documentación C4 de contexto y contenedores | PR #6; merge `b3f393e`; commit fuente `f83b5f1` | ✅ TRAZABLE. La arquitectura documentada corresponde a la etapa histórica de esa semana. |
| Semana 6 | Modelado C4 Nivel 3 / componentes | documentación de componentes | PR #7; merge `277a2f9`; commit fuente `55d5b21` | ✅ TRAZABLE. La evidencia permanece versionada y posteriormente fue contrastada con la arquitectura modular de Semana 8. |
| Semana 7 | Evaluación de estilos y decisión arquitectónica | documentación de estilo arquitectónico y decisión | PR #10; merge `d21069b`; commit fuente `04718a3` | ✅ TRAZABLE. La decisión sirve como antecedente para la materialización posterior del monolito modular. |
| Semana 8 | Materialización del monolito modular, ADR, fronteras, C4 actualizado, ArchUnit, CI, experimentos y validaciones | `docs/adr/`, `docs/semana8/`, tests ArchUnit, workflow CI y experimentos de Semana 8 | PRs #12–#33 asociados al proceso de Semana 8; PR #31 se utilizó como prueba negativa deliberada y fue cerrado sin merge | ✅ TRAZABLE. Existe evidencia de implementación, validación estructural, CI y experimentación. El veredicto del mini-comité permanece pendiente y no debe incorporarse retrospectivamente antes de su realización. |

### 2.1 Evidencia posterior que refuerza semanas anteriores

Dos actividades realizadas el 17/09/2026 mejoran la reproducibilidad y trazabilidad académica sin modificar la historia de las semanas originales.

#### Revalidación posterior de EXP-002

La ejecución histórica del 05/09/2026 se conserva sin cambios:

- 1 VU;
- 25 iteraciones;
- 25 de 25 creaciones exitosas;
- P95 = 31.50 ms.

La revalidación posterior se ejecutó sobre un commit identificado y utilizó:

- cuatro corridas independientes;
- primera corrida conservada como warm-up y excluida del agregado;
- tres corridas válidas;
- 25 de 25 creaciones exitosas en cada corrida válida;
- P95 representativo mediante la mediana de las tres corridas válidas: 16.61 ms;
- condiciones de máquina, herramienta, energía, logs y datos crudos conservados.

Este resultado refuerza la reproducibilidad de EXP-002.

No demuestra por sí mismo una mejora causal entre versiones arquitectónicas, equivalencia estadística, escalabilidad ni comportamiento en producción.

#### Reproducibilidad de ejecución local

Posteriormente se completó `docs/ejecucion-local.md` con el procedimiento realmente utilizado para iniciar el backend:

1. configuración de Java 25;
2. carga de la configuración local de PostgreSQL;
3. configuración de las variables requeridas por Spring Boot;
4. carga local del secreto JWT;
5. ejecución desde `backend` mediante `.\gradlew.bat bootRun`;
6. comprobación mediante `/actuator/health`.

La verificación realizada utilizó:

- Java 25.0.2;
- Spring Boot 4.1.1;
- PostgreSQL 17.11;
- Tomcat mediante HTTP en el puerto 8080;
- `/actuator/health` con estado `UP`.

Este refuerzo fue integrado posteriormente en `master` mediante el merge `9e3b1e3`.

### 2.2 Criterios de interpretación académica

- Una mejora documental posterior no se presenta como si hubiera sido producida originalmente en una semana anterior.
- Una revalidación posterior complementa la evidencia histórica; no la sustituye.
- La existencia de un PR o commit demuestra trazabilidad de integración, pero no implica por sí sola que todos los criterios académicos de una semana hayan sido satisfechos.
- Las métricas experimentales se interpretan únicamente dentro de las condiciones documentadas para cada experimento.
- La ausencia de una regresión reproducible no equivale a demostrar equivalencia estadística.
- El veredicto del mini-comité debe registrarse en los ADR únicamente después de que dicho comité ocurra.

---

## 3. Matriz arquitectónica — Semana 8

| ID | Nivel C4 | Tipo | Responsabilidad | Ancla verificable | Evidencia | Estado |
|---|---|---|---|---|---|---|
| TR-001 | L2 / L3 | Decisión arquitectónica | Mantener un único backend y organizarlo como monolito modular | `docs/adr/0001-decision-estilo.md` | ADR-001 + backend Spring Boot único + C4 | ✅ CONCUERDA |
| TR-002 | L3 | Frontera modular | Organizar el backend por capacidades de negocio | `backend/src/main/kotlin/com/example/rachapro/backend/` | paquetes `identity`, `activities`, `focus`, `progress`, `reminders`, `shared` | 🏗 AS-IS |
| TR-003 | L3 | Persistencia | Evitar acceso directo a repositorios y entidades de otros módulos | `docs/adr/0002-aislamiento-persistencia.md` | ADR-002 + repositorios y entidades dentro del módulo propietario | ✅ CONCUERDA |
| TR-004 | L3 | API intermodular | Permitir a Focus y Reminders consultar Activities sin importar su persistencia | `activities/api/ActivityLookup.kt` | `ActivityLookupService`, `PomodoroSessionService`, `ReminderService` | ✅ CONCUERDA |
| TR-005 | L3 | API intermodular | Permitir a Identity crear categorías iniciales sin importar `CategoryRepository` | `activities/api/DefaultCategoryProvisioning.kt` | `DefaultCategoryProvisioningService` + `UserService` | ✅ CONCUERDA |
| TR-006 | L3 | Fitness function | Evitar ciclos de dependencia entre módulos | `backend/src/test/kotlin/com/example/rachapro/backend/architecture/ModularArchitectureTest.kt` | regla ArchUnit de ciclos | ✅ CONCUERDA |
| TR-007 | L3 | Fitness function | Evitar acceso a persistencia interna de otros módulos | `ModularArchitectureTest.kt` | reglas ArchUnit de aislamiento | ✅ CONCUERDA |
| TR-008 | L3 | Fitness function | Evitar persistencia de negocio dentro de Shared | `ModularArchitectureTest.kt` | reglas contra `Repository` y `Entity` en Shared | ✅ CONCUERDA |
| TR-009 | L2 | Persistencia física | Mantener PostgreSQL como base de datos del backend | `docs/semana8/c4/c4-l2-contenedores.md` | Backend → PostgreSQL mediante JPA / Hibernate / JDBC | 🏗 AS-IS |
| TR-010 | L2 | Cliente | Mantener Android como consumidor externo de la API HTTP | `docs/semana8/c4/c4-l2-contenedores.md` | Android → Backend mediante HTTP/JSON + JWT | 🏗 AS-IS |
| TR-011 | L3 | Rendimiento | Identificar comportamiento del GET de Activities bajo alta concurrencia | `experimentos/semana8-diagnostico-activities/` | evidencia de espera relevante por adquisición de conexiones | 🕰 HISTÓRICO |
| TR-012 | L3 | Experimento | Evaluar limitar la primera respuesta de Activities mediante paginación | `experimentos/semana8-paginacion-activities/` | CONTROL ~1000 elementos vs. PAGED 100 elementos | ✅ CONCUERDA |
| TR-013 | L3 | Endpoint | Mantener endpoint original y agregar variante paginada sin romper consumidor actual | `ActivityController.kt` | `/api/activities` y `/api/activities/paged` | 🏗 AS-IS |
| TR-014 | L3 | Evidencia de rendimiento | Verificar que el efecto de paginación se mantenga después del refactor | `experimentos/semana8-post-modular-validation/` | PRE/POST y réplica con orden inverso | ✅ CONCUERDA |
| TR-015 | L3 | Validación | Determinar si el refactor modular introduce una regresión reproducible | `experimentos/semana8-post-modular-validation/README.md` | la dirección del efecto cambia al invertir el orden | ✅ CONCUERDA |
| TR-016 | L2 / L3 | Documentación | Representar arquitectura implementada y no una propuesta futura | `docs/semana8/c4/` | C4 L2 + C4 L3 + Mermaid | 🏗 AS-IS |
| TR-017 | L3 | Riesgo | Controlar orden no totalmente determinista entre elementos con igual `dueDateEpochDay` | experimento de paginación | segundo criterio estable todavía no materializado | 🧪 PRUEBA PENDIENTE |
| TR-018 | L2 / L3 | Escalabilidad | No atribuir al refactor una solución automática de capacidad | `docs/semana8/edav.md` | diagnóstico mantiene límites bajo alta concurrencia | ⚪ CRITERIO DEFINIDO |

---

## 4. Lectura de la matriz

La trazabilidad arquitectónica de Semana 8 muestra tres niveles principales de evidencia.

### 4.1 Decisión

Los ADR explican las decisiones arquitectónicas adoptadas:

- selección del monolito modular;
- aislamiento de persistencia;
- propiedad de entidades y repositorios;
- comunicación intermodular mediante contratos explícitos.

### 4.2 Implementación

El código materializa:

- módulos organizados por capacidad;
- propiedad de repositorios y entidades;
- contratos públicos entre módulos;
- llamadas síncronas internas;
- `Shared` sin persistencia de negocio;
- PostgreSQL como persistencia central del backend;
- Android como consumidor externo de la API.

### 4.3 Verificación

ArchUnit y los experimentos permiten comprobar, dentro de su alcance:

- reglas estructurales;
- ausencia de ciclos entre módulos;
- aislamiento de persistencia;
- restricciones sobre `Shared`;
- comportamiento de rendimiento medido;
- efecto observado de limitar el tamaño de la primera respuesta mediante paginación;
- ausencia de una degradación consistente en las revalidaciones realizadas bajo condiciones similares.

La verificación estructural mediante ArchUnit también forma parte del flujo de CI en GitHub.

---

## 5. Pendientes explícitos

No todo elemento de Semana 8 se considera cerrado.

Permanece pendiente:

- decidir la adopción definitiva de paginación por el consumidor Android;
- añadir un orden secundario estable si la paginación se convierte en comportamiento principal;
- continuar evaluando la capacidad del pool de conexiones mediante experimentos separados;
- evitar interpretar la ausencia de una regresión reproducible como equivalencia estadística de rendimiento;
- evaluar, si aparecen nuevos paquetes internos dentro de los módulos, una regla ArchUnit más genérica que permita únicamente dependencias hacia APIs públicas del módulo;
- registrar el veredicto del mini-comité en los ADR únicamente después de que este ocurra.

---

## 6. Conclusión

La evidencia versionada permite relacionar decisiones, implementación, validaciones estructurales, experimentos y documentación a lo largo de las semanas del proyecto.

En Semana 8 existe correspondencia verificable entre ADR-001, ADR-002, la arquitectura C4 y la estructura modular materializada en el backend.

Las evidencias posteriores incorporadas el 17/09/2026 fortalecen reproducibilidad y trazabilidad, pero se mantienen diferenciadas de los entregables históricos originales.

La evidencia de rendimiento se interpreta exclusivamente dentro de las condiciones de cada experimento y no se utiliza para atribuir causalidad al refactor modular, demostrar escalabilidad o afirmar equivalencia estadística.

Los elementos todavía no demostrados permanecen clasificados como pendientes y no se presentan como resultados confirmados.

RachaPro queda trazable y defendible bajo la evidencia actual, sin asumir que los pendientes técnicos o académicos ya fueron resueltos.