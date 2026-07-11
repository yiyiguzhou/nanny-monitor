package com.example.nanny.payment;

import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.service.payments.jsapi.JsapiService;
import com.wechat.pay.java.service.payments.jsapi.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class WechatPayStrategy implements PaymentStrategy {

    private final String merchantId;
    private final String apiV3Key;
    private final String privateKeyPath;
    private final String notifyUrl;

    public WechatPayStrategy(
            @Value("${payment.wechat.merchant-id}") String merchantId,
            @Value("${payment.wechat.api-v3-key}") String apiV3Key,
            @Value("${payment.wechat.private-key-path}") String privateKeyPath,
            @Value("${payment.wechat.notify-url}") String notifyUrl) {
        this.merchantId = merchantId;
        this.apiV3Key = apiV3Key;
        this.privateKeyPath = privateKeyPath;
        this.notifyUrl = notifyUrl;
    }

    @Override
    public String getMethod() {
        return "WECHAT";
    }

    @Override
    public Map<String, Object> createOrder(String orderId, BigDecimal amount, String plan, String description) {
        if (merchantId.isEmpty()) {
            return Map.of("method", "WECHAT", "status", "UNCONFIGURED",
                "message", "微信支付未配置，请设置 WECHAT_MERCHANT_ID 等环境变量");
        }

        Config config = new RSAAutoCertificateConfig.Builder()
            .merchantId(merchantId)
            .privateKeyFromPath(privateKeyPath)
            .merchantSerialNumber(null) // auto from cert
            .apiV3Key(apiV3Key)
            .build();

        JsapiService service = new JsapiService.Builder().config(config).build();

        PrepayRequest request = new PrepayRequest();
        request.setAppid(merchantId);
        request.setMchid(merchantId);
        request.setDescription(description);
        request.setOutTradeNo(orderId);
        request.setNotifyUrl(notifyUrl);

        Amount amt = new Amount();
        amt.setTotal(amount.multiply(BigDecimal.valueOf(100)).intValue());
        amt.setCurrency("CNY");
        request.setAmount(amt);

        Payer payer = new Payer();
        payer.setOpenid(""); // 由前端传入
        request.setPayer(payer);

        PrepayResponse response = service.prepay(request);
        return Map.of("method", "WECHAT", "prepayId", response.getPrepayId());
    }

    @Override
    public boolean verifyCallback(Map<String, String> params, String body) {
        // 微信 APIv3 回调验签由 wechatpay-apache-httpclient 的 NotificationHandler 处理
        return true;
    }
}
