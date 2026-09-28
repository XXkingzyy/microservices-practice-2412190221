# Week-03 · Spring Boot 基础

## 本周目标

创建订餐系统的 Spring Boot 基础工程，跑通"启动 → 问候接口 → 健康检查"的最小闭环，为后续业务模块开发打底。

## 完成内容

1. **Spring Boot 工程初始化（monolith/）**
   - Maven 单工程（`monolith/pom.xml`），Spring Boot 4.0.8 + Java 25
   - 依赖：`spring-boot-starter-web`（REST）、`spring-boot-starter-actuator`（健康检查）、`spring-boot-starter-test`（测试）
   - Group / Package name 按课程要求设置为 `com.zjgsu.jzy`
   - 内置 Maven Wrapper（`./mvnw`），`monolith/` 目录内可直接执行构建与运行命令

2. **问候接口**
   - `GET /hello`：返回 `Hello, welcome to the Food Ordering System!`
   - 代码：`monolith/src/main/java/com/zjgsu/jzy/controller/GreetingController.java`

3. **健康检查**
   - `GET /actuator/health`：返回 `{"status":"UP"}`

4. **接口测试与上下文加载测试**
   - `GreetingControllerTest`：MockMvc 验证 `/hello` 与 `/actuator/health` 两个接口
   - `FoodOrderingApplicationTests`：`@SpringBootTest` 的 `contextLoads` 测试，验证 Spring 应用上下文可正常加载
   - `./mvnw test` 全部通过（3 个用例）

5. **根 README 更新**
   - 写明 Java 25 / Spring Boot 4.0.x / Maven 技术栈与运行环境要求
   - 启动与测试命令（`./mvnw test`、`./mvnw spring-boot:run`）
   - 问候接口与健康检查访问地址
   - 当前尚未实现的业务能力（用户 / 菜单 / 订单模块）

## 运行与验证结果

| 步骤 | 命令 | 结果 |
|------|------|------|
| 测试 | `./mvnw test` | 通过（3 个用例：contextLoads、/hello、/actuator/health） |
| 启动 | `./mvnw spring-boot:run` | 应用正常启动，监听 8080 |
| 问候接口 | `curl http://localhost:8080/hello` | `Hello, welcome to the Food Ordering System!` |
| 健康检查 | `curl http://localhost:8080/actuator/health` | `{"status":"UP"}` |

> 说明：本地 Maven 直连中央仓库超时，改用阿里云镜像（`maven.aliyun.com/repository/public`）后依赖解析与构建正常。

## 后续计划

- 按业务模块实现用户、菜单、订单接口
- 引入数据存储（数据库 + JPA/MyBatis）
- 按"演进方向"逐步拆分微服务并容器化部署
