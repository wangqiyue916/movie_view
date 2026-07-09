SET NAMES utf8mb4;
SET time_zone = '+08:00';

USE `movie`;

INSERT INTO `movie`.`homepage_recommendations` (
  section_code, target_type, target_id, title, image_url, sort_order, enabled
) VALUES
('BANNER_NEWS', 'NEWS', 1, '暑期档科幻电影热度持续升温', '/merch-shubiao.png', 1, 1),
('HOT_MOVIE', 'MOVIE', 1, '星际穿越', 'https://img2.doubanio.com/view/photo/s_ratio_poster/public/p2206088801.jpg', 1, 1),
('FEATURED_REVIEW', 'LONG_REVIEW', 1, '穿越星际之后，仍然回到人的情感', NULL, 1, 1),
('RECOMMEND_MERCH', 'MERCHANDISE', 1, '星际主题电影海报', '/merch-shubiao.png', 1, 1)
ON DUPLICATE KEY UPDATE
title = VALUES(title),
image_url = VALUES(image_url),
sort_order = VALUES(sort_order),
enabled = VALUES(enabled),
updated_at = CURRENT_TIMESTAMP;
