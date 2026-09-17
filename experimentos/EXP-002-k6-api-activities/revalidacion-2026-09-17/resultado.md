# Resultado de revalidación posterior - EXP-002

## Identificación

- Fecha: 2026-09-17
- Carácter: revalidación posterior.
- Commit evaluado: `8dc80826ef0ba7e666d6837426c90bc8d5228f8f`
- Endpoint: `POST /api/activities`
- Herramienta: k6 2.2.0
- Configuración: 1 VU, 25 iteraciones por corrida.
- Corridas ejecutadas: 4.
- Corrida 01: conservada como warm-up y excluida del resultado agregado.
- Corridas 02, 03 y 04: utilizadas para el resultado representativo.

Esta revalidación no reemplaza ni modifica la ejecución histórica de EXP-002 realizada el 05/09/2026.

## Resultados por corrida

| Corrida | Uso | Avg (ms) | Mediana (ms) | P90 (ms) | P95 (ms) | Mín. (ms) | Máx. (ms) | Éxito |
|---|---|---:|---:|---:|---:|---:|---:|---:|
| 01 | Warm-up descartada | 15.17 | 9.69 | 15.91 | 23.86 | 7.23 | 123.46 | 100 % |
| 02 | Válida | 13.55 | 9.52 | 19.92 | 38.59 | 8.14 | 59.56 | 100 % |
| 03 | Válida | 9.37 | 7.6 | 11.11 | 16.61 | 7.11 | 25.44 | 100 % |
| 04 | Válida | 8.21 | 7.41 | 8.67 | 9.01 | 6.75 | 22.96 | 100 % |

## Resultado representativo

Se utiliza la mediana de las tres corridas válidas posteriores al warm-up.

| Métrica | Mediana entre corridas 02-04 |
|---|---:|
| Promedio | 9.37 ms |
| Mediana | 7.6 ms |
| P90 | 11.11 ms |
| P95 | 16.61 ms |
| Mínimo | 7.11 ms |
| Máximo | 25.44 ms |

**P95 representativo de la revalidación: 16.61 ms.**

Las tres corridas consideradas válidas completaron 25 de 25 creaciones con tasa de éxito del 100 %.

## Relación con la evidencia histórica

La ejecución histórica del 05/09/2026 conserva su resultado original:

- P95 histórico: 31.50 ms.
- 25 de 25 creaciones exitosas.
- 0 % de fallos HTTP.

El resultado actual corresponde únicamente a una revalidación posterior realizada para reforzar reproducibilidad y disciplina experimental.

No se utiliza esta revalidación para afirmar que el sistema mejoró, porque las dos ejecuciones corresponden a momentos diferentes del proyecto y no constituyen por sí solas un experimento causal controlado.

## Alcance

La medición representa exclusivamente la duración HTTP de `POST /api/activities` bajo las condiciones documentadas.

No demuestra:

- rendimiento end-to-end de Android;
- concurrencia;
- escalabilidad;
- comportamiento en producción;
- equivalencia estadística entre versiones arquitectónicas.

## Evidencia

- `condiciones.md`
- `logs/corrida-01.txt`
- `logs/corrida-02.txt`
- `logs/corrida-03.txt`
- `logs/corrida-04.txt`
- `resultados/corrida-01-raw.json`
- `resultados/corrida-02-raw.json`
- `resultados/corrida-03-raw.json`
- `resultados/corrida-04-raw.json`
- `resultados/resumen-corridas.csv`
