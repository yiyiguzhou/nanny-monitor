package com.example.nanny.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "camera")
public class Camera {

    @Id
    private String id;
    private String name;
    private String rtspUrl;
    private boolean active = false;

    public Camera() {}

    public Camera(String id, String name, String rtspUrl) {
        this.id = id;
        this.name = name;
        this.rtspUrl = rtspUrl;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getRtspUrl() { return rtspUrl; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
