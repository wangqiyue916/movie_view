package com.example.movie.news.controller;

import com.example.movie.common.response.ApiResponse;
import com.example.movie.news.entity.NewsArticle;
import com.example.movie.news.service.NewsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/official/news")
public class OfficialNewsController {

    private final NewsService newsService;

    public OfficialNewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    /**
     * POST /api/official/news
     * 电影商投稿资讯（提交后状态为 PENDING，对接审核流程）
     */
    @PostMapping
    public ApiResponse<NewsArticle> submit(@RequestBody Map<String, Object> body) {
        NewsArticle news = new NewsArticle();
        news.setTitle((String) body.get("title"));
        news.setSummary((String) body.get("summary"));
        news.setContent((String) body.get("content"));
        news.setCoverUrl((String) body.get("coverUrl"));
        news.setCategory((String) body.get("category"));
        news.setSource((String) body.getOrDefault("source", "电影商投稿"));
        // TODO: 对接潘玺名审核流程 - 当前直接设为 PENDING
        news.setStatus("PENDING");
        news.setIsHot(0);
        news.setViewCount(0L);
        news.setPublishedAt(null);

        return ApiResponse.success(newsService.createNews(news));
    }
}