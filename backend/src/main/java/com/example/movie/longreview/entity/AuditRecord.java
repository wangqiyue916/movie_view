package com.example.movie.longreview.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("audit_records")
public class AuditRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String targetType;

    private Long targetId;

    private Long submitterId;

    private Long auditorId;

    private String action;

    private String beforeStatus;

    private String afterStatus;

    private String rejectReason;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
