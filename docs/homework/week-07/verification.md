# 实际验证结果（2026-11-06，Asia/Shanghai）

- 用户服务：2 个测试通过；菜单服务：2 个测试通过；订单服务：10 个测试通过。总计 14，失败 0、错误 0。
- 选课 student/course/enrollment 三个工程独立 `mvn package` 成功。
- Nacos 3.2.4 已初始化并运行；MySQL healthy；六个业务容器均运行。
- PERSONAL_GROUP 中 user/menu/order 使用独立服务名；COURSE_GROUP 中 student/course/enrollment 使用独立服务名。Nacos Admin API 返回实际 172.18.0.x 容器地址。
- 容器内发现寻址后真实下单返回 201，总价 56；确认、完成返回 200。
- 缺失菜品返回 404，订单数不变。
- `docker compose stop menu-service` 后下单返回 503，错误为“依赖服务无可用实例: menu-service”，订单数不变。
- `docker compose start menu-service`，等待健康与重新注册后下单返回 201，订单数增加 1。
- `bash scripts/smoke-test.sh` 四项检查通过，退出 0。将 NACOS_CONSOLE_URL 指向本地关闭端口后退出 7，未假报成功。
- 课内选课：学生创建 201，课程创建 201，选课 201，查询课程 200，enrolled 从 0 变为 1。日志显示发现 COURSE_GROUP 的实际实例 URI。

本机 Docker Desktop 首次将 SQL bind mount 识别成目录，改为 `db/Dockerfile` 在构建时复制 SQL，数据库初始化成功；保留现有数据卷。Docker Hub 下载曾失败，重试后成功。

本次运行先执行各服务 Maven 打包，再使用 `Dockerfile.runtime` 与 `compose.prebuilt.yaml` 构建镜像，命令为：

```bash
# 六个服务各自执行 mvn package；三个订餐服务已先运行测试
docker compose -f compose.yaml -f compose.prebuilt.yaml --profile classroom build
docker compose -f compose.yaml -f compose.prebuilt.yaml --profile classroom up -d --no-build
```

默认 Dockerfile 保留源码多阶段构建；本次实际验证的是上面的预构建 jar 路径。`Dockerfile.runtime.dockerignore` 明确包含 target jar，避免被原 .dockerignore 排除。

证据页面展示 Nacos 实际 Admin API 响应、原始请求响应与程序日志，不伪装成 Nacos 控制台。历史 week-06 截图没有改作本周截图。
