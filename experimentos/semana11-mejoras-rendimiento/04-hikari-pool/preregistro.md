# Prerregistro — Causalidad y Efecto de la Ampliación del Pool HikariCP (`maximumPoolSize`)

## 1. Identificación y Pregunta Experimental

- **Proyecto**: RachaPro
- **Módulo**: Semana 11 — Optimización y Causalidad de Rendimiento
- **Operación Evaluada**: `POST /api/activities`
- **Pregunta Experimental**:
  > ¿El aumento del tamaño máximo del pool de conexiones HikariCP (`maximumPoolSize`) desde el baseline de 10 hacia 20 y 30 reduce de forma reproducible las conexiones pendientes (`pending`), disminuye la latencia P95 y mejora el throughput bajo cargas concurrentes de 50 VUs y 250 VUs?

---

## 2. Hipótesis

- **$H_0$ (Nulo)**: Aumentar `maximumPoolSize` a 20 o 30 no produce una mejora material ni reproducible en P95 ni en throughput a 50 y 250 VUs.
- **$H_1$ (Alternativa)**: Aumentar `maximumPoolSize` reduce la espera por conexión en cola (`pending`), mejorando significativamente P95 y throughput cuando el pool de 10 conexiones se satura.

---

## 3. Variables Experimentales

- **Variable Independiente**: `maximumPoolSize` $\in \{10, 20, 30\}$.
- **Niveles de Concurrencia**: 50 VUs y 250 VUs.
- **Variables Dependientes**:
  1. Latencia P95 (ms).
  2. Throughput (req/s).
  3. Hikari `pending` máximo y promedio.
  4. Hikari `active` máximo.
  5. Tasa de éxito funcional ($\%$).

---

## 4. Diseño de Ejecución (18 Corridas Totales)

- **Duración por Corrida**: 60 segundos.
- **Estructura**:
  - Pool 10: 50 VUs (R1 warmup, R2, R3) + 250 VUs (R1 warmup, R2, R3) = 6 corridas.
  - Pool 20: 50 VUs (R1 warmup, R2, R3) + 250 VUs (R1 warmup, R2, R3) = 6 corridas.
  - Pool 30: 50 VUs (R1 warmup, R2, R3) + 250 VUs (R1 warmup, R2, R3) = 6 corridas.
- **Restauración de Semilla**: Antes de cada corrida se restablece el dataset determinístico S11 (1000 usuarios, 100000 actividades).

---

## 5. Criterios de Aceptación Causal

- Hikari será clasificado como **Causa Contribuyente Demostrada** si:
  1. Las peticiones pendientes (`pending`) disminuyen a cero o bajan drásticamente en los pools de 20 y 30.
  2. El P95 mejora de manera consistente en R2 y R3.
  3. No se observa saturación inmediata de PostgreSQL ni del CPU del backend.

---

## 6. Estado del Prerregistro

`PREREGISTRO CREADO Y VERSIONADO ANTES DE LA EJECUCIÓN`
