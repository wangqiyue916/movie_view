package com.example.movie.longreview.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReviewReplyVO {

    private Long id;

    private Long reviewId;

    private Long userId;

    private String userNickname;

    private String userAvatar;

    private Long parentId;

    private String content;

    private String status;

    private Integer likeCount;

    private Integer reportCount;

    private Boolean liked;

    private List<ReviewReplyVO> children;

    private LocalDateTime createdAt;
}