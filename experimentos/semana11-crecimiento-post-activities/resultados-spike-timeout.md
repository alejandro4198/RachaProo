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

`PENDIENTE DE CÁLCULO CONSOLIDADO`

Reducción porcentual:

`PENDIENTE DE CÁLCULO CONSOLIDADO`

Hipótesis:

`NO EVALUADA`

Comparación principal:

`PENDIENTE DE CONSOLIDACIÓN CUANTITATIVA`

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
- `TIMEOUT3-R3 = EJECUTADA — EVALUABLE`

Valores evaluables consolidados:

`3 de 3`

Valores disponibles:

- `T1 = 3 024 ms`
- `T2 = 3 011 ms`
- `T3 = 3 014 ms`

Reemplazos utilizados:

`1 de 1 permitido`

Reemplazos disponibles:

`0`

La condición posterior ya dispone de las tres corridas evaluables requeridas para realizar posteriormente el cálculo consolidado.

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

---

# 10. Regla de reemplazo

Máximo permitido:

`1 REPETICIÓN DE REEMPLAZO POR CONDICIÓN`

TIMEOUT3-R1 cumplió la condición para utilizar la repetición.

El reemplazo:

`TIMEOUT3-R1-REP1`

se conserva independientemente de la corrida original.

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

---

# 16. TIMEOUT3-R3

Estado:

`EJECUTADA — EVALUABLE`

Identificador definido para la corrida:

`S11-A4-TIMEOUT3-R3`

Valor admitido:

`T3 = 3 014 ms`

La corrida completa las tres mediciones posteriores evaluables requeridas para realizar posteriormente la consolidación cuantitativa.

---

## 16.1 Estado de PostgreSQL antes del intento

Antes de ejecutar la operación experimental se comprobó PostgreSQL utilizando el identificador correspondiente a R3:

`S11-A4-TIMEOUT3-R3`

Resultado:

`0 FILAS`

Esta comprobación proporciona un estado previo sin coincidencias registradas para el identificador consultado.

---

## 16.2 Comprobación de indisponibilidad

Puerto:

`8080`

Resultado:

`SIN LISTENER`

Hora:

`13:41:49.173`

La comprobación ocurrió antes del POST experimental iniciado a las:

`13:42:32.930`

Por tanto, se documentó la indisponibilidad observada del backend antes de la solicitud medida.

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

Duración observada:

`3 014 ms`

Valor admitido:

`T3 = 3 014 ms`

---

## 16.6 Excepción

Tipo:

`SocketTimeoutException`

La excepción se registra de manera independiente del valor temporal.

---

## 16.7 Resultado visible en Android

Android mostró:

`No fue posible crear la actividad`

Falso éxito observado:

`NO`

Esta observación corresponde al comportamiento visible de la aplicación.

---

## 16.8 Pulsaciones de Guardar

Número de pulsaciones durante TIMEOUT3-R3:

`1`

Fuente:

`CONFIRMACIÓN OPERATIVA`

Esta confirmación fue proporcionada después de la ejecución.

No se presenta como un dato obtenido directamente del Logcat BASIC.

---

## 16.9 PostgreSQL después del intento

Después del POST experimental se consultó PostgreSQL utilizando:

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

A diferencia de R2, en R3 no existe una fila histórica previa que deba separarse del intento experimental.

---

## 16.10 Identificador y fuentes de trazabilidad

El identificador definido previamente para la corrida R3 es:

`S11-A4-TIMEOUT3-R3`

La procedencia de esta información se mantiene diferenciada de la evidencia HTTP.

### Identificador previsto

La versión histórica del prerregistro establece para R3:

`S11-A4-TIMEOUT3-R3`

Por tanto:

`IDENTIFICADOR PREVISTO POR EL PRERREGISTRO = S11-A4-TIMEOUT3-R3`

### Identificador utilizado en PostgreSQL

Las comprobaciones antes y después de la operación se realizaron consultando:

`S11-A4-TIMEOUT3-R3`

Por tanto:

`IDENTIFICADOR CONSULTADO EN POSTGRESQL = S11-A4-TIMEOUT3-R3`

### Contenido funcional del POST

El nivel:

`HttpLoggingInterceptor.Level.BASIC`

no expone el contenido completo del body.

Por tanto, Logcat BASIC no demuestra por sí solo que el campo funcional del título contenido dentro del POST fuera:

`S11-A4-TIMEOUT3-R3`

No se transforma:

- el identificador previsto por el prerregistro;
- ni el identificador consultado en PostgreSQL;

en una observación instrumental del contenido del body.

Esta limitación permanece explícita.

La correlación técnica del POST se sustenta separadamente mediante:

- timestamp de inicio;
- timestamp de fallo;
- mismo hilo OkHttp;
- endpoint correspondiente;
- resultado `SocketTimeoutException`.

---

## 16.11 Integridad de evidencia

Archivo privado:

`evidencia-timeout/TIMEOUT3-R3/logcat-TIMEOUT3-R3-privado.txt`

SHA-256:

`572453C199416CA22FF7A57E4FCE75A61837F7B8AC06C5F611070BF4B7325884`

El hash corresponde al archivo privado conservado después de la ejecución.

Las dos marcas utilizadas para el cálculo fueron localizadas en dicho archivo.

---

# 17. Evaluabilidad de TIMEOUT3-R3

## 17.1 Evidencia registrada en Logcat

El Logcat permite observar:

- inicio del POST a las `13:42:32.930`;
- línea `40853`;
- hilo OkHttp `30948`;
- fallo a las `13:42:35.944`;
- línea `40854`;
- `SocketTimeoutException`;
- duración calculable de `3 014 ms`.

---

## 17.2 Comprobación de infraestructura

Puerto:

`8080`

Resultado:

`SIN LISTENER`

Hora:

`13:41:49.173`

La comprobación ocurrió antes de la solicitud experimental.

---

## 17.3 Observación Android

Mensaje:

`No fue posible crear la actividad`

Falso éxito observado:

`NO`

---

## 17.4 Confirmación operativa

Pulsaciones de `Guardar`:

`1`

Fuente:

`CONFIRMACIÓN OPERATIVA`

---

## 17.5 Comprobación PostgreSQL

Identificador consultado:

`S11-A4-TIMEOUT3-R3`

Antes:

`0 FILAS`

Después:

`0 FILAS`

Por tanto:

`NO SE OBSERVÓ NUEVA PERSISTENCIA REMOTA BAJO EL IDENTIFICADOR CONSULTADO`

---

## 17.6 Integridad

SHA-256:

`572453C199416CA22FF7A57E4FCE75A61837F7B8AC06C5F611070BF4B7325884`

Estado:

`EVIDENCIA PRIVADA CONSERVADA`

---

## 17.7 Limitaciones conservadas

Se mantiene explícitamente que:

- Logcat BASIC no expone el título funcional contenido en el body;
- el identificador `S11-A4-TIMEOUT3-R3` está respaldado como identificador previsto por el prerregistro;
- el mismo identificador fue utilizado en las consultas PostgreSQL;
- estas fuentes no se presentan como prueba directa del contenido del body;
- la pulsación única corresponde a confirmación operativa.

No se identificó:

- más de una pulsación de `Guardar`;
- una segunda solicitud experimental de creación;
- persistencia remota inesperada;
- falso éxito en Android;
- imposibilidad de correlacionar técnicamente el POST y su fallo.

---

## 17.8 Decisión metodológica

La corrida dispone de:

- estado PostgreSQL previo de `0 filas` para el identificador consultado;
- comprobación del puerto sin listener;
- POST experimental técnicamente correlacionable;
- mismo hilo para inicio y fallo;
- duración calculable;
- `SocketTimeoutException`;
- una sola pulsación de `Guardar` según confirmación operativa;
- error visible en Android;
- ausencia de falso éxito observada;
- PostgreSQL posterior con `0 filas` para el identificador consultado;
- evidencia privada conservada;
- SHA-256 verificado;
- limitaciones de trazabilidad del body expresamente registradas.

No se identificó una desviación que impida reconstruir suficientemente la operación experimental.

Clasificación:

`TIMEOUT3-R3 = EVALUABLE`

Valor admitido:

`T3 = 3 014 ms`

La clasificación corresponde a la decisión metodológica documentada para la corrida.

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

La condición posterior dispone ya de las tres corridas evaluables requeridas.

---

# 19. Mediana posterior

Valores posteriores admitidos:

- `T1 = 3 024 ms`
- `T2 = 3 011 ms`
- `T3 = 3 014 ms`

Estado de disponibilidad:

`3 de 3 VALORES EVALUABLES DISPONIBLES`

El cálculo formal de:

`M_después`

queda:

`PENDIENTE DE CONSOLIDACIÓN DESPUÉS DEL REGISTRO DOCUMENTAL DE R3`

En esta versión no se anticipa todavía el valor de la mediana posterior.

---

# 20. Reducción porcentual

Mediana de línea base:

`M_antes = 10 028 ms`

Mediana posterior:

`M_después = PENDIENTE DE CÁLCULO`

Fórmula preregistrada:

\[
Reducción(\%) =
\frac{M_{antes} - M_{después}}
{M_{antes}}
\times 100
\]

Resultado:

`PENDIENTE DE CÁLCULO`

Criterio preregistrado:

`REDUCCIÓN >= 50 %`

Aunque los tres valores posteriores ya están disponibles, el contraste cuantitativo se realizará después del registro documental de R3.

---

# 21. Estado de la hipótesis

Hipótesis preregistrada:

> Ante una indisponibilidad controlada de Spring Boot, configurar explícitamente `connectTimeout = 3 segundos` reducirá al menos un 50 % la mediana de la duración técnica de los `POST` fallidos de creación de actividad respecto de una nueva línea base ejecutada bajo el mismo protocolo, sin que Android presente falsamente la operación como exitosa ni se observe persistencia remota inesperada de la actividad intentada durante el fallo.

Estado:

`NO EVALUADA`

La disponibilidad de:

- `T1`
- `T2`
- `T3`

permite realizar posteriormente el contraste cuantitativo.

Sin embargo, esta versión no declara todavía:

`HIPÓTESIS RESPALDADA`

ni:

`HIPÓTESIS NO RESPALDADA`

La evaluación se realizará después de consolidar:

- `M_después`;
- reducción porcentual;
- condiciones funcionales complementarias.

---

# 22. Comparación principal

Línea base:

`3 de 3 CORRIDAS EVALUABLES`

Condición posterior:

`3 de 3 CORRIDAS EVALUABLES`

Reemplazo:

`1 de 1 UTILIZADO`

Estado:

`DATOS NECESARIOS DISPONIBLES — CONSOLIDACIÓN CUANTITATIVA PENDIENTE`

La categoría:

`COMPARACIÓN PRINCIPAL NO EVALUABLE`

no corresponde al estado actual, porque se dispone del número requerido de corridas evaluables en ambas condiciones.

---

# 23. Diferencia entre hipótesis no respaldada y comparación principal no evaluable

Si, después de realizar el cálculo correspondiente, la reducción es menor al:

`50 %`

bajo una comparación válida, corresponderá:

`HIPÓTESIS NO RESPALDADA`

Si no existieran las corridas evaluables requeridas, podría corresponder:

`COMPARACIÓN PRINCIPAL NO EVALUABLE`

En el estado actual existen:

`3 CORRIDAS EVALUABLES ANTES`

y:

`3 CORRIDAS EVALUABLES DESPUÉS`

Por tanto, la comparación dispone de los datos necesarios para realizar el contraste cuantitativo posterior.

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
- tres corridas evaluables como condición para calcular la mediana posterior.

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

Commit de revisión documental previo:

`964ef25`

Commit comprobado posteriormente para REP1:

`11c4092`

Commit documental de REP1:

`bf8fcbc`

Commit documental de TIMEOUT3-R2:

`40eb3cf`

TIMEOUT3-R3 fue ejecutada:

`DESPUÉS DEL REGISTRO DOCUMENTAL DE R2`

Commit documental de TIMEOUT3-R3:

`PENDIENTE`

El commit de R3 deberá ser:

`POSTERIOR A 40eb3cf`

No se asigna un hash al registro documental de R3 hasta que dicho commit exista realmente.

Secuencia consolidada:

`94dcc32 prerregistro`

`-> línea base`

`-> b6f82a5 cierre de línea base`

`-> be2b2f3 merge de línea base`

`-> a722954 intervención`

`-> TIMEOUT3-R1`

`-> cc68828 registro de TIMEOUT3-R1`

`-> clasificación NO EVALUABLE`

`-> verificación histórica`

`-> 964ef25 revisión documental`

`-> TIMEOUT3-R1-REP1`

`-> 11c4092 comprobado posteriormente como commit activo`

`-> clasificación REP1 EVALUABLE`

`-> bf8fcbc registro documental de REP1`

`-> TIMEOUT3-R2`

`-> clasificación EVALUABLE CON INCIDENCIA DOCUMENTADA`

`-> 40eb3cf registro documental de R2`

`-> TIMEOUT3-R3`

`-> clasificación EVALUABLE`

`-> commit documental de R3 PENDIENTE`

`-> consolidación cuantitativa PENDIENTE`

La secuencia conserva por separado:

- ejecución experimental;
- decisión metodológica;
- registro documental;
- comprobaciones posteriores.

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

Identificador previsto por el prerregistro:

`S11-A4-TIMEOUT3-R3`

Identificador consultado en PostgreSQL:

`S11-A4-TIMEOUT3-R3`

Contenido del título en el body demostrado directamente por Logcat BASIC:

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

`PENDIENTE`

---

## 27.7 Condición posterior

T1:

`3 024 ms`

T2:

`3 011 ms`

T3:

`3 014 ms`

Corridas evaluables:

`3 de 3`

Reemplazos:

`1 de 1 UTILIZADO`

M_después:

`PENDIENTE DE CÁLCULO CONSOLIDADO`

Reducción:

`PENDIENTE DE CÁLCULO CONSOLIDADO`

Hipótesis:

`NO EVALUADA`

Comparación principal:

`DATOS COMPLETOS — CÁLCULO FINAL PENDIENTE`

---

# 28. Próximo punto de control

TIMEOUT3-R3 queda documentada como:

`EVALUABLE`

Valor:

`T3 = 3 014 ms`

La condición posterior ya cuenta con:

`3 DE 3 CORRIDAS EVALUABLES`

No es necesario repetir:

- la línea base;
- TIMEOUT3-R1;
- TIMEOUT3-R1-REP1;
- TIMEOUT3-R2;
- TIMEOUT3-R3.

La siguiente acción es registrar documentalmente R3 mediante un commit posterior a:

`40eb3cf`

---

## 28.1 Revisión del documento antes del commit

Archivo:

`experimentos/semana11-crecimiento-post-activities/resultados-spike-timeout.md`

Ejecutar:

```powershell
$ruta = "experimentos/semana11-crecimiento-post-activities/resultados-spike-timeout.md"

git diff -- $ruta
```

La revisión debe confirmar:

- `T1 = 3 024 ms`;
- `T2 = 3 011 ms`;
- `T3 = 3 014 ms`;
- R2 conserva la fila histórica `1500185`;
- R2 utiliza `0 filas nuevas observadas`;
- R2 conserva el commit `40eb3cf`;
- R3 tiene PostgreSQL `0 filas / 0 filas`;
- puerto R3 sin listener a las `13:41:49.173`;
- POST R3 a las `13:42:32.930`;
- fallo R3 a las `13:42:35.944`;
- hilo `30948`;
- una pulsación de `Guardar` según confirmación operativa;
- SHA-256 completo de R3;
- el identificador previsto y el identificador consultado en PostgreSQL están diferenciados del contenido del body;
- Logcat BASIC no se presenta como evidencia directa del título funcional;
- el commit de R2 figura como `40eb3cf`;
- el commit documental de R3 continúa `PENDIENTE`;
- `M_después` todavía no está calculada;
- la reducción todavía no está calculada;
- la hipótesis permanece `NO EVALUADA`.

---

## 28.2 Preparación del commit de R3

Después de revisar el diff:

```powershell
git add -- $ruta
git diff --cached --name-only
git diff --cached --check
```

Debe quedar preparado únicamente:

`experimentos/semana11-crecimiento-post-activities/resultados-spike-timeout.md`

No se deben incluir:

- Logcat privados;
- archivos de evidencia cruda;
- prerregistro modificado;
- JWT;
- credenciales;
- tokens;
- secretos;
- archivos no relacionados.

---

## 28.3 Commit documental de R3

Si el staging contiene únicamente el documento esperado y `git diff --cached --check` no muestra errores:

```powershell
git commit -m "S11 A4: registrar R3 y su evaluabilidad"
git push origin exp/semana11-timeout3-intervencion
```

Después de crear el commit deberá sustituirse:

`Commit documental de TIMEOUT3-R3 = PENDIENTE`

por el hash real obtenido.

---

## 28.4 Consolidación cuantitativa posterior

Después de versionar R3 podrán ordenarse formalmente:

- `T1 = 3 024 ms`
- `T2 = 3 011 ms`
- `T3 = 3 014 ms`

y calcularse:

`M_después`

Posteriormente podrá aplicarse:

\[
Reducción(\%) =
\frac{M_{antes} - M_{después}}
{M_{antes}}
\times 100
\]

utilizando:

`M_antes = 10 028 ms`

El resultado deberá contrastarse con el criterio preregistrado:

`REDUCCIÓN >= 50 %`

Solo después de esa consolidación corresponde determinar el estado final de la hipótesis.

---

## 28.5 Estado actual

Hasta registrar R3 y realizar posteriormente la consolidación cuantitativa:

`M_antes = 10 028 ms`

`T1 = 3 024 ms`

`T2 = 3 011 ms`

`T3 = 3 014 ms`

`M_después = PENDIENTE DE CÁLCULO`

`REDUCCIÓN = PENDIENTE DE CÁLCULO`

`HIPÓTESIS = NO EVALUADA`

`COMPARACIÓN PRINCIPAL = DATOS COMPLETOS — CONSOLIDACIÓN FINAL PENDIENTE`