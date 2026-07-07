package com.example.movie.longreview.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.movie.longreview.entity.AuditRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuditRecordMapper extends BaseMapper<AuditRecord> {
}
