package com.example.nanny.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DetectionRecordRepository extends JpaRepository<DetectionRecord, Long> {
    List<DetectionRecord> findTop20ByCameraIdOrderByDetectedAtDesc(String cameraId);
}
