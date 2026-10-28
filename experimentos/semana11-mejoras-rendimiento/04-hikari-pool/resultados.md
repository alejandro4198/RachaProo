# Resultados del Spike — Impacto del Pool HikariCP (`maximumPoolSize`)

## 1. Identificación y Resumen

Este documento registra los resultados consolidados de las corridas experimentales ejecutadas variando el parámetro `maximumPoolSize` (niveles 10, 20 y 30) bajo cargas de 50 VUs y 250 VUs sobre `POST /api/activities`. Las corridas R1 se trataron como calentamiento y se excluyeron del cálculo representativo (se usó la media aritmética entre R2 y R3: $\text{Resumen} = \frac{R_2 + R_3}{2}$).

---

## 2. Consolidado de Resultados (Excluyendo Warmup R1)

| Pool Size | VUs | Run R2 P95 (ms) | Run R3 P95 (ms) | Resumen P95 (ms) | Resumen Throughput (req/s) | Hikari Pending Max (R2/R3) |
| :---: | :---: | :---: | :---: | :---: | :---: | :---: |
| **10** | **50** | 284.61 | 224.76 | **254.69** | **20.40** | 21 / 33 |
| **20** | **50** | 198.96 | 175.96 | **187.46** | **21.37** | 16 / 27 |
| **30** | **50** | 72.40 | 145.04 | **108.72** | **25.55** | 0 / 15 |
| **10** | **250** | 327.28 | 1653.66 | **990.47** | **17.74** | 0 / 0 |
| **20** | **250** | 375.81 | 1274.41 | **825.11** | **16.95** | 0 / 0 |
| **30** | **250** | 444.14 | 1158.83 | **801.49** | **23.35** | 0 / 0 |

---

## 3. Análisis de Mejora Relativa a 50 VUs

- **Pool 10 → Pool 20**:
  - Reducción de P95: $\sim 26.3\%$ ($254.69\text{ ms} \rightarrow 187.46\text{ ms}$).
  - Mejora de Throughput: $\sim 4.8\%$ ($20.40 \rightarrow 21.37\text{ req/s}$).
- **Pool 20 → Pool 30**:
  - Reducción de P95: $\sim 42.0\%$ ($187.46\text{ ms} \rightarrow 108.72\text{ ms}$).
  - Mejora de Throughput: $\sim 19.6\%$ ($21.37 \rightarrow 25.55\text{ req/s}$).
- **Pool 10 → Pool 30 (Impacto Total)**:
  - Reducción de P95: $\sim 57.3\%$ ($254.69\text{ ms} \rightarrow 108.72\text{ ms}$).
  - Mejora de Throughput: $\sim 25.2\%$ ($20.40 \rightarrow 25.55\text{ req/s}$).

---

## 4. Análisis a 250 VUs (Variabilidad y Segundo Factor)

A 250 VUs, la discrepancia entre R2 y R3 (p. ej., P95 de 327.28 ms en R2 vs. 1653.66 ms en R3) demuestra una alta variabilidad temporal que no se explica exclusivamente por el tamaño del pool Hikari (cuyas conexiones pendientes se mantuvieron en 0). Esto confirma que a 250 VUs opera un **segundo factor o cuello de botella independiente** (asociado a colas de red, conector Tomcat o concurrencia del cliente k6).

---

## 5. Conclusiones Metodológicas

1. **HikariCP**: `CAUSA CONTRIBUYENTE DEMOSTRADA A 50 VUs` (Se demostró empíricamente que expandir el pool mitiga la congestión de conexiones pendientes y reduce el P95).
2. **Pool 30**: `MEJOR CONFIGURACIÓN OBSERVADA ENTRE 10/20/30` (Ofrece el mejor compromiso experimental para 50 VUs, sin representar una optimización universal definitiva).
3. **250 VUs**: `SEGUNDO FACTOR / CAUSA AÚN NO AISLADA` (La variabilidad en alta carga requiere análisis adicional de colas de red/Tomcat).
