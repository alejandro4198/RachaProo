# 02 - Escenarios de calidad

## 1. Atributos de calidad considerados

| Atributo de calidad | Antecedente en el proyecto                   | Estado |
|---|----------------------------------------------|---|
| Usabilidad | RNF01, RNF05 y RNF06 del proyecto histórico. | ANTECEDENTE HISTÓRICO |
| Rendimiento | RNF02: rendimiento del módulo de actividades. | ANTECEDENTE HISTÓRICO |
| Seguridad | RNF03: seguridad de la información.          | ANTECEDENTE HISTÓRICO |
| Disponibilidad | RNF04: disponibilidad del sistema.           | ANTECEDENTE HISTÓRICO |
| Precisión | RNF07: precisión del temporizador Pomodoro.  | ANTECEDENTE HISTÓRICO |

## 2. Relación con stakeholders y contexto

| Stakeholder | Preocupaciones relacionadas | Atributos con antecedente en el proyecto |
|---|---|---|
| Estudiante universitario | Facilidad de uso, rapidez, privacidad, acceso a la información y funcionamiento correcto de las funciones principales. | Usabilidad, Rendimiento, Seguridad y Disponibilidad |
| Usuario general | Facilidad de uso, rapidez, estabilidad, privacidad y consistencia de la información. | Usabilidad, Rendimiento, Seguridad y Disponibilidad |
| Desarrollador / mantenedor | Evitar regresiones, facilitar cambios y comprobar el funcionamiento del sistema. | No existe todavía un atributo histórico formalmente asociado a estas preocupaciones dentro de los seleccionados en esta sección. |

Los atributos anteriores provienen de los requerimientos no funcionales históricos de RachaPro. La relación con los stakeholders actuales se realiza a partir de las preocupaciones identificadas por el equipo y no implica todavía una priorización entre atributos.

## 3. Priorización realizada por el equipo

| Elemento | Decisión actual | Estado |
|---|---|---|
| Atributo priorizado para el trabajo actual | Rendimiento | DECISIÓN ACTUAL DEL EQUIPO |
| Área de aplicación | Módulo de actividades | DECISIÓN ACTUAL DEL EQUIPO |
| Motivo de selección | Se busca comprobar el comportamiento del módulo al cargar un listado con 100 actividades registradas. | HIPÓTESIS NO VERIFICADA AL MOMENTO DE LA SELECCIÓN |

Los requerimientos históricos contemplaban varios atributos de calidad con prioridad alta, pero no establecían una priorización arquitectónica comparativa entre ellos. Para el trabajo actual, el equipo seleccionó el rendimiento del módulo de actividades como foco de evaluación.

## 4. Trade-offs

| Trade-off preliminar | Descripción | Estado |
|---|---|---|
| Rendimiento vs. mantenibilidad | El equipo considera que una optimización orientada a mejorar el rendimiento del módulo de actividades podría aumentar la complejidad del código y dificultar su comprensión, modificación o mantenimiento. | TRADE-OFF PRELIMINAR IDENTIFICADO POR EL EQUIPO |

Este trade-off todavía no ha sido comprobado mediante cambios arquitectónicos ni mediciones, por lo que se mantiene como una consideración preliminar para el análisis.

## 5. Escenarios de calidad

### Escenario de rendimiento del módulo de actividades

| Campo | Definición                                                                                                                                                                 |
|---|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Fuente del estímulo | Inicialización del módulo de actividades de RachaPro. |
| Estímulo | Se inicia automáticamente la ejecución de `loadData()` al crearse `ActivitiesViewModel`. |
| Ambiente | Uso normal de la aplicación con un conjunto previamente cargado de actividades. |
| Artefacto | Módulo de actividades de RachaPro.                                                                                                                                         |
| Respuesta | El sistema recupera y prepara los datos del listado.                                                                                                                       |
| Medida de respuesta | Desde que inicia loadData() hasta que se obtiene ActivitiesUiState.Success con las 100 actividades.                                                                        |
| Criterio / umbral | El listado principal de actividades debe mostrarse en un tiempo ≤ 3 segundos.                                                                                              |
| Condiciones de reproducibilidad | Las mediciones se realizan sobre la misma versión de RachaPro, con la misma cantidad y distribución de actividades y bajo las mismas condiciones del entorno de ejecución. |

## 6. Escenario seleccionado para Semana 4

El equipo seleccionó para la medición de Semana 4 el escenario de rendimiento asociado a la carga del listado de actividades.

Este escenario fue elegido por su relación directa con el driver arquitectónico preliminar `Rendimiento del módulo de actividades` y con el antecedente histórico RNF02.

La medición buscará contrastar si el comportamiento observado en la implementación actual cumple el criterio previamente definido para la carga del listado de actividades.

## 7. Métrica y umbral previamente definidos

| Elemento | Definición | Origen |
|---|---|---|
| Métrica | Tiempo de carga del listado principal de actividades, medido en segundos. | RNF02 histórico |
| Umbral | El listado principal de actividades debe mostrarse en un tiempo ≤ 3 segundos. | RNF02 histórico |

El umbral fue definido en el proyecto previo, antes de realizar la medición actual, por lo que no corresponde a un valor ajustado posteriormente a los resultados.

La medición actual utiliza como punto final la obtención de `ActivitiesUiState.Success` y no el momento exacto en que el listado queda renderizado en pantalla. Por esta razón, el resultado se utilizará como una aproximación al antecedente histórico RNF02 y esta diferencia deberá considerarse al interpretar los resultados.

## 8. Características de la semilla

| Característica | Definición |
|---|---|
| Cantidad | 100 actividades |
| Usuario propietario | Un mismo usuario de prueba |
| Estado | Pendiente |
| Prioridad | Media |
| Categoría | Estudio |
| Títulos | `Actividad 001` hasta `Actividad 100` |
| Descripción | Vacía |
| Subtareas | Ninguna |
| Recordatorios | Ninguno |
| Repetición | Ninguna |
| Fecha límite | 31/12/2030 |
| Hora | Ninguna |

La misma semilla deberá mantenerse en todas las corridas comparables del experimento.

## 9. Instrumento planificado

| Elemento | Definición |
|---|---|
| Instrumento de medición | `SystemClock.elapsedRealtime()` |
| Unidad de medición | Milisegundos, con conversión a segundos para comparar con el umbral histórico |
| Registro de evidencia | Logcat |
| Inicio de la medición | Momento en que comienza la ejecución de `loadData()` en `ActivitiesViewModel`. |
| Fin de la medición | Momento en que se obtiene `ActivitiesUiState.Success` con las 100 actividades cargadas. |

El instrumento será utilizado únicamente para observar el comportamiento de la implementación base y registrar el tiempo correspondiente a cada corrida del experimento.

## 10. Relación con la hipótesis previa

La hipótesis preliminar establece que, con una semilla de 100 actividades registradas, el tiempo de carga del listado principal podría superar el umbral histórico de 3 segundos.

El escenario seleccionado permite contrastar esta hipótesis midiendo el tiempo transcurrido desde el inicio de `loadData()` hasta que el sistema obtiene `ActivitiesUiState.Success` con las 100 actividades cargadas.

Para la medición se definió el uso de la misma versión del sistema, `SystemClock.elapsedRealtime()` y el registro de resultados mediante Logcat.

En el momento de definir este escenario y antes de ejecutar EXP-001, no existía evidencia experimental que permitiera aceptar o rechazar la hipótesis.


## 11. Matriz de atributos de calidad críticos

Esta matriz corresponde a una consolidación posterior de los atributos de calidad identificados en el proyecto. Fue elaborada después de la selección del escenario de Rendimiento y de la ejecución de EXP-001, por lo que no fue utilizada originalmente para seleccionar dicho atributo ni modifica retroactivamente las decisiones previas.

Como consolidación de los atributos de calidad identificados en la documentación histórica de RachaPro, se realizó una valoración utilizando tres criterios:

- Impacto en el usuario.
- Impacto en el funcionamiento del sistema.
- Necesidad de atención arquitectónica.

Cada criterio se valoró mediante una escala de 1 a 3:

- 1: Bajo.
- 2: Medio.
- 3: Alto.

La criticidad corresponde a la suma de los tres criterios, con un valor posible entre 3 y 9.

Los rangos utilizados para interpretar el resultado son:

| Puntaje | Nivel |
|---:|---|
| 3–4 | Bajo |
| 5–6 | Medio |
| 7–8 | Alto |
| 9 | Crítico |

### Valoración

| Atributo | RNF asociados | Impacto usuario | Impacto sistema | Atención arquitectónica | Total | Nivel |
|---|---|---:|---:|---:|---:|---|
| Usabilidad | RNF01, RNF05 y RNF06 | 3 | 2 | 2 | 7 | Alto |
| Rendimiento | RNF02 | 2 | 2 | 3 | 7 | Alto |
| Seguridad | RNF03 | 3 | 3 | 3 | 9 | Crítico |
| Disponibilidad | RNF04 | 3 | 2 | 3 | 8 | Alto |
| Precisión | RNF07 | 1 | 2 | 1 | 4 | Bajo |

### Justificación de las valoraciones

**Usabilidad — RNF01, RNF05 y RNF06.**
La usabilidad tiene un impacto alto sobre el usuario debido a que dificultades importantes para comprender o utilizar la aplicación pueden impedir que esta cumpla su propósito. Se considera que su impacto sobre el sistema y su necesidad de atención arquitectónica son medios.

**Rendimiento — RNF02.**
Un rendimiento deficiente puede generar frustración en los usuarios y afectar la continuidad del uso de la aplicación. Se considera especialmente importante vigilar este atributo en funcionalidades relacionadas con el registro y consulta de actividades.

**Seguridad — RNF03.**
La seguridad obtuvo la valoración máxima debido a que una falla podría comprometer la información de los usuarios. Su impacto se considera alto tanto para el usuario como para el sistema y requiere una atención arquitectónica elevada.

**Disponibilidad — RNF04.**
La disponibilidad tiene un impacto alto para el usuario debido a que, si la aplicación no puede abrirse o sus funciones principales no pueden utilizarse, el usuario queda impedido para interactuar con el sistema. Por esta razón también se considera necesaria una atención arquitectónica alta.

**Precisión — RNF07.**
La precisión del temporizador puede generar inconformidad si presenta fallos, aunque se considera menos crítica frente a otros atributos debido a que el propósito principal de RachaPro se encuentra relacionado con la gestión y registro de actividades.
