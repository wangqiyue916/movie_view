package com.example.movie.common.response;

import java.util.List;

public record PageResult<T>(
        List<T> list,
        Integer page,
        Integer pageSize,
        Long total
) {
    public static <T> PageResult<T> empty(Integer page, Integer pageSize) {
        return new PageResult<>(List.of(), page, pageSize, 0L);
    }
}

