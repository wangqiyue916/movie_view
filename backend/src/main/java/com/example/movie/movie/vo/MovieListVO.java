package com.example.movie.movie.vo;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MovieListVO(
        Long id,
        String title,
        String director,
        String actors,
        String genres,
        LocalDate releaseDate,
        String region,
        String language,
        String posterUrl,
        BigDecimal avgTotalScore,
        Integer ratingCount
) {}
