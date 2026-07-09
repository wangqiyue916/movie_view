package com.example.movie.longreview.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.movie.longreview.entity.ReviewImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ReviewImageMapper extends BaseMapper<ReviewImage> {

    @Select("SELECT image_url FROM review_images WHERE review_id = #{reviewId} ORDER BY sort_order ASC")
    List<String> selectImageUrlsByReviewId(@Param("reviewId") Long reviewId);
}
