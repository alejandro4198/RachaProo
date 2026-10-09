# Veredicto semántico final M5

## 1. Base revisada

Branch:

fix/m5-correcciones-documentales

HEAD utilizado para generar el paquete de auditoría semántica de Semanas 9 y 10:

305dd61465c7a7e53fd7e1fdab76e73cd575bc72

Paquete de evidencia:

`docs/m5/auditoria-semantica-semanas9-10.md`

Esta revisión es posterior al freeze y a la auditoría externa ya registrada.

No reescribe retrospectivamente los artefactos históricos.

## 2. Método

La revisión semántica distingue:

- hecho observable;
- análisis;
- evidencia experimental;
- decisión arquitectónica;
- pendiente auténtico;
- afirmación que excede o no excede la evidencia disponible.

La presencia de una palabra o cadena en un artefacto no se utiliza por sí sola
como veredicto.

## 3. AS-01 — Dominio y fronteras

Las fronteras confirmadas de Activities, Focus, Reminders y Progress están
documentadas mediante responsabilidades, reglas, lenguaje y ownership.

No se encontró en el paquete de auditoría semántica de Semanas 9 y 10 evidencia suficiente para concluir que
Achievement deba asignarse automáticamente a uno de esos contextos.

Estado de Achievement:

`NO DECIDIDO`

La revisión no decide el alcance de Identity dentro del Context Map.

## 4. AS-02 — Context Map

La convención documentada del mapa es:

`A --> B = A consume o referencia información de B`

En el estado revisado aparecen:

- Focus → Activities;
- Reminders → Activities;
- Progress → Activities;
- Progress → Focus.

La dirección es coherente con la convención declarada de consumo/referencia.

Achievement continúa fuera del mapa como bounded context confirmado mientras su
ownership siga sin decidir.

Esta revisión no incorpora Identity automáticamente.

## 5. AS-03 — ActivityLookup

La evidencia revisada permite sostener que `ActivityLookup` comprueba
actualmente que la Activity:

- existe;
- pertenece al usuario;
- no está eliminada.

El contrato es consumido por Focus y Reminders.

### H-SEM-01

Se detectó que, aun después de corregir el título del contrato, buena parte del
cuerpo continuaba describiendo exclusivamente el flujo de Focus.

Esto podía hacer ambiguo si la semántica contractual era compartida o si el
documento seguía limitado conceptualmente a Focus.

Estado:

`CORREGIDO`

Corrección:

se añadió una aclaración de alcance compartido sin reescribir los flujos
específicos de Focus ni inventar comportamiento nuevo para Reminders.

Commit:

739fa7a836546852badfaa833f3cfff9ea8c2c0d

El eventual renombre de `ActivityLookup` continúa siendo una decisión
contractual pendiente y no se realiza automáticamente.

## 6. AS-04 — UserRegisteredV1 y AsyncAPI

Las fuentes revisadas son coherentes en que:

- Identity produce `UserRegisteredV1`;
- Activities lo consume;
- el payload incluye `userId` y `occurredAt`;
- el consumidor está configurado con `AFTER_COMMIT`;
- la implementación utiliza un evento interno de Spring.

Precisión importante:

`AFTER_COMMIT` caracteriza la ejecución del listener, no debe utilizarse para
afirmar que la llamada que publica el evento se invoca necesariamente después
del commit.

## 7. AS-05 — ADR-003

La cronología revisada conserva:

`59fdb47`
→ preregistro / definición previa de SPIKE-01

`597e9c7`
→ implementación y resultados de SPIKE-01

`c6ae059`
→ ADR-003

Por tanto, la evidencia experimental principal declarada para la decisión
existía antes de ADR-003.

La evidencia posterior no se interpreta como causa retrospectiva de la decisión.

## 8. AS-06 — SPIKE-01

Se observaron cuatro corridas con tiempos de evento:

- 28 ms;
- 65 ms;
- 36 ms;
- 32 ms.

Las cuatro quedaron dentro del umbral experimental local de 2000 ms.

La primera medición HTTP fue declarada no comparable, pero el dato del evento
permanece utilizable dentro del protocolo registrado.

El veredicto está correctamente limitado a las condiciones probadas.

SPIKE-01 no demuestra:

- escalabilidad general;
- tolerancia universal a fallos;
- necesidad de CQRS;
- necesidad de broker externo;
- conveniencia de convertir todas las relaciones a eventos.

## 9. AS-07 — SPIKE-02

Bajo el fallo inducido revisado:

- el usuario permaneció persistido;
- el efecto secundario en Activities quedó incompleto;
- no se observó retry automático durante los 5 segundos posteriores a retirar
  el fallo;
- no se observó replay durante los 5 segundos posteriores al reinicio.

El experimento no demuestra que retry o replay sean imposibles bajo cualquier
condición.

Tampoco determina por sí solo que ADR-003 deba sustituirse.

## 10. AS-08 — SPIKE-03

La secuencia Git revisada conserva:

`34234e4`
→ preregistro definitivo

`4da5923`
→ harness

`9e51c0d`
→ evidencia y resultados

`cd1c9b0`
→ veredicto experimental

La hipótesis experimental quedó respaldada bajo las condiciones
preregistradas.

El propio experimento separa resultado experimental de decisión arquitectónica.

## 11. AS-09 — CQRS y Event Sourcing

SPIKE-03 aporta evidencia directa sobre el comportamiento de una operación de
Progress, pero no demuestra por sí solo una necesidad de separar modelos de
lectura y escritura.

Estado de CQRS:

`NO ADOPTADO / DECISIÓN NO DETERMINADA`

No se encontró una necesidad demostrada de event store como fuente de verdad,
reconstrucción completa del estado o replay funcional obligatorio que justifique
Event Sourcing.

Estado de Event Sourcing:

`NO ADOPTADO`

Esto no constituye una prohibición futura.

## 12. AS-10 — Temporalidad

La evidencia Git revisada conserva las relaciones temporales relevantes y
permite distinguir:

- evidencia previa;
- decisión;
- evidencia posterior;
- auditoría;
- correcciones post-freeze.

No se utiliza un documento posterior como causa de una decisión anterior.

## 13. AS-11 — Coherencia entre fuentes

Las fuentes revisadas convergen en los elementos principales de:

- consumidores de ActivityLookup;
- UserRegisteredV1;
- payload `occurredAt`;
- estado no adoptado de CQRS / Event Sourcing.

La coincidencia de términos entre documentos no se considera por sí sola prueba
de coherencia; se contrastó además su función documental y temporalidad.

## 14. AS-12 — Fuerza de las afirmaciones

### H-SEM-02

La revisión posterior de ADR-003 contiene formulaciones como:

- `No se requiere entrega durable`;
- `No se requiere retry garantizado actualmente`;
- `No se requiere replay garantizado actualmente`.

La evidencia citada permite afirmar con mayor precisión:

> En las fuentes revisadas no se encontró un requisito explícito que obligue a
> incorporar esas capacidades para el flujo estudiado.

La ausencia de un requisito encontrado no constituye una demostración universal
de que la capacidad no sea necesaria bajo cualquier escenario.

Estado:

`ACLARADO POSTERIORMENTE`

ADR-003 no se modifica, porque la precisión se registra después del freeze y no
debe reescribir la historia del documento.

## 15. AS-13 — Pendientes auténticos

Permanecen como asuntos que requieren decisión explícita del equipo:

- alcance de Identity en el Context Map;
- ownership de Achievement;
- eventual renombre contractual de ActivityLookup;
- política definitiva de versionado de `.idea`.

La auditoría no los resuelve.

## 16. AS-14 — Navegación

El README actual conduce hacia:

- ADR-003;
- dominio;
- integración;
- AsyncAPI;
- SPIKE-01;
- SPIKE-02;
- SPIKE-03;
- auditoría externa post-freeze.

Después de este cierre deberá enlazar también:

- paquete de evidencia de Semanas 9 y 10;
- este veredicto semántico final.

## 17. H-SEM-03 — separación evidencia / veredicto

El archivo generado automáticamente para AS-01–S15 no debe presentarse como si
fuera por sí solo una auditoría semántica concluida.

Estado:

`CORREGIDO`

Se reclasifica explícitamente como:

`PAQUETE DE EVIDENCIA PARA AUDITORÍA SEMÁNTICA`

y el juicio se mantiene en este documento separado.

## 18. Veredicto metodológico final

Con el corpus revisado no se encontró evidencia de una incoherencia
arquitectónica sistémica que invalide en bloque el modelado actual.

Sí se encontraron y trataron hallazgos documentales y metodológicos posteriores:

- H-SEM-01: corregido;
- H-SEM-02: aclarado posteriormente sin modificar ADR-003;
- H-SEM-03: corregido separando evidencia y veredicto.

Este veredicto no significa:

- que toda decisión arquitectónica posible esté cerrada;
- que Identity deba incorporarse o excluirse definitivamente del Context Map;
- que Achievement tenga ownership decidido;
- que ActivityLookup deba conservar su nombre para siempre;
- que CQRS, Event Sourcing, retry, replay, Outbox o broker estén prohibidos.

Significa que las afirmaciones revisadas quedan trazadas con sus condiciones,
temporalidad y límites de inferencia, y que las decisiones pendientes continúan
siendo decisiones del equipo.
