package com.example.movie.longreview.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class LongReviewVO {

    private Long id;

    private Long movieId;

    private String movieTitle;

    private Long userId;

    private String authorNickname;

    private String authorAvatar;

    private String title;

    private String contentMd;

    private String coverUrl;

    private String status;

    private Long viewCount;

    private Integer likeCount;

    private Integer favoriteCount;

    private Integer replyCount;

    private Boolean isFeatured;

    private LocalDateTime featuredAt;

    private Boolean liked;

    private Boolean favorited;

    private List<String> images;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}