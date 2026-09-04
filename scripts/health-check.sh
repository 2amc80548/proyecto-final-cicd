#!/usr/bin/env bash
set -euo pipefail
#antes de  mandar a todos nuestro usuarios  a la nueva version
PORT="${1:-8080}"
HOST="${2:-localhost}"
URL="http://${HOST}:${PORT}/health-prueba"
MAX_ATTEMPTS=20
SLEEP_TIME=2

echo "🔍 Verificando la salud de la aplicación en ${URL}..."

for ((i=1; i<=MAX_ATTEMPTS; i++)); do
  HTTP_STATUS=$(curl -s -o /dev/null -w "%{http_code}" "$URL" || true)
  if [[ "$HTTP_STATUS" == "200" ]]; then
    echo "✅ [Intento $i/$MAX_ATTEMPTS] Servidor respondiendo OK (200) en puerto ${PORT}."
    exit 0
  fi
  echo "⏳ [Intento $i/$MAX_ATTEMPTS] Esperando respuesta en puerto ${PORT} (Estado HTTP: ${HTTP_STATUS})..."
  sleep "$SLEEP_TIME"
done

echo "❌ Error: La aplicación no respondió exitosamente en puerto ${PORT} tras ${MAX_ATTEMPTS} intentos."
exit 1

#activa rollback automatico si no da 