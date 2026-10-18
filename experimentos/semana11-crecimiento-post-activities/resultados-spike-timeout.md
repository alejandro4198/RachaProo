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
- las reglas para corridas no evaluables;
- la intervención prevista.

---

## 1. Estado general

Prerregistro:

`COMPLETADO`

Commit de prerregistro:

`94dcc32`

Commit de resultados previo a BASE-R2:

`b02c239`

Condición experimental actual:

`LÍNEA BASE`

Configuración HTTP:

`SIN connectTimeout EXPLÍCITO`

Corridas principales previstas de línea base:

`3`

Estado de BASE-R1:

`EJECUTADA — EVALUABLE CON DESVIACIÓN DOCUMENTADA`

Estado de BASE-R2:

`EJECUTADA — PENDIENTE DE CLASIFICACIÓN FINAL`

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

La indisponibilidad fue confirmada antes de ejecutar el POST experimental.

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

No se atribuye persistencia al intento experimental únicamente a partir del título reutilizado.

---

### 2.8 Correlación de solicitudes HTTP

Las dos solicitudes HTTP pueden distinguirse mediante evidencia independiente del título.

#### Control

Inicio:

`16:52:08.227`

Hilo:

`5952`

Tamaño del body registrado:

`105 bytes`

Resultado:

`HTTP 201`

#### Intento experimental

Inicio:

`16:54:24.825`

Hilo:

`6163`

Tamaño del body registrado:

`103 bytes`

Resultado:

`SocketTimeoutException`

Los timestamps, hilos, tamaños registrados y resultados permiten correlacionar cada:

`--> POST`

con su resultado correspondiente.

Por tanto, la duración técnica del intento experimental puede calcularse sin mezclarla con la solicitud de control.

---

### 2.9 Desviación metodológica

Se reutilizó el identificador:

`S11-A4-BASE-R1`

tanto para:

- la operación de control;
- la operación experimental durante la indisponibilidad.

Esto constituye una desviación respecto de la intención preregistrada de utilizar identificadores distinguibles para las operaciones experimentales.

La desviación reduce la trazabilidad funcional en PostgreSQL porque una consulta únicamente por título no permite diferenciar por sí sola ambas operaciones.

No obstante, la desviación no impide distinguir técnicamente las dos solicitudes HTTP debido a la existencia de:

- timestamps diferentes;
- hilos OkHttp diferentes;
- tamaños de cuerpo registrados diferentes;
- resultados HTTP diferentes.

---

### 2.10 Evaluabilidad de BASE-R1

Clasificación:

`EVALUABLE CON DESVIACIÓN DOCUMENTADA`

Esta clasificación constituye una decisión metodológica posterior a la observación de la corrida y no una consecuencia automática del Logcat.

La justificación registrada es:

1. el prerregistro existía antes de la corrida mediante `94dcc32`;
2. la aplicación se encontraba en la condición de línea base;
3. Spring Boot estaba disponible durante el control;
4. el control obtuvo `HTTP 201`;
5. Spring Boot fue detenido antes del POST experimental;
6. se comprobó indisponibilidad del puerto `8080`;
7. el POST experimental puede correlacionarse con su error;
8. existe una duración técnica calculable;
9. Android no mostró falso éxito;
10. PostgreSQL pudo ser consultado;
11. la reutilización del identificador quedó explícitamente documentada;
12. la evidencia permite distinguir las solicitudes HTTP aunque el título funcional haya sido reutilizado.

La principal limitación conservada es:

`LA PERSISTENCIA REMOTA NO PUEDE ATRIBUIRSE A CADA BODY DE FORMA INEQUÍVOCA ÚNICAMENTE MEDIANTE EL LOGCAT BASIC`

---

### 2.11 Participación en la mediana

Decisión de evaluación:

`BASE-R1 PARTICIPARÁ EN LA MEDIANA PRINCIPAL`

Valor:

`B1 = 10 028 ms`

La repetición de reemplazo permitida para la condición BASE:

`NO SE UTILIZA PARA BASE-R1`

---

### 2.12 Corrección procedimental posterior a BASE-R1

A partir de BASE-R2 se decidió no reutilizar el mismo identificador para el control y la operación experimental.

Para BASE-R2:

Control:

`S11-A4-BASE-R2-CONTROL`

Experimento:

`S11-A4-BASE-R2`

Para BASE-R3:

Control:

`S11-A4-BASE-R3-CONTROL`

Experimento:

`S11-A4-BASE-R3`

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

Estado metodológico:

`EVALUABLE CON DESVIACIÓN DOCUMENTADA`

Métrica principal:

`10 028 ms`

Valor para línea base:

`B1 = 10 028 ms`

Tipo de error:

`SocketTimeoutException`

Falso éxito Android:

`NO OBSERVADO`

Persistencia remota del intento experimental:

`NO DEMOSTRADA`

Fila PostgreSQL observada:

`ID 1500178`

Atribución inequívoca de esa fila únicamente mediante Logcat:

`NO`

Trazabilidad HTTP:

`SUFICIENTE PARA CORRELACIONAR LA MEDICIÓN`

Desviación:

`REUTILIZACIÓN DEL IDENTIFICADOR ENTRE CONTROL Y EXPERIMENTO`

Reemplazo requerido:

`NO`

---

## 3. BASE-R2

Estado:

`EJECUTADA — PENDIENTE DE CLASIFICACIÓN FINAL`

Valor temporal observado:

`10 033 ms`

Admisión como B2 para la mediana:

`PENDIENTE`

Commit ejecutado:

`b02c239`

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

La rama experimental fue sincronizada con el remoto antes de continuar.

Spring Boot se encontraba disponible al comenzar la preparación.

Se observó el puerto:

`8080`

en estado:

`Listen`

La configuración HTTP continuaba siendo la original.

No se había realizado todavía la intervención:

`connectTimeout = 3 segundos`

---

### 3.2 Incidencia previa de autenticación

Antes del control exitoso se observó una incidencia de autenticación.

Inicio:

`17:56:47.083`

Solicitud:

`POST /api/activities`

Resultado:

`HTTP 401`

Fin:

`17:56:47.298`

Este POST no corresponde al POST experimental medido durante la indisponibilidad.

La solicitud terminó antes de la autenticación posterior y antes del control exitoso.

---

### 3.3 Restablecimiento de autenticación

Posteriormente se realizó una nueva autenticación.

Inicio:

`17:56:55.297`

Solicitud:

`POST /api/auth/login`

Resultado:

`HTTP 200`

Fin:

`17:56:57.802`

Esta autenticación ocurrió antes del control exitoso.

La existencia del `HTTP 200` permite afirmar que se produjo un login exitoso.

Sin embargo, la clasificación metodológica final de BASE-R2 requiere conservar separada la siguiente cuestión:

`PENDIENTE DE CONFIRMAR DOCUMENTALMENTE SI LA SESIÓN PERMANECIÓ AUTENTICADA SIN CAMBIOS RELEVANTES HASTA EL POST EXPERIMENTAL`

---

### 3.4 Control previo de BASE-R2

Después del login exitoso se ejecutó una creación remota de control.

Identificador:

`S11-A4-BASE-R2-CONTROL`

Inicio del POST:

`17:57:19.284`

Resultado:

`HTTP 201`

Fin:

`17:57:19.544`

El control exitoso demuestra que, después de la incidencia `401` y del login posterior, la aplicación volvió a poder realizar una creación remota válida.

La incidencia `401` había terminado antes de este control.

---

### 3.5 Interpretación de la incidencia de autenticación

La secuencia observada fue:

1. intento de creación con `HTTP 401`;
2. login posterior con `HTTP 200`;
3. control de creación posterior con `HTTP 201`;
4. inducción posterior de indisponibilidad;
5. POST experimental posterior.

Por tanto, el `401` inicial:

`NO ERA UNA SOLICITUD PENDIENTE DURANTE EL POST EXPERIMENTAL`

No obstante, que la incidencia estuviera resuelta antes del control no basta por sí solo para declarar BASE-R2 evaluable.

Debe comprobarse además que:

- el login exitoso ocurrió antes de detener Spring Boot;
- la sesión permaneció autenticada hasta el POST experimental;
- no ocurrió un nuevo `401` relevante antes de la medición;
- no se reinició la aplicación;
- no se realizó un nuevo login entre la inducción del fallo y el POST experimental.

Estas comprobaciones forman parte de la decisión de evaluabilidad.

---

### 3.6 Inducción del fallo de BASE-R2

Spring Boot fue detenido antes de ejecutar el POST experimental.

Se reportó haber comprobado la ausencia de listener en el puerto:

`8080`

Estado reportado:

`SIN LISTENER`

La comprobación ocurrió antes del POST experimental.

Hora exacta documentada mediante salida de PowerShell:

`PENDIENTE DE INCORPORAR SI SE CONSERVÓ LA SALIDA`

La ausencia de una marca temporal incorporada todavía en este documento constituye una limitación documental.

No invalida automáticamente la corrida, pero debe conservarse como parte de la evaluación metodológica.

PostgreSQL permaneció disponible para la comprobación posterior.

---

### 3.7 Intento experimental durante la indisponibilidad

Identificador experimental:

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

La solicitud terminó con un error de establecimiento de conexión.

---

### 3.8 Correlación HTTP de BASE-R2

El inicio del POST experimental y su correspondiente error pueden correlacionarse mediante:

- proximidad temporal;
- mismo hilo OkHttp;
- mismo endpoint;
- ausencia de otro POST experimental concurrente identificado en esa ventana.

Inicio:

`18:00:35.191`

Fin:

`18:00:45.224`

Hilo:

`7878`

Por tanto, la medición técnica es calculable.

---

### 3.9 Duración técnica observada

La métrica preregistrada corresponde al intervalo:

`--> POST`

hasta:

`HTTP FAILED`

Para BASE-R2:

Inicio:

`18:00:35.191`

Fin:

`18:00:45.224`

Cálculo:

`45.224 - 35.191 = 10.033 segundos`

Duración técnica observada:

`10 033 ms`

Valor observado:

`10 033 ms`

Este valor se conserva independientemente de la clasificación final de la corrida.

Mientras BASE-R2 permanezca pendiente de clasificación, deberá expresarse como:

`VALOR OBSERVADO = 10 033 ms`

y no todavía como:

`B2 DEFINITIVO PARA LA MEDIANA`

---

### 3.10 Resultado Android

Durante el intento experimental Android mostró:

`No fue posible crear la actividad`

No se observó que Android presentara el intento como una creación remota exitosa.

Resultado funcional observado:

`ERROR MOSTRADO — SIN FALSO ÉXITO OBSERVADO`

---

### 3.11 Persistencia remota de BASE-R2

Se consultó PostgreSQL utilizando el identificador experimental:

`S11-A4-BASE-R2`

Resultado:

`0 filas`

Por tanto, dentro de la comprobación realizada no se observó persistencia remota de una actividad con el identificador experimental.

Resultado observado:

`0 COINCIDENCIAS`

Esta evidencia tiene una trazabilidad funcional mayor que BASE-R1 porque el identificador de control y el identificador experimental fueron diferentes.

---

### 3.12 Separación entre control y experimento

A diferencia de BASE-R1, BASE-R2 utilizó identificadores distintos.

Control:

`S11-A4-BASE-R2-CONTROL`

Experimento:

`S11-A4-BASE-R2`

Esto permite que la consulta PostgreSQL del identificador experimental sea independiente del control previo.

La desviación de reutilización de identificador observada en BASE-R1:

`NO SE REPITIÓ EN BASE-R2`

---

### 3.13 Incidencia metodológica de BASE-R2

La incidencia relevante de BASE-R2 fue:

`HTTP 401 ANTES DEL CONTROL`

Posteriormente se observó:

- `HTTP 200` en login;
- `HTTP 201` en control.

Por tanto, la incidencia ocurrió antes del control válido.

Sin embargo, su impacto sobre la evaluabilidad no debe decidirse únicamente porque después existiera un `HTTP 201`.

Debe comprobarse si las condiciones de autenticación permanecieron estables hasta la medición experimental.

---

### 3.14 Comprobaciones pendientes para cerrar la evaluabilidad

Antes de clasificar BASE-R2 definitivamente deben responderse las siguientes preguntas con la evidencia disponible.

#### A. Autenticación

¿El login exitoso de las:

`17:56:57.802`

ocurrió antes de detener Spring Boot?

Estado:

`PENDIENTE DE CONFIRMACIÓN EXPLÍCITA`

#### B. Continuidad de sesión

¿La sesión permaneció autenticada desde el login exitoso hasta el POST de:

`18:00:35.191`

sin:

- logout;
- nuevo `401` relevante;
- reinicio de aplicación;
- nuevo login;
- cambio intencional de usuario?

Estado:

`PENDIENTE DE CONFIRMACIÓN EXPLÍCITA`

#### C. Indisponibilidad del backend

¿El puerto `8080` fue comprobado sin listener antes de las:

`18:00:35.191`?

Estado reportado:

`SÍ`

Hora exacta incorporada al documento:

`PENDIENTE SI EXISTE EVIDENCIA CONSERVADA`

#### D. PostgreSQL

¿PostgreSQL permaneció disponible para la comprobación posterior?

Resultado observado:

`0 filas para S11-A4-BASE-R2`

#### E. Guardado único

¿Se pulsó `Guardar` exactamente una vez sobre:

`S11-A4-BASE-R2`

durante la ventana experimental?

Estado:

`PENDIENTE DE CONFIRMACIÓN EXPLÍCITA`

---

### 3.15 Regla para clasificar BASE-R2

BASE-R2 podrá clasificarse como:

`EVALUABLE CON INCIDENCIA PREVIA RESUELTA`

si se puede justificar suficientemente que:

1. el `401` terminó antes del control;
2. el login exitoso ocurrió antes de detener Spring Boot;
3. el control posterior obtuvo `HTTP 201`;
4. la sesión permaneció autenticada hasta el POST experimental;
5. no existió un nuevo evento de autenticación que contaminara la medición;
6. Spring Boot estaba indisponible durante el POST experimental;
7. PostgreSQL permaneció disponible;
8. se realizó una sola operación experimental;
9. el POST y su fallo pueden correlacionarse;
10. Android no mostró falso éxito;
11. PostgreSQL mostró `0 filas` para el identificador experimental.

Si alguna condición esencial no puede verificarse suficientemente, la clasificación será:

`NO EVALUABLE`

conservando de todas maneras:

`10 033 ms`

como observación descriptiva.

---

### 3.16 Estado actual de evaluabilidad de BASE-R2

Clasificación actual:

`PENDIENTE DE CLASIFICACIÓN FINAL`

Valor temporal observado:

`10 033 ms`

Participación en la mediana:

`PENDIENTE`

Reemplazo:

`NO DECIDIDO`

No debe gastarse todavía la repetición de reemplazo mientras exista una auditoría pendiente de la evidencia ya obtenida.

---

### 3.17 Integridad de evidencia BASE-R2

Archivo privado:

`evidencia-timeout/BASE-R2/logcat-BASE-R2-privado.txt`

SHA-256:

`2BF5FDDEA23BF2D4644A616BF46DE9A1B9172EA00091E30348FA5CB571F7F907`

El archivo se conserva como evidencia privada.

No deberá versionarse directamente sin revisión y saneamiento.

---

### 3.18 Resultado consolidado provisional BASE-R2

Estado:

`PENDIENTE DE CLASIFICACIÓN FINAL`

Valor observado:

`10 033 ms`

Tipo de error:

`SocketTimeoutException`

Falso éxito Android:

`NO OBSERVADO`

Persistencia remota del identificador experimental:

`0 FILAS`

Separación control/experimento:

`SÍ`

Incidencia previa:

`HTTP 401`

Login posterior:

`HTTP 200`

Control posterior:

`HTTP 201`

Correlación HTTP experimental:

`SUFICIENTE PARA CALCULAR LA DURACIÓN`

Participación definitiva en mediana:

`PENDIENTE`

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

BASE-R3 no deberá iniciarse hasta cerrar documentalmente la clasificación de BASE-R2.

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

`PENDIENTE DE CLASIFICACIÓN FINAL`

Valor observado:

`10 033 ms`

Valor admitido como B2:

`PENDIENTE`

### BASE-R3

Estado:

`NO EJECUTADA`

Valor:

`B3 = PENDIENTE`

---

## 6. Mediana de línea base

La mediana preregistrada solo podrá calcularse cuando existan:

`3 CORRIDAS EVALUABLES`

Actualmente:

- `B1 = 10 028 ms`
- `B2 = PENDIENTE DE ADMISIÓN`
- `B3 = PENDIENTE`

Mediana:

`NO CALCULADA`

No se calculará una mediana provisional utilizando valores cuya evaluabilidad todavía no haya sido cerrada.

---

## 7. Reemplazos de línea base

El prerregistro permite:

`MÁXIMO 1 REEMPLAZO POR CONDICIÓN`

Condición actual:

`BASE`

Reemplazos utilizados:

`0`

BASE-R1:

`NO REQUIRIÓ REEMPLAZO`

BASE-R2:

`PENDIENTE DE CLASIFICACIÓN — NO CONSUME TODAVÍA REEMPLAZO`

No se ejecutará una repetición adicional simplemente por conveniencia del valor observado.

---

## 8. Intervención experimental

Estado:

`NO IMPLEMENTADA`

Cambio previsto:

`connectTimeout = 3 segundos`

Archivo previsto:

`RetrofitClient.kt`

Estado del archivo respecto de la intervención:

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

La secuencia registrada hasta este momento es:

`prerregistro -> BASE-R1 -> registro BASE-R1 -> BASE-R2 -> auditoría BASE-R2`

Commit de prerregistro:

`94dcc32`

Commit de resultados de BASE-R1:

`b02c239`

Commit de resultados actualizado con BASE-R2:

`PENDIENTE`

Intervención:

`NO REALIZADA`

Commit de intervención:

`PENDIENTE`

Mediciones posteriores:

`NO REALIZADAS`

Commit de resultados finales:

`PENDIENTE`

No se modificará el commit histórico de prerregistro para incorporar resultados observados posteriormente.

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

Los archivos privados no deberán incorporarse directamente al repositorio sin:

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

Valor B1:

`10 028 ms`

BASE-R2:

`COMPLETADA TÉCNICAMENTE — PENDIENTE DE CLASIFICACIÓN METODOLÓGICA`

Valor observado BASE-R2:

`10 033 ms`

BASE-R3:

`PENDIENTE`

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

Antes de ejecutar BASE-R3 deberá cerrarse la clasificación metodológica de BASE-R2.

Se deberá comprobar y registrar explícitamente:

1. si el login exitoso de las `17:56:57.802` ocurrió antes de detener Spring Boot;
2. si la sesión permaneció autenticada hasta el POST experimental de las `18:00:35.191`;
3. si hubo o no un nuevo `401`, logout, login o reinicio de aplicación en esa ventana;
4. si se conserva una comprobación temporal del puerto `8080` sin listener antes del POST experimental;
5. si se pulsó `Guardar` una única vez sobre `S11-A4-BASE-R2`.

Si estas condiciones quedan suficientemente verificadas, podrá justificarse:

`BASE-R2 = EVALUABLE CON INCIDENCIA PREVIA RESUELTA`

y entonces:

`B2 = 10 033 ms`

podrá incorporarse a la mediana.

Si no pueden verificarse suficientemente las condiciones esenciales, deberá registrarse:

`BASE-R2 = NO EVALUABLE`

manteniendo:

`10 033 ms`

únicamente como observación descriptiva.

Solo después de cerrar esa decisión se continuará con:

`BASE-R3`
