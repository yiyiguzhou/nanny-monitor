package com.example.nanny.mapper;

import com.example.nanny.domain.AlertRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author wzhang
* @description 针对表【alert_record】的数据库操作Mapper
* @createDate 2026-06-20 22:54:55
* @Entity com.example.nanny.domain.AlertRecord
*/
public interface AlertRecordMapper extends BaseMapper<AlertRecord> {

    /**
     * 查询前10条告警记录
     */
    List<AlertRecord> selectTop10();
}




