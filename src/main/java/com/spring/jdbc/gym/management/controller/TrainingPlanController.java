package com.spring.jdbc.gym.management.controller;

import com.spring.jdbc.gym.management.annotation.RequiresRole;
import com.spring.jdbc.gym.management.exception.CustomException;
import com.spring.jdbc.gym.management.model.TrainingPlan;
import com.spring.jdbc.gym.management.model.filter.ExerciseFilter;
import com.spring.jdbc.gym.management.service.TrainingPlanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/trainingPlans")
public class TrainingPlanController {
    private final TrainingPlanService trainingPlanService;

    public TrainingPlanController(TrainingPlanService trainingPlanService) {
        this.trainingPlanService = trainingPlanService;
    }

    @RequiresRole({"ADMIN"})
    @GetMapping("/users/{userId}/trainingPlans")
    public ResponseEntity<Object> getTrainingPlans(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit
    ) {
        try {
            ExerciseFilter filter = new ExerciseFilter(limit, offset);
            filter.setUserId(userId);
            List<TrainingPlan> trainingPlans = trainingPlanService.getTrainingPlanList(filter);
            return new ResponseEntity<>(trainingPlans, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequiresRole({"ADMIN"})
    @GetMapping("/assignees/{assigneeId}/trainingPlans")
    public ResponseEntity<Object> getAssigneeTrainingPlans(
            @PathVariable String assigneeId,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit
    ) {
        try {
            ExerciseFilter filter = new ExerciseFilter(limit, offset);
            filter.setAssigneeId(assigneeId);
            List<TrainingPlan> trainingPlans = trainingPlanService.getTrainingPlanList(filter);
            return new ResponseEntity<>(trainingPlans, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @RequiresRole({"ADMIN"})
    @GetMapping("/users/{userId}/trainingPlans/{id}")
    public ResponseEntity<Object> getTrainingPlanById(
            @PathVariable String userId,
            @PathVariable String id,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit
    ) {
        try {
            ExerciseFilter filter = new ExerciseFilter(limit, offset);
            filter.setUserId(userId);
            filter.setEntityId (id);
            TrainingPlan trainingPlan = trainingPlanService.getTrainingPlanById(id, filter);
            return new ResponseEntity<>(trainingPlan, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Training plan not found", HttpStatus.NOT_FOUND);
        }
    }

    @RequiresRole({"ADMIN"})
    @GetMapping("/assignees/{userId}/trainingPlans/{id}")
    public ResponseEntity<Object> getAssigneeTrainingPlanById(
            @PathVariable String userId,
            @PathVariable String id,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit
    ) {
        try {
            ExerciseFilter filter = new ExerciseFilter(limit, offset);
            filter.setAssigneeId(userId);
            filter.setEntityId (id);
            TrainingPlan trainingPlan = trainingPlanService.getTrainingPlanById(id, filter);
            return new ResponseEntity<>(trainingPlan, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Training plan not found", HttpStatus.NOT_FOUND);
        }
    }

    @RequiresRole({"ADMIN"})
    @PostMapping("/users/{userId}/trainingPlans")
    public ResponseEntity<Object> createTrainingPlan(
            @PathVariable String userId,
            @RequestBody TrainingPlan trainingPlan
    ) {
        try {
            TrainingPlan createdTrainingPlan = trainingPlanService.createTrainingPlan(trainingPlan);
            return new ResponseEntity<>(createdTrainingPlan, HttpStatus.CREATED);
        } catch (CustomException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequiresRole({"ADMIN"})
    @PutMapping("/users/{userId}/trainingPlans/{id}")
    public ResponseEntity<Object> updateTrainingPlan(
            @PathVariable String userId,
            @PathVariable String id,
            @RequestBody TrainingPlan trainingPlan
    ) {
        try {
            trainingPlan.setId(id);
            trainingPlanService.updateTrainingPlan(trainingPlan);
            return new ResponseEntity<>("Training plan updated successfully", HttpStatus.OK);
        } catch (CustomException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequiresRole({"ADMIN"})
    @DeleteMapping("/users/{userId}/trainingPlans/{id}")
    public ResponseEntity<Object> deleteTrainingPlan(
            @PathVariable String userId,
            @PathVariable String id
    ) {
        try {
            trainingPlanService.deleteTrainingPlan(id);
            return new ResponseEntity<>("Training plan deleted successfully", HttpStatus.OK);
        } catch (CustomException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
