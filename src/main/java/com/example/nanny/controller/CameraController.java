package com.example.nanny.controller;

import com.example.nanny.repository.AlertRecordRepository;
import com.example.nanny.repository.DetectionRecordRepository;
import com.example.nanny.service.CameraService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cameras")
public class CameraController {
    private final CameraService cameraService;
    private final DetectionRecordRepository detectionRepo;
    private final AlertRecordRepository alertRepo;

    public CameraController(CameraService cameraService,
                             DetectionRecordRepository detectionRepo,
                             AlertRecordRepository alertRepo) {
        this.cameraService = cameraService;
        this.detectionRepo = detectionRepo;
        this.alertRepo = alertRepo;
    }

    @PostMapping
    public Camera register(@RequestBody Map<String, String> body) {
        return cameraService.register(body.get("id"), body.get("name"), body.get("rtspUrl"));
    }

    @GetMapping
    public List<Camera> list() {
        return cameraService.listAll();
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<Void> start(@PathVariable String id) {
        cameraService.start(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/stop")
    public ResponseEntity<Void> stop(@PathVariable String id) {
        cameraService.stop(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/detections")
    public List<DetectionRecord> detections(@PathVariable String id) {
        return detectionRepo.findTop20ByCameraIdOrderByDetectedAtDesc(id);
    }

    @GetMapping("/{id}/alerts")
    public List<AlertRecord> alerts(@PathVariable String id) {
        return alertRepo.findTop20ByCameraIdOrderByAlertedAtDesc(id);
    }
}
