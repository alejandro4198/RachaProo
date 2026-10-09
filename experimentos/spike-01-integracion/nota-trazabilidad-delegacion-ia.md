# Nota posterior de trazabilidad — delegación de IA en SPIKE-01

## 1. Estado del documento

Este documento es una **aclaración documental posterior** elaborada durante la
verificación final de entregables de M5.

No corresponde al preregistro original de SPIKE-01.

No constituye evidencia contemporánea de una delegación realizada a una IA
antes o durante la ejecución del experimento.

Su propósito es registrar de forma explícita una limitación de trazabilidad
detectada posteriormente, sin reconstruir como hecho información que no puede
demostrarse con el historial disponible.

## 2. Estado del requisito

**Registro contemporáneo de trabajo delegado a IA para SPIKE-01: NO DEMOSTRADO.**

Durante la revisión final se buscaron artefactos y evidencia histórica capaces
de demostrar, específicamente para SPIKE-01:

- el objetivo concreto delegado a una IA;
- el prompt o instrucción utilizada;
- el alcance autorizado;
- los archivos que la IA podía modificar;
- los archivos o áreas que no podía modificar;
- los criterios específicos usados para auditar el trabajo delegado;
- los supuestos declarados por la IA;
- los elementos que la IA indicó no poder verificar.

No se recuperó evidencia suficiente para afirmar que ese registro formal
existiera durante SPIKE-01.

## 3. Evidencia histórica que sí existe

La cronología verificable de SPIKE-01 es:

- `59fdb47`: preregistro / definición previa del experimento.
- `597e9c7`: implementación y resultados de SPIKE-01.
- `c6ae059`: ADR-003 posterior al experimento.

La evidencia disponible permite demostrar:

- que el preregistro existía antes de la implementación;
- cuál era el objetivo experimental;
- cuál era el criterio experimental;
- qué implementación fue medida;
- qué archivos fueron modificados por el commit experimental;
- cuáles fueron las corridas registradas;
- cuál fue el resultado observado;
- cuál fue el veredicto experimental;
- que ADR-003 fue registrado después del experimento.

## 4. Evidencia que no se recuperó

La revisión histórica no permitió demostrar de forma suficiente:

- Prompt original de delegación: **NO RECUPERADO**.
- Objetivo delegado específicamente a IA: **NO RECUPERADO COMO REGISTRO CONTEMPORÁNEO**.
- Lista previa de archivos autorizados: **NO RECUPERADA**.
- Lista previa de archivos prohibidos: **NO RECUPERADA**.
- Criterios específicos para auditar implementación delegada: **NO RECUPERADOS COMO REGISTRO CONTEMPORÁNEO**.
- Supuestos declarados por IA: **NO RECUPERADOS**.
- Declaración de elementos no verificables por IA: **NO RECUPERADA**.

Por tanto, esos elementos no se reconstruyen retrospectivamente como hechos.

## 5. Separación respecto de SPIKE-03

Existe posteriormente el artefacto:

`experimentos/spike-03-caracterizacion-racha/registro-delegacion-ia.md`

Ese archivo demuestra que en SPIKE-03 se adoptó una forma explícita de
documentar trabajo delegado a IA.

Sin embargo, evidencia de SPIKE-03 no equivale a evidencia contemporánea de
SPIKE-01.

El registro de SPIKE-03 puede utilizarse como referencia metodológica de una
práctica posterior, pero no como prueba de que el mismo procedimiento existiera
durante SPIKE-01.

## 6. Control de temporalidad

Esta nota fue creada después de:

- el preregistro;
- la implementación;
- las mediciones;
- el veredicto experimental;
- ADR-003.

Por ello:

- no modifica la hipótesis original;
- no modifica las condiciones originales;
- no modifica los datos crudos;
- no modifica el resultado;
- no modifica el veredicto;
- no modifica ADR-003;
- no convierte evidencia posterior en causa retrospectiva.

## 7. Control de agencia humana

No se atribuyen a una IA decisiones o acciones que el historial no permita
demostrar.

En particular, esta nota no afirma retrospectivamente:

- qué código fue escrito por una IA;
- qué instrucciones recibió una IA;
- qué decisiones tomó una IA;
- qué partes fueron implementadas directamente por el equipo.

La ausencia de evidencia suficiente se conserva explícitamente.

## 8. Veredicto de trazabilidad

- SPIKE-01 — preregistro: **DEMOSTRADO**.
- SPIKE-01 — implementación: **DEMOSTRADA**.
- SPIKE-01 — medición: **DEMOSTRADA**.
- SPIKE-01 — veredicto: **DEMOSTRADO**.
- SPIKE-01 — cronología preregistro → experimento → ADR: **DEMOSTRADA**.
- SPIKE-01 — registro formal contemporáneo de delegación IA: **NO DEMOSTRADO**.
- Reconstrucción ficticia posterior: **NO REALIZADA**.

Esta limitación se mantiene visible para preservar evidencia, trazabilidad,
temporalidad y agencia humana.
