package com.example.nanny.payment;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 本地调试策略 — 不接入真实支付，直接返回调试信息。
 * 实际激活订阅请调用 POST /api/payment/debug/activate
 */
@Component
public class DebugPaymentStrategy implements PaymentStrategy {

    @Override
    public String getMethod() {
        return "DEBUG";
    }

    @Override
    public Map<String, Object> createOrder(String orderId, BigDecimal amount, String plan, String description) {
        return Map.of(
            "method", "DEBUG",
            "status", "DEBUG",
            "orderId", orderId,
            "amount", amount,
            "plan", plan,
            "message", "调试模式，请调用 POST /api/payment/debug/activate 直接激活订阅"
        );
    }

    @Override
    public boolean verifyCallback(Map<String, String> params, String body) {
        return true;
    }
}