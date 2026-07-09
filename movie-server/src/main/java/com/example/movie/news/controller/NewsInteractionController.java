package com.example.movie.news.controller;

import com.example.movie.common.response.ApiResponse;
import com.example.movie.news.service.NewsInteractionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/news")
public class NewsInteractionController {

    private final NewsInteractionService interactionService;

    public NewsInteractionController(NewsInteractionService interactionService) {
        this.interactionService = interactionService;
    }

    /**
     * POST /api/news/{newsId}/like?userId=1
     * 切换点赞状态
     */
    @PostMapping("/{newsId}/like")
    public ApiResponse<Map<String, Object>> toggleLike(
            @PathVariable Long newsId,
            @RequestParam(defaultValue = "1") Long userId
    ) {
        boolean liked = interactionService.toggleLike(newsId, userId);
        long count = interactionService.getLikeStatus(newsId, userId).get("likeCount") instanceof Long ? 
                (Long) interactionService.getLikeStatus(newsId, userId).get("likeCount") : 0;
        return ApiResponse.success(Map.of("liked", liked, "likeCount", count));
    }

    /**
     * POST /api/news/{newsId}/favorite?userId=1
     * 切换收藏状态
     */
    @PostMapping("/{newsId}/favorite")
    public ApiResponse<Map<String, Object>> toggleFavorite(
            @PathVariable Long newsId,
            @RequestParam(defaultValue = "1") Long userId
    ) {
        boolean favorited = interactionService.toggleFavorite(newsId, userId);
        return ApiResponse.success(Map.of("favorited", favorited));
    }

    /**
     * GET /api/news/{newsId}/like-status?userId=1
     */
    @GetMapping("/{newsId}/like-status")
    public ApiResponse<Map<String, Object>> likeStatus(
            @PathVariable Long newsId,
            @RequestParam(defaultValue = "1") Long userId
    ) {
        return ApiResponse.success(interactionService.getLikeStatus(newsId, userId));
    }

    /**
     * GET /api/news/{newsId}/favorite-status?userId=1
     */
    @GetMapping("/{newsId}/favorite-status")
    public ApiResponse<Map<String, Object>> favoriteStatus(
            @PathVariable Long newsId,
            @RequestParam(defaultValue = "1") Long userId
    ) {
        return ApiResponse.success(interactionService.getFavoriteStatus(newsId, userId));
    }
}