# Semana 11 — Matrices de fallos y resiliencia

## 1. Propósito

Este documento registra escenarios controlados de fallo utilizados para observar el comportamiento de RachaPro ante condiciones de indisponibilidad y recuperación.

Las expectativas definidas antes de la ejecución se mantienen separadas de los resultados realmente observados.

Una respuesta esperada no se considera evidencia de que el comportamiento esté implementado.

El objetivo de esta actividad es:

- definir escenarios reproducibles de fallo;
- establecer condiciones iniciales controladas;
- definir criterios verificables antes de ejecutar las pruebas;
- registrar evidencia durante la ejecución;
- distinguir entre intento de operación y operación confirmada;
- observar el comportamiento real del sistema;
- analizar posteriormente si existe una respuesta de resiliencia;
- evitar atribuir capacidades al sistema que no hayan sido observadas.

Los resultados se completarán únicamente después de ejecutar cada escenario.

---

## 2. Alcance

La primera prueba se concentrará en la indisponibilidad temporal del backend Spring Boot y en el comportamiento de la aplicación Android antes, durante y después de dicha indisponibilidad.

El escenario no pretende evaluar todavía todas las posibles fallas de RachaPro.

En esta primera iteración se busca responder:

1. qué ocurre en Android cuando el backend deja de estar disponible;
2. si la aplicación evita presentar como confirmada una operación que no fue confirmada por el backend;
3. qué ocurre con el intento de creación realizado durante la indisponibilidad;
4. si existe evidencia de que dicho intento llegó o no al backend;
5. si Android puede recuperar la capacidad de operar después de restablecer el backend;
6. si la recuperación ocurre sin reiniciar la aplicación ni volver a autenticarse.

No se asumirá previamente que existe un mecanismo de fallback, reintento, caché, cola local o recuperación automática.

---

## 3. Condiciones controladas

Durante la ejecución se intentará mantener constantes, en la medida en que sea técnicamente posible:

- aplicación Android utilizada;
- usuario experimental;
- sesión inicialmente autenticada;
- dispositivo o emulador;
- conectividad de red;
- configuración del frontend;
- versión del backend;
- versión de la base de datos;
- datos de prueba utilizados;
- operación funcional utilizada como referencia.

El cambio inducido deliberadamente será la disponibilidad del proceso Spring Boot.

Sin embargo, no se asumirá que detener Spring Boot modifica únicamente una propiedad interna del sistema.

La detención del backend puede afectar, entre otros aspectos:

- conexiones activas;
- recursos asociados al proceso;
- estado mantenido en memoria;
- conexiones hacia PostgreSQL;
- comportamiento de clientes HTTP;
- estado temporal asociado a solicitudes en curso.

Por esta razón, el escenario se describe como una **intervención controlada sobre la disponibilidad del backend**, y no como una modificación aislada de una única variable interna.

---

## 4. Repeticiones previstas

El escenario F-01/R-01 se ejecutará inicialmente en:

`3 repeticiones independientes`

Cada repetición deberá comenzar desde una condición inicial equivalente.

El objetivo de realizar tres repeticiones es observar si el comportamiento se reproduce bajo las mismas condiciones controladas.

Las tres repeticiones no convierten automáticamente el resultado en una conclusión universal.

Si alguna repetición presenta una condición metodológica diferente, deberá registrarse explícitamente y evaluarse antes de combinarla con las demás.

No se añadirán ni eliminarán repeticiones después de observar los resultados con el propósito de favorecer una conclusión.

---

## 5. Principios de ejecución

Cada repetición debe distinguir tres momentos:

### 5.1 Antes del fallo

Se confirma que:

- el backend se encuentra disponible;
- la base de datos requerida está disponible;
- la aplicación Android está operativa;
- el usuario está autenticado;
- existe conectividad entre Android y backend;
- la operación seleccionada funciona normalmente;
- la operación realizada como control puede comprobarse también en el backend o en la persistencia correspondiente.

Este momento establece la condición inicial de referencia.

### 5.2 Durante el fallo

Se induce el fallo definido y se ejecuta nuevamente la operación seleccionada.

Se registran:

- comportamiento visible en Android;
- mensajes mostrados al usuario;
- respuesta o error de red;
- logs disponibles;
- momento de inicio del fallo;
- momento de ejecución de la operación;
- estado observable en Android;
- evidencia disponible sobre si la operación llegó o no al backend;
- evidencia disponible sobre si hubo o no persistencia remota.

### 5.3 Después del fallo

Se restablece el componente afectado.

La aplicación Android debe permanecer abierta y en la misma sesión.

La misma operación se ejecuta nuevamente para comprobar si el cliente recuperó su capacidad de comunicarse con el backend.

Después de restablecer el servicio también se comprobará el estado remoto para distinguir entre:

- intento de operación;
- operación mostrada localmente;
- operación recibida por el backend;
- operación efectivamente persistida.

Reiniciar la aplicación o volver a autenticarse antes de esta comprobación impediría distinguir entre recuperación del cliente y reconstrucción completa de la sesión.

---

## 6. Matriz de fallos

| ID | Componente | Fallo inducido | Condición inicial | Condición desencadenante | Operación de prueba | Efecto esperado | Evidencia requerida | Resultado observado |
|---|---|---|---|---|---|---|---|---|
| F-01 | Backend Spring Boot | Indisponibilidad temporal del servicio | Backend disponible, base de datos disponible, aplicación Android abierta, usuario autenticado y creación remota previamente verificada | Después de comprobar la operación inicial, se detiene deliberadamente el backend y posteriormente se intenta crear una actividad desde Android | Intento de creación de una actividad desde Android | La aplicación no debe presentar la creación como confirmada remotamente si no existe confirmación del backend | Logcat, comportamiento visible de Android, error o respuesta de red, momento de detención del backend, identificación de la actividad intentada y comprobación posterior del estado remoto | Pendiente |

---

## 7. Detalle del escenario F-01

### 7.1 Objetivo

Observar el comportamiento de Android cuando el backend Spring Boot deja de estar disponible temporalmente durante una operación remota.

### 7.2 Condición inicial

Antes de inducir el fallo deben cumplirse todas las condiciones siguientes:

- backend Spring Boot iniciado;
- base de datos disponible;
- aplicación Android abierta;
- usuario autenticado;
- sesión activa;
- comunicación Android-backend funcional;
- creación de actividad verificada bajo condiciones normales;
- posibilidad de comprobar posteriormente si una actividad fue recibida o persistida remotamente.

### 7.3 Control previo

Antes de detener el backend se realizará una creación de actividad de control.

La actividad deberá tener un identificador reconocible para la prueba.

Ejemplo de nomenclatura:

`S11-F01-R1-CONTROL`

La creación de control deberá comprobarse tanto desde Android como desde la evidencia remota disponible.

Este paso permite confirmar que la ruta funcional opera correctamente antes de inducir el fallo.

### 7.4 Inyección del fallo

El fallo se inducirá deteniendo deliberadamente el backend Spring Boot.

La base de datos no será detenida de forma intencional en esta prueba.

No se modificarán deliberadamente:

- credenciales;
- token del usuario;
- configuración de red;
- contenido de la base de datos;
- código de Android;
- código del backend.

La intervención controlada será la detención del proceso Spring Boot.

Se reconoce que esta intervención puede producir efectos secundarios internos asociados al proceso detenido.

### 7.5 Acción durante el fallo

Con la aplicación Android todavía abierta y el usuario en la misma sesión:

1. detener el backend;
2. comprobar que el proceso dejó de atender solicitudes;
3. registrar la hora aproximada de la interrupción;
4. intentar crear una actividad desde Android;
5. utilizar un nombre identificable para ese intento;
6. observar la respuesta visible;
7. registrar Logcat;
8. registrar el error de red disponible;
9. comprobar si Android presenta la operación como completada;
10. no asumir todavía que el estado mostrado localmente representa persistencia remota.

Ejemplo de identificación:

`S11-F01-R1-DURANTE-FALLO`

### 7.6 Comprobación remota posterior

Después de restablecer el backend, se deberá comprobar si la actividad intentada durante la indisponibilidad existe remotamente.

Esta comprobación es necesaria para distinguir entre:

- una actividad únicamente mostrada en Android;
- una operación retenida localmente;
- una operación enviada posteriormente;
- una operación nunca recibida por el backend;
- una operación efectivamente persistida.

La comprobación podrá realizarse mediante un mecanismo verificable disponible en el proyecto, por ejemplo:

- consulta posterior desde la aplicación;
- respuesta del endpoint correspondiente;
- consulta controlada a la persistencia;
- logs del backend;
- otra evidencia equivalente.

El mecanismo finalmente utilizado deberá registrarse en la evidencia de la ejecución.

### 7.7 Efecto esperado

La expectativa previa al experimento es:

> Si el backend no confirma la creación, Android no debe presentar la operación como una creación remota confirmada exitosamente.

Esta expectativa no implica que actualmente exista un mecanismo concreto de:

- fallback;
- almacenamiento local;
- sincronización diferida;
- reintento;
- cola;
- recuperación automática.

El comportamiento real debe determinarse mediante la ejecución.

---

## 8. Matriz de resiliencia

| ID | Fallo relacionado | Estado previo | Respuesta esperada durante el fallo | Recuperación esperada | Criterio verificable | Evidencia requerida | Resultado observado |
|---|---|---|---|---|---|---|---|
| R-01 | F-01 | Backend disponible, usuario autenticado, aplicación Android abierta y operación remota funcional | La aplicación debe manejar la indisponibilidad sin presentar como confirmada remotamente una operación no confirmada por el backend | Después de restablecer el backend, Android debe recuperar la capacidad de ejecutar una nueva operación remota desde la misma sesión | Sin reiniciar Android ni volver a iniciar sesión, se restablece el backend y se crea una nueva actividad identificable. Existe recuperación si la nueva solicitud se completa correctamente y su resultado puede comprobarse tanto en Android como mediante evidencia remota | Logcat, respuesta de red, comportamiento visible de Android y comprobación remota de la actividad creada después de la recuperación | Pendiente |

---

## 9. Detalle del escenario R-01

### 9.1 Objetivo

Comprobar si Android recupera la capacidad de operar con el backend después de una indisponibilidad temporal.

El objetivo no es comprobar únicamente que el backend vuelva a iniciar.

Se busca comprobar que el cliente Android vuelva a realizar correctamente una nueva operación remota desde la sesión que estaba activa antes del fallo.

### 9.2 Condición de recuperación

Después de ejecutar F-01:

1. mantener abierta la aplicación Android;
2. no cerrar sesión;
3. no reiniciar la aplicación;
4. restablecer el backend Spring Boot;
5. comprobar que el backend está nuevamente disponible;
6. ejecutar una nueva creación de actividad;
7. utilizar un identificador diferente al intento realizado durante el fallo;
8. observar el resultado en Android;
9. comprobar remotamente que la nueva actividad fue recibida y persistida.

Ejemplo de identificación:

`S11-R01-R1-RECUPERACION`

### 9.3 Criterio verificable

Se considerará que existe recuperación operativa para este escenario únicamente si:

- Android permanece en la misma sesión;
- no se reinicia la aplicación;
- no se vuelve a autenticar al usuario;
- el backend es restablecido;
- se ejecuta una nueva creación;
- la solicitud se completa correctamente;
- Android muestra un estado coherente con el resultado;
- existe evidencia remota de que la nueva actividad fue recibida y/o persistida según el flujo normal del sistema.

El simple hecho de que el backend vuelva a iniciar no constituye evidencia suficiente de recuperación de Android.

### 9.4 Distinción respecto de la operación ejecutada durante el fallo

La operación utilizada para comprobar recuperación debe ser una nueva operación.

No debe confundirse con el intento realizado durante la indisponibilidad.

Por tanto, en cada repetición deben existir al menos tres identificadores diferenciables:

- operación de control previa;
- operación intentada durante el fallo;
- operación posterior a la recuperación.

Ejemplo para la repetición 1:

- `S11-F01-R1-CONTROL`
- `S11-F01-R1-DURANTE-FALLO`
- `S11-R01-R1-RECUPERACION`

### 9.5 Resultado

Pendiente de ejecución.

---

## 10. Identificación de las tres repeticiones

Se utilizará una nomenclatura diferenciable para cada repetición.

### Repetición 1

- Control: `S11-F01-R1-CONTROL`
- Durante fallo: `S11-F01-R1-DURANTE-FALLO`
- Recuperación: `S11-R01-R1-RECUPERACION`

### Repetición 2

- Control: `S11-F01-R2-CONTROL`
- Durante fallo: `S11-F01-R2-DURANTE-FALLO`
- Recuperación: `S11-R01-R2-RECUPERACION`

### Repetición 3

- Control: `S11-F01-R3-CONTROL`
- Durante fallo: `S11-F01-R3-DURANTE-FALLO`
- Recuperación: `S11-R01-R3-RECUPERACION`

Esta nomenclatura permite rastrear cada operación sin depender únicamente de timestamps.

---

## 11. Evidencia a recolectar

Para cada repetición de F-01 y R-01 se deberá conservar, cuando sea posible:

- fecha y hora de ejecución;
- commit del repositorio;
- dispositivo físico o emulador utilizado;
- versión de Android;
- usuario experimental utilizado;
- estado inicial del backend;
- estado inicial de la base de datos;
- identificación de la operación de control;
- evidencia de la operación de control;
- momento en que se detiene el backend;
- identificación de la operación intentada durante el fallo;
- momento en que se ejecuta la operación durante el fallo;
- mensaje visible en Android;
- Logcat relacionado con la operación;
- respuesta o error de Retrofit;
- comportamiento del estado local;
- momento en que se restablece el backend;
- comprobación remota de la operación intentada durante el fallo;
- identificación de la operación posterior a la recuperación;
- resultado de repetir una nueva operación;
- comprobación remota de la operación posterior a la recuperación;
- evidencia de que la aplicación no fue reiniciada;
- evidencia de que la sesión no fue reemplazada.

La evidencia debe conservarse sin incluir:

- contraseñas;
- JWT completos;
- encabezados Authorization;
- secretos;
- credenciales de base de datos;
- información sensible innecesaria.

---

## 12. Registro de ejecución

## 12.1 Repetición 1

### F-01

Estado:

`PENDIENTE DE EJECUCIÓN`

Fecha:

`PENDIENTE`

Commit:

`PENDIENTE`

Dispositivo o emulador:

`PENDIENTE`

Versión Android:

`PENDIENTE`

Condición inicial verificada:

`PENDIENTE`

Actividad de control:

`S11-F01-R1-CONTROL`

Control confirmado remotamente:

`PENDIENTE`

Hora de detención del backend:

`PENDIENTE`

Actividad intentada durante el fallo:

`S11-F01-R1-DURANTE-FALLO`

Comportamiento visible en Android:

`PENDIENTE`

Error o respuesta registrada:

`PENDIENTE`

Estado local observado:

`PENDIENTE`

Resultado remoto posterior del intento:

`PENDIENTE`

Logs asociados:

`PENDIENTE`

Conclusión experimental:

`PENDIENTE`

### R-01

Estado:

`PENDIENTE DE EJECUCIÓN`

Hora de restablecimiento del backend:

`PENDIENTE`

Aplicación reiniciada:

`NO DEBE REINICIARSE`

Usuario volvió a autenticarse:

`NO DEBE VOLVER A AUTENTICARSE`

Actividad de recuperación:

`S11-R01-R1-RECUPERACION`

Resultado visible en Android:

`PENDIENTE`

Resultado remoto:

`PENDIENTE`

Logs asociados:

`PENDIENTE`

Criterio de recuperación satisfecho:

`PENDIENTE`

Conclusión experimental:

`PENDIENTE`

---

## 12.2 Repetición 2

### F-01

Estado:

`PENDIENTE DE EJECUCIÓN`

Fecha:

`PENDIENTE`

Commit:

`PENDIENTE`

Dispositivo o emulador:

`PENDIENTE`

Versión Android:

`PENDIENTE`

Condición inicial verificada:

`PENDIENTE`

Actividad de control:

`S11-F01-R2-CONTROL`

Control confirmado remotamente:

`PENDIENTE`

Hora de detención del backend:

`PENDIENTE`

Actividad intentada durante el fallo:

`S11-F01-R2-DURANTE-FALLO`

Comportamiento visible en Android:

`PENDIENTE`

Error o respuesta registrada:

`PENDIENTE`

Estado local observado:

`PENDIENTE`

Resultado remoto posterior del intento:

`PENDIENTE`

Logs asociados:

`PENDIENTE`

Conclusión experimental:

`PENDIENTE`

### R-01

Estado:

`PENDIENTE DE EJECUCIÓN`

Hora de restablecimiento del backend:

`PENDIENTE`

Aplicación reiniciada:

`NO DEBE REINICIARSE`

Usuario volvió a autenticarse:

`NO DEBE VOLVER A AUTENTICARSE`

Actividad de recuperación:

`S11-R01-R2-RECUPERACION`

Resultado visible en Android:

`PENDIENTE`

Resultado remoto:

`PENDIENTE`

Logs asociados:

`PENDIENTE`

Criterio de recuperación satisfecho:

`PENDIENTE`

Conclusión experimental:

`PENDIENTE`

---

## 12.3 Repetición 3

### F-01

Estado:

`PENDIENTE DE EJECUCIÓN`

Fecha:

`PENDIENTE`

Commit:

`PENDIENTE`

Dispositivo o emulador:

`PENDIENTE`

Versión Android:

`PENDIENTE`

Condición inicial verificada:

`PENDIENTE`

Actividad de control:

`S11-F01-R3-CONTROL`

Control confirmado remotamente:

`PENDIENTE`

Hora de detención del backend:

`PENDIENTE`

Actividad intentada durante el fallo:

`S11-F01-R3-DURANTE-FALLO`

Comportamiento visible en Android:

`PENDIENTE`

Error o respuesta registrada:

`PENDIENTE`

Estado local observado:

`PENDIENTE`

Resultado remoto posterior del intento:

`PENDIENTE`

Logs asociados:

`PENDIENTE`

Conclusión experimental:

`PENDIENTE`

### R-01

Estado:

`PENDIENTE DE EJECUCIÓN`

Hora de restablecimiento del backend:

`PENDIENTE`

Aplicación reiniciada:

`NO DEBE REINICIARSE`

Usuario volvió a autenticarse:

`NO DEBE VOLVER A AUTENTICARSE`

Actividad de recuperación:

`S11-R01-R3-RECUPERACION`

Resultado visible en Android:

`PENDIENTE`

Resultado remoto:

`PENDIENTE`

Logs asociados:

`PENDIENTE`

Criterio de recuperación satisfecho:

`PENDIENTE`

Conclusión experimental:

`PENDIENTE`

---

## 13. Separación entre expectativa y observación

Las siguientes afirmaciones son expectativas previas y no resultados:

- Android no debería presentar una operación remota como confirmada si el backend no respondió.
- Una operación mostrada localmente no debe asumirse automáticamente como persistida remotamente.
- Android debería recuperar la capacidad de comunicarse con el backend después de que el servicio sea restablecido.
- La recuperación debería poder comprobarse sin reiniciar la aplicación.
- El estado remoto debe comprobarse para distinguir entre intento, visualización local y persistencia real.

Estas afirmaciones solo podrán convertirse en resultados después de ejecutar las tres repeticiones de F-01 y R-01.

---

## 14. Criterio de reproducibilidad

El escenario se considerará reproducido consistentemente si las tres repeticiones cumplen condiciones iniciales equivalentes y producen el mismo comportamiento observable relevante.

Si los resultados difieren entre repeticiones, la divergencia deberá registrarse.

No se descartará una repetición únicamente porque contradiga las otras.

Cualquier repetición invalidada metodológicamente deberá justificar:

- qué condición dejó de cumplirse;
- qué evidencia demuestra la desviación;
- por qué no puede compararse directamente con las demás.

---

## 15. Limitaciones del escenario

Este escenario evalúa únicamente una indisponibilidad temporal inducida del backend Spring Boot.

No evalúa todavía:

- caída de PostgreSQL;
- pérdida parcial de conectividad;
- latencia extrema;
- respuestas HTTP 5xx deliberadas;
- timeouts controlados;
- agotamiento del pool de conexiones;
- fallos de autenticación;
- expiración del JWT;
- reinicio completo del dispositivo;
- comportamiento offline prolongado;
- concurrencia durante la recuperación;
- pérdida de red del dispositivo;
- recuperación después de cierre forzado de Android.

El escenario tampoco demuestra por sí mismo disponibilidad global del sistema.

Los resultados deben interpretarse únicamente dentro de las condiciones reproducidas.

---

## 16. Entorno de ejecución

Antes de ejecutar las pruebas deberá registrarse cuál de los siguientes entornos Android se utilizará:

- emulador Android;
- dispositivo físico.

Estado actual:

`PENDIENTE DE DEFINIR`

La elección del dispositivo no modifica el escenario F-01/R-01, pero sí determina:

- cómo se comprueba la conectividad;
- cómo se captura Logcat;
- cómo se identifica el backend desde Android;
- cómo se preserva evidencia de red;
- qué configuración de host o IP debe utilizarse.

Esta información debe completarse antes de iniciar la primera repetición.

---

## 17. Relación con Semana 11

Esta actividad corresponde al tercer bloque de Semana 11:

- crecimiento de carga: completado;
- registro de degradación: completado;
- matriz de fallos y resiliencia: preregistrada, pendiente de ejecución;
- táctica y comparación antes/después: pendiente.

La selección de una táctica arquitectónica se realizará después de observar los resultados de los escenarios de fallo y recuperación.

No se seleccionará una táctica únicamente a partir de una expectativa no medida.

---

## 18. Secuencia temporal prevista

La secuencia metodológica será:

`definición del escenario -> commit previo -> ejecución -> evidencia -> resultados -> análisis -> decisión de táctica`

La definición de F-01 y R-01 debe quedar registrada en Git antes de inducir el fallo.

Los resultados deberán registrarse en un commit posterior.

Esto permite demostrar que:

- las expectativas existían antes de observar el comportamiento;
- los criterios verificables no fueron modificados post hoc;
- la evidencia experimental puede distinguirse de la definición previa;
- la futura decisión de táctica se apoya en observaciones posteriores.

---

## 19. Próximo paso

Antes de ejecutar F-01:

1. guardar este documento;
2. verificar que no contiene resultados observados;
3. verificar que no contiene secretos;
4. registrar el archivo en Git;
5. definir si la prueba se ejecutará en emulador o dispositivo físico;
6. preparar la captura de evidencia;
7. ejecutar posteriormente las tres repeticiones.

No se deben completar los campos de resultado antes de realizar las pruebas.