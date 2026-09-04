# GUÍA Y GUION COMPLETO PARA LA DEFENSA DEL PROYECTO FINAL - CI/CD

> **Nota:** Este documento contiene la estructura, explicación teórica, código desglosado por partes y todos los comandos ordenados paso a paso para realizar una presentación impecable.

---

## 📋 Resumen de la Estructura de la Defensa

Tu estrategia de presentación está **excelente**. Sigue este orden para demostrar control total del proyecto:
1. **Introducción:** Saludo y explicación de la arquitectura del proyecto (Controladores Java).
2. **Pruebas Unitarias:** Explicación de JUnit 5 y ejecución con Maven.
3. **CI/CD & Scripts:** Explicación desglosada por bloques de Workflows (`maven.yml`, `deploy.yml`, `release.yml`) y Scripts Bash (`deploy.sh`, `health-check.sh`, `traffic-test.sh`).
4. **Demostración en GitHub:** Evidencias de Actions en verde y Releases/Artefactos.
5. **Live Demo (Demostración en Vivo):** 
   - Intento fallido de push a `main` (regla de protección).
   - Creación de rama `feature/demo-defensa`, push, ejecución de CI, Pull Request, Merge y disparo automático del CD.
   - Verificación de la web actualizada y cambio Blue-Green.
6. **Demostración en Servidor AWS EC2 (SSH):** Verificación de directorio `/opt/spring-boot-app`, archivos `.jar`, ejecución manual de `java -jar` y `./scripts/deploy.sh`.

---

## 🎙️ GUION PASO A PASO CON CÓDIGO Y COMANDOS

---

### PASO 1: Saludo e Introducción de Controladores Java

**Lo que vas a decir:**
> *"Buenas noches. Para este proyecto final reutilizamos la arquitectura base de Spring Boot Web API y la expandimos implementando controladores REST, vistas interactivas en HTML y una estrategia completa de CI/CD sobre AWS EC2."*

**Archivos a mostrar y explicar:**

1. **`WebapiApplication.java`**:
   - **Explicación:** *"Es la clase principal de Spring Boot. Además de iniciar la aplicación, configuramos un endpoint en la raíz `/` que entrega un Dashboard visual dinámico en HTML con soporte para modo oscuro, mostrando el estado del servidor y la instancia activa."*

2. **`InstanceController.java`**:
   - **Explicación:** *"Controlador REST que expone `/api/instance`. Devuelve un objeto JSON con la instancia (`BLUE` o `GREEN`) y el puerto (`8080` u `8081`). Es fundamental para verificar cuál versión está activa en el despliegue Blue-Green."*
   ```java
   @GetMapping("/api/instance")
   public Map<String, String> getInstance() {
       Map<String, String> response = new HashMap<>();
       response.put("instance", instance); // BLUE o GREEN
       response.put("port", port);        // 8080 u 8081
       return response;
   }
   ```

3. **`CalculatorController.java`**:
   - **Explicación:** *"Controlador REST en `/api/calculator` que maneja las operaciones matemáticas: suma, resta, multiplicación, división y factorial en formato JSON."*

4. **`CalculatorUIController.java`**:
   - **Explicación:** *"Controlador que sirve la interfaz gráfica dinámica de la calculadora en `/calculator`."*

---

### PASO 2: Pruebas Unitarias (Tests)

**Lo que vas a decir:**
> *"Para garantizar que la lógica de negocio funcione y evitar desplegar código defectuoso, implementamos 12 pruebas unitarias automatizadas con JUnit 5."*

**Archivo:** `WebapiApplicationTests.java`
- **Explicación del código:**
  - `testCalculatorAdd()`: Valida sumas correctas.
  - `testCalculatorDivideByZero()`: Valida la excepción al dividir entre cero.
  - `testInstanceEndpoint()`: Valida que la API devuelva la estructura JSON esperada.

**Comando a ejecutar en tu terminal local:**
```bash
mvn test
```
*(Muestras que aparece `BUILD SUCCESS` y `Tests run: 12, Failures: 0, Errors: 0`).*

---

### PASO 3: Explicación Desglosada de Workflows y Scripts

#### 1. Workflow CI: `.github/workflows/maven.yml`

* **Limitación de Ramas:**
  ```yaml
  on:
    push:
      branches: [ "main", "feature/*" ]
    pull_request:
      branches: [ "main" ]
  ```
  > **Explicación:** *"Configuramos que la integración continua solo se dispare en la rama principal `main` y en ramas de características que empiecen con `feature/*`."*

* **Descarga de Código y Configuración JDK:**
  ```yaml
  - uses: actions/checkout@v4
  - name: Set up JDK 17
    uses: actions/setup-java@v3
    with:
      java-version: '17'
      distribution: 'temurin'
  ```
  > **Explicación:** *"`actions/checkout` clona nuestro código fuente en el servidor de GitHub Actions, y `setup-java` instala la versión de Java 17 Temurin."*

* **Compilación y Pruebas:**
  ```yaml
  - name: Build with Maven
    run: mvn clean compile

  - name: Run unit tests with Maven
    run: mvn test
  ```
  > **Explicación:** *"`mvn clean compile` asegura que no existan errores de sintaxis en el código. Luego `mvn test` ejecuta las 12 pruebas unitarias. Si una falla, el CI se detiene inmediatamente."*

* **Cobertura de Código con JaCoCo:**
  ```yaml
  - name: Run coverage with JaCoCo
    run: mvn jacoco:report
  ```
  > **Explicación:** *"Genera un reporte detallado del porcentaje de código cubierto por pruebas unitarias."*

* **Almacenamiento de Artefactos:**
  ```yaml
  - name: Upload JAR artifact
    uses: actions/upload-artifact@v4
    with:
      name: app-jar
      path: target/*.jar
  ```
  > **Explicación:** *"Empaqueta y guarda el archivo `.jar` resultante para que pueda ser utilizado por el CD o ser descargado."*

---

#### 2. Workflow CD: `.github/workflows/deploy.yml`

```yaml
on:
  workflow_run:
    workflows: ["Java CI with Maven"]
    types: [completed]
    branches: [main]
```
> **Explicación:** *"Este workflow representa el Despliegue Continuo (CD). Solo se dispara cuando el workflow de CI (`maven.yml`) ha finalizado exitosamente en la rama `main`."*

* **Transferencia SSH y Despliegue en EC2:**
  ```yaml
  - name: Deploy to EC2 via SSH
    uses: appleboy/ssh-action@v1.0.3
    with:
      host: ${{ secrets.EC2_HOST }}
      username: ${{ secrets.EC2_USER }}
      key: ${{ secrets.EC2_SSH_KEY }}
      script: |
        cd /opt/spring-boot-app
        ./scripts/deploy.sh target/app-BLUE.jar
  ```
  > **Explicación:** *"Se conecta de forma segura vía SSH usando secretos de GitHub (`EC2_HOST`, `EC2_SSH_KEY`) y ejecuta nuestro script de despliegue Blue-Green dentro de la instancia EC2."*

---

#### 3. Workflow de Releases: `.github/workflows/release.yml`

```yaml
on:
  push:
    tags:
      - 'v*'
```
> **Explicación:** *"Cuando creamos una etiqueta de versión como `v1.0.0`, este workflow compila el proyecto y crea automáticamente una Release pública en GitHub adjuntando el binario `.jar`."*

---

#### 4. Scripts Bash

* **`scripts/deploy.sh` (Despliegue Blue-Green):**
  > **Explicación:** *"Detecta qué versión está corriendo actualmente (ej. BLUE en puerto 8080). Prepara la versión GREEN en el puerto 8081, ejecuta la verificación de salud (`health-check.sh`), y si pasa con éxito, cambia el proxy inverso Nginx al puerto 8081 sin interrumpir el servicio para el usuario final."*

* **`scripts/health-check.sh`:**
  > **Explicación:** *"Realiza hasta 20 reintentos mediante HTTP GET a `/health`. Si supera los 20 reintentos sin éxito, cancela el despliegue para evitar publicar una versión caída."*

* **`scripts/traffic-test.sh`:**
  > **Explicación:** *"Envía peticiones HTTP continuas para verificar la respuesta del servidor y confirmar que no hay pérdida de paquetes ni errores 500."*

---

### PASO 4: Muestra del Repositorio en GitHub

1. **Pestaña Actions:** Muestras que todos los pipelines tienen el check verde $\checkmark$.
2. **Pestaña Releases:** Muestras la versión `v1.0.0` con su archivo `.jar` adjunto.

---

### PASO 5: Demostración en Vivo (Live Demo)

#### 1. Demostración de Bloqueo en `main` (Push Directo Rechazado)
Ejecuta en tu terminal local:
```bash
git checkout main
echo "# Cambio no autorizado" >> README.md
git add .
git commit -m "fix: inteto de push directo"
git push origin main
```
> **Lo que pasará:** GitHub rechaza el push con un mensaje de error como: `remote: error: GH006: Protected branch update failed for refs/heads/main`.  
> **Lo que vas a decir:** *"Como se observa, las reglas de protección impiden subir cambios directamente a `main` sin revisión previa."*

#### 2. Flujo Correcto con Rama Feature y Despliegue Automático
Ejecuta en tu terminal local:
```bash
# Crear rama limpia desde main
git checkout -b feature/demo-defensa

# Realizar un cambio visible (Ejemplo: actualizar una línea del README)
echo "## Demostración Defensa CI/CD" >> README.md

# Confirmar y subir la rama
git add .
git commit -m "feat: actualización para demostración de defensa"
git push origin feature/demo-defensa
```

#### 3. Flujo en la Interfaz de GitHub:
1. Ve a GitHub y muestra cómo se ejecuta automáticamente el pipeline de **CI** (`maven.yml`).
2. Abre un **Pull Request** de `feature/demo-defensa` hacia `main`.
3. Presiona el botón **Merge Pull Request**.
4. Muestra cómo al hacer el Merge, inmediatamente se activa el pipeline de **CD** (`deploy.yml`).

#### 4. Verificación en el Navegador Web:
Abre en tu navegador:
- `http://3.16.186.179/` (Dashboard Web)
- `http://3.16.186.179/api/instance` (JSON que demuestra la conmutación Blue/Green).

---

### PASO 6: Demostración SSH en la Instancia AWS EC2

Abre tu terminal y ejecuta los comandos ordenados:

#### 1. Conexión SSH al servidor:
```bash
ssh -i /home/andres/Escritorio/clave-ec2-cicd.pem ubuntu@3.16.186.179
```

#### 2. Ubicación de los archivos `.jar` y versiones:
```bash
cd /opt/spring-boot-app
ls -l
```
> **Lo que vas a decir:** *"Aquí podemos observar los binarios `app-BLUE.jar` y `app-GREEN.jar`, además de las carpetas de scripts y logs."*

#### 3. Ejecución directa del `.jar` (para mostrar arranque de Spring Boot):
```bash
java -jar app-BLUE.jar --server.port=8080
```
> **Lo que vas a decir:** *"Ejecutamos el JAR directamente para observar el Banner de Spring Boot y la confirmación 'Started WebapiApplication' en la consola."* *(Presionas `Ctrl + C` para detener).*

#### 4. Ejecución manual del script de despliegue:
```bash
./scripts/deploy.sh app-GREEN.jar
```
> **Lo que vas a decir:** *"Ejecutamos manualmente el script de despliegue Blue-Green. Vemos cómo analiza la instancia activa, valida el puerto, corre el healthcheck y recarga Nginx."*

#### 5. Ejecución de verficación de salud y prueba de tráfico:
```bash
./scripts/health-check.sh 8080
./scripts/traffic-test.sh
```

---

## 🎯 Conclusión para cerrar tu defensa:
> *"Con esto demostramos un pipeline completo de Integración y Despliegue Continuo (CI/CD): desde la protección de ramas en Git, pruebas unitarias automatizadas con Maven, empaquetado de Releases, despliegue Blue-Green sin caídas mediante scripts Bash y Nginx en AWS EC2. Quedo atento a sus preguntas."*
