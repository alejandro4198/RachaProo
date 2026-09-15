# EDAV — Evaluación arquitectónica de Semana 8

## 1. Alcance

Esta evaluación corresponde al estado AS-IS de RachaPro después de materializar el monolito modular del backend.

No evalúa una arquitectura futura.

Las principales fuentes de evidencia son:

- ADR-001: decisión de estilo arquitectónico;
- ADR-002: aislamiento de persistencia;
- C4 L2 y L3;
- código del backend;
- fitness functions con ArchUnit;
- pruebas automatizadas;
- experimentos de rendimiento de Semana 8.

---

## 2. Decisión arquitectónica principal

La decisión implementada consiste en mantener un único backend Spring Boot y organizarlo como monolito modular por capacidades de negocio.

Módulos:

- Identity;
- Activities;
- Focus;
- Progress;
- Reminders;
- Shared.

Esta decisión evita introducir distribución de red entre capacidades que actualmente viven en el mismo proceso.

---

## 3. Atributos de calidad evaluados

### 3.1 Modificabilidad

Objetivo:

Reducir el impacto de cambios entre capacidades de negocio.

Evidencia:

- paquetes organizados por módulo;
- repositorios y entidades con propietario definido;
- contratos públicos para llamadas entre módulos;
- ArchUnit restringe accesos indebidos;
- no se permiten dependencias cíclicas entre módulos.

Resultado:

**Mejora estructural respaldada por código y fitness functions.**

La modularización no garantiza que todos los cambios futuros sean locales, pero establece fronteras explícitas para reducir acoplamiento accidental.

---

### 3.2 Mantenibilidad

Objetivo:

Facilitar comprensión, evolución y depuración del backend.

Evidencia:

- Identity, Activities, Focus, Progress y Reminders representan capacidades reconocibles;
- Shared está limitado a soporte técnico;
- los componentes de persistencia permanecen dentro del módulo propietario;
- el C4 L3 coincide con la estructura física implementada.

Resultado:

**La estructura AS-IS presenta mayor alineación entre organización del código y capacidades de negocio.**

---

### 3.3 Rendimiento

Objetivo:

Evitar que la reestructuración arquitectónica introduzca una regresión reproducible.

Evidencia:

- diagnóstico previo identificó esperas relevantes por adquisición de conexiones bajo una prueba local de alta concurrencia;
- el endpoint original de Activities retornaba aproximadamente 1000 registros por usuario sintético;
- la variante paginada limita la primera respuesta a 100 registros;
- la paginación mostró una reducción aproximada del 80 % en latencia promedio/mediana en el experimento histórico;
- la validación PRE/POST del refactor fue repetida en ambos órdenes de ejecución.

Resultado:

**No se obtuvo evidencia reproducible de una regresión causada por el refactor modular.**

En la secuencia PRE -> POST, POST resultó más lento.

En la réplica POST -> PRE, la dirección de la diferencia se invirtió.

Por tanto, no es válido atribuir las diferencias observadas exclusivamente a la modularización.

---

### 3.4 Escalabilidad

Objetivo:

Identificar si la arquitectura actual elimina los límites observados bajo alta concurrencia.

Evidencia:

- el backend continúa siendo un único proceso;
- PostgreSQL continúa siendo una única instancia física;
- los módulos se comunican de forma síncrona en memoria;
- el diagnóstico de rendimiento mostró presión sobre adquisición de conexiones bajo 499 VUs.

Resultado:

**El refactor modular mejora estructura, pero no constituye por sí mismo una solución de escalabilidad.**

La arquitectura mantiene pendientes de capacidad que deben evaluarse mediante experimentación independiente.

---

### 3.5 Integridad y consistencia

Objetivo:

Evitar que la separación modular rompa las operaciones existentes.

Evidencia:

- los módulos permanecen dentro del mismo proceso Spring;
- la comunicación intermodular inicial es síncrona;
- Identity utiliza `DefaultCategoryProvisioning`;
- Focus y Reminders utilizan `ActivityLookup`;
- no se introdujeron eventos asíncronos ni consistencia eventual como parte del refactor.

Resultado:

**La modularización preserva el modelo operacional síncrono existente.**

---

### 3.6 Desplegabilidad y operación

Objetivo:

Mantener una complejidad operacional proporcional al tamaño actual del sistema.

Evidencia:

- existe una única aplicación backend;
- no se introdujeron microservicios;
- no se añadieron despliegues independientes entre módulos;
- no se añadió infraestructura de mensajería para comunicación interna.

Resultado:

**La solución evita el costo operacional de una arquitectura distribuida.**

---

## 4. Síntesis

| Atributo | Efecto observado | Evidencia |
|---|---|---|
| Modificabilidad | Favorable estructuralmente | módulos, APIs públicas, ArchUnit |
| Mantenibilidad | Favorable estructuralmente | organización por capacidades y C4 |
| Rendimiento | Sin regresión reproducible atribuible al refactor | validación PRE/POST |
| Escalabilidad | No resuelta por la modularización | diagnóstico de carga |
| Consistencia | Modelo síncrono preservado | contratos intermodulares |
| Desplegabilidad | Complejidad distribuida evitada | único Spring Boot |

---

## 5. Conclusión

La evidencia disponible respalda el monolito modular como arquitectura AS-IS implementada para RachaPro.

El principal beneficio demostrado es estructural: definición de propiedad, fronteras y reglas de dependencia.

No se atribuyen a la modularización mejoras de rendimiento que no hayan sido medidas.

Tampoco se considera que el refactor resuelva automáticamente escalabilidad, disponibilidad o capacidad de base de datos.