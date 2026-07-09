package com.example.movie.news.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.movie.news.entity.NewsArticle;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NewsArticleMapper extends BaseMapper<NewsArticle> {
}