package com.example.movie.movie.vo;

import java.time.LocalDateTime;
import java.math.BigDecimal;

public record ShortCommentVO(
        Long id,
        Long movieId,
        Long userId,
        String userNickname,
        String userAvatarUrl,
        BigDecimal userTotalScore,
        String content,
        Integer likeCount,
        Integer reportCount,
        boolean likedByMe,
        LocalDateTime createdAt
) {}
