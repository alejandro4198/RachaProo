# Prerregistro — Diagnóstico de Concurrencia y Aislamiento de Cuello de Botella

## 1. Identificación y Pregunta Experimental

- **Proyecto**: RachaPro
- **Módulo**: Semana 11 — Mejoras de Rendimiento y Resiliencia
- **Operación Evaluada**: `POST /api/activities` (Creación de Actividades)
- **Pregunta Experimental**:
  > ¿Cuál es la causa raíz de la degradación observada en la latencia P95 a partir de 50 VUs concurrentes en la operación `POST /api/activities`, y cómo se comportan el throughput (req/s) y el jitter (variabilidad de latencia) a medida que escala la concurrencia hasta 1000 VUs?

---

## 2. Hipótesis Candidatas

1. **$H_1$ (Agotamiento de Conexiones HikariCP)**: El pool de conexiones a PostgreSQL se satura ante solicitudes concurrentes, generando tiempo de espera para obtener conexiones.
2. **$H_2$ (Saturación de CPU Backend / Thread Pool)**: El procesador del host backend o los hilos de Tomcat/Spring MVC alcanzan el $100\%$ de utilización, encolando peticiones HTTP.
3. **$H_3$ (Contención en Base de Datos / Locks)**: Consultas o índices en PostgreSQL sufren contención de locks en escrituras sobre la tabla `activities`.
4. **$H_4$ (Pausas de Garbage Collector JVM)**: La recolección de basura en la JVM (Java 25) introduce pausas (Stop-The-World) periódicas bajo alta asignación de memoria.
5. **$H_5$ (Saturación del Host Generador k6)**: El equipo desde el cual se ejecuta k6 satura CPU/Red, inflando la latencia medida en el cliente.

---

## 3. Variables Experimentales

- **Variable Independiente**: Nivel de usuarios virtuales concurrentes ($VU \in \{1, 10, 50, 100, 250, 500, 750, 1000\}$).
- **Variables Dependientes Medidas**:
  1. **Latencia P95 y P99** (en milisegundos).
  2. **Throughput** ($T = \text{solicitudes completadas exitosamente} / \text{segundo}$).
  3. **Jitter de Latencia** ($J = \text{Desviación Estándar de la Latencia P95 entre corridas medidas}$).
  4. **Tasa de Éxito Funcional** ($\%$ de respuestas HTTP 201 Created).
  5. **Tasa de Errores HTTP 4xx/5xx**.

---

## 4. Diseño Experimental y Condiciones

- **Herramienta de Carga**: k6 (`constant-vus`).
- **Duración por Corrida**: 60 segundos por nivel de carga con `gracefulStop = 30s`.
- **Repeticiones por Nivel**: 4 corridas (R1: calentamiento, R2–R4: mediciones válidas).
- **Semilla de Datos**: Base `rachapro_perf_s11` determinística con 1.000 usuarios sintéticos, 100.000 actividades preexistentes y distribución 80/20.

---

## 5. Criterios de Diagnóstico y Causalidad

- **Criterio de Degradación de Latencia**: $P95_{\text{representativo}} \ge 2 \times P95_{\text{1 VU}}$.
- **Criterio de Atribución de Causalidad**:
  - Si CPU Backend $> 90\%$ y Pool HikariCP $< 80\% \rightarrow$ **Saturación Cómputo Backend**.
  - Si Pool HikariCP $> 90\%$ activo / pendientes y CPU $< 70\% \rightarrow$ **Saturación Pool Conexiones DB**.
  - Si $T$ alcanza una meseta estable mientras P95 crece linealmente $\rightarrow$ **Saturación de Capacidad / Encolamiento**.

---

## 6. Estado del Prerregistro

`PREREGISTRO VERIFICADO Y CONSERVADO` (Compilado y versionado en Git antes del análisis de datos).
