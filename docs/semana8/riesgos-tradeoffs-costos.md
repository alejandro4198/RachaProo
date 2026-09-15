# Riesgos, trade-offs y costos arquitectónicos — Semana 8

## 1. Alcance

Este documento registra consecuencias de la decisión de utilizar un monolito modular en RachaPro.

Los costos se expresan cualitativamente.

No se inventan horas de ingeniería, valores monetarios ni proyecciones económicas no medidas.

---

## 2. Trade-offs

### T01 — Fronteras más fuertes vs. mayor disciplina estructural

Beneficio:

Los módulos poseen responsabilidades, persistencia y contratos definidos.

Costo:

Los desarrolladores deben respetar APIs públicas y evitar accesos directos a componentes internos de otros módulos.

Mitigación:

Fitness functions con ArchUnit.

Estado:

**Aceptado y materializado.**

---

### T02 — Monolito modular vs. microservicios

Beneficio:

- despliegue único;
- llamadas internas simples;
- menor complejidad operacional;
- ausencia de fallos de red entre módulos;
- transacciones existentes más fáciles de preservar.

Costo:

- escalado independiente por módulo no disponible;
- todos los módulos comparten proceso;
- una falla severa del proceso puede afectar el conjunto del backend.

Estado:

**Aceptado mediante ADR-001.**

---

### T03 — Base de datos física compartida vs. independencia de datos

Beneficio:

Mantiene la infraestructura existente y evita migraciones distribuidas.

Costo:

Las fronteras de datos son lógicas y no físicas.

Riesgo:

Un cambio futuro podría reintroducir acceso cruzado directo a repositorios o entidades.

Mitigación:

ADR-002 + ArchUnit + propiedad explícita de persistencia.

---

### T04 — Comunicación síncrona vs. desacoplamiento temporal

Beneficio:

Modelo simple y consistente con el comportamiento existente.

Costo:

Un consumidor depende temporalmente del proveedor durante la llamada.

Ejemplos:

- Identity -> Activities;
- Focus -> Activities;
- Reminders -> Activities.

Estado:

**Aceptado para el AS-IS.**

No existe evidencia que justifique introducir mensajería asíncrona en este punto.

---

### T05 — Contratos públicos internos vs. acceso directo a repositorios

Beneficio:

Reduce exposición de detalles de persistencia.

Costo:

Requiere interfaces y servicios adaptadores adicionales.

Contratos materializados:

- `ActivityLookup`;
- `DefaultCategoryProvisioning`.

Estado:

**Aceptado mediante ADR-002.**

---

## 3. Riesgos

| ID | Riesgo | Evidencia/causa | Mitigación actual | Estado |
|---|---|---|---|---|
| R01 | Reintroducir dependencias indebidas entre módulos | evolución futura del código | ArchUnit | 🟡 Mitigado, requiere vigilancia |
| R02 | Crear ciclos entre capacidades | nuevas dependencias futuras | regla de ciclos ArchUnit | 🟡 Mitigado |
| R03 | Acceso a persistencia ajena | DB compartida físicamente | ADR-002 + fitness functions | 🟡 Mitigado |
| R04 | Shared convertirse en módulo comodín | crecimiento de utilidades globales | regla sin repositorios/entidades de negocio | 🟡 Mitigado |
| R05 | Presión de conexiones bajo alta concurrencia | diagnóstico Semana 8 | paginación reduce trabajo por respuesta, diagnóstico pendiente de capacidad | 🧪 Abierto |
| R06 | Endpoint no paginado continúe siendo costoso con grandes datasets | aproximadamente 1000 actividades en dataset de prueba | endpoint paginado disponible | 🧪 Abierto para migración de consumidor |
| R07 | Límites de página con orden no totalmente determinista ante empates | orden actual por dueDateEpochDay | añadir segundo criterio estable si se adopta paginación definitiva | 🧪 Pendiente |
| R08 | Confundir módulos con microservicios | nomenclatura arquitectónica | C4 declara explícitamente proceso único | ✅ Documentado |

---

## 4. Costos arquitectónicos

### Costos ya asumidos

- reorganización física de paquetes;
- creación de contratos intermodulares;
- implementación de adaptadores de Activities;
- incorporación de ArchUnit;
- mantenimiento de ADR y C4;
- mayor disciplina para modificar dependencias.

### Costos evitados

Al no adoptar microservicios se evitan, por ahora:

- despliegues independientes;
- service discovery;
- comunicación de red entre capacidades;
- observabilidad distribuida;
- coordinación de contratos remotos;
- consistencia eventual obligatoria;
- infraestructura adicional de mensajería.

### Costos pendientes

No se cuantifican porque no existe evidencia medida de horas o dinero.

Sí existen costos potenciales futuros asociados a:

- crecimiento del volumen de datos;
- capacidad del pool de conexiones;
- evolución de PostgreSQL;
- adopción completa de paginación;
- separación física de módulos si alguna capacidad llegara a justificarlo.

---

## 5. Decisión

Los trade-offs identificados son compatibles con el tamaño y evidencia actual del sistema.

No se propone migrar a microservicios como consecuencia automática del refactor.

Una separación futura deberá justificarse mediante necesidades y evidencia nuevas, no únicamente por la existencia de módulos internos.