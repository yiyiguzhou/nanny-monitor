package com.example.nanny;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@EnableDubbo
@SpringBootApplication
@MapperScan({"com.example.nanny.repository", "com.example.nanny.mapper"})
public class NannyMonitorApplication {

    public static void main(String[] args) {
        SpringApplication.run(NannyMonitorApplication.class, args);
    }
}