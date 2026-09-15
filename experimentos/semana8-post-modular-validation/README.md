# Semana 8 — Validación post-refactor modular

## 1. Objetivo

Evaluar si la reestructuración del backend de RachaPro hacia un monolito modular introdujo una regresión observable de rendimiento en la consulta de actividades.

La validación compara:

- PRE-MODULAR: commit `2f00da8`
- POST-MODULAR: commit `7213440`

El experimento no busca demostrar que la modularización mejora el rendimiento.

Busca determinar si existe evidencia reproducible de una regresión asociada al cambio arquitectónico.

---

## 2. Configuración

Se reutilizó el harness:

`experimentos/semana8-paginacion-activities/scripts/comparacion-paginacion.js`

Condiciones:

- executor: `constant-vus`;
- VUs: `499`;
- duración: `60s`;
- PostgreSQL y dataset compartidos;
- mismos usuarios sintéticos;
- mismo equipo;
- mismo script k6;
- mismo tamaño de página;
- CONTROL: `GET /api/activities`;
- PAGED: `GET /api/activities/paged?page=0&size=100`.

Cada corrida válida verificó:

- respuesta HTTP correcta;
- conteo esperado correcto;
- `checks.rate = 1`;
- `http_req_failed.rate = 0`;
- éxitos iguales a iteraciones.

---

## 3. Versiones comparadas

### PRE-MODULAR

Commit:

`2f00da8`

Corresponde al estado inmediatamente anterior a la materialización del refactor modular.

### POST-MODULAR

Commit:

`7213440`

Corresponde al merge de:

`refactor(backend): enforce modular monolith boundaries`

Incluye:

- Identity;
- Activities;
- Focus;
- Progress;
- Reminders;
- Shared;
- contratos públicos entre módulos;
- aislamiento de persistencia;
- fitness functions con ArchUnit.

---

## 4. Primera secuencia: PRE -> POST

### PRE-MODULAR

| Corrida | Variante | Avg | Mediana | p90 | p95 | Iteraciones |
|---|---|---:|---:|---:|---:|---:|
| same-session-pre-control-01 | CONTROL | 3556.43 ms | 3544.76 ms | 4446.16 ms | 4944.24 ms | 8615 |
| same-session-pre-control-02 | CONTROL | 3639.13 ms | 3594.79 ms | 4806.53 ms | 5300.83 ms | 8534 |
| same-session-pre-paged-01 | PAGED | 721.63 ms | 701.82 ms | 936.35 ms | 1006.00 ms | 41444 |
| same-session-pre-paged-02 | PAGED | 721.50 ms | 701.95 ms | 959.88 ms | 1023.32 ms | 41439 |

### POST-MODULAR

| Corrida | Variante | Avg | Mediana | p90 | p95 | Iteraciones |
|---|---|---:|---:|---:|---:|---:|
| same-session-post-control-01 | CONTROL | 4201.53 ms | 4204.86 ms | 5020.45 ms | 5281.53 ms | 7327 |
| same-session-post-control-02 | CONTROL | 3940.20 ms | 3641.74 ms | 5320.73 ms | 5724.31 ms | 7894 |
| same-session-post-paged-01 | PAGED | 921.22 ms | 846.59 ms | 1273.15 ms | 1396.98 ms | 32192 |
| same-session-post-paged-02 | PAGED | 899.19 ms | 823.42 ms | 1327.19 ms | 1522.23 ms | 33297 |

Promedios de las métricas reportadas:

| Variante | PRE avg | POST avg | Cambio avg | PRE p95 | POST p95 | Cambio p95 |
|---|---:|---:|---:|---:|---:|---:|
| CONTROL | 3597.78 ms | 4070.87 ms | +13.15 % | 5122.54 ms | 5502.92 ms | +7.43 % |
| PAGED | 721.56 ms | 910.20 ms | +26.14 % | 1014.66 ms | 1459.60 ms | +43.85 % |

Esta secuencia aislada mostraba al estado POST con mayor latencia.

Por sí sola no permite atribuir causalidad a la modularización porque PRE siempre fue ejecutado antes que POST.

---

## 5. Réplica con orden inverso: POST -> PRE

Para controlar parcialmente el efecto del orden temporal se repitió la comparación invirtiendo la secuencia.

### POST-MODULAR

| Corrida | Variante | Avg | Mediana | p90 | p95 | Iteraciones |
|---|---|---:|---:|---:|---:|---:|
| reverse-post-control | CONTROL | 3407.22 ms | 3458.51 ms | 3742.00 ms | 4747.36 ms | 8960 |
| reverse-post-paged | PAGED | 695.46 ms | 685.93 ms | 908.34 ms | 963.93 ms | 43061 |

### PRE-MODULAR

| Corrida | Variante | Avg | Mediana | p90 | p95 | Iteraciones |
|---|---|---:|---:|---:|---:|---:|
| reverse-pre-control | CONTROL | 3473.96 ms | 3553.32 ms | 4014.30 ms | 4901.94 ms | 8783 |
| reverse-pre-paged | PAGED | 736.89 ms | 719.64 ms | 962.76 ms | 1026.91 ms | 40620 |

Cambio POST respecto de PRE:

| Variante | PRE avg | POST avg | Cambio avg | PRE p95 | POST p95 |
|---|---:|---:|---:|---:|---:|
| CONTROL | 3473.96 ms | 3407.22 ms | -1.92 % | 4901.94 ms | 4747.36 ms |
| PAGED | 736.89 ms | 695.46 ms | -5.62 % | 1026.91 ms | 963.93 ms |

Al invertir el orden experimental, la dirección de la diferencia también se invirtió.

---

## 6. Interpretación

La primera secuencia PRE -> POST presentó mayor latencia en POST.

La réplica POST -> PRE no reprodujo ese comportamiento. En ella, POST obtuvo tiempos ligeramente menores que PRE.

Por tanto, la evidencia no muestra una degradación consistente y reproducible asociada al refactor modular.

La inversión de la dirección del efecto indica que existe variabilidad temporal o ambiental relevante bajo el escenario local de alta concurrencia.

No es metodológicamente válido atribuir las diferencias observadas exclusivamente a la reorganización arquitectónica.

---

## 7. Resultado

**✅ NO SE OBSERVÓ UNA REGRESIÓN REPRODUCIBLE ATRIBUIBLE AL REFACTOR MODULAR**

La reestructuración a monolito modular:

- conserva la funcionalidad verificada por los tests automatizados;
- conserva los contratos HTTP utilizados por el experimento;
- no presenta una degradación de rendimiento que se reproduzca al invertir el orden de ejecución;
- mantiene el comportamiento favorable de la variante paginada frente al endpoint que retorna aproximadamente 1000 actividades.

Este resultado no demuestra equivalencia exacta de rendimiento entre ambas versiones.

Tampoco demuestra que la modularización produzca una mejora de rendimiento.

Demuestra que, bajo este procedimiento experimental, no se obtuvo evidencia consistente para afirmar una regresión causada por el refactor.

---

## 8. Paginación después del refactor

La ventaja relativa de PAGED frente a CONTROL permanece visible tanto antes como después de la modularización.

Por tanto, la evidencia previa que valida la paginación como candidato de optimización no queda invalidada por el cambio arquitectónico.

Esto continúa sin demostrar:

- rendimiento para recuperar 1000 registros mediante múltiples páginas;
- comportamiento completo de Android;
- cumplimiento de un SLA;
- eliminación del cuello de botella de Hikari;
- causalidad interna exacta de la diferencia.

---

## 9. Limitaciones

1. Las pruebas se realizaron en un entorno local.

2. El escenario utiliza 499 VUs y constituye una carga elevada sobre una única máquina.

3. No se aleatorizó completamente el orden corrida por corrida.

4. Se utilizaron pocas réplicas.

5. PRE y POST comparten PostgreSQL y dataset, pero no se controlan todas las variables del sistema operativo, JVM y hardware.

6. La inversión del orden reduce una amenaza experimental, pero no constituye una caracterización estadística exhaustiva.

7. Los percentiles resumidos entre corridas son promedios de percentiles reportados por cada corrida, no percentiles globales calculados sobre todas las observaciones individuales.

---

## 10. Decisión

La validación post-refactor se considera suficiente para continuar con los entregables arquitectónicos de Semana 8.

No se abre una acción correctiva de rendimiento contra la modularización porque la degradación inicialmente observada no se reprodujo al invertir el orden experimental.

La optimización por paginación permanece como candidato validado experimentalmente y separado de la decisión arquitectónica de modularización.