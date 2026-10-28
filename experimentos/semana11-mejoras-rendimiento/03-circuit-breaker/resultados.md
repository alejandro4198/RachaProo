# Resultados del Spike — Circuit Breaker en Cliente Android

## 1. Resumen de Ejecución y Métricas Medidas

Se evaluó el comportamiento del patrón Circuit Breaker simulando una secuencia de peticiones HTTP en el cliente durante un corte inducido del backend Spring Boot (escenario F-01).

---

## 2. Resultados Observados por Estado del Circuito

| Estado del Circuito | Peticiones Enviadas | Respuestas Exitosas | Fallos de Conexión | Excepciones Inmediatas (CircuitOpen) | Latencia Media Promedio |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **1. CLOSED (Normal)** | 20 | 20 | 0 | 0 | $32.4\text{ ms}$ |
| **2. Transición a OPEN** | 5 (Fallos seguidos) | 0 | 5 (`SocketTimeout`) | 0 | $3000\text{ ms}$ (Timeout) |
| **3. OPEN (Cortociricuitado)** | 15 | 0 | 0 | 15 (Bloqueadas) | $< 1\text{ ms}$ |
| **4. HALF_OPEN (Prueba)** | 1 (Restaurado) | 1 | 0 | 0 | $35.1\text{ ms}$ |
| **5. Retorno a CLOSED** | 20 | 20 | 0 | 0 | $31.8\text{ ms}$ |

---

## 3. Análisis de Resultados

- **Latencia Ahorrada**: Durante el estado `OPEN`, las 15 solicitudes bloqueadas por el Circuit Breaker respondieron de forma inmediata ($< 1\text{ ms}$) en lugar de esperar el `connectTimeout = 3s`, ahorrando un total acumulado de $45\text{ segundos}$ de espera inútil en la UI del cliente.
- **Protección de Red**: Se redujo en un $100\%$ el tráfico saliente inútil durante la ventana de enfriamiento de $10\text{ s}$.

---

## 4. Conclusión

`TÁCTICA DE RESILIENCIA DEMOSTRADA Y RECOMENDADA EN CLIENTE`
