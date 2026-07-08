package com.example.movie.news.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.movie.common.response.ApiResponse;
import com.example.movie.news.entity.HomepageRecommendation;
import com.example.movie.news.mapper.HomepageRecommendationMapper;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 管理员-首页推荐位管理
 */
@RestController
@RequestMapping("/api/admin/homepage-recommendations")
public class AdminRecommendationController {

    private final HomepageRecommendationMapper mapper;

    public AdminRecommendationController(HomepageRecommendationMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * GET /api/admin/homepage-recommendations?sectionCode=BANNER_NEWS
     */
    @GetMapping
    public ApiResponse<List<HomepageRecommendation>> list(
            @RequestParam(defaultValue = "BANNER_NEWS") String sectionCode
    ) {
        List<HomepageRecommendation> list = mapper.selectList(
                new LambdaQueryWrapper<HomepageRecommendation>()
                        .eq(HomepageRecommendation::getSectionCode, sectionCode)
                        .orderByAsc(HomepageRecommendation::getSortOrder)
        );
        return ApiResponse.success(list);
    }

    /**
     * POST /api/admin/homepage-recommendations
     */
    @PostMapping
    public ApiResponse<HomepageRecommendation> create(@RequestBody Map<String, Object> body) {
        HomepageRecommendation rec = new HomepageRecommendation();
        rec.setSectionCode((String) body.get("sectionCode"));
        rec.setTargetType((String) body.get("targetType"));
        rec.setTargetId(body.get("targetId") instanceof Integer ? ((Integer) body.get("targetId")).longValue() : (Long) body.get("targetId"));
        rec.setTitle((String) body.get("title"));
        rec.setImageUrl((String) body.get("imageUrl"));
        rec.setSortOrder(body.get("sortOrder") instanceof Integer ? (Integer) body.get("sortOrder") : 0);
        rec.setEnabled(body.get("enabled") instanceof Integer ? (Integer) body.get("enabled") : 1);
        mapper.insert(rec);
        return ApiResponse.success(rec);
    }

    /**
     * PUT /api/admin/homepage-recommendations/{id}
     */
    @PutMapping("/{id}")
    public ApiResponse<HomepageRecommendation> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        HomepageRecommendation rec = new HomepageRecommendation();
        rec.setId(id);
        if (body.containsKey("sectionCode")) rec.setSectionCode((String) body.get("sectionCode"));
        if (body.containsKey("targetType")) rec.setTargetType((String) body.get("targetType"));
        if (body.containsKey("targetId")) rec.setTargetId(body.get("targetId") instanceof Integer ? ((Integer) body.get("targetId")).longValue() : (Long) body.get("targetId"));
        if (body.containsKey("title")) rec.setTitle((String) body.get("title"));
        if (body.containsKey("imageUrl")) rec.setImageUrl((String) body.get("imageUrl"));
        if (body.containsKey("sortOrder")) rec.setSortOrder(body.get("sortOrder") instanceof Integer ? (Integer) body.get("sortOrder") : null);
        if (body.containsKey("enabled")) rec.setEnabled(body.get("enabled") instanceof Integer ? (Integer) body.get("enabled") : null);
        mapper.updateById(rec);
        return ApiResponse.success(rec);
    }

    /**
     * DELETE /api/admin/homepage-recommendations/{id}
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        mapper.deleteById(id);
        return ApiResponse.success();
    }
}