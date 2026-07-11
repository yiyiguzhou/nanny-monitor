package com.example.nanny.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

@TableName("video_clip")
public class VideoClip implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String cameraId;

    private Long alertRecordId;

    private String alertType;

    @TableField(typeHandler = org.apache.ibatis.type.BlobTypeHandler.class)
    private byte[] clipData;

    private Double durationSeconds;

    private Date createdAt;

    public VideoClip() {
    }

    public VideoClip(String cameraId, Long alertRecordId, String alertType,
                     byte[] clipData, Double durationSeconds) {
        this.cameraId = cameraId;
        this.alertRecordId = alertRecordId;
        this.alertType = alertType;
        this.clipData = clipData;
        this.durationSeconds = durationSeconds;
        this.createdAt = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCameraId() {
        return cameraId;
    }

    public void setCameraId(String cameraId) {
        this.cameraId = cameraId;
    }

    public Long getAlertRecordId() {
        return alertRecordId;
    }

    public void setAlertRecordId(Long alertRecordId) {
        this.alertRecordId = alertRecordId;
    }

    public String getAlertType() {
        return alertType;
    }

    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }

    public byte[] getClipData() {
        return clipData;
    }

    public void setClipData(byte[] clipData) {
        this.clipData = clipData;
    }

    public Double getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(Double durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}