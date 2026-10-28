# Spike — Evaluación Conceptual de Despliegue en Google Cloud Platform (GCP)

## 1. Objetivo del Spike

Evaluar conceptualmente la viabilidad, componentes, latencias esperadas y estructura de costos para el despliegue del Monolito Modular de RachaPro sobre servicios gestionados de Google Cloud Platform (GCP).

---

## 2. Arquitectura de Despliegue GCP Mínima

```text
[ Cliente Android ] ──(HTTPS/Internet)──► [ Cloud Load Balancing ]
                                                │
                                                ▼
                                    [ Cloud Run Service ]
                                    (Contenedor Spring Boot Java 25)
                                                │
                                                ▼ (VPC Connector)
                                    [ Cloud SQL PostgreSQL ]
                                    (PostgreSQL 16 Instance)
```

---

## 3. Comparativa: Entorno Local vs. GCP

| Dimensión | Entorno Local (Docker Desktop) | GCP Cloud Run + Cloud SQL (Propuesto) |
| :--- | :--- | :--- |
| **Latencia de Red** | Sub-milisegundo ($< 1\text{ ms}$) en `localhost` / Wi-Fi | $30 - 80\text{ ms}$ por latencia RTT de red celular/móvil |
| **Escalabilidad Cómputo**| Limitada a recursos del host local | Escalado automático de instancias Cloud Run (0 a $N$) |
| **Persistencia** | Volumen local Docker | Cloud SQL con backups automáticos y réplica de lectura |
| **Observabilidad** | Logs locales en consola | Google Cloud Logging + Cloud Monitoring integrado |
| **Costo Monetario** | $0 USD directo | `NO ESTIMABLE CON LA EVIDENCIA ACTUAL` (pago por uso) |

---

## 4. Evaluación de Rendimiento Estimado

- **Aclaración Metodológica**: No se afirma que el despliegue en GCP sea "más rápido" que el entorno local. Si bien Cloud Run permite escalar instancias para procesar mayor volumen concurrente de peticiones, la latencia de red de extremo a extremo ($RTT$) desde dispositivos móviles sobre redes $4G/5G$ agregará entre $30\text{ ms}$ y $100\text{ ms}$ adicionales a cada solicitud individual comparado con una red local de pruebas.

---

## 5. Conclusión del Spike

El despliegue en GCP (Cloud Run + Cloud SQL) se consolida como la arquitectura conceptual recomendada para producción por su capacidad de escalado automático y resiliencia, condicionada a la medición oficial de latencias RTT en pruebas de campo.
