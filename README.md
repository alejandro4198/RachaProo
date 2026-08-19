# RachaPro

## 1. Identificación del proyecto

| Campo | Información |
|---|---|
| Proyecto | RachaPro |
| Integrante | Alejandro Villamizar Rodriguez |
| Curso | Arquitectura de Software |
| Semana actual | Semana 4 |
| Sistema base | Implementación funcional actual de RachaPro para Android, previa a cualquier cambio arquitectónico derivado de los experimentos de Arquitectura de Software. |
| Repositorio | https://github.com/alejandro4198/RachaProo.git |
| Línea base funcional | `3b088858061edc47f6cd018a4130a38c2afb0f73` |
| Commit instrumentado utilizado para EXP-001 | `59c182b12c7a2678f6ed09d1b69b399327d097d4` |

## 2. Navegación de evidencias

| Semana | Actividad realizada | Evidencia / ubicación |
|---|---|---|
| Semana 1 | Localización y recuperación de documentación previa y prototipo de RachaPro. | `docs/sources/historical/` |
| Semana 2 | Elaboración del documento de trabajo para Arquitectura de Software. | `docs/sources/historical/ProyectoAS.docx` |
| Semana 3 | Planificación diaria de los módulos que se desarrollarían en el proyecto. | PENDIENTE DE LOCALIZAR LA EVIDENCIA |
| Semana 4 | Implementación funcional del MVP Android, definición de escenarios de calidad, ejecución de EXP-001 y consolidación de la línea base. | `dossier/` y `experimentos/EXP-001-linea-base/` |

## 3. Dossier

| Documento | Enlace |
|---|---|
| Contexto y drivers | [01-contexto-y-drivers](dossier/01-contexto-y-drivers.md) |
| Escenarios de calidad | [02-escenarios-de-calidad](dossier/02-escenarios-de-calidad.md) |
| Medición y línea base | [04-evidencia-ejecutable](dossier/04-evidencia-ejecutable.md) |

## 4. Experimento de línea base

| Evidencia | Enlace |
|---|---|
| EXP-001 - Línea base | [EXP-001-linea-base](experimentos/EXP-001-linea-base/README.md) |
| Condiciones de ejecución | [condiciones](experimentos/EXP-001-linea-base/condiciones.md) |
| Resultado de línea base | [resultado-linea-base](experimentos/EXP-001-linea-base/resultados/resultado-linea-base.md) |
| Cálculo estadístico | [calculo-estadistico](experimentos/EXP-001-linea-base/resultados/calculo-estadistico.txt) |

### Resultado principal de EXP-001

| Métrica | Resultado |
|---|---:|
| Mediana originalmente planificada | 1308 ms (1.308 s) |
| Mediana de 25 mediciones válidas | 1325 ms (1.325 s) |
| P95 - Nearest Rank | 1621 ms (1.621 s) |
| Máximo observado | 1666 ms (1.666 s) |
| Umbral histórico RNF02 | ≤ 3 s |

La comparación con RNF02 debe interpretarse como una aproximación, ya que EXP-001 finaliza en `ActivitiesUiState.Success` y no mide directamente la finalización del renderizado visual del listado.

## 5. Arquitectura del sistema

| Evidencia | Enlace |
|---|---|
| Arquitectura actual observada | [Ver arquitectura actual](docs/architecture/current/architecture-current.md) |
| Comparación histórica vs. actual | [Ver comparación arquitectónica](docs/architecture/comparison-historical-current.md) |

## 6. Trazabilidad Git

| Elemento | Referencia |
|---|---|
| Rama de trabajo | `master` |
| Línea base funcional | `3b088858061edc47f6cd018a4130a38c2afb0f73` |
| Preparación documental de EXP-001 | `93e7db49312b` |
| Commit instrumentado utilizado para medir | `59c182b12c7a2678f6ed09d1b69b399327d097d4` |
| Commit de cierre de evidencias de Semana 4 | `42d78efffd8d880477e39f59c3add69522e302c0` |
| Historial en GitHub | [Ver historial de commits](https://github.com/alejandro4198/RachaProo/commits/master/) |

### Historial relevante

| Commit | Mensaje |
|---|---|
| `280934d` | `Initial commit` |
| `0f13f0d` | `Correcion de errores` |
| `3b088858061e` | `Define linea base y documentacion previa al experimento` |
| `93e7db49312b` | `Documenta preparacion de EXP-001` |
| `59c182b12c7a` | `Instrumenta medicion de EXP-001` |
| `42d78efffd8d` | `Cierra evidencia y linea base de Semana 4` |

### Estado de trazabilidad

El commit `3b088858061edc47f6cd018a4130a38c2afb0f73` identifica la línea base funcional previa a la instrumentación.

El commit `59c182b12c7a2678f6ed09d1b69b399327d097d4` corresponde a la versión instrumentada utilizada durante las mediciones de EXP-001.

El commit `42d78efffd8d880477e39f59c3add69522e302c0` consolida los dossiers, logs, resultados, script reproducible y documentación de cierre correspondientes a la evidencia de Semana 4.