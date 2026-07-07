package com.example.movie.longreview.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("reports")
public class ReportRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long reporterId;

    private String targetType;

    private Long targetId;

    private String reason;

    private String status;

    private Long handlerId;

    private String handleResult;

    private LocalDateTime handledAt;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
