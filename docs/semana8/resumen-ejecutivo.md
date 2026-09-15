# Resumen ejecutivo — Semana 8

## 1. Punto de partida

RachaPro ya contaba con un backend funcional basado en Kotlin, Spring Boot y PostgreSQL.

El objetivo de Semana 8 no fue reemplazar el sistema por microservicios, sino evaluar y materializar una arquitectura interna más controlada y verificable.

El problema principal era que las capacidades de negocio existían, pero sus fronteras no estaban expresadas ni protegidas de forma suficientemente explícita.

---

## 2. Decisión arquitectónica

Se adoptó un:

**Monolito modular organizado por capacidades de negocio.**

Módulos implementados:

- Identity
- Activities
- Focus
- Progress
- Reminders
- Shared

El backend continúa siendo:

- una sola aplicación Spring Boot;
- un único proceso;
- un único despliegue;
- una única instancia física de PostgreSQL.

No se introdujeron microservicios.

---

## 3. Decisiones documentadas

### ADR-001

Define la selección del monolito modular como estilo arquitectónico.

### ADR-002

Define el aislamiento de persistencia entre módulos.

Cada módulo es propietario de:

- sus entidades;
- sus repositorios;
- sus reglas de acceso.

Los consumidores no deben acceder directamente a persistencia ajena.

---

## 4. Materialización en código

La estructura previa fue reorganizada por capacidad.

Ejemplos:

- auth, user y security -> Identity
- activity, category y subtask -> Activities
- pomodoro -> Focus
- achievement -> Progress
- reminder -> Reminders
- error -> Shared

Activities publica dos contratos internos:

- `ActivityLookup`
- `DefaultCategoryProvisioning`

Relaciones materializadas:

- Identity -> Activities mediante `DefaultCategoryProvisioning`
- Focus -> Activities mediante `ActivityLookup`
- Reminders -> Activities mediante `ActivityLookup`

Estas llamadas son síncronas y ocurren dentro del mismo proceso.

---

## 5. Protección arquitectónica

Se incorporaron fitness functions con ArchUnit.

Las reglas verifican:

- aislamiento de módulos;
- uso de APIs públicas;
- ausencia de ciclos;
- ausencia de repositorios de negocio en Shared;
- ausencia de entidades de negocio en Shared.

Los tests del backend continuaron pasando después del refactor.

---

## 6. C4

Se creó documentación C4 actualizada.

### L2

Representa:

Usuario -> Android App -> Backend Spring Boot -> PostgreSQL.

### L3

Representa internamente:

- Identity
- Activities
- Focus
- Progress
- Reminders
- Shared

El C4 describe el estado AS-IS implementado.

---

## 7. Diagnóstico de rendimiento

Bajo una prueba local de alta concurrencia con 499 VUs se observó que:

- cada usuario sintético tenía aproximadamente 1000 actividades activas;
- el endpoint original devolvía el conjunto completo;
- durante alta concurrencia muchos threads del backend esperaban adquisición de conexión Hikari.

La evidencia permite afirmar que la espera por adquisición de conexión fue un cuello inmediato observado durante esa prueba.

No permite afirmar que PostgreSQL sea irrelevante ni que exista una única causa raíz definitiva.

---

## 8. Experimento de paginación

Se comparó:

CONTROL:

`GET /api/activities`

aproximadamente 1000 actividades.

TREATMENT:

`GET /api/activities/paged?page=0&size=100`

100 actividades en la primera respuesta.

Configuración formal:

- 499 VUs
- 60 segundos
- constant-vus
- mismos usuarios sintéticos
- mismo backend y base de datos bajo condiciones comparables

Resultado:

La variante paginada mostró aproximadamente un 80 % de reducción en latencia promedio y mediana de la primera respuesta en las corridas controladas.

Este resultado no significa que recuperar las 1000 actividades mediante varias páginas sea 80 % más rápido.

El Android existente tampoco fue modificado para consumir la variante paginada.

---

## 9. Validación del refactor modular

Se comparó el backend PRE y POST modular.

Primera secuencia:

PRE -> POST

POST presentó mayores latencias.

Para controlar el posible efecto temporal se realizó una réplica en orden inverso:

POST -> PRE

En esta segunda secuencia POST resultó ligeramente más rápido.

Conclusión:

**No se observó una regresión reproducible atribuible al refactor modular.**

La dirección de la diferencia cambió al invertir el orden de ejecución.

Esto evidencia variabilidad temporal o ambiental relevante.

No se afirma equivalencia estadística ni mejora de rendimiento causada por la modularización.

---

## 10. Trade-offs principales

La solución gana:

- fronteras explícitas;
- propiedad de persistencia;
- protección automatizada;
- mejor correspondencia entre código y negocio;
- menor acoplamiento accidental.

A cambio requiere:

- disciplina para respetar contratos;
- mantenimiento de reglas ArchUnit;
- mantenimiento de ADR y C4;
- mayor atención a dependencias internas.

Se evita por ahora la complejidad operacional de microservicios.

---

## 11. Riesgos abiertos

Permanece pendiente:

- adopción definitiva de paginación por Android;
- añadir orden secundario estable para paginación si se adopta;
- continuar evaluando capacidad del pool de conexiones;
- evaluar crecimiento futuro de PostgreSQL y volumen de datos;
- evitar que Shared evolucione hacia un módulo comodín.

---

## 12. Resultado de Semana 8

Semana 8 deja:

- ADR-001
- ADR-002
- monolito modular materializado
- aislamiento de persistencia
- contratos intermodulares
- fitness functions ArchUnit
- C4 L2 y L3
- diagnóstico de rendimiento
- experimento de paginación
- validación PRE/POST
- EDAV
- riesgos y trade-offs
- matriz de trazabilidad
- comparación antes/después

La principal mejora demostrada es arquitectónica y estructural.

No se atribuyen al refactor mejoras de rendimiento, escalabilidad o disponibilidad que no hayan sido medidas.