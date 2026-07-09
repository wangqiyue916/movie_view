package com.example.movie.news.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.movie.news.entity.NewsArticle;
import com.example.movie.news.entity.NewsRelation;
import com.example.movie.news.mapper.NewsArticleMapper;
import com.example.movie.news.mapper.NewsRelationMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsService {

    private final NewsArticleMapper newsArticleMapper;
    private final NewsRelationMapper newsRelationMapper;

    public NewsService(NewsArticleMapper newsArticleMapper, NewsRelationMapper newsRelationMapper) {
        this.newsArticleMapper = newsArticleMapper;
        this.newsRelationMapper = newsRelationMapper;
    }

    /**
     * 分页查询已上线资讯列表
     */
    public IPage<NewsArticle> getNewsList(Integer page, Integer pageSize, String keyword, String category, Long movieId) {
        LambdaQueryWrapper<NewsArticle> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NewsArticle::getStatus, "ONLINE");

        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w.like(NewsArticle::getTitle, keyword).or().like(NewsArticle::getSummary, keyword));
        }
        if (category != null && !category.isBlank()) {
            wrapper.eq(NewsArticle::getCategory, category);
        }
        if (movieId != null) {
            List<Long> newsIds = newsRelationMapper.selectList(
                    new LambdaQueryWrapper<NewsRelation>()
                            .eq(NewsRelation::getTargetType, "MOVIE")
                            .eq(NewsRelation::getTargetId, movieId)
            ).stream().map(NewsRelation::getNewsId).toList();
            if (newsIds.isEmpty()) {
                return new Page<>(page, pageSize, 0);
            }
            wrapper.in(NewsArticle::getId, newsIds);
        }

        wrapper.orderByDesc(NewsArticle::getPublishedAt);
        return newsArticleMapper.selectPage(new Page<>(page, pageSize), wrapper);
    }

    /**
     * 查询资讯详情（含关联关系）
     */
    public NewsArticle getNewsDetail(Long newsId) {
        return newsArticleMapper.selectById(newsId);
    }

    /**
     * 获取资讯的关联信息（电影等）
     */
    public List<NewsRelation> getNewsRelations(Long newsId) {
        return newsRelationMapper.selectList(
                new LambdaQueryWrapper<NewsRelation>().eq(NewsRelation::getNewsId, newsId)
        );
    }

    /**
     * 获取热门资讯列表（用于首页轮播）
     */
    public List<NewsArticle> getHotNews(int limit) {
        LambdaQueryWrapper<NewsArticle> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NewsArticle::getStatus, "ONLINE")
                .eq(NewsArticle::getIsHot, 1)
                .orderByDesc(NewsArticle::getPublishedAt)
                .last("LIMIT " + limit);
        return newsArticleMapper.selectList(wrapper);
    }

    /**
     * 获取最新资讯（用于首页）
     */
    public List<NewsArticle> getLatestNews(int limit) {
        LambdaQueryWrapper<NewsArticle> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NewsArticle::getStatus, "ONLINE")
                .orderByDesc(NewsArticle::getPublishedAt)
                .last("LIMIT " + limit);
        return newsArticleMapper.selectList(wrapper);
    }

    /**
     * 创建资讯（管理员/电影商投稿）
     */
    public NewsArticle createNews(NewsArticle newsArticle) {
        newsArticleMapper.insert(newsArticle);
        return newsArticle;
    }

    /**
     * 更新资讯
     */
    public NewsArticle updateNews(NewsArticle newsArticle) {
        newsArticleMapper.updateById(newsArticle);
        return newsArticle;
    }

    /**
     * 删除资讯（逻辑删除）
     */
    public void deleteNews(Long newsId) {
        newsArticleMapper.deleteById(newsId);
    }
}