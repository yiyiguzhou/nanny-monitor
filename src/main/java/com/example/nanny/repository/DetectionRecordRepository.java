package com.example.nanny.repository;

import com.example.nanny.domain.DetectionRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

public interface DetectionRecordRepository extends BaseMapper<DetectionRecord> {
    List<DetectionRecord> findTop20ByCameraIdOrderByDetectedAtDesc(String cameraId);

    List<DetectionRecord> findByCameraId(String id);
}