package com.example.nanny.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

/**
 * 
 * @TableName detection_record
 */
@TableName(value ="detection_record")
public class DetectionRecord {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 
     */
    private String cameraId;

    /**
     * 
     */
    private Integer feeding;

    /**
     * 
     */
    private Integer babyPresent;

    /**
     * 
     */
    private Integer caregiverPresent;

    /**
     * 
     */
    private Integer abnormal;

    /**
     * 
     */
    private Double confidence;

    /**
     * 
     */
    private String description;

    /**
     * 
     */
    private Date detectedAt;

    /**
     * 
     */
    public Long getId() {
        return id;
    }

    /**
     * 
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 
     */
    public String getCameraId() {
        return cameraId;
    }

    /**
     * 
     */
    public void setCameraId(String cameraId) {
        this.cameraId = cameraId;
    }

    /**
     * 
     */
    public Integer getFeeding() {
        return feeding;
    }

    /**
     * 
     */
    public void setFeeding(Integer feeding) {
        this.feeding = feeding;
    }

    /**
     * 
     */
    public Integer getBabyPresent() {
        return babyPresent;
    }

    /**
     * 
     */
    public void setBabyPresent(Integer babyPresent) {
        this.babyPresent = babyPresent;
    }

    /**
     * 
     */
    public Integer getCaregiverPresent() {
        return caregiverPresent;
    }

    /**
     * 
     */
    public void setCaregiverPresent(Integer caregiverPresent) {
        this.caregiverPresent = caregiverPresent;
    }

    /**
     * 
     */
    public Integer getAbnormal() {
        return abnormal;
    }

    /**
     * 
     */
    public void setAbnormal(Integer abnormal) {
        this.abnormal = abnormal;
    }

    /**
     * 
     */
    public Double getConfidence() {
        return confidence;
    }

    /**
     * 
     */
    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    /**
     * 
     */
    public String getDescription() {
        return description;
    }

    /**
     * 
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 
     */
    public Date getDetectedAt() {
        return detectedAt;
    }

    /**
     * 
     */
    public void setDetectedAt(Date detectedAt) {
        this.detectedAt = detectedAt;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        DetectionRecord other = (DetectionRecord) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getCameraId() == null ? other.getCameraId() == null : this.getCameraId().equals(other.getCameraId()))
            && (this.getFeeding() == null ? other.getFeeding() == null : this.getFeeding().equals(other.getFeeding()))
            && (this.getBabyPresent() == null ? other.getBabyPresent() == null : this.getBabyPresent().equals(other.getBabyPresent()))
            && (this.getCaregiverPresent() == null ? other.getCaregiverPresent() == null : this.getCaregiverPresent().equals(other.getCaregiverPresent()))
            && (this.getAbnormal() == null ? other.getAbnormal() == null : this.getAbnormal().equals(other.getAbnormal()))
            && (this.getConfidence() == null ? other.getConfidence() == null : this.getConfidence().equals(other.getConfidence()))
            && (this.getDescription() == null ? other.getDescription() == null : this.getDescription().equals(other.getDescription()))
            && (this.getDetectedAt() == null ? other.getDetectedAt() == null : this.getDetectedAt().equals(other.getDetectedAt()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getCameraId() == null) ? 0 : getCameraId().hashCode());
        result = prime * result + ((getFeeding() == null) ? 0 : getFeeding().hashCode());
        result = prime * result + ((getBabyPresent() == null) ? 0 : getBabyPresent().hashCode());
        result = prime * result + ((getCaregiverPresent() == null) ? 0 : getCaregiverPresent().hashCode());
        result = prime * result + ((getAbnormal() == null) ? 0 : getAbnormal().hashCode());
        result = prime * result + ((getConfidence() == null) ? 0 : getConfidence().hashCode());
        result = prime * result + ((getDescription() == null) ? 0 : getDescription().hashCode());
        result = prime * result + ((getDetectedAt() == null) ? 0 : getDetectedAt().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", cameraId=").append(cameraId);
        sb.append(", feeding=").append(feeding);
        sb.append(", babyPresent=").append(babyPresent);
        sb.append(", caregiverPresent=").append(caregiverPresent);
        sb.append(", abnormal=").append(abnormal);
        sb.append(", confidence=").append(confidence);
        sb.append(", description=").append(description);
        sb.append(", detectedAt=").append(detectedAt);
        sb.append("]");
        return sb.toString();
    }
}