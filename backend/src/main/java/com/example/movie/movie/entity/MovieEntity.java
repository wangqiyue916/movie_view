package com.example.movie.movie.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@TableName("movies")
public class MovieEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String originalTitle;
    private String director;
    private String actors;
    private String genres;
    private LocalDate releaseDate;
    private String region;
    private String language;
    private Integer durationMinutes;
    private String synopsis;
    private String posterUrl;
    private String status;
    private Long officialUserId;
    private Long viewCount;
    private Integer ratingCount;
    private BigDecimal avgTotalScore;
    private BigDecimal avgStoryScore;
    private BigDecimal avgVisualScore;
    private BigDecimal avgActingScore;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @TableLogic(value = "null", delval = "now()")
    private LocalDateTime deletedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getOriginalTitle() { return originalTitle; }
    public void setOriginalTitle(String originalTitle) { this.originalTitle = originalTitle; }
    public String getDirector() { return director; }
    public void setDirector(String director) { this.director = director; }
    public String getActors() { return actors; }
    public void setActors(String actors) { this.actors = actors; }
    public String getGenres() { return genres; }
    public void setGenres(String genres) { this.genres = genres; }
    public LocalDate getReleaseDate() { return releaseDate; }
    public void setReleaseDate(LocalDate releaseDate) { this.releaseDate = releaseDate; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
    public String getSynopsis() { return synopsis; }
    public void setSynopsis(String synopsis) { this.synopsis = synopsis; }
    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getOfficialUserId() { return officialUserId; }
    public void setOfficialUserId(Long officialUserId) { this.officialUserId = officialUserId; }
    public Long getViewCount() { return viewCount; }
    public void setViewCount(Long viewCount) { this.viewCount = viewCount; }
    public Integer getRatingCount() { return ratingCount; }
    public void setRatingCount(Integer ratingCount) { this.ratingCount = ratingCount; }
    public BigDecimal getAvgTotalScore() { return avgTotalScore; }
    public void setAvgTotalScore(BigDecimal avgTotalScore) { this.avgTotalScore = avgTotalScore; }
    public BigDecimal getAvgStoryScore() { return avgStoryScore; }
    public void setAvgStoryScore(BigDecimal avgStoryScore) { this.avgStoryScore = avgStoryScore; }
    public BigDecimal getAvgVisualScore() { return avgVisualScore; }
    public void setAvgVisualScore(BigDecimal avgVisualScore) { this.avgVisualScore = avgVisualScore; }
    public BigDecimal getAvgActingScore() { return avgActingScore; }
    public void setAvgActingScore(BigDecimal avgActingScore) { this.avgActingScore = avgActingScore; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
}
