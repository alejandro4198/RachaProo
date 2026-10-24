# Costos relativos de alternativas de distribución — Semana 12

## 1. Objetivo y alcance

Este documento desarrolla la estimación de costos relativos de las alternativas de distribución arquitectónica consideradas para RachaPro durante la Semana 12 — Corte 2 y Comité 2.

La pregunta central del análisis es:

> ¿La evidencia actual de RachaPro justifica asumir el costo adicional de distribuir físicamente las capacidades que hoy funcionan dentro del monolito modular?

La comparación se realiza entre:

- **Alternativa A:** mantener el monolito modular actual.
- **Alternativa B:** distribuir las cinco capacidades funcionales principales en cinco microservicios.

El propósito no es asumir de antemano que una alternativa es superior.

Se busca:

- identificar costos estructurales observables;
- identificar nuevas categorías de costo;
- estimar relaciones cuantitativas cuando exista una unidad común;
- diferenciar hechos, mediciones, supuestos e inferencias;
- hacer explícitos los costos que no pueden estimarse con la evidencia actual;
- registrar beneficios y riesgos de ambas alternativas;
- producir evidencia que sirva como entrada para la re-decisión arquitectónica documentada posteriormente en ADR-004.

La Actividad 1 realiza la estimación de costos relativos.

La decisión arquitectónica formal corresponde a una actividad posterior y quedó documentada en ADR-004.

---

## 2. Antecedentes arquitectónicos

RachaPro utiliza actualmente un backend Spring Boot organizado como monolito modular.

Las capacidades funcionales principales identificadas son:

1. `identity`;
2. `activities`;
3. `focus`;
4. `progress`;
5. `reminders`.

También existe:

```text
shared
```

`shared` no se considera una sexta capacidad funcional de negocio.

La arquitectura actual puede representarse conceptualmente como:

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

Las cinco capacidades funcionales se ejecutan actualmente dentro de un mismo proceso backend.

---

## 3. Alternativa A — Monolito modular actual

La Alternativa A conserva la distribución física existente:

```text
1 backend Spring Boot
```

dentro del cual permanecen:

```text
identity
activities
focus
progress
reminders
```

La separación entre capacidades es lógica y está apoyada por:

- paquetes independientes;
- contratos públicos internos;
- reglas ArchUnit;
- restricciones sobre dependencias;
- control de ciclos;
- separación explícita de responsabilidades;
- propiedad lógica de persistencia.

### Características relevantes

- una unidad backend desplegable;
- un único proceso backend;
- llamadas internas entre capacidades cuando corresponde;
- ausencia de fallos de red entre módulos internos;
- despliegue conjunto;
- persistencia actualmente soportada sobre PostgreSQL;
- imposibilidad de escalar cada módulo como proceso independiente sin cambiar la distribución física.

---

## 4. Alternativa B — Cinco microservicios por capacidad

La Alternativa B es una arquitectura hipotética utilizada para la comparación.

Propone distribuir las cinco capacidades funcionales como unidades independientes:

```text
Identity Service
Activities Service
Focus Service
Progress Service
Reminders Service
```

Conceptualmente:

```text
                    ┌──────────────────┐
                    │ Identity Service │
                    └──────────────────┘
                           ▲
                           │
┌─────────────┐     ┌──────┴───────────┐
│ Android App │────▶│ Activities       │
└─────────────┘     │ Service          │
                    └──────────────────┘
                           ▲
                    ┌──────┴───────┐
                    │              │
             ┌──────┴─────┐ ┌──────┴─────────┐
             │ Focus      │ │ Reminders      │
             │ Service    │ │ Service        │
             └────────────┘ └────────────────┘

                    ┌──────────────────┐
                    │ Progress Service │
                    └──────────────────┘
```

La alternativa B no está implementada.

Por lo tanto, todavía no existen datos medidos sobre:

- infraestructura real;
- costo monetario;
- tiempo de desarrollo;
- costo mensual;
- cantidad real de pipelines;
- protocolos remotos definitivos;
- estrategia de persistencia;
- service discovery;
- mensajería;
- observabilidad distribuida;
- número exacto de endpoints interservicio;
- número de réplicas;
- tiempos de despliegue;
- esfuerzo operativo.

Estos elementos no deben presentarse como hechos.

---

## 5. Horizonte de comparación

La estimación separa dos clases de costo.

### 5.1. Costo inicial de transición

Corresponde al esfuerzo necesario para pasar desde la arquitectura actual hasta la alternativa evaluada.

Puede involucrar:

- reorganización de unidades desplegables;
- adaptación de contratos;
- transformación de interacciones internas;
- definición de protocolos remotos;
- adaptación de seguridad;
- adaptación de pruebas;
- cambios en observabilidad;
- cambios de infraestructura;
- cambios en persistencia;
- cambios en CI/CD.

Cuando no existe una medición real del esfuerzo requerido, el costo se registra como:

```text
NO ESTIMABLE CON LA EVIDENCIA ACTUAL
```

---

### 5.2. Costo recurrente de operación

Corresponde a los elementos que deben mantenerse después de adoptar una alternativa.

Incluye potencialmente:

- unidades desplegables;
- componentes observados;
- contratos remotos;
- configuración;
- mecanismos de resiliencia;
- pruebas de integración;
- coordinación de despliegues;
- infraestructura;
- monitoreo;
- tratamiento de incidentes.

No se establece un horizonte monetario anual porque no existen datos suficientes para sustentar una estimación económica defendible.

---

## 6. Clasificación de la evidencia

Cada elemento utilizado en el análisis se clasifica como uno de los siguientes tipos.

### 6.1. Medición

Resultado obtenido mediante una ejecución experimental.

Ejemplo:

```text
M_después = 3 014 ms
```

---

### 6.2. Hecho del sistema

Elemento directamente verificable mediante código, configuración o estructura del repositorio.

Ejemplos:

```text
5 capacidades funcionales
1 backend actual
3 pares dirigidos de imports intermodulares
```

---

### 6.3. Supuesto

Característica propuesta para una alternativa que todavía no está implementada.

Ejemplo:

```text
5 microservicios desplegados independientemente
```

---

### 6.4. Estimación

Valor derivado de hechos y supuestos explícitos.

Ejemplo:

```text
5 unidades desplegables / 1 unidad desplegable = 5×
```

---

### 6.5. Inferencia arquitectónica

Consecuencia razonable derivada de una decisión de diseño, sin haber sido medida directamente.

Ejemplo:

> Una comunicación entre dos procesos independientes introduce una frontera de red que no existe cuando ambos módulos se ejecutan dentro del mismo proceso.

---

## 7. Inventario estructural AS-IS

La inspección del backend identificó los siguientes paquetes principales:

```text
activities
focus
identity
progress
reminders
shared
```

Resultado:

```text
Capacidades funcionales = 5
Paquetes principales incluyendo shared = 6
```

`shared` no se contabiliza como capacidad funcional independiente.

---

## 8. Controladores REST identificados

Se identificaron ocho controladores REST:

```text
ActivityController
CategoryController
SubtaskController
PomodoroSessionController
AuthController
UserController
AchievementController
ReminderController
```

Resultado:

```text
Controladores REST identificados = 8
```

Este valor caracteriza la estructura actual.

No implica:

```text
8 microservicios
```

ni:

```text
8 unidades desplegables
```

---

## 9. Repositorios identificados

Se identificaron siete repositorios:

```text
ActivityRepository
CategoryRepository
SubtaskRepository
PomodoroSessionRepository
UserRepository
AchievementRepository
ReminderRepository
```

Resultado:

```text
Repositorios identificados = 7
```

Este conteo no permite determinar cuántas bases de datos debería tener una arquitectura de microservicios.

---

## 10. Contratos internos identificados

Dentro de Activities se localizaron los contratos:

```text
ActivityLookup
DefaultCategoryProvisioning
```

### ActivityLookup

Define:

```kotlin
interface ActivityLookup {
    fun existsActiveActivityForUser(
        activityId: Long,
        userId: Long
    ): Boolean
}
```

Implementación observada:

```text
ActivityLookupService
```

Consumidores observados fuera de Activities:

```text
PomodoroSessionService
ReminderService
```

---

### DefaultCategoryProvisioning

Define:

```kotlin
interface DefaultCategoryProvisioning {
    fun createDefaultsForUser(userId: Long)
}
```

Implementación observada:

```text
DefaultCategoryProvisioningService
```

El consumidor identificado:

```text
UserRegisteredV1Listener
```

pertenece al propio módulo Activities.

Por tanto, la existencia de `DefaultCategoryProvisioning` no representa por sí misma una comunicación entre dos microservicios.

---

## 11. Dependencias intermodulares observadas

La inspección automática de imports entre los cinco módulos funcionales produjo:

```text
activities -> identity
focus -> activities
reminders -> activities
```

Resultado:

```text
Pares dirigidos intermodulares observados = 3
```

### 11.1. activities -> identity

Archivo observado:

```text
activities/category/UserRegisteredV1Listener.kt
```

Import:

```kotlin
import com.example.rachapro.backend.identity.api.UserRegisteredV1
```

Esta relación demuestra dependencia estática de Activities hacia un tipo público de Identity.

No demuestra por sí sola:

- transporte HTTP;
- mensajería;
- comunicación síncrona;
- comunicación asíncrona;
- número de mensajes en ejecución.

---

### 11.2. focus -> activities

Archivo observado:

```text
focus/pomodoro/PomodoroSessionService.kt
```

Import:

```kotlin
import com.example.rachapro.backend.activities.api.ActivityLookup
```

Esto demuestra que Focus utiliza un contrato público de Activities.

Actualmente la interacción ocurre dentro del mismo proceso.

---

### 11.3. reminders -> activities

Archivo observado:

```text
reminders/reminder/ReminderService.kt
```

Import:

```kotlin
import com.example.rachapro.backend.activities.api.ActivityLookup
```

Esto demuestra que Reminders utiliza un contrato público de Activities.

Actualmente la interacción ocurre dentro del mismo proceso.

---

## 12. Alcance del conteo de dependencias

Los tres pares anteriores corresponden a:

```text
imports estáticos de Kotlin
```

No deben interpretarse automáticamente como:

```text
3 endpoints HTTP
```

ni como:

```text
3 llamadas remotas
```

ni como:

```text
3 integraciones distribuidas implementadas
```

Si se adoptara la Alternativa B, cada relación tendría que:

- mantenerse mediante alguna forma de comunicación interproceso;
- transformarse;
- eliminarse mediante un rediseño;
- o sustituirse por otro mecanismo.

El diseño concreto todavía no está definido.

---

## 13. Fitness functions arquitectónicas existentes

El archivo:

```text
backend/src/test/kotlin/com/example/rachapro/backend/architecture/ModularArchitectureTest.kt
```

contiene nueve reglas arquitectónicas observadas.

### Reglas de aislamiento

1. `activities internals are isolated`;
2. `identity internals are isolated`;
3. `focus internals are isolated`;
4. `progress internals are isolated`;
5. `reminders internals are isolated`.

### Reglas de Shared

6. `shared does not depend on business modules`;
7. `shared contains no business repositories`;
8. `shared contains no business entities`.

### Regla de ciclos

9. `modules are free of dependency cycles`.

La regla de ciclos utiliza:

```kotlin
slices()
    .matching("$base.(*)..")
    .should()
    .beFreeOfCycles()
```

Por tanto, la Alternativa A no corresponde a un monolito sin disciplina estructural.

La comparación es entre:

```text
monolito modular con fronteras verificables
```

y:

```text
cinco unidades físicamente distribuidas
```

---

## 14. CI arquitectónico actual

Se identificó:

```text
.github/workflows/backend-architecture.yml
```

El workflow ejecuta:

```bash
./gradlew test --tests "com.example.rachapro.backend.architecture.ModularArchitectureTest" --no-daemon
```

Se ejecuta en:

- pull requests hacia `master`;
- determinados pushes hacia `master` que afectan el backend o el propio workflow.

Resultado observado:

```text
Workflows arquitectónicos identificados = 1
```

Esto no implica que exista un único workflow total en cualquier infraestructura externa.

El alcance de la observación corresponde al repositorio inspeccionado.

---

## 15. Antecedentes de Semana 8

El documento:

```text
docs/semana8/riesgos-tradeoffs-costos.md
```

ya registraba el trade-off entre monolito modular y microservicios.

Entre los beneficios de la arquitectura actual se documentaron:

- despliegue único;
- llamadas internas simples;
- menor complejidad operacional;
- ausencia de fallos de red entre módulos internos;
- mayor facilidad para preservar las transacciones existentes.

Entre los costos o limitaciones se documentaron:

- ausencia de escalado independiente por módulo;
- proceso compartido;
- posibilidad de afectación global ante una falla severa del backend.

También se identificaron como costos evitados, por el momento:

- despliegues independientes;
- service discovery;
- comunicación de red entre capacidades;
- observabilidad distribuida;
- coordinación de contratos remotos;
- consistencia eventual cuando fuera necesaria;
- infraestructura adicional de mensajería cuando fuera necesaria.

Estos elementos constituyen antecedentes cualitativos.

No son una medición monetaria.

---

## 16. Evidencia experimental de Semana 11

Semana 11 aporta evidencia relevante para evaluar la distribución, pero no constituye una comparación directa entre monolito y microservicios.

### 16.1. Experimento de crecimiento

La campaña de crecimiento utilizó ocho niveles de concurrencia:

```text
1
10
50
100
250
500
750
1000 VUs
```

Se realizaron:

```text
4 corridas por nivel
32 ejecuciones oficiales
```

El valor representativo definido fue:

```text
mediana(P95_R2, P95_R3, P95_R4)
```

La primera condición preregistrada de degradación relativa del P95 se cumplió en:

```text
50 VUs
```

Esto permite afirmar que, bajo las condiciones probadas, se observó una señal de degradación relativa de latencia desde ese nivel.

Sin embargo, la evidencia no permite establecer causalmente:

```text
monolito modular -> degradación observada
```

Tampoco permite establecer:

```text
microservicios -> eliminación de la degradación
```

No se identificó causalmente si el origen dominante correspondía a:

- una capacidad funcional;
- PostgreSQL;
- consultas;
- CPU;
- memoria;
- GC;
- conexiones;
- threads;
- infraestructura;
- u otra causa.

Por tanto, la degradación observada no constituye evidencia suficiente para migrar automáticamente hacia microservicios.

### 16.2. Experimento de timeout

La comparación consolidada del experimento de timeout fue:

```text
M_antes   = 10 028 ms
M_después = 3 014 ms
```

Las mediciones posteriores válidas utilizadas fueron:

```text
3 024 ms
3 011 ms
3 014 ms
```

Reducción:

```text
69,94 %
```

Umbral preregistrado:

```text
50 %
```

Intervención aplicada:

```text
connectTimeout(3, TimeUnit.SECONDS)
```

Conclusión metodológica registrada:

```text
HIPÓTESIS RESPALDADA
```

dentro de las condiciones y limitaciones documentadas.

Este experimento no compara:

```text
monolito modular
vs.
microservicios
```

Además, la intervención corresponde al cliente Android.

No demuestra el comportamiento de futuros timeouts interservicio.

Sin embargo, sí aporta un antecedente relevante:

> El comportamiento de una comunicación que atraviesa una frontera de red puede depender de políticas explícitas de resiliencia, como el timeout.

La Alternativa B introduciría nuevas fronteras de proceso en aquellas relaciones que se mantengan entre servicios independientes.

---

## 17. Método de comparación relativa

Cuando dos valores representan la misma dimensión y utilizan la misma unidad se utiliza:

\[
R = \frac{C_B}{C_A}
\]

donde:

- \(C_A\) es el valor de la dimensión para la Alternativa A;
- \(C_B\) es el valor correspondiente para la Alternativa B.

El resultado se expresa como:

```text
R×
```

Ejemplo:

```text
A = 1 unidad desplegable
B = 5 unidades desplegables
R = 5 / 1 = 5×
```

---

## 18. Interpretación de órdenes de magnitud

Para este documento, un orden decimal de magnitud se utiliza como referencia aproximada:

```text
10× ≈ un orden de magnitud
```

Esta convención sirve para interpretar relaciones numéricas cuando existen cantidades comparables.

No implica que la actividad exija demostrar necesariamente una diferencia mínima de `10×`.

La expresión:

> estimar costos relativos en órdenes de magnitud

se entiende aquí como una estimación aproximada de escala, evitando una falsa precisión.

Por tanto:

```text
1×  -> misma escala estructural
5×  -> incremento relativo importante, pero inferior a 10×
10× -> aproximadamente un orden decimal de magnitud
```

La relación `5×` obtenida en este análisis no se presentará como un orden completo de magnitud.

---

## 19. Separación de dimensiones

No se suman dimensiones heterogéneas.

Por ejemplo, no sería válido hacer:

```text
5× despliegues
+ 3× observabilidad
+ 2× pruebas
= 10× complejidad
```

porque:

- utilizan unidades distintas;
- algunas ni siquiera se encuentran cuantificadas;
- no existe una función de agregación validada.

Cada dimensión se analiza por separado.

---

## 20. Matriz comparativa de costos relativos

| Dimensión | Alternativa A — Monolito modular | Alternativa B — 5 microservicios | Relación o cambio | Fundamento | Estado |
|---|---|---|---|---|---|
| Capacidades funcionales | 5 | 5 | `1×` | Hecho + definición | Cuantificable |
| Unidades backend desplegables | 1 | 5 | `5×` | Hecho + definición | Cuantificable |
| Pares dirigidos intermodulares observados | 3 | las relaciones deberán mantenerse, transformarse o eliminarse | No comparable directamente | Hecho + inferencia | Parcial |
| Fronteras de proceso entre capacidades | 0 entre módulos internos | `>0` cuando una relación cruce servicios | Nueva categoría | Consecuencia de B | Cualitativo |
| Fallos de red entre módulos internos | 0 | posibles donde exista comunicación remota | Nueva categoría | Consecuencia de B | Cualitativo |
| Contratos remotos | 0 entre módulos actuales | necesarios si una interacción cruza proceso | Nueva categoría | Inferencia | Cualitativo |
| Serialización entre módulos | no requerida para llamadas internas | requerida para comunicaciones remotas | Nueva categoría | Inferencia | Cualitativo |
| Timeout interservicio | no requerido entre módulos internos | requerido cuando una llamada remota síncrona pueda bloquear | Nueva categoría potencial | Inferencia + antecedente S11 | Cualitativo |
| Retry interservicio | no requerido entre módulos internos como problema de red | depende del diseño remoto | Nueva categoría potencial | Supuesto | No cuantificado |
| Observabilidad distribuida | no requerida entre módulos internos | necesaria para diagnosticar flujos distribuidos relevantes | Nueva categoría | Inferencia | No cuantificado |
| Despliegues independientes | 1 backend | hasta 5 unidades independientes por definición de B | `5×` unidades | Definición de alternativas | Cuantificable estructuralmente |
| CI arquitectónico observado | 1 workflow | adaptación o ampliación probable | Desconocida | Hecho + supuesto | No estimable |
| Persistencia física | PostgreSQL dentro de arquitectura actual | por definir | Desconocida | Hecho + supuesto | No estimable |
| Consistencia distribuida | no existe entre procesos internos | posible según estrategia de datos | Nueva categoría potencial | Supuesto | No estimable |
| Service discovery | no requerido entre módulos actuales | depende de la infraestructura elegida | Desconocido | Supuesto | No estimable |
| Mensajería adicional | no obligatoria actualmente | depende de las decisiones de B | Desconocido | Supuesto | No estimable |
| Seguridad servicio-servicio | no existe como frontera entre módulos del mismo proceso | podría requerirse entre procesos | Nueva categoría potencial | Inferencia | No estimable |
| Horas de migración | no medidas | no medidas | Desconocidas | Sin evidencia | No estimable |
| Horas por despliegue | no medidas | no medidas | Desconocidas | Sin evidencia | No estimable |
| Horas de mantenimiento | no medidas | no medidas | Desconocidas | Sin evidencia | No estimable |
| Costo mensual | no medido | no medido | Desconocido | Sin evidencia | No estimable |
| Costo anual | no medido | no medido | Desconocido | Sin evidencia | No estimable |

---

## 21. Resultado cuantitativo directamente defendible

La dimensión más claramente cuantificable con una unidad común es:

```text
número de unidades backend desplegables
```

### Alternativa A

```text
C_A = 1 unidad backend desplegable
```

### Alternativa B

```text
C_B = 5 unidades backend desplegables
```

Aplicando:

\[
R = \frac{C_B}{C_A}
\]

se obtiene:

\[
R = \frac{5}{1}=5
\]

Resultado:

```text
R = 5×
```

Interpretación:

> La Alternativa B multiplica por cinco el número de unidades backend desplegables respecto de la Alternativa A, según la definición de las alternativas utilizadas en este análisis.

Este resultado no significa:

```text
5× costo económico
```

ni:

```text
5× esfuerzo de implementación
```

ni:

```text
5× mantenimiento
```

ni:

```text
5× complejidad total
```

La unidad es exclusivamente:

```text
unidades backend desplegables
```

Además:

```text
5× < 10×
```

Por tanto, esta dimensión aislada no demuestra un aumento completo de un orden decimal de magnitud.

---

## 22. Estimación del costo inicial de transición

### 22.1. Alternativa A

Mantener la Alternativa A no requiere una migración hacia una nueva distribución física.

Los costos de evolución se relacionan principalmente con:

- mantenimiento de fronteras;
- evolución del código;
- actualización de pruebas;
- evolución de contratos internos;
- documentación;
- optimización de rendimiento;
- mejoras de resiliencia;
- ajustes de infraestructura existente.

Horas requeridas:

```text
NO MEDIDAS
```

Costo monetario:

```text
NO MEDIDO
```

### 22.2. Alternativa B

La Alternativa B requeriría resolver explícitamente al menos:

- creación de cinco unidades backend desplegables;
- definición de la estrategia de despliegue;
- adaptación de dependencias que crucen procesos;
- definición de protocolos de comunicación;
- serialización;
- manejo de fallos de comunicación;
- políticas de timeout;
- seguridad entre servicios cuando corresponda;
- observabilidad distribuida;
- pruebas entre servicios;
- estrategia de persistencia;
- manejo de consistencia cuando corresponda;
- adaptación del CI;
- coordinación de contratos remotos.

Horas requeridas:

```text
NO ESTIMABLES CON LA EVIDENCIA ACTUAL
```

Costo monetario:

```text
NO ESTIMABLE CON LA EVIDENCIA ACTUAL
```

---

## 23. Estimación del costo recurrente de operación

### 23.1. Alternativa A

Cantidad estructural conocida:

```text
1 unidad backend desplegable
```

Las dependencias entre módulos internos no atraviesan una frontera de red.

La operación mantiene un único proceso backend como unidad principal.

### 23.2. Alternativa B

Cantidad estructural definida:

```text
5 unidades backend desplegables
```

Esto representa:

```text
5×
```

el número de unidades backend frente a A.

Adicionalmente, las relaciones que crucen procesos introducirían responsabilidades relacionadas con:

- comunicación remota;
- diagnóstico interservicio;
- políticas de resiliencia;
- coordinación de contratos;
- observabilidad;
- seguridad entre servicios cuando corresponda.

El costo monetario recurrente permanece:

```text
NO ESTIMABLE CON LA EVIDENCIA ACTUAL
```

---

## 24. Costo relativo de comunicación

Actualmente se observaron:

```text
3 pares dirigidos de dependencias intermodulares
```

correspondientes a:

```text
activities -> identity
focus -> activities
reminders -> activities
```

En A estas relaciones se resuelven dentro del mismo backend.

En B, las relaciones que continúen existiendo deberán:

- atravesar procesos;
- o ser rediseñadas.

No se asume automáticamente:

```text
3 dependencias = 3 endpoints HTTP
```

Por tanto:

```text
Número exacto de comunicaciones remotas en B = NO DETERMINADO
```

---

## 25. Costo relativo de resiliencia

En A:

```text
fallos de red entre módulos internos = 0
```

porque los módulos comparten proceso.

En B, una interacción remota podría presentar:

- timeout;
- conexión rechazada;
- pérdida de conectividad;
- servicio remoto indisponible;
- latencia adicional;
- fallo parcial;
- respuestas tardías.

El experimento de Semana 11 mostró que una política explícita de timeout produjo una diferencia medible bajo una condición controlada de indisponibilidad desde el cliente Android.

Esto constituye un antecedente para considerar explícitamente políticas de resiliencia cuando una interacción atraviesa una frontera de red.

No demuestra que futuros timeouts entre microservicios presenten el mismo comportamiento.

No permite cuantificar el costo económico de esa resiliencia.

Resultado:

```text
Costo de resiliencia interservicio =
NUEVA CATEGORÍA, NO CUANTIFICADA
```

---

## 26. Costo relativo de observabilidad

A permite observar el backend principal como un proceso único.

B introduce múltiples procesos.

Cuando una operación atraviesa más de un proceso puede ser necesario correlacionar:

- logs;
- identificadores de solicitud;
- errores;
- latencias;
- métricas;
- dependencias;
- eventos.

La implementación concreta no existe.

Por tanto:

```text
Costo cuantitativo de observabilidad distribuida =
NO ESTIMABLE CON LA EVIDENCIA ACTUAL
```

Se registra como:

```text
NUEVA CATEGORÍA OPERACIONAL
```

---

## 27. Costo relativo de datos y consistencia

Actualmente RachaPro utiliza PostgreSQL dentro de una arquitectura de backend único.

La Alternativa B no define todavía si tendrá:

- una única base compartida;
- esquemas separados;
- una base por servicio;
- replicación;
- eventos;
- otro modelo.

Por tanto:

```text
Estrategia de persistencia de B = NO DEFINIDA
```

y:

```text
Costo relativo de consistencia distribuida = NO ESTIMABLE
```

No se afirma que los microservicios obliguen automáticamente a utilizar una base de datos por servicio.

---

## 28. Costo relativo de CI y pruebas

Actualmente se identificó un workflow arquitectónico:

```text
.github/workflows/backend-architecture.yml
```

que verifica:

```text
ModularArchitectureTest
```

La Alternativa B podría requerir nuevas verificaciones relacionadas con:

- contratos remotos;
- compatibilidad entre servicios;
- integración distribuida;
- fronteras de datos;
- despliegues independientes.

Sin embargo, no se conoce:

- cantidad futura de workflows;
- duración;
- infraestructura;
- frecuencia;
- costo;
- esfuerzo de mantenimiento.

Por tanto:

```text
Costo relativo de CI en B =
NO ESTIMABLE CON LA EVIDENCIA ACTUAL
```

---

## 29. Beneficios potenciales de la Alternativa B

Una evaluación de costos debe conservar también los beneficios potenciales.

La separación física puede permitir, dependiendo de su implementación:

- escalado independiente;
- despliegue independiente;
- mayor aislamiento entre procesos;
- evolución tecnológica independiente;
- asignación diferenciada de recursos;
- separación física de responsabilidades.

Estos beneficios son arquitectónicamente plausibles.

Sin embargo, la evidencia disponible no demuestra actualmente que RachaPro necesite:

- escalar una única capacidad de manera independiente;
- desplegar capacidades con ritmos significativamente diferentes;
- utilizar tecnologías diferentes por capacidad;
- separar físicamente una capacidad por requisitos regulatorios;
- separar una capacidad por requisitos de seguridad distintos;
- separar una capacidad debido a evidencia experimental de saturación aislada.

Por tanto, se registran como:

```text
BENEFICIOS POTENCIALES
```

y no como:

```text
BENEFICIOS EXPERIMENTALMENTE DEMOSTRADOS
```

---

## 30. Análisis de sensibilidad

La evaluación podría cambiar ante nueva evidencia.

### S1 — Escalado independiente demostrado

Si una capacidad concentra una proporción dominante de la carga y requiere escalarse de manera independiente, la utilidad de la distribución física aumentaría.

### S2 — Fallos que deban aislarse por proceso

Si una capacidad presenta fallos cuya propagación a todo el backend no sea aceptable, el aislamiento físico podría adquirir mayor valor.

### S3 — Equipos independientes

Si varias capacidades son desarrolladas por equipos independientes con ritmos distintos de entrega y despliegue, el costo del despliegue conjunto podría aumentar.

### S4 — Persistencia incompatible

Si una capacidad requiere un modelo de persistencia, escalabilidad o consistencia incompatible con el resto del sistema, una separación física podría estar mejor justificada.

### S5 — Requisitos de disponibilidad diferentes

Si alguna capacidad necesita una disponibilidad considerablemente superior o inferior a las demás, la distribución podría permitir asignar estrategias de operación distintas.

### S6 — Crecimiento de infraestructura

Si RachaPro evoluciona hacia múltiples réplicas, regiones, servicios gestionados o topologías de alta disponibilidad, deberá realizarse una nueva estimación con datos reales.

### S7 — Evidencia experimental nueva

Si futuros experimentos demuestran que una restricción específica del proceso único limita un atributo de calidad prioritario, el análisis deberá repetirse.

---

## 31. Limitaciones

### L1 — La Alternativa B no está implementada

No existe medición directa de una versión de RachaPro basada en cinco microservicios.

### L2 — Sin costos monetarios medidos

No existe información suficiente para estimar de manera defendible:

```text
COP/mes
USD/mes
COP/año
USD/año
```

para las dos alternativas.

### L3 — Sin horas reales de migración

No se ha realizado la migración propuesta.

Por tanto:

```text
horas de migración = NO MEDIDAS
```

### L4 — Dependencias obtenidas mediante análisis estático

Los tres pares intermodulares fueron identificados mediante imports Kotlin.

No se midió:

- frecuencia de invocación;
- volumen de llamadas;
- frecuencia de eventos;
- porcentaje de tráfico asociado.

### L5 — Semana 11 no comparó distribuciones

La evidencia experimental disponible no corresponde a:

```text
monolito modular vs. microservicios
```

Por tanto, no puede utilizarse como prueba directa de superioridad de A o B.

### L6 — `5×` tiene alcance estrictamente estructural

El valor:

```text
5×
```

corresponde únicamente a:

```text
unidades backend desplegables
```

No corresponde a:

- costo total;
- dinero;
- horas;
- complejidad total;
- disponibilidad;
- rendimiento.

### L7 — Infraestructura futura no definida

No se ha definido para B:

- plataforma de despliegue;
- cantidad de instancias;
- autoscaling;
- gateway;
- service discovery;
- mensajería;
- bases de datos;
- tracing;
- balanceadores.

No se asignan costos a componentes no definidos.

---

## 32. Evaluación del autor

La evidencia disponible permite afirmar que la Alternativa A ya posee separación lógica entre las principales capacidades del backend y mecanismos automatizados para proteger parte de sus fronteras.

La Alternativa B incrementaría el número de unidades backend desplegables de:

```text
1
```

a:

```text
5
```

lo que representa:

```text
5×
```

en esa dimensión estructural.

Este incremento no equivale por sí mismo a un orden decimal completo de magnitud y tampoco permite afirmar que el costo económico u operativo total sea cinco veces superior.

Sin embargo, la separación física introduciría categorías de costo que actualmente no existen entre los módulos internos del backend:

- comunicación remota;
- fallos de red interservicio;
- serialización;
- políticas de timeout y resiliencia entre procesos;
- observabilidad distribuida;
- coordinación de contratos remotos;
- potencial seguridad servicio-servicio;
- potencial complejidad adicional de persistencia y consistencia.

Por otra parte, la evidencia disponible hasta Semana 11 no demuestra que:

```text
los problemas observados sean causados por el monolito modular
```

ni que:

```text
cinco microservicios los resolverían
```

Por tanto, la evaluación de A1 es:

> **Con la evidencia actualmente disponible, no se observa una necesidad experimental suficiente que justifique asumir como obligatoria la separación completa de RachaPro en cinco microservicios.**

Esta afirmación no significa que los microservicios sean una arquitectura incorrecta.

Significa que, para el estado actual de RachaPro, todavía no se ha demostrado que sus beneficios potenciales compensen las nuevas categorías de costo y complejidad asociadas a la distribución física.

---

## 33. Evaluación de la proposición inicial

La proposición de trabajo es:

> Con la evidencia disponible actualmente en RachaPro, la separación completa en microservicios probablemente introduce un aumento de complejidad técnica y operacional sin que exista todavía evidencia experimental suficiente de que esa distribución física sea necesaria.

La evaluación se divide en componentes.

### P1 — Mayor estructura desplegable

Resultado:

```text
RESPALDADO
```

Evidencia:

```text
A = 1 unidad backend
B = 5 unidades backend
R = 5×
```

### P2 — Nuevas categorías operacionales

Resultado:

```text
RESPALDADO COMO CONSECUENCIA ARQUITECTÓNICA
```

Se identifican categorías adicionales asociadas a:

- comunicación remota;
- resiliencia;
- observabilidad;
- coordinación de contratos;
- potencial seguridad entre procesos.

### P3 — Aumento de un orden de magnitud del costo total

Resultado:

```text
NO DEMOSTRADO
```

No existen unidades económicas o de esfuerzo suficientes.

### P4 — Necesidad actual de distribución física

Resultado:

```text
NO OBSERVADA CON LA EVIDENCIA ACTUAL
```

No existe un experimento que demuestre que la separación física sea necesaria para resolver los problemas medidos.

### Resultado consolidado

La proposición queda:

```text
RESPALDADA PARCIALMENTE
```

porque:

- sí se observa mayor estructura desplegable;
- sí aparecen nuevas categorías operacionales;
- no puede cuantificarse el incremento total de costo;
- no se demuestra un aumento de `10×` del costo total;
- no existe evidencia experimental suficiente que haga necesaria la separación completa.

---

## 34. Resultado de la Actividad 1

### Alternativa A

```text
Monolito modular actual
```

### Alternativa B

```text
Cinco microservicios por capacidad funcional
```

### Cantidad estructural principal cuantificable

```text
Unidades backend desplegables
```

### Valores

```text
A = 1
B = 5
```

### Relación

\[
R = \frac{5}{1}=5
\]

```text
R = 5×
```

### Orden decimal completo demostrado en esta dimensión

```text
NO
```

porque:

```text
5× < 10×
```

### Costo monetario relativo

```text
NO ESTIMABLE CON LA EVIDENCIA ACTUAL
```

### Esfuerzo de migración

```text
NO ESTIMABLE CON LA EVIDENCIA ACTUAL
```

### Costo recurrente total

```text
NO ESTIMABLE CON LA EVIDENCIA ACTUAL
```

### Nuevas categorías operacionales en B

```text
IDENTIFICADAS
```

### Necesidad experimental demostrada de migrar completamente a cinco microservicios

```text
NO OBSERVADA
```

---

## 35. Implicación para ADR-004

La Actividad 1 no toma por sí sola la decisión arquitectónica formal.

Su resultado funciona como entrada para la re-decisión posterior.

La pregunta trasladada al ADR-004 fue:

> ¿Debe RachaPro conservar el monolito modular actual o asumir en este momento una distribución física mediante cinco microservicios?

La decisión debía considerar conjuntamente:

- evidencia experimental;
- costo relativo;
- atributos de calidad;
- riesgos;
- beneficios potenciales;
- nuevas categorías operacionales;
- reversibilidad;
- complejidad;
- necesidades actuales del sistema.

El valor:

```text
5×
```

no se utiliza como argumento único para decidir.

### Estado posterior

La re-decisión fue documentada posteriormente en:

```text
docs/adr/0004-redecision-distribucion.md
```

Commit de registro de ADR-004:

```text
ca61951
```

Mensaje:

```text
S12 A2: registrar propuesta de redecision ADR-004
```

La propuesta de decisión registrada es mantener el monolito modular como arquitectura de distribución actual, condicionando una futura re-decisión a nueva evidencia.

---

## 36. Relación con la evaluación crítica de microservicios

La Actividad 4 de Semana 12 utilizó esta estimación para evaluar si una propuesta de microservicios introducía complejidad no justificada por la evidencia.

La evaluación evitó dos posiciones extremas:

```text
microservicios siempre son mejores
```

y:

```text
microservicios siempre son innecesarios
```

La pregunta utilizada fue:

> ¿Qué complejidad introduce la distribución y qué necesidad concreta de RachaPro compensa esa complejidad?

### Estado posterior

La auditoría crítica quedó registrada en:

```text
docs/semana12/uso-ia/auditoria-microservicios.md
```

Commit:

```text
8ebb2f4
```

Mensaje:

```text
S12 A4: registrar auditoria critica de propuesta IA
```

La conclusión de A4 mantiene que la arquitectura distribuida es técnicamente viable, pero que la migración completa no está justificada actualmente por la evidencia disponible.

---

## 37. Relación con la fitness function

La evidencia de este documento también sirvió como entrada para seleccionar la fitness function de Semana 12.

La fitness function debía incluir explícitamente:

- regla;
- unidad o condición evaluada;
- umbral;
- condición exacta que rompe el CI.

Las reglas ArchUnit existentes constituyen antecedentes, pero no sustituyen automáticamente la nueva definición requerida para Semana 12.

### Estado posterior

La definición quedó documentada en:

```text
docs/semana12/fitness-function.md
```

Commit:

```text
2d40393
```

Mensaje:

```text
S12 A3: definir fitness function de fronteras modulares
```

La métrica definida es:

```text
V = número de pares únicos de clases origen-destino
    que representan dependencias intermodulares prohibidas
```

con:

```text
V = 0  -> PASS
V >= 1 -> FAIL
```

La definición metodológica de la fitness function está completada para Semana 12.

La implementación de la nueva regla en ArchUnit, certificación del baseline, ensayo controlado `PASS -> FAIL -> PASS` y validación específica en GitHub Actions permanecen como trabajo ejecutable posterior.

---

## 38. Relación con la defensa del Comité 2

Para la defensa se deberá poder explicar:

### Método

- identificación de alternativas;
- inspección del sistema actual;
- clasificación de evidencia;
- comparación por dimensiones;
- separación entre costos medidos y no estimables.

### Datos

```text
5 capacidades funcionales
1 backend actual
5 microservicios en alternativa B
3 pares dirigidos intermodulares observados
5× unidades backend desplegables
9 reglas arquitectónicas observadas
1 workflow arquitectónico identificado
```

### Evidencia experimental relacionada

```text
M_antes   = 10 028 ms
M_después = 3 014 ms
reducción = 69,94 %
```

con la aclaración de que el experimento de timeout:

- corresponde al cliente Android;
- no compara monolito frente a microservicios;
- no demuestra mayor disponibilidad del servidor.

También deberá conservarse la limitación de la campaña de crecimiento:

> se observó degradación relativa de latencia desde 50 VUs según el criterio preregistrado, pero no se identificó causalmente qué componente o recurso la produjo.

### Conclusión defendible

No existe actualmente evidencia experimental suficiente para considerar obligatoria una migración completa hacia cinco microservicios.

### Estado

```text
ACTIVIDAD 5:
PENDIENTE — ENSAYO Y DEFENSA DEL COMITÉ 2
```

---

## 39. Trazabilidad

### ADR existentes

```text
docs/adr/0001-decision-estilo.md
docs/adr/0002-aislamiento-persistencia.md
docs/adr/0003-integracion-eventos-internos.md
docs/adr/0004-redecision-distribucion.md
```

### Antecedente de costos

```text
docs/semana8/riesgos-tradeoffs-costos.md
```

### Evidencia de Semana 11

```text
experimentos/semana11-crecimiento-post-activities/00-preregistro.md
experimentos/semana11-crecimiento-post-activities/resultados-crecimiento.md
experimentos/semana11-crecimiento-post-activities/matrices-fallos-resiliencia.md
experimentos/semana11-crecimiento-post-activities/prerregistro-spike-timeout.md
experimentos/semana11-crecimiento-post-activities/resultados-spike-timeout.md
```

### Código inspeccionado

```text
backend/src/main/kotlin/com/example/rachapro/backend/
```

### Contratos

```text
backend/src/main/kotlin/com/example/rachapro/backend/activities/api/ActivityLookup.kt
backend/src/main/kotlin/com/example/rachapro/backend/activities/api/DefaultCategoryProvisioning.kt
```

### Pruebas arquitectónicas

```text
backend/src/test/kotlin/com/example/rachapro/backend/architecture/ModularArchitectureTest.kt
```

### CI arquitectónico

```text
.github/workflows/backend-architecture.yml
```

### Documentos posteriores de Semana 12

```text
docs/adr/0004-redecision-distribucion.md
docs/semana12/fitness-function.md
docs/semana12/uso-ia/prompt-original.md
docs/semana12/uso-ia/respuesta-original.md
docs/semana12/uso-ia/auditoria-microservicios.md
```

### Rama de trabajo

```text
docs/semana12-costos-distribucion
```

---

## 40. Trazabilidad Git

La rama de trabajo fue creada a partir de:

```text
master
```

con `master` actualizado hasta el cierre de Semana 11 en:

```text
6fcf860
```

correspondiente a:

```text
Merge pull request #63 from alejandro4198/exp/semana11-timeout3-intervencion
```

### Registro de la Actividad 1

Commit:

```text
99ed270
```

Mensaje:

```text
S12 A1: registrar estimacion relativa de distribucion
```

Este commit registró la primera versión consolidada de:

```text
docs/semana12/costos-relativos-distribucion.md
```

### Actividad 2

Commit:

```text
ca61951
```

Mensaje:

```text
S12 A2: registrar propuesta de redecision ADR-004
```

### Actividad 3

Commit:

```text
2d40393
```

Mensaje:

```text
S12 A3: definir fitness function de fronteras modulares
```

### Actividad 4 — evidencia de IA

Commits:

```text
645702a
72448c5
```

correspondientes a:

```text
preservación inicial de evidencia de IA
completitud y trazabilidad del prompt original
```

### Actividad 4 — auditoría crítica

Commit:

```text
8ebb2f4
```

Mensaje:

```text
S12 A4: registrar auditoria critica de propuesta IA
```

Una actualización documental posterior de este archivo no modifica el significado histórico de los commits anteriores.

---

## 41. Estado de la Actividad 1

```text
SEMANA 12
ACTIVIDAD 1
ESTIMAR EL COSTO RELATIVO DE LAS ALTERNATIVAS DE DISTRIBUCIÓN
```

### Estado

```text
ACTIVIDAD 1 COMPLETADA
```

### Alternativas

```text
DEFINIDAS
```

### Evidencia AS-IS

```text
INVENTARIADA
```

### Dependencias intermodulares

```text
IDENTIFICADAS MEDIANTE ANÁLISIS ESTÁTICO
```

### Método

```text
DEFINIDO
```

### Matriz comparativa

```text
CONSTRUIDA
```

### Relación cuantitativa estructural principal

```text
5× unidades backend desplegables
```

Esta relación corresponde exclusivamente a:

```text
1 unidad backend desplegable
vs.
5 unidades backend desplegables
```

No significa:

```text
5× costo económico
5× esfuerzo
5× mantenimiento
5× complejidad total
```

### Costos monetarios

```text
NO ESTIMABLES CON LA EVIDENCIA ACTUAL
```

### Esfuerzo de migración

```text
NO ESTIMABLE CON LA EVIDENCIA ACTUAL
```

### Nuevas categorías operacionales

```text
IDENTIFICADAS
```

### Limitaciones

```text
DOCUMENTADAS
```

### Evaluación de costos

```text
COMPLETADA
```

### Registro Git de A1

```text
99ed270
S12 A1: registrar estimacion relativa de distribucion
```

### Decisión arquitectónica formal

```text
DOCUMENTADA EN ADR-004
```

Referencia:

```text
docs/adr/0004-redecision-distribucion.md
```

Commit:

```text
ca61951
```

### Fitness function de Semana 12

```text
DEFINIDA Y DOCUMENTADA
```

Referencia:

```text
docs/semana12/fitness-function.md
```

Commit:

```text
2d40393
```

La definición metodológica está completa para Semana 12.

La implementación y validación ejecutable de la nueva regla permanecen como trabajo posterior y no deben confundirse con el alcance completado de A3.

### Evaluación crítica de microservicios

```text
COMPLETADA Y REGISTRADA
```

Referencia:

```text
docs/semana12/uso-ia/auditoria-microservicios.md
```

Commit de registro:

```text
8ebb2f4
```

### Defensa del Comité 2

```text
PENDIENTE — ACTIVIDAD 5
```

### Estado global relacionado

```text
A1 — COSTOS RELATIVOS:
COMPLETADA

A2 — ADR-004:
DOCUMENTADA

A3 — FITNESS FUNCTION:
DEFINIDA Y DOCUMENTADA

A4 — AUDITORÍA CRÍTICA DE IA:
COMPLETADA Y REGISTRADA

```
---

# Cierre metodológico A4 — Módulo 6

## 42. Propósito de A4

Este documento constituye el entregable:

`A4 — Distribución y costos`

del Módulo 6 de RachaPro.

El análisis compara alternativas de distribución utilizando evidencia arquitectónica, experimental y documental disponible en el proyecto.

La comparación no convierte estimaciones o inferencias en mediciones.

---

## 43. Alternativas comparadas

### Alternativa A — arquitectura actual

`Monolito modular`

Características observadas:

- un backend Kotlin + Spring Boot;
- cinco capacidades funcionales internas:
  - Identity;
  - Activities;
  - Focus;
  - Progress;
  - Reminders;
- una unidad backend desplegable;
- comunicación intermodular dentro del mismo proceso;
- PostgreSQL físicamente compartido;
- contratos públicos internos entre capacidades.

### Alternativa B — arquitectura distribuida hipotética

`Cinco microservicios`

Una unidad desplegable hipotética por capacidad:

```text
Identity Service
Activities Service
Focus Service
Progress Service
Reminders Service
