package com.example.movie.longreview.dto;

import lombok.Data;

@Data
public class ReviewListQuery {

    private Long movieId;

    private String sortBy = "createdAt";

    private Integer page = 1;

    private Integer pageSize = 10;
}