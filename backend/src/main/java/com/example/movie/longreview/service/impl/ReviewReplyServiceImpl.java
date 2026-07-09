package com.example.movie.longreview.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.movie.common.exception.BusinessException;
import com.example.movie.common.exception.ErrorCode;
import com.example.movie.common.response.PageResult;
import com.example.movie.longreview.dto.CreateReplyRequest;
import com.example.movie.longreview.entity.LongReview;
import com.example.movie.longreview.entity.ReviewReply;
import com.example.movie.longreview.mapper.LongReviewMapper;
import com.example.movie.longreview.mapper.ReviewReplyMapper;
import com.example.movie.longreview.service.ReviewReplyService;
import com.example.movie.longreview.vo.ReviewReplyVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewReplyServiceImpl implements ReviewReplyService {

    private final ReviewReplyMapper reviewReplyMapper;
    private final LongReviewMapper longReviewMapper;

    @Override
    public PageResult<ReviewReplyVO> getReplies(Long reviewId, Long currentUserId, int page, int pageSize) {
        Page<ReviewReply> mpPage = new Page<>(page, pageSize);
        Page<ReviewReplyVO> resultPage = reviewReplyMapper.selectReplyPage(mpPage, reviewId, currentUserId);

        resultPage.getRecords().forEach(vo -> {
            if (vo.getParentId() == null) {
                List<ReviewReplyVO> children = reviewReplyMapper.selectChildReplies(vo.getId(), currentUserId);
                vo.setChildren(children != null ? children : List.of());
            }
        });

        return new PageResult<>(
                resultPage.getRecords(),
                (int) resultPage.getCurrent(),
                (int) resultPage.getSize(),
                resultPage.getTotal()
        );
    }

    @Override
    @Transactional
    public Long createReply(Long reviewId, CreateReplyRequest request, Long userId) {
        LongReview review = longReviewMapper.selectById(reviewId);
        if (review == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "长评不存在");
        }

        ReviewReply reply = new ReviewReply();
        reply.setReviewId(reviewId);
        reply.setUserId(userId);
        reply.setParentId(request.getParentId());
        reply.setContent(request.getContent());
        reply.setStatus("NORMAL");
        reply.setLikeCount(0);
        reply.setReportCount(0);
        reviewReplyMapper.insert(reply);

        review.setReplyCount(review.getReplyCount() + 1);
        longReviewMapper.updateById(review);

        return reply.getId();
    }

    @Override
    @Transactional
    public void likeReply(Long replyId, Long userId) {
        ReviewReply reply = reviewReplyMapper.selectById(replyId);
        if (reply == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "回复不存在");
        }
        reply.setLikeCount(reply.getLikeCount() + 1);
        reviewReplyMapper.updateById(reply);
    }

    @Override
    public void hideReply(Long replyId, Long adminId) {
        ReviewReply reply = reviewReplyMapper.selectById(replyId);
        if (reply == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "回复不存在");
        }
        reply.setStatus("HIDDEN");
        reviewReplyMapper.updateById(reply);
    }

    @Override
    public void deleteReply(Long replyId, Long adminId) {
        ReviewReply reply = reviewReplyMapper.selectById(replyId);
        if (reply == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "回复不存在");
        }
        reply.setStatus("DELETED");
        reply.setDeletedAt(LocalDateTime.now());
        reviewReplyMapper.updateById(reply);
    }
}