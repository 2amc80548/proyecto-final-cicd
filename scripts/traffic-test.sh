#!/usr/bin/env bash
set -euo pipefail
#prueba de carga  realiza peticiones para ver el trafico en cada una 
TARGET_URL="${1:-http://localhost:8080/api/instance}"
NUM_REQUESTS="${2:-20}"

echo "🚀 Iniciando prueba de tráfico hacia: ${TARGET_URL}"
echo "📊 Enviando ${NUM_REQUESTS} peticiones..."
echo "----------------------------------------"

BLUE_COUNT=0
GREEN_COUNT=0

for ((i=1; i<=NUM_REQUESTS; i++)); do
  RESPONSE=$(curl -s "$TARGET_URL" || echo '{"error":"sin respuesta"}')
  echo "Petición #$i -> $RESPONSE"
  
  if echo "$RESPONSE" | grep -q "BLUE"; then
    BLUE_COUNT=$((BLUE_COUNT + 1))
  elif echo "$RESPONSE" | grep -q "GREEN"; then
    GREEN_COUNT=$((GREEN_COUNT + 1))
  fi
  sleep 0.2
done

echo "----------------------------------------"
echo "📈 Resumen de Tráfico Recibido:"
echo "   🔵 Instancia BLUE:  ${BLUE_COUNT} respuestas"
echo "   🟢 Instancia GREEN: ${GREEN_COUNT} respuestas"
