package com.example.movie.common.config;

import org.springframework.boot.CommandLineRunner;
 import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * 应用启动时强制初始化数据库连接池（潘玺名，2025-07-09）
 * 
 * 解决首页首次请求因 HikariCP 惰性建连接导致的 500 错误。
 * 阿里云外网数据库延迟较高（约 1 秒），启动时预建连接后，
 * 后续请求不再等待连接建立。
 */
@Component
public class DataSourceInitializer implements CommandLineRunner {

    private final DataSource dataSource;

    public DataSourceInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) throws Exception {
        // 预建 minimum-idle 个连接，避免首页并行请求等待连接建立
        for (int i = 0; i < 5; i++) {
            try {
                Connection conn = dataSource.getConnection();
                conn.close();
            } catch (Exception e) {
                System.err.println("[DB] 预热连接 " + (i + 1) + " 失败: " + e.getMessage());
            }
        }
        System.out.println("[DB] 连接池预热完成（共 5 个连接）");
    }
}