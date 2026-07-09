package com.example.movie.movie.controller;

import com.example.movie.common.config.LoginUserContext;
import com.example.movie.common.exception.BusinessException;
import com.example.movie.common.exception.ErrorCode;
import com.example.movie.common.response.ApiResponse;
import com.example.movie.movie.dto.RatingSubmitRequest;
import com.example.movie.movie.service.RatingService;
import com.example.movie.movie.vo.RatingSummaryVO;
import com.example.movie.movie.vo.RatingVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movies/{movieId}/ratings")
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping
    public ApiResponse<Void> submitRating(@PathVariable Long movieId, @Valid @RequestBody RatingSubmitRequest request) {
        Long userId = LoginUserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "请先登录");
        }
        ratingService.submitRating(movieId, userId, request);
        return ApiResponse.success();
    }

    @GetMapping("/summary")
    public ApiResponse<RatingSummaryVO> getSummary(@PathVariable Long movieId) {
        return ApiResponse.success(ratingService.getRatingSummary(movieId));
    }

    @GetMapping("/me")
    public ApiResponse<RatingVO> getMyRating(@PathVariable Long movieId) {
        Long userId = LoginUserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "请先登录");
        }
        return ApiResponse.success(ratingService.getMyRating(movieId, userId));
    }
}
