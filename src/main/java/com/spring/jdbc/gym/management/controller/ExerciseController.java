package com.spring.jdbc.gym.management.controller;

import com.spring.jdbc.gym.management.annotation.RequiresRole;
import com.spring.jdbc.gym.management.exception.CustomException;
import com.spring.jdbc.gym.management.model.Exercise;
import com.spring.jdbc.gym.management.model.TrainingPlanExercise;
import com.spring.jdbc.gym.management.model.filter.ExerciseFilter;
import com.spring.jdbc.gym.management.service.ExerciseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/exercises")
public class ExerciseController {
    private final ExerciseService exerciseService;

    public ExerciseController(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    @GetMapping
    @RequiresRole({"ADMIN"})
    public ResponseEntity<Object> getExercises(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String trainingPlanId,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit
    ) {
        try {
            ExerciseFilter filter = new ExerciseFilter(limit, offset);
            filter.setUserId(userId);
            filter.setTrainingPlanId(trainingPlanId);
            List<Exercise> exercises = exerciseService.getExerciseList(filter);
            return new ResponseEntity<>(exercises, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/users/{userId}/training-plans/{trainingPlanId}/exercises")
    @RequiresRole({"ADMIN"})
    public ResponseEntity<Object> getTrainingPlanExercises(
            @PathVariable String userId,
            @PathVariable String trainingPlanId
    ) {
        try {
            List<TrainingPlanExercise> exercises = exerciseService.getTrainingPlanExercisesByTrainingPlanId(trainingPlanId, userId);
            return new ResponseEntity<>(exercises, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/users/{userId}/training-plans/{trainingPlanId}/exercises/{id}")
    @RequiresRole({"ADMIN"})
    public ResponseEntity<Object> getTrainingPlanExerciseById(
            @PathVariable String id,
            @PathVariable String userId,
            @PathVariable String trainingPlanId
    ) {
        try {
            TrainingPlanExercise exercise = exerciseService.getTrainingPlanExerciseById(id, userId);
            return new ResponseEntity<>(exercise, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Exercise not found", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/users/{userId}/training-plans/{trainingPlanId}/exercises")
    @RequiresRole({"ADMIN"})
    public ResponseEntity<Object> createTrainingPlanExercise(
            @PathVariable String userId,
            @PathVariable String trainingPlanId,
            @RequestBody TrainingPlanExercise trainingPlanExercise
    ) {
        try {
            trainingPlanExercise.setTrainingPlanId(trainingPlanId);
            TrainingPlanExercise created = exerciseService.createTrainingPlanExercise(trainingPlanExercise);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (CustomException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/users/{userId}/training-plans/{trainingPlanId}/exercises/{id}")
    @RequiresRole({"ADMIN"})
    public ResponseEntity<Object> updateTrainingPlanExercise(
            @PathVariable String id,
            @PathVariable String userId,
            @PathVariable String trainingPlanId,
            @RequestBody TrainingPlanExercise trainingPlanExercise
    ) {
        try {
            trainingPlanExercise.setId(id);
            trainingPlanExercise.setTrainingPlanId(trainingPlanId);
            exerciseService.updateTrainingPlanExercise(trainingPlanExercise);
            return new ResponseEntity<>("Training plan exercise updated successfully", HttpStatus.OK);
        } catch (CustomException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    @RequiresRole({"ADMIN"})
    public ResponseEntity<Object> getExerciseById(@PathVariable String id) {
        try {
            Exercise exercise = exerciseService.getExerciseById(id);
            return new ResponseEntity<>(exercise, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Exercise not found", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    @RequiresRole({"ADMIN"})
    public ResponseEntity<Object> createExercise(@RequestBody Exercise exercise) {
        try {
            Exercise createdExercise = exerciseService.createExercise(exercise);
            return new ResponseEntity<>(createdExercise, HttpStatus.CREATED);
        } catch (CustomException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    @RequiresRole({"ADMIN"})
    public ResponseEntity<Object> updateExercise(@PathVariable String id, @RequestBody Exercise exercise) {
        try {
            exercise.setId(id);
            exerciseService.updateExercise(exercise);
            return new ResponseEntity<>("Exercise updated successfully", HttpStatus.OK);
        } catch (CustomException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/users/{userId}/training-plans/{trainingPlanId}/exercises/{id}")
    @RequiresRole({"ADMIN"})
    public ResponseEntity<Object> deleteTrainingPlanExercise(
            @PathVariable String id,
            @PathVariable String userId,
            @PathVariable String trainingPlanId
    ) {
        try {
            exerciseService.deleteTrainingPlanExercise(id);
            return new ResponseEntity<>("Training plan exercise deleted successfully", HttpStatus.OK);
        } catch (CustomException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    @RequiresRole({"ADMIN"})
    public ResponseEntity<Object> deleteExercise(@PathVariable String id) {
        try {
            exerciseService.deleteExercise(id);
            return new ResponseEntity<>("Exercise deleted successfully", HttpStatus.OK);
        } catch (CustomException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
