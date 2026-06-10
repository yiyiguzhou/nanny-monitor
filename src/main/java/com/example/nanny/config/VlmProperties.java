package com.example.nanny.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "nanny.vlm")
public class VlmProperties {
    /** DashScope OpenAI-compatible chat completions endpoint. */
    private String endpoint;
    /** DASHSCOPE_API_KEY. Never hard-code it in source code. */
    private String apiKey;
    /** qwen-vl-plus / qwen-vl-max / qwen2.5-vl-72b-instruct, depending on account availability. */
    private String model = "qwen-vl-plus";
    private int timeoutSeconds = 45;

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public void setTimeoutSeconds(int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }
}
