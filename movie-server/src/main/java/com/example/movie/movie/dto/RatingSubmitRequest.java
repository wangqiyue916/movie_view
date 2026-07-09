package com.example.movie.movie.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record RatingSubmitRequest(
        @NotNull(message = "总评分不能为空")
        @DecimalMin(value = "0.5", message = "评分不能低于0.5")
        @DecimalMax(value = "10.0", message = "评分不能超过10.0")
        BigDecimal totalScore,
        BigDecimal storyScore,
        BigDecimal visualScore,
        BigDecimal actingScore
) {}
