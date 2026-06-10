package com.example.nanny.detection;

import com.example.nanny.dto.VlmResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SlidingWindowEvaluator {
    private final Map<String, Deque<VlmResult>> windows = new ConcurrentHashMap<>();
    private final Map<String, Instant> lastAlertAt = new ConcurrentHashMap<>();

    private final int windowSize;
    private final int abnormalThreshold;
    private final int caregiverAbsentThreshold;
    private final double lowConfidenceThreshold;
    private final long cooldownSeconds;

    public SlidingWindowEvaluator(
        @Value("${nanny.alert.window-size:6}") int windowSize,
        @Value("${nanny.alert.abnormal-threshold:2}") int abnormalThreshold,
        @Value("${nanny.alert.caregiver-absent-threshold:3}") int caregiverAbsentThreshold,
        @Value("${nanny.alert.low-confidence-threshold:0.55}") double lowConfidenceThreshold,
        @Value("${nanny.alert.cooldown-seconds:300}") long cooldownSeconds
    ) {
        this.windowSize = windowSize;
        this.abnormalThreshold = abnormalThreshold;
        this.caregiverAbsentThreshold = caregiverAbsentThreshold;
        this.lowConfidenceThreshold = lowConfidenceThreshold;
        this.cooldownSeconds = cooldownSeconds;
    }

    public AlertRule.Decision feed(String cameraId, VlmResult result) {
        Deque<VlmResult> window = windows.computeIfAbsent(cameraId, key -> new ArrayDeque<>());
        synchronized (window) {
            window.addLast(result);
            while (window.size() > windowSize) {
                window.removeFirst();
            }
            AlertRule.Decision decision = AlertRule.evaluate(
                window,
                cooldownOk(cameraId),
                abnormalThreshold,
                caregiverAbsentThreshold,
                lowConfidenceThreshold
            );
            if (decision.shouldAlert()) {
                lastAlertAt.put(cameraId, Instant.now());
            }
            return decision;
        }
    }

    private boolean cooldownOk(String cameraId) {
        Instant last = lastAlertAt.get(cameraId);
        return last == null || Instant.now().isAfter(last.plusSeconds(cooldownSeconds));
    }
}
