package com.example.movie.movie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.movie.movie.entity.LikeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface LikeMapper extends BaseMapper<LikeEntity> {

    @Select("SELECT target_id FROM likes WHERE user_id = #{userId} AND target_type = #{targetType} AND target_id IN (${targetIds})")
    List<Long> findLikedTargetIds(@Param("userId") Long userId, @Param("targetType") String targetType, @Param("targetIds") String targetIds);
}
