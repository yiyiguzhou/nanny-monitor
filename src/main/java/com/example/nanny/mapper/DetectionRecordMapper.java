package com.example.nanny.mapper;

import com.example.nanny.domain.DetectionRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author wzhang
* @description 针对表【detection_record】的数据库操作Mapper
* @createDate 2026-06-20 22:54:55
* @Entity com.example.nanny.domain.DetectionRecord
*/
public interface DetectionRecordMapper extends BaseMapper<DetectionRecord> {

    /**
     * 查询前10条检测记录
     */
    List<DetectionRecord> selectTop10();
}




