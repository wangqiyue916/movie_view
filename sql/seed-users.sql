-- ==========================================
-- 测试种子用户 (密码均为: 123456)
-- 可单独执行，会先删除旧数据再插入
-- ==========================================

USE `movie`;

-- 1. 清理旧的用户角色关联
DELETE ur
FROM `movie`.`user_roles` ur
JOIN `movie`.`users` u ON ur.user_id = u.id
WHERE u.username IN ('user', 'official', 'admin', 'super_admin');

-- 2. 删除旧用户
DELETE FROM `movie`.`users`
WHERE username IN ('user', 'official', 'admin', 'super_admin');

-- 3. 插入新用户（MySQL 内置 SHA2 与 Java SHA-256 结果一致）
INSERT INTO `movie`.`users` (username, password_hash, nickname, status) VALUES
('user',        SHA2('123456', 256), '普通用户',   'NORMAL'),
('official',    SHA2('123456', 256), '电影官方',   'NORMAL'),
('admin',       SHA2('123456', 256), '管理员',     'NORMAL'),
('super_admin', SHA2('123456', 256), '超级管理员', 'NORMAL');

-- 4. 分配角色
INSERT IGNORE INTO `movie`.`user_roles` (user_id, role_id)
SELECT u.id, r.id
FROM `movie`.`users` u
JOIN `movie`.`roles` r ON
    (u.username = 'user'         AND r.code = 'USER')
 OR (u.username = 'official'     AND r.code = 'OFFICIAL')
 OR (u.username = 'admin'        AND r.code = 'ADMIN')
 OR (u.username = 'super_admin'  AND r.code = 'SUPER_ADMIN');

-- 验证
SELECT u.username, u.nickname, r.code AS role
FROM `movie`.`users` u
JOIN `movie`.`user_roles` ur ON u.id = ur.user_id
JOIN `movie`.`roles` r ON ur.role_id = r.id
WHERE u.username IN ('user', 'official', 'admin', 'super_admin')
ORDER BY u.id;