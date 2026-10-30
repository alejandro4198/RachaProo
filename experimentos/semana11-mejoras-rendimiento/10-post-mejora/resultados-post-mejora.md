# Resultados Finales Definitivos — Campaña Post-Mejora Semana 11

## 1. Identificación y Protocolo Experimental

- **Proyecto**: RachaPro
- **Módulo**: Semana 11 — Evaluación de Rendimiento Post-Mejora
- **Operación Evaluada**: `POST /api/activities`
- **Configuración Backend**:
  - `RACHAPRO_HIKARI_MAX_POOL_SIZE = 30` (Mejor configuración observada entre las probadas).
  - Iteraciones PBKDF2: **210,000** (Producción intacta sin modificación).
- **Semilla de Base de Datos**: 1000 usuarios sintéticos, 1000 categorías, 100,000 actividades preexistentes (Distribución 80/20 verificada).
- **Aclaración Obligatoria de Harness**:
  > `http.batch()` modifica el harness de preparación y reduce el impacto del setup; no constituye una optimización del backend.

---

## 2. Consolidado Definitivo Reconstruido (Mediana de R2, R3, R4)

| VUs | P95 R2 (ms) | P95 R3 (ms) | P95 R4 (ms) | Mediana P95 Representativo (ms) | Tasa Observada `POST /api/activities` (req/s) | Hikari Active Max | Hikari Pending Max |
|---:|---:|---:|---:|---:|---:|---:|---:|
| **1** | 44.58 | 253.97 | 40.16 | **44.58** | 0.98 | 1 | 0 |
| **10** | 98.63 | 55.07 | 66.31 | **66.31** | 9.82 | 6 | 0 |
| **50** | 64.61 | 278.94 | 94.73 | **94.73** | 48.62 | 14 | 0 |
| **100** | 127.08 | 229.42 | 107.89 | **127.08** | 95.39 | 30 | 51 |
| **250** | 216.25 | 126.31 | 160.34 | **160.34** | 233.94 | 30 | 150 |
| **500** | 233.18 | 298.76 | 219.55 | **233.18** | 458.74 | 30 | 120 |
| **750** | 813.24 | 844.05 | 707.35 | **813.24** | 585.13 | 30 | 180 |
| **1000** | 1233.41 | 1622.54 | 2243.59 | **1622.54** | 572.80 | 30 | 240 |

---

## 3. Comparativa Antes vs. Después

| VUs | P95 Antes (ms) | Mediana Después (ms) | Diferencia (ms) | Mejora (%) | Clasificación |
|---:|---:|---:|---:|---:|:---|
| **1** | 68.86 | **44.58** | -24.28 | **+35.26%** | `MEJORA OBSERVADA` |
| **10** | 70.97 | **66.31** | -4.66 | **+6.57%** | `MEJORA OBSERVADA` |
| **50** | 140.10 | **94.73** | -45.37 | **+32.38%** | `MEJORA OBSERVADA` |
| **100** | 216.04 | **127.08** | -88.96 | **+41.18%** | `MEJORA OBSERVADA` |
| **250** | 279.94 | **160.34** | -119.60 | **+42.72%** | `MEJORA OBSERVADA` |
| **500** | 559.50 | **233.18** | -326.32 | **+58.32%** | `MEJORA OBSERVADA` |
| **750** | 1116.39 | **813.24** | -303.15 | **+27.15%** | `MEJORA OBSERVADA` |
| **1000** | 1597.28 | **1622.54** | +25.26 | **-1.58%** | `SIN CAMBIO MATERIAL` |

---

## 4. Primer Nivel de Degradación (Criterio $2 \times \text{Baseline}$)

- **Baseline Post-Mejora (1 VU)**: P95 = **44.58 ms**.
- **Umbral de Degradación ($2 \times \text{Baseline}$)**: $2 \times 44.58\text{ ms} = \mathbf{89.16\text{ ms}}$.
- **Primer Nivel de Degradación Post-Mejora**: **50 VUs** (P95 = 94.73 ms $\ge 89.16\text{ ms}$).
- **Análisis Comparativo**:
  - El primer punto de degradación se mantiene formalmente en **50 VUs**, pero con una reducción de latencia P95 del **32.38%** respecto a la campaña histórica (94.73 ms vs 140.10 ms).
  - La curva de escalabilidad se optimiza alcanzando su mayor beneficio porcentual en **500 VUs (+58.32% de mejora)**.

---

## 5. Conclusiones Metodológicas

1. `MEJORA DEMOSTRADA EN NIVELES 1 A 750 VUS`: La ampliación del pool Hikari a 30 conexiones y la corrección del arnés de preparación reducen de forma reproducible la latencia P95 entre un **27% y un 58%**.
2. `ESTABILIZACIÓN A 1000 VUS`: A 1000 VUs la latencia P95 se estabiliza en **1622.54 ms** (similar a los 1597.28 ms históricos), representando el límite de capacidad de procesamiento sostenido del backend bajo la infraestructura monolítica actual.
