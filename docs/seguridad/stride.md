# Modelo de Amenazas STRIDE — RachaPro

## 1. Contexto y Relación con M7 (`10-seguridad.md`)

Este documento complementa el análisis OWASP Top 10 de Semana 13 (`docs/semana13/10-seguridad.md`) aplicando la metodología **STRIDE** de Microsoft a las 5 fronteras de confianza del sistema RachaPro:
1. `Usuario → Android`
2. `Android → Red`
3. `Red → Backend`
4. `Backend → PostgreSQL`
5. `Pipeline CI/CD`

---

## 2. Matriz de Amenazas STRIDE

| Categoría STRIDE | Componente / Frontera | Evidencia en Código | Threat / Amenaza | Control Existente | Control Faltante / Propuesto | Prioridad |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **Spoofing** (Suplantación) | `Android → Red → Backend` | `AuthController.kt` / `JwtService.kt` | Suplantación de identidad mediante forja o robo de JWT | Validación de firma HS256 y expiración a 1 hora | Rotación de claves y revocación inmediata de tokens | **MEDIA** |
| **Tampering** (Manipulación) | `Red → Backend` | `RetrofitClient.kt` (`usesCleartextTraffic="true"`) | Modificación de payloads de actividades en tránsito por Wi-Fi abierto | Ninguno en tránsito plano | Encriptación TLS/HTTPS y validación de firma en cliente | **ALTA** |
| **Repudiation** (Repudio) | `Backend Spring Boot` | `AuthService.kt` | Un usuario niega haber creado o eliminado una actividad | Timestamping y `userId` asociado en DB (`ActivityEntity`) | Registros de auditoría estructurados inmutables | **BAJA** |
| **Information Disclosure** (Exposición) | `Android → Red` | `application.properties` / Logcat | Fuga de tokens o datos personales por HTTP plano o logs de Android | Logcat en nivel `BASIC` sanitizado | Implementación de HTTPS y cifrado de DB local Room | **ALTA** |
| **Denial of Service** (Denegación de Servicio) | `Backend → PostgreSQL` | `A1-resultados-crecimiento.md` | Saturación del backend y pool HikariCP bajo carga masiva ($> 500\text{ VUs}$) | Timeouts en cliente (`connectTimeout = 3s`) | Rate-limiting en login y autoscaling de instancias | **MEDIA** |
| **Elevation of Privilege** (Elevación de Privilegios) | `Backend Controllers` | `ActivityController.kt` | Acceso a actividades de otros usuarios modificando el ID de recurso (BOLA/IDOR) | Filtrado por `userId` extraído del claim JWT | Pruebas de penetración y verificación de pertenencia en repositorios | **ALTA** |

---

## 3. Conclusión de Integración

La evaluación STRIDE confirma que las mayores vulnerabilidades del sistema se concentran en el **tráfico en texto plano (Tampering / Information Disclosure)** y en la **posibilidad de elevación de privilegios tipo IDOR (Elevation of Privilege)**, alineándose de manera consistente con los hallazgos del análisis OWASP Top 10 en `10-seguridad.md`.
