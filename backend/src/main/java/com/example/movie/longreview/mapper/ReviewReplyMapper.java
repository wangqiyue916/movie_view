package com.example.movie.longreview.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.movie.longreview.entity.ReviewReply;
import com.example.movie.longreview.vo.ReviewReplyVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ReviewReplyMapper extends BaseMapper<ReviewReply> {

    Page<ReviewReplyVO> selectReplyPage(Page<ReviewReply> page,
                                         @Param("reviewId") Long reviewId,
                                         @Param("currentUserId") Long currentUserId);

    java.util.List<ReviewReplyVO> selectChildReplies(@Param("parentId") Long parentId,
                                                       @Param("currentUserId") Long currentUserId);
}