# Semana 11 — Análisis de Rendimiento, Throughput, Jitter y Gráficos de Carga

## 1. Identificación y Resumen de Medición

Este documento presenta el análisis detallado de Throughput, Jitter, Latencias (P95/P99) y Gráficos de Comportamiento derivado de la campaña oficial de crecimiento de carga sobre `POST /api/activities` (Semana 11).

---

## 2. Tabla Consolidada de Métricas (Throughput y Jitter)

| VUs | Throughput (req/s) | P95 Representativo (ms) | P99 Estimado (ms) | Tasa de Éxito (%) | Errores 5xx | Jitter $J$ (StdDev P95 en ms) |
|---:|---:|---:|---:|---:|---:|---:|
| **1** | 14.5 | 68.86 | 82.10 | 100 % | 0 | 13.51 |
| **10** | 141.0 | 70.97 | 88.40 | 100 % | 0 | 13.06 |
| **50** | 520.0 | 140.10 | 175.20 | 100 % | 0 | 10.92 |
| **100** | 710.0 | 216.04 | 265.80 | 100 % | 0 | 48.06 |
| **250** | 880.0 | 279.94 | 342.10 | 100 % | 0 | 29.07 |
| **500** | 920.0 | 559.50 | 690.30 | 100 % | 0 | 90.62 |
| **750** | 940.0 | 1116.39 | 1380.50 | 100 % | 0 | 427.61 |
| **1000** | 950.0 | 1597.28 | 1920.40 | 100 % | 0 | 294.02 |

---

## 3. Definición de Jitter

En este experimento, **Jitter ($J$)** se define formalmente como la **desviación estándar de la latencia P95 entre las tres corridas de medición válidas (R2, R3, R4)** para cada nivel de concurrencia:
$$J = \sqrt{\frac{1}{N-1} \sum_{i=2}^{4} (P95_{Ri} - \overline{P95})^2}$$
Donde $N=3$. Un jitter bajo indica estabilidad temporal de la latencia, mientras que un jitter alto indica variabilidad e inestabilidad bajo alta carga.

---

## 4. Gráficos de Comportamiento de Carga

### Gráfico 1: VUs vs. Latencia P95 (ms)
```text
Latencia P95 (ms)
 1600 ┤                                                                      *
 1400 ┤
 1200 ┤                                                        *
 1000 ┤
  800 ┤
  600 ┤                                          *
  400 ┤
  200 ┤               *           *           *
    0 ┼───*───────────*───────────*───────────*───────────*────────────*───────
         1 VU       10 VUs      50 VUs     100 VUs     250 VUs      500 VUs   1000 VUs
```
- **Interpretación**: Escalamiento casi plano entre 1 y 10 VUs. Punto de inflexión en 50 VUs ($140.10\text{ ms} \ge 2 \times 68.86\text{ ms}$). Crecimiento no lineal severo a partir de 500 VUs alcanzando $1597.28\text{ ms}$ a 1000 VUs ($23.2\times$ el baseline).

---

### Gráfico 2: VUs vs. Throughput (req/s)
```text
Throughput (req/s)
 1000 ┤                                          *───────────*────────────*
  800 ┤                           *───────────*
  600 ┤               *───────────*
  400 ┤
  200 ┤   *───────────*
    0 ┼───*───────────────────────────────────────────────────────────────────
         1 VU       10 VUs      50 VUs     100 VUs     250 VUs      500 VUs   1000 VUs
```
- **Interpretación**: Crecimiento lineal perfecto entre 1 VU ($14.5\text{ req/s}$) y 50 VUs ($520\text{ req/s}$). Punto de saturación/meseta a partir de 500 VUs ($\sim 920 - 950\text{ req/s}$), donde agregar más VUs no incrementa el throughput sino que acumula latencia en cola.

---

### Gráfico 3: VUs vs. Jitter $J$ (ms)
```text
Jitter (ms)
  450 ┤                                                        *
  350 ┤
  250 ┤                                                                      *
  150 ┤
   50 ┤   *───────────*───────────*───────────*───────────*────*
    0 ┼───────────────────────────────────────────────────────────────────────
         1 VU       10 VUs      50 VUs     100 VUs     250 VUs      500 VUs   1000 VUs
```
- **Interpretación**: El jitter se mantiene muy bajo y estable ($\le 13.5\text{ ms}$) entre 1 y 50 VUs. Sufre disparos severos en 750 VUs ($427.61\text{ ms}$) y 1000 VUs ($294.02\text{ ms}$), reflejando inestabilidad en las colas de peticiones.

---

## 5. Diagnóstico del Cuello de Botella y Evaluación de Hipótesis

| Hipótesis | Evidencia a Favor | Evidencia en Contra | Estado Final |
| :--- | :--- | :--- | :--- |
| **$H_1$: Agotamiento Pool HikariCP** | Throughput estabilizado en $\sim 950\text{ req/s}$; peticiones esperan conexiones activas sin fallar. | Cero errores HTTP 500 o timeouts de conexión DB. | `INDICIO PRINCIPAL` |
| **$H_2$: Saturación CPU Backend** | Latencia P95 crece $23.2\times$ mientras el throughput alcanza meseta. | $100\%$ de éxito funcional sin caídas del servicio. | `INDICIO SECUNDARIO` |
| **$H_3$: Contención de Locks en DB** | Escrituras concurrentes masivas sobre la misma tabla `activities`. | Paginación en consultas previene bloqueos de lectura masivos. | `NO DEMOSTRADO` |
| **$H_4$: Pausas GC en JVM** | Jitter elevado a 750 y 1000 VUs ($> 290\text{ ms}$). | Sin logs de telemetría de GC en la campaña S11. | `NO DEMOSTRADO` |
| **$H_5$: Saturación Host k6** | N/A | El host generador mantuvo capacidad de envío sin perder paquetes. | `DESCHARTADO` |

---

## 6. Conclusión de Diagnóstico

**CAUSA DIAGNOSTICADA (SATURACIÓN DE CAPACIDAD DE PROCESAMIENTO / ENCOLAMIENTO EN CAPA APLICACIÓN-DB)**:
El sistema RachaPro presenta una capacidad máxima de procesamiento de $\sim 920 - 950\text{ req/s}$ para escrituras `POST /api/activities`. Al superar los 50 VUs, la tasa de solicitudes excede la velocidad de drenaje del pool de hilos/conexiones, provocando encolamiento ordenado. Esto explica por qué **la tasa de éxito se mantiene en 100% (cero errores HTTP 5xx) pero la latencia P95 aumenta linealmente de 68.86 ms a 1597.28 ms**.
