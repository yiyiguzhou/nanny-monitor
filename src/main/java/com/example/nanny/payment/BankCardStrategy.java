package com.example.nanny.payment;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Component
public class BankCardStrategy implements PaymentStrategy {

    @Override
    public String getMethod() {
        return "BANK_CARD";
    }

    @Override
    public Map<String, Object> createOrder(String orderId, BigDecimal amount, String plan, String description) {
        // 银联/银行卡支付通过第三方聚合支付网关接入（如 Ping++、BeePay）
        // 此处为通用实现，实际对接时替换为具体网关 SDK
        return Map.of(
            "method", "BANK_CARD",
            "orderId", orderId,
            "amount", amount,
            "description", description,
            "paymentUrl", "https://gateway.example.com/pay?orderId=" + orderId,
            "status", "READY"
        );
    }

    @Override
    public boolean verifyCallback(Map<String, String> params, String body) {
        String sign = params.get("sign");
        // 实际对接时：使用网关提供的公钥验签
        return sign != null && !sign.isEmpty();
    }
}
