package com.example.movie.news.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.movie.news.entity.NewsFavorite;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NewsFavoriteMapper extends BaseMapper<NewsFavorite> {
}