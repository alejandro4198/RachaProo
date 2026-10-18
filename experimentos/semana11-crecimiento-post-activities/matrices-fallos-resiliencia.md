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
| F-01 | Backend Spring Boot | Indisponibilidad temporal del servicio | Backend disponible, base de datos disponible, aplicación Android abierta, usuario autenticado y creación remota previamente verificada | Después de comprobar la operación inicial, se detiene deliberadamente el backend y posteriormente se intenta crear una actividad desde Android | Intento de creación de una actividad desde Android | La aplicación no debe presentar la creación como confirmada remotamente si no existe confirmación del backend | Logcat, comportamiento visible de Android, error o respuesta de red, momento de detención del backend, identificación de la actividad intentada y comprobación posterior del estado remoto | Pendiente hasta completar las tres repeticiones |

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
| R-01 | F-01 | Backend disponible, usuario autenticado, aplicación Android abierta y operación remota funcional | La aplicación debe manejar la indisponibilidad sin presentar como confirmada remotamente una operación no confirmada por el backend | Después de restablecer el backend, Android debe recuperar la capacidad de ejecutar una nueva operación remota desde la misma sesión | Sin reiniciar Android ni volver a iniciar sesión, se restablece el backend y se crea una nueva actividad identificable. Existe recuperación si la nueva solicitud se completa correctamente y su resultado puede comprobarse tanto en Android como mediante evidencia remota | Logcat, respuesta de red, comportamiento visible de Android y comprobación remota de la actividad creada después de la recuperación | Pendiente hasta completar las tres repeticiones |

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

Pendiente de completar las tres repeticiones.

---

## 10. Identificación de las tres repeticiones

Se utilizará una nomenclatura diferenciable para cada repetición.

### Repetición 1

- Control preregistrado: `S11-F01-R1-CONTROL`
- Durante fallo preregistrado: `S11-F01-R1-DURANTE-FALLO`
- Recuperación preregistrada: `S11-R01-R1-RECUPERACION`

Durante la ejecución real de R1 se observó una desviación nominal en la actividad de recuperación:

`S11-F01-R1-RECUPERACION`

Esta diferencia se registra como una desviación respecto del identificador preregistrado y no se corrige retroactivamente en las secciones anteriores.

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

`EJECUTADA`

Fecha:

`2026-10-18`

Commit de referencia del preregistro:

`87de9d9`

Commit exacto utilizado durante la ejecución:

`NO CAPTURADO EN EL MOMENTO DE LA PRUEBA — PENDIENTE DE VERIFICACIÓN`

Dispositivo o emulador:

`DISPOSITIVO FÍSICO ANDROID`

Versión Android:

`NO CAPTURADA DURANTE LA EJECUCIÓN`

Conectividad:

`Android -> 192.168.1.20:8080`

Condición inicial verificada:

`SÍ`

Se verificó previamente que:

- Spring Boot estaba disponible;
- PostgreSQL estaba disponible;
- Android permanecía abierto;
- el usuario estaba autenticado;
- existía comunicación Android-backend;
- la creación remota funcionaba correctamente.

Actividad de control:

`S11-F01-R1-CONTROL`

Control confirmado remotamente:

`SÍ`

Resultado del control:

`HTTP 201`

ID observado en PostgreSQL:

`1500170`

La consulta a PostgreSQL confirmó la persistencia remota de la actividad de control.

Hora exacta de detención del backend:

`NO CAPTURADA`

Fallo inducido:

`Detención deliberada del proceso Spring Boot`

Comprobación de indisponibilidad:

`No existía listener activo en el puerto 8080 durante la fase de fallo`

Actividad intentada durante el fallo:

`S11-F01-R1-DURANTE-FALLO`

Comportamiento visible en Android:

`Android mostró el mensaje «No fue posible crear la actividad».`

Error o respuesta registrada:

`SocketTimeoutException aproximadamente después de 10 segundos`

Estado local observado:

`Android no presentó el intento como una creación remota confirmada exitosamente.`

Comprobación remota inicial:

`Después del intento realizado durante la caída, la consulta inicial a PostgreSQL no encontró un registro correspondiente a S11-F01-R1-DURANTE-FALLO.`

Resultado remoto posterior:

`Posteriormente apareció en PostgreSQL una actividad con ID 1500171 correspondiente a S11-F01-R1-DURANTE-FALLO.`

Interpretación de ese resultado:

`NO puede atribuirse al intento original realizado durante la caída.`

Después de restablecer Spring Boot se volvió a pulsar manualmente `Guardar` sobre la actividad `S11-F01-R1-DURANTE-FALLO`.

Por tanto, la persistencia posterior con ID `1500171` puede corresponder a ese segundo intento manual.

No existe evidencia suficiente en R1 para afirmar que:

- Android reintentó automáticamente la operación original;
- Retrofit reenvió automáticamente la solicitud;
- existía una cola local;
- existía sincronización diferida;
- el intento original durante la caída terminó persistido de manera automática.

Logs asociados:

`evidencia-fallos/R1/logcat-R1-privado.txt`

Estado del Logcat:

`EVIDENCIA LOCAL PRIVADA — NO VERSIONAR SIN SANEAMIENTO`

SHA-256 del Logcat original:

`586869C52B6513A4D0F05FC011729EC106109FADA6546D6D3EB640DC8146FB12`

Algoritmo:

`SHA256`

Conclusión experimental F-01/R1:

`Durante la indisponibilidad inducida de Spring Boot, Android no presentó el intento de creación como una operación remota confirmada. La interfaz mostró «No fue posible crear la actividad» y Logcat registró SocketTimeoutException aproximadamente después de 10 segundos. La comprobación inicial en PostgreSQL no encontró la actividad intentada durante la caída.`

La aparición posterior de `S11-F01-R1-DURANTE-FALLO` con ID `1500171` no puede utilizarse como evidencia de recuperación automática porque existió una segunda pulsación manual de `Guardar` después de restablecer Spring Boot.

Esta conclusión corresponde únicamente a R1.

---

### R-01

Estado:

`EJECUTADA CON DESVIACIONES DOCUMENTADAS`

Hora exacta de restablecimiento del backend:

`NO CAPTURADA`

Comprobación de restauración:

`El puerto 8080 volvió a estar disponible después de reiniciar Spring Boot.`

Aplicación reiniciada:

`NO`

Usuario volvió a autenticarse:

`NO`

Continuidad de sesión Android:

`SÍ — la aplicación permaneció abierta y se conservó la misma sesión durante la prueba`

Identificador preregistrado para recuperación:

`S11-R01-R1-RECUPERACION`

Identificador utilizado realmente durante R1:

`S11-F01-R1-RECUPERACION`

Estado de esta diferencia:

`DESVIACIÓN NOMINAL DOCUMENTADA`

No se modifica retroactivamente el identificador preregistrado.

Resultado visible en Android:

`La nueva creación posterior al restablecimiento del backend se completó correctamente.`

Respuesta HTTP observada:

`201`

Resultado remoto:

`La actividad de recuperación quedó persistida en PostgreSQL.`

ID remoto observado:

`1500172`

Título persistido observado:

`S11-F01-R1-RECUPERACION`

Criterio de recuperación satisfecho:

`SÍ PARA R1, respecto de una nueva operación remota`

La evidencia disponible muestra que:

- Android permaneció abierto;
- no se reinició la aplicación;
- no se volvió a autenticar al usuario;
- Spring Boot fue restablecido;
- se realizó una nueva creación;
- la nueva creación obtuvo HTTP 201;
- la actividad quedó persistida en PostgreSQL con ID `1500172`.

Por tanto, para R1 existe evidencia de que Android recuperó la capacidad de realizar una nueva operación remota después de restablecer Spring Boot.

---

### Desviaciones metodológicas registradas en R1

R1 contiene dos desviaciones respecto del procedimiento preregistrado.

#### Desviación 1 — segundo Guardar sobre DURANTE-FALLO

Después de restablecer Spring Boot se volvió a pulsar manualmente `Guardar` sobre:

`S11-F01-R1-DURANTE-FALLO`

Posteriormente esa actividad apareció en PostgreSQL con ID:

`1500171`

Esta intervención impide determinar si el intento original realizado durante la caída habría sido reenviado o persistido automáticamente.

Por tanto:

`La persistencia de 1500171 se atribuye únicamente a una operación observada después de una intervención manual adicional, no a recuperación automática del intento original.`

#### Desviación 2 — identificador de recuperación diferente al preregistrado

El identificador preregistrado fue:

`S11-R01-R1-RECUPERACION`

El identificador utilizado realmente fue:

`S11-F01-R1-RECUPERACION`

La actividad real quedó persistida con ID:

`1500172`

La diferencia se conserva como desviación nominal.

No se corrige silenciosamente el preregistro.

---

### Validez de R1

F-01/R1:

`VÁLIDA para observar el comportamiento durante la indisponibilidad.`

Se observó:

- indisponibilidad del backend;
- ausencia de listener en 8080;
- mensaje de error en Android;
- `SocketTimeoutException`;
- ausencia inicial de persistencia remota.

R-01/R1:

`VÁLIDA para comprobar recuperación de una nueva operación después de restablecer el backend.`

Se observó:

- continuidad de la aplicación;
- continuidad de sesión;
- backend nuevamente disponible;
- HTTP 201;
- persistencia remota con ID `1500172`.

Destino final del intento DURANTE-FALLO:

`NO CONCLUYENTE respecto de recuperación automática debido al segundo Guardar manual.`

La repetición R1 no se descarta.

La desviación se conserva explícitamente para evaluar posteriormente su comparabilidad con R2 y R3.

---

### Conclusión experimental de R1

La primera repetición aporta evidencia de que:

1. al detener Spring Boot, Android no presentó la creación como confirmada exitosamente;
2. Android informó que no fue posible crear la actividad;
3. se registró `SocketTimeoutException` aproximadamente después de 10 segundos;
4. la consulta inicial a PostgreSQL no encontró la actividad intentada durante la caída;
5. después de restablecer Spring Boot, Android permaneció en la misma sesión;
6. una nueva creación posterior obtuvo HTTP 201;
7. la nueva actividad quedó persistida con ID `1500172`.

R1 no permite concluir si una operación fallida puede recuperarse automáticamente después de volver a levantar el backend, porque se realizó manualmente un segundo intento sobre `S11-F01-R1-DURANTE-FALLO`.

R2 y R3 deberán ejecutarse sin repetir esa intervención.

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

`DISPOSITIVO FÍSICO ANDROID`

Versión Android:

`PENDIENTE DE REGISTRAR`

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

`DISPOSITIVO FÍSICO ANDROID`

Versión Android:

`PENDIENTE DE REGISTRAR`

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

Las siguientes afirmaciones corresponden a expectativas definidas antes de ejecutar las pruebas:

- Android no debería presentar una operación remota como confirmada si el backend no respondió.
- Una operación mostrada localmente no debe asumirse automáticamente como persistida remotamente.
- Android debería recuperar la capacidad de comunicarse con el backend después de que el servicio sea restablecido.
- La recuperación debería poder comprobarse sin reiniciar la aplicación.
- El estado remoto debe comprobarse para distinguir entre intento, visualización local y persistencia real.

La ejecución de R1 aporta una primera observación sobre estas expectativas, pero no permite todavía establecer reproducibilidad.

Las conclusiones consolidadas del escenario deberán esperar la ejecución de R2 y R3.

---

## 14. Criterio de reproducibilidad

El escenario se considerará reproducido consistentemente si las tres repeticiones cumplen condiciones iniciales equivalentes y producen el mismo comportamiento observable relevante.

Si los resultados difieren entre repeticiones, la divergencia deberá registrarse.

No se descartará una repetición únicamente porque contradiga las otras.

Cualquier repetición invalidada metodológicamente deberá justificar:

- qué condición dejó de cumplirse;
- qué evidencia demuestra la desviación;
- por qué no puede compararse directamente con las demás.

R1 contiene desviaciones documentadas, pero no se invalida completamente.

La comparabilidad final de R1 con R2 y R3 deberá evaluarse después de terminar las tres ejecuciones.

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

R1 presenta además una limitación específica:

`el intento DURANTE-FALLO fue guardado manualmente una segunda vez después de restablecer Spring Boot.`

Esta intervención impide determinar mediante R1 si el intento original habría sido recuperado automáticamente.

---

## 16. Entorno de ejecución

Las pruebas F-01/R-01 se están ejecutando utilizando:

`DISPOSITIVO FÍSICO ANDROID`

El dispositivo fue conectado al computador mediante USB para permitir la captura de evidencia mediante ADB y Logcat.

ADB reconoció correctamente el dispositivo con estado:

`device`

La aplicación Android se ejecutó en el celular físico.

La comunicación utilizada durante R1 fue:

`Android -> 192.168.1.20:8080 -> Spring Boot`

Spring Boot se ejecutó en el computador de pruebas.

PostgreSQL permaneció disponible durante el escenario de indisponibilidad del backend.

La utilización de un dispositivo físico implica:

- utilizar una dirección de red del computador alcanzable desde Android;
- comprobar que Spring Boot escuche en una interfaz accesible;
- mantener PC y celular con conectividad compatible;
- capturar Logcat mediante ADB;
- preservar evidencia de una ejecución realizada en un dispositivo Android real.

Versión exacta de Android utilizada en R1:

`NO CAPTURADA DURANTE LA EJECUCIÓN`

Esta información no se inferirá retroactivamente sin una comprobación verificable.

R2 y R3 deberán realizarse utilizando el mismo dispositivo y condiciones equivalentes en la medida de lo técnicamente posible.

---

## 17. Relación con Semana 11

Esta actividad corresponde al tercer bloque de Semana 11:

- crecimiento de carga: completado;
- registro de degradación: completado;
- matriz de fallos y resiliencia: preregistrada y en ejecución;
- F-01/R-01 R1: ejecutada y documentada con desviaciones;
- F-01/R-01 R2: pendiente;
- F-01/R-01 R3: pendiente;
- táctica y comparación antes/después: pendiente.

R1 aporta evidencia inicial sobre el comportamiento de RachaPro ante la indisponibilidad temporal de Spring Boot y su posterior restablecimiento.

No se considera todavía completada la actividad de matrices de fallos y resiliencia porque faltan R2 y R3.

La reproducibilidad del comportamiento no puede establecerse hasta terminar las tres repeticiones.

La selección de una táctica arquitectónica se realizará después de analizar el conjunto completo de resultados.

No se seleccionará una táctica únicamente a partir de R1 ni de una expectativa no medida.

---

## 18. Secuencia temporal

La secuencia metodológica definida fue:

`definición del escenario -> commit previo -> ejecución -> evidencia -> resultados -> análisis -> decisión de táctica`

La definición de F-01 y R-01 fue registrada previamente en Git mediante:

`87de9d9`

Posteriormente se ejecutó R1.

El registro de los resultados de R1 se realizará en un commit distinto y posterior al preregistro.

Esto permite demostrar que:

- las expectativas existían antes de observar el comportamiento;
- los criterios verificables no fueron definidos después de conocer R1;
- las desviaciones de R1 se registran explícitamente;
- la evidencia experimental permanece separada de la definición previa;
- la futura decisión de táctica deberá apoyarse en las observaciones completas.

---

## 19. Próximo paso

R1 ya fue ejecutada.

Antes de ejecutar R2 se debe:

1. guardar este registro actualizado de R1;
2. verificar que las expectativas y criterios preregistrados no fueron modificados retroactivamente;
3. conservar `logcat-R1-privado.txt` únicamente como evidencia local privada;
4. conservar su SHA-256:
   `586869C52B6513A4D0F05FC011729EC106109FADA6546D6D3EB640DC8146FB12`;
5. no incorporar el Logcat privado a Git sin un proceso previo de saneamiento;
6. registrar este documento actualizado en un commit posterior al preregistro;
7. mantener el mismo dispositivo físico para R2 cuando sea posible;
8. comprobar nuevamente la condición inicial;
9. crear `S11-F01-R2-CONTROL`;
10. comprobar su persistencia remota;
11. detener Spring Boot;
12. comprobar que el puerto 8080 dejó de atender;
13. intentar `S11-F01-R2-DURANTE-FALLO`;
14. registrar el comportamiento de Android y Logcat;
15. restablecer Spring Boot;
16. **no volver a pulsar Guardar sobre `S11-F01-R2-DURANTE-FALLO`**;
17. comprobar directamente en PostgreSQL si el intento original existe o no;
18. crear después una nueva actividad:
    `S11-R01-R2-RECUPERACION`;
19. comprobar el resultado tanto en Android como en PostgreSQL;
20. conservar cualquier desviación observada sin descartarla.

R2 debe evitar el segundo intento manual realizado en R1.

Esto permitirá distinguir con mayor claridad entre:

- fallo de la operación original;
- posible comportamiento automático después de la recuperación;
- recuperación del cliente para una nueva operación.
