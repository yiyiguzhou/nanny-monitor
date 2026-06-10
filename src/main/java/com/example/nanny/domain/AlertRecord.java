package com.example.nanny.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "alert_record")
public class AlertRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cameraId;
    private String alertType;

    @Column(length = 512)
    private String reason;

    private Instant alertedAt = Instant.now();

    public AlertRecord() {}

    public AlertRecord(String cameraId, String alertType, String reason) {
        this.cameraId = cameraId;
        this.alertType = alertType;
        this.reason = reason;
    }

    public Long getId() { return id; }
    public String getCameraId() { return cameraId; }
    public String getAlertType() { return alertType; }
    public String getReason() { return reason; }
    public Instant getAlertedAt() { return alertedAt; }
}
