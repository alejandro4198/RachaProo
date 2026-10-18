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

Estado posterior a la ejecución:

`LAS TRES REPETICIONES PREVISTAS FUERON EJECUTADAS`

No se añadió una cuarta repetición de manera post hoc.

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
| F-01 | Backend Spring Boot | Indisponibilidad temporal del servicio | Backend disponible, base de datos disponible, aplicación Android abierta, usuario autenticado y creación remota previamente verificada | Después de comprobar la operación inicial, se detiene deliberadamente el backend y posteriormente se intenta crear una actividad desde Android | Intento de creación de una actividad desde Android | La aplicación no debe presentar la creación como confirmada remotamente si no existe confirmación del backend | Logcat, comportamiento visible de Android, error o respuesta de red, momento de detención del backend, identificación de la actividad intentada y comprobación posterior del estado remoto | Ejecutado en R1, R2 y R3. En las tres repeticiones la operación intentada durante la indisponibilidad no fue presentada como creación remota confirmada. Se observaron errores `SocketTimeoutException`. El destino automático de las solicitudes originales no puede determinarse de forma concluyente debido a las desviaciones de R1/R3 y a la ambigüedad de trazabilidad posterior de R2. |

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
| R-01 | F-01 | Backend disponible, usuario autenticado, aplicación Android abierta y operación remota funcional | La aplicación debe manejar la indisponibilidad sin presentar como confirmada remotamente una operación no confirmada por el backend | Después de restablecer el backend, Android debe recuperar la capacidad de ejecutar una nueva operación remota desde la misma sesión | Sin reiniciar Android ni volver a iniciar sesión, se restablece el backend y se crea una nueva actividad identificable. Existe recuperación si la nueva solicitud se completa correctamente y su resultado puede comprobarse tanto en Android como mediante evidencia remota | Logcat, respuesta de red, comportamiento visible de Android y comprobación remota de la actividad creada después de la recuperación | Ejecutado en R1, R2 y R3. En las tres repeticiones RachaPro permaneció abierta, no se reinició la aplicación y no se volvió a autenticar al usuario. Después del restablecimiento se comprobó persistencia de una nueva actividad de recuperación. Las desviaciones y ambigüedades de trazabilidad se documentan individualmente. |

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

Las tres repeticiones previstas fueron ejecutadas.

En R1, R2 y R3 se mantuvo RachaPro abierta y se conservó la misma sesión autenticada durante la indisponibilidad y el restablecimiento del backend.

En las tres repeticiones existe evidencia de que una nueva actividad pudo persistirse después de restablecer Spring Boot.

Este resultado permite observar recuperación operativa para nuevas operaciones dentro de las condiciones reproducidas.

No demuestra recuperación automática de la solicitud original que falló durante la indisponibilidad.

---

## 10. Identificación de las tres repeticiones

Se utilizó una nomenclatura diferenciable para cada repetición.

### Repetición 1

Prerregistro:

- Control: `S11-F01-R1-CONTROL`
- Durante fallo: `S11-F01-R1-DURANTE-FALLO`
- Recuperación: `S11-R01-R1-RECUPERACION`

Ejecución observada:

- Control: `S11-F01-R1-CONTROL`
- Durante fallo: `S11-F01-R1-DURANTE-FALLO`
- Recuperación utilizada: `S11-F01-R1-RECUPERACION`

Existe una desviación nominal entre el identificador preregistrado para recuperación y el utilizado durante la ejecución.

### Repetición 2

Prerregistro:

- Control: `S11-F01-R2-CONTROL`
- Durante fallo: `S11-F01-R2-DURANTE-FALLO`
- Recuperación: `S11-R01-R2-RECUPERACION`

Ejecución observada:

- Control: `S11-F01-R2-CONTROL`
- Durante fallo: `S11-F01-R2-DURANTE-FALLO`
- Recuperación utilizada: `S11-F01-R2-RECUPERACION`

Existe una desviación nominal entre el identificador preregistrado para recuperación y el utilizado durante la ejecución.

### Repetición 3

Prerregistro:

- Control: `S11-F01-R3-CONTROL`
- Durante fallo: `S11-F01-R3-DURANTE-FALLO`
- Recuperación: `S11-R01-R3-RECUPERACION`

Ejecución observada:

- Control: `S11-F01-R3-CONTROL`
- Durante fallo: `S11-F01-R3-DURANTE-FALLO`
- Recuperación utilizada: `S11-F01-R3-RECUPERACION`

Existe una desviación nominal entre el identificador preregistrado para recuperación y el utilizado durante la ejecución.

Los identificadores preregistrados se conservan y no se corrigen retroactivamente.

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

### Integridad local de Logcat

Los archivos Logcat originales permanecen como evidencia local privada.

No deberán versionarse sin saneamiento.

Hashes SHA-256 registrados:

#### R1

Archivo:

`evidencia-fallos/R1/logcat-R1-privado.txt`

SHA-256:

`586869C52B6513A4D0F05FC011729EC106109FADA6546D6D3EB640DC8146FB12`

#### R2

Archivo asociado al hash registrado:

`evidencia-fallos/R2/logcat-R2-continuacion-privado.txt`

SHA-256:

`3E874289E78D3BE8D61E5AE7DCF05CF7228F47CE2E7231D0658014451EAAC5EE`

#### R3

Archivo:

`evidencia-fallos/R3/logcat-R3-20261018-143057-privado.txt`

SHA-256:

`100D28B526252B8D3D10F753871214CDE0019392C88F83ADE7E04D4540A9982C`

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

`NO CAPTURADO EN EL MOMENTO DE LA PRUEBA`

Dispositivo o emulador:

`DISPOSITIVO FÍSICO ANDROID`

Versión Android:

`NO CAPTURADA DURANTE LA EJECUCIÓN`

Conectividad:

`Android -> 192.168.1.20:8080`

Condición inicial verificada:

`SÍ`

Se verificó:

- Spring Boot disponible;
- PostgreSQL disponible;
- Android abierto;
- usuario autenticado;
- comunicación Android-backend funcional;
- creación remota funcional.

Actividad de control:

`S11-F01-R1-CONTROL`

Control confirmado remotamente:

`SÍ`

Resultado:

`HTTP 201`

ID observado en PostgreSQL:

`1500170`

Fallo inducido:

`DETENCIÓN DEL PROCESO SPRING BOOT`

Comprobación de indisponibilidad:

`PUERTO 8080 SIN LISTENER`

Hora exacta de detención:

`NO CAPTURADA`

Actividad intentada durante el fallo:

`S11-F01-R1-DURANTE-FALLO`

Comportamiento visible en Android:

`No fue posible crear la actividad`

Error registrado:

`SocketTimeoutException aproximadamente después de 10 segundos`

Estado local observado:

`Android no presentó la actividad como creación remota confirmada exitosamente.`

Comprobación remota inicial:

`La consulta realizada después del intento fallido no encontró la actividad en PostgreSQL.`

Resultado remoto posterior:

`S11-F01-R1-DURANTE-FALLO apareció posteriormente con ID 1500171.`

### Desviación metodológica de F-01/R1

Después de restablecer Spring Boot se volvió a pulsar manualmente `Guardar` sobre:

`S11-F01-R1-DURANTE-FALLO`

Por esta razón, la aparición posterior de ID:

`1500171`

no puede atribuirse al intento original realizado durante la caída.

No existe evidencia suficiente para afirmar que:

- Android reintentó automáticamente la operación;
- Retrofit reenviara automáticamente la solicitud;
- existiera una cola local;
- existiera sincronización diferida;
- la solicitud original hubiese sido recuperada automáticamente.

### R-01

Estado:

`EJECUTADA CON DESVIACIONES DOCUMENTADAS`

Aplicación reiniciada:

`NO`

Usuario volvió a autenticarse:

`NO`

Continuidad de sesión:

`SÍ — RachaPro permaneció abierta y se conservó la misma sesión autenticada durante toda la repetición.`

Hora exacta de restablecimiento:

`NO CAPTURADA`

Backend restablecido:

`SÍ`

Identificador preregistrado:

`S11-R01-R1-RECUPERACION`

Identificador utilizado realmente:

`S11-F01-R1-RECUPERACION`

Estado:

`DESVIACIÓN NOMINAL DOCUMENTADA`

Resultado:

`HTTP 201`

ID PostgreSQL:

`1500172`

Persistencia remota:

`CONFIRMADA`

Criterio de recuperación para una nueva operación:

`SATISFECHO EN R1`

Conclusión R1:

`Después de restablecer Spring Boot, Android pudo ejecutar una nueva operación remota desde la misma sesión, sin reiniciar la aplicación ni volver a autenticar al usuario.`

Destino automático del intento original durante la caída:

`NO CONCLUYENTE`

Logcat:

`evidencia-fallos/R1/logcat-R1-privado.txt`

SHA-256:

`586869C52B6513A4D0F05FC011729EC106109FADA6546D6D3EB640DC8146FB12`

---

## 12.2 Repetición 2

### F-01

Estado:

`EJECUTADA`

Fecha:

`2026-10-18`

Preparación registrada:

`13:25:47`

Commit:

`83eee68`

Dispositivo o emulador:

`DISPOSITIVO FÍSICO ANDROID`

Versión Android:

`Android 14`

Condición inicial verificada:

`SÍ`

Se verificó:

- Spring Boot disponible;
- puerto 8080 disponible;
- PostgreSQL disponible;
- Android abierto;
- usuario autenticado;
- misma configuración de comunicación;
- creación remota funcional.

Actividad de control:

`S11-F01-R2-CONTROL`

Hora del control:

`13:28:27`

Resultado:

`HTTP 201`

ID PostgreSQL:

`1500173`

Control confirmado remotamente:

`SÍ`

Hora de detención del backend:

`13:31:37`

Comprobación de indisponibilidad:

`PUERTO 8080 SIN LISTENER`

Actividad intentada durante el fallo:

`S11-F01-R2-DURANTE-FALLO`

POST observado durante la caída:

`13:32:29`

Error registrado:

`SocketTimeoutException`

Hora del error:

`13:32:39`

Duración aproximada:

`10 segundos`

Comportamiento durante la caída:

`La creación no fue completada correctamente mientras Spring Boot estaba indisponible.`

Hora de restablecimiento del backend:

`13:34:22`

Comprobación:

`PUERTO 8080 NUEVAMENTE DISPONIBLE`

Comprobación remota del intento realizado durante el fallo:

`0 rows`

La consulta fue realizada después de restablecer Spring Boot y antes de comprobar la nueva actividad de recuperación.

No se reportó una segunda pulsación manual de `Guardar` sobre:

`S11-F01-R2-DURANTE-FALLO`

### Solicitudes posteriores observadas en R2

Después del restablecimiento se observaron dos solicitudes POST en Logcat.

Primera:

`13:36:02 — POST posterior al restablecimiento`

La respuesta correspondiente no quedó identificada en el fragmento analizado.

Segunda:

`13:36:49 — POST posterior`

Resultado:

`HTTP 201`

La evidencia disponible no permite determinar únicamente a partir de ese fragmento de Logcat el origen funcional exacto de cada POST.

Por tanto, no se afirma que:

- el POST de `13:36:02` fuera un reintento automático;
- el POST de `13:36:02` correspondiera necesariamente a `DURANTE-FALLO`;
- el POST de `13:36:49` pueda identificarse únicamente por Logcat con un título específico;
- Android disponga de reintento automático;
- Retrofit haya reenviado automáticamente la solicitud fallida.

Esta condición se conserva como:

`AMBIGÜEDAD DE TRAZABILIDAD DE SOLICITUDES POSTERIORES`

### R-01

Estado:

`EJECUTADA`

Aplicación reiniciada:

`NO`

Usuario volvió a autenticarse:

`NO`

Continuidad de sesión:

`SÍ — RachaPro permaneció abierta y se conservó la misma sesión autenticada durante toda la repetición.`

Identificador preregistrado:

`S11-R01-R2-RECUPERACION`

Identificador utilizado realmente:

`S11-F01-R2-RECUPERACION`

Estado:

`DESVIACIÓN NOMINAL DOCUMENTADA`

Intervención manual informada:

`Una pulsación de Guardar para crear la actividad de recuperación.`

No se reportó una segunda pulsación manual sobre `S11-F01-R2-DURANTE-FALLO`.

ID PostgreSQL de la actividad de recuperación:

`1500174`

Persistencia remota:

`CONFIRMADA`

Criterio de recuperación para una nueva operación:

`SATISFECHO EN R2`

Conclusión R2:

`Después de restablecer Spring Boot, Android pudo mantener la misma sesión y completar una nueva operación cuya persistencia quedó comprobada en PostgreSQL con ID 1500174.`

La comprobación anterior no resuelve la naturaleza de los dos POST posteriores observados en Logcat.

Destino automático del intento original durante la caída:

`NO DEMOSTRADO`

Logcat utilizado para la evidencia complementaria:

`evidencia-fallos/R2/logcat-R2-continuacion-privado.txt`

SHA-256:

`3E874289E78D3BE8D61E5AE7DCF05CF7228F47CE2E7231D0658014451EAAC5EE`

---

## 12.3 Repetición 3

### F-01

Estado:

`EJECUTADA`

Fecha:

`2026-10-18`

Commit:

`83eee68`

Dispositivo o emulador:

`DISPOSITIVO FÍSICO ANDROID`

Versión Android:

`Android 14`

Condición inicial:

`VERIFICADA`

Actividad de control:

`S11-F01-R3-CONTROL`

Resultado del control:

`HTTP 201`

ID PostgreSQL:

`1500175`

Control confirmado remotamente:

`SÍ`

Fallo inducido:

`DETENCIÓN DEL PROCESO SPRING BOOT`

Comprobación:

`PUERTO 8080 SIN LISTENER`

Actividad intentada durante el fallo:

`S11-F01-R3-DURANTE-FALLO`

POST durante la caída:

`14:34:04`

Error registrado:

`SocketTimeoutException`

Hora del error:

`14:34:14`

Duración aproximada:

`10 segundos`

Comportamiento visible en Android:

`No fue posible crear la actividad`

Android no presentó la operación como una creación remota confirmada durante la caída.

### Restablecimiento y solicitudes posteriores

Después del restablecimiento de Spring Boot se observó:

`14:36:13 — POST posterior con HTTP 201`

Posteriormente se observó:

`14:37:17 — otro POST posterior con HTTP 201`

PostgreSQL mostró:

`1500176 — S11-F01-R3-DURANTE-FALLO`

`1500177 — S11-F01-R3-RECUPERACION`

### Desviación metodológica de F-01/R3

Después de restablecer Spring Boot se volvió a pulsar manualmente `Guardar` sobre:

`S11-F01-R3-DURANTE-FALLO`

Por esta razón, la persistencia de:

`1500176`

no demuestra que el intento original realizado durante la caída se recuperara automáticamente.

No se puede afirmar que:

- Android reintentara automáticamente la solicitud;
- Retrofit reenviara automáticamente la operación;
- existiera una cola local;
- existiera sincronización diferida;
- la persistencia de `1500176` procediera del intento original sin intervención.

Además, los POST posteriores observados en Logcat no deben vincularse de manera definitiva con títulos concretos únicamente a partir de ese fragmento.

### R-01

Estado:

`EJECUTADA CON DESVIACIONES DOCUMENTADAS`

Aplicación reiniciada:

`NO`

Usuario volvió a autenticarse:

`NO`

Continuidad de sesión:

`SÍ — RachaPro permaneció abierta y se conservó la misma sesión autenticada durante toda la repetición.`

Identificador preregistrado:

`S11-R01-R3-RECUPERACION`

Identificador utilizado realmente:

`S11-F01-R3-RECUPERACION`

Estado:

`DESVIACIÓN NOMINAL DOCUMENTADA`

ID PostgreSQL:

`1500177`

Persistencia remota:

`CONFIRMADA`

Criterio de recuperación para una nueva operación:

`SATISFECHO EN R3`

Conclusión R3:

`Después del restablecimiento existe evidencia de que Android, manteniendo la misma sesión y sin reiniciar la aplicación, pudo realizar una nueva operación de recuperación que quedó persistida en PostgreSQL con ID 1500177.`

Destino automático del intento original durante la caída:

`NO CONCLUYENTE`

La persistencia con ID `1500176` no puede utilizarse como prueba de recuperación automática debido a la segunda pulsación manual de `Guardar`.

Logcat:

`evidencia-fallos/R3/logcat-R3-20261018-143057-privado.txt`

SHA-256:

`100D28B526252B8D3D10F753871214CDE0019392C88F83ADE7E04D4540A9982C`

---

## 13. Separación entre expectativa y observación

Las siguientes afirmaciones fueron definidas como expectativas previas y no como resultados:

- Android no debería presentar una operación remota como confirmada si el backend no respondió.
- Una operación mostrada localmente no debe asumirse automáticamente como persistida remotamente.
- Android debería recuperar la capacidad de comunicarse con el backend después de que el servicio sea restablecido.
- La recuperación debería poder comprobarse sin reiniciar la aplicación.
- El estado remoto debe comprobarse para distinguir entre intento, visualización local y persistencia real.

Después de ejecutar R1, R2 y R3 se observaron resultados compatibles con algunas de estas expectativas.

En las tres repeticiones:

- Android no presentó como creación remota confirmada la operación que terminó en timeout durante la indisponibilidad;
- Spring Boot volvió a estar disponible;
- RachaPro permaneció abierta;
- el usuario permaneció en la misma sesión autenticada;
- posteriormente pudo comprobarse persistencia de una nueva actividad de recuperación.

Estas observaciones no permiten afirmar mecanismos internos que no fueron medidos.

En particular, no existe evidencia suficiente para afirmar la existencia de:

- reintento automático;
- cola local;
- sincronización diferida;
- recuperación automática del intento original;
- reenvío automático de Retrofit.

---

## 14. Criterio de reproducibilidad y comparabilidad

El escenario se considerará reproducido consistentemente si las tres repeticiones cumplen condiciones iniciales equivalentes y producen el mismo comportamiento observable relevante.

Si los resultados difieren entre repeticiones, la divergencia deberá registrarse.

No se descartará una repetición únicamente porque contradiga las otras.

Cualquier repetición invalidada metodológicamente deberá justificar:

- qué condición dejó de cumplirse;
- qué evidencia demuestra la desviación;
- por qué no puede compararse directamente con las demás.

### Núcleo comparable observado

R1, R2 y R3 permiten comparar el siguiente comportamiento:

- Spring Boot estaba disponible inicialmente;
- existía una actividad de control confirmada remotamente;
- Spring Boot fue detenido;
- el puerto 8080 dejó de atender;
- se intentó crear una actividad durante la caída;
- se produjo un error de comunicación;
- Android no presentó la operación como creación remota confirmada;
- Spring Boot fue restablecido;
- RachaPro permaneció abierta;
- el usuario permaneció autenticado;
- una nueva actividad pudo persistirse después del restablecimiento.

### Diferencias relevantes

#### R1

Se realizó una segunda pulsación manual de `Guardar` sobre:

`S11-F01-R1-DURANTE-FALLO`

La persistencia posterior de ID `1500171` no puede interpretarse como recuperación automática.

#### R2

No se reportó una segunda pulsación manual sobre:

`S11-F01-R2-DURANTE-FALLO`

La consulta remota realizada después del restablecimiento devolvió:

`0 rows`

Sin embargo, Logcat contiene dos POST posteriores al restablecimiento y la relación exacta de cada POST con las acciones funcionales no quedó determinada completamente.

Esta limitación impide utilizar R2 para afirmar un mecanismo automático no observado.

#### R3

Se realizó una segunda pulsación manual de `Guardar` sobre:

`S11-F01-R3-DURANTE-FALLO`

La persistencia posterior con ID `1500176` no puede interpretarse como recuperación automática.

### Evaluación de comparabilidad

Las tres repeticiones son comparables para observar:

- respuesta de Android ante indisponibilidad de Spring Boot;
- timeout de red;
- ausencia de confirmación falsa de éxito durante la caída;
- continuidad de la aplicación;
- continuidad de sesión;
- posibilidad de realizar una nueva operación después del restablecimiento.

Las tres repeticiones no proporcionan evidencia igualmente limpia para determinar:

`qué ocurre automáticamente con la solicitud original realizada durante la indisponibilidad`

debido a:

- segundo Guardar manual en R1;
- ambigüedad de trazabilidad posterior en R2;
- segundo Guardar manual en R3.

No se ejecuta una cuarta repetición únicamente para favorecer una conclusión.

---

## 15. Limitaciones del escenario

Este escenario evalúa únicamente una indisponibilidad temporal inducida del backend Spring Boot.

No evalúa todavía:

- caída de PostgreSQL;
- pérdida parcial de conectividad;
- latencia extrema;
- respuestas HTTP 5xx deliberadas;
- timeouts controlados desde servidor;
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

### Limitaciones específicas observadas

R1:

`Segundo Guardar manual sobre la actividad DURANTE-FALLO.`

R2:

`Dos POST posteriores al restablecimiento cuya relación funcional exacta no quedó completamente determinada mediante el fragmento de Logcat disponible.`

R3:

`Segundo Guardar manual sobre la actividad DURANTE-FALLO.`

Estas condiciones deben conservarse en cualquier análisis posterior y no deben ocultarse al evaluar una táctica arquitectónica.

---

## 16. Entorno de ejecución

Las pruebas F-01/R-01 se ejecutaron utilizando:

`DISPOSITIVO FÍSICO ANDROID`

El dispositivo fue conectado al computador mediante USB para permitir captura de evidencia mediante ADB y Logcat.

ADB reconoció correctamente el dispositivo con estado:

`device`

La aplicación Android se ejecutó en el celular físico.

Spring Boot se ejecutó en el computador de pruebas.

PostgreSQL permaneció disponible durante las interrupciones deliberadas de Spring Boot.

En R1 se registró comunicación mediante:

`Android -> 192.168.1.20:8080 -> Spring Boot`

Para R2 y R3 se registró:

`Android 14`

Para R1 la versión exacta de Android no fue capturada durante la ejecución y no se infiere retroactivamente.

Las tres repeticiones mantuvieron:

- RachaPro abierta;
- la misma sesión autenticada;
- ausencia de reinicio de aplicación;
- ausencia de nueva autenticación después del restablecimiento.

Los Logcat se conservaron como evidencia local privada y no deben publicarse sin saneamiento.

---

## 17. Relación con Semana 11

Esta actividad corresponde al tercer bloque de Semana 11.

Estado:

- crecimiento de carga: completado;
- registro de degradación: completado;
- matriz de fallos y resiliencia: preregistrada;
- F-01/R-01 R1: ejecutada y documentada;
- F-01/R-01 R2: ejecutada y documentada;
- F-01/R-01 R3: ejecutada y documentada;
- comparabilidad: analizada;
- táctica y comparación antes/después: pendiente.

Las tres repeticiones previstas fueron ejecutadas.

No se considera necesario añadir automáticamente una cuarta repetición.

La selección de una táctica arquitectónica se realizará después de cerrar la auditoría de esta actividad.

La evidencia obtenida no selecciona por sí sola la táctica.

La decisión arquitectónica corresponde al equipo y deberá registrarse posteriormente.

---

## 18. Secuencia temporal

La secuencia metodológica definida fue:

`definición del escenario -> commit previo -> ejecución -> evidencia -> resultados -> análisis -> decisión de táctica`

El preregistro de F-01/R-01 quedó registrado antes de ejecutar las pruebas mediante:

`87de9d9`

Después se ejecutó R1.

El registro documental de R1 quedó en:

`83eee68`

R2 y R3 fueron ejecutadas posteriormente con:

`83eee68`

como commit registrado durante sus ejecuciones.

El documento actual deberá quedar registrado en un nuevo commit posterior.

Esta secuencia permite distinguir:

- expectativas previas;
- evidencia obtenida;
- desviaciones observadas;
- interpretación posterior;
- futura decisión arquitectónica.

Los criterios preregistrados no deben ser sustituidos retroactivamente por criterios definidos después de observar los resultados.

---

## 19. Resultado consolidado de la actividad 3

Las tres repeticiones preregistradas de F-01/R-01 fueron ejecutadas.

### F-01 — comportamiento durante la indisponibilidad

En las tres repeticiones se observó que:

- Spring Boot dejó de atender solicitudes;
- Android intentó realizar una creación;
- la operación produjo un error de comunicación;
- se observó `SocketTimeoutException`;
- Android no presentó la creación como una operación remota confirmada exitosamente.

Esto constituye evidencia reproducida, bajo las condiciones del experimento, del comportamiento visible ante indisponibilidad temporal del backend.

### R-01 — recuperación para una nueva operación

En R1, R2 y R3 se confirmó que:

- RachaPro permaneció abierta;
- no se reinició la aplicación;
- no se volvió a iniciar sesión;
- Spring Boot fue restablecido;
- posteriormente pudo persistirse una nueva actividad de recuperación.

Por tanto, las tres repeticiones aportan evidencia de recuperación operativa para una nueva operación desde la sesión Android existente.

Esta conclusión se limita al escenario reproducido y no demuestra disponibilidad global.

### Destino de la solicitud original durante el fallo

No se dispone de evidencia suficiente para afirmar que la solicitud fallida durante la indisponibilidad sea recuperada automáticamente.

R1:

`NO CONCLUYENTE — segundo Guardar manual`

R2:

`NO DEMOSTRADO — 0 rows en comprobación inicial y dos POST posteriores con trazabilidad incompleta`

R3:

`NO CONCLUYENTE — segundo Guardar manual`

Por tanto, no se atribuye al sistema ninguna capacidad observada de:

- reintento automático;
- cola local;
- sincronización posterior;
- reenvío automático;
- recuperación automática de operaciones fallidas.

### Desviaciones nominales

Los identificadores de recuperación preregistrados utilizaban:

`S11-R01-Rx-RECUPERACION`

Durante las ejecuciones se utilizaron:

- `S11-F01-R1-RECUPERACION`
- `S11-F01-R2-RECUPERACION`
- `S11-F01-R3-RECUPERACION`

Esta diferencia se conserva como desviación nominal.

No se modifica retroactivamente el preregistro.

### Estado metodológico

R1:

`UTILIZABLE para F-01 y recuperación de una nueva operación; limitada para analizar el destino automático de DURANTE-FALLO.`

R2:

`UTILIZABLE para F-01 y recuperación de una nueva operación; presenta ambigüedad de trazabilidad en POST posteriores.`

R3:

`UTILIZABLE para F-01 y recuperación de una nueva operación; limitada para analizar el destino automático de DURANTE-FALLO.`

No se descarta ninguna repetición.

### Estado de Semana 11

Actividad 1 — Medición con crecimiento de carga:

`COMPLETADA`

Actividad 2 — Registro de degradación:

`COMPLETADA`

Actividad 3 — Matrices de fallos y resiliencia:

`TRES REPETICIONES EJECUTADAS Y DOCUMENTADAS — PENDIENTE DE AUDITORÍA FINAL EN GIT`

Actividad 4 — Aplicación de táctica y comparación antes/después:

`PENDIENTE`

El siguiente paso es verificar que el documento actual no haya modificado retroactivamente los criterios preregistrados y, si la auditoría es satisfactoria, registrar R2, R3 y la comparabilidad en un nuevo commit.