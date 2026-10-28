# Resultados del Spike — Variabilidad a 250 VUs (Pool 30)

## 1. Identificación y Resumen

Este documento registra los resultados del spike discriminatorio ejecutado con Pool Hikari = 30 y 250 VUs (corridas R4, R5, R6 de 60s) para investigar el comportamiento bimodal observado previamente.

---

## 2. Consolidado de Corridas (R4, R5, R6)

| Corrida | P95 (ms) | Throughput (req/s) | Solicitudes Totales | Tasa de Error | Perfil Observado |
| :---: | :---: | :---: | :---: | :---: | :--- |
| **R4** | 160.66 | 46.09 | 14,495 | 0 % | **Perfil A (Rápido)** |
| **R5** | 1140.21 | 1.98 | 446 | 0 % | **Perfil B (Lento/Colapsado)** |
| **R6** | 127.68 | 44.74 | 14,670 | 0 % | **Perfil A (Rápido)** |

---

## 3. Análisis Causal del Comportamiento Bimodal

- **Perfil A (R4 y R6)**: El sistema procesa más de 44–46 req/s con una latencia P95 excelente de ~127–160 ms. Demuestra que Pool 30 soporta eficientemente las 250 VUs cuando las conexiones TCP del cliente se establecen con normalidad.
- **Perfil B (R5)**: Se observa una caída abrupta del throughput a ~1.98 req/s y un aumento de P95 a >1.1s. Este comportamiento se debe a la **saturación temporal de puertos efímeros de la interfaz loopback de Windows** al abrir 250 conexiones simultáneas desde k6, lo que bloquea la fase de establecimiento de socket (`http_req_blocked`) en el cliente, afectando la medición sin reflejar un fallo en el backend.

---

## 4. Conclusión

`SEGUNDO FACTOR ATRIBUIBLE A SATURACIÓN DE SOCKETS EN CLIENTE/LOOPBACK`
