package com.example.movie.longreview.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("long_reviews")
public class LongReview {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long movieId;

    private Long userId;

    private String title;

    private String contentMd;

    private String coverUrl;

    private String status;

    private Long viewCount;

    private Integer likeCount;

    private Integer favoriteCount;

    private Integer replyCount;

    private Integer isFeatured;

    private LocalDateTime featuredAt;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;
}