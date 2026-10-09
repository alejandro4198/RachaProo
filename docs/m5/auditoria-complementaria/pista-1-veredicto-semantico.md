# Pista 1 — veredicto semántico de consistencia transversal

## 1. Propósito

Este documento contiene la lectura semántica manual del paquete de evidencia:

`pista-1-evidencia-consistencia-transversal.md`

La extracción textual anterior no se utiliza como veredicto automático.

La evaluación distingue:

- evidencia de inferencia;
- observación de decisión;
- estado histórico de estado vigente;
- decisión pendiente de error;
- limitación demostrada de requisito incumplido.

## 2. Resultado general

No se identificó en Pista 1 una nueva contradicción transversal de severidad alta
que requiera modificar código, ADR-003 o Context Map.

Se identifican principalmente:

- decisiones que continúan abiertas;
- mecanismos actuales correctamente trazados;
- cobertura experimental desigual entre interacciones;
- aclaraciones posteriores que deben conservar su carácter temporal.

## 3. Hallazgos

### P1-S01 — Identity y Context Map

**Estado:** `DECISIÓN PENDIENTE`

**Evidencia:** el corpus documenta Identity como capacidad implementada,
`UserService` como productor de `UserRegisteredV1` y Activities como consumidor.
El Context Map formal continúa representando Activities, Focus, Reminders y
Progress.

**Hecho:** existe una interacción vigente Identity → Activities.

**Problema:** la existencia de la interacción no resuelve por sí misma si Identity
debe incorporarse como bounded context dentro del alcance formal del Context Map.

**Severidad:** media, por completitud del modelado; no constituye defecto de
implementación.

**Decisión pendiente:** alcance definitivo de Identity en el Context Map.

**Acción verificable:** mantener el estado abierto hasta decisión explícita del
equipo.

**Trazabilidad:** consistente con los cierres y auditorías anteriores.

---

### P1-S02 — ActivityLookup

**Estado:** `VALIDADO — SIN CAMBIO`

**Evidencia:** Focus y Reminders consumen `ActivityLookup` como capacidad pública
de Activities.

**Hecho:** `existsActiveActivityForUser(activityId, userId)` comprueba existencia,
pertenencia al usuario y ausencia de borrado lógico según la documentación
vigente.

**Problema:** el término `active` puede resultar más amplio o ambiguo que la
semántica efectiva.

**Severidad:** baja.

**Decisión pendiente:** eventual renombre contractual.

**Acción verificable:** no renombrar sin decisión explícita y análisis de impacto.

**Trazabilidad:** la ambigüedad nominal ya fue registrada anteriormente y no
constituye por sí sola evidencia suficiente para modificar el contrato.

---

### P1-S03 — UserRegisteredV1

**Estado:** `VALIDADO — SIN CAMBIO`

**Evidencia:** Identity / `UserService` publica `UserRegisteredV1`; Activities lo
consume mediante `UserRegisteredV1Listener`.

**Hecho:** la incertidumbre histórica sobre el productor pertenece a un corte
temporal anterior.

**Problema:** utilizar aquella incertidumbre como si describiera el estado actual
produciría una contradicción artificial.

**Severidad:** baja.

**Decisión pendiente:** ninguna adicional derivada de este hallazgo.

**Acción verificable:** conservar ambas etapas con su cronología.

**Trazabilidad:** la actualización posterior del productor resuelve la lectura
vigente sin reescribir el análisis histórico.

---

### P1-S04 — Focus → Activities

**Estado:** `VALIDADO — SIN CAMBIO`

**Evidencia:** Focus utiliza `ActivityLookup` para validar una referencia opcional
a Activity.

**Hecho:** existe contrato síncrono explícito y comparación sync/async dedicada.

**Problema:** ninguno nuevo identificado.

**Severidad:** informativa.

**Decisión pendiente:** ninguna modificación obligatoria.

**Acción verificable:** conservar el mecanismo actual mientras no exista evidencia
que justifique reevaluarlo.

**Trazabilidad:** consistente con Context Map, contrato API y análisis
sync/async.

---

### P1-S05 — Reminders → Activities

**Estado:** `VALIDADO — SIN CAMBIO`

**Evidencia:** Reminders utiliza `ActivityLookup` cuando existe `activityId`.

**Hecho:** el mecanismo síncrono está implementado y documentado.

**Problema:** no se identificó una comparación sync/async independiente equivalente
a la realizada para Focus.

**Severidad:** baja.

**Decisión pendiente:** ninguna modificación arquitectónica automática.

**Acción verificable:** registrar la cobertura como parcial.

**Trazabilidad:** la ausencia de una comparación dedicada no demuestra que el
mecanismo actual sea incorrecto.

---

### P1-S06 — Progress → Activities y Progress → Focus

**Estado:** `VALIDADO — CON LIMITACIÓN DE COBERTURA`

**Evidencia:** Progress consume e interpreta hechos o agregados provenientes de
Activities y Focus.

**Hecho:** las relaciones conceptuales están documentadas en el modelado de
dominio y Context Map.

**Problema:** no se identificó una comparación sync/async dedicada para ninguna
de estas dos interacciones.

**Severidad:** media-baja.

**Decisión pendiente:** mecanismo técnico definitivo de integración si el equipo
considera necesario evaluarlo.

**Acción verificable:** mantener `NO EVALUADA ESPECÍFICAMENTE` respecto de la
comparación sync/async.

**Trazabilidad:** relación de dominio documentada no equivale a selección de un
mecanismo técnico específico.

---

### P1-S07 — CQRS y Event Sourcing

**Estado:** `VALIDADO — SIN CAMBIO`

**Evidencia:** el análisis de aplicabilidad y los documentos posteriores no
adoptan automáticamente ninguno de los dos patrones.

**Hecho:** CQRS permanece `NO ADOPTADO / DECISIÓN NO DETERMINADA`; Event Sourcing
permanece `NO ADOPTADO`.

**Problema:** ninguno nuevo identificado.

**Severidad:** informativa.

**Decisión pendiente:** cualquier adopción futura requiere evidencia adicional.

**Acción verificable:** no introducir ninguno de los patrones por inferencia desde
bounded contexts, eventos internos o SPIKE-03.

**Trazabilidad:** consistente con los cierres anteriores.

---

### P1-S08 — Retry, replay, Outbox y broker externo

**Estado:** `NO JUSTIFICADO ACTUALMENTE`

**Evidencia:** SPIKE-02 no observó retry automático durante la ventana de cinco
segundos posterior a retirar el fallo ni replay durante la ventana de cinco
segundos posterior al reinicio.

**Hecho:** el experimento demuestra comportamiento bajo las condiciones probadas.

**Problema:** ese resultado no demuestra imposibilidad universal ni constituye
por sí mismo un requisito de recuperación durable.

**Severidad:** media por riesgo potencial si la criticidad futura cambia.

**Decisión pendiente:** adopción de mecanismos de recuperación únicamente si
aparece un requisito o escenario que lo justifique.

**Acción verificable:** conservar los límites experimentales explícitos.

**Trazabilidad:** `limitación demostrada ≠ requisito incumplido ≠ cambio
obligatorio`.

---

### P1-S09 — SPIKE-01 y estadística

**Estado:** `VALIDADO — ACLARACIÓN TEMPORAL CONSISTENTE`

**Evidencia histórica:** cuatro corridas de evento:

- 28 ms;
- 65 ms;
- 36 ms;
- 32 ms.

Lectura histórica:

- mediana: 34 ms;
- cumplimiento del umbral de 2000 ms: 4/4.

Lectura metodológica posterior conforme al criterio de descarte de la primera
corrida:

- 65 ms;
- 36 ms;
- 32 ms;
- mediana: 36 ms;
- cumplimiento del umbral: 3/3.

**Hecho:** ambas lecturas describen conjuntos estadísticos distintos.

**Problema:** sustituir retrospectivamente la estadística histórica por la lectura
posterior alteraría la trazabilidad.

**Severidad:** media metodológica.

**Decisión pendiente:** ninguna.

**Acción verificable:** conservar ambas cifras con su contexto temporal.

**Trazabilidad:** preregistro → ejecución → ADR → aclaración metodológica posterior.

---

### P1-AS-10 — “MECANISMO ACTUAL VALIDADO”

**Estado:** `VALIDADO — ACLARADO POSTERIORMENTE`

**Evidencia:** el cierre histórico utilizó la expresión para Focus → Activities y
Reminders → Activities; documentación posterior precisó su interpretación.

**Hecho:** la frase puede leerse de forma más fuerte que la evidencia si se extrae
de su contexto temporal.

**Problema:** “validado” puede confundirse con aprobación arquitectónica universal.

**Severidad:** baja.

**Decisión pendiente:** ninguna.

**Acción verificable:** interpretar la expresión como mecanismo actual observado
y trazado, no como obligación arquitectónica futura.

**Trazabilidad:** no se modifica el documento histórico.

## 4. Resultado de Pista 1

Pista 1 puede cerrarse sin modificar:

- ADR-003;
- Context Map;
- código;
- `ActivityLookup`;
- `UserRegisteredV1`;
- mecanismos de integración actuales.

Permanecen abiertas, entre otras:

- Identity en Context Map;
- ownership de Achievement;
- eventual renombre de ActivityLookup;
- cualquier adopción futura de CQRS;
- Event Sourcing;
- retry;
- replay;
- Outbox;
- broker externo.

## 5. Conclusión

La consistencia transversal del corpus es suficiente para continuar con la
auditoría complementaria.

Las diferencias detectadas corresponden principalmente a evolución temporal,
cobertura desigual de evaluación o decisiones deliberadamente abiertas, y no a
contradicciones que autoricen correcciones automáticas.
