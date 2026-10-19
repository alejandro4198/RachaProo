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

- hechos instrumentales observados;
- confirmaciones operativas;
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

`EJECUTADA`

Clasificación metodológica:

`NO EVALUABLE`

Commit documental de TIMEOUT3-R1:

`cc68828`

Commit posterior de revisión documental publicado en la rama experimental:

`964ef25`

Valor T1:

`NO ASIGNADO`

Mediciones técnicas observadas durante TIMEOUT3-R1:

- `3 012 ms`
- `3 015 ms`

Tratamiento:

`OBSERVACIONES DESCRIPTIVAS — NO INCORPORADAS A LA COMPARACIÓN PRINCIPAL`

Motivo metodológico:

`EL PROTOCOLO DE LA CORRIDA NO PUDO CUMPLIRSE SUFICIENTEMENTE`

Incidencia principal:

`SE REALIZARON DOS INTENTOS POST DE CREACIÓN DURANTE LA MISMA CORRIDA`

Regla histórica aplicable:

`PULSAR GUARDAR EXACTAMENTE UNA VEZ`

Reemplazo de TIMEOUT3-R1:

`PERMITIDO POR EL PRERREGISTRO — PENDIENTE DE EJECUCIÓN`

Reemplazos utilizados en la condición posterior:

`0 de 1 permitido`

TIMEOUT3-R2:

`NO EJECUTADA`

TIMEOUT3-R3:

`NO EJECUTADA`

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

Consulta por:

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

La incidencia de control permanece documentada.

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

La intervención se registró antes de ejecutar la condición posterior.

---

# 7. Condición posterior a la intervención

El prerregistro histórico `94dcc32` establece:

`3 CORRIDAS PRINCIPALES`

después de la intervención.

Los identificadores confirmados en la versión histórica son:

- `S11-A4-TIMEOUT3-R1`
- `S11-A4-TIMEOUT3-R2`
- `S11-A4-TIMEOUT3-R3`

Estado:

- `TIMEOUT3-R1 = EJECUTADA — NO EVALUABLE`
- `TIMEOUT3-R2 = NO EJECUTADA`
- `TIMEOUT3-R3 = NO EJECUTADA`

Corridas posteriores evaluables consolidadas:

`0 de 3`

Existe una repetición de reemplazo disponible para la condición posterior conforme a las reglas históricas.

---

# 8. TIMEOUT3-R1

Estado:

`EJECUTADA`

Identificador definido en el prerregistro:

`S11-A4-TIMEOUT3-R1`

Presencia del identificador en la versión histórica `94dcc32`:

`CONFIRMADA`

Commit de intervención utilizado:

`a722954`

Configuración:

`connectTimeout = 3 segundos`

Commit de registro de la corrida:

`cc68828`

Clasificación:

`NO EVALUABLE`

Valor T1:

`NO ASIGNADO`

---

## 8.1 Comprobación de indisponibilidad

Puerto:

`8080`

Listeners:

`0`

Hora:

`20:32:15.603`

La comprobación fue realizada antes de los dos POST fallidos observados posteriormente.

---

## 8.2 Primer POST observado

Inicio:

`20:32:49.240`

Fin:

`20:32:52.252`

Cálculo:

`52.252 - 49.240 = 3.012 segundos`

Duración:

`3 012 ms`

---

## 8.3 Segundo POST observado

Inicio:

`20:33:40.536`

Fin:

`20:33:43.551`

Cálculo:

`43.551 - 40.536 = 3.015 segundos`

Duración:

`3 015 ms`

---

## 8.4 Tratamiento de las mediciones

Los dos POST poseen pares inicio-resultado técnicamente distinguibles.

Por tanto, existen dos duraciones calculables:

- `3 012 ms`
- `3 015 ms`

Estas duraciones constituyen:

`HECHOS TÉCNICOS OBSERVADOS`

No se asigna ninguna de ellas automáticamente como:

`T1`

La decisión metodológica sobre la corrida se mantiene separada de las mediciones observadas.

---

## 8.5 Identificadores utilizados

La versión histórica `94dcc32` contiene:

`S11-A4-TIMEOUT3-R1`

como identificador de la primera corrida posterior a la intervención.

Según confirmación operativa retrospectiva, durante la ejecución se utilizaron dos títulos en este orden:

1. `S11-A4-TIMEOUT-R1`
2. `S11-A4-TIMEOUT3-R1`

Por tanto:

- el primer intento utilizó un identificador diferente del definido para la corrida;
- el segundo intento utilizó el identificador definido para TIMEOUT3-R1.

La secuencia de títulos corresponde a:

`CONFIRMACIÓN OPERATIVA RETROSPECTIVA`

El Logcat BASIC no permite por sí solo identificar el contenido funcional de cada body.

Por tanto, no se presenta la asociación título-POST como una conclusión obtenida exclusivamente del Logcat.

---

## 8.6 Limitación de Logcat BASIC

El nivel:

`HttpLoggingInterceptor.Level.BASIC`

no expone el body completo.

Por tanto, no permite observar directamente el título enviado dentro de cada solicitud.

La distinción temporal entre los dos POST sí se encuentra registrada.

---

## 8.7 Operación registrada a las 20:32:25.078

También se observó una operación con error a las:

`20:32:25.078`

La revisión posterior permitió establecer que correspondía a:

`PATCH`

y no a:

`POST /api/activities`

Por tanto:

`NO SE CONTABILIZA COMO UN TERCER POST DE CREACIÓN`

No participa en la métrica de creación de actividad.

No se le atribuye un inicio no observado.

---

## 8.8 Resultado Android

Android mostró:

`No fue posible crear la actividad`

Falso éxito observado:

`NO`

---

## 8.9 Persistencia remota

Consulta PostgreSQL para:

`S11-A4-TIMEOUT3-R1`

Resultado:

`0 FILAS`

Interpretación:

`NO SE OBSERVÓ PERSISTENCIA REMOTA BAJO EL IDENTIFICADOR CONSULTADO`

La ausencia de persistencia no determina por sí sola la evaluabilidad de la corrida.

---

## 8.10 Integridad de evidencia

SHA-256 del Logcat privado:

`787BB5935347EABAD2C281765E715C5F247BEA3FB626DEF21768273BCCC9AC81`

Estado:

`EVIDENCIA PRIVADA CONSERVADA`

El Logcat completo permanece fuera del repositorio público.

---

# 9. Evaluabilidad de TIMEOUT3-R1

## 9.1 Hechos observados

Se dispone de evidencia de:

- puerto `8080` sin listener antes de las solicitudes;
- primer POST de `3 012 ms`;
- segundo POST de `3 015 ms`;
- resultado de error visible en Android;
- ausencia de falso éxito observada;
- `0 filas` para `S11-A4-TIMEOUT3-R1`;
- operación PATCH separada;
- SHA-256 del Logcat privado.

Estos hechos permanecen registrados independientemente de la decisión metodológica.

---

## 9.2 Desviación del procedimiento

La versión histórica del prerregistro establece para la condición posterior:

- utilizar el identificador correspondiente;
- pulsar `Guardar` exactamente una vez;
- identificar el `--> POST`;
- identificar el resultado de esa misma solicitud;
- registrar el resultado visible;
- comprobar PostgreSQL;
- registrar desviaciones.

También define:

`S11-A4-TIMEOUT3-R1`

como identificador de la primera corrida posterior.

Durante la ejecución se realizaron dos intentos POST de creación.

Según confirmación operativa:

- el primero utilizó `S11-A4-TIMEOUT-R1`;
- el segundo utilizó `S11-A4-TIMEOUT3-R1`.

Por tanto, la ejecución presentó una desviación respecto del procedimiento definido para una única operación experimental.

---

## 9.3 Regla histórica de corrida no evaluable

La versión histórica `94dcc32` contiene la sección:

`Corridas no evaluables`

En ella se establece que una corrida puede clasificarse:

`NO EVALUABLE`

cuando exista evidencia de que:

`EL PROTOCOLO NO PUDO CUMPLIRSE SUFICIENTEMENTE`

Entre los ejemplos establecidos antes de las mediciones aparece:

`HUBO MÁS DE UNA PULSACIÓN DE GUARDAR`

También aparece como ejemplo:

`EXISTIERON SOLICITUDES CONCURRENTES IMPOSIBLES DE CORRELACIONAR`

En TIMEOUT3-R1 los dos pares inicio-resultado son técnicamente distinguibles.

Por tanto, la decisión no se fundamenta en una imposibilidad de correlación temporal.

El criterio utilizado es:

`INCUMPLIMIENTO SUFICIENTE DEL PROTOCOLO POR MÁS DE UNA OPERACIÓN DE GUARDADO DURANTE LA MISMA CORRIDA`

---

## 9.4 Verificación histórica

Estado:

`COMPLETADA`

La consulta directa del commit:

`94dcc32`

confirmó que, antes de ejecutar las mediciones, ya estaban definidos:

- `S11-A4-TIMEOUT3-R1`;
- `S11-A4-TIMEOUT3-R2`;
- `S11-A4-TIMEOUT3-R3`;
- una sola pulsación de `Guardar`;
- la categoría `NO EVALUABLE`;
- el criterio de protocolo no cumplido suficientemente;
- más de una pulsación de `Guardar` como ejemplo;
- máximo un reemplazo por condición;
- uso obligatorio de un identificador nuevo en el reemplazo;
- conservación de la corrida original;
- mantenimiento del mismo protocolo para el reemplazo.

Los caracteres extraños observados en la salida de PowerShell corresponden a la representación de caracteres en consola y no se utilizan para cuestionar el contenido histórico del archivo.

---

## 9.5 Decisión metodológica

Resultados técnicos observados:

- `3 012 ms`
- `3 015 ms`

Clasificación:

`TIMEOUT3-R1 = NO EVALUABLE`

Razón:

`EL PROTOCOLO NO PUDO CUMPLIRSE SUFICIENTEMENTE`

Criterio histórico aplicado:

`MÁS DE UNA PULSACIÓN DE GUARDAR DURANTE LA MISMA CORRIDA`

Incidencia adicional:

`EL PRIMER INTENTO UTILIZÓ UN IDENTIFICADOR DIFERENTE DEL DEFINIDO PARA TIMEOUT3-R1`

Valor T1:

`NO ASIGNADO`

Los valores:

- `3 012 ms`;
- `3 015 ms`;

permanecen registrados como:

`OBSERVACIONES DESCRIPTIVAS`

No participan en la comparación principal.

---

## 9.6 Alcance de la clasificación

TIMEOUT3-R1:

- permanece registrada;
- conserva sus mediciones;
- conserva la incidencia;
- conserva el resultado Android;
- conserva la consulta PostgreSQL;
- conserva su Logcat privado;
- conserva su SHA-256;
- conserva su trazabilidad Git;
- no aporta T1;
- no cuenta como respaldo de la hipótesis;
- no cuenta como no respaldo de la hipótesis.

La clasificación:

`NO EVALUABLE`

corresponde únicamente a:

`TIMEOUT3-R1`

No corresponde al experimento completo.

---

# 10. Regla de reemplazo

## 10.1 Regla histórica

La versión histórica `94dcc32` establece:

`MÁXIMO 1 REPETICIÓN DE REEMPLAZO POR CONDICIÓN`

La repetición puede utilizarse cuando una corrida ha sido previamente clasificada:

`NO EVALUABLE`

La repetición adicional:

- debe responder a una causa metodológica documentada;
- no puede justificarse por el valor temporal obtenido;
- debe conservar la evidencia de la corrida original;
- debe utilizar un identificador nuevo;
- debe seguir exactamente el mismo protocolo;
- debe registrarse explícitamente como reemplazo.

La corrida original:

`NO SERÁ ELIMINADA NI SUSTITUIDA DEL REGISTRO HISTÓRICO`

---

## 10.2 Verificación histórica de la regla

Estado:

`COMPLETADA`

La regla fue comprobada directamente en:

`94dcc32`

Por tanto, estaba definida antes de ejecutar TIMEOUT3-R1.

TIMEOUT3-R1 cumple la condición previa:

`CORRIDA PREVIAMENTE CLASIFICADA COMO NO EVALUABLE`

La causa documentada es metodológica.

La posibilidad de realizar el reemplazo no depende de que las mediciones observadas hayan sido:

- `3 012 ms`;
- `3 015 ms`.

---

## 10.3 Estado del reemplazo

Condición:

`POSTERIOR A LA INTERVENCIÓN`

Máximo permitido:

`1`

Ejecutados:

`0`

Disponible:

`SÍ`

Estado:

`PENDIENTE DE EJECUCIÓN`

---

## 10.4 Identificador del reemplazo

El reemplazo debe utilizar:

`UN IDENTIFICADOR NUEVO`

El identificador propuesto para la repetición es:

`S11-A4-TIMEOUT3-R1-REP1`

Este formato sigue el esquema de reemplazo definido en el prerregistro.

No deberán reutilizarse:

- `S11-A4-TIMEOUT-R1`;
- `S11-A4-TIMEOUT3-R1`.

---

## 10.5 Conservación de TIMEOUT3-R1

La ejecución del reemplazo no elimina ni sobrescribe TIMEOUT3-R1.

La corrida original continuará registrada como:

`NO EVALUABLE`

junto con:

- `3 012 ms`;
- `3 015 ms`;
- incidencia documental;
- resultado Android;
- resultado PostgreSQL;
- SHA-256;
- commits asociados.

---

# 11. TIMEOUT3-R1-REP1

Estado:

`NO EJECUTADA`

Identificador previsto:

`S11-A4-TIMEOUT3-R1-REP1`

Condición:

`REPETICIÓN DE REEMPLAZO DE TIMEOUT3-R1`

Valor:

`PENDIENTE`

Si la repetición cumple suficientemente el protocolo y resulta evaluable, su valor podrá ocupar la posición:

`T1`

---

# 12. TIMEOUT3-R2

Estado:

`NO EJECUTADA`

Identificador definido:

`S11-A4-TIMEOUT3-R2`

Valor:

`T2 = PENDIENTE`

---

# 13. TIMEOUT3-R3

Estado:

`NO EJECUTADA`

Identificador definido:

`S11-A4-TIMEOUT3-R3`

Valor:

`T3 = PENDIENTE`

---

# 14. Estado de los valores posteriores

TIMEOUT3-R1:

`NO EVALUABLE`

T1:

`NO ASIGNADO`

TIMEOUT3-R1-REP1:

`NO EJECUTADA`

TIMEOUT3-R2:

`NO EJECUTADA`

T2:

`PENDIENTE`

TIMEOUT3-R3:

`NO EJECUTADA`

T3:

`PENDIENTE`

Corridas posteriores evaluables consolidadas:

`0 de 3`

---

# 15. Mediana posterior

El prerregistro exige:

`3 CORRIDAS EVALUABLES DESPUÉS DE LA INTERVENCIÓN`

Estado actual:

`0 de 3 consolidadas`

Por tanto:

`M_después = NO CALCULADA`

Las observaciones:

- `3 012 ms`;
- `3 015 ms`;

no participan en el cálculo de la mediana posterior.

---

# 16. Reducción porcentual

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

No se utilizarán las observaciones de TIMEOUT3-R1 para anticipar:

- `M_después`;
- la reducción porcentual;
- el estado final de la hipótesis.

---

# 17. Estado de la hipótesis

Hipótesis preregistrada:

> Ante una indisponibilidad controlada de Spring Boot, configurar explícitamente `connectTimeout = 3 segundos` reducirá al menos un 50 % la mediana de la duración técnica de los `POST` fallidos de creación de actividad respecto de una nueva línea base ejecutada bajo el mismo protocolo, sin que Android presente falsamente la operación como exitosa ni se observe persistencia remota inesperada de la actividad intentada durante el fallo.

Estado actual:

`NO EVALUADA`

TIMEOUT3-R1:

`NO CUENTA COMO RESPALDO`

TIMEOUT3-R1:

`NO CUENTA COMO NO RESPALDO`

Todavía no existen tres corridas posteriores evaluables.

Por tanto, no corresponde declarar todavía:

`HIPÓTESIS RESPALDADA`

ni:

`HIPÓTESIS NO RESPALDADA`

---

# 18. Diferencia entre corrida no evaluable y comparación principal no evaluable

TIMEOUT3-R1 está clasificada:

`NO EVALUABLE`

Esta clasificación corresponde a:

`UNA CORRIDA INDIVIDUAL`

No significa que el resultado global sea:

`COMPARACIÓN PRINCIPAL NO EVALUABLE`

El prerregistro establece que el resultado global solo debe recibir esa clasificación si, después de utilizar cuando corresponda la única repetición permitida por condición, no se consiguen tres corridas evaluables en ambas condiciones.

Ese escenario:

`NO HA OCURRIDO`

porque:

- la línea base ya dispone de tres corridas evaluables;
- el reemplazo posterior todavía está disponible;
- TIMEOUT3-R2 no ha sido ejecutada;
- TIMEOUT3-R3 no ha sido ejecutada.

Estado:

`COMPARACIÓN PRINCIPAL ABIERTA`

---

# 19. Diferencia entre hipótesis no respaldada y comparación principal no evaluable

Si finalmente existen:

- tres corridas evaluables de línea base;
- tres corridas evaluables posteriores;

y la reducción calculada resulta inferior al:

`50 %`

corresponderá:

`HIPÓTESIS NO RESPALDADA`

En cambio, si después de aplicar las reglas de reemplazo permitidas no se reúnen tres corridas evaluables por condición, corresponderá:

`COMPARACIÓN PRINCIPAL NO EVALUABLE`

Estas clasificaciones:

`NO SON EQUIVALENTES`

Estado actual:

`NINGUNA CORRESPONDE TODAVÍA`

---

# 20. Evidencia privada

Los Logcat completos permanecen fuera del repositorio público.

## 20.1 BASE-R1

Archivo privado:

`evidencia-timeout/BASE-R1/logcat-BASE-R1-privado.txt`

SHA-256:

`DE5066FCF6BA7B86E1BD12FCA3041AD77513C38499E25578EB92DD27DA6E1AE5`

---

## 20.2 BASE-R2

Archivo privado:

`evidencia-timeout/BASE-R2/logcat-BASE-R2-privado.txt`

SHA-256:

`2BF5FDDEA23BF2D4644A616BF46DE9A1B9172EA00091E30348FA5CB571F7F907`

---

## 20.3 BASE-R3

Archivo privado:

`evidencia-timeout/BASE-R3/logcat-BASE-R3-privado.txt`

SHA-256:

`6508512032222FCF374F0C8998018DB01E8D466734F0B2516F57128B1894D5FF`

---

## 20.4 TIMEOUT3-R1

SHA-256:

`787BB5935347EABAD2C281765E715C5F247BEA3FB626DEF21768273BCCC9AC81`

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

# 21. Trazabilidad Git

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

Merge del cierre de línea base:

`be2b2f3`

Commit de intervención:

`a722954`

Commit de registro de TIMEOUT3-R1:

`cc68828`

Commit posterior de revisión documental publicado:

`964ef25`

Secuencia:

`prerregistro -> línea base -> cierre de línea base -> intervención -> TIMEOUT3-R1 -> clasificación de TIMEOUT3-R1 -> verificación histórica -> revisión documental -> reemplazo pendiente`

No se modifican retroactivamente:

- el prerregistro `94dcc32`;
- los commits históricos;
- las mediciones BASE;
- `M_antes`;
- la intervención;
- las mediciones observadas de TIMEOUT3-R1;
- la clasificación ya documentada de TIMEOUT3-R1.

Una corrección documental posterior deberá registrarse mediante:

`UN NUEVO COMMIT`

sin reescribir los commits ya publicados.

---

# 22. Verificación histórica del prerregistro

Estado:

`COMPLETADA`

Comando utilizado:

```powershell
$ruta = "experimentos/semana11-crecimiento-post-activities/prerregistro-spike-timeout.md"

git show "94dcc32:$ruta" |
    Select-String -Pattern `
      'NO EVALUABLE|Guardar|reemplazo|repetición|identificador|protocolo|TIMEOUT3' `
      -Context 2,3 |
    Out-String -Width 220
```

La salida histórica confirmó los siguientes elementos.

---

## 22.1 Corridas posteriores

Número previsto:

`3 CORRIDAS PRINCIPALES`

---

## 22.2 Identificadores posteriores

Confirmados:

- `S11-A4-TIMEOUT3-R1`
- `S11-A4-TIMEOUT3-R2`
- `S11-A4-TIMEOUT3-R3`

Los identificadores no deben reutilizarse entre corridas.

---

## 22.3 Operación experimental

El protocolo posterior exige:

`UTILIZAR EL IDENTIFICADOR CORRESPONDIENTE`

y:

`PULSAR GUARDAR EXACTAMENTE UNA VEZ`

---

## 22.4 Corridas no evaluables

El prerregistro establece que una corrida puede clasificarse:

`NO EVALUABLE`

cuando exista evidencia de que:

`EL PROTOCOLO NO PUDO CUMPLIRSE SUFICIENTEMENTE`

Entre los ejemplos establecidos aparece:

`HUBO MÁS DE UNA PULSACIÓN DE GUARDAR`

---

## 22.5 Reemplazo

Máximo:

`1 REPETICIÓN DE REEMPLAZO POR CONDICIÓN`

La repetición:

- debe tener una causa metodológica documentada;
- no puede justificarse por el valor temporal obtenido;
- conserva la evidencia original;
- utiliza un identificador nuevo;
- sigue exactamente el mismo protocolo;
- se registra expresamente como reemplazo.

---

## 22.6 Conservación histórica

La corrida original no evaluable:

`NO SERÁ ELIMINADA NI SUSTITUIDA DEL REGISTRO HISTÓRICO`

---

## 22.7 Condición para calcular medianas

Se requieren:

`3 CORRIDAS EVALUABLES EN LÍNEA BASE`

y:

`3 CORRIDAS EVALUABLES DESPUÉS DE LA INTERVENCIÓN`

La primera condición ya se cumple.

La segunda:

`PENDIENTE`

---

## 22.8 Resultado global no evaluable

Solo si después de utilizar, cuando corresponda, la repetición permitida todavía no se consiguen tres corridas evaluables por condición, podrá corresponder:

`COMPARACIÓN PRINCIPAL NO EVALUABLE`

Ese estado:

`NO CORRESPONDE ACTUALMENTE`

La verificación histórica queda:

`CERRADA`

---

# 23. Estado consolidado de la Actividad 4

## 23.1 Línea base

BASE-R1:

`EVALUABLE CON DESVIACIÓN DOCUMENTADA`

Valor:

`10 028 ms`

BASE-R2:

`EVALUABLE CON INCIDENCIA PREVIA RESUELTA`

Valor:

`10 033 ms`

BASE-R3:

`EVALUABLE CON INCIDENCIA DOCUMENTADA`

Valor:

`10 023 ms`

Mediana:

`M_antes = 10 028 ms`

Estado:

`CERRADA`

Reemplazos utilizados:

`0`

---

## 23.2 Intervención

Configuración:

`connectTimeout = 3 segundos`

Commit:

`a722954`

Estado:

`IMPLEMENTADA`

---

## 23.3 TIMEOUT3-R1

Estado:

`EJECUTADA — NO EVALUABLE`

Identificador definido:

`S11-A4-TIMEOUT3-R1`

Medición técnica 1:

`3 012 ms`

Medición técnica 2:

`3 015 ms`

T1:

`NO ASIGNADO`

Motivo metodológico:

`PROTOCOLO NO CUMPLIDO SUFICIENTEMENTE`

Criterio histórico aplicado:

`MÁS DE UNA PULSACIÓN DE GUARDAR`

Incidencia adicional:

`PRIMER INTENTO CON IDENTIFICADOR DISTINTO DEL DEFINIDO PARA TIMEOUT3-R1`

Android:

`ERROR MOSTRADO`

Falso éxito:

`NO OBSERVADO`

Persistencia:

`0 FILAS`

Operación de las:

`20:32:25.078`

Tipo:

`PATCH`

Tratamiento:

`NO CORRESPONDE A UN TERCER POST DE CREACIÓN`

SHA-256:

`787BB5935347EABAD2C281765E715C5F247BEA3FB626DEF21768273BCCC9AC81`

Commit documental:

`cc68828`

---

## 23.4 Reemplazo de TIMEOUT3-R1

Permitido por el prerregistro:

`SÍ`

Máximo:

`1`

Utilizados:

`0`

Identificador:

`S11-A4-TIMEOUT3-R1-REP1`

Estado:

`PENDIENTE DE EJECUCIÓN`

---

## 23.5 TIMEOUT3-R2

Estado:

`NO EJECUTADA`

Identificador:

`S11-A4-TIMEOUT3-R2`

---

## 23.6 TIMEOUT3-R3

Estado:

`NO EJECUTADA`

Identificador:

`S11-A4-TIMEOUT3-R3`

---

## 23.7 Estado cuantitativo posterior

Valores posteriores evaluables:

`0 de 3`

M_después:

`NO CALCULADA`

Reducción:

`NO CALCULADA`

Hipótesis:

`NO EVALUADA`

Comparación principal:

`ABIERTA`

---

# 24. Próximo punto de control

La verificación histórica:

`YA ESTÁ COMPLETADA`

No es necesario volver a comprobar:

- existencia de `S11-A4-TIMEOUT3-R1`;
- regla de una sola pulsación;
- criterio de corrida no evaluable;
- límite de reemplazos;
- obligación de usar un identificador nuevo;
- conservación de la corrida original.

Tampoco es necesario repetir:

- BASE-R1;
- BASE-R2;
- BASE-R3;
- el cálculo de `M_antes`;
- la implementación de `connectTimeout = 3 segundos`;
- TIMEOUT3-R1 como si nunca hubiera ocurrido.

La evidencia de TIMEOUT3-R1 permanece conservada.

---

## 24.1 Siguiente ejecución

El siguiente paso es:

`TIMEOUT3-R1-REP1`

Identificador:

`S11-A4-TIMEOUT3-R1-REP1`

Esta ejecución corresponde a:

`LA ÚNICA REPETICIÓN DE REEMPLAZO DISPONIBLE EN LA CONDICIÓN POSTERIOR`

---

## 24.2 Verificaciones antes del reemplazo

Antes de inducir la indisponibilidad se deberá comprobar:

1. Spring Boot disponible;
2. PostgreSQL disponible;
3. comunicación Android-backend funcional;
4. usuario autenticado;
5. aplicación Android abierta;
6. captura de Logcat activa;
7. identificador `S11-A4-TIMEOUT3-R1-REP1` correctamente ingresado;
8. ausencia de reutilización de identificadores anteriores;
9. configuración `connectTimeout = 3 segundos` sin cambios;
10. ausencia de modificaciones experimentales adicionales.

---

## 24.3 Ejecución del reemplazo

Después de comprobar el estado inicial:

1. realizar el control previo si corresponde;
2. confirmar que el control terminó;
3. detener Spring Boot;
4. comprobar el puerto `8080`;
5. conservar la evidencia de `0 listeners`;
6. comprobar nuevamente el identificador experimental;
7. pulsar `Guardar` exactamente una vez;
8. no pulsar nuevamente `Guardar`;
9. no iniciar voluntariamente otra operación remota durante la ventana;
10. esperar el resultado;
11. conservar el `--> POST`;
12. conservar el resultado correspondiente;
13. calcular la duración técnica;
14. registrar el resultado visible en Android;
15. comprobar PostgreSQL;
16. registrar el número de coincidencias;
17. registrar cualquier incidencia;
18. calcular el SHA-256 del Logcat privado;
19. clasificar la corrida antes de utilizar su valor.

---

## 24.4 Elementos que no deben modificarse

Durante el reemplazo no se deberá modificar:

- `connectTimeout`;
- `readTimeout`;
- `writeTimeout`;
- `callTimeout`;
- retry;
- fallback;
- circuit breaker;
- interceptores;
- prerregistro;
- criterios de evaluabilidad;
- número de corridas;
- regla de reemplazo.

No se deberá ejecutar:

- un segundo `Guardar`;
- un segundo POST experimental;
- una repetición adicional si este reemplazo también resulta no evaluable.

---

## 24.5 Tratamiento del reemplazo

Si TIMEOUT3-R1-REP1 resulta evaluable:

`SU DURACIÓN PODRÁ ASIGNARSE COMO T1`

TIMEOUT3-R1 original continuará registrada como:

`NO EVALUABLE`

Si TIMEOUT3-R1-REP1 también resulta no evaluable:

`NO SE EJECUTARÁ OTRO REEMPLAZO EN LA CONDICIÓN POSTERIOR`

porque el máximo establecido es:

`1`

---

## 24.6 Corridas posteriores restantes

Después del reemplazo deberán ejecutarse:

- `TIMEOUT3-R2`
- `TIMEOUT3-R3`

utilizando respectivamente:

- `S11-A4-TIMEOUT3-R2`
- `S11-A4-TIMEOUT3-R3`

Cada una deberá cumplir el mismo protocolo.

---

## 24.7 Condición para calcular la mediana posterior

Solo cuando existan tres valores posteriores evaluables podrá calcularse:

\[
M_{después}
\]

Los tres valores deberán provenir de corridas admitidas según los criterios definidos previamente.

---

## 24.8 Contraste cuantitativo final

Una vez obtenida:

\[
M_{después}
\]

se calculará:

\[
Reducción(\%) =
\frac{M_{antes} - M_{después}}
{M_{antes}}
\times 100
\]

utilizando:

`M_antes = 10 028 ms`

El resultado se comparará con:

`REDUCCIÓN >= 50 %`

---

## 24.9 Estado hasta completar la condición posterior

Hasta contar con tres corridas posteriores evaluables:

`HIPÓTESIS = NO EVALUADA`

`M_después = NO CALCULADA`

`REDUCCIÓN = NO CALCULADA`

`COMPARACIÓN PRINCIPAL = ABIERTA`

La clasificación:

`NO EVALUABLE`

permanece asociada exclusivamente a:

`TIMEOUT3-R1`