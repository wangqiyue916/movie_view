package com.example.movie.news.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.movie.news.entity.NewsFavorite;
import com.example.movie.news.entity.NewsLike;
import com.example.movie.news.mapper.NewsFavoriteMapper;
import com.example.movie.news.mapper.NewsLikeMapper;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class NewsInteractionService {

    private final NewsLikeMapper newsLikeMapper;
    private final NewsFavoriteMapper newsFavoriteMapper;

    public NewsInteractionService(NewsLikeMapper newsLikeMapper, NewsFavoriteMapper newsFavoriteMapper) {
        this.newsLikeMapper = newsLikeMapper;
        this.newsFavoriteMapper = newsFavoriteMapper;
    }

    /**
     * 切换点赞状态：已点赞则取消，未点赞则点赞
     */
    public boolean toggleLike(Long newsId, Long userId) {
        NewsLike existing = newsLikeMapper.selectOne(
                new LambdaQueryWrapper<NewsLike>()
                        .eq(NewsLike::getNewsId, newsId)
                        .eq(NewsLike::getUserId, userId)
        );
        if (existing != null) {
            newsLikeMapper.deleteById(existing.getId());
            return false; // 已取消点赞
        }
        NewsLike like = new NewsLike();
        like.setNewsId(newsId);
        like.setUserId(userId);
        newsLikeMapper.insert(like);
        return true; // 已点赞
    }

    /**
     * 切换收藏状态
     */
    public boolean toggleFavorite(Long newsId, Long userId) {
        NewsFavorite existing = newsFavoriteMapper.selectOne(
                new LambdaQueryWrapper<NewsFavorite>()
                        .eq(NewsFavorite::getNewsId, newsId)
                        .eq(NewsFavorite::getUserId, userId)
        );
        if (existing != null) {
            newsFavoriteMapper.deleteById(existing.getId());
            return false;
        }
        NewsFavorite fav = new NewsFavorite();
        fav.setNewsId(newsId);
        fav.setUserId(userId);
        newsFavoriteMapper.insert(fav);
        return true;
    }

    /**
     * 获取点赞数和当前用户是否点赞
     */
    public Map<String, Object> getLikeStatus(Long newsId, Long userId) {
        long count = newsLikeMapper.selectCount(
                new LambdaQueryWrapper<NewsLike>().eq(NewsLike::getNewsId, newsId)
        );
        boolean liked = newsLikeMapper.exists(
                new LambdaQueryWrapper<NewsLike>()
                        .eq(NewsLike::getNewsId, newsId)
                        .eq(NewsLike::getUserId, userId)
        );
        return Map.of("likeCount", count, "isLiked", liked);
    }

    /**
     * 获取用户是否收藏
     */
    public Map<String, Object> getFavoriteStatus(Long newsId, Long userId) {
        boolean favorited = newsFavoriteMapper.exists(
                new LambdaQueryWrapper<NewsFavorite>()
                        .eq(NewsFavorite::getNewsId, newsId)
                        .eq(NewsFavorite::getUserId, userId)
        );
        return Map.of("isFavorited", favorited);
    }
}