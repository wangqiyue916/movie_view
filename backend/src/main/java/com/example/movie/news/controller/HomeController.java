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

import java.time.LocalDateTime;
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
     * 获取指定区域的推荐位列表
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

        // 轮播资讯（数据库有则查数据库，无则 Mock）
        List<com.example.movie.news.entity.NewsArticle> bannerNews = newsService.getHotNews(4);
        data.put("bannerNews", bannerNews.isEmpty() ? buildMockNews(4) : bannerNews);

        // 最新资讯 top 8（数据库有则查数据库，无则 Mock）
        List<com.example.movie.news.entity.NewsArticle> latestNews = newsService.getLatestNews(8);
        data.put("latestNews", latestNews.isEmpty() ? buildMockNews(8) : latestNews);

        data.put("hotMovies", buildHotMovies());

        data.put("topRatedMovies", buildTopRatedMovies());

        data.put("latestMovies", buildLatestMovies());

        // 优质长评：优先使用长评模块真实精选数据，无数据时降级 Mock
        List<Map<String, Object>> featuredReviews = buildFeaturedReviews();
        data.put("featuredReviews", featuredReviews.isEmpty() ? buildMockFeaturedReviews() : featuredReviews);

        // TODO: 对接周秋宏 - 推荐周边 Mock
        data.put("recommendedMerchandise", buildMockMerchandise());

        return ApiResponse.success(data);
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
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> r1 = new LinkedHashMap<>();
        r1.put("id", 1L);
        r1.put("title", "穿越星际之后，仍然回到人的情感");
        r1.put("excerpt", "它最动人的地方，是把宏大的宇宙尺度和具体的人之间重新连接起来。");
        r1.put("author", "影评人 Mori");
        r1.put("date", "2026-07-06");
        r1.put("likes", 32);
        r1.put("comments", 5);
        list.add(r1);

        Map<String, Object> r2 = new LinkedHashMap<>();
        r2.put("id", 2L);
        r2.put("title", "梦境不是谜题，而是欲望的回声");
        r2.put("excerpt", "真正让人反复回看的，并不只是结构，还有每一层梦境背后未被说破的执念。");
        r2.put("author", "用户 北辰");
        r2.put("date", "2026-07-05");
        r2.put("likes", 24);
        r2.put("comments", 7);
        list.add(r2);

        Map<String, Object> r3 = new LinkedHashMap<>();
        r3.put("id", 3L);
        r3.put("title", "灾难片里的群像，为什么仍然能打动人");
        r3.put("excerpt", "当宏大工程、末日危机和个体选择并置时，电影真正要讨论的不是奇观本身。");
        r3.put("author", "用户 山止川行");
        r3.put("date", "2026-07-04");
        r3.put("likes", 18);
        r3.put("comments", 3);
        list.add(r3);

        return list;
    }

    private List<Map<String, Object>> buildFeaturedReviews() {
        Page<FeaturedReviewVO> page = longReviewMapper.selectFeaturedReviews(new Page<LongReview>(1, 3));
        List<Map<String, Object>> list = new ArrayList<>();
        for (FeaturedReviewVO review : page.getRecords()) {
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

    /**
     * Mock 资讯数据（数据库无数据时的降级方案）
     */
    private List<com.example.movie.news.entity.NewsArticle> buildMockNews(int count) {
        String[][] mockData = {
            {"新片动态", "暑期档科幻电影热度持续升温", "多部科幻题材影片带动观影讨论，视觉效果、叙事表达与人物塑造成为关注焦点。", "https://images.unsplash.com/photo-1485846234645-a62644f84728?auto=format&fit=crop&w=900&q=85"},
            {"平台活动", "经典高分电影长评征集活动开启", "平台将根据点赞数、收藏数和回复数推荐优质长评，鼓励更深入的电影讨论。", "https://images.unsplash.com/photo-1524985069026-dd778a71c7b4?auto=format&fit=crop&w=900&q=85"},
            {"票房观察", "本周口碑片单带动二刷热度", "高分影片的长线表现正在回暖，讨论度集中在角色关系、主题表达和视听风格。", "https://images.unsplash.com/photo-1523207911345-32501502db22?auto=format&fit=crop&w=900&q=85"},
            {"幕后花絮", "导演特辑公开多场关键戏拍摄细节", "主创团队分享场景搭建、镜头调度和音乐设计，让观众更深入理解影片创作过程。", "https://images.unsplash.com/photo-1497032628192-86f99bcd76bc?auto=format&fit=crop&w=900&q=85"},
            {"演员动态", "多位主演新片计划进入筹备阶段", "演员阵容、角色设定和类型方向陆续曝光，相关话题持续登上讨论榜。", "https://images.unsplash.com/photo-1516280440614-37939bbacd81?auto=format&fit=crop&w=900&q=85"},
            {"获奖信息", "年度电影奖项公布入围名单", "剧情片、科幻片和动画片竞争激烈，摄影、美术和原创音乐单元关注度提升。", "https://images.unsplash.com/photo-1460881680858-30d872d5b530?auto=format&fit=crop&w=900&q=85"},
            {"行业观察", "流媒体与院线窗口期继续调整", "多平台尝试新的发行节奏，观众观影习惯和影片宣发策略都在发生变化。", "https://images.unsplash.com/photo-1512070679279-8988d32161be?auto=format&fit=crop&w=900&q=85"},
            {"周边资讯", "热门电影主题周边开启预售", "海报、徽章、角色模型和限定文创陆续上架，收藏向商品受到影迷关注。", "https://images.unsplash.com/photo-1462331940025-496dfbfc7564?auto=format&fit=crop&w=900&q=85"},
        };

        List<com.example.movie.news.entity.NewsArticle> list = new ArrayList<>();
        for (int i = 0; i < count && i < mockData.length; i++) {
            com.example.movie.news.entity.NewsArticle news = new com.example.movie.news.entity.NewsArticle();
            news.setId((long) (i + 1));
            news.setCategory(mockData[i][0]);
            news.setTitle(mockData[i][1]);
            news.setSummary(mockData[i][2]);
            news.setCoverUrl(mockData[i][3]);
            news.setSource("平台编辑");
            news.setViewCount(1000L + i * 500);
            news.setPublishedAt(LocalDateTime.now().minusDays(i));
            list.add(news);
        }
        return list;
    }
}
