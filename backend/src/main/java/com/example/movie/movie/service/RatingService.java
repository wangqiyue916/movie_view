package com.example.movie.movie.service;

import com.example.movie.movie.dto.RatingSubmitRequest;
import com.example.movie.movie.vo.RatingSummaryVO;
import com.example.movie.movie.vo.RatingVO;

public interface RatingService {

    void submitRating(Long movieId, Long userId, RatingSubmitRequest request);

    RatingSummaryVO getRatingSummary(Long movieId);

    RatingVO getMyRating(Long movieId, Long userId);
}
