# Revalidación posterior de EXP-002

- Fecha: 2026-09-17
- Carácter: revalidación posterior; no reemplaza la ejecución histórica del 05/09/2026
- Commit evaluado: `8dc80826ef0ba7e666d6837426c90bc8d5228f8f`
- Endpoint: `POST /api/activities`
- Base URL: `http://localhost:8080`
- VUs: 1
- Iteraciones por corrida: 25
- Corridas: 4
- Corrida 01: calentamiento, conservada pero excluida del agregado
- Corridas 02-04: válidas para resultado agregado
- Herramienta: k6.exe v2.2.0 (commit/00a9a1b7f5, go1.26.5, windows/amd64)

## Máquina

- Sistema operativo: Microsoft Windows 11 Pro for Workstations
- CPU: Intel(R) Xeon(R) E-2224G CPU @ 3.50GHz
- RAM: 31.84 GB
- Plan de energía: GUID de plan de energía: 381b4222-f694-41f0-9685-ff5bb260df2e  (Equilibrado)
- Estado de energía: Sin batería detectada

## Criterios

- Backend y PostgreSQL disponibles antes de iniciar.
- Mismo script baseline-activities.js.
- Sin cambios de código entre corridas.
- Cada corrida conserva 1 VU y 25 iteraciones.
- HTTP 201 y presencia de id se verifican mediante el script.
- Credenciales no se versionan.

## Limitaciones

- Entorno local.
- Un único usuario.
- No evalúa concurrencia.
- No evalúa el flujo Android end-to-end.
- No constituye evidencia de producción.
