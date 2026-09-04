# 01 - Contexto del sistema

## 1. Identificación del sistema
| Elemento | Información |
|---|---|
| Sistema | RachaPro |
| Tipo de sistema | Aplicación académica orientada a apoyar la productividad y organización de estudiantes universitarios |
| Público objetivo principal | Estudiantes universitarios |
| Asignatura actual | Arquitectura de Software |
| Integrante actual | Alejandro Villamizar Rodríguez |
| Modalidad de adopción | Opción A |

## 2. Origen y adopción del sistema base
RachaPro se originó como un proyecto desarrollado previamente en la asignatura Ingeniería de Software. En esa etapa se trabajaron el planteamiento del problema, los objetivos, la recolección y análisis de información, los requerimientos funcionales y no funcionales, el diseño del sistema, diagramas UML, una propuesta arquitectónica y un plan de pruebas.

Posteriormente, el proyecto fue adoptado para la asignatura Arquitectura de Software bajo la modalidad **Opción A**.

En el documento de adopción se registró que, en ese momento, RachaPro se encontraba en fase de diseño, no contaba todavía con un repositorio GitHub y disponía de requerimientos funcionales, requerimientos no funcionales y diagramas UML provenientes del proyecto anterior.

Por lo tanto, esa descripción corresponde al **estado histórico del sistema en el momento de su adopción** y no debe interpretarse como una descripción del estado actual de implementación.

## 3. Problema que aborda
RachaPro surge como respuesta a dificultades identificadas en estudiantes universitarios relacionadas con la organización de actividades, la gestión del tiempo y la constancia en el cumplimiento de tareas.

La documentación histórica del proyecto señala además la presencia de procrastinación académica, distracciones frecuentes y dificultades para mantener hábitos de estudio sostenidos. Estas condiciones motivaron la propuesta de una herramienta digital que apoyara no solo el registro de actividades, sino también la concentración, el seguimiento del progreso y la constancia del usuario.

## 4. Objetivo y público objetivo
El objetivo general definido para RachaPro es desarrollar un gestor de tareas interactivo que apoye la constancia y la productividad del usuario mediante un sistema de rachas.

El público objetivo principal está conformado por estudiantes universitarios, especialmente aquellos que presentan dificultades relacionadas con la organización de actividades, la gestión del tiempo, la procrastinación y la constancia académica.

La documentación histórica también contempla el uso de la aplicación para actividades personales, por lo que el sistema no se limita exclusivamente a tareas académicas; sin embargo, el contexto de origen, la investigación realizada y la población analizada se concentran principalmente en estudiantes universitarios.

## 5. Alcance funcional
El alcance funcional definido para RachaPro comprende las siguientes capacidades principales:

- Registro e inicio de sesión de usuarios.
- Creación, consulta, edición, finalización y eliminación de actividades.
- Organización de actividades mediante información como fecha, prioridad, categoría y estado.
- Uso de sesiones de estudio mediante temporizador Pomodoro.
- Registro y visualización del progreso del usuario.
- Seguimiento de rachas, logros y elementos de motivación o recompensa.
- Configuración y gestión de recordatorios asociados a las actividades.

Estas funcionalidades provienen de los requerimientos, casos de uso y módulos definidos durante el proyecto de Ingeniería de Software. Su presencia en la documentación histórica representa el alcance funcional planteado para RachaPro y no constituye, por sí sola, evidencia de que todas las funciones estuvieran implementadas en ese momento.

## 6. Estado del sistema base adoptado
Al momento de su adopción en la asignatura Arquitectura de Software, RachaPro se encontraba documentado como un proyecto en fase de diseño.

La evidencia de adopción registra que en ese momento:

- no existía todavía un repositorio GitHub del proyecto;
- se disponía de requerimientos funcionales;
- se disponía de requerimientos no funcionales;
- existían diagramas UML de la aplicación;
- existía un prototipo de interfaz asociado al proyecto.

Por lo tanto, el sistema base adoptado correspondía inicialmente a un conjunto de artefactos de análisis, diseño y planificación provenientes de Ingeniería de Software, y no a la implementación actual que existe posteriormente en el repositorio.

## 7. Evolución del sistema
Después de su adopción en Arquitectura de Software, RachaPro evolucionó desde los artefactos de análisis y diseño históricos hacia una implementación funcional de la aplicación Android.

En una primera etapa de implementación, la aplicación utilizó persistencia local mediante Room/SQLite para gestionar la información del sistema. Sobre esta versión se desarrollaron y probaron funcionalidades como la gestión de actividades, sesiones Pomodoro, progreso, rachas, logros y recordatorios.

Durante esta etapa se ejecutó posteriormente el experimento EXP-001, orientado a medir el tiempo de carga del listado principal del módulo de actividades bajo una semilla controlada de 100 actividades.

En una etapa posterior, el sistema evolucionó hacia una arquitectura que incorpora un backend desarrollado con Spring Boot y una base de datos PostgreSQL ejecutada mediante Docker. La aplicación Android se comunica actualmente con el backend mediante una API HTTP, mientras el backend gestiona el acceso a la base de datos y mecanismos de autenticación.

Sobre esta arquitectura posterior también se realizó una prueba de carga utilizando 500 usuarios de prueba con 1.000 actividades asociadas a cada uno, para un volumen total de 500.000 actividades.

EXP-001 y la prueba posterior corresponden a momentos y arquitecturas diferentes del proyecto, por lo que sus resultados deben conservarse como evidencias separadas y no interpretarse como una comparación directa entre ambas implementaciones.

## 8. Límites del sistema
El alcance documentado de RachaPro se concentra en las funcionalidades necesarias para apoyar la organización y productividad del usuario: autenticación, gestión de actividades, sesiones Pomodoro, seguimiento del progreso, rachas, logros y recordatorios.

En el estado actual del proyecto, la solución comprende la aplicación Android, el backend encargado de exponer los servicios utilizados por la aplicación y la persistencia de información asociada al sistema.

La documentación histórica contiene además propuestas de despliegue e integración, como el uso de un servicio externo de notificaciones push y una base de datos desplegada sobre Raspberry Pi. Estas propuestas corresponden al diseño realizado durante Ingeniería de Software y no se consideran, por sí mismas, evidencia de los límites o componentes de la implementación actual.

Cualquier integración, servicio externo o componente que no se encuentre respaldado por evidencia del repositorio actual debe mantenerse como información histórica, propuesta o pendiente de verificación.

## 9. Evidencias y fuentes
Las afirmaciones presentadas en este documento se apoyan en fuentes históricas del proyecto y en evidencia producida posteriormente durante su implementación y evolución.

| Fuente / evidencia | Información que respalda | Relación temporal |
|---|---|---|
| `docs/sources/historical/RachaPro_ Gestor Inteligente de Productividad Académica.pdf` | Origen del proyecto, problema, objetivos, público estudiado, requerimientos funcionales y no funcionales, módulos, arquitectura propuesta y plan de pruebas. | Proyecto histórico de Ingeniería de Software |
| `docs/sources/historical/ProyectoAS.docx` | Adopción de RachaPro como Opción A y estado del proyecto en fase de diseño al inicio de Arquitectura de Software. | Momento de adopción |
| Código fuente de la aplicación Android | Evidencia de la implementación funcional posterior del sistema. | Implementación posterior |
| `experimentos/EXP-001-linea-base/` | Evidencia de la medición de rendimiento realizada sobre la etapa de persistencia local con Room/SQLite. | Posterior a la implementación inicial |
| `backend/` | Evidencia de la incorporación posterior del backend Spring Boot. | Evolución posterior |
| `infra/postgres/` | Evidencia de la configuración de PostgreSQL mediante Docker. | Evolución posterior |
| `docs/performance/prueba-carga-500k.md` y `docs/performance/resultados-carga-500k.csv` | Evidencia de la prueba posterior con 500 usuarios y 500.000 actividades. | Arquitectura posterior con backend y PostgreSQL |

## 10. Información pendiente de verificación
La revisión de las fuentes históricas y de la implementación actual permite identificar los siguientes puntos que todavía requieren evidencia adicional o documentación más precisa:

- No se ha identificado una restricción económica actual específica para el proyecto en la asignatura Arquitectura de Software. El documento histórico contiene un presupuesto estimado correspondiente a Ingeniería de Software, pero este no se considera automáticamente una restricción económica vigente.
- Las instrucciones reproducibles para ejecutar el sistema completo todavía requieren ser consolidadas. Actualmente se han identificado dependencias de configuración local en el backend y en la dirección utilizada por la aplicación Android para comunicarse con este.
- Debe verificarse y documentarse de forma explícita el papel que conserva Room/SQLite dentro de la arquitectura actual después de la incorporación del backend y PostgreSQL.
- Las integraciones descritas únicamente en la arquitectura histórica, como el servicio externo de notificaciones push o el despliegue de base de datos sobre Raspberry Pi, no se consideran parte de la implementación actual mientras no exista evidencia correspondiente en el repositorio.
- Los límites arquitectónicos formales del sistema todavía no han sido definidos mediante las vistas C4 requeridas posteriormente en el curso.