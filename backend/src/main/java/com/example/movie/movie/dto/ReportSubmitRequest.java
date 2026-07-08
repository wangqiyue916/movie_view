package com.example.movie.movie.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ReportSubmitRequest(
        @NotBlank(message = "举报原因不能为空")
        @Size(max = 255, message = "举报原因不能超过255字")
        String reason
) {}
