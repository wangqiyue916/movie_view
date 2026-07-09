-- ============================================================
-- 种子测试用户（可单独执行，密码均为 123456 明文）
-- ============================================================
USE `movie`;

-- 确保表存在
CREATE TABLE IF NOT EXISTS `movie`.`users` (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  nickname VARCHAR(50) NOT NULL,
  avatar_url VARCHAR(500) NULL,
  email VARCHAR(100) NULL,
  phone VARCHAR(30) NULL,
  status VARCHAR(30) NOT NULL DEFAULT 'NORMAL',
  last_login_at DATETIME NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  UNIQUE KEY uk_users_username (username),
  KEY idx_users_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户表';

CREATE TABLE IF NOT EXISTS `movie`.`user_roles` (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_user_roles_user_role (user_id, role_id),
  KEY idx_user_roles_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户角色关联表';

-- 清理旧数据
SET @uid1 = (SELECT id FROM `movie`.`users` WHERE username = 'user'      LIMIT 1);
SET @uid2 = (SELECT id FROM `movie`.`users` WHERE username = 'official'  LIMIT 1);
SET @uid3 = (SELECT id FROM `movie`.`users` WHERE username = 'admin'     LIMIT 1);
SET @uid4 = (SELECT id FROM `movie`.`users` WHERE username = 'super_admin' LIMIT 1);
DELETE FROM `movie`.`user_roles` WHERE user_id IN (@uid1, @uid2, @uid3, @uid4);
DELETE FROM `movie`.`users` WHERE username IN ('user','official','admin','super_admin');

-- 插入
INSERT INTO `movie`.`users` (username, password_hash, nickname, status) VALUES
('user',          '123456', '普通用户',   'NORMAL'),
('official',      '123456', '电影官方',   'NORMAL'),
('admin',         '123456', '管理员',     'NORMAL'),
('super_admin',   '123456', '超级管理员', 'NORMAL');

-- 分配角色
INSERT IGNORE INTO `movie`.`user_roles` (user_id, role_id)
SELECT u.id, r.id FROM `movie`.`users` u JOIN `movie`.`roles` r
WHERE (u.username = 'user'         AND r.code = 'USER')
   OR (u.username = 'official'     AND r.code = 'OFFICIAL')
   OR (u.username = 'admin'        AND r.code = 'ADMIN')
   OR (u.username = 'super_admin'  AND r.code = 'SUPER_ADMIN');

-- 验证
SELECT username, password_hash, nickname FROM `movie`.`users`
WHERE username IN ('user','official','admin','super_admin');