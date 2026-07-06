SET NAMES utf8mb4;
SET time_zone = '+08:00';

USE movie;

CREATE TABLE IF NOT EXISTS users (
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

CREATE TABLE IF NOT EXISTS roles (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  code VARCHAR(50) NOT NULL,
  name VARCHAR(50) NOT NULL,
  description VARCHAR(255) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_roles_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色表';

CREATE TABLE IF NOT EXISTS user_roles (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_user_roles_user_role (user_id, role_id),
  KEY idx_user_roles_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户角色关联表';

CREATE TABLE IF NOT EXISTS permissions (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  code VARCHAR(100) NOT NULL,
  name VARCHAR(100) NOT NULL,
  type VARCHAR(30) NOT NULL,
  description VARCHAR(255) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_permissions_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='权限表';

CREATE TABLE IF NOT EXISTS role_permissions (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  role_id BIGINT NOT NULL,
  permission_id BIGINT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_role_permissions_role_permission (role_id, permission_id),
  KEY idx_role_permissions_permission_id (permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色权限关联表';

CREATE TABLE IF NOT EXISTS official_profiles (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  company_name VARCHAR(100) NOT NULL,
  contact_name VARCHAR(50) NOT NULL,
  contact_phone VARCHAR(30) NOT NULL,
  license_url VARCHAR(500) NULL,
  certification_status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
  reject_reason VARCHAR(500) NULL,
  certified_at DATETIME NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_official_profiles_user_id (user_id),
  KEY idx_official_profiles_status (certification_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='电影官方用户资料表';

CREATE TABLE IF NOT EXISTS movies (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='电影表';

CREATE TABLE IF NOT EXISTS official_movie_bindings (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  official_user_id BIGINT NOT NULL,
  movie_id BIGINT NOT NULL,
  status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_official_movie_bindings_user_movie (official_user_id, movie_id),
  KEY idx_official_movie_bindings_movie_id (movie_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='电影商授权电影表';

CREATE TABLE IF NOT EXISTS movie_ratings (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  movie_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  total_score DECIMAL(2,1) NOT NULL,
  story_score DECIMAL(2,1) NULL,
  visual_score DECIMAL(2,1) NULL,
  acting_score DECIMAL(2,1) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_movie_ratings_movie_user (movie_id, user_id),
  KEY idx_movie_ratings_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='电影评分表';

CREATE TABLE IF NOT EXISTS short_comments (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  movie_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  content VARCHAR(500) NOT NULL,
  status VARCHAR(30) NOT NULL DEFAULT 'NORMAL',
  like_count INT NOT NULL DEFAULT 0,
  report_count INT NOT NULL DEFAULT 0,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  KEY idx_short_comments_movie_created (movie_id, created_at),
  KEY idx_short_comments_user_id (user_id),
  KEY idx_short_comments_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='短评表';

CREATE TABLE IF NOT EXISTS likes (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  target_type VARCHAR(50) NOT NULL,
  target_id BIGINT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_likes_user_target (user_id, target_type, target_id),
  KEY idx_likes_target (target_type, target_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='点赞表';

CREATE TABLE IF NOT EXISTS reports (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  reporter_id BIGINT NOT NULL,
  target_type VARCHAR(50) NOT NULL,
  target_id BIGINT NOT NULL,
  reason VARCHAR(255) NOT NULL,
  status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
  handler_id BIGINT NULL,
  handle_result VARCHAR(500) NULL,
  handled_at DATETIME NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_reports_status (status),
  KEY idx_reports_target (target_type, target_id),
  KEY idx_reports_reporter_id (reporter_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='举报表';

CREATE TABLE IF NOT EXISTS long_reviews (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='长评表';

CREATE TABLE IF NOT EXISTS review_images (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  review_id BIGINT NOT NULL,
  image_url VARCHAR(500) NOT NULL,
  sort_order INT NOT NULL DEFAULT 0,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_review_images_review_id (review_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='长评图片表';

CREATE TABLE IF NOT EXISTS review_replies (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  review_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  parent_id BIGINT NULL,
  content VARCHAR(1000) NOT NULL,
  status VARCHAR(30) NOT NULL DEFAULT 'NORMAL',
  like_count INT NOT NULL DEFAULT 0,
  report_count INT NOT NULL DEFAULT 0,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL,
  KEY idx_review_replies_review_created (review_id, created_at),
  KEY idx_review_replies_parent_id (parent_id),
  KEY idx_review_replies_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='长评回复表';

CREATE TABLE IF NOT EXISTS favorites (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  target_type VARCHAR(50) NOT NULL,
  target_id BIGINT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_favorites_user_target (user_id, target_type, target_id),
  KEY idx_favorites_target (target_type, target_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='收藏表';

CREATE TABLE IF NOT EXISTS news_articles (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='资讯表';

CREATE TABLE IF NOT EXISTS news_relations (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  news_id BIGINT NOT NULL,
  target_type VARCHAR(50) NOT NULL,
  target_id BIGINT NULL,
  target_name VARCHAR(100) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_news_relations_news_id (news_id),
  KEY idx_news_relations_target (target_type, target_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='资讯关联表';

CREATE TABLE IF NOT EXISTS interpretation_videos (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='解读视频表';

CREATE TABLE IF NOT EXISTS merchandise (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='电影周边商品表';

CREATE TABLE IF NOT EXISTS audit_records (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  target_type VARCHAR(50) NOT NULL,
  target_id BIGINT NOT NULL,
  submitter_id BIGINT NULL,
  auditor_id BIGINT NOT NULL,
  action VARCHAR(50) NOT NULL,
  before_status VARCHAR(30) NULL,
  after_status VARCHAR(30) NOT NULL,
  reject_reason VARCHAR(500) NULL,
  remark VARCHAR(500) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_audit_records_target (target_type, target_id),
  KEY idx_audit_records_auditor_created (auditor_id, created_at),
  KEY idx_audit_records_submitter_id (submitter_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='审核记录表';

CREATE TABLE IF NOT EXISTS operation_logs (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  operator_id BIGINT NOT NULL,
  action VARCHAR(100) NOT NULL,
  target_type VARCHAR(50) NULL,
  target_id BIGINT NULL,
  detail TEXT NULL,
  ip VARCHAR(50) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_operation_logs_operator_created (operator_id, created_at),
  KEY idx_operation_logs_target (target_type, target_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='操作日志表';

CREATE TABLE IF NOT EXISTS homepage_recommendations (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='首页推荐位表';

CREATE TABLE IF NOT EXISTS ai_chat_sessions (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NULL,
  title VARCHAR(100) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_ai_chat_sessions_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='AI 会话表';

CREATE TABLE IF NOT EXISTS ai_chat_messages (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  session_id BIGINT NOT NULL,
  role VARCHAR(30) NOT NULL,
  content TEXT NOT NULL,
  related_type VARCHAR(50) NULL,
  related_id BIGINT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_ai_chat_messages_session_created (session_id, created_at),
  KEY idx_ai_chat_messages_related (related_type, related_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='AI 消息表';
