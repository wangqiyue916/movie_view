package com.example.movie.merchandise.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.movie.merchandise.entity.Merchandise;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MerchandiseMapper extends BaseMapper<Merchandise> {
}
