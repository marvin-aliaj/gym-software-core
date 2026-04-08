package com.spring.jdbc.gym.management.controller;

import com.spring.jdbc.gym.management.annotation.RequiresRole;
import com.spring.jdbc.gym.management.exception.CustomException;
import com.spring.jdbc.gym.management.model.Subscription;
import com.spring.jdbc.gym.management.model.filter.ExerciseFilter;
import com.spring.jdbc.gym.management.service.SubscriptionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/subscriptions")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @RequiresRole({"ADMIN"})
    @GetMapping("/users/{userId}/subscriptions")
    public ResponseEntity<Object> getSubscriptions(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit
    ) {
        try {
            ExerciseFilter filter = new ExerciseFilter(limit, offset);
            filter.setUserId(userId);
            List<Subscription> subscriptions = subscriptionService.getSubscriptionList(filter);
            return new ResponseEntity<>(subscriptions, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequiresRole({"ADMIN"})
    @GetMapping("/users/{userId}/subscriptions/{id}")
    public ResponseEntity<Object> getSubscriptionById(
            @PathVariable String userId,
            @PathVariable String id,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit
    ) {
        try {
            ExerciseFilter filter = new ExerciseFilter(limit, offset);
            filter.setUserId(userId);
            filter.setEntityId (id);
            Subscription subscription = subscriptionService.getSubscriptionById(id, filter);
            return new ResponseEntity<>(subscription, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Subscription not found", HttpStatus.NOT_FOUND);
        }
    }

    @RequiresRole({"ADMIN"})
    @PostMapping("/users/{userId}/subscriptions")
    public ResponseEntity<Object> createSubscription(
            @PathVariable String userId,
            @RequestBody Subscription subscription
    ) {
        try {
            Subscription createdSubscription = subscriptionService.createSubscription(subscription);
            return new ResponseEntity<>(createdSubscription, HttpStatus.CREATED);
        } catch (CustomException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequiresRole({"ADMIN"})
    @PutMapping("/users/{userId}/subscriptions/{id}")
    public ResponseEntity<Object> updateSubscription(
            @PathVariable String userId,
            @PathVariable String id,
            @RequestBody Subscription subscription
    ) {
        try {
            subscription.setId(id);
            subscriptionService.updateSubscription(subscription);
            return new ResponseEntity<>("Subscription updated successfully", HttpStatus.OK);
        } catch (CustomException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequiresRole({"ADMIN"})
    @DeleteMapping("/users/{userId}/subscriptions/{id}")
    public ResponseEntity<Object> deleteSubscription(
            @PathVariable String userId,
            @PathVariable String id
    ) {
        try {
            subscriptionService.deleteSubscription(id);
            return new ResponseEntity<>("Subscription deleted successfully", HttpStatus.OK);
        } catch (CustomException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
