# Semana 11 — Matrices de fallos y resiliencia

## 1. Propósito

Este documento registra escenarios controlados de fallo utilizados para observar el comportamiento de RachaPro ante condiciones de indisponibilidad y recuperación.

Las expectativas definidas antes de la ejecución se mantienen separadas de los resultados realmente observados.

Una respuesta esperada no se considera evidencia de que el comportamiento esté implementado.

El objetivo de esta actividad es:

- definir escenarios reproducibles de fallo;
- establecer criterios verificables antes de ejecutar las pruebas;
- registrar evidencia durante la ejecución;
- observar el comportamiento real del sistema;
- analizar posteriormente si existe una respuesta de resiliencia;
- evitar atribuir capacidades al sistema que no hayan sido observadas.

Los resultados se completarán únicamente después de ejecutar cada escenario.

---

## 2. Alcance

La primera prueba se concentrará en la indisponibilidad temporal del backend Spring Boot y en el comportamiento de la aplicación Android antes, durante y después de dicha indisponibilidad.

El escenario no pretende todavía evaluar todas las posibles fallas de RachaPro.

En esta primera iteración se busca responder:

1. qué ocurre en Android cuando el backend deja de estar disponible;
2. si la aplicación evita presentar como exitosa una operación que no fue confirmada por el backend;
3. si Android puede recuperar la capacidad de operar después de restablecer el backend;
4. si la recuperación ocurre sin reiniciar la aplicación ni volver a autenticarse.

No se asumirá previamente que existe un mecanismo de fallback, reintento, caché o recuperación automática.

---

## 3. Principios de ejecución

Cada escenario debe distinguir tres momentos:

### 3.1 Antes del fallo

Se confirma que:

- el backend se encuentra disponible;
- la aplicación Android está operativa;
- el usuario está autenticado;
- existe conectividad entre Android y backend;
- la operación seleccionada funciona normalmente.

Este momento establece la condición inicial de referencia.

### 3.2 Durante el fallo

Se induce el fallo definido y se ejecuta nuevamente la operación seleccionada.

Se registran:

- comportamiento visible en Android;
- mensajes mostrados al usuario;
- respuesta o error de red;
- logs disponibles;
- momento de inicio del fallo;
- momento de ejecución de la operación;
- cualquier cambio observable en el estado local.

### 3.3 Después del fallo

Se restablece el componente afectado.

La aplicación Android debe permanecer abierta y en la misma sesión.

La misma operación se ejecuta nuevamente para comprobar si el cliente recuperó su capacidad de comunicarse con el backend.

Reiniciar la aplicación o volver a autenticarse antes de esta comprobación impediría distinguir entre recuperación del cliente y reconstrucción completa de la sesión.

---

## 4. Matriz de fallos

| ID | Componente | Fallo inducido | Condición inicial | Condición desencadenante | Operación de prueba | Efecto esperado | Evidencia requerida | Resultado observado |
|---|---|---|---|---|---|---|---|---|
| F-01 | Backend Spring Boot | Indisponibilidad temporal del servicio | Backend disponible, aplicación Android abierta, usuario autenticado y operación remota funcionando correctamente | Después de comprobar la operación inicial, se detiene deliberadamente el backend y se ejecuta desde Android una operación que requiera comunicación remota | Creación de una actividad desde Android | La aplicación no debe presentar la creación como confirmada exitosamente si el backend no respondió | Logcat, comportamiento visible de Android, error o respuesta de red, momento de detención del backend y comportamiento del estado local | Pendiente |

---

## 5. Detalle del escenario F-01

### 5.1 Objetivo

Observar el comportamiento de Android cuando el backend Spring Boot deja de estar disponible temporalmente.

### 5.2 Condición inicial

Antes de inducir el fallo deben cumplirse todas las condiciones siguientes:

- backend Spring Boot iniciado;
- base de datos disponible;
- aplicación Android abierta;
- usuario autenticado;
- sesión activa;
- comunicación Android-backend funcional;
- creación de actividad verificada previamente bajo condiciones normales.

### 5.3 Inyección del fallo

El fallo se inducirá deteniendo deliberadamente el backend Spring Boot.

La base de datos no será detenida en esta prueba.

No se modificarán:

- credenciales;
- token del usuario;
- configuración de red;
- base de datos;
- código de Android;
- código del backend.

La variable experimental será únicamente la disponibilidad del backend.

### 5.4 Acción durante el fallo

Con la aplicación Android todavía abierta y el usuario en la misma sesión:

1. detener el backend;
2. comprobar que el proceso dejó de atender solicitudes;
3. intentar crear una actividad desde Android;
4. observar la respuesta visible;
5. registrar Logcat;
6. registrar el error de red disponible;
7. comprobar si la aplicación presenta o no la operación como completada.

### 5.5 Efecto esperado

La expectativa previa al experimento es:

> Si el backend no responde, Android no debe presentar la creación como una operación remota confirmada exitosamente.

Esta expectativa no implica que actualmente exista un mecanismo concreto de fallback, reintento o persistencia diferida.

El comportamiento real debe determinarse mediante la ejecución.

---

## 6. Matriz de resiliencia

| ID | Fallo relacionado | Estado previo | Respuesta esperada durante el fallo | Recuperación esperada | Criterio verificable | Evidencia requerida | Resultado observado |
|---|---|---|---|---|---|---|---|
| R-01 | F-01 | Backend disponible, usuario autenticado, aplicación Android abierta y operación remota funcional | La aplicación debe manejar la indisponibilidad sin mostrar una confirmación exitosa falsa de la operación remota | Después de restablecer el backend, Android debe recuperar la capacidad de ejecutar una operación remota desde la misma sesión | Sin reiniciar Android ni volver a iniciar sesión, se restablece el backend y se repite la creación de una actividad. Se considera recuperación si la solicitud vuelve a completarse correctamente y la actividad queda reflejada en el estado esperado de la aplicación | Logcat, respuesta de red, comportamiento visible de Android y evidencia de la actividad creada después de la recuperación | Pendiente |

---

## 7. Detalle del escenario R-01

### 7.1 Objetivo

Comprobar si Android recupera la capacidad de operar con el backend después de una indisponibilidad temporal.

El objetivo no es comprobar únicamente que el backend vuelva a iniciar.

Se busca comprobar que el cliente Android vuelva a realizar correctamente una operación remota desde la sesión que estaba activa antes del fallo.

### 7.2 Condición de recuperación

Después de ejecutar F-01:

1. mantener abierta la aplicación Android;
2. no cerrar sesión;
3. no reiniciar la aplicación;
4. restablecer el backend Spring Boot;
5. comprobar que el backend está nuevamente disponible;
6. ejecutar nuevamente la creación de una actividad;
7. observar el resultado.

### 7.3 Criterio verificable

Se considerará que existe recuperación operativa para este escenario únicamente si:

- Android permanece en la misma sesión;
- no se reinicia la aplicación;
- no se vuelve a autenticar al usuario;
- el backend es restablecido;
- se repite la operación de creación;
- la solicitud se completa correctamente;
- la actividad queda reflejada en el estado esperado de la aplicación.

El simple hecho de que el backend vuelva a iniciar no constituye evidencia suficiente de recuperación de Android.

### 7.4 Resultado

Pendiente de ejecución.

---

## 8. Evidencia a recolectar

Para F-01 y R-01 se deberá conservar, cuando sea posible:

- fecha y hora de ejecución;
- commit del repositorio;
- dispositivo o emulador utilizado;
- versión de Android;
- usuario experimental utilizado;
- estado inicial del backend;
- momento en que se detiene el backend;
- momento en que se ejecuta la operación durante el fallo;
- mensaje visible en Android;
- Logcat relacionado con la operación;
- respuesta o error de Retrofit;
- momento en que se restablece el backend;
- resultado de repetir la operación;
- evidencia de que la aplicación no fue reiniciada;
- evidencia de que la sesión no fue reemplazada.

La evidencia debe conservarse sin incluir contraseñas, JWT completos ni otros secretos.

---

## 9. Registro de ejecución

### 9.1 F-01 — indisponibilidad del backend

Estado:

`PENDIENTE DE EJECUCIÓN`

Fecha:

`PENDIENTE`

Commit:

`PENDIENTE`

Condición inicial verificada:

`PENDIENTE`

Hora de detención del backend:

`PENDIENTE`

Operación ejecutada:

`Creación de actividad`

Comportamiento visible en Android:

`PENDIENTE`

Error o respuesta registrada:

`PENDIENTE`

Estado local observado:

`PENDIENTE`

Logs asociados:

`PENDIENTE`

Conclusión experimental:

`PENDIENTE`

---

### 9.2 R-01 — recuperación posterior

Estado:

`PENDIENTE DE EJECUCIÓN`

Hora de restablecimiento del backend:

`PENDIENTE`

Aplicación reiniciada:

`NO DEBE REINICIARSE`

Usuario volvió a autenticarse:

`NO DEBE VOLVER A AUTENTICARSE`

Operación repetida:

`Creación de actividad`

Resultado de la solicitud:

`PENDIENTE`

Actividad reflejada en la aplicación:

`PENDIENTE`

Logs asociados:

`PENDIENTE`

Criterio de recuperación satisfecho:

`PENDIENTE`

Conclusión experimental:

`PENDIENTE`

---

## 10. Separación entre expectativa y observación

Las siguientes afirmaciones son expectativas previas y no resultados:

- Android no debería presentar una operación remota como exitosa si el backend no respondió.
- Android debería recuperar la capacidad de comunicarse con el backend después de que el servicio sea restablecido.
- La recuperación debería poder comprobarse sin reiniciar la aplicación.

Estas afirmaciones solo podrán convertirse en resultados después de ejecutar F-01 y R-01.

---

## 11. Limitaciones del escenario

Este escenario evalúa únicamente una indisponibilidad temporal del backend.

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
- concurrencia durante la recuperación.

Tampoco demuestra por sí mismo disponibilidad global del sistema.

Los resultados deben interpretarse únicamente dentro de las condiciones reproducidas.

---

## 12. Relación con Semana 11

Esta actividad corresponde al tercer bloque de Semana 11:

- crecimiento de carga: completado;
- registro de degradación: completado;
- matriz de fallos y resiliencia: en ejecución;
- táctica y comparación antes/después: pendiente.

La selección de una táctica arquitectónica se realizará después de observar los resultados de los escenarios de fallo y recuperación.

No se seleccionará una táctica únicamente a partir de una expectativa no medida.

---

## 13. Próximo paso

Antes de ejecutar F-01 debe preservarse esta definición previa en Git.

Después del commit se podrá inducir el fallo y completar exclusivamente los campos correspondientes a evidencia y resultados observados.

Los resultados deberán registrarse en un commit posterior para conservar la secuencia temporal:

`definición del escenario -> ejecución -> evidencia -> resultado -> decisión de táctica`