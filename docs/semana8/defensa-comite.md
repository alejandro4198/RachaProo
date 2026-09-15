# Defensa técnica — Comité Semana 8

## 1. ¿Por qué monolito modular y no microservicios?

Porque el sistema actual no presentó evidencia que justificara asumir el costo adicional de distribución.

El monolito modular permite:

- separar capacidades;
- definir propiedad de persistencia;
- controlar dependencias;
- mantener llamadas locales;
- conservar un único despliegue.

Migrar a microservicios habría añadido complejidad operacional sin una necesidad demostrada.

---

## 2. ¿Entonces sigue siendo un monolito?

Sí.

Existe:

- un backend Spring Boot;
- un proceso;
- un despliegue;
- una base de datos PostgreSQL física.

La diferencia es que internamente el código ahora está organizado y protegido por módulos.

---

## 3. ¿Qué hace que sea modular y no solo carpetas nuevas?

No únicamente se movieron archivos.

También se implementaron:

- propiedad de repositorios y entidades;
- contratos públicos entre módulos;
- eliminación de accesos directos relevantes a persistencia ajena;
- reglas ArchUnit;
- control de ciclos;
- documentación ADR y C4.

---

## 4. ¿Cómo se comunican los módulos?

Mediante llamadas síncronas dentro del mismo proceso.

Actualmente:

- Identity -> Activities con `DefaultCategoryProvisioning`
- Focus -> Activities con `ActivityLookup`
- Reminders -> Activities con `ActivityLookup`

No existe HTTP entre módulos.

---

## 5. ¿Por qué Activities expone interfaces?

Para evitar que otros módulos conozcan detalles internos como:

- `ActivityRepository`
- `ActivityEntity`
- `CategoryRepository`
- `CategoryEntity`

El consumidor depende de una capacidad, no de su persistencia.

---

## 6. ¿Qué garantiza que alguien no vuelva a romper las fronteras?

ArchUnit automatiza parte de esa protección.

Las fitness functions verifican:

- aislamiento;
- ciclos;
- acceso a módulos;
- reglas de Shared.

No sustituye revisión de código, pero evita depender únicamente de disciplina humana.

---

## 7. ¿Por qué todos los módulos usan la misma base de datos?

Porque el objetivo actual fue aislamiento lógico y estructural, no separación física.

Cada módulo posee sus repositorios y entidades aunque PostgreSQL sea compartido.

Separar bases de datos introduciría costos adicionales que hoy no están justificados por evidencia.

---

## 8. ¿Qué problema de rendimiento se encontró?

Bajo alta concurrencia se observó:

- aproximadamente 1000 actividades activas por usuario sintético;
- respuestas grandes del endpoint Activities;
- numerosos threads esperando adquisición de conexión Hikari.

Esto demuestra dónde se manifestó un cuello inmediato durante la prueba.

No demuestra una causa raíz única.

---

## 9. ¿PostgreSQL era el problema?

No se puede afirmar eso.

Los EXPLAIN ANALYZE aislados fueron de pocos milisegundos.

Durante la prueba tampoco se identificó un patrón dominante de bloqueos PostgreSQL, IO o WAL.

Pero esas observaciones no eliminan completamente la posible contribución de la base de datos bajo presión concurrente.

---

## 10. ¿La paginación resolvió el problema?

La paginación redujo significativamente el costo observado de la primera respuesta.

En las corridas controladas, promedio y mediana disminuyeron aproximadamente 80 % al comparar:

- aproximadamente 1000 elementos;
- frente a 100 elementos.

No se afirma que el pool de conexiones haya quedado resuelto.

Tampoco se midió recuperar las 1000 actividades mediante diez páginas.

---

## 11. ¿Por qué no reemplazaron directamente el endpoint original?

Para aislar el experimento y no cambiar simultáneamente el comportamiento del Android.

Se mantuvo:

`/api/activities`

y se agregó:

`/api/activities/paged`

Esto permitió una comparación CONTROL vs TREATMENT sin romper el consumidor existente.

---

## 12. ¿La modularización volvió más lento el backend?

No hay evidencia reproducible para afirmarlo.

En PRE -> POST, POST fue más lento.

En POST -> PRE, POST fue ligeramente más rápido.

La dirección se invirtió.

Por eso la conclusión correcta es:

**No se observó una regresión reproducible atribuible al refactor modular.**

---

## 13. ¿Entonces demostraron que PRE y POST tienen exactamente el mismo rendimiento?

No.

Ausencia de regresión reproducible no equivale a equivalencia estadística.

Las pruebas fueron locales y con pocas réplicas.

---

## 14. ¿Qué limitaciones tuvieron las pruebas?

Entre otras:

- entorno local;
- 499 VUs en una sola máquina;
- pocas réplicas;
- variables del sistema operativo, JVM y hardware no totalmente controladas;
- constant-vus es un modelo cerrado;
- no se realizó un estudio estadístico exhaustivo.

---

## 15. ¿Qué cambió realmente entre antes y después?

Antes:

- organización más cercana a paquetes funcionales individuales;
- fronteras menos explícitas;
- posibles accesos directos entre capacidades.

Después:

- módulos por capacidad;
- contratos intermodulares;
- propiedad de persistencia;
- ArchUnit;
- C4 alineado con código;
- ADR materializados.

El despliegue continúa siendo monolítico.

---

## 16. ¿Cuál fue el principal resultado?

El principal resultado es estructural.

RachaPro pasó a tener fronteras de arquitectura explícitas, verificables y documentadas sin introducir prematuramente complejidad distribuida.

---

## 17. ¿Qué queda pendiente?

Principalmente:

- decidir adopción definitiva de paginación en Android;
- estabilizar el criterio de orden de paginación;
- continuar experimentos de capacidad del pool;
- vigilar crecimiento de datos;
- mantener las reglas modulares.

---

## 18. Respuesta corta para cierre oral

“Semana 8 no consistió en convertir RachaPro en microservicios. Mantuvimos un único backend, pero materializamos un monolito modular con ownership de persistencia, APIs internas y fitness functions. Además, diagnosticamos el comportamiento bajo carga, validamos paginación y comprobamos que el refactor modular no produjo una regresión reproducible. Las conclusiones se limitaron a lo que realmente medimos.”