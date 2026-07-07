package com.example.movie.news.controller;

import com.example.movie.common.response.ApiResponse;
import com.example.movie.news.entity.NewsArticle;
import com.example.movie.news.service.NewsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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

    public HomeController(NewsService newsService) {
        this.newsService = newsService;
    }

    /**
     * GET /api/home
     * 首页聚合数据：轮播资讯 + 最新资讯 + Mock（热门电影/高分电影/优质长评/推荐周边）
     */
    @GetMapping
    public ApiResponse<Map<String, Object>> homeData() {
        Map<String, Object> data = new LinkedHashMap<>();

        // 轮播资讯（热门资讯 top 4）
        data.put("bannerNews", newsService.getHotNews(4));

        // 最新资讯 top 8
        data.put("latestNews", newsService.getLatestNews(8));

        // TODO: 对接王琪越 - 热门电影 Mock
        data.put("hotMovies", buildMockHotMovies());

        // TODO: 对接王琪越 - 高分电影 Mock
        data.put("topRatedMovies", buildMockTopRatedMovies());

        // TODO: 对接郭俊岑 - 优质长评 Mock
        data.put("featuredReviews", buildMockFeaturedReviews());

        // TODO: 对接周秋宏 - 推荐周边 Mock
        data.put("recommendedMerchandise", buildMockMerchandise());

        return ApiResponse.success(data);
    }

    private List<Map<String, Object>> buildMockHotMovies() {
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(movieItem(1L, "星际穿越", "https://images.unsplash.com/photo-1446776811953-b23d57bd21aa?auto=format&fit=crop&w=500&q=85", "9.4"));
        list.add(movieItem(2L, "盗梦空间", "https://images.unsplash.com/photo-1505686994434-e3cc5abf1330?auto=format&fit=crop&w=500&q=85", "9.3"));
        list.add(movieItem(3L, "流浪地球2", "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?auto=format&fit=crop&w=500&q=85", "8.3"));
        list.add(movieItem(4L, "暗夜骑士", "https://images.unsplash.com/photo-1517604931442-7e0c8ed2963c?auto=format&fit=crop&w=500&q=85", "9.0"));
        list.add(movieItem(5L, "星海回响", "https://images.unsplash.com/photo-1485846234645-a62644f84728?auto=format&fit=crop&w=500&q=85", "8.4"));
        list.add(movieItem(6L, "梦境边缘", "https://images.unsplash.com/photo-1524985069026-dd778a71c7b4?auto=format&fit=crop&w=500&q=85", "8.6"));
        return list;
    }

    private List<Map<String, Object>> buildMockTopRatedMovies() {
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(movieItem(1L, "星际穿越", "https://images.unsplash.com/photo-1446776811953-b23d57bd21aa?auto=format&fit=crop&w=500&q=85", "9.4"));
        list.add(movieItem(2L, "盗梦空间", "https://images.unsplash.com/photo-1505686994434-e3cc5abf1330?auto=format&fit=crop&w=500&q=85", "9.3"));
        list.add(movieItem(7L, "光影岁月", "https://images.unsplash.com/photo-1523207911345-32501502db22?auto=format&fit=crop&w=500&q=85", "9.2"));
        list.add(movieItem(4L, "暗夜骑士", "https://images.unsplash.com/photo-1517604931442-7e0c8ed2963c?auto=format&fit=crop&w=500&q=85", "9.0"));
        list.add(movieItem(8L, "午夜剧场", "https://images.unsplash.com/photo-1497032628192-86f99bcd76bc?auto=format&fit=crop&w=500&q=85", "7.9"));
        return list;
    }

    private List<Map<String, Object>> buildMockFeaturedReviews() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> r1 = new LinkedHashMap<>();
        r1.put("title", "穿越星际之后，仍然回到人的情感");
        r1.put("excerpt", "它最动人的地方，是把宏大的宇宙尺度和具体的人之间重新连接起来。");
        r1.put("author", "影评人 Mori");
        r1.put("date", "2026-07-06");
        r1.put("likes", 32);
        r1.put("comments", 5);
        list.add(r1);

        Map<String, Object> r2 = new LinkedHashMap<>();
        r2.put("title", "梦境不是谜题，而是欲望的回声");
        r2.put("excerpt", "真正让人反复回看的，并不只是结构，还有每一层梦境背后未被说破的执念。");
        r2.put("author", "用户 北辰");
        r2.put("date", "2026-07-05");
        r2.put("likes", 24);
        r2.put("comments", 7);
        list.add(r2);

        Map<String, Object> r3 = new LinkedHashMap<>();
        r3.put("title", "灾难片里的群像，为什么仍然能打动人");
        r3.put("excerpt", "当宏大工程、末日危机和个体选择并置时，电影真正要讨论的不是奇观本身。");
        r3.put("author", "用户 山止川行");
        r3.put("date", "2026-07-04");
        r3.put("likes", 18);
        r3.put("comments", 3);
        list.add(r3);

        return list;
    }

    private List<Map<String, Object>> buildMockMerchandise() {
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(merchItem(1L, "星际穿越主题海报", "https://images.unsplash.com/photo-1462331940025-496dfbfc7564?auto=format&fit=crop&w=500&q=85", "￥39.9"));
        list.add(merchItem(2L, "盗梦空间陀螺模型", "https://images.unsplash.com/photo-1460881680858-30d872d5b530?auto=format&fit=crop&w=500&q=85", "￥89.0"));
        list.add(merchItem(3L, "流浪地球2金属书签", "https://images.unsplash.com/photo-1512070679279-8988d32161be?auto=format&fit=crop&w=500&q=85", "￥29.9"));
        return list;
    }

    private Map<String, Object> movieItem(Long id, String title, String poster, String score) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", id);
        map.put("title", title);
        map.put("poster", poster);
        map.put("score", score);
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