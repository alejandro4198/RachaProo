# Semana 11 — Resultados de crecimiento de carga sobre POST /api/activities

## 1. Identificación

Este documento registra los resultados de la campaña experimental de crecimiento de carga ejecutada sobre la operación:

`POST /api/activities`

La campaña corresponde a la Semana 11 del proyecto RachaPro y continúa metodológicamente la medición de creación de actividades realizada previamente en EXP-002.

La campaña fue preregistrada antes de ejecutar las mediciones oficiales.

Commits relevantes:

- Prerregistro: `e5d7557`
- Implementación de semilla e instrumento: `93e266a`
- Coordinador de corridas: `76fbc40`

La auditoría de Git confirmó la precedencia:

`prerregistro -> instrumento -> coordinador -> campaña`

Las 32 corridas oficiales registraron el mismo commit experimental del coordinador.

## 2. Condiciones experimentales

La campaña utilizó los siguientes niveles de concurrencia:

- 1 VU
- 10 VUs
- 50 VUs
- 100 VUs
- 250 VUs
- 500 VUs
- 750 VUs
- 1000 VUs

Para cada nivel se realizaron cuatro corridas independientes:

- R1: calentamiento
- R2: medición
- R3: medición
- R4: medición

En total:

- 32 corridas ejecutadas
- 8 corridas de calentamiento
- 24 corridas medidas

Cada escenario utilizó `constant-vus` con una duración objetivo de 60 segundos y `gracefulStop` de 30 segundos.

Entre corridas se restauró la semilla experimental determinística.

La base experimental contenía:

- 1000 usuarios sintéticos
- una categoría activa por usuario
- 100000 actividades activas preexistentes

La distribución de datos fue:

- 200 usuarios con 400 actividades cada uno: 80000 actividades
- 800 usuarios con 25 actividades cada uno: 20000 actividades

La distribución 80/20 corresponde a concentración del conjunto de datos y no a una distribución de frecuencia de solicitudes.

La base de datos experimental fue independiente del entorno local habitual:

`rachapro_perf_s11`

La ejecución utilizó k6 como generador de carga.

## 3. Método de agregación

La corrida R1 de cada nivel fue tratada exclusivamente como calentamiento y no participó en el cálculo representativo.

Para cada nivel se utilizaron R2, R3 y R4.

El P95 representativo fue calculado según el prerregistro como:

`mediana(P95_R2, P95_R3, P95_R4)`

No se combinaron las solicitudes de las tres corridas en un único conjunto.

La tasa de éxito funcional representativa fue calculada como:

`mediana(exito_R2, exito_R3, exito_R4)`

Una creación fue considerada funcionalmente exitosa de acuerdo con la métrica preregistrada:

`activity_create_success`

Los tres criterios preregistrados de señal de degradación fueron:

1. `P95 representativo >= 2 × P95 representativo de 1 VU`
2. `tasa representativa de éxito < 99 %`
3. presencia de HTTP 5xx en al menos dos de las tres corridas medidas válidas

## 4. Resultados

| VUs | P95 R2 (ms) | P95 R3 (ms) | P95 R4 (ms) | P95 representativo (ms) | Multiplicador vs. 1 VU | Éxito representativo | Corridas con 5xx |
|---:|---:|---:|---:|---:|---:|---:|---:|
| 1 | 91.455505 | 67.720555 | 68.858840 | 68.858840 | 1.000 | 100 % | 0 |
| 10 | 90.912340 | 67.090930 | 70.972600 | 70.972600 | 1.031 | 100 % | 0 |
| 50 | 140.104860 | 121.184140 | 140.306100 | 140.104860 | 2.035 | 100 % | 0 |
| 100 | 135.174345 | 220.956940 | 216.036140 | 216.036140 | 3.137 | 100 % | 0 |
| 250 | 256.845075 | 279.942660 | 314.904300 | 279.942660 | 4.065 | 100 % | 0 |
| 500 | 585.388175 | 559.502995 | 417.174610 | 559.502995 | 8.125 | 100 % | 0 |
| 750 | 1619.341300 | 1116.385150 | 765.048200 | 1116.385150 | 16.213 | 100 % | 0 |
| 1000 | 2074.414500 | 1597.275440 | 1539.031880 | 1597.275440 | 23.196 | 100 % | 0 |

La referencia utilizada para el contraste relativo fue el P95 representativo de 1 VU:

`68.85884 ms`

El doble de la referencia corresponde a:

`137.71768 ms`

## 5. Contraste con los criterios preregistrados

| VUs | P95 >= 2x referencia | Éxito < 99 % | 5xx en >= 2/3 |
|---:|:---:|:---:|:---:|
| 1 | No | No | No |
| 10 | No | No | No |
| 50 | Sí | No | No |
| 100 | Sí | No | No |
| 250 | Sí | No | No |
| 500 | Sí | No | No |
| 750 | Sí | No | No |
| 1000 | Sí | No | No |

La primera condición en la cual se satisface el criterio preregistrado de degradación relativa del P95 es 50 VUs.

La tasa de éxito funcional representativa permaneció en 100 % para todos los niveles evaluados.

No se observaron respuestas HTTP 5xx en las 24 corridas medidas.

Por tanto, dentro de las condiciones de esta campaña, se observó una señal de degradación de latencia a partir de 50 VUs, sin una señal simultánea de degradación del éxito funcional ni de aparición repetida de HTTP 5xx.

Este resultado describe el comportamiento observado y no identifica por sí mismo la causa de la degradación.

## 6. Auditoría e integridad de la evidencia

La campaña fue auditada posteriormente sin modificar la evidencia oficial.

Resultados del inventario:

- carpetas oficiales esperadas: 32
- carpetas oficiales encontradas: 32
- carpetas faltantes: 0
- carpetas adicionales: 0
- corridas validadas mediante extracción corregida: 32
- calentamientos: 8
- mediciones: 24
- corridas pendientes de revisión: 0

El manifiesto SHA-256 contenía 96 entradas correspondientes a los artefactos oficiales de las 32 corridas.

Resultado:

- entradas auditadas: 96
- diferencias de tamaño o SHA-256: 0

La evidencia JSONL también fue recorrida para verificar propiedades estructurales y temporales.

Resultado:

- archivos JSONL auditados: 32
- errores de parseo: 0
- inconsistencias de `run_id`: 0
- inconsistencias de `s11_vus`: 0
- errores aparentes de transporte detectados: 0

En las corridas inspeccionadas, la ventana observada de actividad de VUs fue aproximadamente de 60 segundos, coherente con la duración preregistrada.

## 7. Evidencia y trazabilidad

Prerregistro:

`experimentos/semana11-crecimiento-post-activities/00-preregistro.md`

Instrumento k6:

`experimentos/semana11-crecimiento-post-activities/scripts/crecimiento-post-activities.js`

Restauración de semilla:

`experimentos/semana11-crecimiento-post-activities/scripts/restaurar-semilla.ps1`

Verificación de semilla:

`experimentos/semana11-crecimiento-post-activities/scripts/verificar-semilla.ps1`

Coordinador experimental:

`experimentos/semana11-crecimiento-post-activities/scripts/ejecutar-campana.ps1`

Manifiesto de integridad:

`experimentos/semana11-crecimiento-post-activities/evidencia-manifest-sha256.csv`

Resúmenes saneados:

`experimentos/semana11-crecimiento-post-activities/evidencia-saneada/`

Consolidación derivada:

`experimentos/semana11-crecimiento-post-activities/auditoria-s11-corridas-corregida.csv`

`experimentos/semana11-crecimiento-post-activities/auditoria-s11-consolidado-corregido.csv`

La evidencia oficial original contiene información sensible generada durante el `setup`, por lo que no debe versionarse directamente.

Los resúmenes saneados fueron derivados posteriormente eliminando `setup_data`. La sanitización no forma parte de la medición ni modifica las métricas experimentales.

## 8. Limitaciones y próximos pasos

La campaña permite establecer el comportamiento observado de la latencia y del éxito funcional bajo los niveles de concurrencia evaluados.

Sin embargo, esta campaña no dispone de telemetría suficiente para atribuir causalmente la degradación a un componente específico.

No se registraron de manera sincronizada métricas completas de:

- CPU del backend
- memoria del backend
- GC/JVM
- utilización del pool de conexiones
- métricas internas de PostgreSQL
- CPU y memoria del host generador de carga

Por tanto, el aumento del P95 no demuestra por sí mismo saturación del backend ni saturación del generador.

La siguiente actividad consiste en localizar con mayor precisión el fenómeno observado y construir las matrices de fallos y resiliencia antes de seleccionar una táctica arquitectónica y realizar una comparación antes/después.
---

## 9. Cierre metodológico de A1 — Módulo 6

Esta sección consolida el estado del experimento de crecimiento como entregable A1 del Módulo 6.

No modifica retroactivamente:

- el protocolo;
- la semilla experimental;
- los resultados;
- los criterios preregistrados;
- las métricas;
- las 32 corridas documentadas;
- las conclusiones originales.

Su propósito es hacer explícitos los controles de reproducibilidad, trazabilidad y límites de evidencia requeridos para A1.

---

### 9.1 Operación evaluada

La operación experimental fue:

`POST /api/activities`

Herramienta de generación de carga:

`k6`

Modelo de ejecución:

`constant-vus`

Niveles evaluados:

```text
1
10
50
100
250
500
750
1000 VUs
```