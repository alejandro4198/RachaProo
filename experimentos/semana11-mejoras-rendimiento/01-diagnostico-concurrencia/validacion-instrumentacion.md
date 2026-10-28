# Validación de Instrumentación — SMOKE-INSTRUMENTACION

## Evidencia
- Directorio: `experimentos/semana11-mejoras-rendimiento/01-diagnostico-concurrencia/evidencia-smoke/`
- Ejecutado con backend en `http://127.0.0.1:8080` (PID java 21956). PostgreSQL `rachapro-postgres` (DB `rachapro_db`, usuario `rachapro_user`).

## Tabla de Captura
| Fuente | Métrica | Capturada | Archivo |
|---|---|---|---|
| k6 | http_reqs / p(95) / failed | SÍ (1 req, p(95)=9.5ms, failed 100% en setup/env actual) | `muestras.json`, `resumen.json`, `k6-stdout.txt` |
| host | CPU/memoria | SÍ | `host-capture-*.csv` |
| JVM/backend (Actuator) | process.cpu.usage, jvm.threads.live, hikaricp.connections.active, jvm.memory.*, jvm.gc.pause, etc. | SÍ (múltiples métricas) | `backend-metrics.csv` |
| HikariCP | hikaricp.connections.active/idle/max/min/pending | SÍ (disponible vía Actuator/Micrometer transitivo) | `backend-metrics.csv` |
| PostgreSQL | pg_stat_activity (active/total), pg_locks | SÍ (tras corrección de contenedor/credenciales) | `postgres-capture-*.csv` |

## Observaciones
- Actuator habilitado: `/actuator/health` (200), `/actuator/metrics` expuesto. Métricas HikariCP y JVM/proceso disponibles.
- Capturadores sincronizados con timestamps ISO-8601.
- Script PostgreSQL corregido para usar `rachapro-postgres` / `rachapro_db` / `rachapro_user`.

## Conclusión
`INSTRUMENTACIÓN LISTA`
