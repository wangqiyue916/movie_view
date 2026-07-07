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

import java.time.LocalDateTime;
import java.util.ArrayList;
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
        List<NewsArticle> records = result.getRecords();

        // 数据库无数据时使用 Mock，并支持关键词和分类筛选
        if (records.isEmpty()) {
            records = buildMockNewsList();
            if (keyword != null && !keyword.isBlank()) {
                records = records.stream()
                        .filter(n -> n.getTitle().contains(keyword) || n.getSummary().contains(keyword))
                        .toList();
            }
            if (category != null && !category.isBlank()) {
                records = records.stream()
                        .filter(n -> category.equals(n.getCategory()))
                        .toList();
            }
        }

        return ApiResponse.success(new PageResult<>(
                records,
                (int) result.getCurrent(),
                (int) result.getSize(),
                (long) records.size()
        ));
    }

    /**
     * GET /api/news/{newsId}
     */
    @GetMapping("/{newsId}")
    public ApiResponse<NewsArticle> detail(@PathVariable Long newsId) {
        NewsArticle news = newsService.getNewsDetail(newsId);
        if (news == null) {
            List<NewsArticle> mockList = buildMockNewsList();
            if (newsId >= 1 && newsId <= mockList.size()) {
                NewsArticle mock = mockList.get((int) (newsId - 1));
                mock.setViewCount(mock.getViewCount() + 1);
                return ApiResponse.success(mock);
            }
            return ApiResponse.error(404, "资讯不存在");
        }
        news.setViewCount(news.getViewCount() + 1);
        newsService.updateNews(news);
        return ApiResponse.success(news);
    }

    /**
     * GET /api/news/{newsId}/relations
     */
    @GetMapping("/{newsId}/relations")
    public ApiResponse<List<NewsRelation>> relations(@PathVariable Long newsId) {
        List<NewsRelation> rels = newsService.getNewsRelations(newsId);
        if (rels.isEmpty() && newsId >= 1 && newsId <= 8) {
            rels = buildMockRelations(newsId.intValue());
        }
        return ApiResponse.success(rels);
    }

    private List<NewsRelation> buildMockRelations(int newsId) {
        String[][] mock = {
            {"MOVIE", "1", "星际穿越"},
            {"ACTOR", "1", "马修·麦康纳"},
            {"DIRECTOR", "1", "克里斯托弗·诺兰"},
        };
        String[][] mock2 = {
            {"MOVIE", "2", "盗梦空间"},
            {"ACTOR", "2", "莱昂纳多·迪卡普里奥"},
            {"DIRECTOR", "2", "克里斯托弗·诺兰"},
        };
        String[][] data = (newsId % 2 == 1) ? mock : mock2;

        List<NewsRelation> list = new ArrayList<>();
        for (int i = 0; i < data.length; i++) {
            NewsRelation r = new NewsRelation();
            r.setId((long) (i + 1));
            r.setNewsId((long) newsId);
            r.setTargetType(data[i][0]);
            r.setTargetId(Long.parseLong(data[i][1]));
            r.setTargetName(data[i][2]);
            list.add(r);
        }
        return list;
    }

    private List<NewsArticle> buildMockNewsList() {
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

        List<NewsArticle> list = new ArrayList<>();
        for (int i = 0; i < mockData.length; i++) {
            NewsArticle news = new NewsArticle();
            news.setId((long) (i + 1));
            news.setCategory(mockData[i][0]);
            news.setTitle(mockData[i][1]);
            news.setSummary(mockData[i][2]);
            news.setCoverUrl(mockData[i][3]);
            news.setContent("<p>" + mockData[i][2] + "</p><p>这是一篇精彩的电影资讯文章，详细介绍了相关动态和背景信息。读者可以通过本文了解最新的电影行业趋势和热门话题。</p>");
            news.setSource("平台编辑");
            news.setViewCount(1000L + i * 500);
            news.setPublishedAt(LocalDateTime.now().minusDays(i));
            list.add(news);
        }
        return list;
    }
}
