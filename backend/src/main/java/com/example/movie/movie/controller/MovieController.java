package com.example.movie.movie.controller;

import com.example.movie.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {

    private final JdbcTemplate jdbcTemplate;

    @GetMapping
    public ApiResponse<Map<String, Object>> listMovies(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "50") int pageSize) {
        List<Map<String, Object>> list;
        Long total;
        try {
            list = jdbcTemplate.queryForList(
                "SELECT id, title AS name, poster_url FROM movies WHERE deleted_at IS NULL LIMIT ? OFFSET ?",
                pageSize, (page - 1) * pageSize);
            total = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM movies WHERE deleted_at IS NULL", Long.class);
        } catch (Exception e) {
            list = List.of();
            total = 0L;
        }
        return ApiResponse.success(Map.of(
            "list", list,
            "page", page,
            "size", list.size(),
            "total", total != null ? total : 0L
        ));
    }
}