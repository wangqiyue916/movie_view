package com.example.movie.movie.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MovieUpdateRequest(
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
        String posterUrl
) {}
