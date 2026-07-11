package com.example.nanny.service;

import com.example.nanny.domain.PaymentRecord;
import com.example.nanny.payment.PaymentStrategy;
import com.example.nanny.repository.PaymentRecordRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRecordRepository paymentRepository;
    private final SubscriptionService subscriptionService;
    private final Map<String, PaymentStrategy> strategies;
    private final BigDecimal monthlyPrice;
    private final BigDecimal quarterlyPrice;
    private final BigDecimal yearlyPrice;

    public PaymentService(PaymentRecordRepository paymentRepository,
                          SubscriptionService subscriptionService,
                          List<PaymentStrategy> strategyList,
                          @Value("${subscription.monthly-price:29.9}") BigDecimal monthlyPrice,
                          @Value("${subscription.quarterly-price:79.9}") BigDecimal quarterlyPrice,
                          @Value("${subscription.yearly-price:299.0}") BigDecimal yearlyPrice) {
        this.paymentRepository = paymentRepository;
        this.subscriptionService = subscriptionService;
        this.monthlyPrice = monthlyPrice;
        this.quarterlyPrice = quarterlyPrice;
        this.yearlyPrice = yearlyPrice;
        this.strategies = Map.of(
            "WECHAT", findStrategy(strategyList, "WECHAT"),
            "ALIPAY", findStrategy(strategyList, "ALIPAY"),
            "BANK_CARD", findStrategy(strategyList, "BANK_CARD"),
            "DEBUG", findStrategy(strategyList, "DEBUG")
        );
    }

    private static PaymentStrategy findStrategy(List<PaymentStrategy> list, String method) {
        return list.stream()
            .filter(s -> s.getMethod().equals(method))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No payment strategy for: " + method));
    }

    public Map<String, Object> createPayment(Long userId, String method, String plan) {
        PaymentStrategy strategy = strategies.get(method.toUpperCase());
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported payment method: " + method);
        }

        BigDecimal amount = getPlanPrice(plan);
        String orderId = "NANNY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        PaymentRecord record = new PaymentRecord(userId, amount, method.toUpperCase(), plan);
        record.setTransactionId(orderId); // store orderId for callback lookup
        paymentRepository.insert(record);

        Map<String, Object> result = strategy.createOrder(orderId, amount, plan,
            "Nanny Monitor " + plan + " 订阅");
        result.put("orderId", orderId);
        result.put("amount", amount);
        return result;
    }

    public void handleCallback(String method, Map<String, String> params, String body) {
        PaymentStrategy strategy = strategies.get(method.toUpperCase());
        if (strategy == null) return;

        String orderId = params.getOrDefault("out_trade_no", params.getOrDefault("outTradeNo", ""));
        if (orderId.isEmpty()) return;

        PaymentRecord record = paymentRepository.findByTransactionId(orderId);
        if (record == null || !"PENDING".equals(record.getStatus())) return;

        if (strategy.verifyCallback(params, body)) {
            record.setStatus("SUCCESS");
            record.setTransactionId(params.getOrDefault("trade_no",
                params.getOrDefault("tradeNo", orderId)));
            paymentRepository.updateById(record);

            subscriptionService.activate(record.getUserId(), record.getSubscriptionPlan());
        } else {
            record.setStatus("FAILED");
            paymentRepository.updateById(record);
        }
    }

    private BigDecimal getPlanPrice(String plan) {
        switch (plan.toUpperCase()) {
            case "MONTHLY": return monthlyPrice;
            case "QUARTERLY": return quarterlyPrice;
            case "YEARLY": return yearlyPrice;
            default: throw new IllegalArgumentException("Unknown plan: " + plan);
        }
    }
}
