package com.example.nanny.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AlertRecordRepository extends JpaRepository<AlertRecord, Long> {
    List<AlertRecord> findTop20ByCameraIdOrderByAlertedAtDesc(String cameraId);
}
