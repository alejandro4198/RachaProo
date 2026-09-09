> [!IMPORTANT]
> **ESTADO DOCUMENTAL: REGISTRO HISTÓRICO DE PENDIENTES**
>
> Los elementos descritos a continuación corresponden a los pendientes identificados durante Semana 4.
> No todos continúan abiertos actualmente.
>
> **ESTADO POSTERIOR DE LOS PENDIENTES**
>
> | Área | Estado posterior |
> |---|---|
> | Arquitectura AS-IS | **Resuelta posteriormente** mediante C4 Nivel 1, 2 y 3. |
> | Comparación arquitectónica | **Realizada en un corte histórico y complementada posteriormente** por los C4 vigentes. |
> | Estado exacto de todas las pantallas históricas | **No se declara cerrado aquí sin una verificación específica.** |
> | Usabilidad | **Pendiente de prueba ejecutable completa.** |
> | Rendimiento | **Existe evidencia experimental posterior** mediante EXP-001, EXP-002 y EXP-003, cada uno dentro de su alcance. |
> | Seguridad | **Parcialmente resuelta:** existen mecanismos y evidencia arquitectónica de autenticación/JWT; permanecen verificaciones específicas no demostradas. |
> | Compilación reproducible | **Resuelta posteriormente** bajo el entorno documentado. |
> | Ejecución Android | **Resuelta posteriormente** en el entorno y dispositivo comprobados. |
> | Historial y línea base Git | **Resuelto.** |
> | Fuente oficial de la modalidad académica, si se exige | **Permanece pendiente mientras no exista una fuente oficial incorporada al repositorio.** |
>
> El contenido original se conserva debajo sin modificación para mantener la cronología de la auditoría.

# Evidencia pendiente - Semana 4

Este archivo registra información del proyecto RachaPro que todavía necesita ser comprobada, documentada o relacionada con una evidencia reproducible.

## 1. Arquitectura actual

**Estado:** EVIDENCIA FALTANTE

Todavía debe documentarse la arquitectura que realmente está implementada en el código actual de RachaPro.

Se debe revisar el repositorio para identificar, sin asumir:

- Organización de la interfaz de usuario.
- ViewModels existentes.
- Repositories existentes.
- Mecanismos de persistencia.
- Gestión de autenticación y sesión.
- Gestión de actividades.
- Funcionamiento del módulo Pomodoro.
- Gestión de progreso.
- Gestión de recordatorios y notificaciones.
- Dependencias entre los principales componentes.

## 2. Relación entre arquitectura previa y arquitectura actual

**Estado:** EVIDENCIA FALTANTE

El documento elaborado previamente en Ingeniería de Software contiene una arquitectura propuesta para RachaPro. Sin embargo, todavía no se ha documentado formalmente qué elementos de ese diseño permanecen, cuáles cambiaron y cuáles no fueron implementados.

La comparación debe realizarse utilizando como fuentes:

- Arquitectura propuesta en el documento histórico.
- Código fuente actual del proyecto.
- Evidencias ejecutables del MVP.

No se deben asumir las causas de las diferencias si no existe evidencia que las respalde.

## 3. Estado de implementación de las pantallas

**Estado:** EVIDENCIA FALTANTE

El prototipo previo contempla 15 pantallas.

Actualmente se reporta que la mayoría de estas pantallas se encuentran implementadas y que aproximadamente una o dos todavía están pendientes.

Se debe crear una verificación pantalla por pantalla para establecer exactamente:

- Implementada.
- Parcialmente implementada.
- No implementada.

## 4. RNF01 - Usabilidad

**Estado:** EVIDENCIA FALTANTE

Todavía deben ejecutarse y documentarse las pruebas necesarias para comparar los resultados reales del módulo de actividades con las métricas definidas previamente para RNF01.

Se debe conservar:

- Protocolo de prueba.
- Participantes evaluados.
- Resultados originales.
- Tiempo empleado.
- Nivel de éxito sin ayuda.
- Resultado de satisfacción.
- Condiciones de ejecución.
- Versión o commit evaluado.

## 5. RNF02 - Rendimiento

**Estado:** EVIDENCIA FALTANTE

Todavía deben ejecutarse mediciones de rendimiento del módulo de actividades.

Se debe conservar:

- Protocolo de prueba.
- Cantidad de actividades utilizadas.
- Tiempos obtenidos en cada ejecución.
- Condiciones del dispositivo.
- Versión de Android.
- Versión o commit evaluado.
- Resultados originales antes de cualquier cálculo o interpretación.

## 6. RNF03 - Seguridad

**Estado:** EVIDENCIA FALTANTE

Todavía debe verificarse mediante código y pruebas cómo se implementan actualmente:

- Autenticación.
- Validación de credenciales.
- Persistencia de credenciales.
- Protección de contraseñas.
- Control de sesión.
- Restricción de acceso sin autenticación.
- Separación de información entre usuarios.

No se debe afirmar cumplimiento hasta revisar el código y ejecutar las pruebas correspondientes.

## 7. Evidencia de compilación

**Estado:** PENDIENTE DE REGISTRAR

Se reporta que el proyecto compila correctamente, pero todavía debe conservarse evidencia reproducible asociada a una versión específica del código.

Se debe registrar:

- Commit.
- Sistema operativo.
- Versión de Java/JDK.
- Comando utilizado.
- Resultado de compilación.
- Fecha de ejecución.

## 8. Evidencia de ejecución en Android

**Estado:** PENDIENTE DE REGISTRAR

Se reporta que el MVP funciona correctamente en dispositivos Android evaluados.

Todavía se debe documentar:

- Dispositivo evaluado.
- Versión de Android.
- Commit instalado.
- Resultado de instalación.
- Resultado de inicio.
- Flujo funcional probado.

No se afirmará compatibilidad con todos los dispositivos móviles sin evidencia suficiente.

## 9. Historial del repositorio

**Estado:** PENDIENTE DE REGISTRAR

Se reporta que el repositorio posee actualmente dos commits.

Debe conservarse evidencia mediante el historial de Git y registrar el commit utilizado como línea base de la Semana 4.

## 10. Definición de Opción A

**Estado:** EVIDENCIA FALTANTE

Se ha indicado que Opción A corresponde a la adopción de un proyecto desarrollado previamente en la asignatura Ingeniería de Software.

Todavía falta incorporar, si existe, la fuente oficial de la asignatura Arquitectura de Software donde se define esta modalidad.