package com.example.movie.movie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.movie.movie.entity.MovieRatingEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.math.BigDecimal;

@Mapper
public interface MovieRatingMapper extends BaseMapper<MovieRatingEntity> {

    @Select("SELECT COALESCE(AVG(total_score), 0) FROM movie_ratings WHERE movie_id = #{movieId}")
    BigDecimal avgTotalScore(@Param("movieId") Long movieId);

    @Select("SELECT COALESCE(AVG(story_score), 0) FROM movie_ratings WHERE movie_id = #{movieId} AND story_score IS NOT NULL")
    BigDecimal avgStoryScore(@Param("movieId") Long movieId);

    @Select("SELECT COALESCE(AVG(visual_score), 0) FROM movie_ratings WHERE movie_id = #{movieId} AND visual_score IS NOT NULL")
    BigDecimal avgVisualScore(@Param("movieId") Long movieId);

    @Select("SELECT COALESCE(AVG(acting_score), 0) FROM movie_ratings WHERE movie_id = #{movieId} AND acting_score IS NOT NULL")
    BigDecimal avgActingScore(@Param("movieId") Long movieId);

    @Select("SELECT COUNT(*) FROM movie_ratings WHERE movie_id = #{movieId}")
    long countByMovieId(@Param("movieId") Long movieId);
}
