package com.example.movie.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.movie.common.config.LoginUserContext;
import com.example.movie.common.response.ApiResponse;
import com.example.movie.longreview.entity.AuditRecord;
import com.example.movie.longreview.entity.LongReview;
import com.example.movie.longreview.mapper.AuditRecordMapper;
import com.example.movie.longreview.mapper.LongReviewMapper;
import com.example.movie.news.entity.NewsArticle;
import com.example.movie.news.mapper.NewsArticleMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/admin")
public class AdminAuditController {

    private final LongReviewMapper longReviewMapper;
    private final NewsArticleMapper newsArticleMapper;
    private final AuditRecordMapper auditRecordMapper;

    public AdminAuditController(LongReviewMapper longReviewMapper,
                                 NewsArticleMapper newsArticleMapper,
                                 AuditRecordMapper auditRecordMapper) {
        this.longReviewMapper = longReviewMapper;
        this.newsArticleMapper = newsArticleMapper;
        this.auditRecordMapper = auditRecordMapper;
    }

    /** Get audit list for a target type */
    @GetMapping("/audits")
    public ApiResponse<Map<String, Object>> listAudits(
            @RequestParam(defaultValue = "LONG_REVIEW") String targetType,
            @RequestParam(defaultValue = "PENDING") String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {

        List<Map<String, Object>> items = new ArrayList<>();
        long total = 0;

        if ("LONG_REVIEW".equals(targetType)) {
            LambdaQueryWrapper<LongReview> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(LongReview::getStatus, status);
            wrapper.orderByDesc(LongReview::getCreatedAt);
            Page<LongReview> result = longReviewMapper.selectPage(new Page<>(page, pageSize), wrapper);
            for (LongReview r : result.getRecords()) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("id", r.getId());
                item.put("targetType", "LONG_REVIEW");
                item.put("title", r.getTitle());
                item.put("status", r.getStatus());
                item.put("createdAt", r.getCreatedAt());
                items.add(item);
            }
            total = result.getTotal();
        } else if ("NEWS".equals(targetType)) {
            LambdaQueryWrapper<NewsArticle> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(NewsArticle::getStatus, status);
            wrapper.orderByDesc(NewsArticle::getCreatedAt);
            Page<NewsArticle> result = newsArticleMapper.selectPage(new Page<>(page, pageSize), wrapper);
            for (NewsArticle n : result.getRecords()) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("id", n.getId());
                item.put("targetType", "NEWS");
                item.put("title", n.getTitle());
                item.put("status", n.getStatus());
                item.put("createdAt", n.getCreatedAt());
                items.add(item);
            }
            total = result.getTotal();
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("list", items);
        result.put("page", page);
        result.put("pageSize", pageSize);
        result.put("total", total);
        return ApiResponse.success(result);
    }

    /** Approve */
    @PostMapping("/audits/{targetType}/{targetId}/approve")
    @Transactional
    public ApiResponse<String> approveAudit(@PathVariable String targetType, @PathVariable Long targetId) {
        Long auditorId = LoginUserContext.getUserId();
        String afterStatus = "LONG_REVIEW".equals(targetType) ? "APPROVED" : "ONLINE";

        if ("LONG_REVIEW".equals(targetType)) {
            LongReview item = longReviewMapper.selectById(targetId);
            if (item == null) return ApiResponse.error(404, "内容不存在");
            if (!"PENDING".equals(item.getStatus())) return ApiResponse.error(400, "当前状态不允许审核通过");
            item.setStatus(afterStatus);
            item.setUpdatedAt(LocalDateTime.now());
            longReviewMapper.updateById(item);
        } else if ("NEWS".equals(targetType)) {
            NewsArticle item = newsArticleMapper.selectById(targetId);
            if (item == null) return ApiResponse.error(404, "内容不存在");
            if (!"PENDING".equals(item.getStatus())) return ApiResponse.error(400, "当前状态不允许审核通过");
            item.setStatus(afterStatus);
            item.setPublishedAt(LocalDateTime.now());
            item.setUpdatedAt(LocalDateTime.now());
            newsArticleMapper.updateById(item);
        }

        AuditRecord record = new AuditRecord();
        record.setTargetType(targetType);
        record.setTargetId(targetId);
        record.setAuditorId(auditorId);
        record.setAction("APPROVE");
        record.setAfterStatus(afterStatus);
        record.setCreatedAt(LocalDateTime.now());
        auditRecordMapper.insert(record);

        return ApiResponse.success("审核通过");
    }

    /** Reject */
    @PostMapping("/audits/{targetType}/{targetId}/reject")
    @Transactional
    public ApiResponse<String> rejectAudit(@PathVariable String targetType, @PathVariable Long targetId,
                                            @RequestBody Map<String, String> body) {
        Long auditorId = LoginUserContext.getUserId();
        String reason = body.getOrDefault("rejectReason", "");
        if (reason.isEmpty()) return ApiResponse.error(400, "驳回原因不能为空");

        if ("LONG_REVIEW".equals(targetType)) {
            LongReview item = longReviewMapper.selectById(targetId);
            if (item == null) return ApiResponse.error(404, "内容不存在");
            if (!"PENDING".equals(item.getStatus())) return ApiResponse.error(400, "当前状态不允许驳回");
            item.setStatus("REJECTED");
            item.setUpdatedAt(LocalDateTime.now());
            longReviewMapper.updateById(item);
        } else if ("NEWS".equals(targetType)) {
            NewsArticle item = newsArticleMapper.selectById(targetId);
            if (item == null) return ApiResponse.error(404, "内容不存在");
            if (!"PENDING".equals(item.getStatus())) return ApiResponse.error(400, "当前状态不允许驳回");
            item.setStatus("REJECTED");
            item.setUpdatedAt(LocalDateTime.now());
            newsArticleMapper.updateById(item);
        }

        AuditRecord record = new AuditRecord();
        record.setTargetType(targetType);
        record.setTargetId(targetId);
        record.setAuditorId(auditorId);
        record.setAction("REJECT");
        record.setRejectReason(reason);
        record.setAfterStatus("REJECTED");
        record.setCreatedAt(LocalDateTime.now());
        auditRecordMapper.insert(record);

        return ApiResponse.success("已驳回");
    }

    /** Publish */
    @PostMapping("/audits/{targetType}/{targetId}/publish")
    @Transactional
    public ApiResponse<String> publishAudit(@PathVariable String targetType, @PathVariable Long targetId) {
        Long auditorId = LoginUserContext.getUserId();
        if ("LONG_REVIEW".equals(targetType)) {
            LongReview item = longReviewMapper.selectById(targetId);
            if (item == null) return ApiResponse.error(404, "内容不存在");
            if (!"APPROVED".equals(item.getStatus())) return ApiResponse.error(400, "当前状态不允许上线");
            item.setStatus("ONLINE");
            item.setUpdatedAt(LocalDateTime.now());
            longReviewMapper.updateById(item);
        } else {
            return ApiResponse.error(400, "不支持的上线操作");
        }

        AuditRecord record = new AuditRecord();
        record.setTargetType(targetType);
        record.setTargetId(targetId);
        record.setAuditorId(auditorId);
        record.setAction("PUBLISH");
        record.setAfterStatus("ONLINE");
        record.setCreatedAt(LocalDateTime.now());
        auditRecordMapper.insert(record);

        return ApiResponse.success("已上线");
    }

    /** Offline */
    @PostMapping("/audits/{targetType}/{targetId}/offline")
    @Transactional
    public ApiResponse<String> offlineAudit(@PathVariable String targetType, @PathVariable Long targetId) {
        Long auditorId = LoginUserContext.getUserId();
        if ("LONG_REVIEW".equals(targetType)) {
            LongReview item = longReviewMapper.selectById(targetId);
            if (item == null) return ApiResponse.error(404, "内容不存在");
            if (!"ONLINE".equals(item.getStatus())) return ApiResponse.error(400, "当前状态不允许下架");
            item.setStatus("OFFLINE");
            item.setUpdatedAt(LocalDateTime.now());
            longReviewMapper.updateById(item);
        } else if ("NEWS".equals(targetType)) {
            NewsArticle item = newsArticleMapper.selectById(targetId);
            if (item == null) return ApiResponse.error(404, "内容不存在");
            if (!"ONLINE".equals(item.getStatus())) return ApiResponse.error(400, "当前状态不允许下架");
            item.setStatus("OFFLINE");
            item.setUpdatedAt(LocalDateTime.now());
            newsArticleMapper.updateById(item);
        }

        AuditRecord record = new AuditRecord();
        record.setTargetType(targetType);
        record.setTargetId(targetId);
        record.setAuditorId(auditorId);
        record.setAction("OFFLINE");
        record.setAfterStatus("OFFLINE");
        record.setCreatedAt(LocalDateTime.now());
        auditRecordMapper.insert(record);

        return ApiResponse.success("已下架");
    }
}