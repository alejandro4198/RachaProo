# Pista 2 — matriz de trazabilidad extremo a extremo

## 1. Propósito

Esta pista revisa la trazabilidad entre distintos tipos de evidencia y decisiones
arquitectónicas.

La cadena de referencia es:

`requisito → evidencia → experimento → decisión → artefacto → implementación`

Esta cadena no se interpreta como una obligación de que todos los casos deban
contener necesariamente los seis eslabones.

La auditoría debe distinguir entre:

- decisiones que requieren evidencia experimental;
- decisiones de modelado sustentadas por análisis de dominio;
- observaciones directas del repositorio;
- estados pendientes que deliberadamente no deben cerrarse;
- evidencia posterior que solo aporta trazabilidad adicional.

## 2. Reglas metodológicas

### 2.1 No completar huecos artificialmente

La ausencia de un experimento no invalida automáticamente una decisión de dominio.

La ausencia de requisito explícito no equivale a prohibición.

Una implementación existente no demuestra por sí sola que exista una decisión
arquitectónica formal.

### 2.2 Temporalidad

Una evidencia posterior puede:

- confirmar;
- aclarar;
- ampliar trazabilidad.

No puede utilizarse como causa retrospectiva de una decisión tomada antes.

### 2.3 Agencia

Los huecos de trazabilidad no autorizan a esta auditoría a decidir por el equipo:

- Identity en Context Map;
- ownership de Achievement;
- eventual renombre de ActivityLookup;
- CQRS;
- Event Sourcing;
- retry;
- replay;
- Outbox;
- broker externo.

## 3. Matriz principal

| ID | Tema | Requisito / necesidad | Evidencia | Experimento | Decisión | Artefacto | Implementación | Estado de trazabilidad |
|---|---|---|---|---|---|---|---|---|
| T2-01 | Activities como bounded context | Necesidad de separar responsabilidades de planificación y ciclo de vida de Activity | Evidencia de dominio y ownership documentada | No requerido como condición necesaria | Activities como BC independiente | `docs/dominio/` + Context Map | módulo Activities | COMPLETA PARA DECISIÓN DE DOMINIO |
| T2-02 | Focus como bounded context | Separar ciclo de vida Pomodoro de Activities | Evidencia de dominio y responsabilidades | No requerido como condición necesaria | Focus como BC independiente | `docs/dominio/` + Context Map | módulo Focus | COMPLETA PARA DECISIÓN DE DOMINIO |
| T2-03 | Reminders como bounded context | Separar intención y ciclo de vida de Reminder | Evidencia de dominio y responsabilidades | No requerido como condición necesaria | Reminders como BC independiente | `docs/dominio/` + Context Map | módulo Reminders | COMPLETA PARA DECISIÓN DE DOMINIO |
| T2-04 | Progress como bounded context | Interpretar y agregar hechos producidos por otras capacidades | Evidencia de dominio, rachas y métricas | SPIKE-03 aporta evidencia posterior de caracterización; no causa la decisión original | Progress como BC independiente | `docs/dominio/` + Context Map | módulo Progress | COMPLETA CON EVIDENCIA POSTERIOR ADICIONAL |
| T2-05 | Identity en Context Map | Existe capacidad Identity e interacción con Activities | Evidencia de código, ADR-003 y `UserRegisteredV1` | SPIKE-01 evalúa el flujo Identity → Activities | Alcance de Identity en Context Map no decidido | docs de dominio + auditorías M5 | Identity/UserService existe | DECISIÓN PENDIENTE |
| T2-06 | Focus → Activities | Focus necesita validar Activity opcional | ActivityLookup observado y documentado | Comparación sync/async dedicada | Mantener mecanismo síncrono observado; no constituye obligación permanente | `contrato-api.md`, `sincrono-vs-asincrono.md`, Context Map | Focus consume ActivityLookup | TRAZABLE |
| T2-07 | Reminders → Activities | Reminders necesita validar Activity opcional | ActivityLookup observado y documentado | No existe comparación sync/async independiente equivalente a Focus | Sin cambio arquitectónico decidido | `contrato-api.md`, cobertura sync/async, Context Map | Reminders consume ActivityLookup | TRAZABLE CON COBERTURA EXPERIMENTAL PARCIAL |
| T2-08 | Identity → Activities | Evitar coordinación directa para aprovisionamiento post-registro | flujo histórico DefaultCategoryProvisioning y alternativa por evento | SPIKE-01 | ADR-003 adopta `UserRegisteredV1` para ese flujo específico | ADR-003 + AsyncAPI + docs integración | UserService publica; listener en Activities consume | COMPLETA PARA ESE FLUJO |
| T2-09 | UserRegisteredV1 | Comunicar hecho de registro sin transferir ownership | contrato `userId` + `occurredAt`; productor/consumidor trazables | SPIKE-01 y observación posterior de resiliencia en SPIKE-02 | Adoptado específicamente para Identity → Activities | ADR-003 + AsyncAPI | implementación productiva actual | COMPLETA PARA ALCANCE ACTUAL |
| T2-10 | SPIKE-01 | Evaluar alternativa interna por evento | preregistro histórico | 4 corridas históricas; aclaración posterior con descarte de primera | soporta ADR-003 bajo condiciones probadas | README del spike + ADR-003 | mecanismo materializado | TRAZABLE CON ACLARACIÓN METODOLÓGICA POSTERIOR |
| T2-11 | Resiliencia post-commit | Caracterizar fallo del consumidor | riesgo identificado tras adopción del evento | SPIKE-02 | no se adopta automáticamente retry/replay/Outbox/broker | spike-02 + auditorías posteriores | comportamiento observado bajo prueba | TRAZABLE SIN REQUISITO DE CAMBIO |
| T2-12 | Retry | No se encontró requisito explícito de retry garantizado en fuentes revisadas | SPIKE-02 no observó retry en ventana probada | SPIKE-02 | NO JUSTIFICADO ACTUALMENTE | cierres M5 | no hay mecanismo explícito demostrado | TRAZABLE COMO NO JUSTIFICADO |
| T2-13 | Replay | No se encontró requisito explícito de replay garantizado en fuentes revisadas | SPIKE-02 no observó replay tras reinicio en ventana probada | SPIKE-02 | NO JUSTIFICADO ACTUALMENTE | cierres M5 | no hay mecanismo explícito demostrado | TRAZABLE COMO NO JUSTIFICADO |
| T2-14 | Outbox / entrega durable | No se encontró requisito explícito que lo obligue actualmente | SPIKE-02 muestra riesgo potencial post-commit | SPIKE-02 | NO JUSTIFICADO ACTUALMENTE | cierres M5 | no implementado como requisito actual | TRAZABLE COMO NO JUSTIFICADO |
| T2-15 | Broker externo | No se encontró necesidad actual demostrada | aplicación única y evento interno Spring | SPIKE-01 / SPIKE-02 aportan contexto pero no obligación | NO JUSTIFICADO ACTUALMENTE | ADR-003 + cierres M5 | no implementado | TRAZABLE COMO NO JUSTIFICADO |
| T2-16 | CQRS | Evaluar si separar comandos y lecturas aporta valor | análisis de aplicabilidad + caracterización de Progress | SPIKE-03 aporta evidencia posterior | NO ADOPTADO / DECISIÓN NO DETERMINADA | aplicabilidad CQRS/ES + auditorías | no adoptado como patrón | TRAZABLE COMO DECISIÓN NO DETERMINADA |
| T2-17 | Event Sourcing | Evaluar necesidad de historial/reconstrucción como fuente de verdad | análisis de aplicabilidad | no existe experimento que justifique adopción | NO ADOPTADO | aplicabilidad CQRS/ES + auditorías | no implementado como patrón | TRAZABLE COMO NO ADOPTADO |
| T2-18 | Achievement ownership | Capacidad existente con ownership no resuelto | evidencia de dominio y código | no requiere cierre experimental automático | NO DECIDIDO | docs dominio + auditorías | capacidad implementada | DECISIÓN PENDIENTE |
| T2-19 | ActivityLookup naming | Contrato necesita expresar capacidad de Activities | semántica efectiva documentada | no requiere experimento para mantener nombre actual | SIN CAMBIO JUSTIFICADO; eventual renombre abierto | contrato API + auditorías | contrato vigente | TRAZABLE CON DECISIÓN ABIERTA |
| T2-20 | Progress → Activities | Progress consume información producida por Activities | dominio + Context Map + código Android/local | no existe comparación sync/async dedicada | mecanismo técnico definitivo no decidido | dominio + cobertura interacciones | flujo actual distribuido según implementación | COBERTURA TÉCNICA PARCIAL |
| T2-21 | Progress → Focus | Progress consume resultados producidos por Focus | dominio + Context Map + implementación | no existe comparación sync/async dedicada | mecanismo técnico definitivo no decidido | dominio + cobertura interacciones | flujo actual según implementación | COBERTURA TÉCNICA PARCIAL |

## 4. Clasificación de trazabilidad

### 4.1 Completa para el propósito actual

Se consideran suficientes para su alcance actual:

- Activities como bounded context;
- Focus como bounded context;
- Reminders como bounded context;
- Progress como bounded context;
- Focus → Activities;
- Identity → Activities mediante `UserRegisteredV1`;
- contrato y estado actual de `UserRegisteredV1`.

“Completa” no significa inmutable.

Significa únicamente que la cadena disponible es suficiente para explicar el
estado arquitectónico actual sin inventar evidencia adicional.

## 4.2 Completa con aclaración temporal

SPIKE-01 mantiene dos lecturas distintas:

### Histórica

`28, 65, 36, 32 ms`

- mediana: 34 ms;
- 4/4 bajo el umbral.

### Posterior conforme al criterio metodológico de descarte

`65, 36, 32 ms`

- mediana: 36 ms;
- 3/3 bajo el umbral.

La segunda lectura no sustituye retrospectivamente la primera.

## 4.3 Parcial pero no incorrecta

Mantienen cobertura parcial:

- Reminders → Activities respecto de comparación sync/async dedicada;
- Progress → Activities respecto del mecanismo técnico definitivo;
- Progress → Focus respecto del mecanismo técnico definitivo.

La cobertura parcial no constituye por sí sola un defecto arquitectónico.

## 4.4 Deliberadamente abierta

Continúan abiertas:

- Identity dentro del alcance formal del Context Map;
- ownership de Achievement;
- eventual renombre de ActivityLookup;
- adopción futura de CQRS;
- cualquier adopción futura de mecanismos de resiliencia que requiera nueva
  justificación.

## 5. Temporalidad relevante

La trazabilidad debe conservar el siguiente principio:

`evidencia posterior ≠ causa retrospectiva`

En particular:

- SPIKE-03 no causa ADR-003;
- refinamientos posteriores del Context Map no causan ADR-003;
- correcciones M5 posteriores no cambian el contenido histórico de ADR-003;
- la aclaración estadística posterior de SPIKE-01 no modifica las cifras usadas
  históricamente en el momento de la decisión.

## 6. Resultado preliminar de Pista 2

La matriz no identifica por sí sola una obligación de modificar arquitectura.

Los huecos encontrados corresponden principalmente a:

- decisiones legítimamente abiertas;
- cobertura experimental parcial;
- ausencia deliberada de adopción de patrones;
- diferencias entre evidencia histórica y aclaraciones posteriores.

La siguiente etapa deberá evaluar semánticamente si alguno de estos huecos
constituye un hallazgo material de trazabilidad.
