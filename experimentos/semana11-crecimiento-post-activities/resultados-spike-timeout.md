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

Commit de cierre metodológico de BASE-R2:

`01d8e6a`

Commit de cierre de línea base:

`b6f82a5`

Merge del cierre de línea base:

`be2b2f3`

Condición experimental actual:

`POST-INTERVENCIÓN TIMEOUT3`

Configuración HTTP experimental:

`connectTimeout = 3 segundos`

Commit de intervención:

`a722954`

Corridas principales previstas de línea base:

`3`

Estado de BASE-R1:

`EJECUTADA — EVALUABLE CON DESVIACIÓN DOCUMENTADA`

Estado de BASE-R2:

`EJECUTADA — EVALUABLE CON INCIDENCIA PREVIA RESUELTA`

Estado de BASE-R3:

`EJECUTADA — EVALUABLE CON INCIDENCIA DOCUMENTADA`

Mediana de línea base:

`M_antes = 10 028 ms`

Intervención experimental:

`IMPLEMENTADA`

Estado de TIMEOUT3-R1:

`EJECUTADA — NO EVALUABLE POR TRAZABILIDAD FUNCIONAL INSUFICIENTE`

Valor T1:

`NO ASIGNADO`

Reemplazo de TIMEOUT3-R1:

`PROCEDE SEGÚN PRERREGISTRO — AÚN NO EJECUTADO`

TIMEOUT3-R2:

`NO EJECUTADA`

TIMEOUT3-R3:

`NO EJECUTADA`

Mediana posterior:

`NO CALCULADA`

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

---

### 2.9 Desviación metodológica

Se reutilizó el identificador:

`S11-A4-BASE-R1`

para control y experimento.

Esto reduce la trazabilidad funcional en PostgreSQL.

No obstante, las solicitudes HTTP pueden distinguirse técnicamente mediante:

- timestamps;
- hilos OkHttp;
- tamaños del body;
- resultados diferentes.

---

### 2.10 Evaluabilidad de BASE-R1

Clasificación:

`EVALUABLE CON DESVIACIÓN DOCUMENTADA`

Esta clasificación constituye una decisión metodológica y no una consecuencia automática del Logcat.

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

`SUFICIENTE`

Reemplazo:

`NO`

---

## 3. BASE-R2

Estado:

`EVALUABLE CON INCIDENCIA PREVIA RESUELTA`

Valor admitido:

`B2 = 10 033 ms`

Commit provisional previo al cierre:

`e1dab87`

Commit de cierre metodológico:

`01d8e6a`

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

Inicio:

`17:56:47.083`

Solicitud:

`POST /api/activities`

Resultado:

`HTTP 401`

Fin:

`17:56:47.298`

Este POST terminó antes del login posterior.

---

### 3.3 Restablecimiento de autenticación

Inicio:

`17:56:55.297`

Solicitud:

`POST /api/auth/login`

Resultado:

`HTTP 200`

Fin:

`17:56:57.802`

Después de la ejecución se confirmó operativamente que:

- RachaPro permaneció abierta;
- se mantuvo el mismo usuario;
- no hubo cierre;
- no hubo reinicio;
- no hubo nuevo login.

Estas afirmaciones son:

`CONFIRMACIONES OPERATIVAS RETROSPECTIVAS`

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

---

### 3.5 Secuencia temporal

La secuencia reconstruida es:

1. POST con `HTTP 401`;
2. login con `HTTP 200`;
3. control con `HTTP 201`;
4. detención de Spring Boot;
5. comprobación operativa del puerto;
6. POST experimental.

Los puntos 1, 2 y 3 cuentan con registros técnicos fechados.

Los puntos 4 y 5 cuentan con confirmación operativa retrospectiva.

---

### 3.6 Inducción del fallo

Se confirmó operativamente que:

1. se detuvo Spring Boot;
2. se comprobó `8080`;
3. no había listener;
4. después se pulsó `Guardar`.

Limitación:

`NO SE CONSERVÓ UNA SALIDA POWERSHELL CON TIMESTAMP EXACTO DE ESA COMPROBACIÓN`

---

### 3.7 Intento experimental

Identificador:

`S11-A4-BASE-R2`

Inicio:

`18:00:35.191`

Hilo:

`7878`

Resultado:

`SocketTimeoutException`

Fin:

`18:00:45.224`

El `SocketTimeoutException` demuestra un fallo de conexión.

No demuestra por sí solo que Spring Boot estuviera detenido.

---

### 3.8 Correlación HTTP

Estado:

`CORRELACIÓN SUFICIENTE PARA LA MEDICIÓN`

Inicio:

`18:00:35.191`

Fin:

`18:00:45.224`

Hilo:

`7878`

---

### 3.9 Duración técnica

Cálculo:

`45.224 - 35.191 = 10.033 segundos`

Valor:

`B2 = 10 033 ms`

---

### 3.10 Resultado Android

Resultado:

`No fue posible crear la actividad`

Falso éxito:

`NO OBSERVADO`

---

### 3.11 Persistencia remota

Consulta por:

`S11-A4-BASE-R2`

Resultado:

`0 filas`

---

### 3.12 Separación control/experimento

Control:

`S11-A4-BASE-R2-CONTROL`

Experimento:

`S11-A4-BASE-R2`

La desviación de BASE-R1 no se repitió.

---

### 3.13 Guardado único

Se confirmó operativamente:

`GUARDAR UNA SOLA VEZ`

para:

`S11-A4-BASE-R2`

---

### 3.14 Confirmaciones operativas

Se confirmó retrospectivamente:

- mismo usuario;
- aplicación abierta;
- sin reinicio;
- sin nuevo login;
- un único Guardar;
- puerto `8080` sin listener antes del POST experimental.

Estas confirmaciones no se presentan como registros instrumentales.

---

### 3.15 Limitación documental

Principal limitación:

`NO SE CONSERVÓ UNA SALIDA INSTRUMENTAL CON TIMESTAMP EXACTO DEL PUERTO 8080 SIN LISTENER`

---

### 3.16 Justificación metodológica

Se consideró suficiente la combinación de:

- control previo `HTTP 201`;
- continuidad de sesión;
- comprobación operativa del puerto;
- `SocketTimeoutException`;
- un único Guardar;
- ausencia de falso éxito;
- `0 filas` en PostgreSQL.

Esta es una decisión metodológica de suficiencia de evidencia y no una consecuencia automática del `SocketTimeoutException`.

---

### 3.17 Decisión metodológica final

Clasificación:

`EVALUABLE CON INCIDENCIA PREVIA RESUELTA`

Valor:

`B2 = 10 033 ms`

Participación en mediana:

`SÍ`

Reemplazo:

`NO REQUERIDO`

---

### 3.18 Integridad de evidencia BASE-R2

Archivo privado:

`evidencia-timeout/BASE-R2/logcat-BASE-R2-privado.txt`

SHA-256:

`2BF5FDDEA23BF2D4644A616BF46DE9A1B9172EA00091E30348FA5CB571F7F907`

---

### 3.19 Resultado consolidado BASE-R2

Estado:

`EVALUABLE CON INCIDENCIA PREVIA RESUELTA`

Valor:

`B2 = 10 033 ms`

Tipo de error:

`SocketTimeoutException`

Falso éxito:

`NO OBSERVADO`

Persistencia experimental:

`0 FILAS`

Reemplazo:

`NO`

---

## 4. BASE-R3

Estado:

`EJECUTADA — EVALUABLE CON INCIDENCIA DOCUMENTADA`

Valor técnico observado:

`10 023 ms`

Admisión como B3:

`ADMITIDA — B3 = 10 023 ms`

Condición:

`LÍNEA BASE — SIN connectTimeout EXPLÍCITO`

Fecha:

`2026-10-18`

Dispositivo:

`DISPOSITIVO FÍSICO ANDROID`

Versión Android:

`14`

Identificador de control:

`S11-A4-BASE-R3-CONTROL`

Identificador experimental:

`S11-A4-BASE-R3`

---

### 4.1 Control previo

El Logcat registró una creación remota previa al fallo.

Inicio:

`19:10:45.065`

Solicitud:

`POST /api/activities`

Hilo OkHttp:

`10117`

Tamaño del body:

`113 bytes`

Resultado:

`HTTP 201`

Fin:

`19:10:45.127`

Duración informada por OkHttp:

`60 ms`

Esta solicitud constituye evidencia técnica de que el backend respondió exitosamente a una creación antes de inducir la indisponibilidad.

---

### 4.2 Persistencia observada del identificador de control

Se consultó PostgreSQL por:

`S11-A4-BASE-R3-CONTROL`

Resultado:

`2 filas`

Filas observadas:

- `ID 1500181`
- `ID 1500180`

Usuario:

`1507`

Estado:

`PENDING`

Eliminadas:

`false`

---

### 4.3 Incidencia del control duplicado

La existencia de dos filas con el identificador:

`S11-A4-BASE-R3-CONTROL`

constituye una incidencia de trazabilidad del control.

El Logcat presentado permite identificar un POST de control con resultado:

`HTTP 201`

a las:

`19:10:45.127`

Sin embargo, con la evidencia consolidada no se ha determinado inequívocamente el origen de cada una de las dos filas observadas en PostgreSQL.

Por tanto:

`NO SE ATRIBUYE AUTOMÁTICAMENTE LA CREACIÓN DE AMBAS FILAS A UNA ÚNICA ACCIÓN`

y tampoco se afirma que:

`UNA DE LAS FILAS FUERA NECESARIAMENTE PREEXISTENTE`

Procedencia exacta:

`NO DETERMINADA CON LA EVIDENCIA ACTUAL`

La incidencia afecta principalmente la trazabilidad del control y debe contrastarse con los criterios de evaluabilidad del prerregistro.

---

### 4.4 Relación de la incidencia con el POST experimental

El identificador experimental fue:

`S11-A4-BASE-R3`

mientras que el identificador del control fue:

`S11-A4-BASE-R3-CONTROL`

Por tanto, las dos filas observadas para el control no se confunden nominalmente con la actividad experimental.

La consulta posterior del identificador experimental:

`S11-A4-BASE-R3`

devolvió:

`0 filas`

No existe actualmente evidencia que demuestre que la duplicidad observada en el control haya:

- generado un segundo POST experimental;
- alterado el identificador experimental;
- provocado persistencia bajo `S11-A4-BASE-R3`;
- impedido correlacionar el POST experimental;
- cambiado el resultado Android;
- modificado la duración técnica medida.

Esto no elimina la incidencia del control.

Únicamente establece que:

`ANOMALÍA EN EL CONTROL`

y:

`ALTERACIÓN COMPROBADA DE LA EJECUCIÓN EXPERIMENTAL`

no deben tratarse como equivalentes sin evidencia que las vincule.

---

### 4.5 Posible criterio del prerregistro afectado

La duplicidad del control podría afectar los criterios relacionados con:

- trazabilidad de las operaciones;
- unicidad y distinción de las acciones realizadas durante la corrida;
- ausencia de operaciones adicionales que dificulten atribuir los efectos observados;
- capacidad de reconstruir inequívocamente la secuencia experimental.

El punto a evaluar no es simplemente la existencia de dos filas.

La cuestión metodológica es determinar si esa duplicidad impide reconstruir o verificar suficientemente la operación experimental principal.

El POST experimental dispone de:

- identificador propio;
- inicio propio;
- hilo OkHttp propio;
- fallo propio;
- comprobación instrumental del puerto previa;
- consulta PostgreSQL independiente.

La incidencia quedó posteriormente clasificada mediante la decisión metodológica final documentada en la sección 4.15.

---

### 4.6 Inducción del fallo

Después del control se detuvo Spring Boot.

Se realizó una comprobación instrumental del puerto:

`8080`

Hora registrada:

`19:11:54.078`

Listeners observados:

`0`

Esta comprobación ocurrió antes del POST experimental de las:

`19:12:23.162`

Para BASE-R3 se conserva una comprobación fechada de ausencia de listener antes de ejecutar la solicitud experimental.

---

### 4.7 Intento experimental

Identificador:

`S11-A4-BASE-R3`

Inicio:

`19:12:23.162`

Solicitud:

`POST /api/activities`

Hilo OkHttp:

`10172`

Tamaño del body:

`103 bytes`

Resultado:

`SocketTimeoutException`

Fin:

`19:12:33.185`

Mensaje técnico:

`failed to connect to /192.168.1.20 (port 8080) from /192.168.1.49 (port 41644) after 10000ms`

---

### 4.8 Correlación HTTP

Inicio:

`19:12:23.162`

Fin:

`19:12:33.185`

Hilo:

`10172`

Endpoint:

`POST /api/activities`

Resultado:

`SocketTimeoutException`

La correlación entre el inicio y el fallo del POST experimental es:

`SUFICIENTE PARA CALCULAR LA DURACIÓN`

---

### 4.9 Duración técnica observada

La métrica preregistrada corresponde al intervalo:

`--> POST`

hasta:

`HTTP FAILED`

Inicio:

`19:12:23.162`

Fin:

`19:12:33.185`

Cálculo:

`33.185 - 23.162 = 10.023 segundos`

Duración técnica observada:

`10 023 ms`

Tras cerrar la clasificación metodológica de BASE-R3, este resultado se admite como:

`B3 = 10 023 ms`

Este valor participa en la mediana principal de línea base.

---

### 4.10 Resultado Android

Android mostró:

`No fue posible crear la actividad`

Resultado funcional:

`ERROR MOSTRADO — SIN FALSO ÉXITO OBSERVADO`

---

### 4.11 Persistencia remota experimental

Se consultó PostgreSQL por:

`S11-A4-BASE-R3`

Resultado:

`0 filas`

Interpretación limitada a lo observado:

`NO SE ENCONTRÓ PERSISTENCIA REMOTA CON EL IDENTIFICADOR EXPERIMENTAL EN LA CONSULTA REALIZADA`

Esta consulta es nominalmente independiente de las filas:

`S11-A4-BASE-R3-CONTROL`

---

### 4.12 Guardado experimental

Se confirmó operativamente que para:

`S11-A4-BASE-R3`

se pulsó:

`GUARDAR UNA SOLA VEZ`

Tipo de evidencia:

`CONFIRMACIÓN OPERATIVA`

Esta confirmación se refiere al intento experimental.

No se utiliza para explicar automáticamente la procedencia de las dos filas del control.

---

### 4.13 Evidencia instrumental conservada

Para BASE-R3 se dispone de evidencia técnica de:

- POST de control con `HTTP 201`;
- comprobación del puerto `8080`;
- `0 listeners` a las `19:11:54.078`;
- POST experimental a las `19:12:23.162`;
- `SocketTimeoutException` a las `19:12:33.185`;
- duración técnica de `10 023 ms`;
- resultado de error visible en Android;
- `0 filas` para `S11-A4-BASE-R3`;
- dos filas para `S11-A4-BASE-R3-CONTROL`.

---

### 4.14 Desviación o incidencia metodológica

Incidencia:

`DOS FILAS DE CONTROL CON EL MISMO IDENTIFICADOR`

Filas:

- `1500180`
- `1500181`

Procedencia exacta:

`NO DETERMINADA`

El posible criterio metodológico afectado corresponde a la trazabilidad y reconstrucción inequívoca de las operaciones previas de la corrida.

No obstante, la evidencia disponible también muestra que:

- el identificador experimental fue distinto;
- el puerto `8080` tenía `0 listeners` antes del experimento;
- el POST experimental es correlacionable;
- se confirmó una sola pulsación de Guardar experimental;
- PostgreSQL devolvió `0 filas` para el identificador experimental;
- Android no mostró falso éxito.

Por tanto, se documenta:

`UNA ANOMALÍA COMPROBADA EN EL CONTROL`

pero no:

`UNA ALTERACIÓN COMPROBADA DEL POST EXPERIMENTAL`

La incidencia permanece documentada como una limitación de trazabilidad del control, pero no se considera suficiente para invalidar el POST experimental, cuya ejecución puede reconstruirse y verificarse de forma independiente con la evidencia disponible.

---

### 4.15 Decisión metodológica final

BASE-R3 dispone de:

- medición técnica calculable;
- evidencia instrumental fechada de ausencia de listener;
- identificador experimental independiente;
- correlación HTTP suficiente;
- resultado Android;
- consulta PostgreSQL independiente;
- confirmación de un único Guardar experimental.

La incidencia observada corresponde a:

`DOS FILAS DE CONTROL CON EL MISMO IDENTIFICADOR`

y afecta la trazabilidad del control previo. Su procedencia exacta permanece:

`NO DETERMINADA`

No obstante, la evidencia disponible permite reconstruir de manera independiente el POST experimental mediante:

- el identificador `S11-A4-BASE-R3`;
- la comprobación de `0 listeners` en el puerto `8080` a las `19:11:54.078`;
- el inicio del POST a las `19:12:23.162`;
- el hilo OkHttp `10172`;
- el `SocketTimeoutException` a las `19:12:33.185`;
- la duración técnica de `10 023 ms`;
- la ausencia de falso éxito en Android;
- `0 filas` en PostgreSQL para `S11-A4-BASE-R3`;
- la confirmación operativa de una sola pulsación de Guardar para el intento experimental.

Por tanto, la duplicidad del control se conserva como una incidencia metodológica documentada, pero no se considera suficiente para invalidar la medición experimental principal.

Decisión metodológica final:

`EVALUABLE CON INCIDENCIA DOCUMENTADA`

Valor admitido:

`B3 = 10 023 ms`

Participación en la mediana:

`SÍ`

Reemplazo:

`NO REQUERIDO`

La decisión se adopta por la suficiencia de la evidencia del POST experimental y no por conveniencia respecto del valor temporal observado.

---

### 4.16 Clasificaciones consideradas y decisión adoptada

Durante la revisión metodológica se consideraron dos posibilidades.

#### Opción considerada: EVALUABLE CON INCIDENCIA DOCUMENTADA

Aplicaba si la duplicidad del control no impedía reconstruir ni verificar suficientemente el POST experimental.

Consecuencia prevista:

`B3 = 10 023 ms`

con participación en la mediana.

#### Opción considerada: NO EVALUABLE

Habría aplicado si la duplicidad hubiera impedido verificar suficientemente el protocolo o atribuir de forma confiable la medición al POST experimental.

En ese caso:

`10 023 ms`

se habría conservado únicamente como observación descriptiva y habría podido utilizarse la regla de reemplazo preregistrada, si correspondía.

#### Decisión adoptada

Tras contrastar la incidencia con la evidencia disponible de la ejecución experimental, se adopta:

`EVALUABLE CON INCIDENCIA DOCUMENTADA`

La alternativa:

`NO EVALUABLE`

queda conservada únicamente como opción metodológica considerada durante la revisión y no como clasificación final.

No se seleccionó la clasificación en función del valor temporal observado.

---

### 4.17 Integridad de evidencia BASE-R3

Archivo privado:

`evidencia-timeout/BASE-R3/logcat-BASE-R3-privado.txt`

SHA-256:

`6508512032222FCF374F0C8998018DB01E8D466734F0B2516F57128B1894D5FF`

El hash se registra con sus:

`64 CARACTERES HEXADECIMALES`

El Logcat privado no deberá versionarse directamente sin revisión y saneamiento.

---

### 4.18 Resultado consolidado final BASE-R3

Estado:

`EVALUABLE CON INCIDENCIA DOCUMENTADA`

Valor observado y admitido:

`B3 = 10 023 ms`

Control HTTP:

`201`

Duración informada del control:

`60 ms`

Filas de control observadas:

`2`

IDs:

`1500180, 1500181`

Procedencia exacta de la duplicidad:

`NO DETERMINADA`

Naturaleza de la incidencia:

`TRAZABILIDAD DEL CONTROL`

Alteración comprobada del POST experimental por esta incidencia:

`NO DEMOSTRADA`

Puerto `8080`:

`0 LISTENERS`

Hora de comprobación:

`19:11:54.078`

POST experimental:

`19:12:23.162`

Fallo experimental:

`19:12:33.185`

Tipo de error:

`SocketTimeoutException`

Duración técnica:

`10 023 ms`

Android:

`No fue posible crear la actividad`

Falso éxito:

`NO OBSERVADO`

Persistencia experimental:

`0 FILAS`

Guardar experimental:

`UNA PULSACIÓN SEGÚN CONFIRMACIÓN OPERATIVA`

SHA-256:

`6508512032222FCF374F0C8998018DB01E8D466734F0B2516F57128B1894D5FF`

Participación en la mediana:

`SÍ`

Reemplazo:

`NO REQUERIDO`

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

`EVALUABLE CON INCIDENCIA DOCUMENTADA`

Valor admitido:

`B3 = 10 023 ms`

Incidencia conservada:

`DOS FILAS PARA S11-A4-BASE-R3-CONTROL — PROCEDENCIA EXACTA NO DETERMINADA`

---

## 6. Mediana de línea base

La mediana preregistrada se calcula con las tres corridas evaluables admitidas:

- `B1 = 10 028 ms`
- `B2 = 10 033 ms`
- `B3 = 10 023 ms`

Corridas ejecutadas:

`3 de 3`

Corridas admitidas:

`3 de 3`

Valores ordenados:

- `10 023 ms`
- `10 028 ms`
- `10 033 ms`

Cálculo:

`mediana(10 028, 10 033, 10 023) = 10 028 ms`

Mediana de línea base:

`M_antes = 10 028 ms`

La línea base queda metodológicamente cerrada con tres corridas evaluables.

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

`NO REQUIRIÓ REEMPLAZO`

La incidencia documentada de BASE-R3 no motivó reemplazo porque la corrida fue clasificada finalmente como:

`EVALUABLE CON INCIDENCIA DOCUMENTADA`

---

## 8. Intervención experimental

Estado:

`IMPLEMENTADA`

Cambio preregistrado:

`connectTimeout = 3 segundos`

Archivo modificado:

`app/src/main/java/com/example/rachapro/network/RetrofitClient.kt`

Commit de intervención:

`a722954`

Cambio aplicado:

`.connectTimeout(3, TimeUnit.SECONDS)`

Import requerido:

`java.util.concurrent.TimeUnit`

La intervención se registró después de cerrar la línea base.

La comparación experimental exige mantener constantes las demás variables. Por tanto, la intervención no incluye cambios deliberados en:

- `readTimeout`;
- `writeTimeout`;
- `callTimeout`;
- retry;
- fallback;
- circuit breaker;
- interceptores existentes.

La hipótesis no se considera evaluada por el solo hecho de implementar la intervención.

---

## 9. Corridas posteriores a la intervención

### 9.1 TIMEOUT3-R1

Estado de ejecución:

`EJECUTADA`

Clasificación metodológica final:

`NO EVALUABLE POR TRAZABILIDAD FUNCIONAL INSUFICIENTE`

Valor asignado a T1:

`NO ASIGNADO`

La corrida se conserva íntegramente como evidencia histórica y descriptiva.

No se elimina ni se reescribe por haber sido clasificada como no evaluable.

---

#### 9.1.1 Condición experimental

Configuración HTTP:

`connectTimeout = 3 segundos`

La intervención correspondía al commit:

`a722954`

La prueba se realizó bajo la condición posterior a la intervención.

---

#### 9.1.2 Comprobación de indisponibilidad

Puerto comprobado:

`8080`

Listeners observados:

`0`

Hora registrada:

`20:32:15.603`

Esta comprobación ocurrió antes de los POST fallidos considerados durante la ejecución.

---

#### 9.1.3 Intentos POST observados

Durante TIMEOUT3-R1 se observaron dos solicitudes:

`POST /api/activities`

con inicio y fallo correlacionables.

##### POST observado 1

Inicio:

`20:32:49.240`

Fallo:

`20:32:52.252`

Duración técnica observada:

`3 012 ms`

##### POST observado 2

Inicio:

`20:33:40.536`

Fallo:

`20:33:43.551`

Duración técnica observada:

`3 015 ms`

Ambos valores son mediciones técnicas observadas.

Ninguno se registra como:

`T1`

porque la corrida completa no satisface suficientemente la trazabilidad funcional requerida para ser admitida en la comparación principal.

---

#### 9.1.4 Orden funcional recordado

Según confirmación operativa retrospectiva, los títulos utilizados fueron, en este orden:

1. `S11-A4-TIMEOUT-R1`
2. `S11-A4-TIMEOUT3-R1`

El primer título no correspondía al identificador previsto para la corrida.

El segundo sí correspondía al identificador previsto.

Esta información se clasifica como:

`CONFIRMACIÓN OPERATIVA RETROSPECTIVA`

y no como:

`ATRIBUCIÓN INSTRUMENTAL DEL BODY A CADA POST`

El nivel de Logcat utilizado no expone el título funcional contenido en el body de cada solicitud.

Por ello, el Logcat por sí solo no permite demostrar inequívocamente cuál de los dos POST corresponde a cada título.

---

#### 9.1.5 Fallo adicional de las 20:32:25.078

Se observó además un fallo a las:

`20:32:25.078`

La auditoría posterior permitió establecer que dicho fallo correspondía a una solicitud:

`PATCH`

y no a:

`POST /api/activities`

Por tanto:

`NO SE CONTABILIZA COMO UN TERCER POST`

y no se utiliza para calcular una duración de creación de actividad.

Tampoco se inventa un inicio para esa operación.

---

#### 9.1.6 Resultado Android

Android mostró:

`No fue posible crear la actividad`

Falso éxito observado:

`NO`

La aplicación no presentó la creación experimental como confirmada exitosamente.

---

#### 9.1.7 Persistencia remota

Se realizó la consulta PostgreSQL correspondiente al identificador experimental.

Resultado:

`0 FILAS`

Interpretación limitada a lo observado:

`NO SE ENCONTRÓ PERSISTENCIA REMOTA DEL IDENTIFICADOR EXPERIMENTAL EN LA CONSULTA REALIZADA`

Este hecho no resuelve por sí solo la atribución funcional entre los dos POST registrados en Logcat.

---

#### 9.1.8 Integridad de evidencia

SHA-256 del Logcat privado de TIMEOUT3-R1:

`787BB5935347EABAD2C281765E715C5F247BEA3FB626DEF21768273BCCC9AC81`

El Logcat privado permanece fuera del repositorio público.

No deben incorporarse al repositorio público:

- JWT;
- credenciales;
- encabezados de autenticación;
- tokens;
- secretos;
- Logcat privado completo sin saneamiento;
- información sensible innecesaria.

---

#### 9.1.9 Desviación metodológica

La desviación comprobada es:

`SE EJECUTARON DOS POST DURANTE LA MISMA CORRIDA Y EL PRIMERO UTILIZÓ UN TÍTULO INCORRECTO`

Existe además una limitación instrumental:

`LOGCAT BASIC NO PERMITE ATRIBUIR INEQUÍVOCAMENTE EL TÍTULO FUNCIONAL A CADA POST`

Aunque las dos solicitudes poseen timestamps y duraciones independientes, la evidencia instrumental conservada no demuestra por sí sola que el POST de:

`3 015 ms`

sea el que contenía el título correcto.

El orden recordado se conserva como confirmación operativa, pero no sustituye la exigencia de correlación suficiente establecida antes de observar los resultados.

---

#### 9.1.10 Contraste con los criterios preregistrados

El prerregistro exige tres corridas evaluables por condición y establece criterios de evaluabilidad relacionados con:

- correlación suficiente de la solicitud experimental;
- identificadores que permitan distinguir las operaciones;
- trazabilidad suficiente para reconstruir la corrida;
- tratamiento explícito de desviaciones u operaciones adicionales;
- exclusión metodológica cuando la evidencia no permite atribuir suficientemente el resultado experimental.

También establece:

`MÁXIMO 1 REEMPLAZO POR CONDICIÓN`

para una corrida declarada no evaluable por una causa metodológica documentada.

En TIMEOUT3-R1, la existencia de dos POST dentro de la misma corrida no invalida automáticamente cualquier medición técnica.

Sin embargo, la combinación de:

- un primer POST con título incorrecto;
- un segundo POST con el título previsto según confirmación operativa retrospectiva;
- ausencia del título funcional en Logcat BASIC;
- imposibilidad de atribuir instrumentalmente cada título a cada POST;

impide demostrar con suficiencia cuál de las dos mediciones debe constituir el valor principal de la corrida.

---

#### 9.1.11 Decisión metodológica final

Decisión:

`TIMEOUT3-R1 = NO EVALUABLE`

Motivo:

`TRAZABILIDAD FUNCIONAL INSUFICIENTE PARA ASIGNAR INEQUÍVOCAMENTE EL POST CORRECTO A T1`

Valores conservados únicamente como observaciones descriptivas:

- `3 012 ms`
- `3 015 ms`

Valor T1:

`NO ASIGNADO`

Esta decisión no significa que:

`connectTimeout = 3 segundos`

haya fallado técnicamente.

Tampoco significa que la hipótesis quede respaldada o no respaldada.

Únicamente significa que:

`ESTA CORRIDA NO PUEDE PARTICIPAR EN LA COMPARACIÓN PRINCIPAL`

---

#### 9.1.12 Regla de reemplazo

La causa de exclusión es metodológica y queda documentada antes de consolidar un valor T1.

Por tanto, conforme a la regla preregistrada:

`PROCEDE UN REEMPLAZO DE TIMEOUT3-R1`

Estado del reemplazo:

`AUTORIZADO METODOLÓGICAMENTE — NO EJECUTADO`

Reemplazos ejecutados en la condición posterior hasta este punto:

`0`

Máximo permitido en la condición:

`1`

El reemplazo deberá conservarse como una nueva ejecución identificable.

No deberá:

- sobrescribir TIMEOUT3-R1;
- eliminar su evidencia;
- reinterpretar `3 012 ms` como T1;
- reinterpretar `3 015 ms` como T1.

---

### 9.2 TIMEOUT3-R2

Estado:

`NO EJECUTADA`

Valor:

`T2 = PENDIENTE`

---

### 9.3 TIMEOUT3-R3

Estado:

`NO EJECUTADA`

Valor:

`T3 = PENDIENTE`

---

## 10. Mediana posterior

Para calcular la mediana posterior se requieren:

`3 CORRIDAS EVALUABLES`

Estado actual:

- `TIMEOUT3-R1 = NO EVALUABLE`
- `T1 = NO ASIGNADO`
- `T2 = PENDIENTE`
- `T3 = PENDIENTE`

Reemplazo permitido de TIMEOUT3-R1:

`PENDIENTE DE EJECUCIÓN`

Mediana posterior:

`NO CALCULADA`

No se utilizarán:

- `3 012 ms`;
- `3 015 ms`;

en la mediana principal.

---

## 11. Reducción porcentual

La reducción se calculará únicamente después de disponer de:

- tres corridas evaluables antes;
- tres corridas evaluables después.

Mediana de línea base cerrada:

`M_antes = 10 028 ms`

Mediana posterior:

`M_después = NO CALCULADA`

Fórmula preregistrada:

\[
Reducción(\%) =
\frac{M_{antes} - M_{después}}
{M_{antes}}
\times 100
\]

Resultado:

`NO CALCULADO`

Criterio preregistrado:

`REDUCCIÓN >= 50 %`

El criterio no se modifica después de observar las mediciones.

Los valores descriptivos de TIMEOUT3-R1 no se utilizarán para:

- calcular informalmente la reducción;
- anticipar `M_después`;
- decidir si la hipótesis está respaldada;
- seleccionar retrospectivamente cuál POST conviene utilizar.

---

## 12. Estado de la hipótesis

Hipótesis preregistrada:

> Ante una indisponibilidad controlada de Spring Boot, configurar explícitamente `connectTimeout = 3 segundos` reducirá al menos un 50 % la mediana de la duración técnica de los `POST` fallidos de creación de actividad respecto de una nueva línea base ejecutada bajo el mismo protocolo, sin que Android presente falsamente la operación como exitosa ni se observe persistencia remota inesperada de la actividad intentada durante el fallo.

Estado actual:

`NO EVALUADA`

TIMEOUT3-R1 no aporta un valor T1 a la comparación principal.

Todavía no existe evidencia suficiente para clasificar la hipótesis como:

- respaldada;
- no respaldada;
- comparación principal no evaluable.

La comparación principal solo podrá evaluarse después de completar tres corridas posteriores evaluables.

---

## 13. Trazabilidad temporal

Secuencia registrada:

`prerregistro -> BASE-R1 -> BASE-R2 -> BASE-R3 -> cierre de línea base -> intervención connectTimeout 3s -> TIMEOUT3-R1 -> clasificación metodológica TIMEOUT3-R1`

Commit de prerregistro:

`94dcc32`

Commit de resultados BASE-R1:

`b02c239`

Commit provisional BASE-R2:

`e1dab87`

Commit de cierre metodológico BASE-R2:

`01d8e6a`

Commit de cierre de línea base:

`b6f82a5`

Merge del cierre de línea base:

`be2b2f3`

Mediana de línea base:

`M_antes = 10 028 ms`

Commit de intervención:

`a722954`

Intervención:

`connectTimeout = 3 segundos`

TIMEOUT3-R1:

`EJECUTADA — NO EVALUABLE`

Commit documental de evidencia y clasificación de TIMEOUT3-R1:

`PENDIENTE — DEBERÁ SER POSTERIOR A a722954`

TIMEOUT3-R2:

`NO EJECUTADA`

TIMEOUT3-R3:

`NO EJECUTADA`

Reemplazo de TIMEOUT3-R1:

`PROCEDE — NO EJECUTADO`

No se modifica retroactivamente:

- el prerregistro;
- la hipótesis;
- el umbral del 50 %;
- la métrica principal;
- los criterios de evaluabilidad;
- la regla de reemplazo;
- los commits históricos.

---

## 14. Evidencia privada

Los Logcat completos permanecen fuera del repositorio público.

### BASE-R1

Archivo privado registrado:

`evidencia-timeout/BASE-R1/logcat-BASE-R1-privado.txt`

SHA-256:

`DE5066FCF6BA7B86E1BD12FCA3041AD77513C38499E25578EB92DD27DA6E1AE5`

### BASE-R2

Archivo privado registrado:

`evidencia-timeout/BASE-R2/logcat-BASE-R2-privado.txt`

SHA-256:

`2BF5FDDEA23BF2D4644A616BF46DE9A1B9172EA00091E30348FA5CB571F7F907`

### BASE-R3

Archivo privado registrado:

`evidencia-timeout/BASE-R3/logcat-BASE-R3-privado.txt`

SHA-256:

`6508512032222FCF374F0C8998018DB01E8D466734F0B2516F57128B1894D5FF`

### TIMEOUT3-R1

Estado:

`EVIDENCIA PRIVADA CONSERVADA FUERA DEL REPOSITORIO PÚBLICO`

SHA-256:

`787BB5935347EABAD2C281765E715C5F247BEA3FB626DEF21768273BCCC9AC81`

La huella permite comprobar posteriormente la integridad del archivo privado utilizado durante la auditoría.

Los archivos privados no deberán incorporarse al repositorio sin:

- revisión;
- saneamiento;
- comprobación de credenciales;
- eliminación de JWT;
- eliminación de encabezados sensibles;
- eliminación de tokens o secretos;
- eliminación de información innecesaria.

El documento público de resultados no debe copiar cuerpos completos del Logcat privado cuando no sean necesarios para demostrar el resultado.

---

## 15. Estado actual de la Actividad 4

Prerregistro:

`COMPLETADO`

Commit de prerregistro:

`94dcc32`

### 15.1 Línea base

BASE-R1:

`COMPLETADA — EVALUABLE CON DESVIACIÓN DOCUMENTADA`

Valor:

`B1 = 10 028 ms`

BASE-R2:

`COMPLETADA — EVALUABLE CON INCIDENCIA PREVIA RESUELTA`

Valor:

`B2 = 10 033 ms`

BASE-R3:

`COMPLETADA — EVALUABLE CON INCIDENCIA DOCUMENTADA`

Valor:

`B3 = 10 023 ms`

Corridas de línea base ejecutadas:

`3 de 3`

Corridas de línea base admitidas:

`3 de 3`

Mediana de línea base:

`M_antes = 10 028 ms`

Reemplazos de línea base utilizados:

`0`

### 15.2 Intervención

Estado:

`IMPLEMENTADA`

Configuración:

`connectTimeout = 3 segundos`

Commit:

`a722954`

### 15.3 Condición posterior

TIMEOUT3-R1:

`EJECUTADA — NO EVALUABLE`

Motivo:

`DOS POST EN LA MISMA CORRIDA Y ATRIBUCIÓN FUNCIONAL INSUFICIENTE DEL TÍTULO A CADA POST`

Mediciones descriptivas conservadas:

- `3 012 ms`
- `3 015 ms`

T1:

`NO ASIGNADO`

Android:

`ERROR MOSTRADO — SIN FALSO ÉXITO OBSERVADO`

Persistencia experimental:

`0 FILAS`

Integridad del Logcat privado:

`SHA-256 787BB5935347EABAD2C281765E715C5F247BEA3FB626DEF21768273BCCC9AC81`

Fallo de las:

`20:32:25.078`

Clasificación:

`PATCH — NO TERCER POST`

Reemplazo de TIMEOUT3-R1:

`PROCEDE — NO EJECUTADO`

TIMEOUT3-R2:

`NO EJECUTADA`

TIMEOUT3-R3:

`NO EJECUTADA`

Mediana posterior:

`NO CALCULADA`

Reducción:

`NO CALCULADA`

Hipótesis:

`NO EVALUADA`

Conclusión arquitectónica:

`PENDIENTE`

---

## 16. Próximo punto de control

La línea base permanece cerrada con:

`M_antes = 10 028 ms`

La intervención:

`connectTimeout = 3 segundos`

ya fue implementada y registrada en:

`a722954`

TIMEOUT3-R1 fue ejecutada, pero no se admite en la comparación principal.

Decisión final de TIMEOUT3-R1:

`NO EVALUABLE`

No debe registrarse:

`T1 = 3 012 ms`

ni:

`T1 = 3 015 ms`

Tampoco debe calcularse todavía:

- `M_después`;
- reducción porcentual;
- respaldo de la hipótesis;
- no respaldo de la hipótesis.

El fallo de las:

`20:32:25.078`

se conserva correctamente clasificado como:

`PATCH`

y no debe presentarse como un tercer POST.

La evidencia de TIMEOUT3-R1 debe registrarse en un commit documental posterior a:

`a722954`

sin modificar el prerregistro.

Después de ese registro documental corresponde aplicar la regla preregistrada de reemplazo para obtener una corrida que pueda ocupar la posición metodológica de TIMEOUT3-R1.

El reemplazo deberá ejecutarse bajo la misma intervención:

`connectTimeout = 3 segundos`

y deberá evitar:

- reutilizar un título incorrecto;
- ejecutar más de un POST experimental;
- pulsar Guardar más de una vez;
- introducir operaciones adicionales durante la ventana de medición;
- cambiar `readTimeout`;
- cambiar `writeTimeout`;
- cambiar `callTimeout`;
- modificar retry;
- modificar fallback;
- modificar circuit breaker;
- modificar los interceptores existentes;
- alterar el prerregistro;
- eliminar evidencia de TIMEOUT3-R1;
- reutilizar `3 012 ms` como T1;
- reutilizar `3 015 ms` como T1;
- seleccionar un resultado en función de cuál favorezca la hipótesis.

Una vez exista una corrida de reemplazo evaluable, podrá asignarse:

`T1`

Posteriormente podrán ejecutarse:

- `TIMEOUT3-R2`;
- `TIMEOUT3-R3`.

Solo después de disponer de tres valores posteriores evaluables podrá calcularse:

`M_después`

Después podrá calcularse:

\[
Reducción(\%) =
\frac{M_{antes} - M_{después}}
{M_{antes}}
\times 100
\]

y contrastarse formalmente con el criterio:

`REDUCCIÓN >= 50 %`

utilizando como referencia de línea base:

`M_antes = 10 028 ms`

Hasta ese momento, la hipótesis permanece:

`NO EVALUADA`
