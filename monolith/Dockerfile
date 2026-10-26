# ============================================================
# 订餐系统 monolith - 多阶段构建 Dockerfile
# 阶段一 build：使用 Maven 镜像编译打包（含 JDK 25）
# 阶段二 runtime：仅使用 JRE 镜像，不含任何构建工具
# 构建命令（在仓库根目录执行）：
#   docker build -t food-ordering:week05 ./monolith
# ============================================================

# ---------- 阶段一：构建 ----------
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /build

# 先复制 pom.xml 拉取依赖，利用 Docker 层缓存加速重复构建
COPY pom.xml .
RUN mvn -B dependency:go-offline

# 再复制源码并打包（跳过测试，测试已在本地/CI 中单独验证）
COPY src ./src
RUN mvn -B -DskipTests package

# ---------- 阶段二：运行 ----------
FROM eclipse-temurin:25-jre
WORKDIR /app

# 仅复制构建阶段产出的可执行 jar，体积最小
COPY --from=build /build/target/food-ordering-0.0.1-SNAPSHOT.jar app.jar

# 对外暴露 8080 端口
EXPOSE 8080

# 数据库连接等通过环境变量注入（见 compose.yaml / .env.example）
ENTRYPOINT ["java", "-jar", "app.jar"]
