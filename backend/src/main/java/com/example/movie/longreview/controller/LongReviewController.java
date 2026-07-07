package com.example.movie.longreview.controller;

import com.example.movie.common.response.ApiResponse;
import com.example.movie.common.response.PageResult;
import com.example.movie.longreview.dto.CreateReplyRequest;
import com.example.movie.longreview.dto.CreateReviewRequest;
import com.example.movie.longreview.dto.ReviewListQuery;
import com.example.movie.longreview.dto.UpdateReviewRequest;
import com.example.movie.longreview.service.LongReviewService;
import com.example.movie.longreview.service.ReviewReplyService;
import com.example.movie.longreview.vo.FeaturedReviewVO;
import com.example.movie.longreview.vo.LongReviewVO;
import com.example.movie.longreview.vo.ReviewReplyVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/long-reviews")
@RequiredArgsConstructor
public class LongReviewController {

    private final LongReviewService longReviewService;
    private final ReviewReplyService reviewReplyService;

    @GetMapping
    public ApiResponse<PageResult<LongReviewVO>> getReviewList(
            @RequestParam(required = false) Long movieId,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        ReviewListQuery query = new ReviewListQuery();
        query.setMovieId(movieId);
        query.setSortBy(sortBy);
        query.setPage(page);
        query.setPageSize(pageSize);
        return ApiResponse.success(longReviewService.getReviewList(query, 1L));
    }

    @GetMapping("/my")
    public ApiResponse<PageResult<LongReviewVO>> getMyReviews(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return ApiResponse.success(longReviewService.getMyReviews(1L, page, pageSize));
    }

    @GetMapping("/featured")
    public ApiResponse<PageResult<FeaturedReviewVO>> getFeaturedReviews(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "6") Integer pageSize) {
        return ApiResponse.success(longReviewService.getFeaturedReviews(page, pageSize));
    }

    @GetMapping("/{reviewId}")
    public ApiResponse<LongReviewVO> getReviewDetail(@PathVariable Long reviewId) {
        return ApiResponse.success(longReviewService.getReviewDetail(reviewId, 1L));
    }

    @PostMapping
    public ApiResponse<Map<String, Long>> createReview(@Valid @RequestBody CreateReviewRequest request) {
        Long id = longReviewService.createReview(request, 1L);
        return ApiResponse.success(Map.of("id", id));
    }

    @PutMapping("/{reviewId}")
    public ApiResponse<Void> updateReview(@PathVariable Long reviewId,
                                           @Valid @RequestBody UpdateReviewRequest request) {
        longReviewService.updateReview(reviewId, request, 1L);
        return ApiResponse.success();
    }

    @PostMapping("/{reviewId}/like")
    public ApiResponse<Void> likeReview(@PathVariable Long reviewId) {
        longReviewService.likeReview(reviewId, 1L);
        return ApiResponse.success();
    }

    @PostMapping("/{reviewId}/favorite")
    public ApiResponse<Void> favoriteReview(@PathVariable Long reviewId) {
        longReviewService.favoriteReview(reviewId, 1L);
        return ApiResponse.success();
    }

    @PostMapping("/{reviewId}/report")
    public ApiResponse<Void> reportReview(@PathVariable Long reviewId,
                                           @RequestBody Map<String, String> body) {
        longReviewService.reportReview(reviewId, 1L, body.get("reason"));
        return ApiResponse.success();
    }

    @GetMapping("/{reviewId}/replies")
    public ApiResponse<PageResult<ReviewReplyVO>> getReplies(
            @PathVariable Long reviewId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return ApiResponse.success(reviewReplyService.getReplies(reviewId, 1L, page, pageSize));
    }

    @PostMapping("/{reviewId}/replies")
    public ApiResponse<Map<String, Long>> createReply(@PathVariable Long reviewId,
                                                       @Valid @RequestBody CreateReplyRequest request) {
        Long id = reviewReplyService.createReply(reviewId, request, 1L);
        return ApiResponse.success(Map.of("id", id));
    }
}