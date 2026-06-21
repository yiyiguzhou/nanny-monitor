package com.example.nanny.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NannyAiConfig {

    @Bean
    public ChatClient nannyChatClient(ChatClient.Builder builder) {
        return builder.build();
    }
}
