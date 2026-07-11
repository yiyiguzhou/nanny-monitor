package com.example.nanny.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

@TableName("alert_record")
public class AlertRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 
     */
    private String cameraId;

    /**
     * 
     */
    private String alertType;

    /**
     * 
     */
    private String reason;

    /**
     * 
     */
    private Date alertedAt;

    public AlertRecord() {
    }

    public AlertRecord(String cameraId, String type, String reason) {
        this.cameraId = cameraId;
        this.alertType = type;
        this.reason = reason;
        this.alertedAt = new Date();
    }

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
    public String getAlertType() {
        return alertType;
    }

    /**
     * 
     */
    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }

    /**
     * 
     */
    public String getReason() {
        return reason;
    }

    /**
     * 
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * 
     */
    public Date getAlertedAt() {
        return alertedAt;
    }

    /**
     * 
     */
    public void setAlertedAt(Date alertedAt) {
        this.alertedAt = alertedAt;
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
        AlertRecord other = (AlertRecord) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getCameraId() == null ? other.getCameraId() == null : this.getCameraId().equals(other.getCameraId()))
            && (this.getAlertType() == null ? other.getAlertType() == null : this.getAlertType().equals(other.getAlertType()))
            && (this.getReason() == null ? other.getReason() == null : this.getReason().equals(other.getReason()))
            && (this.getAlertedAt() == null ? other.getAlertedAt() == null : this.getAlertedAt().equals(other.getAlertedAt()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getCameraId() == null) ? 0 : getCameraId().hashCode());
        result = prime * result + ((getAlertType() == null) ? 0 : getAlertType().hashCode());
        result = prime * result + ((getReason() == null) ? 0 : getReason().hashCode());
        result = prime * result + ((getAlertedAt() == null) ? 0 : getAlertedAt().hashCode());
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
        sb.append(", alertType=").append(alertType);
        sb.append(", reason=").append(reason);
        sb.append(", alertedAt=").append(alertedAt);
        sb.append("]");
        return sb.toString();
    }
}