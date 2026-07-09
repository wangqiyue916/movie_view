SET NAMES utf8mb4;
SET time_zone = '+08:00';
USE `movie`;

START TRANSACTION;

-- 添加《霸王别姬》《肖申克的救赎》电影解读视频。
-- cover_url 使用 frontend/public 下的静态封面：
-- /video-cover-bawangbieji.png
-- /video-cover-xiaoshenke.png
-- 脚本按 external_url 删除后重建，重复执行不会产生重复数据。

DELETE FROM `interpretation_videos`
WHERE `external_url` IN (
  'https://www.bilibili.com/video/BV1ft411R7ak/',
  'https://www.bilibili.com/video/BV1us41117yA/'
);

INSERT INTO `interpretation_videos` (
  `movie_id`,
  `title`,
  `cover_url`,
  `platform`,
  `external_url`,
  `description`,
  `heat_score`,
  `click_count`,
  `submitter_id`,
  `status`,
  `created_at`,
  `updated_at`
)
SELECT
  m.id,
  '【木鱼微剧场】华语电影的巅峰之作《霸王别姬》（Re:C）',
  '/video-cover-bawangbieji.png',
  'Bilibili',
  'https://www.bilibili.com/video/BV1ft411R7ak/',
  '木鱼微剧场解读华语电影巅峰之作《霸王别姬》。',
  2500,
  0,
  NULL,
  'ONLINE',
  NOW(),
  NOW()
FROM `movies` m
WHERE m.title = '霸王别姬'
  AND m.deleted_at IS NULL;

INSERT INTO `interpretation_videos` (
  `movie_id`,
  `title`,
  `cover_url`,
  `platform`,
  `external_url`,
  `description`,
  `heat_score`,
  `click_count`,
  `submitter_id`,
  `status`,
  `created_at`,
  `updated_at`
)
SELECT
  m.id,
  '【木鱼微剧场】体制化与希望《肖申克的救赎》',
  '/video-cover-xiaoshenke.png',
  'Bilibili',
  'https://www.bilibili.com/video/BV1us41117yA/',
  '木鱼微剧场深度解读《肖申克的救赎》中的体制化与希望。',
  2800,
  0,
  NULL,
  'ONLINE',
  NOW(),
  NOW()
FROM `movies` m
WHERE m.title = '肖申克的救赎'
  AND m.deleted_at IS NULL;

COMMIT;

SELECT
  v.id,
  m.title AS movie_title,
  v.title AS video_title,
  v.cover_url,
  v.platform,
  v.external_url,
  v.status
FROM `interpretation_videos` v
JOIN `movies` m ON v.movie_id = m.id
WHERE m.title IN ('霸王别姬', '肖申克的救赎')
  AND v.external_url IN (
    'https://www.bilibili.com/video/BV1ft411R7ak/',
    'https://www.bilibili.com/video/BV1us41117yA/'
  )
ORDER BY m.id, v.heat_score DESC;
