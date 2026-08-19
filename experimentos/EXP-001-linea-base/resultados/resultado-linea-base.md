# Resultado de línea base - EXP-001

## Corridas

| Corrida | Tiempo | Estado |
|---|---:|---|
| 1 | 1310 ms | Calentamiento / descartada |
| 2 | 1305 ms | Válida |
| 3 | 1317 ms | Válida |
| 4 | 1308 ms | Válida |

## Resultado reportado

Corridas utilizadas para el cálculo:

`1305 ms`, `1317 ms`, `1308 ms`

Mediana:

`1308 ms = 1.308 s`

Umbral histórico de referencia:

`≤ 3 s`

La mediana obtenida se encuentra por debajo del umbral histórico de referencia.

## Alcance del resultado

La medición realizada comprende el tiempo transcurrido desde el inicio de `loadData()` en `ActivitiesViewModel` hasta la obtención de `ActivitiesUiState.Success` con 100 actividades.

El punto final de esta medición no corresponde al instante exacto en que el listado termina de renderizarse visualmente en pantalla. Por esta razón, el resultado se interpreta como una aproximación al antecedente histórico RNF02.

Bajo las condiciones medidas, el resultado obtenido no respalda la hipótesis preliminar de que la carga con 100 actividades superaría los 3 segundos.

## Versión medida

Commit de línea base funcional:

`3b088858061edc47f6cd018a4130a38c2afb0f73`

Commit instrumentado:

`59c182b12c7a2678f6ed09d1b69b399327d097d4`


## Ampliación estadística para P95

Después de completar el procedimiento originalmente planificado, se realizó una ampliación de la medición con el propósito de obtener una estimación del percentil 95.

Se utilizaron 25 corridas válidas, correspondientes a las corridas 2 a 26. La corrida 1 se mantuvo como calentamiento y no fue incluida en los cálculos.

### Resultados de la ampliación

| Métrica | Resultado |
|---|---:|
| Número de corridas válidas | 25 |
| Mediana | 1325 ms (1.325 s) |
| P95 | 1621 ms (1.621 s) |
| Mínimo | 1305 ms (1.305 s) |
| Máximo | 1666 ms (1.666 s) |
| Umbral histórico de referencia | 3000 ms (3 s) |

### Método de cálculo del P95

El percentil 95 se calculó mediante el método Nearest Rank.

Para 25 observaciones:

`ceil(0.95 × 25) = 24`

Por lo tanto, el P95 corresponde al valor ubicado en la posición 24 de las mediciones ordenadas:

`P95 = 1621 ms`

El cálculo puede reproducirse mediante:

`scripts/calcular-resultados.ps1`

La salida generada se encuentra en:

`resultados/calculo-estadistico.txt`

### Interpretación de la ampliación

La mediana de las 25 mediciones fue de 1.325 s y el P95 fue de 1.621 s.

Bajo las condiciones del experimento, tanto la mediana como el P95 y el máximo observado se encuentran por debajo del umbral histórico de referencia de 3 segundos.

Esta ampliación no modifica retroactivamente el procedimiento inicial de EXP-001. El resultado originalmente planificado continúa siendo la mediana de las corridas 2, 3 y 4, mientras que las 25 corridas corresponden a una ampliación estadística posterior.

La medición continúa teniendo como punto final la obtención de `ActivitiesUiState.Success` con 100 actividades y no el renderizado visual completo del listado. Por esta razón, la comparación con el RNF02 histórico debe interpretarse como una aproximación.
