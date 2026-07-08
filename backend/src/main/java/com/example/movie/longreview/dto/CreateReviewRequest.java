package com.example.movie.longreview.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class CreateReviewRequest {

    @NotNull(message = "电影ID不能为空")
    private Long movieId;

    @NotBlank(message = "标题不能为空")
    @Size(max = 100, message = "标题最长100字符")
    private String title;

    @NotBlank(message = "内容不能为空")
    private String contentMd;

    private String coverUrl;

    private List<String> images;
}