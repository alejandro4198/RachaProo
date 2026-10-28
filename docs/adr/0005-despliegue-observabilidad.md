# ADR-005: PROPUESTA DE ESTRATEGIA DE DESPLIEGUE, OBSERVABILIDAD Y PROTECCIÓN ARQUITECTÓNICA EN MONOLITO MODULAR

- **Estatus**: `PROPUESTO`
- **Fecha**: 2026-10-27
- **Autores**: Equipo RachaPro (Propuesta Técnica)
- **Decisión**: `PROPUESTA DEL EQUIPO — pendiente de validación`
- **Estado de Decisiones Previas**: `VEREDICTO DEL COMITÉ 2: PENDIENTE POR APLAZAMIENTO` (Manteniendo el Monolito Modular como arquitectura base operativa).

---

## 1. Título

ADR-005: Propuesta de Estrategia de Despliegue, Esquema de Observabilidad Integrado a Fallos (F-01) y Protección Continua de Fronteras Modulares mediante ArchUnit en CI.

---

## 2. Estado

`PROPUESTO`

---

## 3. Contexto

RachaPro ha evolucionado hacia un monolito modular bien estructurado en Spring Boot y Kotlin con 5 capacidades de negocio aisladas (`identity`, `activities`, `focus`, `progress`, `reminders`). Se cuenta con evidencia experimental de rendimiento (campañas k6 hasta 500 VUs en `POST /api/activities`), caracterización de fallos e indisponibilidad (escenario F-01), y reglas de arquitectura en ArchUnit (`ModularArchitectureTest.kt`). Es necesario definir la propuesta de estrategia de despliegue conceptual, la observabilidad operacional y la ejecución automatizada de fitness functions en el pipeline de CI/CD.

---

## 4. Problema

Garantizar la disponibilidad del sistema ante fallos operativos (F-01), monitorear el comportamiento del backend en producción sin comprometer información sensible (JWTs/credenciales), y evitar que futuras refactorizaciones degraden la arquitectura modular mediante acoplamientos ilegales entre módulos.

---

## 5. Drivers

1. **Mantenibilidad y Disciplina Modular**: Garantizar que la propiedad $V = 0$ (cero violaciones de fronteras modulares) se preserve automáticamente.
2. **Resiliencia Operacional**: Minimizar el impacto de indisponibilidades temporales del backend (F-01) en los usuarios de la app Android.
3. **Observabilidad Accionable**: Disponer de métricas, logs y alertas claras que identifiquen degradaciones antes de que afecten a los usuarios.
4. **Eficiencia de Costos y Simplicidad**: Evitar la sobre-ingeniería de microservicios independientes cuando la evidencia no lo justifica.

---

## 6. Restricciones

- La solución debe ejecutarse en el ecosistema actual (Java 25 / Kotlin 2.1 / Spring Boot 3.4 / Gradle / GitHub Actions).
- No se deben publicar datos sensibles (claves, JWTs, contraseñas) en logs ni repositorios.
- Se debe respetar el aplazamiento del Comité 2 manteniendo la arquitectura de Monolito Modular como línea base.

---

## 7. Evidencia

- **Fitness Function**: Verificada experimentalmente mediante el ciclo PASS → FAIL → PASS sobre las reglas existentes en `ModularArchitectureTest.kt`, registrado en [`experimentos/semana14-fitness-function/README.md`](file:///C:/Users/Lenovo/AndroidStudioProjects/RachaPro/experimentos/semana14-fitness-function/README.md).
- **Fallos y Timeouts (F-01)**: `RetrofitClient.kt` en Android maneja `connectTimeout = 3s` aislando el cliente de cuelgues ante caídas del servidor (`matrices-fallos-resiliencia.md`).
- **Pipeline CI**: Workflow [`.github/workflows/backend-architecture.yml`](file:///C:/Users/Lenovo/AndroidStudioProjects/RachaPro/.github/workflows/backend-architecture.yml) configurado para fallar el build si ocurren violaciones modulares.

---

## 8. Alternativas Evaluadas

1. **Alternativa A — Monolito Modular Único (Línea Base Actual)**: Despliegue de un solo contenedor Spring Boot conectado a PostgreSQL.
2. **Alternativa B — Monolito Modular Multi-Instancia con Balanceador (Propuesta)**: Despliegue de $N$ réplicas idénticas del backend detrás de NGINX/Load Balancer compartiendo base de datos.
3. **Alternativa C — Distribución Completa en 5 Microservicios**: Descomposición en 5 servicios desplegables independientes.

---

## 9. Decisión Propuesta

Se propone adoptar la **Alternativa B (Monolito Modular Multi-Instancia)** como arquitectura conceptual de despliegue para la fase de producción, manteniendo la **Alternativa A (Monolito Único)** para entornos de desarrollo y staging.

Adicionalmente, se plantea:
- La ejecución obligatoria de la Fitness Function de ArchUnit en el pipeline de GitHub Actions (`backend-architecture.yml`).
- El plan de observabilidad con logs JSON sanitizados y alertas de salud sobre `/actuator/health`.

---

## 10. Consecuencias Positivas

- **Alta Disponibilidad**: La arquitectura multi-instancia propuesta toleraría la caída de una instancia backend (F-01) sin interrumpir el servicio.
- **Protección Automatizada**: CI rechaza automáticamente cualquier PR que introduzca acoplamiento ilegal entre módulos.
- **Simplicidad Operativa**: Se mantiene una sola base de código y una sola base de datos PostgreSQL.

---

## 11. Consecuencias Negativas

- Necesidad de gestionar un balanceador de carga en producción.
- Incurrimiento en costos adicionales de infraestructura frente a una sola instancia.

---

## 12. Riesgos

- Agotamiento potencial de conexiones en PostgreSQL al escalar las instancias backend (Mitigado mediante límites en HikariCP y paginación en consultas).

---

## 13. Costos

- **Costo Relativo de Cómputo**: `NO ESTIMABLE CON LA EVIDENCIA ACTUAL` (requiere medición en infraestructura real).
- **Costo Financiero Absoluto**: `NO ESTIMABLE CON LA EVIDENCIA ACTUAL` (requiere cotización en proveedor cloud).

---

## 14. Reversibilidad

Inmediata y totalmente reversible. Reducir el número de instancias a $1$ convierte el despliegue a la Alternativa A sin requerir cambios en el código ejecutable.

---

## 15. Condiciones de Revisión

Esta propuesta se revisará cuando:
1. El Comité 2 se reúna y emite un veredicto definitivo sobre la arquitectura distribuida.
2. La carga sostenida supere la capacidad de escalado horizontal de la base de datos PostgreSQL.

---

## 16. Referencias

- Documento de Despliegue y Observabilidad: [`docs/semana14/12-despliegue-observabilidad.md`](file:///C:/Users/Lenovo/AndroidStudioProjects/RachaPro/docs/semana14/12-despliegue-observabilidad.md)
- Experimento Fitness Function PASS-FAIL-PASS: [`experimentos/semana14-fitness-function/README.md`](file:///C:/Users/Lenovo/AndroidStudioProjects/RachaPro/experimentos/semana14-fitness-function/README.md)
- ADR-004 de Distribución: [`docs/adr/0004-redecision-distribucion.md`](file:///C:/Users/Lenovo/AndroidStudioProjects/RachaPro/docs/adr/0004-redecision-distribucion.md)

---

## 17. Trazabilidad Git

- **Rama**: `modulo7/seguridad-operacion`
- **Commit**: "M7: corregir alcance y trazabilidad de evidencias"
