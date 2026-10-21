# Fitness Function de Fronteras Modulares — Semana 12

## 1. Objetivo

Este documento define la fitness function arquitectónica correspondiente a la Actividad 3 de Semana 12.

La fitness function tiene como propósito proteger una propiedad central de la arquitectura ratificada en ADR-004:

> Las capacidades funcionales del monolito modular deben conservar fronteras explícitas y solo pueden depender de la API pública de otra capacidad.

La definición incluye:

- propiedad arquitectónica;
- alcance;
- regla;
- unidad exacta de medición;
- baseline esperado;
- umbral;
- condición de éxito;
- condición de violación;
- instrumento de medición;
- relación con CI;
- casos permitidos;
- casos prohibidos;
- limitaciones;
- plan de validación;
- trazabilidad.

La implementación completa y el ensayo controlado en CI podrán realizarse posteriormente, de acuerdo con el cronograma del proyecto.

---

## 2. Relación con ADR-004

ADR-004 propone mantener el monolito modular como arquitectura actual de RachaPro.

La decisión se fundamenta, entre otros aspectos, en que las capacidades funcionales ya cuentan con:

- fronteras explícitas;
- contratos públicos;
- reglas ArchUnit;
- control de ciclos;
- CI arquitectónico.

Por tanto, mantener el monolito modular solo es sostenible si las fronteras internas continúan siendo verificables.

La fitness function definida en este documento busca detectar automáticamente una degradación arquitectónica como:

```text
focus -> activities.activity.ActivityRepository
```

aunque esa dependencia no produzca necesariamente un ciclo.

La propiedad protegida es, por tanto, diferente de la simple ausencia de ciclos.

---

## 3. Propiedad arquitectónica

La propiedad que se desea conservar es:

> Toda dependencia entre dos capacidades funcionales diferentes debe dirigirse exclusivamente a la API pública del módulo proveedor.

En términos conceptuales:

```text
Módulo consumidor
        |
        v
Módulo proveedor.api
```

es válido.

Mientras que:

```text
Módulo consumidor
        |
        v
Módulo proveedor.<internals>
```

es una violación.

---

## 4. Capacidades funcionales bajo control

La fitness function aplica exclusivamente a las cinco capacidades funcionales principales:

```text
activities
identity
focus
progress
reminders
```

Estas capacidades constituyen el universo de módulos de negocio evaluados por la nueva regla.

---

## 5. Tratamiento de `shared`

El paquete:

```text
shared
```

queda explícitamente excluido de esta nueva métrica.

La razón es que `shared` cumple una función transversal diferente de las capacidades funcionales.

Actualmente ya dispone de reglas específicas en ArchUnit:

```text
shared does not depend on business modules
shared contains no business repositories
shared contains no business entities
```

Por tanto:

```text
shared NO forma parte del conteo V
```

y sus restricciones seguirán siendo verificadas mediante las reglas arquitectónicas existentes.

Esta separación evita mezclar dos políticas distintas:

1. dependencias entre capacidades funcionales;
2. restricciones propias del espacio `shared`.

---

## 6. Convención de visibilidad modular

Para consumidores externos a una capacidad funcional se adopta la siguiente convención:

```text
<modulo>.api..
```

representa su superficie pública.

Por ejemplo:

```text
activities.api..
identity.api..
focus.api..
progress.api..
reminders.api..
```

Todo paquete diferente de `.api..` dentro de una capacidad se considera privado frente a otras capacidades.

Ejemplo:

```text
activities.activity..
activities.category..
activities.subtask..
```

son internos de Activities.

Asimismo, si en el futuro aparece:

```text
activities.analytics..
```

también será considerado interno mientras no se encuentre dentro de:

```text
activities.api..
```

Esto permite evitar una lista manual de todos los posibles paquetes internos.

---

## 7. Regla arquitectónica

Sea:

```text
A = módulo funcional consumidor
B = módulo funcional proveedor
```

donde:

```text
A != B
```

Si una clase perteneciente a A depende de una clase perteneciente a B, entonces la clase destino debe residir exclusivamente bajo:

```text
com.example.rachapro.backend.<B>.api..
```

Formalmente:

```text
A -> B.api = permitido

A -> B.<cualquier paquete diferente de api> = prohibido
```

---

## 8. Unidad de medición

La variable utilizada será:

```text
V
```

`V` representa el número de dependencias intermodulares prohibidas detectadas entre las cinco capacidades funcionales.

Sin embargo, para que la medición sea reproducible, la unidad de conteo debe definirse con precisión.

---

## 9. Unidad exacta de conteo de V

Cada unidad de `V` corresponde a un:

> **par único de clases origen-destino que represente una dependencia intermodular prohibida.**

Por tanto, `V` no cuenta:

- imports de código fuente;
- líneas de código;
- puntos individuales de uso;
- pares de módulos;
- llamadas runtime.

La unidad concreta es:

```text
(clase_origen, clase_destino)
```

### 9.1. Condiciones para que un par cuente en V

Un par:

```text
(clase_origen, clase_destino)
```

se contabiliza en `V` si se cumplen simultáneamente las siguientes condiciones:

1. `clase_origen` pertenece a una de las cinco capacidades funcionales;
2. `clase_destino` pertenece a una capacidad funcional diferente;
3. `clase_destino` no reside en el paquete público `.api..` de su capacidad;
4. ninguno de los extremos corresponde a `shared` para efectos de esta métrica.

Formalmente:

\[
V =
\left|
\left\{
(C_o,C_d):
M(C_o)\neq M(C_d)
\land
C_d\notin M(C_d).api
\right\}
\right|
\]

donde:

```text
Co = clase origen
Cd = clase destino
M(C) = capacidad funcional propietaria de la clase
```

y el universo de capacidades es:

```text
activities
identity
focus
progress
reminders
```

---

## 10. Ejemplo de conteo con una única violación

Supóngase la relación:

```text
FocusService -> ActivityRepository
```

donde:

```text
FocusService
```

pertenece a:

```text
focus
```

y:

```text
ActivityRepository
```

pertenece a un paquete interno de:

```text
activities
```

El resultado será:

```text
V = 1
```

---

## 11. Ejemplo con dos clases destino diferentes

Si la misma clase origen depende de dos clases internas distintas:

```text
FocusService -> ActivityRepository
FocusService -> ActivityEntity
```

existen dos pares únicos origen-destino.

Por tanto:

```text
V = 2
```

---

## 12. Referencias repetidas al mismo par de clases

Si existen múltiples puntos de uso que producen dependencia entre el mismo par:

```text
FocusService -> ActivityRepository
```

el par se contabiliza una única vez.

Por ejemplo, aunque `FocusService` utilice `ActivityRepository` en varios métodos, para esta métrica continúa existiendo:

```text
(FocusService, ActivityRepository)
```

como un único par.

Por tanto, aporta:

```text
1
```

a `V`.

---

## 13. Ejemplo con tres pares distintos

Supóngase:

```text
FocusService -> ActivityRepository
FocusService -> ActivityEntity
PomodoroSessionService -> ActivityRepository
```

Existen tres pares únicos de clases:

```text
(FocusService, ActivityRepository)

(FocusService, ActivityEntity)

(PomodoroSessionService, ActivityRepository)
```

Resultado:

```text
V = 3
```

---

## 14. Por qué no se cuentan pares de módulos

No se utilizará como unidad:

```text
focus -> activities
```

porque una única relación entre módulos podría contener:

```text
1
```

o:

```text
100
```

dependencias indebidas entre clases.

Contar solamente pares de módulos ocultaría la magnitud interna de una violación.

---

## 15. Por qué no se cuentan imports

La exploración inicial utilizó imports Kotlin para identificar relaciones candidatas.

Sin embargo:

```text
cantidad de imports
```

no es la unidad oficial de esta fitness function.

ArchUnit analiza dependencias de clases compiladas.

Por tanto:

```text
imports encontrados
!= necesariamente
dependencias estructurales detectadas por ArchUnit
```

La unidad oficial será el par único:

```text
(clase_origen, clase_destino)
```

detectado mediante el instrumento definitivo.

---

## 16. Baseline esperado

La inspección previa mediante imports estáticos identificó:

```text
reminders -> activities.api.ActivityLookup
activities -> identity.api.UserRegisteredV1
focus -> activities.api.ActivityLookup
```

Los tres destinos corresponden a paquetes `api`.

Por tanto, el resultado esperado de la nueva regla es:

```text
V = 0
```

Sin embargo, en el momento de redactar este documento dicho valor se considera:

```text
BASELINE ESPERADO
```

y no:

```text
BASELINE CERTIFICADO
```

La razón es que el script previo inspeccionó imports Kotlin, mientras que ArchUnit analizará dependencias entre clases compiladas.

La equivalencia entre ambos instrumentos no se asume.

---

## 17. Umbral

El umbral definido es:

\[
V=0
\]

Interpretación:

```text
V = 0
Arquitectura conforme
```

```text
V >= 1
Violación arquitectónica
```

No se permite tolerancia distinta de cero.

---

## 18. Justificación del umbral

La regla protege una frontera estructural.

Permitir una dependencia directa hacia internals significa aceptar una ruptura de dicha frontera.

Por ejemplo:

```text
focus -> activities.activity.ActivityRepository
```

haría que Focus conociera directamente detalles internos de Activities.

Incluso una única ocurrencia:

\[
V=1
\]

es suficiente para demostrar que la propiedad arquitectónica fue violada.

Por tanto, el umbral:

```text
V = 0
```

es coherente con la naturaleza de la regla.

---

## 19. Condición de éxito

La fitness function se considera satisfecha cuando:

```text
V = 0
```

Es decir:

> No existe ningún par único de clases origen-destino que represente una dependencia desde una capacidad funcional hacia internals de otra capacidad funcional.

---

## 20. Condición de violación

La fitness function se considera violada cuando:

```text
V >= 1
```

Es decir:

> Existe al menos un par único de clases origen-destino que representa una dependencia intermodular prohibida.

---

## 21. Caso permitido — Focus hacia Activities API

Ejemplo actual:

```kotlin
import com.example.rachapro.backend.activities.api.ActivityLookup
```

Origen:

```text
focus
```

Destino:

```text
activities.api
```

Resultado esperado:

```text
PERMITIDO
```

Motivo:

> Focus consume una clase perteneciente a la superficie pública de Activities.

---

## 22. Caso permitido — Reminders hacia Activities API

Ejemplo actual:

```kotlin
import com.example.rachapro.backend.activities.api.ActivityLookup
```

Origen:

```text
reminders
```

Destino:

```text
activities.api
```

Resultado:

```text
PERMITIDO
```

---

## 23. Caso permitido — Activities hacia Identity API

Ejemplo actual:

```kotlin
import com.example.rachapro.backend.identity.api.UserRegisteredV1
```

Origen:

```text
activities
```

Destino:

```text
identity.api
```

Resultado:

```text
PERMITIDO
```

---

## 24. Caso prohibido — Repository de otro módulo

Ejemplo hipotético:

```kotlin
import com.example.rachapro.backend.activities.activity.ActivityRepository
```

desde Focus.

Relación:

```text
FocusService -> ActivityRepository
```

y estructuralmente:

```text
focus -> activities.activity
```

Resultado esperado:

```text
VIOLACIÓN
```

Si no existen otras violaciones:

```text
V = 1
```

---

## 25. Caso prohibido — Internals de Identity

Ejemplo hipotético:

```kotlin
import com.example.rachapro.backend.identity.user.UserRepository
```

desde Activities.

Relación:

```text
activities -> identity.user
```

Resultado:

```text
VIOLACIÓN
```

---

## 26. Caso prohibido — Paquete futuro

Supóngase que posteriormente se crea:

```text
activities.analytics
```

y Focus utiliza:

```kotlin
import com.example.rachapro.backend.activities.analytics.ActivityAnalyticsService
```

La fitness function deberá detectar:

```text
focus -> activities.analytics
```

como:

```text
VIOLACIÓN
```

aunque `analytics` no existiera cuando se definió la fitness function.

Esta capacidad constituye una ventaja de la convención:

```text
solo .api es público
```

frente a enumerar manualmente paquetes internos.

---

## 27. Dependencias dentro del mismo módulo

Una dependencia dentro de una misma capacidad no forma parte de `V`.

Ejemplo:

```text
activities.category.CategoryService
        ->
activities.activity.ActivityService
```

no se considera una dependencia intermodular porque:

```text
origen = activities
destino = activities
```

por tanto:

```text
M(Co) = M(Cd)
```

y el par queda fuera del universo de `V`.

Esto no implica que toda dependencia interna sea necesariamente correcta.

Solo significa que esta fitness function no la evalúa.

---

## 28. Dependencias hacia `shared`

Una relación como:

```text
activities -> shared
```

queda fuera de la métrica:

```text
V
```

de esta fitness function.

Esto no significa que `shared` esté libre de restricciones.

Sus reglas existentes continúan aplicando.

---

## 29. Dependencias desde `shared`

Las dependencias originadas desde:

```text
shared
```

hacia módulos funcionales tampoco forman parte del conteo `V`.

Sin embargo, ya existe una regla ArchUnit específica:

```text
shared does not depend on business modules
```

que protege esta dirección.

Por tanto, la exclusión de `shared` de `V` no crea intencionalmente una ausencia de control.

---

## 30. Instrumento de medición

La herramienta seleccionada para implementar la medición es:

```text
ArchUnit
```

La razón es que ArchUnit inspecciona relaciones entre clases compiladas y permite evaluar restricciones arquitectónicas sobre paquetes y dependencias.

La regla deberá incorporarse a:

```text
backend/src/test/kotlin/com/example/rachapro/backend/architecture/ModularArchitectureTest.kt
```

o a una suite equivalente incluida explícitamente en el workflow arquitectónico.

---

## 31. Diferencia entre exploración e instrumento definitivo

El script de Python utilizado previamente fue útil para identificar:

```text
3 relaciones intermodulares visibles mediante imports
```

pero constituye:

```text
EVIDENCIA EXPLORATORIA
```

La medición oficial de `V` deberá provenir de:

```text
ArchUnit
```

Por tanto:

```text
script de imports
!=
fitness function definitiva
```

---

## 32. Relación con las nueve reglas existentes

Actualmente `ModularArchitectureTest` contiene nueve pruebas.

Cinco protegen internals específicos:

```text
activities internals are isolated
identity internals are isolated
focus internals are isolated
progress internals are isolated
reminders internals are isolated
```

Tres protegen `shared`:

```text
shared does not depend on business modules
shared contains no business repositories
shared contains no business entities
```

Una protege ciclos:

```text
modules are free of dependency cycles
```

La nueva regla no sustituye esas pruebas.

Las complementa.

---

## 33. Diferencia respecto de las reglas actuales de internals

Las reglas de aislamiento actuales enumeran paquetes internos conocidos.

Por ejemplo, Activities protege explícitamente:

```text
activities.activity..
activities.category..
activities.subtask..
```

Si posteriormente aparece:

```text
activities.analytics..
```

una regla basada exclusivamente en la lista actual tendría que ser modificada para incluir el nuevo paquete.

La nueva fitness function invierte esa lógica:

```text
NO enumerar todos los paquetes privados.
DEFINIR únicamente la superficie pública.
```

Convención:

```text
<modulo>.api.. = público
todo lo demás = privado frente a otras capacidades
```

---

## 34. Diferencia respecto de la regla de ciclos

La prueba:

```text
modules are free of dependency cycles
```

protege contra relaciones cíclicas.

Sin embargo, una violación de encapsulamiento puede ocurrir sin crear un ciclo.

Ejemplo:

```text
FocusService -> ActivityRepository
```

Si Activities no depende de Focus, la arquitectura podría seguir siendo acíclica.

Entonces:

```text
regla de ciclos = PASS
```

mientras:

```text
fitness function de frontera pública = FAIL
```

La nueva regla protege una propiedad distinta.

---

## 35. Medición propuesta

La ejecución de la nueva fitness function deberá producir conceptualmente:

```text
V = número de pares únicos de clases origen-destino
    que representan dependencias intermodulares prohibidas
```

Resultado esperado inicialmente:

```text
V = 0
```

Si aparece cualquier dependencia prohibida:

```text
V >= 1
```

la prueba deberá fallar.

---

## 36. Regla de aceptación

Se define:

\[
PASS \iff V=0
\]

y:

\[
FAIL \iff V\ge1
\]

No existe rango intermedio.

---

## 37. Relación con GitHub Actions

El workflow arquitectónico actual es:

```text
.github/workflows/backend-architecture.yml
```

y ejecuta:

```bash
./gradlew test --tests "com.example.rachapro.backend.architecture.ModularArchitectureTest" --no-daemon
```

Si la nueva fitness function se incorpora a:

```text
ModularArchitectureTest
```

formará parte del conjunto ejecutado por ese comando.

---

## 38. Cadena esperada de fallo del CI

La cadena esperada es:

```text
Se introduce una dependencia prohibida
        ↓
ArchUnit detecta al menos un par prohibido
        ↓
V >= 1
        ↓
la prueba falla
        ↓
Gradle test termina con código != 0
        ↓
el step de GitHub Actions falla
        ↓
el job "ArchUnit fitness functions" falla
```

Esta cadena representa el comportamiento esperado.

Todavía deberá comprobarse mediante una ejecución controlada de la nueva regla.

---

## 39. Qué rompe exactamente el CI

Después de implementar la fitness function, romperá el CI cualquier cambio que produzca al menos un par único:

```text
(clase_origen, clase_destino)
```

tal que:

```text
M(clase_origen) != M(clase_destino)
```

y:

```text
clase_destino NO pertenece a <modulo_destino>.api..
```

para las cinco capacidades funcionales incluidas.

Ejemplos:

```text
FocusService -> ActivityRepository
```

```text
ActivityService -> UserRepository
```

```text
ReminderService -> AchievementEntity
```

```text
UserService -> CategoryRepository
```

Una sola relación prohibida será suficiente.

---

## 40. Condiciones que no rompen esta fitness function

No deberá fallar debido exclusivamente a relaciones válidas como:

```text
focus -> activities.api
```

```text
reminders -> activities.api
```

```text
activities -> identity.api
```

siempre que no exista otra dependencia prohibida.

Tampoco debe contabilizar:

```text
dependencias dentro del mismo módulo
```

ni:

```text
dependencias relacionadas con shared
```

dentro de `V`, porque `shared` posee su propia política.

---

## 41. Alcance del workflow actual

El workflow existente se ejecuta en:

```text
pull_request hacia master
```

y también en determinados:

```text
push hacia master
```

según los paths configurados.

Por tanto:

> Un push cualquiera a una rama de trabajo no demuestra por sí solo que el workflow de GitHub Actions haya sido ejecutado.

Esta diferencia deberá conservarse al registrar evidencia futura.

---

## 42. Plan de implementación

La implementación prevista consiste en:

1. definir la nueva regla ArchUnit;
2. incorporarla a la suite arquitectónica;
3. ejecutar la suite sobre el código actual;
4. medir el baseline real;
5. comprobar el resultado conforme;
6. introducir temporalmente una infracción deliberada;
7. ejecutar nuevamente la suite;
8. comprobar que la prueba falla;
9. revertir completamente la infracción;
10. ejecutar nuevamente la suite;
11. comprobar la recuperación del estado conforme;
12. cuando corresponda, verificar también GitHub Actions.

---

## 43. Ensayo controlado positivo

El primer ensayo deberá ejecutarse sobre el código actual sin una mutación deliberada.

Resultado esperado:

```text
V = 0
```

y:

```text
BUILD SUCCESSFUL
```

Si se obtiene este resultado mediante la nueva regla ArchUnit, podrá registrarse:

```text
BASELINE CERTIFICADO = V = 0
```

---

## 44. Ensayo controlado negativo

Después deberá introducirse temporalmente una dependencia prohibida.

Ejemplo conceptual:

```text
FocusService -> ActivityRepository
```

Resultado esperado:

```text
V >= 1
```

y:

```text
TEST FAILED
```

seguido de:

```text
BUILD FAILED
```

La infracción será exclusivamente experimental.

No deberá permanecer en la arquitectura.

---

## 45. Ensayo de recuperación

Después de revertir la infracción:

```text
V = 0
```

deberá restaurarse.

Resultado esperado:

```text
BUILD SUCCESSFUL
```

Esto demostrará:

```text
PASS
    ↓
FAIL
    ↓
PASS
```

para la nueva regla.

---

## 46. Evidencia esperada del ensayo

La futura validación deberá conservar como mínimo:

- comando ejecutado;
- estado o commit base;
- definición exacta de la regla;
- resultado positivo inicial;
- valor medido de `V`;
- mutación introducida;
- clases implicadas en la violación;
- resultado negativo;
- mensaje producido por ArchUnit;
- resultado de Gradle;
- reversión de la mutación;
- resultado final positivo.

Si también se valida GitHub Actions deberán conservarse:

- ejecución del workflow;
- job ejecutado;
- estado fallido;
- causa del fallo;
- ejecución posterior en verde.

---

## 47. Política Git para el ensayo futuro

La infracción deliberada no debe integrarse permanentemente.

La evidencia deberá distinguir:

```text
CÓDIGO PRODUCTIVO
```

de:

```text
MUTACIÓN TEMPORAL DE VALIDACIÓN
```

La secuencia recomendada es:

```text
definición de la fitness function
        ↓
implementación de la regla
        ↓
baseline
        ↓
mutación controlada
        ↓
detección del fallo
        ↓
reversión
        ↓
validación final
```

La definición documental deberá existir antes de observar los resultados experimentales.

---

## 48. Estado de implementación durante Semana 12

La Actividad 3 exige actualmente:

```text
DEFINIR
```

la fitness function.

Por tanto, durante Semana 12 deben quedar definidos:

```text
propiedad
alcance
regla
unidad exacta
umbral
condición de éxito
condición de fallo
instrumento
relación con CI
qué rompe el CI
plan de validación
```

La implementación y validación ejecutable no deben presentarse como completadas mientras no exista evidencia de ejecución.

---

## 49. Relación con ADR-004

ADR-004 propone mantener el monolito modular.

Esta fitness function protege una condición necesaria para sostener esa decisión:

> La modularidad debe conservar fronteras estructuralmente verificables y no solamente una organización nominal por paquetes.

Si las capacidades empiezan a consumir internals de otras capacidades, el backend podría mantener una única unidad desplegable pero perder progresivamente la independencia modular que sustenta ADR-004.

La fitness function permite detectar esa erosión.

---

## 50. Relación con ADR-002

ADR-002 define:

- contratos;
- dependencias permitidas;
- dependencias prohibidas;
- aislamiento de persistencia.

La nueva fitness function es coherente con ese ADR porque formaliza:

```text
una capacidad externa
        ↓
solo puede entrar
        ↓
por la API pública del proveedor
```

No sustituye ADR-002.

Automatiza una propiedad derivada de dicha decisión.

---

## 51. Relación con ADR-001

ADR-001 estableció:

- cinco capacidades funcionales;
- contratos;
- límites;
- ownership;
- fitness functions;
- una única unidad de despliegue.

La nueva regla protege:

```text
LAS FRONTERAS FUNCIONALES
NO DEBEN SER ATRAVESADAS
MEDIANTE INTERNALS AJENOS
```

Esto ayuda a mantener el estilo arquitectónico seleccionado.

---

## 52. Relación con las dependencias actuales

La exploración previa identificó:

```text
reminders -> activities.api.ActivityLookup
activities -> identity.api.UserRegisteredV1
focus -> activities.api.ActivityLookup
```

Estas relaciones son compatibles con la propiedad propuesta.

No se utiliza:

```text
3
```

como máximo permitido.

La cantidad total de dependencias legítimas no constituye el umbral.

Lo que se controla es:

```text
cantidad de pares prohibidos
```

---

## 53. Por qué el número total de dependencias no es el umbral

No se define:

```text
máximo 3 dependencias intermodulares
```

porque no existe evidencia que justifique ese límite.

Una cuarta dependencia podría ser válida:

```text
A -> B.api
```

mientras que una única dependencia podría ser incorrecta:

```text
A -> B.internal
```

Por tanto:

```text
número total de relaciones intermodulares
```

no es la métrica seleccionada.

La métrica correcta es:

```text
número de pares únicos de clases
con dependencia intermodular prohibida
```

---

## 54. Limitaciones

### L1 — La nueva regla todavía no fue ejecutada

El valor:

```text
V = 0
```

es todavía un baseline esperado.

---

### L2 — La exploración mediante imports no equivale a ArchUnit

La búsqueda sobre código fuente no demuestra ausencia completa de dependencias de bytecode.

---

### L3 — `shared` está excluido de V

La nueva métrica no evalúa dependencias relacionadas con `shared`.

Estas permanecen bajo reglas específicas.

---

### L4 — No mide cantidad total de acoplamiento legítimo

Una arquitectura puede acumular muchas dependencias a través de APIs públicas sin violar esta regla.

---

### L5 — No evalúa calidad semántica de las APIs

Una clase ubicada en:

```text
<modulo>.api
```

podría seguir estando conceptualmente mal diseñada.

La fitness function evalúa una propiedad estructural.

---

### L6 — No mide rendimiento

Cumplir:

```text
V = 0
```

no demuestra:

- menor latencia;
- mayor throughput;
- mejor disponibilidad;
- menor consumo de recursos.

---

### L7 — No demuestra aislamiento físico

Los módulos continúan compartiendo proceso.

La regla protege encapsulamiento lógico.

---

### L8 — El conteo V depende de la semántica de dependencia usada por ArchUnit

La implementación deberá garantizar que la extracción de pares únicos de clases se realice de forma consistente con las dependencias que ArchUnit reconoce.

La definición documental fija la unidad conceptual, pero la implementación deberá evitar doble conteo del mismo par origen-destino.

---

## 55. Riesgos de la fitness function

### R-01 — Falsa sensación de arquitectura correcta

Un resultado:

```text
V = 0
```

no demuestra que toda la arquitectura sea correcta.

**Mitigación:**

mantener múltiples fitness functions y revisión arquitectónica.

---

### R-02 — Inflar `.api`

Un desarrollador podría mover clases a:

```text
.api
```

solo para evitar una violación.

**Mitigación:**

mantener APIs pequeñas y revisar semánticamente su contenido.

---

### R-03 — Abuso de `shared`

Podría utilizarse `shared` para evitar la nueva regla.

**Mitigación:**

mantener las reglas específicas existentes sobre `shared`.

---

### R-04 — Dependencia semánticamente incorrecta pero estructuralmente válida

Una relación:

```text
A -> B.api
```

puede pasar técnicamente y seguir siendo conceptualmente incorrecta.

**Mitigación:**

ADR, revisión de contratos, Context Map y revisión de arquitectura.

---

### R-05 — Interpretar V como métrica absoluta de acoplamiento

`V` mide violaciones, no acoplamiento total.

**Mitigación:**

documentar siempre:

```text
V = violations
```

y no:

```text
V = total coupling
```

---

## 56. Criterios de revisión de esta fitness function

La regla deberá reevaluarse si:

- cambia la convención `.api`;
- aparecen nuevas superficies públicas;
- se agregan nuevas capacidades funcionales;
- una capacidad se convierte en proceso independiente;
- cambia el papel de `shared`;
- ArchUnit deja de ser el instrumento;
- se modifica la estructura base de paquetes;
- RachaPro deja de utilizar un monolito modular.

---

## 57. Definición consolidada

```text
NOMBRE:
Inter-module Public API Boundary Fitness Function

PROPIEDAD:
Toda dependencia entre capacidades funcionales diferentes
debe dirigirse exclusivamente a la API pública
de la capacidad proveedora.

CAPACIDADES BAJO CONTROL:
activities
identity
focus
progress
reminders

SHARED:
Excluido de V.
Continúa protegido mediante sus reglas ArchUnit existentes.

UNIDAD:
Un par único de clases origen-destino
que represente una dependencia prohibida
entre capacidades funcionales diferentes.

VARIABLE:
V

DEFINICIÓN:
V = cardinalidad del conjunto de pares únicos
(clase_origen, clase_destino)
que cruzan capacidades diferentes
y cuyo destino no pertenece a <modulo_destino>.api..

BASELINE:
V = 0 esperado.
Pendiente de certificación mediante la nueva regla ArchUnit.

UMBRAL:
V = 0.

PASS:
V = 0.

FAIL:
V >= 1.

INSTRUMENTO:
ArchUnit.

SUITE OBJETIVO:
ModularArchitectureTest.

CI:
.github/workflows/backend-architecture.yml

COMANDO ACTUAL:
./gradlew test --tests \
"com.example.rachapro.backend.architecture.ModularArchitectureTest" \
--no-daemon

QUÉ ROMPE EL CI:
Cualquier par único de clases origen-destino
que represente una dependencia desde una capacidad funcional
hacia un paquete distinto de .api
de otra capacidad funcional,
cuando la regla se encuentre implementada
en la suite ejecutada por CI.
```

---

## 58. Ejemplos consolidados

### Permitido

```text
focus
    ->
activities.api.ActivityLookup
```

Resultado:

```text
PASS
```

---

### Permitido

```text
reminders
    ->
activities.api.ActivityLookup
```

Resultado:

```text
PASS
```

---

### Permitido

```text
activities
    ->
identity.api.UserRegisteredV1
```

Resultado:

```text
PASS
```

---

### Prohibido

```text
FocusService
    ->
ActivityRepository
```

Resultado:

```text
V += 1
FAIL
```

---

### Dos violaciones

```text
FocusService
    ->
ActivityRepository

FocusService
    ->
ActivityEntity
```

Resultado:

```text
V = 2
FAIL
```

---

### Tres pares distintos

```text
FocusService -> ActivityRepository
FocusService -> ActivityEntity
PomodoroSessionService -> ActivityRepository
```

Resultado:

```text
V = 3
FAIL
```

---

### Prohibido en paquete futuro

```text
ReminderService
    ->
activities.analytics.ActivityAnalyticsService
```

Resultado:

```text
V >= 1
FAIL
```

---

### Fuera del alcance de V

```text
activities
    ->
shared
```

La relación no forma parte de `V`.

Debe evaluarse conforme a la política específica de `shared`.

---

## 59. Criterio exacto que hará fallar CI después de implementar la regla

Se establece:

\[
CI_{architecture}=FAIL
\]

cuando:

\[
V\ge1
\]

y la fitness function se encuentra incorporada a la suite ejecutada por:

```bash
./gradlew test --tests "com.example.rachapro.backend.architecture.ModularArchitectureTest" --no-daemon
```

La secuencia esperada será:

```text
Par de clases prohibido
        ↓
ArchUnit detecta la relación
        ↓
V >= 1
        ↓
Test FAILED
        ↓
Gradle exit code != 0
        ↓
GitHub Actions step FAILED
        ↓
Job "ArchUnit fitness functions" FAILED
```

La validación experimental de esta cadena permanece pendiente.

---

## 60. Criterio de cierre documental de la Actividad 3

La definición documental se considera completa porque ya se encuentran especificados:

- propiedad;
- capacidades;
- exclusión de `shared`;
- convención pública;
- regla;
- unidad exacta de conteo;
- variable;
- baseline esperado;
- umbral;
- criterio PASS;
- criterio FAIL;
- herramienta;
- relación con CI;
- ejemplos positivos;
- ejemplos negativos;
- limitaciones;
- riesgos;
- plan de validación.

La implementación no se considera completada hasta que exista evidencia ejecutable.

---

## 61. Plan de evidencia futura

Para certificar la fitness function deberán existir tres estados.

### Estado 1 — Conforme

```text
V = 0
BUILD SUCCESSFUL
```

Esto certificará el baseline real.

---

### Estado 2 — Mutación deliberada

```text
V >= 1
TEST FAILED
BUILD FAILED
```

Esto demostrará capacidad de detección.

---

### Estado 3 — Recuperación

Después de eliminar completamente la mutación:

```text
V = 0
BUILD SUCCESSFUL
```

Esto demostrará reversibilidad del ensayo.

---

## 62. Trazabilidad documental

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

### ADR-004

```text
docs/adr/0004-redecision-distribucion.md
```

### Costos relativos

```text
docs/semana12/costos-relativos-distribucion.md
```

---

## 63. Trazabilidad de implementación existente

### Suite arquitectónica

```text
backend/src/test/kotlin/com/example/rachapro/backend/architecture/ModularArchitectureTest.kt
```

### Workflow

```text
.github/workflows/backend-architecture.yml
```

### Comando actual

```bash
./gradlew test --tests "com.example.rachapro.backend.architecture.ModularArchitectureTest" --no-daemon
```

---

## 64. Trazabilidad Git previa

Documento de costos de Semana 12:

```text
99ed270
```

ADR-004:

```text
ca61951
```

La definición de esta fitness function será registrada mediante un commit posterior.

El hash exacto no se anticipa.

---

## 65. Estado de la Actividad 3

```text
SEMANA 12
ACTIVIDAD 3
DEFINIR FITNESS FUNCTION
```

### Propiedad

```text
DEFINIDA
```

### Alcance

```text
DEFINIDO
```

### Capacidades evaluadas

```text
activities
identity
focus
progress
reminders
```

### Shared

```text
EXCLUIDO EXPLÍCITAMENTE DE V
```

### Convención pública

```text
<modulo>.api.. = superficie pública
```

### Métrica

```text
V = número de pares únicos de clases origen-destino
    que representan una dependencia intermodular prohibida
```

### Unidad

```text
1 unidad de V
=
1 par único
(clase_origen, clase_destino)
prohibido
```

### Baseline

```text
V = 0 esperado
PENDIENTE DE CERTIFICACIÓN MEDIANTE ARCHUNIT
```

### Umbral

```text
V = 0
```

### Condición de éxito

```text
V = 0
```

### Condición de fallo

```text
V >= 1
```

### Instrumento

```text
ArchUnit
```

### Suite prevista

```text
ModularArchitectureTest
```

### Relación con CI

```text
DEFINIDA
```

### Qué romperá el CI

```text
AL MENOS UN PAR ÚNICO DE CLASES
CON DEPENDENCIA ENTRE CAPACIDADES DIFERENTES
CUYO DESTINO NO PERTENEZCA A .api..
```

### Implementación de la nueva regla

```text
PENDIENTE
```

### Baseline certificado

```text
PENDIENTE
```

### Ensayo controlado PASS -> FAIL -> PASS

```text
PENDIENTE
```

### Validación en GitHub Actions

```text
PENDIENTE
```

### Estado documental

```text
DEFINICIÓN COMPLETA PARA SEMANA 12
```