package com.example.movie.movie.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("movie_ratings")
public class MovieRatingEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long movieId;
    private Long userId;
    private BigDecimal totalScore;
    private BigDecimal storyScore;
    private BigDecimal visualScore;
    private BigDecimal actingScore;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getMovieId() { return movieId; }
    public void setMovieId(Long movieId) { this.movieId = movieId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public BigDecimal getTotalScore() { return totalScore; }
    public void setTotalScore(BigDecimal totalScore) { this.totalScore = totalScore; }
    public BigDecimal getStoryScore() { return storyScore; }
    public void setStoryScore(BigDecimal storyScore) { this.storyScore = storyScore; }
    public BigDecimal getVisualScore() { return visualScore; }
    public void setVisualScore(BigDecimal visualScore) { this.visualScore = visualScore; }
    public BigDecimal getActingScore() { return actingScore; }
    public void setActingScore(BigDecimal actingScore) { this.actingScore = actingScore; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
