# Checkpoint de línea base operativa - Semana 2

## Fecha de verificación

03 de septiembre de 2026.

## Estado verificado

Durante el checkpoint se comprobó el funcionamiento del entorno local actual de RachaPro.

| Componente / prueba | Comprobación | Resultado |
|---|---|---|
| PostgreSQL | Contenedor `rachapro-postgres` mediante Docker Compose | `healthy` |
| Backend | `GET /actuator/health` | `UP` |
| Compilación Android | `.\gradlew.bat assembleDebug` | `BUILD SUCCESSFUL` |
| Pruebas unitarias Android | `.\gradlew.bat testDebugUnitTest` | `BUILD SUCCESSFUL` |
| Pruebas backend | `.\gradlew.bat test` desde `backend/` | `BUILD SUCCESSFUL` |
| Ejecución Android | Instalación y apertura en dispositivo de prueba | Verificada |
| Integración Android-backend-BD | Registro de usuario y creación de actividad desde Android | Verificada |
| Persistencia | Consulta directa de la actividad creada en PostgreSQL | Verificada |

## Alcance de la evidencia

Estos resultados corresponden al entorno local y a las condiciones utilizadas durante la verificación. No constituyen evidencia de funcionamiento en todos los dispositivos, redes o condiciones de despliegue.

## Referencias

- `docs/ejecucion-local.md`
- `backend/`
- `infra/postgres/`
- `app/`
