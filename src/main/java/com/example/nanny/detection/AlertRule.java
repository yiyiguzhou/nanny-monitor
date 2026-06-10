package com.example.nanny.detection;

import com.example.nanny.dto.VlmResult;

import java.util.Deque;

public final class AlertRule {
    private AlertRule() {}

    public record Decision(boolean shouldAlert, String type, String reason) {
        public static Decision none() {
            return new Decision(false, "NONE", "");
        }
    }

    public static Decision evaluate(Deque<VlmResult> window,
                                    boolean cooldownOk,
                                    int abnormalThreshold,
                                    int caregiverAbsentThreshold,
                                    double lowConfidenceThreshold) {
        if (!cooldownOk || window.isEmpty()) {
            return Decision.none();
        }

        long abnormalCount = window.stream()
            .filter(r -> r.abnormal() && r.confidence() >= lowConfidenceThreshold)
            .count();
        if (abnormalCount >= abnormalThreshold) {
            return new Decision(true, "ABNORMAL_FEEDING", "连续多帧检测到疑似喂奶异常");
        }

        long babyPresentCount = window.stream().filter(VlmResult::babyPresent).count();
        long caregiverAbsentCount = window.stream()
            .filter(r -> r.babyPresent() && !r.caregiverPresent())
            .count();
        if (babyPresentCount >= caregiverAbsentThreshold && caregiverAbsentCount >= caregiverAbsentThreshold) {
            return new Decision(true, "CAREGIVER_ABSENT", "婴儿在画面中但看护人连续缺席");
        }

        return Decision.none();
    }
}
