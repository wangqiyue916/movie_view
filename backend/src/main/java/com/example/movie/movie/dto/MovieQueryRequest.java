package com.example.movie.movie.dto;

public record MovieQueryRequest(
        String keyword,
        String genre,
        String releaseYear,
        String releasePeriod,
        String region,
        String contentType,
        Integer page,
        Integer pageSize
) {
    public MovieQueryRequest {
        if (page == null || page < 1) page = 1;
        if (pageSize == null || pageSize < 1) pageSize = 12;
    }
}
