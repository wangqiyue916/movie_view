package com.example.movie.common.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * 启动时将数据库中 Unsplash 封面图替换为本地占位图，消除跨域加载报错。
 */
@Component
public class NewsCoverFixRunner implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    public NewsCoverFixRunner(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        try {
            int updated = jdbcTemplate.update(
                "UPDATE news_articles SET cover_url = '/merch-shubiao.png' WHERE cover_url LIKE '%unsplash%'");
            if (updated > 0) {
                System.out.println("[NewsCoverFix] Updated " + updated + " news cover URLs (unsplash → local)");
            }
        } catch (Exception e) {
            System.err.println("[NewsCoverFix] Skipped: " + e.getMessage());
        }
    }
}
