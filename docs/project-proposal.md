# 课程项目选题：订餐系统（Food Ordering System）

- **项目名称**：订餐系统
- **目标用户**：餐厅顾客与餐厅运营人员
- **准备优先实现的一个业务场景**：用户浏览菜单并提交订单

## 核心实体

- **Dish（菜品）**：id、name、price、category、available（是否可售）
- **Order（订单）**：id、customerName、status（PENDING/CONFIRMED/COMPLETED/CANCELLED）、totalPrice、createdAt

## 业务流程

1. 顾客浏览可售菜品列表
2. 选择菜品并提交订单（校验菜品存在且可售）
3. 系统计算总价并创建 PENDING 状态订单
4. 餐厅确认订单 → CONFIRMED → 完成 → COMPLETED
5. 顾客可取消未确认的订单

## 业务规则

- 菜品不存在或不可售 → 拒绝下单
- 订单总价自动累加，不接受前端传入
- 只有 PENDING 状态的订单可以取消
- 已确认/完成的订单不能取消
