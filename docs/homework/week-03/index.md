# Week-03 · Spring Boot 基础

## 本周目标

创建订餐系统的 Spring Boot 基础工程，跑通"启动 → 问候接口 → 健康检查"的最小闭环，为后续业务模块开发打底。

## 完成内容

1. **Spring Boot 工程初始化**
   - Maven 多模块单工程（`pom.xml`），Spring Boot 3.3.5 + Java 21
   - 依赖：`spring-boot-starter-web`（REST）、`spring-boot-starter-actuator`（健康检查）、`spring-boot-starter-test`（测试）
   - 应用主类：`com.example.foodordering.FoodOrderingApplication`

2. **问候接口**
   - `GET /hello`：返回 `Hello, welcome to the Food Ordering System!`
   - 代码：`src/main/java/com/example/foodordering/controller/GreetingController.java`

3. **健康检查**
   - `GET /actuator/health`：返回 `{"status":"UP"}`

4. **接口测试**
   - `GreetingControllerTest`：MockMvc 验证 `/hello` 与 `/actuator/health` 两个接口，`mvn test` 全部通过

5. **根 README 更新**
   - 补充运行环境要求（Java 21 / Maven 3.9+）、启动与测试命令、接口访问地址、尚未实现的业务能力

## 运行与验证结果

| 步骤 | 命令 | 结果 |
|------|------|------|
| 测试 | `mvn test` | 通过（2 个用例：/hello、/actuator/health） |
| 启动 | `java -jar target/food-ordering-0.0.1-SNAPSHOT.jar` | 应用正常启动，监听 8080 |
| 问候接口 | `curl http://localhost:8080/hello` | `Hello, welcome to the Food Ordering System!` |
| 健康检查 | `curl http://localhost:8080/actuator/health` | `{"status":"UP"}` |

> 说明：本地 Maven 直连中央仓库超时，改用阿里云镜像（`maven.aliyun.com/repository/public`）后依赖解析与构建正常。

## 后续计划

- 按业务模块实现用户、菜单、订单接口
- 引入数据存储（数据库 + JPA/MyBatis）
- 按"演进方向"逐步拆分微服务并容器化部署
