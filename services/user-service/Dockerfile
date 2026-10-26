# ============================================================
# user-service - 多阶段构建 Dockerfile
# 构建：docker build -t user-service:week06 ./services/user-service
# ============================================================

# ---------- 阶段一：构建 ----------
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /build

COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B -DskipTests=false package

# ---------- 阶段二：运行 ----------
FROM eclipse-temurin:25-jre
WORKDIR /app

COPY --from=build /build/target/user-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
