# Semana 11 — Actividad 4
## Prerregistro del experimento de timeout

## 1. Propósito

Este documento prerregistra el experimento correspondiente a la Actividad 4 de la Semana 11.

El objetivo es evaluar, mediante una comparación antes/después, el efecto de configurar explícitamente un `connectTimeout` en el cliente HTTP utilizado por RachaPro ante una indisponibilidad controlada del backend Spring Boot.

Este documento se registra antes de:

- ejecutar la nueva línea base;
- modificar `RetrofitClient.kt`;
- configurar el timeout experimental;
- ejecutar las mediciones posteriores;
- observar los resultados de la comparación.

Las decisiones aquí consignadas no deberán modificarse después de observar los resultados con el propósito de favorecer una conclusión.

---

## 2. Evidencia previa que motiva el experimento

Durante la Actividad 3 de Semana 11 se ejecutaron los escenarios F-01/R-01 de indisponibilidad temporal del backend.

En dichas pruebas se observaron, entre otros elementos:

- intentos de creación remota de actividades mientras Spring Boot estaba indisponible;
- errores de tipo `SocketTimeoutException`;
- tiempos cercanos a 10 segundos antes del fallo;
- ausencia de confirmación remota exitosa durante la indisponibilidad;
- recuperación posterior para nuevas operaciones una vez restablecido Spring Boot.

Estas observaciones constituyen antecedentes del experimento, pero las repeticiones F-01/R-01 anteriores no se utilizarán como la línea base cuantitativa definitiva de esta Actividad 4.

Se ejecutará una nueva línea base bajo el mismo protocolo definido específicamente para este experimento.

---

## 3. Inspección técnica previa

Antes de diseñar el experimento se inspeccionó la configuración HTTP vigente.

Archivo inspeccionado:

`app/src/main/java/com/example/rachapro/network/RetrofitClient.kt`

En dicho archivo se observó que el cliente se construye mediante:

`OkHttpClient.Builder()`

Además, en `RetrofitClient.kt` y en la búsqueda realizada sobre los archivos Kotlin y Java examinados no se encontraron configuraciones explícitas de:

- `connectTimeout`;
- `readTimeout`;
- `writeTimeout`;
- `callTimeout`.

Retrofit utiliza explícitamente ese cliente mediante:

`.client(httpClient)`

La resolución de dependencias de Gradle para:

`debugRuntimeClasspath`

confirmó:

- OkHttp: `4.12.0`
- Logging Interceptor: `4.12.0`
- Retrofit: `3.0.0`
- Converter Gson: `3.0.0`

La inspección técnica no demuestra que modificar un timeout mejore el comportamiento.

Ese efecto será evaluado experimentalmente.

---

## 4. Decisión experimental

La intervención seleccionada para este spike es configurar explícitamente:

`connectTimeout = 3 segundos`

mediante OkHttp.

La modificación prevista será equivalente a:

```kotlin
.connectTimeout(3, TimeUnit.SECONDS)
```

No se modificarán intencionalmente en este experimento:

- `readTimeout`;
- `writeTimeout`;
- `callTimeout`.

El experimento pretende introducir una sola intervención controlada sobre la configuración HTTP.

El valor de 3 segundos fue seleccionado antes de ejecutar las mediciones específicas de esta Actividad 4.

No se considera previamente demostrado que 3 segundos sea:

- el valor óptimo;
- el valor definitivo para producción;
- apropiado para cualquier red;
- suficiente para todos los escenarios de fallo.

Su efecto será evaluado mediante el experimento.

---

## 5. Fenómeno estudiado

El fenómeno evaluado será:

> La duración técnica de una solicitud `POST` de creación de actividad cuando el cliente Android intenta establecer comunicación con Spring Boot mientras el backend está intencionalmente indisponible.

La operación funcional utilizada será la creación remota de una actividad.

Este experimento no pretende medir directamente:

- tiempo total percibido por el usuario;
- capacidad máxima del backend;
- throughput;
- comportamiento bajo carga elevada;
- recuperación automática de solicitudes;
- disponibilidad global del sistema;
- fallos de PostgreSQL;
- fallos prolongados de red;
- otros tipos de resiliencia.

---

## 6. Variable independiente

La variable experimental será:

`configuración de connectTimeout`

Se compararán dos condiciones.

### 6.1 Condición A — línea base

Configuración actual de RachaPro:

`sin connectTimeout configurado explícitamente por la aplicación en el cliente inspeccionado`

Número de corridas principales previstas:

`3`

### 6.2 Condición B — intervención

Configuración experimental:

`connectTimeout = 3 segundos`

Número de corridas principales previstas:

`3`

Corridas experimentales principales previstas:

`6`

Repeticiones de reemplazo permitidas:

`máximo 1 por condición, únicamente ante una corrida previamente clasificada como NO EVALUABLE`

No se incrementará o reducirá el número de corridas principales después de observar los resultados con el propósito de favorecer una conclusión.

---

## 7. Variable dependiente

La métrica principal será:

`duración técnica del POST fallido`

### Punto inicial

Marca temporal correspondiente al inicio de la solicitud:

`--> POST`

### Punto final

Marca temporal correspondiente al fallo de esa misma solicitud:

`HTTP FAILED`

### Unidad

La duración se expresará preferiblemente en:

`milisegundos`

Si la evidencia disponible tiene otra resolución temporal, se conservarán también los timestamps originales utilizados para obtener el cálculo.

La métrica no representa directamente el tiempo total percibido por el usuario.

---

## 8. Instrumento de medición

La medición utilizará:

- Logcat;
- registros producidos por `HttpLoggingInterceptor`;
- información producida por Retrofit/OkHttp relacionada con la solicitud.

La configuración del mecanismo de logging permanecerá constante entre la línea base y la intervención.

No se aumentará o reducirá deliberadamente el nivel de logging como parte de la intervención experimental.

---

## 9. Correlación entre inicio y final de una solicitud

Un `--> POST` y un `HTTP FAILED` solamente podrán utilizarse para calcular una duración cuando exista evidencia suficiente para considerarlos correspondientes a la misma solicitud experimental.

No se realizará una resta de timestamps únicamente porque dos líneas aparezcan próximas temporalmente.

Para reducir ambigüedades:

1. cada corrida se realizará de manera secuencial;
2. no se iniciará la siguiente corrida hasta terminar el registro de la anterior;
3. cada actividad experimental tendrá un identificador único;
4. se evitarán otras operaciones remotas voluntarias durante la ventana medida;
5. se conservará el fragmento de Logcat alrededor de la solicitud;
6. cuando esté disponible, se conservará información sobre hilo, URL y contexto de la llamada;
7. se verificará que dentro de la ventana analizada exista una correspondencia razonable entre el inicio y el fallo;
8. el identificador de hilo podrá utilizarse como evidencia complementaria, pero no como única prueba de correlación.

Si existen varias solicitudes concurrentes y no es posible establecer de manera suficientemente clara cuál `HTTP FAILED` corresponde al `--> POST` medido, la corrida se clasificará como:

`NO EVALUABLE POR AMBIGÜEDAD DE TRAZABILIDAD`

La corrida no será eliminada silenciosamente.

---

## 10. Identificación de las corridas

Cada actividad experimental utilizará un identificador distinguible.

### Línea base

- `S11-A4-BASE-R1`
- `S11-A4-BASE-R2`
- `S11-A4-BASE-R3`

### Intervención

- `S11-A4-TIMEOUT3-R1`
- `S11-A4-TIMEOUT3-R2`
- `S11-A4-TIMEOUT3-R3`

Estos identificadores permitirán diferenciar las operaciones experimentales en Android y PostgreSQL.

No deberán reutilizarse entre corridas.

### Identificadores de reemplazo

Si una corrida resulta `NO EVALUABLE` y se utiliza la repetición de reemplazo permitida, deberá utilizarse un identificador nuevo.

Ejemplos:

- `S11-A4-BASE-R2-REP1`
- `S11-A4-TIMEOUT3-R3-REP1`

El identificador original no será reutilizado.

---

## 11. Condiciones a mantener constantes

En la medida técnicamente posible se mantendrán constantes entre las seis corridas principales:

- mismo dispositivo físico Android;
- misma versión de Android;
- misma red local;
- misma dirección del backend;
- mismo equipo que ejecuta Spring Boot;
- misma instancia o configuración de PostgreSQL;
- mismo usuario experimental;
- misma operación funcional;
- mismo endpoint;
- mismo mecanismo de captura;
- mismo procedimiento para detener Spring Boot;
- mismo procedimiento para restablecer Spring Boot;
- PostgreSQL disponible durante el fallo;
- ausencia intencional de carga artificial adicional;
- ausencia de retry añadido;
- ausencia de modificaciones de `readTimeout`;
- ausencia de modificaciones de `writeTimeout`;
- ausencia de modificaciones de `callTimeout`.

La única diferencia intencional entre las condiciones A y B será:

`configuración de connectTimeout`

Dentro de cada condición se utilizará la misma versión de aplicación correspondiente a esa condición.

Cualquier desviación deberá registrarse.

---

## 12. Estado inicial de cada corrida

Antes de cada corrida se deberá comprobar:

- Spring Boot disponible;
- PostgreSQL disponible;
- aplicación Android abierta;
- usuario autenticado;
- conectividad Android-backend funcional;
- flujo normal de creación de actividad funcional.

Se podrá realizar una actividad de control para comprobar el funcionamiento normal antes de inducir el fallo.

La operación de control no será contada como una de las tres corridas experimentales.

Después de la comprobación inicial y antes del POST medido:

1. Spring Boot será detenido;
2. se verificará su indisponibilidad;
3. se ejecutará la operación experimental.

Después de terminar la corrida:

1. se comprobará PostgreSQL;
2. se registrará el resultado;
3. se restablecerá Spring Boot;
4. se comprobará nuevamente su disponibilidad;
5. se restablecerán las condiciones iniciales antes de comenzar la siguiente corrida.

Este procedimiento deberá aplicarse de la misma manera en ambas condiciones.

---

## 13. Fallo inducido

El fallo experimental consistirá en:

`indisponibilidad intencional del proceso Spring Boot`

PostgreSQL no será detenido intencionalmente.

Antes de ejecutar el POST experimental se comprobará que Spring Boot dejó de atender en:

`puerto 8080`

Una vez confirmada la indisponibilidad:

1. se mantendrá abierta RachaPro;
2. se utilizará la actividad correspondiente a la corrida;
3. se pulsará `Guardar` una sola vez;
4. se capturará la solicitud;
5. se esperará a que finalice o falle;
6. se registrará el resultado visible;
7. se registrará el tipo de error;
8. se calculará la duración técnica si existe correlación suficiente;
9. se comprobará posteriormente PostgreSQL.

---

## 14. Verificación de indisponibilidad

Antes de cada operación medida deberá comprobarse que Spring Boot no está atendiendo en el puerto:

`8080`

Esta comprobación deberá realizarse inmediatamente antes del POST o dentro de una ventana temporal cercana que permita sostener razonablemente que el backend continuaba indisponible cuando se ejecutó la operación.

Para cada corrida deberá conservarse el resultado de esta comprobación.

No se considerará válida una corrida si posteriormente se demuestra que Spring Boot estaba disponible durante el POST experimental.

---

## 15. Captura de Logcat

La captura de Logcat deberá seguir el mismo procedimiento en ambas condiciones.

Para cada corrida:

1. la captura deberá estar activa antes de pulsar `Guardar`;
2. deberá incluir el `--> POST`;
3. deberá mantenerse activa hasta registrar el resultado de la solicitud;
4. deberá incluir el `HTTP FAILED`, si aparece;
5. deberá conservarse contexto suficiente antes y después del evento;
6. no deberá detenerse inmediatamente después del `--> POST`;
7. deberá conservarse hasta que el estado de la solicitud quede suficientemente determinado.

La captura podrá iniciarse antes de inducir el fallo si esto facilita conservar la evidencia completa.

Sin embargo, la ventana utilizada para medir la duración será únicamente la correspondiente a la solicitud experimental.

No se utilizarán fragmentos incompletos cuando impidan correlacionar el inicio y el final de la misma solicitud.

---

## 16. Protocolo de línea base

La línea base deberá ejecutarse:

- después del commit de este prerregistro;
- antes de modificar `RetrofitClient.kt`;
- antes de configurar explícitamente `connectTimeout`.

Se ejecutarán:

`3 corridas principales`

Para cada corrida:

1. comprobar que Spring Boot está inicialmente disponible;
2. comprobar PostgreSQL;
3. comprobar comunicación Android-backend;
4. confirmar el identificador único de la actividad;
5. verificar que la captura de Logcat está activa;
6. detener Spring Boot;
7. verificar indisponibilidad del puerto 8080;
8. pulsar `Guardar` exactamente una vez;
9. identificar el `--> POST`;
10. identificar el resultado de esa misma solicitud;
11. registrar el `HTTP FAILED`, si aparece;
12. registrar el tipo de excepción;
13. calcular la duración técnica;
14. registrar el comportamiento visible en Android;
15. comprobar posteriormente PostgreSQL;
16. registrar el número de coincidencias del identificador;
17. registrar cualquier solicitud concurrente observada;
18. registrar desviaciones;
19. restablecer Spring Boot;
20. comprobar nuevamente que el backend funciona;
21. restablecer las condiciones iniciales antes de la siguiente corrida.

No se realizará un segundo `Guardar` sobre la misma actividad antes de comprobar PostgreSQL.

---

## 17. Registro de la línea base

Los resultados deberán almacenarse individualmente.

### BASE-R1

Estado:

`PENDIENTE`

Duración:

`PENDIENTE`

Error:

`PENDIENTE`

Resultado Android:

`PENDIENTE`

Resultado PostgreSQL:

`PENDIENTE`

### BASE-R2

Estado:

`PENDIENTE`

Duración:

`PENDIENTE`

Error:

`PENDIENTE`

Resultado Android:

`PENDIENTE`

Resultado PostgreSQL:

`PENDIENTE`

### BASE-R3

Estado:

`PENDIENTE`

Duración:

`PENDIENTE`

Error:

`PENDIENTE`

Resultado Android:

`PENDIENTE`

Resultado PostgreSQL:

`PENDIENTE`

No deberán completarse estos campos antes de ejecutar las pruebas.

---

## 18. Implementación experimental

Después de completar y conservar las tres corridas evaluables de línea base se realizará el cambio experimental.

La intervención prevista será exclusivamente:

`connectTimeout = 3 segundos`

La modificación de código deberá realizarse en un commit posterior al commit de este prerregistro.

No se modificarán deliberadamente como parte de este spike:

- lógica funcional de creación;
- repositorios;
- ViewModels;
- backend;
- PostgreSQL;
- autenticación;
- `readTimeout`;
- `writeTimeout`;
- `callTimeout`;
- mecanismos de retry;
- mecanismos de fallback;
- circuit breaker.

Si para compilar resulta necesaria una modificación técnica auxiliar, deberá registrarse explícitamente y evaluarse su posible influencia sobre el experimento.

---

## 19. Protocolo después de la intervención

Después de configurar `connectTimeout = 3 segundos` se ejecutarán:

`3 corridas principales`

El procedimiento deberá ser equivalente al utilizado para la línea base.

Para cada corrida:

1. comprobar Spring Boot disponible inicialmente;
2. comprobar PostgreSQL;
3. comprobar comunicación normal Android-backend;
4. utilizar el identificador correspondiente;
5. verificar que la captura de Logcat está activa;
6. detener Spring Boot;
7. verificar puerto 8080 sin servicio;
8. pulsar `Guardar` exactamente una vez;
9. identificar `--> POST`;
10. identificar el resultado correspondiente;
11. registrar `HTTP FAILED`, si aparece;
12. registrar tipo de excepción;
13. calcular duración;
14. registrar resultado visible en Android;
15. comprobar posteriormente PostgreSQL;
16. registrar coincidencias;
17. registrar solicitudes concurrentes observadas;
18. registrar desviaciones;
19. restablecer Spring Boot;
20. comprobar nuevamente que el backend funciona;
21. restablecer condiciones antes de continuar.

---

## 20. Registro individual obligatorio

Cada una de las corridas deberá registrar al menos:

- condición experimental;
- identificador de corrida;
- identificador de actividad;
- fecha;
- hora;
- commit ejecutado;
- dispositivo;
- versión Android;
- usuario experimental;
- estado inicial de Spring Boot;
- estado de PostgreSQL;
- evidencia de indisponibilidad de 8080;
- timestamp de inicio;
- timestamp de finalización;
- duración calculada;
- tipo de resultado HTTP;
- tipo de excepción;
- comportamiento visible de Android;
- número de pulsaciones de `Guardar`;
- resultado de PostgreSQL;
- número de coincidencias;
- solicitudes concurrentes observadas;
- validez de la corrida;
- desviaciones metodológicas.

---

## 21. Corridas no evaluables

Una corrida podrá clasificarse como:

`NO EVALUABLE`

cuando exista evidencia de que el protocolo no pudo cumplirse suficientemente.

Ejemplos:

- Spring Boot estaba disponible durante la operación;
- PostgreSQL no estaba disponible;
- hubo más de una pulsación de `Guardar`;
- existieron solicitudes concurrentes imposibles de correlacionar;
- falta `--> POST`;
- falta el resultado correspondiente;
- no se capturó Logcat suficiente;
- cambió la red de manera no controlada;
- se reinició accidentalmente la aplicación;
- cambió una condición experimental;
- no fue posible comprobar PostgreSQL.

Una corrida `NO EVALUABLE`:

- permanecerá registrada;
- conservará su evidencia;
- incluirá la razón de invalidez;
- no contará como respaldo;
- no contará como refutación;
- no será eliminada silenciosamente.

---

## 22. Regla de reemplazo de corridas no evaluables

La comparación principal requiere:

`3 corridas evaluables de línea base`

y:

`3 corridas evaluables después de la intervención`

Si una corrida resulta `NO EVALUABLE` según los criterios definidos previamente, podrá ejecutarse como máximo:

`1 repetición de reemplazo por condición`

La repetición adicional:

- solo podrá realizarse por una causa metodológica documentada;
- no podrá justificarse por el valor temporal obtenido;
- conservará la evidencia de la corrida original;
- utilizará un identificador nuevo;
- deberá seguir exactamente el mismo protocolo;
- deberá registrarse explícitamente como reemplazo.

La corrida original no evaluable no será eliminada ni sustituida del registro histórico.

### Condición para calcular las medianas

Las medianas principales solo se calcularán si finalmente existen:

- 3 corridas evaluables en la condición de línea base;
- 3 corridas evaluables en la condición posterior.

Si después de utilizar, cuando corresponda, la única repetición permitida por condición no se obtienen tres corridas evaluables en ambas condiciones, el resultado global se clasificará como:

`COMPARACIÓN PRINCIPAL NO EVALUABLE`

En ese caso podrán mostrarse los valores individuales disponibles de manera descriptiva, pero no se utilizarán para declarar respaldada o no respaldada la hipótesis preregistrada.

No se realizarán repeticiones adicionales después del límite establecido con el propósito de completar o favorecer el resultado.

---

## 23. Valores individuales y mediana

Los tiempos individuales evaluables de línea base serán:

- `B1`
- `B2`
- `B3`

Los tiempos posteriores evaluables serán:

- `T1`
- `T2`
- `T3`

Se calculará:

`M_antes = mediana(B1, B2, B3)`

y:

`M_después = mediana(T1, T2, T3)`

Se conservarán siempre los valores individuales.

La mediana no sustituirá el registro de:

- excepciones;
- desviaciones;
- errores;
- resultados funcionales.

---

## 24. Cálculo de reducción

La reducción porcentual se calculará mediante:

\[
Reducción(\%) =
\frac{M_{antes} - M_{después}}
{M_{antes}}
\times 100
\]

La misma fórmula deberá utilizarse después del experimento.

No se sustituirá por otro cálculo si los resultados observados resultan desfavorables.

---

## 25. Hipótesis preregistrada

Hipótesis:

> Ante una indisponibilidad controlada de Spring Boot, configurar explícitamente `connectTimeout = 3 segundos` reducirá al menos un 50 % la mediana de la duración técnica de los `POST` fallidos de creación de actividad respecto de una nueva línea base ejecutada bajo el mismo protocolo, sin que Android presente falsamente la operación como exitosa ni se observe persistencia remota inesperada de la actividad intentada durante el fallo.

La hipótesis queda definida antes de:

- ejecutar la nueva línea base;
- implementar el timeout experimental;
- ejecutar las mediciones posteriores.

---

## 26. Criterio de respaldo

La hipótesis se considerará respaldada por este experimento únicamente si se cumplen simultáneamente:

1. reducción de la mediana:

`>= 50 %`

2. ausencia de falso éxito visible en Android;

3. ausencia de persistencia remota inesperada de las actividades experimentales;

4. existencia de 3 corridas evaluables en línea base;

5. existencia de 3 corridas evaluables después de la intervención;

6. comparabilidad razonable entre las condiciones antes y después.

Una reducción inferior al 50 % no será reinterpretada posteriormente como cumplimiento.

---

## 27. Criterio de no respaldo

La hipótesis no quedará respaldada si las condiciones son comparables y ocurre cualquiera de las siguientes situaciones:

- reducción de mediana menor al 50 %;
- mediana posterior igual a la anterior;
- mediana posterior mayor;
- ausencia del efecto temporal esperado;
- Android muestra una operación como exitosa sin confirmación adecuada;
- aparece persistencia remota inesperada incompatible con el comportamiento esperado.

Un resultado negativo no implicará que el experimento sea inválido.

Una hipótesis refutada o no respaldada será igualmente registrada.

---

## 28. Diferencia entre no respaldo y comparación no evaluable

No alcanzar el criterio cuantitativo bajo condiciones válidas corresponde a:

`HIPÓTESIS NO RESPALDADA`

En cambio, no disponer finalmente de tres corridas evaluables por condición corresponde a:

`COMPARACIÓN PRINCIPAL NO EVALUABLE`

Estas categorías no deberán confundirse.

Ejemplos:

- mediana posterior reduce 35 % con 3 corridas evaluables antes y 3 después → `HIPÓTESIS NO RESPALDADA`;
- después de utilizar el único reemplazo permitido todavía faltan 3 corridas evaluables en una condición → `COMPARACIÓN PRINCIPAL NO EVALUABLE`.

La clasificación se hará usando criterios definidos previamente y no según la conveniencia del resultado.

---

## 29. Tipo de error

El tipo de error se registrará independientemente del tiempo.

No se asumirá que todas las corridas producirán:

`SocketTimeoutException`

Si se observa:

- `ConnectException`;
- otro timeout;
- error de red;
- respuesta HTTP;
- cancelación;
- comportamiento diferente;

se conservará exactamente como observación.

Una diferencia en el tipo de error puede ser relevante para interpretar el resultado.

---

## 30. Seguridad funcional

La reducción temporal por sí sola no será suficiente para respaldar la hipótesis.

En cada corrida deberán comprobarse adicionalmente:

### Android

Registrar si:

- muestra éxito;
- muestra error;
- queda en un estado ambiguo;
- altera inesperadamente el estado de la actividad.

### PostgreSQL

Después de finalizar el intento medido se buscará específicamente el identificador de la actividad experimental.

El resultado deberá registrarse como:

- `0 coincidencias`;
- `1 coincidencia`;
- más de una coincidencia;
- `NO EVALUABLE`.

Android no será utilizado como sustituto de la comprobación remota.

---

## 31. Momento de la comprobación remota

PostgreSQL se comprobará después de finalizar el POST experimental y antes de realizar una nueva pulsación de `Guardar` sobre la misma actividad.

Esto busca evitar la ambigüedad observada previamente en algunas repeticiones de F-01/R-01.

No deberá ejecutarse manualmente una segunda creación de la misma actividad antes de verificar el estado remoto.

Si esto ocurre accidentalmente:

`la corrida deberá registrarse como desviada y evaluarse según el criterio de corrida no evaluable.`

---

## 32. Amenazas a la validez

Se reconocen antes de ejecutar el experimento las siguientes amenazas:

- variabilidad de la red Wi-Fi;
- precisión temporal de Logcat;
- solicitudes concurrentes;
- errores al correlacionar logs;
- reutilización de conexiones;
- estado interno de OkHttp;
- procesos del sistema Android;
- variaciones temporales al detener Spring Boot;
- cambios no intencionales de red;
- número reducido de repeticiones;
- comportamiento particular del dispositivo físico;
- diferencia entre tiempo de conexión y duración completa de llamada.

Especialmente:

`connectTimeout` controla la fase de establecimiento de conexión.

No se asume que:

`connectTimeout = 3 segundos`

implique necesariamente:

`duración HTTP total = exactamente 3 segundos`

El resultado deberá ser medido.

---

## 33. Alcance de las conclusiones

Si el criterio queda respaldado, la conclusión deberá limitarse al escenario observado.

Podrá sostenerse una afirmación equivalente a:

> Bajo las condiciones reproducidas, la configuración evaluada redujo la mediana del tiempo técnico observado ante la indisponibilidad controlada del backend.

El experimento no permitirá afirmar por sí solo que:

- 3 segundos sea el valor óptimo;
- 3 segundos sea apropiado para todas las redes;
- la experiencia completa del usuario haya mejorado el mismo porcentaje;
- el sistema tenga resiliencia general;
- exista retry automático;
- exista fallback;
- exista circuit breaker;
- exista idempotencia;
- el sistema tolere cualquier fallo;
- la configuración deba pasar directamente a producción.

Una decisión arquitectónica posterior deberá considerar tanto los resultados como las limitaciones.

---

## 34. Trazabilidad temporal

La secuencia prevista será:

`decisión -> prerregistro -> commit -> línea base -> implementación -> commit -> medición posterior -> resultados -> análisis`

Commit documental anterior:

`d772a3b`

Commit de prerregistro:

`PENDIENTE`

Commit de implementación:

`PENDIENTE`

Commit de resultados:

`PENDIENTE`

La línea base específica de la Actividad 4 no deberá ejecutarse antes del commit de prerregistro.

La implementación de `connectTimeout` no deberá realizarse antes de completar la línea base.

Los resultados posteriores no deberán registrarse retroactivamente dentro del commit de prerregistro.

---

## 35. Evidencia

Se conservará para cada corrida:

- identificación;
- timestamps;
- fragmentos relevantes de Logcat;
- duración calculada;
- excepción;
- resultado Android;
- resultado PostgreSQL;
- estado del puerto 8080;
- commit;
- condiciones;
- desviaciones.

Los logs privados no deberán incorporarse directamente al repositorio sin revisión y saneamiento.

No deberán publicarse:

- JWT completos;
- encabezados `Authorization`;
- contraseñas;
- credenciales;
- secretos;
- información sensible innecesaria.

Cuando corresponda, los archivos privados podrán conservarse localmente y referenciarse mediante:

`SHA-256`

---

## 36. Estado previo a ejecución

Prerregistro:

`PREPARADO — PENDIENTE DE COMMIT`

Línea base específica:

`NO EJECUTADA`

Cambio en `RetrofitClient.kt`:

`NO REALIZADO`

Timeout experimental:

`NO IMPLEMENTADO`

Corridas posteriores:

`NO EJECUTADAS`

Resultados:

`NO OBSERVADOS`

Hipótesis:

`NO EVALUADA`

Conclusión arquitectónica:

`PENDIENTE`