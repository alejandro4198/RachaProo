# Spike — Evaluación de Base de Datos Distribuida vs. PostgreSQL Monolítico

## 1. Pregunta Técnica

> ¿Existen evidencias experimentales que demuestren que la base de datos PostgreSQL actual es el cuello de botella del sistema, justificando la migración hacia una base de datos distribuida (ej. CockroachDB / YugabyteDB / Spanner)?

---

## 2. Evaluación de Evidencias

1. **Evidencia de Carga en S11**: En la campaña de 1.000 VUs con 100.000 actividades preexistentes, PostgreSQL respondió sin arrojar errores de conexión, bloqueos muertos (deadlocks) ni corrupción de datos, manteniendo $100\%$ de éxito funcional.
2. **Consultas y Paginación**: Las consultas pesadas de lectura fueron optimizadas mediante paginación en `ActivityRepository` (`Pageable`), reduciendo el consumo de memoria y CPU en la base de datos.
3. **Métricas de Conexión**: La latencia observada se debe al encolamiento en el servidor HTTP / pool de hilos de la aplicación Spring Boot, no a la falla de procesamiento en el motor relacional de PostgreSQL.

---

## 3. Comparativa de Complejidad, Consistencia y Costo

| Criterio | PostgreSQL Monolítico Actual | Base de Datos Distribuida (Cockroach/Yugabyte) |
| :--- | :--- | :--- |
| **Garantías de Consistencia** | ACID Fuerte (Serializable / Read Committed) | Consistencia eventual o consensos Raft/Paxos |
| **Complejidad Operativa** | Muy Baja (1 contenedor / instancia gestionada) | Alta (múltiples nodos de datos, coordinadores) |
| **Costo Financiero** | Bajo (recursos mínimos de cómputo) | Alto (requiere múltiples nodos con almacenamiento SSD) |
| **Reversibilidad** | Inmediata | Compleja (requiere migración de esquemas distribuida) |

---

## 4. Conclusión Final

`NO JUSTIFICADA ACTUALMENTE`

**Justificación**: No existe evidencia experimental de que PostgreSQL sea el cuello de botella que degrada la aplicación. Introducir una base de datos distribuida agregaría una enorme complejidad operacional, latencia inter-nodo por consenso distribuido y costo financiero sin resolver el cuello de botella real de encolamiento en la capa de aplicación.
