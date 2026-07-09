-- 修复 news_articles 表的封面图链接为正确的值
SET NAMES utf8mb4;
SET SQL_SAFE_UPDATES = 0;
USE `movie`;

-- 批量修复所有被错误设置为 /merch-* 的资讯封面（通过主键 id 兜底）
UPDATE `news_articles` SET `cover_url` = CASE id
  WHEN 1  THEN 'https://images.unsplash.com/photo-1485846234645-a62644f84728?auto=format&fit=crop&w=1200&q=85'
  WHEN 2  THEN 'https://images.unsplash.com/photo-1517604931442-7e0c8ed2963c?auto=format&fit=crop&w=1200&q=80'
  WHEN 201 THEN 'https://images.unsplash.com/photo-1485846234645-a62644f84728?auto=format&fit=crop&w=1200&q=85'
  WHEN 202 THEN 'https://images.unsplash.com/photo-1518709268805-4e9042af2176?auto=format&fit=crop&w=1200&q=85'
  WHEN 203 THEN 'https://images.unsplash.com/photo-1497032628192-86f99bcd76bc?auto=format&fit=crop&w=1200&q=85'
  WHEN 204 THEN 'https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=1200&q=85'
  ELSE 'https://images.unsplash.com/photo-1485846234645-a62644f84728?auto=format&fit=crop&w=1200&q=85'
END
WHERE `cover_url` LIKE '/merch-%' OR `cover_url` IS NULL OR `cover_url` = '';

-- 恢复安全模式
SET SQL_SAFE_UPDATES = 1;
