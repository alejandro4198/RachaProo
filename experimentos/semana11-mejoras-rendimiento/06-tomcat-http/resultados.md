# Resultados del Spike — Análisis de Conector HTTP y Hilos JVM (06-tomcat-http)

## 1. Identificación y Resumen

Este documento registra los resultados del spike de diagnóstico ejecutado con Pool Hikari = 30 y 250 VUs (corridas R1, R2, R3 de 60s) incorporando telemetría de hilos vivos (`jvm.threads.live`) y solicitudes HTTP activas (`http.server.requests.active`).

---

## 2. Consolidado de Corridas (R1, R2, R3)

| Corrida | P95 (ms) | Throughput (req/s) | Solicitudes Totales | Tasa de Error | Threads Live Max (`jvm.threads.live`) | Estado |
| :---: | :---: | :---: | :---: | :---: | :---: | :---: |
| **R1** | 111.61 | 47.20 | 14,664 | 0 % | **24** | VALIDA |
| **R2** | 1134.30 | 1.97 | 452 | 0 % | **172** | VALIDA |
| **R3** | 296.23 | 45.44 | 14,353 | 0 % | **24** | VALIDA |

---

## 3. Análisis de Correlación (Rápido vs. Lento)

- **Perfil Rápido (R1 y R3)**: Operan con un número acotado de hilos vivos en la JVM (`max = 24`), alcanzando un throughput de ~45–47 req/s y un P95 excelente de ~111–296 ms.
- **Perfil Lento (R2)**: Experimenta un salto extraordinario en los hilos vivos de la JVM (`max = 172`), coincidiendo temporalmente con la caída del throughput a 1.97 req/s y el aumento del P95 a 1134.30 ms.

---

## 4. Conclusión

`INDICIO FUERTE: SATURACIÓN DE HILOS DE EJECUCIÓN (JVM THREAD SPIKE) EN PERFIL LENTO`

**Dictamen**: La bimodalidad observada a 250 VUs está fuertemente correlacionada con un desborde repentino de hilos vivos en la JVM (de 24 a 172 hilos), lo que introduce contención de scheduling a nivel de sistema operativo/runtime bajo alta concurrencia.
