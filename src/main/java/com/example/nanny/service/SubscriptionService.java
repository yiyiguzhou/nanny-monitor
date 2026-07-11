package com.example.nanny.service;

import com.example.nanny.domain.UserSubscription;
import com.example.nanny.repository.UserSubscriptionRepository;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

@Service
public class SubscriptionService {

    private final UserSubscriptionRepository subscriptionRepository;

    public SubscriptionService(UserSubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    public boolean isActive(Long userId) {
        UserSubscription sub = subscriptionRepository.findActiveByUserId(userId);
        return sub != null;
    }

    public UserSubscription getActiveSubscription(Long userId) {
        return subscriptionRepository.findActiveByUserId(userId);
    }

    public UserSubscription activate(Long userId, String plan) {
        Date now = new Date();
        Date endDate = calculateEndDate(now, plan);

        UserSubscription sub = new UserSubscription(userId, plan, now, endDate);
        subscriptionRepository.insert(sub);
        return sub;
    }

    private Date calculateEndDate(Date start, String plan) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        switch (plan) {
            case "MONTHLY":
                cal.add(Calendar.DAY_OF_MONTH, 30);
                break;
            case "QUARTERLY":
                cal.add(Calendar.DAY_OF_MONTH, 90);
                break;
            case "YEARLY":
                cal.add(Calendar.DAY_OF_YEAR, 365);
                break;
            default:
                throw new IllegalArgumentException("Unknown plan: " + plan);
        }
        return cal.getTime();
    }
}
