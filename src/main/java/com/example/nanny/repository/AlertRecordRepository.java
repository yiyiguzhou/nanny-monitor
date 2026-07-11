package com.example.nanny.repository;

import com.example.nanny.domain.AlertRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

public interface AlertRecordRepository extends BaseMapper<AlertRecord> {
    List<AlertRecord> findTop20ByCameraIdOrderByAlertedAtDesc(String cameraId);
}