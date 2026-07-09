package com.example.movie.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.movie.common.response.ApiResponse;
import com.example.movie.longreview.entity.AuditRecord;
import com.example.movie.longreview.entity.LongReview;
import com.example.movie.longreview.entity.ReportRecord;
import com.example.movie.longreview.mapper.AuditRecordMapper;
import com.example.movie.longreview.mapper.LongReviewMapper;
import com.example.movie.longreview.mapper.ReportRecordMapper;
import com.example.movie.news.entity.NewsArticle;
import com.example.movie.news.mapper.NewsArticleMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/api/admin")
public class AdminDashboardController {

    private final LongReviewMapper longReviewMapper;
    private final NewsArticleMapper newsArticleMapper;
    private final ReportRecordMapper reportRecordMapper;
    private final AuditRecordMapper auditRecordMapper;

    public AdminDashboardController(LongReviewMapper longReviewMapper,
                                     NewsArticleMapper newsArticleMapper,
                                     ReportRecordMapper reportRecordMapper,
                                     AuditRecordMapper auditRecordMapper) {
        this.longReviewMapper = longReviewMapper;
        this.newsArticleMapper = newsArticleMapper;
        this.reportRecordMapper = reportRecordMapper;
        this.auditRecordMapper = auditRecordMapper;
    }

    @GetMapping("/dashboard")
    public ApiResponse<Map<String, Object>> dashboard() {
        // Pending audits (long_reviews + news_articles with status PENDING)
        long pendingLongReviews = longReviewMapper.selectCount(
            new LambdaQueryWrapper<LongReview>().eq(LongReview::getStatus, "PENDING"));
        long pendingNews = newsArticleMapper.selectCount(
            new LambdaQueryWrapper<NewsArticle>().eq(NewsArticle::getStatus, "PENDING"));

        // Pending reports
        long pendingReports = reportRecordMapper.selectCount(
            new LambdaQueryWrapper<ReportRecord>().eq(ReportRecord::getStatus, "PENDING"));

        // Online content
        long onlineLongReviews = longReviewMapper.selectCount(
            new LambdaQueryWrapper<LongReview>().eq(LongReview::getStatus, "ONLINE"));
        long onlineNews = newsArticleMapper.selectCount(
            new LambdaQueryWrapper<NewsArticle>().eq(NewsArticle::getStatus, "ONLINE"));

        // Recent audit records
        LambdaQueryWrapper<AuditRecord> auditWrapper = new LambdaQueryWrapper<>();
        auditWrapper.orderByDesc(AuditRecord::getCreatedAt);
        auditWrapper.last("LIMIT 5");
        List<AuditRecord> recentAudits = auditRecordMapper.selectList(auditWrapper);

        List<Map<String, Object>> recentList = new ArrayList<>();
        for (AuditRecord a : recentAudits) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", a.getId());
            item.put("type", a.getTargetType());
            item.put("action", a.getAction());
            item.put("actionLabel", labelAction(a.getAction()));
            item.put("reason", a.getRejectReason());
            item.put("time", a.getCreatedAt() != null ? a.getCreatedAt().toString() : "");
            recentList.add(item);
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("pendingAudits", pendingLongReviews + pendingNews);
        data.put("pendingReports", pendingReports);
        data.put("onlineContent", onlineLongReviews + onlineNews);
        data.put("recentAudits", recentList);
        return ApiResponse.success(data);
    }

    private String labelAction(String action) {
        return switch (action) {
            case "APPROVE" -> "已通过";
            case "REJECT", "RESUBMIT" -> "已驳回";
            case "PUBLISH" -> "已上线";
            case "OFFLINE" -> "已下架";
            case "SUBMIT" -> "已提交";
            default -> action;
        };
    }
}