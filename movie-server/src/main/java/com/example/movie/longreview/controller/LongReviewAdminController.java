package com.example.movie.longreview.controller;

import com.example.movie.common.response.ApiResponse;
import com.example.movie.longreview.service.LongReviewService;
import com.example.movie.longreview.service.ReviewReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/long-reviews")
@RequiredArgsConstructor
public class LongReviewAdminController {

    private final LongReviewService longReviewService;
    private final ReviewReplyService reviewReplyService;

    @PostMapping("/{reviewId}/feature")
    public ApiResponse<Void> setFeatured(@PathVariable Long reviewId) {
        longReviewService.setFeatured(reviewId, 1L);
        return ApiResponse.success();
    }

    @DeleteMapping("/{reviewId}/feature")
    public ApiResponse<Void> unsetFeatured(@PathVariable Long reviewId) {
        longReviewService.unsetFeatured(reviewId, 1L);
        return ApiResponse.success();
    }

    @PutMapping("/{reviewId}/hide")
    public ApiResponse<Void> hideReview(@PathVariable Long reviewId) {
        longReviewService.hideReview(reviewId, 1L);
        return ApiResponse.success();
    }

    @PutMapping("/{reviewId}/unhide")
    public ApiResponse<Void> unhideReview(@PathVariable Long reviewId) {
        longReviewService.unhideReview(reviewId, 1L);
        return ApiResponse.success();
    }

    @DeleteMapping("/{reviewId}")
    public ApiResponse<Void> deleteReview(@PathVariable Long reviewId) {
        longReviewService.deleteReview(reviewId, 1L);
        return ApiResponse.success();
    }
}