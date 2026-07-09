package com.example.movie.common.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * 数据库表初始化 - 独立运行一次即可建表，用完可删
 * 运行方式：java com.example.movie.common.config.DataInitializer
 */
public class DataInitializer {

    public static void main(String[] args) throws Exception {
        String host = System.getenv().getOrDefault("DB_HOST", "localhost");
        String port = System.getenv().getOrDefault("DB_PORT", "3306");
        String database = System.getenv().getOrDefault("DB_NAME", "movie");
        String user = System.getenv().getOrDefault("DB_USERNAME", "root");
        String password = System.getenv().getOrDefault("DB_PASSWORD", "");
        String url = "jdbc:mysql://" + host + ":" + port + "/" + database
                + "?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai"
                + "&useSSL=false&allowPublicKeyRetrieval=true";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS news_likes (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    news_id BIGINT NOT NULL,
                    user_id BIGINT NOT NULL,
                    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    UNIQUE KEY uk_news_likes_user (news_id, user_id),
                    KEY idx_news_likes_news (news_id)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS news_favorites (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    news_id BIGINT NOT NULL,
                    user_id BIGINT NOT NULL,
                    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    UNIQUE KEY uk_news_favorites_user (news_id, user_id),
                    KEY idx_news_favorites_news (news_id)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci
            """);

            System.out.println("Done: news_likes + news_favorites created.");
        }
    }
}
