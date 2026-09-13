# EXP-004 — Android bajo carga sostenida del backend

## 1. Objetivo

Observar el comportamiento del cliente Android de RachaPro al recuperar actividades mientras el backend se encuentra sometido a una carga sostenida generada con k6.

El experimento compara:

- un baseline Android sin carga k6;
- cinco corridas Android durante una fase sostenida de carga del backend.

EXP-004 no define un umbral de aceptación específico para la recuperación del listado de actividades. Por esta razón, los resultados se interpretan como evidencia de comportamiento y degradación observada, no como aprobación o incumplimiento de un criterio de calidad previamente definido.

---

## 2. Entorno ensayado

- Aplicación Android ejecutada en dispositivo físico.
- Backend Spring Boot ejecutado en entorno local.
- PostgreSQL utilizado por el backend.
- k6 como generador de carga.
- Dataset sintético:
  - 500 usuarios.
  - 1.000 actividades por usuario.
  - 500.000 actividades en total.
- Usuario reservado para Android:
  - `loadtest001@rachapro.test`
- Usuarios utilizados por k6:
  - `loadtest002@rachapro.test` a `loadtest500@rachapro.test`

Las credenciales no se almacenan en scripts ni resultados.

---

## 3. Instrumentación Android

Se instrumentó:

`app/src/main/java/com/example/rachapro/data/repository/ActivityRepository.kt`

Marcadores utilizados:

- `ANDROID_ACTIVITY_GET_START`
- `ANDROID_ACTIVITY_GET durationMs=...`
- `ANDROID_REFRESH_STATUSES_START`
- `ANDROID_REFRESH_STATUSES durationMs=...`

La instrumentación registra duración de operaciones sin almacenar credenciales ni JWT.

---

## 4. Baseline Android sin carga

Se realizaron cinco corridas con:

- backend disponible;
- sin carga k6;
- mismo usuario Android;
- mismo dispositivo físico;
- navegación Home -> Activities.

### GET de actividades

| Corrida | GET |
|---|---:|
| 1 | 266 ms |
| 2 | 211 ms |
| 3 | 164 ms |
| 4 | 161 ms |
| 5 | 178 ms |

Estadísticas:

- mínimo: 161 ms
- mediana: 178 ms
- promedio: 196 ms
- máximo: 266 ms

---

## 5. Carga formal

Configuración de la ejecución formal:

- 499 VUs
- duración sostenida: 600 s
- executor: `constant-vus`
- operación ejercida por k6: `GET /api/activities`
- usuarios k6: `loadtest002` a `loadtest500`
- Android separado: `loadtest001`
- cada respuesta esperada contiene 1.000 actividades

La duración de 600 s corresponde al workload utilizado y no constituye un umbral de aceptación.

---

## 6. Corridas Android bajo carga

| Corrida | Refresh | GET activities | UI visible |
|---|---:|---:|---|
| 1 | 3327 ms | 3483 ms | Sí |
| 2 | 3400 ms | 3582 ms | Sí |
| 3 | 3496 ms | 3384 ms | Sí |
| 4 | 3646 ms | 3305 ms | Sí |
| 5 | 2131 ms | 3578 ms | Sí |

### GET Android bajo carga

- mínimo: 3305 ms
- mediana: 3483 ms
- promedio: 3466.4 ms
- máximo: 3582 ms

Las cinco corridas conservaron el mismo PID Android y la UI de Actividades fue confirmada.

---

## 7. Solapamiento temporal

Inicio de la fase sostenida k6:

`1789332108421`

Fin de la fase sostenida k6:

`1789332711290`

Las cinco corridas Android comenzaron y finalizaron dentro de esta ventana.

Resultado:

- 5/5 corridas con solapamiento confirmado.

Por lo tanto, las observaciones Android corresponden temporalmente a periodos en los que la carga k6 se encontraba activa.

---

## 8. Resultados k6

Métrica `activity_get_duration`:

- promedio: 3142.96 ms
- mediana: 3076.24 ms
- p90: 4213.04 ms
- p95: 4341.84 ms
- mínimo: 110.57 ms
- máximo: 12826.74 ms

Resultados funcionales:

- GET medidos: 95.270
- `activity_get_success`: 95.270
- respuestas con exactamente 1.000 actividades: 95.270
- checks aprobados: 190.540
- checks fallidos: 0
- `http_req_failed`: 0

El proceso k6 terminó con código de salida 0.

---

## 9. Comparación observada

### GET Android

Baseline sin carga:

- mediana: 178 ms
- promedio: 196 ms

Bajo 499 VUs:

- mediana: 3483 ms
- promedio: 3466.4 ms

Relación observada:

- mediana bajo carga ≈ 19.6 veces la mediana sin carga;
- promedio bajo carga ≈ 17.7 veces el promedio sin carga.

La comparación utiliza la misma instrumentación Android para ambos conjuntos de cinco corridas.

---

## 10. Interpretación

EXP-004 evidencia una degradación observable del tiempo de recuperación de actividades en Android durante el workload ensayado de 499 VUs sostenidos.

Los tiempos observados en Android son además compatibles con el orden de magnitud registrado directamente por k6 durante la misma fase de presión.

El experimento no identifica por sí solo cuál componente interno genera la degradación.

La ruta bajo investigación es:

`k6 -> API REST / ActivityController -> ActivityService -> ActivityRepository/JPA -> PostgreSQL -> respuesta`

Antes de proponer una optimización deben investigarse, entre otros aspectos:

- consultas SQL ejecutadas;
- planes de ejecución;
- índices;
- posibles consultas adicionales o N+1;
- volumen de datos transferidos;
- serialización;
- pool de conexiones;
- estrategia de recuperación o paginación.

Estos elementos son hipótesis de investigación y no causas demostradas por EXP-004.

---

## 11. Limitaciones

- Entorno local, no producción.
- Un único dispositivo Android físico observado.
- Cinco corridas Android por condición.
- Dataset sintético.
- No se definió un umbral de aceptación específico para este GET.
- EXP-004 no demuestra escalabilidad general del sistema.
- EXP-004 no localiza por sí mismo el cuello de botella interno.
- EXP-004 no debe compararse directamente con EXP-002 o EXP-003 como si fueran el mismo workload.

---

## 12. Evidencia formal

Resultados:

- `resultados/baseline-android-sin-carga.csv`
- `resultados/android-bajo-carga.csv`
- `resultados/solapamiento-formal.csv`
- `resultados/k6-formal-499vus-600s.json`

Logs:

- `logs/baseline-sin-carga/`
- `logs/android-bajo-carga/`
- `logs/k6-formal-499vus-600s.stdout.txt`
- `logs/k6-formal-499vus-600s.stderr.txt`

Scripts:

- `scripts/carga-sostenida-activities.js`
- `scripts/ejecutar-exp004.ps1`

---

## 13. Integridad de resultados

SHA-256 de los artefactos formales:

| Artefacto | SHA-256 |
|---|---|
| android-bajo-carga.csv | B8FC874D7C6D12C20D4C2FB511544E0EBE9C361F155277E260D7E784A49DD88B |
| baseline-android-sin-carga.csv | ABCA3B82EB2780DDEC2D51351EABBCE0DDEDC134D537E1499B47C76A8BA29306 |
| solapamiento-formal.csv | DEB52E0B5878275858012662B9D6A1E06E38B418AFB1673037A3D872582C0E41 |
| k6-formal-499vus-600s.json | 2E7342BED26240288C767A87BDCD8B4321A0BFB2FA64182486EEBE29D943BD05 |

---

## 14. Conclusión

En las cinco corridas Android realizadas durante la carga sostenida de 499 VUs, el GET de actividades presentó una mediana de 3483 ms y un promedio de 3466.4 ms, frente a 178 ms y 196 ms respectivamente en las cinco corridas sin carga.

Esto constituye evidencia de degradación observable bajo las condiciones ensayadas.

No se declara incumplimiento de un criterio arquitectónico porque no existe un umbral previamente definido para esta operación.

El siguiente paso es investigar la ruta backend para localizar, mediante evidencia adicional, dónde se concentra el costo observado antes de seleccionar o implementar una optimización.
