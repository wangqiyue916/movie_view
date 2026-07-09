SET NAMES utf8mb4;

USE `movie`;

SELECT
  table_name
FROM information_schema.tables
WHERE table_schema = 'movie'
  AND table_name IN (
    'roles',
    'permissions',
    'role_permissions',
    'movies',
    'news_articles',
    'news_relations',
    'long_reviews',
    'interpretation_videos',
    'merchandise',
    'homepage_recommendations',
    'ai_chat_sessions',
    'ai_chat_messages'
  )
ORDER BY table_name;

SHOW TABLES LIKE 'merchandise';
