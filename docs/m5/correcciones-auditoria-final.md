# Correcciones posteriores a la auditoría final de M5

## 1. Base

Esta ronda de correcciones parte de:

`master`

Commit base:

`2cb03ba15b6dc311833ae0530ad41fbcebb6ef1e`

Ese commit corresponde al estado posterior al merge del Pull Request #51.

Por tanto, el hallazgo de entrega T-01 queda resuelto en esta base:

`master`
→ ya contiene la documentación M5 auditada.

## 2. Regla temporal

Estas correcciones son posteriores a:

- freeze original;
- auditoría externa;
- auditoría semántica de Semanas 9 y 10;
- PR #51.

No reescriben retrospectivamente esos artefactos.

## 3. S-01 — localización del preregistro de SPIKE-01

Estado:

`CORREGIDO`

El README de SPIKE-01 ahora referencia explícitamente la secuencia:

`59fdb47`
→ preregistro

`597e9c7`
→ implementación/resultados

`c6ae059`
→ ADR-003

Commit:

`268791f8efd2f5223dbd46195659e4b549432493`

## 4. S-02 — descarte de la primera corrida

Estado:

`ACLARADO POSTERIORMENTE`

Se conservan las cuatro corridas históricas sin modificación.

Para la evaluación conforme al criterio de descarte de la primera corrida se
utilizan:

`65, 36, 32 ms`

con:

- mediana de 36 ms;
- 3/3 corridas bajo 2000 ms.

No se reescribe ADR-003 ni el CSV histórico.

## 5. E-01 — productor de UserRegisteredV1

Estado:

`CORREGIDO`

El catálogo conserva su lectura histórica, pero ahora distingue explícitamente
ese corte del estado posterior en el que:

`Identity / UserService`
→ produce `UserRegisteredV1`

y Activities lo consume.

Commit:

`f39f80bdeb96ff34541c0ee2a292d2fbb13fdc9a`

## 6. I-01 — cobertura sync/async

Estado:

`CORREGIDO DOCUMENTALMENTE`

Se añadió:

`docs/integracion/cobertura-interacciones-sync-async.md`

La matriz muestra qué interacciones cuentan con evaluación específica y cuáles
no, sin elegir nuevos mecanismos.

Commit:

`1bcaefed495a43adcb8ce05c92603d9fba700023`

## 7. A-02 — módulos/capacidades versus bounded contexts

Estado:

`ACLARADO POSTERIORMENTE`

ADR-003 utiliza históricamente una formulación en la que Identity, Activities,
Focus, Progress y Reminders aparecen como contextos principales del monolito
modular.

Para el estado actual de M5, esa expresión no debe utilizarse automáticamente
como equivalencia de:

`bounded contexts formalmente cerrados`

La lectura vigente debe distinguir:

`módulos / capacidades implementadas`
→ pueden existir técnicamente en el sistema

de:

`bounded contexts formalmente decididos`
→ fronteras cerradas mediante el análisis de dominio de M5.

En particular, esta aclaración no decide el alcance de Identity en el Context
Map.

ADR-003 se conserva sin modificación por su valor histórico y temporal.

## 8. D-01 / D-02 — Identity

Estado:

`DECISIÓN PENDIENTE`

La auditoría confirma que Identity existe como capacidad implementada y participa
en una integración vigente con Activities.

Eso no autoriza a esta corrección documental a decidir si Identity debe
incorporarse como bounded context al Context Map.

## 9. Decisiones que permanecen abiertas

Esta ronda no decide:

- Identity en Context Map;
- Achievement ownership;
- renombre de ActivityLookup;
- política `.idea`;
- CQRS;
- Event Sourcing;
- retry;
- replay;
- Outbox;
- broker externo.

## 10. Resultado

Los hallazgos objetivos de higiene, trazabilidad y cobertura de la auditoría
final quedan tratados sin modificar decisiones arquitectónicas pendientes ni
reescribir los artefactos históricos.
