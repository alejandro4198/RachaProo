# Prerregistro — Análisis de Conector HTTP y Cola Tomcat (Semana 11)

## 1. Identificación y Pregunta Experimental

- **Proyecto**: RachaPro
- **Módulo**: Semana 11 — Diagnóstico de Rendimiento
- **Operación Evaluada**: `POST /api/activities` (250 VUs, Pool Hikari 30)
- **Pregunta Experimental**:
  > ¿El aumento de `http_req_waiting` observado en las corridas lentas a 250 VUs se origina en el encolamiento de peticiones HTTP en el conector del servidor (Tomcat / `http.server.requests.active`) o en la ejecución interna de la aplicación, dado que se ha descartado la saturación del pool Hikari, CPU backend y PostgreSQL?

---

## 2. Hipótesis

- **$H_0$ (Nulo)**: Las métricas de solicitudes HTTP activas (`http.server.requests.active`) y hilos vivos (`jvm.threads.live`) no muestran saturación diferencial entre perfiles rápido y lento.
- **$H_1$ (Alternativa)**: El perfil lento exhibe un incremento observable en `http.server.requests.active` y concurrencia HTTP coincidente temporalmente con el pico de latencia `http_req_waiting`.

---

## 3. Variables y Métricas

- **Variables Medidas**: `http.server.requests.active`, `jvm.threads.live`, `http_req_waiting`, `p95_ms`, `throughput`.
- **Criterio de Evaluación**: Si `http.server.requests.active` escala proporcionalmente a la concurrencia y coincide con los saltos en `http_req_waiting`, H1 será clasificado como **Indicio Fuerte**.

---

## 4. Estado del Prerregistro

`PREREGISTRO CREADO Y VERSIONADO ANTES DE ANÁLISIS`
