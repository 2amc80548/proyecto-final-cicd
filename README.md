# Proyecto Final — Implementación de Pipeline CI/CD y Despliegue Blue-Green en AWS EC2

Este repositorio contiene la implementación completa del proceso de **Continuous Integration / Continuous Delivery (CI/CD)** para una aplicación **Spring Boot 3 (Java 21)**, desplegada de forma automatizada sobre infraestructura en la nube (**AWS EC2 con Nginx**) mediante **GitHub Actions** y **SSH**, usando una estrategia de despliegue **Blue-Green** y mecanismo de **Rollback automático**.

---

## 🏗️ 1. Arquitectura de la Solución

```text
                     DESARROLLADOR (Entorno Local)
                                 │
                        git push / Pull Request
                                 │
                                 ▼
                        GitHub Repositorio
                                 │
          ┌──────────────────────┴──────────────────────┐
          ▼                                             ▼
GitHub Actions - CI (maven.yml)             GitHub Actions - CD (deploy.yml / release.yml)
  ├── Build & Package (JAR)                   ├── Autenticación por SSH (EC2_SSH_KEY)
  ├── Unit Tests (JUnit)                      ├── Transferencia SCP (/opt/spring-boot-app)
  ├── Code Coverage (JaCoCo)                  └── Ejecución de scripts/deploy.sh
  └── Publicación Artefactos                                    │
                                                                ▼
                                                   Servidor AWS EC2 (Ubuntu Linux)
                                                                │
                                                          NGINX (Puerto :80)
                                                        ┌───────┴───────┐
                                                        ▼               ▼
                                                   BLUE (:8080)    GREEN (:8081)
                                                        │               │
                                                        └───────┬───────┘
                                                                ▼
                                                          Health Check
                                                        ┌───────┴───────┐
                                                        ▼               ▼
                                                      PASS            FAIL
                                                        │               │
                                                (Conmutar Nginx)   (Rollback)
```

---

## 🛠️ 2. Tecnologías Utilizadas

* **Lenguaje & Framework:** Java 21, Spring Boot 3.2.5
* **Gestor de Construcción:** Apache Maven (vía `./mvnw` y `mvn`)
* **Pruebas y Cobertura:** JUnit 5, MockMvc, JaCoCo (Cobertura de Código)
* **Control de Versiones:** Git, GitHub
* **Motor de CI/CD:** GitHub Actions (Workflows para CI, CD por SSH y Releases)
* **Infraestructura Cloud:** AWS EC2 (Ubuntu Linux 24.04 LTS)
* **Servidor Web & Proxy Reverso:** Nginx (Puerto 80)
* **Automatización del Servidor:** Scripts Bash (`deploy.sh`, `health-check.sh`, `traffic-test.sh`)

---

## 📌 3. Endpoints REST de la Aplicación

| Método | Endpoint | Descripción | Ejemplo de Respuesta |
| :--- | :--- | :--- | :--- |
| `GET` | `/` | Endpoint raíz de saludo | `"Hello CI/CD World!"` |
| `GET` | `/health` | Endpoint de salud del servidor | `"Server Healthy!"` |
| `GET` | `/date` | Fecha actual del servidor | `"Current Server Date: 2026-09-02"` |
| `GET` | `/api/instance` | Instancia y puerto en ejecución | `{"instance": "BLUE", "port": "8080"}` |
| `GET` | `/api/calculator/add?a=10&b=5` | Suma de dos números | `{"a": 10, "b": 5, "operation": "add", "result": 15}` |
| `GET` | `/api/calculator/divide?a=10&b=2` | División validando divisor cero | `{"a": 10, "b": 2, "operation": "divide", "result": 5.0}` |

---

## 🌿 4. Estrategia de Branching (Ramas)

Se utiliza un flujo basado en ramas de desarrollo e integración protegida hacia producción:

```text
main (Producción)
  │
  ├── feature/fase1-endpoints
  ├── feature/ci-pipeline
  └── feature/cd-aws-deploy
```

* **`main`**: Contiene la versión estable de producción. Cualquier cambio en `main` dispara el despliegue automático a AWS EC2.
* **`feature/*`**: Ramas secundarias para el desarrollo de nuevas características. En estas ramas se ejecutan únicamente la compilación y las pruebas unitarias (CI) para validar el código antes de solicitar un Pull Request.
* **Reglas de Integración:** La integración hacia `main` se realiza obligatoriamente vía **Pull Request** previa validación de tests en verde.

---

## 🏷️ 5. Estrategia de Tagging y Versionamiento (SemVer)

Se aplica **Semantic Versioning (`MAJOR.MINOR.PATCH`)** para identificar las versiones liberadas:

* `v1.0.0`: Versión inicial con endpoints base y despliegue Blue-Green.
* `v1.1.0`: Incorporación de nuevas características o mejoras en la API.

### Publicación Automatizada con GitHub Releases:
Al crear y empujar un tag semántico a GitHub:
```bash
git tag v1.0.0
git push origin v1.0.0
```
El workflow `.github/workflows/release.yml` crea automáticamente una **Release oficial en GitHub** adjuntando el archivo ejecutable `.jar`.

---

## ⚙️ 6. Pipeline de CI/CD (GitHub Actions)

Ubicados en `.github/workflows/`:

1. **`maven.yml` (CI - Integración Continua):**
   * Se ejecuta en `push` a ramas `feature/*` y `main`, y en `pull_request`.
   * Puntos de ejecución: Checkout $\rightarrow$ Setup JDK 21 $\rightarrow$ Build Maven $\rightarrow$ Run Tests $\rightarrow$ JaCoCo Coverage.
   * Genera 3 artefactos: `webapi-artifact`, `test-report-artifact`, `code-coverage-report-artifact`.

2. **`deploy.yml` (CD - Despliegue Continuo a AWS EC2):**
   * Se ejecuta al hacer `push` a `main` o al publicar un Tag `v*`.
   * Se conecta por **SSH** a AWS EC2 usando **GitHub Secrets** (`EC2_HOST`, `EC2_USER`, `EC2_SSH_KEY`).
   * Copia los artefactos a `/opt/spring-boot-app` y ejecuta `scripts/deploy.sh`.

3. **`release.yml` (Releases):**
   * Publica la Release oficial en GitHub al empujar un tag `v*`.

---

## 🚀 7. Estrategia de Despliegue Blue-Green y Scripts

El directorio `scripts/` contiene los componentes de automatización:

### **A. `scripts/health-check.sh`**
Realiza verificaciones iterativas contra el endpoint `/health` de un puerto específico (8080 u 8081) hasta confirmar estado `HTTP 200 OK`.

### **B. `scripts/traffic-test.sh`**
Ejecuta ráfagas de peticiones `curl` contra `/api/instance` para contabilizar y graficar el porcentaje de respuestas entregadas por BLUE y GREEN.

### **C. `scripts/deploy.sh` (Despliegue y Rollback)**
1. **Detección de la instancia activa:** Si BLUE (8080) está activo, prepara GREEN (8081).
2. **Arranque en segundo plano:** Inicia la nueva versión con `nohup java -jar ... --server.port=8081`.
3. **Health Check previo:** Evalúa el estado del nuevo puerto mediante `health-check.sh`.
4. **Promoción:** Si pasa el Health Check, reconfigura Nginx (`upstream.conf`), aplica `nginx -s reload` y apaga la versión antigua.
5. **Mecanismo de Rollback:** Si el Health Check falla, destruye el proceso de la nueva versión, no modifica Nginx y mantiene activa la versión anterior funcional sin interrumpir el servicio.

---

## 📄 8. Instrucciones de Reproducción Local y Pruebas

### **Ejecutar la aplicación localmente:**
```bash
./mvnw spring-boot:run
```

### **Ejecutar pruebas unitarias y cobertura JaCoCo:**
```bash
./mvnw clean verify
```
*(Reporte de cobertura disponible en `target/site/jacoco/index.html..`)*.

### **Ejecutar pruebas unitarias y cobertura JaCoCo:**

---
