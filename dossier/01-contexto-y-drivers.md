# 01 - Contexto y drivers

## 1. Identificación y alcance del sistema

| Elemento | Información |
|---|---|
| Sistema | RachaPro |
| Público objetivo principal | Estudiantes universitarios |
| Público adicional considerado actualmente | Usuarios generales |
| Estado del sistema base | Implementación funcional actual de RachaPro |
| Alcance funcional general | Gestión de actividades, sesiones Pomodoro, seguimiento del progreso, rachas, logros y recordatorios |

## 2. Contexto

- RachaPro proviene de un proyecto desarrollado previamente en Ingeniería de Software y estuvo orientado principalmente a estudiantes universitarios.
- El proyecto previo dejó como antecedentes requerimientos, diagramas, una propuesta arquitectónica y un plan de pruebas.
- Posteriormente fue adoptado para Arquitectura de Software bajo la modalidad denominada "Opción A". En el documento de adopción se registró que, en ese momento, el proyecto aún estaba en fase de diseño y sin repositorio GitHub; el sistema base actual corresponde a una implementación funcional posterior.

## 3. Stakeholders y sus preocupaciones

| Stakeholder | Preocupaciones principales | Estado |
|---|---|---|
| Estudiante universitario | Recordatorios oportunos; facilidad de uso; registro y recuperación correcta de actividades y progreso; privacidad; rapidez; visualización del progreso; concentración y personalización. | Confirmado por el equipo y respaldado históricamente como público objetivo principal. |
| Usuario general | Facilidad de uso; recordatorios; personalización; conservación y consistencia de la información; estabilidad; privacidad; rapidez y mensajes claros ante errores. | Incorporado actualmente por decisión del equipo. |
| Desarrollador / mantenedor | Comprensibilidad y mantenimiento del código; localización de errores; cambios controlados; evitar regresiones; evolución de funcionalidades y comprobación mediante pruebas. | Confirmado actualmente por el equipo. |

## 4. Restricciones

| Restricción | Descripción | Estado |
|---|---|---|
| Reutilización de un proyecto previo | La asignatura requiere trabajar sobre un proyecto desarrollado anteriormente; para este caso se seleccionó RachaPro. | CONFIRMADA POR EL EQUIPO |
| Recursos | El proyecto cuenta actualmente con un único desarrollador responsable de su implementación y mantenimiento. | CONFIRMADA |
| Tiempo | Los dossiers de esta fase deben entregarse el 21 de agosto de 2026. | CONFIRMADA POR EL EQUIPO |
| Pruebas y mediciones | Se contempla como exigencia académica realizar pruebas o mediciones sobre el sistema. Falta incorporar la referencia oficial correspondiente al repositorio. | EVIDENCIA PENDIENTE |

## 5. Drivers arquitectónicos preliminares

| Driver preliminar | Justificación | Antecedente | Estado |
|---|---|---|---|
| Rendimiento del módulo de actividades | El equipo considera relevante evaluar el tiempo de respuesta del módulo ante una semilla controlada de actividades registradas. | RNF02 histórico: rendimiento del módulo de actividades. | DRIVER PRELIMINAR CONFIRMADO POR EL EQUIPO |

## 6. Riesgos iniciales

| Riesgo inicial | Posible impacto |
|---|---|
| Caída o cierre inesperado de la aplicación | Puede interrumpir el uso del sistema e impedir temporalmente el acceso a sus funciones. |
| Fallos de persistencia o corrupción de datos | Pueden provocar pérdida, inconsistencia o imposibilidad de recuperar actividades y otra información registrada. |
| Brechas de seguridad o fallos de autenticación | Pueden comprometer la información del usuario o impedir el acceso legítimo a su sesión. |
| Fallos en recordatorios, Pomodoro, progreso, rachas o logros | Pueden producir avisos incorrectos, mediciones erróneas o información inconsistente sobre el progreso del usuario. |
| Fallos de UI/UX | Pueden dificultar el uso de la aplicación y generar errores de interacción. |
| Errores introducidos durante modificaciones o actualizaciones | Un cambio podría afectar funcionalidades existentes o la información almacenada. |

## 7. Supuestos

- El usuario dispone de un dispositivo Android compatible y con recursos suficientes para ejecutar RachaPro.
- Los permisos requeridos por las funcionalidades utilizadas, como notificaciones, se encuentran habilitados y la fecha/hora del dispositivo están correctamente configuradas.
- Durante el uso normal se ingresan datos válidos, excepto en pruebas diseñadas para evaluar entradas incorrectas.
- Las pruebas comparables se realizan sobre una misma versión identificable del sistema, sin cambios de código entre ejecuciones.

## 8. Referencia a la hipótesis inicial

Antes de ejecutar EXP-001, el equipo planteó que el módulo de actividades podría presentar un tiempo de respuesta superior al esperado ante una semilla controlada de actividades.

La hipótesis específica utilizada posteriormente en EXP-001 fue:

Con una semilla de 100 actividades registradas, el tiempo de carga del listado principal podría superar el umbral histórico de 3 segundos.

Esta hipótesis fue formulada antes de disponer de resultados experimentales y fue contrastada posteriormente mediante EXP-001.

## 9. Qué todavía no ha sido verificado

- La existencia y funcionamiento del backend actual, reportado por el equipo pero aún sin evidencia incorporada.
- La referencia oficial sobre pruebas/mediciones y la definición formal de "Opción A".
- La manifestación real de los riesgos identificados.
- El comportamiento del rendimiento con cantidades de actividades diferentes a las 100 utilizadas en EXP-001.
- El comportamiento del escenario medido en dispositivos o condiciones de ejecución diferentes a las utilizadas en EXP-001.

## 10. Trazabilidad

| Fuente / evidencia | Elemento identificado | Relación actual | Estado |
|---|---|---|---|
| Documento histórico de Ingeniería de Software | Estudiantes universitarios como público objetivo | Sustenta el contexto y al stakeholder principal. | HECHO VERIFICADO |
| Encuesta, entrevista y focus group históricos | Necesidades de organización, recordatorios, facilidad de uso, progreso y privacidad | Sustentan preocupaciones del usuario. | HECHO VERIFICADO |
| RNF02 histórico | Rendimiento del módulo de actividades, con criterios previos de tiempo de respuesta y cantidad de actividades | Antecedente del driver seleccionado actualmente. | HECHO VERIFICADO |
| CP19 histórico | Prueba planteada para cargar el listado de actividades | Antecedente de medición de rendimiento. | HECHO VERIFICADO |
| Decisión actual del equipo | Rendimiento del módulo de actividades | Driver arquitectónico preliminar seleccionado. | DECISIÓN ACTUAL |
| Decisión actual del equipo | Hipótesis de rendimiento con una semilla de 100 actividades | Dio origen a la hipótesis previa utilizada en EXP-001. | HIPÓTESIS PREVIA CONTRASTADA POSTERIORMENTE |
| Commit `3b088858061edc47f6cd018a4130a38c2afb0f73` | Línea base funcional previa a instrumentación | Identifica el estado funcional del sistema previo al experimento. | HECHO VERIFICADO |
| Commit `59c182b12c7a2678f6ed09d1b69b399327d097d4` | Versión instrumentada utilizada para medir | Relaciona las corridas con la versión ejecutada durante EXP-001. | HECHO VERIFICADO |
| `experimentos/EXP-001-linea-base/` | Medición real del rendimiento | Contiene condiciones, logs, resultados y script reproducible de EXP-001. | HECHO VERIFICADO |