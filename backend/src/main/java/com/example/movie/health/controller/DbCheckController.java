package com.example.movie.health.controller;

import com.example.movie.common.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

@RestController
public class DbCheckController {

    @Autowired(required = false)
    private DataSource dataSource;

    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/api/db-check")
    public ApiResponse<Map<String, Object>> dbCheck() {
        if (dataSource == null) {
            return ApiResponse.error(500, "DataSource not available");
        }
        try (Connection conn = dataSource.getConnection()) {
            List<Map<String, Object>> tables = jdbcTemplate.queryForList("SHOW TABLES");
            return ApiResponse.success(Map.of(
                "status", "connected",
                "tables", tables.size(),
                "catalog", conn.getCatalog()
            ));
        } catch (Exception e) {
            return ApiResponse.error(500, e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
}
