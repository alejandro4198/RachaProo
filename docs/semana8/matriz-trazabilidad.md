# Matriz de trazabilidad arquitectónica — Semana 8

## 1. Propósito

Relacionar decisiones, arquitectura, código, fitness functions y experimentos de Semana 8.

La matriz distingue entre:

- decisiones arquitectónicas;
- estructura implementada;
- contratos intermodulares;
- fitness functions;
- experimentos;
- documentación C4.

Estados utilizados:

- ✅ CONCUERDA
- 🟡 PARCIAL
- 🔴 CONTRADICCIÓN
- 🧪 PRUEBA PENDIENTE
- ⚪ CRITERIO DEFINIDO
- 🕰 HISTÓRICO
- 🏗 AS-IS
- 🎯 TO-BE

---

## 2. Matriz

| ID | Nivel C4 | Tipo | Responsabilidad | Ancla verificable | Evidencia | Estado |
|---|---|---|---|---|---|---|
| TR-001 | L2 / L3 | Decisión arquitectónica | Mantener un único backend y organizarlo como monolito modular | `docs/adr/0001-decision-estilo.md` | ADR-001 + backend Spring Boot único + C4 | ✅ CONCUERDA |
| TR-002 | L3 | Frontera modular | Organizar el backend por capacidades de negocio | `backend/src/main/kotlin/com/example/rachapro/backend/` | paquetes `identity`, `activities`, `focus`, `progress`, `reminders`, `shared` | 🏗 AS-IS |
| TR-003 | L3 | Persistencia | Evitar acceso directo a repositorios y entidades de otros módulos | `docs/adr/0002-aislamiento-persistencia.md` | ADR-002 + repositorios y entidades dentro del módulo propietario | ✅ CONCUERDA |
| TR-004 | L3 | API intermodular | Permitir a Focus y Reminders consultar Activities sin importar su persistencia | `activities/api/ActivityLookup.kt` | `ActivityLookupService`, `PomodoroSessionService`, `ReminderService` | ✅ CONCUERDA |
| TR-005 | L3 | API intermodular | Permitir a Identity crear categorías iniciales sin importar CategoryRepository | `activities/api/DefaultCategoryProvisioning.kt` | `DefaultCategoryProvisioningService` + `UserService` | ✅ CONCUERDA |
| TR-006 | L3 | Fitness function | Evitar ciclos de dependencia entre módulos | `backend/src/test/kotlin/com/example/rachapro/backend/architecture/ModularArchitectureTest.kt` | regla ArchUnit de ciclos | ✅ CONCUERDA |
| TR-007 | L3 | Fitness function | Evitar acceso a persistencia interna de otros módulos | `ModularArchitectureTest.kt` | reglas ArchUnit de aislamiento | ✅ CONCUERDA |
| TR-008 | L3 | Fitness function | Evitar persistencia de negocio dentro de Shared | `ModularArchitectureTest.kt` | reglas contra Repository y Entity en Shared | ✅ CONCUERDA |
| TR-009 | L2 | Persistencia física | Mantener PostgreSQL como base de datos del backend | `docs/semana8/c4/c4-l2-contenedores.md` | backend -> PostgreSQL mediante JPA/Hibernate/JDBC | 🏗 AS-IS |
| TR-010 | L2 | Cliente | Mantener Android como consumidor externo de la API HTTP | `docs/semana8/c4/c4-l2-contenedores.md` | Android -> Backend mediante HTTP/JSON + JWT | 🏗 AS-IS |
| TR-011 | L3 | Rendimiento | Identificar comportamiento del GET de Activities bajo alta concurrencia | `experimentos/semana8-diagnostico-activities/` | evidencia de espera relevante por adquisición de conexiones | 🕰 HISTÓRICO |
| TR-012 | L3 | Experimento | Evaluar limitar la primera respuesta de Activities mediante paginación | `experimentos/semana8-paginacion-activities/` | CONTROL ~1000 elementos vs PAGED 100 elementos | ✅ CONCUERDA |
| TR-013 | L3 | Endpoint | Mantener endpoint original y agregar variante paginada sin romper consumidor actual | `ActivityController.kt` | `/api/activities` y `/api/activities/paged` | 🏗 AS-IS |
| TR-014 | L3 | Evidencia de rendimiento | Verificar que el efecto de paginación se mantenga después del refactor | `experimentos/semana8-post-modular-validation/` | PRE/POST y réplica con orden inverso | ✅ CONCUERDA |
| TR-015 | L3 | Validación | Determinar si el refactor modular introduce una regresión reproducible | `experimentos/semana8-post-modular-validation/README.md` | la dirección del efecto cambia al invertir el orden | ✅ CONCUERDA |
| TR-016 | L2 / L3 | Documentación | Representar arquitectura implementada y no una propuesta futura | `docs/semana8/c4/` | C4 L2 + C4 L3 + Mermaid | 🏗 AS-IS |
| TR-017 | L3 | Riesgo | Controlar orden no totalmente determinista entre elementos con igual `dueDateEpochDay` | experimento de paginación | segundo criterio estable todavía no materializado | 🧪 PRUEBA PENDIENTE |
| TR-018 | L2 / L3 | Escalabilidad | No atribuir al refactor una solución automática de capacidad | `docs/semana8/edav.md` | diagnóstico mantiene límites bajo alta concurrencia | ⚪ CRITERIO DEFINIDO |

---

## 3. Lectura de la matriz

La trazabilidad muestra tres niveles de evidencia:

### Decisión

Los ADR explican por qué se seleccionó el monolito modular y cómo debe aislarse la persistencia.

### Implementación

El código materializa:

- módulos por capacidad;
- propiedad de repositorios y entidades;
- contratos públicos;
- llamadas síncronas internas;
- Shared sin persistencia de negocio.

### Verificación

ArchUnit y los experimentos permiten comprobar respectivamente:

- reglas estructurales;
- ausencia de ciclos;
- aislamiento de persistencia;
- comportamiento de rendimiento medido.

---

## 4. Pendientes explícitos

No todo elemento de Semana 8 se considera cerrado.

Permanece pendiente:

- decidir la adopción definitiva de paginación por el consumidor Android;
- añadir un orden secundario estable si la paginación se convierte en comportamiento principal;
- continuar evaluando capacidad del pool de conexiones mediante experimentos separados;
- no confundir la ausencia de regresión reproducible con equivalencia estadística de rendimiento.

---

## 5. Conclusión

La documentación, código y experimentos de Semana 8 presentan correspondencia verificable para las decisiones arquitectónicas principales.

No se identifican contradicciones abiertas entre ADR-001, ADR-002, C4 y la estructura modular materializada.

Los elementos todavía no demostrados permanecen clasificados como pendientes y no se presentan como resultados confirmados.