-- ============================================================
-- 微服务拆分（第 06 周）：MySQL 初始化脚本
-- 在同一 MySQL 实例中为三个微服务建立独立数据库与独立账号
-- 由 compose.yaml 挂载到 /docker-entrypoint-initdb.d/init.sql，
-- 仅在数据卷首次初始化时执行；幂等（IF NOT EXISTS），可安全重跑
-- ============================================================

-- 用户服务：userdb / user
CREATE DATABASE IF NOT EXISTS userdb CHARACTER SET utf8mb4;
CREATE USER IF NOT EXISTS 'user'@'%' IDENTIFIED BY 'user123';
GRANT ALL PRIVILEGES ON userdb.* TO 'user'@'%';

-- 菜单服务：menudb / menu
CREATE DATABASE IF NOT EXISTS menudb CHARACTER SET utf8mb4;
CREATE USER IF NOT EXISTS 'menu'@'%' IDENTIFIED BY 'menu123';
GRANT ALL PRIVILEGES ON menudb.* TO 'menu'@'%';

-- 订单服务：orderdb / order_user
CREATE DATABASE IF NOT EXISTS orderdb CHARACTER SET utf8mb4;
CREATE USER IF NOT EXISTS 'order_user'@'%' IDENTIFIED BY 'order123';
GRANT ALL PRIVILEGES ON orderdb.* TO 'order_user'@'%';

FLUSH PRIVILEGES;
