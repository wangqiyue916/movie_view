SET NAMES utf8mb4;
SET time_zone = '+08:00';

USE `movie`;

-- ============================================================
-- auth 模块：用户与角色表（潘玺名负责）
-- ============================================================

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

-- ============================================================

CREATE TABLE IF NOT EXISTS `movie`.`roles` (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  code VARCHAR(50) NOT NULL,
  name VARCHAR(50) NOT NULL,
  description VARCHAR(255) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_roles_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='roles';

CREATE TABLE IF NOT EXISTS `movie`.`permissions` (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  code VARCHAR(100) NOT NULL,
  name VARCHAR(100) NOT NULL,
  type VARCHAR(30) NOT NULL,
  description VARCHAR(255) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_permissions_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='permissions';

CREATE TABLE IF NOT EXISTS `movie`.`role_permissions` (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  role_id BIGINT NOT NULL,
  permission_id BIGINT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_role_permissions_role_permission (role_id, permission_id),
  KEY idx_role_permissions_permission_id (permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='role permissions';

CREATE TABLE IF NOT EXISTS `movie`.`movies` (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(100) NOT NULL,
  original_title VARCHAR(100) NULL,
  director VARCHAR(255) NULL,
  actors VARCHAR(500) NULL,
  genres VARCHAR(255) NULL,
  release_date DATE NULL,
  region VARCHAR(100) NULL,
  language VARCHAR(100) NULL,
  duration_minutes INT NULL,
  synopsis TEXT NULL,
  poster_url VARCHAR(500) NULL,
  status VARCHAR(30) NOT NULL DEFAULT 'ONLINE',
  official_user_id BIGINT NULL,
  view_count BIGINT NOT NULL DEFAULT 0,
  rating_count INT NOT NULL DEFAULT 0,
  avg_total_score DECIMAL(3,1) NULL,
  avg_story_score DECIMAL(3,1) NULL,
  avg_visual_score DECIMAL(3,1) NULL,
  avg_acting_score DECIMAL(3,1) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  KEY idx_movies_title (title),
  KEY idx_movies_status (status),
  KEY idx_movies_release_date (release_date),
  KEY idx_movies_avg_total_score (avg_total_score)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='movies';

CREATE TABLE IF NOT EXISTS `movie`.`news_articles` (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(150) NOT NULL,
  summary VARCHAR(500) NULL,
  content MEDIUMTEXT NOT NULL,
  cover_url VARCHAR(500) NULL,
  category VARCHAR(50) NULL,
  source VARCHAR(100) NULL,
  author_id BIGINT NULL,
  status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
  view_count BIGINT NOT NULL DEFAULT 0,
  is_hot TINYINT NOT NULL DEFAULT 0,
  published_at DATETIME NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  KEY idx_news_articles_status_published (status, published_at),
  KEY idx_news_articles_category (category),
  KEY idx_news_articles_hot (is_hot)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='news articles';

CREATE TABLE IF NOT EXISTS `movie`.`news_relations` (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  news_id BIGINT NOT NULL,
  target_type VARCHAR(50) NOT NULL,
  target_id BIGINT NULL,
  target_name VARCHAR(100) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_news_relations_news_id (news_id),
  KEY idx_news_relations_target (target_type, target_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='news relations';

CREATE TABLE IF NOT EXISTS `movie`.`long_reviews` (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  movie_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  title VARCHAR(100) NOT NULL,
  content_md MEDIUMTEXT NOT NULL,
  cover_url VARCHAR(500) NULL,
  status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
  view_count BIGINT NOT NULL DEFAULT 0,
  like_count INT NOT NULL DEFAULT 0,
  favorite_count INT NOT NULL DEFAULT 0,
  reply_count INT NOT NULL DEFAULT 0,
  is_featured TINYINT NOT NULL DEFAULT 0,
  featured_at DATETIME NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  KEY idx_long_reviews_movie_status_created (movie_id, status, created_at),
  KEY idx_long_reviews_user_id (user_id),
  KEY idx_long_reviews_featured_status (is_featured, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='long reviews';

CREATE TABLE IF NOT EXISTS `movie`.`interpretation_videos` (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  movie_id BIGINT NOT NULL,
  title VARCHAR(150) NOT NULL,
  cover_url VARCHAR(500) NULL,
  platform VARCHAR(50) NOT NULL,
  external_url VARCHAR(500) NOT NULL,
  description VARCHAR(500) NULL,
  heat_score INT NOT NULL DEFAULT 0,
  click_count BIGINT NOT NULL DEFAULT 0,
  submitter_id BIGINT NULL,
  status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  KEY idx_interpretation_videos_movie_status (movie_id, status),
  KEY idx_interpretation_videos_submitter_id (submitter_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='interpretation videos';

CREATE TABLE IF NOT EXISTS `movie`.`merchandise` (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  movie_id BIGINT NOT NULL,
  name VARCHAR(150) NOT NULL,
  image_url VARCHAR(500) NOT NULL,
  product_type VARCHAR(50) NOT NULL,
  price DECIMAL(10,2) NULL,
  description VARCHAR(1000) NULL,
  platform VARCHAR(50) NULL,
  external_url VARCHAR(500) NOT NULL,
  click_count BIGINT NOT NULL DEFAULT 0,
  submitter_id BIGINT NULL,
  status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  KEY idx_merchandise_movie_type_status (movie_id, product_type, status),
  KEY idx_merchandise_price (price),
  KEY idx_merchandise_submitter_id (submitter_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='merchandise';

CREATE TABLE IF NOT EXISTS `movie`.`homepage_recommendations` (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  section_code VARCHAR(50) NOT NULL,
  target_type VARCHAR(50) NOT NULL,
  target_id BIGINT NOT NULL,
  title VARCHAR(150) NULL,
  image_url VARCHAR(500) NULL,
  sort_order INT NOT NULL DEFAULT 0,
  enabled TINYINT NOT NULL DEFAULT 1,
  start_at DATETIME NULL,
  end_at DATETIME NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_homepage_recommendations_section_target (section_code, target_type, target_id),
  KEY idx_homepage_recommendations_section_enabled_sort (section_code, enabled, sort_order),
  KEY idx_homepage_recommendations_target (target_type, target_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='homepage recommendations';

CREATE TABLE IF NOT EXISTS `movie`.`ai_chat_sessions` (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NULL,
  title VARCHAR(100) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_ai_chat_sessions_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='AI chat sessions';

CREATE TABLE IF NOT EXISTS `movie`.`ai_chat_messages` (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  session_id BIGINT NOT NULL,
  role VARCHAR(30) NOT NULL,
  content TEXT NOT NULL,
  related_type VARCHAR(50) NULL,
  related_id BIGINT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_ai_chat_messages_session_created (session_id, created_at),
  KEY idx_ai_chat_messages_related (related_type, related_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='AI chat messages';

-- ============================================================
-- auth 模块：种子测试用户（密码均为 123456）
-- MySQL 内置 SHA2() 与 Java MessageDigest SHA-256 输出完全一致
-- ============================================================

-- 先清理旧数据（避免之前错误哈希的残留）
DELETE FROM `movie`.`user_roles` WHERE user_id IN (SELECT id FROM `movie`.`users` WHERE username IN ('user','official','admin','super_admin'));
DELETE FROM `movie`.`users` WHERE username IN ('user','official','admin','super_admin');

INSERT INTO `movie`.`users` (username, password_hash, nickname, status) VALUES
('user',          SHA2('123456', 256), '普通用户',   'NORMAL'),
('official',      SHA2('123456', 256), '电影官方',   'NORMAL'),
('admin',         SHA2('123456', 256), '管理员',     'NORMAL'),
('super_admin',   SHA2('123456', 256), '超级管理员', 'NORMAL');

-- ============================================================

INSERT INTO `movie`.`roles` (code, name, description) VALUES
('USER', '普通用户', '前台普通用户'),
('OFFICIAL', '电影官方用户', '电影商或官方投稿用户'),
('ADMIN', '管理员', '内容审核和运营管理'),
('SUPER_ADMIN', '超级管理员', '系统最高权限')
ON DUPLICATE KEY UPDATE name = VALUES(name), description = VALUES(description);

-- 给种子用户分配角色
INSERT IGNORE INTO `movie`.`user_roles` (user_id, role_id)
SELECT u.id, r.id FROM `movie`.`users` u JOIN `movie`.`roles` r
WHERE (u.username = 'user'         AND r.code = 'USER')
   OR (u.username = 'official'     AND r.code = 'OFFICIAL')
   OR (u.username = 'admin'        AND r.code = 'ADMIN')
   OR (u.username = 'super_admin'  AND r.code = 'SUPER_ADMIN');

INSERT INTO `movie`.`permissions` (code, name, type, description) VALUES
('movie:rating:create', '提交电影评分', 'ACTION', '普通用户提交或更新电影评分'),
('comment:short:create', '发布短评', 'ACTION', '普通用户发布电影短评'),
('review:long:create', '发布长评', 'ACTION', '普通用户提交长评'),
('review:reply:create', '发布回复', 'ACTION', '普通用户回复长评'),
('interaction:like', '点赞', 'ACTION', '点赞短评、长评或回复'),
('interaction:favorite', '收藏', 'ACTION', '收藏长评'),
('interaction:report', '举报', 'ACTION', '举报违规内容'),
('official:certification:submit', '提交电影商认证', 'ACTION', '电影商提交认证申请'),
('official:submission:create', '创建官方投稿', 'ACTION', '电影商投稿电影、资讯、视频、周边'),
('official:submission:view_own', '查看自己的投稿', 'ACTION', '电影商查看自己的投稿状态'),
('admin:dashboard:view', '查看后台首页', 'MENU', '管理员查看后台概览'),
('admin:audit:view', '查看审核列表', 'MENU', '管理员查看待审核内容'),
('admin:audit:approve', '审核通过', 'ACTION', '管理员审核通过内容'),
('admin:audit:reject', '审核驳回', 'ACTION', '管理员驳回内容'),
('admin:audit:publish', '上线内容', 'ACTION', '管理员上线内容'),
('admin:audit:offline', '下架内容', 'ACTION', '管理员下架内容'),
('admin:homepage:manage', '管理首页推荐位', 'ACTION', '管理员维护首页推荐位'),
('admin:report:handle', '处理举报', 'ACTION', '管理员处理举报'),
('super:user:manage', '管理所有用户', 'ACTION', '超级管理员管理用户'),
('super:role:manage', '管理角色权限', 'ACTION', '超级管理员管理角色权限')
ON DUPLICATE KEY UPDATE name = VALUES(name), type = VALUES(type), description = VALUES(description);

INSERT IGNORE INTO `movie`.`role_permissions` (role_id, permission_id)
SELECT r.id, p.id FROM `movie`.`roles` r JOIN `movie`.`permissions` p
WHERE r.code = 'USER'
  AND p.code IN (
    'movie:rating:create',
    'comment:short:create',
    'review:long:create',
    'review:reply:create',
    'interaction:like',
    'interaction:favorite',
    'interaction:report'
  );

INSERT IGNORE INTO `movie`.`role_permissions` (role_id, permission_id)
SELECT r.id, p.id FROM `movie`.`roles` r JOIN `movie`.`permissions` p
WHERE r.code = 'OFFICIAL'
  AND p.code IN (
    'movie:rating:create',
    'comment:short:create',
    'review:long:create',
    'review:reply:create',
    'interaction:like',
    'interaction:favorite',
    'interaction:report',
    'official:certification:submit',
    'official:submission:create',
    'official:submission:view_own'
  );

INSERT IGNORE INTO `movie`.`role_permissions` (role_id, permission_id)
SELECT r.id, p.id FROM `movie`.`roles` r JOIN `movie`.`permissions` p
WHERE r.code = 'ADMIN'
  AND p.code LIKE 'admin:%';

INSERT IGNORE INTO `movie`.`role_permissions` (role_id, permission_id)
SELECT r.id, p.id FROM `movie`.`roles` r JOIN `movie`.`permissions` p
WHERE r.code = 'SUPER_ADMIN';

INSERT INTO `movie`.`movies` (
  id, title, original_title, director, actors, genres, release_date, region, language,
  duration_minutes, synopsis, poster_url, status, view_count, rating_count,
  avg_total_score, avg_story_score, avg_visual_score, avg_acting_score
) VALUES
(1, '星际穿越', 'Interstellar', '克里斯托弗·诺兰', '马修·麦康纳, 安妮·海瑟薇, 杰西卡·查斯坦', '科幻, 剧情, 冒险', '2014-11-12', '美国', '英语', 169, '一组探险家穿越虫洞，为人类寻找新的家园。', 'https://img2.doubanio.com/view/photo/s_ratio_poster/public/p2206088801.jpg', 'ONLINE', 1200, 3, 9.4, 9.2, 9.6, 9.1),
(2, '流浪地球2', 'The Wandering Earth II', '郭帆', '吴京, 刘德华, 李雪健', '科幻, 冒险, 灾难', '2023-01-22', '中国大陆', '汉语普通话', 173, '太阳危机来临，人类开启推动地球离开太阳系的宏大计划。', 'https://img1.doubanio.com/view/photo/s_ratio_poster/public/p2885955777.jpg', 'ONLINE', 980, 2, 8.3, 8.2, 8.8, 8.0),
(3, '盗梦空间', 'Inception', '克里斯托弗·诺兰', '莱昂纳多·迪卡普里奥, 约瑟夫·高登-莱维特, 艾伦·佩吉', '科幻, 悬疑, 冒险', '2010-09-01', '美国', '英语', 148, '造梦师进入他人梦境，执行一场几乎不可能完成的植入任务。', 'https://img9.doubanio.com/view/photo/s_ratio_poster/public/p513344864.jpg', 'ONLINE', 1080, 2, 9.3, 9.1, 9.4, 9.0)
ON DUPLICATE KEY UPDATE title = VALUES(title), status = VALUES(status), updated_at = CURRENT_TIMESTAMP;

INSERT INTO `movie`.`news_articles` (
  id, title, summary, content, cover_url, category, source, status, view_count, is_hot, published_at
) VALUES
(1, '暑期档科幻电影热度持续升温', '多部科幻电影带动观影讨论热潮。', '暑期档多部科幻题材影片受到关注，观众围绕视觉效果、故事表达和人物塑造展开讨论。', '/merch-shubiao.png', '电影动态', '系统初始化', 'ONLINE', 320, 1, NOW()),
(2, '经典高分电影长评征集活动开启', '平台鼓励用户发布深度长评。', '为了鼓励优质内容创作，平台将根据点赞数、收藏数和回复数推荐优质长评。', '/merch-shubiao.png', '平台活动', '系统初始化', 'ONLINE', 210, 1, NOW())
ON DUPLICATE KEY UPDATE title = VALUES(title), status = VALUES(status), updated_at = CURRENT_TIMESTAMP;

INSERT INTO `movie`.`news_relations` (news_id, target_type, target_id, target_name)
SELECT 1, 'MOVIE', 1, '星际穿越'
WHERE NOT EXISTS (SELECT 1 FROM `movie`.`news_relations` WHERE news_id = 1 AND target_type = 'MOVIE' AND target_id = 1);

INSERT INTO `movie`.`long_reviews` (
  id, movie_id, user_id, title, content_md, cover_url, status, view_count,
  like_count, favorite_count, reply_count, is_featured, featured_at
) VALUES
(1, 1, 1, '穿越星际之后，仍然回到人的情感', '## 关于《星际穿越》\n\n这部电影最动人的地方，是它把宏大的宇宙尺度和亲情联系在一起。真正让人反复回看的，并不只是结构，还有每一层梦境背后未被说破的执念。', NULL, 'ONLINE', 180, 32, 8, 5, 1, NOW()),
(2, 3, 3, '梦境不是谜题，而是欲望的回声', '## 关于《盗梦空间》\n\n真正让人反复回看的，并不只是结构，还有每一层梦境背后未被说破的执念。', NULL, 'ONLINE', 96, 24, 5, 7, 1, NOW()),
(3, 2, 2, '灾难片里的群像，为什么仍然能打动人', '## 关于《流浪地球2》\n\n当宏大工程、末日危机和个体选择并置时，电影真正要讨论的不是奇观本身。', NULL, 'ONLINE', 72, 18, 2, 3, 1, NOW())
ON DUPLICATE KEY UPDATE title = VALUES(title), status = VALUES(status), updated_at = CURRENT_TIMESTAMP;

INSERT INTO `movie`.`interpretation_videos` (
  id, movie_id, title, cover_url, platform, external_url, description, heat_score, click_count, status
) VALUES
(1, 1, '《星际穿越》结局解析', '/merch-shubiao.png', '哔哩哔哩', 'https://www.bilibili.com', '从虫洞、五维空间和亲情线索解析电影结局。', 95, 0, 'ONLINE')
ON DUPLICATE KEY UPDATE title = VALUES(title), status = VALUES(status), updated_at = CURRENT_TIMESTAMP;

INSERT INTO `movie`.`merchandise` (
  id, movie_id, name, image_url, product_type, price, description, platform, external_url, status
) VALUES
(1, 1, '星际主题电影海报', '/merch-shubiao.png', '海报', 39.90, '星际主题装饰海报，适合收藏和房间装饰。', '淘宝', 'https://www.taobao.com', 'ONLINE')
ON DUPLICATE KEY UPDATE name = VALUES(name), status = VALUES(status), updated_at = CURRENT_TIMESTAMP;