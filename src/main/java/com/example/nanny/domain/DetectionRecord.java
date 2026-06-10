package com.example.nanny.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "detection_record")
public class DetectionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cameraId;
    private boolean feeding;
    private boolean babyPresent;
    private boolean caregiverPresent;
    private boolean abnormal;
    private double confidence;

    @Column(length = 512)
    private String description;

    private Instant detectedAt = Instant.now();

    public DetectionRecord() {}

    public DetectionRecord(String cameraId, boolean feeding, boolean babyPresent,
                           boolean caregiverPresent, boolean abnormal,
                           double confidence, String description) {
        this.cameraId = cameraId;
        this.feeding = feeding;
        this.babyPresent = babyPresent;
        this.caregiverPresent = caregiverPresent;
        this.abnormal = abnormal;
        this.confidence = confidence;
        this.description = description;
    }

    public Long getId() { return id; }
    public String getCameraId() { return cameraId; }
    public boolean isFeeding() { return feeding; }
    public boolean isBabyPresent() { return babyPresent; }
    public boolean isCaregiverPresent() { return caregiverPresent; }
    public boolean isAbnormal() { return abnormal; }
    public double getConfidence() { return confidence; }
    public String getDescription() { return description; }
    public Instant getDetectedAt() { return detectedAt; }
}
