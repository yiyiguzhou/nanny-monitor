package com.example.nanny.detection;

import com.example.nanny.ai.FeedingPrompt;
import com.example.nanny.alert.AlertService;
import com.example.nanny.dto.VlmResult;
import com.example.nanny.repository.DetectionRecordRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class DetectionPipeline {
    private static final Logger log = LoggerFactory.getLogger(DetectionPipeline.class);

    private final ChatClient chatClient;
    private final SlidingWindowEvaluator evaluator;
    private final AlertService alertService;
    private final DetectionRecordRepository recordRepository;
    private final ObjectMapper objectMapper;

    public DetectionPipeline(ChatClient chatClient,
                             SlidingWindowEvaluator evaluator,
                             AlertService alertService,
                             DetectionRecordRepository recordRepository,
                             ObjectMapper objectMapper) {
        this.chatClient = chatClient;
        this.evaluator = evaluator;
        this.alertService = alertService;
        this.recordRepository = recordRepository;
        this.objectMapper = objectMapper;
    }

    @Async("vlmExecutor")
    public void process(String cameraId, byte[] jpegFrame) {
        try {
            VlmResult result = detect(jpegFrame);
            recordRepository.save(toRecord(cameraId, result));

            AlertRule.Decision decision = evaluator.feed(cameraId, result);
            if (decision.shouldAlert()) {
                alertService.raise(cameraId, decision.type(), decision.reason());
            }

            log.info("Detection result. cameraId={}, feeding={}, baby={}, caregiver={}, abnormal={}, confidence={}, desc={}",
                cameraId,
                result.feeding(),
                result.babyPresent(),
                result.caregiverPresent(),
                result.abnormal(),
                result.confidence(),
                result.description());
        } catch (Exception e) {
            log.warn("Detection pipeline failed. cameraId={}, error={}", cameraId, e.getMessage(), e);
        }
    }

    /**
     * Detect feeding status from a JPEG frame using the VLM via ChatClient.
     */
    public VlmResult detect(byte[] jpegFrame) {
        try {
            String raw = chatClient.prompt()
                .user(FeedingPrompt.userSpec(jpegFrame))
                .call()
                .content();
            String json = extractJson(raw);
            return objectMapper.readValue(json, VlmResult.class);
        } catch (Exception e) {
            log.warn("VLM detection failed: {}", e.getMessage(), e);
            return VlmResult.unknown();
        }
    }

    private String extractJson(String content) {
        int start = content.indexOf('{');
        int end = content.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return content.substring(start, end + 1);
        }
        throw new IllegalArgumentException("Model did not return JSON: " + content);
    }

    private DetectionRecord toRecord(String cameraId, VlmResult result) {
        return new DetectionRecord(
            cameraId,
            result.feeding(),
            result.babyPresent(),
            result.caregiverPresent(),
            result.abnormal(),
            result.confidence(),
            result.description()
        );
    }
}
