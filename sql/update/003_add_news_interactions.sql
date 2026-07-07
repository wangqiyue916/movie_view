-- 罗智懿 - 资讯互动功能建表脚本
-- 创建时间：2026-07-07
-- 说明：为资讯模块添加点赞和收藏功能

USE `movie`;

-- 资讯点赞表
CREATE TABLE IF NOT EXISTS `movie`.`news_likes` (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  news_id BIGINT NOT NULL COMMENT '资讯ID',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  UNIQUE KEY uk_news_likes_user (news_id, user_id),
  KEY idx_news_likes_news (news_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='资讯点赞表';

-- 资讯收藏表
CREATE TABLE IF NOT EXISTS `movie`.`news_favorites` (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  news_id BIGINT NOT NULL COMMENT '资讯ID',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  UNIQUE KEY uk_news_favorites_user (news_id, user_id),
  KEY idx_news_favorites_news (news_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='资讯收藏表';