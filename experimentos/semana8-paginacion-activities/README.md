# Semana 8 — Experimento de paginación de actividades

## 1. Objetivo

Evaluar experimentalmente el efecto de reducir la cantidad de actividades retornadas por solicitud en el endpoint de consulta de actividades de RachaPro.

El experimento compara:

- **CONTROL:** `GET /api/activities`
  - Retorna aproximadamente 1000 actividades activas por usuario sintético.

- **PAGED:** `GET /api/activities/paged?page=0&size=100`
  - Retorna la primera página con 100 actividades.

La comparación busca medir el comportamiento observado bajo carga manteniendo constantes, en lo posible, las demás condiciones del sistema.

---

## 2. Alcance de la afirmación

Este experimento mide:

> El efecto observado de limitar cada respuesta de aproximadamente 1000 registros a 100 registros bajo el escenario definido.

Este experimento **no mide**:

- el tiempo necesario para recuperar las mismas 1000 actividades mediante 10 páginas;
- el rendimiento completo de la aplicación Android;
- la experiencia de usuario final;
- un SLA de producción;
- la eliminación del cuello de botella previamente observado en adquisición de conexiones;
- la causa interna exacta de la mejora;
- el comportamiento universal fuera del entorno ensayado.

No existe un umbral formal previamente definido para considerar aprobado o fallido este GET. Por tanto, el resultado es **comparativo**, no una certificación de cumplimiento de SLA.

---

## 3. Cambio experimental

La implementación mantiene sin modificar el endpoint existente:

`GET /api/activities`

y añade un endpoint independiente:

`GET /api/activities/paged?page=0&size=100`

### Repositorio

Se añadió una variante del repositorio que acepta `Pageable`.

### Servicio

Se añadió:

`findPageByUserId(userId, page, size)`

utilizando:

`PageRequest.of(page, size)`

El método conserva:

`@Transactional(readOnly = true)`

y el mismo mapeo:

`ActivityEntity -> ActivityResponse`

### Controlador

Se añadió:

`GET /api/activities/paged`

con:

- `page` por defecto: `0`;
- `size` por defecto: `100`;
- `page >= 0`;
- `size` permitido: `1..200`.

El endpoint original no fue sustituido.

---

## 4. Variables mantenidas

Durante las corridas formales no se modificaron deliberadamente:

- backend;
- PostgreSQL;
- configuración de Hikari;
- índices;
- alcance transaccional;
- DTO de actividad;
- aplicación Android;
- usuarios sintéticos;
- cantidad de VUs;
- duración configurada;
- script de carga.

La variable experimental principal fue la cantidad de registros retornados por cada solicitud:

- CONTROL: aproximadamente 1000;
- PAGED: 100.

---

## 5. Configuración de carga

Script:

`scripts/comparacion-paginacion.js`

Configuración formal:

- executor: `constant-vus`;
- VUs: `499`;
- duración: `60s`;
- graceful stop: `5s`;
- usuario reservado para validaciones manuales:
  `loadtest001@rachapro.test`;
- usuarios k6:
  `loadtest002@rachapro.test` hasta `loadtest500@rachapro.test`.

Antes del escenario de carga, `setup()` autentica los 499 usuarios sintéticos.

Por esta razón:

`http_reqs - iterations = 499`

en las corridas formales válidas.

Los 499 requests adicionales corresponden a autenticaciones realizadas durante `setup()`.

---

## 6. Métricas observadas

El script registra, entre otras:

- `activity_get_duration`;
- `activity_get_waiting`;
- `activity_get_blocked`;
- `activity_get_connecting`;
- `activity_get_sending`;
- `activity_get_receiving`;
- `activity_get_success`;
- `activity_count_ok`;
- `checks`;
- `http_req_failed`;
- `http_reqs`;
- `iterations`.

Los tiempos de las métricas personalizadas del GET corresponden al endpoint de actividades del escenario y no a los logins ejecutados durante `setup()`.

---

# 7. Evidencia experimental

## 7.1 Evidencia formal de rendimiento

Estas son las únicas corridas utilizadas para calcular el resultado comparativo.

| Corrida | Variante | Avg | Mediana | p90 | p95 | Iteraciones | Checks fallidos |
|---|---|---:|---:|---:|---:|---:|---:|
| CONTROL-04 | Control | 3238.21 ms | 3290.71 ms | 3474.35 ms | 3568.09 ms | 9438 | 0 |
| CONTROL-05 | Control | 3447.80 ms | 3511.56 ms | 3940.55 ms | 4066.02 ms | 8882 | 0 |
| PAGED-01 | Paged | 668.07 ms | 641.43 ms | 862.45 ms | 940.08 ms | 44837 | 0 |
| PAGED-02 | Paged | 634.13 ms | 617.41 ms | 822.14 ms | 864.61 ms | 47159 | 0 |

Todas las corridas anteriores presentaron:

- exit code `0`;
- JSON de resultados;
- `checks.rate = 1`;
- cero checks fallidos;
- `http_req_failed.rate = 0`;
- conteo esperado de actividades correcto;
- marcadores de inicio y fin del escenario.

Estado:

**✅ EVIDENCIA EXPERIMENTAL FORMAL**

---

## 7.2 Primera pareja: CONTROL-04 -> PAGED-01

Cambio observado:

- promedio: **-79.37 %**;
- mediana: **-80.51 %**;
- p90: **-75.18 %**;
- p95: **-73.65 %**.

Iteraciones:

- CONTROL-04: `9438`;
- PAGED-01: `44837`.

PAGED-01 completó aproximadamente `4.75x` las iteraciones de CONTROL-04.

---

## 7.3 Réplica con orden inverso: PAGED-02 -> CONTROL-05

Cambio observado al comparar CONTROL-05 con PAGED-02:

- promedio: **-81.61 %**;
- mediana: **-82.42 %**;
- p90: **-79.14 %**;
- p95: **-78.74 %**.

Iteraciones:

- CONTROL-05: `8882`;
- PAGED-02: `47159`.

PAGED-02 completó aproximadamente `5.31x` las iteraciones de CONTROL-05.

La inversión del orden permitió comprobar que el patrón general se reprodujo también cuando la variante paginada se ejecutó antes del segundo control.

---

## 7.4 Resumen de las dos corridas por variante

Promedio de la métrica reportada por las dos corridas de cada variante:

| Métrica | CONTROL | PAGED | Cambio |
|---|---:|---:|---:|
| Avg | 3343.01 ms | 651.10 ms | -80.52 % |
| Mediana reportada | 3401.13 ms | 629.42 ms | -81.49 % |
| p90 reportado | 3707.45 ms | 842.29 ms | -77.28 % |
| p95 reportado | 3817.06 ms | 902.35 ms | -76.36 % |
| Iteraciones promedio | 9160 | 45998 | ~5.02x |

Los valores de mediana, p90 y p95 de esta tabla son **promedios de las métricas reportadas por cada corrida**.

No representan una mediana ni percentiles globales calculados sobre todas las observaciones individuales.

---

# 8. Evidencia metodológica

Las siguientes ejecuciones no se utilizan para calcular rendimiento, pero se conservan porque documentan cómo se validó y endureció el procedimiento experimental.

## CONTROL-01

Estado actual:

**✅ EVIDENCIA METODOLÓGICA**

Resultado histórico de la ejecución:

**Inválida para métricas de rendimiento.**

Durante el primer intento, PowerShell utilizaba:

`$ErrorActionPreference = "Stop"`

La salida informativa que k6 escribía por `stderr` fue tratada por PowerShell como `NativeCommandError`, afectando el mecanismo de ejecución.

Artefactos conservados:

- `logs/formal-control-01.stdout.txt`;
- `logs/formal-control-01.stderr.txt`.

Utilidad:

Permitió identificar que el mecanismo de captura debía distinguir entre mensajes escritos por k6 a `stderr` y el resultado real del proceso.

No se utiliza para calcular métricas.

---

## CONTROL-02

Estado actual:

**✅ INCIDENTE METODOLÓGICO DOCUMENTADO**

Resultado histórico:

**Inválido para métricas de rendimiento.**

Se intentó ejecutar k6 mediante `Start-Process`.

El proceso terminó con:

`exit code = 2`

y no produjo los artefactos esperados.

No existen archivos `formal-control-02.*` porque la ejecución no llegó a generar evidencia utilizable.

Utilidad:

Permitió descartar ese mecanismo de ejecución para este experimento.

No se inventan ni reconstruyen artefactos inexistentes.

---

## CONTROL-03

Estado actual:

**✅ EVIDENCIA DE VALIDACIÓN DE PRECONDICIONES**

Resultado histórico:

**Inválido para métricas de rendimiento.**

El `setup()` abortó explícitamente porque:

`K6_PASSWORD`

no estaba disponible.

El JSON generado registra las métricas del escenario como `null`, confirmando que la carga formal no comenzó.

Artefactos conservados:

- `resultados/formal-control-03.json`;
- `logs/formal-control-03.stdout.txt`;
- `logs/formal-control-03.stderr.txt`.

Utilidad:

Demuestra que una ejecución incompleta fue detectada y excluida del análisis en lugar de interpretarse como resultado de rendimiento.

No se utiliza para calcular métricas.

---

# 9. Smoke tests y validación del harness

## Smoke funcional

Se realizaron pruebas preliminares con baja carga para comprobar ambos caminos:

- `resultados/smoke-control.json`;
- `resultados/smoke-paged.json`.

Estas pruebas validaron funcionalidad y conteos esperados.

Estado:

**🧪 SMOKE FUNCIONAL**

No se utilizan como resultados formales de rendimiento.

---

## Wrapper smoke directo

Artefactos:

- `resultados/wrapper-smoke-direct.json`;
- `logs/wrapper-smoke-direct.stdout.txt`;
- `logs/wrapper-smoke-direct.stderr.txt`.

Esta ejecución confirmó que el mecanismo directo podía:

- ejecutar k6;
- finalizar con exit code `0`;
- producir JSON;
- producir logs;
- ejecutar checks correctamente.

Estado:

**✅ VALIDACIÓN TÉCNICA DEL HARNESS**

No se utiliza para calcular el resultado de rendimiento.

La presencia de texto `NativeCommandError` generado por PowerShell al representar contenido procedente de `stderr` no debe interpretarse aisladamente como fallo de k6. La validez de una corrida requiere revisar conjuntamente exit code, resultados, checks y evidencia del escenario.

---

# 10. Interpretación de `constant-vus`

El experimento utiliza un modelo cerrado.

Cada VU:

1. realiza una petición;
2. espera su respuesta;
3. inicia la siguiente iteración.

Por ello, una variante más rápida permite completar más iteraciones durante el mismo intervalo.

El aumento de iteraciones observado en PAGED es un **resultado experimental** y no significa que se haya configurado deliberadamente una tasa de llegada aproximadamente cinco veces mayor.

---

# 11. Waiting

En CONTROL-04:

- duración promedio: `3238.21 ms`;
- waiting promedio: `3235.96 ms`.

En CONTROL-05:

- duración promedio: `3447.80 ms`;
- waiting promedio: `3443.49 ms`.

En PAGED-01:

- duración promedio: `668.07 ms`;
- waiting promedio: `667.77 ms`.

En PAGED-02:

- duración promedio: `634.13 ms`;
- waiting promedio: `633.66 ms`.

En todas las corridas, `waiting` representa prácticamente todo el tiempo de la solicitud.

Sin embargo:

**`waiting` de k6 no equivale directamente a tiempo de Hikari.**

Puede incluir el tiempo transcurrido antes de recibir el primer byte del servidor, incluyendo diferentes partes del procesamiento backend.

La relación con la adquisición de conexiones Hikari proviene del diagnóstico previo de Semana 8 y no puede deducirse únicamente de esta métrica.

---

# 12. Resultado experimental

Bajo las condiciones ensayadas —499 VUs concurrentes, escenario `constant-vus` de 60 segundos y aproximadamente 1000 actividades activas por usuario sintético— limitar cada respuesta a una primera página de 100 registros estuvo asociado consistentemente con una reducción aproximada del 80 % en la latencia media y mediana.

El efecto se reprodujo en dos ejecuciones por variante y en ambos órdenes de comparación.

La variante paginada también completó aproximadamente cinco veces más iteraciones bajo el mismo número de VUs.

Debido al modelo cerrado `constant-vus`, este aumento se interpreta como consecuencia de la menor duración de las iteraciones y no como una tasa de llegada fija equivalente.

---

# 13. Decisión

Estado del candidato:

**✅ CANDIDATO VALIDADO EXPERIMENTALMENTE PARA CONTINUAR**

La evidencia justifica continuar evaluando la paginación como mecanismo de mejora para la consulta de actividades.

Esto no significa todavía que la paginación esté integrada en Android ni que deba considerarse automáticamente una solución final de producción.

Antes de una integración definitiva deben abordarse, entre otros aspectos:

- contrato de paginación para Android;
- comportamiento UX al cargar páginas;
- estabilidad del orden;
- navegación entre páginas;
- estados offline;
- interacción con Room;
- sincronización;
- pruebas automatizadas;
- impacto de recuperar múltiples páginas.

---

# 14. Limitaciones

1. Solo se evaluó `page=0&size=100`.

2. No se midió el tiempo para recuperar las mismas 1000 actividades mediante múltiples páginas.

3. El repositorio ordena por `dueDateEpochDay` sin un segundo criterio explícito de desempate.

4. No se demostró estabilidad global de los límites de página cuando existen valores iguales de `dueDateEpochDay`.

5. Android continúa utilizando el endpoint original durante esta fase.

6. Las pruebas se realizaron en el entorno local utilizado para el experimento.

7. Se realizaron dos corridas formales por variante; esto mejora la evidencia respecto de una corrida única, pero no constituye una caracterización estadística exhaustiva.

8. No se modificó Hikari durante este experimento.

9. No se modificaron índices durante este experimento.

10. No se modificó el alcance transaccional durante este experimento.

11. El experimento demuestra asociación experimental bajo las condiciones controladas; no demuestra por sí mismo la causa interna exacta de la diferencia observada.

---

# 15. Clasificación final de artefactos

| Artefacto | Clasificación | Uso |
|---|---|---|
| `scripts/comparacion-paginacion.js` | ✅ Artefacto reproducible | Repetir el experimento |
| `formal-control-04.*` | ✅ Evidencia formal | Métricas |
| `formal-control-05.*` | ✅ Evidencia formal | Métricas |
| `formal-paged-01.*` | ✅ Evidencia formal | Métricas |
| `formal-paged-02.*` | ✅ Evidencia formal | Métricas |
| `formal-control-01.*` | ✅ Evidencia metodológica | Evolución del procedimiento |
| `formal-control-02.*` | ✅ Incidente documentado | Sin artefactos generados |
| `formal-control-03.*` | ✅ Evidencia metodológica | Validación de precondiciones |
| `wrapper-smoke-direct.*` | ✅ Validación del harness | Verificación técnica |
| `smoke-control.json` | 🧪 Smoke | Validación funcional |
| `smoke-paged.json` | 🧪 Smoke | Validación funcional |

---

## 16. Principio de trazabilidad

Una ejecución inválida para rendimiento no se convierte retroactivamente en una medición válida.

Su valor se conserva como evidencia del procedimiento, del control de errores o de las precondiciones experimentales.

De esta forma se mantiene separada:

- la evidencia de rendimiento;
- la evidencia metodológica;
- la validación funcional;
- la interpretación;
- la decisión.
