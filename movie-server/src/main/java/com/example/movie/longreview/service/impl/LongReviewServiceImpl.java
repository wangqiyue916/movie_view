package com.example.movie.longreview.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.movie.common.enums.AuditStatusEnum;
import com.example.movie.common.enums.ReportStatusEnum;
import com.example.movie.common.enums.TargetTypeEnum;
import com.example.movie.common.exception.BusinessException;
import com.example.movie.common.exception.ErrorCode;
import com.example.movie.common.response.PageResult;
import com.example.movie.longreview.dto.CreateReviewRequest;
import com.example.movie.longreview.dto.ReviewListQuery;
import com.example.movie.longreview.dto.UpdateReviewRequest;
import com.example.movie.longreview.entity.AuditRecord;
import com.example.movie.longreview.entity.FavoriteRecord;
import com.example.movie.longreview.entity.LikeRecord;
import com.example.movie.longreview.entity.LongReview;
import com.example.movie.longreview.entity.ReportRecord;
import com.example.movie.longreview.entity.ReviewImage;
import com.example.movie.longreview.mapper.AuditRecordMapper;
import com.example.movie.longreview.mapper.FavoriteRecordMapper;
import com.example.movie.longreview.mapper.LikeRecordMapper;
import com.example.movie.longreview.mapper.LongReviewMapper;
import com.example.movie.longreview.mapper.ReportRecordMapper;
import com.example.movie.longreview.mapper.ReviewImageMapper;
import com.example.movie.longreview.service.LongReviewService;
import com.example.movie.longreview.vo.FeaturedReviewVO;
import com.example.movie.longreview.vo.LongReviewVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LongReviewServiceImpl implements LongReviewService {

    private final LongReviewMapper longReviewMapper;
    private final ReviewImageMapper reviewImageMapper;
    private final LikeRecordMapper likeRecordMapper;
    private final FavoriteRecordMapper favoriteRecordMapper;
    private final ReportRecordMapper reportRecordMapper;
    private final AuditRecordMapper auditRecordMapper;

    @Override
    public PageResult<LongReviewVO> getReviewList(ReviewListQuery query, Long currentUserId) {
        Page<LongReview> page = new Page<>(query.getPage(), query.getPageSize());
        Page<LongReviewVO> resultPage = longReviewMapper.selectReviewPage(
                page, query.getMovieId(), query.getSortBy(), currentUserId);

        resultPage.getRecords().forEach(vo -> {
            List<String> images = reviewImageMapper.selectImageUrlsByReviewId(vo.getId());
            vo.setImages(images != null ? images : Collections.emptyList());
        });

        return new PageResult<>(
                resultPage.getRecords(),
                (int) resultPage.getCurrent(),
                (int) resultPage.getSize(),
                resultPage.getTotal());
    }

    @Override
    public LongReviewVO getReviewDetail(Long reviewId, Long currentUserId) {
        LongReviewVO vo = longReviewMapper.selectReviewById(reviewId, currentUserId);
        if (vo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "长评不存在");
        }
        List<String> images = reviewImageMapper.selectImageUrlsByReviewId(vo.getId());
        vo.setImages(images != null ? images : Collections.emptyList());

        LongReview review = longReviewMapper.selectById(reviewId);
        if (review != null) {
            review.setViewCount(review.getViewCount() + 1);
            longReviewMapper.updateById(review);
        }

        return vo;
    }

    @Override
    @Transactional
    public Long createReview(CreateReviewRequest request, Long userId) {
        LongReview review = new LongReview();
        review.setMovieId(request.getMovieId());
        review.setUserId(userId);
        review.setTitle(request.getTitle());
        review.setContentMd(request.getContentMd());
        review.setCoverUrl(request.getCoverUrl());
        review.setStatus(AuditStatusEnum.PENDING.name());
        review.setViewCount(0L);
        review.setLikeCount(0);
        review.setFavoriteCount(0);
        review.setReplyCount(0);
        review.setIsFeatured(0);
        longReviewMapper.insert(review);

        if (request.getImages() != null && !request.getImages().isEmpty()) {
            for (int i = 0; i < request.getImages().size(); i++) {
                ReviewImage image = new ReviewImage();
                image.setReviewId(review.getId());
                image.setImageUrl(request.getImages().get(i));
                image.setSortOrder(i);
                reviewImageMapper.insert(image);
            }
        }

        AuditRecord audit = new AuditRecord();
        audit.setTargetType(TargetTypeEnum.LONG_REVIEW.name());
        audit.setTargetId(review.getId());
        audit.setSubmitterId(userId);
        audit.setAuditorId(userId);
        audit.setAction("SUBMIT");
        audit.setAfterStatus(AuditStatusEnum.PENDING.name());
        auditRecordMapper.insert(audit);

        return review.getId();
    }

    @Override
    @Transactional
    public void updateReview(Long reviewId, UpdateReviewRequest request, Long userId) {
        LongReview review = longReviewMapper.selectById(reviewId);
        if (review == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "长评不存在");
        }
        if (!review.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "只能修改自己的长评");
        }
        String status = review.getStatus();
        if (!AuditStatusEnum.DRAFT.name().equals(status)
                && !AuditStatusEnum.REJECTED.name().equals(status)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "只能修改草稿或已驳回状态的长评");
        }

        String oldStatus = review.getStatus();

        review.setTitle(request.getTitle());
        review.setContentMd(request.getContentMd());
        review.setCoverUrl(request.getCoverUrl());
        review.setStatus(AuditStatusEnum.PENDING.name());
        longReviewMapper.updateById(review);

        LambdaQueryWrapper<ReviewImage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReviewImage::getReviewId, reviewId);
        reviewImageMapper.delete(wrapper);

        if (request.getImages() != null && !request.getImages().isEmpty()) {
            for (int i = 0; i < request.getImages().size(); i++) {
                ReviewImage image = new ReviewImage();
                image.setReviewId(review.getId());
                image.setImageUrl(request.getImages().get(i));
                image.setSortOrder(i);
                reviewImageMapper.insert(image);
            }
        }

        AuditRecord audit = new AuditRecord();
        audit.setTargetType(TargetTypeEnum.LONG_REVIEW.name());
        audit.setTargetId(reviewId);
        audit.setSubmitterId(userId);
        audit.setAction("RESUBMIT");
        audit.setBeforeStatus(oldStatus);
        audit.setAfterStatus(AuditStatusEnum.PENDING.name());
        auditRecordMapper.insert(audit);
    }

    @Override
    @Transactional
    public void likeReview(Long reviewId, Long userId) {
        LongReview review = longReviewMapper.selectById(reviewId);
        if (review == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "长评不存在");
        }

        LambdaQueryWrapper<LikeRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LikeRecord::getUserId, userId)
               .eq(LikeRecord::getTargetType, TargetTypeEnum.LONG_REVIEW.name())
               .eq(LikeRecord::getTargetId, reviewId);
        LikeRecord existing = likeRecordMapper.selectOne(wrapper);

        if (existing != null) {
            likeRecordMapper.deleteById(existing.getId());
            review.setLikeCount(Math.max(0, review.getLikeCount() - 1));
        } else {
            LikeRecord record = new LikeRecord();
            record.setUserId(userId);
            record.setTargetType(TargetTypeEnum.LONG_REVIEW.name());
            record.setTargetId(reviewId);
            likeRecordMapper.insert(record);
            review.setLikeCount(review.getLikeCount() + 1);
        }
        longReviewMapper.updateById(review);
    }

    @Override
    @Transactional
    public void favoriteReview(Long reviewId, Long userId) {
        LongReview review = longReviewMapper.selectById(reviewId);
        if (review == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "长评不存在");
        }

        LambdaQueryWrapper<FavoriteRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FavoriteRecord::getUserId, userId)
               .eq(FavoriteRecord::getTargetType, TargetTypeEnum.LONG_REVIEW.name())
               .eq(FavoriteRecord::getTargetId, reviewId);
        FavoriteRecord existing = favoriteRecordMapper.selectOne(wrapper);

        if (existing != null) {
            favoriteRecordMapper.deleteById(existing.getId());
            review.setFavoriteCount(Math.max(0, review.getFavoriteCount() - 1));
        } else {
            FavoriteRecord record = new FavoriteRecord();
            record.setUserId(userId);
            record.setTargetType(TargetTypeEnum.LONG_REVIEW.name());
            record.setTargetId(reviewId);
            favoriteRecordMapper.insert(record);
            review.setFavoriteCount(review.getFavoriteCount() + 1);
        }
        longReviewMapper.updateById(review);
    }

    @Override
    @Transactional
    public void reportReview(Long reviewId, Long userId, String reason) {
        LongReview review = longReviewMapper.selectById(reviewId);
        if (review == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "长评不存在");
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "举报原因不能为空");
        }

        ReportRecord record = new ReportRecord();
        record.setReporterId(userId);
        record.setTargetType(TargetTypeEnum.LONG_REVIEW.name());
        record.setTargetId(reviewId);
        record.setReason(reason.trim());
        record.setStatus(ReportStatusEnum.PENDING.name());
        reportRecordMapper.insert(record);
    }

    @Override
    public void setFeatured(Long reviewId, Long adminId) {
        LongReview review = longReviewMapper.selectById(reviewId);
        if (review == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "长评不存在");
        }
        review.setIsFeatured(1);
        review.setFeaturedAt(LocalDateTime.now());
        longReviewMapper.updateById(review);
    }

    @Override
    public void unsetFeatured(Long reviewId, Long adminId) {
        LongReview review = longReviewMapper.selectById(reviewId);
        if (review == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "长评不存在");
        }
        review.setIsFeatured(0);
        review.setFeaturedAt(null);
        longReviewMapper.updateById(review);
    }

    @Override
    public void hideReview(Long reviewId, Long adminId) {
        LongReview review = longReviewMapper.selectById(reviewId);
        if (review == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "长评不存在");
        }
        review.setStatus(AuditStatusEnum.OFFLINE.name());
        longReviewMapper.updateById(review);
    }

    @Override
    public void unhideReview(Long reviewId, Long adminId) {
        LongReview review = longReviewMapper.selectById(reviewId);
        if (review == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "长评不存在");
        }
        review.setStatus(AuditStatusEnum.ONLINE.name());
        longReviewMapper.updateById(review);
    }

    @Override
    public void deleteReview(Long reviewId, Long adminId) {
        LongReview review = longReviewMapper.selectById(reviewId);
        if (review == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "长评不存在");
        }
        review.setStatus(AuditStatusEnum.DELETED.name());
        review.setDeletedAt(LocalDateTime.now());
        longReviewMapper.updateById(review);
    }

    @Override
    public PageResult<LongReviewVO> getMyReviews(Long userId, int page, int pageSize) {
        Page<LongReview> mpPage = new Page<>(page, pageSize);
        Page<LongReviewVO> resultPage = longReviewMapper.selectMyReviews(mpPage, userId);

        resultPage.getRecords().forEach(vo -> {
            List<String> images = reviewImageMapper.selectImageUrlsByReviewId(vo.getId());
            vo.setImages(images != null ? images : Collections.emptyList());
        });

        return new PageResult<>(
                resultPage.getRecords(),
                (int) resultPage.getCurrent(),
                (int) resultPage.getSize(),
                resultPage.getTotal());
    }

    @Override
    public PageResult<LongReviewVO> getFavoriteReviews(Long userId, int page, int pageSize) {
        Page<LongReview> mpPage = new Page<>(page, pageSize);
        Page<LongReviewVO> resultPage = longReviewMapper.selectFavoriteReviews(mpPage, userId);
        resultPage.getRecords().forEach(vo -> {
            List<String> images = reviewImageMapper.selectImageUrlsByReviewId(vo.getId());
            vo.setImages(images != null ? images : Collections.emptyList());
        });
        return new PageResult<>(
                resultPage.getRecords(),
                (int) resultPage.getCurrent(),
                (int) resultPage.getSize(),
                resultPage.getTotal());
    }

    @Override
    public PageResult<FeaturedReviewVO> getFeaturedReviews(int page, int pageSize) {
        Page<LongReview> mpPage = new Page<>(page, pageSize);
        Page<FeaturedReviewVO> resultPage = longReviewMapper.selectFeaturedReviews(mpPage);
        return new PageResult<>(
                resultPage.getRecords(),
                (int) resultPage.getCurrent(),
                (int) resultPage.getSize(),
                resultPage.getTotal());
    }
}
