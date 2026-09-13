# Baseline AS-IS de disponibilidad — Semana 8

## 1. Identificación

| Campo | Valor |
|---|---|
| Proyecto | RachaPro — Gestor de Productividad Académica |
| Curso | Arquitectura de Software |
| Fecha de ejecución | 2026-09-13 |
| Identificador | PRE-AVAIL-001 |
| Tipo de evidencia | Experimento manual AS-IS |
| Rama | `exp/semana8-disponibilidad-baseline` |
| Estado | Previo a cambios de configuración, timeout o arquitectura de Semana 8 |

Este documento registra el comportamiento observado de RachaPro ante la indisponibilidad intencional del backend Spring Boot.

La prueba fue ejecutada antes de introducir modificaciones de código, timeouts, políticas de reintento o cambios arquitectónicos asociados a Semana 8.

Por tanto, los resultados registrados aquí corresponden al comportamiento AS-IS del sistema.

---

## 2. Objetivo

Medir cuánto tiempo tarda la aplicación Android en detectar e informar al usuario que una operación dependiente del backend no puede completarse cuando Spring Boot se encuentra detenido.

La medición principal se realizó al ingresar a la funcionalidad de Actividades con una sesión previamente autenticada.

También se realizaron observaciones secundarias sobre navegación, creación de actividades, funcionamiento de Pomodoro, falsos éxitos y recuperación después de restaurar el backend.

---

## 3. Criterio de aceptación

El criterio de disponibilidad definido para este escenario es:

> Cuando una funcionalidad Android que requiere el backend intenta operar mientras este se encuentra inaccesible, la aplicación debe detectar la indisponibilidad y mostrar un error al usuario en un tiempo menor o igual a 5 minutos.

Límite temporal: `300 segundos`.

Además del tiempo, se observó:

- si ocurría un cierre inesperado de la aplicación;
- si la aplicación permitía continuar navegando;
- si se producía un falso éxito;
- cómo se comportaban otras funcionalidades dependientes del backend.

Para este experimento, un **crash** significa el cierre inesperado de RachaPro, terminación del proceso o salida involuntaria de la aplicación.

Un **falso éxito** significa que la aplicación informa que una operación remota fue completada correctamente aunque el backend se encontraba detenido y la operación realmente no pudo ejecutarse.

---

## 4. Entorno de prueba

La prueba fue realizada con:

- aplicación Android ejecutada en dispositivo físico;
- sesión de usuario autenticada;
- backend Spring Boot ejecutado localmente;
- backend expuesto en el puerto `8080`;
- PostgreSQL 17 ejecutado mediante Docker;
- PostgreSQL disponible durante las corridas;
- red del dispositivo activa.

Para provocar la indisponibilidad se detuvo únicamente Spring Boot.

No se detuvo PostgreSQL.

No se desconectó la red del dispositivo.

No se modificó código durante las mediciones.

No se modificaron timeouts ni políticas de reintento antes de ejecutar este baseline.

---

## 5. Verificación del estado del entorno

Antes de ejecutar las corridas se verificó que el backend estuviera disponible mediante:

```powershell
Invoke-RestMethod "http://localhost:8080/actuator/health" |
    Select-Object status
```

Resultado observado:

```text
status
------
UP
```

Después de detener Spring Boot se verificó que el puerto `8080` ya no estuviera escuchando:

```text
Puerto 8080 escuchando: False
```

Durante las corridas PostgreSQL permaneció activo y saludable:

```text
NAMES               STATUS
rachapro-postgres   ... (healthy)
```

Esto permitió aislar el escenario estudiado como indisponibilidad del backend Spring Boot sin detener simultáneamente la base de datos.

---

## 6. Inspección estática previa

Antes de modificar el sistema se inspeccionó la configuración Android relacionada con Retrofit y OkHttp.

Archivo principal revisado:

`app/src/main/java/com/example/rachapro/network/RetrofitClient.kt`

En el estado AS-IS inspeccionado no se encontró configuración explícita propia de RachaPro para:

- `connectTimeout`;
- `readTimeout`;
- `writeTimeout`;
- `callTimeout`;
- `retryOnConnectionFailure`.

Por tanto, PRE-AVAIL-001 mide el comportamiento actual observado del cliente configurado por la aplicación.

La prueba no demuestra cuál es el timeout interno exacto utilizado por las bibliotecas.

Tampoco se modificaron estos parámetros antes de ejecutar las mediciones.

---

## 7. Flujo de Actividades inspeccionado

Se revisaron principalmente:

- `ActivitiesViewModel.kt`;
- `ActivitiesScreen.kt`.

La implementación maneja fallos remotos mediante estados y mensajes de error controlados.

También se comprobó que al ingresar a la pantalla de Actividades se ejecuta una actualización de estados remotos.

Cuando esta actualización falla se puede generar el mensaje:

```text
No fue posible actualizar los estados.
```

Este fue el mensaje utilizado como evento final de la medición.

---

## 8. Ajuste exploratorio del protocolo

Antes de las tres corridas finales se realizaron pruebas exploratorias.

Estas ejecuciones no forman parte de los resultados estadísticos.

### 8.1 Reinicio mediante ADB

Inicialmente se utilizó `adb force-stop` seguido del reinicio de la aplicación.

Bajo esa secuencia, RachaPro volvió a la pantalla de Login y no quedó disponible una sesión autenticada utilizable para ejecutar correctamente el escenario previsto.

Por esta razón, esa ejecución fue descartada como evidencia de PRE-AVAIL-001.

No se concluyó que `adb force-stop` eliminara la sesión.

La causa específica de ese comportamiento no fue investigada dentro de este experimento.

### 8.2 Ajuste del mensaje esperado

Inicialmente se esperaba observar:

```text
No fue posible cargar las actividades.
```

Sin embargo, durante una ejecución exploratoria con el backend detenido se observó:

```text
No fue posible actualizar los estados.
```

La inspección del flujo permitió comprobar que al ingresar a la pantalla de Actividades se ejecutaba una actualización de estados remotos.

Por esta razón, el protocolo fue ajustado para medir el comportamiento real del sistema AS-IS.

---

## 9. Protocolo final

Las corridas válidas siguieron el siguiente procedimiento:

1. iniciar Spring Boot;
2. verificar `/actuator/health` con estado `UP`;
3. comprobar que PostgreSQL estuviera activo y saludable;
4. abrir RachaPro con una sesión autenticada;
5. permanecer en Home;
6. detener únicamente Spring Boot;
7. verificar que el puerto `8080` ya no estuviera escuchando;
8. comprobar que PostgreSQL continuara saludable;
9. preparar el cronómetro;
10. tomar como `T0` el momento de pulsar `Actividades`;
11. esperar sin reiniciar la aplicación;
12. detener el cronómetro cuando apareciera `No fue posible actualizar los estados.`;
13. registrar el tiempo;
14. comprobar si existía crash;
15. comprobar si era posible navegar entre pantallas;
16. comprobar si existía falso éxito;
17. realizar observaciones secundarias sobre otras funcionalidades dependientes del backend;
18. restaurar Spring Boot antes de preparar la siguiente corrida.

La métrica temporal principal corresponde únicamente al intervalo entre `T0 = pulsar Actividades` y `T1 = aparición de "No fue posible actualizar los estados."`.

---

## 10. Resultados de las corridas

| Corrida | Tiempo | Loading visible | Mensaje observado | Crash | Navegable | Falso éxito | Cumple ≤ 300 s |
|---|---:|---|---|---|---|---|---|
| R1 | 11.99 s | No | `No fue posible actualizar los estados.` | No | Sí | No | Sí |
| R2 | 11.26 s | No | `No fue posible actualizar los estados.` | No | Sí | No | Sí |
| R3 | 10.80 s | No | `No fue posible actualizar los estados.` | No | Sí | No | Sí |

Las tres corridas válidas fueron realizadas con Spring Boot detenido, puerto `8080` sin escucha, PostgreSQL activo y saludable, aplicación abierta y sesión previamente autenticada.

---

## 11. Estadísticos observados

| Métrica | Resultado |
|---|---:|
| Mínimo | 10.80 s |
| Mediana | 11.26 s |
| Máximo | 11.99 s |
| Promedio | 11.35 s |
| Criterio máximo | 300 s |

El máximo observado fue `11.99 s`, por debajo del criterio de `300 s`.

---

## 12. Comportamiento de navegación

En las tres corridas la aplicación continuó permitiendo navegación entre pantallas.

No se observó cierre inesperado de la aplicación, salida involuntaria al sistema operativo, terminación visible del proceso ni imposibilidad total de navegar por la interfaz.

Por tanto, el comportamiento observado no se clasifica como crash.

La aplicación presentó degradación funcional en las operaciones dependientes del backend, pero continuó siendo navegable.

---

## 13. Degradación funcional observada

Con Spring Boot detenido se observaron fallos controlados en funcionalidades que requieren comunicación remota.

### 13.1 Actividades

Al ingresar a Actividades se observó:

```text
No fue posible actualizar los estados.
```

Al intentar crear una actividad con el backend detenido se observó:

```text
No fue posible crear la actividad.
```

No se observó una confirmación falsa indicando que la actividad hubiera sido creada correctamente.

### 13.2 Pomodoro

Durante las comprobaciones con el backend detenido, las operaciones de Pomodoro dependientes del backend también presentaron errores.

En una de las pruebas se encontraba una sesión Pomodoro activa.

Al intentar pausarla se observó el mensaje reportado manualmente:

```text
Ocurrio un error al pausar el Pomodoro
```

Estas observaciones son evidencia secundaria y no forman parte del cálculo temporal principal de PRE-AVAIL-001.

---

## 14. Falsos éxitos

No se observaron falsos éxitos en ninguna de las tres corridas.

Cuando las operaciones dependientes del backend no pudieron ejecutarse, la aplicación mostró mensajes de error en lugar de informar que habían sido completadas correctamente.

Esta conclusión se limita exclusivamente a las operaciones observadas durante PRE-AVAIL-001.

---

## 15. Control de recuperación

Después de las pruebas de indisponibilidad se volvió a iniciar Spring Boot y se verificó nuevamente estado `UP`.

Con el backend restaurado se observó que:

- Actividades volvió a cargar;
- fue posible crear actividades;
- Pomodoro volvió a funcionar;
- la aplicación continuó navegable;
- no se observó crash.

Durante una comprobación de recuperación se observó aproximadamente `1 s` de carga antes de disponer nuevamente de la funcionalidad.

Ese valor corresponde únicamente a una observación de recuperación.

No forma parte de las tres corridas de PRE-AVAIL-001 y no debe utilizarse como benchmark formal de rendimiento.

---

## 16. Resultado del escenario

**PRE-AVAIL-001: CUMPLE BAJO LAS CONDICIONES ENSAYADAS.**

Las tres corridas válidas informaron el fallo dentro del criterio definido:

```text
R1 = 11.99 s
R2 = 11.26 s
R3 = 10.80 s
```

El máximo observado fue `11.99 s` frente a un criterio máximo de `300 s`.

Durante las tres corridas:

- no se observaron crashes;
- la aplicación permaneció navegable;
- no se observaron falsos éxitos;
- las funcionalidades dependientes del backend presentaron degradación;
- los fallos observados fueron informados mediante mensajes de error.

---

## 17. Conclusión

La evidencia obtenida permite afirmar:

> En tres corridas realizadas en el entorno local ensayado, con Spring Boot detenido y PostgreSQL disponible, RachaPro informó la indisponibilidad del backend entre 10.80 s y 11.99 s. En las tres corridas se cumplió el criterio temporal de ≤ 5 minutos. La aplicación permaneció navegable y no se observaron crashes ni falsos éxitos en las operaciones comprobadas. Las funcionalidades dependientes del backend presentaron degradación funcional y mostraron mensajes de error controlados.

Este resultado corresponde exclusivamente a las condiciones probadas.

---

## 18. Alcance de la conclusión

La evidencia no permite afirmar que:

- RachaPro siempre detectará una caída en aproximadamente 11 segundos;
- el comportamiento será igual en todas las redes;
- el comportamiento será igual en todos los dispositivos;
- todos los tipos de indisponibilidad producirán el mismo resultado;
- todas las funcionalidades fueron verificadas ante indisponibilidad;
- PostgreSQL puede fallar sin afectar el comportamiento observado;
- una conexión lenta produciría los mismos tiempos;
- un servidor que acepte conexiones pero no responda produciría los mismos tiempos;
- RachaPro garantiza disponibilidad general;
- el tiempo observado corresponda a un timeout configurado explícitamente por RachaPro.

---

## 19. Limitaciones

PRE-AVAIL-001 presenta las siguientes limitaciones:

- tres corridas manuales;
- un dispositivo Android físico;
- entorno de desarrollo local;
- medición mediante cronómetro;
- indisponibilidad provocada deteniendo Spring Boot;
- PostgreSQL permaneció disponible;
- no se simuló una red lenta;
- no se simuló pérdida parcial de paquetes;
- no se simuló alta latencia de red;
- no se simuló un servidor conectado pero sin responder;
- no se probó indisponibilidad de PostgreSQL;
- no se probó indisponibilidad simultánea de varios componentes;
- no se instrumentaron timestamps internos de Retrofit;
- no se instrumentaron timestamps internos de OkHttp;
- no se modificaron timeouts;
- no se modificaron políticas de reintento.

---

## 20. Relación con Semana 8

Esta evidencia corresponde al estado AS-IS previo a modificaciones de Semana 8.

PRE-AVAIL-001 fue ejecutado antes de:

- modificar timeouts;
- modificar políticas de reintento;
- introducir cambios de modularización;
- modificar fronteras entre módulos;
- aplicar optimizaciones de rendimiento;
- introducir fitness functions arquitectónicas.

Por tanto, este documento puede utilizarse posteriormente como línea base para realizar comparaciones después de cambios técnicamente justificados.

La existencia de este baseline no implica que sea necesario modificar los timeouts.

Cualquier cambio posterior deberá estar sustentado en evidencia y en una decisión técnica explícita.

---

## 21. Estado final

| Elemento | Estado |
|---|---|
| Baseline de disponibilidad ejecutado | ✅ |
| Corridas válidas | 3 |
| R1 | 11.99 s |
| R2 | 11.26 s |
| R3 | 10.80 s |
| Mínimo | 10.80 s |
| Mediana | 11.26 s |
| Máximo | 11.99 s |
| Promedio | 11.35 s |
| Criterio ≤ 5 min cumplido | ✅ en las 3 corridas |
| Crash observado | No |
| Navegación preservada | Sí |
| Falso éxito observado | No |
| Degradación funcional con backend OFF | Sí |
| PostgreSQL detenido durante la prueba | No |
| Cambios de timeout realizados | No |
| Cambios de código durante el baseline | No |
| Resultado generalizable a cualquier entorno | No |
