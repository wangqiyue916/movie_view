SET NAMES utf8mb4;
SET time_zone = '+08:00';

USE `movie`;

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

SHOW TABLES LIKE 'merchandise';
