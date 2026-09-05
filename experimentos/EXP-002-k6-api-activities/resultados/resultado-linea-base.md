# Resultado de línea base - EXP-002

## Configuración

- VUs: 1
- Iteraciones: 25
- Operación: `POST /api/activities`
- Herramienta: k6
- Fecha: 05/09/2026

## Resultado funcional

- Actividades creadas correctamente: 25
- Actividades esperadas: 25
- Tasa de éxito: 100 %
- Checks exitosos: 52 de 52
- Fallos HTTP: 0 %

## Duración de creación

Métrica:

`activity_create_duration`

| Métrica | Resultado |
|---|---:|
| Promedio | 11.16 ms |
| Mediana | 8.48 ms |
| P90 | 10.58 ms |
| P95 | 31.50 ms |
| Mínimo | 6.84 ms |
| Máximo | 46.02 ms |

## Interpretación

Durante EXP-002 las 25 solicitudes de creación finalizaron correctamente.

El máximo observado para la operación HTTP de creación fue 46.02 ms.

El criterio actual definido por el equipo establece un máximo de 1 minuto para crear correctamente una actividad.

Bajo las condiciones de EXP-002, el componente API evaluado permaneció por debajo de ese límite.

Este resultado no demuestra por sí solo el cumplimiento del flujo completo Android end-to-end, porque la medición comienza y termina en la solicitud HTTP al backend.

## Evidencia cruda

- `../logs/exp-002-console.txt`
- `raw-post-activities.json`
