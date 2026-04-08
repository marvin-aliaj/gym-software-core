package com.spring.jdbc.gym.management.service;

import com.spring.jdbc.gym.management.dao.ExerciseDao;
import com.spring.jdbc.gym.management.exception.CustomException;
import com.spring.jdbc.gym.management.model.Exercise;
import com.spring.jdbc.gym.management.model.TrainingPlanExercise;
import com.spring.jdbc.gym.management.model.filter.ExerciseFilter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExerciseService {
    private final ExerciseDao exerciseDao;

    public ExerciseService(ExerciseDao exerciseDao) {
        this.exerciseDao = exerciseDao;
    }

    public List<Exercise> getExerciseList(ExerciseFilter filter) throws Exception {
        try {
            return exerciseDao.getExerciseList(filter);
        } catch (Exception e) {
            throw new CustomException();
        }
    }

    public Exercise getExerciseById(String id) throws Exception {
        try {
            return exerciseDao.getExerciseById(id);
        } catch (Exception e) {
            throw new CustomException();
        }
    }

    public Exercise createExercise(Exercise exercise) throws Exception {
        try {
            return exerciseDao.createExercise(exercise);
        } catch (Exception e) {
            throw new CustomException();
        }
    }

    public void updateExercise(Exercise exercise) throws Exception {
        try {
            exerciseDao.updateExercise(exercise);
        } catch (Exception e) {
            throw new CustomException();
        }
    }

    public void deleteExercise(String id) throws Exception {
        try {
            exerciseDao.deleteExercise(id);
        } catch (Exception e) {
            throw new CustomException();
        }
    }

    // ===== Training Plan Exercise Methods =====

    public TrainingPlanExercise getTrainingPlanExerciseById(String id, String userId) throws Exception {
        try {
            return exerciseDao.getTrainingPlanExerciseById(id, userId);
        } catch (Exception e) {
            throw new CustomException();
        }
    }

    public List<TrainingPlanExercise> getTrainingPlanExercisesByTrainingPlanId(String trainingPlanId, String userId) throws Exception {
        try {
            return exerciseDao.getTrainingPlanExercisesByTrainingPlanId(trainingPlanId, userId);
        } catch (Exception e) {
            throw new CustomException();
        }
    }

    public TrainingPlanExercise createTrainingPlanExercise(TrainingPlanExercise trainingPlanExercise) throws Exception {
        try {
            return exerciseDao.createTrainingPlanExercise(trainingPlanExercise);
        } catch (Exception e) {
            throw new CustomException();
        }
    }

    public void updateTrainingPlanExercise(TrainingPlanExercise trainingPlanExercise) throws Exception {
        try {
            exerciseDao.updateTrainingPlanExercise(trainingPlanExercise);
        } catch (Exception e) {
            throw new CustomException();
        }
    }

    public void deleteTrainingPlanExercise(String id) throws Exception {
        try {
            exerciseDao.deleteTrainingPlanExercise(id);
        } catch (Exception e) {
            throw new CustomException();
        }
    }
}
