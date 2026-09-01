package com.example.nanny.repository;

import com.example.nanny.domain.AlertRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface AlertRecordRepository extends BaseMapper<AlertRecord> {
    List<AlertRecord> findTop20ByCameraIdOrderByAlertedAtDesc(String cameraId);
}