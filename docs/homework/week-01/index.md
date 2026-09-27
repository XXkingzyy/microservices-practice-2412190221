# Week 01 实践作业：开发环境与个人仓库

- **课程**：微服务课程
- **姓名**：金正宇
- **学号**：2412190221
- **日期**：2026-09-26

## 一、开发环境检查

本次作业完成了微服务课程基础开发环境（Java、Maven、Git、Docker）的检查，当前环境信息如下：

| 工具 | 版本 | 状态 |
|------|------|------|
| Java | OpenJDK 21.0.6 LTS（Microsoft build） | ✅ 已安装 |
| Maven | 3.9.16 | ✅ 已安装 |
| Git | 2.52.0.windows.1 | ✅ 已安装 |
| Docker | 29.8.0（Docker Desktop，build 88096ef） | ✅ 已安装 |

环境检查截图见 [screenshots/](screenshots/) 目录：

- [screenshots/01-java-version.png](screenshots/01-java-version.png)：Java 版本
- [screenshots/02-maven-version.png](screenshots/02-maven-version.png)：Maven 版本
- [screenshots/03-git-version.png](screenshots/03-git-version.png)：Git 版本
- [screenshots/04-docker-version.png](screenshots/04-docker-version.png)：Docker 版本

## 二、概念回答

> 以下为个人理解，用自己的话描述，不照抄官方定义。

### 1. 什么是微服务架构？

微服务架构就是把一个大型应用拆成多个独立的小服务，每个服务只负责一块业务，比如用户服务、订单服务、支付服务。它们各自独立开发、独立部署，通过接口互相通信。好处是团队可以并行开发、某个服务挂了不会拖垮整个系统、也能按需扩展；代价是服务之间的网络通信、数据一致性、运维监控会变得更复杂。

### 2. Git 和 GitHub 分别是什么？为什么要用它们？

Git 是一个本地的版本控制工具，用来记录代码的每一次修改，可以随时回退、对比、分支开发；GitHub 是建立在 Git 之上的远程托管平台，把本地仓库同步到云端，方便多人协作、备份和公开分享。本课程的作业全部在 GitHub 仓库中完成，就是为了让每次提交都有记录可追踪。

### 3. Maven 的作用是什么？

Maven 是 Java 项目的构建和依赖管理工具。它用 `pom.xml` 声明项目需要的第三方库并自动下载、管理版本，同时统一了编译、测试、打包（jar/war）、运行的生命周期，让整个项目在任何机器上都能用同样的方式构建，避免"在我电脑上能跑"的问题。

### 4. Docker 的作用是什么？

Docker 是容器化工具，可以把应用连同它的运行环境（JDK、依赖、配置）一起打包成镜像，在任何装了 Docker 的机器上以容器方式运行，环境完全一致。微服务数量多、环境杂，用 Docker 可以轻松地在本地复现部署环境，也为后续课程的容器化部署打基础。

## 三、GitHub 仓库

- 仓库地址：`https://github.com/XXkingzyy/microservices-practice-2412190221`
- 可见性：公开（Public）
- 提交记录：见下方截图

仓库创建与推送截图见 [screenshots/](screenshots/) 目录：

- [screenshots/05-github-repo.png](screenshots/05-github-repo.png)：GitHub 仓库页面
- [screenshots/06-git-log.png](screenshots/06-git-log.png)：本地提交记录

## 四、提交记录

```
git init
git add .
git commit -m "chore: init microservices practice repository"
git branch -M main
git remote add origin https://github.com/XXkingzyy/microservices-practice-2412190221.git
git push -u origin main
```

实际提交历史（`git log --oneline --graph --decorate`）：

```
* 0cd3ccc (HEAD -> main, origin/main) chore: add src placeholder to track empty source directory
* f1105c2 chore: init microservices practice repository
```
