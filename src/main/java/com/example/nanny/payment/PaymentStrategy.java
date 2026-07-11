package com.example.nanny.payment;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 支付策略接口。微信/支付宝/银联各自实现。
 */
public interface PaymentStrategy {

    String getMethod();

    /**
     * 创建支付订单，返回客户端需要的支付参数（二维码URL、跳转URL等）。
     */
    Map<String, Object> createOrder(String orderId, BigDecimal amount, String plan, String description);

    /**
     * 验证支付回调签名。
     */
    boolean verifyCallback(Map<String, String> params, String body);
}
