# Prompt original — Propuesta de microservicios por IA

- **Actividad:** Semana 12 — Actividad 4
- **Objetivo:** Obtener una propuesta arquitectónica independiente para posteriormente auditarla.
- **Herramienta:** ChatGPT
- **Fecha:** 2026-10-21
- **Estado:** Evidencia original previa a auditoría

---

## Prompt

Actúa como un arquitecto de software senior especializado en sistemas backend, arquitecturas distribuidas, microservicios, rendimiento, seguridad y disponibilidad.

Necesito que propongas una arquitectura de distribución para un proyecto universitario llamado RachaPro.

Contexto del sistema:

RachaPro es una aplicación móvil de productividad académica.

Frontend:
- aplicación Android;
- consume un backend mediante HTTP/JSON;
- utiliza autenticación JWT.

Backend actual:
- Kotlin + Spring Boot;
- PostgreSQL;
- actualmente existe una sola aplicación backend desplegable.

Capacidades funcionales principales:

1. Identity
   - usuarios;
   - autenticación;
   - seguridad.

2. Activities
   - actividades;
   - categorías;
   - subtareas.

3. Focus
   - sesiones Pomodoro.

4. Progress
   - progreso;
   - logros.

5. Reminders
   - recordatorios.

Actualmente las capacidades se encuentran organizadas dentro de un monolito modular.

Existen dependencias funcionales entre algunas capacidades, entre ellas:

- Focus necesita consultar información de Activities.
- Reminders necesita consultar información de Activities.
- Activities reacciona a información originada desde Identity.

Los atributos de calidad priorizados son:

1. rendimiento;
2. seguridad;
3. usabilidad;
4. disponibilidad.

En pruebas de carga se ha observado degradación relativa de latencia al aumentar la concurrencia, aunque no se ha identificado causalmente qué componente produce dicha degradación.

También se ha probado el comportamiento de la aplicación durante una indisponibilidad temporal del backend.

Quiero que evalúes específicamente si sería conveniente distribuir el backend mediante microservicios.

Tu respuesta debe proponer una arquitectura concreta, no limitarse a explicar qué son los microservicios.

Debes indicar:

1. qué microservicios crearías;
2. qué responsabilidad tendría cada uno;
3. cómo se comunicarían entre ellos;
4. si utilizarías comunicación síncrona, eventos o ambas;
5. si introducirías API Gateway;
6. si introducirías un broker de mensajería y cuál;
7. cómo organizarías la persistencia;
8. si utilizarías una base de datos por servicio;
9. cómo manejarías autenticación y autorización entre servicios;
10. qué mecanismos de resiliencia utilizarías;
11. qué mecanismos de observabilidad utilizarías;
12. cómo desplegarías los servicios;
13. cómo escalarías los servicios;
14. qué cambios harías al CI/CD;
15. qué ventajas tendría tu propuesta frente al monolito modular actual;
16. qué riesgos y costos adicionales introduciría;
17. qué componentes consideras obligatorios y cuáles opcionales;
18. una recomendación final sobre si RachaPro debería migrar ahora a microservicios.

No asumas datos monetarios ni métricas que no te he proporcionado.

Si necesitas hacer supuestos, identifícalos expresamente como SUPUESTOS.

Distingue claramente entre:

- hechos proporcionados;
- inferencias arquitectónicas;
- supuestos;
- recomendaciones.

No conozcas ni utilices decisiones posteriores de ADR que no estén incluidas en este prompt.