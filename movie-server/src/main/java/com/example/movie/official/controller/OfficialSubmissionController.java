package com.example.movie.official.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.movie.common.config.LoginUserContext;
import com.example.movie.common.response.ApiResponse;
import com.example.movie.news.entity.NewsArticle;
import com.example.movie.news.entity.NewsRelation;
import com.example.movie.news.mapper.NewsArticleMapper;
import com.example.movie.news.mapper.NewsRelationMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/official")
public class OfficialSubmissionController {

    private final NewsArticleMapper newsArticleMapper;
    private final NewsRelationMapper newsRelationMapper;

    public OfficialSubmissionController(NewsArticleMapper newsArticleMapper,
                                         NewsRelationMapper newsRelationMapper) {
        this.newsArticleMapper = newsArticleMapper;
        this.newsRelationMapper = newsRelationMapper;
    }

    /** 提交资讯投稿 */
    @PostMapping("/submissions/news")
    @Transactional
    public ApiResponse<Map<String, Object>> submitNews(@RequestBody Map<String, Object> body) {
        Long userId = LoginUserContext.getUserId();
        if (userId == null) return ApiResponse.error(401, "请先登录");

        // 创建资讯
        NewsArticle news = new NewsArticle();
        news.setTitle((String) body.getOrDefault("title", ""));
        news.setSummary((String) body.getOrDefault("summary", ""));
        news.setContent((String) body.getOrDefault("content", ""));
        news.setCoverUrl((String) body.getOrDefault("coverUrl", ""));
        news.setCategory((String) body.getOrDefault("category", ""));
        news.setSource((String) body.getOrDefault("source", "电影商投稿"));
        news.setAuthorId(userId);
        String reqStatus = body.getOrDefault("status", "PENDING") instanceof String ?
                (String) body.get("status") : String.valueOf(body.getOrDefault("status", "PENDING"));
        news.setStatus("DRAFT".equals(reqStatus) ? "DRAFT" : "PENDING"); // 支持草稿和直接提交
        news.setViewCount(0L);
        news.setIsHot(0);
        news.setCreatedAt(LocalDateTime.now());
        news.setUpdatedAt(LocalDateTime.now());
        newsArticleMapper.insert(news);

        // 保存关联信息（电影、导演、演员等）
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> relations = (List<Map<String, Object>>) body.getOrDefault("relations", new ArrayList<>());
        for (Map<String, Object> rel : relations) {
            NewsRelation nr = new NewsRelation();
            nr.setNewsId(news.getId());
            nr.setTargetType((String) rel.getOrDefault("targetType", ""));
            nr.setTargetId(rel.get("targetId") != null ? ((Number) rel.get("targetId")).longValue() : null);
            nr.setTargetName((String) rel.getOrDefault("targetName", ""));
            nr.setCreatedAt(LocalDateTime.now());
            newsRelationMapper.insert(nr);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", news.getId());
        result.put("message", "投稿已提交，等待管理员审核");
        return ApiResponse.success(result);
    }

    /** 获取我的投稿列表 */
    @GetMapping("/submissions")
    public ApiResponse<Map<String, Object>> listMySubmissions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Long userId = LoginUserContext.getUserId();
        if (userId == null) return ApiResponse.error(401, "请先登录");

        LambdaQueryWrapper<NewsArticle> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NewsArticle::getAuthorId, userId);
        wrapper.orderByDesc(NewsArticle::getCreatedAt);

        Page<NewsArticle> result = newsArticleMapper.selectPage(new Page<>(page, pageSize), wrapper);

        List<Map<String, Object>> items = new ArrayList<>();
        for (NewsArticle n : result.getRecords()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", n.getId());
            item.put("title", n.getTitle());
            item.put("status", n.getStatus());
            item.put("category", n.getCategory());
            item.put("createdAt", n.getCreatedAt());
            items.add(item);
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("list", items);
        response.put("page", page);
        response.put("pageSize", pageSize);
        response.put("total", result.getTotal());
        return ApiResponse.success(response);
    }
}