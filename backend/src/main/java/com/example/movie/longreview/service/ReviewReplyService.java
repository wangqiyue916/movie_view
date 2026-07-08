package com.example.movie.longreview.service;

import com.example.movie.common.response.PageResult;
import com.example.movie.longreview.dto.CreateReplyRequest;
import com.example.movie.longreview.vo.ReviewReplyVO;

public interface ReviewReplyService {

    PageResult<ReviewReplyVO> getReplies(Long reviewId, Long currentUserId, int page, int pageSize);

    Long createReply(Long reviewId, CreateReplyRequest request, Long userId);

    void likeReply(Long replyId, Long userId);

    void hideReply(Long replyId, Long adminId);

    void deleteReply(Long replyId, Long adminId);
}