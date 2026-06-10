package com.example.nanny.ai;

/** 通义千问-VL 提示词：强制模型只输出 JSON，不含任何额外文本。 */
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
}
