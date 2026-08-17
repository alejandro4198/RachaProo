# Condiciones de ejecución - EXP-001

## 1. Identificación

| Elemento | Valor |
|---|---|
| Experimento | EXP-001 |
| Sistema | RachaPro |
| Commit de línea base | `3b088858061edc47f6cd018a4130a38c2afb0f73` |
| Rama | `master` |
| Fecha de preparación | 17/08/2026 |

## 2. Equipo de desarrollo

| Elemento | Valor |
|---|---|
| Sistema operativo | PENDIENTE DE REGISTRAR |
| Procesador | PENDIENTE DE REGISTRAR |
| Memoria RAM | PENDIENTE DE REGISTRAR |
| Versión de Android Studio | PENDIENTE DE REGISTRAR |

## 3. Dispositivo de ejecución

| Elemento | Valor |
|---|---|
| Tipo | Dispositivo físico |
| Fabricante / modelo | vivo V2205 |
| Versión de Android | Android 14 |
| Nivel de API | 34 |

## 4. Condiciones de la aplicación

| Elemento | Valor |
|---|---|
| Cantidad de actividades | 100 |
| Usuario | Un mismo usuario de prueba |
| Estado de las actividades | Pendiente |
| Prioridad | Media |
| Categoría | Estudio |
| Fecha límite | 31/12/2030 |
| Hora | Ninguna |
| Instrumento | `SystemClock.elapsedRealtime()` |
| Registro | Logcat |
| Número de corridas | 4 |
| Corrida descartada | Corrida 1 |
| Corridas válidas | 2, 3 y 4 |
| Resultado a reportar | Mediana de las corridas válidas |

## 5. Condiciones que deben mantenerse entre corridas

Las corridas comparables deberán ejecutarse utilizando la misma versión de RachaPro, el mismo dispositivo de ejecución, la misma semilla de 100 actividades y sin realizar cambios en el código entre las mediciones.

Las condiciones pendientes deberán registrarse antes de ejecutar las corridas del experimento.
