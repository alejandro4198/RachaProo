# Semana 11 — Actividad 4
## Resultados del experimento de timeout

Este documento registra los resultados observados durante la ejecución del experimento preregistrado en:

`prerregistro-spike-timeout.md`

El prerregistro quedó registrado previamente mediante el commit:

`94dcc32`

Los resultados consignados aquí son posteriores al prerregistro.

La versión histórica del prerregistro utilizada para contrastar las decisiones metodológicas fue consultada directamente mediante:

```powershell
git show 94dcc32:experimentos/semana11-crecimiento-post-activities/prerregistro-spike-timeout.md
```

La comprobación histórica confirmó que las reglas relevantes para la línea base, la condición posterior, las corridas no evaluables y los reemplazos ya se encontraban definidas antes de ejecutar las mediciones.

No se modifican retroactivamente:

- la hipótesis;
- el umbral del 50 %;
- el número de corridas principales;
- la métrica principal;
- los identificadores previstos;
- los criterios de evaluabilidad;
- las reglas para corridas no evaluables;
- las reglas de reemplazo;
- la intervención prevista.

Cuando corresponde, este documento distingue explícitamente entre:

- hechos instrumentales observados;
- confirmaciones operativas;
- inferencias;
- incidencias;
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

Configuración:

`connectTimeout = 3 segundos`

Commit de intervención:

`a722954`

TIMEOUT3-R1:

`EJECUTADA`

Clasificación metodológica:

`NO EVALUABLE`

Commit documental de TIMEOUT3-R1:

`cc68828`

Valor T1:

`NO ASIGNADO`

Mediciones técnicas observadas durante TIMEOUT3-R1:

- `3 012 ms`
- `3 015 ms`

Tratamiento de dichas mediciones:

`DESCRIPTIVAS — NO INCORPORADAS A LA COMPARACIÓN PRINCIPAL`

Motivo de no evaluabilidad:

`EL PROTOCOLO DE LA CORRIDA NO PUDO CUMPLIRSE SUFICIENTEMENTE`

Incidencia principal:

`SE REALIZARON DOS INTENTOS POST DE CREACIÓN DURANTE LA MISMA CORRIDA`

Regla histórica aplicable:

`UNA SOLA PULSACIÓN DE GUARDAR`

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

Resultado global del experimento:

`PENDIENTE`

Conclusión arquitectónica:

`PENDIENTE`

---

# 2. BASE-R1

Estado:

`EVALUABLE CON DESVIACIÓN DOCUMENTADA`

Valor admitido:

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

No estaba implementado todavía:

`connectTimeout = 3 segundos`

---

## 2.2 Control previo

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

La duración del control no forma parte de la métrica principal.

El control se conserva como evidencia de que la creación remota funcionaba antes de inducir la indisponibilidad.

---

## 2.3 Inducción del fallo

Después del control previo se detuvo Spring Boot.

Se comprobó que el puerto:

`8080`

no tenía listener.

Hora de comprobación registrada:

`16:53:43`

La comprobación ocurrió antes de ejecutar el POST experimental.

PostgreSQL no fue detenido intencionalmente.

---

## 2.4 POST experimental

Identificador utilizado:

`S11-A4-BASE-R1`

Inicio:

`16:54:24.825`

Hilo:

`6163`

Tamaño:

`103 bytes`

Resultado:

`SocketTimeoutException`

Fin:

`16:54:34.853`

Mensaje registrado:

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

Resultado funcional:

`ERROR MOSTRADO — SIN FALSO ÉXITO OBSERVADO`

---

## 2.7 Persistencia remota

Consulta por:

`S11-A4-BASE-R1`

Resultado:

`1 fila`

ID observado:

`1500178`

La reutilización del mismo identificador para el control y el experimento reduce la capacidad de atribuir únicamente mediante el título cuál operación originó la fila.

La combinación de:

- control con `HTTP 201`;
- POST experimental fallido;
- una única fila;

es consistente con que la fila corresponda al control.

Esta interpretación se conserva como:

`INFERENCIA APOYADA POR EVIDENCIA CONJUNTA`

y no como:

`IDENTIFICACIÓN INEQUÍVOCA DEL BODY DEL POST`

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

para control y experimento.

Esto reduce la trazabilidad funcional en PostgreSQL.

No obstante, las solicitudes HTTP permanecen técnicamente distinguibles.

---

## 2.10 Decisión metodológica BASE-R1

Clasificación:

`EVALUABLE CON DESVIACIÓN DOCUMENTADA`

Participación en mediana:

`SÍ`

Valor:

`B1 = 10 028 ms`

Reemplazo:

`NO REQUERIDO`

---

## 2.11 Integridad BASE-R1

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

## 3.1 Incidencia previa de autenticación

Se registró inicialmente:

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

## 3.2 Control válido posterior

Identificador:

`S11-A4-BASE-R2-CONTROL`

Inicio:

`17:57:19.284`

Resultado:

`HTTP 201`

Fin:

`17:57:19.544`

---

## 3.3 Secuencia reconstruida

La secuencia fue:

1. POST con `HTTP 401`;
2. login con `HTTP 200`;
3. control con `HTTP 201`;
4. detención de Spring Boot;
5. comprobación operativa del puerto;
6. POST experimental.

---

## 3.4 Limitación documental

La comprobación del puerto antes del POST experimental fue confirmada operativamente.

No se conservó:

`UNA SALIDA POWERSHELL CON TIMESTAMP EXACTO DE ESA COMPROBACIÓN`

---

## 3.5 POST experimental

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

## 3.6 Duración

Cálculo:

`45.224 - 35.191 = 10.033 segundos`

Valor:

`B2 = 10 033 ms`

---

## 3.7 Resultado Android

Android mostró:

`No fue posible crear la actividad`

Falso éxito:

`NO OBSERVADO`

---

## 3.8 Persistencia

Consulta:

`S11-A4-BASE-R2`

Resultado:

`0 FILAS`

---

## 3.9 Guardado experimental

Se confirmó operativamente:

`GUARDAR UNA SOLA VEZ`

para:

`S11-A4-BASE-R2`

---

## 3.10 Decisión metodológica BASE-R2

La incidencia de autenticación ocurrió antes de:

- login exitoso;
- control válido;
- inducción del fallo;
- POST experimental.

Clasificación:

`EVALUABLE CON INCIDENCIA PREVIA RESUELTA`

Participación:

`SÍ`

Valor:

`B2 = 10 033 ms`

Reemplazo:

`NO REQUERIDO`

---

## 3.11 Integridad BASE-R2

Archivo privado:

`evidencia-timeout/BASE-R2/logcat-BASE-R2-privado.txt`

SHA-256:

`2BF5FDDEA23BF2D4644A616BF46DE9A1B9172EA00091E30348FA5CB571F7F907`

---

# 4. BASE-R3

Estado:

`EVALUABLE CON INCIDENCIA DOCUMENTADA`

Valor:

`B3 = 10 023 ms`

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

## 4.1 Control previo

Inicio:

`19:10:45.065`

Resultado:

`HTTP 201`

Fin:

`19:10:45.127`

Duración:

`60 ms`

---

## 4.2 Incidencia del control

Consulta PostgreSQL:

`S11-A4-BASE-R3-CONTROL`

Resultado:

`2 filas`

IDs:

- `1500180`
- `1500181`

Procedencia exacta de ambas:

`NO DETERMINADA`

No se afirma:

- que ambas hayan sido generadas por una única acción;
- que una de ellas fuera necesariamente preexistente.

La incidencia se conserva como:

`ANOMALÍA DE TRAZABILIDAD DEL CONTROL`

---

## 4.3 Independencia nominal del experimento

Identificador experimental:

`S11-A4-BASE-R3`

Consulta:

`0 FILAS`

No existe evidencia que demuestre que la incidencia del control haya:

- generado un segundo POST experimental;
- cambiado el identificador experimental;
- generado persistencia experimental;
- modificado la duración;
- generado falso éxito Android.

---

## 4.4 Comprobación de indisponibilidad

Puerto:

`8080`

Hora:

`19:11:54.078`

Listeners:

`0`

POST experimental:

`19:12:23.162`

La comprobación ocurrió antes del POST.

---

## 4.5 POST experimental

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

---

## 4.6 Duración

Cálculo:

`33.185 - 23.162 = 10.023 segundos`

Valor:

`B3 = 10 023 ms`

---

## 4.7 Resultado Android

Android mostró:

`No fue posible crear la actividad`

Falso éxito:

`NO OBSERVADO`

---

## 4.8 Persistencia experimental

Consulta:

`S11-A4-BASE-R3`

Resultado:

`0 FILAS`

---

## 4.9 Guardado experimental

Se confirmó operativamente:

`GUARDAR UNA SOLA VEZ`

---

## 4.10 Decisión metodológica BASE-R3

La incidencia del control permanece registrada.

No se demostró que hubiera alterado el POST experimental.

Clasificación:

`EVALUABLE CON INCIDENCIA DOCUMENTADA`

Participación:

`SÍ`

Valor:

`B3 = 10 023 ms`

Reemplazo:

`NO REQUERIDO`

---

## 4.11 Integridad BASE-R3

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

Mediana de línea base:

`M_antes = 10 028 ms`

Corridas ejecutadas:

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

Commit:

`a722954`

Configuración agregada:

```kotlin
.connectTimeout(3, TimeUnit.SECONDS)
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

La intervención se registró antes de ejecutar las mediciones posteriores.

---

# 7. Condición posterior a la intervención

El prerregistro histórico `94dcc32` establece:

`3 corridas principales`

para la condición posterior.

Identificadores confirmados literalmente en el prerregistro histórico:

- `S11-A4-TIMEOUT3-R1`
- `S11-A4-TIMEOUT3-R2`
- `S11-A4-TIMEOUT3-R3`

Estado actual:

- `TIMEOUT3-R1 = EJECUTADA — NO EVALUABLE`
- `TIMEOUT3-R2 = NO EJECUTADA`
- `TIMEOUT3-R3 = NO EJECUTADA`

Valores posteriores evaluables consolidados:

`0 de 3`

---

# 8. TIMEOUT3-R1

Estado:

`EJECUTADA`

Identificador preregistrado:

`S11-A4-TIMEOUT3-R1`

Presencia literal del identificador en `94dcc32`:

`CONFIRMADA`

Commit de intervención ejecutado:

`a722954`

Commit documental posterior:

`cc68828`

Configuración:

`connectTimeout = 3 segundos`

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

La comprobación se realizó antes de los POST fallidos posteriores.

---

## 8.2 POST fallido 1

Inicio:

`20:32:49.240`

Fin:

`20:32:52.252`

Cálculo:

`52.252 - 49.240 = 3.012 segundos`

Duración observada:

`3 012 ms`

---

## 8.3 POST fallido 2

Inicio:

`20:33:40.536`

Fin:

`20:33:43.551`

Cálculo:

`43.551 - 40.536 = 3.015 segundos`

Duración observada:

`3 015 ms`

---

## 8.4 Naturaleza de las dos mediciones

Los dos POST poseen pares inicio-fallo técnicamente distinguibles.

Se observaron por tanto dos duraciones calculables:

- `3 012 ms`
- `3 015 ms`

Estas duraciones se registran como:

`HECHOS TÉCNICOS OBSERVADOS`

No se asigna automáticamente ninguna de ellas a:

`T1`

La decisión de evaluabilidad se registra separadamente.

---

## 8.5 Identificadores utilizados

El prerregistro histórico `94dcc32` contiene literalmente el identificador:

`S11-A4-TIMEOUT3-R1`

para la primera corrida principal posterior a la intervención.

Durante la ejecución, según confirmación operativa retrospectiva, se utilizaron dos títulos, en este orden:

1. `S11-A4-TIMEOUT-R1`
2. `S11-A4-TIMEOUT3-R1`

Por tanto:

- el primer intento utilizó un identificador diferente del preregistrado;
- el segundo intento utilizó el identificador preregistrado.

Esta secuencia se clasifica como:

`CONFIRMACIÓN OPERATIVA RETROSPECTIVA`

El Logcat BASIC no muestra el título funcional contenido en el body.

Por ello, la asociación individual título-POST no se presenta como identificación instrumental derivada únicamente del Logcat.

---

## 8.6 Limitación del Logcat BASIC

El nivel:

`HttpLoggingInterceptor.Level.BASIC`

no expone el body funcional completo.

Por tanto, Logcat BASIC no permite demostrar por sí solo qué título estaba contenido en cada POST.

La secuencia de títulos procede de la confirmación operativa de la ejecución.

---

## 8.7 Operación de las 20:32:25.078

También se observó un fallo a las:

`20:32:25.078`

La revisión posterior permitió identificar que correspondía a:

`PATCH`

y no a:

`POST /api/activities`

Por tanto:

`NO SE CONTABILIZA COMO UN TERCER POST DE CREACIÓN`

No participa en la métrica principal.

No se le atribuye un inicio no observado.

---

## 8.8 Resultado Android

Android mostró:

`No fue posible crear la actividad`

Falso éxito:

`NO OBSERVADO`

---

## 8.9 Persistencia remota

Consulta PostgreSQL:

`S11-A4-TIMEOUT3-R1`

Resultado:

`0 FILAS`

Interpretación:

`NO SE OBSERVÓ PERSISTENCIA REMOTA BAJO EL IDENTIFICADOR EXPERIMENTAL EN LA CONSULTA REALIZADA`

La ausencia de persistencia no modifica por sí sola la decisión de evaluabilidad.

---

## 8.10 Integridad de evidencia

SHA-256 del Logcat privado:

`787BB5935347EABAD2C281765E715C5F247BEA3FB626DEF21768273BCCC9AC81`

Estado:

`EVIDENCIA PRIVADA CONSERVADA`

El Logcat completo permanece fuera del repositorio público.

---

# 9. Evaluabilidad de TIMEOUT3-R1

## 9.1 Hechos técnicos observados

Se observó:

- puerto `8080` con `0 listeners`;
- primer POST fallido de `3 012 ms`;
- segundo POST fallido de `3 015 ms`;
- Android mostrando error;
- ausencia de falso éxito;
- `0 filas` para `S11-A4-TIMEOUT3-R1`;
- un PATCH separado;
- SHA-256 del Logcat conservado.

Estos hechos permanecen registrados independientemente de la clasificación metodológica.

---

## 9.2 Desviación del procedimiento

El prerregistro histórico establece para la condición posterior:

- utilizar el identificador correspondiente;
- pulsar `Guardar` exactamente una vez;
- identificar el `--> POST`;
- identificar el resultado correspondiente;
- comprobar PostgreSQL después del intento;
- registrar desviaciones.

También establece expresamente:

`S11-A4-TIMEOUT3-R1`

como identificador de la primera corrida posterior.

Durante TIMEOUT3-R1 se realizaron dos intentos POST de creación.

Según la confirmación operativa:

- el primero utilizó `S11-A4-TIMEOUT-R1`;
- el segundo utilizó `S11-A4-TIMEOUT3-R1`.

La ejecución no reprodujo, por tanto, el protocolo de una única operación experimental con el identificador correspondiente.

---

## 9.3 Regla histórica de corrida no evaluable

La versión histórica `94dcc32` contiene expresamente la sección:

`Corridas no evaluables`

y establece que una corrida puede clasificarse:

`NO EVALUABLE`

cuando exista evidencia de que:

`EL PROTOCOLO NO PUDO CUMPLIRSE SUFICIENTEMENTE`

Entre los ejemplos definidos previamente aparece explícitamente:

`HUBO MÁS DE UNA PULSACIÓN DE GUARDAR`

También se incluye:

`EXISTIERON SOLICITUDES CONCURRENTES IMPOSIBLES DE CORRELACIONAR`

En TIMEOUT3-R1 los pares inicio-fallo de los dos POST sí son técnicamente distinguibles.

Por tanto, la clasificación no se fundamenta en afirmar que:

`LOS POST SON IMPOSIBLES DE CORRELACIONAR TEMPORALMENTE`

La regla específica aplicada es:

`INCUMPLIMIENTO SUFICIENTE DEL PROTOCOLO POR MÁS DE UNA OPERACIÓN DE GUARDADO DURANTE LA MISMA CORRIDA`

---

## 9.4 Verificación histórica

Estado:

`COMPLETADA`

La consulta directa de:

`94dcc32`

confirmó históricamente, antes de las mediciones:

- existencia de la categoría `NO EVALUABLE`;
- criterio general de protocolo no cumplido suficientemente;
- una sola pulsación de `Guardar`;
- más de una pulsación como ejemplo de no evaluabilidad;
- identificador `S11-A4-TIMEOUT3-R1`;
- máximo un reemplazo por condición;
- identificador nuevo para el reemplazo;
- conservación de la corrida original;
- obligación de seguir el mismo protocolo en el reemplazo.

Los caracteres dañados observados en la salida de PowerShell, por ejemplo:

`├¡`

o:

`├│`

corresponden a la visualización/codificación de la salida y no se interpretan como evidencia de corrupción del archivo histórico.

---

## 9.5 Decisión metodológica final

Resultados técnicos observados:

- `3 012 ms`
- `3 015 ms`

Decisión metodológica:

`TIMEOUT3-R1 = NO EVALUABLE`

Razón:

`EL PROTOCOLO NO PUDO CUMPLIRSE SUFICIENTEMENTE`

Criterio histórico aplicado:

`MÁS DE UNA PULSACIÓN DE GUARDAR DURANTE LA MISMA CORRIDA`

Incidencia adicional:

`EL PRIMER INTENTO UTILIZÓ UN IDENTIFICADOR DISTINTO DE S11-A4-TIMEOUT3-R1`

Valor T1:

`NO ASIGNADO`

Los valores:

- `3 012 ms`;
- `3 015 ms`;

permanecen registrados como:

`OBSERVACIONES DESCRIPTIVAS`

y no participan en la comparación principal.

---

## 9.6 Consecuencias de la clasificación

TIMEOUT3-R1:

- permanece registrada;
- conserva sus mediciones;
- conserva su Logcat;
- conserva su SHA-256;
- conserva su motivo de no evaluabilidad;
- no cuenta como respaldo de la hipótesis;
- no cuenta como refutación de la hipótesis;
- no aporta T1.

La clasificación:

`NO EVALUABLE`

no significa que:

- `connectTimeout = 3 segundos` haya fallado;
- la intervención sea ineficaz;
- la hipótesis esté respaldada;
- la hipótesis esté refutada;
- las mediciones observadas sean falsas.

Significa únicamente que:

`LA CORRIDA NO PARTICIPA EN LA COMPARACIÓN PRINCIPAL`

---

# 10. Regla de reemplazo

## 10.1 Regla histórica

La versión histórica `94dcc32` establece:

`MÁXIMO 1 REPETICIÓN DE REEMPLAZO POR CONDICIÓN`

y especifica que procede cuando una corrida ha sido previamente clasificada:

`NO EVALUABLE`

El reemplazo:

- solo puede realizarse por una causa metodológica documentada;
- no puede justificarse por el valor temporal obtenido;
- debe conservar la evidencia de la corrida original;
- debe utilizar un identificador nuevo;
- debe seguir exactamente el mismo protocolo;
- debe registrarse explícitamente como reemplazo.

También establece que la corrida original:

`NO SERÁ ELIMINADA NI SUSTITUIDA DEL REGISTRO HISTÓRICO`

---

## 10.2 Verificación histórica de la regla de reemplazo

Estado:

`COMPLETADA`

La regla fue encontrada directamente en:

`94dcc32`

antes de la ejecución de TIMEOUT3-R1.

Por tanto, ya no se trata de una regla inferida de una versión posterior del documento.

TIMEOUT3-R1 cumple la condición previa necesaria:

`CORRIDA CLASIFICADA COMO NO EVALUABLE`

La causa:

`ES METODOLÓGICA`

y no depende de que las duraciones observadas hayan sido:

- `3 012 ms`;
- `3 015 ms`.

---

## 10.3 Estado del reemplazo

Condición:

`POSTERIOR A LA INTERVENCIÓN`

Reemplazos permitidos:

`1`

Reemplazos ejecutados:

`0`

Reemplazo disponible:

`SÍ`

Estado:

`PENDIENTE DE EJECUCIÓN`

---

## 10.4 Identificador del reemplazo

La regla histórica exige utilizar:

`UN IDENTIFICADOR NUEVO`

y no reutilizar el identificador original.

Para el reemplazo de TIMEOUT3-R1 se utilizará:

`S11-A4-TIMEOUT3-R1-REP1`

Este identificador sigue el esquema de reemplazo definido por el prerregistro.

No deberán reutilizarse:

- `S11-A4-TIMEOUT-R1`;
- `S11-A4-TIMEOUT3-R1`.

---

## 10.5 Conservación de TIMEOUT3-R1

El reemplazo no elimina:

`TIMEOUT3-R1`

La corrida original permanecerá documentada como:

`NO EVALUABLE`

junto con:

- sus dos mediciones;
- la incidencia;
- el resultado Android;
- PostgreSQL;
- su SHA-256;
- su commit documental.

---

# 11. TIMEOUT3-R2

Estado:

`NO EJECUTADA`

Identificador preregistrado:

`S11-A4-TIMEOUT3-R2`

Valor:

`T2 = PENDIENTE`

---

# 12. TIMEOUT3-R3

Estado:

`NO EJECUTADA`

Identificador preregistrado:

`S11-A4-TIMEOUT3-R3`

Valor:

`T3 = PENDIENTE`

---

# 13. Estado de los valores posteriores

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

Valores posteriores evaluables consolidados:

`0 de 3`

---

# 14. Mediana posterior

La versión histórica del prerregistro exige:

`3 CORRIDAS EVALUABLES DESPUÉS DE LA INTERVENCIÓN`

Estado actual:

`0 de 3 consolidadas`

Por tanto:

`M_después = NO CALCULADA`

Las observaciones:

- `3 012 ms`;
- `3 015 ms`;

no se utilizan para calcular la mediana posterior.

---

# 15. Reducción porcentual

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

No se calculará anticipadamente una reducción utilizando las observaciones de la corrida no evaluable.

---

# 16. Estado de la hipótesis

Hipótesis preregistrada:

> Ante una indisponibilidad controlada de Spring Boot, configurar explícitamente `connectTimeout = 3 segundos` reducirá al menos un 50 % la mediana de la duración técnica de los `POST` fallidos de creación de actividad respecto de una nueva línea base ejecutada bajo el mismo protocolo, sin que Android presente falsamente la operación como exitosa ni se observe persistencia remota inesperada de la actividad intentada durante el fallo.

Estado:

`NO EVALUADA`

TIMEOUT3-R1:

`NO CUENTA COMO RESPALDO`

TIMEOUT3-R1:

`NO CUENTA COMO REFUTACIÓN`

Todavía no existen tres corridas posteriores evaluables.

Por tanto, no corresponde declarar:

`HIPÓTESIS RESPALDADA`

ni:

`HIPÓTESIS NO RESPALDADA`

---

# 17. Diferencia entre corrida no evaluable y comparación principal no evaluable

TIMEOUT3-R1 está clasificada:

`NO EVALUABLE`

Esta clasificación corresponde exclusivamente a:

`UNA CORRIDA`

No significa que el experimento completo sea:

`COMPARACIÓN PRINCIPAL NO EVALUABLE`

La versión histórica establece que el resultado global solo será clasificado:

`COMPARACIÓN PRINCIPAL NO EVALUABLE`

si, después de utilizar cuando corresponda el único reemplazo permitido por condición, no se obtienen tres corridas evaluables en ambas condiciones.

Ese escenario:

`NO HA OCURRIDO`

porque:

- la línea base ya dispone de tres corridas evaluables;
- el reemplazo posterior todavía está disponible;
- TIMEOUT3-R2 no ha sido ejecutada;
- TIMEOUT3-R3 no ha sido ejecutada.

Por tanto:

`LA COMPARACIÓN PRINCIPAL CONTINÚA ABIERTA`

---

# 18. Diferencia entre no respaldo y comparación no evaluable

Si existen:

- tres corridas evaluables antes;
- tres corridas evaluables después;

y la reducción final resulta menor al 50 %, corresponderá:

`HIPÓTESIS NO RESPALDADA`

En cambio, si finalmente no se reúnen tres corridas evaluables por condición después de aplicar las reglas permitidas, corresponderá:

`COMPARACIÓN PRINCIPAL NO EVALUABLE`

Estas categorías:

`NO SON EQUIVALENTES`

Estado actual:

`NINGUNA DE LAS DOS CORRESPONDE TODAVÍA`

---

# 19. Evidencia privada

Los Logcat completos permanecen fuera del repositorio público.

## 19.1 BASE-R1

Archivo:

`evidencia-timeout/BASE-R1/logcat-BASE-R1-privado.txt`

SHA-256:

`DE5066FCF6BA7B86E1BD12FCA3041AD77513C38499E25578EB92DD27DA6E1AE5`

---

## 19.2 BASE-R2

Archivo:

`evidencia-timeout/BASE-R2/logcat-BASE-R2-privado.txt`

SHA-256:

`2BF5FDDEA23BF2D4644A616BF46DE9A1B9172EA00091E30348FA5CB571F7F907`

---

## 19.3 BASE-R3

Archivo:

`evidencia-timeout/BASE-R3/logcat-BASE-R3-privado.txt`

SHA-256:

`6508512032222FCF374F0C8998018DB01E8D466734F0B2516F57128B1894D5FF`

---

## 19.4 TIMEOUT3-R1

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

# 20. Trazabilidad Git

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

Commit documental de TIMEOUT3-R1:

`cc68828`

Secuencia consolidada:

`prerregistro -> línea base -> cierre de línea base -> intervención -> TIMEOUT3-R1 -> clasificación NO EVALUABLE -> verificación histórica -> reemplazo pendiente`

No se modifica retroactivamente:

- `94dcc32`;
- los commits históricos;
- las mediciones BASE;
- `M_antes`;
- la intervención;
- los valores observados de TIMEOUT3-R1.

---

# 21. Verificación histórica del prerregistro

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

La salida confirmó que la versión histórica contenía, antes de las mediciones:

### Corridas posteriores

`3 corridas principales`

### Identificadores

- `S11-A4-TIMEOUT3-R1`
- `S11-A4-TIMEOUT3-R2`
- `S11-A4-TIMEOUT3-R3`

### Ejecución

`UTILIZAR EL IDENTIFICADOR CORRESPONDIENTE`

y:

`PULSAR GUARDAR EXACTAMENTE UNA VEZ`

### Corridas no evaluables

Una corrida puede clasificarse:

`NO EVALUABLE`

cuando:

`EL PROTOCOLO NO PUDO CUMPLIRSE SUFICIENTEMENTE`

Entre los ejemplos definidos se encontraba:

`HUBO MÁS DE UNA PULSACIÓN DE GUARDAR`

### Reemplazos

Máximo:

`1 REPETICIÓN DE REEMPLAZO POR CONDICIÓN`

aplicable ante una corrida previamente clasificada:

`NO EVALUABLE`

La repetición debe:

- responder a una causa metodológica documentada;
- no justificarse por el valor temporal;
- conservar la evidencia original;
- usar un identificador nuevo;
- seguir exactamente el mismo protocolo;
- registrarse explícitamente como reemplazo.

### Conservación histórica

La corrida original no evaluable:

`NO SERÁ ELIMINADA NI SUSTITUIDA DEL REGISTRO HISTÓRICO`

### Condición para calcular medianas

Deben existir:

`3 CORRIDAS EVALUABLES EN LÍNEA BASE`

y:

`3 CORRIDAS EVALUABLES DESPUÉS DE LA INTERVENCIÓN`

### Resultado global no evaluable

Solo si, después de utilizar cuando corresponda el único reemplazo permitido, todavía no existen tres corridas evaluables por condición, corresponde:

`COMPARACIÓN PRINCIPAL NO EVALUABLE`

La verificación histórica queda:

`CERRADA`

---

# 22. Estado consolidado de la Actividad 4

## Línea base

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

---

## Intervención

Configuración:

`connectTimeout = 3 segundos`

Commit:

`a722954`

Estado:

`IMPLEMENTADA`

---

## TIMEOUT3-R1

Estado:

`EJECUTADA — NO EVALUABLE`

Identificador preregistrado:

`S11-A4-TIMEOUT3-R1`

Medición 1:

`3 012 ms`

Medición 2:

`3 015 ms`

T1:

`NO ASIGNADO`

Motivo:

`PROTOCOLO NO CUMPLIDO SUFICIENTEMENTE`

Criterio histórico específico:

`MÁS DE UNA PULSACIÓN DE GUARDAR`

Android:

`ERROR MOSTRADO`

Falso éxito:

`NO OBSERVADO`

Persistencia:

`0 FILAS`

PATCH de las:

`20:32:25.078`

Clasificación:

`NO CORRESPONDE A UN TERCER POST DE CREACIÓN`

SHA-256:

`787BB5935347EABAD2C281765E715C5F247BEA3FB626DEF21768273BCCC9AC81`

Commit documental:

`cc68828`

---

## Reemplazo posterior

Permitido:

`SÍ`

Máximo:

`1`

Utilizados:

`0`

Identificador previsto:

`S11-A4-TIMEOUT3-R1-REP1`

Estado:

`PENDIENTE DE EJECUCIÓN`

---

## TIMEOUT3-R2

Estado:

`NO EJECUTADA`

---

## TIMEOUT3-R3

Estado:

`NO EJECUTADA`

---

## Resultado posterior

M_después:

`NO CALCULADA`

Reducción:

`NO CALCULADA`

Hipótesis:

`NO EVALUADA`

Comparación principal:

`ABIERTA`

---

# 23. Próximo punto de control

La verificación histórica:

`YA ESTÁ COMPLETADA`

Por tanto, no es necesario volver a consultar el prerregistro para resolver:

- existencia de `S11-A4-TIMEOUT3-R1`;
- regla de una sola pulsación;
- clasificación de corrida no evaluable;
- límite de reemplazos;
- uso de identificador nuevo;
- conservación de la corrida original.

El siguiente paso experimental permitido es:

`TIMEOUT3-R1-REP1`

Identificador:

`S11-A4-TIMEOUT3-R1-REP1`

Antes de ejecutarlo deberá verificarse:

1. Spring Boot disponible inicialmente;
2. PostgreSQL disponible;
3. comunicación Android-backend funcional;
4. usuario autenticado;
5. aplicación Android abierta;
6. captura de Logcat activa;
7. identificador `S11-A4-TIMEOUT3-R1-REP1` correctamente escrito;
8. que no se reutiliza ninguno de los identificadores de TIMEOUT3-R1;
9. que la configuración continúa siendo `connectTimeout = 3 segundos`.

Después:

1. realizar el control previo si corresponde;
2. detener Spring Boot;
3. comprobar que el puerto `8080` no tiene servicio;
4. verificar nuevamente el identificador;
5. pulsar `Guardar` exactamente una vez;
6. no realizar otro POST voluntario;
7. esperar a que la solicitud termine;
8. conservar `--> POST`;
9. conservar `HTTP FAILED`;
10. calcular la duración;
11. registrar el resultado Android;
12. comprobar PostgreSQL;
13. registrar el número de coincidencias;
14. registrar cualquier desviación;
15. calcular el SHA-256 del Logcat privado;
16. clasificar la corrida antes de incorporar su valor a la comparación.

No deberá realizarse:

- un segundo `Guardar`;
- un segundo POST de creación;
- reutilización de identificadores;
- cambio en `connectTimeout`;
- cambio en `readTimeout`;
- cambio en `writeTimeout`;
- cambio en `callTimeout`;
- incorporación de retry;
- incorporación de fallback;
- incorporación de circuit breaker;
- modificación del prerregistro.

Si TIMEOUT3-R1-REP1 resulta evaluable:

`SU VALOR PODRÁ OCUPAR LA POSICIÓN T1`

La corrida original TIMEOUT3-R1 continuará registrada como:

`NO EVALUABLE`

Si TIMEOUT3-R1-REP1 también resulta no evaluable:

`NO SE EJECUTARÁ UNA SEGUNDA REPETICIÓN DE REEMPLAZO EN LA CONDICIÓN POSTERIOR`

Posteriormente deberán ejecutarse:

- `TIMEOUT3-R2`;
- `TIMEOUT3-R3`.

Solo cuando existan tres corridas posteriores evaluables podrá calcularse:

\[
M_{después}
\]

Después podrá calcularse:

\[
Reducción(\%) =
\frac{M_{antes} - M_{después}}
{M_{antes}}
\times 100
\]

utilizando:

`M_antes = 10 028 ms`

y contrastando el resultado con:

`REDUCCIÓN >= 50 %`

Hasta entonces:

`HIPÓTESIS = NO EVALUADA`

y:

`COMPARACIÓN PRINCIPAL = ABIERTA`