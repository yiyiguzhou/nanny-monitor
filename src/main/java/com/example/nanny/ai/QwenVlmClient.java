package com.example.nanny.ai;

import com.example.nanny.config.VlmProperties;
import com.example.nanny.dto.VlmResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Component
public class QwenVlmClient implements VlmClient {
    private static final Logger log = LoggerFactory.getLogger(QwenVlmClient.class);

    private final VlmProperties properties;
    private final ObjectMapper objectMapper;
    private final RestClient restClient;

    public QwenVlmClient(VlmProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(10));
        requestFactory.setReadTimeout(Duration.ofSeconds(properties.getTimeoutSeconds()));

        this.restClient = RestClient.builder()
            .requestFactory(requestFactory)
            .build();
    }

    @Override
    public VlmResult detect(byte[] jpegFrame) {
        if (!StringUtils.hasText(properties.getApiKey())) {
            log.warn("DASHSCOPE_API_KEY is empty, skip VLM detection");
            return new VlmResult(false, false, false, false, 0.0, "未配置 DASHSCOPE_API_KEY");
        }

        try {
            String base64 = Base64.getEncoder().encodeToString(jpegFrame);
            String dataUrl = "data:image/jpeg;base64," + base64;

            Map<String, Object> body = Map.of(
                "model", properties.getModel(),
                "messages", List.of(Map.of(
                    "role", "user",
                    "content", List.of(
                        Map.of("type", "image_url", "image_url", Map.of("url", dataUrl)),
                        Map.of("type", "text", "text", FeedingPrompt.INSTRUCTION)
                    )
                )),
                "temperature", 0.1,
                "max_tokens", 512
            );

            String raw = restClient.post()
                .uri(properties.getEndpoint())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getApiKey())
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(String.class);

            return parseResponse(raw);
        } catch (Exception e) {
            log.warn("Qwen-VL detection failed: {}", e.getMessage(), e);
            return new VlmResult(false, false, false, false, 0.0, "VLM调用失败: " + e.getMessage());
        }
    }

    private VlmResult parseResponse(String raw) throws Exception {
        JsonNode root = objectMapper.readTree(raw);
        String content = root.path("choices").path(0).path("message").path("content").asText();
        String json = extractJson(content);
        return objectMapper.readValue(json, VlmResult.class);
    }

    private String extractJson(String content) {
        int start = content.indexOf('{');
        int end = content.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return content.substring(start, end + 1);
        }
        throw new IllegalArgumentException("模型未返回JSON: " + content);
    }
}
