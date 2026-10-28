# Spike — Evaluación Conceptual de Base de Datos Distribuida vs. PostgreSQL Monolítico

## 1. Pregunta Técnica

> ¿Existen evidencias experimentales en RachaPro que demuestren que la base de datos PostgreSQL actual es el cuello de botella del sistema, justificando una migración hacia un motor distribuido?

---

## 2. Evaluación de Evidencias

1. **Evidencia de Carga en S11**: En la campaña de 1.000 VUs con 100.000 actividades preexistentes, PostgreSQL respondió sin arrojar errores de conexión, bloqueos muertos (deadlocks) ni corrupción de datos, manteniendo $100\%$ de éxito funcional.
2. **Optimizaciones Existentes**: Las consultas de lectura fueron optimizadas mediante paginación en `ActivityRepository` (`Pageable`), mitigando el consumo desmedido de memoria.
3. **Ausencia de Telemetría Sincronizada de DB**: En la campaña histórica S11 no se registraron métricas internas de locks, I/O o uso de CPU de PostgreSQL. Por lo tanto, no es posible afirmar que PostgreSQL sea el causante del aumento de latencia P95.

---

## 3. Comparativa Conceptual

| Criterio | PostgreSQL Monolítico Actual | Base de Datos Distribuida (Ej. CockroachDB / YugabyteDB) |
| :--- | :--- | :--- |
| **Garantías de Consistencia** | ACID Fuerte (Serializable / Read Committed) | Consistencia eventual o consensos Raft/Paxos |
| **Complejidad Operativa** | Baja (1 contenedor / instancia gestionada) | Alta (múltiples nodos de datos, coordinadores) |
| **Costo Relativo Cómputo** | Bajo (recursos locales) | Alto (requiere múltiples nodos) |
| **Reversibilidad** | Inmediata | Compleja (requiere migración de esquemas distribuida) |

---

## 4. Conclusión Final

`NO JUSTIFICADA ACTUALMENTE POR FALTA DE EVIDENCIA DE QUE POSTGRESQL SEA EL CUELLO DE BOTELLA`

**Justificación**: No existe evidencia experimental de que PostgreSQL sea el factor degradante en RachaPro. Introducir una base de datos distribuida añadiría complejidad operativa y latencia inter-nodo por consensos distribuidos sin respaldo de telemetría que la justifique.
