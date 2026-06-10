package com.example.nanny.service;

import com.example.nanny.domain.Camera;
import com.example.nanny.repository.CameraRepository;
import com.example.nanny.stream.StreamManager;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CameraService {
    private final CameraRepository cameraRepository;
    private final StreamManager streamManager;

    public CameraService(CameraRepository cameraRepository, StreamManager streamManager) {
        this.cameraRepository = cameraRepository;
        this.streamManager = streamManager;
    }

    public Camera register(String id, String name, String rtspUrl) {
        Camera camera = cameraRepository.findById(id)
            .orElseGet(() -> new Camera(id, name, rtspUrl));
        return cameraRepository.save(camera);
    }

    public void start(String id) {
        Camera camera = cameraRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Camera not found: " + id));
        streamManager.start(id, camera.getRtspUrl());
        camera.setActive(true);
        cameraRepository.save(camera);
    }

    public void stop(String id) {
        streamManager.stop(id);
        cameraRepository.findById(id).ifPresent(c -> {
            c.setActive(false);
            cameraRepository.save(c);
        });
    }

    public List<Camera> listAll() {
        return cameraRepository.findAll();
    }
}
