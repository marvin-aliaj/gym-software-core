package com.spring.jdbc.gym.management.service;

import com.spring.jdbc.gym.management.dao.SubscriptionDao;
import com.spring.jdbc.gym.management.exception.CustomException;
import com.spring.jdbc.gym.management.model.Subscription;
import com.spring.jdbc.gym.management.model.filter.ExerciseFilter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriptionService {
    private final SubscriptionDao subscriptionDao;

    public SubscriptionService(SubscriptionDao subscriptionDao) {
        this.subscriptionDao = subscriptionDao;
    }

    public List<Subscription> getSubscriptionList(ExerciseFilter filter) throws Exception {
        try {
            return subscriptionDao.getSubscriptionList(filter);
        } catch (Exception e) {
            throw new CustomException();
        }
    }

    public Subscription getSubscriptionById(String id, ExerciseFilter filter) throws Exception {
        try {
            filter.setEntityId(id);
            List<Subscription> subscriptions = subscriptionDao.getSubscriptionList(filter);
            if (subscriptions.isEmpty()) {
                throw new CustomException();
            }
            return subscriptions.get(0);
        } catch (Exception e) {
            throw new CustomException();
        }
    }

    public Subscription createSubscription(Subscription subscription) throws Exception {
        try {
            return subscriptionDao.createSubscription(subscription);
        } catch (Exception e) {
            throw new CustomException();
        }
    }

    public void updateSubscription(Subscription subscription) throws Exception {
        try {
            subscriptionDao.updateSubscription(subscription);
        } catch (Exception e) {
            throw new CustomException();
        }
    }

    public void deleteSubscription(String id) throws Exception {
        try {
            subscriptionDao.deleteSubscription(id);
        } catch (Exception e) {
            throw new CustomException();
        }
    }
}
