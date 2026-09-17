# Ejecución local reproducible de RachaPro

## 1. Propósito
Este documento describe el procedimiento necesario para ejecutar localmente el sistema RachaPro a partir de los archivos versionados en el repositorio.

El procedimiento contempla la preparación de PostgreSQL mediante Docker, la configuración y ejecución del backend Spring Boot y la ejecución de la aplicación Android.

El objetivo es que la puesta en marcha pueda repetirse siguiendo pasos explícitos, sin depender de configuraciones que existan únicamente en el equipo del desarrollador.

## 2. Requisitos previos
Para ejecutar el sistema se requieren los siguientes elementos:

- Git, para obtener el repositorio.
- Docker Desktop con soporte para Docker Compose.
- Java 25 para la ejecución del backend.
- Android Studio con un SDK compatible con `compileSdk 37`.
- Un dispositivo Android físico o emulador con Android API 24 o superior.
- Conectividad de red entre el dispositivo Android y el equipo que ejecuta el backend cuando se utilice un dispositivo físico.

No es necesario instalar Gradle globalmente, ya que tanto el proyecto Android como el backend incluyen Gradle Wrapper.

## 3. Clonar y preparar el proyecto
Desde una terminal de PowerShell, clonar el repositorio:

```powershell
git clone https://github.com/alejandro4198/RachaProo.git RachaPro
```

## 4. Configurar PostgreSQL
La configuración local de PostgreSQL se encuentra en:

`infra/postgres/`

El repositorio incluye el archivo:

`infra/postgres/.env.example`

con las variables necesarias para el entorno local:

```text
POSTGRES_DB=rachapro_db
POSTGRES_USER=rachapro_user
POSTGRES_PASSWORD=123456789
POSTGRES_PORT=5432
```

## 5. Iniciar PostgreSQL con Docker
Desde la raíz del repositorio, iniciar PostgreSQL utilizando Docker Compose:

```powershell
docker compose --env-file .\infra\postgres\.env -f .\infra\postgres\docker-compose.yml up -d
```

## 6. Configurar el backend
El backend requiere las siguientes variables de entorno:

- `RACHAPRO_DB_URL`
- `RACHAPRO_DB_USER`
- `RACHAPRO_DB_PASSWORD`
- `RACHAPRO_JWT_SECRET`

El repositorio incluye `backend/.env.example` como plantilla de configuración.

En una instalación nueva, crear el archivo local:

```powershell
Copy-Item .\backend\.env.example .\backend\.env
```

## 7. Iniciar el backend

El backend debe ejecutarse desde el directorio `backend`.

Desde la raíz del repositorio:

```powershell
cd .\backend
```

### 7.1 Configurar Java 25

En el entorno utilizado para la verificación, Java 25 se encuentra en el runtime de Android Studio.

```powershell
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio1\jbr"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
```

La ruta de `JAVA_HOME` puede variar entre equipos y debe apuntar a una instalación válida de Java 25.

### 7.2 Cargar la configuración de PostgreSQL

La configuración del contenedor PostgreSQL se obtiene del archivo local:

`infra/postgres/.env`

Desde el directorio `backend`:

```powershell
$config=@{}

Get-Content ..\infra\postgres\.env |
    Where-Object { $_ -match '^[^#].*=' } |
    ForEach-Object {
        $key,$value=$_ -split '=',2
        $config[$key.Trim()]=$value.Trim()
    }
```

A partir de esa configuración se preparan las variables requeridas por Spring Boot:

```powershell
$env:RACHAPRO_DB_URL="jdbc:postgresql://127.0.0.1:$($config['POSTGRES_PORT'])/$($config['POSTGRES_DB'])"
$env:RACHAPRO_DB_USER=$config['POSTGRES_USER']
$env:RACHAPRO_DB_PASSWORD=$config['POSTGRES_PASSWORD']
```

### 7.3 Cargar el secreto JWT

El secreto JWT se obtiene del archivo local `backend/.env`.

```powershell
$jwtLine =
    Get-Content .\.env |
    Where-Object {
        $_ -match '^RACHAPRO_JWT_SECRET='
    }

$env:RACHAPRO_JWT_SECRET =
    ($jwtLine -split '=',2)[1].Trim()
```

El valor real de `RACHAPRO_JWT_SECRET` no debe imprimirse, documentarse ni versionarse.

### 7.4 Iniciar Spring Boot

Con las variables cargadas en la misma terminal:

```powershell
.\gradlew.bat bootRun
```

Durante la verificación del 17/09/2026 este procedimiento inició correctamente el backend con Java 25.0.2 y Spring Boot 4.1.1, estableció conexión con PostgreSQL 17.11 y dejó Tomcat disponible mediante HTTP en el puerto 8080.

La terminal donde se ejecuta `bootRun` debe permanecer abierta mientras el backend esté en uso.

No debe iniciarse una segunda instancia simultánea en el mismo puerto. Si el puerto 8080 ya está ocupado por otra instancia de RachaPro, un segundo `bootRun` finalizará indicando que el puerto se encuentra en uso.

## 8. Verificar el backend
Con el backend en ejecución, verificar su estado desde una segunda terminal de PowerShell:

```powershell
Invoke-RestMethod http://localhost:8080/actuator/health
```

## 9. Configurar la aplicación Android
La aplicación Android consume el backend mediante Retrofit.

La dirección del backend se encuentra actualmente definida en:

`app/src/main/java/com/example/rachapro/network/RetrofitClient.kt`

mediante:

```kotlin
private const val BASE_URL = "http://192.168.1.20:8080/"
```

## 10. Ejecutar la aplicación Android
Antes de ejecutar la aplicación, verificar que:

- PostgreSQL se encuentre activo.
- El backend responda correctamente.
- `BASE_URL` en `RetrofitClient.kt` corresponda a la dirección del equipo donde se ejecuta el backend.

Desde la raíz del repositorio puede comprobarse que la aplicación compila mediante:

```powershell
.\gradlew.bat assembleDebug
```

## 11. Verificación final
La ejecución local se considera verificada cuando los componentes principales del sistema funcionan de manera integrada.

Durante la comprobación realizada se verificó lo siguiente:

- PostgreSQL se encontraba activo mediante Docker y el contenedor `rachapro-postgres` reportaba estado `healthy`.
- El backend respondió correctamente mediante `http://localhost:8080/actuator/health` con estado `UP`.
- La aplicación Android compiló correctamente mediante `.\gradlew.bat assembleDebug`.
- Android Studio instaló y ejecutó correctamente la aplicación en el dispositivo de prueba.
- Se registró un nuevo usuario desde la aplicación.
- El usuario pudo iniciar sesión y acceder al módulo de actividades.
- Se creó una actividad desde la aplicación Android.
- La actividad fue recuperada directamente desde PostgreSQL mediante una consulta SQL.

La consulta de verificación utilizada fue:

```sql
SELECT u.email, a.title
FROM users u
JOIN activities a ON a.user_id = u.id
WHERE u.email = 'prueba.rachapro@test.com'
  AND a.title = 'Prueba reproducibilidad';
```

## 12. Detención del entorno
Para detener el backend Spring Boot, utilizar `Ctrl + C` en la terminal donde se ejecutó:

```powershell
.\gradlew.bat bootRun
```

## 13. Consideraciones y limitaciones
La ejecución local documentada presenta actualmente las siguientes consideraciones y limitaciones:

- El backend requiere Java 25. Durante la verificación se identificó una configuración local de `JAVA_HOME` que apuntaba a una ruta inexistente, por lo que cada entorno debe comprobar que `JAVA_HOME` corresponda a una instalación válida de Java 25.
- Spring Boot requiere las variables `RACHAPRO_DB_URL`, `RACHAPRO_DB_USER`, `RACHAPRO_DB_PASSWORD` y `RACHAPRO_JWT_SECRET`. El archivo `backend/.env` no se carga automáticamente y sus variables deben importarse en la terminal antes de ejecutar el backend.
- Los archivos `.env` utilizados localmente están excluidos del control de versiones. Solo se versionan archivos `.env.example`.
- El secreto JWT debe generarse localmente y no debe almacenarse como secreto real dentro del repositorio.
- La dirección del backend se encuentra actualmente definida directamente en `RetrofitClient.kt`. Al cambiar de equipo o red, `BASE_URL` debe ajustarse.
- Para la prueba realizada con un dispositivo físico fue necesaria conectividad de red entre el dispositivo Android y el equipo donde se ejecutaba Spring Boot.
- La aplicación permite actualmente tráfico HTTP mediante `android:usesCleartextTraffic="true"`. Esta configuración corresponde al entorno local de desarrollo y no constituye una configuración de despliegue productivo.
- La ejecución verificada utilizó PostgreSQL 17 mediante un contenedor previamente existente. Se comprobó el arranque y estado saludable del servicio, pero en esta verificación no se eliminó el volumen existente para comprobar una inicialización completamente nueva desde cero.
- Los valores incluidos en los archivos `.env.example` corresponden únicamente a un entorno local de desarrollo y no deben interpretarse como credenciales apropiadas para producción.