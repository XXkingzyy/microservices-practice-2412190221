# Week 05 大作业：Docker 容器化（订餐系统）

- **项目主题**：订餐系统（Food Ordering System）
- **GitHub 仓库**：https://github.com/XXkingzyy/microservices-practice-2412190221
- **实现范围**：将上周的订餐单体应用（monolith/）与 MySQL 8.4.11 容器化，实现可重复的镜像构建、一键启动、业务 API 验证和数据持久化
- **关键文件**：`monolith/Dockerfile`、`monolith/.dockerignore`、根目录 `compose.yaml`、`.env.example`、`.gitignore`（含 `.env`）

---

## 一、Dockerfile 与镜像构建（题 1）

### 多阶段构建设计

`monolith/Dockerfile` 采用两阶段构建：

| 阶段 | 基础镜像 | 职责 |
|---|---|---|
| build | `maven:3.9-eclipse-temurin-25` | 拷贝 `pom.xml` 拉取依赖（利用层缓存）、拷贝源码、`mvn -B -DskipTests package` 打包 JAR |
| runtime | `eclipse-temurin:25-jre` | 只从 build 阶段拷贝 `food-ordering-0.0.1-SNAPSHOT.jar`，`java -jar` 启动 |

**多阶段构建的作用**：构建阶段需要完整的 Maven + JDK（体积大），运行阶段只需要 JRE，`COPY --from=build` 只把最终 JAR 带进运行镜像，因此运行镜像不包含任何编译工具，体积显著减小。

`monolith/.dockerignore` 排除本地构建产物（`target/`、`*.class`、`*.jar`）、IDE 配置（`.idea/`、`.vscode/`、`*.iml`）、日志与临时文件、密码文件（`.env`）等无关内容，避免其进入构建上下文。

### 构建命令（仓库根目录）

```bash
docker build -t food-ordering:week05 ./monolith
docker images | grep food-ordering
```

预期结果：镜像构建成功，最终镜像只含 JRE 与可运行 JAR，本机 Maven / Java 不参与构建。

截图见 `screenshots/01-docker-build.png`（`docker build` 成功输出）、`screenshots/02-docker-images.png`（`docker images` 查看镜像）。

---

## 二、Compose 编排与启动（题 2）

根目录 `compose.yaml`（不使用已废弃的 `version` 字段）编排两个服务：

| 服务 | 镜像/构建 | 端口 | 说明 |
|---|---|---|---|
| mysql | `mysql:8.4.11` | `3306:3306` | 命名数据卷 `mysql-data:/var/lib/mysql`；环境变量由 `.env` 注入；healthcheck 用 `mysqladmin ping` 探测 |
| app | `build: ./monolith` | `8080:8080` | `depends_on: mysql: condition: service_healthy`，等待数据库健康后再启动；通过 `DB_URL` 环境变量以**服务名 `mysql`** 连接数据库 |

- **服务连接方式**：同一 Compose 网络内，应用通过服务名 `mysql`（而非 `localhost`）访问数据库，连接串 `jdbc:mysql://mysql:3306/course?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC`。
- **环境变量**：`MYSQL_ROOT_PASSWORD`、`MYSQL_DATABASE`、`DB_USER`、`DB_PASSWORD`，提供模板 `.env.example`（`cp .env.example .env` 复制），真实 `.env` 已加入 `.gitignore` 不提交。
- **健康检查**：MySQL 配置 `healthcheck`，应用通过 `depends_on.condition: service_healthy` 等待数据库就绪，避免启动时序竞争。

### 启动命令（仓库根目录）

```bash
cp .env.example .env     # 首次需要，之后可跳过
docker compose up -d --build
docker compose ps        # 查看两个服务均 running
docker compose logs app  # 查看应用启动日志
```

预期结果：`food-app` 与 `food-mysql` 两个容器均处于 running，应用日志显示成功连接数据库并监听 8080。

截图见 `screenshots/03-compose-ps.png`（`docker compose ps` 输出）。

### 停止命令

```bash
docker compose down      # 停止并移除容器，保留数据卷（数据不丢）
docker compose down -v   # ⚠️ 慎用：会删除数据卷，清空所有数据
```

---

## 三、个人业务 API 验证（题 3）

通过容器映射到本机的 8080 端口，验证健康接口与订餐业务接口（一次创建 + 一次查询）：

```bash
# 1. 健康检查 → 200 {"status":"UP"}
curl -i http://localhost:8080/actuator/health

# 2. 创建菜品 → 201（返回带 id 的菜品）
curl -i -X POST http://localhost:8080/api/food/dishes \
  -H "Content-Type: application/json" \
  -d '{"name":"宫保鸡丁","category":"川菜","price":28.00,"available":true}'

# 3. 查询菜品列表 → 200（可看到刚创建的菜品）
curl http://localhost:8080/api/food/dishes

# 4. 下单（创建订单，总价由后端累加）→ 201
curl -i -X POST http://localhost:8080/api/food/orders \
  -H "Content-Type: application/json" \
  -d '{"customerName":"张三","items":[{"dishId":"<菜品ID>","quantity":2}]}'

# 5. 查询订单 → 200
curl http://localhost:8080/api/food/orders
```

预期结果：健康接口 `200 {"status":"UP"}`；创建菜品 `201` 且返回 id；查询接口 `200` 能查到刚创建的数据，响应内容与单体本地运行一致。

截图见 `screenshots/04-api-health.png`（健康检查）、`screenshots/05-api-create-query.png`（创建 + 查询）。

---

## 四、数据持久化验证（题 4）

命名数据卷 `mysql-data` 把 MySQL 数据目录 `/var/lib/mysql` 持久化在宿主机上，容器删除重建后数据仍在。

```bash
# 1. 启动后创建数据（创建菜品/订单，见第三节命令），记下 id
curl -i -X POST http://localhost:8080/api/food/dishes \
  -H "Content-Type: application/json" \
  -d '{"name":"麻婆豆腐","category":"川菜","price":22.00,"available":true}'

# 2. 容器重建：down 后重新 up（不带 -v！）
docker compose down
docker compose up -d

# 3. 等待应用启动完成后再次查询同一条记录
curl http://localhost:8080/api/food/dishes
# 确认 id 与关键字段（名称/价格）不变
```

预期结果：重建容器后查询返回同一条记录，`id` 与关键字段完全一致，证明命名数据卷持久化生效。**注意不要使用 `docker compose down -v`**，该命令会删除数据卷导致数据丢失。

截图见 `screenshots/06-persistence.png`（重建前后两次查询的对比输出）。

---

## 课内实践说明

第 05 周课内实践（沿用校园选课单体应用，配置 Dockerfile 与 Compose 启动应用与 MySQL，通过 `GET /api/courses` 验证可访问）的座位号、容器运行截图与 API 测试截图按课堂要求直接提交至作业系统，截图也备份在 `screenshots/`：`07-课内实践-compose-ps.png`、`08-课内实践-api.png`。
