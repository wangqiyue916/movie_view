SET NAMES utf8mb4;
SET time_zone = '+08:00';

USE `movie`;

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
