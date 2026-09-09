# EXP-003 — Carga concurrente de consulta de actividades

## 1. Objetivo

Evaluar el comportamiento del endpoint autenticado `GET /api/activities` frente a un volumen elevado de datos y niveles crecientes de concurrencia.

EXP-003 complementa a EXP-002:

- EXP-002 evalúa la creación de actividades mediante `POST /api/activities` con 1 VU y 25 iteraciones.
- EXP-003 evalúa la consulta de actividades mediante `GET /api/activities` con un dataset de 500.000 actividades y hasta 500 usuarios concurrentes.

Los experimentos evalúan operaciones y condiciones diferentes y no deben tratarse como mediciones directamente comparables.

---

## 2. Antecedente

El repositorio conserva una prueba de carga histórica realizada el 02/09/2026:

- `docs/performance/prueba-carga-500k.md`
- `docs/performance/resultados-carga-500k.csv`

La prueba histórica utilizó:

- 500 usuarios sintéticos.
- 1.000 actividades por usuario.
- 500.000 actividades en total.
- escenarios concurrentes de 10, 50, 100, 250 y 500 usuarios.
- operación `GET /api/activities`.

Durante la auditoría posterior se verificó que el generador PowerShell original de aquella prueba no quedó versionado en Git y que el dataset sintético histórico ya no estaba presente en PostgreSQL.

Por esta razón, EXP-003 constituye una reconstrucción reproducible del objetivo de aquella prueba, pero no una reproducción bit a bit del experimento histórico.

---

## 3. Dataset

Para EXP-003 se preparó un dataset sintético compuesto por:

- 500 usuarios.
- 3 categorías por usuario.
- 1.000 actividades por usuario.
- 500.000 actividades en total.

Los usuarios sintéticos utilizan correos con el patrón:

`loadtest001@rachapro.test`

hasta:

`loadtest500@rachapro.test`

La contraseña utilizada durante la prueba se proporciona mediante la variable de entorno `LOADTEST_PASSWORD` y no se almacena en el repositorio.

El primer usuario se crea mediante la API del backend para generar credenciales compatibles con el mecanismo real de autenticación. Posteriormente, el script de preparación completa el dataset necesario para la prueba.

---

## 4. Archivos del experimento

### Scripts

- `scripts/preparar-dataset.ps1`
- `scripts/seed-500k.sql`
- `scripts/carga-activities.js`

### Logs

- `logs/k6-10-vus.txt`
- `logs/k6-50-vus.txt`
- `logs/k6-100-vus.txt`
- `logs/k6-250-vus.txt`
- `logs/k6-500-vus.txt`

### Resultados

- `resultados/k6-10-vus.json`
- `resultados/k6-50-vus.json`
- `resultados/k6-100-vus.json`
- `resultados/k6-250-vus.json`
- `resultados/k6-500-vus.json`
- `resultados/resumen-exp-003.csv`

---

## 5. Metodología

El experimento utiliza k6 con el ejecutor `per-vu-iterations`.

Se ejecutaron cinco escenarios:

- 10 VUs.
- 50 VUs.
- 100 VUs.
- 250 VUs.
- 500 VUs.

Cada VU representa un usuario sintético diferente y realiza una iteración de consulta.

### Preparación

Durante `setup()`:

1. se autentica cada usuario mediante `POST /api/auth/login`;
2. se obtiene su JWT;
3. el token se entrega a la fase de carga.

### Fase evaluada

Cada VU ejecuta:

`GET /api/activities`

utilizando su JWT.

Para cada respuesta se comprueba:

1. código HTTP 200;
2. existencia de exactamente 1.000 actividades para el usuario.

La duración del GET se registra mediante la métrica personalizada:

`activity_get_duration`

De esta manera, los tiempos de autenticación ejecutados durante `setup()` no forman parte de esta métrica.

---

## 6. Resultados

| VUs | GET exitosos | Conteo correcto | Promedio ms | Mediana ms | P90 ms | P95 ms |
|---:|---:|---:|---:|---:|---:|---:|
| 10 | 10/10 | 10/10 | 101,21 | 100,68 | 113,57 | 113,79 |
| 50 | 50/50 | 50/50 | 255,32 | 283,73 | 355,84 | 362,50 |
| 100 | 100/100 | 100/100 | 439,13 | 413,38 | 736,21 | 745,43 |
| 250 | 250/250 | 250/250 | 1.173,90 | 1.214,77 | 1.771,76 | 1.867,42 |
| 500 | 500/500 | 500/500 | 1.863,11 | 1.867,80 | 3.002,91 | 3.112,53 |

En todos los escenarios ejecutados:

- el 100 % de los GET evaluados obtuvo respuesta exitosa;
- el 100 % de los usuarios recibió exactamente 1.000 actividades.

En el escenario de 500 VUs:

- 500/500 iteraciones fueron completadas;
- 0 iteraciones fueron interrumpidas;
- 500/500 respuestas cumplieron la validación HTTP;
- 500/500 respuestas contenían exactamente 1.000 actividades;
- promedio: 1.863,11 ms;
- mediana: 1.867,80 ms;
- P90: 3.002,91 ms;
- P95: 3.112,53 ms;
- mínimo: 374,94 ms;
- máximo: 3.239,38 ms.

---

## 7. Interpretación

Bajo las condiciones evaluadas, el endpoint `GET /api/activities` mantuvo la corrección funcional en todos los escenarios ejecutados, incluido el escenario de 500 usuarios concurrentes.

La latencia observada aumenta a medida que aumenta la concurrencia.

Esto permite registrar el comportamiento del sistema bajo las condiciones específicas de EXP-003, pero no demuestra escalabilidad ilimitada ni permite extrapolar automáticamente los resultados a un ambiente de producción.

---

## 8. Comparación con la evidencia histórica

La prueba histórica y EXP-003 comparten:

- 500 usuarios sintéticos;
- 1.000 actividades por usuario;
- 500.000 actividades totales;
- escenarios de 10, 50, 100, 250 y 500 usuarios;
- consulta mediante `GET /api/activities`.

Sin embargo, existen diferencias relevantes:

- la prueba histórica utilizó un generador PowerShell;
- EXP-003 utiliza k6;
- no se conserva el generador histórico original;
- las ejecuciones fueron realizadas en momentos distintos.

Por ello, los resultados pueden conservarse como evidencia histórica y actual del comportamiento observado, pero no constituyen una comparación experimental controlada.

En particular, no debe afirmarse únicamente a partir de estas mediciones que el sistema haya mejorado o empeorado entre ambas pruebas.

---

## 9. Limitaciones

- El backend, PostgreSQL, Docker y el generador de carga fueron ejecutados en el entorno local de desarrollo.
- Los componentes comparten recursos del mismo equipo.
- El dataset utilizado es sintético.
- Cada VU ejecuta una única consulta durante la fase medida.
- La prueba evalúa específicamente `GET /api/activities`.
- No constituye una prueba end-to-end de la aplicación Android.
- No representa una infraestructura distribuida de producción.
- No se definió un umbral de latencia para declarar éxito o fracaso de rendimiento.
- Los resultados solo sustentan conclusiones sobre las condiciones efectivamente probadas.

---

## 10. Seguridad de la evidencia

Los resultados exportados inicialmente por k6 contenían información de `setup_data`.

Antes de versionar la evidencia se eliminó esta información de los JSON y se realizó una búsqueda de patrones compatibles con JWT.

La verificación final no detectó JWT dentro de los archivos versionados de EXP-003.

Las contraseñas sintéticas tampoco se almacenan en los scripts ni en los resultados y deben proporcionarse mediante variables de entorno.
