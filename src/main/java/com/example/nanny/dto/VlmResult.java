package com.example.nanny.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record VlmResult(
    boolean feeding,
    boolean babyPresent,
    boolean caregiverPresent,
    boolean abnormal,
    double confidence,
    String description
) {
    public static VlmResult unknown() {
        return new VlmResult(false, false, false, false, 0.0, "解析失败");
    }
}
