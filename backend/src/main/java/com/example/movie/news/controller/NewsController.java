package com.example.movie.news.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.movie.common.response.ApiResponse;
import com.example.movie.common.response.PageResult;
import com.example.movie.news.entity.NewsArticle;
import com.example.movie.news.entity.NewsRelation;
import com.example.movie.news.service.NewsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    /**
     * GET /api/news?page=1&pageSize=10&keyword=xxx&category=xxx&movieId=1
     */
    @GetMapping
    public ApiResponse<PageResult<NewsArticle>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Long movieId
    ) {
        IPage<NewsArticle> result = newsService.getNewsList(page, pageSize, keyword, category, movieId);
        return ApiResponse.success(new PageResult<>(
                result.getRecords(),
                (int) result.getCurrent(),
                (int) result.getSize(),
                result.getTotal()
        ));
    }

    /**
     * GET /api/news/{newsId}
     */
    @GetMapping("/{newsId}")
    public ApiResponse<NewsArticle> detail(@PathVariable Long newsId) {
        NewsArticle news = newsService.getNewsDetail(newsId);
        if (news == null) {
            return ApiResponse.error(404, "资讯不存在");
        }
        return ApiResponse.success(news);
    }

    /**
     * GET /api/news/{newsId}/relations
     */
    @GetMapping("/{newsId}/relations")
    public ApiResponse<List<NewsRelation>> relations(@PathVariable Long newsId) {
        return ApiResponse.success(newsService.getNewsRelations(newsId));
    }
}