package com.example.nanny;

import com.example.nanny.config.VlmProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
@EnableConfigurationProperties(VlmProperties.class)
public class NannyMonitorApplication {

    public static void main(String[] args) {
        SpringApplication.run(NannyMonitorApplication.class, args);
    }
}
