package com.example.nanny.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.model.Media;
import org.springframework.util.MimeTypeUtils;

import java.util.function.Consumer;

/**
 * Feeding detection prompt for Spring AI ChatClient.
 */
public final class FeedingPrompt {

    private FeedingPrompt() {}

    public static final String INSTRUCTION = """
            你是家庭育儿监控分析助手。分析这张摄像头截图，判断看护人（保姆）是否正在给婴儿喂奶。

            只返回 JSON，不包含任何其他内容，格式严格如下：
            {"feeding":bool,"babyPresent":bool,"caregiverPresent":bool,"abnormal":bool,"confidence":0~1,"description":"简要中文描述"}

            字段说明：
            - feeding: 看护人正在喂奶（bottle/母乳/辅食均算）
            - babyPresent: 画面中可见婴儿
            - caregiverPresent: 画面中可见看护人
            - abnormal: 存在异常（喂奶姿势明显不当、婴儿哭闹无人响应、看护人长时间离开婴儿视线）
            - confidence: 你对 feeding 判断的置信度（0.0~1.0）
            - description: 10~30 字的简要中文描述
            """;

    /**
     * Build a Consumer for ChatClient.user() that includes both text instruction and JPEG image.
     */
    public static Consumer<ChatClient.PromptUserSpec> userSpec(byte[] jpegBytes) {
        Media media = Media.builder()
            .data(jpegBytes)
            .mimeType(MimeTypeUtils.IMAGE_JPEG)
            .build();
        return spec -> spec.text(INSTRUCTION).media(media);
    }
}
