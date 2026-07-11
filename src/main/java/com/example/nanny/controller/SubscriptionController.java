package com.example.nanny.controller;

import com.example.nanny.domain.UserSubscription;
import com.example.nanny.service.SubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/api/subscription")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @GetMapping("/status")
    public ResponseEntity<?> status() {
        Long userId = getCurrentUserId();
        UserSubscription sub = subscriptionService.getActiveSubscription(userId);

        if (sub == null) {
            return ResponseEntity.ok(Map.of(
                "active", false,
                "plan", "NONE",
                "message", "未开通订阅"
            ));
        }

        long daysRemaining = (sub.getEndDate().getTime() - System.currentTimeMillis()) / (1000 * 60 * 60 * 24);
        return ResponseEntity.ok(Map.of(
            "active", true,
            "plan", sub.getPlan(),
            "startDate", sub.getStartDate().toString(),
            "endDate", sub.getEndDate().toString(),
            "daysRemaining", Math.max(0, daysRemaining)
        ));
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (Long) auth.getPrincipal();
    }
}
