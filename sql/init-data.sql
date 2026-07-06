SET NAMES utf8mb4;
SET time_zone = '+08:00';

USE `movie`;

INSERT INTO `movie`.`roles` (code, name, description) VALUES
('USER', '普通用户', '前台普通用户'),
('OFFICIAL', '电影官方用户', '电影商或官方投稿用户'),
('ADMIN', '管理员', '内容审核和运营管理'),
('SUPER_ADMIN', '超级管理员', '系统最高权限')
ON DUPLICATE KEY UPDATE name = VALUES(name), description = VALUES(description);

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
(1, '暑期档科幻电影热度持续升温', '多部科幻电影带动观影讨论热潮。', '暑期档多部科幻题材影片受到关注，观众围绕视觉效果、故事表达和人物塑造展开讨论。', 'https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?auto=format&fit=crop&w=1200&q=80', '电影动态', '系统初始化', 'ONLINE', 320, 1, NOW()),
(2, '经典高分电影长评征集活动开启', '平台鼓励用户发布深度长评。', '为了鼓励优质内容创作，平台将根据点赞数、收藏数和回复数推荐优质长评。', 'https://images.unsplash.com/photo-1517604931442-7e0c8ed2963c?auto=format&fit=crop&w=1200&q=80', '平台活动', '系统初始化', 'ONLINE', 210, 1, NOW())
ON DUPLICATE KEY UPDATE title = VALUES(title), status = VALUES(status), updated_at = CURRENT_TIMESTAMP;

INSERT INTO `movie`.`news_relations` (news_id, target_type, target_id, target_name)
SELECT 1, 'MOVIE', 1, '星际穿越'
WHERE NOT EXISTS (SELECT 1 FROM `movie`.`news_relations` WHERE news_id = 1 AND target_type = 'MOVIE' AND target_id = 1);

INSERT INTO `movie`.`long_reviews` (
  id, movie_id, user_id, title, content_md, cover_url, status, view_count,
  like_count, favorite_count, reply_count, is_featured, featured_at
) VALUES
(1, 1, 1, '穿越星际之后，仍然回到人的情感', '## 关于《星际穿越》\n\n这部电影最动人的地方，是它把宏大的宇宙尺度和亲情联系在一起。', NULL, 'ONLINE', 180, 32, 8, 5, 1, NOW())
ON DUPLICATE KEY UPDATE title = VALUES(title), status = VALUES(status), updated_at = CURRENT_TIMESTAMP;

INSERT INTO `movie`.`interpretation_videos` (
  id, movie_id, title, cover_url, platform, external_url, description, heat_score, click_count, status
) VALUES
(1, 1, '《星际穿越》结局解析', 'https://images.unsplash.com/photo-1446776811953-b23d57bd21aa?auto=format&fit=crop&w=1200&q=80', '哔哩哔哩', 'https://www.bilibili.com', '从虫洞、五维空间和亲情线索解析电影结局。', 95, 0, 'ONLINE')
ON DUPLICATE KEY UPDATE title = VALUES(title), status = VALUES(status), updated_at = CURRENT_TIMESTAMP;

INSERT INTO `movie`.`merchandise` (
  id, movie_id, name, image_url, product_type, price, description, platform, external_url, status
) VALUES
(1, 1, '星际主题电影海报', 'https://images.unsplash.com/photo-1462331940025-496dfbfc7564?auto=format&fit=crop&w=1200&q=80', '海报', 39.90, '星际主题装饰海报，适合收藏和房间装饰。', '淘宝', 'https://www.taobao.com', 'ONLINE')
ON DUPLICATE KEY UPDATE name = VALUES(name), status = VALUES(status), updated_at = CURRENT_TIMESTAMP;

INSERT INTO `movie`.`homepage_recommendations` (
  section_code, target_type, target_id, title, image_url, sort_order, enabled
) VALUES
('BANNER_NEWS', 'NEWS', 1, '暑期档科幻电影热度持续升温', 'https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?auto=format&fit=crop&w=1200&q=80', 1, 1),
('HOT_MOVIE', 'MOVIE', 1, '星际穿越', 'https://img2.doubanio.com/view/photo/s_ratio_poster/public/p2206088801.jpg', 1, 1),
('FEATURED_REVIEW', 'LONG_REVIEW', 1, '穿越星际之后，仍然回到人的情感', NULL, 1, 1),
('RECOMMEND_MERCH', 'MERCHANDISE', 1, '星际主题电影海报', 'https://images.unsplash.com/photo-1462331940025-496dfbfc7564?auto=format&fit=crop&w=1200&q=80', 1, 1)
ON DUPLICATE KEY UPDATE
title = VALUES(title),
image_url = VALUES(image_url),
sort_order = VALUES(sort_order),
enabled = VALUES(enabled),
updated_at = CURRENT_TIMESTAMP;
