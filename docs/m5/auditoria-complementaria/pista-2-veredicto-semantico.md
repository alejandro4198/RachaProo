# Pista 2 — veredicto semántico de trazabilidad extremo a extremo

## 1. Propósito

Este documento registra la lectura semántica de la matriz:

`pista-2-matriz-trazabilidad.md`

La matriz contiene 21 casos de trazabilidad.

El objetivo de este veredicto no es exigir artificialmente la cadena completa:

`requisito → evidencia → experimento → decisión → artefacto → implementación`

en todos los casos.

El objetivo es evaluar si la evidencia disponible es suficiente para explicar el
estado arquitectónico actual y si existen huecos materiales que obliguen a una
corrección.

## 2. Resultado general

No se identificó una ruptura material de trazabilidad que obligue a modificar:

- código;
- ADR-003;
- Context Map;
- contratos actuales;
- mecanismos actuales de integración.

Los huecos identificados corresponden principalmente a:

- decisiones deliberadamente abiertas;
- cobertura experimental parcial;
- mecanismos técnicos todavía no decididos;
- aclaraciones posteriores;
- patrones arquitectónicos no adoptados.

## 3. Hallazgos

### T2-S01 — Bounded contexts decididos

**Estado:** `VALIDADO — SIN CAMBIO`

Activities, Focus, Reminders y Progress poseen evidencia de dominio suficiente
para explicar su condición actual de bounded contexts decididos.

No se requiere que cada decisión de delimitación haya sido precedida por un
experimento técnico.

La evidencia experimental posterior puede complementar la comprensión del
sistema, pero no se utiliza como causa retrospectiva de esas decisiones.

**Severidad:** informativa.

**Acción:** ninguna corrección obligatoria.

---

### T2-S02 — Identity

**Estado:** `DECISIÓN PENDIENTE`

La trazabilidad demuestra:

- existencia de Identity como capacidad implementada;
- existencia de `UserService`;
- publicación de `UserRegisteredV1`;
- consumo del evento por Activities;
- evaluación experimental del flujo mediante SPIKE-01.

Esta cadena permite explicar la interacción Identity → Activities.

No resuelve por sí misma la pregunta distinta de si Identity debe incorporarse
formalmente al alcance del Context Map.

**Severidad:** media por completitud del modelado.

**Decisión pendiente:** Identity en Context Map.

**Acción:** mantener abierta hasta decisión explícita del equipo.

---

### T2-S03 — Focus → Activities

**Estado:** `VALIDADO — SIN CAMBIO`

Existe:

- necesidad funcional de validar una Activity opcional;
- contrato público `ActivityLookup`;
- consumidor en Focus;
- comparación sync/async dedicada;
- representación en el Context Map.

La trazabilidad es suficiente para describir el mecanismo actual.

Esto no convierte el mecanismo síncrono en una obligación arquitectónica
permanente.

**Severidad:** informativa.

---

### T2-S04 — Reminders → Activities

**Estado:** `VALIDADO — COBERTURA PARCIAL`

Existe trazabilidad suficiente para demostrar el uso actual de
`ActivityLookup`.

No existe una comparación sync/async independiente equivalente a la realizada
para Focus.

La diferencia constituye cobertura experimental parcial, no una contradicción.

**Severidad:** baja.

**Acción:** ninguna modificación automática.

---

### T2-S05 — Identity → Activities / UserRegisteredV1

**Estado:** `VALIDADO — SIN CAMBIO`

La cadena es suficiente para el alcance actual:

necesidad de desacoplar coordinación directa

→ alternativa mediante evento

→ SPIKE-01

→ ADR-003

→ AsyncAPI / documentación de integración

→ implementación productiva.

La decisión está limitada a este flujo específico.

No constituye una política universal de integración mediante eventos.

**Severidad:** informativa.

---

### T2-S06 — SPIKE-01

**Estado:** `VALIDADO — ACLARACIÓN TEMPORAL CONSISTENTE`

La trazabilidad conserva:

1. preregistro;
2. implementación y ejecución;
3. resultados históricos;
4. ADR-003;
5. aclaración metodológica posterior.

Resultados históricos:

`28, 65, 36, 32 ms`

- mediana: 34 ms;
- 4/4 bajo 2000 ms.

Lectura posterior con descarte de la primera corrida:

`65, 36, 32 ms`

- mediana: 36 ms;
- 3/3 bajo 2000 ms.

No existe contradicción si ambas lecturas conservan su contexto temporal.

**Severidad:** media metodológica.

---

### T2-S07 — Resiliencia post-commit

**Estado:** `VALIDADO — SIN CAMBIO ARQUITECTÓNICO`

SPIKE-02 documenta que, bajo las condiciones evaluadas:

- no se observó retry automático en la ventana probada;
- no se observó replay después del reinicio en la ventana probada.

Esto caracteriza una limitación observable.

No demuestra:

- que retry sea imposible;
- que replay sea imposible;
- que Outbox sea obligatorio;
- que un broker sea obligatorio.

**Severidad:** media.

La lectura correcta continúa siendo:

`limitación demostrada ≠ requisito incumplido ≠ cambio obligatorio`

---

### T2-S08 — Retry, replay, Outbox y broker

**Estado:** `NO JUSTIFICADO ACTUALMENTE`

No se encontró en las fuentes revisadas un requisito explícito vigente que
obligue a adoptar estos mecanismos para el flujo estudiado.

La ausencia actual de justificación no equivale a una prohibición futura.

**Severidad:** informativa mientras no exista un nuevo requisito.

**Acción:** reevaluar únicamente ante nueva evidencia o requisito.

---

### T2-S09 — CQRS

**Estado:** `NO ADOPTADO / DECISIÓN NO DETERMINADA`

La caracterización posterior de Progress y el análisis de aplicabilidad aportan
evidencia para evaluar CQRS.

No demuestran una necesidad suficiente para adoptarlo.

SPIKE-03 no causa decisiones arquitectónicas anteriores.

**Severidad:** informativa.

---

### T2-S10 — Event Sourcing

**Estado:** `NO ADOPTADO`

No se identificó evidencia suficiente para justificar Event Sourcing como
mecanismo necesario.

La ausencia de adopción actual no equivale a una prohibición permanente.

**Severidad:** informativa.

---

### T2-S11 — Achievement

**Estado:** `DECISIÓN PENDIENTE`

La capacidad existe en la implementación.

La evidencia disponible no cierra su ownership como:

- parte de Progress;
- parte de Activities;
- parte de otro contexto;
- bounded context independiente.

**Severidad:** media-baja.

**Acción:** conservar `NO DECIDIDO`.

---

### T2-S12 — ActivityLookup naming

**Estado:** `VALIDADO — DECISIÓN ABIERTA`

La semántica contractual vigente es trazable.

La posible ambigüedad del término `active` no obliga por sí sola al renombre.

**Decisión pendiente:** eventual renombre contractual.

**Severidad:** baja.

---

### T2-S13 — Progress → Activities / Progress → Focus

**Estado:** `VALIDADO — COBERTURA TÉCNICA PARCIAL`

Las relaciones conceptuales están documentadas.

La implementación actual demuestra consumo de información proveniente de ambas
capacidades.

No se identificó una comparación sync/async dedicada ni una decisión definitiva
sobre un mecanismo técnico futuro.

Esto no invalida las relaciones del Context Map.

**Severidad:** media-baja.

**Acción:** conservar como relaciones conceptuales sin inventar una decisión
técnica adicional.

## 4. Control temporal

Se mantiene como regla de esta auditoría:

`evidencia posterior ≠ causa retrospectiva`

En particular:

- SPIKE-03 no causa ADR-003;
- refinamientos posteriores del Context Map no causan ADR-003;
- aclaraciones posteriores no reescriben ADR históricos;
- el análisis metodológico posterior de SPIKE-01 no sustituye sus resultados
  históricos.

## 5. Resultado de Pista 2

Los 21 casos de la matriz pueden clasificarse dentro de cuatro grupos:

1. trazabilidad suficiente para el propósito actual;
2. trazabilidad suficiente con aclaración temporal;
3. cobertura parcial pero legítima;
4. decisiones deliberadamente abiertas.

No se identificó un quinto grupo de rupturas críticas que requiera corregir la
arquitectura actual.

## 6. Conclusión

Pista 2 puede cerrarse.

No se requiere modificar código ni artefactos arquitectónicos históricos como
resultado de esta revisión.

Las decisiones pendientes deben permanecer pendientes hasta que el equipo las
resuelva explícitamente.
