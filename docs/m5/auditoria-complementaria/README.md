# Auditoría complementaria de consistencia, trazabilidad y suficiencia de M5

## 1. Propósito

Esta fase revisa el estado consolidado de M5 después de las auditorías,
correcciones documentales y merges anteriores.

Baseline:

`259fb740abe3651b3925cbf6e93a3399ce905dbe`

Rama de auditoría:

`audit/m5-auditoria-complementaria`

La fase no parte de la premisa de que existan errores.

Su objetivo es identificar, con evidencia verificable:

- inconsistencias transversales;
- vacíos de trazabilidad;
- problemas de temporalidad;
- decisiones todavía abiertas;
- afirmaciones más fuertes que la evidencia disponible;
- artefactos válidos que no requieren cambio.

## 2. Principios

La auditoría utiliza los siguientes controles:

### EVIDENCIA

Toda afirmación arquitectónica debe distinguir entre:

- observación directa;
- medición;
- inferencia;
- decisión;
- ausencia de evidencia.

### TRAZABILIDAD

Cuando corresponda debe poder seguirse la cadena:

`requisito`

→ `evidencia`

→ `experimento`

→ `decisión`

→ `artefacto`

→ `implementación`

La ausencia de un eslabón no implica automáticamente que la arquitectura esté
equivocada.

### TEMPORALIDAD

Una evidencia posterior no puede utilizarse como causa retrospectiva de una
decisión anterior.

Las correcciones posteriores deben conservar explícitamente su carácter
posterior.

### AGENCIA

La auditoría no adopta decisiones arquitectónicas que pertenezcan al equipo.

En particular, encontrar evidencia insuficiente no autoriza automáticamente a:

- cambiar el Context Map;
- introducir eventos;
- introducir CQRS;
- introducir Event Sourcing;
- introducir broker;
- introducir retry;
- introducir replay;
- introducir Outbox;
- renombrar contratos;
- cambiar ownership de dominio.

## 3. Formato de hallazgo

Cada hallazgo debe registrar:

`Estado | Evidencia | Hecho | Problema | Severidad | Decisión pendiente | Acción verificable | Trazabilidad`

## 4. Estados permitidos

- `CORREGIDO`
- `VALIDADO — SIN CAMBIO`
- `DECIDIDO POR EL EQUIPO`
- `NO JUSTIFICADO`
- `DECISIÓN PENDIENTE`

Pueden utilizarse precisiones adicionales cuando se trate de evolución
posterior, siempre que no oculten la condición histórica original.

## 5. Alcance inicial

La auditoría revisará cinco dimensiones.

### 5.1 Consistencia transversal

Comparará, cuando corresponda:

- README;
- documentos M5;
- integración;
- dominio;
- ADR;
- experimentos;
- contratos;
- Context Map.

### 5.2 Trazabilidad

Se revisará la relación entre:

- requisitos;
- evidencia;
- experimentos;
- decisiones;
- implementación.

### 5.3 Temporalidad

Se buscarán:

- preregistros;
- decisiones posteriores;
- aclaraciones posteriores;
- posibles usos retrospectivos de evidencia.

### 5.4 Decisiones pendientes

Las decisiones abiertas se registrarán como tales.

La auditoría no las cerrará automáticamente.

### 5.5 Suficiencia de evidencia

Las afirmaciones se clasificarán, cuando corresponda, según si cuentan con:

- evidencia suficiente;
- evidencia parcial;
- inferencia;
- ausencia de justificación identificada.

## 6. Artefactos protegidos

Esta fase comienza protegiendo explícitamente:

- `docs/adr/0003-integracion-eventos-internos.md`;
- `docs/dominio/context-map.puml`.

La existencia de esta protección no significa que dichos artefactos sean
incuestionables.

Significa únicamente que no se modificarán como efecto secundario de la fase de
inventario.

## 7. Primera actividad

El primer artefacto producido por esta fase es:

`docs/m5/auditoria-complementaria/inventario-inicial.md`

Este inventario fija el corpus documental inicial y no constituye todavía un
veredicto semántico.
