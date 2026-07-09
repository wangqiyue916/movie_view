package com.example.movie.news.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.movie.common.response.ApiResponse;
import com.example.movie.longreview.entity.LongReview;
import com.example.movie.longreview.mapper.LongReviewMapper;
import com.example.movie.longreview.vo.FeaturedReviewVO;
import com.example.movie.movie.entity.MovieEntity;
import com.example.movie.movie.mapper.MovieMapper;
import com.example.movie.news.entity.HomepageRecommendation;
import com.example.movie.news.mapper.HomepageRecommendationMapper;
import com.example.movie.news.service.NewsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/home")
public class HomeController {

    private final NewsService newsService;
    private final HomepageRecommendationMapper recommendationMapper;
    private final LongReviewMapper longReviewMapper;
    private final MovieMapper movieMapper;

    public HomeController(NewsService newsService,
                          HomepageRecommendationMapper recommendationMapper,
                          LongReviewMapper longReviewMapper,
                          MovieMapper movieMapper) {
        this.newsService = newsService;
        this.recommendationMapper = recommendationMapper;
        this.longReviewMapper = longReviewMapper;
        this.movieMapper = movieMapper;
    }

    /**
     * GET /api/home/recommendations?sectionCode=BANNER_NEWS
     * 获取指定区域的推荐位列表（供管理员后台使用）
     */
    @GetMapping("/recommendations")
    public ApiResponse<List<HomepageRecommendation>> recommendations(
            @RequestParam(defaultValue = "BANNER_NEWS") String sectionCode
    ) {
        List<HomepageRecommendation> list = recommendationMapper.selectList(
                new LambdaQueryWrapper<HomepageRecommendation>()
                        .eq(HomepageRecommendation::getSectionCode, sectionCode)
                        .eq(HomepageRecommendation::getEnabled, 1)
                        .orderByAsc(HomepageRecommendation::getSortOrder)
        );
        return ApiResponse.success(list);
    }

    /**
     * GET /api/home
     * 首页聚合数据：轮播资讯 + 最新资讯 + 数据库电影 + 优质长评 + 推荐周边
     */
    @GetMapping
    public ApiResponse<Map<String, Object>> homeData() {
        Map<String, Object> data = new LinkedHashMap<>();

        // ===== 顶部轮播：从管理员推荐位获取电影，无配置时自动使用热门电影 =====
        List<Map<String, Object>> bannerRecs = buildBannerRecsFromRecommendations();
        data.put("bannerRecs", bannerRecs);

        // 最新资讯 top 4（数据库有则查数据库，无则不展示）
        List<com.example.movie.news.entity.NewsArticle> latestNews = newsService.getLatestNews(4);
        data.put("latestNews", latestNews.isEmpty() ? List.of() : latestNews);

        data.put("hotMovies", buildHotMovies());

        data.put("topRatedMovies", buildTopRatedMovies());

        data.put("latestMovies", buildLatestMovies());

        // 优质长评：优先使用长评模块真实精选数据，无数据时降级 Mock
        List<Map<String, Object>> featuredReviews = buildFeaturedReviews();
        data.put("featuredReviews", featuredReviews.isEmpty() ? buildMockFeaturedReviews() : featuredReviews);

        data.put("recommendedMerchandise", buildMockMerchandise());

        return ApiResponse.success(data);
    }

    /** 从 homepage_recommendations 表读取 BANNER_NEWS 推荐位，组装轮播卡片 */
    private List<Map<String, Object>> buildBannerRecsFromRecommendations() {
        List<HomepageRecommendation> recs = recommendationMapper.selectList(
            new LambdaQueryWrapper<HomepageRecommendation>()
                .eq(HomepageRecommendation::getSectionCode, "BANNER_NEWS")
                .eq(HomepageRecommendation::getEnabled, 1)
                .orderByAsc(HomepageRecommendation::getSortOrder)
        );
        // 只取类型为 MOVIE 的推荐位；管理员未配置时自动用热门电影
        List<HomepageRecommendation> movieRecs = recs.stream()
            .filter(r -> "MOVIE".equals(r.getTargetType()))
            .toList();

        if (movieRecs.isEmpty()) {
            List<MovieEntity> movies = movieMapper.selectList(
                new LambdaQueryWrapper<MovieEntity>().eq(MovieEntity::getStatus, "ONLINE").last("LIMIT 4"));
            List<Map<String, Object>> cards = new ArrayList<>();
            for (MovieEntity m : movies) {
                Map<String, Object> card = new LinkedHashMap<>();
                card.put("id", m.getId());
                card.put("title", m.getTitle());
                // 处理空字符串或 null 的海报链接
                String poster = (m.getPosterUrl() == null || m.getPosterUrl().isBlank()) ? "" : m.getPosterUrl();
                card.put("image", poster);
                card.put("targetType", "MOVIE");
                cards.add(card);
            }
            return cards;
        }
        List<Map<String, Object>> cards = new ArrayList<>();
        for (HomepageRecommendation r : movieRecs) {
            Map<String, Object> card = new LinkedHashMap<>();
            card.put("id", r.getTargetId());
            card.put("title", r.getTitle() != null ? r.getTitle() : "");
            String img = (r.getImageUrl() == null || r.getImageUrl().isBlank()) ? "" : r.getImageUrl();
            card.put("image", img);
            card.put("targetType", r.getTargetType());
            cards.add(card);
        }
        return cards;
    }

    private List<Map<String, Object>> buildHotMovies() {
        return movieMapper.selectList(
                        new LambdaQueryWrapper<MovieEntity>()
                                .eq(MovieEntity::getStatus, "ONLINE")
                                .orderByDesc(MovieEntity::getViewCount)
                                .orderByDesc(MovieEntity::getRatingCount)
                                .last("LIMIT 8")
                ).stream()
                .map(this::movieItem)
                .toList();
    }

    private List<Map<String, Object>> buildTopRatedMovies() {
        return movieMapper.selectList(
                        new LambdaQueryWrapper<MovieEntity>()
                                .eq(MovieEntity::getStatus, "ONLINE")
                                .isNotNull(MovieEntity::getAvgTotalScore)
                                .orderByDesc(MovieEntity::getAvgTotalScore)
                                .orderByDesc(MovieEntity::getRatingCount)
                                .last("LIMIT 8")
                ).stream()
                .map(this::movieItem)
                .toList();
    }

    private List<Map<String, Object>> buildLatestMovies() {
        return movieMapper.selectList(
                        new LambdaQueryWrapper<MovieEntity>()
                                .eq(MovieEntity::getStatus, "ONLINE")
                                .orderByDesc(MovieEntity::getReleaseDate)
                                .orderByDesc(MovieEntity::getCreatedAt)
                                .last("LIMIT 8")
                ).stream()
                .map(this::movieItem)
                .toList();
    }

    private List<Map<String, Object>> buildMockFeaturedReviews() {
        // 数据库无长评时返回空列表，不显示 mock 数据
        return List.of();
    }

    private List<Map<String, Object>> buildFeaturedReviews() {
        try {
            Page<FeaturedReviewVO> page = longReviewMapper.selectFeaturedReviews(new Page<LongReview>(1, 3));
            if (page == null || page.getRecords() == null) return List.of();
            List<Map<String, Object>> list = new ArrayList<>();
            for (FeaturedReviewVO review : page.getRecords()) {
                if (review == null) continue;
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("id", review.getId());
                item.put("title", review.getTitle());
                item.put("excerpt", review.getSummary());
                item.put("author", review.getAuthorNickname());
                item.put("date", review.getCreatedAt() == null ? null : review.getCreatedAt().toLocalDate().toString());
                item.put("likes", review.getLikeCount());
                item.put("comments", review.getReplyCount());
                list.add(item);
            }
            return list;
        } catch (Exception e) {
            // 若精选长评查询异常（表不存在/字段缺失），降级 Mock
            return List.of();
        }
    }

    private List<Map<String, Object>> buildMockMerchandise() {
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(merchItem(1L, "星际穿越主题海报", "https://images.unsplash.com/photo-1462331940025-496dfbfc7564?auto=format&fit=crop&w=500&q=85", "￥39.9"));
        list.add(merchItem(2L, "盗梦空间陀螺模型", "https://images.unsplash.com/photo-1460881680858-30d872d5b530?auto=format&fit=crop&w=500&q=85", "￥89.0"));
        list.add(merchItem(3L, "流浪地球2金属书签", "https://images.unsplash.com/photo-1512070679279-8988d32161be?auto=format&fit=crop&w=500&q=85", "￥29.9"));
        return list;
    }

    private Map<String, Object> movieItem(MovieEntity movie) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", movie.getId());
        map.put("title", movie.getTitle());
        map.put("poster", movie.getPosterUrl());
        map.put("score", movie.getAvgTotalScore() == null ? "暂无" : movie.getAvgTotalScore().toPlainString());
        return map;
    }

    private Map<String, Object> merchItem(Long id, String name, String image, String price) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", id);
        map.put("name", name);
        map.put("image", image);
        map.put("price", price);
        return map;
    }
}
