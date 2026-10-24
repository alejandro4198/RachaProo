# ADR-004 — Re-decisión de distribución arquitectónica de RachaPro

- **Estado:** Propuesta de decisión del equipo — pendiente de validación en Comité 2
- **Fecha de elaboración:** 2026-10-21
- **Formalización:** Semana 12 — Corte 2 / Comité 2
- **Ámbito:** Backend Spring Boot
- **Tipo:** Re-decisión de estilo y distribución arquitectónica
- **Decisión previa relacionada:** `ADR-001 — Adoptar un monolito modular para el backend de RachaPro`
- **Documento de costos asociado:** `docs/semana12/costos-relativos-distribucion.md`
- **Commit de costos disponible antes de este ADR:** `99ed270`

---

## 1. Contexto

ADR-001 estableció como arquitectura objetivo del backend de RachaPro un monolito modular compuesto por cinco capacidades funcionales principales:

```text
Identity
Activities
Focus
Progress
Reminders
```

La decisión original conservó:

- una aplicación Spring Boot;
- un único proceso backend;
- una única unidad de despliegue;
- PostgreSQL como persistencia central;
- fronteras funcionales explícitas entre módulos.

ADR-001 no declaró que el monolito modular fuera universalmente superior a una arquitectura distribuida.

La decisión original se tomó porque, con la evidencia disponible en ese momento, permitía:

- conservar simplicidad operacional;
- mantener llamadas internas entre capacidades;
- introducir fronteras funcionales;
- controlar dependencias;
- mantener ownership lógico de datos;
- evitar introducir problemas distribuidos sin una necesidad demostrada.

Después de ADR-001 se generó nueva evidencia arquitectónica y experimental durante las Semanas 8 a 12.

Por tanto, Semana 12 requiere volver a evaluar la distribución del backend y determinar si la evidencia acumulada justifica conservar la decisión original o modificarla hacia una arquitectura distribuida.

---

## 2. Naturaleza de esta decisión

Este ADR no parte de una arquitectura sin una decisión previa.

Corresponde a una:

```text
RE-DECISIÓN ARQUITECTÓNICA
```

La pregunta no es:

> ¿Qué arquitectura elegiríamos si RachaPro comenzara hoy desde cero?

La pregunta es:

> ¿La evidencia obtenida después de ADR-001 justifica modificar la arquitectura actual y separar físicamente las capacidades funcionales de RachaPro?

Por tanto, la comparación considera explícitamente:

- estado actual del código;
- evidencia experimental;
- atributos de calidad;
- costo relativo;
- costo de transición;
- costo recurrente;
- complejidad operacional;
- beneficios potenciales;
- riesgos;
- reversibilidad.

La decisión registrada en este documento corresponde al análisis del equipo y no debe confundirse con una aprobación o veredicto emitido por el docente.

---

## 3. Decisión previa que se reevalúa

ADR-001 decidió:

```text
MONOLITO MODULAR
```

como arquitectura objetivo del backend.

La arquitectura materializada mantiene:

```text
Android App
    |
    | HTTP/JSON + JWT
    v
Spring Boot
├── identity
├── activities
├── focus
├── progress
├── reminders
└── shared
    |
    v
PostgreSQL
```

ADR-001 dejó explícitamente fuera de alcance una futura separación en microservicios.

ADR-004 evalúa precisamente si la nueva evidencia obtenida posteriormente justifica activar esa posibilidad.

---

## 4. Problema

Durante Semana 11 se obtuvo nueva evidencia relacionada con:

- crecimiento de carga;
- degradación relativa de latencia;
- comportamiento durante una indisponibilidad temporal;
- recuperación posterior del backend;
- efecto de una política explícita de timeout.

Además, durante Semana 12 se realizó una estimación de costos relativos entre:

- mantener el monolito modular;
- distribuir las cinco capacidades funcionales en cinco microservicios.

La existencia de degradación, indisponibilidad y posibilidades de escalado independiente podría sugerir que una arquitectura distribuida ofrece ventajas.

Sin embargo, la distribución física también introduce nuevas responsabilidades operacionales y modos de fallo.

La pregunta arquitectónica que debe resolverse es:

> ¿La evidencia actual demuestra una necesidad suficiente de distribución física que compense el costo y la complejidad adicionales de separar RachaPro en cinco microservicios?

---

## 5. Drivers arquitectónicos

Los atributos de calidad priorizados para RachaPro son:

1. rendimiento;
2. seguridad;
3. usabilidad;
4. disponibilidad.

Para esta re-decisión también son relevantes:

- mantenibilidad;
- modularidad;
- complejidad operacional;
- aislamiento de fallos;
- escalabilidad;
- capacidad de despliegue independiente;
- observabilidad;
- facilidad de pruebas;
- consistencia;
- costo de adopción;
- costo recurrente;
- reversibilidad.

Ninguna alternativa se considera automáticamente superior en todos los drivers.

---

## 6. Restricciones metodológicas

La decisión se toma bajo las siguientes restricciones.

### 6.1. No inventar costos monetarios

No existe evidencia suficiente para calcular de forma defendible:

```text
COP/mes
USD/mes
COP/año
USD/año
```

para ambas alternativas.

---

### 6.2. No inventar horas de migración

No se ha realizado una migración completa de RachaPro hacia microservicios.

Por tanto:

```text
horas reales de migración = NO MEDIDAS
```

---

### 6.3. No convertir estructura en costo económico

El escenario compara:

```text
Alternativa A = 1 unidad backend desplegable
Alternativa B = 5 unidades backend desplegables
```

lo cual permite calcular:

```text
5 / 1 = 5× unidades backend desplegables
```

pero no permite afirmar:

```text
5× costo económico
```

ni:

```text
5× esfuerzo de ingeniería
```

ni:

```text
5× complejidad total
```

---

### 6.4. No atribuir causalidad no observada

La campaña de crecimiento de Semana 11 no identificó causalmente que el monolito modular fuera responsable de la degradación observada.

---

### 6.5. No atribuir beneficios no medidos a microservicios

No existe una versión de RachaPro implementada mediante cinco microservicios.

Por tanto, no existe evidencia experimental directa que permita afirmar que la alternativa distribuida:

- reduciría el P95;
- incrementaría el throughput;
- aumentaría la disponibilidad;
- eliminaría la degradación;
- reduciría errores;
- mejoraría la escalabilidad observada.

---

### 6.6. Ausencia de evidencia no equivale a imposibilidad

La ausencia de evidencia que obligue actualmente a distribuir RachaPro no demuestra que una distribución física nunca vaya a ser necesaria.

La decisión aplica al estado actual del sistema y deberá reconsiderarse si cambian sus drivers o aparece nueva evidencia.

---

## 7. Alternativas consideradas

### 7.1. Alternativa A — Mantener el monolito modular

Mantener:

```text
1 backend Spring Boot
```

con cinco capacidades funcionales internas:

```text
Identity
Activities
Focus
Progress
Reminders
```

y conservar:

- fronteras modulares;
- contratos internos;
- reglas ArchUnit;
- despliegue conjunto;
- PostgreSQL compartido físicamente;
- ownership lógico de datos.

---

### 7.2. Alternativa B — Cinco microservicios por capacidad

Separar físicamente las cinco capacidades en:

```text
Identity Service
Activities Service
Focus Service
Progress Service
Reminders Service
```

Cada capacidad sería una unidad backend desplegable independiente.

Los detalles concretos de:

- protocolos;
- persistencia;
- gateway;
- mensajería;
- service discovery;
- balanceo;
- réplicas;
- observabilidad;
- seguridad servicio-servicio;
- CI/CD;

no están definidos ni implementados actualmente.

---

## 8. Evidencia estructural posterior a ADR-001

La arquitectura modular prevista por ADR-001 ya fue materializada.

Se identifican cinco capacidades funcionales principales:

```text
identity
activities
focus
progress
reminders
```

Además existe:

```text
shared
```

que no representa una sexta capacidad funcional de negocio.

Esto permite evaluar ADR-001 sobre una arquitectura existente y no solamente sobre una intención de diseño.

---

## 9. Fronteras arquitectónicas materializadas

El backend contiene reglas ArchUnit para proteger fronteras entre capacidades.

Se observaron nueve reglas arquitectónicas.

### 9.1. Aislamiento de capacidades

1. `activities internals are isolated`;
2. `identity internals are isolated`;
3. `focus internals are isolated`;
4. `progress internals are isolated`;
5. `reminders internals are isolated`.

### 9.2. Protección de Shared

6. `shared does not depend on business modules`;
7. `shared contains no business repositories`;
8. `shared contains no business entities`.

### 9.3. Control de ciclos

9. `modules are free of dependency cycles`.

Esto demuestra que la Alternativa A no corresponde a un monolito sin disciplina estructural.

La comparación real es entre:

```text
MONOLITO MODULAR CON FRONTERAS VERIFICABLES
```

y:

```text
CINCO UNIDADES FÍSICAMENTE DISTRIBUIDAS
```

---

## 10. Fitness functions arquitectónicas existentes

Las reglas anteriores se encuentran en:

```text
backend/src/test/kotlin/com/example/rachapro/backend/architecture/ModularArchitectureTest.kt
```

La regla de ciclos utiliza:

```kotlin
slices()
    .matching("$base.(*)..")
    .should()
    .beFreeOfCycles()
```

Estas pruebas constituyen fitness functions ya existentes para proteger aspectos del diseño modular.

No sustituyen la fitness function específica solicitada en la Actividad 3 de Semana 12.

---

## 11. CI arquitectónico existente

Se identificó:

```text
.github/workflows/backend-architecture.yml
```

El workflow ejecuta:

```bash
./gradlew test --tests "com.example.rachapro.backend.architecture.ModularArchitectureTest" --no-daemon
```

Por tanto, algunas propiedades arquitectónicas de la Alternativa A ya poseen verificación automática.

Este dato no permite determinar cuántos workflows necesitaría la Alternativa B.

---

## 12. Contratos internos observados

Se identificaron, entre otros:

```text
ActivityLookup
DefaultCategoryProvisioning
```

`ActivityLookup` expone:

```kotlin
fun existsActiveActivityForUser(
    activityId: Long,
    userId: Long
): Boolean
```

Consumidores observados fuera de Activities:

```text
PomodoroSessionService
ReminderService
```

Esto produce relaciones lógicas:

```text
focus -> activities
reminders -> activities
```

---

## 13. Dependencias intermodulares observadas

El análisis estático de imports entre capacidades identificó:

```text
activities -> identity
focus -> activities
reminders -> activities
```

Resultado:

```text
Pares dirigidos observados = 3
```

Estos pares representan dependencias estáticas del código actual.

No equivalen automáticamente a:

```text
3 endpoints HTTP
```

ni:

```text
3 llamadas remotas
```

en una arquitectura distribuida.

La forma concreta de resolver cada relación en B tendría que diseñarse explícitamente.

---

## 14. Evento UserRegisteredV1

Activities importa el tipo público:

```text
UserRegisteredV1
```

proveniente de Identity.

Esto demuestra una relación estática:

```text
activities -> identity
```

pero no demuestra por sí solo cuál debería ser el mecanismo entre procesos si ambas capacidades fueran servicios independientes.

Podría requerirse una decisión posterior entre mecanismos síncronos, eventos u otra forma de integración.

---

## 15. Evidencia de Semana 11 — crecimiento

La campaña de crecimiento evaluó niveles de concurrencia hasta:

```text
1000 VUs
```

El P95 representativo se calculó según el protocolo preregistrado como:

```text
mediana(P95_R2, P95_R3, P95_R4)
```

La primera condición que cumplió el criterio preregistrado de degradación relativa del P95 fue:

```text
50 VUs
```

La documentación establece que se observó una señal de degradación de latencia a partir de ese nivel.

Al mismo tiempo:

- no se observó una señal simultánea equivalente de degradación del éxito funcional;
- no se observó aparición repetida de HTTP 5xx bajo el criterio utilizado.

La propia campaña establece que:

> el comportamiento observado no identifica por sí mismo la causa de la degradación.

También documenta que no se dispone de telemetría suficiente para atribuir causalmente la degradación a un componente específico.

---

## 16. Interpretación arquitectónica de la campaña de crecimiento

La campaña permite afirmar:

```text
SE OBSERVÓ DEGRADACIÓN RELATIVA DE LATENCIA
BAJO DETERMINADAS CONDICIONES DE CARGA
```

No permite afirmar:

```text
EL MONOLITO MODULAR ES LA CAUSA
```

Tampoco permite afirmar:

```text
MICROSERVICIOS RESOLVERÍAN LA DEGRADACIÓN
```

Por tanto, la campaña constituye evidencia relevante para vigilar capacidad y rendimiento, pero no constituye una prueba suficiente para cambiar la distribución arquitectónica.

---

## 17. Evidencia de Semana 11 — fallos y resiliencia

La matriz de fallos evaluó una indisponibilidad temporal inducida de Spring Boot.

Bajo las condiciones del experimento se observó:

- comportamiento visible de Android durante la indisponibilidad;
- recuperación posterior del backend;
- posibilidad de realizar una nueva operación después del restablecimiento;
- recuperación operativa desde la sesión Android existente.

Las repeticiones aportaron evidencia de:

```text
RECUPERACIÓN OPERATIVA PARA UNA NUEVA OPERACIÓN
```

después del restablecimiento del backend.

---

## 18. Límite de la evidencia de recuperación

La matriz no demuestra:

```text
RECUPERACIÓN AUTOMÁTICA DEL INTENTO ORIGINAL FALLIDO
```

La documentación establece expresamente que no existe evidencia suficiente para afirmar que la solicitud fallida durante la indisponibilidad sea recuperada automáticamente.

Además, existen incidencias documentadas en algunas repeticiones relacionadas con:

- identificadores de recuperación;
- trazabilidad;
- segundas pulsaciones manuales.

Por tanto, la evidencia se limita a:

```text
RECUPERACIÓN DE CAPACIDAD OPERACIONAL PARA UNA NUEVA OPERACIÓN
```

y no se utilizará para atribuir mecanismos automáticos que no fueron demostrados.

---

## 19. Evidencia de Semana 11 — experimento de timeout

El experimento evaluó una táctica concreta:

```text
connectTimeout = 3 segundos
```

ante una indisponibilidad controlada de Spring Boot.

La línea base consolidada fue:

```text
M_antes = 10 028 ms
```

Después de la intervención:

```text
M_después = 3 014 ms
```

La reducción calculada fue:

```text
69,94 %
```

El umbral preregistrado fue:

```text
50 %
```

Por tanto:

```text
69,94 % >= 50 %
```

La decisión metodológica del autor registrada para ese experimento fue:

```text
HIPÓTESIS RESPALDADA
```

---

## 20. Alcance del experimento de timeout

El experimento demuestra que, bajo las condiciones experimentales utilizadas, una política explícita de `connectTimeout` redujo la mediana de la duración técnica de POST fallidos durante una indisponibilidad controlada.

No demuestra:

- superioridad del monolito modular;
- inferioridad de microservicios;
- disponibilidad global;
- escalabilidad general;
- eliminación de fallos de red;
- comportamiento de una arquitectura de cinco microservicios.

La conclusión del experimento de timeout no se utiliza como conclusión directa sobre distribución arquitectónica.

---

## 21. Relevancia arquitectónica del experimento de timeout

Actualmente las llamadas entre módulos internos del backend no atraviesan una frontera de red.

Si una dependencia lógica pasa a conectar procesos independientes, podría quedar expuesta a condiciones como:

- conexión rechazada;
- timeout;
- servicio remoto indisponible;
- latencia de red;
- fallo parcial;
- respuesta tardía.

Por tanto, el experimento no demuestra que los microservicios sean inconvenientes.

Sí proporciona evidencia empírica dentro de RachaPro de que una frontera de red puede requerir decisiones explícitas de resiliencia.

---

## 22. Evidencia de Semana 12 — costos relativos

La Actividad 1 de Semana 12 se encuentra documentada en:

```text
docs/semana12/costos-relativos-distribucion.md
```

Commit previo a este ADR:

```text
99ed270
```

Las alternativas evaluadas fueron:

```text
A = monolito modular actual
B = cinco microservicios por capacidad
```

---

## 23. Resultado estructural principal

La dimensión directamente cuantificable con una unidad común fue:

```text
UNIDADES BACKEND DESPLEGABLES
```

Alternativa A:

```text
1
```

Alternativa B:

```text
5
```

Relación:

\[
R=\frac{5}{1}=5
\]

Resultado:

```text
5× unidades backend desplegables
```

---

## 24. Alcance del resultado 5×

El valor:

```text
5×
```

no significa:

```text
5× costo económico
```

ni:

```text
5× esfuerzo de ingeniería
```

ni:

```text
5× costo operacional total
```

ni:

```text
5× complejidad total
```

Corresponde exclusivamente a:

```text
NÚMERO DE UNIDADES BACKEND DESPLEGABLES
```

Además:

```text
5× < 10×
```

por lo que esta dimensión aislada no representa un orden decimal completo de magnitud.

---

## 25. Costos no estimables actualmente

La evidencia disponible no permite cuantificar de forma defendible:

- costo monetario mensual;
- costo monetario anual;
- horas de migración;
- horas por despliegue;
- horas de mantenimiento;
- costo de observabilidad;
- costo de pruebas distribuidas;
- costo de incidentes;
- número exacto de pipelines futuros;
- infraestructura completa de B.

Estos valores permanecen:

```text
NO ESTIMABLES CON LA EVIDENCIA ACTUAL
```

Esto no significa que sus costos sean cero.

Significa que no existe evidencia suficiente para asignarles una cifra defendible.

---

## 26. Nuevas categorías operacionales de B

Separar físicamente las capacidades introduce o puede introducir categorías que actualmente no existen entre módulos dentro del mismo proceso:

- comunicación remota;
- serialización;
- fallos de red interservicio;
- políticas de timeout;
- posibles retries;
- observabilidad distribuida;
- coordinación de contratos remotos;
- seguridad servicio-servicio;
- potencial consistencia distribuida;
- coordinación de despliegues.

La magnitud económica o temporal de estas categorías no fue medida.

---

## 27. Beneficios potenciales de B

Una distribución mediante microservicios podría permitir:

- despliegue independiente;
- escalado independiente;
- mayor aislamiento entre procesos;
- evolución tecnológica independiente;
- asignación diferenciada de recursos;
- separación física de responsabilidades.

Estos beneficios son arquitectónicamente plausibles.

Sin embargo, la evidencia actual no demuestra que RachaPro necesite:

- escalar una capacidad concreta independientemente;
- desplegar capacidades con ciclos significativamente distintos;
- utilizar tecnologías diferentes por capacidad;
- separar físicamente datos por obligación;
- aislar una capacidad por requisitos regulatorios;
- aislar una capacidad por requisitos especiales de seguridad;
- separar una capacidad debido a saturación causalmente localizada.

Por tanto, se consideran:

```text
BENEFICIOS POTENCIALES
```

y no:

```text
BENEFICIOS EXPERIMENTALMENTE DEMOSTRADOS EN RACHAPro
```

---

## 28. Comparación contra los drivers

| Driver | A — Monolito modular | B — 5 microservicios | Evidencia actual |
|---|---|---|---|
| Rendimiento | Mantiene llamadas internas entre módulos | Puede introducir comunicación remota | No existe comparación experimental A vs. B |
| Seguridad | Seguridad centralizada en el backend actual | Puede requerir seguridad entre servicios | B no implementada |
| Usabilidad | Impacto principalmente indirecto | Impacto principalmente indirecto | Sin diferencia demostrada |
| Disponibilidad | Una falla severa del proceso puede afectar el backend | Puede permitir aislamiento si se diseña correctamente | No probado para RachaPro |
| Escalabilidad | El backend escala como unidad | Potencial escalado independiente | No existe necesidad aislada demostrada |
| Mantenibilidad | Fronteras internas verificadas | Fronteras físicas potencialmente fuertes | A ya posee controles automatizados |
| Complejidad operacional | Una unidad backend | Cinco unidades backend según el escenario | B = 5× unidades desplegables |
| Observabilidad | Principalmente un proceso backend | Flujos potencialmente distribuidos | B no implementada |
| Consistencia | Más simple dentro de la infraestructura actual | Depende de la estrategia de datos | B no definida |
| Migración | No requiere cambiar distribución | Requiere transición estructural | Esfuerzo no medido |

---

## 29. Evidencia nueva que justifica reconsiderar ADR-001

Después de ADR-001 aparecieron elementos suficientemente relevantes para justificar una nueva evaluación:

- materialización real de las fronteras modulares;
- dependencias intermodulares observadas;
- degradación relativa del P95 bajo carga;
- comportamiento durante indisponibilidad;
- recuperación posterior para nuevas operaciones;
- efecto medible de una política explícita de timeout;
- estimación de costos relativos de distribución.

Estos elementos justifican revisar ADR-001.

Sin embargo:

> reconsiderar una decisión no implica necesariamente modificarla.

---

## 30. Evidencia que no apareció

No se obtuvo evidencia de:

- una capacidad concreta que requiera escalado independiente;
- una capacidad concreta identificada causalmente como cuello de botella dominante;
- necesidad demostrada de despliegue independiente;
- necesidad de una base de datos independiente por capacidad;
- requisito demostrado de aislamiento físico;
- fallos que deban contenerse obligatoriamente mediante procesos separados;
- tecnología incompatible entre capacidades;
- incompatibilidad del monolito modular con los atributos prioritarios;
- beneficio medido de la alternativa de cinco microservicios.

---

## 31. Evaluación de la Alternativa A

A presenta evidencia directa de:

- implementación real;
- fronteras funcionales;
- contratos internos;
- ownership lógico;
- reglas ArchUnit;
- control de ciclos;
- CI arquitectónico;
- comportamiento experimental conocido;
- capacidad de introducir tácticas localizadas de resiliencia.

Sus limitaciones permanecen:

- un proceso;
- una unidad de despliegue;
- ausencia de escalado independiente por capacidad;
- ausencia de aislamiento físico interno;
- posibilidad de afectación global ante una falla severa.

---

## 32. Evaluación de la Alternativa B

B ofrece beneficios potenciales relacionados con:

- distribución;
- aislamiento;
- escalado independiente;
- despliegue independiente.

Sin embargo:

- no está implementada;
- no posee medición experimental propia;
- aumenta de 1 a 5 las unidades backend desplegables;
- introduce fronteras de proceso;
- puede introducir nuevos modos de fallo;
- requiere decisiones adicionales de operación;
- no existe evidencia suficiente de que esos beneficios sean necesarios actualmente.

---

## 33. Evaluación del autor

A partir de la evidencia disponible, el autor considera que no existe actualmente una necesidad experimental suficiente para justificar una migración completa hacia cinco microservicios.

Esta evaluación se basa en que:

1. el monolito modular ya posee fronteras estructurales verificables;
2. la degradación de Semana 11 no fue atribuida causalmente al estilo monolítico;
3. no existe evidencia experimental de que B reduzca esa degradación;
4. una táctica localizada de resiliencia produjo una mejora medible sin cambiar la distribución;
5. B introduce mayor estructura desplegable y nuevas categorías operacionales;
6. todavía no se ha demostrado una necesidad concreta de escalado, despliegue o aislamiento independiente.

La evaluación corresponde al autor del ADR.

No constituye un dictamen del docente ni una aprobación del Comité 2.

---

## 34. Re-decisión propuesta por el equipo

Después de evaluar:

- ADR-001;
- ADR-002;
- ADR-003;
- materialización del monolito modular;
- fitness functions existentes;
- campaña de crecimiento;
- matrices de fallos y resiliencia;
- experimento de timeout;
- costos relativos de Semana 12;

se propone:

> **RATIFICAR el monolito modular como arquitectura de distribución actual del backend de RachaPro.**

Por tanto:

```text
NO SE PROPONE actualmente una separación completa
en cinco microservicios.
```

Esta es una decisión arquitectónica del equipo para ser defendida en Comité 2.

No se presenta como aprobación previa del docente.

---

## 35. Naturaleza de la re-decisión

La clasificación propuesta es:

```text
RATIFICACIÓN CON EVIDENCIA POSTERIOR
```

ADR-004 no declara que ADR-001 haya sido incorrecto.

Tampoco declara que microservicios sea una alternativa inválida.

Registra que:

> la evidencia posterior disponible no proporciona actualmente una justificación suficiente para asumir una distribución física completa.

---

## 36. Justificación

### J1 — La modularidad interna ya está materializada

RachaPro no se encuentra en un monolito sin fronteras.

Existen:

- cinco capacidades funcionales;
- contratos públicos;
- reglas de aislamiento;
- reglas sobre `shared`;
- control automático de ciclos;
- CI arquitectónico.

---

### J2 — La degradación observada no fue atribuida causalmente al monolito

Semana 11 observó degradación relativa del P95.

Sin embargo, la campaña no contó con telemetría suficiente para identificar una causa específica.

No existe evidencia para afirmar:

```text
monolito modular -> degradación observada
```

---

### J3 — No existe evidencia de que cinco microservicios solucionen la degradación

B no fue implementada ni medida.

Por tanto, no se ha demostrado:

```text
microservicios -> menor P95
```

ni:

```text
microservicios -> mayor disponibilidad
```

---

### J4 — Una táctica localizada produjo una mejora medible

La configuración:

```text
connectTimeout = 3 segundos
```

redujo la mediana de duración técnica de los POST fallidos desde:

```text
10 028 ms
```

hasta:

```text
3 014 ms
```

con una reducción de:

```text
69,94 %
```

bajo el protocolo definido.

Esto demuestra que una preocupación concreta de resiliencia pudo abordarse mediante una táctica localizada sin cambiar el estilo de distribución.

No demuestra que todos los problemas futuros puedan resolverse del mismo modo.

---

### J5 — La distribución introduce costos adicionales

B incrementa:

```text
1 -> 5
```

las unidades backend desplegables.

Además introduce o puede introducir nuevas categorías operacionales.

No existe todavía evidencia de que los beneficios potenciales sean necesarios para compensar esos costos.

---

## 37. Consecuencias positivas de la re-decisión

Se conserva:

- una única unidad backend desplegable;
- comunicación interna entre módulos;
- menor cantidad de fronteras de red;
- seguridad centralizada;
- trazabilidad operacional más simple;
- pruebas arquitectónicas existentes;
- CI arquitectónico actual;
- contratos internos;
- persistencia actual;
- posibilidad de mejoras incrementales.

---

## 38. Consecuencias negativas

Se acepta continuar con:

- un único proceso backend;
- un único despliegue;
- ausencia de escalado independiente por capacidad;
- ausencia de aislamiento físico entre módulos;
- posibilidad de afectación global ante una falla severa del backend;
- persistencia físicamente compartida;
- necesidad de continuar vigilando crecimiento y capacidad.

Estas limitaciones se aceptan explícitamente.

---

## 39. Trade-off principal

El trade-off principal es:

```text
SIMPLICIDAD OPERACIONAL
vs.
AISLAMIENTO Y ESCALADO INDEPENDIENTE
```

La re-decisión propuesta prioriza actualmente:

```text
SIMPLICIDAD OPERACIONAL
+
MODULARIDAD INTERNA VERIFICABLE
```

sin impedir una futura separación física.

---

## 40. Riesgos de mantener A

### R-01 — Crecimiento de carga

El P95 ya mostró degradación relativa bajo carga.

**Mitigación:**

- continuar mediciones;
- mejorar telemetría;
- identificar causas antes de cambiar la distribución.

---

### R-02 — Punto único de fallo

El backend continúa siendo un proceso único.

**Mitigación:**

- tácticas de resiliencia;
- observabilidad;
- procedimientos de recuperación;
- reevaluación si los requisitos de disponibilidad cambian.

---

### R-03 — Modularidad nominal futura

Cambios futuros podrían erosionar las fronteras.

**Mitigación:**

- ArchUnit;
- contratos públicos;
- CI;
- revisión arquitectónica.

---

### R-04 — Persistencia compartida

PostgreSQL compartido puede facilitar acoplamiento accidental.

**Mitigación:**

- ownership lógico;
- ADR-002;
- prohibición de acceso directo indebido;
- fitness functions.

---

### R-05 — Sobreinterpretación

La ratificación podría interpretarse como:

```text
MICROSERVICIOS NUNCA SERÁN NECESARIOS
```

Esto sería incorrecto.

**Mitigación:**

- criterios explícitos de re-decisión;
- revisión ante nueva evidencia.

---

## 41. Riesgos de adoptar B prematuramente

### R-B01 — Monolito distribuido

La separación de procesos sin independencia real podría producir fuerte acoplamiento remoto sin obtener beneficios suficientes.

---

### R-B02 — Complejidad operacional innecesaria

Podrían introducirse componentes cuya necesidad no esté demostrada.

---

### R-B03 — Mayor superficie de fallo

Cada frontera remota puede introducir modos de fallo adicionales.

---

### R-B04 — Falsa atribución de rendimiento

Podría adoptarse B asumiendo que resolverá una degradación cuyo origen no ha sido determinado.

---

## 42. Mitigaciones mientras se mantenga A

- conservar ArchUnit;
- mantener CI arquitectónico;
- evitar accesos a internals de otros módulos;
- evitar repositories cruzados;
- mantener ownership lógico;
- mejorar telemetría;
- monitorizar crecimiento de latencia;
- documentar nuevas dependencias;
- aplicar tácticas de resiliencia cuando exista evidencia;
- reevaluar la distribución cuando aparezcan drivers verificables.

---

## 43. Reversibilidad

La decisión propuesta es:

```text
REVERSIBLE
```

Mantener el monolito modular no impide una separación posterior.

Las fronteras existentes pueden facilitar conceptualmente una futura separación porque:

- las capacidades están identificadas;
- existen contratos;
- existen reglas de aislamiento;
- se conocen dependencias intermodulares.

Esto no significa que una futura migración vaya a ser automática, rápida o barata.

---

## 44. Criterios de re-decisión

ADR-004 deberá revisarse si aparece evidencia suficiente de alguno de los siguientes escenarios.

### C1 — Escalado independiente necesario

Una capacidad requiere escalarse independientemente y se demuestra que escalar el backend completo resulta insuficiente o desproporcionado.

**Evidencia requerida:**

- métricas por capacidad;
- consumo de CPU/memoria;
- throughput;
- latencia;
- saturación atribuible.

---

### C2 — Saturación causalmente localizada

La telemetría identifica una capacidad concreta como causa dominante de una limitación de rendimiento.

**Evidencia requerida:**

- métricas por componente;
- profiling;
- pool de conexiones;
- CPU;
- memoria;
- trazas;
- comportamiento reproducible.

---

### C3 — Despliegue independiente requerido

Una capacidad necesita ciclos de entrega diferentes y el despliegue conjunto se convierte en una restricción relevante.

**Evidencia requerida:**

- frecuencia de despliegues;
- dependencias de release;
- incidentes o bloqueos derivados del despliegue conjunto.

---

### C4 — Aislamiento de fallos requerido

Un requisito de disponibilidad exige impedir que una falla de una capacidad afecte al resto del backend.

**Evidencia requerida:**

- escenarios de fallo reproducibles;
- impacto sobre capacidades no relacionadas;
- requisito de disponibilidad explícito.

---

### C5 — Persistencia incompatible

Una capacidad requiere un modelo de almacenamiento o escalabilidad incompatible con la persistencia compartida.

**Evidencia requerida:**

- patrón de acceso;
- volumen de datos;
- requerimientos de consistencia;
- limitaciones observadas de PostgreSQL o del modelo actual.

---

### C6 — Seguridad o regulación

Una capacidad necesita aislamiento físico debido a requisitos de seguridad, privacidad o regulación.

**Evidencia requerida:**

- requisito normativo;
- política de seguridad;
- análisis de amenazas;
- necesidad concreta de aislamiento.

---

### C7 — Equipos independientes

La organización evoluciona hacia equipos capaces de desarrollar, desplegar y operar capacidades de manera autónoma.

**Evidencia requerida:**

- estructura organizacional;
- ownership operativo;
- ciclos de entrega independientes.

---

### C8 — Evidencia experimental favorable a distribución

Una comparación controlada demuestra que separar una o más capacidades produce beneficios suficientes para justificar el costo adicional.

**Evidencia requerida:**

- protocolo comparable;
- baseline;
- alternativa distribuida implementada;
- métricas;
- repetibilidad;
- trade-offs.

---

## 45. Respuesta a la pregunta de re-decisión

La restricción nueva que podría invalidar esta decisión sería, entre otras:

> una necesidad demostrada de escalado, despliegue, aislamiento, persistencia o seguridad independiente que el monolito modular actual no pueda satisfacer razonablemente.

La evidencia necesaria para reconsiderarla deberá ser:

```text
OBSERVABLE
REPRODUCIBLE
RELACIONADA CON UN DRIVER CONCRETO
Y SUFICIENTE PARA JUSTIFICAR EL COSTO DE DISTRIBUCIÓN
```

No será suficiente una preferencia tecnológica aislada.

---

## 46. Qué NO constituye por sí solo un criterio de migración

No será suficiente por sí solo:

- que existan módulos;
- que existan contratos internos;
- que exista alta concurrencia;
- que exista degradación sin causa identificada;
- que microservicios sea una arquitectura popular;
- que una herramienta de IA proponga microservicios;
- que una capacidad tenga muchos archivos;
- que exista una base de datos compartida;
- que se observe un único fallo aislado.

La decisión deberá estar relacionada con drivers y evidencia concreta.

---

## 47. Relación con ADR-001

ADR-001:

```text
SE MANTIENE VIGENTE COMO DECISIÓN ORIGINAL
```

ADR-004 registra una reevaluación posterior.

La propuesta del equipo es:

```text
RATIFICAR ADR-001
```

La ratificación deberá presentarse y defenderse en Comité 2.

ADR-004 no elimina el razonamiento histórico de ADR-001.

Añade:

- evidencia posterior;
- costos relativos;
- riesgos actualizados;
- criterios explícitos de re-decisión.

---

## 48. Relación con ADR-002

ADR-002 define:

- aislamiento de persistencia;
- contratos entre módulos;
- dependencias permitidas y prohibidas.

ADR-004 conserva esa estrategia.

No se introduce mediante este ADR una estrategia de persistencia distribuida.

---

## 49. Relación con ADR-003

ADR-003 evaluó mecanismos de integración interna y dejó fuera de alcance:

- adopción de microservicios;
- brokers externos obligatorios;
- bases de datos independientes;
- garantías generales de escalabilidad.

ADR-004 no contradice ADR-003.

La evidencia de ADR-003 apoya el principio de no generalizar mecanismos distribuidos sin necesidades demostradas.

---

## 50. Relación con Semana 12 — Actividad 1

El documento:

```text
docs/semana12/costos-relativos-distribucion.md
```

es una entrada directa para esta re-decisión.

Commit:

```text
99ed270
```

Resultado estructural principal:

```text
A = 1 unidad backend desplegable
B = 5 unidades backend desplegables
R = 5×
```

Los costos económicos y las horas de migración permanecen no estimables con la evidencia disponible.

---

## 51. Relación con la fitness function de Semana 12

Semana 12 requiere definir posteriormente una fitness function que incluya:

- regla;
- umbral;
- condición exacta que rompe el CI.

Las reglas ArchUnit existentes constituyen antecedentes útiles.

Sin embargo:

```text
LA FITNESS FUNCTION ESPECÍFICA DE SEMANA 12
TODAVÍA NO ESTÁ DEFINIDA EN ESTE ADR
```

No se considera cumplida la Actividad 3 por mencionar reglas existentes.

---

## 52. Propiedad candidata para Actividad 3

Una propiedad candidata es mantener la integridad de las fronteras modulares y evitar que el sistema evolucione hacia dependencias no controladas.

Ejemplos ya protegidos parcialmente:

- ausencia de ciclos;
- prohibición de acceso a internals de otro módulo;
- restricciones sobre `shared`.

La Actividad 3 deberá determinar explícitamente:

```text
REGLA
UMBRAL
MÉTRICA O CONDICIÓN
EVENTO EXACTO QUE ROMPE EL CI
```

La selección final se documentará por separado.

---

## 53. Relación con la evaluación de microservicios propuesta por IA

Semana 12 requiere además evaluar críticamente una estrategia de microservicios propuesta por IA.

Este ADR establece el baseline para esa evaluación.

La pregunta no será:

```text
¿LA IA ESTÁ EQUIVOCADA?
```

sino:

> ¿Qué complejidad introduce la propuesta y qué necesidad observada de RachaPro justifica cada componente adicional?

La evaluación deberá separar:

- necesidades demostradas;
- beneficios plausibles;
- supuestos;
- complejidad accidental;
- componentes no justificados.

---

## 54. Posición del autor para presentar al Comité 2

Con base en la evidencia actual, la posición que se propone defender es:

```text
MANTENER EL MONOLITO MODULAR
```

y:

```text
NO ADOPTAR ACTUALMENTE
LA SEPARACIÓN COMPLETA EN CINCO MICROSERVICIOS
```

El fundamento principal es:

> La evidencia posterior a ADR-001 justifica reevaluar la distribución, pero no demuestra actualmente una necesidad suficiente de separación física que compense las nuevas categorías de costo y complejidad identificadas.

Esta sección registra:

```text
LA POSICIÓN DEL AUTOR/EQUIPO
```

No registra:

```text
UN VEREDICTO DEL DOCENTE
```

ni:

```text
UNA APROBACIÓN DEL COMITÉ 2
```

El resultado real de la defensa deberá registrarse posteriormente si el docente emite observaciones, solicita cambios o valida la re-decisión.

---

## 55. Resultado esperado de la defensa

Durante Comité 2 se defenderá:

1. el método utilizado;
2. la evidencia experimental;
3. las limitaciones;
4. los costos relativos;
5. la re-decisión propuesta;
6. las condiciones que obligarían a reconsiderarla.

Si durante la defensa aparecen observaciones relevantes del docente, deberán registrarse posteriormente como:

```text
REVISIÓN POSTERIOR DEL ADR-004
```

sin modificar retroactivamente la evidencia disponible antes de la defensa.

---

## 56. Limitaciones del ADR

### L1 — B no está implementada

No existe comparación experimental directa entre A y B.

---

### L2 — Sin costos económicos medidos

No existe TCO monetario comparable.

---

### L3 — Sin horas reales de migración

No existe medición de esfuerzo de migración a cinco microservicios.

---

### L4 — Telemetría de crecimiento limitada

La campaña de carga no identifica causalmente el origen de la degradación.

---

### L5 — Timeout limitado a un escenario

La intervención `connectTimeout = 3 segundos` fue evaluada bajo condiciones controladas específicas.

No debe generalizarse a todos los modos de fallo.

---

### L6 — Incidencias en la matriz de resiliencia

Las pruebas de fallos y recuperación incluyen incidencias documentadas de identificadores y acciones manuales.

Estas limitaciones restringen cualquier afirmación sobre recuperación automática.

---

### L7 — Dependencias identificadas mediante análisis estático

Los tres pares dirigidos observados no representan frecuencia ni volumen de comunicación en runtime.

---

### L8 — Decisión pendiente de defensa

La re-decisión documentada corresponde al análisis del autor/equipo.

Todavía no constituye una aprobación formal del Comité 2.

---

## 57. Evidencia que podría cambiar la decisión

La decisión deberá reconsiderarse si se demuestra, por ejemplo, que:

```text
UNA CAPACIDAD REQUIERE RECURSOS
DESPROPORCIONADAMENTE MAYORES QUE LAS DEMÁS
```

o que:

```text
UNA CAPACIDAD NECESITA
DESPLIEGUES INDEPENDIENTES DE FORMA RECURRENTE
```

o que:

```text
UNA FALLA LOCAL PRODUCE IMPACTOS
INCOMPATIBLES CON EL REQUISITO DE DISPONIBILIDAD
```

o que:

```text
LA PERSISTENCIA COMPARTIDA
SE CONVIERTE EN UNA RESTRICCIÓN DEMOSTRADA
```

Los ejemplos anteriores representan condiciones de revisión.

No representan hechos actualmente demostrados.

---

## 58. Síntesis de la re-decisión

```text
DECISIÓN ORIGINAL
ADR-001:
Monolito modular.

EVIDENCIA POSTERIOR:
- fronteras modulares implementadas;
- ArchUnit;
- contratos internos;
- carga;
- degradación relativa;
- fallos;
- recuperación;
- experimento de timeout;
- costos relativos.

RESULTADO DEL ANÁLISIS:
La evidencia justifica reconsiderar la distribución,
pero no demuestra actualmente una necesidad suficiente
de migración completa.

PROPUESTA DEL EQUIPO:
Ratificar el monolito modular.

CINCO MICROSERVICIOS:
No adoptados actualmente.

REVERSIBILIDAD:
Sí.

VALIDACIÓN DOCENTE:
Pendiente de Comité 2.
```

---

## 59. Trazabilidad documental

### ADR-001

```text
docs/adr/0001-decision-estilo.md
```

### ADR-002

```text
docs/adr/0002-aislamiento-persistencia.md
```

### ADR-003

```text
docs/adr/0003-integracion-eventos-internos.md
```

### Costos relativos de Semana 12

```text
docs/semana12/costos-relativos-distribucion.md
```

### Antecedente de riesgos y costos

```text
docs/semana8/riesgos-tradeoffs-costos.md
```

---

## 60. Trazabilidad experimental

### Campaña de crecimiento

```text
experimentos/semana11-crecimiento-post-activities/resultados-crecimiento.md
```

### Fallos y resiliencia

```text
experimentos/semana11-crecimiento-post-activities/matrices-fallos-resiliencia.md
```

### Prerregistro del experimento de timeout

```text
experimentos/semana11-crecimiento-post-activities/prerregistro-spike-timeout.md
```

### Resultados del experimento de timeout

```text
experimentos/semana11-crecimiento-post-activities/resultados-spike-timeout.md
```

---

## 61. Trazabilidad de código y automatización

### Backend

```text
backend/src/main/kotlin/com/example/rachapro/backend/
```

### ActivityLookup

```text
backend/src/main/kotlin/com/example/rachapro/backend/activities/api/ActivityLookup.kt
```

### DefaultCategoryProvisioning

```text
backend/src/main/kotlin/com/example/rachapro/backend/activities/api/DefaultCategoryProvisioning.kt
```

### Fitness functions existentes

```text
backend/src/test/kotlin/com/example/rachapro/backend/architecture/ModularArchitectureTest.kt
```

### CI arquitectónico

```text
.github/workflows/backend-architecture.yml
```

---

## 62. Trazabilidad Git y estado

### Rama de trabajo

```text
docs/semana12-costos-distribucion
```

### Integración de Semana 11 disponible en master

```text
6fcf860
```

### Documento de costos disponible antes de ADR-004

```text
99ed270
```

### Commit de ADR-004

```text
PENDIENTE
```

El hash exacto no se anticipa.

Se registrará únicamente después de que Git cree el commit correspondiente.

### Estado actual del ADR

```text
ADR-004
RE-DECISIÓN DE DISTRIBUCIÓN ARQUITECTÓNICA

ESTADO DOCUMENTAL:
PROPUESTA DE DECISIÓN DEL EQUIPO.

PROPUESTA:
RATIFICAR EL MONOLITO MODULAR.

MIGRACIÓN COMPLETA A CINCO MICROSERVICIOS:
NO PROPUESTA ACTUALMENTE.

FUNDAMENTO:
EVIDENCIA ARQUITECTÓNICA,
EVIDENCIA EXPERIMENTAL,
COSTOS RELATIVOS,
TRADE-OFFS,
RIESGOS Y REVERSIBILIDAD.

APROBACIÓN / VEREDICTO DEL DOCENTE:
PENDIENTE DE COMITÉ 2.

FITNESS FUNCTION ESPECÍFICA DE SEMANA 12:
PENDIENTE — ACTIVIDAD 3.

EVALUACIÓN CRÍTICA DE MICROSERVICIOS PROPUESTOS POR IA:
PENDIENTE — ACTIVIDAD 4.

DEFENSA:
PENDIENTE — ACTIVIDAD 5.

REVISIÓN FUTURA:
REQUERIDA ANTE NUEVOS DRIVERS O EVIDENCIA.
```
---

# Cierre metodológico A5 — Módulo 6

## 63. Propósito de A5

Este documento constituye el entregable:

`A5 — ADR 4 y mini-comité`

del Módulo 6 de RachaPro.

Su propósito es consolidar:

- la decisión arquitectónica propuesta por el equipo;
- las alternativas consideradas;
- las fuentes utilizadas;
- los costos y consecuencias;
- la reversibilidad;
- los supuestos;
- las condiciones de revisión;
- el estado real del mini-comité.

Este cierre no modifica retroactivamente el contexto histórico del ADR.

---

## 64. Estado actual de ADR-004

Decisión propuesta por el equipo:

`RATIFICAR EL MONOLITO MODULAR`

Migración completa a cinco microservicios:

`NO ADOPTADA ACTUALMENTE`

Estado de la decisión:

`PROPUESTA DEL EQUIPO`

Estado del veredicto del comité:

`PENDIENTE DE REGISTRO DESPUÉS DEL COMITÉ REAL`

La propuesta del equipo no debe presentarse como aprobación docente.

---

## 65. Alternativas consideradas

### Alternativa A

`Mantener el monolito modular`

Características:

- cinco capacidades funcionales;
- una unidad backend desplegable;
- comunicación interna dentro del mismo proceso;
- contratos públicos entre módulos;
- persistencia físicamente compartida.

### Alternativa B

`Distribuir las cinco capacidades en cinco microservicios`

Características hipotéticas:

- cinco unidades backend desplegables;
- comunicación remota entre capacidades;
- mayor necesidad de observabilidad distribuida;
- tratamiento de fallos parciales;
- seguridad servicio-servicio;
- contratos remotos;
- posibles cambios de persistencia y despliegue.

La alternativa B no fue implementada ni medida experimentalmente.

---

## 66. Fuentes de evidencia

### A1 — crecimiento

`experimentos/semana11-crecimiento-post-activities/A1-resultados-crecimiento.md`

Aporta:

- campaña de carga;
- ocho niveles;
- 32 corridas documentadas;
- señal de degradación relativa del P95 desde 50 VUs;
- ausencia de causalidad identificada.

### A2 — fallos

`experimentos/semana11-crecimiento-post-activities/A2-matriz-fallos.md`

Aporta:

- F-01;
- indisponibilidad temporal de Spring Boot;
- tres repeticiones;
- evidencia de timeout;
- ausencia de falso éxito remoto.

### A3 — resiliencia

`experimentos/semana11-crecimiento-post-activities/A3-matriz-resiliencia.md`

Aporta:

- táctica `connectTimeout = 3 segundos`;
- comparación antes/después;
- reducción del `69,94 %`;
- recuperación operativa para nuevas operaciones;
- ausencia de evidencia suficiente de retry automático.

### A4 — distribución y costos

`docs/semana12/A4-costos-relativos-distribucion.md`

Aporta:

- comparación estructural de alternativas;
- 1 vs. 5 unidades desplegables;
- costos iniciales y recurrentes;
- supuestos explícitos;
- costos no estimables;
- reversibilidad.

---

## 67. Evidencia arquitectónica

El sistema actual conserva:

```text
Identity
Activities
Focus
Progress
Reminders
```