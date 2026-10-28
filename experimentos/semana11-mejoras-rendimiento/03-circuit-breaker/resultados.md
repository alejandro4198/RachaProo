# Resultados del Spike — Circuit Breaker en Cliente Android

## 1. Estatus del Spike

`PROPUESTO — PENDIENTE DE SPIKE EXPERIMENTAL`

---

## 2. Evaluación Conceptual Propuesta

- **Propósito**: Detener el envío de peticiones hacia el backend cuando detecte fallos consecutivos de red ($N_{\text{fail}} = 5$), entrando en estado `OPEN`.
- **Métricas Esperadas (Pendientes de Medición)**:
  - **Comportamiento en `OPEN`**: Fallo inmediato ($< 1\text{ ms}$) evitando agotar los $3\text{ segundos}$ de `connectTimeout` de Retrofit.
  - **Protección de Red**: Supresión del tráfico saliente inútil durante el período de enfriamiento ($10\text{ s}$).
  - **Recuperación**: Transición a `HALF_OPEN` con $1$ petición de prueba para verificar salud del servidor antes de retornar a `CLOSED`.

---

## 3. Conclusión

`PROPUESTO — PENDIENTE DE EJECUCIÓN EXPERIMENTAL`
