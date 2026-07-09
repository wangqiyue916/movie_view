package com.example.movie.movie.dto;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDate;

public record MovieCreateRequest(
        @NotBlank(message = "电影名称不能为空")
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
