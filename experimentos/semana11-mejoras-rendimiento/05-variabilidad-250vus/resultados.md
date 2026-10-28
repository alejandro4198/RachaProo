# Resultados del Spike — Variabilidad a 250 VUs (Pool 30)

## 1. Identificación y Resumen

Este documento registra los resultados de las corridas R4, R5 y R6 ejecutadas con Pool Hikari = 30 y 250 VUs sobre `POST /api/activities` para discriminar el fenómeno bimodal observado previamente.

---

## 2. Consolidado de Corridas (R4, R5, R6)

| Corrida | P95 (ms) | Throughput (req/s) | Solicitudes Totales | Tasa de Error | Perfil Observado |
| :---: | :---: | :---: | :---: | :---: | :--- |
| **R4** | 160.66 | 46.09 | 14,495 | 0 % | **Perfil A (Rápido)** |
| **R5** | 1140.21 | 1.98 | 446 | 0 % | **Perfil B (Lento/Colapsado)** |
| **R6** | 127.68 | 44.74 | 14,670 | 0 % | **Perfil A (Rápido)** |

---

## 3. Descomposición de Latencia HTTP (k6)

| Métrica k6 (avg) | R4 (Rápido) | R5 (Lento) | R6 (Rápido) |
| :--- | :---: | :---: | :---: |
| `http_req_blocked` | 0.34 ms | 0.01 ms | 0.61 ms |
| `http_req_connecting` | 0.25 ms | 0.01 ms | 0.56 ms |
| `http_req_waiting` | 79.48 ms | 503.23 ms | 74.13 ms |
| `http_req_receiving` | 0.11 ms | 0.26 ms | 0.11 ms |

---

## 4. Análisis de Causalidad a Partir de la Descomposición

- **Descarte de Causa de Red / Socket**: En la corrida lenta (R5), `http_req_blocked` y `http_req_connecting` son prácticamente cero (0.01 ms), lo que **descarta** de forma concluyente que la degradación se deba a saturación de puertos efímeros, sockets o bucle local loopback en el cliente k6.
- **Evidencia del Servidor**: El tiempo se concentra íntegramente en `http_req_waiting` (salto de ~74–79 ms en R4/R6 hasta 503 ms promedio en R5), evidenciando que la demora ocurre en el procesamiento del servidor backend.

---

## 5. Matriz de Hipótesis Actualizada

| Hipótesis | Evidencia a Favor | Evidencia en Contra | Estado |
| :--- | :--- | :--- | :--- |
| **H1: k6 / Generador** | N/A | `http_req_blocked`/`connecting` mínimos en R5. | `DESCARTADO CON EVIDENCIA` |
| **H2: Socket / Red Local** | N/A | Sin bloqueo de sockets en k6. | `DESCARTADO CON EVIDENCIA` |
| **H3: Tomcat / HTTP Queue** | `http_req_waiting` elevado en R5. | Falta telemetría detallada de hilos Actuator. | `INDICIO FUERTE` |
| **H4: CPU Backend** | Procesador estable (~24-27%). | Sin saturación al 100%. | `NO DEMOSTRADO` |
| **H5: JVM / GC** | Heap estable. | Sin registros de pausas de GC. | `NO DEMOSTRADO` |
| **H6: PostgreSQL** | Conexiones activas bajas. | Falta análisis exhaustivo de wait events. | `NO DEMOSTRADO` |

---

## 6. Conclusión

`BIMODALIDAD DEMOSTRADA — CAUSA NO AISLADA (INDICIO FUERTE EN HTTP WAITING)`

**Conclusión**: La descomposición de latencia k6 descarta problemas de red/sockets del cliente. La ralentización en R5 se debe a un incremento en el tiempo de espera de respuesta del backend (`http_req_waiting`), apuntando a colas de procesamiento en la capa HTTP/Tomcat o concurrencia de aplicación, pendiente de confirmación mediante instrumentación específica.
