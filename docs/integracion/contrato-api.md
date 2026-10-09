# Contrato de API intermodular Activities → Focus

<!-- M5:ACTIVITYLOOKUP-CONSUMERS:BEGIN -->

## Consumidores actuales observados

En el estado actual observado del backend, `ActivityLookup` es consumido por al
menos:

- Focus;
- Reminders.

La semántica contractual documentada en este archivo corresponde a la capacidad
pública expuesta por Activities.

Que ambos consuman el contrato no implica que tengan el mismo flujo funcional
ni decide automáticamente otro mecanismo de integración.

<!-- M5:ACTIVITYLOOKUP-CONSUMERS:END -->


## 1. Propósito y alcance

Este documento formaliza el contrato público intermodular mediante el cual el
bounded context Activities ofrece a Focus una capacidad de validación de
referencias a Activity.

En este documento, el término API significa:

```text
API pública intermodular
```

y no implica necesariamente:

```text
API HTTP
API REST
endpoint externo
OpenAPI
```

El contrato analizado corresponde a una llamada local síncrona dentro del
monolito modular de RachaPro.

La interacción formalizada es:

```text
Focus → Activities
```

donde:

```text
Activities
→ proveedor de la capacidad

Focus
→ consumidor de la capacidad
```

El objetivo del contrato es permitir que Focus determine si una Activity
identificada puede ser referenciada por un usuario durante la creación de una
`PomodoroSession`.

Este documento no introduce nuevas capacidades de negocio.

---

## 2. Contexto proveedor y consumidor

### 2.1 Proveedor

```text
Activities
```

Activities posee la responsabilidad sobre la información y las reglas utilizadas
para determinar si una referencia a Activity es válida.

Activities conserva el ownership de:

- Activity.
- persistencia de Activity.
- reglas de acceso asociadas a Activity.
- validación de existencia.
- validación de pertenencia al usuario.
- validación de no eliminación.

### 2.2 Consumidor

```text
Focus
```

Focus consume la capacidad ofrecida por Activities cuando necesita crear una
`PomodoroSession` asociada a una Activity.

Focus no adquiere ownership sobre Activity ni sobre su persistencia.

---

## 3. Capacidad pública

La capacidad pública expuesta es:

```text
validar si una Activity identificada puede ser referenciada
por el usuario durante la creación de una PomodoroSession
```

Esta capacidad se utiliza únicamente cuando Focus ya dispone de un
`activityId`.

La opcionalidad de la asociación pertenece al flujo de creación de
`PomodoroSession`.

Por tanto:

```text
PomodoroSession.activityId
→ puede ser nulo

ActivityLookup.activityId
→ no es nulo cuando el contrato es invocado
```

Si la `PomodoroSession` no contiene `activityId`, Focus no invoca este contrato.

---

## 4. Operación contractual

El contrato actualmente utilizado es:

```kotlin
interface ActivityLookup {
    fun existsActiveActivityForUser(
        activityId: Long,
        userId: Long
    ): Boolean
}
```

La operación expuesta es:

```text
existsActiveActivityForUser
```

Su semántica contractual se limita a comprobar que la Activity:

```text
1. existe
2. pertenece al usuario indicado
3. no está eliminada
```

El término `Active` presente en el nombre del método no debe interpretarse como
un estado funcional adicional.

En particular, este contrato no establece por sí mismo que una Activity deba
estar en estado:

```text
PENDING
```

ni demuestra que deban excluirse estados como:

```text
COMPLETED
OVERDUE
```

La semántica documentada para este contrato es únicamente:

```text
existe
+
pertenece al usuario
+
no está eliminada
```

---

## 5. Entradas y precondiciones

### 5.1 Datos de entrada

El contrato recibe:

```text
activityId: Long
userId: Long
```

### 5.2 `activityId`

`activityId` identifica la Activity cuya referencia desea validar Focus.

Precondición:

```text
activityId debe estar presente cuando se invoca ActivityLookup
```

La ausencia de `activityId` se resuelve antes de la invocación.

Por tanto:

```text
activityId == null
→ Focus no invoca ActivityLookup

activityId != null
→ Focus puede invocar ActivityLookup
```

### 5.3 `userId`

`userId` identifica al usuario para el cual se verifica la relación con la
Activity.

Precondición:

```text
userId debe estar presente cuando se invoca el contrato
```

### 5.4 Información mínima

La información que cruza la frontera para ejecutar la validación es:

```text
activityId
userId
```

No se requiere transferir una entidad Activity completa.

---

## 6. Salida y semántica funcional

El contrato devuelve:

```text
Boolean
```

La semántica de la respuesta es la siguiente.

### 6.1 Resultado `true`

```text
true
```

significa que la validación pudo ejecutarse correctamente y que la Activity:

```text
existe
+
pertenece al usuario
+
no está eliminada
```

Por tanto, Focus puede considerar válida la referencia desde el punto de vista
de esta capacidad contractual.

### 6.2 Resultado `false`

```text
false
```

significa que la validación pudo ejecutarse correctamente, pero al menos una de
las condiciones funcionales no se cumple.

Los siguientes casos se representan contractualmente mediante `false`:

```text
Activity inexistente
Activity perteneciente a otro usuario
Activity eliminada
```

El contrato no diferencia públicamente cuál de esas condiciones produjo el
resultado negativo.

### 6.3 Regla de interpretación

Debe preservarse la siguiente distinción:

```text
false
=
la validación pudo ejecutarse
y produjo un resultado funcional negativo
```

Esto es diferente de un fallo técnico.

---

## 7. Semántica de errores

### 7.1 Fallo técnico

Un fallo técnico ocurre cuando Activities no puede completar la validación de
forma confiable.

En ese caso:

```text
fallo técnico
≠
false
```

La llamada no debe transformar automáticamente el fallo técnico en un resultado
funcional negativo.

La semántica acordada es:

```text
fallo técnico
→ no fue posible producir un resultado Boolean confiable
→ la llamada termina con fallo
→ Focus no considera exitosa la operación que dependía de la validación
```

### 7.2 Diferencia entre resultado funcional y fallo

La separación contractual es:

```text
true
→ referencia válida

false
→ referencia funcionalmente inválida

fallo técnico
→ no fue posible determinar la validez de forma confiable
```

### 7.3 Representación técnica del fallo

Este contrato define la semántica que debe observar Focus cuando ocurre un fallo
técnico, pero no fija una clase concreta de excepción pública.

Por tanto:

```text
semántica del fallo
→ definida

representación técnica concreta
→ no fijada
```

La implementación puede propagar el fallo mediante un mecanismo compatible con
la arquitectura actual siempre que no convierta silenciosamente el fallo en
`false`.

### 7.4 Detalles internos del fallo

Los detalles internos no forman parte del contrato público.

No deben exponerse como parte de la API intermodular:

- excepciones específicas de PostgreSQL.
- consultas SQL.
- stack traces internos.
- clases internas de persistencia.
- nombres de tablas.
- detalles de `ActivityRepository`.
- detalles de configuración de la base de datos.
- errores internos que no sean necesarios para interpretar el contrato.

Debe preservarse siempre la diferencia entre:

```text
resultado funcional negativo
```

y:

```text
fallo técnico de la validación
```

---

## 8. Responsabilidades de Activities

Activities es responsable de:

- ofrecer la capacidad pública de validación.
- interpretar `activityId` y `userId`.
- determinar si la Activity existe.
- determinar si la Activity pertenece al usuario.
- determinar si la Activity no está eliminada.
- devolver `true` cuando se cumplen las tres condiciones.
- devolver `false` cuando la validación se completa pero alguna condición no se
  cumple.
- no transformar un fallo técnico en `false`.
- ocultar los detalles internos de persistencia.
- mantener el ownership de Activity y sus reglas.
- preservar la semántica pública del contrato mientras este permanezca vigente.

Activities no transfiere ownership de Activity a Focus.

---

## 9. Responsabilidades de Focus

Focus es responsable de:

- decidir si debe invocar el contrato.
- no invocar `ActivityLookup` cuando no existe `activityId`.
- proporcionar un `activityId` no nulo cuando realiza la invocación.
- proporcionar el `userId` requerido por la validación.
- interpretar `true` como una referencia válida según la semántica documentada.
- interpretar `false` como una referencia funcionalmente inválida.
- no intentar deducir a partir de `false` cuál condición concreta falló.
- tratar un fallo técnico como una validación no completada.
- no considerar exitosa la creación asociada cuando la validación no pudo
  completarse.
- no acceder directamente a la persistencia interna de Activities.

Focus no debe reproducir por su cuenta la lógica interna de Activities para
evitar utilizar el contrato público.

---

## 10. Información que no cruza la frontera

La interacción debe limitarse a la información necesaria para la capacidad
expuesta.

La siguiente información no debe formar parte del contrato público:

- `ActivityRepository`.
- entidades JPA de Activities.
- consultas de persistencia.
- estructura interna de almacenamiento.
- consultas SQL.
- nombres de tablas.
- detalles específicos de PostgreSQL.
- stack traces internos.
- excepciones específicas de persistencia.
- configuración interna de Activities.
- detalles internos de implementación.
- una entidad Activity completa cuando solo se requieren sus identificadores.
- mecanismos internos utilizados para determinar el resultado.

La frontera contractual debe mantenerse en términos del dominio y no de la
tecnología de persistencia.

---

## 11. Mecanismo de interacción

El mecanismo formalizado para este contrato es:

```text
SÍNCRONO
```

La invocación se realiza localmente dentro del mismo proceso del monolito
modular.

Por tanto:

```text
Focus
→ invoca ActivityLookup
→ Activities ejecuta la validación
→ Focus recibe el resultado o el fallo
```

Este documento describe el contrato vigente.

La elección del mecanismo síncrono para este contrato no debe interpretarse como
una afirmación de que una alternativa asíncrona sea incorrecta en todos los
escenarios futuros.

Cualquier cambio posterior del mecanismo deberá preservar o redefinir
explícitamente la semántica contractual.

---

## 12. Compatibilidad

### 12.1 Principio general

Un cambio es compatible cuando puede introducirse sin obligar a Focus a cambiar
la interpretación vigente del contrato.

Los cambios internos de Activities pueden realizarse sin afectar al consumidor
si preservan:

```text
entradas
+
semántica de salida
+
semántica de fallo
+
responsabilidades
+
límites de información
```

### 12.2 Cambios compatibles

Se consideran compatibles, mientras no alteren el comportamiento observable del
contrato:

- cambiar la implementación interna.
- cambiar el repository utilizado.
- modificar consultas de persistencia.
- reorganizar código interno.
- cambiar la estructura interna de almacenamiento.
- agregar logging.
- agregar métricas.
- agregar tracing.
- optimizar la implementación.
- refactorizar Activities sin modificar la semántica contractual.
- mejorar documentación sin alterar comportamiento.

### 12.3 Cambios incompatibles

Se considera incompatible un cambio que obligue a Focus a modificar la forma en
que consume o interpreta el contrato.

Entre ellos:

- cambiar el significado de `true`.
- cambiar el significado de `false`.
- cambiar el tipo contractual de `activityId`.
- cambiar el tipo contractual de `userId`.
- eliminar una entrada requerida.
- agregar una nueva entrada obligatoria que Focus deba proporcionar.
- cambiar la capacidad funcional expuesta.
- dejar de comprobar alguna de las tres condiciones documentadas.
- convertir un caso actualmente representado mediante `false` en un resultado
  cuya interpretación exija modificar Focus.
- cambiar la semántica del fallo técnico de forma incompatible.
- exigir a Focus conocer detalles internos de Activities.

---

## 13. Política de evolución y versionamiento

### 13.1 Versionamiento actual

Actualmente:

```text
no se introduce un mecanismo explícito de versionamiento
```

No se definen por ahora esquemas como:

```text
v1
v2
/api/v1
```

porque este contrato corresponde a una API pública intermodular local dentro del
mismo monolito modular.

Esto representa una decisión del equipo para el estado actual y no una
afirmación de que el versionamiento sea innecesario en cualquier escenario
futuro.

Esto tampoco significa que el contrato no pueda evolucionar.

### 13.2 Evolución compatible

Los cambios compatibles pueden introducirse manteniendo la misma superficie
contractual cuando no cambian la interpretación que realiza Focus.

### 13.3 Evolución incompatible

Cuando sea necesario realizar un cambio incompatible, la política es:

```text
Activities cambia el contrato
+
Focus adapta su consumo
+
se actualizan las pruebas afectadas
+
se verifican las reglas arquitectónicas
+
proveedor y consumidor se despliegan dentro de la misma versión del sistema
```

Un cambio incompatible debe identificarse explícitamente antes de integrarse.

No debe producirse silenciosamente una situación en la que:

```text
Activities exponga una semántica nueva
```

mientras:

```text
Focus continúe interpretando la semántica anterior
```

### 13.4 Versiones paralelas

Mientras Activities y Focus:

```text
formen parte del mismo monolito modular
+
se desplieguen conjuntamente
```

no se establece como requisito mantener versiones paralelas del contrato.

Si en el futuro aparecen:

- consumidores adicionales con ciclos de evolución diferentes.
- despliegues independientes.
- necesidad de compatibilidad simultánea entre versiones.
- exposición externa del contrato.
- separación del proceso actual.

la necesidad de un mecanismo explícito de versionamiento deberá reevaluarse.

---

## 14. Ambigüedades y decisiones pendientes

Con la especificación actual no se identifican ambigüedades bloqueantes para
formalizar el contrato vigente.

Quedan fuera del alcance de este documento decisiones futuras como:

- una posible migración a interacción asíncrona.
- un mecanismo explícito de versionamiento.
- una eventual exposición mediante HTTP o REST.
- una especificación OpenAPI.
- una separación futura de Activities y Focus en procesos independientes.
- una posible ampliación de la semántica de salida.
- una representación técnica específica para los fallos del contrato.

Estas posibilidades no forman parte del contrato vigente y no deben
interpretarse como requisitos pendientes de implementación.

### 14.1 Decisión vigente sobre la salida

La salida se mantiene como:

```text
Boolean
```

No se introduce actualmente un resultado diferenciado como:

```text
VALID
NOT_FOUND
NOT_OWNED
DELETED
```

porque el consumidor no requiere esa diferenciación para la capacidad
formalizada.

### 14.2 Decisión vigente sobre errores

Los casos funcionalmente inválidos se mantienen representados mediante:

```text
false
```

Los fallos técnicos se mantienen fuera del resultado Boolean.

La semántica de esos fallos está definida, pero este documento no establece una
clase concreta de excepción pública.

### 14.3 Decisión vigente sobre `Active`

El término `Active` del nombre actual no amplía la semántica contractual.

La interpretación formal continúa siendo:

```text
existe
+
pertenece al usuario
+
no está eliminada
```

---

## 15. Trazabilidad hacia ADR-002 y Context Map

### 15.1 Relación con el Context Map

El Context Map establece la relación:

```text
Focus → Activities
```

bajo la convención:

```text
A → B
=
A consume o referencia información de B
```

Este contrato materializa una capacidad necesaria para esa relación.

Focus consume una capacidad de Activities sin adquirir ownership sobre sus
datos.

### 15.2 Relación con ADR-002

ADR-002 establece como reglas relevantes:

- cada módulo conserva ownership sobre sus entidades y repositories.
- un módulo externo no debe importar repositories de otro módulo.
- un módulo externo no debe depender de entidades JPA internas de otro módulo.
- los cruces deben realizarse mediante contratos públicos.
- los contratos iniciales pueden implementarse mediante llamadas locales
  síncronas.
- la implementación interna del proveedor puede cambiar sin transferir esa
  responsabilidad al consumidor.

`ActivityLookup` es el contrato público utilizado para preservar esta frontera.

### 15.3 Relación con la comparación síncrono vs. asíncrono

El análisis previo de integración comparó ambas alternativas sin declarar una
superioridad universal.

Para este documento se formaliza el mecanismo vigente:

```text
síncrono
```

Esta formalización describe el contrato actualmente adoptado para la relación
analizada.

No elimina la posibilidad de que una decisión arquitectónica futura cambie el
mecanismo si aparece nueva evidencia.

---

## 16. Resumen contractual

```text
Proveedor:
Activities

Consumidor:
Focus

Capacidad:
validar si una Activity identificada puede ser referenciada
por un usuario durante la creación de una PomodoroSession

Operación:
ActivityLookup.existsActiveActivityForUser

Entradas:
activityId: Long
userId: Long

Precondición:
activityId y userId están presentes al invocar el contrato

Opcionalidad:
si PomodoroSession no contiene activityId,
Focus no invoca ActivityLookup

Salida:
Boolean

true:
la Activity existe,
pertenece al usuario
y no está eliminada

false:
la validación se ejecutó correctamente,
pero al menos una condición funcional no se cumple

Fallo técnico:
no se representa mediante false
la llamada termina con fallo
Focus no considera exitosa la operación dependiente

Representación técnica del fallo:
no fijada actualmente

Información prohibida:
repositories
entidades JPA
consultas SQL
detalles de persistencia
detalles de PostgreSQL
stack traces
detalles internos de implementación

Mecanismo:
síncrono

Tipo de API:
API pública intermodular local
no API HTTP o REST

Versionamiento explícito:
no se introduce actualmente

Cambios incompatibles:
requieren evolución coordinada de Activities y Focus

Despliegue:
proveedor y consumidor evolucionan dentro de la misma versión
del monolito modular
```

---

## 17. Estado del contrato

El contrato queda formalizado respecto a:

```text
operación                         DEFINIDA
entradas                          DEFINIDAS
precondiciones                    DEFINIDAS
salida                            DEFINIDA
semántica funcional               DEFINIDA
semántica de errores              DEFINIDA
representación técnica del fallo  NO FIJADA
responsabilidades                 DEFINIDAS
límites                           DEFINIDOS
mecanismo                         DEFINIDO
compatibilidad                    DEFINIDA
evolución                         DEFINIDA
versionamiento                    DEFINIDO PARA EL ESTADO ACTUAL
```

El contrato no introduce capacidades adicionales de negocio ni expone detalles
internos de Activities.

La frontera formalizada mantiene a Activities como propietario de la capacidad
de validación y a Focus como consumidor de su resultado.
