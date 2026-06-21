package com.example.nanny.controller;

import com.example.nanny.detection.DetectionPipeline;
import com.example.nanny.dto.VlmResult;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/detect")
public class DetectionController {
    private final DetectionPipeline detectionPipeline;

    public DetectionController(DetectionPipeline detectionPipeline) {
        this.detectionPipeline = detectionPipeline;
    }

    /**
     * Manually upload a screenshot to test VLM analysis without connecting an RTSP camera.
     */
    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public VlmResult detectImage(@RequestParam("file") MultipartFile file) throws IOException {
        return detectionPipeline.detect(file.getBytes());
    }
}
