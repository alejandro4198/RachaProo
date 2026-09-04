# Inventario inicial y registro crítico de riesgos - Semana 2

## 1. Inventario inicial de riesgos
Antes de realizar la clasificación crítica solicitada para Semana 2, se recuperó el inventario de riesgos que ya se encontraba documentado previamente en el proyecto:

| ID | Riesgo documentado | Estado de origen |
|---|---|---|
| R-01 | Caída o cierre inesperado de la aplicación. | ORIGEN PENDIENTE DE DETERMINAR |
| R-02 | Fallos de persistencia o corrupción de datos. | ORIGEN PENDIENTE DE DETERMINAR |
| R-03 | Brechas de seguridad o fallos de autenticación. | ORIGEN PENDIENTE DE DETERMINAR |
| R-04 | Fallos en recordatorios, Pomodoro, progreso, rachas o logros. | ORIGEN PENDIENTE DE DETERMINAR |
| R-05 | Fallos de UI/UX. | ORIGEN PENDIENTE DE DETERMINAR |
| R-06 | Errores introducidos durante modificaciones o actualizaciones. | ORIGEN PENDIENTE DE DETERMINAR |

La existencia previa de estos elementos en la documentación demuestra que formaban parte del inventario inicial del proyecto, pero no permite determinar por sí sola si cada riesgo fue identificado por el equipo, sugerido por una herramienta de IA o derivado de otra fuente.

Por esta razón, su origen no se asigna retrospectivamente sin evidencia.

## 2. Origen de cada riesgo
Se revisó el historial de Git para identificar la primera aparición versionada del inventario de riesgos.

El commit más antiguo encontrado fue:

- Commit: `3b088858061edc47f6cd018a4130a38c2afb0f73`
- Mensaje: `Define linea base y documentacion previa al experimento`

En dicho commit se creó por primera vez el archivo `dossier/01-contexto-y-drivers.md`, incluyendo los seis riesgos iniciales.

| ID | Primera evidencia versionada | Origen histórico | Relación actual |
|---|---|---|---|
| R-01 | Commit `3b08885` | NO DETERMINADO | No se ha identificado todavía un equivalente actual confirmado por el equipo. |
| R-02 | Commit `3b08885` | NO DETERMINADO | Relacionado parcialmente con `EQ-R03`, debido a que ambos consideran posibles inconsistencias o problemas en la información de actividades, aunque no representan exactamente el mismo riesgo. |
| R-03 | Commit `3b08885` | NO DETERMINADO | Relacionado parcialmente con `EQ-R04`, ya que ambos involucran seguridad de la información, aunque `R-03` también incluía fallos de autenticación. |
| R-04 | Commit `3b08885` | NO DETERMINADO | Relacionado parcialmente con `EQ-R02`, específicamente por el posible fallo de Pomodoro; el riesgo histórico también incluía recordatorios, progreso, rachas y logros. |
| R-05 | Commit `3b08885` | NO DETERMINADO | Relacionado actualmente con `EQ-R01`, identificado por el equipo: posible dificultad de comprensión de la UI/UX. |
| R-06 | Commit `3b08885` | NO DETERMINADO | No se ha identificado todavía un equivalente actual confirmado por el equipo. |

El historial de Git permite determinar cuándo fueron incorporados estos riesgos al repositorio, pero no quién los formuló originalmente.

La relación actual mostrada en la tabla no modifica su origen histórico. Algunos riesgos fueron identificados nuevamente de forma explícita por el equipo durante la revisión documental actual, por lo que pueden relacionarse con preocupaciones actuales sin atribuir retrospectivamente su autoría.

## 3. Riesgos sugeridos por IA
Como parte de la revisión documental realizada el 03 de septiembre de 2026 para completar la evidencia correspondiente a Semana 2, se solicitó a ChatGPT proponer riesgos a partir de la arquitectura y configuración actualmente observadas en RachaPro.

Este registro fue producido posteriormente a la Semana 2 original y no se presenta como evidencia histórica de que estas sugerencias hubieran sido generadas durante dicha semana.

Estas propuestas no se consideran automáticamente riesgos válidos del proyecto. Deben ser evaluadas por el equipo en la sección siguiente.

| ID | Riesgo sugerido por IA | Base observada |
|---|---|---|
| IA-R01 | Un cambio de red o dirección IP del equipo que ejecuta el backend podría impedir la comunicación de la aplicación Android con la API. | `BASE_URL` se encuentra actualmente definida directamente en `RetrofitClient.kt`. |
| IA-R02 | Una configuración incorrecta o incompleta de las variables de entorno podría impedir el inicio del backend. | Spring Boot requiere variables de conexión a PostgreSQL y el secreto JWT. |
| IA-R03 | El uso de HTTP sin TLS podría exponer información intercambiada entre la aplicación y el backend si el mismo esquema se utilizara fuera del entorno local controlado. | `android:usesCleartextTraffic="true"` y comunicación HTTP en el entorno actual. |
| IA-R04 | La pérdida o indisponibilidad de PostgreSQL podría impedir operaciones que dependan de la persistencia central del backend. | La implementación actual utiliza PostgreSQL como datasource del backend. |
| IA-R05 | Si Room/SQLite y PostgreSQL mantienen información equivalente sin reglas claras de sincronización, podrían producirse inconsistencias entre los datos locales y remotos. | Room continúa presente en las dependencias Android y PostgreSQL forma parte de la arquitectura posterior; el papel actual de Room todavía requiere verificación formal. |
| IA-R06 | Un cambio en el backend podría introducir incompatibilidades con la aplicación Android si cambia el contrato de la API sin una coordinación correspondiente en el cliente. | Android consume servicios del backend mediante Retrofit. |
| IA-R07 | La concentración del desarrollo, documentación y mantenimiento en un único integrante podría generar dependencia de conocimiento en una sola persona. | El proyecto actual cuenta con un único integrante. |
| IA-R08 | La falta de pruebas sobre condiciones diferentes al entorno local comprobado podría ocultar fallos que aparezcan en otras redes, dispositivos o condiciones de despliegue. | El checkpoint actual fue verificado bajo un entorno local específico. |

**Origen de las propuestas:** IA - ChatGPT, 03 de septiembre de 2026.

Los riesgos anteriores representan únicamente hipótesis propuestas para evaluación crítica. Su inclusión en esta tabla no implica aceptación por parte del equipo.

## 4. Clasificación crítica de riesgos sugeridos por IA
Los riesgos propuestos por IA fueron revisados individualmente por el equipo. La clasificación no implica aceptar automáticamente las sugerencias de la herramienta.

| ID | Clasificación del equipo | Justificación |
|---|---|---|
| IA-R01 | VÁLIDO | La aplicación Android utiliza actualmente una dirección IP fija en `RetrofitClient.kt`. Si cambia la red o la dirección IPv4 del equipo donde se ejecuta el backend, la aplicación podría dejar de comunicarse con la API hasta actualizar `BASE_URL`. |
| IA-R02 | VÁLIDO | El backend depende de variables de entorno para la conexión con PostgreSQL y la configuración JWT. Una configuración incorrecta o incompleta puede impedir su ejecución correcta. |
| IA-R03 | VÁLIDO | La aplicación permite actualmente tráfico HTTP sin cifrado y se comunica con el backend mediante una URL `http://`. Si este esquema se utilizara fuera del entorno local controlado, la información transmitida podría quedar expuesta. |
| IA-R04 | VÁLIDO | El backend utiliza PostgreSQL como datasource. Su indisponibilidad puede afectar las operaciones que dependen de la persistencia central. |
| IA-R05 | GENÉRICO | Plantea un riesgo habitual cuando existen almacenamientos locales y remotos, pero todavía no se ha verificado formalmente si Room mantiene información equivalente a PostgreSQL ni si existe sincronización entre ambos. |
| IA-R06 | VÁLIDO | Android consume servicios del backend mediante Retrofit. Un cambio no coordinado en rutas, estructuras, parámetros o respuestas podría provocar incompatibilidades entre cliente y servidor. |
| IA-R07 | GENÉRICO | La dependencia de conocimiento en una única persona es un riesgo común en proyectos individuales, pero la formulación no identifica actualmente una consecuencia técnica concreta ni evidencia de que haya producido un bloqueo en RachaPro. |
| IA-R08 | VÁLIDO | Las verificaciones realizadas corresponden a condiciones concretas del entorno local y no demuestran el comportamiento del sistema en otras redes, dispositivos o condiciones de despliegue. |

### Resultado de la revisión

- **VÁLIDOS:** IA-R01, IA-R02, IA-R03, IA-R04, IA-R06 e IA-R08.
- **GENÉRICOS:** IA-R05 e IA-R07.
- **IRRELEVANTES:** ninguno.
- **FALSOS:** ninguno.

La clasificación corresponde a la evaluación realizada por el equipo sobre las sugerencias de IA y no a una clasificación automática realizada por la herramienta.

## 5. Riesgos identificados por el equipo
Durante la revisión documental realizada el 03 de septiembre de 2026 para completar la evidencia de Semana 2, el equipo identificó explícitamente los siguientes riesgos a partir de sus preocupaciones sobre el funcionamiento actual de RachaPro.

| ID | Riesgo identificado por el equipo | Posible impacto | Estado |
|---|---|---|---|
| EQ-R01 | Que la interfaz UI/UX resulte difícil de comprender para el usuario. | Podría dificultar el uso de la aplicación o generar errores durante la interacción con sus funcionalidades. | RIESGO IDENTIFICADO - MANIFESTACIÓN NO VERIFICADA |
| EQ-R02 | Que las funcionalidades de Pomodoro o gestión de actividades dejen de estar disponibles o fallen durante su uso. | Podría impedir al usuario utilizar funciones centrales de productividad de RachaPro. | RIESGO IDENTIFICADO - MANIFESTACIÓN NO VERIFICADA |
| EQ-R03 | Que existan inconsistencias entre las actividades creadas y las actividades que posteriormente se muestran o permanecen registradas en el sistema. | Podría generar información incompleta, duplicada, perdida o diferente de la esperada por el usuario. | RIESGO IDENTIFICADO - MANIFESTACIÓN NO VERIFICADA |
| EQ-R04 | Que se produzcan fugas o exposición no autorizada de información. | Podría comprometer la privacidad o seguridad de la información gestionada por RachaPro. | RIESGO IDENTIFICADO - MANIFESTACIÓN NO VERIFICADA |
| EQ-R05 | Que el sistema no permita registrar nuevos usuarios. | Podría impedir el acceso de nuevos usuarios y bloquear el flujo inicial de uso de la aplicación. | RIESGO IDENTIFICADO - MANIFESTACIÓN NO VERIFICADA |

Estos riesgos corresponden a preocupaciones expresadas directamente por el equipo. Su identificación no demuestra que los fallos hayan ocurrido; para determinar su manifestación o probabilidad se requiere evidencia adicional mediante pruebas, revisión de código o registros de ejecución.

## 6. Evidencias y observaciones
La identificación de un riesgo no implica que el problema se haya manifestado. Para mantener la trazabilidad, se distingue entre evidencia comprobada, riesgos planteados y aspectos que todavía requieren verificación.

| Elemento | Evidencia / observación | Estado |
|---|---|---|
| Funcionamiento general del sistema | Durante el checkpoint se verificaron compilación Android, pruebas unitarias, ejecución del backend, PostgreSQL, registro de usuario, creación de una actividad y persistencia en la base de datos. | HECHO VERIFICADO |
| Comunicación Android-backend | La aplicación utiliza actualmente una `BASE_URL` con una dirección IP definida directamente en `RetrofitClient.kt`. | HECHO VERIFICADO |
| Configuración del backend | El backend requiere variables de entorno para PostgreSQL y JWT. | HECHO VERIFICADO |
| Comunicación HTTP | La configuración actual permite tráfico HTTP sin cifrado para el entorno de desarrollo local. | HECHO VERIFICADO |
| Dependencia de PostgreSQL | El backend actual utiliza PostgreSQL como datasource y durante el checkpoint se comprobó su conexión. | HECHO VERIFICADO |
| UI/UX difícil de comprender | Fue identificada como preocupación del equipo, pero no se dispone todavía de una prueba de usabilidad que demuestre el problema. | EVIDENCIA FALTANTE |
| Fallo de Pomodoro | El equipo lo reconoce como riesgo, pero no se ha aportado evidencia de que la funcionalidad se encuentre actualmente caída. | EVIDENCIA FALTANTE |
| Fallo del módulo de actividades | Durante el checkpoint fue posible crear una actividad y comprobar su persistencia. Esto no descarta fallos bajo otras condiciones. | HECHO VERIFICADO bajo las condiciones probadas |
| Inconsistencias entre actividades creadas y mostradas | No se dispone todavía de una prueba específica que compare sistemáticamente creación, recuperación y visualización de actividades. | EVIDENCIA FALTANTE |
| Fuga o exposición de información | Se reconoce como riesgo de seguridad. No existe evidencia actual de que haya ocurrido una fuga de información. | RIESGO / MANIFESTACIÓN NO VERIFICADA |
| Registro de nuevos usuarios | Durante el checkpoint se logró registrar un usuario nuevo. Esto demuestra el funcionamiento del flujo bajo las condiciones probadas, pero no garantiza su funcionamiento en todos los escenarios. | HECHO VERIFICADO bajo las condiciones probadas |
| Papel actual de Room/SQLite | Room continúa presente en el proyecto, pero todavía no se ha documentado formalmente qué responsabilidad conserva frente a PostgreSQL. | EVIDENCIA FALTANTE |
| Funcionamiento en otros dispositivos o redes | Las comprobaciones realizadas corresponden al entorno utilizado durante el checkpoint. | EVIDENCIA FALTANTE para otros entornos |

### Observación metodológica

Los resultados del checkpoint permiten contrastar algunos de los riesgos identificados, pero no eliminarlos automáticamente.

Por ejemplo, haber logrado registrar un usuario o crear una actividad demuestra que esos flujos funcionaron durante la prueba realizada, pero no constituye evidencia de disponibilidad permanente ni de funcionamiento correcto bajo todas las condiciones posibles.

## 7. Información pendiente de verificación
A partir del inventario y de la revisión crítica realizada, permanecen abiertos los siguientes aspectos:

| ID | Información pendiente | Relación |
|---|---|---|
| P-01 | Verificar mediante una prueba de usabilidad si la interfaz de RachaPro presenta dificultades reales de comprensión para los usuarios. | EQ-R01 |
| P-02 | Comprobar de manera específica el comportamiento de Pomodoro ante diferentes condiciones de ejecución y posibles interrupciones. | EQ-R02 |
| P-03 | Verificar sistemáticamente que las actividades creadas, recuperadas y mostradas por la aplicación permanezcan consistentes. | EQ-R03 |
| P-04 | Evaluar los mecanismos actuales de protección de información y determinar si existen condiciones que puedan producir exposición no autorizada de datos. | EQ-R04, IA-R03 |
| P-05 | Ampliar las pruebas del registro de usuarios a condiciones diferentes de las utilizadas durante el checkpoint. | EQ-R05 |
| P-06 | Determinar y documentar formalmente el papel actual de Room/SQLite dentro de la implementación y su relación con PostgreSQL. | IA-R05 |
| P-07 | Evaluar el comportamiento de la aplicación cuando cambia la dirección de red utilizada para acceder al backend. | IA-R01 |
| P-08 | Verificar el comportamiento del sistema ante indisponibilidad de PostgreSQL. | IA-R04 |
| P-09 | Verificar qué ocurre cuando existe una incompatibilidad entre el contrato esperado por Retrofit y las respuestas o endpoints del backend. | IA-R06 |
| P-10 | Ejecutar pruebas en otras redes, dispositivos o condiciones de despliegue antes de generalizar los resultados obtenidos en el entorno local. | IA-R08 |
| P-11 | Determinar, si aparece nueva evidencia histórica, el origen de los riesgos R-01 a R-06 incorporados originalmente en el commit `3b08885`. | R-01 a R-06 |

Estos elementos se mantienen como pendientes y no se consideran problemas demostrados mientras no exista evidencia que permita confirmarlos.
