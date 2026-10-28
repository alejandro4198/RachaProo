# Prerregistro — Spike de Circuit Breaker en Cliente Android RachaPro

## 1. Identificación y Pregunta Experimental

- **Proyecto**: RachaPro
- **Módulo**: Resiliencia en Cliente Android
- **Componente Evaluado**: Interceptor de Circuit Breaker en OkHttp / Retrofit
- **Pregunta Experimental**:
  > ¿Cómo se comporta un Circuit Breaker conceptual en el cliente Android ante la caída del backend (escenario F-01), evaluando las transiciones de estado `CLOSED` $\rightarrow$ `OPEN` $\rightarrow$ `HALF_OPEN` $\rightarrow$ `CLOSED`, el número de peticiones inútiles evitadas y la latencia de respuesta en estado `OPEN`?

---

## 2. Parámetros Prerregistrados del Circuit Breaker

- **Umbral de Fallos Consecutivos ($N_{\text{fail}}$)**: 5 fallos consecutivos de conexión HTTP/Socket.
- **Tiempo de Enfriamiento en Estado `OPEN` ($T_{\text{cooldown}}$)**: 10 segundos antes de pasar a `HALF_OPEN`.
- **Peticiones de Prueba en `HALF_OPEN` ($N_{\text{trial}}$)**: 1 petición de prueba exitosa para retornar a `CLOSED`.

---

## 3. Hipótesis Evaluadas

1. **$H_1$ (Ahorro de Latencia)**: En estado `OPEN`, el cliente responde de forma inmediata ($< 1\text{ ms}$) lanzando una excepción local, evitando agotar los $3\text{ segundos}$ de `connectTimeout` de Retrofit.
2. **$H_2$ (Protección de Red)**: Se evita el $100\%$ del tráfico saliente infructuoso durante la indisponibilidad del servidor en la ventana de enfriamiento.

---

## 4. Estado del Prerregistro

`PREREGISTRO REVISADO Y VERIFICADO`
