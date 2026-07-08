package com.example.movie.news.controller;

import com.example.movie.common.response.ApiResponse;
import com.example.movie.news.entity.NewsArticle;
import com.example.movie.news.service.NewsService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 管理员-资讯审核流程
 * TODO: 后续对接潘玺名通用审核流程后替换为统一的 /api/admin/audits/{targetType}/{targetId} 接口
 */
@RestController
@RequestMapping("/api/admin/news")
public class AdminNewsAuditController {

    private final NewsService newsService;

    public AdminNewsAuditController(NewsService newsService) {
        this.newsService = newsService;
    }

    /**
     * PUT /api/admin/news/{newsId}/approve
     * 审核通过，状态改为 APPROVED
     */
    @PutMapping("/{newsId}/approve")
    public ApiResponse<NewsArticle> approve(@PathVariable Long newsId) {
        NewsArticle news = new NewsArticle();
        news.setId(newsId);
        news.setStatus("APPROVED");
        return ApiResponse.success(newsService.updateNews(news));
    }

    /**
     * PUT /api/admin/news/{newsId}/reject
     * 驳回，状态改为 REJECTED
     */
    @PutMapping("/{newsId}/reject")
    public ApiResponse<NewsArticle> reject(@PathVariable Long newsId, @RequestBody Map<String, String> body) {
        NewsArticle news = new NewsArticle();
        news.setId(newsId);
        news.setStatus("REJECTED");
        // TODO: 对接潘玺名审核记录表，写入驳回原因 body.get("reason")
        return ApiResponse.success(newsService.updateNews(news));
    }

    /**
     * PUT /api/admin/news/{newsId}/publish
     * 上线发布，状态改为 ONLINE
     */
    @PutMapping("/{newsId}/publish")
    public ApiResponse<NewsArticle> publish(@PathVariable Long newsId) {
        NewsArticle news = new NewsArticle();
        news.setId(newsId);
        news.setStatus("ONLINE");
        news.setPublishedAt(LocalDateTime.now());
        return ApiResponse.success(newsService.updateNews(news));
    }

    /**
     * PUT /api/admin/news/{newsId}/offline
     * 下架，状态改为 OFFLINE
     */
    @PutMapping("/{newsId}/offline")
    public ApiResponse<NewsArticle> offline(@PathVariable Long newsId, @RequestBody Map<String, String> body) {
        NewsArticle news = new NewsArticle();
        news.setId(newsId);
        news.setStatus("OFFLINE");
        // TODO: 对接潘玺名审核记录表，写入下架原因 body.get("reason")
        return ApiResponse.success(newsService.updateNews(news));
    }
}