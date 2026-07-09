package com.example.movie.movie.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ShortCommentSubmitRequest(
        @NotBlank(message = "短评内容不能为空")
        @Size(max = 500, message = "短评内容不能超过500字")
        String content
) {}
