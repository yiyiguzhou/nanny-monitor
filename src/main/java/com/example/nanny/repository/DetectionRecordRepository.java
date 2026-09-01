package com.example.nanny.repository;

import com.example.nanny.domain.DetectionRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface DetectionRecordRepository extends BaseMapper<DetectionRecord> {
    List<DetectionRecord> findTop20ByCameraIdOrderByDetectedAtDesc(String cameraId);

    List<DetectionRecord> findByCameraId(String id);
}