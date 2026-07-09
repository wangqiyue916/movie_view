package com.example.movie.movie.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RatingVO(
        Long id,
        Long movieId,
        Long userId,
        BigDecimal totalScore,
        BigDecimal storyScore,
        BigDecimal visualScore,
        BigDecimal actingScore,
        LocalDateTime createdAt
) {}
