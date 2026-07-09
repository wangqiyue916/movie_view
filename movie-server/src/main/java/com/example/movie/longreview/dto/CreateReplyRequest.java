package com.example.movie.longreview.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateReplyRequest {

    private Long parentId;

    @NotBlank(message = "回复内容不能为空")
    @Size(max = 1000, message = "回复内容最长1000字符")
    private String content;
}