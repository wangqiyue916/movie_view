package com.example.movie.news.controller;

import com.example.movie.common.response.ApiResponse;
import com.example.movie.news.entity.NewsArticle;
import com.example.movie.news.service.NewsService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/news")
public class AdminNewsController {

    private final NewsService newsService;

    public AdminNewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    /**
     * POST /api/admin/news
     * 管理员新增资讯
     */
    @PostMapping
    public ApiResponse<NewsArticle> create(@RequestBody Map<String, Object> body) {
        NewsArticle news = new NewsArticle();
        news.setTitle((String) body.get("title"));
        news.setSummary((String) body.get("summary"));
        news.setContent((String) body.get("content"));
        news.setCoverUrl((String) body.get("coverUrl"));
        news.setCategory((String) body.get("category"));
        news.setSource((String) body.getOrDefault("source", "平台编辑"));
        news.setStatus("ONLINE");
        news.setIsHot(0);
        news.setViewCount(0L);
        news.setPublishedAt(LocalDateTime.now());

        return ApiResponse.success(newsService.createNews(news));
    }

    /**
     * PUT /api/admin/news/{newsId}
     * 管理员修改资讯
     */
    @PutMapping("/{newsId}")
    public ApiResponse<NewsArticle> update(@PathVariable Long newsId, @RequestBody Map<String, Object> body) {
        NewsArticle news = new NewsArticle();
        news.setId(newsId);
        if (body.containsKey("title")) news.setTitle((String) body.get("title"));
        if (body.containsKey("summary")) news.setSummary((String) body.get("summary"));
        if (body.containsKey("content")) news.setContent((String) body.get("content"));
        if (body.containsKey("coverUrl")) news.setCoverUrl((String) body.get("coverUrl"));
        if (body.containsKey("category")) news.setCategory((String) body.get("category"));
        if (body.containsKey("source")) news.setSource((String) body.get("source"));
        if (body.containsKey("isHot")) news.setIsHot((Integer) body.get("isHot"));
        if (body.containsKey("status")) news.setStatus((String) body.get("status"));

        return ApiResponse.success(newsService.updateNews(news));
    }

    /**
     * DELETE /api/admin/news/{newsId}
     * 管理员删除资讯（逻辑删除）
     */
    @DeleteMapping("/{newsId}")
    public ApiResponse<Void> delete(@PathVariable Long newsId) {
        newsService.deleteNews(newsId);
        return ApiResponse.success();
    }
}