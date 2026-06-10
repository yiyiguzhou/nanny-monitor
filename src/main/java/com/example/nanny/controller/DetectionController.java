package com.example.nanny.controller;

import com.example.nanny.ai.VlmClient;
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
    private final VlmClient vlmClient;

    public DetectionController(VlmClient vlmClient) {
        this.vlmClient = vlmClient;
    }

    /**
     * 手动上传一张截图测试通义千问-VL，不需要先接 RTSP 摄像头。
     */
    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public VlmResult detectImage(@RequestParam("file") MultipartFile file) throws IOException {
        return vlmClient.detect(file.getBytes());
    }
}
