# 03 - Atributos de calidad

## 1. Propósito

Este documento consolida los atributos de calidad relacionados con RachaPro a partir de dos fuentes diferenciadas:

- los requerimientos no funcionales definidos durante el proyecto histórico de Ingeniería de Software;
- las decisiones actuales tomadas por el equipo durante la etapa de Arquitectura de Software.

El objetivo es mantener la trazabilidad entre los antecedentes históricos y la priorización actual, sin presentar decisiones posteriores como si hubieran formado parte del proyecto original.

Los atributos identificados históricamente no se consideran automáticamente drivers arquitectónicos actuales. La selección y priorización vigente corresponde a decisiones explícitas del equipo.

## 2. Antecedentes históricos de atributos de calidad

La documentación histórica de RachaPro contiene requerimientos no funcionales asociados a diferentes atributos de calidad.

| Atributo de calidad | Antecedente histórico | Estado |
|---|---|---|
| Usabilidad | RNF01, RNF05 y RNF06 | ANTECEDENTE HISTÓRICO |
| Rendimiento | RNF02 | ANTECEDENTE HISTÓRICO |
| Seguridad | RNF03 | ANTECEDENTE HISTÓRICO |
| Disponibilidad | RNF04 | ANTECEDENTE HISTÓRICO |
| Precisión | RNF07, relacionado con el temporizador Pomodoro | ANTECEDENTE HISTÓRICO |

Estos atributos provienen de la documentación elaborada durante Ingeniería de Software y se conservan como antecedentes del proyecto.

Su existencia histórica no implica que todos ellos mantengan actualmente la misma prioridad ni que los criterios definidos originalmente continúen siendo umbrales vigentes.

## 3. Priorización actual del equipo

Durante la revisión documental realizada el 04 de septiembre de 2026, el equipo formalizó la siguiente priorización preliminar de atributos de calidad para la etapa actual de RachaPro.

| Prioridad | Atributo de calidad | Estado |
|---:|---|---|
| 1 | Rendimiento | PRIORIZADO POR EL EQUIPO |
| 2 | Seguridad | PRIORIZADO POR EL EQUIPO |
| 3 | Usabilidad | PRIORIZADO POR EL EQUIPO |
| 4 | Disponibilidad | PRIORIZADO POR EL EQUIPO |

Esta priorización corresponde a una decisión actual del equipo y no se presenta como evidencia de que el mismo orden hubiera sido establecido durante la Semana 2 original.

El atributo de precisión permanece documentado como antecedente histórico mediante RNF07, pero no fue seleccionado por el equipo dentro de los cuatro atributos priorizados actualmente.

Esto no implica que precisión haya sido descartada como atributo del sistema; únicamente indica que no forma parte de la priorización actual registrada en este documento.

## 4. Justificación de la priorización

| Prioridad | Atributo | Justificación del equipo |
|---:|---|---|
| 1 | Rendimiento | El equipo considera que la fluidez de la aplicación es fundamental para RachaPro. Si la carga de información genera tiempos de espera altos, la experiencia de uso se deteriora y la aplicación puede perder utilidad a largo plazo. |
| 2 | Seguridad | El equipo considera importante proteger la información asociada a los usuarios, principalmente el correo, el nombre de usuario y la información que podría permitir observar sus rutinas de uso. Dentro del alcance actual, el equipo no considera que se maneje información de alto impacto, por lo que seguridad se ubica después de rendimiento. |
| 3 | Usabilidad | RachaPro está orientada principalmente a estudiantes, por lo que el equipo considera importante que la aplicación sea simple, comprensible y fácil de utilizar para su público objetivo. |
| 4 | Disponibilidad | El equipo considera necesario que la infraestructura pueda recibir y atender las solicitudes de los usuarios. Sin embargo, dentro de la priorización actual se ubica después de rendimiento, seguridad y usabilidad, porque el valor principal de RachaPro depende especialmente de la rapidez de respuesta, la protección de la información y la facilidad de uso. |

Las posiciones anteriores expresan el criterio actual del equipo y no corresponden a una ponderación numérica derivada automáticamente de los requerimientos históricos.

## 5. Relación con stakeholders y preocupaciones

El stakeholder principal respaldado por la investigación histórica es el estudiante universitario.

| Stakeholder | Preocupación respaldada | Atributo relacionado | Estado |
|---|---|---|---|
| Estudiante universitario | Utilizar la aplicación con tiempos de respuesta adecuados. | Rendimiento | RELACIÓN ACTUAL CON ANTECEDENTE HISTÓRICO |
| Estudiante universitario | Proteger la privacidad de la información gestionada por el sistema. | Seguridad | RELACIÓN ACTUAL CON ANTECEDENTE HISTÓRICO |
| Estudiante universitario | Utilizar una herramienta clara, simple y fácil de comprender. | Usabilidad | RELACIÓN ACTUAL CON ANTECEDENTE HISTÓRICO |
| Estudiante universitario | Poder acceder a las funcionalidades necesarias para organizar sus actividades y sesiones de estudio. | Disponibilidad | RELACIÓN ACTUAL CON ANTECEDENTE HISTÓRICO |

El desarrollador / mantenedor también forma parte del contexto actual del proyecto debido a su responsabilidad sobre implementación, pruebas y mantenimiento.

Sin embargo, no se cuenta con un instrumento histórico equivalente a la encuesta o focus group que permita afirmar una priorización formal de atributos desde la perspectiva de este stakeholder.

Por esta razón, cualquier preocupación específica atribuida al desarrollador / mantenedor que no provenga de una decisión explícita del equipo debe mantenerse como **INFERENCIA**.

No se incorpora actualmente un “usuario general” como stakeholder independiente, ya que la evidencia histórica se concentra principalmente en estudiantes universitarios y el equipo no ha formalizado una ampliación del público objetivo.

## 6. Relación con requerimientos no funcionales históricos

Los atributos priorizados actualmente tienen antecedentes en los requerimientos no funcionales del proyecto histórico.

| Atributo actual | Antecedente histórico | Relación | Estado |
|---|---|---|---|
| Rendimiento | RNF02 | Contiene criterios históricos relacionados con el rendimiento del módulo de actividades. | ANTECEDENTE HISTÓRICO |
| Seguridad | RNF03 | Contiene el antecedente histórico asociado a seguridad de la información. | ANTECEDENTE HISTÓRICO |
| Usabilidad | RNF01, RNF05 y RNF06 | Contienen antecedentes relacionados con facilidad de uso del sistema, gestión de actividades y Pomodoro. | ANTECEDENTE HISTÓRICO |
| Disponibilidad | RNF04 | Contiene el antecedente histórico relacionado con disponibilidad del sistema. | ANTECEDENTE HISTÓRICO |
| Precisión | RNF07 | Contiene el antecedente histórico relacionado con la precisión del temporizador Pomodoro. | ANTECEDENTE HISTÓRICO NO PRIORIZADO ACTUALMENTE |

En particular, RNF02 incluía como criterios históricos que el listado principal de actividades no superara los 3 segundos de carga en condiciones normales y que el sistema mantuviera un comportamiento estable con al menos 100 actividades registradas.

Estos valores permanecen documentados como **ANTECEDENTES HISTÓRICOS**.

No se consideran automáticamente métricas o umbrales vigentes para la etapa actual de Arquitectura de Software mientras el equipo no adopte explícitamente dichos criterios dentro de un escenario de calidad actual.

## 7. Mapa atributo → decisión

El siguiente mapa relaciona atributos de calidad priorizados con decisiones actuales del proyecto únicamente cuando el equipo ha confirmado que dicho atributo influyó en la decisión.

| Atributo | Decisión relacionada | Motivación confirmada por el equipo | Evidencia / estado                                                                                                                                                                                                  |
|---|---|---|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Rendimiento | Evolucionar desde una solución principalmente local hacia una arquitectura con backend Spring Boot y persistencia central en PostgreSQL. | El equipo confirma que mejorar la capacidad de manejar mayores volúmenes de información y usuarios, así como mantener tiempos de respuesta adecuados, influyó en esta decisión. | DECISIÓN ACTUAL CONFIRMADA POR EL EQUIPO. Las pruebas de rendimiento realizadas posteriormente sirven como evidencia experimental, pero no constituyen por sí mismas la decisión arquitectónica.                    |
| Seguridad | Incorporar autenticación en el backend mediante JWT y propagar el token desde Android mediante `AuthInterceptor`. | El equipo confirma que la protección del acceso y de la información del usuario influyó en esta decisión. | DECISIÓN ACTUAL CONFIRMADA POR EL EQUIPO. La implementación puede verificarse en la configuración JWT del backend y en `AuthInterceptor` del cliente Android.                                                       |
| Usabilidad | Organizar la aplicación Android mediante pantallas y flujos específicos para actividades, Pomodoro, progreso y otras funciones principales. | El equipo confirma que facilitar la comprensión y el uso de las funciones por parte de los estudiantes influyó en la organización de estos flujos. | DECISIÓN ACTUAL CONFIRMADA POR EL EQUIPO. La implementación de las pantallas constituye evidencia del estado actual y su efectividad se contrastará mediante el escenario de usabilidad definido en la sección 9.3. |
| Disponibilidad | Incorporar mecanismos de comprobación del estado de los servicios mediante `healthcheck` de PostgreSQL y `/actuator/health` en el backend. | El equipo confirma que poder detectar si los servicios necesarios se encuentran operativos influyó en esta decisión. | DECISIÓN ACTUAL CONFIRMADA POR EL EQUIPO. Estos mecanismos permiten comprobar estado, pero no garantizan por sí solos disponibilidad continua.                                                                      |

## 8. Escenarios de calidad asociados

Los siguientes escenarios se construyen a partir de antecedentes históricos y de preocupaciones expresadas actualmente por el equipo.

Los escenarios siguientes distinguen entre antecedentes históricos y criterios actuales definidos por el equipo. Cuando un criterio proviene de una decisión actual, se identifica explícitamente para no confundirlo con los valores históricos del proyecto.

### 8.1 Rendimiento - escenario derivado del antecedente histórico RNF02

Este escenario corresponde a la reformulación verificable del antecedente histórico RNF02 y se conserva para mantener su trazabilidad.

No debe confundirse con el escenario actual de rendimiento definido posteriormente por el equipo en la sección 9.1, cuya operación evaluada es la creación de una actividad y cuyo umbral actual es de 1 minuto.

| Elemento | Descripción |
|---|---|
| Atributo | Rendimiento |
| Fuente del estímulo | Usuario de RachaPro |
| Estímulo | Solicita la carga del listado de actividades. |
| Entorno | Uso normal de la aplicación con actividades previamente registradas. |
| Artefacto | Módulo de actividades |
| Respuesta esperada | El sistema recupera y presenta la información solicitada. |
| Medida histórica de respuesta | RNF02 establecía históricamente una carga de hasta 3 segundos y comportamiento estable con al menos 100 actividades. |
| Estado | ESCENARIO CON ANTECEDENTE HISTÓRICO. Los valores de 3 segundos y 100 actividades no se adoptan automáticamente como umbrales actuales. |

### 8.2 Seguridad

| Elemento | Descripción |
|---|---|
| Atributo | Seguridad |
| Fuente del estímulo | Persona sin credenciales válidas o sin una cuenta autenticada. |
| Estímulo | Intenta acceder a información privada correspondiente a otros usuarios. |
| Entorno | Sistema en funcionamiento normal. |
| Artefacto | Mecanismos de autenticación y acceso a información de usuarios. |
| Respuesta esperada | El sistema impide el acceso no autorizado a la información perteneciente a otros usuarios. |
| Información considerada por el equipo | Correo, nombre, semestre y datos que permitan observar la rutina del usuario. Las credenciales de autenticación tampoco deben quedar expuestas. |
| Medida de respuesta | El intento de acceso es rechazado y no se entrega información privada perteneciente a otro usuario. |
| Estado | ESCENARIO ACTUAL CON CRITERIO DE VERIFICACIÓN DEFINIDO POR EL EQUIPO. |

### 8.3 Usabilidad

| Elemento | Descripción |
|---|---|
| Atributo | Usabilidad |
| Fuente del estímulo | Estudiante que utiliza RachaPro. |
| Estímulo | Intenta crear una nueva actividad. |
| Entorno | Uso normal de la aplicación. |
| Artefacto | Flujo de creación de actividades. |
| Respuesta esperada | El estudiante puede completar la creación de una actividad incluyendo la información requerida por el flujo, como tiempo y fecha límite, además de los demás datos asociados a la actividad. |
| Medida de respuesta | El estudiante completa correctamente el flujo desde Home hasta crear la actividad en un máximo de 5 minutos. |
| Estado | ESCENARIO ACTUAL CON CRITERIO DE VERIFICACIÓN DEFINIDO POR EL EQUIPO. |

### 8.4 Disponibilidad

| Elemento | Descripción |
|---|---|
| Atributo | Disponibilidad |
| Fuente del estímulo | Indisponibilidad del backend. |
| Estímulo | La aplicación Android intenta utilizar una funcionalidad que requiere comunicación con el backend mientras este no se encuentra disponible. |
| Entorno | Aplicación Android en ejecución. |
| Artefacto | Comunicación Android-backend. |
| Respuesta esperada | La aplicación detecta que el backend no se encuentra disponible. |
| Medida de respuesta | La aplicación detecta la indisponibilidad del backend e informa al usuario en un máximo de 5 minutos. |
| Estado | ESCENARIO ACTUAL CON CRITERIO DE VERIFICACIÓN DEFINIDO POR EL EQUIPO. |

## 9. Registro de escenarios sugeridos por IA y correcciones del equipo

Como parte de la revisión documental realizada el 05 de septiembre de 2026 para completar la evidencia asociada a Semana 3, se revisaron formulaciones generales propuestas por IA y el equipo las transformó en escenarios observables y medibles.

Este registro corresponde a la revisión documental actual y no se presenta como evidencia de que estas formulaciones hubieran sido producidas durante la Semana 3 original.

### 9.1 Escenario reformulado de rendimiento

La formulación inicial:

> “La aplicación debe responder rápidamente.”

no permite realizar una verificación objetiva porque no define una operación concreta ni un límite de tiempo.

El equipo decidió utilizar como operación de referencia la creación de una actividad.

| Elemento | Definición actual |
|---|---|
| Atributo | Rendimiento |
| Operación evaluada | Creación de una actividad |
| Inicio de medición | Momento en que el usuario confirma la creación de la actividad |
| Fin de medición | Momento en que la actividad queda creada correctamente y el sistema informa o refleja el resultado |
| Umbral actual | Máximo 1 minuto |
| Resultado satisfactorio | La actividad queda creada antes de transcurrir 1 minuto |
| Resultado no satisfactorio | La operación supera 1 minuto o no finaliza correctamente |
| Respuesta esperada ante fallo | La aplicación debe informar al usuario mediante un mensaje de error en lugar de permanecer indefinidamente esperando |

**Criterio de verificación:**

`CUMPLE` si la creación finaliza correctamente en un tiempo menor o igual a 1 minuto.

`NO CUMPLE` si supera 1 minuto, la operación queda bloqueada o no se informa adecuadamente el fallo.

El límite de 1 minuto corresponde a una **decisión actual del equipo** y no reemplaza retrospectivamente los valores históricos de RNF02.

---

### 9.2 Escenario reformulado de seguridad

La formulación inicial:

> “La aplicación debe proteger los datos del usuario.”

se reformula mediante un intento concreto de acceso no autorizado.

| Elemento | Definición actual |
|---|---|
| Atributo | Seguridad |
| Actor de prueba | Persona sin una cuenta autenticada o sin credenciales válidas |
| Acción | Intentar acceder a información perteneciente a otro usuario |
| Información protegida | Correo, nombre, semestre, información asociada a la rutina del usuario y credenciales de autenticación. |
| Resultado satisfactorio | El sistema rechaza el acceso y no entrega información privada |
| Respuesta esperada | La aplicación o API informa que el acceso no está autorizado |
| Resultado no satisfactorio | Se obtiene total o parcialmente información privada sin autorización |

**Criterio de verificación:**

`CUMPLE` si el intento es rechazado y no se obtiene información privada.

`NO CUMPLE` si una persona no autenticada obtiene información perteneciente a otro usuario.

El mensaje de error sirve como evidencia visible del rechazo, pero el criterio principal es que **los datos no sean entregados**.

---

### 9.3 Escenario reformulado de usabilidad

La formulación inicial:

> “La aplicación debe ser fácil de utilizar por los estudiantes.”

no permite una verificación objetiva porque no define una tarea concreta, un punto de inicio, un punto de finalización ni una medida observable.

| Elemento | Definición actual |
|---|---|
| Atributo | Usabilidad |
| Usuario de prueba | Estudiante |
| Tarea | Crear una actividad completa |
| Punto inicial | Usuario ubicado en la pantalla Home |
| Acciones esperadas | Acceder a creación de actividad, completar la información requerida, seleccionar tiempo y fecha límite y confirmar la creación |
| Punto final | La actividad aparece creada correctamente |
| Medición principal | Tiempo total desde Home hasta finalizar la creación |
| Medición complementaria | Si la tarea pudo completarse correctamente |
| Observaciones adicionales | Errores de entrada, retrocesos, cancelaciones o mensajes de error encontrados durante el flujo |

La medición puede registrarse de la siguiente forma:

| Prueba | Tiempo Home → actividad creada | Completó la tarea | Errores / dificultades observadas |
|---|---:|---|---|
| U-01 | Pendiente | Sí / No | Pendiente |
| U-02 | Pendiente | Sí / No | Pendiente |
| U-03 | Pendiente | Sí / No | Pendiente |

El tiempo permite comparar la facilidad con que diferentes usuarios completan el mismo flujo.

Sin embargo, **un tiempo bajo por sí solo no demuestra usabilidad**. También debe verificarse que el usuario haya podido terminar la tarea correctamente y registrar las dificultades observadas.

**Umbral de tiempo aceptable:** máximo 5 minutos desde que el usuario se encuentra en la pantalla Home hasta que la actividad queda creada correctamente.

`CUMPLE` si el estudiante completa correctamente la creación de la actividad en un tiempo menor o igual a 5 minutos.

`NO CUMPLE` si supera los 5 minutos, abandona el flujo o no logra completar correctamente la creación de la actividad.

Este umbral corresponde a una **decisión actual del equipo** tomada durante la revisión documental del 05 de septiembre de 2026.

---

### 9.4 Escenario reformulado de disponibilidad

La formulación inicial:

> “La aplicación debe estar disponible.”

se reformula provocando una indisponibilidad controlada del backend.

| Elemento | Definición actual |
|---|---|
| Atributo | Disponibilidad |
| Condición inicial | Aplicación Android en ejecución |
| Falla provocada | Backend detenido o inaccesible |
| Acción | Utilizar una funcionalidad que requiera comunicación con el backend |
| Resultado satisfactorio | La aplicación detecta que no puede comunicarse con el backend |
| Respuesta esperada | Se muestra un mensaje informando al usuario que la operación no puede realizarse en ese momento |
| Resultado no satisfactorio | La aplicación queda bloqueada, falla sin explicación o presenta información incorrecta como si la operación hubiera sido exitosa |

**Criterio de verificación:**

`CUMPLE` si la aplicación detecta la indisponibilidad del backend y muestra un mensaje de error al usuario en un tiempo menor o igual a 5 minutos.

`NO CUMPLE` si después de 5 minutos la aplicación no ha detectado la indisponibilidad, queda bloqueada, se cierra inesperadamente o presenta como exitosa una operación que no pudo completarse.

El umbral de 5 minutos corresponde a una **decisión actual del equipo** tomada durante la revisión documental del 05 de septiembre de 2026.

---

### 9.5 Resultado de la reformulación

Los escenarios generales fueron transformados en operaciones concretas que pueden ejecutarse y observarse.

| Atributo | Qué se medirá o comprobará | Estado |
|---|---|---|
| Rendimiento | Tiempo necesario para crear una actividad. Umbral actual: 1 minuto. | MEDIBLE |
| Seguridad | Rechazo de acceso no autorizado y ausencia de exposición de información privada. | MEDIBLE |
| Usabilidad | Tiempo desde Home hasta completar la creación de una actividad, éxito de la tarea y dificultades observadas. Umbral actual: máximo 5 minutos. | MEDIBLE |
| Disponibilidad | Tiempo necesario para detectar que el backend está inaccesible y comunicar el fallo al usuario. Umbral actual: máximo 5 minutos. | MEDIBLE |

## 9.6 Matriz de trazabilidad de atributos

La siguiente matriz resume la relación entre los antecedentes históricos, la priorización actual, las decisiones arquitectónicas y los escenarios definidos.

| Atributo | RNF histórico | Prioridad actual | Decisión relacionada | Escenario actual | Criterio actual | Evidencia relacionada |
|---|---|---:|---|---|---|---|
| Rendimiento | RNF02 | 1 | Evolución hacia backend Spring Boot y persistencia central PostgreSQL, influida por la necesidad de manejar mayores volúmenes de información y usuarios. | Creación de una actividad. | La operación debe finalizar correctamente en máximo 1 minuto. | EXP-001 constituye evidencia experimental previa sobre otro escenario de rendimiento relacionado con carga de actividades; no valida retrospectivamente el nuevo criterio. |
| Seguridad | RNF03 | 2 | Autenticación mediante JWT en backend y propagación del token mediante `AuthInterceptor` en Android. | Intento de acceso no autorizado a información privada. | El acceso debe ser rechazado y la información privada no debe ser entregada. | Implementación actual de autenticación JWT y `AuthInterceptor`. |
| Usabilidad | RNF01, RNF05 y RNF06 | 3 | Organización de pantallas y flujos específicos para las principales funcionalidades de RachaPro. | Crear una actividad desde Home. | El estudiante debe completar correctamente la tarea en máximo 5 minutos. | Flujo actual de creación de actividades; el resultado de una prueba de usabilidad se registrará como evidencia separada cuando se ejecute. |
| Disponibilidad | RNF04 | 4 | Uso de `healthcheck` de PostgreSQL y `/actuator/health` del backend para comprobar el estado de servicios. | Uso de una funcionalidad mientras el backend está inaccesible. | La aplicación debe detectar la indisponibilidad e informar al usuario en máximo 5 minutos. | `healthcheck` de PostgreSQL y endpoint `/actuator/health`; la reacción específica de Android corresponde al escenario definido. |

## 10. Evidencias y fuentes

La información consolidada en este documento se apoya en documentación histórica, decisiones actuales del equipo, implementación existente y evidencia experimental producida durante Arquitectura de Software.

| Fuente / evidencia | Información que respalda | Clasificación |
|---|---|---|
| `docs/sources/historical/RachaPro_ Gestor Inteligente de Productividad Académica.pdf` | RNF históricos relacionados con usabilidad, rendimiento, seguridad, disponibilidad y precisión. | FUENTE HISTÓRICA |
| `dossier/02-stakeholders-drivers.md` | Stakeholders actuales, preocupaciones respaldadas, restricciones y priorización actual de drivers arquitectónicos. | DOCUMENTACIÓN ACTUAL |
| `dossier/02-escenarios-de-calidad.md` | Antecedentes de escenarios de calidad y trabajo previo relacionado con rendimiento. | DOCUMENTACIÓN PREVIA |
| `docs/checkpoint-semana2.md` | Evidencia de funcionamiento comprobado de PostgreSQL, backend, aplicación Android, registro de usuarios y creación de actividades bajo las condiciones del checkpoint. | EVIDENCIA EJECUTABLE |
| `docs/ejecucion-local.md` | Procedimiento reproducible para ejecutar la infraestructura, backend y aplicación Android. | EVIDENCIA EJECUTABLE |
| `experimentos/EXP-001-linea-base/` | Evidencia experimental previa asociada al rendimiento del módulo de actividades. | EVIDENCIA EXPERIMENTAL |
| `app/src/main/java/com/example/rachapro/network/RetrofitClient.kt` | Comunicación entre Android y backend mediante Retrofit. | EVIDENCIA ACTUAL |
| `app/src/main/AndroidManifest.xml` | Configuración actual de conectividad de la aplicación Android. | EVIDENCIA ACTUAL |
| Configuración JWT del backend y `AuthInterceptor` del cliente Android | Implementación relacionada con autenticación y control de acceso. | EVIDENCIA ACTUAL |
| `infra/postgres/docker-compose.yml` | Configuración del servicio PostgreSQL y mecanismo de `healthcheck`. | EVIDENCIA ACTUAL |
| Endpoint `/actuator/health` del backend | Mecanismo actual para comprobar el estado del backend y sus componentes. | EVIDENCIA ACTUAL |
| Decisión del equipo, 04 de septiembre de 2026 | Priorización actual: Rendimiento, Seguridad, Usabilidad y Disponibilidad. | DECISIÓN ACTUAL DEL EQUIPO |
| Decisiones del equipo, 05 de septiembre de 2026 | Definición y reformulación de escenarios medibles y de los umbrales actuales de rendimiento, usabilidad y disponibilidad. | DECISIÓN ACTUAL DEL EQUIPO |

### Decisiones actuales registradas

Los siguientes criterios fueron definidos por el equipo durante la revisión documental actual y no corresponden a los valores históricos del proyecto:

| Atributo | Criterio actual definido |
|---|---|
| Rendimiento | La creación de una actividad debe finalizar correctamente en un máximo de 1 minuto. |
| Seguridad | Un usuario no autenticado o sin credenciales válidas no debe obtener información privada perteneciente a otros usuarios. |
| Usabilidad | Un estudiante debe poder completar el flujo desde Home hasta la creación correcta de una actividad en un máximo de 5 minutos. |
| Disponibilidad | La aplicación debe detectar la indisponibilidad del backend e informar al usuario en un máximo de 5 minutos. |

Estos criterios constituyen la base actual definida por el equipo para verificar posteriormente el comportamiento de RachaPro mediante pruebas reproducibles.

## 11. Estado de los escenarios y próximos pasos de verificación

Los cuatro atributos priorizados cuentan actualmente con escenarios concretos y criterios observables definidos por el equipo.

| Atributo | Escenario definido | Criterio actual | Estado |
|---|---|---|---|
| Rendimiento | Creación de una actividad. | La operación debe finalizar correctamente en un máximo de 1 minuto. | ESCENARIO DEFINIDO Y LISTO PARA MEDICIÓN |
| Seguridad | Intento de acceso a información privada sin autenticación válida. | El acceso debe ser rechazado y la información privada no debe ser entregada. | ESCENARIO DEFINIDO Y LISTO PARA VERIFICACIÓN |
| Usabilidad | Creación de una actividad desde la pantalla Home. | El estudiante debe completar correctamente el flujo en un máximo de 5 minutos. | ESCENARIO DEFINIDO Y LISTO PARA VERIFICACIÓN |
| Disponibilidad | Uso de una funcionalidad mientras el backend se encuentra inaccesible. | La aplicación debe detectar la indisponibilidad e informar al usuario en un máximo de 5 minutos. | ESCENARIO DEFINIDO Y LISTO PARA VERIFICACIÓN |

### Relación con la evidencia experimental

La definición y actualización de los escenarios de calidad debe mantenerse separada de los resultados experimentales obtenidos sobre ellos.

En el proyecto ya existe evidencia experimental previa asociada al rendimiento en:

`experimentos/EXP-001-linea-base/`

EXP-001 fue ejecutado antes de la consolidación actual de este documento y evaluó un escenario relacionado con la carga del módulo de actividades utilizando una semilla de 100 actividades.

Por esta razón, EXP-001 se conserva como evidencia experimental previa y no se presenta como validación retrospectiva del nuevo criterio actual de rendimiento definido el 05 de septiembre de 2026 para la creación de una actividad en máximo 1 minuto.

La trazabilidad debe conservar la secuencia real:

`atributo → escenario correspondiente → criterio vigente en ese momento → experimento → resultado`

Los escenarios actuales que todavía no cuentan con una medición correspondiente podrán contrastarse mediante experimentos posteriores sin alterar la cronología de la evidencia existente.

### Estado de Semana 3

Con la documentación actual se cuenta con:

- identificación de atributos de calidad con antecedentes históricos;
- priorización actual realizada y justificada por el equipo;
- relación entre atributos y preocupaciones de stakeholders;
- relación con requerimientos no funcionales históricos;
- mapa atributo → decisión;
- escenarios verificables para rendimiento, seguridad, usabilidad y disponibilidad;
- criterios actuales definidos por el equipo;
- registro de propuestas de IA y su reformulación crítica.

Las mediciones experimentales se registrarán como evidencia independiente de los escenarios, evitando mezclar la definición del atributo con los resultados obtenidos posteriormente.