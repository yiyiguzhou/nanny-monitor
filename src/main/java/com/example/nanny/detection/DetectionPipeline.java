package com.example.nanny.detection;

import com.example.nanny.ai.VlmClient;
import com.example.nanny.alert.AlertService;
import com.example.nanny.domain.DetectionRecord;
import com.example.nanny.dto.VlmResult;
import com.example.nanny.repository.DetectionRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class DetectionPipeline {
    private static final Logger log = LoggerFactory.getLogger(DetectionPipeline.class);

    private final VlmClient vlmClient;
    private final SlidingWindowEvaluator evaluator;
    private final AlertService alertService;
    private final DetectionRecordRepository recordRepository;

    public DetectionPipeline(VlmClient vlmClient,
                             SlidingWindowEvaluator evaluator,
                             AlertService alertService,
                             DetectionRecordRepository recordRepository) {
        this.vlmClient = vlmClient;
        this.evaluator = evaluator;
        this.alertService = alertService;
        this.recordRepository = recordRepository;
    }

    @Async("vlmExecutor")
    public void process(String cameraId, byte[] jpegFrame) {
        try {
            VlmResult result = vlmClient.detect(jpegFrame);
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
