package com.example.movie.movie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.movie.movie.dto.RatingSubmitRequest;
import com.example.movie.movie.entity.MovieEntity;
import com.example.movie.movie.entity.MovieRatingEntity;
import com.example.movie.movie.mapper.MovieMapper;
import com.example.movie.movie.mapper.MovieRatingMapper;
import com.example.movie.movie.service.RatingService;
import com.example.movie.movie.vo.RatingSummaryVO;
import com.example.movie.movie.vo.RatingVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;

@Service
public class RatingServiceImpl implements RatingService {

    private final MovieRatingMapper ratingMapper;
    private final MovieMapper movieMapper;

    public RatingServiceImpl(MovieRatingMapper ratingMapper, MovieMapper movieMapper) {
        this.ratingMapper = ratingMapper;
        this.movieMapper = movieMapper;
    }

    @Override
    @Transactional
    public void submitRating(Long movieId, Long userId, RatingSubmitRequest request) {
        MovieRatingEntity existing = ratingMapper.selectOne(
                new QueryWrapper<MovieRatingEntity>()
                        .eq("movie_id", movieId)
                        .eq("user_id", userId));

        if (existing != null) {
            existing.setTotalScore(request.totalScore());
            existing.setStoryScore(request.storyScore());
            existing.setVisualScore(request.visualScore());
            existing.setActingScore(request.actingScore());
            ratingMapper.updateById(existing);
        } else {
            MovieRatingEntity rating = new MovieRatingEntity();
            rating.setMovieId(movieId);
            rating.setUserId(userId);
            rating.setTotalScore(request.totalScore());
            rating.setStoryScore(request.storyScore());
            rating.setVisualScore(request.visualScore());
            rating.setActingScore(request.actingScore());
            ratingMapper.insert(rating);
        }

        recalculateMovieScores(movieId);
    }

    @Override
    public RatingSummaryVO getRatingSummary(Long movieId) {
        MovieEntity movie = movieMapper.selectById(movieId);
        if (movie == null) return null;
        return new RatingSummaryVO(
                movie.getAvgTotalScore(), movie.getAvgStoryScore(),
                movie.getAvgVisualScore(), movie.getAvgActingScore(),
                movie.getRatingCount());
    }

    @Override
    public RatingVO getMyRating(Long movieId, Long userId) {
        MovieRatingEntity rating = ratingMapper.selectOne(
                new QueryWrapper<MovieRatingEntity>()
                        .eq("movie_id", movieId)
                        .eq("user_id", userId));
        if (rating == null) return null;
        return new RatingVO(
                rating.getId(), rating.getMovieId(), rating.getUserId(),
                rating.getTotalScore(), rating.getStoryScore(),
                rating.getVisualScore(), rating.getActingScore(),
                rating.getCreatedAt());
    }

    private void recalculateMovieScores(Long movieId) {
        long count = ratingMapper.countByMovieId(movieId);
        MovieEntity movie = new MovieEntity();
        movie.setId(movieId);
        movie.setRatingCount((int) count);
        movie.setAvgTotalScore(ratingMapper.avgTotalScore(movieId).setScale(1, RoundingMode.HALF_UP));
        movie.setAvgStoryScore(ratingMapper.avgStoryScore(movieId).setScale(1, RoundingMode.HALF_UP));
        movie.setAvgVisualScore(ratingMapper.avgVisualScore(movieId).setScale(1, RoundingMode.HALF_UP));
        movie.setAvgActingScore(ratingMapper.avgActingScore(movieId).setScale(1, RoundingMode.HALF_UP));
        movieMapper.updateById(movie);
    }
}
