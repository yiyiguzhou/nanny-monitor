package com.example.nanny.controller;

import com.example.nanny.domain.PaymentRecord;
import com.example.nanny.repository.PaymentRecordRepository;
import com.example.nanny.service.PaymentService;
import com.example.nanny.service.SubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentService paymentService;
    private final SubscriptionService subscriptionService;
    private final PaymentRecordRepository paymentRecordRepository;

    public PaymentController(PaymentService paymentService,
                             SubscriptionService subscriptionService,
                             PaymentRecordRepository paymentRecordRepository) {
        this.paymentService = paymentService;
        this.subscriptionService = subscriptionService;
        this.paymentRecordRepository = paymentRecordRepository;
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody Map<String, String> body) {
        Long userId = getCurrentUserId();
        String method = body.get("method");
        String plan = body.get("plan");

        if (method == null || plan == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "method and plan are required"));
        }

        try {
            Map<String, Object> result = paymentService.createPayment(userId, method, plan);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 调试模式：直接激活订阅，无需真实支付。
     */
    @PostMapping("/debug/activate")
    public ResponseEntity<?> debugActivate(@RequestBody Map<String, String> body) {
        Long userId = getCurrentUserId();
        String plan = body.getOrDefault("plan", "MONTHLY");

        if (!"MONTHLY".equals(plan) && !"QUARTERLY".equals(plan) && !"YEARLY".equals(plan)) {
            return ResponseEntity.badRequest().body(Map.of("error", "plan must be MONTHLY, QUARTERLY, or YEARLY"));
        }

        java.math.BigDecimal price = getPlanPrice(plan);

        PaymentRecord record = new PaymentRecord(userId, price, "DEBUG", plan);
        record.setStatus("SUCCESS");
        record.setTransactionId("DEBUG-" + System.currentTimeMillis());
        paymentRecordRepository.insert(record);

        subscriptionService.activate(userId, plan);

        return ResponseEntity.ok(Map.of(
            "status", "SUCCESS",
            "message", "订阅已激活",
            "plan", plan,
            "amount", price
        ));
    }

    private java.math.BigDecimal getPlanPrice(String plan) {
        switch (plan) {
            case "MONTHLY": return new java.math.BigDecimal("29.9");
            case "QUARTERLY": return new java.math.BigDecimal("79.9");
            case "YEARLY": return new java.math.BigDecimal("299.0");
            default: throw new IllegalArgumentException("Unknown plan: " + plan);
        }
    }

    @PostMapping("/callback/wechat")
    public ResponseEntity<String> wechatCallback(@RequestBody String body,
                                                  @RequestHeader Map<String, String> headers) {
        paymentService.handleCallback("WECHAT", Map.of(), body);
        return ResponseEntity.ok("SUCCESS");
    }

    @PostMapping("/callback/alipay")
    public ResponseEntity<String> alipayCallback(@RequestParam Map<String, String> params) {
        paymentService.handleCallback("ALIPAY", params, "");
        return ResponseEntity.ok("success");
    }

    @PostMapping("/callback/bankcard")
    public ResponseEntity<String> bankcardCallback(@RequestBody Map<String, String> params) {
        paymentService.handleCallback("BANK_CARD", params, "");
        return ResponseEntity.ok("SUCCESS");
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (Long) auth.getPrincipal();
    }
}
