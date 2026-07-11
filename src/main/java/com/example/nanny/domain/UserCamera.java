package com.example.nanny.domain;

import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

@TableName("user_camera")
public class UserCamera implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long userId;

    private String cameraId;

    public UserCamera() {
    }

    public UserCamera(Long userId, String cameraId) {
        this.userId = userId;
        this.cameraId = cameraId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCameraId() {
        return cameraId;
    }

    public void setCameraId(String cameraId) {
        this.cameraId = cameraId;
    }
}
