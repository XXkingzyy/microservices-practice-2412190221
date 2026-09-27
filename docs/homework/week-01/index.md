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

> 以下为个人理解，用自己的话描述，不照抄官方定义，与作业系统提交内容一致。

### 1. 什么是微服务架构？

微服务架构就是把一个大型应用拆成多个独立的小服务，每个服务只负责一块业务，比如用户服务、订单服务、支付服务。它们各自独立开发、独立部署、独立演进，服务之间通过网络接口（如 HTTP/RPC）互相调用，再通过服务注册与发现、负载均衡、网关等基础设施把它们组织成一个完整的系统。核心思想是"分而治之"：把复杂问题拆小，每个团队只维护自己的服务，独立迭代、独立扩容，某个服务出问题也不会拖垮整个系统。

### 2. 微服务和单体架构的主要区别是什么？

单体架构把全部业务代码打进同一个进程、同一个包里，所有功能一起编译、一起部署、一起扩容，代码简单、调试方便，但项目变大后代码耦合严重，改一处可能影响全局，发布时整个系统都要重新上线。微服务架构把系统拆成多个独立进程的服务，每个服务单独开发、单独部署、单独扩缩容，技术栈也可以不同；代价是服务间多了网络通信，分布式环境下的数据一致性、链路追踪、部署运维都会更复杂。简单说：单体是"一个应用包含所有功能"，微服务是"多个小应用协作完成整套功能"。

### 3. 为什么本课程先实现单体系统，再逐步拆分为微服务？

先做单体可以让我们先把业务逻辑和整体架构跑通，验证需求、降低初期复杂度，也方便调试和测试；而且在很多真实项目里，一开始就把系统拆成微服务反而会引入大量不必要的分布式复杂度。当单体已经暴露出瓶颈（比如某个模块改动频繁、需要独立扩容、团队协作冲突多）时，再按业务边界逐步拆分出独立的服务，这样拆分依据明确、风险可控，也是业界推荐的演进路线："单体优先，按需拆分"。

### 4. 为什么作业需要提供可重复运行的测试验证脚本？

因为作业要能被客观地验证和评分。如果只有代码没有可重复运行的脚本，换个环境可能就跑不起来，别人也无法确认功能是否正确。提供可重复运行的测试验证脚本（比如 `mvn test`、JUnit 用例、部署脚本等），相当于把"我的代码是对的"变成"任何人按这个步骤都能得到同样的结果"，既方便老师检查，也方便自己回归测试，是工程上可追踪、可复现的基本要求。

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
