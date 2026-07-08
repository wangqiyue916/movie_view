package com.example.movie.movie.vo;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MovieDetailVO(
        Long id,
        String title,
        String originalTitle,
        String director,
        String actors,
        String genres,
        LocalDate releaseDate,
        String region,
        String language,
        Integer durationMinutes,
        String synopsis,
        String posterUrl,
        String status,
        Long viewCount,
        Integer ratingCount,
        BigDecimal avgTotalScore,
        BigDecimal avgStoryScore,
        BigDecimal avgVisualScore,
        BigDecimal avgActingScore
) {}
