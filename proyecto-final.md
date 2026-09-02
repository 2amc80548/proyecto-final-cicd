# Proyecto Final — Implementación de CI/CD

## 1. Descripción

El proyecto final consiste en diseñar e implementar un proceso completo de **Continuous Integration / Continuous Delivery (CI/CD)** utilizando GitHub como plataforma de gestión del código y **GitHub Actions** como motor de automatización.

El proyecto deberá integrar los principales conceptos y herramientas desarrollados durante el módulo:

* Gestión del código fuente con Git y GitHub.
* Estrategia de branching.
* Estrategia de tagging y versionamiento.
* Continuous Integration mediante GitHub Actions.
* Construcción automatizada de la aplicación.
* Ejecución de pruebas automatizadas.
* Análisis de cobertura de código.
* Generación de artefactos.
* Publicación de versiones mediante GitHub Actions Releases.
* Deployment automatizado mediante scripts.
* Ejecución de la aplicación en infraestructura local.
* Verificación del deployment.
* Implementación de una estrategia de despliegue.
* Documentación y evidencias del proceso.

El resultado esperado es disponer de un flujo reproducible que permita llevar una modificación del código fuente desde el repositorio hasta una aplicación ejecutándose en un ambiente local.

---

## 2. Objetivo general

Diseñar e implementar un proceso de CI/CD que automatice la construcción, validación, publicación y despliegue de una aplicación utilizando GitHub Actions y una infraestructura local.

---

## 3. Objetivos específicos

El proyecto deberá permitir demostrar que el equipo es capaz de:

1. Definir una estrategia de trabajo con ramas.
2. Gestionar versiones mediante tags.
3. Implementar un pipeline de Continuous Integration.
4. Automatizar la ejecución de pruebas.
5. Incorporar Code Coverage al proceso de CI.
6. Generar un artefacto ejecutable.
7. Publicar una versión mediante GitHub Release.
8. Automatizar el deployment mediante scripts.
9. Ejecutar la aplicación en una infraestructura local.
10. Implementar una estrategia de deployment.
11. Verificar que la aplicación desplegada funciona correctamente.
12. Definir un mecanismo de rollback.
13. Documentar y demostrar el proceso completo.

---

## 4. Tecnologías mínimas

El proyecto deberá utilizar como mínimo:

* Git
* GitHub
* GitHub Actions
* Java
* Spring Boot
* Maven
* JUnit
* JaCoCo
* GitHub Releases
* Bash
* Linux
* SSH

Para la infraestructura local se podrá utilizar:

* Ubuntu físico.
* Ubuntu en WSL.
* Máquina Virtual con Ubuntu u otra distribución Linux.
* Otra infraestructura Linux equivalente.

Se podrán incorporar herramientas adicionales cuando aporten valor al proceso de CI/CD.

---

## 5. Aplicación

El proyecto podrá utilizar:

* La aplicación Spring Boot utilizada durante el módulo, o
* Una aplicación propia desarrollada por el equipo.

La aplicación deberá poder ser construida mediante Maven y generar un archivo `.jar` ejecutable.

El proyecto deberá permitir identificar claramente la versión de la aplicación que está siendo construida, publicada y desplegada.

---

# 6. Estrategia de Branching

El equipo deberá definir y documentar una estrategia de branching.

Como mínimo deberán existir:

```text
main
feature/*
```

Se recomienda utilizar un flujo basado en:

```text
main
  │
  ├── feature/feature-1
  ├── feature/feature-2
  └── feature/feature-3
```

Las nuevas funcionalidades deberán desarrollarse en ramas `feature/*`.

La integración hacia `main` deberá realizarse mediante Pull Request.

El equipo deberá documentar:

* Ramas utilizadas.
* Propósito de cada rama.
* Reglas para crear ramas.
* Reglas para realizar merge.
* Reglas de protección de `main`, cuando sea posible.
* Relación entre las ramas y la ejecución del pipeline.

---

# 7. Estrategia de Tagging y Versionamiento

El equipo deberá definir una estrategia para identificar las versiones liberadas.

Se recomienda utilizar **Semantic Versioning**:

```text
MAJOR.MINOR.PATCH
```

Ejemplos:

```text
v1.0.0
v1.1.0
v1.1.1
v2.0.0
```

Los tags deberán representar versiones que puedan ser publicadas y desplegadas.

Ejemplo:

```bash
git tag v1.0.0
git push origin v1.0.0
```

El equipo deberá documentar:

* Formato utilizado.
* Cuándo se crea un tag.
* Qué representa cada versión.
* Relación entre tag, artifact y Release.

---

# 8. Continuous Integration

El proyecto deberá implementar un workflow de GitHub Actions.

El archivo deberá ubicarse en:

```text
.github/workflows/maven.yml
```

El pipeline deberá implementar como mínimo:

```text
Checkout
   ↓
Setup JDK
   ↓
Build
   ↓
Unit Tests
   ↓
Code Coverage
   ↓
Package
```

Los comandos Maven deberán utilizar la estructura:

```yaml
run: mvn -B package --file pom.xml
```

Para los diferentes procesos podrán utilizarse comandos como:

```yaml
run: mvn -B test --file pom.xml
```

o:

```yaml
run: mvn -B clean verify --file pom.xml
```

No se deberá depender de:

```text
./mvnw
```

para la ejecución en GitHub Actions cuando el Maven Wrapper no disponga de los permisos necesarios.

---

# 9. Pruebas unitarias

El pipeline deberá ejecutar automáticamente las pruebas unitarias mediante JUnit.

Ejemplo:

```yaml
- name: Run tests with Maven
  run: mvn -B test --file pom.xml
```

El equipo deberá demostrar:

* Existencia de pruebas unitarias.
* Ejecución automática.
* Resultado exitoso del job.
* Comportamiento del pipeline cuando una prueba falla.

---

# 10. Code Coverage

El proyecto deberá incorporar **JaCoCo** para obtener información sobre la cobertura de código.

El proyecto deberá generar el reporte de cobertura durante el proceso de CI.

El reporte deberá permitir consultar, como mínimo cuando la configuración de JaCoCo lo permita:

* Classes.
* Methods.
* Lines.
* Branches.

El equipo deberá explicar brevemente los resultados obtenidos y su utilidad dentro del proceso de CI.

---

# 11. Generación del Artifact

El proceso de CI deberá generar el archivo `.jar` de la aplicación.

El resultado esperado será similar a:

```text
target/
└── application.jar
```

El artifact deberá ser identificable y reutilizable posteriormente por el proceso de publicación y deployment.

El proyecto deberá evitar reconstruir innecesariamente la aplicación durante el deployment.

La versión generada durante CI deberá ser la misma que posteriormente se publique y despliegue.

---

# 12. Publicación mediante GitHub Releases

El proyecto deberá utilizar **GitHub Actions para publicar una Release**.

La Release deberá estar asociada a un tag de versión.

Ejemplo:

```text
Tag:
v1.0.0

Release:
v1.0.0
```

La Release deberá contener como mínimo el archivo ejecutable `.jar`.

El flujo esperado será:

```text
Código
   ↓
Build
   ↓
Test
   ↓
Coverage
   ↓
Package
   ↓
Tag
   ↓
GitHub Release
   ↓
JAR
```

El equipo deberá demostrar que el artefacto publicado corresponde a la versión identificada mediante el tag.

---

# 13. Deployment

El deployment deberá realizarse sobre infraestructura local.

Se podrán utilizar:

* Ubuntu.
* WSL.
* Máquina Virtual.
* Otra infraestructura Linux equivalente.

No es obligatorio utilizar servicios cloud.

La infraestructura deberá permitir:

1. Ejecutar la aplicación Spring Boot.
2. Ejecutar scripts Bash.
3. Recibir o descargar la versión de la aplicación.
4. Ejecutar verificaciones del servicio.
5. Permitir demostrar la estrategia de deployment seleccionada.

---

# 14. Automatización del Deployment

El deployment deberá ejecutarse mediante scripts.

Se deberá crear como mínimo un script Bash para automatizar el proceso.

Se recomienda una estructura similar a:

```text
scripts/
├── deploy.sh
├── health-check.sh
└── traffic-test.sh
```

El proceso deberá permitir:

1. Identificar la versión a desplegar.
2. Obtener o recibir el artefacto.
3. Preparar el ambiente.
4. Detener la versión anterior cuando corresponda.
5. Instalar la nueva versión.
6. Iniciar la aplicación.
7. Ejecutar un health check.
8. Informar el resultado del deployment.

---

# 15. Estrategia de Deployment

El equipo deberá seleccionar una estrategia de deployment.

Se recomienda utilizar una de las siguientes:

* Blue-Green Deployment.
* Canary Release.
* Rolling Deployment.

La estrategia seleccionada deberá estar justificada técnicamente.

La selección deberá considerar:

* Características de la aplicación.
* Infraestructura disponible.
* Complejidad de implementación.
* Riesgo del deployment.
* Posibilidad de rollback.
* Capacidad de verificación.

---

# 16. Opción recomendada: Blue-Green Deployment

Como estrategia recomendada para el proyecto se propone **Blue-Green Deployment**.

La infraestructura deberá disponer de dos instancias de la aplicación.

Ejemplo:

```text
BLUE
└── :8080

GREEN
└── :8081
```

El tráfico podrá gestionarse mediante Nginx:

```text
                  NGINX
                   :80
                    │
           ┌────────┴────────┐
           ▼                 ▼
       BLUE :8080        GREEN :8081
```

Una instancia podrá permanecer activa mientras la nueva versión se despliega sobre la segunda instancia.

Flujo esperado:

```text
BLUE activo
    ↓
Deploy GREEN
    ↓
Health Check
    ↓
Pruebas de validación
    ↓
Switch Traffic
    ↓
GREEN activo
```

En caso de fallo:

```text
GREEN
  ↓
Health Check / E2E
  ↓
FAIL
  ↓
Rollback
  ↓
BLUE
```

---

# 17. Identificación de las instancias

Para demostrar el funcionamiento de la estrategia de deployment y del balanceador, se recomienda incorporar un endpoint que permita identificar la instancia que procesó una solicitud.

Ejemplo:

```text
GET /api/instance
```

Una instancia podrá responder:

```json
{
  "instance": "BLUE",
  "port": "8080"
}
```

y la otra:

```json
{
  "instance": "GREEN",
  "port": "8081"
}
```

Esto permitirá verificar experimentalmente el tráfico entre las instancias.

---

# 18. Verificación del tráfico

Se deberá implementar un mecanismo para realizar múltiples solicitudes hacia la aplicación.

Se podrá utilizar un script Bash y `curl`.

Ejemplo:

```bash
for i in {1..20}
do
    curl -s http://localhost/api/instance
    echo
done
```

Este mecanismo permitirá observar qué instancia está procesando cada solicitud.

Cuando se utilice Nginx, el equipo podrá experimentar con:

```text
Round Robin
Weighted Round Robin
Least Connections
```

y documentar el comportamiento observado.

---

# 19. Health Check

El deployment deberá incluir una verificación posterior al despliegue.

Podrá utilizarse un endpoint de Spring Boot Actuator u otro endpoint de salud de la aplicación.

Ejemplo:

```bash
curl http://localhost:8080/actuator/health
```

El resultado deberá permitir determinar si la instancia está disponible antes de considerarla apta para recibir tráfico.

---

# 20. Pruebas End-to-End

Cuando corresponda a la estrategia seleccionada, se deberán ejecutar pruebas End-to-End sobre la aplicación desplegada.

El flujo recomendado será:

```text
Deploy
   ↓
Health Check
   ↓
E2E Tests
   ↓
Success / Failure
```

El equipo deberá explicar dónde se ejecutan las pruebas dentro del proceso de deployment y qué condición determina si la nueva versión puede continuar hacia la siguiente etapa.

---

# 21. Rollback

El proyecto deberá definir un procedimiento de rollback.

El mecanismo deberá permitir recuperar una versión funcional cuando la nueva versión presente problemas.

Para Blue-Green, por ejemplo:

```text
BLUE activo
     ↓
Deploy GREEN
     ↓
GREEN FAIL
     ↓
Traffic → BLUE
```

El equipo deberá documentar:

* Cómo se detecta el fallo.
* Cómo se detiene o aísla la versión defectuosa.
* Cómo se recupera la versión anterior.
* Cómo se restablece el tráfico.
* Cómo se verifica el rollback.

---

# 22. Arquitectura esperada

La solución final deberá aproximarse conceptualmente a:

```text
                         DEVELOPER
                             │
                             ▼
                          GitHub
                             │
                       Feature Branch
                             │
                             ▼
                        Pull Request
                             │
                             ▼
                    GitHub Actions - CI
                             │
              ┌──────────────┼──────────────┐
              ▼              ▼              ▼
            Build          Tests          JaCoCo
              │              │              │
              └──────────────┼──────────────┘
                             ▼
                           JAR
                             │
                             ▼
                     GitHub Release
                             │
                             ▼
                         Deployment
                             │
                             ▼
                      Local Linux VM
                             │
                             ▼
                           Nginx
                             │
                    ┌────────┴────────┐
                    ▼                 ▼
                BLUE :8080        GREEN :8081
                    │                 │
                    └────────┬────────┘
                             ▼
                      Health Check
                             │
                             ▼
                         E2E Tests
                             │
                    ┌────────┴────────┐
                    ▼                 ▼
                  PASS              FAIL
                    │                 │
                    ▼                 ▼
              New Version          Rollback
```

---

# 23. Estructura sugerida del repositorio

```text
project/
│
├── .github/
│   └── workflows/
│       └── maven.yml
│
├── scripts/
│   ├── deploy.sh
│   ├── health-check.sh
│   └── traffic-test.sh
│
├── src/
│
├── pom.xml
│
├── README.md
│
└── CHANGELOG.md
```

La estructura podrá modificarse de acuerdo con las necesidades del proyecto, siempre que las responsabilidades de cada componente estén claramente identificadas.

---

# 24. Documentación

El repositorio deberá incluir un `README.md` que permita comprender y reproducir el proyecto.

Como mínimo deberá documentar:

* Descripción del proyecto.
* Arquitectura.
* Tecnologías.
* Estrategia de branching.
* Estrategia de tagging.
* Pipeline CI/CD.
* Generación del artifact.
* GitHub Release.
* Configuración de la infraestructura local.
* Estrategia de deployment.
* Ejecución de los scripts.
* Health checks.
* E2E tests.
* Procedimiento de rollback.

---

# 25. Resultado esperado

El proyecto deberá demostrar el siguiente flujo:

```text
Desarrollo
    ↓
Feature Branch
    ↓
Pull Request
    ↓
Merge
    ↓
CI
    ├── Build
    ├── Unit Tests
    ├── Code Coverage
    └── Package
             ↓
          JAR
             ↓
       GitHub Release
             ↓
         Deployment
             ↓
      Estrategia elegida
             ↓
       Health Check
             ↓
         E2E Tests
             ↓
       ┌─────┴─────┐
       ▼           ▼
     PASS         FAIL
       │           │
       ▼           ▼
   Promoción    Rollback
```

El proyecto deberá demostrar que el proceso de entrega de software puede ejecutarse de forma:

* **Automatizada**
* **Repetible**
* **Verificable**
* **Trazable**
* **Documentada**

---

# 26. Consideraciones

El proyecto no deberá limitarse a demostrar que los comandos funcionan individualmente.

El objetivo es integrar los diferentes elementos en un **proceso coherente de CI/CD**.

Se deberá prestar especial atención a la relación entre:

```text
Branching
    ↓
Versionamiento
    ↓
CI
    ↓
Artifact
    ↓
Release
    ↓
Deployment
    ↓
Verification
    ↓
Rollback
```

Cada etapa deberá tener una responsabilidad clara dentro del proceso.

La infraestructura local deberá ser suficiente para demostrar el funcionamiento de la solución, sin requerir servicios cloud.

La selección de herramientas adicionales será válida siempre que el equipo pueda justificar su utilización y demostrar su integración con el proceso CI/CD.
