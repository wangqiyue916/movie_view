package com.example.movie.longreview.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.movie.longreview.entity.FavoriteRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FavoriteRecordMapper extends BaseMapper<FavoriteRecord> {
}
