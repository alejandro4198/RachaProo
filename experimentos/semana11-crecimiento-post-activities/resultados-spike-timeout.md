# Semana 11 — Actividad 4
## Resultados del experimento de timeout

Este documento registra los resultados observados durante la ejecución del experimento preregistrado en:

`prerregistro-spike-timeout.md`

El prerregistro quedó registrado previamente mediante el commit:

`94dcc32`

Los resultados consignados en este documento corresponden a observaciones realizadas después de dicho prerregistro.

La versión histórica del prerregistro utilizada para contrastar las decisiones metodológicas fue consultada directamente mediante:

```powershell
git show 94dcc32:experimentos/semana11-crecimiento-post-activities/prerregistro-spike-timeout.md
```

La comprobación histórica confirmó que las reglas relevantes para:

- la línea base;
- la condición posterior a la intervención;
- los identificadores de las corridas;
- la ejecución de una sola operación experimental;
- las corridas no evaluables;
- los reemplazos;
- el cálculo de las medianas;

ya se encontraban definidas antes de ejecutar las mediciones.

No se modifican retroactivamente:

- la hipótesis;
- el umbral del `50 %`;
- el número de corridas principales;
- la métrica principal;
- los identificadores previstos;
- las condiciones experimentales;
- los criterios de evaluabilidad;
- las reglas para corridas no evaluables;
- las reglas de reemplazo;
- la intervención prevista.

Cuando corresponde, este documento distingue entre:

- evidencia técnica registrada en Logcat;
- observaciones visibles en Android;
- consultas realizadas en PostgreSQL;
- comprobaciones de infraestructura;
- confirmaciones operativas;
- comprobaciones realizadas posteriormente;
- inferencias apoyadas por evidencia;
- incidencias;
- limitaciones;
- decisiones metodológicas.

---

# 1. Estado general

Prerregistro:

`COMPLETADO`

Commit de prerregistro:

`94dcc32`

Verificación histórica del prerregistro:

`COMPLETADA`

Estado de la línea base:

`CERRADA`

Corridas evaluables de línea base:

`3 de 3`

Valores admitidos:

- `B1 = 10 028 ms`
- `B2 = 10 033 ms`
- `B3 = 10 023 ms`

Mediana de línea base:

`M_antes = 10 028 ms`

Commit de cierre de línea base:

`b6f82a5`

Merge del cierre de línea base:

`be2b2f3`

Intervención experimental:

`IMPLEMENTADA`

Configuración experimental:

`connectTimeout = 3 segundos`

Commit de intervención:

`a722954`

TIMEOUT3-R1:

`EJECUTADA — NO EVALUABLE`

Valor procedente de TIMEOUT3-R1 para la comparación:

`NO ASIGNADO`

Observaciones técnicas conservadas de TIMEOUT3-R1:

- `3 012 ms`
- `3 015 ms`

Reemplazo de TIMEOUT3-R1:

`TIMEOUT3-R1-REP1`

Estado del reemplazo:

`EJECUTADA — EVALUABLE`

Valor admitido:

`T1 = 3 024 ms`

TIMEOUT3-R2:

`EJECUTADA — EVALUABLE CON INCIDENCIA DOCUMENTADA`

Valor admitido:

`T2 = 3 011 ms`

Incidencia principal de R2:

`EXISTENCIA DE UNA FILA HISTÓRICA ELIMINADA LÓGICAMENTE CON EL MISMO IDENTIFICADOR EXPERIMENTAL`

TIMEOUT3-R3:

`NO EJECUTADA`

Valor T3:

`PENDIENTE`

Corridas posteriores evaluables consolidadas:

`2 de 3`

Reemplazos utilizados en la condición posterior:

`1 de 1 permitido`

Reemplazos disponibles:

`0`

Mediana posterior:

`NO CALCULADA`

Reducción porcentual:

`NO CALCULADA`

Hipótesis:

`NO EVALUADA`

Comparación principal:

`ABIERTA`

Conclusión arquitectónica:

`PENDIENTE`

---

# 2. BASE-R1

Estado:

`EVALUABLE CON DESVIACIÓN DOCUMENTADA`

Valor admitido:

`B1 = 10 028 ms`

Condición:

`LÍNEA BASE — SIN connectTimeout CONFIGURADO EXPLÍCITAMENTE`

Fecha:

`2026-10-18`

Dispositivo:

`DISPOSITIVO FÍSICO ANDROID`

Versión Android:

`14`

---

## 2.1 Condición inicial

Antes de inducir la indisponibilidad se verificó:

- dispositivo Android conectado;
- aplicación RachaPro disponible;
- usuario autenticado;
- Spring Boot disponible;
- puerto `8080` atendiendo;
- PostgreSQL disponible;
- comunicación Android-backend funcional.

La configuración HTTP correspondía a la condición de línea base.

No se había implementado:

`connectTimeout = 3 segundos`

---

## 2.2 Control previo

Identificador utilizado:

`S11-A4-BASE-R1`

Inicio:

`16:52:08.227`

Hilo OkHttp:

`5952`

Tamaño registrado:

`105 bytes`

Resultado:

`HTTP 201`

Fin:

`16:52:08.348`

Duración informada por OkHttp:

`119 ms`

Diferencia entre timestamps visibles:

`121 ms`

La duración del control no forma parte de la métrica principal.

El control constituye evidencia de que la creación remota respondía correctamente antes de inducir la indisponibilidad.

---

## 2.3 Inducción de indisponibilidad

Después del control previo se detuvo Spring Boot.

Se comprobó que el puerto:

`8080`

no tenía listener.

Hora de comprobación registrada:

`16:53:43`

La comprobación se realizó antes del POST experimental.

PostgreSQL no fue detenido intencionalmente.

---

## 2.4 POST experimental

Identificador utilizado:

`S11-A4-BASE-R1`

Inicio:

`16:54:24.825`

Hilo OkHttp:

`6163`

Tamaño registrado:

`103 bytes`

Resultado:

`SocketTimeoutException`

Fin:

`16:54:34.853`

Mensaje técnico registrado:

`failed to connect ... after 10000ms`

---

## 2.5 Duración técnica

La métrica principal corresponde al intervalo entre:

`--> POST`

y:

`HTTP FAILED`

Inicio:

`16:54:24.825`

Fin:

`16:54:34.853`

Cálculo:

`34.853 - 24.825 = 10.028 segundos`

Duración:

`10 028 ms`

Valor:

`B1 = 10 028 ms`

---

## 2.6 Resultado visible en Android

Android mostró:

`No fue posible crear la actividad`

Falso éxito observado:

`NO`

Resultado funcional observado:

`ERROR MOSTRADO — SIN FALSO ÉXITO OBSERVADO`

---

## 2.7 Persistencia remota

Se consultó PostgreSQL por:

`S11-A4-BASE-R1`

Resultado:

`1 fila`

ID observado:

`1500178`

### Hechos observados

La evidencia permite establecer que:

- existió un POST de control;
- el control obtuvo `HTTP 201`;
- existió posteriormente un POST experimental;
- el POST experimental terminó en `SocketTimeoutException`;
- PostgreSQL mostró una fila bajo el identificador reutilizado.

### Limitación

El mismo identificador:

`S11-A4-BASE-R1`

fue utilizado tanto para control como para experimento.

Además, el nivel:

`HttpLoggingInterceptor.Level.BASIC`

no expone el contenido funcional completo del body.

Por tanto, mediante el título y Logcat BASIC no puede atribuirse de forma inequívoca la fila observada a uno de los cuerpos enviados.

### Interpretación

La combinación de:

- control con `HTTP 201`;
- POST experimental terminado en error;
- una sola fila observada;

es consistente con que la fila corresponda al control.

Esta interpretación se registra como:

`INFERENCIA APOYADA POR EVIDENCIA CONJUNTA`

y no como:

`IDENTIFICACIÓN INSTRUMENTAL INEQUÍVOCA`

---

## 2.8 Correlación HTTP

### Control

Inicio:

`16:52:08.227`

Hilo:

`5952`

Tamaño:

`105 bytes`

Resultado:

`HTTP 201`

### Experimento

Inicio:

`16:54:24.825`

Hilo:

`6163`

Tamaño:

`103 bytes`

Resultado:

`SocketTimeoutException`

Los timestamps, hilos, tamaños y resultados permiten distinguir técnicamente ambas solicitudes.

---

## 2.9 Desviación metodológica

Se reutilizó:

`S11-A4-BASE-R1`

para el control y el experimento.

La reutilización disminuye la trazabilidad funcional en PostgreSQL.

No obstante, las dos solicitudes HTTP permanecen técnicamente distinguibles mediante:

- timestamps;
- hilos;
- tamaños registrados;
- resultados diferentes.

---

## 2.10 Decisión metodológica BASE-R1

Clasificación:

`EVALUABLE CON DESVIACIÓN DOCUMENTADA`

Justificación:

- la duración experimental puede calcularse;
- el POST experimental puede distinguirse técnicamente del control;
- Android no mostró éxito;
- la desviación de identificación permanece expresamente registrada;
- la limitación sobre atribución de persistencia no se oculta.

Participación en la mediana:

`SÍ`

Valor:

`B1 = 10 028 ms`

Reemplazo:

`NO REQUERIDO`

---

## 2.11 Integridad de evidencia BASE-R1

Archivo privado:

`evidencia-timeout/BASE-R1/logcat-BASE-R1-privado.txt`

SHA-256:

`DE5066FCF6BA7B86E1BD12FCA3041AD77513C38499E25578EB92DD27DA6E1AE5`

---

# 3. BASE-R2

Estado:

`EVALUABLE CON INCIDENCIA PREVIA RESUELTA`

Valor admitido:

`B2 = 10 033 ms`

Commit provisional:

`e1dab87`

Commit de cierre metodológico:

`01d8e6a`

Condición:

`LÍNEA BASE — SIN connectTimeout CONFIGURADO EXPLÍCITAMENTE`

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

## 3.1 Incidencia previa de autenticación

Antes del control válido se registró:

`POST /api/activities`

Inicio:

`17:56:47.083`

Resultado:

`HTTP 401`

Fin:

`17:56:47.298`

Posteriormente se realizó:

`POST /api/auth/login`

Inicio:

`17:56:55.297`

Resultado:

`HTTP 200`

Fin:

`17:56:57.802`

---

## 3.2 Restablecimiento de la condición funcional

Después del login se confirmó:

- aplicación abierta;
- mismo usuario;
- comunicación nuevamente disponible;
- control posterior exitoso.

Estas condiciones permiten separar la incidencia previa de la medición experimental posterior.

---

## 3.3 Control previo válido

Identificador:

`S11-A4-BASE-R2-CONTROL`

Inicio:

`17:57:19.284`

Resultado:

`HTTP 201`

Fin:

`17:57:19.544`

El control confirmó que la comunicación normal Android-backend se encontraba restablecida antes de inducir la indisponibilidad.

---

## 3.4 Secuencia reconstruida

La secuencia fue:

1. POST con `HTTP 401`;
2. login con `HTTP 200`;
3. control con `HTTP 201`;
4. detención de Spring Boot;
5. comprobación operativa del puerto;
6. POST experimental.

Los primeros tres elementos cuentan con registro técnico fechado.

La detención del backend y la comprobación del puerto cuentan con confirmación operativa retrospectiva.

---

## 3.5 Limitación documental

La comprobación del puerto antes del POST experimental fue confirmada operativamente.

No se conservó:

`UNA SALIDA POWERSHELL CON TIMESTAMP EXACTO DE ESA COMPROBACIÓN`

Esta limitación permanece documentada.

---

## 3.6 POST experimental

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

---

## 3.7 Correlación HTTP

Inicio:

`18:00:35.191`

Fin:

`18:00:45.224`

Hilo:

`7878`

Resultado:

`SocketTimeoutException`

La correspondencia entre inicio y resultado fue considerada suficiente para calcular la duración.

---

## 3.8 Duración técnica

Cálculo:

`45.224 - 35.191 = 10.033 segundos`

Valor:

`B2 = 10 033 ms`

---

## 3.9 Resultado Android

Android mostró:

`No fue posible crear la actividad`

Falso éxito observado:

`NO`

---

## 3.10 Persistencia remota

Consulta:

`S11-A4-BASE-R2`

Resultado:

`0 FILAS`

---

## 3.11 Separación control/experimento

Control:

`S11-A4-BASE-R2-CONTROL`

Experimento:

`S11-A4-BASE-R2`

La reutilización del identificador observada en BASE-R1 no se repitió.

---

## 3.12 Guardado experimental

Se confirmó operativamente:

`GUARDAR UNA SOLA VEZ`

para:

`S11-A4-BASE-R2`

---

## 3.13 Decisión metodológica BASE-R2

La incidencia de autenticación ocurrió antes de:

- login exitoso;
- control válido;
- inducción de indisponibilidad;
- POST experimental.

La medición principal dispone de:

- solicitud identificable;
- resultado correlacionable;
- duración calculable;
- resultado Android;
- consulta PostgreSQL específica.

La ausencia de una captura fechada del puerto permanece documentada como limitación.

Clasificación:

`EVALUABLE CON INCIDENCIA PREVIA RESUELTA`

Participación:

`SÍ`

Valor:

`B2 = 10 033 ms`

Reemplazo:

`NO REQUERIDO`

---

## 3.14 Integridad de evidencia BASE-R2

Archivo privado:

`evidencia-timeout/BASE-R2/logcat-BASE-R2-privado.txt`

SHA-256:

`2BF5FDDEA23BF2D4644A616BF46DE9A1B9172EA00091E30348FA5CB571F7F907`

---

# 4. BASE-R3

Estado:

`EVALUABLE CON INCIDENCIA DOCUMENTADA`

Valor admitido:

`B3 = 10 023 ms`

Condición:

`LÍNEA BASE — SIN connectTimeout CONFIGURADO EXPLÍCITAMENTE`

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

## 4.1 Control previo

Inicio:

`19:10:45.065`

Solicitud:

`POST /api/activities`

Hilo:

`10117`

Tamaño:

`113 bytes`

Resultado:

`HTTP 201`

Fin:

`19:10:45.127`

Duración informada:

`60 ms`

---

## 4.2 Persistencia del identificador de control

Consulta:

`S11-A4-BASE-R3-CONTROL`

Resultado:

`2 FILAS`

IDs:

- `1500180`
- `1500181`

Usuario:

`1507`

Estado:

`PENDING`

Eliminadas:

`false`

---

## 4.3 Incidencia del control

Se observaron dos filas con el mismo identificador de control.

Procedencia exacta:

`NO DETERMINADA`

No se afirma:

- que ambas hayan sido generadas por una única acción;
- que necesariamente existieran dos pulsaciones;
- que una fila fuera necesariamente preexistente.

La incidencia se conserva como:

`ANOMALÍA DE TRAZABILIDAD DEL CONTROL`

---

## 4.4 Separación respecto del experimento

Identificador del control:

`S11-A4-BASE-R3-CONTROL`

Identificador experimental:

`S11-A4-BASE-R3`

La consulta posterior del identificador experimental devolvió:

`0 FILAS`

No existe evidencia que demuestre que la incidencia del control haya:

- generado un segundo POST experimental;
- alterado el identificador experimental;
- provocado persistencia experimental;
- cambiado la duración;
- alterado el resultado Android.

Por tanto, se distingue entre:

`INCIDENCIA COMPROBADA EN EL CONTROL`

y:

`ALTERACIÓN COMPROBADA DEL POST EXPERIMENTAL`

Para la segunda:

`NO DEMOSTRADA`

---

## 4.5 Comprobación de indisponibilidad

Puerto:

`8080`

Hora:

`19:11:54.078`

Listeners:

`0`

La comprobación se realizó antes del POST experimental.

---

## 4.6 POST experimental

Identificador:

`S11-A4-BASE-R3`

Inicio:

`19:12:23.162`

Hilo:

`10172`

Tamaño:

`103 bytes`

Resultado:

`SocketTimeoutException`

Fin:

`19:12:33.185`

Mensaje técnico registrado:

`failed to connect to /192.168.1.20 (port 8080) from /192.168.1.49 (port 41644) after 10000ms`

---

## 4.7 Correlación HTTP

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

La correspondencia entre inicio y resultado fue considerada suficiente para calcular la duración.

---

## 4.8 Duración técnica

Cálculo:

`33.185 - 23.162 = 10.023 segundos`

Valor:

`B3 = 10 023 ms`

---

## 4.9 Resultado Android

Android mostró:

`No fue posible crear la actividad`

Falso éxito observado:

`NO`

---

## 4.10 Persistencia experimental

Consulta:

`S11-A4-BASE-R3`

Resultado:

`0 FILAS`

---

## 4.11 Guardado experimental

Se confirmó operativamente:

`GUARDAR UNA SOLA VEZ`

para el intento experimental.

Esta confirmación no se utiliza para explicar el origen de las dos filas del control.

---

## 4.12 Decisión metodológica BASE-R3

La incidencia del control permanece documentada.

El POST experimental dispone de:

- identificador propio;
- comprobación del puerto con `0 listeners`;
- inicio identificado;
- hilo identificado;
- resultado correlacionable;
- duración calculable;
- resultado Android;
- consulta PostgreSQL independiente;
- confirmación operativa de un solo Guardar experimental.

No se demostró que la incidencia del control hubiera alterado la operación experimental.

Clasificación:

`EVALUABLE CON INCIDENCIA DOCUMENTADA`

Participación:

`SÍ`

Valor:

`B3 = 10 023 ms`

Reemplazo:

`NO REQUERIDO`

---

## 4.13 Integridad de evidencia BASE-R3

Archivo privado:

`evidencia-timeout/BASE-R3/logcat-BASE-R3-privado.txt`

SHA-256:

`6508512032222FCF374F0C8998018DB01E8D466734F0B2516F57128B1894D5FF`

---

# 5. Consolidación de línea base

Valores admitidos:

- `B1 = 10 028 ms`
- `B2 = 10 033 ms`
- `B3 = 10 023 ms`

Valores ordenados:

- `10 023 ms`
- `10 028 ms`
- `10 033 ms`

Cálculo:

\[
\operatorname{mediana}(10\,023,\ 10\,028,\ 10\,033)
=
10\,028\text{ ms}
\]

Mediana:

`M_antes = 10 028 ms`

Corridas principales ejecutadas:

`3 de 3`

Corridas evaluables:

`3 de 3`

Reemplazos utilizados:

`0`

Estado:

`LÍNEA BASE CERRADA`

No es necesario repetir:

- BASE-R1;
- BASE-R2;
- BASE-R3.

---

# 6. Intervención experimental

Estado:

`IMPLEMENTADA`

Cambio preregistrado:

`connectTimeout = 3 segundos`

Archivo:

`app/src/main/java/com/example/rachapro/network/RetrofitClient.kt`

Commit de intervención:

`a722954`

Configuración agregada:

```kotlin
connectTimeout(3, TimeUnit.SECONDS)
```

Import agregado:

```kotlin
import java.util.concurrent.TimeUnit
```

No se modificaron deliberadamente como parte de la intervención:

- `readTimeout`;
- `writeTimeout`;
- `callTimeout`;
- retry;
- fallback;
- circuit breaker;
- interceptores existentes.

La intervención quedó registrada antes de iniciar las mediciones posteriores.

---

# 7. Condición posterior a la intervención

El prerregistro histórico `94dcc32` establece:

`3 CORRIDAS PRINCIPALES`

después de la intervención.

Los identificadores definidos son:

- `S11-A4-TIMEOUT3-R1`
- `S11-A4-TIMEOUT3-R2`
- `S11-A4-TIMEOUT3-R3`

Estado actual:

- `TIMEOUT3-R1 = EJECUTADA — NO EVALUABLE`
- `TIMEOUT3-R1-REP1 = EJECUTADA — EVALUABLE`
- `TIMEOUT3-R2 = EJECUTADA — EVALUABLE CON INCIDENCIA DOCUMENTADA`
- `TIMEOUT3-R3 = NO EJECUTADA`

Valores evaluables consolidados:

`2 de 3`

Valores disponibles:

- `T1 = 3 024 ms`
- `T2 = 3 011 ms`
- `T3 = PENDIENTE`

Reemplazos utilizados:

`1 de 1 permitido`

Reemplazos disponibles:

`0`

---

# 8. TIMEOUT3-R1

Estado:

`EJECUTADA`

Identificador definido en el prerregistro:

`S11-A4-TIMEOUT3-R1`

Presencia en `94dcc32`:

`CONFIRMADA`

Commit de intervención:

`a722954`

Configuración:

`connectTimeout = 3 segundos`

Commit documental:

`cc68828`

Clasificación:

`NO EVALUABLE`

Valor para la comparación:

`NO ASIGNADO`

---

## 8.1 Comprobación de indisponibilidad

Puerto:

`8080`

Listeners:

`0`

Hora:

`20:32:15.603`

La comprobación ocurrió antes de los POST fallidos posteriores.

---

## 8.2 Primer POST observado

Inicio:

`20:32:49.240`

Fin:

`20:32:52.252`

Duración:

`3 012 ms`

---

## 8.3 Segundo POST observado

Inicio:

`20:33:40.536`

Fin:

`20:33:43.551`

Duración:

`3 015 ms`

---

## 8.4 Naturaleza de las mediciones

Los dos POST poseen pares inicio-resultado técnicamente distinguibles.

Se conservan:

- `3 012 ms`
- `3 015 ms`

como:

`HECHOS TÉCNICOS OBSERVADOS`

Ninguno se utiliza como:

`T1`

---

## 8.5 Identificadores utilizados

Según confirmación operativa retrospectiva, se utilizaron:

1. `S11-A4-TIMEOUT-R1`
2. `S11-A4-TIMEOUT3-R1`

El primero era distinto del identificador definido para la corrida.

El segundo correspondía al identificador definido.

El Logcat BASIC no expone el body completo, por lo que la asociación título-POST no se presenta como evidencia derivada exclusivamente del Logcat.

---

## 8.6 Operación de las 20:32:25.078

La operación registrada a las:

`20:32:25.078`

correspondía a:

`PATCH`

y no a:

`POST /api/activities`

Por tanto:

`NO SE CONTABILIZA COMO UN TERCER POST DE CREACIÓN`

---

## 8.7 Android

Mensaje:

`No fue posible crear la actividad`

Falso éxito:

`NO OBSERVADO`

---

## 8.8 PostgreSQL

Consulta:

`S11-A4-TIMEOUT3-R1`

Resultado:

`0 FILAS`

---

## 8.9 Integridad

SHA-256:

`787BB5935347EABAD2C281765E715C5F247BEA3FB626DEF21768273BCCC9AC81`

---

# 9. Evaluabilidad de TIMEOUT3-R1

El prerregistro exige:

`PULSAR GUARDAR EXACTAMENTE UNA VEZ`

y contempla una corrida como:

`NO EVALUABLE`

cuando el protocolo no pudo cumplirse suficientemente.

Durante TIMEOUT3-R1 hubo dos intentos de creación.

Por tanto:

`TIMEOUT3-R1 = NO EVALUABLE`

Las observaciones:

- `3 012 ms`
- `3 015 ms`

se conservan de manera descriptiva.

Valor para la comparación:

`NO ASIGNADO`

TIMEOUT3-R1 no cuenta como respaldo ni como no respaldo de la hipótesis.

---

# 10. Regla de reemplazo

La versión histórica establece:

`MÁXIMO 1 REPETICIÓN DE REEMPLAZO POR CONDICIÓN`

La repetición debe:

- responder a una causa metodológica documentada;
- utilizar un identificador nuevo;
- conservar la evidencia original;
- seguir el mismo protocolo;
- registrarse expresamente como reemplazo.

TIMEOUT3-R1 cumplió la condición para utilizar la repetición.

---

# 11. TIMEOUT3-R1-REP1

Estado:

`EJECUTADA`

Clasificación:

`EVALUABLE`

Valor:

`T1 = 3 024 ms`

Fecha:

`2026-10-19`

Identificador:

`S11-A4-TIMEOUT3-R1-REP1`

---

## 11.1 Identificador

Identificador utilizado:

`S11-A4-TIMEOUT3-R1-REP1`

Fuente:

`CONFIRMACIÓN OPERATIVA`

---

## 11.2 Control previo

Resultado:

`HTTP 201`

Fila de control:

`1500184`

El control documenta funcionamiento previo de comunicación y persistencia.

---

## 11.3 Indisponibilidad

Puerto:

`8080`

Resultado:

`SIN LISTENER`

Hora:

`12:29:36.332`

---

## 11.4 POST experimental

Inicio:

`12:30:11.583`

Fin:

`12:30:14.607`

Hilo:

`29183`

Excepción:

`SocketTimeoutException`

---

## 11.5 Duración

Cálculo:

`14.607 - 11.583 = 3.024 segundos`

Valor:

`T1 = 3 024 ms`

---

## 11.6 Android

Mensaje:

`No fue posible crear la actividad`

Falso éxito:

`NO OBSERVADO`

---

## 11.7 PostgreSQL

Consulta:

`S11-A4-TIMEOUT3-R1-REP1`

Resultado:

`0 COINCIDENCIAS`

---

## 11.8 Guardar

Pulsaciones:

`1`

Fuente:

`CONFIRMACIÓN OPERATIVA`

---

## 11.9 Commit asociado

Commit comprobado posteriormente:

`11c4092`

La comprobación ocurrió:

`DESPUÉS DE LA EJECUCIÓN`

No se presenta como una captura contemporánea a la ventana experimental.

---

## 11.10 Limitación de configuración

La intervención prevista era:

`connectTimeout = 3 segundos`

No se registró durante la ventana exacta de REP1 una captura instrumental que enumerara toda la configuración HTTP.

Esta limitación permanece documentada.

---

## 11.11 Integridad

Archivo:

`evidencia-timeout/TIMEOUT3-R1-REP1/logcat-TIMEOUT3-R1-REP1-privado.txt`

SHA-256:

`34A67A1FA46927A4C65EA3CE8C40923A87BE2F8A75EB510B6A0088142C371EC2`

---

# 12. Evaluabilidad de TIMEOUT3-R1-REP1

## 12.1 Evidencia Logcat

Logcat registra:

- inicio `12:30:11.583`;
- hilo `29183`;
- fallo `12:30:14.607`;
- `SocketTimeoutException`;
- duración `3 024 ms`.

---

## 12.2 Infraestructura

Puerto `8080`:

`SIN LISTENER`

Hora:

`12:29:36.332`

---

## 12.3 Android

Mensaje:

`No fue posible crear la actividad`

Falso éxito:

`NO`

---

## 12.4 PostgreSQL

Resultado:

`0 COINCIDENCIAS`

---

## 12.5 Confirmaciones operativas

- identificador correcto;
- una pulsación de `Guardar`.

---

## 12.6 Limitaciones

Se conservan:

- identificador confirmado operativamente;
- pulsación única confirmada operativamente;
- commit `11c4092` comprobado posteriormente;
- ausencia de captura contemporánea completa de configuración HTTP.

---

## 12.7 Decisión metodológica

No se identificó una desviación adicional que impidiera reconstruir suficientemente la operación.

Clasificación:

`TIMEOUT3-R1-REP1 = EVALUABLE`

Valor:

`T1 = 3 024 ms`

---

# 13. Estado de reemplazos

Máximo permitido:

`1`

Utilizados:

`1`

Disponibles:

`0`

Reemplazo utilizado:

`TIMEOUT3-R1-REP1`

Resultado:

`EVALUABLE`

---

# 14. TIMEOUT3-R2

Estado:

`EJECUTADA — EVALUABLE CON INCIDENCIA DOCUMENTADA`

Identificador definido:

`S11-A4-TIMEOUT3-R2`

Valor admitido:

`T2 = 3 011 ms`

La corrida presenta una incidencia de preparación asociada al uso previo del mismo identificador experimental antes de la ventana medida.

---

## 14.1 Antecedente previo a la ventana experimental

Antes de ejecutar la ventana experimental de TIMEOUT3-R2 se realizaron accidentalmente dos creaciones con nombres similares mientras el backend se encontraba disponible.

Una utilizó:

`S11-A4-TIMEOUT3-R2`

La otra utilizó un identificador con sufijo:

`-CONTROL`

Estas dos creaciones accidentales ocurrieron:

`ANTES DE LA VENTANA EXPERIMENTAL DE R2`

No forman parte del POST medido de las:

`13:18:00.033`

pero son relevantes para interpretar el estado de PostgreSQL.

---

## 14.2 Fila histórica

Como consecuencia de la preparación anterior, PostgreSQL ya contenía una fila histórica para:

`S11-A4-TIMEOUT3-R2`

ID:

`1500185`

Estado:

`is_deleted = true`

La fila fue eliminada lógicamente antes de la ventana experimental.

Por tanto, no corresponde afirmar que:

`S11-A4-TIMEOUT3-R2 NUNCA HABÍA SIDO UTILIZADO`

ni que:

`R2 COMENZÓ SIN ANTECEDENTES PARA ESE IDENTIFICADOR`

---

## 14.3 Diferencia respecto de REP1

La preparación de TIMEOUT3-R2 no fue idéntica a la de TIMEOUT3-R1-REP1.

En R2 existió:

`UNA ELIMINACIÓN LÓGICA PREVIA DE UNA FILA CON EL MISMO IDENTIFICADOR`

Esta diferencia permanece registrada como una limitación de comparabilidad procedimental.

No se oculta ni se reinterpreta como si ambas preparaciones hubieran sido idénticas.

---

## 14.4 Ventana experimental considerada

Después de la preparación anterior se ejecutó la ventana experimental utilizada para medir TIMEOUT3-R2.

Durante esta ventana se observó:

`UN ÚNICO POST EXPERIMENTAL`

Se confirmó operativamente:

`UNA SOLA PULSACIÓN DE GUARDAR`

No se afirma que se haya creado exitosamente una nueva actividad.

La operación experimental terminó en error.

---

## 14.5 Comprobación de indisponibilidad

Puerto:

`8080`

Hora de comprobación:

`13:17:16.961`

Resultado:

`SIN LISTENER`

Esta comprobación ocurrió antes del POST experimental iniciado a las:

`13:18:00.033`

Por tanto, la indisponibilidad observada del backend fue comprobada antes de la operación medida.

---

## 14.6 POST experimental

Inicio:

`13:18:00.033`

Hilo OkHttp:

`30386`

Resultado:

`SocketTimeoutException`

Fin:

`13:18:03.044`

El inicio y el fallo corresponden a la solicitud experimental registrada en la ventana medida.

---

## 14.7 Correlación HTTP

Inicio:

`13:18:00.033`

Fin:

`13:18:03.044`

Hilo:

`30386`

Excepción:

`SocketTimeoutException`

La correspondencia entre inicio y fallo permite calcular la duración técnica.

---

## 14.8 Duración técnica

Cálculo:

`03.044 - 00.033 = 3.011 segundos`

Duración observada:

`3 011 ms`

Valor admitido:

`T2 = 3 011 ms`

---

## 14.9 Excepción

Tipo:

`SocketTimeoutException`

La excepción se registra independientemente del valor temporal.

---

## 14.10 Resultado Android

Android mostró:

`No fue posible crear la actividad`

Falso éxito observado:

`NO`

Esta observación corresponde al comportamiento visible de la aplicación.

---

## 14.11 Pulsaciones de Guardar

Pulsaciones durante la ventana experimental:

`1`

Fuente:

`CONFIRMACIÓN OPERATIVA`

La pulsación única no se presenta como información derivada directamente del Logcat BASIC.

---

## 14.12 PostgreSQL antes de la operación

Antes de la ventana experimental se conocía la existencia de:

`ID 1500185`

Título:

`S11-A4-TIMEOUT3-R2`

Estado:

`is_deleted = true`

Esta fila constituye el antecedente histórico utilizado para interpretar correctamente la consulta posterior.

---

## 14.13 PostgreSQL después de la operación

Después del POST experimental se consultó nuevamente:

`S11-A4-TIMEOUT3-R2`

La consulta devolvió:

`1 FILA`

ID:

`1500185`

Estado:

`is_deleted = true`

Filas adicionales observadas:

`0`

Por tanto, no debe registrarse:

`0 COINCIDENCIAS`

porque existe una coincidencia histórica.

La formulación correcta es:

`0 FILAS NUEVAS OBSERVADAS`

---

## 14.14 Comparación antes/después

### Antes

Fila:

`1500185`

Estado:

`is_deleted = true`

### Después

Fila:

`1500185`

Estado:

`is_deleted = true`

Filas adicionales:

`0`

La consulta posterior mostró exactamente la fila histórica ya conocida y no mostró una segunda fila.

Por tanto:

`NO SE OBSERVÓ NUEVA PERSISTENCIA REMOTA ASOCIADA AL POST EXPERIMENTAL`

Esta afirmación no significa que el identificador nunca hubiera existido.

Significa que no apareció una nueva fila durante la comparación antes/después utilizada para la corrida.

---

## 14.15 Incidencia de identificación

El identificador:

`S11-A4-TIMEOUT3-R2`

había sido utilizado antes de la ventana experimental.

Esto constituye una incidencia respecto del objetivo de mantener identificadores distinguibles y simples de rastrear.

La existencia de la fila histórica significa que una consulta posterior con:

`1 coincidencia`

no puede interpretarse automáticamente como persistencia causada por el POST experimental.

Por esta razón, la evaluación utiliza:

`COMPARACIÓN DEL ESTADO DE POSTGRESQL ANTES Y DESPUÉS`

y no únicamente el número bruto de coincidencias posteriores.

---

## 14.16 Integridad de evidencia

Archivo privado:

`evidencia-timeout/TIMEOUT3-R2/logcat-TIMEOUT3-R2-privado.txt`

Estado:

`CONSERVADO`

El archivo contiene las marcas necesarias para correlacionar el inicio y el fallo del POST.

SHA-256:

`EEF1B3083BA7470C350C9C6E935295F06AA0FB275FF9D02A17331CA33AB16FB0`

El Logcat privado permanece fuera del repositorio público.

---

# 15. Evaluabilidad de TIMEOUT3-R2

## 15.1 Evidencia registrada en Logcat

Logcat permite observar:

- inicio del POST a las `13:18:00.033`;
- hilo OkHttp `30386`;
- fallo a las `13:18:03.044`;
- `SocketTimeoutException`;
- duración calculable de `3 011 ms`.

La solicitud y su resultado son técnicamente correlacionables.

---

## 15.2 Comprobación de infraestructura

Puerto:

`8080`

Resultado:

`SIN LISTENER`

Hora:

`13:17:16.961`

La comprobación ocurrió antes del POST experimental.

---

## 15.3 Observación Android

Mensaje:

`No fue posible crear la actividad`

Falso éxito:

`NO OBSERVADO`

---

## 15.4 Confirmación operativa

Durante la ventana experimental se confirmó:

`UNA SOLA PULSACIÓN DE GUARDAR`

---

## 15.5 Comprobación PostgreSQL

Estado conocido antes del POST:

`1500185 — is_deleted = true`

Estado observado después del POST:

`1500185 — is_deleted = true`

Filas nuevas observadas:

`0`

La comparación permite distinguir la fila histórica de una eventual persistencia nueva.

---

## 15.6 Incidencia conservada

Incidencia:

`USO PREVIO DEL IDENTIFICADOR S11-A4-TIMEOUT3-R2`

Las dos creaciones accidentales con nombres similares ocurrieron:

`ANTES DE LA VENTANA EXPERIMENTAL`

Una correspondía al identificador:

`S11-A4-TIMEOUT3-R2`

y otra utilizaba el sufijo:

`-CONTROL`

La preparación previa no fue idéntica a la de REP1 porque incluyó una eliminación lógica.

Esta diferencia permanece documentada.

---

## 15.7 Limitación de persistencia

La existencia previa de la fila histórica impide utilizar simplemente:

`NÚMERO DE COINCIDENCIAS = 1`

como prueba aislada sobre el POST experimental.

La evidencia de ausencia de nueva persistencia se apoya en:

`ANTES = FILA 1500185`

y:

`DESPUÉS = LA MISMA FILA 1500185, SIN FILAS ADICIONALES`

---

## 15.8 Decisión metodológica

La incidencia de identificación no impide reconstruir suficientemente la operación experimental porque:

- la indisponibilidad del puerto fue comprobada antes del POST;
- el POST tiene inicio y fallo correlacionables;
- el hilo OkHttp es identificable;
- la duración puede calcularse;
- se confirmó una sola pulsación de `Guardar` durante la ventana;
- Android mostró error;
- no se observó falso éxito;
- se conoce el estado histórico previo de PostgreSQL;
- la consulta posterior mostró la misma fila histórica `1500185`;
- no se observó una segunda fila;
- la evidencia privada quedó conservada;
- la incidencia previa permanece documentada.

Clasificación:

`TIMEOUT3-R2 = EVALUABLE CON INCIDENCIA DOCUMENTADA`

Valor admitido:

`T2 = 3 011 ms`

La clasificación no elimina la incidencia ni presenta la preparación de R2 como idéntica a REP1.

---

# 16. TIMEOUT3-R3

Estado:

`NO EJECUTADA`

Identificador definido:

`S11-A4-TIMEOUT3-R3`

Valor:

`T3 = PENDIENTE`

---

# 17. Estado de los valores posteriores

TIMEOUT3-R1:

`NO EVALUABLE`

Observaciones descriptivas:

- `3 012 ms`
- `3 015 ms`

TIMEOUT3-R1-REP1:

`EVALUABLE`

T1:

`3 024 ms`

TIMEOUT3-R2:

`EVALUABLE CON INCIDENCIA DOCUMENTADA`

T2:

`3 011 ms`

TIMEOUT3-R3:

`NO EJECUTADA`

T3:

`PENDIENTE`

Corridas posteriores evaluables:

`2 de 3`

Reemplazos utilizados:

`1 de 1`

Reemplazos disponibles:

`0`

---

# 18. Mediana posterior

El prerregistro requiere:

`3 CORRIDAS EVALUABLES DESPUÉS DE LA INTERVENCIÓN`

Actualmente existen:

`2 de 3`

Valores disponibles:

- `T1 = 3 024 ms`
- `T2 = 3 011 ms`

Valor pendiente:

`T3`

Por tanto:

`M_después = NO CALCULADA`

No corresponde calcular todavía la mediana con solamente dos valores.

Las observaciones descriptivas de TIMEOUT3-R1:

- `3 012 ms`
- `3 015 ms`

no participan en este cálculo.

---

# 19. Reducción porcentual

Mediana de línea base:

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

Aunque ya se dispone de:

- `T1 = 3 024 ms`
- `T2 = 3 011 ms`

estos valores no sustituyen la mediana posterior requerida.

No se calcula anticipadamente la reducción principal.

---

# 20. Estado de la hipótesis

Hipótesis preregistrada:

> Ante una indisponibilidad controlada de Spring Boot, configurar explícitamente `connectTimeout = 3 segundos` reducirá al menos un 50 % la mediana de la duración técnica de los `POST` fallidos de creación de actividad respecto de una nueva línea base ejecutada bajo el mismo protocolo, sin que Android presente falsamente la operación como exitosa ni se observe persistencia remota inesperada de la actividad intentada durante el fallo.

Estado:

`NO EVALUADA`

La condición posterior dispone actualmente de:

`2 de 3 CORRIDAS EVALUABLES`

T1:

`3 024 ms`

T2:

`3 011 ms`

T3:

`PENDIENTE`

Por tanto, no corresponde declarar todavía:

`HIPÓTESIS RESPALDADA`

ni:

`HIPÓTESIS NO RESPALDADA`

---

# 21. Diferencia entre corrida no evaluable y comparación principal no evaluable

TIMEOUT3-R1 está clasificada:

`NO EVALUABLE`

Esta clasificación corresponde únicamente a:

`UNA CORRIDA`

La comparación principal:

`NO ESTÁ CLASIFICADA COMO NO EVALUABLE`

Actualmente existen:

- tres corridas evaluables en línea base;
- dos corridas evaluables posteriores;
- TIMEOUT3-R3 pendiente.

Por tanto:

`COMPARACIÓN PRINCIPAL = ABIERTA`

---

# 22. Consecuencia del reemplazo utilizado

La condición posterior ya utilizó:

`1 DE 1 REEMPLAZO PERMITIDO`

Reemplazos disponibles:

`0`

El reemplazo utilizado fue:

`TIMEOUT3-R1-REP1`

Resultado:

`EVALUABLE`

Valor:

`T1 = 3 024 ms`

Por tanto, TIMEOUT3-R3 deberá ejecutarse conforme al protocolo sin posibilidad de una segunda repetición de reemplazo en esta condición.

Si R3 resulta evaluable:

`SE DISPONDRÁ DE T1, T2 Y T3`

Si R3 resulta no evaluable:

`NO EXISTIRÁ UN REEMPLAZO ADICIONAL DISPONIBLE`

y deberá aplicarse posteriormente la regla global correspondiente del prerregistro.

---

# 23. Diferencia entre hipótesis no respaldada y comparación principal no evaluable

Si finalmente existen:

- tres corridas evaluables de línea base;
- tres corridas evaluables posteriores;

y la reducción calculada resulta inferior al:

`50 %`

corresponderá:

`HIPÓTESIS NO RESPALDADA`

En cambio, si al finalizar la condición posterior no se consiguen tres corridas evaluables y ya fue utilizado el único reemplazo permitido, corresponderá aplicar la regla de:

`COMPARACIÓN PRINCIPAL NO EVALUABLE`

Estas categorías:

`NO SON EQUIVALENTES`

Estado actual:

`NINGUNA CORRESPONDE TODAVÍA`

---

# 24. Evidencia privada

Los Logcat completos permanecen fuera del repositorio público.

## 24.1 BASE-R1

Archivo:

`evidencia-timeout/BASE-R1/logcat-BASE-R1-privado.txt`

SHA-256:

`DE5066FCF6BA7B86E1BD12FCA3041AD77513C38499E25578EB92DD27DA6E1AE5`

---

## 24.2 BASE-R2

Archivo:

`evidencia-timeout/BASE-R2/logcat-BASE-R2-privado.txt`

SHA-256:

`2BF5FDDEA23BF2D4644A616BF46DE9A1B9172EA00091E30348FA5CB571F7F907`

---

## 24.3 BASE-R3

Archivo:

`evidencia-timeout/BASE-R3/logcat-BASE-R3-privado.txt`

SHA-256:

`6508512032222FCF374F0C8998018DB01E8D466734F0B2516F57128B1894D5FF`

---

## 24.4 TIMEOUT3-R1

SHA-256:

`787BB5935347EABAD2C281765E715C5F247BEA3FB626DEF21768273BCCC9AC81`

Estado:

`EVIDENCIA PRIVADA CONSERVADA`

---

## 24.5 TIMEOUT3-R1-REP1

Archivo:

`evidencia-timeout/TIMEOUT3-R1-REP1/logcat-TIMEOUT3-R1-REP1-privado.txt`

SHA-256:

`34A67A1FA46927A4C65EA3CE8C40923A87BE2F8A75EB510B6A0088142C371EC2`

Estado:

`EVIDENCIA PRIVADA CONSERVADA`

---

## 24.6 TIMEOUT3-R2

Archivo:

`evidencia-timeout/TIMEOUT3-R2/logcat-TIMEOUT3-R2-privado.txt`

SHA-256:

`EEF1B3083BA7470C350C9C6E935295F06AA0FB275FF9D02A17331CA33AB16FB0`

Estado:

`EVIDENCIA PRIVADA CONSERVADA`

Los archivos privados no deberán incorporarse directamente al repositorio público sin:

- revisión;
- saneamiento;
- eliminación de JWT;
- eliminación de credenciales;
- eliminación de encabezados sensibles;
- eliminación de tokens;
- eliminación de secretos;
- eliminación de información innecesaria.

---

# 25. Verificación histórica del prerregistro

Estado:

`COMPLETADA`

La consulta directa de:

`94dcc32`

confirmó que antes de las mediciones ya estaban establecidos:

- tres corridas principales posteriores;
- `S11-A4-TIMEOUT3-R1`;
- `S11-A4-TIMEOUT3-R2`;
- `S11-A4-TIMEOUT3-R3`;
- una sola pulsación de `Guardar`;
- criterios de corrida no evaluable;
- más de una pulsación como ejemplo de no evaluabilidad;
- máximo un reemplazo por condición;
- identificador nuevo para el reemplazo;
- conservación de la corrida original;
- mantenimiento del mismo protocolo;
- tres corridas evaluables como condición para calcular la mediana posterior.

Estado:

`CERRADA`

No es necesario repetir la comprobación histórica para ejecutar R3.

---

# 26. Trazabilidad Git

Commit de prerregistro:

`94dcc32`

Commit BASE-R1:

`b02c239`

Commit provisional BASE-R2:

`e1dab87`

Commit de cierre metodológico BASE-R2:

`01d8e6a`

Commit de cierre de línea base:

`b6f82a5`

Merge de línea base:

`be2b2f3`

Commit de intervención:

`a722954`

Commit documental de TIMEOUT3-R1:

`cc68828`

Commit de revisión documental previo:

`964ef25`

Commit comprobado posteriormente para REP1:

`11c4092`

Commit documental de resultado y evaluabilidad de REP1:

`bf8fcbc`

Commit documental de R2:

`PENDIENTE`

El próximo commit deberá ser:

`POSTERIOR A bf8fcbc`

y registrar exclusivamente la actualización documental correspondiente a TIMEOUT3-R2.

Secuencia consolidada:

`prerregistro -> línea base -> cierre -> intervención -> TIMEOUT3-R1 -> clasificación NO EVALUABLE -> REP1 -> clasificación EVALUABLE -> commit bf8fcbc -> TIMEOUT3-R2 -> clasificación EVALUABLE CON INCIDENCIA DOCUMENTADA -> commit pendiente`

No se reescriben los commits ya publicados.

---

# 27. Estado consolidado de la Actividad 4

## 27.1 Línea base

BASE-R1:

`EVALUABLE CON DESVIACIÓN DOCUMENTADA`

Valor:

`B1 = 10 028 ms`

BASE-R2:

`EVALUABLE CON INCIDENCIA PREVIA RESUELTA`

Valor:

`B2 = 10 033 ms`

BASE-R3:

`EVALUABLE CON INCIDENCIA DOCUMENTADA`

Valor:

`B3 = 10 023 ms`

Mediana:

`M_antes = 10 028 ms`

Estado:

`CERRADA`

---

## 27.2 Intervención

Configuración:

`connectTimeout = 3 segundos`

Commit:

`a722954`

Estado:

`IMPLEMENTADA`

---

## 27.3 TIMEOUT3-R1

Estado:

`NO EVALUABLE`

Observaciones:

- `3 012 ms`
- `3 015 ms`

Valor para comparación:

`NO ASIGNADO`

SHA-256:

`787BB5935347EABAD2C281765E715C5F247BEA3FB626DEF21768273BCCC9AC81`

---

## 27.4 TIMEOUT3-R1-REP1

Estado:

`EVALUABLE`

Valor:

`T1 = 3 024 ms`

SHA-256:

`34A67A1FA46927A4C65EA3CE8C40923A87BE2F8A75EB510B6A0088142C371EC2`

---

## 27.5 TIMEOUT3-R2

Estado:

`EVALUABLE CON INCIDENCIA DOCUMENTADA`

Identificador:

`S11-A4-TIMEOUT3-R2`

Antecedente:

`FILA HISTÓRICA 1500185 — is_deleted = true`

Creaciones accidentales previas:

`2, ANTERIORES A LA VENTANA EXPERIMENTAL`

Una utilizó:

`S11-A4-TIMEOUT3-R2`

La otra utilizó:

`SUFIJO -CONTROL`

Puerto sin listener:

`13:17:16.961`

Inicio POST:

`13:18:00.033`

Fin:

`13:18:03.044`

Hilo:

`30386`

Excepción:

`SocketTimeoutException`

Duración:

`3 011 ms`

Android:

`No fue posible crear la actividad`

Falso éxito:

`NO OBSERVADO`

Guardar:

`1 SEGÚN CONFIRMACIÓN OPERATIVA`

PostgreSQL posterior:

`MISMA FILA HISTÓRICA 1500185 — is_deleted = true`

Filas nuevas observadas:

`0`

SHA-256:

`EEF1B3083BA7470C350C9C6E935295F06AA0FB275FF9D02A17331CA33AB16FB0`

Valor admitido:

`T2 = 3 011 ms`

---

## 27.6 TIMEOUT3-R3

Estado:

`NO EJECUTADA`

T3:

`PENDIENTE`

---

## 27.7 Estado posterior

T1:

`3 024 ms`

T2:

`3 011 ms`

T3:

`PENDIENTE`

Corridas posteriores evaluables:

`2 de 3`

M_después:

`NO CALCULADA`

Reducción:

`NO CALCULADA`

Hipótesis:

`NO EVALUADA`

Comparación principal:

`ABIERTA`

---

# 28. Próximo punto de control

TIMEOUT3-R2 queda documentada como:

`EVALUABLE CON INCIDENCIA DOCUMENTADA`

Valor:

`T2 = 3 011 ms`

La incidencia histórica del identificador permanece registrada.

No es necesario repetir:

- la línea base;
- TIMEOUT3-R1;
- TIMEOUT3-R1-REP1;
- TIMEOUT3-R2.

Todavía no debe ejecutarse TIMEOUT3-R3 hasta registrar documentalmente R2 en Git.

---

## 28.1 Registro documental de R2

Archivo:

`experimentos/semana11-crecimiento-post-activities/resultados-spike-timeout.md`

Antes del commit deberá revisarse:

```powershell
$ruta = "experimentos/semana11-crecimiento-post-activities/resultados-spike-timeout.md"

git diff -- $ruta
```

La revisión deberá comprobar especialmente que:

- `T2 = 3 011 ms`;
- no aparece `0 coincidencias` para R2;
- se utiliza `0 filas nuevas observadas`;
- se conserva `1500185 — is_deleted = true`;
- se registra `13:17:16.961`;
- las dos creaciones accidentales figuran como anteriores a la ventana experimental;
- no se afirma que R2 comenzara desde cero;
- no se afirma que el identificador nunca se hubiera utilizado;
- no se presenta la preparación de R2 como idéntica a REP1;
- `M_después` permanece sin calcular;
- la hipótesis permanece `NO EVALUADA`;
- TIMEOUT3-R3 permanece pendiente.

---

## 28.2 Después del registro documental

Una vez registrada R2 mediante un commit posterior a:

`bf8fcbc`

podrá ejecutarse:

`TIMEOUT3-R3`

Identificador:

`S11-A4-TIMEOUT3-R3`

---

## 28.3 Reglas para R3

Antes de R3 deberá comprobarse:

1. Spring Boot disponible;
2. PostgreSQL disponible;
3. comunicación Android-backend funcional;
4. usuario autenticado;
5. captura de Logcat activa;
6. identificador correcto;
7. ausencia de una fila histórica previa con `S11-A4-TIMEOUT3-R3`, si técnicamente puede comprobarse;
8. condición experimental correspondiente.

Después:

1. finalizar el control previo;
2. detener Spring Boot;
3. comprobar puerto `8080` sin listener;
4. conservar la hora de comprobación;
5. verificar `S11-A4-TIMEOUT3-R3`;
6. pulsar `Guardar` exactamente una vez;
7. no realizar otro POST voluntario;
8. esperar el resultado;
9. conservar el inicio del POST;
10. conservar el fallo correspondiente;
11. calcular la duración;
12. registrar Android;
13. consultar PostgreSQL;
14. registrar coincidencias;
15. conservar la evidencia;
16. calcular SHA-256;
17. documentar cualquier incidencia;
18. clasificar R3 antes de incorporarla como T3.

---

## 28.4 Restricción de reemplazo

La condición posterior ya utilizó:

`1 DE 1 REEMPLAZO`

Por tanto:

`NO EXISTE UN REEMPLAZO ADICIONAL DISPONIBLE`

Si R3 resulta no evaluable, deberá conservarse ese resultado y aplicarse la regla global preregistrada.

---

## 28.5 Estado hasta ejecutar R3

Hasta completar TIMEOUT3-R3:

`T1 = 3 024 ms`

`T2 = 3 011 ms`

`T3 = PENDIENTE`

`M_después = NO CALCULADA`

`REDUCCIÓN = NO CALCULADA`

`HIPÓTESIS = NO EVALUADA`

`COMPARACIÓN PRINCIPAL = ABIERTA`