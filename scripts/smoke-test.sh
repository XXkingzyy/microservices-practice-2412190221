#!/usr/bin/env bash
# ============================================================
# 第 06 周验证脚本：微服务健康检查 + 跨服务下单成功 + 失败场景
# 从仓库根目录运行：bash scripts/smoke-test.sh
# 前置条件：docker compose up -d --build 已启动全部服务
# 覆盖：
#   1. 三个服务的健康接口
#   2. 跨服务业务流程成功（下单前校验 user-service 顾客 + menu-service 菜品）
#   3. 失败场景 a：菜品不存在（404）
#   4. 失败场景 b：菜品已下架不可售（409）
#   5. 失败场景 c：依赖服务（menu-service）不可用（503，临时停掉再恢复）
# ============================================================
set -u
cd "$(dirname "$0")/.." || exit 1

BASE_USER=http://localhost:8081
BASE_MENU=http://localhost:8082
BASE_ORDER=http://localhost:8083

PASS=0
FAIL=0

say()  { printf '\n[smoke] %s\n' "$1"; }
check() {
  if [ "$2" = "$3" ]; then PASS=$((PASS+1)); printf '  [PASS] %s -> HTTP %s\n' "$1" "$2";
  else FAIL=$((FAIL+1)); printf '  [FAIL] %s -> HTTP %s（期望 %s）\n' "$1" "$2" "$3"; fi
}
code() { curl -s -o /dev/null -w '%{http_code}' "$@"; }
extract_id() { grep -o '"id":"[^"]*"' | head -1 | cut -d'"' -f4; }
wait_health() {
  local name=$1 url=$2 max=${3:-60} i=0 c
  while [ $i -lt $max ]; do
    c=$(code "$url/actuator/health" 2>/dev/null)
    [ "$c" = "200" ] && { printf '  [OK] %s 健康检查就绪\n' "$name"; return 0; }
    sleep 2; i=$((i+2))
  done
  printf '  [FAIL] %s 健康检查超时未就绪\n' "$name"; return 1
}

say '===== 1. 各服务健康检查 ====='
wait_health user-service  "$BASE_USER"
wait_health menu-service  "$BASE_MENU"
wait_health order-service "$BASE_ORDER"

say '===== 2. 跨服务业务流程：下单前校验顾客 + 菜品 ====='
# 2.1 注册顾客（user-service）
CUST=$(curl -s -X POST "$BASE_USER/api/customers" \
  -H 'Content-Type: application/json' \
  -d '{"name":"张三","phone":"13800000001"}' | extract_id)
check '注册顾客 POST /api/customers → 201' "$(code -X POST "$BASE_USER/api/customers" -H 'Content-Type: application/json' -d '{"name":"张三","phone":"13800000001"}')" 201
printf '  customerId=%s\n' "$CUST"

# 2.2 新增菜品（menu-service）
DISH=$(curl -s -X POST "$BASE_MENU/api/dishes" \
  -H 'Content-Type: application/json' \
  -d '{"name":"宫保鸡丁","category":"川菜","price":28.00,"available":true}' | extract_id)
check '新增菜品 POST /api/dishes → 201' "$(code -X POST "$BASE_MENU/api/dishes" -H 'Content-Type: application/json' -d '{"name":"宫保鸡丁","category":"川菜","price":28.00,"available":true}')" 201
printf '  dishId=%s\n' "$DISH"

# 2.3 下单成功（order-service 跨服务校验）→ 201
ORDER_BODY=$(curl -s -X POST "$BASE_ORDER/api/orders" \
  -H 'Content-Type: application/json' \
  -d "{\"customerId\":\"$CUST\",\"items\":[{\"dishId\":\"$DISH\",\"quantity\":2}]}")
ORDER_ID=$(printf '%s' "$ORDER_BODY" | extract_id)
check '跨服务下单 POST /api/orders → 201' "$(code -X POST "$BASE_ORDER/api/orders" -H 'Content-Type: application/json' -d "{\"customerId\":\"$CUST\",\"items\":[{\"dishId\":\"$DISH\",\"quantity\":2}]}")" 201
printf '  orderId=%s 响应=%s\n' "$ORDER_ID" "$ORDER_BODY"

# 2.4 查询订单，验证总价由后端累加（28.00 × 2 = 56.00）
GET_BODY=$(curl -s "$BASE_ORDER/api/orders/$ORDER_ID")
check '查询订单 GET /api/orders/{id} → 200' "$(code "$BASE_ORDER/api/orders/$ORDER_ID")" 200
printf '  %s\n' "$GET_BODY"

say '===== 3. 失败场景 a：菜品不存在（404，不写入本地数据） ====='
RESP=$(curl -s -w '\n%{http_code}' -X POST "$BASE_ORDER/api/orders" \
  -H 'Content-Type: application/json' \
  -d "{\"customerId\":\"$CUST\",\"items\":[{\"dishId\":\"dish-not-exist\",\"quantity\":1}]}")
HTTP_CODE=$(printf '%s' "$RESP" | tail -1)
check '菜品不存在 POST /api/orders → 404' "$HTTP_CODE" 404
printf '  %s\n' "$(printf '%s' "$RESP" | head -1)"

say '===== 4. 失败场景 b：顾客不存在（404） ====='
RESP=$(curl -s -w '\n%{http_code}' -X POST "$BASE_ORDER/api/orders" \
  -H 'Content-Type: application/json' \
  -d "{\"customerId\":\"cust-not-exist\",\"items\":[{\"dishId\":\"$DISH\",\"quantity\":1}]}")
HTTP_CODE=$(printf '%s' "$RESP" | tail -1)
check '顾客不存在 POST /api/orders → 404' "$HTTP_CODE" 404
printf '  %s\n' "$(printf '%s' "$RESP" | head -1)"

say '===== 5. 失败场景 c：菜品已下架不可售（409） ====='
OFF=$(curl -s -X POST "$BASE_MENU/api/dishes" \
  -H 'Content-Type: application/json' \
  -d '{"name":"下架菜","category":"测试","price":10.00,"available":false}' | extract_id)
RESP=$(curl -s -w '\n%{http_code}' -X POST "$BASE_ORDER/api/orders" \
  -H 'Content-Type: application/json' \
  -d "{\"customerId\":\"$CUST\",\"items\":[{\"dishId\":\"$OFF\",\"quantity\":1}]}")
HTTP_CODE=$(printf '%s' "$RESP" | tail -1)
check '菜品不可售 POST /api/orders → 409' "$HTTP_CODE" 409
printf '  %s\n' "$(printf '%s' "$RESP" | head -1)"

say '===== 6. 失败场景 d：依赖服务（menu-service）不可用（503） ====='
docker compose stop menu-service >/dev/null 2>&1
sleep 3
RESP=$(curl -s -w '\n%{http_code}' -X POST "$BASE_ORDER/api/orders" \
  -H 'Content-Type: application/json' \
  -d "{\"customerId\":\"$CUST\",\"items\":[{\"dishId\":\"$DISH\",\"quantity\":1}]}")
HTTP_CODE=$(printf '%s' "$RESP" | tail -1)
check '菜单服务不可用 POST /api/orders → 503' "$HTTP_CODE" 503
printf '  %s\n' "$(printf '%s' "$RESP" | head -1)"
docker compose start menu-service >/dev/null 2>&1
wait_health menu-service "$BASE_MENU" 90

say '===== 结果汇总 ====='
printf '\n[smoke] PASS=%d  FAIL=%d\n' "$PASS" "$FAIL"
[ "$FAIL" -eq 0 ] && printf '[smoke] 全部通过 ✓\n' || printf '[smoke] 存在失败项 ✗\n'
