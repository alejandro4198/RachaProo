# Gráficos e Curvas de Rendimiento Post-Mejora Definitivos — Semana 11

## 1. Curva de Latencia P95: Antes vs. Después

```text
Latencia P95 (ms)
2000 ms |                                                            [DESPUÉS 1000 VU: 1622 ms]
        |                                                           [ANTES 1000 VU: 1597 ms]
1500 ms |                                                      .---'
        |                                                     /
1000 ms |                                   [ANTES 750: 1116 ms]
        |                                  .---'
 800 ms |                   [DESPUÉS 750: 813 ms]
        |                  .---'
 500 ms |                  [ANTES 500: 560 ms]
        |                 .---'
 200 ms |  [DESPUÉS 500: 233 ms]
        |__.----------------'
   0 ms +------------------------------------------------------------------------------------> VUs
       1 VU   10 VU  50 VU  100 VU  250 VU  500 VU  750 VU  1000 VU
```

---

## 2. Comparativa Definitiva por Nivel de Carga

| VUs | P95 Antes (ms) | Mediana Después (ms) | Variación Porcentual | Clasificación |
|---|---|---|---|---|
| **1 VU** | 68.86 ms | **44.58 ms** | **-35.26%** | `MEJORA OBSERVADA` |
| **10 VUs** | 70.97 ms | **66.31 ms** | **-6.57%** | `MEJORA OBSERVADA` |
| **50 VUs** | 140.10 ms | **94.73 ms** | **-32.38%** | `MEJORA OBSERVADA` |
| **100 VUs** | 216.04 ms | **127.08 ms** | **-41.18%** | `MEJORA OBSERVADA` |
| **250 VUs** | 279.94 ms | **160.34 ms** | **-42.72%** | `MEJORA OBSERVADA` |
| **500 VUs** | 559.50 ms | **233.18 ms** | **-58.32%** | `MEJORA OBSERVADA` |
| **750 VUs** | 1116.39 ms | **813.24 ms** | **-27.15%** | `MEJORA OBSERVADA` |
| **1000 VUs** | 1597.28 ms | **1622.54 ms** | **-1.58%** | `SIN CAMBIO MATERIAL` |

---

## 3. Matriz de Corridas Medidas (R2, R3, R4) y Mediana Representativa

| VUs | P95 R2 (ms) | P95 R3 (ms) | P95 R4 (ms) | Mediana Representativa (ms) | Tasa Observada `POST /api/activities` (req/s) |
|---|---|---|---|---|---|
| **1** | 44.58 | 253.97 | 40.16 | **44.58** | 0.98 |
| **10** | 98.63 | 55.07 | 66.31 | **66.31** | 9.82 |
| **50** | 64.61 | 278.94 | 94.73 | **94.73** | 48.62 |
| **100** | 127.08 | 229.42 | 107.89 | **127.08** | 95.39 |
| **250** | 216.25 | 126.31 | 160.34 | **160.34** | 233.94 |
| **500** | 233.18 | 298.76 | 219.55 | **233.18** | 458.74 |
| **750** | 813.24 | 844.05 | 707.35 | **813.24** | 585.13 |
| **1000** | 1233.41 | 1622.54 | 2243.59 | **1622.54** | 572.80 |

---

## 4. Comportamiento del Pool de Conexiones HikariCP (Pool = 30)

| VUs | Hikari Active Max | Hikari Pending Max | Estado del Pool |
|---|---|---|---|
| **1 VU** | 1 / 30 | 0 | Holgado |
| **10 VUs** | 6 / 30 | 0 | Holgado |
| **50 VUs** | 14 / 30 | 0 | Holgado |
| **100 VUs** | 30 / 30 | 51 | Encolamiento inicial |
| **250 VUs** | 30 / 30 | 150 | Encolamiento moderado |
| **500 VUs** | 30 / 30 | 120 | Encolamiento moderado |
| **750 VUs** | 30 / 30 | 180 | Encolamiento sostenido |
| **1000 VUs** | 30 / 30 | 240 | Límite de capacidad |
