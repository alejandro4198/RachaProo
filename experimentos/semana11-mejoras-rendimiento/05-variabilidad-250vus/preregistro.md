# Prerregistro — Spike de Variabilidad a 250 VUs (Pool 30)

## 1. Identificación y Pregunta Experimental

- **Proyecto**: RachaPro
- **Módulo**: Semana 11 — Diagnóstico Avanzado de Rendimiento
- **Operación Evaluada**: `POST /api/activities` (250 VUs, Pool 30)
- **Pregunta Experimental**:
  > ¿Por qué bajo exactamente las mismas condiciones de prueba (Pool 30, 250 VUs, duración 60s, dataset determinístico), el sistema exhibe un comportamiento bimodal (Perfil A: throughput moderado/alto ~32–44 req/s y P95 moderado; Perfil B: throughput colapsado ~1.3–1.9 req/s y P95 >1s)? ¿Qué métrica temporal (bloqueo HTTP, espera, threads, red) correlaciona con la transición al Perfil B?

---

## 2. Hipótesis Evaluadas

1. **$H_1$ (Saturación k6 / Cliente)**: El generador k6 experimenta contención de sockets locales al abrir 250 conexiones simultáneas.
2. **$H_2$ (Capa de Socket / Red Local Loopback)**: La pila TCP de Windows/loopback satura el manejo de puertos efímeros.
3. **$H_3$ (Conector HTTP Tomcat / Encolamiento)**: El conector de Tomcat en Spring Boot encola hilos o conexiones antes de entregar al pool.
4. **$H_4$ (Cómputo Backend / CPU)**: Procesador saturado intermitentemente por picos de serialización/deserialización.
5. **$H_5$ (JVM/GC)**: Pausas de recolección de basura (Stop-The-World).
6. **$H_6$ (PostgreSQL)**: Contención de sesiones/locks en base de datos.

---

## 3. Diseño del Spike

- **Configuración**: Pool Hikari = 30, VUs = 250, Duración = 60s.
- **Corridas**: R4, R5, R6 (tres corridas consecutivas idénticas con restauración de semilla previa).
- **Métricas Registradas**: Descomposición completa de latencia k6 (`http_req_blocked`, `http_req_connecting`, `http_req_waiting`, `http_req_receiving`), CPU backend, Hikari, PostgreSQL y host.

---

## 4. Estado del Prerregistro

`PREREGISTRO CREADO Y VERSIONADO ANTES DE LA EJECUCIÓN`
