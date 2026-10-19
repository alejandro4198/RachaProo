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

Valor de TIMEOUT3-R1 para la comparación:

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

Reemplazos utilizados en la condición posterior:

`1 de 1 permitido`

TIMEOUT3-R2:

`NO EJECUTADA`

TIMEOUT3-R3:

`NO EJECUTADA`

Valores posteriores evaluables consolidados:

`1 de 3`

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
- `TIMEOUT3-R2 = NO EJECUTADA`
- `TIMEOUT3-R3 = NO EJECUTADA`

Valores evaluables consolidados:

`1 de 3`

Valor disponible:

`T1 = 3 024 ms`

Reemplazos utilizados:

`1 de 1 permitido`

No existen reemplazos adicionales disponibles en la condición posterior.

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

Por tanto, se conservan dos mediciones:

- `3 012 ms`
- `3 015 ms`

Estas mediciones son:

`HECHOS TÉCNICOS OBSERVADOS`

No se utiliza ninguna como:

`T1`

La decisión metodológica de la corrida se registra independientemente de los valores observados.

---

## 8.5 Identificadores utilizados

La versión histórica contiene:

`S11-A4-TIMEOUT3-R1`

como identificador de esta corrida.

Según confirmación operativa retrospectiva, durante la ejecución se utilizaron dos títulos en este orden:

1. `S11-A4-TIMEOUT-R1`
2. `S11-A4-TIMEOUT3-R1`

Por tanto:

- el primer intento utilizó un identificador distinto del definido;
- el segundo utilizó el identificador definido.

La secuencia corresponde a:

`CONFIRMACIÓN OPERATIVA RETROSPECTIVA`

El Logcat BASIC no muestra directamente el contenido funcional del body.

---

## 8.6 Operación de las 20:32:25.078

Se observó adicionalmente una operación con error a las:

`20:32:25.078`

La revisión posterior permitió identificarla como:

`PATCH`

No corresponde a:

`POST /api/activities`

Por tanto:

`NO SE CONTABILIZA COMO UN TERCER POST DE CREACIÓN`

---

## 8.7 Resultado Android

Android mostró:

`No fue posible crear la actividad`

Falso éxito:

`NO OBSERVADO`

---

## 8.8 Persistencia remota

Consulta:

`S11-A4-TIMEOUT3-R1`

Resultado:

`0 FILAS`

---

## 8.9 Integridad

SHA-256 del Logcat privado:

`787BB5935347EABAD2C281765E715C5F247BEA3FB626DEF21768273BCCC9AC81`

---

# 9. Evaluabilidad de TIMEOUT3-R1

## 9.1 Regla aplicable

El prerregistro histórico establece que una corrida puede clasificarse:

`NO EVALUABLE`

cuando:

`EL PROTOCOLO NO PUDO CUMPLIRSE SUFICIENTEMENTE`

También establece:

`PULSAR GUARDAR EXACTAMENTE UNA VEZ`

Entre los ejemplos definidos para una corrida no evaluable aparece:

`HUBO MÁS DE UNA PULSACIÓN DE GUARDAR`

---

## 9.2 Desviación observada

Durante TIMEOUT3-R1 se realizaron dos intentos POST de creación.

Según confirmación operativa:

- el primero utilizó `S11-A4-TIMEOUT-R1`;
- el segundo utilizó `S11-A4-TIMEOUT3-R1`.

Los POST son temporalmente distinguibles.

Por tanto, la decisión no se fundamenta en una imposibilidad de correlación temporal.

La desviación corresponde al incumplimiento del procedimiento de una única operación experimental.

---

## 9.3 Decisión metodológica

Clasificación:

`TIMEOUT3-R1 = NO EVALUABLE`

Razón:

`EL PROTOCOLO NO PUDO CUMPLIRSE SUFICIENTEMENTE`

Criterio aplicado:

`MÁS DE UNA PULSACIÓN DE GUARDAR DURANTE LA MISMA CORRIDA`

Valor para la comparación:

`NO ASIGNADO`

Las mediciones:

- `3 012 ms`
- `3 015 ms`

se conservan como:

`OBSERVACIONES DESCRIPTIVAS`

TIMEOUT3-R1:

- permanece registrada;
- conserva su evidencia;
- conserva sus mediciones;
- conserva su SHA-256;
- no participa en la comparación principal;
- no cuenta como respaldo;
- no cuenta como no respaldo de la hipótesis.

---

# 10. Regla de reemplazo

La versión histórica `94dcc32` establece:

`MÁXIMO 1 REPETICIÓN DE REEMPLAZO POR CONDICIÓN`

La repetición puede realizarse cuando una corrida ha sido previamente clasificada:

`NO EVALUABLE`

La repetición:

- debe responder a una causa metodológica documentada;
- no puede justificarse por el valor temporal;
- conserva la evidencia original;
- utiliza un identificador nuevo;
- sigue el mismo protocolo;
- debe quedar registrada explícitamente como reemplazo.

La corrida original no evaluable:

`NO SERÁ ELIMINADA NI SUSTITUIDA DEL REGISTRO HISTÓRICO`

TIMEOUT3-R1 cumplió la condición previa para utilizar la repetición.

---

# 11. TIMEOUT3-R1-REP1

Estado:

`EJECUTADA`

Clasificación metodológica:

`EVALUABLE`

Valor admitido:

`T1 = 3 024 ms`

Fecha:

`2026-10-19`

Identificador del reemplazo:

`S11-A4-TIMEOUT3-R1-REP1`

Tipo:

`REPETICIÓN DE REEMPLAZO DE TIMEOUT3-R1`

Configuración experimental prevista para la condición:

`connectTimeout = 3 segundos`

---

## 11.1 Identificador

Identificador utilizado:

`S11-A4-TIMEOUT3-R1-REP1`

La utilización exacta de este título fue:

`CONFIRMADA OPERATIVAMENTE`

El Logcat BASIC no expone por sí solo el título funcional contenido en el body.

Por tanto, el identificador se conserva como:

`CONFIRMACIÓN OPERATIVA`

y no como lectura directa del body desde Logcat BASIC.

---

## 11.2 Estado inicial y control previo

Antes de inducir la indisponibilidad se realizó un control previo.

Resultado:

`HTTP 201`

Persistencia observada del control:

`1 FILA`

ID:

`1500184`

El control constituye evidencia de que la comunicación Android-backend y la persistencia remota funcionaban antes de inducir la indisponibilidad.

---

## 11.3 PostgreSQL

PostgreSQL se encontraba disponible durante la preparación de la corrida.

La existencia de la fila de control:

`1500184`

constituye evidencia de disponibilidad de persistencia durante el control previo.

Después del POST experimental se realizó una consulta específica para:

`S11-A4-TIMEOUT3-R1-REP1`

Resultado:

`0 COINCIDENCIAS`

---

## 11.4 Inducción de indisponibilidad

Después del control previo se detuvo Spring Boot.

Se comprobó el puerto:

`8080`

Resultado:

`SIN LISTENER`

Hora registrada:

`12:29:36.332`

Esta comprobación ocurrió antes del POST experimental.

---

## 11.5 POST experimental

Inicio:

`12:30:11.583`

Hilo OkHttp:

`29183`

Resultado:

`SocketTimeoutException`

Fin:

`12:30:14.607`

El inicio y el fallo corresponden al mismo hilo OkHttp identificado.

---

## 11.6 Correlación HTTP

Inicio:

`12:30:11.583`

Fin:

`12:30:14.607`

Hilo:

`29183`

Resultado:

`SocketTimeoutException`

La correspondencia entre el inicio y el fallo es suficiente para calcular la duración técnica de la solicitud observada.

---

## 11.7 Duración técnica

Cálculo:

`14.607 - 11.583 = 3.024 segundos`

Duración:

`3 024 ms`

Valor observado:

`3 024 ms`

Valor admitido:

`T1 = 3 024 ms`

---

## 11.8 Excepción

Tipo de error observado:

`SocketTimeoutException`

La excepción se registra independientemente de la duración.

---

## 11.9 Resultado visible en Android

Android mostró:

`No fue posible crear la actividad`

Falso éxito observado:

`NO`

Resultado funcional:

`ERROR MOSTRADO — SIN FALSO ÉXITO OBSERVADO`

---

## 11.10 Persistencia experimental

Consulta PostgreSQL:

`S11-A4-TIMEOUT3-R1-REP1`

Resultado:

`0 COINCIDENCIAS`

Interpretación limitada a la consulta realizada:

`NO SE OBSERVÓ PERSISTENCIA REMOTA BAJO EL IDENTIFICADOR EXPERIMENTAL CONSULTADO`

---

## 11.11 Pulsaciones de Guardar

Número de pulsaciones:

`1`

Naturaleza de la evidencia:

`CONFIRMACIÓN OPERATIVA`

La pulsación única no se presenta como un dato derivado directamente del Logcat.

---

## 11.12 Commit asociado a la ejecución

Commit comprobado posteriormente:

`11c4092`

La comprobación se realizó:

`DESPUÉS DE LA EJECUCIÓN`

Por tanto, `11c4092` no se presenta como una captura Git realizada durante la ventana experimental.

Forma de registro:

`EL COMMIT ACTIVO FUE COMPROBADO POSTERIORMENTE COMO 11c4092`

Esta comprobación posterior forma parte de la trazabilidad de la ejecución, conservando explícitamente su momento de obtención.

---

## 11.13 Configuración experimental y limitación de comprobación

La condición experimental definida para esta fase era:

`connectTimeout = 3 segundos`

La intervención había sido implementada previamente en:

`a722954`

Durante REP1 no se registró una captura instrumental contemporánea que enumerara toda la configuración HTTP durante la ventana exacta de ejecución.

Por tanto, no se afirma:

`TODA LA CONFIGURACIÓN HTTP FUE VERIFICADA INSTRUMENTALMENTE DURANTE REP1`

La trazabilidad disponible incluye:

- intervención previamente registrada;
- continuidad de la rama experimental;
- commit posteriormente comprobado;
- ausencia de una desviación de configuración reportada durante la ejecución.

Esta limitación se conserva expresamente.

---

## 11.14 Evidencia privada

Archivo:

`evidencia-timeout/TIMEOUT3-R1-REP1/logcat-TIMEOUT3-R1-REP1-privado.txt`

SHA-256:

`34A67A1FA46927A4C65EA3CE8C40923A87BE2F8A75EB510B6A0088142C371EC2`

Estado:

`EVIDENCIA PRIVADA CONSERVADA`

El archivo privado no se incorpora al repositorio público.

---

# 12. Evaluabilidad de TIMEOUT3-R1-REP1

## 12.1 Fuentes de evidencia disponibles

La evaluación de REP1 se apoya en varias fuentes de evidencia que se mantienen diferenciadas.

### 12.1.1 Evidencia registrada en Logcat

El Logcat permite observar y correlacionar:

- inicio del POST experimental a las `12:30:11.583`;
- hilo OkHttp `29183`;
- fallo a las `12:30:14.607`;
- `SocketTimeoutException`;
- duración calculable de `3 024 ms`.

Estas observaciones proceden directamente del registro técnico de la solicitud.

### 12.1.2 Comprobación de infraestructura

Antes del POST experimental se comprobó:

- puerto `8080`;
- ausencia de listener;
- hora de comprobación `12:29:36.332`.

Esta evidencia permite documentar la indisponibilidad observada de Spring Boot antes de la operación medida.

### 12.1.3 Observación funcional en Android

La aplicación mostró:

`No fue posible crear la actividad`

Falso éxito observado:

`NO`

Esta información corresponde al comportamiento visible de Android y no al contenido del Logcat HTTP.

### 12.1.4 Consulta de PostgreSQL

Después de la operación experimental se consultó:

`S11-A4-TIMEOUT3-R1-REP1`

Resultado:

`0 COINCIDENCIAS`

Esta información procede de la comprobación remota en PostgreSQL y se mantiene diferenciada del Logcat.

### 12.1.5 Evidencia del control previo

Antes de inducir la indisponibilidad se obtuvo:

`HTTP 201`

y se observó la fila:

`1500184`

La combinación permite documentar el funcionamiento de la comunicación y persistencia antes de la operación experimental.

### 12.1.6 Integridad de la evidencia privada

Archivo:

`evidencia-timeout/TIMEOUT3-R1-REP1/logcat-TIMEOUT3-R1-REP1-privado.txt`

SHA-256:

`34A67A1FA46927A4C65EA3CE8C40923A87BE2F8A75EB510B6A0088142C371EC2`

El hash permite comprobar posteriormente la integridad del archivo privado conservado.

---

## 12.2 Confirmaciones operativas

Se confirmó operativamente:

- identificador `S11-A4-TIMEOUT3-R1-REP1`;
- una sola pulsación de `Guardar`.

Estas confirmaciones no se presentan como información extraída directamente del Logcat BASIC.

---

## 12.3 Comprobación posterior

Se comprobó posteriormente:

`11c4092`

como commit activo asociado a la ejecución.

Esta comprobación no se presenta como una observación realizada durante la ventana experimental.

---

## 12.4 Contraste con el protocolo

### Comunicación inicial

Control previo:

`HTTP 201`

Evaluación:

`CUMPLE`

### PostgreSQL disponible

Fila de control:

`1500184`

Evaluación:

`CUMPLE`

### Identificador nuevo

Identificador:

`S11-A4-TIMEOUT3-R1-REP1`

Fuente:

`CONFIRMACIÓN OPERATIVA`

Evaluación:

`CUMPLE`

### Indisponibilidad de Spring Boot

Puerto:

`8080`

Resultado:

`SIN LISTENER`

Hora:

`12:29:36.332`

Evaluación:

`CUMPLE`

### Pulsación única

Número:

`1`

Fuente:

`CONFIRMACIÓN OPERATIVA`

Evaluación:

`CUMPLE`

### Correlación del POST

Inicio:

`12:30:11.583`

Fin:

`12:30:14.607`

Hilo:

`29183`

Evaluación:

`CUMPLE`

### Duración

Valor:

`3 024 ms`

Evaluación:

`CALCULABLE`

### Android

Resultado:

`No fue posible crear la actividad`

Falso éxito:

`NO OBSERVADO`

### PostgreSQL posterior

Resultado:

`0 COINCIDENCIAS`

Evaluación:

`CUMPLE`

### Integridad

SHA-256:

`34A67A1FA46927A4C65EA3CE8C40923A87BE2F8A75EB510B6A0088142C371EC2`

Evaluación:

`EVIDENCIA CONSERVADA`

---

## 12.5 Limitaciones conservadas

Se mantienen expresamente las siguientes limitaciones:

1. el identificador exacto corresponde a confirmación operativa y no a exposición del body en Logcat BASIC;
2. la pulsación única corresponde a confirmación operativa;
3. `11c4092` fue comprobado después de la ejecución;
4. no se dispone de una captura instrumental contemporánea que enumere toda la configuración HTTP durante la ventana exacta de REP1.

Estas limitaciones se registran sin transformarlas en evidencia instrumental que no fue obtenida.

Con la evidencia disponible, no se identificó una desviación adicional que impida reconstruir suficientemente la operación experimental.

---

## 12.6 Decisión metodológica

Clasificación:

`TIMEOUT3-R1-REP1 = EVALUABLE`

Justificación:

- existió un control previo exitoso;
- PostgreSQL estuvo disponible durante el control;
- se comprobó la indisponibilidad de Spring Boot antes del intento;
- existe un POST experimental con inicio y resultado correlacionables;
- la duración puede calcularse;
- se confirmó una sola pulsación de `Guardar`;
- se confirmó el identificador nuevo correspondiente al reemplazo;
- Android mostró error;
- no se observó falso éxito;
- PostgreSQL devolvió `0 coincidencias`;
- la evidencia privada quedó conservada;
- las limitaciones de evidencia permanecen explícitamente documentadas;
- no se identificó una desviación adicional que impida considerar suficientemente cumplido el protocolo de la corrida.

Valor admitido:

`T1 = 3 024 ms`

---

## 12.7 Relación con TIMEOUT3-R1

TIMEOUT3-R1 permanece:

`NO EVALUABLE`

Sus observaciones:

- `3 012 ms`
- `3 015 ms`

continúan registradas de manera descriptiva.

TIMEOUT3-R1-REP1:

- no borra TIMEOUT3-R1;
- no modifica sus resultados;
- no reinterpreta sus mediciones;
- no elimina su evidencia;
- ocupa la posición metodológica correspondiente a `T1`.

Valor:

`T1 = 3 024 ms`

---

# 13. Estado de la regla de reemplazo

Máximo permitido en la condición posterior:

`1`

Reemplazos ejecutados:

`1`

Reemplazos disponibles:

`0`

Reemplazo utilizado:

`TIMEOUT3-R1-REP1`

Resultado:

`EVALUABLE`

Valor:

`T1 = 3 024 ms`

No corresponde ejecutar:

`UN SEGUNDO REEMPLAZO EN LA CONDICIÓN POSTERIOR`

---

# 14. TIMEOUT3-R2

Estado:

`NO EJECUTADA`

Identificador definido:

`S11-A4-TIMEOUT3-R2`

Valor:

`T2 = PENDIENTE`

---

# 15. TIMEOUT3-R3

Estado:

`NO EJECUTADA`

Identificador definido:

`S11-A4-TIMEOUT3-R3`

Valor:

`T3 = PENDIENTE`

---

# 16. Estado de los valores posteriores

TIMEOUT3-R1:

`NO EVALUABLE`

Mediciones descriptivas:

- `3 012 ms`
- `3 015 ms`

TIMEOUT3-R1-REP1:

`EVALUABLE`

T1:

`3 024 ms`

TIMEOUT3-R2:

`NO EJECUTADA`

T2:

`PENDIENTE`

TIMEOUT3-R3:

`NO EJECUTADA`

T3:

`PENDIENTE`

Corridas posteriores evaluables consolidadas:

`1 de 3`

Reemplazos utilizados:

`1 de 1`

---

# 17. Mediana posterior

El prerregistro exige:

`3 CORRIDAS EVALUABLES DESPUÉS DE LA INTERVENCIÓN`

Actualmente existe:

`1 de 3`

Valor disponible:

`T1 = 3 024 ms`

Valores pendientes:

- `T2`
- `T3`

Por tanto:

`M_después = NO CALCULADA`

No corresponde calcular una mediana posterior a partir de un único valor.

Las observaciones de TIMEOUT3-R1:

- `3 012 ms`
- `3 015 ms`

no forman parte de la mediana posterior.

---

# 18. Reducción porcentual

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

Criterio:

`REDUCCIÓN >= 50 %`

Aunque:

`T1 = 3 024 ms`

ya está disponible, un único valor no sustituye:

`M_después`

y no se utiliza para anticipar la reducción principal.

---

# 19. Estado de la hipótesis

Hipótesis preregistrada:

> Ante una indisponibilidad controlada de Spring Boot, configurar explícitamente `connectTimeout = 3 segundos` reducirá al menos un 50 % la mediana de la duración técnica de los `POST` fallidos de creación de actividad respecto de una nueva línea base ejecutada bajo el mismo protocolo, sin que Android presente falsamente la operación como exitosa ni se observe persistencia remota inesperada de la actividad intentada durante el fallo.

Estado:

`NO EVALUADA`

La condición posterior dispone actualmente de:

`1 de 3 CORRIDAS EVALUABLES`

TIMEOUT3-R1:

`NO PARTICIPA`

TIMEOUT3-R1-REP1:

`APORTA T1 = 3 024 ms`

Todavía faltan:

- `T2`
- `T3`

Por tanto, no corresponde declarar:

`HIPÓTESIS RESPALDADA`

ni:

`HIPÓTESIS NO RESPALDADA`

---

# 20. Diferencia entre corrida no evaluable y comparación principal no evaluable

TIMEOUT3-R1 está clasificada:

`NO EVALUABLE`

Esta clasificación corresponde a:

`UNA CORRIDA INDIVIDUAL`

La comparación global:

`NO ESTÁ CLASIFICADA COMO NO EVALUABLE`

Actualmente:

- existen tres corridas evaluables de línea base;
- existe una corrida posterior evaluable;
- falta ejecutar TIMEOUT3-R2;
- falta ejecutar TIMEOUT3-R3.

Por tanto:

`COMPARACIÓN PRINCIPAL = ABIERTA`

---

# 21. Consecuencia de haber utilizado el único reemplazo

La condición posterior ya utilizó:

`1 DE 1 REEMPLAZO PERMITIDO`

Por tanto:

`NO QUEDAN REEMPLAZOS DISPONIBLES`

TIMEOUT3-R2 y TIMEOUT3-R3 deben ejecutarse conforme al protocolo previsto.

Si ambas resultan evaluables, se dispondrá de:

- `T1`
- `T2`
- `T3`

y podrá calcularse:

`M_después`

Si alguna de las corridas posteriores restantes resulta no evaluable, no existe una segunda repetición de reemplazo disponible para esta condición.

La clasificación global deberá realizarse posteriormente conforme a las reglas preregistradas y al número final de corridas evaluables.

---

# 22. Diferencia entre hipótesis no respaldada y comparación principal no evaluable

Si finalmente existen:

- tres corridas evaluables de línea base;
- tres corridas evaluables posteriores;

y la reducción resulta inferior al:

`50 %`

corresponderá:

`HIPÓTESIS NO RESPALDADA`

En cambio, si al finalizar la condición posterior no se reúnen tres corridas evaluables y ya se utilizó el único reemplazo permitido, corresponderá aplicar la regla preregistrada sobre:

`COMPARACIÓN PRINCIPAL NO EVALUABLE`

Estas categorías:

`NO SON EQUIVALENTES`

Estado actual:

`NINGUNA CORRESPONDE TODAVÍA`

---

# 23. Evidencia privada

Los Logcat completos permanecen fuera del repositorio público.

## 23.1 BASE-R1

Archivo:

`evidencia-timeout/BASE-R1/logcat-BASE-R1-privado.txt`

SHA-256:

`DE5066FCF6BA7B86E1BD12FCA3041AD77513C38499E25578EB92DD27DA6E1AE5`

---

## 23.2 BASE-R2

Archivo:

`evidencia-timeout/BASE-R2/logcat-BASE-R2-privado.txt`

SHA-256:

`2BF5FDDEA23BF2D4644A616BF46DE9A1B9172EA00091E30348FA5CB571F7F907`

---

## 23.3 BASE-R3

Archivo:

`evidencia-timeout/BASE-R3/logcat-BASE-R3-privado.txt`

SHA-256:

`6508512032222FCF374F0C8998018DB01E8D466734F0B2516F57128B1894D5FF`

---

## 23.4 TIMEOUT3-R1

SHA-256:

`787BB5935347EABAD2C281765E715C5F247BEA3FB626DEF21768273BCCC9AC81`

Estado:

`EVIDENCIA PRIVADA CONSERVADA`

---

## 23.5 TIMEOUT3-R1-REP1

Archivo:

`evidencia-timeout/TIMEOUT3-R1-REP1/logcat-TIMEOUT3-R1-REP1-privado.txt`

SHA-256:

`34A67A1FA46927A4C65EA3CE8C40923A87BE2F8A75EB510B6A0088142C371EC2`

Estado:

`EVIDENCIA PRIVADA CONSERVADA`

Los archivos privados no deberán incorporarse al repositorio público sin:

- revisión;
- saneamiento;
- eliminación de JWT;
- eliminación de credenciales;
- eliminación de encabezados sensibles;
- eliminación de tokens;
- eliminación de secretos;
- eliminación de información innecesaria.

---

# 24. Verificación histórica del prerregistro

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

La comprobación histórica permanece:

`CERRADA`

No es necesario repetirla para continuar con R2 y R3.

---

# 25. Trazabilidad Git

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

Commit comprobado posteriormente como activo para REP1:

`11c4092`

Momento de la comprobación de `11c4092`:

`POSTERIOR A LA EJECUCIÓN`

Commit documental que registre la clasificación de REP1:

`PENDIENTE`

Dicho commit deberá ser:

`POSTERIOR A 11c4092`

Secuencia consolidada:

`prerregistro -> línea base -> cierre -> intervención -> TIMEOUT3-R1 -> clasificación NO EVALUABLE -> verificación histórica -> revisión documental -> TIMEOUT3-R1-REP1 -> clasificación EVALUABLE -> registro documental pendiente`

No se reescriben los commits ya publicados.

La actualización de este documento deberá registrarse mediante:

`UN NUEVO COMMIT`

---

# 26. Estado consolidado de la Actividad 4

## 26.1 Línea base

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

## 26.2 Intervención

Configuración:

`connectTimeout = 3 segundos`

Commit:

`a722954`

Estado:

`IMPLEMENTADA`

---

## 26.3 TIMEOUT3-R1

Estado:

`NO EVALUABLE`

Observaciones técnicas:

- `3 012 ms`
- `3 015 ms`

Valor para la comparación:

`NO ASIGNADO`

Motivo:

`PROTOCOLO NO CUMPLIDO SUFICIENTEMENTE`

Criterio:

`MÁS DE UNA PULSACIÓN DE GUARDAR`

SHA-256:

`787BB5935347EABAD2C281765E715C5F247BEA3FB626DEF21768273BCCC9AC81`

---

## 26.4 TIMEOUT3-R1-REP1

Estado:

`EVALUABLE`

Fecha:

`2026-10-19`

Identificador:

`S11-A4-TIMEOUT3-R1-REP1`

Control:

`HTTP 201`

Fila de control:

`1500184`

Puerto `8080`:

`SIN LISTENER`

Hora:

`12:29:36.332`

Inicio POST:

`12:30:11.583`

Fin:

`12:30:14.607`

Hilo:

`29183`

Duración:

`3 024 ms`

Excepción:

`SocketTimeoutException`

Android:

`No fue posible crear la actividad`

Falso éxito:

`NO OBSERVADO`

PostgreSQL:

`0 COINCIDENCIAS`

Guardar:

`UNA PULSACIÓN SEGÚN CONFIRMACIÓN OPERATIVA`

Identificador:

`CONFIRMADO OPERATIVAMENTE`

Commit:

`11c4092 COMPROBADO POSTERIORMENTE`

SHA-256:

`34A67A1FA46927A4C65EA3CE8C40923A87BE2F8A75EB510B6A0088142C371EC2`

Valor admitido:

`T1 = 3 024 ms`

---

## 26.5 Reemplazos

Máximo permitido:

`1`

Utilizados:

`1`

Disponibles:

`0`

---

## 26.6 TIMEOUT3-R2

Estado:

`NO EJECUTADA`

Valor:

`T2 = PENDIENTE`

---

## 26.7 TIMEOUT3-R3

Estado:

`NO EJECUTADA`

Valor:

`T3 = PENDIENTE`

---

## 26.8 Estado cuantitativo posterior

T1:

`3 024 ms`

T2:

`PENDIENTE`

T3:

`PENDIENTE`

Corridas evaluables posteriores:

`1 de 3`

M_después:

`NO CALCULADA`

Reducción:

`NO CALCULADA`

Hipótesis:

`NO EVALUADA`

Comparación principal:

`ABIERTA`

---

# 27. Próximo punto de control

TIMEOUT3-R1-REP1:

`CERRADA METODOLÓGICAMENTE COMO EVALUABLE`

Valor:

`T1 = 3 024 ms`

No es necesario repetir:

- la línea base;
- TIMEOUT3-R1;
- TIMEOUT3-R1-REP1;
- la comprobación histórica del prerregistro.

El reemplazo permitido:

`YA FUE UTILIZADO`

No se ejecutará un segundo reemplazo en la condición posterior.

---

## 27.1 Registro documental en Git

Antes de iniciar TIMEOUT3-R2, esta actualización debe quedar registrada en un nuevo commit.

Archivo a preparar:

`experimentos/semana11-crecimiento-post-activities/resultados-spike-timeout.md`

El commit documental deberá ser posterior a:

`11c4092`

y deberá registrar:

- ejecución de REP1;
- evidencia disponible;
- fuentes de evidencia diferenciadas;
- limitaciones;
- clasificación `EVALUABLE`;
- asignación `T1 = 3 024 ms`;
- consumo del único reemplazo permitido.

El prerregistro:

`NO SE MODIFICA`

Los Logcat privados:

`NO SE VERSIONAN`

---

## 27.2 Comandos de registro

Después de reemplazar el contenido del documento:

```powershell
$ruta = "experimentos/semana11-crecimiento-post-activities/resultados-spike-timeout.md"

git add -- $ruta
git diff --cached --name-only
git diff --cached --check
```

Debe aparecer únicamente:

`experimentos/semana11-crecimiento-post-activities/resultados-spike-timeout.md`

Si no aparecen errores:

```powershell
git commit -m "S11 A4: registrar resultado y evaluabilidad de REP1"
git push origin exp/semana11-timeout3-intervencion
```

No se incluirán:

- el prerregistro local modificado;
- carpetas completas de evidencia;
- Logcat privados;
- credenciales;
- tokens;
- archivos ajenos a este registro documental.

---

## 27.3 Siguiente corrida

Después de registrar REP1 corresponde:

`TIMEOUT3-R2`

Identificador:

`S11-A4-TIMEOUT3-R2`

Debe utilizarse el mismo protocolo definido previamente.

Antes de la medición se deberá comprobar:

1. Spring Boot disponible;
2. PostgreSQL disponible;
3. comunicación Android-backend funcional;
4. usuario autenticado;
5. aplicación Android abierta;
6. captura de Logcat activa;
7. identificador correcto;
8. condición experimental correspondiente.

Después:

1. realizar control previo si corresponde;
2. finalizar completamente el control;
3. detener Spring Boot;
4. comprobar indisponibilidad del puerto `8080`;
5. conservar la hora de comprobación;
6. comprobar nuevamente el identificador;
7. pulsar `Guardar` exactamente una vez;
8. no realizar otro POST voluntario;
9. esperar el resultado;
10. conservar `--> POST`;
11. conservar `HTTP FAILED`, si aparece;
12. calcular la duración;
13. registrar el comportamiento visible en Android;
14. comprobar PostgreSQL;
15. registrar coincidencias;
16. registrar cualquier desviación;
17. conservar la evidencia privada;
18. calcular SHA-256;
19. clasificar la corrida antes de utilizar `T2`.

---

## 27.4 Restricción de reemplazos

La condición posterior ya utilizó:

`1 DE 1 REEMPLAZO`

Por tanto, no existe una repetición de reemplazo adicional disponible para la condición posterior.

Si TIMEOUT3-R2 o TIMEOUT3-R3 presentaran una desviación que obligara a clasificarlas como no evaluables, dicha situación deberá conservarse y tratarse según las reglas globales del prerregistro.

---

## 27.5 Estado hasta ejecutar R2 y R3

Hasta completar TIMEOUT3-R2 y TIMEOUT3-R3:

`T1 = 3 024 ms`

`T2 = PENDIENTE`

`T3 = PENDIENTE`

`M_después = NO CALCULADA`

`REDUCCIÓN = NO CALCULADA`

`HIPÓTESIS = NO EVALUADA`

`COMPARACIÓN PRINCIPAL = ABIERTA`