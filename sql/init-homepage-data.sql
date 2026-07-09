SET NAMES utf8mb4;
SET time_zone = '+08:00';

USE `movie`;

-- 清空旧的推荐位，重新填充（幂等：有唯一索引保护不重复插入）
DELETE FROM `movie`.`homepage_recommendations`;

-- ===== 顶部轮播资讯（至少 4 条，供首页轮播展示） =====
INSERT INTO `movie`.`homepage_recommendations` (
  section_code, target_type, target_id, title, image_url, sort_order, enabled
) VALUES
('BANNER_NEWS', 'NEWS', 1, '暑期档科幻电影热度持续升温', 'https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?auto=format&fit=crop&w=1200&q=80', 1, 1),
('BANNER_NEWS', 'MOVIE', 1, '星际穿越 - 时间可以伸缩折叠，唯有爱不可替代', 'https://img2.doubanio.com/view/photo/s_ratio_poster/public/p2206088801.jpg', 2, 1),
('BANNER_NEWS', 'MOVIE', 2, '盗梦空间 - 梦境层层，现实何处？', 'https://img1.doubanio.com/view/photo/s_ratio_poster/public/p513344864.jpg', 3, 1),
('BANNER_NEWS', 'NEWS', 2, '经典高分电影长评征集活动开启', 'https://images.unsplash.com/photo-1524985069026-dd778a71c7b4?auto=format&fit=crop&w=1200&q=80', 4, 1),

-- ===== 热门电影（至少 8 条） =====
('HOT_MOVIE', 'MOVIE', 1, '星际穿越', 'https://img2.doubanio.com/view/photo/s_ratio_poster/public/p2206088801.jpg', 1, 1),
('HOT_MOVIE', 'MOVIE', 2, '盗梦空间', 'https://img1.doubanio.com/view/photo/s_ratio_poster/public/p513344864.jpg', 2, 1),
('HOT_MOVIE', 'MOVIE', 3, '阿甘正传', 'https://img9.doubanio.com/view/photo/s_ratio_poster/public/p2372307693.jpg', 3, 1),
('HOT_MOVIE', 'MOVIE', 4, '肖申克的救赎', 'https://img2.doubanio.com/view/photo/s_ratio_poster/public/p480747492.jpg', 4, 1),
('HOT_MOVIE', 'MOVIE', 5, '千与千寻', 'https://img1.doubanio.com/view/photo/s_ratio_poster/public/p2557573348.jpg', 5, 1),
('HOT_MOVIE', 'MOVIE', 6, '泰坦尼克号', 'https://img9.doubanio.com/view/photo/s_ratio_poster/public/p457760035.jpg', 6, 1),
('HOT_MOVIE', 'MOVIE', 7, '楚门的世界', 'https://img3.doubanio.com/view/photo/s_ratio_poster/public/p479682972.jpg', 7, 1),
('HOT_MOVIE', 'MOVIE', 8, '大话西游之大圣娶亲', 'https://img9.doubanio.com/view/photo/s_ratio_poster/public/p2455050536.jpg', 8, 1),

-- ===== 精选长评（至少 3 条） =====
('FEATURED_REVIEW', 'LONG_REVIEW', 1, '穿越星际之后，仍然回到人的情感', NULL, 1, 1),
('FEATURED_REVIEW', 'LONG_REVIEW', 2, '梦境不是谜题，而是欲望的回声', NULL, 2, 1),
('FEATURED_REVIEW', 'LONG_REVIEW', 3, '灾难片里的群像，为什么仍然能打动人', NULL, 3, 1),

-- ===== 推荐周边（至少 4 条） =====
('RECOMMEND_MERCH', 'MERCHANDISE', 1, '星际穿越主题海报', 'https://images.unsplash.com/photo-1462331940025-496dfbfc7564?auto=format&fit=crop&w=1200&q=80', 1, 1),
('RECOMMEND_MERCH', 'MERCHANDISE', 2, '盗梦空间陀螺模型', 'https://images.unsplash.com/photo-1460881680858-30d872d5b530?auto=format&fit=crop&w=1200&q=80', 2, 1),
('RECOMMEND_MERCH', 'MERCHANDISE', 3, '流浪地球金属书签', 'https://images.unsplash.com/photo-1512070679279-8988d32161be?auto=format&fit=crop&w=1200&q=80', 3, 1),
('RECOMMEND_MERCH', 'MERCHANDISE', 4, '楚门的世界限定卡片', 'https://images.unsplash.com/photo-1497032628192-86f99bcd76bc?auto=format&fit=crop&w=1200&q=80', 4, 1)
ON DUPLICATE KEY UPDATE
title = VALUES(title),
image_url = VALUES(image_url),
sort_order = VALUES(sort_order),
enabled = VALUES(enabled),
updated_at = CURRENT_TIMESTAMP;
