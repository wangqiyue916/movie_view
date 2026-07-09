package com.example.movie.report.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.movie.common.response.ApiResponse;
import com.example.movie.common.response.PageResult;
import com.example.movie.longreview.entity.ReportRecord;
import com.example.movie.longreview.mapper.ReportRecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportRecordMapper reportRecordMapper;

    @GetMapping
    public ApiResponse<Map<String, Object>> pageReports(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {

        LambdaQueryWrapper<ReportRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(ReportRecord::getCreatedAt);

        if (status != null && !status.isEmpty()) {
            wrapper.eq(ReportRecord::getStatus, status);
        }

        Page<ReportRecord> mpPage = new Page<>(page, pageSize);
        Page<ReportRecord> result = reportRecordMapper.selectPage(mpPage, wrapper);

        // Also check pending count
        long pendingCount = reportRecordMapper.selectCount(
            new LambdaQueryWrapper<ReportRecord>().eq(ReportRecord::getStatus, "PENDING"));

        Map<String, Object> data = Map.of(
            "list", result.getRecords(),
            "page", (int) result.getCurrent(),
            "pageSize", (int) result.getSize(),
            "total", result.getTotal(),
            "pendingCount", pendingCount
        );
        return ApiResponse.success(data);
    }

    @PostMapping("/{id}/resolve")
    public ApiResponse<Void> resolve(@PathVariable Long id) {
        ReportRecord record = reportRecordMapper.selectById(id);
        if (record != null) {
            record.setStatus("RESOLVED");
            record.setHandledAt(LocalDateTime.now());
            record.setHandlerId(1L);
            record.setHandleResult("标记为已处理");
            reportRecordMapper.updateById(record);
        }
        return ApiResponse.success();
    }

    @PostMapping("/{id}/reject")
    public ApiResponse<Void> reject(@PathVariable Long id) {
        ReportRecord record = reportRecordMapper.selectById(id);
        if (record != null) {
            record.setStatus("REJECTED");
            record.setHandledAt(LocalDateTime.now());
            record.setHandlerId(1L);
            record.setHandleResult("驳回举报");
            reportRecordMapper.updateById(record);
        }
        return ApiResponse.success();
    }
}