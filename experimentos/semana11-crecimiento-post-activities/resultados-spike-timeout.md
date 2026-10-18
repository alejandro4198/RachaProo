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

Condición actual:

`LÍNEA BASE`

Configuración HTTP:

`SIN connectTimeout EXPLÍCITO`

Corridas principales previstas de línea base:

`3`

Estado de BASE-R1:

`EJECUTADA`

Estado de BASE-R2:

`PENDIENTE`

Estado de BASE-R3:

`PENDIENTE`

Intervención experimental:

`NO IMPLEMENTADA`

Mediciones posteriores:

`NO EJECUTADAS`

---

## 2. BASE-R1

Estado:

`EVALUABLE CON DESVIACIÓN DOCUMENTADA`

Valor destinado a la comparación principal:

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

La diferencia anterior no constituye la métrica principal de BASE-R1.

El control previo se conserva únicamente como evidencia de que la creación remota funcionaba antes de inducir la indisponibilidad.

Después del POST de control también se observaron solicitudes posteriores normales hacia el backend.

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

Este valor será utilizado posteriormente en la mediana de línea base únicamente si la corrida conserva su clasificación como evaluable.

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

El hecho de observar:

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

La desviación reduce la trazabilidad funcional de PostgreSQL porque una consulta únicamente por título no permite diferenciar por sí sola ambas operaciones.

No obstante, la desviación no impide distinguir técnicamente las dos solicitudes HTTP debido a la existencia de:

- timestamps diferentes;
- hilos OkHttp diferentes;
- tamaños de cuerpo registrados diferentes;
- resultados HTTP diferentes.

---

### 2.10 Evaluabilidad de BASE-R1

Clasificación:

`EVALUABLE CON DESVIACIÓN DOCUMENTADA`

Justificación:

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
12. la evidencia permite distinguir las dos solicitudes HTTP aunque el título funcional haya sido reutilizado.

La corrida no se clasifica como `NO EVALUABLE` porque la desviación no impide obtener ni correlacionar la métrica principal.

---

### 2.11 Participación en la mediana

Decisión de evaluación:

`BASE-R1 PARTICIPARÁ EN LA MEDIANA PRINCIPAL`

Valor:

`B1 = 10 028 ms`

Esta decisión no modifica:

- la hipótesis preregistrada;
- el umbral del 50 %;
- la métrica;
- el número de corridas;
- la intervención.

La repetición de reemplazo permitida para la condición BASE:

`NO SE UTILIZA PARA BASE-R1`

---

### 2.12 Corrección procedimental para BASE-R2 y BASE-R3

A partir de BASE-R2 no se reutilizará el mismo identificador para control y operación experimental.

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

BASE-R1 permanece documentada con la desviación realmente ocurrida.

---

### 2.13 Integridad de la evidencia

Archivo privado:

`evidencia-timeout/BASE-R1/logcat-BASE-R1-privado.txt`

SHA-256:

`DE5066FCF6BA7B86E1BD12FCA3041AD77513C38499E25578EB92DD27DA6E1AE5`

El archivo se conserva como evidencia privada.

No deberá versionarse sin revisión y saneamiento debido a la posibilidad de contener información sensible o innecesaria.

---

### 2.14 Resultado consolidado de BASE-R1

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

`PENDIENTE DE EJECUCIÓN`

Identificador de control previsto:

`S11-A4-BASE-R2-CONTROL`

Identificador experimental previsto:

`S11-A4-BASE-R2`

Valor:

`B2 = PENDIENTE`

No se registrarán resultados antes de ejecutar la corrida.

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

No se registrarán resultados antes de ejecutar la corrida.

---

## 5. Mediana de línea base

Valores previstos:

- `B1 = 10 028 ms`
- `B2 = PENDIENTE`
- `B3 = PENDIENTE`

Mediana:

`PENDIENTE`

La mediana no se calculará hasta disponer de tres corridas evaluables de línea base conforme al prerregistro.

---

## 6. Intervención experimental

Estado:

`NO IMPLEMENTADA`

Cambio previsto:

`connectTimeout = 3 segundos`

`RetrofitClient.kt`:

`SIN MODIFICAR PARA LA INTERVENCIÓN`

La intervención no deberá implementarse hasta completar las tres corridas evaluables de línea base.

---

## 7. Corridas posteriores a la intervención

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

## 8. Mediana posterior

Valores:

- `T1 = PENDIENTE`
- `T2 = PENDIENTE`
- `T3 = PENDIENTE`

Mediana:

`PENDIENTE`

---

## 9. Reducción porcentual

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

## 10. Estado de la hipótesis

Hipótesis preregistrada:

> Ante una indisponibilidad controlada de Spring Boot, configurar explícitamente `connectTimeout = 3 segundos` reducirá al menos un 50 % la mediana de la duración técnica de los `POST` fallidos de creación de actividad respecto de una nueva línea base ejecutada bajo el mismo protocolo, sin que Android presente falsamente la operación como exitosa ni se observe persistencia remota inesperada de la actividad intentada durante el fallo.

Estado actual:

`NO EVALUADA`

No existe todavía evidencia suficiente para clasificarla como:

- respaldada;
- no respaldada;
- comparación principal no evaluable.

---

## 11. Estado actual de la Actividad 4

Prerregistro:

`COMPLETADO`

Commit:

`94dcc32`

BASE-R1:

`COMPLETADA — EVALUABLE CON DESVIACIÓN DOCUMENTADA`

BASE-R2:

`PENDIENTE`

BASE-R3:

`PENDIENTE`

Mediana de línea base:

`PENDIENTE`

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

## 12. Siguiente paso

El siguiente paso corresponde a ejecutar:

`BASE-R2`

manteniendo la condición de línea base y sin modificar todavía `RetrofitClient.kt`.

Para evitar la desviación observada en BASE-R1 se utilizarán identificadores diferenciados:

Control:

`S11-A4-BASE-R2-CONTROL`

Experimento:

`S11-A4-BASE-R2`

No se realizará una segunda pulsación de `Guardar` sobre la actividad experimental antes de comprobar PostgreSQL.

Después de completar BASE-R2 se conservarán:

- timestamps;
- duración técnica;
- tipo de error;
- resultado Android;
- resultado PostgreSQL;
- estado del puerto 8080;
- Logcat privado;
- SHA-256;
- cualquier desviación metodológica.