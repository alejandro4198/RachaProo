# 01 - Contexto y drivers

## 1. Identificación y alcance del sistema

| Elemento | Información |
|---|---|
| Sistema | RachaPro |
| Público objetivo principal | Estudiantes universitarios |
| Público adicional | Usuarios generales |
| Estado del sistema base | Implementación funcional actual de RachaPro |
| Alcance funcional general | Gestión de actividades, sesiones Pomodoro, seguimiento del progreso, rachas, logros y recordatorios |

## 2. Contexto

- RachaPro proviene de un proyecto desarrollado previamente en Ingeniería de Software y estuvo orientado principalmente a estudiantes universitarios.
- El proyecto previo dejó requerimientos, diagramas, una propuesta arquitectónica y un plan de pruebas.
- Posteriormente fue adoptado para Arquitectura de Software bajo la modalidad denominada "Opción A". En el documento de adopción se registró que, en ese momento, el proyecto aún estaba en fase de diseño y sin repositorio GitHub; el sistema base actual corresponde a una implementación funcional posterior.

## 3. Stakeholders y sus preocupaciones

| Stakeholder | Preocupaciones principales |
|---|---|
| Estudiante universitario | Recordatorios oportunos; facilidad de uso; registro y recuperación correcta de actividades y progreso; privacidad; rapidez; visualización del progreso; concentración y personalización. |
| Usuario general | Facilidad de uso; recordatorios; personalización; conservación y consistencia de la información; estabilidad; privacidad; rapidez y mensajes claros ante errores. |
| Desarrollador / mantenedor | Comprensibilidad y mantenimiento del código; localización de errores; cambios controlados; evitar regresiones; evolución de funcionalidades y comprobación mediante pruebas. |

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

- Caída o cierre inesperado de la aplicación.
- Fallos de persistencia o corrupción de datos.
- Brechas de seguridad o fallos de autenticación.
- Fallos en recordatorios, Pomodoro, progreso, rachas o logros.
- Fallos de UI/UX.
- Errores introducidos durante modificaciones o actualizaciones.

## 7. Supuestos

- El usuario dispone de un dispositivo Android compatible.
- Los permisos necesarios están habilitados y la fecha/hora están correctamente configuradas.
- Se ingresan datos válidos, salvo en pruebas de entradas incorrectas.
- Las pruebas comparables utilizan la misma versión del sistema sin cambios de código entre ejecuciones.

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

| Fuente / evidencia | Relación actual | Estado |
|---|---|---|
| Documento histórico de Ingeniería de Software | Sustenta a estudiantes universitarios como público objetivo principal. | HECHO VERIFICADO |
| Encuesta, entrevista y focus group históricos | Sustentan preocupaciones de organización, recordatorios, facilidad de uso, progreso y privacidad. | HECHO VERIFICADO |
| RNF02 histórico | Antecedente del driver de rendimiento y de sus criterios previos. | HECHO VERIFICADO |
| CP19 histórico | Antecedente de una prueba de carga del listado de actividades. | HECHO VERIFICADO |
| Decisión actual del equipo | Rendimiento del módulo de actividades como driver preliminar. | DECISIÓN ACTUAL |
| Decisión actual del equipo | Hipótesis de rendimiento con una semilla de 100 actividades. | HIPÓTESIS PREVIA CONTRASTADA POSTERIORMENTE |