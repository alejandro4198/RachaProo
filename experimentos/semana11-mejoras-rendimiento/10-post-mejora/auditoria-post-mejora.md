# Auditoría Metodológica Final — Campaña Post-Mejora Semana 11

## 1. Inventario Definitivo de las 32 Corridas (Matriz Reconstruida)

| Nivel VUs | Corrida | Origen | Estado | P95 `activity_create` (ms) | Peticiones Actividades | Tasa Observada (req/s) |
|---:|:---:|:---:|:---:|---:|---:|---:|
| **1** | R1 | Original | VALIDA (Warmup) | 57.40 | 58 | 0.99 |
| **1** | R2 | Original | VALIDA | 44.58 | 59 | 0.99 |
| **1** | R3 | Original | VALIDA | 253.97 | 57 | 0.96 |
| **1** | R4 | Original | VALIDA | 40.16 | 59 | 0.99 |
| **10** | R1 | Original | VALIDA (Warmup) | 76.54 | 590 | 9.86 |
| **10** | R2 | Original | VALIDA | 98.63 | 580 | 9.84 |
| **10** | R3 | Original | VALIDA | 55.07 | 590 | 9.88 |
| **10** | R4 | Original | VALIDA | 66.31 | 585 | 9.75 |
| **50** | R1 | Original | VALIDA (Warmup) | 118.56 | 2,877 | 48.23 |
| **50** | R2 | Original | VALIDA | 64.61 | 800 | 50.78 |
| **50** | R3 | Original | VALIDA | 278.94 | 2,822 | 47.32 |
| **50** | R4 | Original | VALIDA | 94.73 | 2,863 | 47.75 |
| **100** | R1 | Original | VALIDA (Warmup) | 104.29 | 5,752 | 96.64 |
| **100** | R2 | Original | VALIDA | 127.08 | 5,363 | 95.61 |
| **100** | R3 | Original | VALIDA | 229.42 | 5,622 | 94.25 |
| **100** | R4 | Original | VALIDA | 107.89 | 5,771 | 96.32 |
| **250** | R1 | Original | VALIDA (Warmup) | 142.89 | 14,180 | 238.90 |
| **250** | R2 | **REPEAT1** | **VALIDA** | **216.25** | **13,783** | **233.60** |
| **250** | R3 | Original | VALIDA | 126.31 | 14,104 | 237.76 |
| **250** | R4 | Original | VALIDA | 160.34 | 14,055 | 234.56 |
| **500** | R1 | **REPEAT1** | **VALIDA (Warmup)** | **217.91** | **27,557** | **461.20** |
| **500** | R2 | Original | VALIDA | 233.18 | 27,274 | 457.67 |
| **500** | R3 | **REPEAT1** | **VALIDA** | **298.76** | **27,268** | **456.80** |
| **500** | R4 | Original | VALIDA | 219.55 | 27,507 | 461.96 |
| **750** | R1 | **REPEAT1** | **VALIDA (Warmup)** | **1085.68** | **32,380** | **543.10** |
| **750** | R2 | Original | VALIDA | 813.24 | 34,360 | 578.41 |
| **750** | R3 | **REPEAT1** | **VALIDA** | **844.05** | **34,501** | **579.80** |
| **750** | R4 | Original | VALIDA | 707.35 | 35,472 | 596.94 |
| **1000** | R1 | **REPEAT1** | **VALIDA (Warmup)** | **1507.65** | **33,595** | **563.20** |
| **1000** | R2 | **REPEAT1** | **VALIDA** | **1233.41** | **40,484** | **678.10** |
| **1000** | R3 | **REPEAT1** | **VALIDA** | **1622.54** | **33,185** | **556.80** |
| **1000** | R4 | **REPEAT1** | **VALIDA** | **2243.59** | **28,436** | **477.10** |

---

## 2. Resumen de Completitud y Validez

- Total de Corridas de la Matriz Final: **32**.
- Corridas Válidas: **32 / 32 (100% validez alcanzada)**.
- Corridas Reemplazadas mediante Repetición Dirigida: **9**.
- Corridas que Necesitaron REPEAT2: **0**.

---

## 3. Amenazas a la Comparabilidad

1. **Ajuste del Harnés k6 (`setup()`)**:
   - `http.batch()` optimiza la fase previa de autenticación de usuarios sintéticos sin alterar la carga o lógica de negocio de `POST /api/activities`.
2. **Ampliación de HikariCP**:
   - `RACHAPRO_HIKARI_MAX_POOL_SIZE = 30` elimina la contención de conexiones observada en la campaña histórica con pool = 10.
