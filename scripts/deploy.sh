#!/usr/bin/env bash
set -euo pipefail

APP_DIR="/opt/spring-boot-app"
cd "$APP_DIR"
mkdir -p "${APP_DIR}/logs" "${APP_DIR}/versions"

NEW_JAR="${1:-}"

if [[ -z "$NEW_JAR" ]]; then
  NEW_JAR=$(find . -name "*.jar" ! -name "*original*" ! -path "*/versions/*" 2>/dev/null | head -n 1 || true)
fi

if [[ -z "$NEW_JAR" || ! -f "$NEW_JAR" ]]; then
  echo "❌ Error: No se encontró el archivo JAR a desplegar en ${APP_DIR}."
  exit 1
fi

echo "🚀 Iniciando Despliegue Blue-Green"
echo "📦 Artefacto seleccionado: ${NEW_JAR}"

# Detectar puerto activo (si puerto 8080 responde 200, BLUE está activo y desplegamos a GREEN 8081)
ACTIVE_PORT=8080
HTTP_STATUS=$(curl -s -o /dev/null -w "%{http_code}" "http://localhost:8080/health" || true)

if [[ "$HTTP_STATUS" == "200" ]]; then
  NEW_PORT=8081
  NEW_INSTANCE="GREEN"
  OLD_PORT=8080
  OLD_INSTANCE="BLUE"
else
  NEW_PORT=8080
  NEW_INSTANCE="BLUE"
  OLD_PORT=8081
  OLD_INSTANCE="GREEN"
fi

echo "ℹ️ Instancia Activa Actual: ${OLD_INSTANCE} (Puerto ${OLD_PORT})"
echo "➡️ Desplegando Nueva Versión en: ${NEW_INSTANCE} (Puerto ${NEW_PORT})"

# Copiar JAR a la ruta de producción
NEW_JAR_PATH="${APP_DIR}/app-${NEW_INSTANCE}.jar"
cp "$NEW_JAR" "$NEW_JAR_PATH"
chmod 755 "$NEW_JAR_PATH"

# Guardar backup con marca de tiempo
TIMESTAMP=$(date +%Y%m%d%H%M%S)
cp "$NEW_JAR" "${APP_DIR}/versions/app-${NEW_INSTANCE}-${TIMESTAMP}.jar"

# Detener proceso previo en NEW_PORT si existiera alguno
PID_OLD_NEW=$(lsof -ti:${NEW_PORT} 2>/dev/null || true)
if [[ -n "$PID_OLD_NEW" ]]; then
  echo "🛑 Deteniendo proceso previo en puerto ${NEW_PORT} (PID: ${PID_OLD_NEW})"
  kill -9 "$PID_OLD_NEW" 2>/dev/null || true
  sleep 2
fi

# Iniciar la nueva versión en segundo plano
echo "▶️ Levantando instancia ${NEW_INSTANCE} en puerto ${NEW_PORT}..."
nohup java -Xms256m -Xmx512m \
  -jar "$NEW_JAR_PATH" \
  --server.port="$NEW_PORT" \
  --app.instance="$NEW_INSTANCE" \
  > "${APP_DIR}/logs/app-${NEW_INSTANCE}.log" 2>&1 &

# Ejecutar Health Check sobre la nueva instancia recién levantada
echo "🔍 Ejecutando Health Check en puerto ${NEW_PORT}..."
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

if bash "${SCRIPT_DIR}/health-check.sh" "$NEW_PORT"; then
  echo "✅ Instancia ${NEW_INSTANCE} verificada exitosamente."
  
  # Si Nginx está instalado, actualizar el backend upstream conservando el bloque server en puerto 80
  NGINX_CONF="/etc/nginx/conf.d/upstream.conf"
  if [[ -d "/etc/nginx/conf.d" ]]; then
    cat << EOF | sudo tee "$NGINX_CONF" > /dev/null
upstream backend {
    server 127.0.0.1:${NEW_PORT};
}

server {
    listen 80;
    server_name _;

    location / {
        proxy_pass http://backend;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
    }
}
EOF
    sudo nginx -t 2>/dev/null && sudo systemctl reload nginx 2>/dev/null || sudo systemctl restart nginx 2>/dev/null || true
    echo "🔄 Nginx actualizado apuntando a ${NEW_INSTANCE} (: ${NEW_PORT})."
  fi

  # Detener la instancia anterior si estaba corriendo
  PID_OLD=$(lsof -ti:${OLD_PORT} 2>/dev/null || true)
  if [[ -n "$PID_OLD" ]]; then
    echo "🛑 Deteniendo la instancia anterior ${OLD_INSTANCE} (PID: ${PID_OLD})..."
    kill "$PID_OLD" 2>/dev/null || true
  fi

  echo "🎉 Despliegue completado con éxito. Ahora la versión activa es ${NEW_INSTANCE} (${NEW_PORT})."
  exit 0
else
  echo "❌ ROLLBACK ACTIVADO: El health check falló en ${NEW_INSTANCE} (${NEW_PORT})."
  PID_FAIL=$(lsof -ti:${NEW_PORT} 2>/dev/null || true)
  if [[ -n "$PID_FAIL" ]]; then
    kill -9 "$PID_FAIL" 2>/dev/null || true
  fi
  echo "🛡️ La instancia anterior ${OLD_INSTANCE} (${OLD_PORT}) se mantiene activa."
  exit 1
fi
