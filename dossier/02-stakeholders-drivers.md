# 02 - Stakeholders, restricciones y drivers preliminares

## 1. Stakeholders identificados
A partir de la documentación histórica y del estado actual del proyecto se identifican los siguientes stakeholders respaldados por evidencia:

| Stakeholder | Relación con RachaPro | Evidencia |
|---|---|---|
| Estudiante universitario | Usuario principal del sistema. Utiliza RachaPro para organizar actividades, gestionar sesiones de estudio, consultar su progreso y utilizar funciones de apoyo a la constancia académica. | La investigación histórica, los requerimientos y los casos de uso se concentran principalmente en estudiantes universitarios. |
| Desarrollador / mantenedor | Responsable actual de implementar, mantener, probar y evolucionar el sistema. | El proyecto de Arquitectura de Software cuenta actualmente con un único integrante responsable de su desarrollo. |

El docente entrevistado durante Ingeniería de Software se mantiene como **fuente de información histórica** sobre organización, concentración, manejo del tiempo y motivación, pero la evidencia disponible no permite clasificarlo automáticamente como stakeholder actual del sistema.

Aunque la documentación contempla actividades de carácter personal además de académico, la investigación original se realizó principalmente con estudiantes universitarios. Por esta razón, no se incorpora por ahora un “usuario general” como stakeholder independiente sin una decisión explícita del equipo sobre la ampliación del público objetivo.

## 2. Preocupaciones de los stakeholders
Las preocupaciones asociadas al estudiante universitario se derivan de los instrumentos aplicados durante Ingeniería de Software: encuesta, entrevista y focus group.

| Stakeholder | Preocupación | Clasificación | Evidencia |
|---|---|---|---|
| Estudiante universitario | Organizar actividades y administrar mejor el tiempo. | HECHO VERIFICADO | Encuesta y focus group identificaron dificultades relacionadas con organización, constancia y manejo del tiempo. |
| Estudiante universitario | Recibir recordatorios oportunos. | HECHO VERIFICADO | La encuesta mostró interés explícito por recibir recordatorios automáticos. |
| Estudiante universitario | Utilizar una herramienta clara y fácil de usar. | HECHO VERIFICADO | Los participantes valoraron simplicidad, facilidad de uso y claridad en herramientas de organización. |
| Estudiante universitario | Visualizar progreso y avance. | HECHO VERIFICADO | La encuesta y el focus group identificaron interés por estadísticas, tareas completadas y seguimiento visual del progreso. |
| Estudiante universitario | Apoyar la concentración durante el estudio. | HECHO VERIFICADO | Encuesta, entrevista y focus group identificaron distracciones frecuentes y valoraron mecanismos como Pomodoro. |
| Estudiante universitario | Personalizar la forma de organizar la información. | HECHO VERIFICADO | El focus group resaltó la personalización como una característica valorada. |
| Estudiante universitario | Mantener motivación y constancia. | HECHO VERIFICADO | Entrevista y focus group mencionaron rachas, recompensas, incentivos y gamificación. |
| Estudiante universitario | Proteger la privacidad de sus datos académicos. | HECHO VERIFICADO | La encuesta incluyó una pregunta específica sobre preocupación por la privacidad de los datos académicos. |

Para el desarrollador / mantenedor no se cuenta con un instrumento de recolección equivalente a la encuesta o focus group aplicado a estudiantes.

A partir de su responsabilidad actual sobre la implementación, pruebas y mantenimiento del proyecto puede inferirse una preocupación por mantener el sistema ejecutable, comprensible y modificable sin introducir fallos; sin embargo, esta preocupación se clasifica como **INFERENCIA** y no como resultado de una fuente histórica de usuarios.

**EVIDENCIA FALTANTE:** no existe actualmente una fuente específica que recoja y priorice formalmente las preocupaciones del desarrollador / mantenedor.

## 3. Restricciones técnicas
Las siguientes condiciones técnicas se encuentran respaldadas por la implementación actual del sistema. Se documentan como restricciones o condicionantes del entorno actual y no como decisiones arquitectónicas permanentes.

| Restricción o condicionante | Evidencia | Clasificación |
|---|---|---|
| La aplicación cliente se ejecuta sobre Android. | El módulo `app/` corresponde a una aplicación Android y utiliza `applicationId = "com.example.rachapro"`. | HECHO VERIFICADO |
| La versión mínima configurada para Android es API 24. | `app/build.gradle.kts` define `minSdk = 24`. | HECHO VERIFICADO |
| El backend requiere actualmente Java 25 para su ejecución. | `backend/build.gradle.kts` define un toolchain con Java 25. | HECHO VERIFICADO |
| El backend depende de PostgreSQL para la persistencia central actual. | `application.properties` configura un datasource PostgreSQL y el backend utiliza el driver correspondiente. | HECHO VERIFICADO |
| PostgreSQL se ejecuta localmente mediante Docker Compose en el entorno documentado. | `infra/postgres/docker-compose.yml` utiliza la imagen `postgres:17`. | HECHO VERIFICADO |
| La aplicación Android requiere conectividad con el backend para utilizar las funcionalidades migradas a la API. | `RetrofitClient.kt` configura la comunicación HTTP con el backend. | HECHO VERIFICADO |
| La dirección del backend se encuentra actualmente definida directamente en `RetrofitClient.kt`. | `BASE_URL` contiene una dirección IPv4 local y el puerto `8080`. | HECHO VERIFICADO / LIMITACIÓN ACTUAL |
| El entorno local utiliza HTTP sin TLS para la comunicación Android-backend. | `AndroidManifest.xml` contiene `android:usesCleartextTraffic="true"`. | HECHO VERIFICADO / ENTORNO DE DESARROLLO |

Estas condiciones describen el estado técnico de la implementación actual. No se asume que todas ellas sean restricciones impuestas por la asignatura ni que deban conservarse en futuras decisiones arquitectónicas.

La arquitectura histórica incluía propuestas adicionales, como un servicio externo de notificaciones y despliegue sobre Raspberry Pi. Estas propuestas no se consideran restricciones técnicas actuales mientras no exista evidencia de su implementación o de una obligación vigente de utilizarlas.

## 4. Restricciones económicas
La documentación histórica de RachaPro contiene un presupuesto estimado elaborado durante la asignatura Ingeniería de Software. Este presupuesto consideraba recursos humanos, herramientas de software y costos operativos asociados al desarrollo del proyecto.

El propio documento histórico indica que los valores correspondían a una estimación teórica basada en horas de trabajo y precios de mercado aproximados.

Por esta razón, dicho presupuesto se conserva como **ANTECEDENTE HISTÓRICO**, pero no se utiliza como evidencia de una restricción económica vigente para la etapa actual de Arquitectura de Software.

**EVIDENCIA FALTANTE:** hasta el momento no se ha identificado una limitación presupuestal, tope de gasto o condición económica actual impuesta al proyecto que pueda documentarse como restricción económica vigente.

## 5. Restricciones organizacionales
Las siguientes condiciones organizacionales se encuentran respaldadas por la situación actual del proyecto:

| Restricción o condicionante | Descripción | Clasificación |
|---|---|---|
| Equipo de un solo integrante | La etapa actual de RachaPro en Arquitectura de Software cuenta con un único integrante responsable de la implementación, documentación, pruebas y mantenimiento del proyecto. | HECHO VERIFICADO |
| Adopción de un sistema previo | RachaPro fue adoptado bajo la modalidad Opción A a partir del proyecto desarrollado previamente en Ingeniería de Software. | HECHO VERIFICADO |
| Conservación de la trazabilidad histórica | Las decisiones, requerimientos, diagramas y evidencias provenientes de Ingeniería de Software deben distinguirse de las decisiones y evidencias producidas posteriormente durante Arquitectura de Software. | CONDICIÓN METODOLÓGICA DERIVADA DE LA CRONOLOGÍA DEL PROYECTO |

La existencia de un único integrante implica una distribución individual de las actividades que normalmente podrían repartirse entre varios miembros del equipo; sin embargo, no se asume por ello ningún impacto específico sobre calidad, tiempos o decisiones arquitectónicas sin evidencia adicional.

La reutilización de RachaPro como sistema previo condiciona el trabajo actual a partir de artefactos históricos existentes, pero no obliga a conservar sin cambios las propuestas arquitectónicas elaboradas durante Ingeniería de Software.

## 6. Drivers arquitectónicos preliminares
El equipo mantiene como driver arquitectónico preliminar el **rendimiento del módulo de actividades**.

| Driver preliminar | Justificación | Antecedente | Estado |
|---|---|---|---|
| Rendimiento del módulo de actividades | Se considera relevante evaluar el comportamiento del módulo de actividades ante distintos volúmenes de información y condiciones de uso. | RNF02 histórico: rendimiento del módulo de actividades. | DRIVER PRELIMINAR CONFIRMADO POR EL EQUIPO |

El antecedente histórico de este driver se encuentra en el RNF02 definido durante Ingeniería de Software. Dicho requerimiento establecía, entre otros criterios, que el listado principal de actividades no superara los 3 segundos de carga en condiciones normales y que el sistema mantuviera un comportamiento estable con al menos 100 actividades registradas.

La selección actual del rendimiento como driver preliminar corresponde a una decisión del equipo en la etapa de Arquitectura de Software. Los valores de 3 segundos y 100 actividades se conservan como **antecedentes históricos** y no se reinterpretan automáticamente como nuevos umbrales o métricas definidos en la etapa actual.

## 7. Evidencias y fuentes
Las afirmaciones de este documento se sustentan en fuentes históricas del proyecto y en evidencia de la implementación actual.

| Fuente / evidencia | Información que respalda | Clasificación |
|---|---|---|
| `docs/sources/historical/RachaPro_ Gestor Inteligente de Productividad Académica.pdf` | Público objetivo, resultados de encuesta, entrevista y focus group, requerimientos funcionales y no funcionales, presupuesto histórico, módulos, arquitectura propuesta y plan de pruebas. | FUENTE HISTÓRICA |
| `docs/sources/historical/ProyectoAS.docx` | Adopción de RachaPro como Opción A, integrante actual y estado del proyecto al momento de la adopción. | FUENTE DE ADOPCIÓN |
| `app/build.gradle.kts` | Configuración actual de la aplicación Android, incluyendo `minSdk`, `compileSdk` y dependencias principales. | EVIDENCIA ACTUAL |
| `app/src/main/java/com/example/rachapro/network/RetrofitClient.kt` | Comunicación actual de la aplicación Android con el backend mediante Retrofit y configuración de `BASE_URL`. | EVIDENCIA ACTUAL |
| `app/src/main/AndroidManifest.xml` | Permiso de Internet y habilitación de tráfico HTTP para el entorno local actual. | EVIDENCIA ACTUAL |
| `backend/build.gradle.kts` | Configuración del backend, Spring Boot y requerimiento de Java 25. | EVIDENCIA ACTUAL |
| `backend/src/main/resources/application.properties` | Dependencia del backend respecto a las variables de conexión con PostgreSQL. | EVIDENCIA ACTUAL |
| `infra/postgres/docker-compose.yml` | Ejecución local de PostgreSQL mediante Docker Compose. | EVIDENCIA ACTUAL |
| `docs/ejecucion-local.md` | Procedimiento reproducible y comprobaciones realizadas para ejecutar PostgreSQL, backend y aplicación Android. | EVIDENCIA EJECUTABLE |
| Decisión actual del equipo | Mantener el rendimiento del módulo de actividades como driver arquitectónico preliminar. | DECISIÓN ACTUAL DEL EQUIPO |

El RNF02 histórico se utiliza únicamente como antecedente del driver de rendimiento. La decisión de mantener dicho driver en la etapa actual corresponde al equipo y no se presenta como una consecuencia automática de la documentación previa.

## 8. Información pendiente de verificación
A partir de la revisión de las fuentes históricas y de la implementación actual, permanecen los siguientes puntos pendientes de verificación o formalización:

- No se ha identificado una restricción económica actual específica para la etapa de Arquitectura de Software. El presupuesto existente corresponde al proyecto histórico de Ingeniería de Software y se conserva únicamente como antecedente.
- No existe una fuente específica que recoja y priorice formalmente las preocupaciones del desarrollador / mantenedor; las preocupaciones asociadas a este rol se mantienen como inferencias derivadas de sus responsabilidades actuales.
- La ampliación del público objetivo hacia un “usuario general” no se considera confirmada mientras no exista una decisión explícita del equipo que la incorpore como stakeholder independiente.
- El rendimiento del módulo de actividades se mantiene como driver arquitectónico preliminar por decisión actual del equipo, pero la priorización relativa frente a otros posibles drivers todavía no se considera formalmente establecida.
- Los valores históricos de 3 segundos y 100 actividades asociados al RNF02 se conservan como antecedentes. No se interpretan automáticamente como métricas o umbrales redefinidos para la etapa actual.
- Las restricciones técnicas documentadas describen el estado actual de la implementación y no deben interpretarse como obligaciones permanentes de arquitectura sin evidencia adicional.
