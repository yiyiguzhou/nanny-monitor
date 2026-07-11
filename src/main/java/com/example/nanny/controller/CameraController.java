package com.example.nanny.controller;

import com.example.nanny.domain.AlertRecord;
import com.example.nanny.domain.Camera;
import com.example.nanny.domain.DetectionRecord;
import com.example.nanny.repository.AlertRecordRepository;
import com.example.nanny.repository.DetectionRecordRepository;
import com.example.nanny.service.CameraService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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
        Long userId = getCurrentUserId();
        return cameraService.register(body.get("id"), body.get("name"), body.get("rtspUrl"), userId);
    }

    @GetMapping
    public List<Camera> list() {
        Long userId = getCurrentUserId();
        String role = getCurrentUserRole();
        return cameraService.listAll(userId, role);
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<Void> start(@PathVariable String id) {
        Long userId = getCurrentUserId();
        cameraService.start(id, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/stop")
    public ResponseEntity<Void> stop(@PathVariable String id) {
        Long userId = getCurrentUserId();
        cameraService.stop(id, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/detections")
    public List<DetectionRecord> detections(@PathVariable String id) {
        return detectionRepo.findByCameraId(id);
    }

    @GetMapping("/{id}/alerts")
    public List<AlertRecord> alerts(@PathVariable String id) {
        return alertRepo.findTop20ByCameraIdOrderByAlertedAtDesc(id);
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (Long) auth.getPrincipal();
    }

    private String getCurrentUserRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .filter(a -> a.startsWith("ROLE_"))
            .map(a -> a.substring(5))
            .findFirst()
            .orElse("USER");
    }
}
