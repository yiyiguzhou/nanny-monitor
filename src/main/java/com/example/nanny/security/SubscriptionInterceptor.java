package com.example.nanny.security;

import com.example.nanny.service.SubscriptionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class SubscriptionInterceptor implements HandlerInterceptor {

    private final SubscriptionService subscriptionService;

    public SubscriptionInterceptor(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        String path = request.getRequestURI();
        String method = request.getMethod();

        // Only check subscription for these endpoints
        boolean needsSubscription = path.matches(".*/api/cameras/[^/]+/start")
            || (path.matches(".*/api/cameras/[^/]+/detections") && "GET".equals(method));

        if (!needsSubscription) {
            return true;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Long)) {
            response.sendError(401, "Authentication required");
            return false;
        }

        Long userId = (Long) auth.getPrincipal();
        if (!subscriptionService.isActive(userId)) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(403);
            response.getWriter().write("{\"error\":\"需要有效订阅才能使用此功能\",\"code\":\"SUBSCRIPTION_REQUIRED\"}");
            return false;
        }

        return true;
    }
}
