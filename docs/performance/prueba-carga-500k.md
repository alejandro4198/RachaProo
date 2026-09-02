# Prueba de carga RachaPro - 500k actividades

Fecha: 2026-09-02

## Objetivo

Evaluar el comportamiento del backend de RachaPro con un volumen elevado de datos y niveles crecientes de concurrencia.

## Datos de prueba

- 500 usuarios sintéticos.
- 1.000 actividades por usuario.
- 500.000 actividades sintéticas en total.
- 3 categorías por usuario sintético.
- PostgreSQL ejecutándose en Docker.
- Backend Spring Boot ejecutándose localmente.

## Escenarios de concurrencia

Se realizaron solicitudes GET /api/activities con:

- 10 usuarios concurrentes.
- 50 usuarios concurrentes.
- 100 usuarios concurrentes.
- 250 usuarios concurrentes.
- 500 usuarios concurrentes.

Cada usuario debía recuperar exactamente 1.000 actividades.

En todos los escenarios ejecutados las solicitudes terminaron exitosamente y cada usuario obtuvo las 1.000 actividades esperadas.

La prueba de 500 usuarios se ejecutó dos veces. Ambas corridas obtuvieron:

- 500/500 solicitudes exitosas.
- 0 respuestas con una cantidad de actividades diferente de 1.000.

Las métricas detalladas disponibles están almacenadas en:

docs/performance/resultados-carga-500k.csv

## Segunda corrida de 500 usuarios

- Solicitudes exitosas: 500/500.
- Actividades servidas: 500.000.
- Mínimo: 94 ms.
- Promedio: 1.107,86 ms.
- Mediana: 1.066,5 ms.
- P95: 2.263 ms.
- Máximo: 2.662 ms.
- Tiempo total del lote: 23.473 ms.

## Comprobación posterior al estrés

Después de finalizar la carga se realizó una solicitud individual autenticada.

Resultado:

POST-STRESS -> 115 ms | 1000 actividades

El servicio continuó operativo después de la prueba.

## Limitaciones

El generador de carga PowerShell, Spring Boot, Docker y PostgreSQL se ejecutaron en el mismo computador.

Por tanto:

- Los tiempos incluyen competencia por recursos del mismo equipo.
- El tiempo total del lote incluye creación, ejecución y recolección de runspaces de PowerShell.
- Esta prueba no representa una infraestructura distribuida de producción.
- Los resultados constituyen evidencia únicamente para las condiciones probadas.
- El volumen de 500 usuarios almacenados es diferente del concepto de concurrencia; ambos aspectos fueron probados en este experimento.

## Interpretación

Bajo las condiciones del experimento, RachaPro mantuvo la corrección funcional hasta el escenario probado de 500 usuarios concurrentes, recuperando 1.000 actividades por usuario sin errores observados.

Los resultados no implican escalabilidad ilimitada ni permiten generalizar el comportamiento a otras infraestructuras.
