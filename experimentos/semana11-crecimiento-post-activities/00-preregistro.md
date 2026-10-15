# Prerregistro — Experimento de crecimiento de carga sobre `POST /api/activities`

## 1. Identificación

- Proyecto: RachaPro
- Módulo académico: Módulo 6
- Semana: 11
- Actividad: re-ejecución de una medición del Módulo 2 con carga aumentada
- Atributo de calidad principal: rendimiento
- Operación evaluada: `POST /api/activities`
- Backend: Spring Boot
- Persistencia: PostgreSQL 17
- Herramienta de generación de carga: Grafana k6
- Modelo de carga: cerrado
- Ejecutor previsto: `constant-vus`
- Tipo de experimento: crecimiento de concurrencia mediante niveles independientes
- Estado del documento: PRERREGISTRO
- Momento del documento: anterior a las mediciones oficiales de Semana 11
- Resultados de Semana 11: todavía no ejecutados

Este documento define previamente:

- pregunta experimental;
- hipótesis;
- configuración;
- semilla;
- distribución de datos;
- distribución de identidades activas;
- métricas;
- reglas estadísticas;
- criterios de degradación;
- criterios de validez;
- criterios de invalidación;
- reglas de interpretación;
- evidencia a conservar.

Las reglas registradas aquí no deberán modificarse después de observar resultados oficiales con el propósito de adaptar el método a los valores obtenidos.

Si durante la implementación se identifica una imposibilidad técnica que obligue a modificar alguna regla, la campaña deberá detenerse y el cambio deberá:

1. documentarse;
2. registrarse mediante un nuevo commit;
3. realizarse antes de las mediciones ejecutadas bajo el nuevo procedimiento;
4. mantener separada la evidencia producida bajo versiones metodológicas diferentes.

---

## 2. Relación con la evidencia anterior

### 2.1 EXP-002 como antecedente directo

El antecedente directo de este experimento es:

`experimentos/EXP-002-k6-api-activities/`

EXP-002 evaluó:

`POST /api/activities`

utilizando Grafana k6.

Su configuración histórica incluyó:

- 1 VU;
- 25 iteraciones;
- una creación de actividad por iteración;
- autenticación mediante JWT;
- selección previa de una categoría activa;
- ausencia de concurrencia entre múltiples VUs.

Las métricas específicas principales fueron:

- `activity_create_duration`
- `activity_create_success`

EXP-002 produjo una línea base HTTP para la creación de actividades, pero no estudió el comportamiento del sistema bajo concurrencia elevada.

El experimento de Semana 11:

- no reemplaza EXP-002;
- no modifica EXP-002;
- no reinterpreta retrospectivamente sus resultados;
- no convierte EXP-002 en una prueba concurrente que nunca fue.

Su propósito es extender la observación del mismo endpoint hacia condiciones de concurrencia superiores.

### 2.2 Relación con EXP-003

EXP-003 constituye un antecedente metodológico útil para:

- Grafana k6;
- concurrencia;
- preparación de usuarios sintéticos;
- generación de categorías;
- generación de datasets;
- automatización de pruebas;
- PostgreSQL con gran volumen de información.

Sin embargo, EXP-003 evaluó principalmente:

`GET /api/activities`

sobre un dataset aproximado de 500.000 actividades.

Por tanto:

- EXP-003 no será utilizado como línea base numérica directa;
- sus latencias no serán comparadas directamente con las de este experimento;
- sus resultados no serán presentados como evidencia de `POST /api/activities`;
- sus scripts podrán utilizarse únicamente como referencia técnica.

---

## 3. Objetivo

Evaluar cómo cambia el comportamiento del endpoint autenticado:

`POST /api/activities`

cuando aumenta progresivamente la cantidad de usuarios virtuales concurrentes bajo condiciones experimentales controladas.

Se evaluarán los niveles previstos:

- 1 VU;
- 10 VUs;
- 50 VUs;
- 100 VUs;
- 250 VUs;
- 500 VUs;
- 750 VUs;
- 1000 VUs.

Cada nivel constituye una condición experimental independiente.

Se busca identificar si existe algún nivel en el que aparezca degradación observable de:

- latencia;
- éxito funcional;
- estabilidad HTTP;
- throughput.

Se observarán como mínimo:

- `activity_create_duration`;
- P50;
- P90;
- P95;
- máximo;
- `activity_create_success`;
- tasa de éxito funcional;
- cantidad de respuestas HTTP 2xx;
- cantidad de respuestas HTTP 4xx;
- cantidad de respuestas HTTP 5xx;
- iteraciones completadas;
- iteraciones interrumpidas cuando el instrumento permita evidenciarlas;
- solicitudes completadas;
- throughput observado.

El experimento no presupone qué componente produciría una eventual degradación.

Una degradación observada no será atribuida automáticamente a:

- Spring Boot;
- PostgreSQL;
- Docker;
- CPU;
- memoria;
- almacenamiento;
- pool de conexiones;
- Grafana k6;
- JVM;
- sistema operativo;
- red local;
- u otro componente.

Toda atribución causal requerirá evidencia adicional.

---

## 4. Pregunta experimental

¿Cómo cambia el comportamiento de `POST /api/activities` cuando la concurrencia aumenta desde una condición de referencia de 1 VU hasta un máximo previsto de 1000 VUs, manteniendo controladas las demás condiciones experimentales relevantes?

Pregunta complementaria:

¿Existe algún nivel de concurrencia dentro del rango efectivamente ejecutado y válido que satisfaga al menos uno de los criterios de degradación establecidos antes de medir?

---

## 5. Hipótesis

### 5.1 H1 — Hipótesis experimental

Al aumentar la concurrencia desde 1 VU hasta un máximo previsto de 1000 VUs sobre `POST /api/activities`, existirá al menos un nivel ejecutado válidamente que satisfaga uno o más criterios de degradación definidos previamente en este documento.

### 5.2 H0 — Hipótesis nula

Bajo las condiciones específicas de este experimento, ninguno de los niveles efectivamente ejecutados y válidos satisfará los criterios previamente definidos de degradación.

### 5.3 Regla de interpretación

H1 recibirá respaldo si al menos un nivel ejecutado y válido satisface al menos uno de los siguientes criterios:

1. degradación relativa del P95;
2. degradación de éxito funcional;
3. presencia repetida de HTTP 5xx.

No es necesario que las tres señales aparezcan simultáneamente.

Cada señal será reportada por separado.

Si ningún nivel ejecutado y válido satisface los criterios, H1 no quedará respaldada dentro del rango efectivamente medido.

Si uno o más niveles previstos no pueden ejecutarse correctamente:

- no serán tratados como niveles sin degradación;
- no contarán como evidencia favorable a H0;
- se declarará el mayor nivel ejecutado válidamente;
- las conclusiones quedarán limitadas al rango realmente medido.

El experimento no permitirá afirmar:

- escalabilidad ilimitada;
- capacidad máxima absoluta;
- equivalencia con producción;
- comportamiento idéntico en otra infraestructura;
- ausencia de degradación más allá del mayor nivel efectivamente evaluado.

---

## 6. Variables experimentales

### 6.1 Variable independiente

Número configurado de usuarios virtuales concurrentes en Grafana k6.

| Nivel | VUs |
|---:|---:|
| 1 | 1 |
| 2 | 10 |
| 3 | 50 |
| 4 | 100 |
| 5 | 250 |
| 6 | 500 |
| 7 | 750 |
| 8 | 1000 |

### 6.2 Variables dependientes

Se observarán:

- `activity_create_duration`;
- P50;
- P90;
- P95;
- máximo;
- `activity_create_success`;
- tasa de éxito funcional;
- HTTP 2xx;
- HTTP 4xx;
- HTTP 5xx;
- iteraciones completadas;
- iteraciones interrumpidas cuando exista evidencia verificable;
- solicitudes completadas;
- throughput observado.

### 6.3 Variables controladas

Se intentarán mantener constantes:

- commit del backend;
- versión del script k6;
- endpoint;
- payload;
- duración;
- pausa entre iteraciones;
- versión de PostgreSQL;
- esquema;
- índices;
- semilla;
- distribución 80/20;
- cantidad total de usuarios;
- cantidad total de categorías;
- cantidad total de actividades preexistentes;
- distribución de actividades por usuario;
- configuración del backend;
- instancia PostgreSQL;
- equipo físico;
- versión de Grafana k6;
- procedimiento de autenticación;
- procedimiento de restauración;
- criterios estadísticos;
- criterios de invalidación.

La variable deliberadamente modificada entre niveles será principalmente el número de VUs configurados.

---

## 7. Modelo y configuración de carga

### 7.1 Modelo de carga

Se utilizará un modelo de carga cerrado mediante:

`constant-vus`

Cada VU completa una iteración antes de comenzar la siguiente.

Por tanto:

`1000 VUs`

no equivale necesariamente a:

`1000 solicitudes HTTP simultáneas en cada instante`

Si una operación tarda más, el VU correspondiente tardará más en iniciar su siguiente iteración.

Por ello, el throughput puede:

- aumentar;
- estabilizarse;
- crecer de forma no proporcional;
- disminuir;

aunque el número de VUs configurados permanezca constante.

Esta distinción deberá conservarse tanto en el análisis escrito como en la defensa oral.

### 7.2 Niveles independientes

Los ocho niveles serán ejecutados como condiciones separadas.

No se realizará una única ejecución que aumente progresivamente desde 1 hasta 1000 VUs.

### 7.3 Duración

Cada corrida tendrá:

`60 segundos`

de carga nominal.

### 7.4 `gracefulStop`

Se utilizará:

`gracefulStop = 30s`

Los 30 segundos no constituyen una extensión planificada de la carga nominal.

Su función es permitir que iteraciones iniciadas antes del final de los 60 segundos puedan terminar de manera controlada.

### 7.5 Evidencia temporal

El resumen agregado de k6 no será asumido como evidencia suficiente para identificar individualmente todas las solicitudes o iteraciones pendientes.

Durante la implementación deberá comprobarse qué mecanismo de exportación disponible permite conservar evidencia temporal suficiente.

Como mínimo, se intentará distinguir:

- intervalo nominal de 60 segundos;
- período de `gracefulStop`;
- iteraciones completadas;
- información disponible sobre iteraciones abortadas o interrumpidas.

No se afirmará que cada solicitud interrumpida puede identificarse individualmente si el instrumento no produce esa evidencia.

La capacidad exacta de trazabilidad temporal deberá comprobarse antes de la primera corrida oficial.

### 7.6 Iteración experimental

Una iteración comprenderá:

1. utilizar la identidad autenticada asignada al VU;
2. construir un payload válido;
3. ejecutar `POST /api/activities`;
4. registrar la duración específica de la operación;
5. comprobar el código HTTP;
6. comprobar la presencia de un identificador válido;
7. registrar las métricas;
8. esperar 1 segundo;
9. iniciar la siguiente iteración.

### 7.7 Pausa

Cada VU esperará:

`1 segundo`

después de completar una iteración.

La pausa será constante para:

- todos los niveles;
- todos los grupos de usuarios;
- todas las corridas.

No se utilizará una pausa diferente para generar artificialmente una distribución 80/20 de solicitudes.

### 7.8 Throughput

El throughput no será fijado como entrada del experimento.

Será una variable observada.

No se establecerá retrospectivamente un umbral de throughput después de conocer los resultados.

---

## 8. Repeticiones y calentamiento

Cada nivel tendrá:

`4 corridas`

distribuidas así:

- corrida 1: calentamiento;
- corrida 2: medida;
- corrida 3: medida;
- corrida 4: medida.

La corrida 1 no participará en el cálculo del valor representativo principal del nivel.

Sin embargo:

- sus logs serán conservados;
- sus métricas serán conservadas;
- sus errores serán conservados;
- no será eliminada de la evidencia.

### 8.1 Estado del dataset entre corridas

Todas las corridas, incluida la de calentamiento, deberán comenzar desde una semilla experimental reconstruida bajo las mismas reglas.

Después de cada corrida se restaurará el estado experimental antes de comenzar la siguiente.

### 8.2 Estado de la JVM

La restauración del dataset entre las cuatro corridas de un mismo nivel no implicará reiniciar deliberadamente la JVM del backend.

Se busca que:

- la primera corrida absorba efectos de calentamiento;
- las corridas 2, 3 y 4 utilicen el backend ya iniciado;
- el dataset vuelva al estado inicial;
- la JVM permanezca activa durante las repeticiones del nivel.

No se afirmará que el estado interno de la JVM sea idéntico entre corridas.

Si el backend se reinicia involuntariamente durante una corrida medida o entre corridas medidas, el evento deberá registrarse y evaluarse contra los criterios de validez.

### 8.3 Cambio de nivel

Al finalizar las cuatro corridas de un nivel:

1. se conservará la evidencia;
2. se restaurará la semilla;
3. se verificará el entorno;
4. se iniciará el siguiente nivel.

La corrida 1 del siguiente nivel volverá a tratarse como calentamiento.

---

## 9. Agregación estadística

### 9.1 P95 representativo

Cada corrida medida producirá un P95 independiente.

El P95 representativo de cada nivel será:

`mediana(P95 corrida 2, P95 corrida 3, P95 corrida 4)`

No se combinarán todas las solicitudes de las tres corridas para calcular un único P95 global.

### 9.2 P50, P90 y máximo

Para cada corrida se conservarán independientemente:

- P50;
- P90;
- P95;
- máximo.

### 9.3 Tasa de éxito representativa

Cada corrida medida tendrá una tasa de éxito funcional independiente.

La tasa representativa del nivel será:

`mediana(tasa corrida 2, tasa corrida 3, tasa corrida 4)`

### 9.4 Discordancia entre corridas

Una corrida extrema no será eliminada simplemente por diferir de las otras.

La corrida permanecerá como evidencia.

La regla formal utilizará la mediana preregistrada.

La variabilidad entre corridas podrá analizarse adicionalmente.

---

## 10. Éxito funcional

Una creación será considerada funcionalmente exitosa cuando:

1. se ejecute `POST /api/activities`;
2. el backend responda con HTTP `201`;
3. la respuesta contenga un identificador de actividad válido.

La métrica:

`activity_create_success`

representará esta definición.

No se añadirán criterios nuevos después de observar los resultados para aumentar o reducir artificialmente la tasa de éxito.

---

## 11. Criterios de degradación

### 11.1 Condición de referencia

La referencia numérica de la nueva campaña será el nivel de:

`1 VU`

ejecutado dentro de esta misma campaña.

EXP-002 permanecerá como antecedente histórico, pero sus valores no se utilizarán directamente como denominador del criterio relativo.

### 11.2 Degradación relativa de latencia

Se considerará cumplida cuando:

`P95 representativo del nivel >= 2 × P95 representativo del nivel de 1 VU`

### 11.3 Degradación funcional

Se considerará cumplida cuando:

`tasa representativa de éxito < 99 %`

### 11.4 HTTP 5xx repetidos

Todos los HTTP 5xx serán registrados.

Para que constituyan formalmente la señal:

`HTTP 5xx repetidos`

deberá aparecer al menos un HTTP 5xx en:

`2 o más de las 3 corridas medidas válidas del mismo nivel`

Un único HTTP 5xx seguirá siendo evidencia, aunque no satisfaga esta regla.

### 11.5 Regla conjunta

Un nivel será clasificado como degradado si satisface al menos una de las siguientes condiciones:

- P95 representativo >= 2 × referencia;
- tasa representativa de éxito < 99 %;
- HTTP 5xx en al menos dos corridas medidas válidas.

Las señales responsables deberán indicarse por separado.

---

## 12. Relación con el escenario de calidad vigente

El escenario vigente del proyecto establece que la creación de una actividad debe completarse correctamente en un máximo de un minuto.

Este experimento mide específicamente:

`POST /api/activities`

Por tanto:

- el criterio de un minuto se conservará como antecedente;
- no se presentará esta prueba como medición Android end-to-end;
- no se equiparará latencia HTTP con tiempo total de interacción del usuario;
- el criterio relativo de P95 servirá para caracterizar degradación dentro de la operación backend evaluada.

---

## 13. Entorno PostgreSQL experimental

### 13.1 Entorno habitual protegido

La infraestructura habitual identificada utiliza:

- contenedor: `rachapro-postgres`;
- puerto host: `5432`;
- volumen: `postgres_rachapro_postgres_data`.

Estos recursos quedan fuera del procedimiento destructivo experimental.

No se ejecutará sobre ellos:

- `docker compose down -v`;
- `TRUNCATE` experimental;
- eliminación de volumen;
- borrado masivo;
- restauración de la semilla de Semana 11.

### 13.2 Entorno experimental previsto

Se preparará una instancia aislada equivalente a:

- PostgreSQL 17;
- contenedor independiente;
- nombre previsto: `rachapro-postgres-s11`;
- puerto host previsto: `5433`;
- base prevista: `rachapro_perf_s11`;
- volumen Docker exclusivo;
- esquema proveniente de `infra/postgres/init/001-schema.sql`.

El volumen experimental deberá ser distinto de:

`postgres_rachapro_postgres_data`

Antes de las mediciones deberá demostrarse operacionalmente que el aislamiento existe.

---

## 14. Semilla experimental

### 14.1 Población

La semilla prevista tendrá:

- 1000 usuarios sintéticos;
- 1000 categorías activas;
- una categoría activa por usuario;
- 100.000 actividades activas preexistentes;
- distribución controlada aproximada 80/20, definida de manera exacta para esta semilla.

### 14.2 Definición del patrón 80/20

La distribución se aplicará a:

`actividades preexistentes almacenadas en PostgreSQL`

No se aplicará a la frecuencia de solicitudes de creación.

Los usuarios se dividirán en dos grupos.

#### Grupo A — alta concentración

- usuarios: 200;
- proporción de usuarios: 20 %;
- actividades por usuario: 400;
- actividades totales: 80.000;
- proporción de actividades: 80 %.

Cálculo:

`200 × 400 = 80.000`

#### Grupo B — baja concentración

- usuarios: 800;
- proporción de usuarios: 80 %;
- actividades por usuario: 25;
- actividades totales: 20.000;
- proporción de actividades: 20 %.

Cálculo:

`800 × 25 = 20.000`

### 14.3 Total

La semilla deberá contener:

`80.000 + 20.000 = 100.000 actividades`

y:

`200 + 800 = 1000 usuarios`

### 14.4 Verificación matemática

La distribución esperada será:

`200 / 1000 × 100 = 20 % de usuarios`

`80.000 / 100.000 × 100 = 80 % de actividades`

y:

`800 / 1000 × 100 = 80 % de usuarios`

`20.000 / 100.000 × 100 = 20 % de actividades`

### 14.5 Justificación experimental

La distribución 80/20 se incorpora como una condición fija del dataset.

Se busca que:

- la semilla tenga una concentración explícita y reproducible;
- la condición se mantenga idéntica entre corridas;
- el número de VUs siga siendo la variable deliberadamente modificada entre niveles.

La distribución 80/20 NO se conseguirá haciendo que unos usuarios ejecuten más solicitudes que otros.

Todos los VUs seguirán utilizando el mismo patrón:

`POST → validación → pausa de 1 segundo → siguiente iteración`

Por tanto, la intensidad de generación por VU permanecerá uniforme.

### 14.6 Naturaleza del 80/20

El patrón 80/20 es una condición experimental preregistrada.

No será presentado como:

- resultado del experimento;
- evidencia posterior;
- descubrimiento producido por k6.

Su existencia deberá demostrarse mediante consultas sobre PostgreSQL antes de cada corrida.

---

## 15. Identificación de los grupos

Los grupos no dependerán exclusivamente de IDs autogenerados por PostgreSQL.

Se utilizará una identificación sintética determinista que permita reconstruir el mismo grupo incluso después de reiniciar secuencias.

Ejemplo conceptual:

Grupo A:

`loadtest-high-0001@rachapro.test`

hasta:

`loadtest-high-0200@rachapro.test`

Grupo B:

`loadtest-low-0001@rachapro.test`

hasta:

`loadtest-low-0800@rachapro.test`

Los nombres exactos podrán implementarse técnicamente de forma equivalente siempre que:

- sean deterministas;
- permitan identificar el grupo;
- no dependan únicamente del ID secuencial de PostgreSQL;
- permitan verificar la distribución mediante consultas reproducibles.

---

## 16. Correspondencia entre VUs y grupos

### 16.1 Principio general

La asignación de identidades será determinista y estratificada.

No se utilizará simplemente:

`VU n → primer usuario n de la base`

porque eso haría que los niveles pequeños utilizaran exclusivamente usuarios del grupo de alta concentración.

### 16.2 Distribución por nivel

Para los niveles iguales o superiores a 10 VUs se mantendrá exactamente:

- 20 % de identidades del Grupo A;
- 80 % de identidades del Grupo B.

| Nivel | Grupo A | Grupo B | Total |
|---:|---:|---:|---:|
| 10 | 2 | 8 | 10 |
| 50 | 10 | 40 | 50 |
| 100 | 20 | 80 | 100 |
| 250 | 50 | 200 | 250 |
| 500 | 100 | 400 | 500 |
| 750 | 150 | 600 | 750 |
| 1000 | 200 | 800 | 1000 |

### 16.3 Caso especial de 1 VU

Una única identidad no puede reproducir matemáticamente una composición poblacional 20/80.

Por tanto, el nivel de:

`1 VU`

se utilizará exclusivamente como:

`condición técnica de referencia`

para el cálculo relativo del P95.

El nivel de 1 VU:

- utilizará una identidad sintética fija del Grupo A;
- no será presentado como representación poblacional del patrón 80/20;
- no será utilizado para afirmar que un solo usuario reproduce ambos estratos.

### 16.4 Interpretación

A partir de 10 VUs, todos los niveles conservarán exactamente una composición 20/80 de identidades activas.

Sin embargo, esto no significa que el 20 % de los usuarios genere el 80 % de las nuevas solicitudes.

La intensidad de solicitudes continuará siendo uniforme por VU.

---

## 17. Categorías

Cada usuario sintético tendrá exactamente una categoría activa asociada.

Cada VU utilizará únicamente la categoría perteneciente a su usuario autenticado.

La restauración deberá comprobar que:

- existen 1000 categorías activas;
- cada categoría pertenece al usuario esperado;
- no existen usuarios de la semilla sin categoría;
- no existen categorías de la semilla asignadas al usuario incorrecto.

---

## 18. Restauración del estado inicial

### 18.1 Estado metodológico

El procedimiento descrito en esta sección es:

`PROCEDIMIENTO PREVISTO`

Al momento del prerregistro todavía no se ha demostrado operacionalmente que:

- `TRUNCATE ... RESTART IDENTITY CASCADE` produzca el estado esperado;
- la semilla 80/20 se reconstruya correctamente;
- `ANALYZE` se ejecute correctamente;
- los índices mantengan el estado esperado;
- no existan escrituras concurrentes durante la restauración;
- el backend permanezca funcional después de la restauración.

Estas propiedades deberán comprobarse antes de la primera corrida oficial.

### 18.2 Creación inicial del entorno

Antes de la campaña oficial deberá verificarse que:

1. el contenedor experimental es independiente;
2. utiliza un volumen independiente;
3. utiliza el puerto previsto 5433;
4. utiliza la base experimental;
5. el esquema se inicializa correctamente;
6. los índices existen;
7. Spring Boot puede conectarse al entorno experimental;
8. la base habitual permanece sin modificación.

### 18.3 Exclusión de escrituras durante restauración

La restauración no deberá ejecutarse mientras exista carga activa.

Antes de restaurar deberá garantizarse que:

- k6 no está ejecutando una corrida;
- no existen VUs enviando solicitudes;
- no se inicia una nueva prueba hasta terminar las verificaciones.

Si se detectan escrituras concurrentes durante la reconstrucción, la semilla resultante no será considerada válida.

### 18.4 Restauración entre corridas

El procedimiento previsto será equivalente a:

1. finalizar completamente la corrida anterior;
2. confirmar ausencia de carga activa;
3. limpiar únicamente la base experimental;
4. ejecutar `TRUNCATE ... RESTART IDENTITY CASCADE` sobre las tablas experimentales correspondientes;
5. reconstruir 1000 usuarios;
6. reconstruir 1000 categorías;
7. reconstruir 100.000 actividades;
8. reconstruir la distribución 80/20;
9. comprobar propiedad usuario-categoría;
10. comprobar distribución por usuario;
11. ejecutar `ANALYZE`;
12. verificar índices;
13. verificar conteos;
14. verificar porcentajes 80/20;
15. comprobar disponibilidad del backend;
16. iniciar la siguiente corrida.

El `TRUNCATE` deberá ejecutarse exclusivamente contra:

`rachapro_perf_s11`

y nunca contra la base habitual.

### 18.5 Razón del procedimiento

La restauración lógica busca:

- eliminar datos producidos por la corrida anterior;
- reiniciar secuencias;
- reconstruir la misma población;
- reconstruir la misma distribución;
- mantener el proceso PostgreSQL;
- mantener la JVM del backend activa;
- reducir diferencias provocadas por reinicios completos entre repeticiones.

### 18.6 Índices

Los índices no serán eliminados y recreados deliberadamente entre corridas.

Provendrán del mismo esquema inicial.

Antes de la campaña deberá verificarse su existencia.

### 18.7 Estadísticas PostgreSQL

Después de reconstruir la semilla se prevé ejecutar:

`ANALYZE`

sobre las tablas relevantes.

El objetivo es reducir diferencias asociadas exclusivamente a estadísticas desactualizadas después de reconstruir los datos.

Este procedimiento todavía deberá verificarse técnicamente.

No se afirmará que el estado físico interno de PostgreSQL sea idéntico bit a bit.

La equivalencia buscada será operacional y verificable.

### 18.8 Fechas e identificadores

Los datos dependientes de fecha deberán generarse mediante una regla controlada.

La semilla no deberá depender de valores temporales variables que alteren innecesariamente la distribución entre restauraciones.

Si se utilizan marcas temporales generadas durante la preparación, estas:

- no deberán cambiar la lógica funcional de la semilla;
- deberán documentarse como valores variables no utilizados para distinguir condiciones experimentales.

Los grupos A y B no dependerán de IDs autogenerados.

---

## 19. Verificación de equivalencia antes de cada corrida

Antes de cada corrida deberá verificarse como mínimo:

### 19.1 Infraestructura

- base actual = `rachapro_perf_s11`;
- PostgreSQL experimental disponible;
- backend disponible;
- backend conectado al entorno experimental;
- ausencia de carga activa durante restauración.

### 19.2 Usuarios

- usuarios sintéticos totales = 1000;
- usuarios Grupo A = 200;
- usuarios Grupo B = 800.

### 19.3 Categorías

- categorías activas = 1000;
- una categoría activa por usuario;
- propiedad usuario-categoría correcta.

### 19.4 Actividades

- actividades activas totales = 100.000;
- actividades Grupo A = 80.000;
- actividades Grupo B = 20.000.

### 19.5 Distribución individual

Grupo A:

- mínimo por usuario = 400;
- máximo por usuario = 400.

Grupo B:

- mínimo por usuario = 25;
- máximo por usuario = 25.

### 19.6 Concentración

La consulta de verificación deberá calcular, no solamente declarar:

- Grupo A = 20 % de usuarios;
- Grupo A = 80 % de actividades;
- Grupo B = 80 % de usuarios;
- Grupo B = 20 % de actividades.

### 19.7 Estado técnico

También deberá comprobarse:

- secuencias según procedimiento;
- índices esperados;
- ejecución de `ANALYZE`;
- script k6 esperado;
- commit esperado;
- nivel de VUs esperado.

Si cualquiera de las precondiciones obligatorias falla:

`la corrida no comienza`

o, si el problema se descubre después:

`la corrida deberá evaluarse como inválida`

No será suficiente verificar únicamente el total de filas.

---

## 20. Autenticación

### 20.1 Regla

Todas las solicitudes de creación deberán ejecutarse con usuarios autenticados.

Cada VU utilizará una identidad sintética independiente.

### 20.2 Preparación de tokens

Los JWT requeridos deberán prepararse antes del período medido de creación.

Los tokens deberán permanecer:

- en memoria;
- fuera del repositorio;
- fuera de archivos versionados;
- fuera de logs públicos;
- fuera de resultados comprometidos en Git.

### 20.3 Separación de métricas

Las solicitudes de autenticación deberán distinguirse de:

`POST /api/activities`

La métrica principal:

`activity_create_duration`

medirá únicamente la operación de creación.

El tiempo de login no formará parte de esa métrica.

Las métricas HTTP globales que incluyan autenticación no sustituirán la métrica específica de creación.

### 20.4 Fallos de autenticación

Los fallos de login deberán registrarse por separado.

Si no puede prepararse correctamente el conjunto de identidades necesarias:

`la corrida no deberá comenzar`

Un fallo en la preparación de JWT no será interpretado automáticamente como degradación de `POST /api/activities`.

### 20.5 Viabilidad todavía pendiente

La preparación de hasta 1000 identidades autenticadas todavía debe demostrarse operacionalmente.

Antes de la primera corrida oficial deberá comprobarse que:

- los 1000 usuarios pueden autenticarse;
- los JWT pueden prepararse sin exponer secretos;
- el proceso no contamina `activity_create_duration`;
- la asignación estratificada puede realizarse correctamente.

---

## 21. Condiciones de validez

Una corrida será considerada válida si:

- la restauración finaliza correctamente;
- PostgreSQL experimental está disponible;
- el backend está disponible;
- el backend utiliza la base experimental;
- la semilla cumple los conteos;
- la distribución 80/20 cumple las verificaciones;
- las categorías pertenecen a los usuarios correctos;
- los tokens necesarios se preparan correctamente;
- el script corresponde a la versión registrada;
- el nivel de VUs corresponde al previsto;
- la composición A/B corresponde al nivel;
- la duración es 60 segundos;
- `gracefulStop` es 30 segundos;
- la pausa es 1 segundo;
- k6 no falla como instrumento;
- los logs y métricas pueden recuperarse.

Una corrida NO será invalidada únicamente por:

- alta latencia;
- bajo throughput;
- HTTP 4xx;
- HTTP 5xx;
- éxito funcional bajo;
- saturación;
- iteraciones lentas.

Esos eventos pueden constituir resultados reales.

---

## 22. Criterios de invalidación

Una corrida podrá invalidarse por causas como:

- semilla incorrecta;
- distribución 80/20 incorrecta;
- conteos incorrectos;
- categorías asociadas a usuarios equivocados;
- backend conectado a la base equivocada;
- ejecución accidental contra `rachapro_db`;
- script incorrecto;
- composición A/B incorrecta;
- VUs distintos de los previstos;
- duración incorrecta;
- k6 abortado por fallo del generador;
- backend reiniciado accidentalmente durante una corrida medida;
- PostgreSQL experimental detenido por una causa externa;
- equipo reiniciado;
- evidencia necesaria perdida;
- error de preparación de tokens;
- restauración ejecutada mientras existían escrituras activas.

Toda invalidación deberá:

1. conservar la evidencia disponible;
2. quedar documentada;
3. explicar la causa;
4. identificar la corrida;
5. indicar si corresponde una corrida de reemplazo.

Nunca se eliminará silenciosamente una corrida.

---

## 23. Niveles no ejecutados o no válidos

Los ocho niveles constituyen el plan experimental.

Un nivel puede terminar en uno de tres estados.

### 23.1 Ejecutado y válido

Cumple las condiciones experimentales y puede incorporarse al análisis formal.

### 23.2 Ejecutado pero inválido

Existe una ejecución, pero incumple alguna condición de validez.

La evidencia se conserva.

No se utiliza como corrida válida sin una justificación explícita.

Cuando sea posible, se realizará una repetición de reemplazo.

### 23.3 No ejecutado correctamente

Puede ocurrir que un nivel previsto no pueda completarse por limitaciones del instrumento o del entorno.

En ese caso:

- no será clasificado como “sin degradación”;
- no contará como evidencia favorable a H0;
- se documentará la causa;
- se identificará el mayor nivel ejecutado válidamente;
- las conclusiones quedarán limitadas a dicho rango.

Ejemplo:

Si 1000 VUs no pueden ejecutarse por una limitación demostrada del generador, pero 750 VUs sí se ejecutan válidamente, el alcance formal será:

`hasta 750 VUs`

No se afirmará que RachaPro soporta ni que no soporta 1000 VUs basándose exclusivamente en una ejecución inválida.

---

## 24. Tratamiento de HTTP 4xx y 5xx

### 24.1 HTTP 4xx

Todos los HTTP 4xx serán registrados.

Se analizará si corresponden a:

- validación funcional;
- autenticación;
- autorización;
- payload;
- instrumento;
- comportamiento real del backend.

No serán descartados automáticamente.

### 24.2 HTTP 5xx

Los HTTP 5xx serán conservados como observaciones del sistema salvo evidencia que permita atribuirlos a un fallo externo o del instrumento.

No invalidarán automáticamente una corrida.

---

## 25. Iteraciones pendientes e interrumpidas

Las iteraciones que continúen después de los 60 segundos nominales podrán finalizar durante `gracefulStop`.

Las operaciones completadas durante ese margen formarán parte de la evidencia producida por k6.

Las iteraciones que no finalicen dentro del margen también deberán registrarse en la medida que el instrumento permita demostrarlo.

Antes de la primera corrida oficial se verificará qué salidas permiten distinguir:

- iteraciones completadas;
- iteraciones abortadas o interrumpidas;
- intervalo nominal;
- período de `gracefulStop`.

No se prometerá identificación individual de cada solicitud incompleta si Grafana k6 no proporciona dicha evidencia.

---

## 26. Reproducibilidad

Cada corrida deberá quedar vinculada a:

- commit Git;
- rama;
- fecha y hora;
- nivel;
- número de corrida;
- tipo de corrida;
- versión de Grafana k6;
- versión de PostgreSQL;
- versión de Java/JDK;
- versión del backend;
- sistema operativo;
- configuración relevante de Docker;
- CPU disponible;
- memoria disponible;
- endpoint;
- script utilizado;
- configuración de VUs;
- distribución A/B;
- duración;
- `gracefulStop`;
- pausa;
- resultado de verificación de semilla;
- resultado de verificación 80/20.

No se almacenarán secretos.

---

## 27. Recursos del entorno

Durante la preparación previa Docker reportó:

- 4 CPUs;
- 16.685.572.096 bytes de memoria.

Estos valores deberán registrarse nuevamente durante la campaña porque constituyen una observación previa y no una garantía de disponibilidad idéntica durante todas las ejecuciones.

Grafana k6, Spring Boot, Docker y PostgreSQL se ejecutarán localmente en el mismo equipo.

Por tanto, una degradación observada puede estar relacionada con:

- backend;
- PostgreSQL;
- generador;
- CPU;
- memoria;
- almacenamiento;
- recursos compartidos;
- combinación de factores.

El experimento por sí solo no permitirá atribuir causalidad.

---

## 28. Evidencia por corrida

Cada corrida oficial deberá conservar como mínimo:

- nivel de VUs;
- composición Grupo A / Grupo B;
- número de corrida;
- tipo de corrida;
- fecha y hora;
- commit;
- versión de k6;
- configuración utilizada;
- verificación previa;
- verificación 80/20;
- salida disponible de k6;
- resumen final;
- evidencia temporal disponible;
- P50;
- P90;
- P95;
- máximo;
- tasa de éxito;
- HTTP 2xx;
- HTTP 4xx;
- HTTP 5xx;
- throughput;
- iteraciones completas;
- información disponible sobre iteraciones interrumpidas;
- observaciones de validez.

---

## 29. Organización prevista de evidencia

La evidencia se almacenará dentro de:

`experimentos/semana11-crecimiento-post-activities/`

Estructura prevista:

```text
semana11-crecimiento-post-activities/
│
├── 00-preregistro.md
├── README.md
│
├── scripts/
│   ├── preparar-entorno.ps1
│   ├── restaurar-semilla.ps1
│   ├── verificar-semilla.ps1
│   ├── ejecutar-campana.ps1
│   └── crecimiento-post-activities.js
│
├── evidencia/
│   ├── 001-vu/
│   │   ├── corrida-01/
│   │   ├── corrida-02/
│   │   ├── corrida-03/
│   │   └── corrida-04/
│   ├── 010-vu/
│   ├── 050-vu/
│   ├── 100-vu/
│   ├── 250-vu/
│   ├── 500-vu/
│   ├── 750-vu/
│   └── 1000-vu/
│
└── resultados/
    ├── resumen.csv
    └── analisis.md
```
