package com.example.movie.movie.vo;

import java.math.BigDecimal;

public record RatingSummaryVO(
        BigDecimal avgTotalScore,
        BigDecimal avgStoryScore,
        BigDecimal avgVisualScore,
        BigDecimal avgActingScore,
        Integer ratingCount
) {}
