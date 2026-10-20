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
- el criterio cuantitativo del `50 %`;

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

TIMEOUT3-R1-REP1:

`EJECUTADA — EVALUABLE`

Valor admitido:

`T1 = 3 024 ms`

TIMEOUT3-R2:

`EJECUTADA — EVALUABLE CON INCIDENCIA DOCUMENTADA`

Valor admitido:

`T2 = 3 011 ms`

Incidencia principal de R2:

`EXISTENCIA DE UNA FILA HISTÓRICA ELIMINADA LÓGICAMENTE CON EL MISMO IDENTIFICADOR EXPERIMENTAL`

Commit documental de R2:

`40eb3cf`

TIMEOUT3-R3:

`EJECUTADA — EVALUABLE`

Valor admitido:

`T3 = 3 014 ms`

Corridas posteriores evaluables consolidadas:

`3 de 3`

Reemplazos utilizados en la condición posterior:

`1 de 1 permitido`

Reemplazos disponibles:

`0`

Mediana posterior:

`M_después = 3 014 ms`

Reducción porcentual:

`69,94 %`

Umbral preregistrado:

`50 %`

Criterio cuantitativo:

`CUMPLIDO`

Hipótesis:

`RESPALDADA`

Comparación principal:

`EVALUABLE Y COMPLETADA`

Conclusión arquitectónica:

`LA CONFIGURACIÓN EXPLÍCITA DE connectTimeout = 3 SEGUNDOS REDUJO SUSTANCIALMENTE EL TIEMPO DE FALLO OBSERVADO BAJO LAS CONDICIONES DEL EXPERIMENTO`

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

Estado final:

- `TIMEOUT3-R1 = EJECUTADA — NO EVALUABLE`
- `TIMEOUT3-R1-REP1 = EJECUTADA — EVALUABLE`
- `TIMEOUT3-R2 = EJECUTADA — EVALUABLE CON INCIDENCIA DOCUMENTADA`
- `TIMEOUT3-R3 = EJECUTADA — EVALUABLE`

Valores evaluables consolidados:

`3 de 3`

Valores admitidos:

- `T1 = 3 024 ms`
- `T2 = 3 011 ms`
- `T3 = 3 014 ms`

Reemplazos utilizados:

`1 de 1 permitido`

Reemplazos disponibles:

`0`

Estado:

`CONDICIÓN POSTERIOR COMPLETADA`

---

# 8. TIMEOUT3-R1

Estado:

`EJECUTADA`

Identificador definido:

`S11-A4-TIMEOUT3-R1`

Clasificación:

`NO EVALUABLE`

Observaciones técnicas:

- `3 012 ms`
- `3 015 ms`

Valor para comparación:

`NO ASIGNADO`

---

## 8.1 Comprobación de indisponibilidad

Puerto:

`8080`

Listeners:

`0`

Hora:

`20:32:15.603`

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

## 8.4 Incidencia

Según confirmación operativa retrospectiva se realizaron dos intentos de creación.

Se utilizaron:

1. `S11-A4-TIMEOUT-R1`
2. `S11-A4-TIMEOUT3-R1`

El prerregistro exigía:

`PULSAR GUARDAR EXACTAMENTE UNA VEZ`

Por tanto, la corrida fue clasificada:

`NO EVALUABLE`

---

## 8.5 PATCH separado

La operación registrada a:

`20:32:25.078`

correspondía a:

`PATCH`

No corresponde a un tercer POST de creación.

---

## 8.6 Android

Resultado:

`No fue posible crear la actividad`

Falso éxito:

`NO OBSERVADO`

---

## 8.7 PostgreSQL

Consulta:

`S11-A4-TIMEOUT3-R1`

Resultado:

`0 FILAS`

---

## 8.8 Integridad

SHA-256:

`787BB5935347EABAD2C281765E715C5F247BEA3FB626DEF21768273BCCC9AC81`

---

# 9. Decisión metodológica TIMEOUT3-R1

Clasificación:

`NO EVALUABLE`

Motivo:

`EL PROTOCOLO NO PUDO CUMPLIRSE SUFICIENTEMENTE`

Criterio aplicado:

`MÁS DE UNA PULSACIÓN DE GUARDAR DURANTE LA MISMA CORRIDA`

Las mediciones:

- `3 012 ms`
- `3 015 ms`

permanecen como observaciones descriptivas.

No participan en la comparación principal.

TIMEOUT3-R1 no cuenta como respaldo ni como no respaldo de la hipótesis.

---

# 10. Regla de reemplazo

Máximo permitido:

`1 REPETICIÓN DE REEMPLAZO POR CONDICIÓN`

TIMEOUT3-R1 cumplió la condición para utilizar la repetición.

El reemplazo:

`TIMEOUT3-R1-REP1`

se conserva independientemente de la corrida original.

La corrida original permanece:

`NO EVALUABLE`

y no se elimina del registro histórico.

---

# 11. TIMEOUT3-R1-REP1

Estado:

`EJECUTADA — EVALUABLE`

Valor:

`T1 = 3 024 ms`

Fecha:

`2026-10-19`

Identificador:

`S11-A4-TIMEOUT3-R1-REP1`

---

## 11.1 Control previo

Resultado:

`HTTP 201`

Fila de control:

`1500184`

---

## 11.2 Indisponibilidad

Puerto:

`8080`

Resultado:

`SIN LISTENER`

Hora:

`12:29:36.332`

---

## 11.3 POST experimental

Inicio:

`12:30:11.583`

Fin:

`12:30:14.607`

Hilo:

`29183`

Resultado:

`SocketTimeoutException`

Duración:

`3 024 ms`

---

## 11.4 Android

Resultado:

`No fue posible crear la actividad`

Falso éxito:

`NO OBSERVADO`

---

## 11.5 PostgreSQL

Consulta:

`S11-A4-TIMEOUT3-R1-REP1`

Resultado:

`0 COINCIDENCIAS`

---

## 11.6 Guardar

Pulsaciones:

`1`

Fuente:

`CONFIRMACIÓN OPERATIVA`

---

## 11.7 Commit asociado

Commit comprobado posteriormente:

`11c4092`

La comprobación ocurrió después de la ejecución.

No se presenta como una captura Git realizada durante la ventana experimental.

---

## 11.8 Limitación de configuración

No se registró durante la ventana exacta una captura instrumental completa de toda la configuración HTTP.

La condición experimental prevista era:

`connectTimeout = 3 segundos`

La limitación permanece documentada.

---

## 11.9 Integridad

Archivo:

`evidencia-timeout/TIMEOUT3-R1-REP1/logcat-TIMEOUT3-R1-REP1-privado.txt`

SHA-256:

`34A67A1FA46927A4C65EA3CE8C40923A87BE2F8A75EB510B6A0088142C371EC2`

---

# 12. Decisión metodológica TIMEOUT3-R1-REP1

Clasificación:

`EVALUABLE`

Valor admitido:

`T1 = 3 024 ms`

La corrida dispone de:

- control previo exitoso;
- indisponibilidad comprobada;
- POST correlacionable;
- duración calculable;
- una pulsación según confirmación operativa;
- error visible en Android;
- ausencia de falso éxito;
- comprobación PostgreSQL;
- evidencia privada conservada.

Las limitaciones permanecen registradas sin impedir reconstruir suficientemente la operación.

---

# 13. Estado de reemplazos

Máximo:

`1`

Utilizados:

`1`

Disponibles:

`0`

Reemplazo utilizado:

`TIMEOUT3-R1-REP1`

Resultado:

`EVALUABLE`

Valor:

`T1 = 3 024 ms`

---

# 14. TIMEOUT3-R2

Estado:

`EJECUTADA — EVALUABLE CON INCIDENCIA DOCUMENTADA`

Identificador:

`S11-A4-TIMEOUT3-R2`

Valor admitido:

`T2 = 3 011 ms`

Commit documental:

`40eb3cf`

---

## 14.1 Antecedentes anteriores a la ventana experimental

Antes de la ventana experimental de R2 se realizaron accidentalmente dos creaciones con nombres similares mientras el backend se encontraba disponible.

Una utilizó:

`S11-A4-TIMEOUT3-R2`

La otra utilizó un identificador con sufijo:

`-CONTROL`

Estas operaciones ocurrieron:

`ANTES DE LA VENTANA EXPERIMENTAL`

---

## 14.2 Fila histórica

PostgreSQL contenía previamente:

`ID 1500185`

Identificador:

`S11-A4-TIMEOUT3-R2`

Estado:

`is_deleted = true`

La fila había sido eliminada lógicamente antes de la ventana experimental.

---

## 14.3 Diferencia respecto de REP1

La preparación de R2 no fue idéntica a REP1.

En R2 existió:

`UNA ELIMINACIÓN LÓGICA PREVIA`

con el mismo identificador experimental.

Esta diferencia permanece registrada.

---

## 14.4 Ventana experimental

Durante la ventana utilizada para medir R2 se observó:

`UN ÚNICO POST EXPERIMENTAL`

Se confirmó:

`UNA SOLA PULSACIÓN DE GUARDAR`

---

## 14.5 Comprobación de indisponibilidad

Puerto:

`8080`

Hora:

`13:17:16.961`

Resultado:

`SIN LISTENER`

La comprobación ocurrió antes del POST experimental.

---

## 14.6 POST experimental

Inicio:

`13:18:00.033`

Fin:

`13:18:03.044`

Hilo:

`30386`

Resultado:

`SocketTimeoutException`

Duración:

`3 011 ms`

---

## 14.7 Android

Resultado:

`No fue posible crear la actividad`

Falso éxito:

`NO OBSERVADO`

---

## 14.8 Guardar

Pulsaciones:

`1`

Fuente:

`CONFIRMACIÓN OPERATIVA`

---

## 14.9 PostgreSQL antes/después

### Antes

ID:

`1500185`

Estado:

`is_deleted = true`

### Después

ID:

`1500185`

Estado:

`is_deleted = true`

Filas nuevas observadas:

`0`

Por tanto:

`NO SE OBSERVÓ NUEVA PERSISTENCIA REMOTA ASOCIADA AL POST EXPERIMENTAL`

No se utiliza la expresión:

`0 coincidencias`

porque existía una fila histórica.

---

## 14.10 Integridad

Archivo:

`evidencia-timeout/TIMEOUT3-R2/logcat-TIMEOUT3-R2-privado.txt`

SHA-256:

`EEF1B3083BA7470C350C9C6E935295F06AA0FB275FF9D02A17331CA33AB16FB0`

---

# 15. Decisión metodológica TIMEOUT3-R2

Incidencia:

`USO PREVIO DEL IDENTIFICADOR EXPERIMENTAL`

La incidencia no impidió reconstruir suficientemente el intento porque:

- la indisponibilidad fue comprobada antes del POST;
- el POST y el fallo son correlacionables;
- la duración es calculable;
- hubo una pulsación según confirmación operativa;
- Android mostró error;
- no se observó falso éxito;
- se conocía el estado de PostgreSQL antes;
- después permaneció la misma fila histórica;
- no se observaron filas nuevas;
- la evidencia privada quedó conservada.

Clasificación:

`EVALUABLE CON INCIDENCIA DOCUMENTADA`

Valor:

`T2 = 3 011 ms`

La clasificación no elimina ni minimiza la incidencia.

---

# 16. TIMEOUT3-R3

Estado:

`EJECUTADA — EVALUABLE`

Identificador definido para la corrida:

`S11-A4-TIMEOUT3-R3`

Valor admitido:

`T3 = 3 014 ms`

---

## 16.1 Estado de PostgreSQL antes del intento

Antes de ejecutar la operación experimental se comprobó PostgreSQL utilizando:

`S11-A4-TIMEOUT3-R3`

Resultado:

`0 FILAS`

---

## 16.2 Comprobación de indisponibilidad

Puerto:

`8080`

Resultado:

`SIN LISTENER`

Hora:

`13:41:49.173`

La comprobación ocurrió antes del POST experimental iniciado a:

`13:42:32.930`

---

## 16.3 POST experimental

Inicio:

`13:42:32.930`

Línea del Logcat privado:

`40853`

Hilo OkHttp:

`30948`

Resultado:

`SocketTimeoutException`

Fin:

`13:42:35.944`

Línea del Logcat privado:

`40854`

Las dos marcas corresponden al mismo hilo OkHttp.

---

## 16.4 Correlación HTTP

Inicio:

`13:42:32.930`

Fin:

`13:42:35.944`

Hilo:

`30948`

Excepción:

`SocketTimeoutException`

La correspondencia entre inicio y fallo permite correlacionar suficientemente la solicitud observada.

---

## 16.5 Duración técnica

Cálculo:

`35.944 - 32.930 = 3.014 segundos`

Duración:

`3 014 ms`

Valor:

`T3 = 3 014 ms`

---

## 16.6 Excepción

Tipo:

`SocketTimeoutException`

---

## 16.7 Resultado visible en Android

Android mostró:

`No fue posible crear la actividad`

Falso éxito observado:

`NO`

---

## 16.8 Pulsaciones de Guardar

Número:

`1`

Fuente:

`CONFIRMACIÓN OPERATIVA`

La pulsación única no se presenta como información derivada directamente de Logcat BASIC.

---

## 16.9 PostgreSQL después del intento

Consulta:

`S11-A4-TIMEOUT3-R3`

Resultado:

`0 FILAS`

Comparación:

### Antes

`0 FILAS`

### Después

`0 FILAS`

Por tanto:

`NO SE OBSERVÓ PERSISTENCIA REMOTA BAJO EL IDENTIFICADOR CONSULTADO`

---

## 16.10 Identificador y fuentes de trazabilidad

El identificador definido previamente para la corrida R3 es:

`S11-A4-TIMEOUT3-R3`

### Identificador previsto

La versión histórica del prerregistro establece:

`S11-A4-TIMEOUT3-R3`

Por tanto:

`IDENTIFICADOR PREVISTO POR EL PRERREGISTRO = S11-A4-TIMEOUT3-R3`

### Identificador utilizado en PostgreSQL

Las comprobaciones antes y después se realizaron consultando:

`S11-A4-TIMEOUT3-R3`

Por tanto:

`IDENTIFICADOR CONSULTADO EN POSTGRESQL = S11-A4-TIMEOUT3-R3`

### Contenido funcional del POST

El nivel:

`HttpLoggingInterceptor.Level.BASIC`

no expone el body completo.

Por tanto, Logcat BASIC no demuestra por sí solo que el título contenido dentro del POST fuera:

`S11-A4-TIMEOUT3-R3`

No se transforma:

- el identificador previsto por el prerregistro;
- ni el identificador utilizado en PostgreSQL;

en una observación instrumental del contenido del body.

La correlación técnica del POST se sustenta independientemente mediante:

- timestamp de inicio;
- timestamp de fallo;
- hilo OkHttp;
- operación HTTP correspondiente;
- resultado `SocketTimeoutException`.

---

## 16.11 Integridad

Archivo privado:

`evidencia-timeout/TIMEOUT3-R3/logcat-TIMEOUT3-R3-privado.txt`

SHA-256:

`572453C199416CA22FF7A57E4FCE75A61837F7B8AC06C5F611070BF4B7325884`

Las dos marcas utilizadas para el cálculo fueron localizadas en dicho archivo.

---

# 17. Decisión metodológica TIMEOUT3-R3

La corrida dispone de:

- estado PostgreSQL previo de `0 filas`;
- puerto sin listener antes del POST;
- POST correlacionable;
- mismo hilo para inicio y fallo;
- duración calculable;
- `SocketTimeoutException`;
- una sola pulsación de `Guardar` según confirmación operativa;
- error visible en Android;
- ausencia de falso éxito observada;
- PostgreSQL posterior con `0 filas`;
- evidencia privada conservada;
- SHA-256 verificado.

Se conserva la limitación de que Logcat BASIC no expone directamente el título contenido en el body.

No se identificó una desviación que impida reconstruir suficientemente la operación experimental.

Clasificación:

`EVALUABLE`

Valor:

`T3 = 3 014 ms`

---

# 18. Estado de los valores posteriores

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

`EVALUABLE`

T3:

`3 014 ms`

Corridas posteriores evaluables:

`3 de 3`

Reemplazos utilizados:

`1 de 1`

Reemplazos disponibles:

`0`

Estado:

`CONDICIÓN POSTERIOR CERRADA`

---

# 19. Mediana posterior

La condición posterior dispone de tres corridas evaluables:

- `T1 = 3 024 ms`
- `T2 = 3 011 ms`
- `T3 = 3 014 ms`

Estas corresponden a:

- `TIMEOUT3-R1-REP1 = EVALUABLE`
- `TIMEOUT3-R2 = EVALUABLE CON INCIDENCIA DOCUMENTADA`
- `TIMEOUT3-R3 = EVALUABLE`

TIMEOUT3-R1 original permanece:

`NO EVALUABLE`

y sus observaciones:

- `3 012 ms`
- `3 015 ms`

no participan en el cálculo de la mediana posterior.

---

## 19.1 Valores utilizados

Valores:

- `3 024 ms`
- `3 011 ms`
- `3 014 ms`

Valores ordenados:

- `3 011 ms`
- `3 014 ms`
- `3 024 ms`

El valor central es:

`3 014 ms`

Por tanto:

\[
M_{después} = 3\,014\text{ ms}
\]

Resultado:

`M_después = 3 014 ms`

---

## 19.2 Verificación mediante PowerShell

El cálculo fue verificado mediante:

```powershell
$valores = @(3024, 3011, 3014) | Sort-Object
$mediana = $valores[1]
$reduccion = (10028 - $mediana) / 10028 * 100

"Valores ordenados: $($valores -join ', ')"
"Mediana posterior: $mediana ms"
"Reducción: $([math]::Round($reduccion, 2)) %"
```

Salida obtenida:

```text
Valores ordenados: 3011, 3014, 3024
Mediana posterior: 3014 ms
Reducción: 69.94 %
```

La salida coincide con el cálculo realizado a partir de los tres valores admitidos.

---

## 19.3 Condición requerida por el prerregistro

La versión histórica del prerregistro:

`94dcc32`

requiere:

`3 CORRIDAS EVALUABLES DESPUÉS DE LA INTERVENCIÓN`

Estado final:

`3 de 3`

Por tanto, se cumplen las condiciones necesarias para calcular la mediana posterior según el procedimiento establecido.

Resultado consolidado:

`M_después = 3 014 ms`

---

# 20. Reducción porcentual

Mediana de línea base:

`M_antes = 10 028 ms`

Mediana posterior:

`M_después = 3 014 ms`

La fórmula definida en el prerregistro es:

\[
Reducción(\%) =
\frac{M_{antes} - M_{después}}
{M_{antes}}
\times 100
\]

Sustituyendo:

\[
Reducción(\%) =
\frac{10\,028 - 3\,014}
{10\,028}
\times 100
\]

Diferencia:

\[
10\,028 - 3\,014 = 7\,014
\]

Por tanto:

\[
Reducción(\%) =
\frac{7\,014}
{10\,028}
\times 100
\]

Resultado:

\[
Reducción(\%) \approx 69.94\%
\]

Valor consolidado:

`REDUCCIÓN = 69,94 %`

---

## 20.1 Contraste con el umbral preregistrado

Criterio definido previamente en:

`94dcc32`

Umbral:

`REDUCCIÓN >= 50 %`

Resultado:

`69,94 %`

Comparación:

\[
69.94\% \ge 50\%
\]

Resultado del criterio cuantitativo:

`CUMPLIDO`

Diferencia respecto del umbral:

\[
69.94 - 50 = 19.94
\]

La reducción observada supera el umbral en:

`19,94 puntos porcentuales`

---

## 20.2 Alcance del cálculo

### Línea base

- `B1 = 10 028 ms`
- `B2 = 10 033 ms`
- `B3 = 10 023 ms`

Mediana:

`M_antes = 10 028 ms`

### Condición posterior

- `T1 = 3 024 ms`
- `T2 = 3 011 ms`
- `T3 = 3 014 ms`

Mediana:

`M_después = 3 014 ms`

No participan:

- `3 012 ms`;
- `3 015 ms`;

observados en TIMEOUT3-R1, porque dicha corrida fue:

`NO EVALUABLE`

TIMEOUT3-R1 permanece conservada documentalmente.

---

# 21. Estado de la hipótesis

Hipótesis preregistrada:

> Ante una indisponibilidad controlada de Spring Boot, configurar explícitamente `connectTimeout = 3 segundos` reducirá al menos un 50 % la mediana de la duración técnica de los `POST` fallidos de creación de actividad respecto de una nueva línea base ejecutada bajo el mismo protocolo, sin que Android presente falsamente la operación como exitosa ni se observe persistencia remota inesperada de la actividad intentada durante el fallo.

La evaluación requiere contrastar:

1. reducción de al menos `50 %`;
2. ausencia de falso éxito visible en Android;
3. ausencia de persistencia remota inesperada durante los intentos fallidos evaluables.

---

## 21.1 Condición cuantitativa

Mediana de línea base:

`M_antes = 10 028 ms`

Mediana posterior:

`M_después = 3 014 ms`

Reducción:

`69,94 %`

Umbral:

`50 %`

Resultado:

`CUMPLE`

---

## 21.2 Resultado Android

### TIMEOUT3-R1-REP1

Android mostró:

`No fue posible crear la actividad`

Falso éxito:

`NO OBSERVADO`

### TIMEOUT3-R2

Android mostró:

`No fue posible crear la actividad`

Falso éxito:

`NO OBSERVADO`

### TIMEOUT3-R3

Android mostró:

`No fue posible crear la actividad`

Falso éxito:

`NO OBSERVADO`

Resultado consolidado:

`NO SE OBSERVÓ FALSO ÉXITO EN ANDROID EN LAS TRES CORRIDAS POSTERIORES EVALUABLES`

Estado:

`CUMPLE SEGÚN LAS OBSERVACIONES REGISTRADAS`

---

## 21.3 Persistencia remota — REP1

Consulta:

`S11-A4-TIMEOUT3-R1-REP1`

Resultado:

`0 COINCIDENCIAS`

Conclusión observacional:

`NO SE OBSERVÓ PERSISTENCIA REMOTA BAJO EL IDENTIFICADOR EXPERIMENTAL CONSULTADO`

---

## 21.4 Persistencia remota — R2

Antes existía:

`1500185 — is_deleted = true`

Después permaneció:

`1500185 — is_deleted = true`

Filas nuevas observadas:

`0`

Por tanto:

`NO SE OBSERVÓ NUEVA PERSISTENCIA REMOTA ASOCIADA AL POST EXPERIMENTAL`

Esta conclusión se basa en la comparación antes/después.

No se afirma que el identificador nunca hubiera sido utilizado.

La preparación de R2 no fue idéntica a REP1.

---

## 21.5 Persistencia remota — R3

Antes:

`0 FILAS`

Después:

`0 FILAS`

Por tanto:

`NO SE OBSERVÓ PERSISTENCIA REMOTA BAJO EL IDENTIFICADOR CONSULTADO`

---

## 21.6 Resultado consolidado de persistencia

REP1:

`0 coincidencias`

R2:

`0 filas nuevas observadas`

R3:

`0 filas antes / 0 filas después`

Resultado consolidado:

`NO SE OBSERVÓ PERSISTENCIA REMOTA INESPERADA ATRIBUIBLE A LOS POST EXPERIMENTALES EVALUABLES`

Esta conclusión queda limitada a las consultas realizadas y evidencia conservada.

---

## 21.7 Limitaciones que afectan el alcance

### TIMEOUT3-R1

La corrida original fue:

`NO EVALUABLE`

debido a más de una pulsación de `Guardar`.

Sus valores no participan en la comparación.

### TIMEOUT3-R1-REP1

Se conservan:

- identificador confirmado operativamente;
- una pulsación confirmada operativamente;
- commit `11c4092` comprobado posteriormente;
- ausencia de captura instrumental contemporánea de toda la configuración HTTP.

### TIMEOUT3-R2

Se conserva:

`USO PREVIO DEL IDENTIFICADOR EXPERIMENTAL`

y:

`1500185 — is_deleted = true`

La ausencia de nueva persistencia se determina mediante comparación antes/después.

La preparación no fue idéntica a REP1.

### TIMEOUT3-R3

Se mantiene la distinción entre:

- identificador previsto por el prerregistro;
- identificador consultado en PostgreSQL;
- contenido del body no visible mediante Logcat BASIC.

La pulsación única corresponde a:

`CONFIRMACIÓN OPERATIVA`

---

## 21.8 Contraste final

### Condición 1 — reducción >= 50 %

Resultado:

`69,94 %`

Estado:

`CUMPLE`

### Condición 2 — ausencia de falso éxito Android

Resultado:

`NO SE OBSERVÓ FALSO ÉXITO EN LAS TRES CORRIDAS POSTERIORES EVALUABLES`

Estado:

`CUMPLE SEGÚN LA EVIDENCIA REGISTRADA`

### Condición 3 — ausencia de persistencia remota inesperada

Resultado:

`NO SE OBSERVÓ NUEVA PERSISTENCIA REMOTA ATRIBUIBLE A LOS POST EXPERIMENTALES EVALUABLES`

Estado:

`CUMPLE SEGÚN LAS CONSULTAS REGISTRADAS`

---

## 21.9 Decisión final

Los resultados observados muestran que:

- la mediana pasó de `10 028 ms` a `3 014 ms`;
- la reducción fue de `69,94 %`;
- el umbral preregistrado era `50 %`;
- no se observó falso éxito Android en las tres corridas posteriores evaluables;
- no se observó persistencia remota inesperada atribuible a los POST experimentales evaluables.

Por tanto, dentro del alcance y las limitaciones documentadas:

`HIPÓTESIS RESPALDADA`

Conclusión:

`LA HIPÓTESIS PRERREGISTRADA QUEDA RESPALDADA POR LA EVIDENCIA OBTENIDA EN ESTE EXPERIMENTO`

Esta conclusión no elimina:

- las incidencias;
- las limitaciones;
- TIMEOUT3-R1 no evaluable;
- las diferencias de preparación de R2;
- las limitaciones de Logcat BASIC.

La conclusión se restringe al protocolo, entorno, aplicación, condiciones y evidencia registrados.

---

# 22. Comparación principal

Línea base:

`3 de 3 CORRIDAS EVALUABLES`

Condición posterior:

`3 de 3 CORRIDAS EVALUABLES`

Reemplazo utilizado:

`1 de 1`

Mediana de línea base:

`10 028 ms`

Mediana posterior:

`3 014 ms`

Reducción:

`69,94 %`

Umbral:

`50 %`

Estado:

`COMPARACIÓN PRINCIPAL EVALUABLE Y COMPLETADA`

La categoría:

`COMPARACIÓN PRINCIPAL NO EVALUABLE`

no corresponde al resultado obtenido porque se consiguieron las tres corridas evaluables requeridas en ambas condiciones.

---

# 23. Diferencia entre hipótesis no respaldada y comparación principal no evaluable

La regla metodológica distingue:

### Comparación válida con reducción inferior al 50 %

Correspondería:

`HIPÓTESIS NO RESPALDADA`

### Falta de tres corridas evaluables después de aplicar las reglas permitidas

Correspondería:

`COMPARACIÓN PRINCIPAL NO EVALUABLE`

Ninguno de esos escenarios corresponde al resultado final.

Se obtuvieron:

`3 CORRIDAS EVALUABLES ANTES`

y:

`3 CORRIDAS EVALUABLES DESPUÉS`

Además:

`69,94 % >= 50 %`

Por tanto:

`HIPÓTESIS RESPALDADA`

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

---

## 24.6 TIMEOUT3-R2

Archivo:

`evidencia-timeout/TIMEOUT3-R2/logcat-TIMEOUT3-R2-privado.txt`

SHA-256:

`EEF1B3083BA7470C350C9C6E935295F06AA0FB275FF9D02A17331CA33AB16FB0`

---

## 24.7 TIMEOUT3-R3

Archivo:

`evidencia-timeout/TIMEOUT3-R3/logcat-TIMEOUT3-R3-privado.txt`

SHA-256:

`572453C199416CA22FF7A57E4FCE75A61837F7B8AC06C5F611070BF4B7325884`

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
- tres corridas evaluables como condición para calcular la mediana posterior;
- umbral cuantitativo del `50 %`.

Estado:

`CERRADA`

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

Commit de revisión documental:

`964ef25`

Commit comprobado posteriormente para REP1:

`11c4092`

Commit documental de REP1:

`bf8fcbc`

Commit documental de TIMEOUT3-R2:

`40eb3cf`

Commit documental de TIMEOUT3-R3:

`84c7e3c`

Commit de consolidación cuantitativa:

`PENDIENTE`

El commit de consolidación deberá ser:

`POSTERIOR A 84c7e3c`

No se asigna un hash hasta que el commit exista realmente.

Secuencia consolidada:

`94dcc32 prerregistro`

`-> línea base`

`-> b6f82a5 cierre de línea base`

`-> be2b2f3 merge de línea base`

`-> a722954 intervención`

`-> TIMEOUT3-R1`

`-> cc68828 registro de R1`

`-> R1 NO EVALUABLE`

`-> verificación histórica`

`-> 964ef25 revisión documental`

`-> TIMEOUT3-R1-REP1`

`-> 11c4092 comprobado posteriormente como commit activo`

`-> REP1 EVALUABLE`

`-> bf8fcbc registro documental de REP1`

`-> TIMEOUT3-R2`

`-> R2 EVALUABLE CON INCIDENCIA DOCUMENTADA`

`-> 40eb3cf registro documental de R2`

`-> TIMEOUT3-R3`

`-> R3 EVALUABLE`

`-> 84c7e3c registro documental de R3`

`-> cálculo de M_después = 3 014 ms`

`-> cálculo de reducción = 69,94 %`

`-> HIPÓTESIS RESPALDADA`

`-> commit de consolidación cuantitativa PENDIENTE`

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

Valor principal:

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

Commit documental:

`bf8fcbc`

---

## 27.5 TIMEOUT3-R2

Estado:

`EVALUABLE CON INCIDENCIA DOCUMENTADA`

Valor:

`T2 = 3 011 ms`

Incidencia:

`FILA HISTÓRICA 1500185 — is_deleted = true`

Puerto sin listener:

`13:17:16.961`

Inicio:

`13:18:00.033`

Fin:

`13:18:03.044`

Hilo:

`30386`

PostgreSQL:

`0 FILAS NUEVAS OBSERVADAS`

SHA-256:

`EEF1B3083BA7470C350C9C6E935295F06AA0FB275FF9D02A17331CA33AB16FB0`

Commit documental:

`40eb3cf`

---

## 27.6 TIMEOUT3-R3

Estado:

`EVALUABLE`

Valor:

`T3 = 3 014 ms`

Identificador previsto:

`S11-A4-TIMEOUT3-R3`

Identificador consultado en PostgreSQL:

`S11-A4-TIMEOUT3-R3`

Título del body demostrado directamente por Logcat BASIC:

`NO`

Puerto sin listener:

`13:41:49.173`

Inicio:

`13:42:32.930`

Fin:

`13:42:35.944`

Hilo:

`30948`

Excepción:

`SocketTimeoutException`

Android:

`No fue posible crear la actividad`

Falso éxito:

`NO OBSERVADO`

Guardar:

`1 SEGÚN CONFIRMACIÓN OPERATIVA`

PostgreSQL antes:

`0 FILAS`

PostgreSQL después:

`0 FILAS`

SHA-256:

`572453C199416CA22FF7A57E4FCE75A61837F7B8AC06C5F611070BF4B7325884`

Commit documental:

`84c7e3c`

---

## 27.7 Comparación cuantitativa

Valores de línea base:

- `10 028 ms`
- `10 033 ms`
- `10 023 ms`

Mediana:

`M_antes = 10 028 ms`

Valores posteriores:

- `3 024 ms`
- `3 011 ms`
- `3 014 ms`

Valores posteriores ordenados:

- `3 011 ms`
- `3 014 ms`
- `3 024 ms`

Mediana:

`M_después = 3 014 ms`

Reducción:

`69,94 %`

Umbral:

`50 %`

Resultado cuantitativo:

`CUMPLE`

---

## 27.8 Condiciones funcionales

Falso éxito Android en las tres corridas posteriores evaluables:

`NO OBSERVADO`

Persistencia remota inesperada atribuible a los POST experimentales evaluables:

`NO OBSERVADA`

La conclusión sobre persistencia conserva las particularidades documentadas de R2.

---

## 27.9 Resultado final

Comparación principal:

`EVALUABLE Y COMPLETADA`

Hipótesis:

`RESPALDADA`

Conclusión:

`LA HIPÓTESIS PRERREGISTRADA QUEDA RESPALDADA POR LA EVIDENCIA OBTENIDA EN ESTE EXPERIMENTO, DENTRO DE LAS CONDICIONES Y LIMITACIONES DOCUMENTADAS`

---

# 28. Cierre del experimento

La Actividad 4 dispone de:

- prerregistro histórico verificable;
- línea base completa;
- intervención registrada;
- tres valores evaluables de línea base;
- tres valores evaluables posteriores;
- una corrida no evaluable conservada;
- un reemplazo utilizado conforme a la regla preregistrada;
- evidencia privada con hashes SHA-256;
- mediana antes;
- mediana después;
- reducción porcentual;
- contraste contra el umbral;
- comprobaciones funcionales de Android;
- comprobaciones de persistencia;
- limitaciones expresamente documentadas.

---

## 28.1 Resultado cuantitativo final

```text
M_antes    = 10 028 ms
M_después  = 3 014 ms
Reducción  = 69,94 %
Umbral     = 50 %
```

Comparación:

```text
69,94 % >= 50 %
```

Resultado:

`CRITERIO CUANTITATIVO CUMPLIDO`

---

## 28.2 Resultado funcional final

Android:

`NO SE OBSERVÓ FALSO ÉXITO EN LAS TRES CORRIDAS POSTERIORES EVALUABLES`

Persistencia:

`NO SE OBSERVÓ NUEVA PERSISTENCIA REMOTA INESPERADA ATRIBUIBLE A LOS POST EXPERIMENTALES EVALUABLES`

---

## 28.3 Resultado metodológico final

Hipótesis:

`RESPALDADA`

Comparación principal:

`EVALUABLE`

Estado experimental:

`COMPLETADO`

La categoría:

`COMPARACIÓN PRINCIPAL NO EVALUABLE`

no corresponde.

La categoría:

`HIPÓTESIS NO RESPALDADA`

tampoco corresponde.

---

## 28.4 Alcance arquitectónico

Bajo las condiciones registradas en este experimento, la configuración explícita:

`connectTimeout = 3 segundos`

estuvo asociada con una reducción de la mediana técnica de los POST fallidos desde:

`10 028 ms`

hasta:

`3 014 ms`

equivalente a:

`69,94 %`

La evidencia observada también mostró que Android continuó presentando el error al usuario y no se observó nueva persistencia remota inesperada atribuible a las solicitudes fallidas evaluables.

Por tanto, dentro del alcance del experimento, la intervención proporciona evidencia favorable para mantener explícitamente:

`connectTimeout = 3 segundos`

como decisión de configuración para este escenario de indisponibilidad controlada.

Esta conclusión no se generaliza automáticamente a:

- otras redes;
- otros dispositivos;
- otros endpoints;
- otros tipos de fallo;
- indisponibilidad de PostgreSQL;
- fallos parciales del backend;
- timeouts de lectura;
- timeouts de escritura;
- timeouts globales de llamada;
- escenarios con retry;
- escenarios con fallback;
- escenarios con circuit breaker.

---

## 28.5 Limitaciones finales conservadas

### BASE-R1

Reutilización del identificador entre control y experimento.

### BASE-R2

Incidencia previa de autenticación y ausencia de una captura fechada exacta de la comprobación del puerto.

### BASE-R3

Dos filas de control con procedencia exacta no determinada.

### TIMEOUT3-R1

Dos pulsaciones de `Guardar`; corrida no evaluable.

### TIMEOUT3-R1-REP1

Confirmaciones operativas para identificador y pulsación; commit comprobado posteriormente; ausencia de captura instrumental completa de configuración durante la ventana exacta.

### TIMEOUT3-R2

Uso previo del identificador y existencia de la fila histórica:

`1500185 — is_deleted = true`

La preparación no fue idéntica a REP1.

### TIMEOUT3-R3

Logcat BASIC no expone directamente el título funcional contenido en el body.

Estas limitaciones:

`NO SE ELIMINAN DEL REGISTRO`

y delimitan el alcance de la conclusión final.

---

## 28.6 Próximo paso documental

El último commit documental previo a esta consolidación es:

`84c7e3c`

La actualización cuantitativa final deberá registrarse mediante:

`UN NUEVO COMMIT POSTERIOR A 84c7e3c`

Archivo:

`experimentos/semana11-crecimiento-post-activities/resultados-spike-timeout.md`

Antes del commit deberá ejecutarse:

```powershell
$ruta = "experimentos/semana11-crecimiento-post-activities/resultados-spike-timeout.md"

git diff -- $ruta
git add -- $ruta
git diff --cached --name-only
git diff --cached --check
```

Debe quedar preparado únicamente:

`experimentos/semana11-crecimiento-post-activities/resultados-spike-timeout.md`

No se deben incluir:

- Logcat privados;
- carpetas de evidencia privada;
- prerregistro modificado;
- JWT;
- tokens;
- credenciales;
- secretos;
- archivos no relacionados.

---

## 28.7 Commit de consolidación final

Si el staging contiene únicamente el documento esperado y:

```powershell
git diff --cached --check
```

no presenta errores, podrá registrarse la consolidación mediante un commit nuevo.

Mensaje propuesto:

```powershell
git commit -m "S11 A4: consolidar resultado final del experimento de timeout"
```

Después podrá publicarse en:

`exp/semana11-timeout3-intervencion`

mediante:

```powershell
git push origin exp/semana11-timeout3-intervencion
```

Una vez creado el commit, deberá sustituirse:

`Commit de consolidación cuantitativa = PENDIENTE`

por el hash real correspondiente.

---

## 28.8 Estado final antes del commit de consolidación

```text
Prerregistro                         = COMPLETADO
Verificación histórica              = COMPLETADA

Línea base                          = CERRADA
B1                                  = 10 028 ms
B2                                  = 10 033 ms
B3                                  = 10 023 ms
M_antes                             = 10 028 ms

Intervención                        = connectTimeout = 3 segundos

TIMEOUT3-R1                         = NO EVALUABLE
TIMEOUT3-R1-REP1                    = EVALUABLE
T1                                  = 3 024 ms

TIMEOUT3-R2                         = EVALUABLE CON INCIDENCIA DOCUMENTADA
T2                                  = 3 011 ms

TIMEOUT3-R3                         = EVALUABLE
T3                                  = 3 014 ms

Corridas posteriores evaluables     = 3 de 3
M_después                           = 3 014 ms
Reducción                           = 69,94 %
Umbral                              = 50 %

Falso éxito Android                 = NO OBSERVADO
Persistencia remota inesperada      = NO OBSERVADA EN LAS CORRIDAS EVALUABLES

Comparación principal               = EVALUABLE Y COMPLETADA
Hipótesis                           = RESPALDADA
Estado experimental                 = COMPLETADO
```