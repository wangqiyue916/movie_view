package com.example.movie.longreview.service;

import com.example.movie.common.response.PageResult;
import com.example.movie.longreview.dto.CreateReviewRequest;
import com.example.movie.longreview.dto.ReviewListQuery;
import com.example.movie.longreview.dto.UpdateReviewRequest;
import com.example.movie.longreview.vo.FeaturedReviewVO;
import com.example.movie.longreview.vo.LongReviewVO;

public interface LongReviewService {

    PageResult<LongReviewVO> getReviewList(ReviewListQuery query, Long currentUserId);

    LongReviewVO getReviewDetail(Long reviewId, Long currentUserId);

    Long createReview(CreateReviewRequest request, Long userId);

    void updateReview(Long reviewId, UpdateReviewRequest request, Long userId);

    void likeReview(Long reviewId, Long userId);

    void favoriteReview(Long reviewId, Long userId);

    void reportReview(Long reviewId, Long userId, String reason);

    void setFeatured(Long reviewId, Long adminId);

    void unsetFeatured(Long reviewId, Long adminId);

    void hideReview(Long reviewId, Long adminId);

    void unhideReview(Long reviewId, Long adminId);

    void deleteReview(Long reviewId, Long adminId);

    PageResult<FeaturedReviewVO> getFeaturedReviews(int page, int pageSize);

    PageResult<LongReviewVO> getMyReviews(Long userId, int page, int pageSize);
}