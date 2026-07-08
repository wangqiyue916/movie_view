package com.example.movie.longreview.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.movie.longreview.entity.LongReview;
import com.example.movie.longreview.vo.FeaturedReviewVO;
import com.example.movie.longreview.vo.LongReviewVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LongReviewMapper extends BaseMapper<LongReview> {

    Page<LongReviewVO> selectReviewPage(Page<LongReview> page,
                                        @Param("movieId") Long movieId,
                                        @Param("sortBy") String sortBy,
                                        @Param("currentUserId") Long currentUserId);

    LongReviewVO selectReviewById(@Param("id") Long id,
                                   @Param("currentUserId") Long currentUserId);

    Page<FeaturedReviewVO> selectFeaturedReviews(Page<LongReview> page);

    Page<LongReviewVO> selectMyReviews(Page<LongReview> page,
                                       @Param("userId") Long userId);
}