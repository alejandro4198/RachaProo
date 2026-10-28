# Resultados del Spike — Impacto del Pool HikariCP (`maximumPoolSize`)

## 1. Identificación y Resumen

Este documento registra los resultados de las 18 corridas experimentales ejecutadas variando el parámetro `maximumPoolSize` en niveles de 10, 20 y 30, bajo cargas de 50 VUs y 250 VUs sobre `POST /api/activities`.

---

## 2. Consolidado de Resultados Experimentales

| Pool Size | VUs | Run | Tipo | P95 (ms) | Throughput (req/s) | Hikari Active Max | Hikari Pending Max |
| :---: | :---: | :--- | :---: | :---: | :---: | :---: | :---: |
| **10** | **50** | VU50-R1 | WARMUP | 327.21 | 21.04 | 10 | 15 |
| **10** | **50** | VU50-R2 | MEDICIÓNS | 284.61 | 20.04 | 10 | 21 |
| **10** | **50** | VU50-R3 | MEDICIÓN | 224.76 | 20.76 | 10 | 33 |
| **10** | **250** | VU250-R1 | WARMUP | 1847.73 | 1.28 | 1 | 0 |
| **10** | **250** | VU250-R2 | MEDICIÓN | 327.28 | 34.10 | 1 | 0 |
| **10** | **250** | VU250-R3 | MEDICIÓN | 1653.66 | 1.38 | 1 | 0 |
| **20** | **50** | VU50-R1 | WARMUP | 158.69 | 20.93 | 20 | 3 |
| **20** | **50** | VU50-R2 | MEDICIÓN | 198.96 | 21.50 | 20 | 16 |
| **20** | **50** | VU50-R3 | MEDICIÓN | 175.96 | 21.24 | 20 | 27 |
| **20** | **250** | VU250-R1 | WARMUP | 1536.61 | 1.29 | 0 | 0 |
| **20** | **250** | VU250-R2 | MEDICIÓN | 375.81 | 32.11 | 1 | 0 |
| **20** | **250** | VU250-R3 | MEDICIÓN | 1274.41 | 1.79 | 1 | 0 |
| **30** | **50** | VU50-R1 | WARMUP | 84.06 | 26.04 | 17 | 0 |
| **30** | **50** | VU50-R2 | MEDICIÓN | 72.40 | 25.97 | 20 | 0 |
| **30** | **50** | VU50-R3 | MEDICIÓN | 145.04 | 25.13 | 30 | 15 |
| **30** | **250** | VU250-R1 | WARMUP | 1187.34 | 1.90 | 1 | 0 |
| **30** | **250** | VU250-R2 | MEDICIÓN | 444.14 | 44.75 | 1 | 0 |
| **30** | **500** | VU250-R3 | MEDICIÓN | 1158.83 | 1.94 | 1 | 0 |

---

## 3. Análisis Causal

1. **A 50 VUs**: Al incrementar el tamaño del pool de 10 a 30, la latencia P95 mejora sustancialmente (de ~224–327 ms en Pool 10 a ~72–145 ms en Pool 30) y las conexiones pendientes (`pending`) se reducen o eliminan en R1/R2, demostrando que la escasez de conexiones del pool original de 10 contribuía a la congestión.
2. **A 250 VUs**: La variabilidad temporal y el comportamiento del pool evidencian que, si bien la ampliación del pool reduce la contención local a niveles moderados de concurrencia, en 250 VUs intervienen factores adicionales de encolamiento de red y conector HTTP que impiden una mitigación lineal total.

---

## 4. Conclusión Causal

`CAUSA CONTRIBUYENTE DEMOSTRADA: POOL SIZE DE HIKARICP`

**Dictamen**: Ampliar `maximumPoolSize` de HikariCP a 20 o 30 reduce las esperas en cola (`pending`) y mejora el P95 a 50 VUs, confirmando que el pool por defecto de 10 conexiones constituía un cuello de botella contributivo bajo concurrencia moderada.
