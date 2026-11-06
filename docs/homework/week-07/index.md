# 第 07 周：服务注册与发现

项目：订餐系统；姓名：金正宇；学号：2412190221。

## 部署与初始化

沿用三个独立业务服务与 MySQL 数据归属。根目录 Compose 新增 Nacos `v3.2.4`，独立持久化 data 与 logs。控制台为 `http://localhost:18080/`，服务端 8848，SDK gRPC 9848，均只绑定宿主机回环地址。真实凭据仅位于未提交的 `.env`。

1. 复制 `.env.example` 为 `.env`，填写随机 Base64 Token（解码不少于 32 字节）、Server identity 与管理员密码。
2. `docker compose up -d nacos mysql`。
3. 打开控制台，首次初始化 nacos 管理员密码，须与 `.env` 的 NACOS_PASSWORD 一致。
4. `docker compose up -d --build user-service menu-service order-service`。
5. 控制台 public 命名空间切换 PERSONAL_GROUP，核对三个服务名、实例 IP 与端口。

依赖版本使用 Spring Cloud 2025.1.0 与 Alibaba 2025.1.0.0 BOM，不单独指定 Nacos Client 版本，不引入 Config Starter。

## 服务发现与真实 HTTP 调用

订单服务在每次下单校验中调用 `DiscoveryClient.getInstances("user-service")` 与 `getInstances("menu-service")`。寻址组件使用第一个实例 URI，客户端拼接原有 `/api/customers/{id}` 和 `/api/dishes/{id}` 路径，使用带 2 秒连接和 3 秒读取超时的 RestTemplate 发起 HTTP 请求。每次操作重新读取实例列表，未保存永久固定地址。

原有 USER_SERVICE_URL、MENU_SERVICE_URL 配置已移除。空实例、发现异常、连接失败、超时与依赖 5xx 转成 503；依赖 404 保持 404；不可售菜品保持 409。校验全部通过才保存订单，保留菜品名称、单价与顾客名称快照。

## 正常、下线与恢复验证

从根目录执行 `python scripts/verify-business.py --outage`，脚本逐项打印实际请求地址、HTTP 状态与响应，并断言：

- 创建顾客与菜品后下单为 201，2 份单价 28 的菜品总价为 56，订单数增加 1。
- 确认和完成订单各返回 200。
- 不存在菜品返回 404，订单数不变。
- `docker compose stop menu-service` 后下单返回 503，订单数不变。
- `docker compose start menu-service`，等待健康和重新注册，再下单为 201，订单数增加 1。

同时在 Nacos 控制台观察菜单实例消失与重新出现。订单日志 `Discovery ... -> http://IP:port` 能与业务请求对应。脚本不能代替控制台实例变化截图。

SDK gRPC 连接存活只说明注册连接存在；Nacos 的实例状态与业务数据库是否可用不同；Actuator 健康检查能反映数据库状态。数据库故障导致 DOWN 时，SDK 仍可能连接，不能认为 Nacos 会自动轮询 Actuator 并摘除实例。注册中心也不会消除远端已写入、本地事务失败的窗口。

## 脚本与测试

`bash scripts/smoke-test.sh` 检查真实 18080 控制台 HTTP 地址和三个健康接口，任一 curl 或 UP 断言失败都会非零退出。

各服务执行 `mvn test`；测试配置禁用 discovery 和 Nacos 注册，用户及菜单使用 H2，订单模拟 DiscoveryClient 与 HTTP 响应，不依赖真实 MySQL/Nacos。新增测试覆盖空实例、每次重新寻址、使用发现 URI 的原业务路径、404 与远端 500；原业务测试覆盖失败不保存。

## 课内选课实践

`classroom/` 保留选课系统的学生、课程、选课独立工程，基础包名 `com.zjgsu.jzy`，端口 8091/8092/8093，各自拥有 H2 文件库。执行 `docker compose --profile classroom up -d --build student-service course-service enrollment-service`。三个服务在 COURSE_GROUP 注册，选课服务的 `CourseClient` 使用 DiscoveryClient 获取 URI 后发送真实 HTTP 请求，课程人数由课程服务本地事务维护。

选课先校验学生与课程、检查重复，再请求课程服务占位，最后写选课记录。远端占位成功而本地写库失败可能留下人数不一致，这是本周应理解的事务窗口；未声称具有分布式原子性。课内座位号需按当堂实际填写。

## 实际验证记录

本次实际执行结果见 `verification.md`；未执行项不得写成通过。历史 week-06 截图仅作为第 6 周已有材料。
