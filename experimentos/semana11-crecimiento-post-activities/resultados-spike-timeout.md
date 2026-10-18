# Semana 11 — Actividad 4
## Resultados del experimento de timeout

Este documento registra los resultados observados durante la ejecución del experimento preregistrado en:

`prerregistro-spike-timeout.md`

El prerregistro quedó registrado previamente mediante el commit:

`94dcc32`

Los resultados consignados aquí son posteriores al prerregistro.

No se modifican retroactivamente:

- la hipótesis;
- el umbral del 50 %;
- el número de corridas;
- la métrica principal;
- los criterios de evaluabilidad;
- las reglas para corridas no evaluables;
- las reglas de reemplazo;
- la intervención prevista.

---

## 1. Estado general

Prerregistro:

`COMPLETADO`

Commit de prerregistro:

`94dcc32`

Commit de resultados de BASE-R1:

`b02c239`

Commit provisional de BASE-R2:

`e1dab87`

Condición experimental actual:

`LÍNEA BASE`

Configuración HTTP:

`SIN connectTimeout EXPLÍCITO`

Corridas principales previstas de línea base:

`3`

Estado de BASE-R1:

`EJECUTADA — EVALUABLE CON DESVIACIÓN DOCUMENTADA`

Estado de BASE-R2:

`EJECUTADA — EVALUABLE CON INCIDENCIA PREVIA RESUELTA`

Estado de BASE-R3:

`PENDIENTE`

Intervención experimental:

`NO IMPLEMENTADA`

Mediciones posteriores:

`NO EJECUTADAS`

Hipótesis:

`NO EVALUADA`

Conclusión arquitectónica:

`PENDIENTE`

---

## 2. BASE-R1

Estado:

`EVALUABLE CON DESVIACIÓN DOCUMENTADA`

Valor admitido para la comparación principal:

`B1 = 10 028 ms`

Commit ejecutado:

`94dcc32`

Condición:

`LÍNEA BASE — SIN connectTimeout EXPLÍCITO`

Fecha:

`2026-10-18`

Dispositivo:

`DISPOSITIVO FÍSICO ANDROID`

Versión Android:

`14`

---

### 2.1 Condición inicial

Antes de inducir la indisponibilidad se verificó:

- dispositivo Android conectado;
- aplicación RachaPro disponible;
- usuario autenticado;
- Spring Boot disponible;
- puerto `8080` en estado `Listen`;
- PostgreSQL disponible;
- comunicación Android-backend funcional.

La configuración HTTP correspondía a la línea base preregistrada.

No se había implementado todavía:

`connectTimeout = 3 segundos`

---

### 2.2 Control previo

Identificador utilizado:

`S11-A4-BASE-R1`

Inicio del POST:

`16:52:08.227`

Hilo OkHttp:

`5952`

Tamaño de cuerpo registrado:

`105 bytes`

Resultado:

`HTTP 201`

Fin:

`16:52:08.348`

Duración informada por OkHttp:

`119 ms`

Diferencia entre timestamps visibles de Logcat:

`121 ms`

La duración del control no forma parte de la métrica principal de BASE-R1.

El control previo se conserva únicamente como evidencia de que la creación remota funcionaba antes de inducir la indisponibilidad.

---

### 2.3 Inducción del fallo

Después del control previo se detuvo Spring Boot.

Se comprobó que el puerto:

`8080`

no tenía listener.

Hora de comprobación registrada:

`16:53:43`

La comprobación ocurrió antes de ejecutar el POST experimental.

PostgreSQL no fue detenido intencionalmente.

---

### 2.4 Intento experimental durante la indisponibilidad

Identificador utilizado:

`S11-A4-BASE-R1`

Inicio del POST:

`16:54:24.825`

Hilo OkHttp:

`6163`

Tamaño de cuerpo registrado:

`103 bytes`

Resultado:

`SocketTimeoutException`

Fin:

`16:54:34.853`

Mensaje técnico registrado:

`failed to connect ... after 10000ms`

---

### 2.5 Duración técnica

La métrica principal preregistrada corresponde al intervalo entre:

`--> POST`

y:

`HTTP FAILED`

Para BASE-R1:

Inicio:

`16:54:24.825`

Fin:

`16:54:34.853`

Cálculo:

`34.853 - 24.825 = 10.028 segundos`

Duración técnica:

`10 028 ms`

Valor individual:

`B1 = 10 028 ms`

---

### 2.6 Resultado visible en Android

Android mostró:

`No fue posible crear la actividad`

No se observó que Android presentara la operación experimental como una creación remota confirmada exitosamente.

Resultado funcional visible:

`ERROR MOSTRADO — SIN FALSO ÉXITO OBSERVADO`

---

### 2.7 Persistencia remota

Después de las operaciones se consultó PostgreSQL utilizando el título:

`S11-A4-BASE-R1`

Se observó:

`1 fila`

ID observado:

`1500178`

#### Hechos observados

La evidencia disponible permite afirmar directamente que:

- existió un POST de control;
- el POST de control obtuvo `HTTP 201`;
- existió posteriormente un POST experimental;
- el POST experimental terminó en `SocketTimeoutException`;
- PostgreSQL mostró una única fila con el título reutilizado.

#### Limitación de identificación

El nivel:

`HttpLoggingInterceptor.Level.BASIC`

no expone el contenido funcional de los cuerpos de las solicitudes.

Por tanto, el Logcat por sí solo no permite vincular inequívocamente la fila:

`1500178`

con el contenido funcional específico del POST de control.

#### Inferencia apoyada por evidencia conjunta

La combinación de:

- un POST de control con `HTTP 201`;
- un POST posterior terminado en `SocketTimeoutException`;
- una única fila en PostgreSQL;

es consistente con que la fila observada corresponda a la operación de control y que no exista una segunda persistencia asociada al intento realizado durante la indisponibilidad.

Esta interpretación se registra como:

`INFERENCIA APOYADA POR EVIDENCIA CONJUNTA`

y no como:

`IDENTIFICACIÓN INEQUÍVOCA DEL BODY DEL POST`

---

### 2.8 Correlación de solicitudes HTTP

Las dos solicitudes HTTP pueden distinguirse mediante evidencia independiente del título.

#### Control

Inicio:

`16:52:08.227`

Hilo:

`5952`

Tamaño de body:

`105 bytes`

Resultado:

`HTTP 201`

#### Intento experimental

Inicio:

`16:54:24.825`

Hilo:

`6163`

Tamaño de body:

`103 bytes`

Resultado:

`SocketTimeoutException`

Los timestamps, hilos, tamaños registrados y resultados permiten correlacionar cada solicitud HTTP con su resultado correspondiente.

Por tanto, la duración técnica del intento experimental puede calcularse sin mezclarla con la solicitud de control.

---

### 2.9 Desviación metodológica

Se reutilizó el identificador:

`S11-A4-BASE-R1`

tanto para:

- la operación de control;
- la operación experimental durante la indisponibilidad.

Esto constituye una desviación respecto del objetivo de mantener operaciones funcionalmente distinguibles.

La desviación reduce la trazabilidad funcional en PostgreSQL.

No obstante, las dos solicitudes HTTP pueden distinguirse técnicamente mediante:

- timestamps diferentes;
- hilos OkHttp diferentes;
- tamaños de body diferentes;
- resultados diferentes.

---

### 2.10 Evaluabilidad de BASE-R1

Clasificación:

`EVALUABLE CON DESVIACIÓN DOCUMENTADA`

Esta clasificación constituye una decisión metodológica posterior a la observación de la corrida y no una consecuencia automática del Logcat.

La justificación registrada es:

1. el prerregistro existía antes de la corrida;
2. la aplicación se encontraba en condición de línea base;
3. Spring Boot estaba disponible durante el control;
4. el control obtuvo `HTTP 201`;
5. Spring Boot fue detenido antes del POST experimental;
6. se comprobó indisponibilidad del puerto `8080`;
7. el POST experimental puede correlacionarse con su fallo;
8. existe una duración técnica calculable;
9. Android no mostró falso éxito;
10. PostgreSQL pudo ser consultado;
11. la desviación quedó documentada;
12. las solicitudes HTTP pueden distinguirse técnicamente.

La principal limitación conservada es:

`LA PERSISTENCIA REMOTA NO PUEDE ATRIBUIRSE A CADA BODY DE FORMA INEQUÍVOCA ÚNICAMENTE MEDIANTE EL LOGCAT BASIC`

---

### 2.11 Participación en la mediana

Decisión:

`BASE-R1 PARTICIPA EN LA MEDIANA PRINCIPAL`

Valor:

`B1 = 10 028 ms`

Reemplazo:

`NO REQUERIDO`

---

### 2.12 Corrección procedimental posterior a BASE-R1

A partir de BASE-R2 se utilizan identificadores diferentes para control y experimento.

BASE-R2:

- control: `S11-A4-BASE-R2-CONTROL`;
- experimento: `S11-A4-BASE-R2`.

BASE-R3:

- control: `S11-A4-BASE-R3-CONTROL`;
- experimento: `S11-A4-BASE-R3`.

Esta corrección no altera retrospectivamente BASE-R1.

---

### 2.13 Integridad de evidencia BASE-R1

Archivo privado:

`evidencia-timeout/BASE-R1/logcat-BASE-R1-privado.txt`

SHA-256:

`DE5066FCF6BA7B86E1BD12FCA3041AD77513C38499E25578EB92DD27DA6E1AE5`

El archivo se conserva como evidencia privada.

No deberá versionarse sin revisión y saneamiento.

---

### 2.14 Resultado consolidado BASE-R1

Estado:

`EVALUABLE CON DESVIACIÓN DOCUMENTADA`

Valor:

`B1 = 10 028 ms`

Tipo de error:

`SocketTimeoutException`

Falso éxito Android:

`NO OBSERVADO`

Persistencia del intento experimental:

`NO DEMOSTRADA DE FORMA INEQUÍVOCA`

Fila observada:

`ID 1500178`

Trazabilidad HTTP:

`SUFICIENTE PARA CORRELACIONAR LA MEDICIÓN`

Reemplazo:

`NO`

---

## 3. BASE-R2

Estado:

`EVALUABLE CON INCIDENCIA PREVIA RESUELTA`

Valor admitido para la comparación principal:

`B2 = 10 033 ms`

Commit provisional previo al cierre:

`e1dab87`

Condición:

`LÍNEA BASE — SIN connectTimeout EXPLÍCITO`

Fecha:

`2026-10-18`

Dispositivo:

`DISPOSITIVO FÍSICO ANDROID`

Versión Android:

`14`

Identificador de control:

`S11-A4-BASE-R2-CONTROL`

Identificador experimental:

`S11-A4-BASE-R2`

---

### 3.1 Preparación de BASE-R2

Antes de BASE-R2 se confirmó que el commit activo era:

`b02c239`

La configuración HTTP continuaba correspondiendo a la línea base.

No se había implementado:

`connectTimeout = 3 segundos`

Spring Boot se encontraba disponible al comenzar la preparación.

El puerto:

`8080`

fue observado inicialmente en estado:

`Listen`

---

### 3.2 Incidencia previa de autenticación

Antes del control exitoso ocurrió una incidencia de autenticación.

Inicio:

`17:56:47.083`

Solicitud:

`POST /api/activities`

Resultado:

`HTTP 401`

Fin:

`17:56:47.298`

Este POST terminó antes del login posterior.

No corresponde al POST experimental utilizado para medir BASE-R2.

---

### 3.3 Restablecimiento de autenticación

Posteriormente se realizó autenticación.

Inicio:

`17:56:55.297`

Solicitud:

`POST /api/auth/login`

Resultado:

`HTTP 200`

Fin:

`17:56:57.802`

El `HTTP 200` constituye evidencia técnica de un login exitoso.

Después de la ejecución se confirmó operativamente que:

- RachaPro permaneció abierta;
- se mantuvo el mismo usuario;
- no se cerró la aplicación;
- no se reinició la aplicación;
- no se realizó un nuevo login;
- no se cambió intencionalmente de usuario.

Estas afirmaciones se clasifican como:

`CONFIRMACIONES OPERATIVAS RETROSPECTIVAS`

No se presentan como evidencia instrumental independiente.

---

### 3.4 Control previo de BASE-R2

Identificador:

`S11-A4-BASE-R2-CONTROL`

Inicio:

`17:57:19.284`

Resultado:

`HTTP 201`

Fin:

`17:57:19.544`

El control constituye evidencia técnica de que, después del `HTTP 401` y del login posterior, la aplicación volvió a realizar una creación remota válida.

---

### 3.5 Secuencia temporal previa al experimento

La secuencia reconstruida es:

1. POST de creación con `HTTP 401`;
2. login posterior con `HTTP 200`;
3. control posterior con `HTTP 201`;
4. detención posterior de Spring Boot;
5. comprobación del puerto `8080` sin listener;
6. POST experimental.

Los puntos 1, 2 y 3 cuentan con registros técnicos fechados.

Los puntos 4 y 5 fueron confirmados posteriormente por el operador de la prueba, pero no cuentan con una salida instrumental fechada equivalente.

Por tanto, estas fuentes de evidencia se mantienen diferenciadas.

---

### 3.6 Inducción del fallo de BASE-R2

Se confirmó operativamente que el procedimiento aplicado fue:

1. detener Spring Boot;
2. comprobar el puerto `8080`;
3. observar que no tenía listener;
4. pulsar posteriormente `Guardar` para `S11-A4-BASE-R2`.

Confirmación operativa:

`PUERTO 8080 SIN LISTENER ANTES DE GUARDAR`

Limitación documental:

`NO SE CONSERVÓ UNA SALIDA POWERSHELL CON TIMESTAMP EXACTO DE ESA COMPROBACIÓN`

Por tanto:

- existe confirmación operativa de la comprobación;
- no existe evidencia instrumental independiente con hora exacta de esa comprobación.

Esta limitación se conserva explícitamente.

---

### 3.7 Intento experimental

Identificador:

`S11-A4-BASE-R2`

Inicio:

`18:00:35.191`

Solicitud:

`POST /api/activities`

Hilo OkHttp:

`7878`

Resultado:

`SocketTimeoutException`

Fin:

`18:00:45.224`

El `SocketTimeoutException` constituye evidencia técnica de un fallo de conexión.

No se utiliza, por sí solo, como demostración de que Spring Boot estuviera detenido.

---

### 3.8 Correlación HTTP

El inicio y el fallo del POST experimental pueden correlacionarse mediante:

- mismo hilo OkHttp;
- mismo endpoint;
- proximidad temporal;
- secuencia registrada;
- ausencia de otro POST experimental concurrente identificado en esa ventana.

Inicio:

`18:00:35.191`

Fin:

`18:00:45.224`

Hilo:

`7878`

Estado:

`CORRELACIÓN SUFICIENTE PARA LA MEDICIÓN`

---

### 3.9 Duración técnica

La métrica preregistrada corresponde al intervalo:

`--> POST`

hasta:

`HTTP FAILED`

Inicio:

`18:00:35.191`

Fin:

`18:00:45.224`

Cálculo:

`45.224 - 35.191 = 10.033 segundos`

Duración:

`10 033 ms`

Valor admitido:

`B2 = 10 033 ms`

---

### 3.10 Resultado Android

Android mostró:

`No fue posible crear la actividad`

No se observó que Android presentara el intento experimental como exitoso.

Resultado:

`ERROR MOSTRADO — SIN FALSO ÉXITO OBSERVADO`

---

### 3.11 Persistencia remota

Consulta PostgreSQL por:

`S11-A4-BASE-R2`

Resultado:

`0 filas`

Interpretación:

`NO SE ENCONTRÓ PERSISTENCIA REMOTA CON EL IDENTIFICADOR EXPERIMENTAL EN LA CONSULTA REALIZADA`

La afirmación se limita estrictamente al resultado observado de esa consulta.

---

### 3.12 Separación entre control y experimento

Control:

`S11-A4-BASE-R2-CONTROL`

Experimento:

`S11-A4-BASE-R2`

La reutilización de identificador ocurrida en BASE-R1:

`NO SE REPITIÓ`

Esto permite consultar PostgreSQL por el identificador experimental sin confundirlo con el control.

---

### 3.13 Guardado único

Después de la ejecución se confirmó operativamente que para:

`S11-A4-BASE-R2`

se pulsó:

`GUARDAR UNA SOLA VEZ`

No se realizó una segunda pulsación de `Guardar` sobre la actividad experimental antes de consultar PostgreSQL.

Tipo de evidencia:

`CONFIRMACIÓN OPERATIVA RETROSPECTIVA`

---

### 3.14 Cuatro confirmaciones operativas

Después de la ejecución se confirmaron las siguientes condiciones.

#### 1. Mismo usuario y aplicación abierta

Respuesta:

`SÍ`

Tipo de evidencia:

`CONFIRMACIÓN OPERATIVA RETROSPECTIVA`

#### 2. Sin cierre, reinicio ni nuevo login

Respuesta:

`SÍ`

Tipo de evidencia:

`CONFIRMACIÓN OPERATIVA RETROSPECTIVA`

#### 3. Una sola pulsación de Guardar

Respuesta:

`SÍ`

Tipo de evidencia:

`CONFIRMACIÓN OPERATIVA RETROSPECTIVA`

#### 4. Puerto 8080 sin listener antes de Guardar

Respuesta:

`SÍ`

Tipo de evidencia:

`CONFIRMACIÓN OPERATIVA RETROSPECTIVA`

Limitación:

`NO EXISTE UNA SALIDA POWERSHELL CONSERVADA CON TIMESTAMP EXACTO DE ESA COMPROBACIÓN`

---

### 3.15 Evidencia técnica conservada

Se dispone de registros técnicos para:

- `HTTP 401` previo;
- login posterior con `HTTP 200`;
- control posterior con `HTTP 201`;
- inicio del POST experimental;
- fallo del POST experimental;
- `SocketTimeoutException`;
- duración técnica de `10 033 ms`;
- resultado visible de error en Android;
- consulta PostgreSQL con `0 filas`;
- SHA-256 del Logcat privado.

---

### 3.16 Evidencia operativa retrospectiva

Se confirmó posteriormente que:

- RachaPro permaneció abierta;
- se mantuvo el mismo usuario;
- no hubo reinicio;
- no hubo cierre;
- no hubo nuevo login;
- se realizó un único `Guardar`;
- el puerto `8080` se comprobó sin listener antes del POST experimental.

Estas confirmaciones no se describen como evidencia instrumental.

---

### 3.17 Limitación documental principal

La principal limitación documental de BASE-R2 es:

`NO SE CONSERVÓ UNA SALIDA INSTRUMENTAL CON TIMESTAMP EXACTO QUE MUESTRE EL PUERTO 8080 SIN LISTENER INMEDIATAMENTE ANTES DEL POST EXPERIMENTAL`

Asimismo:

`SocketTimeoutException`

demuestra un fallo de conexión, pero:

`NO DEMUESTRA POR SÍ SOLO QUE SPRING BOOT ESTUVIERA DETENIDO`

Esta limitación se conserva explícitamente en el análisis.

---

### 3.18 Justificación de la clasificación metodológica

La clasificación de BASE-R2 se realiza contrastando la evidencia disponible con los criterios de evaluabilidad registrados previamente.

Considero suficiente la verificación de la condición experimental porque existe una combinación consistente de:

1. comprobación operativa de que el puerto `8080` estaba sin listener antes de pulsar `Guardar`;
2. control previo exitoso con `HTTP 201`;
3. POST experimental posterior terminado en `SocketTimeoutException`;
4. continuidad de sesión confirmada operativamente;
5. una única pulsación de `Guardar`;
6. ausencia de falso éxito en Android;
7. `0 filas` observadas en PostgreSQL para el identificador experimental;
8. identificadores diferentes para control y experimento;
9. correlación suficiente entre el inicio y el fallo de la solicitud experimental.

La ausencia de una salida PowerShell fechada de la comprobación del puerto se considera una:

`LIMITACIÓN DOCUMENTAL`

y no se presenta como si dicha evidencia instrumental existiera.

Mi decisión metodológica es que esta limitación no impide, en este caso, acreditar suficientemente la corrida mediante la combinación de evidencia técnica y confirmaciones operativas disponibles.

Esta conclusión representa una decisión de evaluación sobre la suficiencia de la evidencia y no una consecuencia automática del `SocketTimeoutException`.

---

### 3.19 Decisión metodológica final

Clasificación:

`EVALUABLE CON INCIDENCIA PREVIA RESUELTA`

La incidencia de autenticación `HTTP 401` ocurrió antes del control válido.

Posteriormente se observaron:

- login exitoso con `HTTP 200`;
- control exitoso con `HTTP 201`.

Además, se confirmó operativamente la continuidad de la sesión hasta el POST experimental.

Por tanto, la incidencia previa no se considera contaminante para la medición utilizada.

Valor admitido:

`B2 = 10 033 ms`

Participación en la mediana:

`SÍ`

Reemplazo:

`NO REQUERIDO`

---

### 3.20 Integridad de evidencia BASE-R2

Archivo privado:

`evidencia-timeout/BASE-R2/logcat-BASE-R2-privado.txt`

SHA-256:

`2BF5FDDEA23BF2D4644A616BF46DE9A1B9172EA00091E30348FA5CB571F7F907`

El archivo permanece como evidencia privada.

No deberá versionarse directamente sin revisión y saneamiento.

---

### 3.21 Resultado consolidado BASE-R2

Estado:

`EVALUABLE CON INCIDENCIA PREVIA RESUELTA`

Valor:

`B2 = 10 033 ms`

Tipo de error:

`SocketTimeoutException`

Falso éxito Android:

`NO OBSERVADO`

Persistencia del identificador experimental:

`0 FILAS OBSERVADAS`

Separación control/experimento:

`SÍ`

Incidencia previa:

`HTTP 401`

Login posterior:

`HTTP 200`

Control posterior:

`HTTP 201`

Continuidad de sesión:

`CONFIRMADA OPERATIVAMENTE`

Reinicio de aplicación:

`NO, SEGÚN CONFIRMACIÓN OPERATIVA`

Nuevo login antes del experimento:

`NO, SEGÚN CONFIRMACIÓN OPERATIVA`

Guardar único:

`SÍ, SEGÚN CONFIRMACIÓN OPERATIVA`

Puerto `8080` sin listener antes del POST:

`SÍ, SEGÚN CONFIRMACIÓN OPERATIVA`

Evidencia instrumental fechada de esa comprobación:

`NO CONSERVADA`

Correlación HTTP:

`SUFICIENTE`

Participación en mediana:

`SÍ`

Reemplazo:

`NO`

---

## 4. BASE-R3

Estado:

`PENDIENTE DE EJECUCIÓN`

Identificador de control previsto:

`S11-A4-BASE-R3-CONTROL`

Identificador experimental previsto:

`S11-A4-BASE-R3`

Valor:

`B3 = PENDIENTE`

BASE-R3 deberá ejecutarse manteniendo la condición:

`LÍNEA BASE — SIN connectTimeout EXPLÍCITO`

No se modificará todavía:

`RetrofitClient.kt`

No se registrarán resultados antes de ejecutar la corrida.

---

## 5. Estado de los valores de línea base

### BASE-R1

Estado:

`EVALUABLE CON DESVIACIÓN DOCUMENTADA`

Valor admitido:

`B1 = 10 028 ms`

### BASE-R2

Estado:

`EVALUABLE CON INCIDENCIA PREVIA RESUELTA`

Valor admitido:

`B2 = 10 033 ms`

### BASE-R3

Estado:

`NO EJECUTADA`

Valor:

`B3 = PENDIENTE`

---

## 6. Mediana de línea base

La mediana preregistrada solo se calculará cuando existan:

`3 CORRIDAS EVALUABLES`

Actualmente:

- `B1 = 10 028 ms`
- `B2 = 10 033 ms`
- `B3 = PENDIENTE`

Corridas evaluables:

`2 de 3`

Mediana:

`NO CALCULADA`

No se calculará la mediana definitiva hasta ejecutar y clasificar BASE-R3.

---

## 7. Reemplazos de línea base

Máximo preregistrado:

`1 REEMPLAZO POR CONDICIÓN`

Condición:

`BASE`

Reemplazos utilizados:

`0`

BASE-R1:

`NO REQUIRIÓ REEMPLAZO`

BASE-R2:

`NO REQUIRIÓ REEMPLAZO`

BASE-R3:

`PENDIENTE`

No se realizarán repeticiones adicionales motivadas por los valores temporales observados.

---

## 8. Intervención experimental

Estado:

`NO IMPLEMENTADA`

Cambio previsto:

`connectTimeout = 3 segundos`

Archivo previsto:

`RetrofitClient.kt`

Estado:

`SIN MODIFICAR`

No se modificarán antes de cerrar la línea base:

- `connectTimeout`;
- `readTimeout`;
- `writeTimeout`;
- `callTimeout`;
- retry;
- fallback;
- circuit breaker.

La intervención solo podrá comenzar después de completar tres corridas evaluables de línea base.

---

## 9. Corridas posteriores a la intervención

### TIMEOUT3-R1

Estado:

`NO EJECUTADA`

Valor:

`T1 = PENDIENTE`

### TIMEOUT3-R2

Estado:

`NO EJECUTADA`

Valor:

`T2 = PENDIENTE`

### TIMEOUT3-R3

Estado:

`NO EJECUTADA`

Valor:

`T3 = PENDIENTE`

---

## 10. Mediana posterior

Valores:

- `T1 = PENDIENTE`
- `T2 = PENDIENTE`
- `T3 = PENDIENTE`

Mediana:

`PENDIENTE`

---

## 11. Reducción porcentual

La reducción se calculará únicamente después de disponer de:

- tres corridas evaluables antes;
- tres corridas evaluables después.

Fórmula preregistrada:

\[
Reducción(\%) =
\frac{M_{antes} - M_{después}}
{M_{antes}}
\times 100
\]

Resultado:

`PENDIENTE`

Criterio preregistrado:

`REDUCCIÓN >= 50 %`

El criterio no será modificado después de observar los resultados.

---

## 12. Estado de la hipótesis

Hipótesis preregistrada:

> Ante una indisponibilidad controlada de Spring Boot, configurar explícitamente `connectTimeout = 3 segundos` reducirá al menos un 50 % la mediana de la duración técnica de los `POST` fallidos de creación de actividad respecto de una nueva línea base ejecutada bajo el mismo protocolo, sin que Android presente falsamente la operación como exitosa ni se observe persistencia remota inesperada de la actividad intentada durante el fallo.

Estado actual:

`NO EVALUADA`

Todavía no existe evidencia suficiente para clasificarla como:

- respaldada;
- no respaldada;
- comparación principal no evaluable.

---

## 13. Trazabilidad temporal

Secuencia registrada hasta este momento:

`prerregistro -> BASE-R1 -> registro BASE-R1 -> BASE-R2 -> registro provisional BASE-R2 -> cierre metodológico BASE-R2`

Commit de prerregistro:

`94dcc32`

Commit de resultados BASE-R1:

`b02c239`

Commit provisional BASE-R2:

`e1dab87`

Commit de cierre metodológico BASE-R2:

`PENDIENTE`

BASE-R3:

`NO EJECUTADA`

Intervención:

`NO REALIZADA`

Commit de intervención:

`PENDIENTE`

Mediciones posteriores:

`NO REALIZADAS`

Commit de resultados finales:

`PENDIENTE`

No se modificará retroactivamente el commit de prerregistro para incorporar evidencia o decisiones observadas posteriormente.

---

## 14. Evidencia privada

Los Logcat completos permanecen fuera del repositorio público.

### BASE-R1

Archivo:

`evidencia-timeout/BASE-R1/logcat-BASE-R1-privado.txt`

SHA-256:

`DE5066FCF6BA7B86E1BD12FCA3041AD77513C38499E25578EB92DD27DA6E1AE5`

### BASE-R2

Archivo:

`evidencia-timeout/BASE-R2/logcat-BASE-R2-privado.txt`

SHA-256:

`2BF5FDDEA23BF2D4644A616BF46DE9A1B9172EA00091E30348FA5CB571F7F907`

Los archivos privados no deberán incorporarse al repositorio sin:

- revisión;
- saneamiento;
- comprobación de credenciales;
- eliminación de JWT;
- eliminación de encabezados sensibles;
- eliminación de información innecesaria.

---

## 15. Estado actual de la Actividad 4

Prerregistro:

`COMPLETADO`

Commit:

`94dcc32`

BASE-R1:

`COMPLETADA — EVALUABLE CON DESVIACIÓN DOCUMENTADA`

Valor:

`B1 = 10 028 ms`

BASE-R2:

`COMPLETADA — EVALUABLE CON INCIDENCIA PREVIA RESUELTA`

Valor:

`B2 = 10 033 ms`

BASE-R3:

`PENDIENTE`

Corridas evaluables de línea base:

`2 de 3`

Mediana de línea base:

`NO CALCULADA`

Reemplazos utilizados:

`0`

Intervención de timeout:

`NO IMPLEMENTADA`

Mediciones posteriores:

`NO EJECUTADAS`

Comparación antes/después:

`PENDIENTE`

Hipótesis:

`NO EVALUADA`

Conclusión arquitectónica:

`PENDIENTE`

---

## 16. Próximo punto de control

BASE-R2 queda cerrada metodológicamente como:

`EVALUABLE CON INCIDENCIA PREVIA RESUELTA`

Valor admitido:

`B2 = 10 033 ms`

La limitación documental conservada es:

`NO SE DISPONE DE UNA SALIDA POWERSHELL CON TIMESTAMP EXACTO DE LA COMPROBACIÓN DEL PUERTO 8080 SIN LISTENER`

Esta limitación:

- permanece documentada;
- no se elimina;
- no se convierte retrospectivamente en evidencia instrumental;
- no se atribuye al `SocketTimeoutException` una capacidad probatoria que no posee por sí solo.

El siguiente paso corresponde a ejecutar:

`BASE-R3`

manteniendo:

`LÍNEA BASE — SIN connectTimeout EXPLÍCITO`

Identificador de control:

`S11-A4-BASE-R3-CONTROL`

Identificador experimental:

`S11-A4-BASE-R3`

Durante BASE-R3 deberá conservarse explícitamente:

- Spring Boot disponible antes del control;
- PostgreSQL disponible;
- sesión autenticada;
- control exitoso;
- detención de Spring Boot;
- comprobación de puerto `8080` sin listener;
- timestamp de la comprobación;
- una sola pulsación de `Guardar`;
- Logcat privado;
- inicio del POST;
- resultado del POST;
- duración técnica;
- resultado Android;
- consulta PostgreSQL antes de cualquier segundo guardado;
- SHA-256 de la evidencia privada;
- cualquier desviación observada.

`RetrofitClient.kt` deberá permanecer sin cambios hasta completar y cerrar las tres corridas evaluables de línea base.