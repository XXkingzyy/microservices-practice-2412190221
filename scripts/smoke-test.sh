#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
check() {
  local name="$1" url="$2" body
  body=$(curl --fail --silent --show-error --connect-timeout 3 --max-time 10 "$url")
  if [[ "$name" != nacos ]]; then
    printf '%s' "$body" | grep -Eq '"status"[[:space:]]*:[[:space:]]*"UP"'
  fi
  printf '[PASS] %s %s\n' "$name" "$url"
}
check nacos "${NACOS_CONSOLE_URL:-http://localhost:18080/}"
check user-service "${USER_URL:-http://localhost:8081}/actuator/health"
check menu-service "${MENU_URL:-http://localhost:8082}/actuator/health"
check order-service "${ORDER_URL:-http://localhost:8083}/actuator/health"
printf 'Health checks passed. Run scripts/verify-business.py for business and outage checks.\n'
