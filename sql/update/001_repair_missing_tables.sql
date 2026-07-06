SET NAMES utf8mb4;
SET time_zone = '+08:00';

USE `movie`;

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
