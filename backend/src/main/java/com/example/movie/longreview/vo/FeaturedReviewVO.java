package com.example.movie.longreview.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FeaturedReviewVO {

    private Long id;

    private Long movieId;

    private String movieTitle;

    private String moviePoster;

    private Long userId;

    private String authorNickname;

    private String authorAvatar;

    private String title;

    private String summary;

    private String coverUrl;

    private Integer likeCount;

    private Integer favoriteCount;

    private Integer replyCount;

    private Long viewCount;

    private Double hotScore;

    private LocalDateTime createdAt;
}