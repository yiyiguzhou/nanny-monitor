package com.example.nanny.payment;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class AlipayStrategy implements PaymentStrategy {

    private final String appId;
    private final String privateKey;
    private final String alipayPublicKey;
    private final String notifyUrl;

    public AlipayStrategy(
            @Value("${payment.alipay.app-id}") String appId,
            @Value("${payment.alipay.private-key}") String privateKey,
            @Value("${payment.alipay.alipay-public-key}") String alipayPublicKey,
            @Value("${payment.alipay.notify-url}") String notifyUrl) {
        this.appId = appId;
        this.privateKey = privateKey;
        this.alipayPublicKey = alipayPublicKey;
        this.notifyUrl = notifyUrl;
    }

    @Override
    public String getMethod() {
        return "ALIPAY";
    }

    @Override
    public Map<String, Object> createOrder(String orderId, BigDecimal amount, String plan, String description) {
        if (appId.isEmpty()) {
            return Map.of("method", "ALIPAY", "status", "UNCONFIGURED",
                "message", "支付宝未配置，请设置 ALIPAY_APP_ID 等环境变量");
        }

        AlipayClient client = new DefaultAlipayClient(
            "https://openapi.alipay.com/gateway.do",
            appId, privateKey, "json", "UTF-8", alipayPublicKey, "RSA2");

        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(notifyUrl);
        request.setBizContent(String.format(
            "{\"out_trade_no\":\"%s\",\"total_amount\":\"%.2f\",\"subject\":\"%s\",\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}",
            orderId, amount, description));

        try {
            String form = client.pageExecute(request).getBody();
            return Map.of("method", "ALIPAY", "payForm", form);
        } catch (AlipayApiException e) {
            throw new RuntimeException("Alipay order creation failed", e);
        }
    }

    @Override
    public boolean verifyCallback(Map<String, String> params, String body) {
        try {
            return AlipaySignature.rsaCheckV1(params, alipayPublicKey, "UTF-8", "RSA2");
        } catch (AlipayApiException e) {
            return false;
        }
    }
}
