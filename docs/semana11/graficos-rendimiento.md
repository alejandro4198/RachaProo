 # Semana 11 — Análisis de Rendimiento, Throughput, Jitter y Gráficos de Carga

## 1. Identificación y Resumen de Medición

Este documento presenta el análisis derivado de la campaña oficial de crecimiento de carga sobre `POST /api/activities` (Semana 11) para el proyecto RachaPro.

---

## 2. Tabla Consolidada de Métricas (Throughput y Jitter)

| VUs | Throughput (req/s) | P95 Representativo (ms) | P99 | Tasa de Éxito (%) | Errores 5xx | Jitter $J$ Post-Hoc (ms) |
|---:|---:|---:|:---:|---:|---:|---:|
| **1** | `THROUGHPUT REPRESENTATIVO: PENDIENTE POR FALTA DE EVIDENCIA` | 68.86 | `P99 NO DISPONIBLE EN LA EVIDENCIA HISTÓRICA` | 100 % | 0 | 13.51 |
| **10** | `THROUGHPUT REPRESENTATIVO: PENDIENTE POR FALTA DE EVIDENCIA` | 70.97 | `P99 NO DISPONIBLE EN LA EVIDENCIA HISTÓRICA` | 100 % | 0 | 13.06 |
| **50** | `THROUGHPUT REPRESENTATIVO: PENDIENTE POR FALTA DE EVIDENCIA` | 140.10 | `P99 NO DISPONIBLE EN LA EVIDENCIA HISTÓRICA` | 100 % | 0 | 10.92 |
| **100** | `THROUGHPUT REPRESENTATIVO: PENDIENTE POR FALTA DE EVIDENCIA` | 216.04 | `P99 NO DISPONIBLE EN LA EVIDENCIA HISTÓRICA` | 100 % | 0 | 48.06 |
| **250** | `THROUGHPUT REPRESENTATIVO: PENDIENTE POR FALTA DE EVIDENCIA` | 279.94 | `P99 NO DISPONIBLE EN LA EVIDENCIA HISTÓRICA` | 100 % | 0 | 29.07 |
| **500** | `THROUGHPUT REPRESENTATIVO: PENDIENTE POR FALTA DE EVIDENCIA` | 559.50 | `P99 NO DISPONIBLE EN LA EVIDENCIA HISTÓRICA` | 100 % | 0 | 90.62 |
| **750** | `THROUGHPUT REPRESENTATIVO: PENDIENTE POR FALTA DE EVIDENCIA` | 1116.39 | `P99 NO DISPONIBLE EN LA EVIDENCIA HISTÓRICA` | 100 % | 0 | 427.61 |
| **1000** | `NO VERIFICADO` (solo evidencia disponible: R1/warmup S11-VU1000-R1 → 540.65 req/s, `experimentos/semana11-crecimiento-post-activities/evidencia/oficial/S11-VU1000-R1/muestras.json`, filtrado `POST /api/activities`) | 1597.28 | `P99 NO DISPONIBLE EN LA EVIDENCIA HISTÓRICA` | 100 % | 0 | 294.02 |

**Fuente de extracción**: `experimentos/semana11-mejoras-rendimiento/scripts/extraer-throughput-historico.py` → `experimentos/semana11-mejoras-rendimiento/02-throughput-jitter/throughput-historico.csv`. Solo R2-R4 son válidos como medición representativa según metodología; para la evidencia accesible actualmente solo existe R1 (warmup) para 1000 VUs.

---

## 3. Definición de Jitter Post-Hoc

En este análisis post-hoc secundario, **Jitter ($J$)** se calcula como la **desviación estándar de la latencia P95 entre las tres corridas de medición válidas (R2, R3, R4)** para cada nivel de concurrencia (cuando existen):
$$J = \sqrt{\frac{1}{N-1} \sum_{i=2}^{4} (P95_{Ri} - \overline{P95})^2}$$
Donde $N=3$. Esta métrica **NO** formó parte del prerregistro original; se clasifica como `ANÁLISIS SECUNDARIO POST HOC`. Los valores mostrados provienen del documento histórico A1, pero **no se han verificado desde las muestras.json completas de todas las corridas** con el script de extracción.

---

## 4. Gráficos de Comportamiento de Carga

Los gráficos ASCII anteriores se consideran **no concluyentes como evidencia principal**. Se generarán gráficos reales reproducibles desde datos verificados en `experimentos/semana11-mejoras-rendimiento/graficos/` usando `experimentos/semana11-mejoras-rendimiento/scripts/generar-graficos.py`.

- Gráfico 1: VUs vs. Latencia P95 (ms) — Fuente: `A1-resultados-crecimiento.md` (P95 representativo). No se infiere throughput.
- Gráfico 2: VUs vs. Throughput (req/s) — `THROUGHPUT REPRESENTATIVO: PENDIENTE POR FALTA DE EVIDENCIA` para la mayoría de niveles; solo se documenta el valor de R1 (warmup) para 1000 VUs como no representativo.

---

## 5. Diagnóstico del Cuello de Botella y Evaluación de Hipótesis

| Hipótesis | Evidencia a Favor | Evidencia en Contra | Estado Final |
| :--- | :--- | :--- | :--- |
| **$H_1$: Agotamiento Pool HikariCP** | Throughput observado como meseta no está verificado con evidencia completa de R2-R4. | Sin telemetría sincronizada de conexiones activas/pendientes de HikariCP en la campaña S11. | `NO DEMOSTRADO` |
| **$H_2$: Saturación CPU Backend** | Latencia P95 crece con VUs. | Sin telemetría sincronizada de CPU del backend durante la ejecución. | `NO DEMOSTRADO` |
| **$H_3$: Contención de Locks en DB** | Escrituras concurrentes sobre `activities`. | Sin telemetría sincronizada de locks/waits de PostgreSQL. | `NO DEMOSTRADO` |
| **$H_4$: Pausas GC en JVM** | Variabilidad de latencia elevada a 750/1000 VUs. | Sin registros de logs de GC/JVM durante la campaña. | `NO DEMOSTRADO` |
| **$H_5$: Saturación Host k6** | N/A | Sin telemetría sincronizada de CPU/memoria del equipo generador. | `NO DEMOSTRADO` |

---

## 6. Conclusión de Diagnóstico

`CAUSA NO AISLADA POR FALTA DE TELEMETRÍA SINCRONIZADA EN CAMPAÑA HISTÓRICA`

**Conclusión Metodológica**: La campaña original demuestra la degradación de latencia P95 a partir de 50 VUs y un posible comportamiento de meseta, pero **los valores de throughput reportados anteriormente (~950 req/s) no son reproducibles con la evidencia k6 actualmente accesible**. Solo se verifica que para `S11-VU1000-R1` (R1/warmup) se obtuvieron ~32452 requests `POST /api/activities` en ~60.02 s → ~540.65 req/s. Se requiere evidencia de R2-R4 para todos los niveles o una nueva campaña instrumentada para aislar la causa raíz.
